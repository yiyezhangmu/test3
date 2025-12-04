package com.coolcollege.intelligent.service.system.sysconf.impl;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.sysconf.EnterpriseDictMappingMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.DictTypeEnum;
import com.coolcollege.intelligent.model.system.sysconf.EnterpriseDictMappingDO;
import com.coolcollege.intelligent.service.system.sysconf.EnterpriseDictMappingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/3/26 13:34
 */
@Service
public class EnterpriseDictMappingServiceImpl implements EnterpriseDictMappingService {
    @Resource
    private EnterpriseDictMappingMapper enterpriseDictMappingMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Override
    public List<EnterpriseDictMappingDO> listDictMapping(String enterpriseId, String dingCorpId, String dictKey, String businessType) {
        return enterpriseDictMappingMapper.listMappingByKeyAndType(enterpriseId,dingCorpId,dictKey,businessType);
    }

    @Override
    public EnterpriseDictMappingDO addDictMapping(EnterpriseDictMappingDO enterpriseDictMappingDO) {
        enterpriseDictMappingDO.setRemark(DictTypeEnum.getDescribeByCode(enterpriseDictMappingDO.getBusinessType()));
        enterpriseDictMappingMapper.batchInsert(Collections.singletonList(enterpriseDictMappingDO));
        return enterpriseDictMappingDO;
    }

    @Override
    public Boolean deleteDictMapping(EnterpriseDictMappingDO enterpriseDictMappingDO) {
        enterpriseDictMappingMapper.deleteById(enterpriseDictMappingDO.getId());
        return Boolean.TRUE;
    }

    @Override
    public Object updateDictMapping(EnterpriseDictMappingDO enterpriseDictMappingDO) {
        enterpriseDictMappingMapper.updateValueByKeyAndType(enterpriseDictMappingDO.getDictValue(),
                enterpriseDictMappingDO.getId(),
                enterpriseDictMappingDO.getEnterpriseId(),null);
        return enterpriseDictMappingDO;
    }
}
