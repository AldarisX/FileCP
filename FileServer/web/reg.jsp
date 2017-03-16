<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
%>
<html>
<head>
    <title>注册</title>
    <script src="js/jquery-3.1.1.min.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(document).ready(function () {

        });

        function subReg() {
            $.post("reg.do", {
                uname: $("#f_uname").val(),
                passwd: $("#f_passwd").val(),
                reqCode: $("#f_reqCode").val()
            }, function (result) {
                if (result == 200) {
                    alert("注册成功");
                } else if (result == 234) {
                    alert("已存在同用户名");
                } else if (result == 233) {
                    alert("数据库错误,请检查");
                }
            });
        }
    </script>
</head>
<body>
<h1>注册</h1>
<form id="r_form" method="post" action="reg.do" onsubmit="return subReg();">
    用户名:&nbsp;<input id="f_uname" name="uname" type="text"/><br>
    密&nbsp;&nbsp;&nbsp;&nbsp;码:&nbsp;<input id="f_passwd" name="passwd" type="text"/><br>
    确认码:&nbsp;<input id="f_reqCode" name="reqCode" type="text"/><br>
    <input type="button" value="确定" onclick="subReg()">
</form>
</body>
</html>
