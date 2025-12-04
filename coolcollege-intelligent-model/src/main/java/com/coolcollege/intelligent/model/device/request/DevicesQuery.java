package com.coolcollege.intelligent.model.device.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/6/26 10:09
 */
@Data
public class DevicesQuery implements Serializable {

    private static final long serialVersionUID = -6707280013173782033L;

    /**
     * 门店id
     */
    @JsonProperty(value= "store_id")
    private String storeId;

    /**
     * 区域ID与门店互斥
     */
    @JsonProperty(value= "area_id")
    private String areaId;

    /**
     * 查询设备类型
     */
    @JsonProperty(value= "device_type")
    private String deviceType;

    /**
     * 设备名
     */
    @JsonProperty(value= "device_name")
    private String device;


    /**
     * 绑定状态
     */
    @JsonProperty(value= "bind_status")
    private String bindStatus="unbind";

    /**
     * 设备状态
     */
    @JsonProperty(value= "device_status")
    private String deviceStatus;

    /**
     * 页码
     */
    @JsonProperty(value= "page_number")
    private Integer pageNumber =1;

    /**
     * 页数
     */
    @JsonProperty(value= "page_size")
    private Integer pageSize =10;
}
