package com.crocoro.model;

import com.crocoro.Config;
import com.crocoro.sql.JDBCUtils;
import com.crocoro.tool.Tool;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;


public class GetInstall {

    public static String mysqlState = "未安装或者端口不正确";
    public static String mysqlVer = "0.0";
    public static boolean mysqlIsERROR = false;
    public static long freeSpace = 0;
    public static String freeSpaceState = "空间不足";
    public static boolean freeSpaceIsERROR = false;
    public static String dbExState = "即将创建数据库";
    public static int isDBEx = 0;
    private static JDBCUtils JDBC;
    private static ResultSet rs;

    public static void main(String[] args) {
    }

    public static void init() {
        getINFO();
        getFreeSpace();
        dbEx();
    }

    private static void getINFO() {
        try {
            JDBC = new JDBCUtils("");
            rs = JDBC.getRS("select version();");
            rs.next();
            mysqlVer = rs.getString(1).replaceAll("-log", "");
            int vera = Integer.parseInt(String.valueOf(mysqlVer.charAt(0)));
            int verb = Integer.parseInt(String.valueOf(mysqlVer.charAt(2)));
            if (vera < 5 || (vera < 5 && verb < 5)) {
                mysqlState = "错误:MySQL版本太低";
                mysqlIsERROR = true;
            } else {
                mysqlState = "提示:符合要求(" + mysqlVer + ")";
            }
        } catch (SQLException e) {
            mysqlIsERROR = true;
            int code = e.getErrorCode();
            System.out.println(code);
            switch (code) {
                case 0:
                    mysqlState = "错误:无法链接到服务器";
                    break;
                case 1045:
                    mysqlState = "错误:账号或密码错误";
                    break;
                default:
                    mysqlState = "未知错误:" + e.getErrorCode();
                    break;
            }
        }
    }

    private static void getFreeSpace() {
        File loc = new File(Config.warLoc);
        freeSpace = loc.getFreeSpace();
        if (freeSpace / 1024 / 1024 < 128) {
            freeSpaceIsERROR = true;
            freeSpaceState = "空间不足(" + Tool.FormetFileSize(freeSpace) + ")";
        } else {
            freeSpaceState = "空间充足(" + Tool.FormetFileSize(freeSpace) + ")";
        }
    }

    private static void dbEx() {
        try {
            JDBC = new JDBCUtils("n_blog");
            dbExState = "警告:已经存在数据库,即将重建";
            isDBEx = 1;
        } catch (SQLException e) {
            int code = e.getErrorCode();
            switch (code) {
                case 1049:
                    dbExState = "提示:即将创建数据库";
                    break;
                case 1044:
                    dbExState = "错误:用户权限不足";
                    break;
                default:
                    dbExState = "未知错误:" + e.getErrorCode();
                    isDBEx = 2;
                    break;
            }
        }
    }

    public static void jspIndex(boolean flag) {
        if (flag) {
            File indexJSP = new File(Config.warLoc + "index.jspx");
            indexJSP.renameTo(new File(Config.warLoc + "index.jsp"));
        } else {
            File indexJSP = new File(Config.warLoc + "index.jsp");
            indexJSP.renameTo(new File(Config.warLoc + "index.jspx"));
        }
    }

}
