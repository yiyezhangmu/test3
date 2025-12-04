package com.coolcollege.intelligent.service.system.impl;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.config.redission.RedissonLocker;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.*;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataType;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.common.enums.songxia.SongXiaEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.songxia.SongxiaRoleEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.menu.SysMenuMapper;
import com.coolcollege.intelligent.dao.menu.SysRoleMenuMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.system.dao.SysRoleDao;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiAddRoleDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.RoleDetailVO;
import com.coolcollege.intelligent.mapper.homeTemplate.HomeTemplateDAO;
import com.coolcollege.intelligent.mapper.homeTemplate.HomeTemplateRoleMappingDAO;
import com.coolcollege.intelligent.model.coolcollege.CoolStoreDataChangeDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enums.RoleSourceEnum;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateDO;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateRoleMappingDO;
import com.coolcollege.intelligent.model.impoetexcel.dto.RoleImportDTO;
import com.coolcollege.intelligent.model.menu.SysRoleMenuDO;
import com.coolcollege.intelligent.model.menu.vo.RoleMenuAuthVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.SysRoleQueryDTO;
import com.coolcollege.intelligent.model.system.dto.*;
import com.coolcollege.intelligent.model.system.request.SysRoleModifyAuthRequest;
import com.coolcollege.intelligent.model.system.request.SysRoleModifyBaseRequest;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.homeTemplate.HomeTemplateRoleMappingService;
import com.coolcollege.intelligent.service.position.PositionService;
import com.coolcollege.intelligent.service.system.SysMenuService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.POSITION_IS_FULL;

/**
 * 角色service
 *
 * @author wangchunhui
 */
@Service(value = "sysRoleService")
@Slf4j
public class SysRoleServiceImpl implements SysRoleService {
    @Resource
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private PositionService positionService;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Lazy
    @Autowired
    EnterpriseUserService enterpriseUserService;
    @Autowired
    private SimpleMessageService simpleMessageService;
    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Autowired
    HomeTemplateRoleMappingDAO homeTemplateRoleMappingDAO;
    @Autowired
    HomeTemplateDAO homeTemplateDAO;
    @Autowired
    HomeTemplateRoleMappingService homeTemplateRoleMappingService;
    @Autowired
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private RedissonLocker redissonLocker;


    private static final List<String> SPECIAL_ROLE = Arrays.asList("主管", "主管理员", "子管理员", "负责人");

    private static final List<Long> SPECIAL_PC_MENU = Arrays.asList(585L, 589L, 600L, 33L, 37L, 634L, 666L, 611L, 577L, 26L, 29L, 18L, 12L, 605L, 4385L, 4362L, 4383L, 4356L, 4357L, 4470L, 4469L, 4359L, 4358L, 4360L, 4361L, 4362L, 4363L, 4364L, 4365L, 4366L, 4367L, 4368L, 4369L, 4370L, 4384L, 4385L, 4468L, 4374L, 4379L, 4541L, 4543L, 4545L, 4547L, 4549L, 4551L, 4554L);

    private static final List<Long> SPECIAL_APP_MENU = Arrays.asList(2020L, 2021L, 2022L, 2023L, 4002L, 4006L, 4016L, 4017L, 4022L, 4023L, 4015L, 4631L, 4632L, 4024L);
    @Autowired
    private EnterpriseUserRoleDao enterpriseUserRoleDao;
    @Autowired
    private SysRoleDao sysRoleDao;

    @Override
    public List<RoleDTO> getRoles(String enterpriseId, String roleName, String positionType, Integer pageNum, Integer pageSize) {
        DataSourceHelper.changeToMy();

        PageHelper.startPage(pageNum, pageSize);
        List<RoleDTO> sysRoleDos = sysRoleMapper.fuzzyRole(enterpriseId, roleName, positionType);
        Long roleIdByRoleEnum = getRoleIdByRoleEnum(enterpriseId, Role.MASTER.getRoleEnum());
        if (CollectionUtils.isEmpty(sysRoleDos)) {
            return sysRoleDos;
        }
        List<String> createUserIds = sysRoleDos.stream().filter(x -> StringUtils.isNotEmpty(x.getCreateUser()))
                .map(RoleDTO::getCreateUser).collect(Collectors.toList());
        List<String> updateUserIds = sysRoleDos.stream().filter(x -> StringUtils.isNotEmpty(x.getUpdateUser()))
                .map(RoleDTO::getUpdateUser).collect(Collectors.toList());
        createUserIds.addAll(updateUserIds);
        List<EnterpriseUserDO> enterpriseUserDOS = enterpriseUserService.selectUsersByUserIds(enterpriseId, createUserIds);
        Map<String, String> enterpriseUserMap = enterpriseUserDOS.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName));

        List<Long> roleIdList = ListUtils.emptyIfNull(sysRoleDos)
                .stream()
                .map(RoleDTO::getId)
                .collect(Collectors.toList());

        //根据roleIds查询应用到的模板 一对一关系
        List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOS = homeTemplateRoleMappingDAO.selectByRoleIds(enterpriseId, roleIdList);
        Map<Long, HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOMap = new HashMap<>();
        Map<Integer, HomeTemplateDO> HomeTemplateDOMap = new HashMap<>();
        List<Integer> templateIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(homeTemplateRoleMappingDOS)) {
            homeTemplateRoleMappingDOMap = homeTemplateRoleMappingDOS.stream().collect(Collectors.toMap(HomeTemplateRoleMappingDO::getRoleId, data -> data, (k1, k2) -> k2));
            templateIds = homeTemplateRoleMappingDOS.stream().map(HomeTemplateRoleMappingDO::getTemplateId).collect(Collectors.toList());
        }
        //将默认模板ID添加到list中
        templateIds.add(Constants.INDEX_ONE);
        templateIds.add(Constants.INDEX_TWO);
        List<HomeTemplateDO> homeTemplateDOS = homeTemplateDAO.selectByIds(enterpriseId, templateIds);
        HomeTemplateDOMap = homeTemplateDOS.stream().collect(Collectors.toMap(HomeTemplateDO::getId, data -> data));

        List<RoleUserDTO> roleUserDTOList = sysRoleMapper.selectRoleUser(enterpriseId, roleIdList);
        Map<Long, Integer> userCountMap = ListUtils.emptyIfNull(roleUserDTOList).stream()
                .filter(a -> a.getRoleId() != null && a.getUserCount() != null)
                .collect(Collectors.toMap(RoleUserDTO::getRoleId, RoleUserDTO::getUserCount, (a, b) -> a));

        Map<Long, HomeTemplateRoleMappingDO> finalHomeTemplateRoleMappingDOMap = homeTemplateRoleMappingDOMap;
        Map<Integer, HomeTemplateDO> finalHomeTemplateDOMap = HomeTemplateDOMap;
        List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOList = new ArrayList<>();
        ListUtils.emptyIfNull(sysRoleDos).stream()
                .forEach(data -> {
                    if (roleIdByRoleEnum.equals(data.getId())) {
                        data.setIsDelete(false);
                    }
                    CoolPositionTypeEnum positionTypeEnum = CoolPositionTypeEnum.getByCode(data.getPositionType());
                    if (positionTypeEnum != null) {
                        data.setPositionTypeName(positionTypeEnum.getMsg());
                    }
                    AuthRoleEnum authRoleEnum = AuthRoleEnum.getByCode(data.getRoleAuth());
                    if (authRoleEnum != null) {
                        data.setRoleAuthName(authRoleEnum.getMsg());
                    }
                    if (MapUtils.isNotEmpty(userCountMap)) {
                        Integer userCount = userCountMap.get(data.getId());
                        if (userCount == null) {
                            data.setUserCount(0);
                        } else {
                            data.setUserCount(userCount);
                        }
                    }
                    HomeTemplateRoleMappingDO homeTemplateRoleMappingDO = finalHomeTemplateRoleMappingDOMap.get(data.getId());
                    //如果角色没有对应的模板，根据角色职位类型给默认模板-
                    if (homeTemplateRoleMappingDO == null) {
                        homeTemplateRoleMappingDO = homeTemplateRoleMappingService.initHomeTempRoleMapping(data.getId(), data.getPositionType());
                        homeTemplateRoleMappingDOList.add(homeTemplateRoleMappingDO);
                    }
                    data.setHomeTemplateId(homeTemplateRoleMappingDO.getTemplateId());
                    String templateName = "";
                    if (homeTemplateRoleMappingDO.getTemplateId() != null) {
                        templateName = finalHomeTemplateDOMap.get(homeTemplateRoleMappingDO.getTemplateId()).getTemplateName();
                    }
                    data.setHomeTemplateName(templateName);
                    if (MapUtils.isNotEmpty(enterpriseUserMap) && StringUtils.isNotBlank(data.getCreateUser())) {
                        data.setCreateUserName(enterpriseUserMap.get(data.getCreateUser()));
                    }
                    if (MapUtils.isNotEmpty(enterpriseUserMap) && StringUtils.isNotBlank(data.getUpdateUser())) {
                        data.setUpdateUserName(enterpriseUserMap.get(data.getUpdateUser()));
                    }
                });
        //存储模板与角色映射关系
        homeTemplateRoleMappingDAO.batchInsert(enterpriseId, homeTemplateRoleMappingDOList);

        return sysRoleDos;
    }

    @Override
    public Long addSystemRoles(String enterpriseId, String roleName, String positionType, List<Long> appMenuIdList, Integer priority) {
        DataSourceHelper.changeToMy();
        if (StringUtils.isEmpty(roleName)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), ExceptionMessage.RELENAME_MISS.getMessage());
        }
        List<SysRoleDO> rolesByName = sysRoleMapper.getRolesByName(enterpriseId, roleName);
        if (CollectionUtils.isNotEmpty(rolesByName)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "该职位已存在，不可重复添加");
        }
        CoolPositionTypeEnum coolPositionTypeEnum = CoolPositionTypeEnum.getByCode(positionType);
        if (coolPositionTypeEnum == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "职位类型错误！");
        }
        long roleId = getRoleId();
        SysRoleDO sysRoleDO = new SysRoleDO();
        sysRoleDO.setId(roleId);
        sysRoleDO.setRoleName(roleName);
        sysRoleDO.setPositionType(positionType);
        sysRoleDO.setRoleAuth(AuthRoleEnum.PERSONAL.getCode());
        if (priority <= 0) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "优先级不能小于1！");
        }
        List<SysRoleDO> rolesByPriority = sysRoleMapper.selectRoleByPriority(enterpriseId, priority);
        if (rolesByPriority.size() > 0) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "优先级不能重复！");
        }
        sysRoleDO.setPriority(priority);
        sysRoleDO.setSource(PositionSourceEnum.CREATE.getValue());
        sysRoleDO.setCreateTime(new Date());
        sysRoleDO.setCreateUser(UserHolder.getUser().getUserId());
        sysRoleMapper.addSystemRole(enterpriseId, sysRoleDO);
        //角色添加默认模板
        HomeTemplateRoleMappingDO homeTemplateRoleMappingDO = homeTemplateRoleMappingService.initHomeTempRoleMapping(sysRoleDO.getId(), sysRoleDO.getPositionType());
        homeTemplateRoleMappingDAO.batchInsert(enterpriseId, Arrays.asList(homeTemplateRoleMappingDO));
        if (CollectionUtils.isNotEmpty(appMenuIdList)) {
            sysRoleMapper.addMenuByRole(enterpriseId, roleId, appMenuIdList, PlatFormTypeEnum.NEW_APP.getCode());
        }
        //职位数据新增，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(String.valueOf(roleId)), ChangeDataOperation.ADD.getCode(), ChangeDataType.POSITION.getCode());
        return roleId;
    }

    private long getRoleId() {
        return System.currentTimeMillis() + new Random().nextInt(1000);
    }

    @Override
    public RoleBaseDetailDTO detailSystemRole(String enterpriseId, Long roleId) {
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, roleId);
        if (role == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "职位不存在");
        }
        List<RoleMenuAuthVO> menusByRole = sysMenuService.getMenusByRole(enterpriseId, role, PlatFormTypeEnum.PC);
        //移动端菜单权限
        List<AppMenuDTO> appMenuDTOList = sysMenuService.getAppMenusByRoleNew(enterpriseId, roleId);


        List<AppMenuDTO> collect = ListUtils.emptyIfNull(appMenuDTOList)
                .stream()
                .filter(AppMenuDTO::getChecked)
                .collect(Collectors.toList());
        RoleBaseDetailDTO roleBaseDetailDTO = new RoleBaseDetailDTO();

        if (CollectionUtils.isEmpty(collect)) {
            //初始化4项移动端菜单数据返回
            appMenuDTOList
                    .forEach(data -> {
                        if (data.getId() == 686 || data.getId() == 691 || data.getId() == 694 || data.getId() == 693) {
                            data.setChecked(true);
                        }
                    });
        }
        roleBaseDetailDTO.setRoleMenuAuthList(menusByRole);
        roleBaseDetailDTO.setAppMenuList(appMenuDTOList);
        roleBaseDetailDTO.setId(role.getId());
        roleBaseDetailDTO.setRoleName(role.getRoleName());
        roleBaseDetailDTO.setIsInternal(role.getIsInternal());
        roleBaseDetailDTO.setRoleAuth(role.getRoleAuth());
        roleBaseDetailDTO.setPositionType(role.getPositionType());
        roleBaseDetailDTO.setRoleId(role.getId());
        roleBaseDetailDTO.setPriority(role.getPriority());
        roleBaseDetailDTO.setSource(role.getSource());
        return roleBaseDetailDTO;

    }

    @Override
    public RoleBaseDetailDTO detailSystemRoleNew(String enterpriseId, Long roleId) {
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, roleId);
        if (role == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "职位不存在");
        }
        List<RoleMenuAuthVO> menusByRole = sysMenuService.getMenusByRole(enterpriseId, role, PlatFormTypeEnum.PC);
        //移动端菜单权限
        List<AppMenuDTO> appMenuDTOList = sysMenuService.getAppMenusByRoleNew(enterpriseId, roleId);


        List<AppMenuDTO> collect = ListUtils.emptyIfNull(appMenuDTOList)
                .stream()
                .filter(AppMenuDTO::getChecked)
                .collect(Collectors.toList());
        RoleBaseDetailDTO roleBaseDetailDTO = new RoleBaseDetailDTO();

        if (CollectionUtils.isEmpty(collect)) {
            //初始化4项移动端菜单数据返回
            appMenuDTOList
                    .forEach(data -> {
                        if (data.getId() == 686 || data.getId() == 691 || data.getId() == 694 || data.getId() == 693) {
                            data.setChecked(true);
                        }
                    });
        }
        roleBaseDetailDTO.setRoleMenuAuthList(menusByRole);
        roleBaseDetailDTO.setAppMenuList(appMenuDTOList);
        roleBaseDetailDTO.setId(role.getId());
        roleBaseDetailDTO.setRoleName(role.getRoleName());
        roleBaseDetailDTO.setIsInternal(role.getIsInternal());
        roleBaseDetailDTO.setRoleAuth(role.getRoleAuth());
        roleBaseDetailDTO.setPositionType(role.getPositionType());
        roleBaseDetailDTO.setRoleId(role.getId());
        roleBaseDetailDTO.setPriority(role.getPriority());
        roleBaseDetailDTO.setSource(role.getSource());
        return roleBaseDetailDTO;

    }

    @Override
    public List<String> getRoleIdByUserId(String eid, String userId) {
        return sysRoleMapper.selectRolesByuserId(eid, userId);
    }


    @Override
    public List<UserDTO> detailUserSystemRole(String enterpriseId, Long roleId, String userName, Integer pageNum, Integer pageSize, Boolean active) {
        PageHelper.startPage(pageNum, pageSize);
//        if (Role.EMPLOYEE.getId()) {
//
//        }
        return sysRoleMapper.getRoleUser(enterpriseId, roleId, userName, active);

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyRole(String enterpriseId, SysRoleModifyAuthRequest request, PlatFormTypeEnum appType) {
        Long roleId = request.getRoleId();
        AuthRoleEnum authRoleEnum = AuthRoleEnum.getByCode(request.getRoleAuth());
        if (authRoleEnum == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "可视化范围类型不存在！");
        }
        List<Long> menus = request.getMenus();
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, roleId);
        if (role == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "职位不存在！");
        }
        if (StringUtils.equals(role.getRoleEnum(), Role.MASTER.getRoleEnum())) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "[管理员]职位不能被修改");
        }
        //基础信息修改
        role.setRoleAuth(request.getRoleAuth());
        role.setUpdateTime(new Date());
        role.setUpdateUser(UserHolder.getUser().getUserId());
        sysRoleMapper.updateRole(enterpriseId, role);
        sysRoleMapper.deleteMenuByRoles(enterpriseId, roleId, null);
        if (CollectionUtils.isNotEmpty(menus)) {
            sysRoleMapper.addMenuByRole(enterpriseId, roleId, menus, PlatFormTypeEnum.PC.getCode());
        }
        if (CollectionUtils.isNotEmpty(request.getAppMenuList())) {
            sysRoleMapper.addMenuByRole(enterpriseId, roleId, request.getAppMenuList(), appType.getCode());
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean modifyRoleBase(String enterpriseId, SysRoleModifyBaseRequest request) {
        Long roleId = request.getRoleId();

        CoolPositionTypeEnum positionTypeEnum = CoolPositionTypeEnum.getByCode(request.getPositionType());
        if (positionTypeEnum == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "职位类型不存在！");
        }
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, roleId);
        if (role == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "职位不存在！");
        }
        if (!StringUtils.equals(role.getRoleName(), request.getRoleName())) {
            List<SysRoleDO> rolesByName = sysRoleMapper.getRolesByName(enterpriseId, request.getRoleName());
            if (CollectionUtils.isNotEmpty(rolesByName)) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "职位名称已经存在！");
            }
        }
        if (request.getPriority() != null && !request.getPriority().equals(role.getPriority())) {
            if (request.getPriority() <= 0) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "优先级不能小于1！");
            }
            List<SysRoleDO> rolesByPriority = sysRoleMapper.selectRoleByPriority(enterpriseId, request.getPriority());
            if (rolesByPriority.size() > 0) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "优先级不能重复！");
            }
            role.setPriority(request.getPriority());
        }
        Long masterRoleId = getRoleIdByRoleEnum(enterpriseId, Role.MASTER.getRoleEnum());
        Long employeeRoleId = getRoleIdByRoleEnum(enterpriseId, Role.EMPLOYEE.getRoleEnum());
        Long shopownerRoleId = getRoleIdByRoleEnum(enterpriseId, Role.SHOPOWNER.getRoleEnum());

        if (roleId.equals(Long.valueOf(masterRoleId.toString())) || roleId.equals(Long.valueOf(employeeRoleId.toString())) || roleId.equals(Long.valueOf(shopownerRoleId.toString()))) {
            if (!StringUtils.equals(role.getRoleName(), request.getRoleName())) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "[管理员]、[未分配]、[店长]职位名称不允许修改");
            }
            if (!StringUtils.equals(role.getPositionType(), request.getPositionType())) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "[管理员]、[未分配]、[店长]职位类型不允许修改");
            }
        }
        role.setRoleName(request.getRoleName());
        role.setPositionType(request.getPositionType());
        role.setUpdateUser(UserHolder.getUser().getUserId());
        sysRoleMapper.updateRole(enterpriseId, role);
        //职位数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(String.valueOf(role.getId())), ChangeDataOperation.UPDATE.getCode(), ChangeDataType.POSITION.getCode());
        return true;
    }


    @Override
    public Boolean deleteRoles(String enterpriseId, Long roleId) {

        log.info("要删除的角色Id为{}", roleId);
        if (roleId == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), ExceptionMessage.ROLEID_MISS.getMessage());
        }
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, roleId);
        if (role == null) {
            log.info("要删除的角色Id不存在{}", roleId);
            return false;
        }
        if (StringUtils.equals(Role.MASTER.getRoleEnum(), role.getRoleEnum())) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), role.getRoleName() + "角色不能被删除！");

        }
        if (StringUtils.equals(Role.EMPLOYEE.getRoleEnum(), role.getRoleEnum())) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), role.getRoleName() + "角色不允许删除!");

        }
        Integer personNumsByRoles = sysRoleMapper.getPersonNumsByRoles(enterpriseId, roleId);
        if (personNumsByRoles > 0) {
            throw new ServiceException(ExceptionMessage.ROLE_BY_PERSON_USE.getCode(), ExceptionMessage.ROLE_BY_PERSON_USE.getMessage());
        }
        try {
            sysRoleMapper.deleteMenuByRoles(enterpriseId, roleId, null);
        } catch (Exception e) {
            throw new ServiceException(ExceptionMessage.MENU_DEL_FAILED.getCode(), ExceptionMessage.MENU_DEL_FAILED.getMessage());
        }
        try {
            sysRoleMapper.deleteRoles(enterpriseId, roleId);
            //删除角色映射关系
            homeTemplateRoleMappingDAO.deletedByRoleIds(enterpriseId, Arrays.asList(roleId));
            //删除角色映射信息
            enterpriseUserRoleMapper.deleteByRoleId(enterpriseId, String.valueOf(roleId));
        } catch (Exception e) {
            throw new ServiceException(ExceptionMessage.ROLE_DEL_FAILED.getCode(), ExceptionMessage.ROLE_DEL_FAILED.getMessage());
        }
        //职位数据删除，推送酷学院，发送mq消息，异步操作
        CoolStoreDataChangeDTO coolStoreDataChangeDTO = new CoolStoreDataChangeDTO();
        coolStoreDataChangeDTO.setOperation(ChangeDataOperation.DELETE.getCode());
        coolStoreDataChangeDTO.setDataIds(Arrays.asList(String.valueOf(role.getId())));
        coolStoreDataChangeDTO.setSysRoleDOS(Arrays.asList(role));
        coolStoreDataChangeDTO.setEnterpriseId(enterpriseId);
        coolStoreDataChangeDTO.setType(ChangeDataType.POSITION.getCode());
        simpleMessageService.send(JSONObject.toJSONString(coolStoreDataChangeDTO), RocketMqTagEnum.COOL_STORE_DATA_CHANGE);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteRoles(String enterpriseId, String userId, List<Long> roleIdList, Boolean enableDingSync) {

        log.info("批量删除,操作人{},角色Id为{}", userId, roleIdList);
        Boolean hasMaster;
        Boolean hasEmployee;
        Boolean hasShopOwner;

        if (CollectionUtils.isEmpty(roleIdList)) {
            return true;
        }
        Long masterRoleId = getRoleIdByRoleEnum(enterpriseId, Role.MASTER.getRoleEnum());
        Long employeeRoleId = getRoleIdByRoleEnum(enterpriseId, Role.EMPLOYEE.getRoleEnum());
        Long shopownerRoleId = getRoleIdByRoleEnum(enterpriseId, Role.SHOPOWNER.getRoleEnum());

        hasMaster = roleIdList.stream().anyMatch(data -> StringUtils.equals(masterRoleId.toString(), data.toString()));
        hasEmployee = roleIdList.stream().anyMatch(data -> StringUtils.equals(employeeRoleId.toString(), data.toString()));
        hasShopOwner = roleIdList.stream().anyMatch(data -> StringUtils.equals(shopownerRoleId.toString(), data.toString()));

        if (hasMaster) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "角色不能被删除！");
        }
        if (hasEmployee) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "未分配角色不允许删除!");
        }
        if (hasShopOwner) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "店长角色不允许删除!");
        }
        List<RoleUserDTO> userDTOList = sysRoleMapper.selectRoleUser(enterpriseId, roleIdList);
        List<RoleUserDTO> notEmptyRoleUserList = ListUtils.emptyIfNull(userDTOList).stream()
                .filter(data -> data.getUserCount() > 0)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(notEmptyRoleUserList)) {
            String collect = notEmptyRoleUserList.stream().map(RoleUserDTO::getRoleName).collect(Collectors.joining(","));
            throw new ServiceException(ExceptionMessage.ROLE_BY_PERSON_USE.getCode(), collect + ExceptionMessage.ROLE_BY_PERSON_USE.getMessage());
        }
        //列表中包含钉钉角色且开启钉钉同步，不允许删除
        List<SysRoleDO> roleList = sysRoleMapper.getRoleByRoleIds(enterpriseId, roleIdList);
        List<SysRoleDO> dingRoleList = ListUtils.emptyIfNull(roleList).stream()
                .filter(data -> !PositionSourceEnum.CREATE.getValue().equals(data.getSource()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(dingRoleList) && enableDingSync) {
            String collect = dingRoleList.stream().map(SysRoleDO::getRoleName).collect(Collectors.joining(","));
            throw new ServiceException(ExceptionMessage.DING_ROLE.getCode(), collect + ExceptionMessage.DING_ROLE.getMessage());
        }
        try {
            sysRoleMapper.batchDeleteMenuRole(enterpriseId, roleIdList);
        } catch (Exception e) {
            throw new ServiceException(ExceptionMessage.MENU_DEL_FAILED.getCode(), ExceptionMessage.MENU_DEL_FAILED.getMessage());
        }
        try {
            sysRoleMapper.batchDeleteRoles(enterpriseId, roleIdList);
            //角色首页模板映射删除
            homeTemplateRoleMappingDAO.deletedByRoleIds(enterpriseId, roleIdList);
        } catch (Exception e) {
            throw new ServiceException(ExceptionMessage.ROLE_DEL_FAILED.getCode(), ExceptionMessage.ROLE_DEL_FAILED.getMessage());
        }
        //职位数据删除，推送酷学院，发送mq消息，异步操作
        CoolStoreDataChangeDTO coolStoreDataChangeDTO = new CoolStoreDataChangeDTO();
        coolStoreDataChangeDTO.setOperation(ChangeDataOperation.DELETE.getCode());
        coolStoreDataChangeDTO.setSysRoleDOS(roleList);
        coolStoreDataChangeDTO.setEnterpriseId(enterpriseId);
        coolStoreDataChangeDTO.setType(ChangeDataType.POSITION.getCode());
        simpleMessageService.send(JSONObject.toJSONString(coolStoreDataChangeDTO), RocketMqTagEnum.COOL_STORE_DATA_CHANGE);
        return true;

    }

    @Override
    public Map<String, Object> getPersonsByRole(String enterpriseId, Long roleId, String userName, Integer pageNum, Integer pageSize) {
        DataSourceHelper.changeToMy();
        log.info("获取角色下的人员列表,角色Id为{},用户名为{},企业Id为{}", roleId, userName, enterpriseId);
        if (roleId == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), ExceptionMessage.ROLEID_MISS.getMessage());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<EnterpriseUserDTO> persons = sysRoleMapper.getPersonsByRole(enterpriseId, Arrays.asList(roleId), userName);
        log.info("获取到角色下的用户列表为{}", JSONObject.toJSONString(persons));
        if (CollectionUtils.isEmpty(persons)) {
            return PageHelperUtil.getPageInfo(new PageInfo<>(new ArrayList<EnterpriseUserDTO>()));
        }
        PageInfo<EnterpriseUserDTO> pageInfo = new PageInfo<>(persons);
        persons = persons.stream().filter(s -> !Objects.equals(AIEnum.AI_USERID.getCode(), s.getUserId())).collect(Collectors.toList());
        positionService.setOtherInfoForUsers(enterpriseId, persons);
        return PageHelperUtil.getPageInfo(pageInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addPersonToUser(String enterpriseId, SysRoleQueryDTO sysRoleQueryDTO, boolean delAll) {
        String roleId = sysRoleQueryDTO.getRoleId();
        if (StringUtils.isEmpty(roleId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), ExceptionMessage.ROLEID_MISS.getMessage());
        }
        List<String> userIds = sysRoleQueryDTO.getUserIds();
        if (CollectionUtils.isEmpty(userIds)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), ExceptionMessage.USER_MISS.getMessage());
        }
        List<EnterpriseUserDO> mainAdminList = enterpriseUserDao.getMainAdmin(enterpriseId);
        Long masterRoleId = getRoleIdByRoleEnum(enterpriseId, Role.MASTER.getRoleEnum());
        // 主管理员不能移动到其他角色列表
        if (mainAdminList != null && !roleId.equals(masterRoleId.toString())) {
            List<String> mainUserIdList = mainAdminList.stream()
                    .map(EnterpriseUserDO::getUserId)
                    .collect(Collectors.toList());
            boolean hasMainAdmin = userIds.stream().anyMatch(mainUserIdList::contains);
            if (hasMainAdmin) {
                throw new ServiceException(ErrorCodeEnum.NOT_SUPPORTED_DELETE.getCode(), "主管理员不能移动");
            }
        }
        List<EnterpriseUserDTO> enterpriseUserVos = sysRoleMapper.getPersonsByRole(enterpriseId, Arrays.asList(Long.valueOf(roleId)), null);
        //获得这些角色下的所有用户 柠檬向右 人数限制
        if ("d9c8f45190dc4071b8f18596c86aacb4".equals(enterpriseId)) {
            List<Long> roleIds = new ArrayList<>();
            roleIds.add(NmxyUserRoleEnum.FINANCE.getCode());
            roleIds.add(NmxyUserRoleEnum.SELECT_SITE_COMMISSIONER.getCode());
            roleIds.add(NmxyUserRoleEnum.INVESTMENT_COMMISSIONER.getCode());
            roleIds.add(NmxyUserRoleEnum.OPERATIONS_MANAGER.getCode());
            roleIds.add(NmxyUserRoleEnum.ENGINEER_DEP_SUPERVISOR.getCode());
            roleIds.add(NmxyUserRoleEnum.TRAIN_TEACHER.getCode());
            roleIds.add(NmxyUserRoleEnum.PERSONNEL.getCode());
            roleIds.add(NmxyUserRoleEnum.MARKETING_SPECIALIST.getCode());
            roleIds.add(NmxyUserRoleEnum.PROCUREMENT.getCode());
            //判断当前职位是否为招商职位
            if (roleIds.contains(Long.valueOf(sysRoleQueryDTO.getRoleId()))) {
                //获取招商职位的人
                Map<String, List<Long>> enterpriseUserMap = enterpriseUserRoleDao.selectByRoleIdList(enterpriseId, roleIds);
                if (!enterpriseUserMap.isEmpty()) {
                    List<String> noRoleUsers = new ArrayList<>();
                    //获取request，人的职位
                    Map<String, List<Long>> userRoleIds = enterpriseUserRoleDao.getUserRoleIds(enterpriseId, sysRoleQueryDTO.getUserIds());
                    for (String userId : userRoleIds.keySet()) {
                        //判断角色权限和招商角色是否无交集
                        if (Collections.disjoint(userRoleIds.get(userId), roleIds)) {
                            noRoleUsers.add(userId);
                        }
                    }
                    if (enterpriseUserMap.keySet().size() + noRoleUsers.size() > 9) {
                        throw new ServiceException(POSITION_IS_FULL);
                    }
                } else {
                    if (sysRoleQueryDTO.getUserIds().size() > 9) {
                        throw new ServiceException(POSITION_IS_FULL);
                    }
                }
            }

        }
        //把所有用户id赛选出来
        List<String> collect = enterpriseUserVos.stream().map(EnterpriseUserDTO::getUserId).collect(Collectors.toList());
        //赛选出没有该角色的用户列表，后续添加这些用户的映射关系
        userIds = userIds.stream().filter(p -> !collect.contains(p)).collect(Collectors.toList());

        log.info("role change info sysRoleQueryDTO={}", sysRoleQueryDTO);
        log.info("role change info enterprise_id={},user_idList={}", enterpriseId, userIds);
        if (CollectionUtils.isNotEmpty(userIds)) {
            //支持用户多角色，不需要删除用户其他权限
//            sysRoleMapper.deleteRolesByPerson(enterpriseId, userIds, delAll);
            sysRoleMapper.addPersonToUser(enterpriseId, Long.valueOf(roleId), userIds, RoleSyncTypeEnum.CREATE.getCode());
        }
        if (SongXiaEnterpriseEnum.songXiaCompany(enterpriseId)) {
            log.info("松下企业 eid={}", enterpriseId);
            addUserNumBySongXia(enterpriseId, userIds, roleId);
        }
        //用户数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, userIds, ChangeDataOperation.UPDATE.getCode(), ChangeDataType.USER.getCode());
        return Boolean.TRUE;
    }

    public void addUserNumBySongXia(String eid, List<String> userIds, String roleId) {
        if (roleId.equals(SongxiaRoleEnum.PROMOTER.getCode())) {
            for (String userId : userIds) {
                EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(eid, userId);
                if (enterpriseUserDO != null) {
                    String jobNum = enterpriseUserService.addUserNumBySongXia(eid, userId, roleId);
                    enterpriseUserDO.setJobnumber(jobNum);
                    enterpriseUserDao.updateEnterpriseUser(eid, enterpriseUserDO);
                }
            }
        }
    }

    @Override
    public void delRolesByUserIds(String eid, List<String> userIds, boolean delAll) {
        sysRoleMapper.deleteRolesByPerson(eid, userIds, delAll);
    }

    @Override
    public Integer countRoleByPerson(String eid, String userId) {
        return sysRoleMapper.countRoleByPerson(eid, userId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePersonToUser(String enterpriseId, Long roleId, List<String> userIdList) {
        if (roleId == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), ExceptionMessage.ROLEID_MISS.getMessage());
        }
        if (CollectionUtils.isEmpty(userIdList)) {
            return true;
        }
        List<EnterpriseUserDO> mainAdminList = enterpriseUserDao.getMainAdmin(enterpriseId);
        Long masterRoleId = getRoleIdByRoleEnum(enterpriseId, Role.MASTER.getRoleEnum());
        List<String> mainUserIdList = mainAdminList.stream().map(EnterpriseUserDO::getUserId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(mainAdminList) && roleId.equals(masterRoleId)) {
            boolean hasMainAdmin = userIdList.stream().anyMatch(mainUserIdList::contains);
            if (hasMainAdmin) {
                throw new ServiceException(ErrorCodeEnum.NOT_SUPPORTED_DELETE.getCode(), "主管理员不能删除!");
            }
        }
        try {
            //删除职位后将人员加入未分配职位中
            sysRoleMapper.deletePersonToUser(enterpriseId, roleId, userIdList);
            Long roleIdByRoleEnum = getRoleIdByRoleEnum(enterpriseId, Role.EMPLOYEE.getRoleEnum());
            //默认角色 与删除的角色不相等的时候 才给添加到未分配中
            if (!roleId.equals(roleIdByRoleEnum)) {
                //获取有角色的人员
                List<String> haveRoleUserIds = enterpriseUserRoleMapper.getHaveRoleUserIds(enterpriseId, userIdList);
                //过滤掉主管理员
                userIdList = userIdList.stream().filter(p -> !mainUserIdList.contains(p) && !haveRoleUserIds.contains(p)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(userIdList)) {
                    SysRoleQueryDTO queryDTO = new SysRoleQueryDTO();
                    queryDTO.setUserIds(userIdList);
                    queryDTO.setRoleId(roleIdByRoleEnum.toString());
                    addPersonToUser(enterpriseId, queryDTO, false);
                }
            }
        } catch (Exception e) {
            log.error("deletePersonToUser,error:", e);
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "角色下的用户删除失败");
        }
        //用户数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, userIdList, ChangeDataOperation.UPDATE.getCode(), ChangeDataType.USER.getCode());
        return true;
    }


    @Override
    public Boolean insertOrUpdateRole(String enterpriseId, SysRoleDO role) {
        //根据roleId查询模板
        List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOS = homeTemplateRoleMappingDAO.selectByRoleIds(enterpriseId, Arrays.asList(role.getId()));
        //如果是null，角色没有对应的模板，给默认模板，如果不为null,不改变角色的模板
        if (CollectionUtils.isEmpty(homeTemplateRoleMappingDOS)) {
            String rolePosition = role.getPositionType();
            if (StringUtils.isBlank(rolePosition)) {
                rolePosition = CoolPositionTypeEnum.STORE_INSIDE.getCode();
            }
            HomeTemplateRoleMappingDO homeTemplateRoleMappingDO = homeTemplateRoleMappingService.initHomeTempRoleMapping(role.getId(), rolePosition);
            homeTemplateRoleMappingDAO.batchInsert(enterpriseId, Arrays.asList(homeTemplateRoleMappingDO));
        }
        role.setCreateUser(AIEnum.AI_USERID.getCode());
        role.setCreateTime(new Date());
        role.setUpdateUser(AIEnum.AI_USERID.getCode());
        role.setUpdateTime(new Date());
        return sysRoleMapper.insertOrUpdateRole(enterpriseId, role);
    }

    @Override
    public Boolean insertBatchRoles(String enterpriseId, List<SysRoleDO> roles) {
        return sysRoleMapper.batchInsertOrUpdateRoles(enterpriseId, roles);
    }

    @Override
    public Boolean insertBatchUserRole(String eid, List<EnterpriseUserRole> userRole) {
        return sysRoleMapper.insertBatchUserRole(eid, userRole);
    }

    @Override
    public Boolean deleteSyncRoleRelate(String eid, String userId) {
        return sysRoleMapper.deleteSyncRoleRelate(eid, userId);
    }

    @Override
    public Object getSyncRoles(String eid) {
        List<Map<String, Object>> syncRoleList = sysRoleMapper.getSyncRoleList(eid);
        if (CollUtil.isNotEmpty(syncRoleList)) {
            syncRoleList = syncRoleList.stream().filter(f -> !SPECIAL_ROLE.contains(f.get("role_name"))).collect(Collectors.toList());
        }
        return syncRoleList;
    }

    @Override
    public Boolean checkIsAdmin(String enterpriseId, String userId) {
        if (BailiEnterpriseEnum.bailiAffiliatedCompany(enterpriseId)) {
            return this.checkIsAdminAndSubAdmin(enterpriseId, userId);
        }
        // 1.取出所有用户角色
        // 2.匹配是否有管理员角色
        List<SysRoleDO> sysRoleDOList = sysRoleMapper.listRoleByUserId(enterpriseId, userId);
        return ListUtils.emptyIfNull(sysRoleDOList)
                .stream()
                .anyMatch(role -> StringUtils.equals(Role.MASTER.getRoleEnum(), role.getRoleEnum()));
    }

    @Override
    public Boolean checkIsAdminAndSubAdmin(String enterpriseId, String userId) {

        // 1.取出所有用户角色
        // 2.匹配是否有管理员角色
        List<SysRoleDO> sysRoleDOList = sysRoleMapper.listRoleByUserId(enterpriseId, userId);
        return ListUtils.emptyIfNull(sysRoleDOList)
                .stream()
                .anyMatch(role -> StringUtils.equals(Role.MASTER.getRoleEnum(), role.getRoleEnum()) || StringUtils.equals(Role.SUB_MASTER.getRoleEnum(), role.getRoleEnum()));
    }


    @Override
    public List<SysRoleDO> selectByRoleNameAndSource(String eid, String roleName, String source) {
        return sysRoleMapper.selectByRoleNameAndSource(eid, roleName, source);
    }

    @Override
    public Integer getNormalRoleMaxPriority(String eid) {
        List<Integer> priority = sysRoleMapper.getNormalRoleMaxPriority(eid);
        if (priority == null) {
            return 1;
        }
        priority.removeIf(Objects::isNull);
        if (priority.size() >= 2) {
            return priority.get(1);
        }
        return priority.size() == 0 ? 1 : priority.get(0);
    }

    @Override
    public void initDefaultRolePriority(String eid) {
        log.info("企业开始初始化预设职位的信息");
        Map<String, Integer> rolePriorityMap = Stream.of(Role.values())
                .filter(a -> a.getId() != null && a.getPriority() != null)
                .collect(Collectors.toMap(Role::getId, Role::getPriority));
        List<String> ids = new ArrayList<>();
        ids.addAll(rolePriorityMap.keySet());
        List<SysRoleDO> roles = sysRoleMapper.selectRoleByIdList(eid, ids);
        roles.forEach(role -> {
            if (role.getPriority() == null || role.getPriority().equals(0)) {
                role.setPriority(rolePriorityMap.get(role.getId().toString()));
                role.setUpdateUser(AIEnum.AI_USERID.getCode());
                sysRoleMapper.updateRole(eid, role);
            }
        });
    }

    @Override
    public Long getRoleIdByRoleEnum(String eid, String roleEnum) {

        SysRoleDO roleByRoleEnum = sysRoleMapper.getRoleByRoleEnum(eid, roleEnum);
        if (roleByRoleEnum == null) {
            return Long.valueOf(Role.getByCode(roleEnum).getId());
        }
        return roleByRoleEnum.getId();
    }

    @Override
    public Boolean initMenuWhenSyncRole(String eid, Long roleId) {
        log.info("给新增角色初始化移动端菜单,企业id{},角色Id为{}", eid, roleId);
        if (roleId != null) {
            sysRoleMapper.addMenuByRole(eid, roleId, SPECIAL_PC_MENU, PlatFormTypeEnum.PC.getCode());
            sysRoleMapper.addMenuByRole(eid, roleId, SPECIAL_APP_MENU, PlatFormTypeEnum.NEW_APP.getCode());
        }
        return true;
    }

    @Override
    public List<SysRoleDO> getRoleByRoleIds(String eid, List<Long> ids) {
        return sysRoleMapper.getRoleByRoleIds(eid, ids);
    }

    @Override
    public Boolean fixData(String enterpriseId, List<String> enterpriseIds, Long menuId) {
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> ConfigDOS;
        if (CollectionUtils.isEmpty(enterpriseIds)) {
            ConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        } else {
            enterpriseIds.add(enterpriseId);
            enterpriseIds = enterpriseIds.stream().distinct().collect(Collectors.toList());
            ConfigDOS = enterpriseConfigMapper.selectByEnterpriseIds(enterpriseIds);
        }
        for (EnterpriseConfigDO configDO : ConfigDOS) {
            try {
                DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
                //查询当前企业的角色列表中含有门店权限的 角色id
                List<Long> longs = sysRoleMenuMapper.listSysRoleMenuIdByMenuId(configDO.getEnterpriseId(), PlatFormTypeEnum.NEW_APP.getCode(), new Long(4022L));
                if (CollectionUtils.isEmpty(longs)) {
                    log.info("当前企业没有角色有门店权限");
                    continue;
                }
                List<SysRoleMenuDO> dos = new ArrayList<>();
                longs.stream().forEach(c -> {
                    SysRoleMenuDO sysRoleMenuDO = new SysRoleMenuDO();
                    sysRoleMenuDO.setRoleId(c);
                    sysRoleMenuDO.setMenuId(menuId);
                    sysRoleMenuDO.setPlatform(PlatFormTypeEnum.NEW_APP.getCode());
                    dos.add(sysRoleMenuDO);
                });
                //往映射表里加一条新数据映射
                sysRoleMenuMapper.batchInsertRoleMenu(configDO.getEnterpriseId(), dos);
            } catch (Exception e) {
                log.info("修正失败{}", configDO.getEnterpriseId());
            }
        }
        log.info("修正结束");
        return true;
    }

    @Override
    public RoleDetailVO insertOrUpdateSysRole(String enterpriseId, OpenApiAddRoleDTO param) {
        if(!param.check()){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        SysRoleDO oldRole = sysRoleMapper.getRoleIdByThirdUniqueId(enterpriseId, param.getThirdUniqueId());
        SysRoleDO role = new SysRoleDO();
        if (Objects.isNull(oldRole)) {
            if (!param.insertCheck()) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
            }
            long roleId = getRoleId();
            role.setId(roleId);
            role.setThirdUniqueId(param.getThirdUniqueId());
            role.setRoleName(param.getRoleName());
            role.setPositionType(param.getPositionType());
            role.setRoleAuth(AuthRoleEnum.PERSONAL.getCode());
            role.setSource(RoleSourceEnum.CREATE.getCode());
            role.setCreateUser(param.getCreateUser());
            role.setCreateTime(new Date());
            String key = MessageFormat.format(RedisConstant.INSERT_OR_UPDATE_ROLE_KEY, enterpriseId);
            boolean flag = redissonLocker.tryLock(key, TimeUnit.SECONDS, 10, 10);
            try {
                if (flag) {
                    List<SysRoleDO> rolesByName = sysRoleMapper.getRolesByName(enterpriseId, param.getRoleName());
                    if (CollectionUtils.isNotEmpty(rolesByName)) {
                        throw new ServiceException(ErrorCodeEnum.ROLE_ALREADY_EXISTS);
                    }
                    Integer priority = sysRoleDao.getLastedPriority(enterpriseId);
                    role.setPriority(priority);
                    sysRoleMapper.addSystemRole(enterpriseId, role);
                    //角色添加默认模板
                    HomeTemplateRoleMappingDO homeTemplateRoleMappingDO = homeTemplateRoleMappingService.initHomeTempRoleMapping(role.getId(), role.getPositionType());
                    homeTemplateRoleMappingDAO.batchInsert(enterpriseId, Collections.singletonList(homeTemplateRoleMappingDO));
                    //职位数据新增，推送酷学院，发送mq消息，异步操作
                    coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Collections.singletonList(String.valueOf(roleId)), ChangeDataOperation.ADD.getCode(), ChangeDataType.POSITION.getCode());
                }
            } catch (Exception e) {
                log.error("新增职位失败", e);
                throw new ServiceException(ErrorCodeEnum.ROLE_ALREADY_EXISTS);
            } finally {
                redissonLocker.unlock(key);
            }
        } else {
            this.updateCheck(enterpriseId, param.getRoleName(), param.getPositionType(), oldRole);
            role.setId(oldRole.getId());
            role.setRoleName(param.getRoleName());
            role.setPositionType(param.getPositionType());
            role.setUpdateUser(param.getUpdateUser());
            sysRoleMapper.updateRole(enterpriseId, role);
            coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Collections.singletonList(String.valueOf(role.getId())), ChangeDataOperation.UPDATE.getCode(), ChangeDataType.POSITION.getCode());
        }
        return new RoleDetailVO(role.getId());
    }

    @Override
    public List<SysRoleDO> getRoleIdByThirdUniqueIds(String enterpriseId, List<String> thirdUniqueIds) {
        if (CollectionUtils.isEmpty(thirdUniqueIds)) {
            return Collections.emptyList();
        }
        return sysRoleMapper.getRoleIdByThirdUniqueIds(enterpriseId, thirdUniqueIds);
    }

    @Override
    public Boolean updateThirdUniqueIds(String enterpriseId, MultipartFile file) {
        List<String> errorInfo = new ArrayList<>();
        // 1.解析excel数据
        List<RoleImportDTO> importList;
        try (InputStream inputStream = file.getInputStream()) {
            importList = ExcelImportUtil.importExcel(inputStream, RoleImportDTO.class, new ImportParams());
        } catch (Exception e) {
            log.error("文件解析失败", e);
            errorInfo.add("文件解析失败");
            throw new ServiceException(ErrorCodeEnum.FILE_PARSE_FAIL);
        }
        if (CollectionUtils.isEmpty(importList)){
            return true;
        }
        Lists.partition(importList, 100).forEach(i -> sysRoleDao.updateThirdUniqueIds(enterpriseId, importList));
        return true;
    }

    @Override
    public Integer deleteRoleWithoutUsers(String enterpriseId, Boolean isDeleteDefault) {
        return sysRoleDao.deleteRoleWithoutUsers(enterpriseId, isDeleteDefault);
    }

    /**
     * 数据校验
     * @param enterpriseId
     * @param roleName
     * @param positionType
     * @param role
     */
    private void updateCheck(String enterpriseId, String roleName, String positionType, SysRoleDO role) {
        Long roleId = role.getId();
        if (!StringUtils.equals(role.getRoleName(), roleName)) {
            List<SysRoleDO> rolesByName = sysRoleMapper.getRolesByName(enterpriseId, roleName);
            if (CollectionUtils.isNotEmpty(rolesByName)) {
                throw new ServiceException(ErrorCodeEnum.ROLE_ALREADY_EXISTS);
            }
        }
        Long masterRoleId = getRoleIdByRoleEnum(enterpriseId, Role.MASTER.getRoleEnum());
        Long employeeRoleId = getRoleIdByRoleEnum(enterpriseId, Role.EMPLOYEE.getRoleEnum());
        Long shopownerRoleId = getRoleIdByRoleEnum(enterpriseId, Role.SHOPOWNER.getRoleEnum());
        if (roleId.equals(Long.valueOf(masterRoleId.toString())) || roleId.equals(Long.valueOf(employeeRoleId.toString())) || roleId.equals(Long.valueOf(shopownerRoleId.toString()))) {
            if (!StringUtils.equals(role.getRoleName(), roleName)) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "[管理员]、[未分配]、[店长]职位名称不允许修改");
            }
            if (!StringUtils.equals(role.getPositionType(), positionType)) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "[管理员]、[未分配]、[店长]职位类型不允许修改");
            }
        }
    }

    @Override
    public SysRoleDO getRoleByRoleEnum(String eid, String roleEnum) {

        /**
         * 根据角色枚举查询角色
         * 如果没有角色构建一个角色
         */
        SysRoleDO roleByRoleEnum = sysRoleMapper.getRoleByRoleEnum(eid, roleEnum);
        if (roleByRoleEnum == null) {
            SysRoleDO newRoleDO = new SysRoleDO();
            Role byCode = Role.getByCode(roleEnum);
            newRoleDO.setId(Long.valueOf(byCode.getId()));
            newRoleDO.setRoleName(byCode.getName());
            newRoleDO.setPriority(byCode.getPriority());
            newRoleDO.setRoleEnum(byCode.getRoleEnum());
            return newRoleDO;
        }
        return roleByRoleEnum;
    }


}
