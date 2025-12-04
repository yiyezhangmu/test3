package com.coolcollege.intelligent.model.bosspackage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/24 14:11
 */
@Data
public class CurrentPackageDetailVO {
    @ApiModelProperty("套餐id")
    private Long packageId;

    @ApiModelProperty("套餐名称")
    private String packageName;

    @ApiModelProperty("业务模块id列表")
    private List<Long> moduleIdList;

}
