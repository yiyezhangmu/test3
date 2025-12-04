package com.coolcollege.intelligent.model.region.response;

import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionNodeDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDeviceDTO;
import com.coolcollege.intelligent.model.store.vo.StoreAndDeviceVO;
import lombok.Data;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/3/20 20:29
 */
@Data
public class RegionStoreListResp {
    /**
     * 区域列表
     */
    private List<RegionNodeDTO> regionList;

    /**
     * 门店列表
     */

    private List<StoreAndDeviceVO> storeList;



}
