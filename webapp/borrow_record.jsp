<%--
  Created by IntelliJ IDEA.
  User: 祝广程
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <!-- 引入 Bootstrap -->
    <script src="https://cdn.bootcss.com/jquery/2.2.4/jquery.min.js"></script>
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
    <!-- 引入 font-awesome -->
    <link href="https://cdn.bootcss.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <title>图书管理系统</title>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-sm-10">
            <!-- 列表展示-->
            <div class="table-responsive">
                <table class="table table-hover ">
                    <thead>
                    <tr>
                        <th>书号</th>
                        <th>类别</th>
                        <th>书名</th>
                        <th>出版社</th>
                        <th>年份</th>
                        <th>作者</th>
                        <th>价格</th>
                        <th>借书时间(Unix)</th>
                        <th>还书时间(Unix)</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${list}" var="book">
                        <tr>
                            <td>${book.bookId}</td>
                            <td>${book.category}</td>
                            <td>${book.title}</td>
                            <td>${book.press}</td>
                            <td>${book.publishYear}</td>
                            <td>${book.author}</td>
                            <td>${book.price}</td>
                            <td>${book.borrowTime}</td>
                            <td>${book.returnTime}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>

</html>
