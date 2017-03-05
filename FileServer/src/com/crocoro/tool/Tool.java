package com.crocoro.tool;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Tool {
    public static ArrayList<String> imgTypeList = new ArrayList<String>();
    public static String imgJSUL = "";
    public static String imgUL = "";
    private static ExecutorService service;
    private static AtomicLong pendingFileVisits = new AtomicLong();
    private static CountDownLatch latch = new CountDownLatch(1);
    private static AtomicLong totalSize = new AtomicLong();

    public static String transMapToString(Map<String, String[]> map) {
        String mapToString = "";
        for (String key : map.keySet()) {
            String tempString[] = map.get(key);
            for (int i = 0; i < tempString.length; i++) {
                mapToString += key + "-" + tempString[i] + "-";
            }
        }
        return mapToString;
    }

    public static String getFileType(String name) {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static boolean isIMG(String type) {
        return imgTypeList.contains(type);
    }

    public static void cacImgUL() {
        for (String key : Tool.imgTypeList) {
            imgUL += "image/" + key + ",";
            imgJSUL += "\"" + key + "\",";
        }
        imgUL = imgUL.substring(0, imgUL.length() - 1);
        imgJSUL = imgJSUL.substring(0, imgJSUL.length() - 1);
    }

    public static long getFileSize(File file) throws InterruptedException {
        latch = new CountDownLatch(1);
        pendingFileVisits = new AtomicLong();
        totalSize = new AtomicLong();
        service = Executors.newFixedThreadPool(100);
        pendingFileVisits.incrementAndGet();
        try {
            updateTotalSizeOfFilesInDir(file);
            latch.await(100, TimeUnit.SECONDS);
            return totalSize.longValue();
        } finally {
            service.shutdown();
        }
    }

    private static void updateTotalSizeOfFilesInDir(final File file) {
        long fileSize = 0;
        if (file.isFile())
            fileSize = file.length();
        else {
            final File[] children = file.listFiles();
            if (children != null) {
                for (final File child : children) {
                    if (child.isFile())
                        fileSize += child.length();
                    else {
                        pendingFileVisits.incrementAndGet();
                        service.execute(new Runnable() {
                            public void run() {
                                updateTotalSizeOfFilesInDir(child);
                            }
                        });
                    }
                }
            }
        }
        totalSize.addAndGet(fileSize);
        if (pendingFileVisits.decrementAndGet() == 0)
            latch.countDown();
    }

    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS == 0) {
            fileSizeString = "0";
        } else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static boolean isFileExist(File file, long time) throws IOException {
        if (file.exists()) {
            Date date = new Date();
            return (date.getTime() - file.lastModified()) / 100 <= time;
        } else {
            return false;
        }
    }

    public static boolean isFileExist(File file) throws IOException {
        return file.exists();
    }

    public static void dirClean(File dir, boolean inn) {
        File[] children = dir.listFiles();
        if (children != null) {
            for (final File child : children) {
                if (child.isFile()) {
                    child.delete();
                } else {
                    if (inn) {
                        dirClean(child, inn);
                    } else {

                    }
                }
            }
        }
    }

    public static void delVMSP(File dir, String fileName) {
        File[] children = dir.listFiles();
        if (children != null) {
            for (final File child : children) {
                if (child.isFile()) {
                    if (child.getName().startsWith(fileName)) {
                        child.delete();
                    }
                }
            }
        }
    }

    /***
     * encode by Base64
     */
    public static String encodeBase64(byte[] input) {
        try {
            Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
            Method mainMethod = clazz.getMethod("encode", byte[].class);
            mainMethod.setAccessible(true);
            Object retObj = mainMethod.invoke(null, new Object[]{input});
            return (String) retObj;
        } catch (Exception e) {
            return "";
        }
    }

    /***
     * decode by Base64
     */
    public static String decodeBase64(String input) {
        try {
            Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
            Method mainMethod = clazz.getMethod("decode", String.class);
            mainMethod.setAccessible(true);
            Object retObj = mainMethod.invoke(null, input);
            return new String((byte[]) retObj, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
}
