package com.coolcollege.intelligent.dao.mapstrack;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 地图轨迹基本操作
 * @ClassName  MapsTrackController
 * @Description 地图轨迹基本操作
 * @author Aaron
 */
@Mapper
@Deprecated   //垃圾代码，地图功能需要重构
public interface MapStrackMapper {



    /**
     * 获取当前人员门店列表
     * @param map
     * @return
     */
    List<Map<String, Object>> getStoreList(@Param("map") Map<String, Object> map);
}
