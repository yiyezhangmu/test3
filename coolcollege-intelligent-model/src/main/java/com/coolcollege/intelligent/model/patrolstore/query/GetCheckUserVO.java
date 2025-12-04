package com.coolcollege.intelligent.model.patrolstore.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetCheckUserVO {

    @ApiModelProperty("大区稽核人")
    private String bigRegionUsers;

    @ApiModelProperty("战区稽核人")
    private String warZoneUsers;

    @ApiModelProperty("扩展字段")
    private String extendField;

}
