package controller;

import org.junit.Assert;
import queries.ApiResult;
import queries.BorrowHistories;
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
@WebServlet("/TraversalRecordServlet")
public class TraversalRecordServlet extends HttpServlet {
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
        String id = request.getParameter("value");
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
        if (!id.equals("")) {
            res = library.showBorrowHistory(Integer.parseInt(id));
        } else {
            res = library.showBorrowHistory(0);
        }
        // 释放数据库
        boolean releaseStatus = connector.release();
        if (releaseStatus) {
            System.out.println("Successfully release database connection.");
        } else {
            System.out.println("Failed to release database connection.");
        }
        BorrowHistories bqRes = (BorrowHistories) res.payload;
        request.setAttribute("list", bqRes.getItems());
        request.getRequestDispatcher("record_query.jsp").forward(request, response);
    }
}
