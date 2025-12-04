package com.coolcollege.intelligent.model.boss;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   xugangkun
 * @date   2022-04-07 04:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BossUserLoginRecordDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("登陆ip")
    private String ip;

    @ApiModelProperty("登陆时间")
    private Date loginTime;
}