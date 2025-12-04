package com.coolcollege.intelligent.model.share.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskShareInsertDTO {

    @ApiModelProperty("分享id")
    private String shareId;

    @ApiModelProperty("分享的类型")
    @JsonProperty("share_type")
    private String shareType;


    @ApiModelProperty("分享的门店标签")
    @JsonProperty("share_store_label")
    private String shareStoreLabel;


    @ApiModelProperty("分享可见人")
    @JsonProperty(value ="user_id_list" , access = JsonProperty.Access.WRITE_ONLY)
    private List<String> userIdList;


    @ApiModelProperty("可见范围")
    @JsonProperty("visible_range")
    private Integer visibleRange;


    @ApiModelProperty("任务id")
    @JsonProperty(value = "task_id", access = JsonProperty.Access.WRITE_ONLY)
    private Long taskId;

    @ApiModelProperty("门店id")
    @JsonProperty("store_id")
    private String storeId;

    @ApiModelProperty("子任务id")
    @JsonProperty("task_sub_id")
    private Long taskSubId;

    @JsonProperty("business_id_list")
    private List<Long> businessIdList;

    @ApiModelProperty("业务id")
    private Long businessId;

    @ApiModelProperty("loopCount")
    private Long loopCount;



}
