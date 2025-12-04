package com.coolcollege.intelligent.facade.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.myj.MyjEnterpriseEnum;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.ListOptUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.facade.SyncUserFacade;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.*;
import com.coolcollege.intelligent.facade.sync.SyncAPI;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseOperateLogDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseOperateLogService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shenyu.common.utils.JsonUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 数据同步
 *
 * @author chenyupeng
 * @since 2021/8/17
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.SYNC_USER_FACADE_UNIQUE_ID,
        interfaceType = SyncAPI.class,
        bindings = {@SofaServiceBinding(bindingType = IntelligentFacadeConstants.SOFA_BINDING_TYPE)})
@Component
public class SyncAPIImpl implements SyncAPI {

    @Autowired
    private SyncUserFacade syncUserFacade;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private EnterpriseOperateLogService enterpriseOperateLogService;


    @Override
    public BaseResultDTO syncSingleUser(SyncRequest request) {
        try {
            syncUserFacade.syncThirdOaSingleUserAuth(request.getEnterpriseId(),request.getUserId(), ListOptUtils.longListConvertStringList(request.getUnitIdList()), request.getOrgIds());
        }catch (Exception e){
            log.error("同步权限失败，request：{},{}",request.toString(),e);
        }
        return BaseResultDTO.SuccessResult();
    }


    @Override
    public BaseResultDTO syncAll(SyncAllRequest request) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(request.getEnterpriseId());
        String requestId = MDC.get(Constants.REQUEST_ID);
        log.info("syncAll request:{}", JSONObject.toJSONString(request));
        new Thread(() -> {
            MDC.put(Constants.REQUEST_ID, requestId);
            if (MyjEnterpriseEnum.myjCompany(request.getEnterpriseId())) {
                syncUserFacade.syncMyjOrgAll(request.getEnterpriseId(), request.getUserId(), request.getUserName(), enterpriseConfigDO.getDingCorpId(),
                        enterpriseConfigDO.getDbName(), request.getUnitId(), request.getDeptList(), request.getLogId(), request.getRegionid());
            } else {
                syncUserFacade.syncThirdOaAll(request.getEnterpriseId(), request.getUserId(), request.getUserName(), enterpriseConfigDO, request.getUnitId(), request.getDeptList(), request.getLogId(), request.getRegionid(), request.getThirdDeptList());
            }
        }).start();
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO syncUserAuth(SyncAllRequest request) {
        new Thread(() -> syncUserFacade.syncUserAndAuthOa(request)).start();
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO syncSenYuSingleUser(SyncSenYuOARequest request) {
        try {
            syncUserFacade.syncSenYuSingleUserAuth(request.getEnterpriseId(),request.getThirdOaUniqueFlag(),request.getUserId(),request.getKehbmList()
            ,request.getName(), request.getRoleCode(), request.getRoleName(), request.getMobile(), request.getActive(), request.getParentThirdOaUniqueFlag());
        }catch (Exception e){
            log.error("同步森宇权限失败，request：{}",request.toString(),e);
            return BaseResultDTO.FailResult("同步森宇权限失败");
        }
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO<Long> beginSyncLog(SyncLogRequest request) {
        DataSourceHelper.reset();
        EnterpriseOperateLogDO logDO = EnterpriseOperateLogDO.builder().enterpriseId(request.getEnterpriseId()).operateDesc("第三方OA同步")
                .operateType(SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC).operateStartTime(new Date()).userName(request.getUserName()).createTime(new Date())
                .status(SyncConfig.SYNC_STATUS_ONGOING).userId(request.getUserId()).build();
        enterpriseOperateLogService.insert(logDO);
        return BaseResultDTO.SuccessResult(logDO.getId());
    }

    @Override
    public BaseResultDTO failSyncLog(SyncLogRequest request) {
        DataSourceHelper.reset();
        enterpriseOperateLogService.updateStatusAndOperateEndTimeById(SyncConfig.SYNC_STATUS_FAIL, new Date(), request.getErrMsg(), request.getLogId());
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO syncBailiUserRegion(SyncRequest request) {
        try {
            syncUserFacade.syncBailiUserRegion(request.getEnterpriseId(),request.getUserId(), request.getOrgIds());
        }catch (Exception e){
            log.error("同步权限失败，request：{},{}",request.toString(),e);
        }
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO syncMyjAll(SyncAllRequest request) {
        try {
            syncUserFacade.syncMyjAll(request.getEnterpriseId(), request.getDeptList());
        } catch (Exception e) {
            log.error("同步权限失败，request：{},{}", request.toString(), e);
        }
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO syncMyjSingleUserAuth(SyncMyjUserRequest request) {
        try {
            log.info("syncMyjSingleUserAuth同步权限，request：{}", JsonUtils.toJson(request));
            syncUserFacade.syncMyjSingleUserAuth(request.getEnterpriseId(), request);
        } catch (Exception e) {
            log.error("同步权限失败，request：{},{}", JsonUtils.toJson(request), e);
        }
        return BaseResultDTO.SuccessResult();
    }

    @Override
    public BaseResultDTO syncXfsgSingleUser(SyncXfsgOARequest request) {
        try {
            syncUserFacade.syncXfsgSingleUserAuth(request.getEnterpriseId(), request);
        }catch (Exception e){
            log.error("syncXfsgSingleUser同步权限失败，request：{},{}", JsonUtils.toJson(request), e);
        }
        return BaseResultDTO.SuccessResult();
    }
}
