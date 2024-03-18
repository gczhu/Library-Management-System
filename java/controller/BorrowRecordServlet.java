package controller;

import entities.User;
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
import java.util.List;

/**
 * @author 祝广程
 * @version 1.0
 */
@WebServlet("/BorrowRecordServlet")
public class BorrowRecordServlet extends HttpServlet {
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
        String account = (String) request.getSession().getAttribute("account");
        User user = library.queryUser(account);
        int cardId = user.getCardId();
        ApiResult res1 = library.showBorrowHistory(cardId);
        BorrowHistories bqRes = (BorrowHistories) res1.payload;
        List<BorrowHistories.Item> items = bqRes.getItems();
        request.setAttribute("list", items);
        // 释放数据库
        boolean releaseStatus = connector.release();
        if (releaseStatus) {
            System.out.println("Successfully release database connection.");
        } else {
            System.out.println("Failed to release database connection.");
        }
        request.getRequestDispatcher("borrow_record.jsp").forward(request, response);
    }
}
