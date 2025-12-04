package com.coolcollege.intelligent.model.patrolstore.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2024/9/18 16:46
 * @Version 1.0
 */
@Data
@AllArgsConstructor
public class PatrolRecordDataVO {

    @ApiModelProperty("本周数据")
    private Integer weeklyStoreCount;
    @ApiModelProperty("本月数据")
    private Integer monthlyStoreCount;

}
