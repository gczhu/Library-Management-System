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
            <!-- 顶部搜索部分 -->
            <div class="panel panel-default">
                <div class="panel-heading">借书记录查询</div>
                <div class="panel-body">
                    <form role="form" class="form-inline" action="TraversalRecordServlet" method="post">
                        <div class="form-group">
                            <label>卡号：</label>
                            <input type="text" class="form-control" name="value" placeholder="字段值" maxlength="12" style="width: 130px">
                        </div>
                        <div class="form-group " style="margin-left: 20px">
                            <button type="submit" class="btn btn-info ">
										<span style="margin-right: 5px"
                                              class="glyphicon glyphicon-search" aria-hidden="true">
										</span>开始搜索
                            </button>
                        </div>
                    </form>
                </div>
            </div>
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
                        <th>借书时间</th>
                        <th>还书时间</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${list}" var="borrow">
                        <tr>
                            <td>${borrow.bookId}</td>
                            <td>${borrow.category}</td>
                            <td>${borrow.title}</td>
                            <td>${borrow.press}</td>
                            <td>${borrow.publishYear}</td>
                            <td>${borrow.author}</td>
                            <td>${borrow.price}</td>
                            <td>${borrow.borrowTime}</td>
                            <td>${borrow.returnTime}</td>
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
