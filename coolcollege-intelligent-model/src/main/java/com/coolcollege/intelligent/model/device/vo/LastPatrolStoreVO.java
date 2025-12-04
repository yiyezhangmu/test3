package com.coolcollege.intelligent.model.device.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: LastPatrolStoreVO
 * @Description:
 * @date 2022-12-19 11:34
 */
@Data
public class LastPatrolStoreVO {

    @ApiModelProperty("门店Id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("是否绑定门店")
    private Boolean isBindStore;

    public LastPatrolStoreVO(String storeId, String storeName, String storeNum, Boolean isBindStore) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeNum = storeNum;
        this.isBindStore = isBindStore;
    }
}
