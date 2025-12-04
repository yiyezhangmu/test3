package com.coolcollege.intelligent.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * <p>
 * AES对称加密工具类
 * </p>
 *
 * @author wangff
 * @since 2025/5/22
 */
public class AESUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String CHARSET = "UTF-8";

    public static String encrypt(String key, String value) {
        try {
            // 生成随机初始化向量（IV）
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 创建密钥
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

            // 加密
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(value.getBytes(CHARSET));

            // 合并IV和加密数据并进行Base64编码
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    public static String decrypt(String key, String encryptedValue) {
        try {
            // Base64解码
            byte[] combined = Base64.getDecoder().decode(encryptedValue);

            // 提取IV（前16字节）
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 提取加密数据（16字节之后的部分）
            byte[] encrypted = new byte[combined.length - 16];
            System.arraycopy(combined, 16, encrypted, 0, encrypted.length);

            // 创建密钥
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

            // 解密
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }
}
