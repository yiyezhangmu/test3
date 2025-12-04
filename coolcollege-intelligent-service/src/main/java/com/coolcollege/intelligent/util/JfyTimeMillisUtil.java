package com.coolcollege.intelligent.util;

/**
 * <p>
 * 杰峰云时间戳工具
 * </p>
 *
 * @author wangff
 * @since 2025/5/6
 */
public class JfyTimeMillisUtil {
    private static long timMillis;//时间戳
    private static long counter = 0l;//计数器
    /**
     * 获取计数器
     *
     * @return
     */
    private static synchronized String getCounter(){
        ++counter;
        if (counter < 10L)
            return "000000" + String.valueOf(counter);
        else if (counter < 100L)
            return "00000" + String.valueOf(counter);
        else if (counter < 1000L)
            return "0000" + String.valueOf(counter);
        else if (counter < 10000L)
            return "000" + String.valueOf(counter);
        else if (counter < 100000L)
            return "00" + String.valueOf(counter);
        else if (counter < 1000000L)
            return "0" + String.valueOf(counter);
        else if (counter < 10000000L)
            return String.valueOf(counter);
        else {
            counter = 1L;
            return "000000" + String.valueOf(counter);
        }
    }

    /**
     * 获取组合时间戳
     *
     * @return
     */

    public static String getTimMillis() {
        timMillis = System.currentTimeMillis();
        return getCounter() + String.valueOf(timMillis);
    }
}
