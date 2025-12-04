package com.coolcollege.intelligent.common.util;


import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;

public class PbeEncryptUtil {

    private static final String ALGORITHM = "PBEWITHMD5ANDDES";

    /**
     * 定义迭代次数为1000次,次数越多，运算越大，越不容易破解之类。
     */
    private static final int ITERATIONCOUNT = 10;

    /**
     * 盐值(如需解密,该参数需要与加密时使用的一致)
     */
    private static final byte[] SALT = new byte[]{-2,-13,16,-8,102,-51,126,-103};

    public static void main(String[] args) throws Exception {

        String encrypt = URLEncoder.encode(encrypt("dingef2502a50df74ccc35c2f4657eb6378f|dingding2", SyncConfig.OPEN_ENTERPRISE_ID), "UTF-8");
        System.out.println(encrypt);
        String code = decrypt(URLDecoder.decode(encrypt, "UTF-8"), SyncConfig.OPEN_ENTERPRISE_ID);
        System.out.println(code);

    }

    /**
     * 加密明文字符串
     *
     * @param plaintext 待加密的明文字符串
     * @param password  生成密钥时所使用的密码
     * @return 加密后的密文字符串
     * @throws Exception
     */
    public static String encrypt(String plaintext, String password) throws Exception {
        //获取根据PBE口令生成的key
        Key key = getPBEKey(password);
        //设置PBE参数的盐和运算次数
        PBEParameterSpec parameterSpec = new PBEParameterSpec(SALT, ITERATIONCOUNT);
        //构建实例化
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        //cipher对象使用之前还需要初始化，共三个参数("加密模式或者解密模式","密匙","向量")
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        //数据转换
        byte encipheredData[] = cipher.doFinal(plaintext.getBytes("UTF-8"));

        return Base64.encodeBase64String(encipheredData);
    }

    /**
     * 解密密文字符串
     *
     * @param ciphertext 待解密的密文字符串
     * @param password   生成密钥时所使用的密码(如需解密,该参数需要与加密时使用的一致)
     * @return 解密后的明文字符串
     * @throws Exception
     */
    public static String decrypt(String ciphertext, String password)
            throws Exception {
        //转换密钥
        Key key = getPBEKey(password);
        //实例化PBE参数
        PBEParameterSpec parameterSpec = new PBEParameterSpec(SALT, ITERATIONCOUNT);
        //实例化
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        //初始化
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
        //数据转换
        byte[] passDec = cipher.doFinal(Base64.decodeBase64(ciphertext));
        //返回明文字符
        return new String(passDec);
    }

    /**
     * 根据PBE密码生成一把密钥
     *
     * @param password 生成密钥时所使用的密码
     * @return Key PBE算法密钥
     */
    private static Key getPBEKey(String password) throws Exception {
        // 实例化使用的算法
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        // 设置PBE密钥参数
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        // 生成密钥
        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        return secretKey;
    }

}