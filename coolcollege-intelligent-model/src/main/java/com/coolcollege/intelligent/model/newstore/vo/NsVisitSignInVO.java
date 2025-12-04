package com.coolcollege.intelligent.model.newstore.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangnan
 * @date 2022-03-08 18:04
 */
@Data
public class NsVisitSignInVO {

    @ApiModelProperty("拜访记录id")
    private Long recordId;
}
