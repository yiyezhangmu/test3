package com.coolcollege.intelligent.common.enums.storework;

/**
 * @author wxp
 * @FileName: SortTypeEnum
 * @Description: 排序字段
 * @date 2022-09-21 9:48
 */
public enum SortFieldEnum {

    finishPercent("平均完成率"),
    totalColumnNum("应完成项"),
    unFinishColumnNum("未完成项"),
    finishColumnNum("已完成项"),
    avgPassRate("平均合格率"),
    avgScore("平均得分"),
    avgScoreRate("平均得分率"),
    questionNum("工单数"),
    failColumnNum("不合格项数"),
    passColumnNum("合格项数"),
    totalStoreNum("应完成门店 该区域下的门店数"),
    unFinishStoreNum("未完成门店"),
    failRate("不合格率"),
    passRate("合格率"),
    ;

    private String message;

    SortFieldEnum(String message) {
        this.message = message;
    }

    public static SortFieldEnum getSortFieldEnum(String code){
        for (SortFieldEnum value : SortFieldEnum.values()) {
            if(value.name().equals(code)){
                return value;
            }
        }
        return null;
    }
}
