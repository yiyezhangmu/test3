package com.coolcollege.intelligent.model.unifytask.dto;

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
public class AchievementTaskStoreSubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店名称
     */
    @ApiModelProperty("门店id")
    private String storeId;


    @ApiModelProperty("父任务id")
    private Long unifyTaskId;


    @ApiModelProperty("型号")
    private List<TaskModelsDTO> taskModelsDTOList;

}

