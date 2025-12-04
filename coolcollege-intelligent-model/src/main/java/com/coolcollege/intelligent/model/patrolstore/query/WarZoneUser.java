package com.coolcollege.intelligent.model.patrolstore.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WarZoneUser {
    @ApiModelProperty("战区稽核人id")
    private String warZoneUserId;
    @ApiModelProperty("战区稽核人姓名")
    private String warZoneUserName;
}
