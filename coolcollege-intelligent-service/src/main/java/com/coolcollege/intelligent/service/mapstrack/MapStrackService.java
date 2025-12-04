package com.coolcollege.intelligent.service.mapstrack;

import java.util.Map;

/**
 * 地图轨迹基本操作
 * @ClassName  MapsTrackController
 * @Description 地图轨迹基本操作
 * @author Aaron
 */
public interface MapStrackService {

    /**
     * 门店地图
     * @Description 门店地图
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     */
    Object queryStoreMap(String enterpriseId, Map<String, Object> map, Integer pageNum, Integer pageSize);
}
