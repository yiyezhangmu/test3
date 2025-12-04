package com.coolcollege.intelligent.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/6/16 11:49
 */
public enum StoreInfoExportFieldEnum {
    STORE_NAME("门店名称","storeName",Boolean.FALSE),
    GROUP_NAME("门店分组","groupName",Boolean.FALSE),
    STORE_NUM("门店编号","storeNum",Boolean.FALSE),
    STORE_ADDRESS("门店地址","storeAddress",Boolean.FALSE),
    TELEPHONE("门店电话","telephone",Boolean.FALSE),
    BUSINESS_HOURS("营业时间","businessHours",Boolean.FALSE),
    STORE_ACREAGE("门店面积","storeAcreage",Boolean.FALSE),
    STORE_BANDWIDTH("门店带宽","storeBandwidth",Boolean.FALSE),
    REGION_NAME("区域名称","regionName",Boolean.FALSE),
    USER_NAME("门店人员","userName",Boolean.TRUE),
    REGION_PATH("一级区域","region_path",Boolean.FALSE),

    ;

    private String name;

    private String fieldName;
    /**
     * 是否默认导出
     */
    private Boolean defaultExport;

    private static final Map<String, StoreInfoExportFieldEnum> map = Arrays.stream(values()).collect(Collectors.toMap(StoreInfoExportFieldEnum::getName, Function.identity()));


    StoreInfoExportFieldEnum(String name, String fieldName, Boolean defaultExport) {
        this.name = name;
        this.fieldName = fieldName;
        this.defaultExport = defaultExport;
    }

    public String getName() {
        return name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static StoreInfoExportFieldEnum getEnum(String name){
        return map.get(name);
    }

    public static List<String> nameList(){
        return Arrays.stream(values()).filter(data -> !data.defaultExport).map(data -> data.getName()).collect(Collectors.toList());
    }

    public Boolean getDefaultExport() {
        return defaultExport;
    }
}
