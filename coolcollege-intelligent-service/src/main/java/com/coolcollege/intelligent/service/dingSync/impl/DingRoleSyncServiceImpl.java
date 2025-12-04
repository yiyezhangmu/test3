package com.coolcollege.intelligent.service.dingSync.impl;

import com.alipay.remoting.util.StringUtils;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.ak.AkEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataType;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.common.enums.role.OnePartySyncRoleEnum;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dto.OpRoleDTO;
import com.coolcollege.intelligent.mapper.homeTemplate.HomeTemplateRoleMappingDAO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateRoleMappingDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.dingSync.DingRoleSyncService;
import com.coolcollege.intelligent.service.homeTemplate.HomeTemplateRoleMappingService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.dingtalk.api.response.OapiRoleListResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 钉钉角色同步实现类
 *
 * @ClassName: DingRoleSyncServiceImpl
 * @Author: xugangkun
 * @Date: 2021/3/23 10:15
 */
@Slf4j
@Service
public class DingRoleSyncServiceImpl implements DingRoleSyncService {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Autowired
    HomeTemplateRoleMappingDAO homeTemplateRoleMappingDAO;

    @Autowired
    HomeTemplateRoleMappingService homeTemplateRoleMappingService;

    /**
     * 同步企业角色信息
     * @param eid 钉钉的企业标识
     * @param openRoleList
     * @return: void
     * @Author: xugangkun
     * @Date: 2021/3/22 15:29
     */
    @Override
    public void syncDingRoles(String eid, List<OapiRoleListResponse.OpenRoleGroup> openRoleList,EnterpriseSettingVO setting) {
        //获得数智门店以同步的钉钉角色列表
        List<SysRoleDO> coolRoleList = sysRoleMapper.selectSysRoleBySource(eid, PositionSourceEnum.SYNC.getValue());
        AtomicInteger maxSort = new AtomicInteger(sysRoleService.getNormalRoleMaxPriority(eid));
        //钉钉角色列表:转化钉钉反回实体为门店角色实体
        List<SysRoleDO> dingRoleList = new ArrayList<>();
        String dingSyncRoleRuleDetail = setting.getDingSyncRoleRuleDetail();
        //转化为DO对象
        openRoleList.forEach(openRoleGroup -> {
            List<OapiRoleListResponse.OpenRole> openRoles = Optional.ofNullable(openRoleGroup.getRoles()).orElseGet(ArrayList::new);
            List<SysRoleDO> sysRoles = openRoles.stream()
                    .filter(role -> !SyncConfig.checkSubManage(role.getName()))
                    .filter(role -> SyncConfig.checkRoleRule(role.getName(),dingSyncRoleRuleDetail))
                    .map(m -> initSysRoleDO(m))
                    .collect(Collectors.toList());
            dingRoleList.addAll(sysRoles);
        });
        //钉钉角色id-实体map
        Map<Long, SysRoleDO> dingRoleMap = dingRoleList.stream()
                .collect(Collectors.toMap(SysRoleDO::getSynDingRoleId, data -> data, (a, b) -> a));
        //cool角色id-实体map
        Map<Long, SysRoleDO> coolRoleMap = coolRoleList.stream()
                .collect(Collectors.toMap(SysRoleDO::getSynDingRoleId, data -> data, (a, b) -> a));
        //2.对钉钉列表进行处理：id已存在，但是名称发生了改变，就更新名称，或者新增
        dingRoleMap.forEach((k, v) -> {
            //当coolRoleMap中无该key时，代表新增，添加对象至新增列表
            SysRoleDO coolRole = coolRoleMap.get(k);
            if (coolRole == null) {
//                maxSort.incrementAndGet();
                maxSort.getAndAdd(10);
                v.setPriority(maxSort.get());
                v.setCreateTime(new Date());
                v.setCreateUser(AIEnum.AI_USERID.getCode());
                sysRoleMapper.addSystemRole(eid, v);
                //给角色添加默认的模板
                HomeTemplateRoleMappingDO homeTemplateRoleMappingDO = homeTemplateRoleMappingService.initHomeTempRoleMapping(v.getId(), v.getPositionType());
                homeTemplateRoleMappingDAO.batchInsert(eid,Arrays.asList(homeTemplateRoleMappingDO));
            } else {
                //不是空，代表已存在，对比菜单名称是否相等，不相等，添加到更新队列
                if (!coolRole.getRoleName().equals(v.getRoleName())) {
                    coolRole.setRoleName(v.getRoleName());
                    v.setUpdateUser(AIEnum.AI_USERID.getCode());
                    v.setUpdateTime(new Date());
                    sysRoleMapper.updateRole(eid, coolRole);
                }
                //如果当前角色的排序值为Null，设置
                if (coolRole.getPriority() == null || coolRole.getPriority() == 0) {
//                    maxSort.incrementAndGet();
                    maxSort.getAndAdd(10);
                    coolRole.setPriority(maxSort.get());
                    String positionType = coolRole.getPositionType() == null ? CoolPositionTypeEnum.STORE_OUTSIDE.getCode() : coolRole.getPositionType();
                    coolRole.setPositionType(positionType);
                    v.setUpdateUser(AIEnum.AI_USERID.getCode());
                    v.setUpdateTime(new Date());
                    sysRoleMapper.updateRole(eid, coolRole);
                }
                //删除已处理角色
                coolRoleList.remove(coolRole);
            }
        });
        coolRoleList.forEach(deleteRole -> {
            if(StringUtils.isBlank(deleteRole.getRoleEnum())){
                sysRoleMapper.deleteRoles(eid, deleteRole.getId());
            }
            //删除角色映射信息
            enterpriseUserRoleMapper.deleteByRoleId(eid, deleteRole.getId().toString());
        });
        //删除无用的角色模板映射
        List<Long> roleIds = coolRoleList.stream().filter(x->StringUtils.isBlank(x.getRoleEnum())).map(SysRoleDO::getId).collect(Collectors.toList());
        homeTemplateRoleMappingDAO.deletedByRoleIds(eid,roleIds);
        //同步到酷学院
        if (CollectionUtils.isNotEmpty(coolRoleList) && setting.getAccessCoolCollege()){
            coolCollegeIntegrationApiService.sendDelPositionsToCoolCollege(eid,coolRoleList);
        }
    }

    @Override
    public void deleteDingRole(String eid,EnterpriseSettingVO vo) {
        List<SysRoleDO> coolRoleList = sysRoleMapper.selectSysRoleBySource(eid, PositionSourceEnum.SYNC.getValue());
        coolRoleList.forEach(deleteRole -> {
            if(StringUtils.isBlank(deleteRole.getRoleEnum())) {
                sysRoleMapper.deleteRoles(eid, deleteRole.getId());
            }
            //删除角色映射信息
            enterpriseUserRoleMapper.deleteByRoleId(eid, deleteRole.getId().toString());
        });
        //删除无用的角色模板映射
        List<Long> roleIds = coolRoleList.stream().filter(x->StringUtils.isBlank(x.getRoleEnum())).map(SysRoleDO::getId).collect(Collectors.toList());
        homeTemplateRoleMappingDAO.deletedByRoleIds(eid,roleIds);
        //同步到酷学院
        if (CollectionUtils.isNotEmpty(coolRoleList)&&vo.getAccessCoolCollege()){
            coolCollegeIntegrationApiService.sendDelPositionsToCoolCollege(eid,coolRoleList);
        }
    }

    @Autowired
    CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;

    @Override
    public void deleteDingPosition(String eid,EnterpriseSettingVO vo) {
        List<SysRoleDO> coolPositionList = sysRoleMapper.selectSysRoleBySource(eid, PositionSourceEnum.SYNC_POSITION.getValue());
        coolPositionList.forEach(deleteRole -> {
            if(StringUtils.isBlank(deleteRole.getRoleEnum())){
                sysRoleMapper.deleteRoles(eid, deleteRole.getId());
            }
            //删除角色映射信息
            enterpriseUserRoleMapper.deleteByRoleId(eid, deleteRole.getId().toString());
        });
        //删除无用的角色模板映射
        List<Long> roleIds = coolPositionList.stream().filter(x->StringUtils.isBlank(x.getRoleEnum())).map(SysRoleDO::getId).collect(Collectors.toList());
        homeTemplateRoleMappingDAO.deletedByRoleIds(eid,roleIds);
        //同步到酷学院
        if (CollectionUtils.isNotEmpty(coolPositionList)&&vo.getAccessCoolCollege()){
            coolCollegeIntegrationApiService.sendDelPositionsToCoolCollege(eid,coolPositionList);
        }
    }

    private SysRoleDO initSysRoleDO(OapiRoleListResponse.OpenRole openRole) {
        SysRoleDO sysRoleDO = new SysRoleDO();
        sysRoleDO.setId(openRole.getId())
                .setRoleName(openRole.getName())
                .setRoleAuth(AuthRoleEnum.PERSONAL.getCode())
                .setIsInternal(0)
                .setSource(PositionSourceEnum.SYNC.getValue())
                .setPositionType(CoolPositionTypeEnum.STORE_OUTSIDE.getCode())
                .setSynDingRoleId(openRole.getId())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        return sysRoleDO;
    }

    @Override
    public void syncDingOnePartyRoles(String eid, List<OpRoleDTO> openRoles, Long roleId) {
        // 1.查询数智门店已同步角色列表
        List<SysRoleDO> coolRoleList = sysRoleMapper.selectBySynDingRoleIdAndSource(eid, null, roleId);
        // 钉钉角色列表（DTO转为DO）
        List<SysRoleDO> dingRoleList = openRoles.stream()
                // 如果roleId不为空，同步指定的角色
                .filter(openRole -> Objects.isNull(roleId) || openRole.getRoleId().equals(roleId))
                .map(m -> initSysRoleDO(m))
                .collect(Collectors.toList());
        // 钉钉角色id-实体map
        Map<Long, SysRoleDO> dingRoleMap = dingRoleList.stream()
                .collect(Collectors.toMap(SysRoleDO::getSynDingRoleId, data -> data, (a, b) -> a));
        // cool角色id-实体map
        Map<Long, SysRoleDO> coolRoleMap = coolRoleList.stream()
                .filter(a -> a.getSynDingRoleId() != null)
                .collect(Collectors.toMap(SysRoleDO::getSynDingRoleId, data -> data, (a, b) -> a));
        //2.遍历钉钉角色列表：id已存在，但是名称发生了改变，就更新名称，或者新增
        dingRoleMap.forEach((k, v) -> {
            // 当coolRoleMap中无该key时，新增角色，添加对象至新增列表
            SysRoleDO coolRole = coolRoleMap.get(k);
            if (coolRole == null) {
                if(AkEnterpriseEnum.aokangAffiliatedCompany(eid) && PositionSourceEnum.SYNC.getValue().equals(v.getSource()) &&  roleId != null){
                    log.info("奥康企业只监听手动创建的角色变更事件,roleId:{}", roleId);
                    return;
                }
                v.setCreateTime(new Date());
                v.setCreateUser(AIEnum.AI_USERID.getCode());
                sysRoleMapper.addSystemRole(eid, v);
                // 给新增角色初始化移动端菜单
                try {
                    sysRoleService.initMenuWhenSyncRole(eid,v.getId());
                } catch (Exception e) {
                    log.error("syncDingOnePartyRoles给新增角色初始化移动端菜单,企业id:{},角色Id:{}", eid, v.getId(), e);
                }
                //职位数据新增，推送酷学院，发送mq消息，异步操作
                coolCollegeIntegrationApiService.sendDataChangeMsg(eid, Arrays.asList(String.valueOf(v.getId())), ChangeDataOperation.ADD.getCode(), ChangeDataType.POSITION.getCode());
            } else {
                // cool角色存在，对比角色名称是否相等，不相等，添加到更新列表
                if (!coolRole.getRoleName().equals(v.getRoleName()) || !coolRole.getPriority().equals(v.getPriority())
                        || !coolRole.getSource().equals(v.getSource())) {
                    coolRole.setPriority(v.getPriority());
                    coolRole.setRoleName(v.getRoleName());
                    coolRole.setRoleEnum(v.getRoleEnum());
                    coolRole.setSource(v.getSource());
                    coolRole.setUpdateUser(AIEnum.AI_USERID.getCode());
                    coolRole.setUpdateTime(new Date());
                    sysRoleMapper.updateRole(eid, coolRole);
                    //职位数据修改，推送酷学院，发送mq消息，异步操作
                    coolCollegeIntegrationApiService.sendDataChangeMsg(eid, Arrays.asList(String.valueOf(coolRole.getId())), ChangeDataOperation.UPDATE.getCode(), ChangeDataType.POSITION.getCode());
                }
                // 将待更新的角色在cool列表中移除
                coolRoleList.remove(coolRole);
            }
        });
        // 3.遍历cool角色列表，删除未匹配到的角色
        for (SysRoleDO deleteRole : coolRoleList) {
            /*if(StringUtils.isNotBlank(deleteRole.getRoleEnum())){
                continue;
            }*/
            sysRoleMapper.deleteRoles(eid, deleteRole.getId());
            //删除角色映射信息
            enterpriseUserRoleMapper.deleteByRoleId(eid, deleteRole.getId().toString());
        }
    }

    /**
     * 初始化角色信息
     * @param openRole
     * @return
     */
    private SysRoleDO initSysRoleDO(OpRoleDTO openRole) {
        SysRoleDO sysRoleDO = new SysRoleDO();
        sysRoleDO.setId(openRole.getRoleId())
                .setRoleName(openRole.getRoleName())
                .setRoleAuth(OnePartySyncRoleEnum.isAdmin(openRole.getRoleCode()) ? AuthRoleEnum.ALL.getCode() : AuthRoleEnum.INCLUDE_SUBORDINATE.getCode())
                .setRoleEnum(OnePartySyncRoleEnum.getEnumByCode(openRole.getRoleCode()))
                .setIsInternal(0)
                .setSource(PositionSourceEnum.SYNC.getValue())
                .setPositionType(CoolPositionTypeEnum.STORE_INSIDE.getCode())
                .setSynDingRoleId(openRole.getRoleId())
                .setPriority(openRole.getLevel())
                .setCreateTime(new Date())
                .setUpdateTime(new Date());
        // 门店通返回角色来源
        if(StringUtils.isNotBlank(openRole.getSource())){
            sysRoleDO.setSource(openRole.getSource());
        }
        return sysRoleDO;
    }
}
