package com.coolcollege.intelligent.util;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * @author 邵凌志
 * @date 2020/11/18 9:49
 */
public class PinyinUtil {

    /**
     * 获取字符的首字母
     * @param ch
     * @return
     */
    public static String fillKey(char ch) {
        if (ch >= 'a' && ch <= 'z') {
            return String.valueOf(ch).toUpperCase();
        }
        if (ch >= 'A' && ch <= 'Z') {
            return String.valueOf(ch);
        }
        String[] str = PinyinHelper.toHanyuPinyinStringArray(ch);
        if (str == null) {
            return "#";
        } else {
            return String.valueOf(str[0].charAt(0)).toUpperCase();
        }
    }

    public static int compareTo(String a, String b) {
        if (a.equals("#") && b.equals("#")) {
            return 0;
        } else if (a.equals("#")) {
            return 1;
        } else {
            return a.compareTo(b);
        }
    }
}
