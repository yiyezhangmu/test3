package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnReasonValueDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author byd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbMetaStaColumnDetailExportVO extends TenRegionExportDTO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 巡店记录id
     */
    @Excel(name = "巡店记录id", orderNum = "0")
    private Long businessId;

    /**
     * 自定义名称
     */
    @Excel(name = "检查项名称", orderNum = "6")
    private String columnName;

    @Excel(name = "所属分类", orderNum = "8")
    private String categoryName;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称", orderNum = "1")
    private String storeName;

    @Excel(name = "门店编号", orderNum = "2")
    private String storeNum;

    /**
     * 任务名称
     */
    @Excel(name = "任务名称", orderNum = "3")
    private String taskName;

    /**
     * 任务描述
     */
    @Excel(name = "任务说明", orderNum = "4")
    private String taskDesc;

    /**
     * 区域ID
     */
    private Long regionId;

    private String regionName;



    @Excel(name = "所属区域", orderNum = "0")
    private String fullRegionName;

    /**
     * 区域名称列表
     */
    private List<String> regionNameList;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 检查表名称
     */
    @Excel(name = "任务内容", orderNum = "6")
    private String metaTableName;

    /**
     * 默认分值
     */
    @Excel(name = "本次得分", orderNum = "18", type = 10)
    private BigDecimal checkScore;

    /**
     * 结果
     */

    private String checkResult;

    /**
     * 结果
     */
    @Excel(name = "巡店结果", orderNum = "20")
    private String checkResultName;

    /**
     * 结果
     */
    @Excel(name = "统计维度", orderNum = "17")
    private String statisticalDimension;


    /**
     * 检查项分值
     */
    @Excel(name = "检查项分值", orderNum = "9", type = 10)
    private BigDecimal supportScore;

    /**
     * 处罚金额
     */
    private BigDecimal punishMoney;

    /**
     * 奖励金额
     */
    private BigDecimal awardMoney;

    /**
     * 标准图
     */
    @Excel(name = "标准图", orderNum = "11")
    private String standardPic;

    /**
     * SOP文档id
     */
    private Long sopId;

    /**
     * 酷学院课程信息
     */
    private String coolCourse;

    /**
     * 酷学院课程信息
     */
    @Excel(name = "附件/知识库", orderNum = "14")
    private String coolCourseAndSop;


    @Excel(name = "巡店记录创建时间", orderNum = "15", format = "yyyy.MM.dd HH:mm")
    private Date createTime;

    @Excel(name = "巡店人名称",orderNum = "16")
    private String supervisorName;

    /**
     * 巡店人id
     */
    private String supervisorId;

    @Excel(name = "巡店人职位",orderNum = "16")
    private String supervisorPositionName;

    /**
     * 检查结果
     */
    @Excel(name = "检查结果", orderNum = "19")
    private String checkInfo;

    private String checkPics;

    /**
     * 检查结果
     */
    @Excel(name = "结果细则", orderNum = "21")
    private String checkResultReasonName;

    /**
     * 备注
     */
    @Excel(name = "备注", orderNum = "22")
    private String checkText;

    /**
     * 子任务审批链开始时间
     */

    private Date subBeginTime;

    @Excel(name = "任务有效期", orderNum = "5")
    private String validTime;
    /**
     * 子任务审批链结束时间
     */
    private Date subEndTime;


    private String value1;

    private String value2;

    @Excel(name = "检查项标准", orderNum = "12")
    private String description;

    /**
     * 关联场景id
     */
    private Long storeSceneId;

    @Excel(name = "关联场景", orderNum = "13")
    private String storeSceneName;

    /**
     * 实际奖赏
     */
    @Excel(name = "奖罚金额（实际）", orderNum = "19")
    private String checkAwardPunish;

    /**
     * 奖惩（标准）
     */
    @Excel(name = "奖惩（标准）", orderNum = "10")
    private String awardPunish;

    private String format;

    private Integer tableProperty;

    private String checkResultReason;

    private String patrolType;

    /**
     * 页面停留时间
     */
    private String dwellTime;

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

    private String checkVideo;

    public String getCheckResultReasonName() {
        if (StringUtils.isBlank(this.getCheckResultReason())) {
            return null;
        }
        List<ColumnReasonValueDTO> columnReasonValueDTOList = JSONObject.parseArray(this.getCheckResultReason(), ColumnReasonValueDTO.class);
        if (CollectionUtils.isNotEmpty(columnReasonValueDTOList)) {
            List<String> nameList = columnReasonValueDTOList.stream().map(ColumnReasonValueDTO::getReasonName).collect(Collectors.toList());
            this.checkResultReasonName = StringUtils.join(nameList, Constants.BR);
        }
        return this.checkResultReasonName;
    }

}