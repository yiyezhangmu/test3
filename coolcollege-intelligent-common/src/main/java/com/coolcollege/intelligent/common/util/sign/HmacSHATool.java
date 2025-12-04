package com.coolcollege.intelligent.common.util.sign;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Locale;

@Slf4j
public class HmacSHATool {

    public static String encodeHmacSHA256(String plantText, String appSecret){
        try {
            byte[] data = ByteFormat.hexToBytes(ByteFormat.toHex((plantText).getBytes()));
            byte[] key = ByteFormat.hexToBytes(ByteFormat.toHex(appSecret.getBytes()));

            SecretKey secretKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            byte digest[] = mac.doFinal(data);
            return ByteFormat.toHex(digest);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }


    public static String szmlmacSHA256(String plantText, String appSecret){
        try {
            SecretKey secretKey = new SecretKeySpec(appSecret.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(plantText.getBytes());
            return Base64.getEncoder().encodeToString(hmacBytes).toUpperCase(Locale.ROOT);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
