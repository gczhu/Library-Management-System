package controller;

import entities.Card;
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

@WebServlet("/AddCardServlet_User")
public class AddCardServlet_User extends HttpServlet {

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
        String name = request.getParameter("name");
        String department = request.getParameter("department");
        String type = request.getParameter("type");
        if (type.equals("教师")) {
            type = "T";
        } else if(type.equals("学生")) {
            type = "S";
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
        ApiResult res = library.registerCard(new Card(0, name, department, Card.CardType.values(type)));
        if (!res.ok) {
            this.getServletContext().setAttribute("msg", "该用户已有借书证，注册失败");
            response.sendRedirect("RegisterCardServlet?method=error");
            return;
        }else{
            int id = library.queryCard(name, department, type);
            library.bondCard(account, id);
        }
        // 释放数据库
        boolean releaseStatus = connector.release();
        if (releaseStatus) {
            System.out.println("Successfully release database connection.");
        } else {
            System.out.println("Failed to release database connection.");
        }
        response.sendRedirect("RegisterCardServlet?method=borrow");
        return;
    }
}
