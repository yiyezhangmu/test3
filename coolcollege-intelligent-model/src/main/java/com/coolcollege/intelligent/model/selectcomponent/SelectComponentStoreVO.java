package com.coolcollege.intelligent.model.selectcomponent;

import lombok.Data;

import java.util.List;

/**
 * @desc: 选人组件中人员信息的返回
 * @author: xuanfeng
 * @date: 2021-10-27 15:07
 */
@Data
public class SelectComponentStoreVO {
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 门店对应区域id
     */
    private Long storeRegionId;

    /**
     * 门店名称
     */
    private String name;

    /**
     * 人员数
     */
    private Integer userCount;

    /**
     * 是否有摄像头
     */
    private Boolean hasCamera;

    /**
     * 门店地址
     */
    private String address;

    private String storeStatus;

    /**
     * 根目录->上级的区域
     */
    List<SelectComponentRegionVO> regions;

    /**
     * 设备
     */
    List<SelectComponentDeviceVO> devices;
}
