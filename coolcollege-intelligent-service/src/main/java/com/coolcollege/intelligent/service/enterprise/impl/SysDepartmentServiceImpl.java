package com.coolcollege.intelligent.service.enterprise.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.TwoResultTuple;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.dao.enterprise.*;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.department.DeptNode;
import com.coolcollege.intelligent.model.department.dto.*;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.qywx.dto.ImportUserDTO;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.SysDepartmentService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.coolcollege.intelligent.service.sync.service.AutoSyncOrgRangeService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.PinyinUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 首亮
 * @ClassName SysDepartmentServiceImpl
 * @Description 用一句话描述什么
 */
@Slf4j
@Service(value = "sysDepartmentService")
public class SysDepartmentServiceImpl implements SysDepartmentService {

    @Resource
    public SysDepartmentMapper sysDepartmentMapper;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private DingService dingService;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private EnterpriseUserDepartmentMapper enterpriseUserDepartmentMapper;

    @Autowired
    private RedisUtilPool redisUtil;

    @Autowired
    private AutoSyncOrgRangeService syncOrgRangeService;

    @Autowired
    private SelectionComponentService selectionComponentService;

    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Autowired
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public void batchInsertOrUpdate(List<SysDepartmentDO> sysDepartmentsList, String eid) {
        log.info("batchInsertOrUpdate into");
        // 然后更新有效部门显示
        if (CollectionUtils.isNotEmpty(sysDepartmentsList)) {
            Lists.partition(sysDepartmentsList, SyncConfig.DEFAULT_BATCH_SIZE).forEach(p -> {
                sysDepartmentMapper.batchInsertOrUpdate(p, eid);
            });
        }
        syncOrgRangeService.syncDeptRange(eid);
    }

    @Override
    public List<SysDepartmentDO> selectAllDepts(String eid) {
        return sysDepartmentMapper.selectAll(eid);
    }

    @Override
    public void deleteByIds(List<String> sysDepartmentDOList, String eid) {
        //非空判断，避免动态sql没有判断该list为空的报错
        if (CollectionUtils.isEmpty(sysDepartmentDOList)) {
            return;
        }
        sysDepartmentMapper.deleteByIds(sysDepartmentDOList, eid);
    }

    @Override
    public TwoResultTuple<Set<String>, Map<String, String>> getAllDeptInfo(String eid) {
        List<SysDepartmentDO> allDeptList = selectAllDepts(eid);
        Set<String> deptIdSet = allDeptList.stream().map(SysDepartmentDO::getId).collect(Collectors.toSet());
        Map<String, String> deptIdMap = allDeptList.stream().filter(d -> d.getParentId() != null).collect(Collectors.toMap(SysDepartmentDO::getId, SysDepartmentDO::getParentId));

        return new TwoResultTuple<>(deptIdSet, deptIdMap);
    }

    @Override
    public List<SyncTreeNode> getSyncDeptTreeList(String eid, String parentId) {
        return sysDepartmentMapper.getSyncDeptTreeList(eid, parentId);
    }

    @Override
    public List<DeptNode> getDepListByDepName(String eid, String name) {
        List<DeptNode> deptNodes = sysDepartmentMapper.getDepListByDepName(eid, name, null);
        return deptNodes;
    }

    @Override
    public Object getDepUsersByPage(String enterpriseId, DepartmentQueryDTO departmentQueryDTO, Boolean type) {
       return enterpriseUserService.getDepUsersByPage(enterpriseId, departmentQueryDTO);
    }

    @Override
//    @Cacheable(value = CacheConstant.MAP_CACHE, key = "targetClass + #eid")
    public Object getDeptUserTree(String eid, String userType) {
        DataSourceHelper.changeToMy();
        String tree = redisUtil.getString("deptUserTree_" + eid);
        if (StrUtil.isNotBlank(tree)) {
            return JSON.parseObject(tree);
        }
//        DataSourceHelper.changeToSpecificDataSource("coolcollege_intelligent_2");
        List<DeptUserTreeDTO> deptList = sysDepartmentMapper.getDeptList(eid);
        // 获取根节点部门
        Optional<DeptUserTreeDTO> first = deptList.stream().filter(m -> StrUtil.isBlank(m.getParentId())).findFirst();
        DeptUserTreeDTO root = new DeptUserTreeDTO();
        if (first.isPresent()) {
            root = first.get();
        }
        // 区域按照父id分组
        Map<String, List<DeptUserTreeDTO>> deptMap = deptList.stream().filter(s -> StringUtils.isNotEmpty(s.getParentId()))
                .collect(Collectors.groupingBy(DeptUserTreeDTO::getParentId));
        List<DeptUserTreeDTO> userList = enterpriseUserMapper.getDeptUser(eid, userType);
        userList = userList.stream().distinct()
                .peek(m -> m.setKey(PinyinUtil.fillKey(m.getName().charAt(0))))
                .collect(Collectors.toList());
        // 装配门店的区域id
        String rootId = root.getId();
        root.setUserCount(userList.size());
        root.setChildren(getChildren(rootId, deptMap, userList));
        redisUtil.setString("deptUserTree_" + eid, JSON.toJSONString(root), 60);
        return root;
    }

    /**
     * 获取子级节点及装配部门/人员列表
     *
     * @param deptId
     * @param deptMap
     * @return
     */
    private List<DeptUserTreeDTO> getChildren(String deptId, Map<String, List<DeptUserTreeDTO>> deptMap, List<DeptUserTreeDTO> userList) {
        List<DeptUserTreeDTO> regionDOS = deptMap.get(deptId);
        // 最后一层
        if (CollUtil.isEmpty(regionDOS) && !deptMap.containsKey(deptId) && !deptId.equals(Constants.ROOT_DEPT_ID_STR)) {
            return Collections.emptyList();
        }
        List<DeptUserTreeDTO> nodes = new ArrayList<>();
        if (regionDOS != null) {
            regionDOS.forEach(s -> {
                List<DeptUserTreeDTO> deptUserList = userList.stream().filter(m -> isDeptUser(m.getDeptId(), s.getId())).collect(Collectors.toList());
                List<DeptUserTreeDTO> children = getChildren(s.getId(), deptMap, userList);
                // 部门排在前面
                if (CollUtil.isEmpty(children)) {
                    children = deptUserList;
                } else {
                    children.addAll(deptUserList);
                }
                s.setUserCount(userList.stream().filter(m -> m.getDeptId().contains(s.getId())).count());
                s.setChildren(children);
                nodes.add(s);
            });
        }
        // 根节点门店获取
        if (deptId.equals(Constants.ROOT_DEPT_ID_STR)) {
            // deptId下对应的人员
            List<DeptUserTreeDTO> deptUser = userList.stream().filter(m -> isDeptUser(m.getDeptId(), deptId)).collect(Collectors.toList());
            // 保证部门排在前面
            if (CollUtil.isEmpty(nodes)) {
                return deptUser;
            } else {
                nodes.addAll(deptUser);
            }
        }
        return nodes;
    }

    private static boolean isDeptUser(String userDeptId, String deptId) {
        String[] split = userDeptId.replace("[", "").replace("]", "").split(",");
        boolean flag = false;
        for (String s : split) {
            String[] deptIds = s.split("/");
            if (deptIds[deptIds.length - 1].equals(deptId)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

//    /**
//     * 获取管理员、主管权限列表
//     *
//     * @param accessToken
//     * @return
//     */
//    @Override
//    public List<String> getAdminList(String accessToken) {
//        List<String> adminList;
//        try {
//            adminList = dingService.getAdminList(accessToken);
//        } catch (ApiException e) {
//            log.info("获取钉钉管理员的accessToken为{}", accessToken);
//            throw new ServiceException(3000001, "获取钉钉管理员失败");
//        }
//        List<EnterpriseUserDO> users;
//        try {
//            List<OapiRoleListResponse.OpenRoleGroup> roleList = dingService.getRoleList(accessToken);
//            Long openRole = roleList.get(0).getRoles().get(2).getId();
//            users = dingService.getUsersByRoleId(accessToken, openRole);
//        } catch (ApiException e) {
//            log.info("获取主管用户列表的accessToken为{}", accessToken);
//            throw new ServiceException(3000001, "获取主管用户列表失败{}");
//        }
//        if (CollectionUtils.isNotEmpty(users)) {
//            List<String> strings = users.stream().map(s -> s.getUserId()).collect(Collectors.toList());
//            adminList.addAll(strings);
//        }
//        return adminList;
//
//    }

    @Override
    public SysDepartmentDO selectById(String eid, String id) {
        return sysDepartmentMapper.selectById(eid, id);
    }

    @Override
    public List<String> getChildDeptIdList(String eid, String id, boolean recursion) {
        List<DeptIdDTO> allIdAndParentId = sysDepartmentMapper.getAllIdAndParentId(eid, id, recursion);
        List<String> childList;
        // 如果是非递归查询
        if (!recursion) {
            childList = ListUtils.emptyIfNull(allIdAndParentId.stream().map(DeptIdDTO::getId).collect(Collectors.toList()));
            childList.add(id);
            return childList;
        }
        // 如果是根节点下所有的节点则获取全部的节点
        if ("1".equals(id)) {
            childList = allIdAndParentId.stream().map(DeptIdDTO::getId).collect(Collectors.toList());
        } else {
            childList = new ArrayList<>();
            Map<String, List<String>> group = allIdAndParentId.stream().
                    filter(f -> f.getParentId() != null).
                    collect(Collectors.groupingBy(DeptIdDTO::getParentId, Collectors.mapping(DeptIdDTO::getId, Collectors.toList())));
            getAllChildList(id, group, childList);
            childList.add(id);
        }
        return childList;
    }

    @Override
    public Object getDeptChild(String eid, String pid, boolean hasUser, boolean hasUserNum, Boolean hasAuth, String userId) {
        DataSourceHelper.reset();
        DataSourceHelper.changeToMy();

        //如果pid为空  给默认值  保留以前的逻辑
        SysDepartmentDO sysDepartmentDO = sysDepartmentMapper.selectById(eid, SyncConfig.ROOT_DEPT_ID_STR);
        DeptChildDTO rootDept = new DeptChildDTO();
        rootDept.setId(sysDepartmentDO.getId());
        rootDept.setName(sysDepartmentDO.getName());
        rootDept.setDepartOrder(sysDepartmentDO.getDepartOrder());
        rootDept.setUserFlag(Boolean.FALSE);

        List<DeptChildDTO> result = new ArrayList<>();
        if (StringUtils.isBlank(pid) && (hasAuth == null || !hasAuth)) {
            result.add(rootDept);
        } else if (StrUtil.isBlank(pid) && hasAuth != null && hasAuth) {
            // 判断是否是管理员
            boolean isAdmin = sysRoleService.checkIsAdmin(eid, userId);
            if(isAdmin){
                result.add(rootDept);
            }else {
                List<EnterpriseUserDepartmentDO> userDepartmentDOList = enterpriseUserDepartmentMapper.selectUserDeptByUserId(eid, userId);
                if (CollUtil.isNotEmpty(userDepartmentDOList)) {
                    List<String> deptIds = userDepartmentDOList.stream().map(EnterpriseUserDepartmentDO::getDepartmentId).collect(Collectors.toList());
                    result = sysDepartmentMapper.getDeptListById(eid, deptIds);
                }
            }
        } else {
            result = sysDepartmentMapper.getDeptChildList(eid, Collections.singletonList(pid));
        }
        //如果不是查询的根部门并且查询的部门为空，需要人员数据 查询
        if (CollUtil.isEmpty(result) && hasUser && StringUtils.isNotBlank(pid)) {
            //get 部门下人员信息
            result = sysDepartmentMapper.getDeptUserList(eid, Collections.singletonList(pid));
            //补充人员的信息
            result = selectionComponentService.supplementDeptUserQueryResult(eid, result);
            return result;
        }
        if (CollUtil.isEmpty(result)) {
            return result;
        }
        // 判断返回的列表是否有子节点
        List<String> childIds = result.stream()
                .map(DeptChildDTO::getId)
                .collect(Collectors.toList());
        List<DeptChildDTO> deptChildList = sysDepartmentMapper.getDeptChildList(eid, childIds);
        //获取到子节点后的数据，根据parentId 进行分组
        Map<String, List<DeptChildDTO>> deptMap = ListUtils.emptyIfNull(deptChildList)
                .stream()
                .collect(Collectors.groupingBy(DeptChildDTO::getParentId));
        //统计人数，只统计当前部门下的直属人数，不统计整条树下的人员
        List<EnterpriseUserDepartmentDO> userDepartments = enterpriseUserDepartmentMapper.getUserDepartments(eid, childIds);
        Map<String, List<EnterpriseUserDepartmentDO>> departUsersMap = ListUtils.emptyIfNull(userDepartments)
                .stream()
                .collect(Collectors.groupingBy(EnterpriseUserDepartmentDO::getDepartmentId));
        //设置部门下的直挂人数和是否有子节点标识
        for (DeptChildDTO deptChild : result) {
            if (hasUserNum) {
                //需要用户数，则设置当前部门下的直挂人数
                deptChild.setUserNum(CollectionUtils.isEmpty(departUsersMap.get(deptChild.getId())) ? 0
                        : departUsersMap.get(deptChild.getId()).size());
            }
            deptChild.setHasChild(CollUtil.isNotEmpty(deptMap.get(deptChild.getId())) || (hasUserNum && (deptChild.getUserNum() > 0)));
        }
        //获取部门下的人员信息
        if (hasUser) {
            List<DeptChildDTO> deptUserList = sysDepartmentMapper.getDeptUserList(eid, Collections.singletonList(pid));
            result.addAll(deptUserList);
        }
        //补充人员的信息
        result = selectionComponentService.supplementDeptUserQueryResult(eid, result);
        return result;
    }

    public static void getAllChildList(String id, Map<String, List<String>> group, List<String> childList) {
        List<String> child = group.get(id);
        if (CollUtil.isNotEmpty(child)) {
            childList.addAll(child);
            for (String cid: child) {
                getAllChildList(cid, group, childList);
            }
        }
    }

    @Override
    public List<String> selectIdList(String eid) {
        return sysDepartmentMapper.selectIdList(eid);
    }

    @Override
    public void initWeComDepartment(String eid, ImportUserDTO importUserDTO) {
        try {
            String departmentString = importUserDTO.getDepartment();
            String userId = importUserDTO.getUserId();
            String[] deptNameArray = departmentString.split(";");
            //该用户所有部门信息
            List<String[]> childDeptNameList = new ArrayList<>();
            for (String deptName : deptNameArray) {
                if (deptName.contains("/")) {
                    //带斜杠的部门，需要根据斜杠继续切割
                    childDeptNameList.add(deptName.split("/"));
                } else {
                    //不带“/”符号的，为根部门
                    SysDepartmentDO checkDept = sysDepartmentMapper.selectById(eid, "1");
                    if (checkDept == null) {
                        //没有根目录需要插入根目录
                        SysDepartmentDO rootDept = new SysDepartmentDO();
                        rootDept.setId("1");
                        rootDept.setName(deptName);
                        sysDepartmentMapper.insertDept(eid, rootDept);
                        checkDept = rootDept;
                    }
                    if (!deptName.equals(checkDept.getName())) {
                        checkDept.setName(deptName);
                        sysDepartmentMapper.updateDeptName(eid, checkDept);
                    }
                }
            }
            List<EnterpriseUserDepartmentDO> userAllDepts = enterpriseUserDepartmentMapper.selectUserDeptByUserId(eid, userId);
            //筛选除不是跟部门的用户关联部门
            List<EnterpriseUserDepartmentDO> userChildDepts = userAllDepts.stream().filter(dept -> !Constants.ROOT_DEPT_ID_STR.equals(dept.getDepartmentId()))
                    .collect(Collectors.toList());
            //当用户只关联除根部门以外一个部门的时候，更新该部门名称
            if (userChildDepts.size() == 1 && childDeptNameList.size() == 1) {
                String[] childDeptName = childDeptNameList.get(0);
                String deptId = userChildDepts.get(0).getDepartmentId();
                for (int i = childDeptName.length - 1 ; i >= 0; i--) {
                    SysDepartmentDO sysDepartmentDO = sysDepartmentMapper.selectById(eid, deptId);
                    if (!childDeptName[i].equals(sysDepartmentDO.getName())) {
                        sysDepartmentDO.setName(childDeptName[i]);
                        sysDepartmentMapper.updateDeptName(eid, sysDepartmentDO);
                    }
                    deptId = sysDepartmentDO.getParentId();
                }
            }
        } catch (Exception e) {
            log.error("SysDepartmentServiceImpl initWeComDepartment 初始化部门异常", e);
        }
    }

    @Override
    public void batchUpdateDeptName(String enterpriseId, List<SysDepartmentDO> depts) {
        Lists.partition(depts, Constants.BATCH_INSERT_COUNT).forEach(s -> {
            sysDepartmentMapper.batchUpdateDeptName(enterpriseId, s);
        });
    }

    @Override
    public List<Long> listParentIdByIdList(String eid, List<Long> idList) {
        if (CollUtil.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return sysDepartmentMapper.listParentIdByIdList(eid, idList);
    }
}