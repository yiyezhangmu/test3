package com.coolcollege.intelligent.service.user.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.UserSelectRangeEnum;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.user.UserJurisdictionStoreMapper;
import com.coolcollege.intelligent.dao.user.UserJurisdictionSubordinateMapper;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.user.UserJurisdictionStoreDO;
import com.coolcollege.intelligent.model.user.UserJurisdictionSubordinateDO;
import com.coolcollege.intelligent.model.user.dto.DynamicRegionDTO;
import com.coolcollege.intelligent.model.user.dto.UserJurisdictionDTO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.user.UserJurisdictionService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/19 13:38
 */
@Service
@Slf4j
public class UserJurisdictionServiceImpl implements UserJurisdictionService {

    @Resource
    private RegionService regionService;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private EnterpriseMapper enterpriseMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private UserJurisdictionStoreMapper userJurisdictionStoreMapper;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Resource(name = "userStoreThreadPool")
    private TaskExecutor userStoreThreadPool;
    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private SubordinateMappingService subordinateMappingService;

    @Resource
    private UserJurisdictionSubordinateMapper userJurisdictionSubordinateMapper;

    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private EnterpriseService enterpriseService;



    @Override
    public void updateAllUserJurisdictionStore(List<String> enterpriseIds) {
        //时间日志
        long start = System.currentTimeMillis();
        log.info("更新所有企业所有人的管理门店开始,企业id:{}",enterpriseIds);
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while (hasNext) {
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if (org.apache.commons.collections4.CollectionUtils.isEmpty(enterpriseConfigList)) {
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                try {
                    addStoreAndUser(enterpriseConfig);
                }catch (Exception e){
                    log.error("更新企业权限门店失败,企业id:{}",enterpriseConfig.getEnterpriseId(),e);
                }
            }
        }
        //时间日志
        long end = System.currentTimeMillis();
        log.info("更新所有企业所有人的管理门店结束,企业id:{},耗时:{}",enterpriseIds,end - start);
    }
    @Override
    public void updateUserJurisdictionStore(String eid){
        //时间日志
        long start = System.currentTimeMillis();
        log.info("更新企业权限门店开始,企业id:{}",eid);

        DataSourceHelper.reset();
        //根据eid获取企业配置信息
        EnterpriseConfigDO curEnterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        try {
            addStoreAndUser(curEnterpriseConfigDO);
        }catch (Exception e){
            log.error("更新企业权限门店失败,企业id:{}",eid,e);
        }

        //时间日志
        long end = System.currentTimeMillis();
        log.info("更新企业权限门店结束,企业id:{},耗时:{}",eid,end - start);
    }

    @Override
    public UserJurisdictionDTO getUserJurisdiction(String eid, String userId) {
        //切换对应数据源
        DataSourceHelper.reset();
        EnterpriseConfigDO curEnterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        List<String> ids = Lists.newArrayList();
        ids.add(eid);
        List<EnterpriseDO> enterpriseByIds = enterpriseMapper.getEnterpriseByIds(ids);
        UserJurisdictionDTO userJurisdictionDTO = new UserJurisdictionDTO();
        if (CollectionUtils.isEmpty(enterpriseByIds)){
            log.info("企业不存在");
            return userJurisdictionDTO;
        }
        DataSourceHelper.changeToSpecificDataSource(curEnterpriseConfigDO.getDbName());
        //获取用户信息
        EnterpriseUserDO userDetail = enterpriseUserDao.selectByUserId(eid, userId);
        if (userDetail == null){
            log.info("用户不存在");
            return userJurisdictionDTO;
        }
        userJurisdictionDTO.setUserId(userId);
        userJurisdictionDTO.setUserName(userDetail.getName());
        userJurisdictionDTO.setEnterpriseId(eid);
        userJurisdictionDTO.setEnterpriseName(enterpriseByIds.get(0).getName());

        userJurisdictionDTO.setAdminUser(0);
        userJurisdictionDTO.setAdminStore(0);

        List<String> regionIdByUserId = userAuthMappingMapper.getRegionIdByUserId(eid, userId);
        if (subordinateMappingService.checkHaveAllSubordinateStore(eid, userId) ){
            log.info("用户为管理员:{}",userId);
            userJurisdictionDTO.setAdminUser(1);
            userJurisdictionDTO.setAdminStore(1);
            return userJurisdictionDTO;
        }
        //检查是否管辖全员或者老企业
        if (UserSelectRangeEnum.ALL.getCode().equals(userDetail.getSubordinateRange())||enterpriseService.isHistoryEnterprise(eid)){
            log.info("用户管理所有人:{}",userId);
            userJurisdictionDTO.setAdminUser(1);
        }
        //检查管辖区域是否为根节点，也算管理所有门店
        if (regionIdByUserId.stream().anyMatch(c->c.equals("1"))){
            log.info("用户管理所有店:{}",userId);
            userJurisdictionDTO.setAdminStore(1);
        }

        return userJurisdictionDTO;
    }

    @Override
    public List<DynamicRegionDTO> getDynamicRegionParam(String enterpriseId, String userId) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<DynamicRegionDTO> dynamicRegionDTOList = Lists.newArrayList();
        List<String> defaultDeptIdList = Lists.newArrayList();
        String deptLevel = "";
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        if(isAdmin){
            List<Long> defaultRegionIdList = regionMapper.getRegionIdListByParentId(enterpriseId, Constants.ROOT_DEPT_ID);
            deptLevel = "dep2";
            if(CollectionUtils.isEmpty(defaultRegionIdList)){
                defaultRegionIdList.add(Constants.ROOT_DEPT_ID);
                deptLevel = "dep1";
            }
            defaultDeptIdList = defaultRegionIdList.stream().map(a -> String.valueOf(a)).collect(Collectors.toList());
        }else {
            List<String> regionAuthList = userAuthMappingMapper.getRegionIdByUserId(enterpriseId, userId);
            if(CollectionUtils.isEmpty(regionAuthList)){
                return dynamicRegionDTOList;
            }else if(regionAuthList.size() == 1){
                String parentId = regionAuthList.get(0);
                RegionDO parentRegion = regionMapper.getByRegionId(enterpriseId, Long.valueOf(parentId));
                List<String> deptIdList = StrUtil.splitTrim(parentRegion.getRegionPath(), "/");
                int deptLevelInt = deptIdList.size() + 2;
                List<Long> defaultRegionIdList = regionMapper.getRegionIdListByParentId(enterpriseId, Long.valueOf(parentId));
                if(CollectionUtils.isEmpty(defaultRegionIdList)){
                    defaultRegionIdList.add(Long.valueOf(parentId));
                    deptLevelInt = deptIdList.size() + 1;
                }
                deptLevel = "dep" + deptLevelInt;
                defaultDeptIdList = defaultRegionIdList.stream().map(a -> String.valueOf(a)).collect(Collectors.toList());
            }else{
                List<RegionDO> regionAuthDOList= regionService.getRegionDOsByRegionIds(enterpriseId, regionAuthList);
                Map<String, List<String>> regionAuthMap = Maps.newHashMap();
                ListUtils.emptyIfNull(regionAuthDOList).stream().forEach(regionAuth -> {
                            List<String> deptIdList = StrUtil.splitTrim(regionAuth.getRegionPath(), "/");
                            int deptLevelInt = deptIdList.size() + 1;
                            String deptLevelTmp = "dep" + deptLevelInt;
                            List<String> authRegionIdList = regionAuthMap.get(deptLevelTmp);
                            if (CollectionUtils.isEmpty(authRegionIdList)) {
                                authRegionIdList = Lists.newArrayList();
                                regionAuthMap.put(deptLevelTmp, authRegionIdList);
                            }
                            authRegionIdList.add(String.valueOf(regionAuth.getId()));
                });
                Set<String> depLevelSet = regionAuthMap.keySet();
                List<String> depLevelList = new ArrayList<>(depLevelSet);
                deptLevel = depLevelList.get(0);
                defaultDeptIdList = regionAuthMap.get(deptLevel);
            }
        }
        DynamicRegionDTO dynamicRegionDTO = new DynamicRegionDTO();
        dynamicRegionDTO.setAppId("127572");
        dynamicRegionDTO.setName(deptLevel);
        dynamicRegionDTO.setValue(defaultDeptIdList);
        dynamicRegionDTOList.add(dynamicRegionDTO);
        return dynamicRegionDTOList;
    }

    private void addStoreAndUser(EnterpriseConfigDO curEnterpriseConfigDO) {
        String eid = curEnterpriseConfigDO.getEnterpriseId();
        //更换对应数据源
        DataSourceHelper.changeToSpecificDataSource(curEnterpriseConfigDO.getDbName());
        //获取企业人数
        Integer activeUserCount = enterpriseUserDao.getActiveUserCount(eid);
        //如果当前企业没有用户则跳过
        if (activeUserCount == 0){
            log.info("企业没有用户");
            return;
        }
        //企业所有用户Id
        List<String> allUserIdS = Lists.newArrayList();
        //如果总数大于1000条,那就分批获取
        if (activeUserCount > 0){
            //获取总页数
            int totalPage = (activeUserCount + Constants.LENGTH_SIZE - 1) / Constants.LENGTH_SIZE;
            for (int i = Constants.INDEX_ONE; i <= totalPage; i++) {
                //先查1000条用户
                PageHelper.startPage(i,Constants.LENGTH_SIZE);
                allUserIdS.addAll(enterpriseUserDao.selectAllUserIdsByActive(eid,Boolean.TRUE));
            }
        }
        //查出未激活用户的id 并删除
        List<String> activeFalseUserIds = enterpriseUserDao.selectAllUserIdsByActive(eid, Boolean.FALSE);
        userJurisdictionSubordinateMapper.deleteUserJurisdictionSubordinateByUserId(eid,activeFalseUserIds);
        for (String userId : allUserIdS) {
            //将任务放入线程池运行
            userStoreThreadPool.execute(()->{
                try {
                    addJurisdictionalStore(curEnterpriseConfigDO,userId,eid);
                }catch (Exception e){
                    log.error("更新管辖门店失败,用户id:{}",userId,e);
                }
                try {
                    addJurisdictionalUser(curEnterpriseConfigDO,userId,eid,allUserIdS);
                }catch (Exception e){
                    log.error("更新管辖用户失败,用户id:{}",userId,e);
                }
            });
        }
    }

    private void addJurisdictionalStore(EnterpriseConfigDO curEnterpriseConfigDO,String userId,String eid) {
        //更换对应数据源
        DataSourceHelper.changeToSpecificDataSource(curEnterpriseConfigDO.getDbName());
        log.info("当前线程为:{}",Thread.currentThread().getName());
        //如果当前用户为管理员则获取企当前企业所有门店id
        if (Constants.AI_USER_ID.equals(userId) ||sysRoleService.checkIsAdmin(eid,userId)){
            //获取到当前企业所有门店id
            List<String> allStoreId = storeMapper.getAllStoreId(eid);
            if (CollectionUtils.isEmpty(allStoreId)){
                log.info("当前企业没有门店,{}",eid);
                return;
            }
            //插入前根据用户id删除记录
            userJurisdictionStoreMapper.deleteUserJurisdictionStoreByUserId(eid,userId);
            ArrayList<UserJurisdictionStoreDO> userJurisdictionStoreDOS = new ArrayList<>();
            //然后批量插入
            ListUtils.partition(allStoreId, 200).forEach(groupStoreIds -> {
                userJurisdictionStoreDOS.clear();
                groupStoreIds.stream().forEach(storeId -> {
                    UserJurisdictionStoreDO userJurisdictionStoreDO = new UserJurisdictionStoreDO();
                    userJurisdictionStoreDO.setUserId(userId);
                    userJurisdictionStoreDO.setStoreId(storeId);
                    userJurisdictionStoreDOS.add(userJurisdictionStoreDO);
                });
                Integer integer = userJurisdictionStoreMapper.batchInsertUserJurisdictionStore(eid, userJurisdictionStoreDOS);
                log.info("管理员插入管辖门店记录成功,{},{}",integer,eid);
            });
            return;
        }
        //如果不是管理员,先查用户所管辖的区域id
        List<UserAuthMappingDO> userAuthMappingDOS = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);

        if (CollectionUtils.isEmpty(userAuthMappingDOS)){
            log.info("当前用户没有管辖区域,{}",userId);
            //插入前根据用户id删除记录
            userJurisdictionStoreMapper.deleteUserJurisdictionStoreByUserId(eid,userId);
            return;
        }
        //插入前根据用户id删除记录
        userJurisdictionStoreMapper.deleteUserJurisdictionStoreByUserId(eid,userId);
        //根据mappingId查询区域信息
        List<String> storeIdsList= Lists.newArrayList();
        for (UserAuthMappingDO userAuthMappingDO : userAuthMappingDOS) {
            //根据mappingId查询区域信息
            if (RegionTypeEnum.STORE.getType().equals(userAuthMappingDO.getType())){
                //如果是门店直接插入
                storeIdsList.add(userAuthMappingDO.getMappingId());
                continue;
            }
            //是区域就查询区域信息
            RegionDO regionDO = regionMapper.getByRegionId(eid, Long.valueOf(userAuthMappingDO.getMappingId()));
            String fullRegionPath = regionDO.getFullRegionPath();
            //根据fullRegionPath查询用户所管辖的最小单位门店列表
            List<String> storeIdByFullRegionPath = storeMapper.getStoreIdByFullRegionPath(eid, fullRegionPath);
            if (CollectionUtils.isEmpty(storeIdByFullRegionPath)){
                log.info("当前区域没有门店,{}",userAuthMappingDO.getMappingId());
                continue;
            }
            storeIdsList.addAll(storeIdByFullRegionPath);
        }
        //然后批量插入
        List<UserJurisdictionStoreDO> userJurisdictionStoreDOS = new ArrayList<>();
        storeIdsList = storeIdsList.stream().distinct().collect(Collectors.toList());
        ListUtils.partition(storeIdsList, 200).forEach(storesGroupList -> {
            userJurisdictionStoreDOS.clear();
            storesGroupList.stream().forEach(storeId -> {
                UserJurisdictionStoreDO userJurisdictionStoreDO = new UserJurisdictionStoreDO();
                userJurisdictionStoreDO.setUserId(userId);
                userJurisdictionStoreDO.setStoreId(storeId);
                userJurisdictionStoreDOS.add(userJurisdictionStoreDO);
            });
            Integer integer = userJurisdictionStoreMapper.batchInsertUserJurisdictionStore(eid, userJurisdictionStoreDOS);
            log.info("普通用户插入管辖门店记录成功,{},{}",integer,eid);
        });
    }

    private void addJurisdictionalUser(EnterpriseConfigDO curEnterpriseConfigDO,String userId,String eid,List<String> allActiveUserIds ){
        //更换对应数据源
        DataSourceHelper.changeToSpecificDataSource(curEnterpriseConfigDO.getDbName());
        log.info("当前线程为:{}",Thread.currentThread().getName());
        //删除当前用户管辖用户
        List<String> id = Lists.newArrayList();
        id.add(userId);
        Integer integer= userJurisdictionSubordinateMapper.deleteUserJurisdictionSubordinateByUserId(eid, id);
        log.info("删除当前用人员管辖用户记录,userId:{},{}",userId,integer);
        List<String> subordinateUserIdList;
        //判断当前用户是否为管理员
        if (Constants.AI_USER_ID.equals(userId) ||subordinateMappingService.checkHaveAllSubordinateUser(eid,userId)){
            subordinateUserIdList = allActiveUserIds;
        }else {
            //获取当前人所管辖人员Ids 带本人
            subordinateUserIdList = subordinateMappingService.getSubordinateUserIdList(eid, userId, true);
        }

        List<UserJurisdictionSubordinateDO> data = Lists.newArrayList();
        ListUtils.partition(subordinateUserIdList,200).stream().forEach(subordinateUserIdGroupList->{
            data.clear();
            subordinateUserIdGroupList.stream().forEach(subordinateUserId->{
                UserJurisdictionSubordinateDO userJurisdictionSubordinateDO = new UserJurisdictionSubordinateDO();
                userJurisdictionSubordinateDO.setUserId(userId);
                userJurisdictionSubordinateDO.setUnderlingId(subordinateUserId);
                data.add(userJurisdictionSubordinateDO);
            });
            Integer integer1 = userJurisdictionSubordinateMapper.batchInsertUserJurisdictionSubordinate(eid, data);
            log.info("批量插入用户管辖用户成功,userId:{},数量:{}",userId,integer1);
        });
    }


}
