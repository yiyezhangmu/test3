package com.coolcollege.intelligent.common.util.sign;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HuShangSignUtils {
    public static String generateSign(int timestamp, String nonce, String msg, String signKey,boolean needSort) {
        String[] array = new String[] { String.valueOf(timestamp), nonce, msg,signKey };
        if(needSort) {
            Arrays.sort(array);
        }
        return getSha1( String.join("",array).getBytes());
    }

    public static String getSha1(byte[] input) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(input);
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}