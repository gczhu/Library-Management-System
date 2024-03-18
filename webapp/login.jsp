<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录图书管理系统</title>
    <style>
        * {
            margin: 0;
            padding: 0;
        }

        a {
            text-decoration: none;
        }

        input,
        button {
            background: transparent;
            border: 0;
            outline: none;
        }

        body {
            height: 100vh;
            background-image: url(pictures/login.jpg);
            background-size: cover;
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 16px;
            color: black;
        }

        .loginBox {
            width: 498px;
            height: 452px;
            background-color: #f5f5f5;
            margin: 100px auto;
            border-radius: 10px;
            box-shadow: 0 15px 25px 0 rgba(0, 0, 0, .6);
            padding: 40px;
            box-sizing: border-box;
        }

        h2 {
            text-align: center;
            color: black;
            margin-bottom: 30px;
            font-family: 'Courier New', Courier, monospace;
        }

        .item {
            height: 45px;
            border-bottom: 1px solid black;
            margin-bottom: 10px;
            position: relative;
        }

        .item input {
            width: 100%;
            height: 100%;
            color: black;
            padding-top: 20px;
            box-sizing: border-box;
        }

        .item input:focus+label,
        .item input:valid+label {
            top: 0px;
            font-size: 2px;
        }

        .item label {
            position: absolute;
            left: 0;
            top: 12px;
            transition: all 0.5s linear;
        }

        .btn1 {
            border: 1px solid black;
            border-radius: 5px;
            cursor: pointer;
            padding: 10px 20px;
            color: black;
            position: relative;
            overflow: hidden;
            letter-spacing: 2px;
            display: block;
            margin-top: 45px;
            margin-bottom: 0px;
            margin-left: auto;
            margin-right: auto;
        }

        .btn2 {
            border: 1px solid black;
            border-radius: 5px;
            cursor: pointer;
            padding: 10px 20px;
            color: black;
            position: relative;
            overflow: hidden;
            letter-spacing: 2px;
            display: block;
            margin: 0 auto;
        }

        button:hover {
            background-color: #fcfcfc;
        }

        .error {
            color: red;
            font-size: 12px;
        }

    </style>
</head>

<body>
<div class="loginBox">
    <h2>login</h2>
    <br>
    <form action="LoginServlet" method="post">
        <div class="item">
            <input type="text" name="account" required>
            <label>account</label>
        </div>
        <br>
        <div class="item">
            <input type="password" name="password" required>
            <label>password</label>
        </div>
        <div style="width: 300px; height: 12px;">
            <span class="error">${msg}</span>
        </div>
        <button class="btn1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;LOGIN&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <span></span>
            <span></span>
            <span></span>
            <span></span>
        </button>
    </form>
    <form action="SignUpServlet" method="post">
        <br>
        <button class="btn2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SIGN UP&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <span></span>
            <span></span>
            <span></span>
            <span></span>
        </button>
    </form>
</div>
</body>
</html>