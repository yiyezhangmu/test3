package com.coolcollege.intelligent.model.patrolstore.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @Author: hu hu
 * @Date: 2024/12/20 9:42
 * @Description: 每天的门店是否已巡
 */
@Data
@Builder
@ApiModel("每天的门店是否已巡")
public class PatrolRecordStatusEveryDayVO {

    @ApiModelProperty("日期")
    private String dayDate;

    @ApiModelProperty("是否已巡")
    private Boolean status;
}
