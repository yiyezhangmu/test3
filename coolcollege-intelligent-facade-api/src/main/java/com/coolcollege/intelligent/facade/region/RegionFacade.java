package com.coolcollege.intelligent.facade.region;

import com.coolcollege.intelligent.facade.dto.BaseResultDTO;
import com.coolcollege.intelligent.facade.dto.CorrectRegionPathRequest;
import com.coolcollege.intelligent.facade.dto.CorrectRegionStoreNumRequest;
import com.coolcollege.intelligent.facade.dto.RegionDTO;

import java.util.List;

/**
 * 区域RPC接口
 *
 * @author chenyupeng
 * @since 2021/12/28
 */
public interface RegionFacade {

    /**
     * 订正区域表的区域路径字段
     * @author chenyupeng
     * @date 2021/12/29
     * @param requestList
     * @return com.coolcollege.intelligent.facade.dto.BaseResultDTO
     */
    BaseResultDTO updateRegionPath(List<CorrectRegionPathRequest> requestList);

    /**
     * 订正门店表的区域路径字段
     * @author chenyupeng
     * @date 2021/12/29
     * @param requestList
     * @return com.coolcollege.intelligent.facade.dto.BaseResultDTO
     */
    BaseResultDTO updateStoreRegionPath(List<CorrectRegionPathRequest> requestList);

    /**
     * 订正区域的门店数量
     * @author chenyupeng
     * @date 2021/12/29
     * @param requestList
     * @return com.coolcollege.intelligent.facade.dto.BaseResultDTO
     */
    BaseResultDTO updateRegionStoreNum(List<CorrectRegionStoreNumRequest> requestList);

    /**
     * 订正区域门店数量（包含门店实际统计数量）
     * @param eid
     * @return
     */
    BaseResultDTO updateRecursionRegionStoreNum(String eid);
    /**
     * 订正新店区域路径
     * @param requestList List<CorrectRegionPathRequest>
     * @return BaseResultDTO
     */
    BaseResultDTO updateNsStoreRegionPath(List<CorrectRegionPathRequest> requestList);


    BaseResultDTO<List<RegionDTO>> getRegionByIds(String eid,List<String> regionIds);

    BaseResultDTO<List<RegionDTO>> getSubRegionByRegionIds(String enterpriseId, List<String> regionIds);

}
