package controller;

import org.junit.Assert;
import queries.ApiResult;
import queries.CardList;
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
@WebServlet("/TraversalCardServlet")
public class TraversalCardServlet extends HttpServlet {
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
            case "error":
                res = library.showCards();
                String message = (String)this.getServletContext().getAttribute("msg");
                this.getServletContext().setAttribute("msg", "");
                request.setAttribute("msg", message);
                break;
            case "search":
                if(request.getParameter("value") != ""){
                    res = library.queryCard(Integer.parseInt(request.getParameter("value")));
                    break;
                }
            case "list":
                res = library.showCards();
                break;
        }
        // 释放数据库
        boolean releaseStatus = connector.release();
        if (releaseStatus) {
            System.out.println("Successfully release database connection.");
        } else {
            System.out.println("Failed to release database connection.");
        }
        CardList cqRes = (CardList)res.payload;
        request.setAttribute("list", cqRes.getCards());
        request.getRequestDispatcher("card_manage.jsp").forward(request, response);
    }
}
