package com.coolcollege.intelligent.model.unifytask.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementTaskStoreQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Integer pageNum = 1;
    /**
     *
     */
    private Integer pageSize = 10;
    /**
     * 门店名称
     */
    @ApiModelProperty("门店名称")
    private String storeName;

    /**
     * 子任务状态
     */
    @ApiModelProperty("任务状态 进行中:0 已完成: 1")
    private Integer status;

    @ApiModelProperty("父任务id")
    private Long unifyTaskId;

    /**
     * 子任务状态
     */
    @ApiModelProperty("是否上报 0: 未上报 1: 已上报")
    private Boolean report;


    private List<String> regionPathList;

    @ApiModelProperty("任务类型 新品上架 ACHIEVEMENT_NEW_RELEASE\n" +
            "   老品下架 ACHIEVEMENT_OLD_PRODUCTS_OFF")
    private String taskType;

}
