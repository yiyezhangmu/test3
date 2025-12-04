package com.coolcollege.intelligent.model.patrolstore.statistics;

import java.math.BigDecimal;
import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检查项基础详情表
 * 
 * @author yezhe
 * @date 2020/12/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolStoreStatisticsStaColumnInfoDTO {

    private static final long serialVersionUID = 1L;

    private Long staColumnId;
    //问题工单id
    private Long taskQuestionId;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 所属区域
     */
    @Excel(name = "所属区域")
    private String regionName;

    /**
     * 门店名称
     */
    @Excel(name = "所属门店")
    private String storeName;

    /**
     * 任务名称
     */
    @Excel(name = "所属任务")
    private String taskName;

    /**
     * 检查表名称
     */
    @Excel(name = "检查表名称")
    private String tableName;

    /**
     * 检查项分类
     */
    @Excel(name = "检查项分类")
    private String categoryName;

    /**
     * 检查项名称
     */
    @Excel(name = "检查项名称")
    private String columnName;

    /**
     * 标准图
     */
    @Excel(name = "标准图")
    private String standardPic;

    /**
     * 检查项描述
     */
    @Excel(name = "检查项描述")
    private String description;

    /**
     * 等级
     */
    private String level;

    /**
     * 标准分值
     */
    @Excel(name = "标准分值")
    private BigDecimal supportScore;

    /**
     * 最低分
     */
    private BigDecimal lowestScore;

    /**
     * 奖惩（标准）
     */
    @Excel(name = "奖惩（标准）")
    private String awardPunish;

    /**
     * 结果
     */
    @Excel(name = "结果", replace = {"合格_PASS", "不合格_FAIL", "不适用_INAPPLICABLE", "_null"})
    private String checkResult;

    /**
     * 得分
     */
    @Excel(name = "得分")
    private BigDecimal checkScore;

    /**
     * 实际奖惩
     */
    @Excel(name = "实际奖惩")
    private String checkAwardPunish;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String checkText;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String taskDesc;

    /**
     * 检查图片
     */
    @Excel(name = "检查图片")
    private String checkPics;

    /**
     * 问题图片
     */
    @Excel(name = "问题图片")
    private String questionPics;

    /**
     * 检查时间
     */
    @Excel(name = "检查时间")
    private String checkTime;

    /**
     * 整改图片
     */
    @Excel(name = "整改图片")
    private String handlePics;

    /**
     * 整改完成时间
     */
    @Excel(name = "整改完成时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date handleDoneTime;

    /**
     * 是否过期解决
     */
    @Excel(name = "是否过期解决")
    private String overdue;

    /**
     * 问题流程状态
     */
    @Excel(name = "问题流程状态")
    private String questionStatus;

    /**
     * 检查人
     */
    @Excel(name = "检查人")
    private String checkUserName;

    /**
     * 整改人
     */
    @Excel(name = "整改人")
    private String handleUserName;

    /**
     * 复检人
     */
    @Excel(name = "复检人")
    private String reCheckUserName;

}
