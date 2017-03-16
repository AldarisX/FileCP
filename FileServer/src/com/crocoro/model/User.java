package com.crocoro.model;

import com.crocoro.sql.JDBCUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by aldaris on 17-3-16.
 */
public class User {
    private int id;
    private String uname = null;
    private String passwd;
    private int level = 3;
    private String token = "token";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void getUserByToken(String token) {
        try {
            JDBCUtils jdbc = new JDBCUtils();
            PreparedStatement ps = jdbc.getPST("select * from user where token=?");
            ps.setString(1, token);
            ResultSet rs = jdbc.getQuery(ps);
            if (rs.next()) {
                setId(rs.getInt("id"));
                setLevel(rs.getInt("level"));
                setToken(rs.getString("token"));
                setUname(rs.getString("uname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
