package com.crocoro.sql;

import com.crocoro.Config;
import com.ibatis.common.jdbc.ScriptRunner;
import net.sf.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class JDBCUtils {
    public static String url = "jdbc:mysql://127.0.0.1:3306/";
    public static String dbName = "n_blog";
    public static String driver = "com.mysql.jdbc.Driver";
    public static String user = "root";
    public static String passwd = "aldaris";

    public Connection conn = null;
    public Statement st = null;
    public PreparedStatement pst = null;
    public ResultSet rs = null;

    private Timer timer = new Timer();
    private int limit = 5;
    private int count = 0;

    {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }

        count = limit;
        timer.schedule(new TimerTask() {
            public void run() {
                if (count <= 0) {
                    close();
                }
                count--;
            }
        }, 0, 1000);
    }

    public JDBCUtils() throws SQLException {
        boolean tryGetConn = true;
        try {
            while (tryGetConn) {
                conn = DriverManager.getConnection(url + dbName, user, passwd);
                tryGetConn = false;
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e) {
            System.out.println("数据库链接不足 建议调整数据库链接");
            tryGetConn = true;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public JDBCUtils(String dbName) throws SQLException {
        boolean tryGetConn = true;
        try {
            while (tryGetConn) {
                conn = DriverManager.getConnection(url + dbName, user, passwd);
                tryGetConn = false;
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException e) {
            System.out.println("数据库链接不足 建议调整数据库链接");
            tryGetConn = true;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void saveDBConfig() {
        HashMap<String, String> dbConfig = new HashMap<String, String>();
        dbConfig.put("url", url);
        dbConfig.put("dbName", dbName);
        dbConfig.put("driver", driver);
        dbConfig.put("user", user);
        dbConfig.put("passwd", passwd);

        JSONObject jobj = JSONObject.fromObject(dbConfig);

        try {
            String path = Config.warLoc + "WEB-INF/classes/Config.json";
            File file = new File(path);
            BufferedWriter ow = new BufferedWriter(new FileWriter(file));
            ow.write(jobj.toString());
            ow.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void installDB() {
        try {
            Class.forName(driver).newInstance();
            Connection conn = DriverManager.getConnection(url, user, passwd);
            ScriptRunner runner = new ScriptRunner(conn, false, false);
            runner.setErrorLogWriter(null);
            runner.setLogWriter(null);

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File(Config.warLoc + "WEB-INF/classes/backup-init.sql")), "UTF-8"));
            runner.runScript(br);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException
                | IOException e) {
            e.printStackTrace();
        }
    }

    private Statement getST() throws SQLException {
        return conn.createStatement();
    }

    public PreparedStatement getPST(String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    public ResultSet getQuery(String sql) throws SQLException {
        count = limit;
        return getST().executeQuery(sql);
    }

    public ResultSet getQuery(PreparedStatement ps) throws SQLException {
        count = limit;
        pst = ps;
        return ps.executeQuery();
    }

    public ResultSet getRS(String sql) {
        ResultSet rs = null;
        try {
            rs = getST().executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public int getUpdate(String sql) throws SQLException {
        count = limit;
        return getST().executeUpdate(sql);
    }

    public int getUpdate(PreparedStatement ps) throws SQLException {
        count = limit;
        pst = ps;
        return ps.executeUpdate();
    }

    public void setLimit(int limit) {
        this.limit = limit;
        count = limit;
    }

    public void close() {
        try {
            rs.close();
        } catch (Exception e) {

        }
        try {
            st.close();
        } catch (Exception e) {

        }
        try {
            pst.close();
        } catch (Exception e) {

        }
        try {
            conn.close();
        } catch (Exception e) {

        }

        timer.cancel();
    }
}
