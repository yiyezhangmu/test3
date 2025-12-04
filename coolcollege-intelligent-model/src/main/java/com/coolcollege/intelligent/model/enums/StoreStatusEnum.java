package com.coolcollege.intelligent.model.enums;

/**
 * Created by Administrator on 2020/1/20.
 */
public enum StoreStatusEnum {

    //营业
    OPEN("open","在营"),
    //闭店
    CLOSED("closed","闭店解约"),
    //未开业
    NOT_OPEN("not_open","未开业"),
    //迁址
    CHANGE_ADDRESS("change_address","迁址"),
    //退单
    CHARGEBACK("chargeback","退单"),
    //暂停营业
    CLOSE_UP("close_up","暂停营业");
    ;


    private final String value;

    private final String name;

    StoreStatusEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static StoreStatusEnum parse(String value) {
        for (StoreStatusEnum storeStatusEnum : StoreStatusEnum.values()) {
            if (storeStatusEnum.getValue().equals(value)) {
                return storeStatusEnum;
            }
        }
        return null;
    }

    public static String getName(String value) {
        for (StoreStatusEnum storeStatusEnum : StoreStatusEnum.values()) {
            if (storeStatusEnum.getValue().equals(value)) {
                return storeStatusEnum.name;
            }
        }
        return null;
    }

    public static String getCode(String flag) {
        switch (flag) {
            case "营业":
            case "在营":
                return "open";
            case "闭店":
            case "闭店解约":
                return "closed";
            case "未开业":
                return "not_open";
            case "迁址":
                return "change_address";
            case "退单":
                return "chargeback";
            case "暂停营业":
                return "close_up";
        }
        return "open";
    }
}
