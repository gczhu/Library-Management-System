package controller;

import entities.Borrow;
import entities.User;
import org.junit.Assert;
import queries.ApiResult;
import queries.BookQueryConditions;
import queries.BookQueryResults;
import service.LibraryManagementSystem;
import service.LibraryManagementSystemImpl;
import utils.ConnectConfig;
import utils.DatabaseConnector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 祝广程
 * @version 1.0
 */
@WebServlet("/BorrowBookServlet")
public class BorrowBookServlet extends HttpServlet {
    private DatabaseConnector connector;
    private LibraryManagementSystem library;

    private static ConnectConfig connectConfig = null;

    static {
        try {
            // parse connection config from "resources/application.yaml"
            connectConfig = new ConnectConfig();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置客户端的编码格式
        request.setCharacterEncoding("UTF-8");
        // 连接数据库
        try {
            connector = new DatabaseConnector(connectConfig);
            library = new LibraryManagementSystemImpl(connector);
            boolean connStatus = connector.connect();
            Assert.assertTrue(connStatus);
            System.out.println("Successfully connect to database.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        ApiResult res = null;
        switch (request.getParameter("method")) {
            case "borrow":
                String account = (String) request.getSession().getAttribute("account");
                User user = library.queryUser(account);
                int cardId = user.getCardId();
                int bookId = Integer.parseInt(request.getParameter("id"));
                Borrow borrow = new Borrow(cardId, bookId);
                borrow.resetBorrowTime();
                borrow.setReturnTime(0);
                ApiResult res1 = library.borrowBook(borrow);
                if (!res1.ok) {
                    if (res1.message.equals("The customer borrowed this book but hasn't return it.")) {
                        request.setAttribute("msg", "您此前借过这本书但尚未归还，借书失败");
                    } else if (res1.message.equals("The book is out of stock.")) {
                        request.setAttribute("msg", "库存不足，借书失败");
                    }
                } else {
                    request.setAttribute("msg", "借书成功");
                }
            case "list":
                res = library.queryBook(new BookQueryConditions());
                break;
            case "search":
                String key = request.getParameter("key");
                String value1 = request.getParameter("value1");
                String value2 = null;
                BookQueryConditions conditions = new BookQueryConditions();
                switch (key) {
                    case "category":
                        if (value1 != "") {
                            conditions.setCategory(value1);
                        }
                        res = library.queryBook(conditions);
                        break;
                    case "title":
                        if (value1 != "") {
                            conditions.setTitle(value1);
                        }
                        res = library.queryBook(conditions);
                        break;
                    case "press":
                        if (value1 != "") {
                            conditions.setPress(value1);
                        }
                        res = library.queryBook(conditions);
                        break;
                    case "publish_year":
                        value2 = request.getParameter("value2");
                        if (value1 != "") {
                            conditions.setMinPublishYear(Integer.valueOf(value1));
                        }
                        if (value2 != "") {
                            conditions.setMaxPublishYear(Integer.valueOf(value2));
                        }
                        res = library.queryBook(conditions);
                        break;
                    case "author":
                        if (value1 != "") {
                            conditions.setAuthor(value1);
                        }
                        res = library.queryBook(conditions);
                        break;
                    case "price":
                        value2 = request.getParameter("value2");
                        if (value1 != "") {
                            conditions.setMinPrice(Double.valueOf(value1));
                        }
                        if (value2 != "") {
                            conditions.setMaxPrice(Double.valueOf(value2));
                        }
                        res = library.queryBook(conditions);
                        break;
                }
                break;
        }
        // 释放数据库
        boolean releaseStatus = connector.release();
        if (releaseStatus) {
            System.out.println("Successfully release database connection.");
        } else {
            System.out.println("Failed to release database connection.");
        }
        BookQueryResults bqRes = (BookQueryResults) res.payload;
        request.setAttribute("list", bqRes.getResults());
        request.getRequestDispatcher("borrow.jsp").forward(request, response);
    }
}
