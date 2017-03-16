package com.crocoro.servlet;

import com.crocoro.Config;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by aldaris on 17-3-16.
 */
@WebFilter(filterName = "DownloadFilter")
public class DownloadFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        chain.doFilter(req, resp);
        resp.reset();
        long pos = 0;
        HttpServletRequest httpServletRequest = (HttpServletRequest) req;
        HttpServletResponse httpServletResponse = (HttpServletResponse) resp;

        String reqURL = httpServletRequest.getRequestURI();
        if (reqURL.contains("..")) {
            ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_RESET_CONTENT);
            return;
        }

        OutputStream os = null;
        FileInputStream is = null;

        File f = new File(Config.warLoc + reqURL);
        if (f.exists()) {
            is = new FileInputStream(f);
            long fSize = f.length();
            byte xx[] = new byte[4096];
            httpServletResponse.setHeader("Accept-Ranges", "bytes");
            httpServletResponse.setHeader("Content-Length", fSize + "");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + reqURL);
            if (httpServletRequest.getHeader("Range") != null) {
                // 若客户端传来Range，说明之前下载了一部分，设置206状态(SC_PARTIAL_CONTENT)
                httpServletResponse.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                pos = Long.parseLong(httpServletRequest.getHeader("Range").replaceAll("bytes=", "").replaceAll("-", ""));
            }
            if (pos != 0) {
                String contentRange = new StringBuffer("bytes ").append(
                        new Long(pos).toString()).append("-").append(
                        new Long(fSize - 1).toString()).append("/").append(
                        new Long(fSize).toString()).toString();
                httpServletResponse.setHeader("Content-Range", contentRange);
                // 略过已经传输过的字节
                is.skip(pos);

                os = resp.getOutputStream();
                boolean all = false;
                while (!all) {
                    int n = is.read(xx);
                    if (n != -1) {
                        os.write(xx, 0, n);
                    } else {
                        all = true;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            }
        } else {
            ((HttpServletResponse) resp).sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
