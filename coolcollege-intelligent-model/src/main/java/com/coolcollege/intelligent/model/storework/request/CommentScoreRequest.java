package com.coolcollege.intelligent.model.storework.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author suzhuhong
 * @Date 2022/9/27 14:29
 * @Version 1.0
 */
@Data
@ApiModel
public class CommentScoreRequest {
    @ApiModelProperty(value = "图片 [{\"handle\":\"执行人图片\",\"final\":\"点评人编辑的图片\"}]")
    private String checkPics;
    @ApiModelProperty(value = "检查结果")
    private String checkResult;
    @ApiModelProperty(value = "检查项分值")
    private BigDecimal checkScore;
    @ApiModelProperty(value = "点评内容")
    private String commentContent;
    @ApiModelProperty(value = "得分倍数")
    private BigDecimal scoreTimes;
    @ApiModelProperty(value = "奖罚倍数")
    private BigDecimal awardTimes;
    @ApiModelProperty(value = "项数据ID")
    private Long id;
    @ApiModelProperty(value = "检查结果名称")
    private String checkResultName;
    @ApiModelProperty(value = "检查结果Id")
    private Long checkResultId;
}
