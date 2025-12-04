package com.coolcollege.intelligent.model.achievement.qyy.dto;

import com.coolcollege.intelligent.model.qyy.QyyConfidenceFeedbackDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangchenbiao
 * @FileName: SubmitConfidenceFeedbackDTO
 * @Description: 信心反馈
 * @date 2023-04-06 11:38
 */
@Data
public class SubmitConfidenceFeedbackDTO {

    @ApiModelProperty("部门id")
    private String synDingDeptId;

    @ApiModelProperty("群id")
    private String conversationId;

    @ApiModelProperty("分数")
    private BigDecimal score;

    @ApiModelProperty("保障举措")
    private String measure;

    @ApiModelProperty("资源支持")
    private String resourceSupport;

    public static QyyConfidenceFeedbackDO convert(SubmitConfidenceFeedbackDTO param, String username, String userId, Long regionId){
        QyyConfidenceFeedbackDO result = new QyyConfidenceFeedbackDO();
        result.setUsername(username);
        result.setUserId(userId);
        result.setRegionId(regionId);
        result.setConversationId(param.getConversationId());
        result.setScore(param.getScore());
        result.setMeasure(param.getMeasure());
        result.setResourceSupport(param.getResourceSupport());
        return result;
    }

}
