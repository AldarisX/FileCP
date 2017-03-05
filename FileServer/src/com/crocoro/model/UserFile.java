package com.crocoro.model;

import com.crocoro.Config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述文件的类
 */
public class UserFile {
    private String loc;
    private String name;
    private String time;
    private boolean isFile = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public UserFile(File f) {
        setLoc(f.getPath().replace(new File(Config.warLoc).getPath(), ""));
        setName(f.getName());
        setTime(sdf.format(new Date(f.lastModified())));
        if (f.isFile()) {
            isFile = true;
        }
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean file) {
        isFile = file;
    }
}
