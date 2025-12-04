package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.safetycheck.vo.TbDataColumnCommentAppealVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/4/14 14:14
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDataStaTableColumnVO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 子任务ID
     */
    private Long subTaskId;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 区域ID
     */
    private Long regionId;

    /**
     * 区域路径新
     */
    private String regionWay;

    /**
     * 记录id
     */
    private Long businessId;

    /**
     * 记录类型
     */
    private String businessType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

    /**
     * 数据表的ID
     */
    private Long dataTableId;

    /**
     * 表ID
     */
    private Long metaTableId;

    /**
     * columnID
     */
    private Long metaColumnId;

    /**
     * 属性名称
     */
    private String metaColumnName;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 创建者
     */
    private String createUserId;

    /**
     * 巡店人
     */
    private String supervisorId;

    /**
     * 分类
     */
    private String categoryName;

    /**
     * 检查项结果:failed,unapplicable,pass
     */
    private String checkResult;

    /**
     * 检查项结果id
     */
    private Long checkResultId;

    /**
     * 检查项结果名称
     */
    private String checkResultName;

    /**
     * 检查项上传的图片
     */
    private String checkPics;

    /**
     * 检查项上传的视频
     */
    private String checkVideo;

    /**
     * 检查项的描述信息
     */
    private String checkText;

    /**
     * 检查项的分值
     */
    private BigDecimal checkScore;

    /**
     * 上报人ID
     */
    private String handlerUserId;

    /**
     * 审核人员
     */
    private String checkUserId;

    /**
     * 复核人员
     */
    private String reCheckUserId;

    /**
     * 问题任务状态
     */
    private String taskQuestionStatus;

    /**
     * 问题工单ID，没有写0
     */
    private Long taskQuestionId;

    /**
     * 检查项是否已经上报
     */
    private Integer submitStatus;

    /**
     * 业务记录状态
     */
    private Integer businessStatus;

    /**
     * 删除标记
     */
    private Integer deleted;

    /**
     * 创建日期
     */
    private String createDate;

    /**
     * 奖罚金额
     */
    private BigDecimal rewardPenaltMoney;

    /**
     * 门店场景名称
     */
    private String storeSceneName;

    /**
     * 门店场景id
     */
    private Long storeSceneId;

    /**
     * 巡店时间
     */
    private Date patrolStoreTime;

    /**
     * 得分倍数
     */
    private BigDecimal scoreTimes;

    /**
     * 奖罚倍数
     */
    private BigDecimal awardTimes;

    /**
     * 权重
     */
    private BigDecimal weightPercent;

    /**
     * 检查项总分 根据不适用配置计算得出
     */
    private BigDecimal columnMaxScore;

    /**
     * 上次该项检查结果
     */
    @ApiModelProperty("上次该项检查结果")
    private String lastTimeCheckResult;

    /**
     * 上次该项检查结果
     */
    @ApiModelProperty("上次该项检查不合格项id")
    private Long lastDataColumnId;

    /**
     * 不合格原因名称
     */
    @ApiModelProperty("不合格原因")
    private String checkResultReason;

    @ApiModelProperty("点评申诉信息")
    private TbDataColumnCommentAppealVO commentAppealVO;

    @ApiModelProperty("是否有检查历史")
    private Boolean hasCheckHistory;

    @ApiModelProperty("上报人姓名")
    private String handlerUserName;

    @ApiModelProperty("是否可以发起工单")
    private Boolean canSendProblem;

    @ApiModelProperty("是否AI检查")
    private Integer isAiCheck;

    private static final long serialVersionUID = 1L;
}