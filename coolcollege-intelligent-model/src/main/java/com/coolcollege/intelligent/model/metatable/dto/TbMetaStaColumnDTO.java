package com.coolcollege.intelligent.model.metatable.dto;

import com.coolcollege.intelligent.model.coolrelation.dto.CoolCourseDTO;
import com.coolcollege.intelligent.model.safetycheck.dto.TbMetaColumnAppealDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@ApiModel
@Data
public class TbMetaStaColumnDTO {

    /**
     * ID
     */
    private Long id;



    /**
     * 分类
     */
    private String categoryName;

    /**
     * 检查表ID
     */
    private Long metaTableId;

    /**
     * 检查项名称
     */
    private String columnName;

    /**
     *  描述信息
     */
    private String description;

    /**
     * 工单处理人类型
     */
    private String questionHandlerType;
    /**
     * 工单处理人id
     */
    private String questionHandlerId;

    /**
     * 工单复检人类型
     */
    private String questionRecheckerType;
    /**
     * 工单复检人Id
     */
    private String questionRecheckerId;

    @ApiModelProperty("工单发起人复审 0 不可以 1 可以")
    private Boolean createUserApprove;

    /**
     * 工单抄送人类型   1:人员   2:岗位
     */
    private String questionCcType;

    /**
     * 工单抄送人(岗位)id 逗号隔开
     */
    private String questionCcId;

    /**
     * 工单审批人列表
     */
    @ApiModelProperty("工单审批人列表")
    private String questionApproveUser;

    /**
     * 重要程度等级
     */
    private String level;

    /**
     * 分值满分
     */
    private BigDecimal supportScore;

    /**
     * 最低分值
     */
    private BigDecimal lowestScore;

    /**
     * 处罚金额
     */
    private BigDecimal punishMoney;

    /**
     * 奖励金额
     */
    private BigDecimal awardMoney;

    /**
     * 排序  前段传参 后端排序返回
     */
    private Integer orderNum;

    /**
     * 标准图
     */
    private String standardPic;

    /**
     * 结果项
     */
    private List<TbMetaColumnResultDTO> columnResultDTOList;

    /**
     * SOP文档id
     */
    private Long sopId;

    /**
     * 酷学院课程信息
     */
    private CoolCourseDTO coolCourse;

    /**
     * 免费课程信息
     */
    private CoolCourseDTO freeCourse;

    /**
     * 门店场景id
     */
    private Long storeSceneId;

    private BigDecimal threshold;

    /**
     * AI算法类型
     */
    private String aiType;

    /**
     * 权重百分比
     */
    @ApiModelProperty("权重百分比")
    private BigDecimal weightPercent;

    /**
     * 是否允许用户自定义评分
     */
    @ApiModelProperty("是否允许用户自定义评分")
    private Integer userDefinedScore;

    /**
     * 0:不需要结果项和打分, 1:仅需要结果项，2:仅需要打分
     */
    @ApiModelProperty("0:不需要结果项和打分, 1:仅需要结果项，2:仅需要打分")
    private Integer configType;

    /**
     * 快速检查项ID
     */
    @ApiModelProperty("快速检查项ID")
    private Long quickColumnId;

    /**
     * 冻结状态 false 解冻  true 冻结
     */
    @ApiModelProperty("冻结状态 false 解冻  true 冻结")
    private Boolean status = Boolean.FALSE;

    /**
     * 项类型 0:普通项，1:高级项，2:红线项，3:否决项，4:加倍项，5:采集项，6:ai项
     */
    @ApiModelProperty("项类型 0:普通项，1:高级项，2:红线项，3:否决项，4:加倍项，5:采集项，6:ai项")
    private Integer columnType;

    /**
     * 项最大分值
     */
    private BigDecimal maxScore;

    /**
     * 项最小分值
     */
    private BigDecimal minScore;

    @ApiModelProperty("不合格项原因列表")
    List<TbMetaColumnReasonDTO> columnReasonList;


    @ApiModelProperty("申诉快捷項")
    List<TbMetaColumnAppealDTO> columnAppealList;

    /**
     * 是否可以修改
     */
    @ApiModelProperty("是否可以修改 1:可以 0:不可以")
    private Boolean canModify;

    /**
     * 是否强制拍照
     */
    @ApiModelProperty("是否强制拍照 0:否 1:是")
    private Integer mustPic;

    @ApiModelProperty("是否AI检查")
    private Integer isAiCheck;

    @ApiModelProperty("ai标准描述")
    private String aiCheckStdDesc;

    @ApiModelProperty("检查描述是否必填")
    private Boolean descRequired;

    @ApiModelProperty("自动工单有效期（时）")
    private Integer autoQuestionTaskValidity;

    @ApiModelProperty("是否设置工单有效期")
    private Boolean isSetAutoQuestionTaskValidity;

    @ApiModelProperty("AI模型")
    private String aiModel;

    @ApiModelProperty("强制检查图片上传数量-最小值")
    public Integer minCheckPicNum;

    @ApiModelProperty("强制检查图片上传数量-最大值")
    public Integer maxCheckPicNum;

    private static final long serialVersionUID = 1L;
}
