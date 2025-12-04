package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName EnterpriseConfigServiceImpl
 * @Description 用一句话描述什么
 */
@Service(value = "enterpriseConfigService")
@Slf4j
public class EnterpriseConfigServiceImpl implements EnterpriseConfigService {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;


    @Override
    public EnterpriseConfigDO selectByCorpId(String corpId, String appType) {
        List<EnterpriseConfigDO> configs = enterpriseConfigMapper.selectByCorpId(corpId);
        if (StringUtils.isBlank(appType)) {
            if (configs.isEmpty()) {
                return null;
            }
            return configs.get(0);
        }
        String message = AppTypeEnum.getMessage(appType);
        if (StringUtils.isBlank(message)) {
            appType = AppTypeEnum.DING_DING.getValue();
        }
        String finalAppType = appType;
        if (AppTypeEnum.DING_DING.getValue().equals(finalAppType)) {
            return configs.stream()
                    .filter(config -> finalAppType.equals(config.getAppType()) || Constants.E_APP.equals(config.getAppType()))
                    .findFirst().orElse(null);
        }
        return configs.stream().filter(config -> finalAppType.equals(config.getAppType())).findFirst().orElse(null);
    }

    @Override
    public EnterpriseConfigDO selectByEnterpriseId(String enterpriseId) {
        return enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
    }

    @Override
    public void updateByEnterpriseId(EnterpriseConfigDO enterpriseConfigDO) {
        enterpriseConfigMapper.updateByEnterpriseId(enterpriseConfigDO);
    }

    @Override
    public List<EnterpriseConfigDO> selectByEnterpriseIds(List<String> enterpriseIds) {
        if(CollectionUtils.isEmpty(enterpriseIds)){
            return Lists.newLinkedList();
        }
        return enterpriseConfigMapper.selectByEnterpriseIds(enterpriseIds);
    }

    @Override
    public List<EnterpriseConfigDO> selectAllEnterpriseConfig(List<String> enterpriseIds) {
        return enterpriseConfigMapper.selectByEnterpriseIds(enterpriseIds);
    }

    @Override
    public List<EnterpriseConfigDO> selectAllEnterpriseConfig(List<String> enterpriseIds, List<Integer> statusList) {
        return enterpriseConfigMapper.selectByEnterpriseIdsAndStatus(enterpriseIds, statusList);
    }

    @Override
    public String getDbServerByDbName(String dbName) {
        if(StringUtils.isBlank(dbName)){
            return null;
        }
        DataSourceHelper.reset();
        return enterpriseConfigMapper.getDbServerByDbName(dbName);
    }

    @Override
    public List<String> getDistinctDbServer() {
        DataSourceHelper.reset();
        return enterpriseConfigMapper.getDistinctDbServer();
    }

    @Override
    public void updateCurrentPackageByEnterpriseId(String enterpriseId, Long packageId) {
        enterpriseConfigMapper.updateCurrentPackageByEnterpriseId(enterpriseId, packageId);
    }

    @Override
    public EnterpriseConfigDO selectByDingCorpId(String dingCorpId) {
        return enterpriseConfigMapper.selectByDingCorpId(dingCorpId);
    }
}
