package com.coolcollege.intelligent.model.metatable.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 普通项导入
 *
 * @author chenyupeng
 * @since 2021/12/6
 */
@Data
public class NormalColumnExportDTO {

    @Excel(name = "分类", width = 20, orderNum = "1")
    private String category;

    @Excel(name = "检查项名称（必填）", width = 40, orderNum = "2")
    private String columnName;

    @Excel(name = "检查项描述", width = 20, orderNum = "3")
    private String description;

    @Excel(name = "分值", width = 20, orderNum = "4")
    private String score;

    @Excel(name = "奖金（正数，如50）", width = 20, orderNum = "5")
    private String awardMoney;

    @Excel(name = "罚款（正数，如50）", width = 20, orderNum = "6")
    private String punishMoney;

    @Excel(name = "检查图片", width = 20, orderNum = "7")
    private String checkImg;

    @Excel(name = "检查描述", width = 30,orderNum = "8")
    private String checkDec;
}
