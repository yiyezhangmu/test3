package com.coolcollege.intelligent.model.patrolstore.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author byd
 * @date 2023-08-17 16:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataColumnAppealParam extends PatrolStoreSubmitParam.DataStaTableColumnParam {

    @ApiModelProperty("申诉记录Id ")
    private Long appealId;

    @ApiModelProperty("申诉审核结果 pass:通过 reject:驳回")
    private String appealResult;

    @ApiModelProperty("申诉审核备注")
    private String appealReviewRemark;
}
