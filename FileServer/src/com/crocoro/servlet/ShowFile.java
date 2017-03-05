package com.crocoro.servlet;

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
import java.util.ArrayList;

/**
 * 显示文件
 */
@WebServlet(name = "ShowFile", urlPatterns = "/showfile.json")
public class ShowFile extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        //取得用户名
        String uname = (request.getSession().getAttribute("uname") != null) ? (String) request.getSession().getAttribute("uname") : "";
        if (uname.equals("")) {
            return;
        }

        String loc = request.getParameter("loc");
        if (loc == null) {
            loc = "/";
        }

        //获取项目的路径
        String root = request.getSession().getServletContext().getRealPath("/");
        //取得用户请求的目录
        File ugFile = new File(root, "/upload/" + uname + "/" + loc);
        File[] ugFiles = ugFile.listFiles();

        ArrayList<UserFile> userFiles = new ArrayList<>();
        for (File f : ugFiles) {
            userFiles.add(new UserFile(f));
        }

        PrintWriter out = response.getWriter();
        out.print(JSONArray.fromObject(userFiles));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
