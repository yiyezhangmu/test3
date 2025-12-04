package com.coolcollege.intelligent.model.qyy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyNewspaperDataDO implements Serializable {
    private Long id;

    /**
     * 所在周的周一（yyyy-MM-dd）
     */
    private String mondyOfWeek;

    /**
     * 组织id(门店id)
     */
    private String dingDeptId;

    /**
     * 分公司排名
     */
    private String compRank;

    /**
     * 全国排名
     */
    private String nationalRank;

    /**
     * 月目标
     */
    private String  monthTarget;

    /**
     * 月达成率
     */
    private String monthAchieveRate;

    /**
     * 周目标
     */
    private String weekTarget;

    /**
     * 周业绩
     */
    private String weekAchieve;


    /**
     * 周连带率
     */
    private String weekAssociatedRate;

    /**
     * 销量top5_JSON
     */
    private String salesVolums;
}
