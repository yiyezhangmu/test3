package com.coolcollege.intelligent.common.enums.yunda;

import org.apache.commons.lang3.StringUtils;

/**
 * @author wxp
 * @FileName: YundaEnterpriseEnum
 * @Description:
 */
public enum YundaEnterpriseEnum {


    YUNDA("86a2dff0a95e499899013dda56d8a0c3","韵达速递总部");

    private String code;

    private String message;

    YundaEnterpriseEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    /**
     * 判断是否是韵达类型的企业
     * @param eid
     * @return
     */
    public static Boolean yundaAffiliatedCompany(String eid){
        //判断是否是韵达类型的企业
        for (YundaEnterpriseEnum yundaEnterpriseEnum: YundaEnterpriseEnum.values()) {
            if (StringUtils.isNotBlank(eid) && eid.equals(yundaEnterpriseEnum.getCode())){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
