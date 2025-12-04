package com.coolcollege.intelligent.model.metatable;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionDTO;
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
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaQuickColumnDO implements Serializable {
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
    @Excel(name = "检查项分类")
    private String category;

    /**
     * 检查项名称
     */
    @Excel(name = "检查项名称")
    private String columnName;

    /**
     *  描述信息
     */
    @Excel(name = "检查项描述")
    private String description;

    /**
     * 创建者
     */
    private String createUser;



    /**
     * 检查表 工单处理人类型   1:人员   2:岗位
     */
    private String questionHandlerType;

    /**
     * 检查表 工单处理人(岗位)id 逗号隔开
     */
    private String questionHandlerId;
    /**
     * 检查表 工单处理人(岗位)名称
     */
    private String questionHandlerName;

    /**
     * 检查表 工单复检人类型   1:人员   2:岗位
     */
    private String questionRecheckerType;

    /**
     * 检查表 工单复检人名称
     */
    private String questionRecheckerName;


    /**
     * 检查表 工单复检人(岗位)id 逗号隔开
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
     * 工单抄送人(岗位)名称 顿号（、）隔开
     */
    private String questionCcName;

    /**
     * 工审批人列表
     */
    private String questionApproveUser;

    /**
     * 检查表 处罚金额
     */
    @Excel(name = "惩罚金额")
    private BigDecimal punishMoney;

    /**
     * 检查表 奖励金额
     */
    @Excel(name = "奖励金额")
    private BigDecimal awardMoney;

    /**
     * 检查表 排序
     */
    private Integer orderNum;

    /**
     * 检查表 标准图
     */
    private String standardPic;

    /**
     * 创建人名字
     */
    private String createUserName;

    /**
     * 更新人id
     */
    private String editUserId;

    /**
     * 更新人姓名
     */
    private String editUserName;

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
     * 抄送人列表
     */
    List<PersonPositionDTO> ccPeopleList;

    /**
     * 门店场景id
     */
    private Long storeSceneId;

    /**
     * @see com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum
     * 检查项类型
     */
    private Integer columnType;

    /**
     * ai分析阈值
     */
    private BigDecimal threshold;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     *是否允许用户自定义评分
     */
    private Integer userDefinedScore;

    /**
     * 0:不需要结果项和打分, 1:仅需要结果项，2:仅需要打分
     */
    private Integer configType;

    /**
     * 打分最低值-采集项
     */
    private BigDecimal minScore;

    /**
     * 打分最高值-采集项
     */
    private BigDecimal maxScore;

    /**
     * 归档状态 0未归档  1已归档
     */
    private Integer status;

    private String aiType;


    @ApiModelProperty("原始选取的使用人[{type:person,value:}{type:position,value:}]")
    private String usePersonInfo;

    @ApiModelProperty("使用人范围：self-仅自己，all-全部人员，part-部分人员")
    private String useRange;

    /**
     * 使用人userId集合（前后逗号分隔）
     */
    private String useUserids;

    /**
     * 使用人userId集合（前后逗号分隔）
     */
    private String commonEditUserids;

    /**
     * 是否必须拍照
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

    public TbMetaQuickColumnDO(Date editTime, String category, String columnName, String description, String createUser,
        String questionHandlerType, String questionHandlerId, BigDecimal punishMoney, BigDecimal awardMoney,
        String standardPic, String createUserName, String editUserId, String editUserName, String questionHandlerName,
        String questionCcId, String questionCcName, String questionCcType) {
        this.editTime = editTime;
        this.category = category;
        this.columnName = columnName;
        this.description = description;
        this.createUser = createUser;
        this.questionHandlerType = questionHandlerType;
        this.questionHandlerId = questionHandlerId;
        this.punishMoney = punishMoney;
        this.awardMoney = awardMoney;
        this.standardPic = standardPic;
        this.createUserName = createUserName;
        this.editUserId = editUserId;
        this.editUserName = editUserName;
        this.questionHandlerName = questionHandlerName;
        this.questionCcId = questionCcId;
        this.questionCcName = questionCcName;
        this.questionCcType = questionCcType;
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