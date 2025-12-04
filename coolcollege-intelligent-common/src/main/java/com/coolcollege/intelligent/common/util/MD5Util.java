package com.coolcollege.intelligent.common.util;

import com.coolcollege.intelligent.common.constant.Constants;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes("utf-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e ) {
            throw new RuntimeException("MD5算法不存在", e);
        }
    }

    public static void main(String[] args) {
        String s = com.coolcollege.intelligent.common.util.MD5Util.md5("159357");
        String s1 = com.coolcollege.intelligent.common.util.MD5Util.md5("36e1a5072c78359066ed7715f5ff3da8" + Constants.USER_AUTH_KEY);
        String ss = md5("159357");
        String ss1 = md5("36e1a5072c78359066ed7715f5ff3da8" + Constants.USER_AUTH_KEY);
        System.out.println(s);
        System.out.println(ss);
        System.out.println("---------");
        System.out.println(s1);
        System.out.println(ss1);
    }

}
