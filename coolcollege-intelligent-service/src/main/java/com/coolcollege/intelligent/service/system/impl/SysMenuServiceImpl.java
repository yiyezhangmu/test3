package com.coolcollege.intelligent.service.system.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MenuTypeEnum;
import com.coolcollege.intelligent.common.enums.PlatFormTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.CommonNodeUtils;
import com.coolcollege.intelligent.dao.menu.SysMenuExtendMapper;
import com.coolcollege.intelligent.dao.menu.SysMenuMapper;
import com.coolcollege.intelligent.dao.menu.SysRoleMenuMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.menu.SysMenuDO;
import com.coolcollege.intelligent.model.menu.SysMenuExtendDO;
import com.coolcollege.intelligent.model.menu.SysRoleMenuDO;
import com.coolcollege.intelligent.model.menu.request.AppMenuAddRequest;
import com.coolcollege.intelligent.model.menu.request.AppMenuDeleteRequest;
import com.coolcollege.intelligent.model.menu.request.AppMenuModifyRequest;
import com.coolcollege.intelligent.model.menu.vo.MenuAuthVO;
import com.coolcollege.intelligent.model.menu.vo.MenuTreeVO;
import com.coolcollege.intelligent.model.menu.vo.RoleMenuAuthVO;
import com.coolcollege.intelligent.model.platform.PlatformExpandInfoDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.VO.SysMenuVO;
import com.coolcollege.intelligent.model.system.dto.AppMenuDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.bosspackage.EnterprisePackageService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.platform.PlatformExpandInfoService;
import com.coolcollege.intelligent.service.system.SysMenuService;
import com.coolcollege.intelligent.util.AIUserTool;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.sync.conf.Role.MASTER;

/**
 * 菜单serviceImpl
 *
 * @author 首亮
 */
@Service
@Slf4j
public class SysMenuServiceImpl implements SysMenuService {

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private SysMenuMapper sysMenuMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;
    @Resource
    private SysMenuExtendMapper sysMenuExtendMapper;
    @Resource
    private PlatformExpandInfoService platformExpandInfoService;

    @Resource
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private EnterpriseSettingService enterpriseSettingService;
    @Resource
    private EnterprisePackageService enterprisePackageService;

    private static final long DEFAULT_PARENT_ID = 0L;

    @Value("${spring.profiles.active}")
    private String env;

    /**
     * 只有线上环境才会有菜单环境筛选。默认返回Null，sql查询时不填加环境判断
     * @return
     */
    private String getEnv() {
        if (Constants.ONLINE_ENV.equals(env)) {
            return env;
        }
        return null;
    }

    @Override
    public List<SysMenuDO> selectMenuAll(String eid, List<Long> parentIds, String platformType, String env) {
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        EnterpriseSettingDO setting = enterpriseSettingService.selectByEnterpriseId(eid);
        //如果企业没有套餐。默认是基础套餐
        Long currentPackage = config.getCurrentPackage() == null ? Constants.LONG_ONE : config.getCurrentPackage();
        List<Long> menuIds = enterprisePackageService.getMenuIdListByPackageId(currentPackage, platformType, config.getAppType(), setting.getAccessCoolCollege());
        //如果菜单列表为空，返回所有菜单
        if (CollectionUtils.isEmpty(menuIds)) {
            return sysMenuMapper.selectMenuAll(parentIds, platformType, env);
        }
        return getMenuByPackage(menuIds, platformType, env, false);

    }

    @Override
    public List<SysMenuDO> selectOldMenuAll(String eid, List<Long> parentIds, String platformType, String env) {
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        EnterpriseSettingDO setting = enterpriseSettingService.selectByEnterpriseId(eid);
        //如果企业没有套餐。默认是基础套餐
        Long currentPackage = config.getCurrentPackage() == null ? Constants.LONG_ONE : config.getCurrentPackage();
        List<Long> menuIds = enterprisePackageService.getMenuIdListByPackageId(currentPackage, platformType, config.getAppType(), setting.getAccessCoolCollege());
        //如果菜单列表为空，返回所有菜单
        if (CollectionUtils.isEmpty(menuIds)) {
            return sysMenuMapper.selectMenuAllOld(parentIds, platformType, env);
        }
        return getMenuByPackage(menuIds, platformType, env, true);

    }

    private List<SysMenuDO> getMenuByPackage(List<Long> menuIds, String platformType, String env, Boolean isOld) {
        Set<Long> packageAllMenuIds = new HashSet<>(menuIds);
//            List<Long> packageAllMenuIds = new ArrayList<>(menuIds);
        //获得套餐内所有菜单的父级菜单以及子菜单nag
        List<Long> packageParentIds = getMenuAllParent(menuIds, platformType, env, isOld);
        List<Long> packageChildIds = getMenuAllChild(menuIds, platformType, env, isOld);
        packageAllMenuIds.addAll(packageParentIds);
        packageAllMenuIds.addAll(packageChildIds);

        //获得所有权限类型的菜单
        List<SysMenuDO> authorityMenus = new ArrayList<>();
        if (isOld) {
            authorityMenus = sysMenuMapper.selectMenuByMenuTypeOld(platformType, MenuTypeEnum.AUTH.getCode(), getEnv());
        } else {
            authorityMenus = sysMenuMapper.selectMenuByMenuType(platformType, MenuTypeEnum.AUTH.getCode(), getEnv());
        }
        //过滤获得父级菜单在套餐内的权限
        authorityMenus.forEach(auth -> {
            if (packageParentIds.contains(auth.getParentId())) {
                packageAllMenuIds.add(auth.getId());
            }
        });
        List<Long> allMenuIds = new ArrayList<>(packageAllMenuIds);
        List<SysMenuDO> result= new ArrayList<>();
        if (isOld) {
            result = sysMenuMapper.selectMenuByIdsOld(allMenuIds, platformType, env);
        } else {
            result = sysMenuMapper.selectMenuByIds(allMenuIds, platformType, env);
        }
        return result;
    }

    /**
     * 获得菜单列表的所有父菜单
     * @param menuIds
     * @param platformType
     * @param env
     * @param isOld
     * @author: xugangkun
     * @return java.util.List<java.lang.Long>
     * @date: 2022/4/6 17:45
     */
    private List<Long> getMenuAllParent(List<Long> menuIds, String platformType, String env, Boolean isOld) {
        Set<Long> allParentIds = new HashSet<>(menuIds);
        List<Long> currMenuIds = new ArrayList<>(menuIds);
        //为防止出现死循环，使用递归。设置最多循环10次，即只支持到10级菜单
        for (int i = 0; i < Constants.INDEX_TEN; i++) {
            List<Long> packageParentIds = new ArrayList<>();
            if (isOld) {
                packageParentIds = sysMenuMapper.selectParentIdByIdsOld(currMenuIds, platformType, env);
            } else {
                packageParentIds = sysMenuMapper.selectParentIdByIds(currMenuIds, platformType, env);
            }
            packageParentIds.remove(Constants.LONG_ZERO);
            if (CollectionUtils.isEmpty(packageParentIds)) {
                break;
            }
            allParentIds.addAll(packageParentIds);
            currMenuIds = packageParentIds;
        }
        List<Long> result = new ArrayList<>(allParentIds);
        return result;
    }

    /**
     * 获得菜单列表的所有子菜单
     * @param menuIds
     * @param platformType
     * @param env
     * @param isOld
     * @author: xugangkun
     * @return java.util.List<java.lang.Long>
     * @date: 2022/4/6 17:45
     */
    private List<Long> getMenuAllChild(List<Long> menuIds, String platformType, String env, Boolean isOld) {
        Set<Long> allChildIds = new HashSet<>(menuIds);
        List<Long> currMenuIds = new ArrayList<>(menuIds);
        //为防止出现死循环，使用递归。设置最多循环10次，即只支持到10级菜单
        for (int i = 0; i < Constants.INDEX_TEN; i++) {
            List<Long> packageChildIds = new ArrayList<>();
            if (isOld) {
                packageChildIds = sysMenuMapper.selectByIdByParentIdOld(currMenuIds, platformType, env);
            } else {
                packageChildIds = sysMenuMapper.selectByIdByParentId(currMenuIds, platformType, env);
            }
            if (CollectionUtils.isEmpty(packageChildIds)) {
                break;
            }
            allChildIds.addAll(packageChildIds);
            currMenuIds = packageChildIds;
        }
        List<Long> result = new ArrayList<>(allChildIds);
        return result;
    }


    @Override
    public List<MenuTreeVO> getUserMenus(String enterpriseId, String userId,CurrentUser user,Boolean isOld) {
        DataSourceHelper.reset();
        List<SysMenuDO> sysMenuDOList;
        if(isOld){
            sysMenuDOList= selectOldMenuAll(enterpriseId, null, PlatFormTypeEnum.PC.getCode(), getEnv());
        }else {
            sysMenuDOList= selectMenuAll(enterpriseId, null, PlatFormTypeEnum.PC.getCode(), getEnv());
        }
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        sysMenuDOList = removeUserExportMenu(enterpriseId, sysMenuDOList, config.getAppType());
        DataSourceHelper.changeToMy();
        // 查询当前用户优先级最高的角色
        SysRoleDO role = getHighestRole(enterpriseId, user.getUserId());
        if (Objects.isNull(role)) {
            return new ArrayList<>();
        }
        Long rootId = 0L;
        //如果是管理员或者门店通应用有所有的权限
        if (AppTypeEnum.ONE_PARTY_APP.getValue().equals(user.getAppType()) || MASTER.getRoleEnum().equals(role.getRoleEnum())) {
            return menuChild(enterpriseId, rootId, sysMenuDOList);
        }
        List<SysRoleMenuDO> sysRoleMenuDOList;
        if(isOld){
            sysRoleMenuDOList = sysRoleMenuMapper.listSysRoleMenuByRoleIdOld(enterpriseId, role.getId(),PlatFormTypeEnum.PC.getCode());
        }else {
            sysRoleMenuDOList = sysRoleMenuMapper.listSysRoleMenuByRoleId(enterpriseId, role.getId(),PlatFormTypeEnum.PC.getCode());
        }
        if (CollectionUtils.isEmpty(sysRoleMenuDOList)) {
            return new ArrayList<>();
        }
        //将非权限节点过滤
        List<Long> anthMenuIdList = sysMenuDOList.stream()
                .filter(data -> StringUtils.isNotBlank(data.getType()))
                .map(SysMenuDO::getId)
                .collect(Collectors.toList());
        List<Long> roleMenuIdList = sysRoleMenuDOList.stream()
                .map(SysRoleMenuDO::getMenuId)
                .filter(anthMenuIdList::contains)
                .collect(Collectors.toList());
        Set<Long> authMenuSet = new HashSet<>();
        //1倒推菜单列表 2.转换成树`
        if (CollectionUtils.isEmpty(roleMenuIdList)) {
            return new ArrayList<>();
        }
        authMenuSet.addAll(roleMenuIdList);
        Map<Long, Long> idMap = sysMenuDOList.stream()
                .filter(a -> a.getId() != null && a.getParentId() != null)
                .collect(Collectors.toMap(SysMenuDO::getId, SysMenuDO::getParentId));
        for (Long menuId : roleMenuIdList) {
            getParentNode(menuId, idMap, authMenuSet);
        }
        List<SysMenuDO> collect = sysMenuDOList.stream()
                .filter(data -> authMenuSet.contains(data.getId()))
                .collect(Collectors.toList());
        return menuChild(enterpriseId, rootId, collect);

    }
    @Override
    public List<MenuTreeVO> getEnterpriseMenus(String enterpriseId) {
        Long rootId = 0L;
        DataSourceHelper.reset();
        List<SysMenuDO> sysMenuDOList= selectMenuAll(enterpriseId, null, PlatFormTypeEnum.PC.getCode(), getEnv());
        return menuChild(enterpriseId, rootId, sysMenuDOList);
    }

        private void getParentNode(Long menuId, Map<Long, Long> idMap, Set<Long> authMenuList) {
        Long parentId = idMap.get(menuId);
        authMenuList.add(parentId);
        if (parentId!=null&&parentId != 0) {
            getParentNode(parentId, idMap, authMenuList);
        }
    }

    @Override
    public List<MenuTreeVO> getAllMenus() {
        Long rootId = 0L;
        List<SysMenuDO> sysMenuDOList = sysMenuMapper.selectMenuAll(null,PlatFormTypeEnum.PC.getCode(), null);
        return menuChild(null, rootId, sysMenuDOList);
    }

    @Override
    public List<MenuAuthVO> getMenuAuthList(Long id,String menuPlatFormType) {
        List<SysMenuDO> sysMenuDOList = sysMenuMapper.selectMenuAll(Collections.singletonList(id),menuPlatFormType, null);
        return ListUtils.emptyIfNull(sysMenuDOList)
                .stream()
                .filter(data -> data.getMenuType()==2)
                .map(this::mapMenuAuthVO)
                .collect(Collectors.toList());
    }

    private MenuAuthVO mapMenuAuthVO(SysMenuDO data) {
        MenuAuthVO vo = new MenuAuthVO();
        vo.setId(data.getId());
        vo.setType(data.getType());
        vo.setName(data.getName());
        vo.setEnv(data.getEnv());
        return vo;
    }

    /**
     * 将list 以rootId为跟节点转换为树
     *
     * @param rootId
     * @param sysMenuDOList
     * @return
     */
    private List<MenuTreeVO> menuChild(String enterpriseId, Long rootId, List<SysMenuDO> sysMenuDOList) {

        Map<Long, List<SysMenuDO>> parentGroup = ListUtils.emptyIfNull(sysMenuDOList)
                .stream()
                .collect(Collectors.groupingBy(SysMenuDO::getParentId));
        List<SysMenuDO> menuDOList = parentGroup.get(rootId);
        //循环
        if (CollectionUtils.isEmpty(menuDOList)) {
            return Collections.emptyList();
        }
        List<MenuTreeVO> voList = mapSysMenuToMenuTree(enterpriseId, menuDOList);
        List<MenuTreeVO> treeVOList = new LinkedList<>(voList);
        for (MenuTreeVO data : treeVOList) {
            getChild(enterpriseId, data, parentGroup);
        }
        return treeVOList;
    }

    private void getChild(String enterpriseId, MenuTreeVO data, Map<Long, List<SysMenuDO>> parentGroup) {

        List<SysMenuDO> sysMenuDOList = parentGroup.get(data.getId());

        List<SysMenuDO> parentMenuList = ListUtils.emptyIfNull(sysMenuDOList).stream()
                .filter(menu -> MenuTypeEnum.MENU.getCode().equals(menu.getMenuType()))
                .collect(Collectors.toList());
        List<SysMenuDO> parentAuthList = ListUtils.emptyIfNull(sysMenuDOList).stream()
                .filter(menu -> MenuTypeEnum.AUTH.getCode().equals(menu.getMenuType()))
                .collect(Collectors.toList());
        //属于菜单下时候
        if (CollectionUtils.isNotEmpty(parentMenuList)) {
            List<MenuTreeVO> voList = mapSysMenuToMenuTree(enterpriseId, parentMenuList);
            List<String> authList = parentMenuList.stream()
                    .map(SysMenuDO::getType)
                    .collect(Collectors.toList());
            data.setAuthorityList(authList);
            List<MenuTreeVO> menuList = voList.stream()
                    .filter(vo -> MenuTypeEnum.MENU.getCode().equals(vo.getMenuType()))
                    .collect(Collectors.toList());
            data.setChildren(menuList);
            voList.forEach(child -> {
                getChild(enterpriseId, child, parentGroup);
            });
        }
        //数据是权限的的时候
        if (CollectionUtils.isNotEmpty(parentAuthList)) {
            List<String> authList = parentAuthList.stream()
                    .map(SysMenuDO::getType)
                    .collect(Collectors.toList());
            data.setAuthorityList(authList);
        }
    }

    private List<MenuTreeVO> mapSysMenuToMenuTree(String enterpriseId, List<SysMenuDO> sysMenuDOList) {

        List<Long> menuIdList = ListUtils.emptyIfNull(sysMenuDOList).stream().map(SysMenuDO::getId).collect(Collectors.toList());
        Map<Long, SysMenuExtendDO> menuExtendMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(enterpriseId) && CollectionUtils.isNotEmpty(menuIdList)) {
            DataSourceHelper.reset();
            EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(config.getDbName());
            List<SysMenuExtendDO> menuExtendDOList = sysMenuExtendMapper.listByMenuIdList(enterpriseId, menuIdList);
            menuExtendMap = ListUtils.emptyIfNull(menuExtendDOList).stream()
                    .collect(Collectors.toMap(SysMenuExtendDO::getMenuId, data -> data, (a, b) -> a));
        }
        Map<Long, SysMenuExtendDO> finalMenuExtendMap = menuExtendMap;
        return sysMenuDOList.stream()
                .sorted(Comparator.comparing(SysMenuDO::getSort))
                .map(child -> {
                    MenuTreeVO vo = new MenuTreeVO();
                    vo.setId(child.getId());
                    vo.setParentId(child.getParentId());
                    vo.setName(child.getName());
                    vo.setCode(child.getType());
                    vo.setPath(child.getPath());
                    vo.setComponent(child.getComponent());
                    vo.setTarget(child.getTarget());
                    vo.setIcon(child.getIcon());
                    vo.setMenuType(child.getMenuType());
                    vo.setEnv(child.getEnv());
                    vo.setCommonFunctionsIcon(child.getCommonFunctionsIcon());
                    vo.setType(child.getType());
                    vo.setSort(child.getSort());
                    vo.setPlatform(child.getPlatform());
                    vo.setLabel(child.getLabel());
                    if(finalMenuExtendMap != null && finalMenuExtendMap.get(child.getId()) != null){
                        SysMenuExtendDO menuExtendDO = finalMenuExtendMap.get(child.getId());
                        vo.setDefineName(menuExtendDO.getDefineName());
                        vo.setMenuPic(menuExtendDO.getMenuPic());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleMenuAuthVO> getMenusByRole(String enterpriseId,  SysRoleDO role, PlatFormTypeEnum platFormType) {

        //获取列表中的集合
        List<SysMenuVO> list = new ArrayList<>();
        DataSourceHelper.reset();
        List<SysMenuDO> sysMenuDos = selectMenuAll(enterpriseId, null,platFormType.getCode(), getEnv());
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        sysMenuDos = removeUserExportMenu(enterpriseId, sysMenuDos, config.getAppType());
        DataSourceHelper.changeToMy();
        if(role==null){
            List<SysMenuVO> sysMenuVOList = getSysMenuVOList(enterpriseId, sysMenuDos);
            return getAllMenuList(enterpriseId, sysMenuVOList);
        }
        Long roleId = role.getId();
        //管理员=
        if (MASTER.getRoleEnum().equals(role.getRoleEnum())) {
            sysMenuDos.iterator().forEachRemaining(p -> {
                SysMenuVO sysMenuVO = new SysMenuVO();
                BeanUtils.copyProperties(p, sysMenuVO);
                sysMenuVO.setChecked("1");
                list.add(sysMenuVO);
            });
            return getAllMenuList(enterpriseId, list);
        }

        List<SysRoleMenuDO> sysRoleMenuDOList = sysRoleMenuMapper.listSysRoleMenuByRoleId(enterpriseId, roleId, platFormType.getCode());
        if (CollectionUtils.isEmpty(sysRoleMenuDOList)) {
            List<SysMenuVO> sysMenuVOList = getSysMenuVOList(enterpriseId, sysMenuDos);
            return getAllMenuList(enterpriseId, sysMenuVOList);
        }
        //查询出权限对应的菜单所有父节点ID
        List<Long> menuParentList = getMenuParent(sysRoleMenuDOList,sysMenuDos);

        List<Long> menuIds = sysRoleMenuDOList.stream().map(SysRoleMenuDO::getMenuId).distinct().collect(Collectors.toList());
        ListUtils.emptyIfNull(sysMenuDos).forEach(p -> {
            SysMenuVO sysMenuVO = new SysMenuVO();
            BeanUtils.copyProperties(p, sysMenuVO);
            if (menuIds.contains(p.getId())||menuParentList.contains(p.getId())) {
                sysMenuVO.setChecked("1");
            } else {
                sysMenuVO.setChecked("0");
            }
            list.add(sysMenuVO);
        });

        return getAllMenuList(enterpriseId, list);
    }

    @Override
    public List<SysMenuVO> getMenuVO(List<SysMenuDO> menuList, List<SysRoleMenuDO> authList) {
        //获取列表中的集合
        List<SysMenuVO> list = new ArrayList<>();
        //查询出权限对应的菜单所有父节点ID
        List<Long> menuParentList = getMenuParent(authList, menuList);

        List<Long> menuIds = authList.stream().map(SysRoleMenuDO::getMenuId).distinct().collect(Collectors.toList());
        ListUtils.emptyIfNull(menuList).forEach(p -> {
            SysMenuVO sysMenuVO = new SysMenuVO();
            BeanUtils.copyProperties(p, sysMenuVO);
            if (menuIds.contains(p.getId())||menuParentList.contains(p.getId())) {
                sysMenuVO.setChecked("1");
            } else {
                sysMenuVO.setChecked("0");
            }
            list.add(sysMenuVO);
        });
        return list;
    }

    @Override
    public List<RoleMenuAuthVO> getMenusByRole(String enterpriseId, String roleId) {
        DataSourceHelper.reset();
        List<SysMenuDO> sysMenuDos = selectMenuAll(enterpriseId, null,PlatFormTypeEnum.PC.getCode(), getEnv());
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        if(StringUtils.isBlank(roleId)){
            sysMenuDos = removeUserExportMenu(enterpriseId, sysMenuDos, config.getAppType());
            List<SysMenuVO> sysMenuVOList = getSysMenuVOList(enterpriseId, sysMenuDos);
            return getAllMenuList(enterpriseId, sysMenuVOList);
        }
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, Long.valueOf(roleId));
       return getMenusByRole(enterpriseId, role, PlatFormTypeEnum.PC);

    }


    @Override
    public List<AppMenuDTO> getAppMenusByRole(String eid, SysRoleDO roleDO) {

        DataSourceHelper.reset();
        List<SysMenuDO> sysMenuDos = selectMenuAll(eid, null,PlatFormTypeEnum.APP.getCode(), getEnv());
        if(Role.isAdmin(roleDO.getRoleEnum())){
           return sysMenuDos.stream()
                    .map(data-> mapAppMenuCheckDTO(data,true))
                    .collect(Collectors.toList());
        }
        DataSourceHelper.changeToMy();
        List<SysRoleMenuDO> sysRoleMenuDOList = sysRoleMenuMapper.listSysRoleMenuByRoleId(eid, roleDO.getId(), PlatFormTypeEnum.APP.getCode());
        List<Long> authMenuIdList = ListUtils.emptyIfNull(sysRoleMenuDOList)
                .stream()
                .map(SysRoleMenuDO::getMenuId)
                .collect(Collectors.toList());
        return ListUtils.emptyIfNull(sysMenuDos)
                .stream()
                .map(data->mapAppMenuDTO(data,authMenuIdList))
                .collect(Collectors.toList());
    }



    @Override
    public List<AppMenuDTO> getAppMenusByUser(String enterpriseId, String userId, CurrentUser currentUser) {
        if(StringUtils.isBlank(userId)){
            DataSourceHelper.reset();
            List<SysMenuDO> sysMenuDos = selectMenuAll(enterpriseId, null,PlatFormTypeEnum.APP.getCode(), getEnv());
                return sysMenuDos.stream()
                        .map(data-> mapAppMenuCheckDTO(data,false))
                        .collect(Collectors.toList());
        }
        // 查询当前用户优先级最高的角色
        SysRoleDO sysRoleDoByUserId = getHighestRole(enterpriseId, currentUser.getUserId());
        return getAppMenusByRole(enterpriseId,sysRoleDoByUserId);
    }

    @Override
    public List<AppMenuDTO> getAppMenusByUserNew(String enterpriseId, String userId, CurrentUser currentUser) {
        if(StringUtils.isBlank(userId)){
            DataSourceHelper.reset();
            List<SysMenuDO> sysMenuDos = selectMenuAll(enterpriseId, null,PlatFormTypeEnum.APP.getCode(), getEnv());
            return getChildMenuApp(DEFAULT_PARENT_ID,sysMenuDos);
        }
        // 查询当前用户优先级最高的角色
        SysRoleDO sysRoleDoByUserId = getHighestRole(enterpriseId, currentUser.getUserId());
        return getAppMenusByRoleNew(enterpriseId,sysRoleDoByUserId.getId());
    }

    @Override
    public List<AppMenuDTO> getAppMenusByRoleNew(String eid, Long roleId) {
        DataSourceHelper.reset();
        List<SysMenuDO> sysMenuDos = selectMenuAll(eid, null,PlatFormTypeEnum.APP.getCode(), getEnv());
        if(CollectionUtils.isEmpty(sysMenuDos)){
            return new ArrayList<>();
        }
        if(Role.isAdminById(String.valueOf(roleId))){
            sysMenuDos.stream().forEach(e -> {
                e.setIsChecked(true);
            });
            return getChildMenuApp(DEFAULT_PARENT_ID,sysMenuDos);
        }
        DataSourceHelper.changeToMy();
        //查询出权限对应的菜单所有父节点ID
        List<SysRoleMenuDO> sysRoleMenuDOList = sysRoleMenuMapper.listSysRoleMenuByRoleId(eid, roleId, PlatFormTypeEnum.APP.getCode());
        List<Long> menuParentList = getMenuParent(sysRoleMenuDOList,sysMenuDos);
        List<Long> authMenuIdList = ListUtils.emptyIfNull(sysRoleMenuDOList)
                .stream()
                .map(SysRoleMenuDO::getMenuId)
                .collect(Collectors.toList());
        ListUtils.emptyIfNull(sysMenuDos)
                .stream().forEach(e -> {
            if (authMenuIdList.contains(e.getId())||menuParentList.contains(e.getId())) {
                e.setIsChecked(true);
            } else {
                e.setIsChecked(false);
            }
        });
        return getChildMenuApp(DEFAULT_PARENT_ID,sysMenuDos);
    }


    private AppMenuDTO mapAppMenuDTO(SysMenuDO sysMenuDO,List<Long> authMenuIdList){

        AppMenuDTO appMenuDTO =new AppMenuDTO();
        if(CollectionUtils.isNotEmpty(authMenuIdList)&&authMenuIdList.contains(sysMenuDO.getId())){
            appMenuDTO.setChecked(true);
        }else {
            appMenuDTO.setChecked(false);
        }
        appMenuDTO.setId(sysMenuDO.getId());
        appMenuDTO.setMenuKey(sysMenuDO.getType());
        appMenuDTO.setMenuName(sysMenuDO.getName());
        return appMenuDTO;
    }
    private AppMenuDTO mapAppMenuCheckDTO(SysMenuDO sysMenuDO, Boolean checked){
        AppMenuDTO appMenuDTO =new AppMenuDTO();
        appMenuDTO.setId(sysMenuDO.getId());
        appMenuDTO.setChecked(checked);
        appMenuDTO.setMenuKey(sysMenuDO.getType());
        appMenuDTO.setMenuName(sysMenuDO.getName());
        return appMenuDTO;
    }

    private List<Long> getMenuParent(List<SysRoleMenuDO> sysRoleMenuList,List<SysMenuDO> sysMenuDOS){
        List<Long> menuParentList = new ArrayList<>();

        Map<Long, SysMenuDO> menuDOMap = ListUtils.emptyIfNull(sysMenuDOS).stream()
                .collect(Collectors.toMap(SysMenuDO::getId, menu -> menu, (a, b) -> a));
        Map<Long, List<SysMenuDO>> parentIdMap = ListUtils.emptyIfNull(sysMenuDOS).stream()
                .collect(Collectors.groupingBy(SysMenuDO::getParentId));
        ListUtils.emptyIfNull(sysRoleMenuList).forEach(data->{
            SysMenuDO sysMenuDO = menuDOMap.get(data.getMenuId());
            if(sysMenuDO!=null){
                Long parentId = sysMenuDO.getParentId();
                parentIdRecursion(menuParentList,parentId,menuDOMap,parentIdMap);
            }
        });
        return  ListUtils.emptyIfNull(menuParentList)
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }
    private void parentIdRecursion(List<Long> menuParentList,
                                   Long parentId,
                                   Map<Long, SysMenuDO> menuDOMap ,
                                   Map<Long, List<SysMenuDO>> parentIdMap){
        SysMenuDO sysMenuDO = menuDOMap.get(parentId);
        if(CollectionUtils.isNotEmpty(parentIdMap.get(parentId))&&sysMenuDO!=null){
            menuParentList.add(sysMenuDO.getId());
            parentIdRecursion(menuParentList,sysMenuDO.getParentId(),menuDOMap,parentIdMap);
        }
    }

    @Override
    public Boolean addMenuOrAuth(Long parentId, String name, String path, String type, String target, String component, String icon, MenuTypeEnum menuTypeEnum,
                                 String env, String label) {

        SysMenuDO sysMenuDO = new SysMenuDO();
        if (parentId != 0) {
            SysMenuDO parentMenu = sysMenuMapper.selectMenu(parentId);
            if (parentMenu == null) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "不存在的父ID");
            }
        }
        sysMenuDO.setParentId(parentId);
        sysMenuDO.setName(name);
        sysMenuDO.setCode(name);
        sysMenuDO.setPath(path);
        sysMenuDO.setType(type);
        sysMenuDO.setSource("menu");
        sysMenuDO.setAction(1);
        sysMenuDO.setPlatform("PC");
        //排序先差出菜单中最大的排序 +1
        Integer sort = sysMenuMapper.selectMaxSort();
        sysMenuDO.setSort(sort + 1);
        sysMenuDO.setComponent(component);
        sysMenuDO.setTarget(target);
        sysMenuDO.setIcon(icon);
        sysMenuDO.setMenuType(menuTypeEnum.getCode());
        sysMenuDO.setEnv(env);
        sysMenuDO.setLabel(label);
        sysMenuMapper.insertMenu(sysMenuDO);
        return true;
    }

    @Override
    public Boolean modifyMenuOrAuth(Long id, String name, String path, String type, String target, String component, String icon, String env,String commonFunctionsIcon,
                                    String label) {
        SysMenuDO sysMenuDO = sysMenuMapper.selectMenu(id);
        if (sysMenuDO == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "数据不存在!");
        }
        sysMenuDO.setName(name);
        sysMenuDO.setPath(path);
        sysMenuDO.setType(type);
        sysMenuDO.setComponent(component);
        sysMenuDO.setTarget(target);
        sysMenuDO.setIcon(icon);
        sysMenuDO.setEnv(env);
        sysMenuDO.setCommonFunctionsIcon(commonFunctionsIcon);
        sysMenuDO.setLabel(label);
        sysMenuMapper.updateMenu(sysMenuDO);
        return true;
    }

    @Override
    public Boolean deleteMenuOrAuthById(Long id) {

        /**
         * 1.判断是否有子项将子项全部删除
         * 2.需要去业务库删除角色分配权限表中包含删除项的数据
         */
        List<SysMenuDO> sysMenuDOList = sysMenuMapper.selectMenuAll(null,PlatFormTypeEnum.PC.getCode(), null);
        List<Long> idList = ListUtils.emptyIfNull(sysMenuDOList)
                .stream()
                .map(SysMenuDO::getId)
                .collect(Collectors.toList());
        Map<Long, List<Long>> parentGroupMap = ListUtils.emptyIfNull(sysMenuDOList)
                .stream()
                .collect(Collectors.groupingBy(SysMenuDO::getParentId, Collectors.mapping(SysMenuDO::getId, Collectors.toList())));
        List<Long> allChildList = CommonNodeUtils.getAllChildListContainSelf(0L,id, idList, parentGroupMap);
        sysMenuMapper.batchDeleteMenu(allChildList);
        return true;
    }

    @Override
    public Boolean sortMenu(List<Long> idList) {

        //给传入的list重新设置排序值后更新
        List<SysMenuDO> list = new LinkedList<>();
        for (int i = 1; i <= idList.size(); i++) {
            SysMenuDO sysMenuDO = new SysMenuDO();
            sysMenuDO.setSort(i);
            sysMenuDO.setId(idList.get(i - 1));
            list.add(sysMenuDO);
        }
        sysMenuMapper.updateMenuSort(list);
        return true;
    }

    @Override
    public Boolean addAppMenu(AppMenuAddRequest request) {
        SysMenuDO sysMenuDO =new SysMenuDO();
        sysMenuDO.setParentId(request.getParentId());
        sysMenuDO.setName(request.getName());
        sysMenuDO.setType(request.getKey());
        sysMenuDO.setIcon(request.getIcon());
        sysMenuDO.setPlatform(PlatFormTypeEnum.NEW_APP.getCode());
        sysMenuDO.setSource("menu");
        sysMenuDO.setAction(1);
        sysMenuDO.setMenuType(request.getMenuType());
        sysMenuDO.setCode(request.getName());
        sysMenuDO.setPath(request.getPath());
        Integer sort = sysMenuMapper.selectMaxSort();
        sysMenuDO.setSort(sort + 1);
        sysMenuDO.setEnv(request.getEnv());
        sysMenuDO.setLabel(request.getLabel());
        sysMenuMapper.insertMenu(sysMenuDO);
        return true;
    }

    @Override
    public Boolean modifyAppMenu(AppMenuModifyRequest request) {
        DataSourceHelper.reset();
        SysMenuDO sysMenuDO =new SysMenuDO();
        sysMenuDO.setId(request.getId());
        sysMenuDO.setName(request.getName());
        sysMenuDO.setType(request.getKey());
        sysMenuDO.setParentId(request.getParentId());
        sysMenuDO.setMenuType(request.getMenuType());
        sysMenuDO.setEnv(request.getEnv());
        sysMenuDO.setPath(request.getPath());
        sysMenuDO.setIcon(request.getIcon());
        sysMenuDO.setLabel(request.getLabel());
        sysMenuMapper.updateMenu(sysMenuDO);
        return true;
    }

    @Override
    public Boolean deleteAppMenu(AppMenuDeleteRequest request) {
         sysMenuMapper.batchDeleteMenu(Collections.singletonList(request.getId()));
         return true;
    }

    @Override
    public List<AppMenuDTO> ListAppMenu() {
        List<SysMenuDO> sysMenuDos = sysMenuMapper.selectMenuAll(null,PlatFormTypeEnum.NEW_APP.getCode(), null);
        List<SysMenuDO> sysMenuDOList = ListUtils.emptyIfNull(sysMenuDos)
                .stream()
                .filter(data -> 1 == data.getMenuType())
                .collect(Collectors.toList());
        return getChildMenuApp(DEFAULT_PARENT_ID,sysMenuDOList);
    }

    @Override
    public List<RoleMenuAuthVO> getAppMenuList(String enterpriseId, String roleId, CurrentUser currentUser) {
        DataSourceHelper.reset();
        List<SysMenuDO> sysMenuDos = selectMenuAll(enterpriseId, null,PlatFormTypeEnum.NEW_APP.getCode(), getEnv());
        DataSourceHelper.changeToMy();
        if(StringUtils.isBlank(roleId)){
            List<SysMenuVO> sysMenuVOList = getSysMenuVOList(enterpriseId, sysMenuDos);
            return getAllMenuList(enterpriseId, sysMenuVOList);
        }
        SysRoleDO role = sysRoleMapper.getRole(enterpriseId, Long.valueOf(roleId));
        return getMenusByRole(enterpriseId, role, PlatFormTypeEnum.NEW_APP);
    }

    @Override
    public List<MenuTreeVO> getAppAuthList(String enterpriseId, String userId, CurrentUser user,Boolean isOld) {
        DataSourceHelper.reset();
        List<SysMenuDO> sysMenuDOList;
        if(isOld){
            sysMenuDOList = selectOldMenuAll(enterpriseId, null, PlatFormTypeEnum.NEW_APP.getCode(), getEnv());
        }else {
            sysMenuDOList = selectMenuAll(enterpriseId, null, PlatFormTypeEnum.NEW_APP.getCode(), getEnv());
        }
        DataSourceHelper.changeToMy();
        // 查询当前用户优先级最高的角色
        SysRoleDO role = getHighestRole(enterpriseId, user.getUserId());
        if (Objects.isNull(role)) {
            return new ArrayList<>();
        }
        Long rootId = 0L;
        //如果是管理员或者门店通应用有所有的权限
        if (AppTypeEnum.ONE_PARTY_APP.getValue().equals(user.getAppType()) || MASTER.getRoleEnum().equals(role.getRoleEnum())) {
            return menuChild(enterpriseId, rootId, sysMenuDOList);
        }
        List<SysRoleMenuDO> sysRoleMenuDOList = sysRoleMenuMapper.listSysRoleMenuByRoleId(enterpriseId, role.getId(),PlatFormTypeEnum.NEW_APP.getCode());
        if (CollectionUtils.isEmpty(sysRoleMenuDOList)) {
            return new ArrayList<>();
        }
        //将非权限节点过滤
        List<Long> anthMenuIdList = sysMenuDOList.stream()
                .filter(data -> StringUtils.isNotBlank(data.getType()))
                .map(SysMenuDO::getId)
                .collect(Collectors.toList());
        List<Long> roleMenuIdList = sysRoleMenuDOList.stream()
                .map(SysRoleMenuDO::getMenuId)
                .filter(anthMenuIdList::contains)
                .collect(Collectors.toList());
        Set<Long> authMenuSet = new HashSet<>();
        //1倒推菜单列表 2.转换成树`
        if (CollectionUtils.isEmpty(roleMenuIdList)) {
            return new ArrayList<>();
        }
        authMenuSet.addAll(roleMenuIdList);
        Map<Long, Long> idMap = sysMenuDOList.stream()
                .filter(a -> a.getId() != null && a.getParentId() != null)
                .collect(Collectors.toMap(SysMenuDO::getId, SysMenuDO::getParentId));
        for (Long menuId : roleMenuIdList) {
            getParentNode(menuId, idMap, authMenuSet);
        }
        List<SysMenuDO> collect = sysMenuDOList.stream()
                .filter(data -> authMenuSet.contains(data.getId()))
                .collect(Collectors.toList());
        return menuChild(enterpriseId, rootId, collect);
    }

    /**
     * rootId作为parentId查询权限树 移动端
     * @Author chenyupeng
     * @Date 2021/7/9
     * @param rootId
     * @param sysMenuDOList
     * @return: java.util.List<com.coolcollege.intelligent.model.system.dto.AppMenuDTO>
     */
    private List<AppMenuDTO> getChildMenuApp(Long rootId, List<SysMenuDO> sysMenuDOList) {

        Map<Long, List<SysMenuDO>> parentGroup = ListUtils.emptyIfNull(sysMenuDOList)
                .stream()
                .collect(Collectors.groupingBy(SysMenuDO::getParentId));
        List<SysMenuDO> menuDOList = parentGroup.get(rootId);
        //循环
        if (CollectionUtils.isEmpty(menuDOList)) {
            return Collections.emptyList();
        }
        List<AppMenuDTO> voList = mapSysMenuToAppMenu(menuDOList);
        List<AppMenuDTO> treeVOList = new LinkedList<>(voList);
        for (AppMenuDTO data : treeVOList) {
            getChildApp(data, parentGroup);
        }
        return treeVOList;
    }
    private List<AppMenuDTO> mapSysMenuToAppMenu(List<SysMenuDO> sysMenuDOList) {

        return sysMenuDOList.stream()
                .sorted(Comparator.comparing(SysMenuDO::getSort))
                .map(child -> {
                    AppMenuDTO dto = new AppMenuDTO();
                    dto.setId(child.getId());
                    dto.setParentId(child.getParentId());
                    dto.setMenuKey(child.getType());
                    dto.setPath(child.getPath());
                    dto.setIcon(child.getIcon());
                    dto.setMenuName(child.getName());
                    dto.setChecked(child.getIsChecked() != null && child.getIsChecked());
                    dto.setEnv(child.getEnv());
                    dto.setLabel(child.getLabel());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void getChildApp(AppMenuDTO data, Map<Long, List<SysMenuDO>> parentGroup) {

        List<SysMenuDO> sysMenuDOList = parentGroup.get(data.getId());

        List<SysMenuDO> parentMenuList = ListUtils.emptyIfNull(sysMenuDOList).stream()
                .filter(menu -> StringUtils.isNotBlank(menu.getType()))
                .collect(Collectors.toList());
        //属于菜单下时候
        if (CollectionUtils.isNotEmpty(parentMenuList)) {
            List<AppMenuDTO> voList = mapSysMenuToAppMenu(parentMenuList);
            List<AppMenuDTO> menuList = voList.stream()
                    .filter(vo -> StringUtils.isNotBlank(vo.getMenuKey()))
                    .collect(Collectors.toList());
            data.setChildren(menuList);
            voList.forEach(child -> getChildApp(child, parentGroup));
        }
    }


    @Override
    public Boolean moveMenu(Long id, Long parentId) {

        //先更新节点的对应关系  再更新同级节点直接的排序
        SysMenuDO sysMenuDO = sysMenuMapper.selectMenu(id);
        if (sysMenuDO == null) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "节点不存在!");
        }
        SysMenuDO parentSysMenuDO = sysMenuMapper.selectMenu(parentId);
        if (parentSysMenuDO == null && parentId != 0) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "父节点不存在!");
        }
        sysMenuMapper.updateMenuMove(id, parentId);
        return true;
    }


    @Override
    public List<RoleMenuAuthVO> getAllMenuList(String enterpriseId, List<SysMenuVO> list) {
        Long rootId=0L;
        //创建一个List集合来存放最终的树状结构数据
        Map<Long, List<SysMenuVO>> parentGroup = ListUtils.emptyIfNull(list)
                .stream()
                .collect(Collectors.groupingBy(SysMenuVO::getParentId));
        List<SysMenuVO> menuDOList = parentGroup.get(rootId);
        //循环
        if (CollectionUtils.isEmpty(menuDOList)) {
            return Collections.emptyList();
        }
        List<RoleMenuAuthVO> voList = mapSysMenuToRoleMenuAuthVO(enterpriseId, menuDOList);
        List<RoleMenuAuthVO> roleMenuAuthVOList = new LinkedList<>(voList);
        for (RoleMenuAuthVO data : roleMenuAuthVOList) {
            getChildRoleMenuAuthVO(enterpriseId, data, parentGroup);
        }
        return roleMenuAuthVOList;
    }

    private List<RoleMenuAuthVO> mapSysMenuToRoleMenuAuthVO(String enterpriseId, List<SysMenuVO> sysMenuDOList) {

        List<Long> menuIdList = ListUtils.emptyIfNull(sysMenuDOList).stream().map(SysMenuDO::getId).collect(Collectors.toList());
        Map<Long, SysMenuExtendDO> menuExtendMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(enterpriseId) && CollectionUtils.isNotEmpty(menuIdList)) {
            DataSourceHelper.reset();
            EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(config.getDbName());
            List<SysMenuExtendDO> menuExtendDOList = sysMenuExtendMapper.listByMenuIdList(enterpriseId, menuIdList);
            menuExtendMap = ListUtils.emptyIfNull(menuExtendDOList).stream()
                    .collect(Collectors.toMap(SysMenuExtendDO::getMenuId, data -> data, (a, b) -> a));
        }
        Map<Long, SysMenuExtendDO> finalMenuExtendMap = menuExtendMap;
        return sysMenuDOList.stream()
                .sorted(Comparator.comparing(SysMenuVO::getSort))
                .map(child -> {
                    RoleMenuAuthVO vo = new RoleMenuAuthVO();
                    vo.setId(child.getId());
                    vo.setParentId(child.getParentId());
                    vo.setChecked(child.getChecked());
                    vo.setName(child.getName());
                    vo.setCode(child.getType());
                    vo.setPath(child.getPath());
                    if(finalMenuExtendMap != null && finalMenuExtendMap.get(child.getId()) != null){
                        SysMenuExtendDO menuExtendDO = finalMenuExtendMap.get(child.getId());
                        vo.setDefineName(menuExtendDO.getDefineName());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private void getChildRoleMenuAuthVO(String enterpriseId, RoleMenuAuthVO data, Map<Long, List<SysMenuVO>> parentGroup) {

        List<SysMenuVO> sysMenuDOList = parentGroup.get(data.getId());
        List<SysMenuVO> menuList = ListUtils.emptyIfNull(sysMenuDOList).stream()
                .filter(menu -> MenuTypeEnum.MENU.getCode().equals(menu.getMenuType()))
                .collect(Collectors.toList());
        List<SysMenuVO> authList = ListUtils.emptyIfNull(sysMenuDOList).stream()
                .filter(menu -> MenuTypeEnum.AUTH.getCode().equals(menu.getMenuType()))
                .collect(Collectors.toList());

        //属于菜单下时候
        if (CollectionUtils.isNotEmpty(menuList)) {
            List<RoleMenuAuthVO> voList = mapSysMenuToRoleMenuAuthVO(enterpriseId, menuList);
            data.setChildren(voList);
            voList.forEach(child -> {
                getChildRoleMenuAuthVO(enterpriseId, child, parentGroup);
            });
        }
        //数据是权限的的时候
        if (CollectionUtils.isNotEmpty(authList)) {
            List<RoleMenuAuthVO> authRoleList = authList.stream()
                    .sorted(Comparator.comparing(SysMenuVO::getSort))
                    .map(auth->{
                        RoleMenuAuthVO roleMenuAuthVO = new RoleMenuAuthVO();
                        roleMenuAuthVO.setId(auth.getId());
                        roleMenuAuthVO.setName(auth.getName());
                        roleMenuAuthVO.setChecked(auth.getChecked());
                        roleMenuAuthVO.setParentId(auth.getParentId());
                        roleMenuAuthVO.setCode(auth.getType());
                        return roleMenuAuthVO;
                    })
                    .collect(Collectors.toList());
            data.setAuthorityList(authRoleList);
        }
    }

    /**
     * 转换list为树形结构
     *
     * @param list
     * @return
     */
    private List<SysMenuVO> getSysMenuVOList(String enterpriseId, List<SysMenuDO> list) {
        List<SysMenuVO> sysMenuVOList = new ArrayList<>();
        list.iterator().forEachRemaining(p -> {
            SysMenuVO sysMenuVO = new SysMenuVO();
            BeanUtils.copyProperties(p, sysMenuVO);
            sysMenuVO.setChecked("0");
            sysMenuVOList.add(sysMenuVO);
        });
        return sysMenuVOList;
    }

    /**
     * 企微上架,需要屏蔽部分企业的导出按钮
     * 注意：使用stream().filter获得的列表对象是新对象，外层接口需要重新指向一边
     * @param eid
     * @param sysMenuDos
     * @author: xugangkun
     * @return void
     * @date: 2021/12/1 16:20
     */
    public List<SysMenuDO> removeUserExportMenu(String eid, List<SysMenuDO> sysMenuDos, String appType) {
        try {
            if (!AppTypeEnum.isQwType(appType)) {
                return sysMenuDos;
            }
            PlatformExpandInfoDO platformExpandInfo = platformExpandInfoService.selectByCode(Constants.USER_EXPORT_WHITELIST);
            if (platformExpandInfo == null || !platformExpandInfo.getValid() || StringUtils.isBlank(platformExpandInfo.getContent())) {
                return sysMenuDos;
            }
            int start = platformExpandInfo.getContent().indexOf(Constants.COMMA + eid + Constants.COMMA);
            if (start < Constants.ZERO) {
                sysMenuDos = sysMenuDos.stream().filter(menu -> !(isShieldExport(menu.getParentId(), menu.getType()) ||
                        (Constants.IMPORT.equals(menu.getType()) && Constants.USER_MANAGE_MENU_ID.equals(menu.getParentId()))))
                        .collect(Collectors.toList());
            }
//            if (!Constants.SENYU_ENTERPRISE_ID.equals(eid)) {
//                sysMenuDos = sysMenuDos.stream().filter(menu -> )
//            }
        } catch (Exception e) {
            log.error("removeUserExportMenu error", e);
        }
        return sysMenuDos;
    }

    /**
     * 过滤获得
     * @param eid
     * @param sysMenuDos
     * @author: xugangkun
     * @return void
     * @date: 2021/12/1 16:20
     */
    public List<SysMenuDO> filterPackageMenu(String eid, List<SysMenuDO> sysMenuDos, String appType) {
        try {
            if (!AppTypeEnum.isQwType(appType)) {
                return sysMenuDos;
            }
            PlatformExpandInfoDO platformExpandInfo = platformExpandInfoService.selectByCode(Constants.USER_EXPORT_WHITELIST);
            if (platformExpandInfo == null || !platformExpandInfo.getValid() || StringUtils.isBlank(platformExpandInfo.getContent())) {
                return sysMenuDos;
            }
            int start = platformExpandInfo.getContent().indexOf(Constants.COMMA + eid + Constants.COMMA);
            if (start < Constants.ZERO) {
                sysMenuDos = sysMenuDos.stream().filter(menu -> !(isShieldExport(menu.getParentId(), menu.getType()) ||
                        (Constants.IMPORT.equals(menu.getType()) && Constants.USER_MANAGE_MENU_ID.equals(menu.getParentId()))))
                        .collect(Collectors.toList());
            }
//            if (!Constants.SENYU_ENTERPRISE_ID.equals(eid)) {
//                sysMenuDos = sysMenuDos.stream().filter(menu -> )
//            }
        } catch (Exception e) {
            log.error("removeUserExportMenu error", e);
        }
        return sysMenuDos;
    }

    /**
     * 判断是否是需要屏蔽的导出. 需要屏蔽返回true
     * @param parentId
     * @param type
     * @author: xugangkun
     * @return java.lang.Boolean
     * @date: 2021/12/24 11:23
     */
    private Boolean isShieldExport(Long parentId, String type) {
        if (Constants.EXPORT.equals(type)) {
            //不屏蔽sop检查项导出
            if (Constants.CHECK_ITEM_MANAGE_MENU_ID.equals(parentId)) {
                return false;
            }
            return true;
        }
        return false;

    }

    @Override
    public List<SysMenuDO> getEnterpriseAllMenus(String enterpriseId) {
        DataSourceHelper.reset();
        List<SysMenuDO> result = new ArrayList<>();
        List<SysMenuDO> pcMenuList = selectMenuAll(enterpriseId, null, PlatFormTypeEnum.PC.getCode(), getEnv());
        List<SysMenuDO> appNewMenuList = selectMenuAll(enterpriseId, null, PlatFormTypeEnum.NEW_APP.getCode(), getEnv());
        if(CollectionUtils.isNotEmpty(pcMenuList)){
            result.addAll(pcMenuList);
        }
        if(CollectionUtils.isNotEmpty(appNewMenuList)){
            result.addAll(appNewMenuList);
        }
        return result;
    }

    /**
     * 查询当前用户优先级最高的角色，如果没有最高优先级的，给未分配的角色
     * @param enterpriseId 企业id
     * @param userId 用户id
     * @return 角色信息
     */
    private SysRoleDO getHighestRole(String enterpriseId, String userId) {
        if(AIEnum.AI_USERID.getCode().equals(userId)){
            return sysRoleMapper.getRoleByRoleEnum(enterpriseId, MASTER.getRoleEnum());
        }
        SysRoleDO role = sysRoleMapper.getHighestPrioritySysRoleDoByUserId(enterpriseId, userId);
        if (Objects.isNull(role)) {
            // 如果没有最高优先级的，给未分配的角色
            role = sysRoleMapper.getRoleByRoleEnum(enterpriseId, Role.EMPLOYEE.getRoleEnum());
        }
        return role;
    }
}
