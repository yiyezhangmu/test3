package com.coolcollege.intelligent.common.util;

import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 2020/12/14 11:58
 */
public class EncryptUtil {

    /** Base64 编码 */
    private static final Base64 B64 = new Base64();
    /** 安全的随机数源 */
    private static final SecureRandom RANDOM = new SecureRandom();
    /** AES加密算法 */
    private static final String AES_ALGORITHM = "AES";

    private static final String AES = "AES/ECB/PKCS5Padding";


    public static String MD5(String str) {
        String re_md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuilder buf = new StringBuilder("");
            for (byte aB : b) {
                i = aB;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    public static String maskMail(String mail) {
        if (Strings.isNullOrEmpty(mail)) {
            return mail;
        }
        if (!mail.contains("@")) {
            return mail;
        }
        String[] arrays = mail.split("@");
        String account = arrays[0];
        String domain = arrays[1];
        Integer len = account.length();
        if (len <= 2) return mail;
        if (len <= 4) {
            account = account.substring(0, 2) + "****";
        } else {
            String left = account.substring(0, 2);
            String right = account.substring(len - 2, len);
            account = left + "****" + right;
        }
        return account + "@" + domain;
    }

    public static String maskMobile(String mobile) {
        if (Strings.isNullOrEmpty(mobile)) {
            return mobile;
        }
        if (mobile.length() < 11) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(7, 11);
    }

    public static String mask(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return str;
        }

        int len = str.length();

        if (len < 5) {
            return str;
        }

        return str.substring(0, 2) + "****" + str.substring(len - 2, len);
    }

    /**
     * AES加密
     *
     * @param str
     *            需要加密的明文
     * @param key
     *            密钥
     * @return 加密后的密文(str / key为null返回null)
     */
    public static String aesEncryp(String str, String key) {
        return aesEncryp(str, key, false);
    }


    /**
     * AES加密
     *
     * @param str
     *            需要加密的明文
     * @param key
     *            密钥
     * @param urlSafety
     *            密文是否需要Url安全
     * @return 加密后的密文(str / key为null返回null)
     */
    public static String aesEncryp(String str, String key, boolean urlSafety) {
        if (null != str && null != key) {
            try {
                Cipher c = Cipher.getInstance(AES);
                c.init(Cipher.ENCRYPT_MODE, aesKey(key), RANDOM);
                // 加密
                byte[] bytes = c.doFinal(str.getBytes("UTF-8"));
                if (urlSafety) {
                    return Base64.encodeBase64URLSafeString(bytes);
                } else {
                    return new String(B64.encode(bytes));
                }
            } catch (Exception e) {
                return new BaseOut(2, "AES加密失败, 密文：" + str + ", key：" + key, null).toString();
            }
        }
        return null;
    }


    /**
     * AES解密
     *
     * @param str
     *            需要解密的密文(base64编码字符串)
     * @param key
     *            密钥
     * @return 解密后的明文
     */
    public static BaseOut aesDecrypt(String str, String key) {
        if (null != str && null != key) {
            try {
                Cipher c = Cipher.getInstance(AES);
                c.init(Cipher.DECRYPT_MODE, aesKey(key), RANDOM);
                // 解密
                return new BaseOut(0, "解密成功", new String(c.doFinal(B64.decode(str)), "UTF-8"));
            } catch (BadPaddingException e) {
                return new BaseOut(2, "AES解密失败, 密文：" + str + ", key：" + key, null);
            } catch (Exception e) {
                return new BaseOut(2, "AES解密失败, 密文：" + str + ", key：" + key, null);
            }
        }
        return null;
    }


    /** AES密钥 */
    private static SecretKeySpec aesKey(String key) {
        byte[] bs = key.getBytes();
        if (bs.length != 16) {
            bs = Arrays.copyOf(bs, 16);// 处理数组长度为16
        }
        return new SecretKeySpec(bs, AES_ALGORITHM);
    }

    public static String oaMd5() {
        String key = "coolcollege20201211sc";
        String thirdSecret = "135990bd839c5fe0a1ca9cbee2475431";
        return MD5(key + thirdSecret);
    }

    public static String oaB2gnMd5() {
        String key = "coolStore_buErJia_20220425";
        String thirdSecret = "d14cc076b44b435ea0ab06d0b7e04ea8";
        return MD5(key + thirdSecret);
    }

    public static String oaMd5(String param) {
        String key = "coolstore20220329";
        String thirdSecret = "d14cc076b44b435ea0ab06d0b7e04ea8";
        return MD5(key + thirdSecret + param);
    }

    public static String xfsgMd5(String param) {
        String key = "coolstorexfsg20240329";
        return MD5(key + param);
    }

    public static String getData(String ticket) {
        BaseOut result = aesDecrypt(ticket, oaMd5());
        if(result == null || result.getCode() != 0){
            return null;
        }
        return result.getData().toString();
    }

    public static String getB2gnData(String ticket) {
        BaseOut result = aesDecrypt(ticket, oaB2gnMd5());
        if(result == null || result.getCode() != 0){
            return null;
        }
        return result.getData().toString();
    }
}

class BaseOut {

    private int code = 0;

    private String msg;

    private Object data;

    public BaseOut() {
    }

    public BaseOut(int code) {
        this.code = code;
    }

    public BaseOut(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseOut(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

