package com.crocoro.servlet;

import com.crocoro.Config;
import com.crocoro.model.User;
import com.crocoro.model.UserFile;
import net.sf.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * 显示文件
 */
@WebServlet(name = "ShowFile", urlPatterns = "/showfile.json")
public class ShowFile extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");

        String uname;
        String token = request.getParameter("token");
        if (token != null) {
            User u = new User();
            u.getUserByToken(token);
            if (u.getUname() == null) {
                response.reset();
                return;
            }
            uname = u.getUname();
        } else {
            //取得用户名
            uname = (request.getSession().getAttribute("uname") != null) ? (String) request.getSession().getAttribute("uname") : "";
            if (uname.equals("")) {
                response.reset();
                return;
            }
        }

        String loc = URLDecoder.decode(request.getParameter("loc"), "UTF-8");
        if (loc == null) {
            loc = "/";
        }

        //获取项目的路径
        String root = request.getSession().getServletContext().getRealPath("/");
        //取得用户请求的目录
        File ugFile = new File(root + "/upload/" + uname, loc);
        File[] ugFiles = ugFile.listFiles();

        //获取已经选择的文件列表
        ArrayList<String> tmpFiles = (ArrayList<String>) request.getSession().getAttribute("tmpFile");
        ArrayList<File> tmpFilesList = new ArrayList<>();
        if (tmpFiles != null) {
            //转化选择列表
            for (String tmpFile : tmpFiles) {
                tmpFilesList.add(new File(Config.warLoc + "/upload/" + uname + tmpFile));
            }
        }
        ArrayList<UserFile> userFiles = new ArrayList<>();
        for (File f : ugFiles) {
            UserFile uFile = new UserFile(f);
            // 如果在选择列表里
            if (tmpFilesList.indexOf(new File(Config.warLoc + uFile.getLoc())) != -1) {
                uFile.setSelect(true);
            }
            userFiles.add(uFile);
        }

        PrintWriter out = response.getWriter();
        out.print(JSONArray.fromObject(userFiles));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
