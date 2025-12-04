package com.coolcollege.intelligent.model.passengerflow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

/**
 * describe: 客流新看板
 * @author wxp
 * @date 2024/09/19
 */
@ApiModel
@Data
public class PassengerNewBoardRequest extends PassengerBaseRequest {
    @ApiModelProperty("门店id 多个,分隔")
    private String storeIdStr;
    @ApiModelProperty("区域id")
    private Long regionId;
    @ApiModelProperty("排序字段 flowInOut:经店  flowIn:进店 flowInPercent:进店转化")
    private String sortField;
    @ApiModelProperty("排序类型 正序ASC 倒序DESC")
    private String sortType;
}
