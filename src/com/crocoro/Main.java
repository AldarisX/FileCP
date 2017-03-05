package com.crocoro;

import com.crocoro.model.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Main {
    ArrayList<File> dirFile = new ArrayList<>();
    ArrayList<File> locFile = new ArrayList<>();

    public static void main(String[] args) {
//        new Main().start();
    }

    public static String byteArrayToHex(byte[] byteArray) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < byteArray.length; n++) {
            stmp = (Integer.toHexString(byteArray[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < byteArray.length - 1) {
                hs = hs + "";
            }
        }
        // return hs.toUpperCase();
        return hs;
    }

    public static String fileMD5(String inputFile) throws IOException, NoSuchAlgorithmException {
        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        try {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 使用DigestInputStream
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0) ;
            // 获取最终的MessageDigest
            messageDigest = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return byteArrayToHex(resultByteArray);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } finally {
            try {
                digestInputStream.close();
            } catch (Exception e) {
            }
            try {
                fileInputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public void start() {
//        for (String dir : Config.dirLoc) {
//            dirFile.add(new File(dir));
//        }
//
//        for (String file : Config.fileLoc) {
//            locFile.add(new File(file));
//        }

        upload(new File("E:/dd-wrt.v24-30949_NEWD-2_K3.x_mega-WNDR4500.chk"));
    }

    public void upload(File file) {
        if (file.isFile()) {
            try {
                FileInfo info = new FileInfo();
                info.setFileLoc(file.getAbsolutePath());
                info.setFileName(file.getName());
                info.setFileMD5(fileMD5(file.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("内部错误");
        }
    }
}
