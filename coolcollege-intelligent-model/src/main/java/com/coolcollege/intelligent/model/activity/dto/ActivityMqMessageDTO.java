package com.coolcollege.intelligent.model.activity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-07-03 08:23
 */
@Data
public class ActivityMqMessageDTO implements Serializable {

    @ApiModelProperty("企业id")
    private String enterpriseId;

    @ApiModelProperty("活动id")
    private Long activityId;

    @ApiModelProperty("评论id")
    private Long commentId;

    public ActivityMqMessageDTO(String enterpriseId, Long activityId) {
        this.enterpriseId = enterpriseId;
        this.activityId = activityId;
    }

    public ActivityMqMessageDTO(String enterpriseId, Long activityId, Long commentId) {
        this.enterpriseId = enterpriseId;
        this.activityId = activityId;
        this.commentId = commentId;
    }
}