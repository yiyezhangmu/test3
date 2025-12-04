package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/16 14:31
 * @Version 1.0
 */
@Data
@ApiModel(value = "门店检查项数据项VO 标准项")
public class StoreWorkDataTableColumnVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("recordId")
    private Long recordId;

    @ApiModelProperty("businessId")
    private String businessId;

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("门店ID")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径")
    private String regionPath;

    @ApiModelProperty("数据表的ID")
    private Long dataTableId;

    @ApiModelProperty("表ID")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    private String tableName;

    @ApiModelProperty("columnID")
    private Long metaColumnId;

    @ApiModelProperty("属性名称")
    private String metaColumnName;

    @ApiModelProperty("描述信息/执行标准")
    private String description;

    @ApiModelProperty("分类")
    private String categoryName;

    @ApiModelProperty("检查项结果:PASS,FAIL,INAPPLICABLE")
    private String checkResult;

    @ApiModelProperty("检查项结果id")
    private Long checkResultId;

    @ApiModelProperty("检查项结果名称")
    private String checkResultName;

    @ApiModelProperty("检查项上传的图片")
    private String checkPics;

    @ApiModelProperty("检查项的描述信息")
    private String checkText;

    @ApiModelProperty("检查项分值")
    private BigDecimal checkScore;

    @ApiModelProperty("检查项上传的视频")
    private String checkVideo;

    @ApiModelProperty("执行人ID ")
    private String handlerUserId;

    @ApiModelProperty("执行人名称 ")
    private String handlerUserName;

    @ApiModelProperty("执行人头像")
    private String Avatar;

    @ApiModelProperty("执行人电话 ")
    private String handlerUserMobile;

    @ApiModelProperty("执行人优先级最高的角色名称 ")
    private String handlerUserRoleName;

    @ApiModelProperty("问题任务状态")
    private String taskQuestionStatus;

    @ApiModelProperty("问题工单ID，没有写0")
    private Long taskQuestionId;

    @ApiModelProperty("检查项是否已经提交")
    private Integer submitStatus;

    @ApiModelProperty("奖罚金额 正数奖励金额 负数罚款金额")
    private BigDecimal rewardPenaltMoney;

    @ApiModelProperty("得分倍数")
    private BigDecimal scoreTimes;

    @ApiModelProperty("奖罚倍数")
    private BigDecimal awardTimes;

    @ApiModelProperty("权重百分比")
    private BigDecimal weightPercent;

    @ApiModelProperty("检查项最高分 根据不适用配置计算得出，各项累计可得出表的总分")
    private BigDecimal columnMaxScore;

    @ApiModelProperty("检查项最高奖励 根据不适用配置计算得出")
    private BigDecimal columnMaxAward;

    @ApiModelProperty("0普通项,1高级项,2红线项,3否决项,4加倍项,5采集项,6AI项")
    private Integer columnType;

    @ApiModelProperty("执行要求,值逗号隔开[true,true,true,true]，分别表示(拍照片 拍视频 录语音 写说明)")
    private String executeDemand;

    @ApiModelProperty("实际点评人id")
    private String actualCommentUserId;

    @ApiModelProperty("点评内容")
    private String commentContent;

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

    @ApiModelProperty("标准图")
    private String staPic;

    @ApiModelProperty("上次巡检结果")
    private String lastPatrolStoreResult;

    @ApiModelProperty("上次巡检结果ID")
    private Long lastDataColumnId;

    @ApiModelProperty("标准表检查项结果项")
    private List<TbMetaColumnResultDO> columnResultList;

    @ApiModelProperty("自定义表定义项详情")
    private StoreWorkDataTableDefColumnVO storeWorkDataTableDefColumn;

    @ApiModelProperty("是否允许用户自定义评分")
    private Integer userDefinedScore;

    @ApiModelProperty("采集项配置")
    private Integer configType;

    @ApiModelProperty("分值满分")
    private BigDecimal supportScore;

    @ApiModelProperty("最低分值")
    private BigDecimal lowestScore;

    @ApiModelProperty("是否开启ai检查")
    private Integer isAiCheck;

    @ApiModelProperty("AI检查项结果:PASS,FAIL,INAPPLICABLE")
    private String aiCheckResult;

    @ApiModelProperty("AI检查项结果id")
    private Long aiCheckResultId;

    @ApiModelProperty("AI检查项结果名称")
    private String aiCheckResultName;

    @ApiModelProperty("AI点评内容")
    private String aiCommentContent;

    @ApiModelProperty("AI检查项分值")
    private BigDecimal aiCheckScore;

    @ApiModelProperty("AI执行状态，0未执行 1分析中 2已完成 3失败")
    private Integer aiStatus;
}
