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

@WebServlet("/AddBookServlet")
public class AddBookServlet extends HttpServlet {

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
        Integer stock = null;
        try {
            stock = Integer.parseInt(request.getParameter("stock"));
            if (stock < 0) {
                throw new Exception();
            }
        } catch (Exception e) {
            this.getServletContext().setAttribute("msg", "初始库存非法");
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
        ApiResult res = library.storeBook(new Book(category, title, press, publish_year, author, price, stock));
        // 释放数据库
        boolean releaseStatus = connector.release();
        if (releaseStatus) {
            System.out.println("Successfully release database connection.");
        } else {
            System.out.println("Failed to release database connection.");
        }
        if (!res.ok) {
            this.getServletContext().setAttribute("msg", "该书已存在，入库失败");
            response.sendRedirect("TraversalBookServlet?method=error");
            return;
        }
        response.sendRedirect("TraversalBookServlet?method=list");
        return;
    }
}
