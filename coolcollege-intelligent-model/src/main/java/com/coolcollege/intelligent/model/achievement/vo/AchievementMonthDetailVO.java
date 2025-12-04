package com.coolcollege.intelligent.model.achievement.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/28
 */
@Data
public class AchievementMonthDetailVO {


    private String storeId;
    /**
     * 门店名称
     */
    @Excel(name = "门店名称", width = 20, orderNum = "1")
    private String storeName;

    private Long regionId;

    /**
     * 区域名称
     */
    @Excel(name = "所属区域", width = 20, orderNum = "3")
    private String regionName;

    @Excel(name = "门店编号", width = 20, orderNum = "2")
    private String storeNum;

    /**
     * 月目标业绩额
     */
    @Excel(name = "本月目标业绩", width = 20, orderNum = "4",type = 10)
    private BigDecimal achievementTarget;
    /**
     * 月完成业绩额
     */
    @Excel(name = "本月完成业绩", width = 20, orderNum = "5",type = 10)
    private BigDecimal completionTarget;

    /**
     * 完成率
     */
    @Excel(name = "本月业绩完成率", width = 20, orderNum = "6")
    private Double completionRate;

    /**
     * 完成率
     */
    private String completionRateStr;

}
