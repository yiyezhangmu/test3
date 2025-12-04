package com.coolcollege.intelligent.model.question.vo;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 问题工单详情vo
 * @author zhangnan
 * @date 2021-12-23 14:11
 */
@ApiModel(value = "问题工单详情")
@Data
public class TbQuestionRecordDetailVO {

    @ApiModelProperty("主键id自增")
    private Long id;

    @ApiModelProperty("任务名称（工单名称）")
    private String taskName;

    @ApiModelProperty("工单信息")
    private String taskInfo;

    @ApiModelProperty("检查表检查项ID")
    private Long metaColumnId;

    @ApiModelProperty("检查表检查项名称")
    private String metaColumnName;

    @ApiModelProperty("检查项等级，redline：红线项")
    private String metaColumnLevel;

    @ApiModelProperty("检查项描述")
    private String metaColumnDescription;

    @ApiModelProperty("标准分")
    private BigDecimal supportScore;

    @ApiModelProperty("实际得分")
    private BigDecimal checkScore;

    @ApiModelProperty("实际奖惩")
    private BigDecimal rewardPenaltMoney;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

    @ApiModelProperty("所属区域")
    private String regionName;

    @ApiModelProperty("工单截至时间，前端根据此字段自行拆分日期和时间")
    private Date handlerEndTime;

    @ApiModelProperty("工单状态1 : 待处理 （2、3、4):待审核 endNode:已完成")
    private String status;

    @ApiModelProperty(value = "是否逾期")
    private Boolean isOverdue;

    @ApiModelProperty("处理人id")
    private String handleUserId;

    @ApiModelProperty("处理人姓名，处理人，创建的时候未空")
    private String handleUserName;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人姓名")
    private String createUserName;

    @ApiModelProperty("创建时间，前端根据此字段自行拆分日期和时间")
    private Date createTime;

    @ApiModelProperty("工单说明")
    private String taskDesc;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("创建类型 1 、手动  2、自动")
    private Integer createType;

    @ApiModelProperty("数据项ID 大于0: 不合格项工单 等于0:自主工单")
    private Long dataColumnId;

    @ApiModelProperty("培训内容高级设置：先学习后处理工单（1）、边学边处理（0）")
    private Boolean learnFirst;

    @ApiModelProperty("审批人id")
    private String approveUserId;

    @ApiModelProperty("审批人名称")
    private String approveUserName;

    @ApiModelProperty("抄送人")
    private List<PersonDTO> ccUsers;

    @ApiModelProperty("待处理-处理人列表")
    private List<PersonDTO> handleUsers;

    @ApiModelProperty("待处理，待审批-审批人列表")
    private List<PersonDTO> approveUsers;

    @ApiModelProperty("二级审批人列表")
    private List<PersonDTO> secondApproveUsers;

    @ApiModelProperty("三级审批人列表")
    private List<PersonDTO> thirdApproveUsers;

    @ApiModelProperty("附件地址")
    private String attachUrl;

    @ApiModelProperty("是当前节点处理人")
    private Boolean isHandleUser;

    @ApiModelProperty("门店任务id，用作重新分配")
    private Long taskStoreId;

    @ApiModelProperty("二级实际审批人id")
    private String secondApproveUserId;

    @ApiModelProperty("二级实际审批人名称")
    private String secondApproveUserName;

    @ApiModelProperty("三级实际审批人id")
    private String thirdApproveUserId;

    @ApiModelProperty("三级实际审批人名称")
    private String thirdApproveUserName;

    @ApiModelProperty("AI工单 AI、普通工单 common、巡店工单 patrolStore")
    private String questionType;

    @ApiModelProperty("轮次")
    private Long loopCount;

    @ApiModelProperty("检查项结果:PASS,FAIL,INAPPLICABLE")
    private String checkResult;

    @ApiModelProperty("检查项结果名称")
    private String checkResultName;

    @ApiModelProperty("标准图")
    private String standardPic;

    @ApiModelProperty("检查项类型:0:普通项，1:高级项，2:红线项，3:否决项，4:加倍项，5:采集项，6:ai项")
    private Integer columnType;

    @ApiModelProperty("父工单ID")
    private Long parentQuestionId;

    @ApiModelProperty("检查表id")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    private String metaTableName;

    @ApiModelProperty("不合格原因")
    private String checkResultReason;

    @ApiModelProperty("是否需要ai检查")
    private Integer isAiCheck;
}
