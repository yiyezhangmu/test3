package com.coolcollege.intelligent.facade.enterprise.init;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.sync.vo.EnterpriseOpenMsg;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.sop.TaskSopDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 大商app的企业开通的初始化同步逻辑
 *
 * @author wangshuo
 */
@Component
@Data
@Slf4j
public class DaShangAppEnterpriseInitService extends EnterpriseInitBaseService {
    @Resource
    private EnterpriseService enterpriseDao;

    @Resource
    private TaskSopService taskSopService;

    @Override
    public void enterpriseInit(String corpId, String eid, String appType, String dbName, String openUserId) {
        DataSourceHelper.reset();
        EnterpriseDO enterpriseDO = enterpriseDao.selectById(eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //同步部门
        initDepartment(eid, enterpriseDO);
        //初始化根部门
        initRootRegion(eid, enterpriseDO);
        //初始化默认分组区域
        RegionDO unclassifiedRegionDO = regionService.getUnclassifiedRegionDO(eid);
        //初始化用户
        initUser(eid, dbName, openUserId, unclassifiedRegionDO.getId());
    }

    private void initRootRegion(String eid, EnterpriseDO enterpriseDO) {
        RegionDO rootRegion = new RegionDO();
        rootRegion.setId(Long.valueOf(SyncConfig.ROOT_DEPT_ID));
        rootRegion.setName(enterpriseDO.getName());
        rootRegion.setCreateName(Constants.SYSTEM);
        rootRegion.setSynDingDeptId(SyncConfig.ROOT_DEPT_ID_STR);
        rootRegion.setRegionPath(null);
        rootRegion.setRegionType(RegionTypeEnum.ROOT.getType());
        rootRegion.setUnclassifiedFlag(SyncConfig.ZERO);
        rootRegion.setCreateTime(Calendar.getInstance().getTimeInMillis());
        rootRegion.setStoreNum(SyncConfig.ONE);
        //根区域落库
        regionService.insertRoot(eid, rootRegion);
    }

    private void initDepartment(String eid, EnterpriseDO enterpriseDO) {
        log.info("app端注册企业，只初始化根部门");
        SysDepartmentDO rootDepartment = new SysDepartmentDO();
        rootDepartment.setId(SyncConfig.ROOT_DEPT_ID_STR);
        rootDepartment.setName(enterpriseDO.getName());
        sysDepartmentMapper.batchInsertOrUpdate(Arrays.asList(rootDepartment), eid);
    }

    private void initUser(String eid, String dbName, String openUserId, Long unclassifiedId) {
        DataSourceHelper.reset();
        EnterpriseUserDO enterpriseUserDO = enterpriseUserService.selectConfigUserByUserId(openUserId);
        String managerRoleId = Role.MASTER.getId();
        //ai用户
        EnterpriseUserRequest aiUser = getAIUser();
        //企业库
        //添加用户
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<EnterpriseUserDO> insertUsers = new ArrayList<>();
        insertUsers.add(enterpriseUserDO);
        //insertUsers.add(aiUser.getEnterpriseUserDO());
        enterpriseUserService.batchInsertOrUpdate(insertUsers, eid);
        //ai添加用户角色
        List<EnterpriseUserRole> insertUserRoles = new ArrayList<>();
        EnterpriseUserRole aiUserRole = new EnterpriseUserRole(managerRoleId, aiUser.getEnterpriseUserDO().getUserId());
        insertUserRoles.add(aiUserRole);
        sysRoleService.insertBatchUserRole(eid, insertUserRoles);
        //添加用户部门
        EnterpriseUserDepartmentDO applyUserDept = new EnterpriseUserDepartmentDO(enterpriseUserDO.getUserId(), SyncConfig.ROOT_DEPT_ID_STR, Boolean.FALSE);
        EnterpriseUserDepartmentDO aiUserDept = new EnterpriseUserDepartmentDO(aiUser.getEnterpriseUserDO().getUserId(), SyncConfig.ROOT_DEPT_ID_STR, Boolean.FALSE);
        List<EnterpriseUserDepartmentDO> insertUserDept = new ArrayList<>();
        insertUserDept.add(applyUserDept);
        insertUserDept.add(aiUserDept);
        enterpriseUserDepartmentDao.deleteMapping(eid, Arrays.asList(enterpriseUserDO.getUserId(), aiUser.getEnterpriseUserDO().getUserId()));
        enterpriseUserDepartmentDao.batchInsert(eid, insertUserDept);
        //添加用户区域，放入未分组下
        UserRegionMappingDO userRegionMappingDO = new UserRegionMappingDO();
        userRegionMappingDO.setUserId(openUserId);
        userRegionMappingDO.setRegionId(String.valueOf(unclassifiedId));
        userRegionMappingDAO.deletedByUserIds(eid, Arrays.asList(openUserId));
        userRegionMappingDAO.batchInsertRegionMapping(eid, Arrays.asList(userRegionMappingDO));
        //调用订正用户表字段user_region_ids
        enterpriseUserService.updateUserRegionPathList(eid, Arrays.asList(openUserId));
    }

    @Override
    public void enterpriseInitDepartment(String corpId, String eid, String appType, String dbName) {

    }

    @Override
    public void enterpriseInitUser(String corpId, String eid, String appType, String dbName, Boolean isScopeChange) {

    }

    @Override
    public void onlySyncUser(String corpId, String eid, String appType, String dbName) {

    }

    @Override
    public void runEnterpriseScript(EnterpriseOpenMsg msg) {
        super.runEnterpriseScript(msg);
        List<TaskSopDO> sops = new ArrayList<>();
        TaskSopDO sop = new TaskSopDO();
        sop.setId(1L);
        sop.setFileName("陈列管理手册");
        sop.setUrl("https://oss-cool.coolstore.cn/doc/sop/dashangchenlie.pptx");
        sops.add(sop);

        TaskSopDO sop1 = new TaskSopDO();
        sop1.setId(2L);
        sop1.setFileName("巡店管理手册");
        sop1.setUrl("https://oss-cool.coolstore.cn/doc/sop/dashangxundian.pptx");
        sops.add(sop1);

        TaskSopDO sop2 = new TaskSopDO();
        sop2.setId(3L);
        sop2.setFileName("系统部署手册");
        sop2.setUrl("https://oss-cool.coolstore.cn/doc/sop/dashangsystemdeploy.pptx");
        sops.add(sop2);
        taskSopService.updateSopUrl(msg.getEid(), sops);

    }
}
