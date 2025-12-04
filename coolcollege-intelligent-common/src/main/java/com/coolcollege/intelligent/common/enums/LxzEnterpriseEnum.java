package com.coolcollege.intelligent.common.enums;

/**
 * @author suzhuhong
 * @FileName: 美宜佳
 * @Description:
 */
public enum LxzEnterpriseEnum {


    TEST("d9bbf08547b3400f9f438f9df74ef28a", "测试企业"),

    ONLINE("b265df3ac05749838120d3b1a0190e52", "西安兰湘子餐饮管理有限公司"),

    ;


    private String code;

    private String message;

    LxzEnterpriseEnum(String code, String message) {
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
     * 判断是否是兰湘子类型的企业
     *
     * @param eid
     * @return
     */
    public static Boolean lxzCompany(String eid) {
        //判断是否是百丽类型的企业
        for (LxzEnterpriseEnum myjEnterpriseEnum : LxzEnterpriseEnum.values()) {
            if (eid.equals(myjEnterpriseEnum.getCode())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
