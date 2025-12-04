package com.coolcollege.intelligent.model.tbdisplay.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yezhe
 * @date 2020-11-17 15:45
 */
@ApiModel
@Data
public class TbDisplayBatchDeleteParam {


    @ApiModelProperty("门店任务id")
    private List<Long> taskStoreIds;
}
