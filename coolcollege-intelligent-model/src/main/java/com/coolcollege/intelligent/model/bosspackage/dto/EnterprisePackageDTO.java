package com.coolcollege.intelligent.model.bosspackage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author   xugangkun
 * @date   2022-03-22 04:10
 */
@Data
@ApiModel(value = "企业套餐入参实体")
public class EnterprisePackageDTO implements Serializable {

    @ApiModelProperty("主键")
    private Long packageId;

    @ApiModelProperty("套餐名称")
    private String packageName;

    @ApiModelProperty("业务模块id列表")
    private List<Long> moduleIds;
}