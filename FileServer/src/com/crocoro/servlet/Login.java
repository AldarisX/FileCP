package com.crocoro.servlet;

import com.crocoro.Config;
import com.crocoro.sql.JDBCUtils;

import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 管理用户登陆
 */
@WebServlet(name = "login", urlPatterns = "/login.do")
public class Login extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");

        String uname = request.getParameter("uname");
        String passwd = request.getParameter("passwd");
        try {
            //查询数据库
            JDBCUtils jdbc = new JDBCUtils();
            PreparedStatement ps = jdbc.getPST("select * from user where uname=? and passwd=?");
            ps.setString(1, uname);
            ps.setString(2, passwd);
            ResultSet rs = jdbc.getQuery(ps);
            if (rs.next()) {
                //设置session
                request.getSession().setAttribute("uname", uname);
                //如果没有用户目录就创建用户目录
                File userDir = new File(Config.warLoc + "/upload/" + uname);
                if (!userDir.exists()) {
                    userDir.mkdirs();
                }
                response.sendRedirect("showfile.jsp");
            } else {
                PrintWriter out = response.getWriter();
                out.println("用户名或密码错误");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }
}
