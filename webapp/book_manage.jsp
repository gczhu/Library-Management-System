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
                <div class="panel-heading">图书管理</div>
                <div class="panel-body">
                    <form role="form" class="form-inline" action="TraversalBookServlet?method=search" method="post">
                        <div class="form-group">
                            <label>字段：</label>
                            <select name="key" class="form-control" id="select-option">
                                <option value="category">类别</option>
                                <option value="title">书名</option>
                                <option value="press">出版社</option>
                                <option value="publish_year">年份范围</option>
                                <option value="author">作者</option>
                                <option value="price">价格范围</option>
                            </select>
                        </div>
                        <div class="form-group" style="margin-left: 20px">
                            <label>值：</label>
                            <input type="text" class="form-control" name="value1" placeholder="字段值" maxlength="12" style="width: 68px">
                        </div>
                        <div class="form-group" id="input-container" style="display: none">
                            -
                            <input type="text" class="form-control" name="value2" placeholder="字段值" maxlength="12" style="width: 68px">
                        </div>
                        <script>
                            // 获取select元素
                            var selectElm = document.getElementById("select-option");
                            // 获取 input container 元素
                            var inputContainerElm = document.getElementById("input-container");
                            // 监听 select 元素的变化
                            selectElm.addEventListener("change", function() {
                                // 如果选择了 "price"或"publish_year"，显示 input container 元素
                                if (selectElm.value === "price" || selectElm.value === "publish_year") {
                                    inputContainerElm.style.display = "inline-block";
                                } else {
                                    // 否则隐藏 input container 元素
                                    inputContainerElm.style.display = "none";
                                }
                            });
                        </script>
                        <div class="form-group " style="margin-left: 20px">
                            <button type="submit" class="btn btn-info ">
										<span style="margin-right: 5px"
                                              class="glyphicon glyphicon-search" aria-hidden="true">
										</span>开始搜索
                            </button>
                        </div>
                        <div class="form-group " style="margin-left: 48px">
                            <button type="button" class="btn btn-default" data-toggle="modal" data-target="#addBookModel">
										<span style="margin-right: 5px" class="" aria-hidden="true">
											<i class="fa fas fa-plus-circle">图书入库</i>
											</span>
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
                        <th>库存</th>
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
                            <td>${book.stock}</td>
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
                                            data-stock="${book.stock}"
                                            data-toggle="modal"
                                            data-target="#updateBookModal">
                                        <i class="fa fas fa-edit">修改</i>
                                    </button>

                                    <button type="button" class="btn btn-danger "
                                            data-id="${book.bookId}" data-toggle="modal"
                                            onclick="" data-target="#delBookModal">
                                        <i class="fa fas fa-trash">删除</i>
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
                <!-- add框 -->
                <form method="post" action="AddBookServlet" class="form-horizontal" style="margin-top: 0px"
                      role="form" style="margin: 20px;">
                    <div class="modal fade" id="addBookModel" tabindex="-1"
                         role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal"
                                            aria-hidden="true">x</button>
                                    <h4 class="modal-title">图书入库</h4>
                                </div>
                                <div class="modal-body">
                                    <form class="form-horizontal" role="form" onsubmit="return validateForm()">
                                        <div class="form-group">
                                            <label for="category" class="col-sm-3 control-label">类别</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control"  id="category"
                                                       name="category" placeholder="请输入类别">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label for="title" class="col-sm-3 control-label">书名</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" id="title"
                                                       name="title" value="" placeholder="请输入书名">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label for="press" class="col-sm-3 control-label">出版社</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" id="press"
                                                       name="press" value="" placeholder="请输入出版社">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label for="publish_year" class="col-sm-3 control-label">年份</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" id="publish_year"
                                                       name="publish_year" value="" placeholder="请输入年份">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label for="author" class="col-sm-3 control-label">作者</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" id="author"
                                                       name="author" value="" placeholder="请输入作者">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label for="price" class="col-sm-3 control-label">价格</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" id="price"
                                                       name="price" value="" placeholder="请输入价格">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label for="stock" class="col-sm-3 control-label">库存</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" id="stock"
                                                       name="stock" value="" placeholder="请输入库存">
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
                    </div>
                </form>

                <!-- update框 -->
                <form method="post" action="ModifyBookServlet" class="form-horizontal" style="margin-top: 0px" role="form"
                      style="margin: 20px;">
                    <div class="modal fade" id="updateBookModal" tabindex="-1"
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
                                            <label class="col-sm-3 control-label">ID</label>
                                            <div class="col-sm-9">
                                                <input type="text" readonly required class="form-control"
                                                       name="id" id="id">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">类别</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" name="category"
                                                       id="category">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">书名</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" name="title"
                                                       id="title" value="">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">出版社</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" name="press"
                                                       id="press" value="">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">年份</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" name="publish_year"
                                                       id="publish_year" value="">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">作者</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" name="author"
                                                       id="author" value="">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">价格</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" name="price" id="price"
                                                       value="">
                                            </div>
                                        </div>

                                        <div class="form-group">
                                            <label class="col-sm-3 control-label">库存</label>
                                            <div class="col-sm-9">
                                                <input type="text" required class="form-control" name="stock" id="stock"
                                                       value="">
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
                    </div>
                </form>

                <!-- delete框 -->
                <form method="post" action="DeleteBookServlet"
                      class="form-horizontal" style="margin-top: 0px" role="form"
                      id="form_data" style="margin: 20px;">
                    <div class="modal fade" id="delBookModal" tabindex="-1"
                         role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal"
                                            aria-hidden="true">×</button>
                                    <h4 class="modal-title" id="myModalLabel">用户信息</h4>
                                </div>
                                <div class="modal-body">
                                    <form class="form-horizontal" role="form">
                                        <div class="form-group">
                                            <div class="col-sm-9">
                                                <h3 class="col-sm-18 control-label" id="deleteLabel">删除信息</h3>
                                                <input type="hidden" class="form-control" id="tab"
                                                       name="tab" placeholder="" value="dor_admin"> <input
                                                    type="hidden" class="form-control" id="id"
                                                    name="id" placeholder="">
                                            </div>
                                        </div>
                                    </form>
                                </div>
                                <div class="modal-footer">
                                    <button type="submit" class="btn btn-danger">删除</button>
                                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                    <span id="tip"> </span>
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
    $('#updateBookModal').on('show.bs.modal', function(event) {
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

        modal.find('.modal-title').text('修改图书信息')
        modal.find('#id').val(id)
        modal.find('#category').val(category)
        modal.find('#title').val(title)
        modal.find('#press').val(press)
        modal.find('#publish_year').val(publish_year)
        modal.find('#author').val(author)
        modal.find('#price').val(price)
        modal.find('#stock').val(stock)
    })

    $('#delBookModal').on('show.bs.modal', function(event) {
        var button = $(event.relatedTarget)
        var id = button.data('id')
        var modal = $(this)
        modal.find('.modal-title').text('删除图书信息')
        modal.find('#deleteLabel').text('是否删除ID为  ' + id + ' 的图书')
        modal.find('#id').val(id)
    })
</script>

</body>

</html>
