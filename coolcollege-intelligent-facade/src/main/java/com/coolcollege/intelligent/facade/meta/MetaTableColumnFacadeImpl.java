package com.coolcollege.intelligent.facade.meta;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.BaseResultDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.metatable.TbMetaQuickColumnService;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhangchenbiao
 * @FileName: MetaTableColumnFacadeImpl
 * @Description:
 * @date 2023-12-04 17:17
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.META_TABLE_COLUMN_FACADE ,interfaceType = MetaTableColumnFacade.class
        , bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class MetaTableColumnFacadeImpl implements MetaTableColumnFacade{

    @Resource
    private TbMetaTableService tbMetaTableService;
    @Resource
    private TbMetaQuickColumnService tbMetaQuickColumnService;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;

    @Override
    @Async
    public BaseResultDTO updateQuickColumnUseUser(String enterpriseId) {
        log.info("更新检查项的使用人：{}", enterpriseId);
        String enterpriseDbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseDbName);
        tbMetaQuickColumnService.updateQuickColumnUseUser(enterpriseId);
        return BaseResultDTO.SuccessResult();
    }

    @Override
    @Async
    public BaseResultDTO updateMetaTableUser(String enterpriseId) {
        log.info("更新检查表相关人信息：{}", enterpriseId);
        String enterpriseDbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseDbName);
        tbMetaTableService.updateMetaTableUser(enterpriseId, null);
        return BaseResultDTO.SuccessResult();
    }
}
