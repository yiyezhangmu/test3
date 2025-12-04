package com.coolcollege.intelligent.model.safetycheck.vo;

import com.coolcollege.intelligent.model.safetycheck.TbDataColumnAppealDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 
 * @author   wxp
 * @date   2023-08-14 07:53
 */
@Data
public class TbDataColumnAppealVO extends TbDataColumnAppealDO {

    @ApiModelProperty("申诉人")
    private String appealUserName;

    @ApiModelProperty("申诉实际审批人")
    private String appealActualReviewUserName;

    @ApiModelProperty("是否有申诉历史")
    private Boolean hasAppealHistory;


}