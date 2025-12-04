package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.coolrelation.dto.CoolCourseDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaQuickColumnResultDTO;
import com.coolcollege.intelligent.model.metatable.request.TbMetaColumnReasonRequest;
import com.coolcollege.intelligent.model.metatable.request.TbMetaQuickColumnAppealRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@ApiModel
@Data
public class QuickTableColumnRequest {
    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private String category;

    /**
     * 检查项名称
     */
    @NotNull(message = "检查项名称不能为空")
    @ApiModelProperty("检查项名称")
    private String columnName;

    /**
     * 描述信息
     */
    @ApiModelProperty("描述信息")
    private String description;

    /**
     * 工单处理人类型: person,position
     */
    @ApiModelProperty("工单处理人类型: person,position")
    private String questionHandlerType;

    /**
     * 工单处理人/岗位id
     */
    @ApiModelProperty("工单处理人/岗位id")
    private String questionHandlerId;

    /**
     * 工单复检人类型:person,position
     */
    @ApiModelProperty("工单复检人类型:person,position")
    private String questionRecheckerType;


    /**
     * 工单复检人/岗位id
     */
    @ApiModelProperty("工单复检人/岗位id")
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
    @ApiModelProperty("工单抄送人")
    private String questionCcId;

    /**
     * 工审批人列表
     */
    @ApiModelProperty("工审批人列表")
    private String questionApproveUser;

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
     * 标准图
     */
    @ApiModelProperty("标准图")
    private String standardPic;


    /**
     * 检查项id列表
     */
    private List<Long> columnIdList;

    /**
     * 检查项id
     */
    @ApiModelProperty("检查项id")
    private Long id;

    /**
     * SOP文档id
     */
    @ApiModelProperty("SOP文档id")
    private Long sopId;

    /**
     * 酷学院课程信息
     */
    @ApiModelProperty("酷学院课程信息")
    private CoolCourseDTO coolCourse;

    /**
     * 酷学院课程信息
     */
    @ApiModelProperty("免费课程信息")
    private CoolCourseDTO freeCourse;

    /**
     * 门店场景id
     */
    @ApiModelProperty("门店场景id")
    private Long storeSceneId;

    @ApiModelProperty("分类id")
    private Long categoryId;

    @ApiModelProperty("是否允许用户自定义评分")
    private Integer userDefinedScore;

    @ApiModelProperty("0:不需要结果项和打分, 1:仅需要结果项，2:仅需要打分")
    public Integer configType;

    @ApiModelProperty("打分最低值-采集项")
    public BigDecimal minScore;

    @ApiModelProperty("打分最高值-采集项")
    public BigDecimal maxScore;

    @ApiModelProperty("0:普通项，1:高级项，2:红线项，3:否决项，4:加倍项，5:采集项，6:ai项")
    public Integer columnType;

    @ApiModelProperty("结果项")
    private List<TbMetaQuickColumnResultDTO> columnResultList;

    @ApiModelProperty("原始选取的使用人[{type:person,value:}(人),{type:position,value:}(职位),{type:userGroup,value:}(分组),{type:organization,value:}(组织架构)]")
    private String usePersonInfo;

    @ApiModelProperty("使用人范围：self-仅自己，all-全部人员，part-部分人员")
    private String useRange;

    @ApiModelProperty("共同编辑人userId集合")
    private List<String> commonEditUserIdList;

    @ApiModelProperty("不合格原因列表")
    private List<TbMetaColumnReasonRequest> columnReasonList;

    @ApiModelProperty("采集项是否需要拍照 0:否 1:是")
    private Integer mustPic;

    @ApiModelProperty("申诉快捷项列表")
    private List<TbMetaQuickColumnAppealRequest> columnAppealList;

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
}
