package com.coolcollege.intelligent.model.storework;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2022-09-08 02:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwStoreWorkDataTableDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("店务记录表tc_business_id")
    private String tcBusinessId;

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;

    @ApiModelProperty("店务日期 月2022-08-01 周2022-08-01 日2022-08-02")
    private Date storeWorkDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

    @ApiModelProperty("作业开始时间")
    private Date beginTime;

    @ApiModelProperty("作业结束时间")
    private Date endTime;

    @ApiModelProperty("检查表ID")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    private String tableName;

    @ApiModelProperty("开始执行时间")
    private Date beginHandleTime;

    @ApiModelProperty("完成执行时间 随项处理时间变化")
    private Date endHandleTime;

    @ApiModelProperty("完成状态 0:未完成  1:已完成")
    private Integer completeStatus;

    @ApiModelProperty("点评状态 0:未点评  1:已点评")
    private Integer commentStatus;

    @ApiModelProperty("实际处理人id")
    private String actualHandleUserId;

    @ApiModelProperty("实际点评人id")
    private String actualCommentUserId;

    @ApiModelProperty("点评时间")
    private Date commentTime;

    @ApiModelProperty("得分")
    private BigDecimal score;

    @ApiModelProperty("总分")
    private BigDecimal totalScore;

    @ApiModelProperty("合格作业数")
    private Integer passColumnNum;

    @ApiModelProperty("不合格作业数")
    private Integer failColumnNum;

    @ApiModelProperty("不适用作业数")
    private Integer inapplicableColumnNum;

    @ApiModelProperty("已完成作业数")
    private Integer finishColumnNum;

    @ApiModelProperty("采集项数")
    private Integer collectColumnNum;

    @ApiModelProperty("总作业数")
    private Integer totalColumnNum;

    @ApiModelProperty("处理人id 逗号隔开")
    private String handleUserIds;

    @ApiModelProperty("点评人id 逗号隔开")
    private String commentUserIds;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("分组排序")
    private Integer groupNum;

    @ApiModelProperty("检查结果")
    private String checkResultLevel;

    @ApiModelProperty("表属性")
    private Integer tableProperty;

    @ApiModelProperty("子工单数量")
    private Integer questionNum;

    @ApiModelProperty("待处理子工单数量")
    private Integer unHandleQuestionNum;

    @ApiModelProperty("待审批子工单数量")
    private Integer unApproveQuestionNum;

    @ApiModelProperty("已完成子工单数量")
    private Integer finishQuestionNum;

    @ApiModelProperty("检查表映射表id")
    private Long tableMappingId;

    @ApiModelProperty("逾期是否允许继续执行，0：否，1：是")
    private Integer overdueContinue;

    @ApiModelProperty("参与计算总项数")
    private Integer totalCalColumnNum;

    @ApiModelProperty("是否开启AI检查")
    private Integer isAiCheck;

    @ApiModelProperty("是否需要执行AI")
    private Integer isAiProcess;

    @ApiModelProperty("AI执行状态，0未执行 &1已点评 &2点评人已点评")
    private Integer aiStatus;
}