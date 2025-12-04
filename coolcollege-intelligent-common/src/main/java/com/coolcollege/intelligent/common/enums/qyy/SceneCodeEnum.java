package com.coolcollege.intelligent.common.enums.qyy;

/**
 * @author zhangchenbiao
 * @FileName: SceneCodeEnum
 * @Description: 场景code
 * @date 2023-04-19 19:30
 */
public enum SceneCodeEnum {

    /**
     * 业绩分配  achieveAllocate
     * 业绩排行TOP3 storeAchieveRanking
     * 区域业绩报告 regionAchieve
     * 全国业绩报告 nationAchieve
     * 店长周报 shopownerWeekly
     * 开单播报 openOrder
     * 大单播报 bigOrder
     * 主推款播报 recommendStyle
     */
    ACHIEVE_ALLOCATE("achieveAllocate", "业绩分配"),
    STORE_ACHIEVE_RANKING("storeAchieveRanking", "业绩排行TOP3"),
    REGION_ACHIEVE("regionAchieve", "区域业绩报告"),
    NATION_ACHIEVE("nationAchieve", "全国业绩报告"),
    SHOP_OWNER_WEEKLY("shopownerWeekly", "店长周报"),
    OPEN_ORDER("openOrder", "开单播报"),
    BIG_ORDER("bigOrder", "大单播报"),
    RECOMMEND_STYLE("recommendStyle", "主推款播报"),
    ACHIEVE_GOAL_PUSH("achieveGoalPush","业绩目标推送"),//卓诗尼用户业绩目标使用


    ;

    private String code;

    private String remark;

    public String getCode() {
        return code;
    }

    public String getRemark() {
        return remark;
    }

    SceneCodeEnum(String code, String remark) {
        this.code = code;
        this.remark = remark;
    }
}
