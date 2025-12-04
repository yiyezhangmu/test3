package com.coolcollege.intelligent.model.achievement.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shuchang.wei
 * @date 2021/5/26 10:00
 */
@Data
public class AchievementDetailExportDTO {

    private static final long serialVersionUID = 1L;

    @Excel(name = "门店名称")
    private String storeName;

    private Date produceTime;

    @Excel(name = "业绩值")
    private BigDecimal achievementAmount;

    @Excel(name = "业绩类型")
    private String achievementTypeName;

    @Excel(name = "业绩产生人")
    private String produceUserName;

    @Excel(name = "上报人")
    private String createUserName;

    private Date createTime;

    @Excel(name = "上报时间")
    private String createDate;

    @Excel(name = "业绩产生日")
    private String produceDate;

    /**
     * 业绩类型id
     */
    private Long typeId;
}
