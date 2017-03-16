package com.crocoro.servlet;

import com.crocoro.Config;
import com.crocoro.sql.JDBCUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 注册的处理类
 */
@WebServlet(name = "Regist", urlPatterns = "/reg.do")
public class Regist extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String token = request.getParameter("reqCode");
        if (Config.adminPasswd.equals(token)) {
            String uname = request.getParameter("uname");
            String passwd = request.getParameter("passwd");
            try {
                JDBCUtils jdbc = new JDBCUtils();
                ResultSet rs = jdbc.getRS("select * from user where uname='" + uname + "'");
                if (!rs.next()) {
                    PreparedStatement ps = jdbc.getPST("INSERT INTO user (`uname`, `passwd`,`token`) VALUES (?, ? ,md5(uuid()));");
                    ps.setString(1, uname);
                    ps.setString(2, passwd);
                    int cRows = jdbc.getUpdate(ps);
                    if (cRows == 1) {
                        out.println("200");
                    } else {
                        out.println("233");
                    }
                } else {
                    out.println("234");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
