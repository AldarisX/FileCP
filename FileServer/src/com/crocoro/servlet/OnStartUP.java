package com.crocoro.servlet;

import com.crocoro.Config;
import com.crocoro.sql.JDBCUtils;
import net.sf.json.JSONObject;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.Scanner;

/**
 * Login implementation class OnStartUP
 */
@WebServlet("/OnStartUP")
public class OnStartUP extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public OnStartUP() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        System.out.println("开始获取获取项目路径");
        try {
            Config.warLoc = (config.getServletContext().getResource("/")).toString();
            String osName = System.getProperty("os.name");
            if (osName.toLowerCase().contains("windows")) {
                osName = "Windows";
            } else if (osName.toLowerCase().contains("linux")) {
                osName = "Linux";
            }
            switch (osName) {
                case "Linux":
                    Config.warLoc = URLDecoder.decode(Config.warLoc.replace("file:", ""), "utf-8");
                    break;
                case "Windows":
                    Config.warLoc = URLDecoder.decode(Config.warLoc.replace("file:/", ""), "utf-8");
                    break;
            }
            System.out.println("获取项目路径成功:" + Config.warLoc);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            System.err.println("获取项目路径失败");
            e.printStackTrace();
        }

        System.out.println("配置数据库属性");
        File dbJson = new File(Config.warLoc + "/WEB-INF/classes/db.json");
        Scanner scanner = null;
        StringBuilder jsonBuffer = new StringBuilder();
        try {
            scanner = new Scanner(dbJson, "utf-8");
            while (scanner.hasNextLine()) {
                jsonBuffer.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("找不到数据库配置文件!!!");
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        JSONObject dbOBJ = JSONObject.fromObject(jsonBuffer.toString());
        JDBCUtils.url = dbOBJ.getString("url");
        JDBCUtils.dbName = dbOBJ.getString("dbName");
        JDBCUtils.driver = dbOBJ.getString("driver");
        JDBCUtils.user = dbOBJ.getString("user");
        JDBCUtils.passwd = dbOBJ.getString("passwd");

        try {
            new JDBCUtils();
            Config.isInstall = true;
            System.out.println("加载完成");
        } catch (Exception e) {
            if (e.getMessage().startsWith("Unknown database")) {
                System.err.println("发现没有安装,即将进入安装");
            } else {
                e.printStackTrace();
            }
            Config.isInstall = false;
        }
//		GetInstall.jspIndex(Config.isInstall);
    }

    public void initx() {

    }

}
