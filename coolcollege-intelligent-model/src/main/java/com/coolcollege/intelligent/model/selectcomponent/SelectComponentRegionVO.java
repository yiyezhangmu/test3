package com.coolcollege.intelligent.model.selectcomponent;

import lombok.Data;

import java.util.List;

/**
 * @desc: 选人组件中人员信息的返回
 * @author: xuanfeng
 * @date: 2021-10-27 15:07
 */
@Data
public class SelectComponentRegionVO {
    /**
     * 区域id
     */
    private String id;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 门店数
     */
    private Integer StoreNum;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 区域类型
     */
    private String regionType;

    /**
     * 上级区域
     */
    List<SelectComponentRegionVO> regions;

    // 以下信息是冗余门店的 门店类型区域  会有
    /**
     * 门店地址
     */
    private String address;
    /**
     * 设备
     */
    List<SelectComponentDeviceVO> devices;

    /**
     * 人员数
     */
    private Integer userCount;

    /**
     * 是否有摄像头
     */
    private Boolean hasCamera;

    /**
     * 是否有权限
     */
    private Boolean hasAuth;

}
