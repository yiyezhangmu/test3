package com.coolcollege.intelligent.model.mapstrack;

import lombok.Data;

/**
 * 巡店轨迹
 * @ClassName  MapStrackDO
 * @Description 巡店轨迹
 * @author Aaron
 */
@Data
public class MapStrackDO {

    /**
     * 巡店记录
     */
    private String recordId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 巡店人姓名
     */
    private String supervisorName;

    /**
     * 巡店人id
     */
    private String supervisorId;

    /**
     * 巡店开始时间
     */
    private  Long signStartTime;

    /**
     * 巡店结束时间
     */
    private  Long signEndTime;


    /**
     * 门店经纬度
     */
    private  String storeLongitudeLatitude;


}
