package com.coolcollege.intelligent.controller.system.sysconf;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enums.DictTypeEnum;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.system.sysconf.EnterpriseDictMappingService;
import com.coolcollege.intelligent.service.system.sysconf.SysDictService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shuchang.wei
 * @date 2021/3/27 15:18
 */
@RestController
@RequestMapping("/v3/system/sysconf/sysConfig")
@BaseResponse
public class SysConfigController {
    @Resource
    private SysDictService sysDictService;
    @Resource
    private EnterpriseDictMappingService enterpriseDictMappingService;
    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    private EnterpriseConfigService enterpriseConfigService;
    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @GetMapping("getSysConfig")
    public ResponseResult getSysConfig(@RequestParam(value = "enterpriseId",required = false) String enterpriseId,
                                       @RequestParam(value = "dingCorpId",required = false) String dingCorpId,
                                       @RequestParam(value = "appType", required = false, defaultValue = "dingding") String appType){
        DataSourceHelper.reset();
        if(StringUtils.isNotBlank(dingCorpId)){
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(dingCorpId, appType);
            enterpriseId = enterpriseConfigDO == null? null:enterpriseConfigDO.getEnterpriseId();
        }
        Map result = new HashMap(8);
        //默认模块名称自定义字典
        result.put("modelDefineDictList",sysDictService.listSysDict(DictTypeEnum.MODEL_NAME_DEFINE.getCode(),null));

        if(StringUtils.isNotBlank(enterpriseId)){
            //企业模块名称自定义字典映射
            result.put("modelDefineMappingList",enterpriseDictMappingService.listDictMapping(enterpriseId,null,null, DictTypeEnum.MODEL_NAME_DEFINE.getCode()));
            //企业巡店设置
            result.put("storeCheckSetting",enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId));
            //企业设置
            result.put("enterpriseSetting",enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId));
        }
        return ResponseResult.success(result);
    }
}
