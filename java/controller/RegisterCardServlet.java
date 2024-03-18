package controller;

import entities.User;
import org.junit.Assert;
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
@WebServlet("/RegisterCardServlet")
public class RegisterCardServlet extends HttpServlet {
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
        String account = (String) request.getSession().getAttribute("account");
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
        request.setAttribute("account", account);
        User user = library.queryUser(account);
        if(request.getParameter("method").equals("error")){
            String message = (String)this.getServletContext().getAttribute("msg");
            request.setAttribute("msg", message);
        }
        if (user.getCardId() == 0) {        // 如果该用户没有注册借书证，则要求其注册一个
            request.getRequestDispatcher("register_card.jsp").forward(request, response);
        }
        switch (request.getParameter("method")) {
            case "borrow":
                request.getRequestDispatcher("BorrowBookServlet?method=list").forward(request, response);
                break;
            case "myBook":
                request.getRequestDispatcher("MyBookServlet?method=list").forward(request, response);
                break;
            case "record":
                request.getRequestDispatcher("BorrowRecordServlet").forward(request, response);
                break;
        }
    }
}
