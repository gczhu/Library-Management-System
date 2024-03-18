<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>图书管理系统</title>
</head>
<body>
    <form action="loginServlet" method="post">
        姓名：<input type="text" name="uname"> <br>
        密码：<input type="password" name="upwd"> <br>
        <button>登录</button> <span style="color: red;font-size: 12px"><%=request.getAttribute("msg")%>></span>
    </form>
</body>
</html>
