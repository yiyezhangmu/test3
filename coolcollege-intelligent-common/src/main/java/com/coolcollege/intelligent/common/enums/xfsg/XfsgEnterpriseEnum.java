package com.coolcollege.intelligent.common.enums.xfsg;

/**
 * @author wxp
 * @FileName: 鲜丰水果
 * @Description:
 */
public enum XfsgEnterpriseEnum {


    ONE("28c20a7b42b94171acb1ab3f631d69e1", "鲜丰水果测试"),
    TWO("9ee7b8b48e2447f9a2075b5a46e94d08", "鲜丰水果线上"),
    ;


    private String code;

    private String message;

    XfsgEnterpriseEnum(String code, String message) {
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
     * 判断是否是鲜丰水果类型的企业
     * @param eid
     * @return
     */
    public static Boolean xfsgCompany(String eid) {
        //判断是否是百丽类型的企业
        for (XfsgEnterpriseEnum xfsgEnterpriseEnum : XfsgEnterpriseEnum.values()) {
            if (eid.equals(xfsgEnterpriseEnum.getCode())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
