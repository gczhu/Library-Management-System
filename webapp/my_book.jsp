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
                        <th>操作</th>
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
                            <td>
                                <div class="btn-group">
                                    <button type="button" class="btn btn-default "
                                            data-id="${book.bookId}"
                                            data-category="${book.category}"
                                            data-title="${book.title}"
                                            data-press="${book.press}"
                                            data-author="${book.author}"
                                            data-publish_year="${book.publishYear}"
                                            data-price="${book.price}"
                                            data-stock="${book.borrowTime}"
                                            data-toggle="modal"
                                            data-target="#returnBookModal">
                                        <i class="fa fa-mouse-pointer">还书</i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <script>
                    var errorMessage = '${msg}';
                    if (errorMessage) {
                        alert(errorMessage);
                    }
                </script>
                <!-- return框 -->
                <form method="post" action="MyBookServlet?method=return" class="form-horizontal" style="margin-top: 0px" role="form"
                      style="margin: 20px;">
                    <div class="modal fade" id="returnBookModal" tabindex="-1"
                         role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal"
                                            aria-hidden="true">x</button>
                                    <h4 class="modal-title">图书信息</h4>
                                </div>
                                <div class="modal-body">
                                    <form class="form-horizontal" role="form">
                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">书号</label>
                                            <div class="col-sm-9">
                                                <input type="text" readonly required class="form-control"
                                                       name="id" id="id">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">类别</label>
                                            <div class="col-sm-9">
                                                <input type="text" readonly required class="form-control" name="category"
                                                       id="category">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">书名</label>
                                            <div class="col-sm-9">
                                                <input type="text" readonly required class="form-control" name="title"
                                                       id="title" value="">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">出版社</label>
                                            <div class="col-sm-9">
                                                <input type="text" readonly required class="form-control" name="press"
                                                       id="press" value="">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">年份</label>
                                            <div class="col-sm-9">
                                                <input type="text" readonly required class="form-control" name="publish_year"
                                                       id="publish_year" value="">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">作者</label>
                                            <div class="col-sm-9">
                                                <input type="text" readonly required class="form-control" name="author"
                                                       id="author" value="">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">价格</label>
                                            <div class="col-sm-9">
                                                <input type="text" readonly required class="form-control" name="price" id="price"
                                                       value="">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">借书时间</label>
                                            <div class="col-sm-9">
                                                <input type="text" readonly required class="form-control" name="stock" id="stock"
                                                       value="">
                                            </div>
                                        </div>
                                    </form>
                                </div>
                                <div class="modal-footer">
                                    <button type="submit" class="btn btn-primary">确认</button>
                                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    $('#returnBookModal').on('show.bs.modal', function(event) {
        var button = $(event.relatedTarget)
        var id = button.data('id')
        var category = button.data('category')
        var title = button.data('title')
        var press = button.data('press')
        var publish_year = button.data('publish_year')
        var author = button.data('author')
        var price = button.data('price')
        var stock = button.data('stock')
        var modal = $(this)

        modal.find('.modal-title').text('归还图书')
        modal.find('#id').val(id)
        modal.find('#category').val(category)
        modal.find('#title').val(title)
        modal.find('#press').val(press)
        modal.find('#publish_year').val(publish_year)
        modal.find('#author').val(author)
        modal.find('#price').val(price)
        modal.find('#stock').val(stock)
    })

</script>

</body>

</html>
