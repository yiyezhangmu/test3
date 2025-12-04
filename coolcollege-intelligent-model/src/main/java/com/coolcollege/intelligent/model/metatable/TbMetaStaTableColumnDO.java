package com.coolcollege.intelligent.model.metatable;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author
 * 标准检查项的表
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaStaTableColumnDO implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

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
     * 工单处理人类型   1:人员   2:岗位
     */
    private String questionHandlerType;

    /**
     * 工单处理人(岗位)id 逗号隔开
     */
    private String questionHandlerId;

    /**
     * 工单复检人类型   1:人员   2:岗位
     */
    private String questionRecheckerType;

    /**
     * 工单复检人(岗位)id 逗号隔开
     */
    private String questionRecheckerId;

    /**
     * 工单抄送人类型   1:人员   2:岗位
     */
    private String questionCcType;

    /**
     * 工单抄送人(岗位)id 逗号隔开
     */
    private String questionCcId;

    /**
     * 工单抄送人(岗位)id 逗号隔开
     */
    private String questionCcName;
    /**
     * 创建者
     */
    private String createUserId;

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
     * 排序
     */
    private Integer orderNum;

    /**
     * 标准图
     */
    private String standardPic;

    /**
     * 是否删除
     */
    private Boolean deleted;
    /**
     *更新人id
     */
    private String editUserId;

    private String editUserName;

    private String createUserName;

    // 其他非数据库数据
    private String questionHandlerName;
    private String questionRecheckerName;

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
    private String coolCourse;

    /**
     * 免费课程信息
     */
    private String freeCourse;

    /**
     * 门店场景id
     */
    private Long storeSceneId;
    /**
     * 门店场景名称
     */
    private String storeSceneName;
    /**
     * 门店场景是否删除
     */
    private Boolean storeSceneIsDelete;


    /**
     * 工单审批人列表
     */
    @ApiModelProperty("工单审批人列表")
    private String questionApproveUser;

    /**
     * ai分析阈值
     */
    private BigDecimal threshold;

    /**
     * 算法类型 帽子：hat，口罩：mask
     */
    private String aiType;

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
     * 执行要求,值逗号隔开[true,true,true,true]，分别表示(拍照片 拍视频 录语音 写说明)
     */
    private String executeDemand;

    /**
     * 是否可以修改
     */
    @ApiModelProperty("是否可以修改 1:可以 0:不可以")
    private Boolean canModify;

    /**
     * 是否必须拍照 0否 1是
     */
    private Integer mustPic;

    @ApiModelProperty("工单发起人复审 0 不可以 1 可以")
    private Boolean createUserApprove;

    /**
     * 是否AI检查
     */
    private Integer isAiCheck;

    /**
     * ai标准描述
     */
    private String aiCheckStdDesc;

    /**
     * 扩展信息
     */
    private String extendInfo;


    private static final long serialVersionUID = 1L;

    public TbMetaStaTableColumnDO(Date editTime, String categoryName, Long metaTableId, String columnName,
        String description, String questionHandlerType, String questionHandlerId, String questionRecheckerType,
        String questionRecheckerId, String questionCcType, String questionCcId, String createUserId, String level,
        BigDecimal supportScore, BigDecimal lowestScore, BigDecimal punishMoney, BigDecimal awardMoney, Integer orderNum,
        String standardPic, Boolean deleted, String createUserName, String editUserId, String editUserName) {
        this.editTime = editTime;
        this.categoryName = categoryName;
        this.metaTableId = metaTableId;
        this.columnName = columnName;
        this.description = description;
        this.questionHandlerType = questionHandlerType;
        this.questionHandlerId = questionHandlerId;
        this.questionRecheckerType = questionRecheckerType;
        this.questionRecheckerId = questionRecheckerId;
        this.questionCcType = questionCcType;
        this.questionCcId = questionCcId;
        this.createUserId = createUserId;
        this.level = level;
        this.supportScore = supportScore;
        this.lowestScore = lowestScore;
        this.punishMoney = punishMoney;
        this.awardMoney = awardMoney;
        this.orderNum = orderNum;
        this.standardPic = standardPic;
        this.deleted = deleted;
        this.createUserName = createUserName;
        this.editUserId = editUserId;
        this.editUserName = editUserName;
    }

    public String getAiModel() {
        JSONObject extendInfo = JSONObject.parseObject(this.extendInfo);
        if (Objects.nonNull(extendInfo)) {
            return extendInfo.getString(Constants.STORE_WORK_AI.AI_MODEL);
        }
        return null;
    }

    public Long getAiSceneId() {
        JSONObject extendInfo = JSONObject.parseObject(this.extendInfo);
        if (Objects.nonNull(extendInfo)) {
            return extendInfo.getLong(Constants.STORE_WORK_AI.AI_SCENE_ID);
        }
        return null;
    }
}