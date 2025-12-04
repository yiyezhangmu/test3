package com.coolcollege.intelligent.common.enums.baili;

/**
 * @author suzhuhong
 * @FileName: BailiEnterpriseEnum
 * @Description:
 */
public enum BailiEnterpriseEnum {


    XXX("1ad10f0ee1234be3a5ce7256ad794ffd",""),
    /**
     * 百丽 线上
     */
    FIVE("de7c6b2f39444402a97e4d081643224d",""),
    /**
     *依迪索 线上环境
     */
    EDS("c29a9e6ca52b4f26b877dead5a152048",""),
    /**
     * 新业务事业部 线上环境
     */
    XYW("f51dab7fc31d4c42bc7401e2f0af3ef0",""),

    /**
     * 测试环境 鞋类事业部
     */
    TEST_XYW("0d99ec8b2bd4441f9ba206057f937716",""),

    RRR("45f92210375346858b6b6694967f44de",""),

    Online_NEW_BELLE("61444effa18f4d23997864de205dff3c",""),


    ONLINE_NEW_BELLE_TEST("0a528f2da6de40b9a22af3c8f3c700a8", ""),

    FIVE_ONLINE_NEW_BELLE("83687bcf05434474a6441b9060bb6ba1", ""),




//    ONLINE("",""),

;



    private String code;

    private String message;

    BailiEnterpriseEnum(String code, String message) {
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
     * @param eid
     * @return
     */
    public static Boolean bailiAffiliatedCompany(String eid){
        //判断是否是百丽类型的企业
        for (BailiEnterpriseEnum bailiEnterpriseEnum:BailiEnterpriseEnum.values()) {
            if (eid.equals(bailiEnterpriseEnum.getCode())){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
