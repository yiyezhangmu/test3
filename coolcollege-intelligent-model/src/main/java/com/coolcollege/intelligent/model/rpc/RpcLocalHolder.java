package com.coolcollege.intelligent.model.rpc;

/**
 * @author zhangchenbiao
 * @FileName: RpcLocalHolder
 * @Description:
 * @date 2022-07-11 18:53
 */
public class RpcLocalHolder {

    private static final ThreadLocal<String> enterpriseId = new ThreadLocal<String>();

    public static void setEnterpriseId(String enterpriseId){
        RpcLocalHolder.enterpriseId.set(enterpriseId);
    }

    public static String getEnterpriseId(){
        return RpcLocalHolder.enterpriseId.get();
    }

    public static void clear(){
        enterpriseId.remove();
    }

}
