package com.coolcollege.intelligent.model.metatable.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 快速检查项导入实体
 * @author ：xugangkun
 * @date ：2022/1/7 16:31
 */
@Data
public class TbMetaQuickColumnImportDTO {

    @Excel(name = "描述", width = 30)
    private String dec;

    @Excel(name = "检查项分类", orderNum = "1", width = 10)
    private String category;

    @Excel(name = "检查项名称", orderNum = "2", width = 10)
    private String columnName;

    @Excel(name = "检查项描述", orderNum = "3", width = 10)
    private String description;

    @Excel(name = "分值", orderNum = "4", width = 10)
    private Long supportScore;

    @Excel(name = "奖励金额", orderNum = "5", width = 10)
    private BigDecimal awardMoney;

    @Excel(name = "惩罚金额", orderNum = "6", width = 10)
    private BigDecimal punishMoney;


}
