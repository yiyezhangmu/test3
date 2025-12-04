package com.coolcollege.intelligent.common.util;

/**
 * describe:
 *
 * @author wxp
 * @date 2021/06/17
 */
public class Base16Utils {

    public static String hex2Str(String theHex) {
        char[] chars = theHex.toCharArray();
        int len = chars.length / 2;
        byte[] theByte = new byte[len];

        for (int i = 0; i < len; i++) {
            theByte[i] = Integer.decode("0X" + chars[i*2] + chars[i*2+1]).byteValue();
        }

        return new String(theByte);
    }

    public static String str2Hex(String theStr) {
        int tmp;
        String tmpStr;
        byte[] bytes = theStr.getBytes();
        StringBuffer result = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            tmp = bytes[i];
            if (tmp < 0) {
                tmp += 256;
            }

            tmpStr = Integer.toHexString(tmp);
            if (tmpStr.length() == 1) {
                result.append('0');
            }

            result.append(tmpStr);
        }

        return result.toString();
    }

    public static void main(String[] argv) {
        String a = "abcdef你好吗？";
        String b = str2Hex(a);
        String c = hex2Str(b);
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }

}