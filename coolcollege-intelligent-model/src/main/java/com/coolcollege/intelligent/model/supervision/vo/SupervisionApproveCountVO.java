package com.coolcollege.intelligent.model.supervision.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/4/12 10:54
 * @Version 1.0
 */
@Data
public class SupervisionApproveCountVO {

    @ApiModelProperty("按人审批数量")
    private Integer personApproveCount;
    @ApiModelProperty("按门店审批数量")
    private Integer storeApproveCount;

}
