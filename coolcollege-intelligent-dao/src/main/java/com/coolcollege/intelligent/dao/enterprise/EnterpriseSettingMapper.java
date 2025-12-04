package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnterpriseSettingMapper {

    int insert(EnterpriseSettingDO record);

    EnterpriseSettingDO selectByEnterpriseId(String enterpriseId);

    int updateDingDingSyncSettingByEnterpriseId(EnterpriseSettingDO record);

    /**
     * 根据企业id跟新企业接入酷学院配置
     * @param record
     * @author: xugangkun
     * @return int
     * @date:
     */
    int updateAccessCoolCollegeByEnterpriseId(EnterpriseSettingDO record);

    int updateSyncPassengerByEnterpriseId(EnterpriseSettingDO record);


    /**
     * 修改图片
     * @param enterpriseId
     * @param appHomePagePic
     */
     void updateAppHomePagePic(@Param("enterpriseId") String enterpriseId, @Param("appHomePagePic") String appHomePagePic);

    List<EnterpriseSettingDO> selectListExtend();
}