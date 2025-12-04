package com.coolcollege.intelligent.model.store.vo;

import com.coolcollege.intelligent.model.device.dto.DeviceDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/3/23 20:02
 */
@Data
@ToString
public class StoreAndDeviceVO {
    /**
     * 门店信息
     */
    private StoreDO store;

    private String allRegionName;

    /**
     * 总设备列表
     */
    private List<DeviceDTO> deviceList;


    /**
     * 收藏状态:0未收藏，1收藏
     */
    private Integer collectionStatus;
    /**
     * 距离我的距离
     */
    private String distance;
}
