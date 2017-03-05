<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
%>
<html>
<head>
    <title>登陆</title>
</head>
<body>
<form method="post" action="login.do">
    <table border="0">
        <tr>
            <td>用户名:<input type="text" name="uname" prefix="输入用户名"/></td>
        </tr>
        <tr>
            <td>密&nbsp;码:<input type="password" name="passwd" prefix="输入密码"/></td>
        </tr>
        <tr>
            <td><input type="submit" value="登陆"/></td>
        </tr>
    </table>
</form>
</body>
</html>
