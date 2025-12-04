package com.coolcollege.intelligent.common.util;

import java.text.NumberFormat;

/**
 * @Author suzhuhong
 * @Date 2021/11/30 14:40
 * @Version 1.0
 * 算数计算 格式化处理
 */
public class NumberFormatUtils {

    public static final NumberFormat percentInstance =  NumberFormat.getPercentInstance();

    /**
     * 返回两个整数相除的百分百 小数点后面保留两位小数 例：33%
     * @param num1
     * @param num2
     * @return
     */
    public static String getPercentString(int num1,int num2){
        //设置两位小数
        //percentInstance.setMinimumFractionDigits(2);
        //百分之出现0-1之外的数值 兼容出来
        if (num1==0||num2==0){
            return "0%";
        }
        num1 = Math.min(num1,num2);
        return percentInstance.format((num1*1d)/num2);
    }

    /**
     * 返回两个整数相除的百分百 小数点后面保留两位小数 例：1.44%
     * @param num1
     * @param num2
     * @return
     */
    public static String getPercent(int num1,int num2){
        //设置两位小数
        percentInstance.setMinimumFractionDigits(2);
        //百分之出现0-1之外的数值 兼容出来
        if (num1==0||num2==0){
            return "0%";
        }
        num1 = Math.min(num1,num2);
        return percentInstance.format((num1*1d)/num2);
    }
}
