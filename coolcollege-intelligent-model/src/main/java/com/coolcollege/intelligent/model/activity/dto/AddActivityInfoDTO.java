package com.coolcollege.intelligent.model.activity.dto;

import com.coolcollege.intelligent.model.activity.entity.ActivityInfoDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: AddActivityInfoDTO
 * @Description:
 * @date 2023-07-05 17:17
 */
@Data
public class AddActivityInfoDTO {

    @ApiModelProperty("活动内容")
    private String activityContent;

    @ApiModelProperty("活动导语")
    private String activityInstruction;

    @ApiModelProperty("活动标题")
    private String activityTitle;

    @ApiModelProperty("封面")
    private String coverImage;

    @ApiModelProperty("活动结束时间'")
    private Date endTime;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("0暂存/1提交/2暂存提交")
    private Integer submitFlag;

    @ApiModelProperty("可见范围")
    private String viewRangeType;

    @ApiModelProperty("可见范围")
    private List<ViewRangeDTO> viewRangeList;

    public static ActivityInfoDO convertDO(AddActivityInfoDTO param){
        if(Objects.isNull(param)){
            return null;
        }
        ActivityInfoDO result = new ActivityInfoDO();
        result.setActivityTitle(param.getActivityTitle());
        result.setActivityInstruction(param.getActivityInstruction());
        result.setCoverImage(param.getCoverImage());
        result.setActivityContent(param.getActivityContent());
        result.setStartTime(param.getStartTime());
        result.setEndTime(param.getEndTime());
        result.setViewRangeType(param.getViewRangeType());
        return result;
    }
}
