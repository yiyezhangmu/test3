package com.coolcollege.intelligent.model.store.vo;

import com.coolcollege.intelligent.model.device.dto.DeviceDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StoreDeviceVO {
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 门店名字
     */
    private String storeName;

    private String storeStatus;

    private String  storeAddress;

    private String locationAddress;

    private String storeNum;

    private Integer storeUserCount;

    private Integer unHandleQuestionCount;
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
     * 店长名称
     */
    private String shopowner;
    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;
    /**
     * 阿里云租户id（视觉）
     */
    private String aliyunCorpId;

    /**
     * B1集合
     */
    private List<String> B1Names;
    /**
     * B1的id集合
     */
    private List<String> B1List;
    /**
     * 摄像头集合
     */
    private List<String> videoNames;

    private String vdsCorpId;

    private Integer status;

    private String groupId;
    private Integer distance;

    private List<DeviceDTO> deviceList;

    private Boolean hasOnlineTask;

    /**
     * 是否完善门店信息
     * */
    private String isPerfect;

    /**
     * 门头照
     */
    private String avatar;


    private Date lastPatrolStoreTime;
}
