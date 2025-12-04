package com.coolcollege.intelligent.common.util.sign;

import org.apache.commons.codec.Charsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUtils {

    public SignUtils() {
    }

    public static String getSignatureBySHA256(String text) {
        String signature = "";
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(text.getBytes(Charsets.UTF_8));
            signature = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException ignore) {
        }
        return signature;
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        byte[] var3 = bytes;
        int var4 = bytes.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            byte aByte = var3[var5];
            temp = Integer.toHexString(aByte & 255);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }

            stringBuffer.append(temp);
        }

        return stringBuffer.toString();
    }


    public static String getEncryptText(String appId, String appSecret, String nonce, String timestamp) {
        return "appId" + "=" + appId + "&" + "timestamp" + "=" + timestamp + "&" + "nonce" + "=" + nonce + "&" + "appSecret" + "=" + appSecret;
    }
}
