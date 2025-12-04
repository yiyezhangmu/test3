package com.coolcollege.intelligent.common.enums.josiny;

import org.apache.commons.lang3.StringUtils;

/**
 * @author wxp
 * @FileName: JosinyEnterpriseEnum
 * @Description:
 */
public enum JosinyEnterpriseEnum {


    YUNDA("ding008c300096090fa7","浙江卓诗尼控股有限公司");

    private String code;

    private String message;

    JosinyEnterpriseEnum(String code, String message) {
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
     * 判断是否是卓诗尼类型的企业
     * @param corpId
     * @return
     */
    public static Boolean josinyAffiliatedCompany(String corpId){
        //判断是否是韵达类型的企业
        for (JosinyEnterpriseEnum josinyEnterpriseEnum: JosinyEnterpriseEnum.values()) {
            if (StringUtils.isNotBlank(corpId) && corpId.equals(josinyEnterpriseEnum.getCode())){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
