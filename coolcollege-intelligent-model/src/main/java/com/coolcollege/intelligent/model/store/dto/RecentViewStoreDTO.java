package com.coolcollege.intelligent.model.store.dto;

import com.coolcollege.intelligent.model.device.DeviceDO;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/1/13 15:11
 */
@Data
public class RecentViewStoreDTO extends SelectStoreDTO{

    private List<DeviceDO> deviceList;

    private int deviceNum;
}
