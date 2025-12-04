package com.coolcollege.intelligent.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;

/**
 * <p>
 * 杰峰云签名工具
 * </p>
 *
 * @author wangff
 * @since 2025/5/6
 */
public class JfySignatureUtil {
    /**
     * 获取签名字符串
     *
     * @param uuid       客户唯一标识
     * @param appKey     应用key
     * @param appSecret  应用密钥
     * @param timeMillis 时间戳
     * @param moveCard  移动取模基数
     * @return
     * @throws Exception
     */
    public static String getEncryptStr(String uuid, String appKey, String appSecret, String timeMillis, int moveCard) {
        try {
            String encryptStr = uuid + appKey + appSecret + timeMillis;
            byte[] encryptByte = encryptStr.getBytes("iso-8859-1");
            byte[] changeByte = change(encryptStr, moveCard);
            byte[] mergeByte = mergeByte(encryptByte, changeByte);
            return DigestUtils.md5Hex(mergeByte);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String timeMillis = JfyTimeMillisUtil.getTimMillis();
        System.out.println(timeMillis);
        System.out.println(getEncryptStr("681065930696f0c1b5e8810b", "4e1a9f48447c75cf454fbf5dae9d9af4", "b332c0a01cd9442dbce1edf1c39720a9", timeMillis, 2));
    }

    /**
     * 简单移位
     */
    private static byte[] change(String encryptStr, int moveCard) {
        try {
            byte[] encryptByte = encryptStr.getBytes("iso-8859-1");
            int encryptLength = encryptByte.length;
            byte temp;
            for (int i = 0; i < encryptLength; i++) {
                temp = ((i % moveCard) > ((encryptLength - i) % moveCard)) ? encryptByte[i] : encryptByte[encryptLength - (i + 1)];
                encryptByte[i] = encryptByte[encryptLength - (i + 1)];
                encryptByte[encryptLength - (i + 1)] = temp;
            }
            return encryptByte;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 合并
     *
     * @param encryptByte
     * @param changeByte
     * @return
     */
    private static byte[] mergeByte(byte[] encryptByte, byte[] changeByte) {
        int encryptLength = encryptByte.length;
        int encryptLength2 = encryptLength * 2;
        byte[] temp = new byte[encryptLength2];
        for (int i = 0; i < encryptByte.length; i++) {
            temp[i] = encryptByte[i];
            temp[encryptLength2 - 1 - i] = changeByte[i];
        }
        return temp;
    }
}
