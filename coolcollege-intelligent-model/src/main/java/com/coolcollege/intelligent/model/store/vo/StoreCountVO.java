package com.coolcollege.intelligent.model.store.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangchenbiao
 * @FileName: StoreCountVO
 * @Description:门店数量
 * @date 2022-11-30 10:05
 */
@Data
public class StoreCountVO {

    @ApiModelProperty("企业名称")
    private String name;

    @ApiModelProperty("企业logo")
    private String logo;

    @ApiModelProperty("原始名称")
    private String originalName;

    @ApiModelProperty("套餐开始时间")
    private Date packageBeginTime;

    @ApiModelProperty("套餐结束时间")
    private Date packageEndTime;

    @ApiModelProperty("所属行业")
    private String industry;

    @ApiModelProperty("现有门店数量")
    private Integer storeCount;

    @ApiModelProperty("限制的门店数量")
    private Integer limitStoreCount;

    @ApiModelProperty("自定义套餐结束时间")
    private Long customizePackageEndTime;

    public StoreCountVO(String name, String logo, String originalName, Date packageBeginTime, Date packageEndTime, String industry, Integer storeCount, Integer limitStoreCount) {
        this.name = name;
        this.logo = logo;
        this.originalName = originalName;
        this.packageBeginTime = packageBeginTime;
        this.packageEndTime = packageEndTime;
        this.industry = industry;
        this.storeCount = storeCount;
        this.limitStoreCount = limitStoreCount;
    }
}
