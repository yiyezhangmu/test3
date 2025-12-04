package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/7/19 10:58
 * @Version 1.0
 */
@Data
public class StoreDetailVO {
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
     * 门头照
     */
    private String avatar;


    /**
     * 区域id
     */
    private Long regionId;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String county;

    /**
     * 门店地址
     */
    private String storeAddress;

    /**
     * 定位地址
     */
    private String locationAddress;

    /**
     * 锁定定位:locked/锁定,not_ocked/未锁定
     */
    private String isLock;

    /**
     * 经维度
     */
    private String longitudeLatitude;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;

    /**
     * 状态:effective/有效,invalid/失效
     */
    private String isDelete;

    /**
     * 电话号码
     */
    private String telephone;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 门店面积
     */
    private String storeAcreage;

    /**
     * 门店带宽
     */
    private String storeBandwidth;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 创建人id
     */
    private String createUser;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 更新人
     */
    private String updateName;

    /**
     * 更新人id
     */
    private String updateUser;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否设备打卡:device/设备打卡,not_device/非设备打卡
     */
    private String isDevice;

    /**
     * 阿里云租户id（视觉）
     */
    private String aliyunCorpId;
    /**
     * 门店来源
     */
    private String source;
    /**
     * 是否忽略
     */
    private Long isValid;
    /**
     * 钉钉Id
     */
    private String dingId;

    /**
     * vdscorpId
     */
    private String vdsCorpId;

    /**
     * dinging部门id
     */
    private String synDingDeptId;

    /**
     * 区域路径
     */
    private String regionPath;

    /**
     * 门店是否绑定摄像头
     */
    private Boolean hasCamera;

    /**
     * 门店状态（open：营业、closed：闭店、not_open：未开业）
     */
    private String storeStatus;

    /**
     * 动态扩展字段
     */
    private String extendField;

}
