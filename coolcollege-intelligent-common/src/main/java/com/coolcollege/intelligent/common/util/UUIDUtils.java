package com.coolcollege.intelligent.common.util;

import cn.hutool.core.util.IdUtil;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.MD5Util;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;

/**
 * 唯一性ID工具类
 *
 * @author Aaron
 * @ClassName UUIDUtils
 * @Description 唯一性ID工具类
 */
public class UUIDUtils {

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     */
    public static String get32UUID() {
        return IdUtil.fastSimpleUUID();
    }
    public static String get8UUID() {
        return StringUtils.substring(IdUtil.fastSimpleUUID(),1,9);
    }

    public static Long get8LongUuid() {
        long uuid = (int) (Math.random() * 90000000 + 10000000);
        return uuid;
    }

    public static String generatePassword(int length) {
        if (length < 6) {
            throw new IllegalArgumentException("密码长度必须至少6位");
        }

        // 定义字符集合
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "@!#&";
        String allChars = uppercase + lowercase + digits + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // 确保每个字符类别至少包含一个
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // 填充剩余长度
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 随机打乱字符顺序
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    public static void main(String[] args) {
        System.out.println(generatePassword(8));

    }


}
