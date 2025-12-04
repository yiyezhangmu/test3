package com.coolcollege.intelligent.model.question.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * 导出工单
 *
 * @author zhangnan
 * @date 2021-12-29 9:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbQuestionSubRecordListExportVO {


    @ApiModelProperty("AI工单 AI、普通工单 common、巡店工单 patrolStore")
    @Excel(name = "工单来源", replace = {"_null", "AI工单_AI", "普通工单_common", "巡店工单_patrolStore","店务工单_storeWork", "复审工单_patrolRecheck", "稽核工单_safetyCheck"}, orderNum = "0", width = 20)
    private String questionType;

    @ApiModelProperty("父工单编号")
    @Excel(name = "父工单编号", orderNum = "1")
    private Long id;

    @ApiModelProperty("父工单名称")
    @Excel(name = "父工单名称", orderNum = "2", width = 20)
    private String questionName;



    @Excel(name = "发起人", orderNum = "4", width = 20)
    private String createUserName;

    @Excel(name = "发起时间", format = "yyyy.MM.dd HH:mm", orderNum = "5", width = 20)
    private Date createTime;


    @ApiModelProperty("创建人")
    private String createId;


    @Excel(name = "子工单编号", orderNum = "6")
    private Long recordId;

    @Excel(name = "子工单名称", orderNum = "7")
    private String taskName;

    @Excel(name = "工单说明", orderNum = "8")
    private String taskDesc;

    @Excel(name = "工单附件", orderNum = "9")
    private String photosAndVideos;

    @Excel(name = "检查表", orderNum = "10")
    private String metaTableName;

    @Excel(name = "检查项", orderNum = "11")
    private String metaColumnName;

    @Excel(name = "检查结果", orderNum = "12")
    private String checkResultName;

    @Excel(name = "门店", orderNum = "13")
    private String storeName;

    @Excel(name = "门店编号", orderNum = "14")
    private String storeNum;

    @Excel(name = "所属区域", orderNum = "15")
    private String fullRegionName;

    @Excel(name = "截止时间", orderNum = "16", format = "yyyy.MM.dd HH:mm")
    private Date handlerEndTime;

    @Excel(name = "工单状态", orderNum = "17")
    private String status;

    @Excel(name = "处理状态（最新）", orderNum = "18")
    private String handleActionKey;

    @Excel(name = "是否逾期", orderNum = "18")
    private String isOverdue;

    @Excel(name = "指派整改人", orderNum = "20")
    private String assignHandleUserName;

    @Excel(name = "实际整改人", orderNum = "21")
    private String handleUserName;

    @Excel(name = "整改人提交时间", orderNum = "22", format = "yyyy.MM.dd HH:mm")
    private Date handleTime;

    @Excel(name = "整改人备注", orderNum = "23")
    private String handleRemark;

    @Excel(name = "整改人附件", orderNum = "24")
    private String handlePhoto;

    @Excel(name = "指派一级审批人", orderNum = "25")
    private String assignApproveUserName;

    @Excel(name = "实际一级审批人", orderNum = "26")
    private String approveUserName;

    @Excel(name = "一级审批人提交时间", orderNum = "27", format = "yyyy.MM.dd HH:mm")
    private Date approveTime;

    @Excel(name = "一级审批人备注", orderNum = "28")
    private String approveRemark;

    @Excel(name = "一级审批人附件", orderNum = "29")
    private String approvePhoto;

    @Excel(name = "指派二级审批人", orderNum = "30")
    private String assignSecondApproveUserName;

    @Excel(name = "实际二级审批人", orderNum = "31")
    private String secondApproveUserName;

    @Excel(name = "二级审批人提交时间", orderNum = "32", format = "yyyy.MM.dd HH:mm")
    private Date secondApproveTime;

    @Excel(name = "二级审批人备注", orderNum = "33")
    private String secondApproveRemark;

    @Excel(name = "二级审批人附件", orderNum = "34")
    private String secondApprovePhoto;

    @Excel(name = "指派三级审批人", orderNum = "35")
    private String assignThirdApproveUserName;

    @Excel(name = "实际三级审批人", orderNum = "36")
    private String thirdApproveUserName;

    @Excel(name = "三级审批人提交时间", orderNum = "37", format = "yyyy.MM.dd HH:mm")
    private Date thirdApproveTime;

    @Excel(name = "三级审批人备注", orderNum = "38")
    private String thirdApproveRemark;

    @Excel(name = "三级审批人附件", orderNum = "39")
    private String thirdApprovePhoto;

    @Excel(name = "抄送人", orderNum = "40")
    private String ccUserNames;

    @Excel(name = "工单完成时间", orderNum = "41", format = "yyyy.MM.dd HH:mm")
    private Date completeTime;

    @Excel(name = "工单总时长(小时)", orderNum = "42")
    private String totalDuration;
    

    private BigDecimal checkScore;

    private String rewardPenaltMoney;

    private Long totalDurationTime;

    public String getCheckResultName(){
        if(this.checkScore != null){
            this.checkResultName = this.checkResultName + "(" + this.checkScore +  "分)";
        }
        if(StringUtils.isNotBlank(this.rewardPenaltMoney)){
            this.checkResultName = this.checkResultName + "(" + this.rewardPenaltMoney +  ")";
        }
        return this.checkResultName;
    }

    public String getTotalDuration(){
        if(totalDurationTime == null){
            return null;
        }
        return new BigDecimal(this.totalDurationTime).divide(new BigDecimal(60 * 60 * 1000),2, RoundingMode.HALF_UP).toString();
    }
}
