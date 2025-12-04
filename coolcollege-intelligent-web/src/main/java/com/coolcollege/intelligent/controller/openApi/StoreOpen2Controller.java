package com.coolcollege.intelligent.controller.openApi;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.facade.SyncDeptFacade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.openApi.request.SyncStoreRequest;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/open/store")
public class StoreOpen2Controller {

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private SyncDeptFacade syncDeptFacade;

    @ApiOperation("二开项目同步门店")
    @RequestMapping("/syncSingleStore")
    public ResponseResult syncSingleStore(@RequestBody @Validated SyncStoreRequest request) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(request.getEnterpriseId());
        if(enterpriseConfig == null){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        return ResponseResult.success(syncDeptFacade.open2SyncSingleStore(enterpriseConfig, request));
    }

}
