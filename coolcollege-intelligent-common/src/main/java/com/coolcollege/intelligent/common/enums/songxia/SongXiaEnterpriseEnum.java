package com.coolcollege.intelligent.common.enums.songxia;

/**
 * @author suzhuhong
 * @FileName: 美宜佳
 * @Description:
 */
public enum SongXiaEnterpriseEnum {


    ONE("220d678f532d4cbd884045ba819f2fb9", ""),

    HD_TEST("cca977c6d84d47a38fe349216d686f82", ""),

    TEST("f6708e77c53c4b45882ba4dd31bd5001","")
    ;


    private String code;

    private String message;

    SongXiaEnterpriseEnum(String code, String message) {
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
     * 判断是否是松下类型的企业
     *
     * @param eid
     * @return
     */
    public static Boolean songXiaCompany(String eid) {
        //判断是否是百丽类型的企业
        for (SongXiaEnterpriseEnum myjEnterpriseEnum : SongXiaEnterpriseEnum.values()) {
            if (eid.equals(myjEnterpriseEnum.getCode())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
