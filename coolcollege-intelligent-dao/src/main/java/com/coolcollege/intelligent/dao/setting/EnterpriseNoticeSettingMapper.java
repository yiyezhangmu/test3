package com.coolcollege.intelligent.dao.setting;

import com.coolcollege.intelligent.model.setting.EnterpriseNoticeSettingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/22
 */
@Mapper
public interface EnterpriseNoticeSettingMapper {

    void batchInsertEnterpriseNoticeSetting(@Param("enterpriseNoticeSettingList")List<EnterpriseNoticeSettingDO> enterpriseNoticeSettingDOList);

    List<EnterpriseNoticeSettingDO> selectEnterpriseNoticeSetting(@Param("eid")String eid);

    void deleteEnterpriseNoticeSetting(@Param("eid")String eid);

}
