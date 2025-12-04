package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnAppealDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnReasonDTO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaQuickColumnResultVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ApiModel
@Data
public class QuickTableColumnVO {

    /**
     * ID
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    private Date editTime;

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
     * 检查表 工单处理人(岗位)id 逗号隔开
     */
    @ApiModelProperty("工单处理人(岗位)名称")
    private String questionHandlerName;

    /**
     * 检查表 工单处理人(岗位)id 逗号隔开
     */
    @ApiModelProperty("单处理人(岗位)id 逗号隔开")
    private String questionHandlerId;

    /**
     * 检查表 工单处理人(岗位)id 逗号隔开
     */
    @ApiModelProperty("工单处理人类型 类型   person:人员   position:岗位")
    private String questionHandlerType;

    /**
     * 检查表 工单复检人(岗位)名称 逗号隔开
     */
    @ApiModelProperty("工单复检人(岗位)名称 逗号隔开")
    private String questionRecheckerName;
    /**
     * 检查表 工单复检人(岗位)id 逗号隔开
     */
    @ApiModelProperty("工单复检人(岗位)id 逗号隔开")
    private String questionRecheckerId;

    @ApiModelProperty("工单发起人复审 0 不可以 1 可以")
    private Boolean createUserApprove;
    /**
     * 检查表 工单复检人(岗位)类型 逗号隔开
     */
    @ApiModelProperty("工单复检人类型  person:人员   position:岗位")
    private String questionRecheckerType;

    /**
     * 工单抄送人类型   1:人员   2:岗位
     */
    private String questionCcType;

    /**
     * 抄送人（岗位） 名称
     */
    private String questionCcName;

    /**
     * 工单抄送人(岗位)id 逗号隔开
     */
    @ApiModelProperty("工单抄送人")
    private String questionCcId;

    /**
     * 检查表 是否支持分值, -1表示不支持，正表示满分值
     */
    @ApiModelProperty("是否支持分值, -1表示不支持，正表示满分值")
    private Long supportScore;

    /**
     * 检查表 处罚金额
     */
    @ApiModelProperty("处罚金额")
    private BigDecimal punishMoney;

    /**
     * 检查表 奖励金额
     */
    @ApiModelProperty("奖励金额")
    private BigDecimal awardMoney;



    /**
     * 检查表 标准图
     */
    @ApiModelProperty("标准图")
    private String standardPic;

    /**
     * 创建人姓名
     */
    @ApiModelProperty("创建人姓名")
    private String createUserName;

    /**
     * 更新人姓名
     */
    private String updateUserName;

    /**
     * 抄送人列表
     */
    @ApiModelProperty("抄送人列表")
    List<PersonPositionDTO> ccPeopleList;

    /**
     * 审批人列表
     */
    @ApiModelProperty("审批人列表")
    private String questionApproveUser;

    /**
     * SOP文档信息
     */
    private TaskSopVO taskSopVO;

    /**
     * 酷学院课程信息
     */
    private CoolCourseVO coolCourse;

    /**
     * 免费课程信息
     */
    private CoolCourseVO freeCourse;

    /**
     * 门店场景id
     */
    private Long storeSceneId;

    /**
     * 门店场景是否删除
     */
    private Boolean storeSceneIsDelete;

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

    @ApiModelProperty("采集项 是否必须拍照 0:否 1:是")
    public Integer mustPic;

    @ApiModelProperty("0:普通项，1:高级项，2:红线项，3:否决项，4:加倍项，5:采集项，6:ai项")
    public Integer columnType;

    @ApiModelProperty("结果项")
    private List<TbMetaQuickColumnResultVO> columnResultList;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("场景名称")
    private String storeSceneName;

    @ApiModelProperty("原始选取的使用人[{type:person,value:}{type:position,value:}]")
    private String usePersonInfo;

    @ApiModelProperty("使用人范围：self-仅自己，all-全部人员，part-部分人员")
    private String useRange;


    /**
     *
     */
    @ApiModelProperty("共同编辑使用人userIds")
    private String commonEditUserids;

    @ApiModelProperty("共同编辑使用人列表")
    private List<PersonDTO> commonEditUserList;

    @ApiModelProperty("编辑人")
    private Boolean editFlag;

    @ApiModelProperty("创建人")
    private String createUser;


    @ApiModelProperty("不合格检查原因列表")
    private List<TbQuickColumnReasonDTO> columnReasonList;


    @ApiModelProperty("申诉原因列表")
    private List<TbQuickColumnAppealDTO> columnAppealList;

    /**
     * 是否AI检查
     */
    private Integer isAiCheck;

    /**
     * ai标准描述
     */
    private String aiCheckStdDesc;

    @ApiModelProperty("检查描述是否必填")
    private Boolean descRequired;

    @ApiModelProperty("自动工单有效期（时）")
    private Integer autoQuestionTaskValidity;

    @ApiModelProperty("是否设置工单有效期")
    private Boolean isSetAutoQuestionTaskValidity;

    @ApiModelProperty("AI模型")
    private String aiModel;

    @ApiModelProperty("AI模型名称")
    private String aiModelName;

    @ApiModelProperty("强制检查图片上传数量-最小值")
    public Integer minCheckPicNum;

    @ApiModelProperty("强制检查图片上传数量-最大值")
    public Integer maxCheckPicNum;
}
