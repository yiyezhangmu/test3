package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.FictitiousInstanceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author shoul
 */
@Mapper
public interface FictitiousInstanceMapper {

    /**
     * 获取虚拟实例
     * @param enterpriseId
     * @return
     */
    FictitiousInstanceDO selectFictitiousInstance(@Param("enterpriseId") String enterpriseId);

    /**
     * 插入虚拟实例
     * @param enterpriseId
     * @param instanceDO
     */
    void insertFictitiousInstance(@Param("enterpriseId") String enterpriseId,
                                  @Param("instanceDO") FictitiousInstanceDO instanceDO);
}