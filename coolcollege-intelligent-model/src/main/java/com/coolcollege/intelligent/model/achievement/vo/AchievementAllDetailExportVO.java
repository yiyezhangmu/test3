package com.coolcollege.intelligent.model.achievement.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chenyupeng
 * @since 2021/10/30
 */
@Data
public class AchievementAllDetailExportVO {

    /**
     * 门店名称
     */
    @Excel(name = "门店名称", width = 20, orderNum = "1")
    private String storeName;

    /**
     * 业绩产生时间
     */
    @Excel(name = "业绩产生日", width = 20, orderNum = "2")
    private String produceTime;

    /**
     * 业绩值
     */
    @Excel(name = "业绩值", width = 20, orderNum = "3")
    private BigDecimal achievementAmount;

    /**
     * 模板名称
     */
    @Excel(name = "业绩模板", width = 20, orderNum = "4")
    private String formworkName;

    /**
     * 业绩类型名称
     */
    @Excel(name = "业绩类型", width = 20, orderNum = "5")
    private String achievementTypeName;

    /**
     * 业绩产生人姓名
     */
    @Excel(name = "业绩产生人", width = 20, orderNum = "6")
    private String produceUserName;

    /**
     * 上传人姓名
     */
    @Excel(name = "上报人", width = 20, orderNum = "6")
    private String createUserName;

    /**
     * 上报时间
     */
    @Excel(name = "上报时间", width = 20, orderNum = "6")
    private String editTime;
}
