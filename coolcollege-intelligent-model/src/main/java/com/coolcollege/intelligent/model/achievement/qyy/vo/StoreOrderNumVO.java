package com.coolcollege.intelligent.model.achievement.qyy.vo;

import lombok.Data;

import java.util.Date;

@Data
public class StoreOrderNumVO {
    /**
     * 截止时间
     */
    private Date etlTm;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 大单数
     */
    private Integer orderNumber;

    /**
     * 大单总数
     */
    private Integer orderTotal;


}
