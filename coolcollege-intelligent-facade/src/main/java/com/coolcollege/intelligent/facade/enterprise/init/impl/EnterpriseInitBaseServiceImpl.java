package com.coolcollege.intelligent.facade.enterprise.init.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.sync.vo.AddressBookChangeReqBody;
import com.coolcollege.intelligent.common.sync.vo.EnterpriseOpenMsg;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dto.SysDepartmentDTO;
import com.coolcollege.intelligent.facade.enterprise.init.*;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseInitDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolstore.base.utils.CommonContextUtil;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author xuanfeng
 */
@Component
@Slf4j
public class EnterpriseInitBaseServiceImpl implements EnterpriseInitService {

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;
    @Autowired
    private SimpleMessageService simpleMessageService;
    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;
    @Autowired
    private RegionMapper regionMapper;


    public EnterpriseInitBaseService getThirdPartyBaseService(AppTypeEnum appType) {
        if(appType.getValue().equals(AppTypeEnum.DING_DING.getValue())){
            return CommonContextUtil.getBean(DingEnterpriseInitService.class);
        }
        if(appType.getValue().equals(AppTypeEnum.DING_DING2.getValue())){
            return CommonContextUtil.getBean(DingEnterpriseInitService.class);
        }
        if(appType.getValue().equals(AppTypeEnum.WX_APP.getValue())){
            return CommonContextUtil.getBean(QwEnterpriseInitService.class);
        }
        if(appType.getValue().equals(AppTypeEnum.WX_APP2.getValue())){
            return CommonContextUtil.getBean(QwEnterpriseInitService.class);
        }
        if(appType.getValue().equals(AppTypeEnum.WX_SELF_DKF.getValue())){
            return CommonContextUtil.getBean(QwEnterpriseInitService.class);
        }
        if(appType.getValue().equals(AppTypeEnum.APP.getValue())){
            return CommonContextUtil.getBean(AppEnterpriseInitService.class);
        }
        if(appType.getValue().equals(AppTypeEnum.DASHANG_APP.getValue())){
            return CommonContextUtil.getBean(DaShangAppEnterpriseInitService.class);
        }
        if(AppTypeEnum.ONE_PARTY_APP.equals(appType)) {
            return CommonContextUtil.getBean(DingOnePartyEnterpriseInitService.class);
        }
        if(AppTypeEnum.FEI_SHU.equals(appType)) {
            return CommonContextUtil.getBean(FeiShuEnterpriseInitService.class);
        }
        if(AppTypeEnum.ONE_PARTY_APP2.equals(appType)) {
            return CommonContextUtil.getBean(DingEnterpriseInitService.class);
        }
        return null;
    }

    @Override
    public void enterpriseInit(String cropId, AppTypeEnum appType, String eid, String dbName, String openUserId) {
        getThirdPartyBaseService(appType).enterpriseInit(cropId, eid, appType.getValue(), dbName, openUserId);
    }

    @Override
    public void enterpriseInitDeptOrder(String cropId, AppTypeEnum appType, String eid, String dbName, List<String> deptIds) {
        if (CollectionUtils.isEmpty(deptIds)) {
            return;
        }
        //切库
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<SysDepartmentDO> sysDepartmentDOS = new ArrayList<>();
        List<RegionDO> regionDOS = new ArrayList<>();
        for (String deptId : deptIds) {
            //获取部门的详情
            try {
                SysDepartmentDTO departmentDetail = enterpriseInitConfigApiService.getDepartmentDetail(cropId, deptId, appType.getValue());
                if (Objects.nonNull(departmentDetail)) {
                    SysDepartmentDO sysDepartmentDO = new SysDepartmentDO();
                    sysDepartmentDO.setId(departmentDetail.getId());
                    sysDepartmentDO.setDepartOrder(departmentDetail.getDepartOrder());
                    sysDepartmentDOS.add(sysDepartmentDO);
                    RegionDO regionDO = new RegionDO();
                    regionDO.setSynDingDeptId(String.valueOf(departmentDetail.getId()));
                    regionDO.setOrderNum(departmentDetail.getDepartOrder());
                    regionDOS.add(regionDO);
                    if (sysDepartmentDOS.size() > SyncConfig.DEFAULT_BATCH_MAX_SIZE) {
                        //更新部门和区域中的顺序值
                        sysDepartmentMapper.batchUpdateDeptOrder(eid, sysDepartmentDOS);
                        regionMapper.batchUpdateOrder(eid, regionDOS);
                        sysDepartmentDOS.clear();
                        regionDOS.clear();
                    }
                }
            } catch (ApiException e) {
                log.error("enterpriseInitDeptOrder error", e);
                throw new ServiceException(ErrorCodeEnum.DING_SERVICE_EXCEPTION);
            }
        }
        if (CollectionUtils.isNotEmpty(sysDepartmentDOS)) {
            sysDepartmentMapper.batchUpdateDeptOrder(eid, sysDepartmentDOS);
            regionMapper.batchUpdateOrder(eid, regionDOS);
        }
    }

    @Override
    public void sendBossMessage(String cropId, AppTypeEnum appType) {
        getThirdPartyBaseService(appType).sendBossMessage(cropId, appType.getValue());
    }

    @Override
    public void runEnterpriseScript(EnterpriseOpenMsg msg) {
        getThirdPartyBaseService(AppTypeEnum.getAppType(msg.getAppType())).runEnterpriseScript(msg);
        //抛出开始数据同步消息
        EnterpriseInitDTO enterpriseInitDTO = new EnterpriseInitDTO();
        enterpriseInitDTO.setEid(msg.getEid());
        enterpriseInitDTO.setAppType(msg.getAppType());
        enterpriseInitDTO.setCorpId(msg.getCorpId());
        enterpriseInitDTO.setDbName(msg.getDbName());
        enterpriseInitDTO.setUserId(msg.getAuthUserId());
        log.info("send msg to enterprise_open_data_sync, eid:{}, appType:{}, corpId:{}, dbName:{}, authUserId:{}", msg.getEid(), msg.getAppType(), msg.getCorpId(), msg.getDbName(), msg.getAuthUserId());
        simpleMessageService.send(JSONObject.toJSONString(enterpriseInitDTO), RocketMqTagEnum.ENTERPRISE_OPEN_DATA_SYNC);
    }

    @Override
    public void enterpriseInitDepartment(String corpId, String eid, AppTypeEnum appType, String dbName) {
        getThirdPartyBaseService(appType).enterpriseInitDepartment(corpId, eid, appType.getValue(), dbName);
    }

    @Override
    public void enterpriseInitUser(String corpId, String eid, AppTypeEnum appType, String dbName, Boolean isScopeChange) {
        getThirdPartyBaseService(appType).enterpriseInitUser(corpId, eid, appType.getValue(), dbName, isScopeChange);
    }

    @Override
    public void onlySyncUser(String corpId, String eid, AppTypeEnum appType, String dbName) {
        getThirdPartyBaseService(appType).onlySyncUser(corpId, eid, appType.getValue(), dbName);
    }

    @Override
    public void userUpdateEvent(AddressBookChangeReqBody param) {
        getThirdPartyBaseService(AppTypeEnum.getAppType(param.getAppType())).userUpdateEvent(param);
    }

    @Override
    public void sendOpenSucceededMsg(String corpId, String appType, List<String> userList) {
        if(CollectionUtils.isEmpty(userList)){
            return;
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userList", userList);
        jsonObject.put("appType", appType);
        jsonObject.put("corpId", corpId);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.OPEN_SUCCEEDED_MSG_QUEUE);
    }
}
