package com.coolcollege.intelligent.common.enums;

/**
 * @author Aaron
 * @Description 业务统一返回码
 * @date 2019/12/20
 */
public enum RegionTypeEnum {
    /**
     *
     */
    ROOT("root", "根节点"),


    PATH("path", "区域"),


    STORE("store", "门店");


    private String type;


    private String desc;

    RegionTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
