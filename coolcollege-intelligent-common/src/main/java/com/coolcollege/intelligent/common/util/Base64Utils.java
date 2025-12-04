package com.coolcollege.intelligent.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/02
 */
public class Base64Utils {

    public static  String strConvertBase(String str) {
        if(null != str){
            Base64.Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(str.getBytes());
        }
        return null;
    }

    public static   String baseConvertStr(String str) {
        if(null != str){
            Base64.Decoder decoder = Base64.getDecoder();
            try {
                return new String(decoder.decode(str.getBytes()), "utf-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
        return null;
    }
}
