package com.crocoro.servlet;

import com.crocoro.Config;
import org.apache.commons.io.FileExistsException;
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
import java.util.ArrayList;

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
                if (!fileDel(uname, request.getParameter("fileLoc"), request)) {
                    return;
                }
                break;
            case "mkdir":
                if (!mkdir(uname, request.getParameter("dirLoc"))) {
                    return;
                }
                break;
            case "rename":
                if (!rename(uname, request.getParameter("fileLoc"), request.getParameter("desFileName"), request)) {
                    return;
                }
                break;
            case "move":
                if (!doMoveFile(uname, request.getParameter("desFile"), request)) {
                    return;
                }
                break;
            case "copy":
                if (!doCopyFile(uname, request.getParameter("desFile"), request)) {
                    return;
                }
                break;
            case "select":
                if (!doSelectFile(request.getParameter("fileLoc"), request)) {
                    return;
                }
                break;
            case "clearSelect":
                request.getSession().removeAttribute("tmpFile");
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

    protected boolean fileDel(String uname, String fileLoc, HttpServletRequest request) {
        fileLoc = doFileLocCheck(fileLoc);
        if (fileLoc == null) {
            return false;
        }
        File file = new File(Config.warLoc + "/upload/" + uname + "/" + fileLoc);
        try {
            FileUtils.forceDelete(file);
            //判断选择的文件
            String tmpFile = (String) request.getSession().getAttribute("tmpFile");
            if (tmpFile != null) {
                if (tmpFile.equals(fileLoc)) {
                    request.getSession().removeAttribute("tmpFile");
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rename(String uname, String fileLoc, String desFileName, HttpServletRequest request) {
        fileLoc = doFileLocCheck(fileLoc);
        if (fileLoc == null) {
            return false;
        }
        File file = new File(Config.warLoc + "/upload/" + uname + "/" + fileLoc);
        File desFile = new File(Config.warLoc + "/upload/" + uname + "/" + desFileName);
        try {
            FileUtils.moveFile(file, desFile);
            //判断选择的文件
            String tmpFile = (String) request.getSession().getAttribute("tmpFile");
            if (tmpFile != null) {
                if (tmpFile.equals(fileLoc)) {
                    request.getSession().removeAttribute("tmpFile");
                }
            }
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
        ArrayList<String> tmpFiles = (ArrayList<String>) request.getSession().getAttribute("tmpFile");
        if (tmpFiles != null) {
            if (tmpFiles.indexOf(fileLoc) == -1) {
                tmpFiles.add(fileLoc);
            } else {
                tmpFiles.remove(fileLoc);
            }
        } else {
            tmpFiles = new ArrayList<>();
            tmpFiles.add(fileLoc);
            request.getSession().setAttribute("tmpFile", tmpFiles);
        }
        return true;
    }

    public boolean doCopyFile(String uname, String desLoc, HttpServletRequest request) {
        desLoc = doFileLocCheck(desLoc);
        if (desLoc == null) {
            return false;
        }
        ArrayList<String> tmpFiles = (ArrayList<String>) request.getSession().getAttribute("tmpFile");
        for (String tmpFile : tmpFiles) {
            File file = new File(Config.warLoc + "/upload/" + uname + "/" + tmpFile);
            File desFile = new File(Config.warLoc + "/upload/" + uname + "/" + desLoc + "/" + file.getName());
            try {
                FileUtils.copyFile(file, desFile);
            } catch (FileExistsException e) {

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        request.getSession().removeAttribute("tmpFile");
        return true;
    }

    public boolean doMoveFile(String uname, String desLoc, HttpServletRequest request) {
        desLoc = doFileLocCheck(desLoc);
        if (desLoc == null) {
            return false;
        }
        ArrayList<String> tmpFiles = (ArrayList<String>) request.getSession().getAttribute("tmpFile");
        for (String tmpFile : tmpFiles) {
            File file = new File(Config.warLoc + "/upload/" + uname + "/" + tmpFile);
            File desFile = new File(Config.warLoc + "/upload/" + uname + "/" + desLoc + "/" + file.getName());
            try {
                if (file.isFile()) {
                    FileUtils.moveFile(file, desFile);
                } else {
                    FileUtils.moveDirectory(file, desFile);
                }
            } catch (FileExistsException e) {

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        request.getSession().removeAttribute("tmpFile");
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
