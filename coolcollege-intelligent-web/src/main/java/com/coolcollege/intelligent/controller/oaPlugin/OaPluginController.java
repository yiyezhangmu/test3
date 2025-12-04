package com.coolcollege.intelligent.controller.oaPlugin;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.oaPlugin.vo.OptionDataVO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.metatable.TbMetaDataColumnService;
import com.coolcollege.intelligent.service.oaPlugin.OaPluginService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * oa插件
 * @author wxp
 * @date 2023-11-29 17:26
 */
@Api(tags = "oa插件")
@ErrorHelper
@RequestMapping("/v3/enterprises/oaPlugin")
@RestController
public class OaPluginController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private TbMetaDataColumnService tbMetaDataColumnService;

    @Autowired
    private OaPluginService oaPluginService;


    @ApiOperation(value = "门店列表")
    @GetMapping("/listStore")
    public ResponseResult<List<OptionDataVO>> listStoreForOaPlugin(@RequestParam("corpid") String corpid, @RequestParam(value = "appType", required = false, defaultValue = "dingding2") String appType) {
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigService.selectByCorpId(corpid, appType);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        return ResponseResult.success(storeService.listStoreForOaPlugin(configDO.getEnterpriseId()));
    }

    @ApiOperation(value = "检查项列表")
    @GetMapping("/listMetaColumn")
    public ResponseResult<List<OptionDataVO>> listColumnForOaPlugin(@RequestParam("corpid") String corpid, @RequestParam(value = "appType", required = false, defaultValue = "dingding2") String appType) {
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigService.selectByCorpId(corpid, appType);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        return ResponseResult.success(tbMetaDataColumnService.listColumnForOaPlugin(configDO.getEnterpriseId()));
    }

    @ApiOperation(value = "初始化数据")
    @GetMapping("/initData")
    public OptionDataVO initData(@RequestParam("corpId") String corpId, @RequestParam(value = "appType", required = false, defaultValue = "dingding2") String appType) {
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigService.selectByCorpId(corpId, appType);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        return oaPluginService.initOaPluginData(configDO.getEnterpriseId());
    }

}
