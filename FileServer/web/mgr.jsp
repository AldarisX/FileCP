<%@ page import="com.crocoro.model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    String uname;
    String token = request.getParameter("token");
    if (token != null) {
        User u = new User();
        u.getUserByToken(token);
        if (u.getUname() == null || u.getLevel() > 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        uname = u.getUname();
        session.setAttribute("uname", uname);
    } else {
        uname = (session.getAttribute("uname") != null) ? (String) session.getAttribute("uname") : "";
        if (uname.equals("")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }
%>
<html>
<head>
    <title>管理页面</title>
</head>
<body>
<a href="mgr.do?code=arrangement"> 整理数据库</a><br>
</body>
</html>
