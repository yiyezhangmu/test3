package com.coolcollege.intelligent.model.achievement.qyy.vo;

import com.coolcollege.intelligent.model.qyy.QyyConfidenceFeedbackDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: ConfidenceFeedbackDetailVO
 * @Description:
 * @date 2023-04-06 16:45
 */
@Data
public class ConfidenceFeedbackDetailVO {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("部门名称")
    private String regionName;

    @ApiModelProperty("分数")
    private BigDecimal score;

    @ApiModelProperty("保障举措")
    private String measure;

    @ApiModelProperty("资源支持")
    private String resourceSupport;

    @ApiModelProperty("创建时间")
    private Date createTime;

    public static ConfidenceFeedbackDetailVO convert(QyyConfidenceFeedbackDO param, String regionName){
        if(Objects.isNull(param)){
            return null;
        }
        ConfidenceFeedbackDetailVO result = new ConfidenceFeedbackDetailVO();
        result.setId(param.getId());
        result.setUserId(param.getUserId());
        result.setUsername(param.getUsername());
        result.setRegionName(regionName);
        result.setScore(param.getScore());
        result.setMeasure(param.getMeasure());
        result.setResourceSupport(param.getResourceSupport());
        result.setCreateTime(param.getCreateTime());
        return result;
    }

}
