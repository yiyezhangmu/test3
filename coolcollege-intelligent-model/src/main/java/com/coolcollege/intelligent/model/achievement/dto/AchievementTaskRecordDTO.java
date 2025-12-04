package com.coolcollege.intelligent.model.achievement.dto;

import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author   zhangchenbiao
 * @date   2024-03-16 01:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementTaskRecordDTO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;


    @ApiModelProperty("子任务审批链开始时间")
    private Date subBeginTime;

    @ApiModelProperty("子任务审批链结束时间")
    private Date subEndTime;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("循环任务的循环批次")
    private Long loopCount;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("区域id")
    private Long regionId;


    @ApiModelProperty("任务状态 进行中:0 已完成: 1")
    private Integer status;
    @ApiModelProperty("任务类型   新品上架 ACHIEVEMENT_NEW_RELEASE\n" +
            "   老品下架 ACHIEVEMENT_OLD_PRODUCTS_OFF")
    private String taskType;

    @ApiModelProperty("门店任务id")
    private Long taskStoreId;

    @ApiModelProperty("是否填报 0:未填报 1:已填报")
    private Boolean report;


    /**
     * 处理人
     */
    @ApiModelProperty("处理人")
    private List<PersonDTO> handleUser;

    @ApiModelProperty("是否是处理人 0:否 1:是")
    private Boolean handle;
}