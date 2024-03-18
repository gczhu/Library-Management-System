package controller;

import entities.User;
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
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

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
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        // 首先判断账号密码长度是否符合要求
        if (account.length() > 20 || account.length() < 6) {
            // 提示错误信息
            request.setAttribute("msg", "用户名长度应在6-20个字符之间");
            // 跳转到login.jsp
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        if (password1.length() > 20 || password1.length() < 6) {
            // 提示错误信息
            request.setAttribute("msg", "密码长度应在6-20个字符之间");
            // 跳转到login.jsp
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        // 然后判断两次输入的密码是否一致
        if (!password1.equals(password2)) {
            // 提示错误信息
            request.setAttribute("msg", "两次输入的密码不一致");
            // 跳转到login.jsp
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        // 如果输入的账号密码合法，则到数据库中进行校验
        // 连接数据库
        try {
            connector = new DatabaseConnector(connectConfig);
            library = new LibraryManagementSystemImpl(connector);
            System.out.println("Successfully init class LoginServlet.");
            boolean connStatus = connector.connect();
            Assert.assertTrue(connStatus);
            System.out.println("Successfully connect to database.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        // 创建一个用户(注：注册页面只能进行普通用户的注册，管理员的注册由数据库管理员来操作)
        User user = new User(account, password1, User.UserRole.User, 0);
        // 调用newUser方法将该用户插入到数据库中，若数据库中存在同名用户，则插入失败
        ApiResult res = library.newUser(user);
        if (!res.ok) {
            // 提示错误信息
            request.setAttribute("msg", "该用户名已经存在");
            // 跳转到login.jsp
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        // 释放数据库
        boolean releaseStatus = connector.release();
        if (releaseStatus) {
            System.out.println("Successfully release database connection.");
        } else {
            System.out.println("Failed to release database connection.");
        }
        // 注册成功
        // 提示成功信息
        request.setAttribute("msg", "注册成功");
        // 跳转到register.jsp
        request.getRequestDispatcher("register.jsp").forward(request, response);
        return;
    }
}
