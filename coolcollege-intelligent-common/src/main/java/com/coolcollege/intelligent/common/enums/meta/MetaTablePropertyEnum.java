package com.coolcollege.intelligent.common.enums.meta;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: MetaTablePropertyEnum
 * @Description: 表属性
 * @date 2022-04-01 20:13
 */
public enum MetaTablePropertyEnum {

    STANDARD_TABLE(0, "普通表", Lists.newArrayList(MetaColumnTypeEnum.STANDARD_COLUMN)),
    HIGH_TABLE(1, "高级表", Lists.newArrayList(MetaColumnTypeEnum.HIGH_COLUMN)),
    ADD_SCORE_TABLE(2, "加分表",Lists.newArrayList(MetaColumnTypeEnum.HIGH_COLUMN, MetaColumnTypeEnum.RED_LINE_COLUMN, MetaColumnTypeEnum.VETO_COLUMN, MetaColumnTypeEnum.DOUBLE_COLUMN, MetaColumnTypeEnum.COLLECT_COLUMN)),
    WEIGHT_TABLE(3, "权重表", Lists.newArrayList(MetaColumnTypeEnum.HIGH_COLUMN, MetaColumnTypeEnum.COLLECT_COLUMN)),
    DEDUCT_SCORE_TABLE(4, "扣分表", Lists.newArrayList(MetaColumnTypeEnum.HIGH_COLUMN, MetaColumnTypeEnum.VETO_COLUMN, MetaColumnTypeEnum.COLLECT_COLUMN)),
    AI_TABLE(5, "AI表", Lists.newArrayList(MetaColumnTypeEnum.AI_COLUMN)),
    USER_DEFINED_TABLE(6, "自定义表", Lists.newArrayList()),;

    private Integer code;

    private String name;

    private List<MetaColumnTypeEnum> columnTypes;

    MetaTablePropertyEnum(Integer code, String message, List<MetaColumnTypeEnum> columnTypes) {
        this.code = code;
        this.name = message;
        this.columnTypes = columnTypes;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<MetaColumnTypeEnum> getColumnTypes() {
        return columnTypes;
    }

    /**
     * 获取表对应的项
     * @param code
     * @return
     */
    public static List<Integer> getTableColumnTypes(Integer code){
        for (MetaTablePropertyEnum value : MetaTablePropertyEnum.values()) {
            if(value.code.equals(code)){
                return value.columnTypes.stream().map(MetaColumnTypeEnum::getCode).collect(Collectors.toList());
            }
        }
        return null;
    }

    public static MetaTablePropertyEnum getTablePropertyEnum(Integer code){
        for (MetaTablePropertyEnum value : MetaTablePropertyEnum.values()) {
            if(value.code.equals(code)){
                return value;
            }
        }
        return null;
    }
}
