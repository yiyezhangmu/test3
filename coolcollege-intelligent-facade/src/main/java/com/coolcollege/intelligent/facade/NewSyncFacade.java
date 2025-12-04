package com.coolcollege.intelligent.facade;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.convert.ConvertFactory;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.system.dao.SysRoleDao;
import com.coolcollege.intelligent.dto.AuthScopeDTO;
import com.coolcollege.intelligent.dto.SysDepartmentDTO;
import com.coolcollege.intelligent.mapper.homeTemplate.HomeTemplateRoleMappingDAO;
import com.coolcollege.intelligent.model.coolcollege.GetCoolCollegeOpenResultDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseOperateLogDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseOperateLogService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.util.RedisConstantUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/7/28 16:05
 * @Version 1.0
 */
@Service
@Slf4j
public class NewSyncFacade {

    @Autowired
    SyncDeptFacade syncDeptFacade;

    @Autowired
    SyncUserFacade syncUserFacade;

    @Autowired
    SyncRoleFacade syncRoleFacade;

    @Autowired
    EnterpriseOperateLogService enterpriseOperateLogService;

    @Autowired
    private RedisUtilPool redisUtilPool;

    @Autowired
    private RedisConstantUtil redisConstantUtil;

    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    EnterpriseSettingService enterpriseSettingService;

    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;

    @Autowired
    CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;

    @Autowired
    private SimpleMessageService simpleMessageService;
    @Autowired
    private DingDeptSyncService dingDeptSyncService;
    @Resource
    private SysRoleDao sysRoleDao;
    @Resource
    private HomeTemplateRoleMappingDAO homeTemplateRoleMappingDAO;
    @Autowired
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;
    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private ConvertFactory convertFactory;
    @Resource
    private EnterpriseMapper enterpriseMapper;
    @Resource
    private RegionMapper regionMapper;

    public void syncDeptAndUser(String eid, String userName, String userId, Long regionId){
        long start = System.currentTimeMillis();
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        EnterpriseDO enterprise = enterpriseMapper.selectById(eid);
        Boolean isDingType = AppTypeEnum.isDingType(enterpriseConfigDO.getAppType());
        EnterpriseOperateLogDO logDO = EnterpriseOperateLogDO.builder().enterpriseId(eid).operateDesc(isDingType ? "钉钉同步" : "企业微信同步")
                .operateType(SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC).operateStartTime(new Date()).userName(userName).createTime(new Date())
                .status(SyncConfig.SYNC_STATUS_ONGOING).userId(userId).build();
        enterpriseOperateLogService.insert(logDO);
        EnterpriseSettingVO enterpriseSetting = enterpriseSettingService.getEnterpriseSettingVOByEid(eid);
        //是否开通钉钉/企微同步 没有开通false 开通true
        boolean isOpenSync = Objects.equals(enterpriseSetting.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN);
        //记录失败节点
        String stageFailRemark = SyncConfig.SYNC_STAGE_DEPT_NODE;
        try {
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
            String syncDeptId =null; RegionDO region = null;
            if(Objects.isNull(regionId)){
                region = regionMapper.getByRegionId(eid, Constants.ROOT_DEPT_ID);
                syncDeptId = Constants.ROOT_DEPT_ID_STR;
            }else{
                region = regionMapper.getByRegionId(eid, regionId);
                syncDeptId = region.getSynDingDeptId();
            }
            if(Objects.isNull(region)){
                throw new ServiceException(ErrorCodeEnum.SYNC_REGION_NOT_EXIST);
            }
            //首先全量同步一次授权部门
            syncDeptFacade.syncDept(enterpriseConfigDO, enterprise, syncDeptId, logDO.getId());
            log.info("同步组织架构 部门耗时:{}", DateUtils.formatBetween(System.currentTimeMillis() - start));
            if (isOpenSync){
                long startTime = System.currentTimeMillis();
                syncDeptFacade.syncRegionAndStore(eid, region, enterpriseSetting, logDO.getId());
                log.info("同步组织架构 区域门店耗时:{}", DateUtils.formatBetween(System.currentTimeMillis() - startTime));
                stageFailRemark = SyncConfig.SYNC_STAGE_ROLE_NODE;
                if (isDingType) {
                    //2.同步角色
                    startTime = System.currentTimeMillis();
                    syncRoleFacade.syncDingRoles(eid);
                    log.info("同步组织架构 角色耗时:{}", DateUtils.formatBetween(System.currentTimeMillis() - startTime));
                }
            }
            //2.同步用户职位
            stageFailRemark = SyncConfig.SYNC_STAGE_USER_NODE;
            long startTime = System.currentTimeMillis();
            syncUserFacade.syncSpecifyNodeUser(eid,regionId, true, enterpriseConfigDO, enterpriseSetting);
            log.info("同步组织架构 人员耗时:{}", DateUtils.formatBetween(System.currentTimeMillis() - startTime));
            //成功
            DataSourceHelper.reset();
            stageFailRemark = SyncConfig.SYNC_STAGE_SUCCESS_NODE;
            //开始同步业培一体数据
            syncToCoolCollege(enterpriseConfigDO, enterpriseSetting, regionId);
            enterpriseOperateLogService.updateStageStatusById(SyncConfig.SYNC_STATUS_SUCCESS, new Date(), "同步成功",stageFailRemark, logDO.getId());
            if(Objects.nonNull(enterpriseSetting.getIsDeleteNoUserRole()) && enterpriseSetting.getIsDeleteNoUserRole()){
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                //删除没有人员的角色
                List<Long> noUserSyncRoleIds = sysRoleDao.getNoUserSyncRoleIds(eid);
                sysRoleDao.deleteByRoleIds(eid, noUserSyncRoleIds);
                homeTemplateRoleMappingDAO.deletedByRoleIds(eid, noUserSyncRoleIds);
            }
            log.info("同步组织架构 总共耗时:{}", DateUtils.formatBetween(System.currentTimeMillis() - start));
        }catch (Exception e){
            String errMsg = null;
            if(e instanceof ServiceException){
                errMsg = ((ServiceException) e).getErrorMessage();
            }
            if(e instanceof DuplicateKeyException){
                errMsg = "DuplicateKeyException ";
            }
            if(e instanceof ApiException){
                errMsg = ((ApiException) e).getErrMsg();
            }
            //同步失败删除拦截key
            redisUtilPool.delKey(redisConstantUtil.getSyncEidEffectiveKey(eid));
            log.error("同步失败 eid:{}, appType:{}, {}", eid, enterpriseConfigDO.getAppType(), e);
            //失败
            DataSourceHelper.reset();
            enterpriseOperateLogService.updateStageStatusById(SyncConfig.SYNC_STATUS_FAIL, new Date(), errMsg,stageFailRemark, logDO.getId());
        }finally {
            //无论是否失败，删除节点同步信息锁
            redisUtilPool.delKey(redisConstantUtil.getSyncLockKey(eid));
        }
    }


    /**
     * 处理数据推送到酷学院
     * @param enterpriseConfigDO
     * @param enterpriseSetting
     * @param regionId
     */
    public void syncToCoolCollege(EnterpriseConfigDO enterpriseConfigDO, EnterpriseSettingVO enterpriseSetting, Long regionId){
        if (StringUtils.isNotEmpty(enterpriseConfigDO.getCoolCollegeEnterpriseId()) && StringUtils.isNotEmpty(enterpriseConfigDO.getCoolCollegeSecret())) {
            String eid = enterpriseConfigDO.getEnterpriseId();
            Boolean isCoolCollege = AppTypeEnum.isCoolCollege(enterpriseConfigDO.getAppType());
            //如果是酷店掌并且是开通了业培一体的业务  在进行数据的推送
            if (!isCoolCollege && enterpriseSetting.getAccessCoolCollege()) {
                log.info("start_sync_coolcollege appType:{},dingCorpId:{},eid:{}",enterpriseConfigDO.getAppType(), enterpriseConfigDO.getDingCorpId(), eid);
                executor.execute(() -> {
                    //推送部门
                    coolCollegeIntegrationApiService.sendDepartmentsToCoolCollege(eid, Collections.emptyList(), regionId);
                    //推送职位
                    coolCollegeIntegrationApiService.sendPositionsToCoolCollege(eid, Collections.emptyList());
                    //推送人员 延迟推送 延迟时间2分钟
                    GetCoolCollegeOpenResultDTO resultDTO = new GetCoolCollegeOpenResultDTO(enterpriseConfigDO.getAppType(), enterpriseConfigDO.getDingCorpId(), eid, regionId);
                    simpleMessageService.send(JSONObject.toJSONString(resultDTO), RocketMqTagEnum.COLLEGE_SYNC_USER_DELAY, System.currentTimeMillis() + 2 * 60 * 1000);
                });
            }
        }
    }

}
