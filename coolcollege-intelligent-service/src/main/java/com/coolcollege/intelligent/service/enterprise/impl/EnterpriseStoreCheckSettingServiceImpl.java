package com.coolcollege.intelligent.service.enterprise.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseStoreCheckDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseStoreCheckNewDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseStoreCheckRequestDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseStoreCheckRequestNewDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterprisePatrolBiosVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterprisePatrolCheckResultVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterprisePatrolLevelVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseStoreCheckSettingVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.VO.SysRoleBaseVO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.util.ParamFormatUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("enterpriseStoreCheckSettingService")
@Slf4j
public class EnterpriseStoreCheckSettingServiceImpl implements EnterpriseStoreCheckSettingService {

    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private RedisUtilPool redisUtilPool;

    private final Integer TIME = 60 * 60;

    @Resource
    private EnterpriseMapper enterpriseMapper;


    @Override
    public EnterpriseStoreCheckSettingVO queryEnterpriseStoreCheckSettingVO(String eid) {
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSetting = enterpriseMapper.getEnterpriseSetting(eid);
        EnterpriseStoreCheckSettingVO enterpriseStoreCheckSettingVO = getEnterpriseStoreCheckSettingVO(eid);
        enterpriseStoreCheckSettingVO.setPatrolWaterMark(enterpriseSetting.getPhotoWatermark());
        return enterpriseStoreCheckSettingVO;
    }

    private EnterpriseStoreCheckSettingVO getEnterpriseStoreCheckSettingVO(String eid) {
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        EnterpriseStoreCheckSettingVO vo = mapEnterpriseStoreCheckSettingVO(enterpriseStoreCheckSetting);
        //填充角色信息
        if (StringUtils.isBlank(enterpriseStoreCheckSetting.getB1RoleScope()) && StringUtils.isBlank(enterpriseStoreCheckSetting.getGpsRoleScope())) {
            return vo;
        }
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<String> b1RoleIdList = StrUtil.splitTrim(enterpriseStoreCheckSetting.getB1RoleScope(), ",");
        List<String> gpsRoleIdList = StrUtil.splitTrim(enterpriseStoreCheckSetting.getGpsRoleScope(), ",");

        List<Long> allRoleIdList = ListUtils.union(b1RoleIdList, gpsRoleIdList).stream()
                .distinct()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(allRoleIdList)) {
            return vo;
        }
        List<SysRoleDO> roleByRoleIds = sysRoleMapper.getRoleByRoleIds(eid, allRoleIdList);
        if (CollectionUtils.isEmpty(roleByRoleIds)) {
            return vo;
        }
        Map<String, SysRoleDO> sysRoleMap = ListUtils.emptyIfNull(roleByRoleIds)
                .stream()
                .collect(Collectors.toMap(data -> data.getId().toString(), data -> data, (a, b) -> a));
        List<SysRoleBaseVO> b1RoleBaseList = ListUtils.emptyIfNull(b1RoleIdList)
                .stream()
                .map(data -> mapSysRoleBaseVO(sysRoleMap, data))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<SysRoleBaseVO> gpsRoleBaseList = ListUtils.emptyIfNull(gpsRoleIdList)
                .stream()
                .map(data -> mapSysRoleBaseVO(sysRoleMap, data))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        vo.setB1RoleScopeList(b1RoleBaseList);
        vo.setGpsRoleScopeList(gpsRoleBaseList);
        return vo;
    }

    private EnterpriseStoreCheckSettingVO mapEnterpriseStoreCheckSettingVO(EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting) {
        EnterpriseStoreCheckSettingVO vo = new EnterpriseStoreCheckSettingVO();
        vo.setEnterpriseId(enterpriseStoreCheckSetting.getEnterpriseId());
        vo.setLocationRange(enterpriseStoreCheckSetting.getLocationRange());
        vo.setLocationMethod(enterpriseStoreCheckSetting.getLocationMethod());
        vo.setSignInError(enterpriseStoreCheckSetting.getSignInError());
        vo.setSignOutError(enterpriseStoreCheckSetting.getSignOutError());
        vo.setStoreCheckTime(enterpriseStoreCheckSetting.getStoreCheckTime());
        vo.setDefaultCheckTime(enterpriseStoreCheckSetting.getDefaultCheckTime());
        vo.setLocationErrorCommit(enterpriseStoreCheckSetting.getLocationErrorCommit());
        vo.setUploadLocalImg(enterpriseStoreCheckSetting.getUploadLocalImg());
        vo.setCustomizeGrade(enterpriseStoreCheckSetting.getCustomizeGrade());
        vo.setAutoSendProblem(enterpriseStoreCheckSetting.getAutoSendProblem());
        vo.setNotifySupervisor(enterpriseStoreCheckSetting.getNotifySupervisor());
        vo.setPatrolDay(enterpriseStoreCheckSetting.getPatrolDay());
        vo.setPatrolPosition(enterpriseStoreCheckSetting.getPatrolPosition());
        vo.setPatrolOpen(enterpriseStoreCheckSetting.getPatrolOpen());
        vo.setOverdueTaskContinue(enterpriseStoreCheckSetting.getOverdueTaskContinue());
        vo.setTaskRemind(enterpriseStoreCheckSetting.getTaskRemind());
        vo.setProblemTickRemind(enterpriseStoreCheckSetting.getProblemTickRemind());
        vo.setContinuePatrol(enterpriseStoreCheckSetting.getContinuePatrol());
        vo.setUploadImgNeed(enterpriseStoreCheckSetting.getUploadImgNeed());
        vo.setAutonomyOpenSummary(enterpriseStoreCheckSetting.getAutonomyOpenSummary());
        vo.setAutonomyOpenSignature(enterpriseStoreCheckSetting.getAutonomyOpenSignature());
        vo.setOpenSubmitFirst(enterpriseStoreCheckSetting.getOpenSubmitFirst());
        vo.setTaskCcRemind(enterpriseStoreCheckSetting.getTaskCcRemind());
        vo.setTaskQuestionValidday(enterpriseStoreCheckSetting.getTaskQuestionValidday());
        vo.setPatrolOpenScheduleId(enterpriseStoreCheckSetting.getPatrolOpenScheduleId());
        vo.setLevelInfo(enterpriseStoreCheckSetting.getCheckResultInfo());
        vo.setCheckResultInfo(enterpriseStoreCheckSetting.getCheckResultInfo());
        vo.setCreateTime(enterpriseStoreCheckSetting.getCreateTime());
        vo.setCreateUserId(enterpriseStoreCheckSetting.getCreateUserId());
        vo.setUpdateTime(enterpriseStoreCheckSetting.getUpdateTime());
        vo.setUpdateUserId(enterpriseStoreCheckSetting.getUpdateUserId());
        vo.setHandlerOvertimeTaskContinue(enterpriseStoreCheckSetting.getHandlerOvertimeTaskContinue());
        vo.setApproveOvertimeTaskContinue(enterpriseStoreCheckSetting.getApproveOvertimeTaskContinue());
        vo.setOverdueDisplay(enterpriseStoreCheckSetting.getOverdueDisplay());
        vo.setCheckPriority(enterpriseStoreCheckSetting.getCheckPriority());
        vo.setPatrolRecheck(enterpriseStoreCheckSetting.getPatrolRecheck());
        vo.setPatrolRecheckSendProblem(enterpriseStoreCheckSetting.getPatrolRecheckSendProblem());
        vo.setSelfGuidedStoreCCRules(enterpriseStoreCheckSetting.getSelfGuidedStoreCCRules());
        vo.setVideoPatrolStoreCCRules(enterpriseStoreCheckSetting.getVideoPatrolStoreCCRules());
        vo.setUploadSignInOutImg(enterpriseStoreCheckSetting.getUploadSignInOutImg());
        vo.setExtendField(enterpriseStoreCheckSetting.getExtendField());
        vo.setPatrolSkipApproval(enterpriseStoreCheckSetting.getPatrolSkipApproval());
        return vo;
    }

    private SysRoleBaseVO mapSysRoleBaseVO(Map<String, SysRoleDO> sysRoleMap, String data) {
        SysRoleDO sysRoleDO = sysRoleMap.get(data);
        if (sysRoleDO != null) {
            SysRoleBaseVO roleBaseVO = new SysRoleBaseVO();
            roleBaseVO.setId(sysRoleDO.getId());
            roleBaseVO.setRoleName(sysRoleDO.getRoleName());
            roleBaseVO.setSource(sysRoleDO.getSource());
            roleBaseVO.setPositionType(sysRoleDO.getPositionType());
            return roleBaseVO;
        } else {
            return null;
        }
    }

    @Override
    public EnterpriseStoreCheckSettingDO getEnterpriseStoreCheckSetting(String eid) {
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO storeCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        return storeCheckSettingDO;
    }


    @Override
    public Boolean saveOrUpdateStoreCheckSetting(String eid, EnterpriseStoreCheckDTO entity) {
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        List<String> fieldNameList = entity.getFieldNameList();
        for (String fieldName : fieldNameList) {
            try {
                fieldName = ParamFormatUtil.UnderlineToHump(fieldName);
                Field field2 = EnterpriseStoreCheckSettingDO.class.getDeclaredField(fieldName);
                Field field = EnterpriseStoreCheckRequestDTO.class.getDeclaredField(fieldName);
                if (field == null || field2 == null) {
                    continue;
                }
                field2.setAccessible(true);
                field.setAccessible(true);
                field2.set(enterpriseStoreCheckSettingDO, field.get(entity.getStoreCheckSetting()));

            } catch (Exception e) {
                log.error("保存巡店参数出错,{}", e);
                return false;
            }
        }
        DataSourceHelper.reset();
        enterpriseStoreCheckSettingMapper.insertOrUpdate(eid, enterpriseStoreCheckSettingDO);
        redisUtilPool.delKey(getStoreCheckVOKey(eid));
        redisUtilPool.delKey(getStoreCheckDOKey(eid));
        return Boolean.TRUE;
    }

    @Override
    public Boolean saveOrUpdateStoreCheckSettingNew(String eid, EnterpriseStoreCheckNewDTO entity) {
        if (Objects.nonNull(entity.getStoreCheckSetting().getPatrolWaterMark())) {
            EnterpriseSettingDO enterpriseSetting = enterpriseMapper.getEnterpriseSetting(eid);
            enterpriseSetting.setPhotoWatermark(entity.getStoreCheckSetting().getPatrolWaterMark());
            enterpriseMapper.saveOrUpdateSettings(eid,enterpriseSetting);
            return true;
        }
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        List<String> fieldNameList = entity.getFieldNameList();

        for (String fieldName : fieldNameList) {
            try {
                Field field = EnterpriseStoreCheckRequestNewDTO.class.getDeclaredField(fieldName);
                Field field2 = EnterpriseStoreCheckSettingDO.class.getDeclaredField(fieldName);
                if(field==null||field2==null){
                    continue;
                }
                field.setAccessible(true);
                field2.setAccessible(true);
                if (field.getName().equals("selfGuidedStoreCCRules")){
                    String selfGuidedStoreCCRules = JSONObject.toJSONString(entity.getStoreCheckSetting().getSelfGuidedStoreCCRules());
                    field2.set(enterpriseStoreCheckSettingDO, selfGuidedStoreCCRules);
                }else if (field.getName().equals("videoPatrolStoreCCRules")){
                    String videoPatrolStoreCCRules = JSONObject.toJSONString(entity.getStoreCheckSetting().getVideoPatrolStoreCCRules());
                    field2.set(enterpriseStoreCheckSettingDO, videoPatrolStoreCCRules);
                }else {
                    field2.set(enterpriseStoreCheckSettingDO, field.get(entity.getStoreCheckSetting()));
                }
            } catch (Exception e) {
                log.error("保存巡店参数出错,{}", e);
                return false;
            }
        }
        DataSourceHelper.reset();
        enterpriseStoreCheckSettingMapper.insertOrUpdate(eid, enterpriseStoreCheckSettingDO);
        redisUtilPool.delKey(getStoreCheckVOKey(eid));
        redisUtilPool.delKey(getStoreCheckDOKey(eid));
        return Boolean.TRUE;
    }


    @Override
    public EnterprisePatrolBiosVO getPatrolBiosInfo(String eid) {
        Map<String, String> patrolBiosInfo = enterpriseStoreCheckSettingMapper.getPatrolBiosInfo(eid);
        String levelInfo = patrolBiosInfo.get("levelInfo");
        EnterprisePatrolLevelVO levelVO = JSON.parseObject(levelInfo, EnterprisePatrolLevelVO.class);
        String checkResultInfo = patrolBiosInfo.get("checkResultInfo");
        EnterprisePatrolCheckResultVO checkResultVO = JSON.parseObject(checkResultInfo, EnterprisePatrolCheckResultVO.class);

        return new EnterprisePatrolBiosVO(checkResultVO, levelVO);
    }

    @Override
    public EnterprisePatrolLevelVO getStoreCheckLevel(String eid) {
        String levelInfo = enterpriseStoreCheckSettingMapper.getLevelInfo(eid);
        return JSON.parseObject(levelInfo, EnterprisePatrolLevelVO.class);
    }

    @Override
    public EnterprisePatrolCheckResultVO getStoreCheckResult(String eid) {
        String levelInfo = enterpriseStoreCheckSettingMapper.getCheckResult(eid);
        return JSON.parseObject(levelInfo, EnterprisePatrolCheckResultVO.class);
    }

    @Override
    public Boolean updateStoreBios(String eid, EnterprisePatrolBiosVO bios) {
//        List<EnterprisePatrolLevelDTO> list = patrolLevelVO.getList();
        enterpriseStoreCheckSettingMapper.updateLevelInfo(eid, JSON.toJSONString(bios.getLevelInfo()), JSON.toJSONString(bios.getCheckResultInfo()));
        return Boolean.TRUE;
    }

    private String getStoreCheckVOKey(String eid) {
        return Constants.STORE_CHECK_SETTING_VO + eid;
    }

    private String getStoreCheckDOKey(String eid) {
        return Constants.STORE_CHECK_SETTING_DO + eid;
    }
}
