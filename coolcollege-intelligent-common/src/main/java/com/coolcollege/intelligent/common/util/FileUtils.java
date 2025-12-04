package com.coolcollege.intelligent.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private static List<String> picSuffixs = new ArrayList<String>();

    static {
        picSuffixs.add(".pdf");
        picSuffixs.add(".ppt");
        picSuffixs.add(".pptx");
        picSuffixs.add(".doc");
        picSuffixs.add(".docx");
        picSuffixs.add(".xls");
        picSuffixs.add(".xlsx");
        picSuffixs.add(".txt");
        picSuffixs.add(".jpg");
        picSuffixs.add(".jpeg");
        picSuffixs.add(".png");
    }

    public static int getFileSizeKb(InputStream inputStream)  {// 取得文件大小
        int size= 0;
        if (!(inputStream instanceof FileInputStream)) return size;

        FileInputStream fis = (FileInputStream) inputStream;
        try {
            size = fis.available();
        } catch (IOException e) {
            e.printStackTrace();
        }// 计算byte 数
        size = size / (1024);
        return size;
    }

    /**
     * 校验文件大小
     * 
     * @param inputStream
     * @param maxSizeMB 文件大小 MB
     * @return
     */
    public static boolean checkFileSizeByInputStream(InputStream inputStream, double maxSizeMB) {
        double size;
        try {
            size = getFileSizeKb(inputStream);
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
        if (size/1024 > maxSizeMB) {
            return false;
        }
        return true;
    }

    public static boolean checkPicFileName(String fileName) {
        boolean result = false;
        if (fileName == null) {
            return result;
        }
        for (String type : picSuffixs) {
            if (fileName.toLowerCase().endsWith(type)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public static String getFilePathWithOutHost(String fileUrl) {
        URL url = null;
        try {
            url = new URL(fileUrl);
            String filePath = url.getFile();
            filePath = filePath.replaceFirst("//","");
            filePath = filePath.replaceFirst("/","");
            return filePath;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return "";
    }

}
