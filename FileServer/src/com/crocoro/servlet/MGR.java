package com.crocoro.servlet;

import com.crocoro.Config;
import com.crocoro.model.User;
import com.crocoro.sql.JDBCUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 管理后台
 */
@WebServlet(name = "MGR", urlPatterns = "/mgr.do")
public class MGR extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uname;
        String token = request.getParameter("token");
        HttpSession session = request.getSession();
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

        String code = request.getParameter("code");
        switch (code) {
            case "arrangement":
                arrangement();
                break;
            case "check":
                break;
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void arrangement() {
        try {
            JDBCUtils jdbc = new JDBCUtils();
            PreparedStatement filesPS = jdbc.getPST("select * from file");
            ResultSet files = jdbc.getQuery(filesPS);
            while (files.next()) {
                String path = files.getString("path");
                String fileName = files.getString("name");
                String md5 = files.getString("md5");
                String uname = files.getString("uname");

                File uFile = new File(Config.warLoc + "/upload/" + uname + path + fileName);
                if (!uFile.exists()) {
                    int fID = files.getInt("id");
                    PreparedStatement deletePS = jdbc.getPST("delete from file where id=?");
                    deletePS.setInt(1, fID);
                    jdbc.getUpdate(deletePS);
                }

                PreparedStatement filePS = jdbc.getPST("select * from file where path=? and name=? and md5=? and uname=? order by utime;");
                filePS.setString(1, path);
                filePS.setString(2, fileName);
                filePS.setString(3, md5);
                filePS.setString(4, uname);
                ResultSet sameFiles = jdbc.getQuery(filePS);
                sameFiles.last();//移到最后一行
                int sameCount = sameFiles.getRow();
                sameFiles.beforeFirst();
                if (sameCount > 1) {
                    for (int i = 1; i < sameCount; i++) {
                        sameFiles.next();
                        int fileID = sameFiles.getInt("id");
                        PreparedStatement deletePS = jdbc.getPST("delete from file where id=?");
                        deletePS.setInt(1, fileID);
                        jdbc.getUpdate(deletePS);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
