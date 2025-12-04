package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnAppealDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnReasonDTO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaQuickColumnResultVO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class QuickTableColumnListVO {
    private Long id;

    private String columnName;

    private String categoryName;

    private Date createTime;

    private String createUser;

    private String createUserId;

    private String description;


    /**
     * 检查表 工单处理人(岗位)id 逗号隔开
     */
    private String questionHandlerName;

    /**
     * 检查表 工单处理人(岗位)id 逗号隔开
     */
    private String questionHandlerId;

    /**
     * 检查表 工单处理人(岗位)id 逗号隔开
     */
    private String questionHandlerType;

    /**
     * 检查表 工单复检人(岗位)名称 逗号隔开
     */
    private String questionRecheckerName;
    /**
     * 检查表 工单复检人(岗位)id 逗号隔开
     */
    private String questionRecheckerId;
    /**
     * 检查表 工单复检人(岗位)类型 逗号隔开
     */
    private String questionRecheckerType;

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
     * 工单抄送人(岗位)名称 顿号（、）隔开
     */
    private String questionCcName;

    /**
     * 检查表 是否支持分值, -1表示不支持，正表示满分值
     */
    private Long supportScore;

    /**
     * 检查表 处罚金额
     */
    private BigDecimal punishMoney;

    /**
     * 检查表 奖励金额
     */
    private BigDecimal awardMoney;

    /**
     * 检查表 标准图
     */
    private String standardPic;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人姓名
     */
    private String updateUserName;

    /**
     * 更新人id
     */
    private String updateUserId;


    /**
     * 抄送人列表
     */
    List<PersonPositionDTO> ccPeopleList;

    /**
     * SOP文档id
     */
    private Long sopId;

    /**
     * 酷学院课程信息
     */
    private CoolCourseVO coolCourseVO;

    /**
     * 免费课程信息
     */
    private CoolCourseVO freeCourseVO;

    private TaskSopVO taskSopVO;

    private Long storeSceneId;

    private Boolean storeSceneIsDelete;

    /**
     * 工审批人列表
     */
    private String questionApproveUser;

    private BigDecimal threshold;

    private String aiType;
    private String columnTypeName;
    private Integer columnType;
    private Long categoryId;
    private BigDecimal maxScore;
    private BigDecimal minScore;
    private Integer status;
    private Integer userDefinedScore;
    private Integer configType;
    private String storeSceneName;


    private Integer mustPic;

    @ApiModelProperty("结果项")
    private List<TbMetaQuickColumnResultVO> columnResultList;

    @ApiModelProperty("是否可以編輯")
    private Boolean editFlag;

    @ApiModelProperty("共同编辑人集合")
    private List<PersonDTO> commonEditUserList;

    @ApiModelProperty("原始选取的使用人[{type:person,value:}{type:position,value:}]")
    private String usePersonInfo;

    @ApiModelProperty("使用人范围：self-仅自己，all-全部人员，part-部分人员")
    private String useRange;


    @ApiModelProperty("不合格检查原因列表")
    private List<TbQuickColumnReasonDTO> columnReasonList;

    @ApiModelProperty("申诉快捷项列表")
    private List<TbQuickColumnAppealDTO> columnAppealList;

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

    @ApiModelProperty("AI模型名称")
    private String aiModelName;

    @ApiModelProperty("强制检查图片上传数量-最小值")
    public Integer minCheckPicNum;

    @ApiModelProperty("强制检查图片上传数量-最大值")
    public Integer maxCheckPicNum;
}
