package com.coolcollege.intelligent.service.system.sysconf;

import com.coolcollege.intelligent.model.system.sysconf.EnterpriseDictMappingDO;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/3/26 13:32
 */
public interface EnterpriseDictMappingService {
    List<EnterpriseDictMappingDO> listDictMapping(String enterpriseId, String dingCorpId, String dictKey, String businessType);

    EnterpriseDictMappingDO addDictMapping(EnterpriseDictMappingDO enterpriseDictMappingDO);

    Boolean deleteDictMapping(EnterpriseDictMappingDO enterpriseDictMappingDO);

    Object updateDictMapping(EnterpriseDictMappingDO enterpriseDictMappingDO);
}
