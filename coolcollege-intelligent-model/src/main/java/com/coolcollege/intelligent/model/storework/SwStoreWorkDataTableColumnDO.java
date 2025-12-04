package com.coolcollege.intelligent.model.storework;

import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   wxp
 * @date   2022-09-08 02:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwStoreWorkDataTableColumnDO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("店务记录表tc_business_id")
    private String tcBusinessId;

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;

    @ApiModelProperty("店务日期 月2022-08-01 周2022-08-01 日2022-08-02")
    private Date storeWorkDate;

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

    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("分类")
    private String categoryName;

    @ApiModelProperty("检查项结果:PASS,FAIL,INAPPLICABLE")
    private String checkResult;

    @ApiModelProperty("检查项结果id")
    private Long checkResultId;

    @ApiModelProperty("检查项结果名称")
    private String checkResultName;

    @ApiModelProperty("检查项上传的图片图片数组,[{\"handle\":\"url1\",\"final\":\"url2\"}]")
    private String checkPics;

    @ApiModelProperty("检查项的描述信息")
    private String checkText;

    @ApiModelProperty("检查项分值")
    private BigDecimal checkScore;

    @ApiModelProperty("检查项上传的视频")
    private String checkVideo;

    @ApiModelProperty("处理人ID ")
    private String handlerUserId;

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

    @ApiModelProperty("值1")
    private String value1;

    @ApiModelProperty("值2")
    private String value2;

    @ApiModelProperty("提交时间")
    private Date submitTime;

    @ApiModelProperty("检查表映射表id")
    private Long tableMappingId;

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

    @ApiModelProperty("AI执行失败原因")
    private String aiFailReason;
}