package com.crocoro.servlet;

import com.crocoro.model.User;
import com.crocoro.sql.JDBCUtils;
import com.crocoro.tool.Base64Tool;
import com.crocoro.tool.MD5Tool;
import com.crocoro.tool.TimeTool;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * 上载组件
 */
@WebServlet(name = "Upload", urlPatterns = "/upload.do")
public class Upload extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        //获取参数
        String fileName = new String(Base64Tool.decode(request.getParameter("fileName")));
        String fileLoc = new String(Base64Tool.decode(request.getParameter("fileloc")));
        if (fileLoc.contains("..")) {
            response.reset();
            return;
        }
        String fileMD5 = request.getParameter("md5").toLowerCase();

        //获取项目的路径
        String root = request.getSession().getServletContext().getRealPath("/");

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

        //取得上载的路径
        String uploadPath = root + "/upload/" + uname + "/" + fileLoc.replace(":", "");

        //如果不存在目录就创建目录
        if (!new File(uploadPath).isDirectory())
            new File(uploadPath).mkdirs();

        JDBCUtils jdbc = null;
        try {
            jdbc = new JDBCUtils();
            //判断文件是否存在
            File f = new File(uploadPath, fileName);
            if (f.exists()) {
                System.out.println("已经存在文件" + fileLoc + fileName);
                if (!fileMD5.equals("null")) {
                    //获取这个用户的同md5文件
                    PreparedStatement ps = jdbc.getPST("select name,path,utime from file where uname=? and md5=?;");
                    ps.setString(1, uname);
                    ps.setString(2, fileMD5);
                    ResultSet rs = jdbc.getQuery(ps);
                    if (rs.next()) {
                        //如果有同md5的文件就判断是不是一个路径
                        String locFilePath = rs.getString("path");
                        String locFileName = rs.getString("name");
                        if (locFileName.equals(fileName) && locFilePath.equals(fileLoc)) {
                            //如果路径都一样
                            System.out.println("跳过文件" + fileLoc + fileName);
                            return;
                        }
                    } else {
                        //如果没有同md5的文件就说明文件已经修改，那就需要进行备份
                        ps = jdbc.getPST("select id,utime from file where uname=? and path=? and name=?;");
                        ps.setString(1, uname);
                        ps.setString(2, fileLoc);
                        ps.setString(3, fileName);
                        rs = jdbc.getQuery(ps);
                        if (rs.next()) {
                            //获取文件ID
                            int locFileID = rs.getInt("id");
                            String locFileTime = rs.getString("utime");
                            //重命名文件
                            File bak = new File(uploadPath, fileName + "." + TimeTool.convSqlDate(locFileTime) + ".webbak");
                            FileUtils.moveFile(f, bak);
                            //修改数据库
                            ps = jdbc.getPST("UPDATE `file` SET `name`=? WHERE `id`=?;");
                            ps.setString(1, bak.getName());
                            ps.setInt(2, locFileID);
                            jdbc.getUpdate(ps);
                            System.out.println("备份文件" + uploadPath + fileName + "到" + bak.getAbsolutePath());
                        } else {
                            //这里需要处理文件已经存在,但是数据库没有的情况
                        }
                    }
                }
            } else {
                System.out.println("不存在文件,即将创建" + fileLoc + fileName);
            }
        } catch (SQLException e) {
            response.reset();
            e.printStackTrace();
            return;
        }
        jdbc.setLimit(Integer.MAX_VALUE);

        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8");
            if (!ServletFileUpload.isMultipartContent(request)) {
                response.reset();
                return;
            }

            List<FileItem> list = upload.parseRequest(request);
            Iterator i = list.iterator();

            for (FileItem item : list) {
                if (!item.isFormField()) {
                    while (i.hasNext()) {
                        FileItem file = (FileItem) i.next();
                        String sourcefileName = file.getName();
                        File uf = new File(uploadPath + "/" + sourcefileName);
                        file.write(uf);

                        String reMD5 = new MD5Tool().fileMD5(uf.getAbsolutePath()).toLowerCase();
                        System.out.println(reMD5 + ":" + fileMD5);
                        if (!reMD5.equals(fileMD5)) {
                            response.sendError(HttpServletResponse.SC_BAD_GATEWAY);
                            return;
                        }

                        PreparedStatement ps = jdbc.getPST("INSERT INTO `file` (`path`, `name`, `md5`, `uname`) VALUES (?, ?, ?, ?);");
                        ps.setString(1, fileLoc);
                        ps.setString(2, fileName);
                        ps.setString(3, fileMD5);
                        ps.setString(4, uname);
                        jdbc.getUpdate(ps);
                        jdbc.setLimit(5);
                        new File(uploadPath + "/" + "null").delete();

                        response.sendRedirect("showfile.jsp?loc=" + URLEncoder.encode(fileLoc, "UTF-8"));
                    }
                }
            }
        } catch (Exception e) {
            response.reset();
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
