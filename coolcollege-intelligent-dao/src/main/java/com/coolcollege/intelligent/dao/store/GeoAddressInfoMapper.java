package com.coolcollege.intelligent.dao.store;


import com.coolcollege.intelligent.model.store.GeoAddressInfoDO;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchenbiao
 * @date 2025-09-29 05:29
 */
public interface GeoAddressInfoMapper {

    /**
     * 插入或更新
     * @param record
     */
    void insertOrUpdate(@Param("record") GeoAddressInfoDO record);

    String getAddressByLongitudeLatitude(@Param("longitudeLatitude") String lat);

}