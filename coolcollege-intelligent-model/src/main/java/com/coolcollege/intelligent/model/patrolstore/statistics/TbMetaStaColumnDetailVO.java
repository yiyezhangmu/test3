package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbMetaStaColumnDetailVO extends TenRegionExportDTO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 门店id
     */
    private String storeId;



    /**
     * 区域ID
     */
    private Long regionId;

    private String regionPath;


    /**
     * 检查表id
     */
    private Long metaTableId;


    /**
     * 结果
     */

    private String checkResult;


    /**
     * 处罚金额
     */
    private BigDecimal punishMoney;

    /**
     * 奖励金额
     */
    private BigDecimal awardMoney;

    /**
     * SOP文档id
     */
    private Long sopId;

    /**
     * 酷学院课程信息
     */
    private String coolCourse;



    private Date createTime;



    private String checkVideo;


    /**
     * 子任务审批链开始时间
     */

    private Date subBeginTime;

    /**
     * 子任务审批链结束时间
     */
    private Date subEndTime;


    private String value1;

    private String value2;


    /**
     * 统计维度
     */
    private String statisticalDimension;

    private String format;

    /**
     * 关联场景id
     */
    private Long storeSceneId;

    /**
     * 表属性
     */
    private Integer tableProperty;

    private String regionName;
    @Excel(name = "所属区域")
    private String fullRegionName;

    private List<String> regionNameList;

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
    private BigDecimal supportScore;

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
     * 默认分值
     */
    @Excel(name = "得分", type = 10)
    private BigDecimal checkScore;
    /**
     * 实际奖赏
     */
    @Excel(name = "奖罚金额（实际）")
    private String checkAwardPunish;
    /**
     * 检查结果
     */
    private String checkPics;

    @Excel(name = "检查结果")
    private String checkInfo;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String checkText;

    public String getCheckResultName(){
        if(StringUtils.isNotBlank(this.checkResultName)){
            return this.checkResultName;
        }
        if(StringUtils.isNotBlank(this.checkResult)){
            if("FAIL".equals(this.checkResult)){
                return "不合格";
            }else if("INAPPLICABLE".equals(this.checkResult)){
                return "不适用";
            }else if("PASS".equals(this.checkResult)){
                return "合格";
            }
        }
        return null;
    }

    private static final long serialVersionUID = 1L;

}