package com.crocoro.servlet;

import com.crocoro.Config;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 文件操作
 */
@WebServlet(name = "FileAction", urlPatterns = "/fileAction.do")
public class FileAction extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        if (code == null) {
            return;
        }
        String uname = (String) request.getSession().getAttribute("uname");
        if (uname == null) {
            return;
        }
        switch (code) {
            case "del":
                if (!fileDel(uname, request.getParameter("fileLoc"))) {
                    return;
                }
                break;
            case "mkdir":
                if (!mkdir(uname, request.getParameter("dirLoc"))) {
                    return;
                }
                break;
            case "rename":
                if (!rename(uname, request.getParameter("fileLoc"), request.getParameter("desFileName"))) {
                    return;
                }
                break;
            case "move":
                break;
            case "copy":
                break;
            case "select":
                if (!doSelectFile(request.getParameter("fileLoc"), request)) {
                    return;
                }
                break;
            default:
                return;
        }
        PrintWriter out = response.getWriter();
        out.print("200");
    }

    protected boolean mkdir(String uname, String dirLoc) {
        dirLoc = doFileLocCheck(dirLoc);
        if (dirLoc == null) {
            return false;
        }
        File dir = new File(Config.warLoc + "/upload/" + uname + "/" + dirLoc);
        if (!dir.exists()) {
            dir.mkdirs();
            return true;
        } else {
            return false;
        }
    }

    protected boolean fileDel(String uname, String fileLoc) {
        fileLoc = doFileLocCheck(fileLoc);
        if (fileLoc == null) {
            return false;
        }
        File file = new File(Config.warLoc + "/upload/" + uname + "/" + fileLoc);
        file.delete();
        return true;
    }

    public boolean rename(String uname, String fileLoc, String desFileName) {
        fileLoc = doFileLocCheck(fileLoc);
        if (fileLoc == null) {
            return false;
        }
        File file = new File(Config.warLoc + "/upload/" + uname + "/" + fileLoc);
        File desFile = new File(Config.warLoc + "/upload/" + uname + "/" + desFileName);
        try {
            FileUtils.moveFile(file, desFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean doSelectFile(String fileLoc, HttpServletRequest request) {
        fileLoc = doFileLocCheck(fileLoc);
        if (fileLoc == null) {
            return false;
        }
        request.getSession().setAttribute("tmpFile", fileLoc);
        return true;
    }

    protected String doFileLocCheck(String fileLoc) {
        if (fileLoc == null) {
            return null;
        } else {
            try {
                return URLDecoder.decode(fileLoc, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
