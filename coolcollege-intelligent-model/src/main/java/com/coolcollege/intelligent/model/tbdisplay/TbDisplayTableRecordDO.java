package com.coolcollege.intelligent.model.tbdisplay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
* @Description:
* @Author:
* @CreateDate: 2021-03-02 17:24:31
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTableRecordDO {
    /**
     * 主键id自增
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标识
     */
    private Boolean deleted;

    /**
     * 父任务id
     */
    private Long unifyTaskId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 循环轮次
     */
    private Long loopCount;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 处理人，创建的时候未空
     */
    private String handleUserId;

    /**
     * 处理人姓名，处理人，创建的时候未空
     */
    private String handleUserName;

    /**
     * 状态,handle,approve,recheck,complete
     */
    private String status;

    /**
     * 附件地址
     */
    private String attachUrl;

    /**
     * 是否支持单项评分 0 不支持  1 支持
     */
    private Boolean isSupportScore;

    /**
     * 是否支持拍照上传 0不支持  1支持 哈
     */
    private Boolean isSupportPhoto;

    /**
     * 整体得分
     */
    private BigDecimal score;

    /**
     * AI得分
     */
    private BigDecimal aiScore;

    /**
     * 整体评价
     */
    private String remark;

    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 审批链任务开始时间
     */
    private Date subBeginTime;
    /**
     * 审批链任务结束时间
     */
    private Date subEndTime;

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
     * 审批人id
     */
    private String approveUserId;

    /**
     * 审批人名称
     */
    private String approveUserName;

    /**
     * 复审人id
     */
    private String recheckUserId;

    /**
     * 复审人名称
     */
    private String recheckUserName;


    /**
     * 完成时间
     */
    private Date completeTime;


    /**
     * 删除人id
     */
    private String deleteUserId;

    /**
     * 删除人名称
     */
    private String deleteUserName;


    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 是否是ai检查
     */
    private Boolean isAiCheck;

    /**
     * 最新处理时间
     */
    private Date latestHandlerTime;

    /**
     * 最新审批时间
     */
    private Date latestApproveTime;
}