package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MenuTypeEnum;
import com.coolcollege.intelligent.common.enums.PlatFormTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserAppMenuMapper;
import com.coolcollege.intelligent.dao.menu.SysMenuMapper;
import com.coolcollege.intelligent.dao.menu.SysRoleMenuMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserAppMenuDO;
import com.coolcollege.intelligent.model.enterprise.request.AppMenuCustomizeInfo;
import com.coolcollege.intelligent.model.enterprise.request.AppMenuCustomizeRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseUserAppMenuInfoVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseUserAppMenuVO;
import com.coolcollege.intelligent.model.menu.SysMenuDO;
import com.coolcollege.intelligent.model.menu.SysRoleMenuDO;
import com.coolcollege.intelligent.model.menu.vo.MenuTreeVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.VO.SysMenuVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserAppMenuService;
import com.coolcollege.intelligent.service.system.SysMenuService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
@Service
@Slf4j
public class EnterpriseUserAppMenuServiceImpl implements EnterpriseUserAppMenuService {

    @Resource
    private EnterpriseUserAppMenuMapper enterpriseUserAppMenuMapper;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysMenuService sysMenuService;

    @Value("${spring.profiles.active}")
    private String env;

    private String getEnv() {
        if (Constants.ONLINE_ENV.equals(env)) {
            return env;
        }
        return null;
    }

    @Override
    public EnterpriseUserAppMenuVO getUserAppMenu(String eid) {

        EnterpriseUserAppMenuVO vo =new EnterpriseUserAppMenuVO();
        CurrentUser user = UserHolder.getUser();
        String userId =user.getUserId();
        vo.setUserId(userId);
        PageHelper.clearPage();
        SysRoleDO sysRoleDO =user.getSysRoleDO();
        if(sysRoleDO==null){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "用户没有分配职位！");
        }
        List<SysRoleMenuDO> authRoleMenuDOList = sysRoleMenuMapper.listSysRoleMenuByRoleId(eid, sysRoleDO.getId(),PlatFormTypeEnum.APP.getCode());
        //不是管理并且没有配置菜单权限直接返回结果
        if(!Role.isAdmin(sysRoleDO.getRoleEnum())&& CollectionUtils.isEmpty(authRoleMenuDOList)){
            List<EnterpriseUserAppMenuInfoVO> initMenuList=new ArrayList<>();
            //初始化一个菜单给移动端使用
            initMenuList.add(initEnterpriseUserAppMenuInfoVO(686L,"online","线上巡店",1));
            initMenuList.add(initEnterpriseUserAppMenuInfoVO(691L,"offine","线下巡店",2));
            initMenuList.add(initEnterpriseUserAppMenuInfoVO(693L,"record","巡店记录",3));
            initMenuList.add(initEnterpriseUserAppMenuInfoVO(694L,"display","标准陈列",4));

            vo.setMenuInfoList(initMenuList);
            return vo;
        }
        DataSourceHelper.reset();
//        List<SysMenuDO> sysMenuDOList = sysMenuMapper.selectMenuAll(null, PlatFormTypeEnum.APP.getCode(), getEnv());
        List<SysMenuDO> sysMenuDOList = sysMenuService.selectMenuAll(eid, null, PlatFormTypeEnum.APP.getCode(), getEnv());
        DataSourceHelper.changeToMy();
        Map<Long, SysMenuDO> appMenuMap = sysMenuDOList.stream()
                .collect(Collectors.toMap(SysMenuDO::getId, data -> data, (a, b) -> a));
        //用户有权限的菜单
        if(Role.isAdmin(sysRoleDO.getRoleEnum())){
            List<EnterpriseUserAppMenuInfoVO> allMenuListVO = ListUtils.emptyIfNull(sysMenuDOList).stream()
                    .map(this::mapEnterpriseUserAppMenuInfoVO)
                    .collect(Collectors.toList());
            vo.setMenuInfoList(getChildMenuApp(0L,allMenuListVO));
        }else {
            List<EnterpriseUserAppMenuInfoVO> authAppMenuInfoVOList = ListUtils.emptyIfNull(authRoleMenuDOList)
                    .stream()
                    .map(data -> mapEnterpriseUserAppMenuInfoVO(data, appMenuMap))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            vo.setMenuInfoList(getChildMenuApp(0L,authAppMenuInfoVOList));
        }
        //用户自定义菜单
        List<Long> authMenuList = ListUtils.emptyIfNull(vo.getMenuInfoList())
                .stream()
                .map(EnterpriseUserAppMenuInfoVO::getMenuId)
                .collect(Collectors.toList());
        List<EnterpriseUserAppMenuDO> userAppMenuDOList = enterpriseUserAppMenuMapper.selectEnterpriseUserAppMenuByUser(eid, userId,Constants.INDEX_TWO);

        List<EnterpriseUserAppMenuInfoVO> customizeAppMenuInfoVOList = ListUtils.emptyIfNull(userAppMenuDOList)
                .stream()
                .map(data->mapEnterpriseUserAppMenuInfoByUser(data,appMenuMap,authMenuList))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        vo.setUserMenuInfoList(customizeAppMenuInfoVOList);
        return vo;
    }
    private EnterpriseUserAppMenuInfoVO initEnterpriseUserAppMenuInfoVO(Long menuId,String menuKey,String menuName,Integer sort){

        EnterpriseUserAppMenuInfoVO vo = new EnterpriseUserAppMenuInfoVO();
        vo.setMenuId(menuId);
        vo.setMenuKey(menuKey);
        vo.setMenuName(menuName);
        vo.setSort(sort);
        return vo;
    }

    @Override
    public Boolean updateUserAppMenu(String eid, AppMenuCustomizeRequest request,Integer menuLevel) {
        String userId = UserHolder.getUser().getUserId();
        enterpriseUserAppMenuMapper.deleteEnterpriseUserAppMenuByUser(eid,userId,menuLevel);
        List<EnterpriseUserAppMenuDO> enterpriseUserAppMenuDOList = ListUtils.emptyIfNull(request.getMenuList())
                .stream()
                .map(data -> mapEnterpriseUserAppMenuDO(userId, data,menuLevel))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(enterpriseUserAppMenuDOList)){
            return true;
        }
        enterpriseUserAppMenuMapper.batchInsert(eid,enterpriseUserAppMenuDOList );
        return true;
    }

    @Override
    public List<EnterpriseUserAppMenuInfoVO> getUserDefinedMenuApp(String enterpriseId, CurrentUser user,Boolean isOld,Integer menuLevel) {
        String userId = user.getUserId();
        SysRoleDO sysRoleDO =user.getSysRoleDO();
        if(sysRoleDO==null){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "用户没有分配职位！");
        }
        Long roleId = sysRoleDO.getId();
        //所有的App菜单
        DataSourceHelper.reset();
        List<SysMenuDO> menuList;
        if(isOld){
            menuList = sysMenuService.selectOldMenuAll(enterpriseId, null, PlatFormTypeEnum.NEW_APP.getCode(), getEnv());
        }else {
            menuList = sysMenuService.selectMenuAll(enterpriseId, null, PlatFormTypeEnum.NEW_APP.getCode(), getEnv());
        }
        //角色的权限
        DataSourceHelper.changeToMy();
        List<SysRoleMenuDO> authRoleMenuList = sysRoleMenuMapper.listSysRoleMenuByRoleId(enterpriseId, roleId, PlatFormTypeEnum.NEW_APP.getCode());
        if(Role.isAdmin(sysRoleDO.getRoleEnum())){
            for (SysMenuDO sysMenuDO : menuList) {
                authRoleMenuList.add(new SysRoleMenuDO(sysMenuDO.getId(), roleId, PlatFormTypeEnum.NEW_APP.getCode()));
            }
        }
        List<EnterpriseUserAppMenuInfoVO> resultList = new ArrayList<>();
        //用户自定义的
        List<EnterpriseUserAppMenuDO> userAppMenuList = enterpriseUserAppMenuMapper.selectEnterpriseUserAppMenuByUser(enterpriseId, userId,menuLevel);
        List<SysMenuVO> menuVO = sysMenuService.getMenuVO(menuList, authRoleMenuList);
        Map<Long, SysMenuVO> checkMap = menuVO.stream().filter(o -> "1".equals(o.getChecked())).collect(Collectors.toMap(o->o.getId(), Function.identity()));
        Set<Long> menuIdSet = new HashSet<>();
        for (EnterpriseUserAppMenuDO enterpriseUserAppMenu : userAppMenuList) {
            if(menuIdSet.contains(enterpriseUserAppMenu.getMenuId())){
                continue;
            }
            SysMenuVO sysMenuVO = checkMap.get(enterpriseUserAppMenu.getMenuId());
            if(Objects.isNull(sysMenuVO)){
                continue;
            }
            menuIdSet.add(enterpriseUserAppMenu.getMenuId());
            EnterpriseUserAppMenuInfoVO enterpriseUserAppMenuInfoVO = mapEnterpriseUserAppMenuInfoVO(sysMenuVO);
            enterpriseUserAppMenuInfoVO.setMenuKey(sysMenuVO.getType());
            enterpriseUserAppMenuInfoVO.setSort(enterpriseUserAppMenu.getSort());
            resultList.add(enterpriseUserAppMenuInfoVO);
        }
        return resultList;
    }

    @Override
    public List<EnterpriseUserAppMenuInfoVO> getUserDefinedMenuAppNew(String enterpriseId, CurrentUser user,Boolean isOld,Integer menuLevel) {
        List<MenuTreeVO>  menuTreeVOList = sysMenuService.getAppAuthList(enterpriseId,user.getUserId(), user, isOld);
        Map<String, MenuTreeVO> menuTreeVOMap  = ListUtils.emptyIfNull(menuTreeVOList).stream()
                .collect(Collectors.toMap(MenuTreeVO::getCode, data -> data, (a, b) -> a));
        MenuTreeVO menuTreeVO = menuTreeVOMap.get(Constants.MOBILE_HOME);
        if(menuTreeVO == null){
            return new ArrayList<>();
        }
        List<MenuTreeVO> children = menuTreeVO.getChildren();
        List<EnterpriseUserAppMenuInfoVO> resultList = new ArrayList<>();
        //用户自定义的
        DataSourceHelper.changeToMy();
        List<EnterpriseUserAppMenuDO> userAppMenuList = enterpriseUserAppMenuMapper.selectEnterpriseUserAppMenuByUser(enterpriseId, user.getUserId(), menuLevel);
        Map<Long,Integer> userAppMenuMap = ListUtils.emptyIfNull(userAppMenuList).stream()
                .filter(a -> a.getMenuId() != null && a.getSort() != null)
                .collect(Collectors.toMap(userAppMenu -> userAppMenu.getMenuId(), data ->data.getSort(),(a, b)->a));
        Set<Long> menuIdSet = new HashSet<>();
        for (MenuTreeVO child : children) {
            if(menuIdSet.contains(child.getId())){
                continue;
            }
            menuIdSet.add(child.getId());
            EnterpriseUserAppMenuInfoVO enterpriseUserAppMenuInfoVO = mapEnterpriseUserAppMenuInfoVOByMenuTreeVO(child);
            enterpriseUserAppMenuInfoVO.setMenuKey(child.getType());
            enterpriseUserAppMenuInfoVO.setPlatform(child.getPlatform());
            enterpriseUserAppMenuInfoVO.setDefineName(child.getDefineName());
            enterpriseUserAppMenuInfoVO.setMenuPic(child.getMenuPic());
            enterpriseUserAppMenuInfoVO.setIcon(child.getIcon());
            enterpriseUserAppMenuInfoVO.setPath(child.getPath());
            if(userAppMenuMap != null && userAppMenuMap.get(child.getId()) != null ){
                enterpriseUserAppMenuInfoVO.setSort(userAppMenuMap.get(child.getId()));
            }
            resultList.add(enterpriseUserAppMenuInfoVO);
        }
        return resultList;
    }

    private EnterpriseUserAppMenuDO mapEnterpriseUserAppMenuDO(String userId,AppMenuCustomizeInfo request,Integer menuLevel){
        EnterpriseUserAppMenuDO userAppMenuDO =new EnterpriseUserAppMenuDO();
        userAppMenuDO.setMenuId(request.getMenuId());
        userAppMenuDO.setUserId(userId);
        userAppMenuDO.setSort(request.getSort());
        userAppMenuDO.setMenuLevel(menuLevel);
        return userAppMenuDO;
    }


    private EnterpriseUserAppMenuInfoVO mapEnterpriseUserAppMenuInfoVO(SysMenuDO sysMenuDO){
        EnterpriseUserAppMenuInfoVO vo =new EnterpriseUserAppMenuInfoVO();
        vo.setParentId(sysMenuDO.getParentId());
        vo.setType(sysMenuDO.getType());
        vo.setMenuId(sysMenuDO.getId());
        vo.setSort(sysMenuDO.getSort());
        vo.setMenuName(sysMenuDO.getName());
        vo.setMenuKey(sysMenuDO.getType());
        vo.setMenuType(sysMenuDO.getMenuType());
        vo.setPath(sysMenuDO.getPath());
        return vo;
    }

    private EnterpriseUserAppMenuInfoVO mapEnterpriseUserAppMenuInfoVOByMenuTreeVO(MenuTreeVO menuTreeVO){
        EnterpriseUserAppMenuInfoVO vo =new EnterpriseUserAppMenuInfoVO();
        vo.setParentId(menuTreeVO.getParentId());
        vo.setType(menuTreeVO.getType());
        vo.setMenuId(menuTreeVO.getId());
        vo.setSort(menuTreeVO.getSort());
        vo.setMenuName(menuTreeVO.getName());
        vo.setMenuKey(menuTreeVO.getType());
        vo.setMenuType(menuTreeVO.getMenuType());
        return vo;
    }

    private EnterpriseUserAppMenuInfoVO mapEnterpriseUserAppMenuInfoVO(SysRoleMenuDO sysRoleMenuDO,Map<Long, SysMenuDO> appMenuMap){
        EnterpriseUserAppMenuInfoVO vo =new EnterpriseUserAppMenuInfoVO();
        Long menuId = sysRoleMenuDO.getMenuId();
        SysMenuDO sysMenuDO = appMenuMap.get(menuId);
        if(sysMenuDO==null){
            return null;
        }
        vo.setParentId(sysMenuDO.getParentId());
        vo.setType(sysMenuDO.getType());
        vo.setMenuId(sysRoleMenuDO.getMenuId());
        vo.setMenuKey(sysMenuDO.getType());
        vo.setMenuName(sysMenuDO.getName());
        vo.setSort(sysMenuDO.getSort());
        vo.setMenuType(sysMenuDO.getMenuType());
        return vo;
    }
    private List<EnterpriseUserAppMenuInfoVO> getChildMenuApp(Long rootId, List<EnterpriseUserAppMenuInfoVO> sysMenuDOList) {

        Map<Long, List<EnterpriseUserAppMenuInfoVO>> parentGroup = ListUtils.emptyIfNull(sysMenuDOList)
                .stream()
                .collect(Collectors.groupingBy(EnterpriseUserAppMenuInfoVO::getParentId));
        List<EnterpriseUserAppMenuInfoVO> menuDOList = parentGroup.get(rootId);
        //循环
        if (CollectionUtils.isEmpty(menuDOList)) {
            return Collections.emptyList();
        }
        List<EnterpriseUserAppMenuInfoVO> treeVOList = new LinkedList<>(menuDOList);
        for (EnterpriseUserAppMenuInfoVO data : treeVOList) {
            getChildApp(data, parentGroup);
        }
        return treeVOList;
    }

    private void getChildApp(EnterpriseUserAppMenuInfoVO data, Map<Long, List<EnterpriseUserAppMenuInfoVO>> parentGroup) {

        List<EnterpriseUserAppMenuInfoVO> sysMenuDOList = parentGroup.get(data.getMenuId());

        List<EnterpriseUserAppMenuInfoVO> parentMenuList = ListUtils.emptyIfNull(sysMenuDOList).stream()
                .filter(menu -> StringUtils.isNotBlank(menu.getType()))
                .collect(Collectors.toList());
        //属于菜单下时候
        if (CollectionUtils.isNotEmpty(parentMenuList)) {
            List<EnterpriseUserAppMenuInfoVO> menuList = parentMenuList.stream()
                    .filter(vo -> MenuTypeEnum.MENU.getCode().equals(vo.getMenuType()))
                    .collect(Collectors.toList());
            data.setChildren(menuList);
            parentMenuList.forEach(child -> getChildApp(child, parentGroup));
        }
    }
    private EnterpriseUserAppMenuInfoVO mapEnterpriseUserAppMenuInfoByUser(EnterpriseUserAppMenuDO appMenuDO , Map<Long, SysMenuDO> appMenuMap,
                                                                           List<Long> authMenuList){

        EnterpriseUserAppMenuInfoVO vo =new EnterpriseUserAppMenuInfoVO();
        Long menuId = appMenuDO.getMenuId();
        SysMenuDO sysMenuDO = appMenuMap.get(menuId);
      if(authMenuList.contains(appMenuDO.getMenuId())){
          vo.setMenuId(appMenuDO.getMenuId());
          vo.setMenuKey(sysMenuDO.getType());
          vo.setMenuName(sysMenuDO.getName());
          vo.setSort(appMenuDO.getSort());
          return vo;
      }
      return null;
    }

}
