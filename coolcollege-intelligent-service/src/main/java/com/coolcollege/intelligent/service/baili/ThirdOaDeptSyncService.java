package com.coolcollege.intelligent.service.baili;

import com.coolcollege.intelligent.facade.dto.RegionDTO;

import java.util.List;

/**
 * @author byd
 * @date 2021-08-11 16:05
 */
public interface ThirdOaDeptSyncService {

    /**
     * 同步百丽区域组织架构
     * @param eid 企业id
     * @param unitId 组织架构id
     * @param resultList 组织架构列表
     */
    void syncOrgAll(String eid, String unitId, List<RegionDTO> resultList);

    /**
     *  同步区域组织架构 支持节点同步
     * @param eid
     * @param unitId
     * @param resultList
     */
    void newSyncOrgAll(String eid,Long regionId, String unitId, List<RegionDTO> resultList);
}
