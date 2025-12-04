package com.coolcollege.intelligent.model.patrolstore.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BigRegionUser {
    @ApiModelProperty("大区稽核人id")
    private String bigRegionUserId;
    @ApiModelProperty("大区稽核人姓名")
    private String bigRegionUserName;
}
