package com.coolcollege.intelligent.common.enums.myj;

/**
 * @author suzhuhong
 * @FileName: 美宜佳
 * @Description:
 */
public enum MyjEnterpriseEnum {


    ONE("faa7a67956dc480a82e1df44acf2cc08", ""),

    HD_TEST("eb2becdfe9db4279b70d369653356178", ""),

    HD_TEST_2("c50954fb02fc4923b8868201163afa3a", ""),

    TEST("e17cd2dc350541df8a8b0af9bd27f77d", ""),

    ;


    private String code;

    private String message;

    MyjEnterpriseEnum(String code, String message) {
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
     * 判断是否是百丽类型的企业
     *
     * @param eid
     * @return
     */
    public static Boolean myjCompany(String eid) {
        //判断是否是百丽类型的企业
        for (MyjEnterpriseEnum myjEnterpriseEnum : MyjEnterpriseEnum.values()) {
            if (eid.equals(myjEnterpriseEnum.getCode())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
