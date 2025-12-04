package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.enterprise.SubordinateSourceEnum;
import com.coolcollege.intelligent.common.enums.enterprise.UserSelectRangeEnum;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.SubordinateMappingDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SubordinateMappingDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author wxp
 * @Date 2023/1/6 11:18
 * @Version 1.0
 */
@Service
@Slf4j
public class SubordinateMappingServiceImpl implements SubordinateMappingService {

    @Resource
    SubordinateMappingDAO subordinateMappingDAO;
    @Autowired
    private EnterpriseUserDao enterpriseUserDao;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    private SysRoleService sysRoleService;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    /**
     * 判断用户是否管辖全部用户
     * @param enterpriseId
     * @param currentUserId
     * @return
     */
    @Override
    public Boolean checkHaveAllSubordinateUser(String enterpriseId, String currentUserId) {
        if(Constants.SYSTEM_USER_ID.equals(currentUserId) || Constants.AI.equals(currentUserId)){
            return true;
        }
        // 判断是否是管理员
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, currentUserId);
        //查看是否是老企业
        boolean historyEnterprise = enterpriseService.isHistoryEnterprise(enterpriseId);
        EnterpriseUserDO user = enterpriseUserDao.selectByUserId(enterpriseId, currentUserId);
        //失效人员没有权限
        if(user == null){
            return false;
        }
        if(isAdmin || historyEnterprise || UserSelectRangeEnum.ALL.getCode().equals(user.getSubordinateRange())){
            return true;
        }
        return false;
    }

    @Override
    public Boolean checkHaveAllSubordinateStore(String enterpriseId, String currentUserId) {
        if(Constants.SYSTEM_USER_ID.equals(currentUserId)|| Constants.AI_USER_ID.equals(currentUserId)){
            return true;
        }
        // 判断是否是管理员
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, currentUserId);
        if(isAdmin ){
            return true;
        }
        return false;
    }

    /**
     * 获取管辖用户
     * @param enterpriseId
     * @param currentUserId
     * @return
     */
    @Override
    public List<String> getSubordinateUserIdList(String enterpriseId, String currentUserId,Boolean addCurrentFlag) {

        List<String> allUserIdList = Lists.newArrayList();
        if (addCurrentFlag){
            allUserIdList.add(currentUserId);
        }
        EnterpriseUserDO user = enterpriseUserDao.selectByUserId(enterpriseId, currentUserId);
        // 查询管辖用户
        List<SubordinateMappingDO> subordinateMappingList = subordinateMappingDAO.selectByUserIds(enterpriseId, Collections.singletonList(currentUserId));
        // 如果用户关联用户配置是自定义，但是没有具体数据，默认关联区域门店区域权限下的人
        if(user != null && UserSelectRangeEnum.DEFINE.getCode().equals(user.getSubordinateRange()) && CollectionUtils.isEmpty(subordinateMappingList)){
            subordinateMappingList = Lists.newArrayList();
            SubordinateMappingDO subordinateMappingDO = fillDefaultAutoSubordinate(currentUserId, UserSelectRangeEnum.DEFINE.getCode(), SubordinateSourceEnum.AUTO.getCode());
            subordinateMappingList.add(subordinateMappingDO);
            subordinateMappingDAO.batchInsertSubordinateMapping(enterpriseId, subordinateMappingList);
        }

        List<String> sourceList = ListUtils.emptyIfNull(subordinateMappingList).stream().filter(x -> StringUtils.isNotBlank(x.getSource()))
                .map(SubordinateMappingDO::getSource).distinct().collect(Collectors.toList());

        //自动关联单独查询 过滤自动关联
        subordinateMappingList = ListUtils.emptyIfNull(subordinateMappingList).stream().filter(o -> !SubordinateSourceEnum.AUTO.getCode().equals(o.getSource())).collect(Collectors.toList());

        List<String> regionIds = ListUtils.emptyIfNull(subordinateMappingList).stream().filter(x -> StringUtils.isNotBlank(x.getRegionId()))
                .map(SubordinateMappingDO::getRegionId).collect(Collectors.toList());
        List<String> personalIds = ListUtils.emptyIfNull(subordinateMappingList).stream().filter(x -> StringUtils.isNotBlank(x.getPersonalId()))
                .map(SubordinateMappingDO::getPersonalId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(personalIds)) {
            allUserIdList.addAll(personalIds);
        }

        if(CollectionUtils.isNotEmpty(sourceList) && sourceList.contains(SubordinateSourceEnum.AUTO.getCode())) {
            List<String> authRegionIdList = userAuthMappingMapper.getMappingUserAuthMappingByUserId(enterpriseId, currentUserId);
            if(CollectionUtils.isNotEmpty(authRegionIdList)){
                regionIds.addAll(authRegionIdList);
            }
        }
        if(CollectionUtils.isNotEmpty(regionIds)) {
            List<String> enterpriseUserIds = enterpriseUserDao.getUserIdsByRegionIdList(enterpriseId, regionIds);
            if (CollectionUtils.isNotEmpty(enterpriseUserIds)) {
                allUserIdList.addAll(enterpriseUserIds);
            }
        }
        allUserIdList = allUserIdList.stream().distinct().collect(Collectors.toList());
        return allUserIdList;
    }

    /**
     *  保留管辖用户 userId管辖用户范围
     * @param enterpriseId
     * @param currentUserId
     * @param userIdList
     * @return
     */
    @Override
    public List<String> retainSubordinateUserIdList(String enterpriseId, String currentUserId, List<String> userIdList,Boolean addCurrentFlag) {
        Boolean flag = this.checkHaveAllSubordinateUser(enterpriseId, currentUserId);
        if(flag){
            return userIdList;
        }
        List<String> subordinateUserIdList = getSubordinateUserIdList(enterpriseId, currentUserId,addCurrentFlag);
        userIdList.retainAll(subordinateUserIdList);
        return userIdList;
    }


    private SubordinateMappingDO fillDefaultAutoSubordinate(String userId, String userRange, String source) {
        SubordinateMappingDO subordinateMappingDO = new SubordinateMappingDO();
        subordinateMappingDO.setUserId(userId);
        subordinateMappingDO.setRegionId(Constants.ZERO_STR);
        subordinateMappingDO.setCreateId(Constants.SYSTEM);
        subordinateMappingDO.setUpdateId(Constants.SYSTEM);
        subordinateMappingDO.setType(Constants.INDEX_ZERO);
        subordinateMappingDO.setUserRange(userRange);
        subordinateMappingDO.setSource(source);
        return subordinateMappingDO;
    }

}
