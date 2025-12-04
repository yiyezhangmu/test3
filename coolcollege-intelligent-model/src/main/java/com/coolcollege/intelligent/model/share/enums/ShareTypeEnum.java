package com.coolcollege.intelligent.model.share.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum ShareTypeEnum {
    STANDARD("standard","标准陈列"),
    TB_DISPLAY_STANDARD("tb_display_standard","新标准陈列"),
    QUESTION_ORDER("QUESTION_ORDER","工单"),
    PATROL("PATROL_STORE","巡店任务");
    private String shareType;
    private String shareName;


    private static final Map<String,ShareTypeEnum> shareTypeCodeEnumMap = Arrays.stream(values()).collect(Collectors.toMap(ShareTypeEnum::getShareName, data -> data));
    private static final Map<String,ShareTypeEnum> shareTypeNameEnumMap = Arrays.stream(values()).collect(Collectors.toMap(ShareTypeEnum::getShareType, data -> data));
    ShareTypeEnum(String shareType,String shareName){
        this.shareType = shareType;
        this.shareName = shareName;
    }


    public String getShareType() {
        return shareType;
    }

    public String getShareName() {
        return shareName;
    }
    public static ShareTypeEnum getShareTypeEnumByName(String shareName){
        return shareTypeCodeEnumMap.get(shareName);
    }
    public static ShareTypeEnum getShareTypeEnumByType(String shareType){
        return shareTypeNameEnumMap.get(shareType);
    }

    public static void main(String[] args) {
        ShareTypeEnum question_order = getShareTypeEnumByType("QUESTION_ORDER");
        System.out.println();
    }

}
