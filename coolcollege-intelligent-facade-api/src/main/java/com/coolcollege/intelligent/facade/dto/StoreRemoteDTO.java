package com.coolcollege.intelligent.facade.dto;

import lombok.Data;

import java.util.List;

@Data
public class StoreRemoteDTO {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店编号
     */
    private String storeNum;
    /**
     * 区域id
     */
    private Long regionId;
    /**
     * 区域路径
     */
    private String regionPath;
    /**
     * 推送标识 0：需要推送 1：取消推送
     */
    private Integer pushFlag;
    /**
     * 处理标志true已成功false未成功
     */
    private Boolean flag;
    /**
     * 推送次数
     */
    private Integer pushNum;
    /**
     * 推送时间
     */
    private String pushTime;
    /**
     * 支持玩法
     */
    private List<String> playCode;
}
