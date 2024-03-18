package controller;

import entities.Book;
import org.junit.Assert;
import queries.ApiResult;
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

@WebServlet("/ModifyBookServlet")
public class ModifyBookServlet extends HttpServlet {

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
        // 接收前端参数
        String category = request.getParameter("category");
        String title = request.getParameter("title");
        String press = request.getParameter("press");
        Integer publish_year = null;
        try {
            publish_year = Integer.parseInt(request.getParameter("publish_year"));
            if (publish_year < 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            // 提示错误信息
            this.getServletContext().setAttribute("msg", "年份非法");
            response.sendRedirect("TraversalBookServlet?method=error");
            return;
        }
        String author = request.getParameter("author");
        Double price = null;
        try {
            price = Double.parseDouble(request.getParameter("price"));
            if (price < 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            this.getServletContext().setAttribute("msg", "价格非法");
            response.sendRedirect("TraversalBookServlet?method=error");
            return;
        }
        Integer stock = 0;
        try {
            stock = Integer.parseInt(request.getParameter("stock"));
            if (stock < 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            this.getServletContext().setAttribute("msg", "库存非法");
            response.sendRedirect("TraversalBookServlet?method=error");
            return;
        }
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
        // 修改该书除库存和id外的其他信息
        Book book = new Book(category, title, press, publish_year, author, price, 0);
        book.setBookId(Integer.parseInt(request.getParameter("id")));
        ApiResult modifyRes = library.modifyBookInfo(book);
        // 根据id查找该书的原始库存
        ApiResult res1 = library.queryStock(book.getBookId());
        Integer pre_stock = (Integer) res1.payload;
        // 修改该书的库存
        ApiResult stockRes = library.incBookStock(book.getBookId(), stock - pre_stock);
        // 释放数据库
        boolean releaseStatus = connector.release();
        if (releaseStatus) {
            System.out.println("Successfully release database connection.");
        } else {
            System.out.println("Failed to release database connection.");
        }
        this.getServletContext().setAttribute("msg", "");
        response.sendRedirect("TraversalBookServlet?method=list");
        return;
    }
}
