package com.coolcollege.intelligent.model.tbdisplay.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataContentDO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author wxp
 * @date 2021-3-12 20:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTaskDataVO {

    private Long id;

    private Long unifyTaskId;

    private String regionId;

    /**
     * 任务类型
     */
    private String taskType;

    private String storeArea;
    /**
     * 门店区域
     */
    @Excel(name = "门店区域")
    private String storeAreaName;

    private String regionPath;

    /**
     * 门店分组名称
     */
    private String storeGroupName;
    // 门店id
    private String storeId;

    private Long loopCount;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称")
    private String storeName;

    private Long metaTableId;

    /**
     * 检查表
     */
    @Excel(name = "检查表")
    private String tableName;

    /**
     * 总检查表项数
     */
    @Excel(name = "总检查表项数")
    private Integer metaColumnNum;

    /**
     * 处理人
     */
    @Excel(name = "处理人")
    private String handleUserName;

    @Excel(name = "最新处理时间", exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date latestHandlerTime;

    /**
     * 审批人
     */
    @Excel(name = "审批人")
    private String approveUserName;

    @Excel(name = "最新审批时间", exportFormat = "yyyy-MM-dd HH:mm:ss")
    private Date latestApproveTime;

    /**
     * 复审人
     */
    @Excel(name = "复审人")
    private String recheckUserName;

    /**
     * 门店得分
     */
    @Excel(name = "门店得分")
    private BigDecimal score;

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

    @Excel(name = "任务状态")
    private String taskStatus;

    /**
     * 开始时间
     */
    private Long beginTime;
    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 检查项图片list
     */
    private List<String> picList;

    /**
            * 处理截止时间
     */
    private Date handlerEndTime;

    /**
     * 首次处理时间
     */
    private Date firstHandlerTime;

    /**
     * 首次审批时间
     */
    private Date firstApproveTime;

    /**
     * 检查项列表
     */
    private List<TbDisplayTableDataColumnDO> dataColumnList;

    /**
     * 检查内容列表
     */
    private List<TbDisplayTableDataContentDO> dataContentList;

    /**
     * 处理时长
     */
    private String handlerDuration;

    /**
     * 审批时长
     */
    private String approveDuration;

    /**
     * 处理逾期
     */
    private String handlerOverdue;

    /**
     * 是否过期完成
     */
    private String completeOverdue;

    /**
     * 是否支持单项评分 0 不支持  1 支持
     */
    private Boolean isSupportScore;

}
