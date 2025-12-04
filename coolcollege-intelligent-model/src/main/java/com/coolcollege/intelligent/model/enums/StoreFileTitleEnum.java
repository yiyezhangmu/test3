package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 门店文件标题枚举
 */
public enum StoreFileTitleEnum {
    STORE_NAME("门店名称(必填)"),// 门店名称(必填)
    STORE_NUM("门店编号"),// 门店编号
    STORE_AREA("所属区域(必填)"),// 所属区域(必填)
    SHOPOWNER_ID("店长用户ID(必填)"),// 店长ID(必填)
    TELEPHONE("联系方式"),// 联系方式
    DEVICE_ID("关联B1设备号"),// 关联B1设备号
    LONGITUDE_LATITUDE("门店定位坐标"),// 门店定位坐标
    STORE_ADDRESS("地址"),// 门店地址(必填)
    OPERATOR_ID("运营用户ID"),// 门店运营ID
    CLERK_ID("店员用户ID"),// 店员ID
    REMARK("备注"),   // 备注
    STORE_ID("门店id(请勿操作该栏！)") // 门店id
    ;

    private final String value;

    private static final Map<String, StoreFileTitleEnum> map = Arrays.stream(values()).collect(Collectors.toMap(StoreFileTitleEnum::getValue, Function.identity()));

    StoreFileTitleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StoreFileTitleEnum parse(int value) {
        return map.get(value);
    }
}
