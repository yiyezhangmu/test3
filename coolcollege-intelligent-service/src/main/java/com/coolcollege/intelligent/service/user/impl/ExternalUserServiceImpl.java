package com.coolcollege.intelligent.service.user.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.region.FixedRegionEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.menu.SysRoleMenuMapper;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.system.dao.SysRoleDao;
import com.coolcollege.intelligent.mapper.homeTemplate.HomeTemplateDAO;
import com.coolcollege.intelligent.mapper.homeTemplate.HomeTemplateRoleMappingDAO;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.coolcollege.intelligent.model.enums.RoleSourceEnum;
import com.coolcollege.intelligent.model.homeTemplate.DTO.ComponentsJsonDTO;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateDO;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateRoleMappingDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.user.ExternalUserService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: ExternalUserServiceImpl
 * @Description:
 * @date 2023-10-18 15:17
 */
@Service
public class ExternalUserServiceImpl implements ExternalUserService {

    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private RegionDao regionDao;
    @Resource
    private SysRoleDao sysRoleDao;
    @Resource
    private HomeTemplateDAO homeTemplateDAO;
    @Resource
    private HomeTemplateRoleMappingDAO homeTemplateRoleMappingDAO;
    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private EnterpriseUserRoleDao enterpriseUserRoleDao;
    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public void openOrCloseExternalUser(String enterpriseId, Boolean enableExternalUser) {
        String enterpriseDbName = enterpriseConfigApiService.getEnterpriseDbName(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseDbName);
        //新增外部用户节点和神秘访客节点
        regionDao.batchInsertOrUpdate(enterpriseId, getExternalUserRegionList(enableExternalUser));
        HomeTemplateDO homeTemplate = getHomeTemplate(enableExternalUser);
        homeTemplateDAO.insertIdTemplate(enterpriseId, homeTemplate);
        if(enableExternalUser){
            //处理神秘访客职位
            sysRoleDao.insertOrUpdateRole(enterpriseId, getRole());
            //初始化角色权限

            HomeTemplateRoleMappingDO homeTemplateRoleMapping = new HomeTemplateRoleMappingDO();
            homeTemplateRoleMapping.setTemplateId(homeTemplate.getId());
            homeTemplateRoleMapping.setRoleId(Long.valueOf(Role.MYSTERIOUS_GUEST.getId()));
            homeTemplateRoleMapping.setCreateTime(new Date());
            homeTemplateRoleMapping.setUpdateTime(new Date());
            homeTemplateRoleMappingDAO.batchInsert(enterpriseId, Arrays.asList(homeTemplateRoleMapping));
        }
        if(!enableExternalUser){
            //删除职位
            sysRoleDao.deleteRoles(enterpriseId, Long.valueOf(Role.MYSTERIOUS_GUEST.getId()));
            //删除用户角色
            enterpriseUserRoleDao.deleteByRoleId(enterpriseId, Role.MYSTERIOUS_GUEST.getId());
            //删除角色关联的模板
            homeTemplateRoleMappingDAO.deletedByRoleIds(enterpriseId, Arrays.asList(Long.valueOf(Role.MYSTERIOUS_GUEST.getId())));
            //删除外部区域节点下的所有区域
            List<RegionDO> externalRegionList = regionDao.getExternalRegionList(enterpriseId);
            if(CollectionUtils.isNotEmpty(externalRegionList)){
                List<String> regionIds = externalRegionList.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
                regionDao.deleteRegionsByIds(enterpriseId, regionIds);
                List<String> userIds = userRegionMappingDAO.getUserIdsByRegionIds(enterpriseId, regionIds);
                if(CollectionUtils.isNotEmpty(userIds)){
                    //更新用户部门
                    userRegionMappingDAO.batchDeletedByUserIdsAndRegionIds(enterpriseId, null, regionIds);
                    List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
                    List<UserRegionMappingDO> userRegionList = new ArrayList<>();
                    for (EnterpriseUserDO enterpriseUser : userList) {
                        EnterpriseUserDO update = new EnterpriseUserDO();
                        String userRegionIds = enterpriseUser.getUserRegionIds();
                        List<String> newRegionList = new ArrayList<>();
                        if(StringUtils.isNotBlank(userRegionIds)){
                            newRegionList = Arrays.asList(userRegionIds.substring(1, userRegionIds.length() - 1).split(",")).stream().filter(o->!o.contains(String.valueOf(FixedRegionEnum.EXTERNAL_USER.getId()))).collect(Collectors.toList());
                        }
                        String userRegion = Constants.SQUAREBRACKETSLEFT + FixedRegionEnum.DEFAULT.getFullRegionPath() + Constants.SQUAREBRACKETSRIGHT;
                        if(CollectionUtils.isNotEmpty(newRegionList)){
                            userRegion = Constants.SQUAREBRACKETSLEFT + newRegionList.stream().collect(Collectors.joining(Constants.COMMA)) + Constants.SQUAREBRACKETSRIGHT;
                        }else{
                            UserRegionMappingDO userRegionMapping = new UserRegionMappingDO();
                            userRegionMapping.setUserId(enterpriseUser.getUserId());
                            userRegionMapping.setRegionId(String.valueOf(FixedRegionEnum.DEFAULT.getId()));
                            userRegionList.add(userRegionMapping);
                        }
                        enterpriseUser.setUserRegionIds(userRegionIds);
                        update.setUserId(enterpriseUser.getUserId());
                        update.setUserRegionIds(userRegion);
                        enterpriseUserDao.updateEnterpriseUser(enterpriseId, update);
                    }
                    userRegionMappingDAO.batchInsertRegionMapping(enterpriseId, userRegionList);
                }
            }
        }
    }

    public HomeTemplateDO getHomeTemplate(boolean enableExternalUser){
        HomeTemplateDO homeTemplateDO = new HomeTemplateDO();
        homeTemplateDO.setId(-1);
        homeTemplateDO.setTemplateName("神秘访客职位模版");
        homeTemplateDO.setTemplateDescription("适用于神秘访客角色使用");
        homeTemplateDO.setIsDefault(Constants.INDEX_ONE);
        homeTemplateDO.setDeleted(enableExternalUser ? 0 : 1);
        homeTemplateDO.setCreateTime(new Date());
        homeTemplateDO.setUpdateTime(new Date());
        ComponentsJsonDTO appComponentsJsonDTO = new ComponentsJsonDTO();
        appComponentsJsonDTO.setComponentsJson(JSONObject.parseObject("{\"moduleList\":[{\"key\":\"userInfo\",\"name\":\"个人信息\",\"visible\":true,\"dragable\":true,\"configurable\":true},{\"key\":\"steriousShopTour\",\"name\":\"神访巡店\",\"visible\":true,\"dragable\":true,\"configurable\":true},{\"key\":\"steriousTask\",\"name\":\"我的神访记录\",\"visible\":true,\"dragable\":true,\"configurable\":true},{\"key\":\"Banner\",\"name\":\"轮播图\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"CommonFunctions\",\"name\":\"常用功能\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"DataOverview\",\"name\":\"数据概况\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"LicenseData\",\"name\":\"证照数据\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"WorkorderData\",\"name\":\"工单数据\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"RankingofUnqualifiedItems\",\"name\":\"不合格项排名\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"TourRecord\",\"name\":\"巡店记录\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"ToDoList\",\"name\":\"我的待办\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"TourData\",\"name\":\"巡店数据\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"AverageScoreOfVisitedStores\",\"name\":\"已巡门店平均得分\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"ExecutionRanking\",\"name\":\"巡店执行力排名\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"DisplayData\",\"name\":\"陈列数据\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"DayClear\",\"name\":\"日清\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"StoreTaskTodo\",\"name\":\"门店日清\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"StoreTaskOverview\",\"name\":\"店务概况\",\"visible\":false,\"dragable\":true,\"configurable\":true},{\"key\":\"AssistantTask\",\"name\":\"我的任务\",\"visible\":false,\"dragable\":true,\"configurable\":true}]}"));
        homeTemplateDO.setAppComponentsJson(JSONObject.toJSONString(appComponentsJsonDTO));
        return homeTemplateDO;
    }

    private SysRoleDO getRole(){
        SysRoleDO role = new SysRoleDO();
        role.setId(Long.valueOf(Role.MYSTERIOUS_GUEST.getId()));
        role.setRoleName(Role.MYSTERIOUS_GUEST.getName());
        role.setRoleAuth(AuthRoleEnum.PERSONAL.getCode());
        role.setSource(RoleSourceEnum.CREATE.getCode());
        role.setPositionType(CoolPositionTypeEnum.STORE_OUTSIDE.getCode());
        role.setPriority(Role.MYSTERIOUS_GUEST.getPriority());
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        role.setRoleEnum(Role.MYSTERIOUS_GUEST.getRoleEnum());
        return role;
    }

    private List<RegionDO> getExternalUserRegionList(Boolean enableExternalUser){
        List<RegionDO> regionList = new ArrayList<>();
        RegionDO externalUser = new RegionDO();
        externalUser.setId(FixedRegionEnum.EXTERNAL_USER.getId());
        externalUser.setParentId(FixedRegionEnum.EXTERNAL_USER.getParentId());
        externalUser.setRegionType(FixedRegionEnum.EXTERNAL_USER.getRegionType());
        externalUser.setName(FixedRegionEnum.EXTERNAL_USER.getName());
        externalUser.setRegionPath(FixedRegionEnum.EXTERNAL_USER.getRegionPath());
        externalUser.setUnclassifiedFlag(FixedRegionEnum.EXTERNAL_USER.getUnclassifiedFlag());
        externalUser.setIsExternalNode(FixedRegionEnum.EXTERNAL_USER.getExternalNode());
        externalUser.setDeleted(!enableExternalUser);
        RegionDO mysteriousGuest = new RegionDO();
        mysteriousGuest.setId(FixedRegionEnum.MYSTERIOUS_GUEST.getId());
        mysteriousGuest.setParentId(FixedRegionEnum.MYSTERIOUS_GUEST.getParentId());
        mysteriousGuest.setRegionType(FixedRegionEnum.MYSTERIOUS_GUEST.getRegionType());
        mysteriousGuest.setName(FixedRegionEnum.MYSTERIOUS_GUEST.getName());
        mysteriousGuest.setRegionPath(FixedRegionEnum.MYSTERIOUS_GUEST.getRegionPath());
        mysteriousGuest.setUnclassifiedFlag(FixedRegionEnum.MYSTERIOUS_GUEST.getUnclassifiedFlag());
        mysteriousGuest.setIsExternalNode(FixedRegionEnum.MYSTERIOUS_GUEST.getExternalNode());
        mysteriousGuest.setDeleted(!enableExternalUser);
        regionList.add(externalUser);
        regionList.add(mysteriousGuest);
        return regionList;
    }
}
