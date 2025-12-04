package com.coolcollege.intelligent.model.metatable.vo;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

@ApiModel
@Data
public class TbMetaStaColumnVO {
    /**
     * ID
     */
    @ApiModelProperty("检查项id")
    private Long id;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private String category;

    /**
     * 检查表ID
     */
    @ApiModelProperty("检查表ID")
    private Long metaTableId;

    /**
     * 检查项名称
     */
    @ApiModelProperty("检查项名称")
    private String columnName;

    /**
     *  描述信息
     */
    @ApiModelProperty("描述信息")
    private String description;

    /**
     * 工单处理人
     */
    @ApiModelProperty("工单处理人")
    private String questionHandlerName;

    /**
     * 工单处理人类型
     */
    @ApiModelProperty("工单处理人类型")
    private String questionHandlerType;

    /**
     * 工单处理人id
     */
    @ApiModelProperty("工单处理人id")
    private String questionHandlerId;

    /**
     * 工单复检人
     */
    @ApiModelProperty("工单复检人")
    private String questionRecheckerName;

    /**
     * 工单复检人类型
     */
    @ApiModelProperty("工单复检人类型")
    private String questionRecheckerType;

    /**
     * 工单复检人id
     */
    @ApiModelProperty("工单复检人id")
    private String questionRecheckerId;

    @ApiModelProperty("工单发起人复审 0 不可以 1 可以")
    private Boolean createUserApprove;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String createUser;
    /**
     * 重要程度等级
     */
    @ApiModelProperty("重要程度等级")
    private String level;

    /**
     * 分值满分
     */
    @ApiModelProperty("分值满分")
    private BigDecimal supportScore;

    /**
     * 最低分值
     */
    @ApiModelProperty("最低分值")
    private BigDecimal lowestScore;

    /**
     * 处罚金额
     */
    @ApiModelProperty("处罚金额")
    private BigDecimal punishMoney;

    /**
     * 奖励金额
     */
    @ApiModelProperty("奖励金额")
    private BigDecimal awardMoney;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer orderNum;

    /**
     * 标准图
     */
    @ApiModelProperty("标准图")
    private String standardPic;


    /**
     * SOP文档id
     */
    @ApiModelProperty("SOP文档id")
    private Long sopId;

    /**
     * SOP文档名称
     */
    @ApiModelProperty("SOP文档名称")
    private String sopName;

    /**
     * 酷学院课程信息
     */
    @ApiModelProperty("酷学院课程信息")
    private String coolCourse;

    /**
     * 免费课程信息
     */
    @ApiModelProperty("免费课程信息")
    private String freeCourse;

    @ApiModelProperty("审批人列表")
    private String questionApproveUser;


    /**
     * 权重百分比
     */
    private BigDecimal weightPercent;

    /**
     * 是否允许用户自定义评分
     */
    private Integer userDefinedScore;

    /**
     * 0:不需要结果项和打分, 1:仅需要结果项，2:仅需要打分
     */
    private Integer configType;

    /**
     * 项类型 0:普通项，1:高级项，2:红线项，3:否决项，4:加倍项，5:采集项，6:ai项
     */
    private Integer columnType;

    /**
     * 快速检查表ID
     */
    private Long quickColumnId;

    /**
     * 冻结状态 false 冻结  true 解冻
     */
    private Boolean status;
    /**
     * 是否删除
     */
//    private Boolean deleted;

    private static final long serialVersionUID = 1L;
}
