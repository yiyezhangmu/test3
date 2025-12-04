package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author suzhuhong
 * @Date 2021/11/2 22:13
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbOpenHighCheckColumnDetailVO extends TenRegionExportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String regionName;

    @Excel(name = "所属区域", orderNum = "0")
    private String fullRegionName;
    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    private String storeName;

    @Excel(name = "门店编号")
    private String storeNum;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    private String taskName;

    /**
     * 任务描述
     */
    @Excel(name = "任务说明")
    private String taskDesc;

    @Excel(name = "任务有效期")
    private String validTime;

    /**
     * 检查表名称
     */
    @Excel(name = "任务内容")
    private String metaTableName;

    /**
     * 自定义名称
     */
    @Excel(name = "检查项名称")
    private String columnName;

    @Excel(name = "所属分类")
    private String categoryName;
    /**
     * 检查项分值
     */
    @Excel(name = "检查项分值", type = 10)
    private Integer supportScore;

    /**
     * 奖惩（标准）
     */
    @Excel(name = "奖惩（标准）")
    private String awardPunish;

    /**
     * 标准图
     */
    @Excel(name = "标准图")
    private String standardPic;
    @Excel(name = "检查项标准")
    private String description;
    @Excel(name = "关联场景")
    private String storeSceneName;
    /**
     * 酷学院课程信息
     */
    @Excel(name = "附件/知识库")
    private String coolCourseAndSop;
    @Excel(name = "巡店结果")
    private String checkResultName;
    /**
     * 结果
     */
    @Excel(name = "统计维度")
    private String statisticalDimension;
    /**
     * 默认分值
     */
    @Excel(name = "得分", type = 10)
    private Long checkScore;
    /**
     * 实际奖赏
     */
    @Excel(name = "奖罚金额（实际）")
    private String checkAwardPunish;
    /**
     * 检查结果
     */
    @Excel(name = "检查结果")
    private String checkPics;
    /**
     * 备注
     */
    @Excel(name = "备注")
    private String checkText;


}
