import entities.Book;
import entities.Borrow;
import entities.Card;
import queries.*;
import utils.DBInitializer;
import utils.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LibraryManagementSystemImpl implements LibraryManagementSystem {

    private final DatabaseConnector connector;

    public LibraryManagementSystemImpl(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public ApiResult storeBook(Book book) {
        Connection conn = connector.getConn();
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            // 查询该书是否已经存在于图书库中
            String sql = "SELECT *\n" +
                    "FROM book\n" +
                    "WHERE category = ? AND title = ? AND press = ? AND publish_year = ? AND author = ?\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, book.getCategory());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getPress());
            pstmt.setInt(4, book.getPublishYear());
            pstmt.setString(5, book.getAuthor());
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {       // 如果该书已经存在于图书库中，则入库操作失败
                throw new Exception("The book already exists in the library system.");
            } else {        // 否则，将该书入库
                String sql1 = "INSERT INTO book\n" +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)\n";
                pstmt = conn.prepareStatement(sql1);
                pstmt.setString(1, book.getCategory());
                pstmt.setString(2, book.getTitle());
                pstmt.setString(3, book.getPress());
                pstmt.setInt(4, book.getPublishYear());
                pstmt.setString(5, book.getAuthor());
                pstmt.setDouble(6, book.getPrice());
                pstmt.setInt(7, book.getStock());
                pstmt.executeUpdate();
            }
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            try {
                // 查询当前book_id自增到哪里了
                PreparedStatement pstmt = null;
                String sql = "SELECT max(book_id) FROM book\n";
                pstmt = conn.prepareStatement(sql);
                ResultSet res1 = pstmt.executeQuery();
                int cur_id;
                if (res1.next()) {
                    cur_id = res1.getInt(1);
                } else {
                    cur_id = 0;
                }
                sql = "DBCC CHECKIDENT('book', RESEED, ?)\n";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cur_id);
                pstmt.executeUpdate();
            } catch (Exception e1) {
                // do nothing...
            }
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "storeBook(Book book) success");
    }

    @Override
    public ApiResult incBookStock(int bookId, int deltaStock) {
        Connection conn = connector.getConn();
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            // 查询得到该书的现有库存
            String sql = "SELECT stock\n" +
                    "FROM book\n" +
                    "WHERE book_id = ?\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {       // 如果查询到该书，则更新库存
                int stock = res.getInt("stock");
                if (stock + deltaStock < 0) {       // 如果更新后库存为负，则更新操作失败
                    throw new Exception("The stock can't be negative.");
                } else {      // 否则，正常进行更新库存操作
                    String sql1 = "UPDATE book\n" +
                            "SET stock = ?\n" +
                            "WHERE book_id = ?\n";
                    pstmt = conn.prepareStatement(sql1);
                    pstmt.setInt(1, stock + deltaStock);
                    pstmt.setInt(2, bookId);
                    pstmt.executeUpdate();
                    commit(conn);
                }
            } else {        // 否则，更新库存操作失败
                throw new Exception("The book doesn't exist in the library system.");
            }
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "incBookStock(int bookId, int deltaStock) success");
    }

    @Override
    public ApiResult storeBook(List<Book> books) {
        Connection conn = connector.getConn();
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            ResultSet res = null;
            String sql1 = "SELECT *\n" +
                    "FROM book\n" +
                    "WHERE category = ? AND title = ? AND press = ? AND publish_year = ? AND author = ?\n";
            String sql2 = "INSERT INTO book\n" +
                    "VALUES(?, ?, ?, ?, ?, ?, ?)\n";
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                // 查询该书是否已经存在于图书库中
                pstmt = conn.prepareStatement(sql1);
                pstmt.setString(1, book.getCategory());
                pstmt.setString(2, book.getTitle());
                pstmt.setString(3, book.getPress());
                pstmt.setInt(4, book.getPublishYear());
                pstmt.setString(5, book.getAuthor());
                res = pstmt.executeQuery();
                if (res.next()) {     // 如果该书已经存在于图书库中，则无法进行入库操作
                    throw new Exception("There is some book already exists in the library system.");
                } else {        // 否则，将该书入库
                    pstmt = conn.prepareStatement(sql2);
                    pstmt.setString(1, book.getCategory());
                    pstmt.setString(2, book.getTitle());
                    pstmt.setString(3, book.getPress());
                    pstmt.setInt(4, book.getPublishYear());
                    pstmt.setString(5, book.getAuthor());
                    pstmt.setDouble(6, book.getPrice());
                    pstmt.setInt(7, book.getStock());
                    pstmt.executeUpdate();
                }
            }
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            try {
                // 查询当前book_id自增到哪里了
                PreparedStatement pstmt = null;
                String sql = "SELECT max(book_id) FROM book\n";
                pstmt = conn.prepareStatement(sql);
                ResultSet res1 = pstmt.executeQuery();
                int cur_id;
                if (res1.next()) {
                    cur_id = res1.getInt(1);
                } else {
                    cur_id = 0;
                }
                sql = "DBCC CHECKIDENT('book', RESEED, ?)\n";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cur_id);
                pstmt.executeUpdate();
            } catch (Exception e1) {
                // do nothing...
            }
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "storeBook(List<Book> books) success");
    }

    @Override
    public ApiResult removeBook(int bookId) {
        Connection conn = connector.getConn();
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            // 首先查询该书是否存在于图书库中
            String sql = "SELECT *\n" +
                    "FROM book\n" +
                    "WHERE book_id = ?\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            ResultSet res = pstmt.executeQuery();
            if (!res.next()) {        // 如果没有该书，则删除失败
                throw new Exception("The book doesn't exist in the library system.");
            }
            // 然后查询是否有人尚未归还这本书
            sql = "SELECT book_id\n" +
                    "FROM borrow\n" +
                    "WHERE book_id = ?\n" +
                    "AND return_time = 0\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            res = pstmt.executeQuery();
            if (res.next()) {     // 如果有人尚未归还这本书，则删除失败
                throw new Exception("There is someone who hasn't return this book.");
            }
            // 如果该书存在于图书库中且当前没人借走这本书，则将其从图书库中删除
            sql = "DELETE FROM book\n" +
                    "WHERE book_id = ?\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "removeBook(int bookId) success");
    }

    @Override
    public ApiResult modifyBookInfo(Book book) {
        Connection conn = connector.getConn();
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            // 首先查询该书是否存在于图书库中
            String sql = "SELECT *\n" +
                    "FROM book\n" +
                    "WHERE book_id = ?\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, book.getBookId());
            ResultSet res = pstmt.executeQuery();
            if (!res.next()) {        // 如果没有该书，则修改失败
                throw new Exception("The book doesn't exist in the library system.");
            }
            String sql1 = "UPDATE book\n" +
                    "SET category = ?, title = ?, press = ?, publish_year = ?, author = ?, price = ?\n" +
                    "WHERE book_id = ?\n";
            pstmt = conn.prepareStatement(sql1);
            pstmt.setString(1, book.getCategory());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getPress());
            pstmt.setInt(4, book.getPublishYear());
            pstmt.setString(5, book.getAuthor());
            pstmt.setDouble(6, book.getPrice());
            pstmt.setInt(7, book.getBookId());
            pstmt.executeUpdate();
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "modifyBookInfo(Book book) success");
    }

    @Override
    public ApiResult queryBook(BookQueryConditions conditions) {
        Connection conn = connector.getConn();
        BookQueryResults bqRes = null;
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            String sql = "SELECT *\n" +
                    "FROM book\n" +
                    "WHERE ";
            int flag = 0;       // 标志是否为第一个条件
            if (conditions.getCategory() != null) {       // 类别点查(精确查询)
                if (flag == 0) {
                    sql += "category = '" + conditions.getCategory() + "'";
                    flag = 1;
                } else {
                    sql += "AND category = '" + conditions.getCategory() + "'";
                }
            }
            if (conditions.getTitle() != null) {     // 书名点查(模糊查询)
                if (flag == 0) {
                    sql += "title like '%" + conditions.getTitle() + "%'";
                    flag = 1;
                } else {
                    sql += "AND title like '%" + conditions.getTitle() + "%'";
                }
            }
            if (conditions.getPress() != null) {     // 出版社点查(模糊查询)
                if (flag == 0) {
                    sql += "press like '%" + conditions.getPress() + "%'";
                    flag = 1;
                } else {
                    sql += "AND press like '%" + conditions.getPress() + "%'";
                }
            }
            if (conditions.getMinPublishYear() != null) {      // 年份范围查
                if (flag == 0) {
                    sql += "publish_year >= " + String.valueOf(conditions.getMinPublishYear());
                    flag = 1;
                } else {
                    sql += "AND publish_year >= " + String.valueOf(conditions.getMinPublishYear());
                }
            }
            if (conditions.getMaxPublishYear() != null) {      // 年份范围查
                if (flag == 0) {
                    sql += "publish_year <= " + String.valueOf(conditions.getMaxPublishYear());
                    flag = 1;
                } else {
                    sql += "AND publish_year <= " + String.valueOf(conditions.getMaxPublishYear());
                }
            }
            if (conditions.getAuthor() != null) {     // 作者点查(模糊查询)
                if (flag == 0) {
                    sql += "author like '%" + conditions.getAuthor() + "%'";
                    flag = 1;
                } else {
                    sql += "AND author like '%" + conditions.getAuthor() + "%'";
                }
            }
            if (conditions.getMinPrice() != null) {       // 价格范围查
                if (flag == 0) {
                    sql += "price >= " + String.valueOf(conditions.getMinPrice());
                    flag = 1;
                } else {
                    sql += "AND price >= " + String.valueOf(conditions.getMinPrice());
                }
            }
            if (conditions.getMaxPrice() != null) {       // 价格范围查
                if (flag == 0) {
                    sql += "price <= " + String.valueOf(conditions.getMaxPrice());
                    flag = 1;
                } else {
                    sql += "AND price <= " + String.valueOf(conditions.getMaxPrice());
                }
            }
            if (flag == 0) {      // 如果没有设置条件，则重置sql
                sql = "SELECT *\n" +
                        "FROM book";
            }
            if (conditions.getSortBy().equals(Book.SortColumn.BOOK_ID)) {
                sql += "\nORDER BY " + conditions.getSortBy() + " " + conditions.getSortOrder() + "\n";
            } else {
                sql += "\nORDER BY " + conditions.getSortBy() + " " + conditions.getSortOrder() + ", book_id ASC" + "\n";
            }
            pstmt = conn.prepareStatement(sql);
            ResultSet res = pstmt.executeQuery();
            // 通过BookQueryResults对结果进行返回
            List<Book> books = new ArrayList<>();
            while (res.next()) {
                Book book = new Book();
                book.setBookId(res.getInt("book_id"));
                book.setCategory(res.getString("category"));
                book.setTitle(res.getString("title"));
                book.setPress(res.getString("press"));
                book.setPublishYear(res.getInt("publish_year"));
                book.setAuthor(res.getString("author"));
                book.setPrice(res.getDouble("price"));
                book.setStock(res.getInt("stock"));
                books.add(book);
            }
            bqRes = new BookQueryResults(books);
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "queryBook(BookQueryConditions conditions) success", bqRes);
    }

    @Override
    public ApiResult borrowBook(Borrow borrow) {
        Connection conn = connector.getConn();
        try {
            PreparedStatement pstmt = null;
            // 将事务隔离级别设置为Serializable以应对并发情形
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            // 查找是否该用户此前已经借过这本书但尚未归还
            String sql = "SELECT *\n" +
                    "FROM borrow\n" +
                    "WHERE card_id = ? AND book_id = ? AND return_time = 0\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, borrow.getCardId());
            pstmt.setInt(2, borrow.getBookId());
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {     // 如果该用户此前已经借过这本书但尚未归还，则借书操作失败
                throw new Exception("The customer borrowed this book but hasn't return it.");
            }
            // 查找这本书，并判断是否还有库存
            sql = "SELECT *\n" +
                    "FROM book\n" +
                    "WHERE book_id = ?\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, borrow.getBookId());
            res = pstmt.executeQuery();
            int stock = 0;
            if (res.next()) {       // 如果查到这本书，则获取库存
                stock = res.getInt("stock");
            } else {      // 如果查不到这本书，则借书操作失败
                throw new Exception("The book doesn't exist in the library system.");
            }
            if (stock <= 0) {     // 如果没有库存，则借书操作失败
                throw new Exception("The book is out of stock.");
            } else {      // 如果有库存，则更新库存并添加借书记录
                sql = "UPDATE book\n" +
                        "SET stock = stock - 1\n" +
                        "WHERE book_id = ? and stock = ?\n";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, borrow.getBookId());
                pstmt.setInt(2, stock);
                pstmt.executeUpdate();
                String sql1 = "INSERT INTO borrow\n" +
                        "VALUES(?, ?, ?, ?)\n";
                pstmt = conn.prepareStatement(sql1);
                pstmt.setInt(1, borrow.getCardId());
                pstmt.setInt(2, borrow.getBookId());
                pstmt.setLong(3, borrow.getBorrowTime());
                pstmt.setLong(4, 0);
                pstmt.executeUpdate();
            }
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "modifyBookInfo(Book book) success");
    }

    @Override
    public ApiResult returnBook(Borrow borrow) {
        Connection conn = connector.getConn();
        try {
            // 将事务隔离级别设置为Serializable以应对并发情形
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            // 查询借书记录
            String sql = "SELECT *\n" +
                    "FROM borrow\n" +
                    "WHERE card_id = ? AND book_id = ? AND borrow_time = ? AND return_time = 0\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, borrow.getCardId());
            pstmt.setInt(2, borrow.getBookId());
            pstmt.setLong(3, borrow.getBorrowTime());
            ResultSet res = pstmt.executeQuery();
            if (!res.next()) {        // 如果没有相应的借书记录或已经还书，则还书失败
                throw new Exception("There is no such borrowing record or the book has been returned.");
            } else {        // 如果有该借书记录，则更新还书时间及库存
                // 更新还书时间
                String sql1 = "UPDATE borrow\n" +
                        "SET return_time = ?\n" +
                        "WHERE card_id = ? AND book_id = ? AND borrow_time = ?\n";
                pstmt = conn.prepareStatement(sql1);
                pstmt.setLong(1, borrow.getReturnTime());
                pstmt.setInt(2, borrow.getCardId());
                pstmt.setInt(3, borrow.getBookId());
                pstmt.setLong(4, borrow.getBorrowTime());
                pstmt.executeUpdate();
                // 查询得到该书的库存
                sql = "SELECT *\n" +
                        "FROM book\n" +
                        "WHERE book_id = ?\n";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, borrow.getBookId());
                res = pstmt.executeQuery();
                int stock = 0;
                if (res.next()) {       // 如果查到这本书，则获取库存
                    stock = res.getInt("stock");
                } else {      // 如果查不到这本书，则还书操作失败
                    throw new Exception("The book doesn't exist in the library system.");
                }
                // 更新该书的库存
                sql = "UPDATE book\n" +
                        "SET stock = stock + 1\n" +
                        "WHERE book_id = ?\n";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, borrow.getBookId());
                pstmt.executeUpdate();
            }
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "returnBook(Borrow borrow) success");
    }

    @Override
    public ApiResult showBorrowHistory(int cardId) {
        Connection conn = connector.getConn();
        BorrowHistories borrowHistories = null;     // 通过borrowHistories对结果进行返回
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            String sql = "SELECT *\n" +
                    "FROM borrow\n" +
                    "WHERE card_id = ?\n" +
                    "ORDER BY borrow_time DESC, book_id ASC\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cardId);
            ResultSet res = pstmt.executeQuery();
            List<BorrowHistories.Item> items = new ArrayList<BorrowHistories.Item>();
            while (res.next()) {
                // 从查询结果中获取借书信息
                int bookId = res.getInt("book_id");
                long borrowTime = res.getLong("borrow_time");
                long returnTime = res.getLong("return_time");
                // 再从book中查询相关信息
                sql = "SELECT *\n" +
                        "FROM book\n" +
                        "WHERE book_id = ?\n";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, bookId);
                ResultSet res1 = pstmt.executeQuery();
                if (res1.next()) {
                    String category = res1.getString("category");
                    String title = res1.getString("title");
                    String press = res1.getString("press");
                    int publishYear = res1.getInt("publish_year");
                    String author = res1.getString("author");
                    double price = res1.getDouble("price");
                    // 创建Item对象，并将其加入到列表中
                    BorrowHistories.Item item = new BorrowHistories.Item();
                    item.setCardId(cardId);
                    item.setBookId(bookId);
                    item.setCategory(category);
                    item.setTitle(title);
                    item.setPress(press);
                    item.setPublishYear(publishYear);
                    item.setAuthor(author);
                    item.setPrice(price);
                    item.setBorrowTime(borrowTime);
                    item.setReturnTime(returnTime);
                    items.add(item);
                }
            }
            borrowHistories = new BorrowHistories(items);
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "returnBook(Borrow borrow) success", borrowHistories);
    }

    @Override
    public ApiResult registerCard(Card card) {
        Connection conn = connector.getConn();
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            // 查询该借书证是否已经存在
            String sql = "SELECT *\n" +
                    "FROM card\n" +
                    "WHERE name = ? AND department = ? AND type = ?\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, card.getName());
            pstmt.setString(2, card.getDepartment());
            pstmt.setString(3, card.getType().getStr());
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {     // 如果该借书证已经存在，则注册操作失败
                throw new Exception("The card already exists.");
            } else {      // 否则在card表中插入一条新记录
                sql = "INSERT INTO card\n" +
                        "VALUES(?, ?, ?)\n";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, card.getName());
                pstmt.setString(2, card.getDepartment());
                pstmt.setString(3, card.getType().getStr());
                pstmt.executeUpdate();
            }
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            try {
                // 查询当前card_id自增到哪里了
                PreparedStatement pstmt = null;
                String sql = "SELECT max(card_id) FROM card\n";
                pstmt = conn.prepareStatement(sql);
                ResultSet res1 = pstmt.executeQuery();
                int cur_id;
                if (res1.next()) {
                    cur_id = res1.getInt(1);
                } else {
                    cur_id = 0;
                }
                sql = "DBCC CHECKIDENT('card', RESEED, ?)\n";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cur_id);
                pstmt.executeUpdate();
            } catch (Exception e1) {
                // do nothing...
            }
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "registerCard(Card card) success");
    }

    @Override
    public ApiResult removeCard(int cardId) {
        Connection conn = connector.getConn();
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            // 查询该借书证是否存在
            String sql = "SELECT *\n" +
                    "FROM card\n" +
                    "WHERE card_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cardId);
            ResultSet res = pstmt.executeQuery();
            if (!res.next()) {        // 如果该借书证不存在，则报错
                throw new Exception("The card doesn't exist.");
            }
            // 查询该借书证是否有未归还的图书
            sql = "SELECT *\n" +
                    "FROM borrow\n" +
                    "WHERE card_id = ? AND return_time = 0\n";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cardId);
            res = pstmt.executeQuery();
            if (res.next()) {     // 如果该借书证有尚未归还的图书，则移除操作失败
                throw new Exception("There is some book that hasn't been returned.");
            } else {      // 否则将该借书证的信息从card表中移除
                sql = "DELETE FROM card\n" +
                        "WHERE card_id = ?\n";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cardId);
                pstmt.executeUpdate();
            }
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "removeCard(int cardId) success");
    }

    @Override
    public ApiResult showCards() {
        Connection conn = connector.getConn();
        CardList cardList = null;
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmt = null;
            // 查询所有的借书证
            String sql = "SELECT *\n" +
                    "FROM card\n" +
                    "ORDER BY card_id\n";
            pstmt = conn.prepareStatement(sql);
            ResultSet res = pstmt.executeQuery();
            List<Card> cards = new ArrayList<Card>();
            while (res.next()) {
                int cardId = res.getInt("card_id");
                String name = res.getString("name");
                String department = res.getString("department");
                String type = res.getString("type");
                Card card = new Card(cardId, name, department, Card.CardType.values(type));
                cards.add(card);
            }
            cardList = new CardList(cards);
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, "showCards() success", cardList);
    }

    @Override
    public ApiResult resetDatabase() {
        Connection conn = connector.getConn();
        try {
            Statement stmt = conn.createStatement();
            DBInitializer initializer = connector.getConf().getType().getDbInitializer();
            stmt.addBatch(initializer.sqlDropUsers());
            stmt.addBatch(initializer.sqlDropBorrow());
            stmt.addBatch(initializer.sqlDropBook());
            stmt.addBatch(initializer.sqlDropCard());
            stmt.addBatch(initializer.sqlCreateUsers());
            stmt.addBatch(initializer.sqlCreateCard());
            stmt.addBatch(initializer.sqlCreateBook());
            stmt.addBatch(initializer.sqlCreateBorrow());
            stmt.executeBatch();
            commit(conn);
        } catch (Exception e) {
            rollback(conn);
            return new ApiResult(false, e.getMessage());
        }
        return new ApiResult(true, null);
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commit(Connection conn) {
        try {
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
