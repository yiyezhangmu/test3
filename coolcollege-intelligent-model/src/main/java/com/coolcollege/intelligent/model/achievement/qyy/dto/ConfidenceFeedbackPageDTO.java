package com.coolcollege.intelligent.model.achievement.qyy.dto;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: ConfidenceFeedbackPageDTO
 * @Description:
 * @date 2023-04-06 16:36
 */
@Data
public class ConfidenceFeedbackPageDTO extends PageBaseRequest {

    @ApiModelProperty("用户ids")
    private List<String> userIds;

    @ApiModelProperty("开始时间")
    private Date beginTime;

    @ApiModelProperty("截止时间")
    private Date endTime;

}
