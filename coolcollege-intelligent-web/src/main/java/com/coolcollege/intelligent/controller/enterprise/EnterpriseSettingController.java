package com.coolcollege.intelligent.controller.enterprise;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseOperateLogDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseSettingAppHomePageDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseThemeColorSettingsDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseLatestSyncInfoVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.service.dingSync.DingTalkClientService;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseOperateLogService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wxp
 * @date 2021-03-25 17:36
 */
@Api(tags = "企业设置")
@RestController
@RequestMapping("/v3/enterprise/{enterprise-id}/enterpriseSetting")
@BaseResponse
@Slf4j
public class EnterpriseSettingController {

    @Autowired
    private DingService dingService;

    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @Autowired
    private EnterpriseOperateLogService enterpriseOperateLogService;

    @Autowired
    private DingTalkClientService dingTalkClientService;

    @Autowired
    private RedisConstantUtil redisConstantUtil;

    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    EnterpriseSettingMapper enterpriseSettingMapper;

    /**
     * 查询企业钉钉参数配置
     *
     * @return
     */
    @GetMapping(value = "/getDingSyncSettingByEid")
    public ResponseResult getEnterpriseSettingByEid(@PathVariable("enterprise-id") String enterpriseId) {
        DataSourceHelper.reset();
        Map<String, Object> result = new HashMap<>();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        EnterpriseOperateLogDO latestSyncLog = enterpriseOperateLogService.getLatestLogByEnterpriseIdAndOptType(enterpriseId, SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC);
        EnterpriseOperateLogDO latestSuccessLog =  enterpriseOperateLogService.getLatestSuccessLog(enterpriseId, SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC, SyncConfig.SYNC_STATUS_SUCCESS);
        EnterpriseLatestSyncInfoVO enterpriseLatestSyncInfoVO = new EnterpriseLatestSyncInfoVO();
        enterpriseLatestSyncInfoVO.setLatestStatus(latestSyncLog != null ? latestSyncLog.getStatus() : null);
        enterpriseLatestSyncInfoVO.setLatestSyncSuccessEndTime(latestSuccessLog != null?latestSuccessLog.getOperateEndTime() : null);
        enterpriseLatestSyncInfoVO.setLatestFailRemark(latestSyncLog != null && (SyncConfig.SYNC_STATUS_FAIL == latestSyncLog.getStatus()) && StrUtil.isNotEmpty(latestSyncLog.getRemark()) ? latestSyncLog.getRemark() : "");

        // 是否有进行中的同步
        String eidLockKey = redisConstantUtil.getSyncEidEffectiveKey(enterpriseId);
        if (StringUtils.isNotBlank(redisUtilPool.getString(eidLockKey))) {
            enterpriseLatestSyncInfoVO.setHasLatestSync(true);
        }
        result.put("enterpriseSetting", enterpriseSettingVO);
        result.put("enterpriseLatestSyncInfo", enterpriseLatestSyncInfoVO);
        return ResponseResult.success(result);
    }


    // 获取钉钉组织架构
    @GetMapping("/getDeptList")
    public ResponseResult getDeptList(@PathVariable(value = "enterprise-id") String eid,
                                      @RequestParam(value = "deptId") Long deptId) throws ApiException {

        DataSourceHelper.reset();
        Map<String, Object> result = new HashMap<>();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String accessToken = dingService.getAccessToken(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType());
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptBaseResponseList = Lists.newArrayList();
        if(deptId == null){
            deptBaseResponseList = dingTalkClientService.getAuthDeptList(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), accessToken);
        }else {
            deptBaseResponseList = dingTalkClientService.getDeptList(deptId, accessToken);
        }
        result.put("dingDeptSubList", deptBaseResponseList);
        return ResponseResult.success(result);
    }

    // 获取钉钉组织详情
    @GetMapping("/getDeptDetail")
    public ResponseResult getDeptDetail(@PathVariable(value = "enterprise-id") String eid,
                                        @RequestParam(value = "deptId") Long deptId) throws ApiException {

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        String accessToken = dingService.getAccessToken(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType());
        deptId = (deptId == null ? 1 : deptId);
        OapiV2DepartmentListsubResponse.DeptBaseResponse deptBaseResponse =  dingTalkClientService.getDeptDetail(String.valueOf(deptId), accessToken);
        return ResponseResult.success(deptBaseResponse);
    }


    @PostMapping("/updateAppHomePagePic")
    public ResponseResult updateAppHomePagePic(@PathVariable(value = "enterprise-id") String eid,
                                               @RequestBody EnterpriseSettingAppHomePageDTO req)  {
        DataSourceHelper.reset();
        enterpriseSettingMapper.updateAppHomePagePic(eid,req.getAppHomePagePic());
        return ResponseResult.success(Boolean.TRUE);
    }

    @PostMapping("/deleteAppHomePagePic")
    public ResponseResult deleteAppHomePagePic(@PathVariable(value = "enterprise-id") String eid)  {
        DataSourceHelper.reset();
        Boolean aBoolean = enterpriseSettingService.deleteAppHomePagePic(eid);
        return ResponseResult.success(aBoolean);
    }

    @ApiOperation(value = "主题色自定义设置")
    @PostMapping("/updateThemeColorSetting")
    public ResponseResult updateThemeColorSetting(@PathVariable(value = "enterprise-id") String enterpriseId, @Validated @RequestBody EnterpriseThemeColorSettingsDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(enterpriseSettingService.updateThemeColorSetting(enterpriseId, param));
    }



}
