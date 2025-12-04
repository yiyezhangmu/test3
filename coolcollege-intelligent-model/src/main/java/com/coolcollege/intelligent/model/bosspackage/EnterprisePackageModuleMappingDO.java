package com.coolcollege.intelligent.model.bosspackage;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author   xugangkun
 * @date   2022-03-22 04:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterprisePackageModuleMappingDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("套餐id")
    private Long packageId;

    @ApiModelProperty("模块id")
    private Long moduleId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private String createUserId;
}