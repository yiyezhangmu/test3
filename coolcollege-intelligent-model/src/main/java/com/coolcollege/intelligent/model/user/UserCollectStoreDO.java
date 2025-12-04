package com.coolcollege.intelligent.model.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2022-12-20 11:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCollectStoreDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("设备id")
    private String userId;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}