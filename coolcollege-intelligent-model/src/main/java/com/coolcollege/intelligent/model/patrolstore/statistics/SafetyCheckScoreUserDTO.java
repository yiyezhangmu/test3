

package com.coolcollege.intelligent.model.patrolstore.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户执行力相关统计
 * 
 * @author jeffrey
 * @date 2020/12/10
 */
@Data
public class SafetyCheckScoreUserDTO {

    private String userId;

    @ApiModelProperty("门店平均得分")
    private BigDecimal storeAvgScore;

    @ApiModelProperty("90分以上门店数")
    private Long ninetyScoreStoreNum;

    @ApiModelProperty("80分到89门店数")
    private Long eightyScoreStoreNum;

    @ApiModelProperty("80分一下门店数")
    private Long eightyDownScoreStoreNum;
}
