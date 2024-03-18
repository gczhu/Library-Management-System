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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

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
        // 接收客户端传递的参数
        String account = request.getParameter("account");
        String password = request.getParameter("password");
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
        // 根据account查找user信息
        User user = library.queryUser(account);
        // 释放数据库
        boolean releaseStatus = connector.release();
        if (releaseStatus) {
            System.out.println("Successfully release database connection.");
        } else {
            System.out.println("Failed to release database connection.");
        }
        if (user.getAccount() == null || !password.equals(user.getPassword())) {     // 如果找不到该用户或密码不匹配
            // 提示错误信息
            request.setAttribute("msg", "用户名或密码有误");
            // 跳转到login.jsp
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        // 登录成功
        // 设置登录信息到session作用域
        request.getSession().setAttribute("account", account);
        // 根据user的role跳转到不同的首页
        if (user.getRole() == User.UserRole.User) {
            response.sendRedirect("user_index.jsp");
        } else {
            response.sendRedirect("admin_index.jsp");
        }
    }
}
