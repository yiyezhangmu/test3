package com.coolcollege.intelligent.model.question.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
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
public class TbQuestionRecordListExportVO {


    @ApiModelProperty("AI工单 AI、普通工单 common、巡店工单 patrolStore")
    @Excel(name = "工单来源", replace = {"_null", "AI工单_AI", "普通工单_common", "巡店工单_patrolStore", "神秘访客工单_mysteriousGuest"}, orderNum = "0", width = 20)
    private String questionType;

    @ApiModelProperty("父工单编号")
    @Excel(name = "工单编号", orderNum = "1")
    private Long id;

    @ApiModelProperty("工单名称")
    @Excel(name = "工单名称", orderNum = "2", width = 20)
    private String questionName;

    @Excel(name = "子工单数", orderNum = "3")
    private Integer totalNum;

    @Excel(name = "进度", orderNum = "4")
    private String plannedSpeed;


    @Excel(name = "发起人", orderNum = "5", width = 20)
    private String createUserName;

    @Excel(name = "发起时间", format = "yyyy.MM.dd HH:mm", orderNum = "6", width = 20)
    private Date createTime;

    @ApiModelProperty("已完成数量")
    private Integer finishNum;


    @ApiModelProperty("创建人")
    private String createId;


    @Excel(name = "子工单编号", orderNum = "10")
    private Long recordId;

    @Excel(name = "子工单名称", orderNum = "11")
    private String taskName;

    @Excel(name = "工单说明", orderNum = "13")
    private String taskDesc;

    @Excel(name = "工单附件", orderNum = "14")
    private String photosAndVideos;

    @Excel(name = "检查表", orderNum = "15")
    private String metaTableName;

    @Excel(name = "检查项", orderNum = "16")
    private String metaColumnName;

    @Excel(name = "检查结果", orderNum = "17")
    private String checkResultName;

    @Excel(name = "门店", orderNum = "21")
    private String storeName;

    @Excel(name = "门店编号", orderNum = "22")
    private String storeNum;

    @Excel(name = "所属区域", orderNum = "23")
    private String fullRegionName;

    @Excel(name = "截止时间", orderNum = "25", format = "yyyy.MM.dd HH:mm")
    private Date handlerEndTime;

    @Excel(name = "工单状态", orderNum = "26")
    private String status;

    @Excel(name = "是否逾期", orderNum = "27")
    private String isOverdue;

    @Excel(name = "指派整改人", orderNum = "28")
    private String assignHandleUserName;

    @Excel(name = "实际整改人", orderNum = "29")
    private String handleUserName;

    @Excel(name = "整改人提交时间", orderNum = "30", format = "yyyy.MM.dd HH:mm")
    private Date handleTime;

    @Excel(name = "整改人备注", orderNum = "31")
    private String handleRemark;

    @Excel(name = "整改人附件", orderNum = "32")
    private String handlePhoto;

    @Excel(name = "指派一级审批人", orderNum = "33")
    private String assignApproveUserName;

    @Excel(name = "实际一级审批人", orderNum = "34")
    private String approveUserName;

    @Excel(name = "一级审批人提交时间", orderNum = "35", format = "yyyy.MM.dd HH:mm")
    private Date approveTime;

    @Excel(name = "一级审批人备注", orderNum = "36")
    private String approveRemark;

    @Excel(name = "一级审批人附件", orderNum = "37")
    private String approvePhoto;

    @Excel(name = "指派二级审批人", orderNum = "38")
    private String assignSecondApproveUserName;

    @Excel(name = "实际二级审批人", orderNum = "39")
    private String secondApproveUserName;


    @Excel(name = "二级审批人提交时间", orderNum = "40", format = "yyyy.MM.dd HH:mm")
    private Date secondApproveTime;

    @Excel(name = "二级审批人备注", orderNum = "41")
    private String secondApproveRemark;

    @Excel(name = "二级审批人附件", orderNum = "42")
    private String secondApprovePhoto;

    @Excel(name = "指派三级审批人", orderNum = "43")
    private String assignThirdApproveUserName;

    @Excel(name = "实际三级审批人", orderNum = "44")
    private String thirdApproveUserName;

    @Excel(name = "三级审批人提交时间", orderNum = "45", format = "yyyy.MM.dd HH:mm")
    private Date thirdApproveTime;

    @Excel(name = "三级审批人备注", orderNum = "46")
    private String thirdApproveRemark;

    @Excel(name = "三级审批人附件", orderNum = "47")
    private String thirdApprovePhoto;

    @Excel(name = "抄送人", orderNum = "48")
    private String ccUserNames;

    @Excel(name = "工单完成时间", orderNum = "52", format = "yyyy.MM.dd HH:mm")
    private Date completeTime;

    @Excel(name = "工单总时长(小时)", orderNum = "53")
    private String totalDuration;
    

    
    private BigDecimal checkScore;

    private String rewardPenaltMoney;

    private Long totalDurationTime;

    public void convertQuestionParentInfoVOForExport(TbQuestionParentInfoVO parentInfoVO) {
        this.questionType = QuestionTypeEnum.getByCode(parentInfoVO.getQuestionType());
        this.id = parentInfoVO.getId();
        this.questionName = parentInfoVO.getQuestionName();
        this.totalNum = parentInfoVO.getTotalNum();
        this.plannedSpeed = parentInfoVO.getPlannedSpeed();
        this.createUserName = parentInfoVO.getCreateUserName();
        this.createTime = parentInfoVO.getCreateTime();
        this.finishNum = parentInfoVO.getFinishNum();
        this.createId = parentInfoVO.getCreateId();
    }

    public void convertQuestionRecordExportVOForExport(TbQuestionRecordExportVO questionRecordExportVO) {
        this.recordId = questionRecordExportVO.getId();
        this.taskName = questionRecordExportVO.getTaskName();
        this.taskDesc = questionRecordExportVO.getTaskDesc();
        this.photosAndVideos = questionRecordExportVO.getPhotosAndVideos();
        this.metaTableName = questionRecordExportVO.getMetaTableName();
        this.metaColumnName = questionRecordExportVO.getMetaColumnName();
        this.checkResultName = questionRecordExportVO.getCheckResultName();
        this.storeName = questionRecordExportVO.getStoreName();
        this.storeNum = questionRecordExportVO.getStoreNum();
        this.fullRegionName = questionRecordExportVO.getFullRegionName();
        this.handlerEndTime = questionRecordExportVO.getHandlerEndTime();
        this.status = questionRecordExportVO.getStatus();
        this.isOverdue = questionRecordExportVO.getIsOverdue();
        this.assignHandleUserName = questionRecordExportVO.getAssignHandleUserName();
        this.handleUserName = questionRecordExportVO.getHandleUserName();
        this.handleTime = questionRecordExportVO.getHandleTime();
        this.handleRemark = questionRecordExportVO.getHandleRemark();
        this.handlePhoto = questionRecordExportVO.getHandlePhoto();
        this.assignApproveUserName = questionRecordExportVO.getAssignApproveUserName();
        this.approveUserName = questionRecordExportVO.getApproveUserName();
        this.approveTime = questionRecordExportVO.getApproveTime();
        this.approveRemark = questionRecordExportVO.getApproveRemark();
        this.approvePhoto = questionRecordExportVO.getApprovePhoto();
        this.assignSecondApproveUserName = questionRecordExportVO.getAssignSecondApproveUserName();
        this.secondApproveUserName = questionRecordExportVO.getSecondApproveUserName();
        this.secondApproveTime = questionRecordExportVO.getSecondApproveTime();
        this.secondApproveRemark = questionRecordExportVO.getSecondApproveRemark();
        this.secondApprovePhoto = questionRecordExportVO.getSecondApprovePhoto();
        this.assignThirdApproveUserName = questionRecordExportVO.getAssignThirdApproveUserName();
        this.thirdApproveUserName = questionRecordExportVO.getThirdApproveUserName();
        this.thirdApproveTime = questionRecordExportVO.getThirdApproveTime();
        this.thirdApproveRemark = questionRecordExportVO.getThirdApproveRemark();
        this.thirdApprovePhoto = questionRecordExportVO.getThirdApprovePhoto();
        this.ccUserNames = questionRecordExportVO.getCcUserNames();
        this.completeTime = questionRecordExportVO.getCompleteTime();
        this.totalDuration = questionRecordExportVO.getTotalDuration();
        this.checkScore = questionRecordExportVO.getCheckScore();
        this.rewardPenaltMoney = questionRecordExportVO.getRewardPenaltMoney();
        this.totalDurationTime = questionRecordExportVO.getTotalDurationTime();
    }

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
