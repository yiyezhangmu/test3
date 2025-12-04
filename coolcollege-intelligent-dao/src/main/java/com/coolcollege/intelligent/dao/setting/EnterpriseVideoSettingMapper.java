package com.coolcollege.intelligent.dao.setting;

import com.coolcollege.intelligent.model.setting.EnterpriseVideoSettingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnterpriseVideoSettingMapper {

    List<EnterpriseVideoSettingDO> listEnterpriseVideoSettingByEid(@Param("eid")String eid );

    EnterpriseVideoSettingDO selectEnterpriseVideoSettingByYunType(@Param("eid")String eid, @Param("yunType")String yunType, @Param("accountType")String accountType);

    void  insertEnterpriseVideoSetting (EnterpriseVideoSettingDO enterpriseVideoSettingDO);

    void updateEnterpriseVideoSetting(EnterpriseVideoSettingDO enterpriseVideoSettingDO);

    void updateVdsCorpId(@Param("id") Long id, @Param("rootVdsCorpId") String rootVdsCorpId);

    void updateLastSyncLastTime(@Param("enterpriseId")String enterpriseId, @Param("yunType")String yunType, @Param("syncStatus")Integer syncStatus);

    EnterpriseVideoSettingDO selectEnterpriseVideoSettingByAppkey(@Param("eid")String eid, @Param("yunType")String yunType, @Param("accountType")String accountType, @Param("appKey") String appKey);
}
