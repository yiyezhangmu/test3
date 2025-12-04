package com.coolcollege.intelligent.common.enums.meta;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: MetaColumnTypeEnum
 * @Description: 检查项
 * @date 2022-04-01 19:23
 */
public enum MetaColumnTypeEnum {

    STANDARD_COLUMN(0, "普通项",2),
    HIGH_COLUMN(1, "高级项",2),
    RED_LINE_COLUMN(2, "红线项",1),
    VETO_COLUMN(3, "否决项",0),
    DOUBLE_COLUMN(4, "加倍项",2),
    COLLECT_COLUMN(5, "采集项",2),
    AI_COLUMN(6, "AI项",2),

    ;

    MetaColumnTypeEnum(Integer code, String message, Integer calScorePriority) {
        this.code = code;
        this.name = message;
        this.calScorePriority = calScorePriority;
    }

    private Integer code;

    private String name;

    private Integer calScorePriority;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getCalScorePriority() {
        return calScorePriority;
    }

    public static MetaColumnTypeEnum getColumnType(Integer columnType){
        if(Objects.isNull(columnType)){
            return null;
        }
        for (MetaColumnTypeEnum value : MetaColumnTypeEnum.values()) {
            if(value.code.equals(columnType)){
                return value;
            }
        }
        return null;
    }

    public static String getColumnTypeName(Integer columnType){
        if(Objects.isNull(columnType)){
            return null;
        }
        for (MetaColumnTypeEnum value : MetaColumnTypeEnum.values()) {
            if(value.code.equals(columnType)){
                return value.name;
            }
        }
        return null;
    }

    /**
     * 获取列表默认加载的项
     * @return
     */
    public static List<Integer> getDefaultColumnTypes(){
        List<Integer> resultList = new ArrayList<>();
        resultList.add(STANDARD_COLUMN.code);
        resultList.add(HIGH_COLUMN.code);
        resultList.add(RED_LINE_COLUMN.code);
        resultList.add(VETO_COLUMN.code);
        resultList.add(DOUBLE_COLUMN.code);
        resultList.add(COLLECT_COLUMN.code);
        return resultList;
    }
}
