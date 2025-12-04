package com.coolcollege.intelligent.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 3DES解密
 * @Author: mao
 * @CreateDate: 2021/6/7 14:37
 */
@Slf4j
public class DecryptUtil {

    private static final int VECTOR_LEGHT = 8;

    /**
     * All supported methods
     */
    public enum Method {
        DESEDE("DESEDE"), DESEDE_CBC_NoPadding("DESEDE/CBC/NoPadding"),
        DESEDE_CBC_PKCS5Padding("DESEDE/CBC/PKCS5Padding"), DESEDE_CBC_PKCS7Padding("DESEDE/CBC/PKCS7Padding"),
        DESEDE_CBC_ISO10126Padding("DESEDE/CBC/ISO10126Padding");

        private final String method;

        Method(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }
    }

    /**
     * Keysize must be equal to 128 or 192 bits. Default Keysize equals 128 bits.
     */
    public enum Key {
        SIZE_128(16), SIZE_192(24);

        private final int size;

        Key(int size) {
            this.size = size;
        }
    }

    /**
     * Implementation of DESede encryption
     */

    public static String encrypt(Method method, byte[] key, byte[] vector, byte[] message) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, method.getMethod());
        IvParameterSpec ivSpec = new IvParameterSpec(vector);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(method.getMethod());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] cipherText = cipher.doFinal(message);
        String encodeCipertext = Base64.getEncoder().encodeToString(cipherText);
        return encodeCipertext;
    }

    /**
     * 3DES解密
     *
     * @param method
     * @param key
     * @param keySize
     * @param vector
     * @param message
     * @return String
     * @author mao
     * @date 2021/6/7 14:50
     */
    public static String decrypt(Method method, byte[] key, Key keySize, byte[] vector, byte[] message) {
        String result = "";
        try {
            byte[] keyBytes = generateKey(key, keySize.size);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, method.getMethod());
            byte[] keyBytesIv = generateVector(vector, VECTOR_LEGHT);
            IvParameterSpec ivSpec = new IvParameterSpec(keyBytesIv);
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(method.getMethod());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] cipherText = cipher.doFinal(Base64.getDecoder().decode(message));
            result = new String(cipherText);
        } catch (Exception e) {
            log.error("3DES Occur Exception: exception={}", e);
        }
        return result;
    }

    /**
     * Method for creation of valid byte array from key
     */
    public static byte[] generateKey(byte[] key, int lenght) throws UnsupportedEncodingException {
        byte[] keyBytes = new byte[lenght];
        int len = key.length;

        if (len > keyBytes.length) {
            len = keyBytes.length;
        }

        System.arraycopy(key, 0, keyBytes, 0, len);
        return keyBytes;
    }

    /**
     * Method for creation of valid byte array from initialization vector
     */
    public static byte[] generateVector(byte[] vector, int lenght) throws UnsupportedEncodingException {
        byte[] keyBytesIv = new byte[lenght];
        int len = vector.length;

        if (len > keyBytesIv.length) {
            len = keyBytesIv.length;
        }

        System.arraycopy(vector, 0, keyBytesIv, 0, len);
        return keyBytesIv;
    }

    /**
     * This method contains a list of encryption methods, that do does not have a initialization vector
     */
    public static boolean hasInitVector(String method) {
        if (method.contains("ECB")) {
            return false;
        }
        switch (method) {
            case "PBEWITHSHA1AND128BITRC4":
            case "PBEWITHSHA1AND40BITRC4":
            case "PBEWITHSHAAND128BITRC4":
            case "PBEWITHSHAAND40BITRC4":
                return false;
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        byte[] keybytes = "lottery-fghw5H_Fghs-ding".getBytes(StandardCharsets.UTF_8);
        byte[] ivbytes = {50, 51, 53, 55, 49, 52, 54, 56};
        // 加密字符串
        String content =
            "{\"Kcv\":\"123456\",\"IDc\":\"User1\",\"IDo\":\"DataOwner1\",\"IDv\":\"222\",\"TS4\":\"1564657964010\",\"lifetime4\":\"657964010\","
                + "\"AC\":{\"IDc\":\"User1\",\"permission\":{\"Folder_pdf\":{\"Folder_pdf\":[0,1,1,1]},"
                + "\"Folder_txt\":{\"Folder_txt\":[0,1,0,0],\"xiaohua.txt\":[0,1,1]}}}}";
        System.out.println("加密前的：" + content);
        System.out.println("加密密钥：" + new String(keybytes));
        // 加密方法
        String enc = DecryptUtil.encrypt(Method.DESEDE_CBC_PKCS7Padding, keybytes, ivbytes, content.getBytes());
        System.out.println("加密后的内容：" + enc);
        String dec =
            DecryptUtil.decrypt(Method.DESEDE_CBC_PKCS7Padding, keybytes, Key.SIZE_192, ivbytes, enc.getBytes());
        System.out.println("解密后的内容：" + dec);
    }

}
