package com.coolcollege.intelligent.model.region.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/10
 */
@Data
public class AuthRegionStoreUserDTO {

    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 是否是门店
     */
    @JsonProperty("store_flag")
    private Boolean storeFlag ;

    /**
     * 权限来源 create-数智门店创建  sync-钉钉同步
     */
    private String source;

    /**
     * 门店类型区域对应的 门店id
     */
    @JsonProperty("store_id")
    private String storeId;

    private String storeStatus;
}
