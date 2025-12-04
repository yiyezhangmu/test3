package com.coolcollege.intelligent.common.util;

/**
 * @Author suzhuhong
 * @Date 2021/8/4 19:21
 * @Version 1.0
 */
public class StateMachineUtils {
    /**
     * 状态机 数字转为二进制
     * @param n
     */
    public static StringBuffer decimalToBinary(int n){
        StringBuffer submit = new StringBuffer() ;
        for(int i = 2;i >= 0; i--){
            submit.append(n >>> i & 1);
        }
        return submit;
    }

    /**
     * 状态机 二进制转为数字
     * @param binaryString
     */
    public static Integer binaryToDecimal(String binaryString){
       return Integer.parseInt(binaryString, 2);
    }

}
