package com.coolcollege.intelligent.model.question.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.patrolstore.statistics.TenRegionExportDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 导出工单
 * @author zhangnan
 * @date 2021-12-29 9:08
 */
@Data
public class TbQuestionRecordExportVO extends TenRegionExportDTO {

    @Excel(name = "工单来源", orderNum = "0")
    private String taskType;

    @Excel(name = "创建类型", orderNum = "1")
    private String createType;

    @Excel(name = "工单名称", orderNum = "2")
    private String taskName;

    @Excel(name = "工单说明", orderNum = "3")
    private String taskDesc;

    @Excel(name = "工单图片/视频", orderNum = "4")
    private String photosAndVideos;

    @Excel(name = "检查表", orderNum = "5")
    private String metaTableName;

    @Excel(name = "检查项", orderNum = "6")
    private String metaColumnName;

    @Excel(name = "优先级", orderNum = "7")
    private String level;

    @Excel(name = "标准分", orderNum = "8")
    private BigDecimal supportScore;

    @Excel(name = "实际得分", orderNum = "9")
    private BigDecimal checkScore;

    @Excel(name = "实际奖惩", orderNum = "10")
    private String rewardPenaltMoney;

    private String checkResultName;

    @Excel(name = "门店名称", orderNum = "11")
    private String storeName;

    @Excel(name = "门店编号", orderNum = "12")
    private String storeNum;

    @Excel(name = "所属区域", orderNum = "13")
    private String fullRegionName;

    @Excel(name = "工单截至日期", orderNum = "14", format = "yyyy.MM.dd")
    private Date handlerEndDate;

    @Excel(name = "工单截至时间", orderNum = "15", format = "HH:mm")
    private Date handlerEndTime;

    @Excel(name = "工单状态", orderNum = "16")
    private String status;

    @Excel(name = "是否逾期", orderNum = "17")
    private String isOverdue;

    @Excel(name = "实际处理人", orderNum = "18")
    private String handleUserName;

    @Excel(name = "处理状态（最新）", orderNum = "19")
    private String handleActionKey;

    @Excel(name = "处理人提交时间", orderNum = "20", format = "yyyy.MM.dd HH:mm")
    private Date handleTime;

    @Excel(name = "处理人提交备注", orderNum = "21")
    private String handleRemark;

    @Excel(name = "处理人提交图片/视频", orderNum = "22")
    private String handlePhoto;

    @Excel(name = "一级审批人", orderNum = "23")
    private String approveUserName;

    @Excel(name = "一级审批状态（最新）", orderNum = "24")
    private String approveActionKey;

    @Excel(name = "一级审批人提交时间", orderNum = "25", format = "yyyy.MM.dd HH:mm")
    private Date approveTime;

    @Excel(name = "一级审批人提交备注", orderNum = "26")
    private String approveRemark;

    @Excel(name = "一级审批人提交图片/视频", orderNum = "27")
    private String approvePhoto;

    @Excel(name = "二级审批人", orderNum = "28")
    private String secondApproveUserName;

    @Excel(name = "二级审批状态（最新）", orderNum = "29")
    private String secondApproveActionKey;

    @Excel(name = "二级审批人提交时间", orderNum = "30", format = "yyyy.MM.dd HH:mm")
    private Date secondApproveTime;

    @Excel(name = "二级审批人提交备注", orderNum = "31")
    private String secondApproveRemark;

    @Excel(name = "二级审批人提交图片/视频", orderNum = "32")
    private String secondApprovePhoto;

    @Excel(name = "三级审批人", orderNum = "33")
    private String thirdApproveUserName;

    @Excel(name = "三级审批状态（最新）", orderNum = "34")
    private String thirdApproveActionKey;

    @Excel(name = "三级审批人提交时间", orderNum = "35", format = "yyyy.MM.dd HH:mm")
    private Date thirdApproveTime;

    @Excel(name = "三级审批人提交备注", orderNum = "36")
    private String thirdApproveRemark;

    @Excel(name = "三级审批人提交图片/视频", orderNum = "37")
    private String thirdApprovePhoto;

    @Excel(name = "抄送人", orderNum = "38")
    private String ccUserNames;

    @Excel(name = "创建人姓名", orderNum = "39")
    private String createUserName;

    @Excel(name = "创建日期", orderNum = "40", format = "yyyy.MM.dd")
    private Date createDate;

    @Excel(name = "创建时间", orderNum = "41", format = "HH:mm")
    private Date createTime;

    @Excel(name = "工单完成时间", orderNum = "42", format = "yyyy.MM.dd HH:mm")
    private Date completeTime;

    @Excel(name = "工单总时长", orderNum = "43")
    private String totalDuration;

    private Long totalDurationTime;

    private Long parentQuestionId;

    private Long id;

    private String assignHandleUserName;

    private String assignApproveUserName;

    private String assignSecondApproveUserName;

    private String assignThirdApproveUserName;
}
