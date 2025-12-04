package com.coolcollege.intelligent.model.patrolstore.vo;

import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author byd
 * @date 2023-01-09 20:44
 */
@Data
public class TbDataTableInfoVO extends TbDataTableDO {

    private Long checkDataTableId;

    /**
     * 总项数
     */
    private Integer totalColumnNum;


    @ApiModelProperty("是否可以批量发起工单")
    private Boolean canBatchSendProblem;

}
