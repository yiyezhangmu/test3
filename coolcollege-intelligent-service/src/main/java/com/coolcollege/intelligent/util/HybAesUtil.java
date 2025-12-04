package com.coolcollege.intelligent.util;

import cn.hutool.json.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Random;

/**
 * 慧云班sso链接生成工具类
 *
 * @author litb
 * @since 2025/3/13 19:34
 */
@Component
@Slf4j
public class HybAesUtil {

    @Value("${api.hyb.appKey:null}")
    private String appKey;
    @Value("${api.hyb.appSecret:null}")
    private String appSecret;
    @Value("${api.hyb.domain:null}")
    private String domain;
    @Value("${api.hyb.url:null}")
    private String url;

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generatorUrl(String enterpriseId, String userId) {
        try {
            //采用AES对称加密
            SecretKeySpec secretKeySpec = new SecretKeySpec(appSecret.getBytes(), "AES");

            String transformation = "AES/ECB/PKCS5Padding";
            //加密Cipher
            Cipher cipherEncrypt = Cipher.getInstance(transformation);
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            //平台固定为酷店掌
            String platform = "coolstore";
            //1-6位 数字字母 随机数
            String nonce = generateNonce(1, 6);
            //毫秒时间戳
            Long tamestamp = System.currentTimeMillis();

            JSONObject jsonObject = new JSONObject();
//            jsonObject.set("app_secret", appSecret);
            jsonObject.set("platform", platform);
            jsonObject.set("tenant_id", enterpriseId);
            jsonObject.set("employee_id", userId);
            jsonObject.set("nonce", nonce);
            jsonObject.set("tamestamp", tamestamp);

            String jsonStr = jsonObject.toString();
            byte[] encryptBytes = cipherEncrypt.doFinal(jsonStr.getBytes());
            //加密后的字符串
            String encryptStr = Base64.getEncoder().encodeToString(encryptBytes);
            return domain + "/sso?token=" + encryptStr + "&app_key=" + appKey + "&url=" + url;
        } catch (Exception e) {
            log.error("加密失败", e);
            throw new ServiceException(ErrorCodeEnum.ENCRYPT_ERROR);
        }

    }

    /**
     * 生成随机字符串
     * @param minLength 最小长度
     * @param maxLength 最大长度
     * @return 随机字符串
     */
    private String generateNonce(int minLength, int maxLength) {
        Random random = new Random();
        // 随机生成字符串的长度
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // 从CHARACTERS中随机选择一个字符
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static void main(String[] args) throws Exception {

        String appKey = "app_key";

        //根据appKey找到对应的appSecret
        String appSecret = "CtKyuJpEkH8A6AEe";

        //采用AES对称加密
        SecretKeySpec secretKeySpec = new SecretKeySpec(appSecret.getBytes(), "AES");

        String transformation = "AES/ECB/PKCS5Padding";
        //加密Cipher
        Cipher cipherEncrypt = Cipher.getInstance(transformation);
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        //解密Cipher
        Cipher cipherDecrypt = Cipher.getInstance(transformation);
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec);


        //平台固定为酷店掌
        String platform = "coolstore";
        //三方租户id
        String tenantId = "11011";
        //三方员工id
        String employeeId = "94304321451354";
        //1-6位 数字字母 随机数
        String nonce = "134ff";
        //毫秒时间戳
        Long tamestamp = System.currentTimeMillis();

        JSONObject jsonObject = new JSONObject();
        jsonObject.set("app_secret", appSecret);
        jsonObject.set("platform", platform);
        jsonObject.set("tenant_id", tenantId);
        jsonObject.set("employee_id", employeeId);
        jsonObject.set("nonce", nonce);
        jsonObject.set("tamestamp", tamestamp);

        String jsonStr = jsonObject.toString();
        System.out.println(jsonStr);
        byte[] encryptBytes = cipherEncrypt.doFinal(jsonStr.getBytes());
        //加密后的字符串
        String encryptStr = Base64.getEncoder().encodeToString(encryptBytes);
        System.out.println(encryptStr);

        //解密
        byte[] decryptBytes = Base64.getDecoder().decode(encryptStr);
        String decryptStr = new String(cipherDecrypt.doFinal(decryptBytes));
        System.out.println(decryptStr);

        //构造的sso链接形如
        String link = "https://xxx.com/sso?token=" + encryptStr + "&app_key=" + appKey + "&url=" + "跳转的url";
    }
}
