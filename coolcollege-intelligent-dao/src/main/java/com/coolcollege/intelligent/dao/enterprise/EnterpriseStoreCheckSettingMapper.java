package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface EnterpriseStoreCheckSettingMapper {
    /**
     *获取企业巡店配置
     * @param eId 企业ID
     * @return
     */
    EnterpriseStoreCheckSettingDO getEnterpriseStoreCheckSetting(@Param("eId") String eId);

    List<EnterpriseStoreCheckSettingDO> getEnterpriseStoreCheckSettingAll();
    /**
     *
     * @param eId 企业ID
     * @param entity 门店配置详细信息
     * @return
     */
    int insertOrUpdate(@Param("eId") String eId,@Param("entity") EnterpriseStoreCheckSettingDO entity);

    void updatePatrolScheduleId(@Param("eId") String eid, @Param("patrolOpenScheduleId") String patrolOpenScheduleId);

    /**
     * 获取巡店等级信息
     * @param eid
     * @return
     */
    String getLevelInfo(@Param("eid") String eid);

    void updateLevelInfo(@Param("eid") String eid, @Param("levelInfo") String levelInfo, @Param("checkResultInfo") String checkResultInfo);

    String getCheckResult(@Param("eid") String eid);

    Map<String, String> getPatrolBiosInfo(@Param("eid") String eid);

    int initCheckSetting(@Param("eId") String eId,@Param("entity") EnterpriseStoreCheckSettingDO entity);
}
