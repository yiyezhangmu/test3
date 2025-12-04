package com.coolcollege.intelligent.model.oneparty.dto;

import lombok.Data;

/**
 * 门店通购买信息
 * @author zhangnan
 * @date 2022-06-20 11:18
 */
@Data
public class OnePartyBusinessRestrictionsDTO {

    /**
     * 是否可用
     */
    private Boolean isAvailable;

    /**
     * 已用数量
     */
    private Integer used;

    /**
     * 可用数量
     */
    private Integer available;

    /**
     * 全部数量
     */
    private Integer all;

    /**
     * 可用值
     */
    private String availableValue;

    /**
     * 套餐版本
     */
    private String setMealVersion;
}
