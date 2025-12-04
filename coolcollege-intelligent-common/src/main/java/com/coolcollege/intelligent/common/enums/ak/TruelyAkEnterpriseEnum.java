package com.coolcollege.intelligent.common.enums.ak;

import org.apache.commons.lang3.StringUtils;

/**
 * @author wxp
 * @FileName: AkEnterpriseEnum
 * @Description: 奥康企业
 */
public enum TruelyAkEnterpriseEnum {


    AOKANG("0954c8399b5749c395e1c9e20c028c87","奥康国际"),
    AOKANGPARTNER("798835a93c174ea39344e51bc46b46f1","奥康国际合作伙伴"),


    ;

    private String code;

    private String message;

    TruelyAkEnterpriseEnum(String code, String message) {
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
     * 判断是否是奥康类型的企业
     * @param eid
     * @return
     */
    public static Boolean aokangAffiliatedCompany(String eid){
        //判断是否是奥康类型的企业
        for (TruelyAkEnterpriseEnum yundaEnterpriseEnum: TruelyAkEnterpriseEnum.values()) {
            if (StringUtils.isNotBlank(eid) && eid.equals(yundaEnterpriseEnum.getCode())){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
