package com.coolcollege.intelligent.common.enums.baili;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 百丽门店设置自定义字段 枚举
 * @Author suzhuhong
 * @Date 2022/9/5 17:26
 * @Version 1.0
 */
public enum BailiCustomizeFieldEnum {

    /**
     * 主品牌
     */
    BRAND("brand","主品牌"),

    /**
     * 大区名称
     */
    ZONENAME("zoneName","大区名称"),


    /**
     * 省区
     */
    PROVINCENAME("provinceName","省区"),

    /**
     * 管理分区
     */
    MANGERCITY("mangerCity","管理分区"),
    /**
     * 经营城市
     */
    BIZCITY("bizCity","经营城市"),
    ;

    private String code;

    private String name;

    BailiCustomizeFieldEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


    public static List<String> getAllNames(){
        List<String> names = Arrays.asList(BailiCustomizeFieldEnum.values()).stream().map(BailiCustomizeFieldEnum::getName).collect(Collectors.toList());
        return names;
    }


    /**
     * oldNames在BailiCustomizeFieldEnum中不存在name筛选出来，，添加到数据库中
     * @param oldNames
     * @return
     */
    public static  List<String> getExcludeNames(List<String> oldNames){
        List<String> allNames = getAllNames();
        for (BailiCustomizeFieldEnum bailiCustomizeFieldEnum :Arrays.asList(BailiCustomizeFieldEnum.values())) {
            if (oldNames.contains(bailiCustomizeFieldEnum.getName())){
                allNames.remove(bailiCustomizeFieldEnum.getName());
            }
        }
        return allNames;
    }
}
