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
            <script>
                var errorMessage = '${msg}';
                if (errorMessage) {
                    alert(errorMessage);
                }
            </script>
            <!-- add框 -->
            <form method="post" action="AddCardServlet_User" class="form-horizontal" style="margin-top: 0px" role="form"
                  style="margin: 20px;">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal"
                                    aria-hidden="true">x</button>
                            <h4 class="modal-title">注册借书证</h4>
                        </div>
                        <div class="modal-body">
                            <form class="form-horizontal" role="form">

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">姓名</label>
                                    <div class="col-sm-9">
                                        <input type="text" required class="form-control" id="name"
                                               name="name" value="" placeholder="请输入姓名">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">单位</label>
                                    <div class="col-sm-9">
                                        <input type="text" required class="form-control" id="department"
                                               name="department" value="" placeholder="请输入单位">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">身份</label>
                                    <div class="col-sm-9">
                                        <input type="radio" checked value="学生" class="type"
                                               name="type"> 学生
                                        &nbsp;&nbsp;&nbsp;<input type="radio" value="教师" class="type"
                                                                 name="type"> 教师
                                    </div>
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-primary">提交</button>
                            <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
