package com.coolcollege.intelligent.model.tbdisplay.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author wxp
 * @date 2021-3-10 20:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayReportVO {
    /**
     * 子任务id
     */
    private Long subTaskId;

    /**
     * 门店区域
     */
    @Excel(name = "门店区域")
    private String storeAreaName;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    private String storeName;

    /**
     * 检查表
     */
    @Excel(name = "检查表")
    private String templateName;

    /**
     * 总检查表项数
     */
    @Excel(name = "总检查表项数")
    private Long templateCheckItemNum;

    /**
     * 不适用项数
     */
    @Excel(name = "不适用项数")
    private Long uselessCheckItemNum;

    /**
     * 处理人
     */
    @Excel(name = "处理人")
    private String handleUserName;

    /**
     * 审批人
     */
    @Excel(name = "审批人")
    private String approveUserName;

    /**
     * 复审人
     */
    @Excel(name = "复审人")
    private String recheckUserName;

    /**
     * 门店得分
     */
    @Excel(name = "门店得分")
    private Integer score;

    /**
     * 门店评价
     */
    @Excel(name = "门店评价")
    private String remark;

    /**
     * 是否过期完成
     */
    @Excel(name = "是否过期完成")
    private String overdue;

    /**
     * 结束时间
     */
    @Excel(name = "结束时间", exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date doneTime;
    /**
     * 检查时长
     */
    @Excel(name = "检查时长")
    private String checkTime;
    /**
     * 任务名称
     */
    @Excel(name = "任务名称")
    private String taskName;
    /**
     * 有效期
     */
    @Excel(name = "有效期")
    private String validTime;
    /**
     * 创建人
     */
    @Excel(name = "创建人")
    private String createUserName;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 任务说明
     */
    @Excel(name = "任务说明")
    private String taskDesc;
    /**
     * 流程状态
     */
    @Excel(name = "流程状态")
    private String status;

    /**
     * 检查项图片list
     */
    private List<String> picList;
}
