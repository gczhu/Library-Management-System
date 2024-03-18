package controller;

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

@WebServlet("/TraversalBookServlet")
public class TraversalBookServlet extends HttpServlet {
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
            case "list":
                res = library.queryBook(new BookQueryConditions());
                break;
            case "error":
                res = library.queryBook(new BookQueryConditions());
                String message = (String)this.getServletContext().getAttribute("msg");
                this.getServletContext().setAttribute("msg", "");
                request.setAttribute("msg", message);
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
        request.getRequestDispatcher("book_manage.jsp").forward(request, response);
    }
}
