package com.coolcollege.intelligent.service.enterprise.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.*;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataType;
import com.coolcollege.intelligent.common.enums.enterprise.SubordinateSourceEnum;
import com.coolcollege.intelligent.common.enums.enterprise.UserSelectRangeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.myj.MyjEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.enums.region.FixedRegionEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.enums.role.CoolPositionTypeEnum;
import com.coolcollege.intelligent.common.enums.songxia.SongXiaEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.songxia.SongxiaRoleEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.enums.user.UserTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.*;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.enterprise.dao.SubordinateMappingDAO;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.songxia.SongXiaMapper;
import com.coolcollege.intelligent.dao.store.CoolcollegeStoreUserCollectMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.system.dao.SysRoleDao;
import com.coolcollege.intelligent.dao.userstatus.dao.UserPersonnelStatusHistoryDao;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.SyncRequest;
import com.coolcollege.intelligent.facade.dto.openApi.*;
import com.coolcollege.intelligent.facade.dto.openApi.vo.OpenUserVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.UserInfoVO;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.achievement.entity.ManageStoreCategoryCodeDO;
import com.coolcollege.intelligent.model.activity.entity.PromoterStoreInfoDO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.department.DeptNode;
import com.coolcollege.intelligent.model.department.dto.DepartmentQueryDTO;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enterprise.dto.*;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseDetailUserVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseUserBossVO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.event.UserNotAuthEvent;
import com.coolcollege.intelligent.model.export.request.UserInfoExportRequest;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.menu.SysMenuDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.*;
import com.coolcollege.intelligent.model.region.response.RegionStoreListResp;
import com.coolcollege.intelligent.model.songXia.ActualStoreDO;
import com.coolcollege.intelligent.model.songXia.PromoterInfoDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.store.dto.UploadFileMessageDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.user.*;
import com.coolcollege.intelligent.model.user.dto.ExportUserRequest;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusHistoryVO;
import com.coolcollege.intelligent.model.usergroup.dto.UserGroupDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.enterprise.*;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.enterpriseUserGroup.EnterpriseUserGroupService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.position.PositionService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.recent.LRUService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.requestBody.user.EnterpriseUserRequestBody;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.AIUserTool;
import com.coolcollege.intelligent.util.LoginUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.taobao.api.ApiException;
import com.coolcollege.intelligent.common.util.MD5Util;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.POSITION_IS_FULL;
import static com.coolcollege.intelligent.common.sync.conf.SyncConfig.DEFAULT_BATCH_SIZE;

/**
 * @ClassName EnterpriseUserServiceImpl
 * @Description 用一句话描述什么
 */
@Service(value = "enterpriseUserService")
@Slf4j
public class EnterpriseUserServiceImpl implements EnterpriseUserService {

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;
    @Autowired
    private PositionService positionService;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysDepartmentService sysDepartmentService;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private CoolcollegeStoreUserCollectMapper coolcollegeStoreUserCollectMapper;
    @Resource
    private EnterpriseUserDepartmentMapper enterpriseUserDepartmentMapper;
    @Autowired
    private AuthVisualService visualService;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Autowired
    private LRUService lruService;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    @Lazy
    private RegionService regionService;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Autowired
    private EnterpriseUserMappingService enterpriseUserMappingService;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;
    @Resource
    private EnterpriseSettingService enterpriseSettingService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SelectionComponentService selectionComponentService;
    @Autowired
    private ChatService chatService;
    @Resource
    private EnterpriseMapper enterpriseMapper;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private UserPersonnelStatusHistoryDao userPersonnelStatusHistoryDao;

    @Value("${spring.profiles.active}")
    private String env;
    @Resource
    UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    RegionMapper regionMapper;
    @Resource
    SubordinateMappingDAO subordinateMappingDAO;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Autowired
    private EnterpriseUserGroupService enterpriseUserGroupService;
    @Autowired
    private RegionDao regionDao;
    @Autowired
    private SysRoleDao sysRoleDao;
    @Autowired
    private UserAuthMappingService userAuthMappingService;
    @Autowired
    private EnterpriseInitConfigApiService enterpriseInitConfigApiService;
    @Resource
    private ImportTaskService importTaskService;
    @Autowired
    private SongXiaMapper songXiaMapper;
    @Autowired
    private EnterpriseUserRoleDao enterpriseUserRoleDao;
    @Resource
    private LoginUtil loginUtil;

    @Override
    public EnterpriseUserDO selectConfigUserByUserId(String userId) {
        return enterpriseUserMapper.selectConfigUserByUserId(userId);
    }


    @Override
    public EnterpriseUserDO selectConfigUserByUnionid(String unionid) {
        return enterpriseUserMapper.selectConfigUserByUnionid(unionid);
    }

    @Override
    public void setUsersInfo(String eid, List<EnterpriseUserRequest> users, Set<String> deptIdSet, Map<String, String> deptIdMap) {
        try {
            List<EnterpriseUserDepartmentDO> deptUsers = new ArrayList<>();
            List<String> userIds = new ArrayList<>();
            users.forEach(user -> {
                //矫正用户所属部门(移除不在授权范围内的部门)
                updateUserDept(user, deptIdSet);
                if (Objects.nonNull(user.getEnterpriseUserDO())) {
                    List<String> deptIds = JSONObject.parseArray(user.getDepartment(), String.class);
                    List<EnterpriseUserDepartmentDO> deptUserMapping = ListUtils.emptyIfNull(deptIds)
                            .stream()
                            .map(m -> new EnterpriseUserDepartmentDO(user.getEnterpriseUserDO().getUserId(), m, Boolean.FALSE)).collect(Collectors.toList());
                    List<String> leaderDeptIds = JSONObject.parseArray(user.getEnterpriseUserDO().getIsLeaderInDepts(), String.class);
                    List<EnterpriseUserDepartmentDO> leaderDeptMapping = ListUtils.emptyIfNull(leaderDeptIds)
                            .stream()
                            .map(m -> new EnterpriseUserDepartmentDO(user.getEnterpriseUserDO().getUserId(), m, Boolean.TRUE)).collect(Collectors.toList());
                    deptUsers.addAll(deptUserMapping);
                    deptUsers.addAll(leaderDeptMapping);
                    userIds.add(user.getEnterpriseUserDO().getUserId());
                }
                //设置用户所属部门全路径(方便查询包括子部门的所有用户)
                updateUserDeptPath(user, deptIdMap);
                //设置用户角色
                //updateUserRole(user);
            });
            if (CollectionUtils.isNotEmpty(userIds)) {
                enterpriseUserDepartmentMapper.deleteMapping(eid, userIds);
                enterpriseUserDepartmentMapper.deleteUserDepartmentAuth(eid, userIds);
            }
            if (CollectionUtils.isNotEmpty(deptUsers)) {
                Lists.partition(deptUsers, DEFAULT_BATCH_SIZE).forEach(data -> {
                    enterpriseUserDepartmentMapper.batchInsert(eid, data);
                });
            }
        } catch (Exception e) {
            log.error("设置用户部门信息异常", e);
        }
    }

    /**
     * 更新平台库用户表
     */
    @Override

    public void batchInsertPlatformUsers(List<EnterpriseUserDO> list) {

        List<String> unionidList = Lists.newArrayList(list.stream().map(EnterpriseUserDO::getUnionid).collect(Collectors.toList()));

        List<EnterpriseUserDTO> enterriseDTOList = enterpriseUserMapper.selectUsersIdByUnionid(unionidList);

        Map<String, String> map = enterriseDTOList.stream()
                .filter(a -> a.getUnionid() != null && a.getId() != null)
                .collect(Collectors.toMap(EnterpriseUserDTO::getUnionid, EnterpriseUserDTO::getId));

        Map<String, EnterpriseUserDTO> enterriseDTOMap = enterriseDTOList.stream()
                .filter(a -> a.getUnionid() != null)
                .collect(Collectors.toMap(EnterpriseUserDTO::getUnionid, data -> data, (a, b) -> a));

        List<EnterpriseUserDO> insertList = Lists.newArrayList();
        list.forEach(user -> {
            String id = map.get(user.getUnionid());
            EnterpriseUserDTO enterpriseUserDTO = enterriseDTOMap.get(user.getUnionid());
            if (id == null) {
                log.info("开始比对AI用户");
                if (Objects.equals(AIEnum.AI_UUID.getCode(), user.getUnionid())) {
                    log.info("AI用户信息为{}", user);
                    user.setId(AIEnum.AI_ID.getCode());
                } else {
                    user.setId(UUIDUtils.get32UUID());
                }

                insertList.add(user);
            } else {
                // 企微用户用之前的name和手机号
                Boolean isWxApp = AppTypeEnum.WX_APP.getValue().equals(user.getAppType()) || AppTypeEnum.WX_APP2.getValue().equals(user.getAppType());
                if (enterpriseUserDTO != null && isWxApp) {
                    log.info("企微平台存在用户" + JSON.toJSONString(enterpriseUserDTO));
                    if (StrUtil.isNotEmpty(enterpriseUserDTO.getName())) {
                        user.setName(enterpriseUserDTO.getName());
                    }
                    if (StrUtil.isNotEmpty(enterpriseUserDTO.getMobile())) {
                        user.setMobile(enterpriseUserDTO.getMobile());
                    }
                }
                user.setId(id);
            }
        });

        if (CollectionUtils.isNotEmpty(insertList)) {
            enterpriseUserDao.batchInsertPlatformUsers(insertList);

        }
    }


    /**
     * 更新企业用户表
     *
     * @param deptUsers
     * @param eid
     */

    @Override
    public void batchInsertOrUpdate(List<EnterpriseUserDO> deptUsers, String eid) {
        if (!CollectionUtils.isEmpty(deptUsers)) {
            Lists.partition(deptUsers, DEFAULT_BATCH_SIZE).forEach(p -> {
                enterpriseUserDao.batchInsertOrUpdate(p, eid);
            });
        }
    }

    @Override
    public Boolean insertEnterpriseUser(String eid, EnterpriseUserDO entity) {
        if (Objects.isNull(entity.getUserStatus())) {
            entity.setUserStatus(UserStatusEnum.NORMAL.getCode());
        }
        if (Objects.isNull(entity.getActive())) {
            entity.setActive(true);
        }
        if (StringUtils.isBlank(entity.getThirdOaUniqueFlag())) {
            entity.setThirdOaUniqueFlag(null);
        }
        if (StringUtils.isBlank(entity.getSubordinateRange())) {
            if (BailiEnterpriseEnum.bailiAffiliatedCompany(eid) ||
                    MyjEnterpriseEnum.myjCompany(eid) || Constants.A_SHUI_EID.equals(eid)) {
                entity.setSubordinateRange(UserSelectRangeEnum.DEFINE.getCode());
            } else {
                entity.setSubordinateRange(UserSelectRangeEnum.ALL.getCode());
            }
        }
        return enterpriseUserDao.insertEnterpriseUser(eid, entity);
    }

    /**
     * 查询企业用户
     *
     * @param eid
     * @return
     */
    @Override
    public List<EnterpriseUserDO> selectAllList(String eid) {
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserMapper.selectAllList(eid);
        return enterpriseUserDOList;
    }

    /**
     * 查询企业用户(包括未激活用户)
     *
     * @param eid
     * @return
     */
    @Override
    public List<EnterpriseUserDO> selectAllUser(String eid) {
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserMapper.selectAllUser(eid);
        return enterpriseUserDOList;
    }

    @Override
    public List<String> selectAllUserId(String eid) {
        return enterpriseUserMapper.selectAllUserIds(eid);
    }

    @Override
    public List<String> selectAllUserIdByUserType(String eid, UserTypeEnum userType) {
        return enterpriseUserMapper.selectAllUserIdByUserType(eid, userType.getCode());
    }

    @Override
    public List<SyncEnterpriseUserDTO> selectSpecifyNodeUserIds(String eid, String dingDeptId) {
        return enterpriseUserMapper.selectSpecifyNodeUserIds(eid, dingDeptId);
    }

    @Override
    public void deleteEnterpriseByUserIds(List<String> userIds, String eid) {
        enterpriseUserMapper.deleteEnterpriseUsersByDingUserIds(eid, userIds);
    }


    @Override
    public List<EnterpriseUserDO> selectUsersByUserIds(String eid, List<String> userIds) {
        return enterpriseUserMapper.selectUsersByUserIds(eid, userIds);
    }

    @Override
    public Object getDepUsersByPage(String enterpriseId, DepartmentQueryDTO query) {
        String deptId = query.getDeptId();
        List<String> childDeptIdList = new ArrayList<>();
        if (!(StringUtils.isBlank(deptId) || StringUtils.equals(deptId, "1"))) {
            childDeptIdList = sysDepartmentService.getChildDeptIdList(enterpriseId, query.getDeptId(), true);
        }
        PageHelper.startPage(query.getPage_num(), query.getPage_size());
        List<EnterpriseUserDTO> depUsers = enterpriseUserMapper.getDepUsersByPage(
                enterpriseId,
                StringUtils.isBlank(deptId) || StringUtils.equals(deptId, "1") ? null : childDeptIdList,
                query.getKeyword(),
                query.getUserIds());
        if (CollectionUtils.isEmpty(depUsers)) {
            return PageHelperUtil.getPageInfo(new PageInfo<>(new ArrayList<>()));
        }
        List<String> userIdList = ListUtils.emptyIfNull(depUsers)
                .stream()
                .map(EnterpriseUserDTO::getUserId)
                .collect(Collectors.toList());

        List<UserRoleDTO> userRoleDOTList = sysRoleMapper.userAndRolesByUserId(enterpriseId, userIdList);
        Map<String, UserRoleDTO> userRoleMap = ListUtils.emptyIfNull(userRoleDOTList)
                .stream()
                .collect(Collectors.toMap(UserRoleDTO::getUserId, a -> a, (b, c) -> c));
        convertRole(depUsers, userRoleMap);
        positionService.setOtherInfoForUsers(enterpriseId, depUsers);
//        positionService.setOtherInfoForUsers(enterpriseId, depUsers);
        Map<String, Object> pageInfo = PageHelperUtil.getPageInfo(new PageInfo<>(depUsers));
        pageInfo.put("list", depUsers);
        return pageInfo;
    }

    private void convertRole(List<EnterpriseUserDTO> depUsers, Map<String, UserRoleDTO> sysRoleDOMap) {
        ListUtils.emptyIfNull(depUsers)
                .forEach(data -> {
                    UserRoleDTO userRoleDTO = sysRoleDOMap.get(data.getUserId());
                    if (userRoleDTO != null) {
                        data.setRoleName(userRoleDTO.getRoleName());
                        data.setRoleAuth(userRoleDTO.getRoleAuth());
                        AuthRoleEnum byCode = AuthRoleEnum.getByCode(userRoleDTO.getRoleAuth());
                        data.setRoleAuthName(byCode.getMsg());
                    }
                });

    }

    /**
     * 矫正用户部门
     *
     * @param enterpriseUser
     * @param deptIdSet
     */
    public void updateUserDept(EnterpriseUserRequest enterpriseUser, Set<String> deptIdSet) {

        //logger.info("矫正用户部门");
        String department = enterpriseUser.getDepartment();
        // 如果是AI用户则过滤掉
        if (Objects.nonNull(enterpriseUser.getEnterpriseUserDO()) && enterpriseUser.getEnterpriseUserDO().getUserId().equals(AIEnum.AI_ID.getCode())) {
            return;
        }
        //logger.info("矫正用户部门:"+department);
        if (StringUtils.isEmpty(department)) {
            enterpriseUser.setDepartment("[" + SyncConfig.ROOT_DEPT_ID + "]");
        } else {
            String deptStr = department.replaceAll("\\[", "").replaceAll("\\]", "");
            if (StringUtils.isEmpty(deptStr)) {
                enterpriseUser.setDepartment("[" + SyncConfig.ROOT_DEPT_ID + "]");
            } else {
                deptStr = Arrays.stream(deptStr.split(",")).map(String::trim).filter(id -> deptIdSet.contains(id)).collect(Collectors.joining(","));
                deptStr = StringUtils.isNotEmpty(deptStr) ? deptStr : SyncConfig.ROOT_DEPT_ID.toString();
                enterpriseUser.setDepartment("[" + deptStr + "]");
            }
        }
    }

    /**
     * 设置用户部门全路径
     *
     * @param user
     * @param deptIdMap
     */
    @Override
    public void updateUserDeptPath(EnterpriseUserRequest user, Map<String, String> deptIdMap) {
        List<String> departmentLists = user.getDepartmentLists();
        //logger.info("设置用户部门全路径 start department:{},user:{},id:{}",department,user.getUnionid(),user.getId());
        if (CollectionUtils.isNotEmpty(departmentLists)) {
            //logger.info("into if else department ");
            List<String> deptList = Lists.newArrayList();
            departmentLists.forEach(deptId -> {
                List<String> tmpList = Lists.newArrayList(deptId);
                String parentId;
                while ((parentId = deptIdMap.get(deptId)) != null && (!parentId.equals(deptId))) {
                    tmpList.add(parentId);
                    deptId = parentId;
                }
                String collect = Lists.reverse(tmpList).stream().map(String::valueOf).collect(Collectors.joining("/"));
                String data = collect.startsWith("/") ? collect : "/" + collect;
                data = data.endsWith("/") ? data : data + "/";
                deptList.add(data);
            });
            user.setDepartments("[" + String.join(",", deptList) + "]");
            user.getEnterpriseUserDO().setDepartments("[" + String.join(",", deptList) + "]");
        }
    }

    @Override
    public void batchUpdateUserMobile(String enterpriseId, List<EnterpriseUserDO> users) {
        Lists.partition(users, Constants.BATCH_INSERT_COUNT).forEach(s -> {
            enterpriseUserDao.batchUpdateUserMobile(enterpriseId, s);
        });
    }

    @Override
    public List<AddressBookUserDTO> getAddressBookUsers(String enterpriseId) {
        List<AddressBookUserDTO> addressBookUsers = enterpriseUserMapper.getAddressBookUsers(enterpriseId);
        positionService.setOtherInfoForUsers(enterpriseId, addressBookUsers);
        // 设置序号
        int i = 0;
        for (AddressBookUserDTO userDTO : addressBookUsers) {
            userDTO.setOrderNum(++i);
        }
        return addressBookUsers;
    }

    @Override
    public Object importAddressBook(String enterpriseId, List<Map<String, Object>> dataMapList, String fileName) {
        UploadFileMessageDTO messageDTO = new UploadFileMessageDTO();

        List<String> failReasons = Lists.newArrayList();
        messageDTO.setFileName(fileName);
        messageDTO.setFailReason(failReasons);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(dataMapList)) {
            if (dataMapList.size() > Constants.ADDRESS_BOOK_MAX_COUNT) {
                failReasons.add("文件行数超过" + Constants.ADDRESS_BOOK_MAX_COUNT + "行");
                return messageDTO;
            }
            int failCount = 0;
            List<String> userIds = dataMapList.stream().filter(s -> Objects.nonNull(s.get(AddressBookFileTitleEnum.USER_ID.getValue())))
                    .map(s -> s.get(AddressBookFileTitleEnum.USER_ID.getValue()).toString()).collect(Collectors.toList());
            List<EnterpriseUserDO> enterpriseUserDOS = org.apache.commons.collections4.CollectionUtils.isNotEmpty(userIds)
                    ? enterpriseUserMapper.selectUsersByUserIds(enterpriseId, userIds) : Lists.newArrayList();
            Map<String, EnterpriseUserDO> userIdDOMap = enterpriseUserDOS.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, s -> s));
            int count = dataMapList.size();
            Map<String, Object> objectMap = null;
            String orderNum = null;
            String userId = null;
            String mobile = null;
            List<EnterpriseUserDO> users = Lists.newArrayListWithExpectedSize(0);
            Set<String> userSet = Sets.newHashSet();
            for (int i = 0; i < count; i++) {
                StringBuilder failReason = new StringBuilder();
                objectMap = dataMapList.get(i);
                orderNum = Objects.nonNull(objectMap.get(AddressBookFileTitleEnum.ORDER_NUM.getValue())) ? objectMap.get(AddressBookFileTitleEnum.ORDER_NUM.getValue()).toString() : null;
                userId = Objects.nonNull(objectMap.get(AddressBookFileTitleEnum.USER_ID.getValue())) ? objectMap.get(AddressBookFileTitleEnum.USER_ID.getValue()).toString() : null;
                mobile = Objects.nonNull(objectMap.get(AddressBookFileTitleEnum.MOBILE.getValue())) ? objectMap.get(AddressBookFileTitleEnum.MOBILE.getValue()).toString() : null;
                if (StringUtils.isEmpty(orderNum)) {
                    failCount++;
                    failReason.append("第").append(i + 3).append("行序号不能为空;");
                    failReasons.add(failReason.toString());
                    continue;
                }
                if (StringUtils.isEmpty(userId)) {
                    failCount++;
                    failReason.append("序号").append(orderNum).append("学员ID不能为空;");
                    failReasons.add(failReason.toString());
                    continue;
                }
                if (!userIdDOMap.containsKey(userId) || !userIdDOMap.get(userId).getActive()) {
                    failCount++;
                    failReason.append("序号").append(orderNum).append("学员ID未能识别;");
                    failReasons.add(failReason.toString());
                    continue;
                }
                if (StringUtils.isEmpty(mobile)) {
                    failCount++;
                    failReason.append("序号").append(orderNum).append("学员手机号码未填写;");
                    failReasons.add(failReason.toString());
                    continue;
                }
                if (userSet.contains(userId)) {
                    failCount++;
                    failReason.append("序号").append(orderNum).append("学员ID存在多条,请删除重复学员重新导入;");
                    failReasons.add(failReason.toString());
                    continue;
                }

                if (!ValidateUtil.validateMobile(mobile)) {
                    failCount++;
                    failReason.append("序号").append(orderNum).append("手机号码格式不正确;");
                    failReasons.add(failReason.toString());
                    continue;
                }
                userSet.add(userId);

                EnterpriseUserDO userDO = userIdDOMap.get(userId);
                userDO.setMobile(mobile);
                users.add(userDO);
            }
            messageDTO.setFailCount(failCount);
            messageDTO.setSuccessCount(count - failCount);
            if (failCount > 0) {
                return messageDTO;
            }
            this.batchUpdateUserMobile(enterpriseId, users);
        }
        return messageDTO;
    }

    @Override
    public Object getUserList(String enterpriseId, EnterpriseUserQueryDTO enterpriseUserQueryDTO) {

        String userId = UserHolder.getUser().getUserId();
        PageHelper.startPage(enterpriseUserQueryDTO.getPage_num(), enterpriseUserQueryDTO.getPage_size());
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserMapper
                .fuzzyUsersByUserIdsAndUserName(enterpriseId, null, enterpriseUserQueryDTO.getUserName(), UserStatusEnum.NORMAL.getCode());

        if (CollectionUtils.isEmpty(enterpriseUserDOList)) {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>()));
        }
        // 搜素时添加缓存
        if (StrUtil.isNotBlank(enterpriseUserQueryDTO.getUserName())) {
            List<String> userIds = new ArrayList<>(enterpriseUserDOList).stream().map(EnterpriseUserDO::getUserId).collect(Collectors.toList());
            lruService.putRecentUseUser(enterpriseId, userId, userIds);
        }
        return PageHelperUtil.getPageInfo(new PageInfo<>(enterpriseUserDOList));
    }

    @Override
    public List<EnterpriseUserDTO> getDeptUserList(String enterpriseId, String userName, String deptId,
                                                   String orderBy, String orderRule,
                                                   Long roleId, Integer userStatus, Integer pageNum, Integer pageSize, Boolean isQueryByName, String mobile, Integer userType) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        PageHelper.startPage(pageNum, pageSize);
        List<EnterpriseUserDTO> enterpriseUserList = new ArrayList<>();
        if (AppTypeEnum.isQwType(config.getAppType())) {
            List<String> userIdList = new ArrayList<>();
            if (isQueryByName && StringUtils.isNotBlank(userName)) {
                userIdList = chatService.
                        searchUserOrDeptByName(config.getDingCorpId(), config.getAppType(), userName, Constants.ONE_VALUE_STRING, pageNum, pageSize).getKey();
                if (CollectionUtils.isEmpty(userIdList)) {
                    return enterpriseUserList;
                }
            }
            if (roleId != null) {
                enterpriseUserList = enterpriseUserMapper.fuzzyUsersByDepartment(enterpriseId, deptId, roleId, orderBy, orderRule, null, isQueryByName ? null : userName, userStatus, userIdList, null, mobile, userType);
            } else {
                enterpriseUserList = enterpriseUserMapper.fuzzyUsersByNotRole(enterpriseId, deptId, orderBy, orderRule, null, isQueryByName ? null : userName, userStatus, userIdList, null, mobile, userType);
            }
        } else {
            if (roleId != null) {
                enterpriseUserList = enterpriseUserMapper.fuzzyUsersByDepartment(enterpriseId, deptId, roleId, orderBy, orderRule, isQueryByName ? userName : null, isQueryByName ? null : userName, userStatus, null, null, mobile, userType);
            } else {
                enterpriseUserList = enterpriseUserMapper.fuzzyUsersByNotRole(enterpriseId, deptId, orderBy, orderRule, isQueryByName ? userName : null, isQueryByName ? null : userName, userStatus, null, null, mobile, userType);
            }
        }
        //如果是森宇企业，添加人事状态字段
        initUserPersonnelStatus(enterpriseId, enterpriseUserList);
        this.fetchRoleAndDeptData(enterpriseId, enterpriseUserList);

        List<String> userIdList = enterpriseUserList.stream().map(EnterpriseUserDTO::getUserId).collect(Collectors.toList());
        Map<String, List<UserGroupDTO>> userGroupMap = enterpriseUserGroupService.getUserGroupMap(enterpriseId, userIdList);
        enterpriseUserList.stream()
                .forEach(data -> {
                    //填充用户分组
                    data.setUserGroupList(userGroupMap.get(data.getUserId()));
                });

        // 添加至常用联系人
        if (StrUtil.isNotBlank(userName)) {
            List<String> userIds = enterpriseUserList.stream().map(EnterpriseUserDTO::getUserId).collect(Collectors.toList());
            lruService.putRecentUseUser(enterpriseId, UserHolder.getUser().getUserId(), userIds);
        }
        return enterpriseUserList;
    }

    private List<EnterpriseUserDTO> fetchRoleAndDeptData(String enterpriseId, List<EnterpriseUserDTO> enterpriseUserList) {
        //填充角色信息如果存在角色信息
        List<String> userIdList = initUserRole(enterpriseId, enterpriseUserList);
        if (CollectionUtils.isEmpty(enterpriseUserList)) {
            return enterpriseUserList;
        }
        List<EnterpriseUserDepartmentDO> enterpriseUserDepartmentDOS = enterpriseUserDepartmentMapper.selectEnterpriseUserDepartmentByUserList(enterpriseId, userIdList);
        List<String> deptIdList = ListUtils.emptyIfNull(enterpriseUserDepartmentDOS).stream()
                .map(EnterpriseUserDepartmentDO::getDepartmentId)
                .distinct()
                .collect(Collectors.toList());
        List<SysDepartmentDO> departmentList = new ArrayList<>();
        //如果deptIdList不为空  在查询
        if (CollectionUtils.isNotEmpty(deptIdList)) {
            departmentList = sysDepartmentMapper.getAllDepartmentList(enterpriseId, deptIdList);
        }
        Map<String, String> deptMap = ListUtils.emptyIfNull(departmentList)
                .stream()
                .filter(a -> a.getId() != null && a.getName() != null)
                .collect(Collectors.toMap(data -> data.getId().toString(), SysDepartmentDO::getName, (a, b) -> a));
        Map<String, Set<String>> userDeptMap = ListUtils.emptyIfNull(enterpriseUserDepartmentDOS)
                .stream()
                .collect(Collectors.groupingBy(EnterpriseUserDepartmentDO::getUserId,
                        Collectors.mapping(EnterpriseUserDepartmentDO::getDepartmentId, Collectors.toSet())));
        //填充门店总数以及权限区域列表
        List<AuthRegionStoreDTO> authRegionStoreDTOList = visualService.authRegionStoreByUserList(enterpriseId, userIdList);
        List<AuthStoreCountDTO> authStoreCountDTOS = visualService.authStoreCount(enterpriseId, userIdList, false);
        Map<String, AuthStoreCountDTO> storeCountMap = ListUtils.emptyIfNull(authStoreCountDTOS)
                .stream()
                .collect(Collectors.toMap(AuthStoreCountDTO::getUserId, data -> data, (a, b) -> a));
        Map<String, AuthRegionStoreDTO> authRegionStoreMap = ListUtils.emptyIfNull(authRegionStoreDTOList)
                .stream()
                .collect(Collectors.toMap(AuthRegionStoreDTO::getUserId, data -> data, (a, b) -> a));
        enterpriseUserList.stream()
                .forEach(data -> {
                    if (MapUtils.isNotEmpty(authRegionStoreMap) && authRegionStoreMap.get(data.getUserId()) != null) {
                        AuthRegionStoreDTO authRegionStoreDTO = authRegionStoreMap.get(data.getUserId());
                        data.setAuthRegionStoreList(authRegionStoreDTO.getAuthRegionStoreUserList());
                    }
                    if (MapUtils.isNotEmpty(storeCountMap) && storeCountMap.get(data.getUserId()) != null) {
                        AuthStoreCountDTO authStoreCountDTO = storeCountMap.get(data.getUserId());
                        if (authStoreCountDTO.getStoreCount() != null) {
                            data.setStoreCount(authStoreCountDTO.getStoreCount());
                        } else {
                            data.setStoreCount(0);
                        }
                    }
                    if (MapUtils.isNotEmpty(userDeptMap) && MapUtils.isNotEmpty(deptMap)) {
                        Set<String> departments = userDeptMap.get(data.getUserId());

                        String deptNames = SetUtils.emptyIfNull(departments)
                                .stream()
                                .map(dept -> deptMap.get(dept.toString()))
                                .filter(StrUtil::isNotBlank)
                                .collect(Collectors.joining(","));
                        data.setDepartment(deptNames);
                    }
                });
        return enterpriseUserList;
    }

    @Override
    public List<String> initUserRole(String enterpriseId, List<EnterpriseUserDTO> enterpriseUserList) {
        List<String> userIdList = ListUtils.emptyIfNull(enterpriseUserList)
                .stream()
                .map(EnterpriseUserDTO::getUserId)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(userIdList)) {
            List<UserRoleDTO> userRoleDTOS = sysRoleMapper.userAndRolesByUserId(enterpriseId, userIdList);
            //封装 userId-userRole map,以表示一个用户对应几个角色
            Map<String, List<Long>> userRoleDtoMap = new HashMap<>();
            userRoleDTOS.forEach(roleDto -> {
                List<Long> check = userRoleDtoMap.get(roleDto.getUserId());
                if (check == null) {
                    List<Long> roleDtoList = new ArrayList<>();
                    roleDtoList.add(roleDto.getRoleId());
                    userRoleDtoMap.put(roleDto.getUserId(), roleDtoList);
                } else {
                    check.add(roleDto.getRoleId());
                    userRoleDtoMap.put(roleDto.getUserId(), check);
                }
            });
            //获得角色id -角色名称的map
            Map<Long, String> roleNameMap = ListUtils.emptyIfNull(userRoleDTOS)
                    .stream()
                    .filter(data -> StringUtils.isNotBlank(data.getRoleName()))
                    .collect(Collectors.toMap(UserRoleDTO::getRoleId, UserRoleDTO::getRoleName, (a, b) -> a));
            enterpriseUserList.forEach(user -> {
                List<Long> roleIdList = userRoleDtoMap.get(user.getUserId());
                if (roleIdList != null && roleIdList.size() != 0) {
                    String roleName = roleIdList.stream()
                            .filter(data -> roleNameMap.get(data) != null)
                            .map(role -> roleNameMap.get(role))
                            .collect(Collectors.joining(","));
                    user.setRoleName(roleName);
                }
            });

        }
        return userIdList;
    }

    public void initUserPersonnelStatus(String eid, List<EnterpriseUserDTO> enterpriseUserList) {
        Boolean isOnline = Constants.ONLINE_ENV.equals(env);
        if (isOnline && !Constants.SENYU_ENTERPRISE_ID.equals(eid)) {
            return;
        }
        String effectiveTime = DateUtil.format(new Date());
        List<String> userIds = enterpriseUserList.stream().map(EnterpriseUserDTO::getUserId).collect(Collectors.toList());
        List<UserPersonnelStatusHistoryVO> historyVOList = userPersonnelStatusHistoryDao.getStatusHistoryReport(eid, userIds, effectiveTime, effectiveTime);
        Map<String, String> statusMap = ListUtils.emptyIfNull(historyVOList)
                .stream()
                .collect(Collectors.toMap(UserPersonnelStatusHistoryVO::getUserId, UserPersonnelStatusHistoryVO::getStatusName, (a, b) -> a));
        enterpriseUserList.forEach(user -> {
            user.setUserPersonnelStatus(statusMap.get(user.getUserId()));
        });
    }

    private List<String> getMenus(List<SysMenuDO> data, Long id) {
        List<String> list = new ArrayList<>();
        data.iterator().forEachRemaining(p -> {
            if (p.getParentId().equals(id)) {
                list.add(p.getType());
            }
        });

        return list;
    }

    @Override
    public EnterpriseDetailUserVO getFullDetail(String enterpriseId, String userId) {
        EnterpriseUserDTO userDetail = enterpriseUserMapper.getUserDetail(enterpriseId, userId);
        if (userDetail == null) {
            return null;
        }
        EnterpriseDetailUserVO enterpriseUserVo = new EnterpriseDetailUserVO();
        enterpriseUserVo.setUserId(userId);
        enterpriseUserVo.setUserName(userDetail.getName());
        enterpriseUserVo.setMobile(userDetail.getMobile());
        enterpriseUserVo.setEmail(userDetail.getEmail());
        enterpriseUserVo.setJobnumber(userDetail.getJobnumber());
        enterpriseUserVo.setRemark(userDetail.getRemark());
        enterpriseUserVo.setFaceUrl(userDetail.getFaceUrl());
        enterpriseUserVo.setUserStatus(userDetail.getUserStatus());
        enterpriseUserVo.setThirdOaUniqueFlag(userDetail.getThirdOaUniqueFlag());
        //人员所在部门数据处理
        List<RegionDTO> regionBaseData = getRegionBaseData(enterpriseId, userId);
        enterpriseUserVo.setRegionBaseDataList(regionBaseData);

        //查询该用户的直接上级
        SubordinateMappingDO subordinateMappingDO = subordinateMappingDAO.selectByUserIdAndType(enterpriseId, userId);
        if (subordinateMappingDO != null) {
            EnterpriseUserDO directSuperior = enterpriseUserDao.selectByUserId(enterpriseId, subordinateMappingDO.getPersonalId());
            if (directSuperior != null) {
                enterpriseUserVo.setDirectSuperiorId(subordinateMappingDO.getPersonalId());
                enterpriseUserVo.setDirectSource(subordinateMappingDO.getSource());
                enterpriseUserVo.setDirectSuperiorName(directSuperior.getName());
            }
        }
        List<SysRoleDO> sysRoleDo = sysRoleMapper.getSysRoleByUserId(enterpriseId, userId);
        if (sysRoleDo != null) {
            List<EntUserRoleDTO> userRoles = sysRoleDo.stream()
                    .filter(data -> data != null)
                    .map(role -> {
                        EntUserRoleDTO entUserRoleDTO = new EntUserRoleDTO();
                        entUserRoleDTO.setRoleId(role.getId());
                        entUserRoleDTO.setRoleName(role.getRoleName());
                        entUserRoleDTO.setSource(role.getSource());
                        return entUserRoleDTO;
                    })
                    .collect(Collectors.toList());
            enterpriseUserVo.setUserRoles(userRoles);
        }
        List<AuthRegionStoreUserDTO> authRegionStoreDTOList = visualService.authRegionStore(enterpriseId, userId);
        enterpriseUserVo.setAuthRegionStoreList(authRegionStoreDTOList);
        //填充用户分组
        Map<String, List<UserGroupDTO>> userGroupMap = enterpriseUserGroupService.getUserGroupMap(enterpriseId, Collections.singletonList(userId));
        enterpriseUserVo.setUserGroupList(userGroupMap.get(userId));
        Map<String, SubordinateUserRangeDTO> subordinateUserRangeMap = fillUserSubordinateNames(enterpriseId, Collections.singletonList(userId));
        if (subordinateUserRangeMap.get(userId) != null) {
            enterpriseUserVo.setSubordinateUserRange(subordinateUserRangeMap.get(userId).getSubordinateUserRange());
            enterpriseUserVo.setSourceList(subordinateUserRangeMap.get(userId).getSourceList());
            enterpriseUserVo.setMySubordinates(subordinateUserRangeMap.get(userId).getMySubordinates());
        }
        // 是否修改过密码
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        Boolean hasPassword = enterpriseUserMapper.hasPassword(userDetail.getUnionid());
        enterpriseUserVo.setHasPassword(ObjectUtil.isNotNull(hasPassword) ? hasPassword : false);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        return enterpriseUserVo;
    }


    /**
     * 根据人员区域映射表 查出人员所在区域的id
     * 根据区域回查区域的具体信息
     *
     * @param enterpriseId
     * @param userId
     * @return
     */
    public List<RegionDTO> getRegionBaseData(String enterpriseId, String userId) {
        //根据人员id查询人员所在部门
        List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.listUserRegionMappingByUserId(enterpriseId, Lists.newArrayList(userId));
        if (CollectionUtils.isEmpty(userRegionMappingDOS)) {
            return null;
        }
        //所有的regionids
        List<String> regionIds = userRegionMappingDOS.stream().map(UserRegionMappingDO::getRegionId).collect(Collectors.toList());
        //根据regionIds查询所有的区域数据
        List<RegionDO> regionList = regionService.getRegionDOsByRegionIds(enterpriseId, regionIds);
        List<RegionDTO> regionDTOS = new ArrayList<>();
        for (RegionDO tempRegion : regionList) {
            RegionDTO regionDTO = new RegionDTO();
            regionDTO.setId(tempRegion.getId());
            regionDTO.setParentId(tempRegion.getParentId());
            regionDTO.setName(tempRegion.getName());
            regionDTO.setType(tempRegion.getRegionType());
            regionDTOS.add(regionDTO);
        }
        return regionDTOS;
    }

    @Override
    @Transactional
    public Boolean deleteEnterpriseUser(String enterpriseId, String userId) {
        try {

            //删除用户和角色相关信息
            sysRoleMapper.deleteRolesByPerson(enterpriseId, Lists.newArrayList(userId), false);
            //删除用户和部门的关联信息
            sysDepartmentMapper.deleteDepartmentUser(enterpriseId, userId);
            //删除用户收藏的门店信息
            coolcollegeStoreUserCollectMapper.deleteStoreCollect(enterpriseId, userId);
            //删除用户信息
            enterpriseUserMapper.batchDeleteUserIds(enterpriseId, Lists.newArrayList(userId));
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "用户删除失败");
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean freezeEnterpriseUser(String enterpriseId, String userId) {
//        EnterpriseUserDO enterpriseUserDO=new EnterpriseUserDO();
//        enterpriseUserDO.setActive(2l);
//        enterpriseUserMapper.batchUpdateUserMobile();
//        return null;
        return null;
    }

    @Override
    //@Transactional(rollbackFor = Exception.class)
    public Boolean updateDetailUser(String eid, EnterpriseUserRequestBody userRequestBody, Boolean enableDingSync, CurrentUser currentUser) {
        EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(eid, userRequestBody.getUserId());
        if (Objects.isNull(enterpriseUserDO)) {
            throw new ServiceException(ErrorCodeEnum.USER_NON_EXISTENT);
        }
        if (StringUtils.isNotBlank(userRequestBody.getMobile())) {
            EnterpriseUserDO enterpriseUser = enterpriseUserMapper.getEnterpriseUserByMobile(eid, userRequestBody.getMobile(), enterpriseUserDO.getUnionid());
            if (Objects.nonNull(enterpriseUser)) {
                throw new ServiceException(ErrorCodeEnum.MOBILE_USED, enterpriseUser.getName());
            }
        }
        if (StringUtils.isNotBlank(userRequestBody.getThirdOaUniqueFlag())) {
            EnterpriseUserDO exsitThirdOaUniqueFlag = enterpriseUserMapper.getEnterpriseUserByThirdOaUniqueFlag(eid, userRequestBody.getThirdOaUniqueFlag(), enterpriseUserDO.getUnionid());
            if (Objects.nonNull(exsitThirdOaUniqueFlag)) {
                throw new ServiceException(ErrorCodeEnum.THIRDOAFLAG_USED, exsitThirdOaUniqueFlag.getName());
            }
        }
        if (Objects.nonNull(enterpriseUserDO.getUserType()) && UserTypeEnum.EXTERNAL_USER.getCode() == enterpriseUserDO.getUserType()) {
            if (CollectionUtils.isNotEmpty(userRequestBody.getRegionIds())) {
                List<RegionDO> regionList = regionService.getRegionDOsByRegionIds(eid, userRequestBody.getRegionIds());
                if (CollectionUtils.isEmpty(regionList)) {
                    throw new ServiceException(ErrorCodeEnum.REGION_IS_NULL);
                }
                List<RegionDO> externalRegionList = regionList.stream().filter(o -> o.getIsExternalNode()).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(externalRegionList)) {
                    throw new ServiceException(ErrorCodeEnum.EXTERNAL_USER_REGION_IS_ERROR);
                }
            }
        }

        //获得这些角色下的所有用户 柠檬向右 人数限制
        if ("d9c8f45190dc4071b8f18596c86aacb4".equals(eid)) {
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
            List<String> enterpriseUserList = enterpriseUserRoleDao.selectUserIdsByRoleIdList(eid, roleIds);
            String[] split = userRequestBody.getRoleId().split(",");
            List<Long> updateRoles = new ArrayList<>();
            for(String s : split){
                updateRoles.add(Long.valueOf(s));
            }
            //判断用户角色权限是否无招商角色
            if (!enterpriseUserList.contains(userRequestBody.getUserId())) {
                //判断修改角色权限和招商角色是否有交集
                if (!Collections.disjoint(updateRoles, roleIds)){
                    if (CollectionUtils.isNotEmpty(enterpriseUserList)) {
                        //计算拥有招商职位人的数量
                        // >=9,修改失败
                        if (enterpriseUserList.size() >= 9) {
                            throw new ServiceException(POSITION_IS_FULL);
                        }
                    }
                }

            }
        }
        EnterpriseUserDO userDO = new EnterpriseUserDO();
        //部门信息更新
        //人员部门映射处理
        handleUserRegionMapping(eid, userRequestBody.getRegionIds(), userRequestBody.getUserId(), currentUser, userDO);
        //处理直属上级
        handleDirectSuperior(eid, userRequestBody.getUserId(), userRequestBody.getDirectSuperiorId(), currentUser);
        handleSubordinateMappingNew(eid, userRequestBody.getUserId(), userRequestBody.getSubordinateUserRange(), userRequestBody.getSourceList(), userRequestBody.getMySubordinates(), currentUser, userDO);
        // 更新用户分组
        if (CollectionUtils.isNotEmpty(userRequestBody.getGroupIdList())) {
            enterpriseUserGroupService.updateUserGroup(eid, userRequestBody.getGroupIdList(), userRequestBody.getUserId(), currentUser);
        }

        userDO.setUserId(userRequestBody.getUserId());
        userDO.setName(userRequestBody.getUserName());
        userDO.setFaceUrl(userRequestBody.getFaceUrl());
        userDO.setAvatar(userRequestBody.getFaceUrl());
        userDO.setMobile(userRequestBody.getMobile());
        userDO.setEmail(userRequestBody.getEmail());
        String jobnumber = userRequestBody.getJobnumber();
        if (StrUtil.isNotBlank(jobnumber)) {
            if (Pattern.matches("[\u4E00-\u9FA5]+", jobnumber)) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "工号不能存在中文");
            }
            int num = enterpriseUserMapper.getNumByJobnumber(eid, jobnumber, userRequestBody.getUserId());
            if (num > 0) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "工号已经存在");
            }
        }
        if (SongXiaEnterpriseEnum.songXiaCompany(eid)) {
            log.info("松下企业 eid={}", eid);
            if (StringUtils.isNotBlank(userRequestBody.getJobnumber())) {
                userDO.setJobnumber(userRequestBody.getJobnumber());
            } else {
                String jobNum = addUserNumBySongXia(eid, userRequestBody.getUserId(), userRequestBody.getRoleId());
                userDO.setJobnumber(jobNum);
            }
        } else {
            userDO.setJobnumber(userRequestBody.getJobnumber());
        }
        userDO.setRemark(userRequestBody.getRemark());
        userDO.setUserStatus(userRequestBody.getUserStatus());
        if (StringUtils.isNotBlank(userRequestBody.getThirdOaUniqueFlag())) {
            userDO.setThirdOaUniqueFlag(userRequestBody.getThirdOaUniqueFlag());
        }
        enterpriseUserMapper.overwriteUpdateEnterpriseUser(eid, userDO);
        //更新用户角色
        if (StringUtils.isNotEmpty(userRequestBody.getRoleId())) {
            updateUserRole(eid, userRequestBody.getRoleId(), userRequestBody.getUserId(), enableDingSync);
        }
        //更新权限门店、区域的信息
        updateUserAuth(eid, userRequestBody.getAuthRegionStoreList(), userRequestBody.getUserId(), enableDingSync);
        //更新平台库用户状态
        if (Objects.nonNull(userRequestBody.getUserStatus()) && !enterpriseUserDO.getUserStatus().equals(userRequestBody.getUserStatus())) {
            DataSourceHelper.reset();
            //更新平台库 enterprise_user_mapping 用户状态
            enterpriseUserMappingService.updateEnterpriseUserStatus(enterpriseUserDO.getUnionid(), eid, userRequestBody.getUserStatus());
        }
        if (Objects.nonNull(userRequestBody.getMobile()) && !userRequestBody.getMobile().equals(enterpriseUserDO.getMobile())) {
            DataSourceHelper.reset();
            //更新平台库手机号
            EnterpriseUserDO update = new EnterpriseUserDO();
            update.setUnionid(enterpriseUserDO.getUnionid());
            update.setMobile(userRequestBody.getMobile());
            if (StringUtils.isNotBlank(userRequestBody.getThirdOaUniqueFlag())) {
                update.setThirdOaUniqueFlag(userRequestBody.getThirdOaUniqueFlag());
            }
            enterpriseUserMapper.updateConfigEnterpriseUserByUnionId(update);
        }
        // 身份证号不为空
        if (StringUtil.isNotBlank(userDO.getThirdOaUniqueFlag())) {
            SyncRequest syncRequest = SyncRequest.builder()
                    .employeeCode(userDO.getJobnumber())
                    .enterpriseId(eid)
                    .userId(userDO.getUserId())
                    .thirdOaUniqueFlag(userDO.getThirdOaUniqueFlag())
                    .build();
            simpleMessageService.send(JSONObject.toJSONString(syncRequest), RocketMqTagEnum.THIRD_OA_SYNC_SINGLE_QUEUE);
        }
        //用户数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(eid, Arrays.asList(userRequestBody.getUserId()), ChangeDataOperation.UPDATE.getCode(), ChangeDataType.USER.getCode());
        return Boolean.TRUE;
    }

    public String addUserNumBySongXia(String eid, String promoterUserId, String roleId) {
        //查询当前用户角色是否有促销员
        String[] roleIdArray = roleId.split(",");
        List<String> roleIds = Arrays.asList(roleIdArray);
        log.info("促销员编号：{}", SongxiaRoleEnum.PROMOTER.getCode());
        String newString = "";
        EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserIdIgnoreActive(eid, promoterUserId);
        if (roleIds.contains(SongxiaRoleEnum.PROMOTER.getCode())) {
            log.info("当前用户角色有促销员 roleId={}, promoterUserId={}", roleId, promoterUserId);
            //查询该用户是否有促销员编号
            if (enterpriseUserDO != null) {
                if (StringUtil.isBlank(enterpriseUserDO.getJobnumber())) {
                    log.info("该用户没有促销员编号，新增促销员编号 promoterUserNum={}", promoterUserId);
                    //查询目前编号最大值
                    String maxNum = enterpriseUserMapper.selectMaxNum(eid);
                    int num = 0;
                    try {
                        // 提取数字部分并转换为整数
                        num = Integer.parseInt(maxNum.substring(1));
                    } catch (Exception e) {
                        log.error("转换异常", e);
                    }
                    // 在最大值基础上加 1
                    int newNum = num + 1;
                    // 格式化为原始格式
                    newString = "P" + newNum;
                } else {
                    newString = enterpriseUserDO.getJobnumber();
                }
            }

        } else {
            //非促销员依旧保留原工号
            if (enterpriseUserDO != null) {
                newString = enterpriseUserDO.getJobnumber();
            }
        }
        return newString;
    }


    /**
     * 人员部门关系映射
     *
     * @param enterpriseId
     * @param newRegionIds
     * @param userId
     * @param currentUser
     */
    @Override
    public void handleUserRegionMapping(String enterpriseId, List<String> newRegionIds, String userId, CurrentUser currentUser, EnterpriseUserDO userDO) {
        //查询该用户当前所在部门
        List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.listUserRegionMappingByUserId(enterpriseId, Lists.newArrayList(userId));

        List<String> oldRegionIds = userRegionMappingDOS.stream().map(UserRegionMappingDO::getRegionId).collect(Collectors.toList());

        //查询未分组区域 默认
        RegionDO regionDO = regionService.getUnclassifiedRegionDO(enterpriseId);

        //异常情况处理 如果人员所属部门为null ,将该人员添加到未分组中
        if (CollectionUtils.isEmpty(oldRegionIds)) {
            addUserRegionMappingDO(enterpriseId, userId, regionDO.getRegionId(), currentUser);
            oldRegionIds.add(regionDO.getRegionId());
        }

        //删掉的部门映射关系
        List<String> deleteList = oldRegionIds.stream().filter(data -> !newRegionIds.contains(data))
                .collect(Collectors.toList());
        log.info("删掉的人员部门映射关系:{}", JSONObject.toJSONString(deleteList));
        if (CollectionUtils.isNotEmpty(deleteList)) {
            //删掉的映射关系 查看人员是否还在其他部门，如果在其他部门，只将映射关系删除，如果不在其他部门，不仅要删除关系映射，还需要将用户关联到未分组
            userRegionMappingDAO.deletedByUserIdAndRegionId(enterpriseId, userId, deleteList);
            //之前的部门列表-删除的部门列表=留存的
            List<String> otherUserRegionMappingList = oldRegionIds.stream().filter(data -> !deleteList.contains(data))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(otherUserRegionMappingList)) {
                addUserRegionMappingDO(enterpriseId, userId, regionDO.getRegionId(), currentUser);
            }
        }

        //新增的部门映射关系
        List<String> addRegionsList = newRegionIds.stream().filter(data -> !oldRegionIds.contains(data))
                .collect(Collectors.toList());
        log.info("删掉的人员部门映射关系:{}", JSONObject.toJSONString(addRegionsList));
        //批量新增映射关系
        ArrayList<UserRegionMappingDO> UserRegionMappingList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(addRegionsList)) {
            for (String regionId : addRegionsList) {
                UserRegionMappingDO userRegionMappingDO = new UserRegionMappingDO();
                userRegionMappingDO.setUserId(userId);
                userRegionMappingDO.setRegionId(regionId);
                userRegionMappingDO.setCreateId(currentUser.getUserId());
                userRegionMappingDO.setUpdateId(currentUser.getUserId());
                userRegionMappingDO.setSource(UserAuthMappingSourceEnum.CREATE.getCode());
                UserRegionMappingList.add(userRegionMappingDO);
            }
            userRegionMappingDAO.batchInsert(enterpriseId, UserRegionMappingList);
            //删除未分组映射(只要addRegionsList不为null 人员就不在未分组中)
            userRegionMappingDAO.deletedByUserIdAndRegionId(enterpriseId, userId, Lists.newArrayList(regionDO.getRegionId()));
        }

        List<EnterpriseUserDO> userRegionPathListStr = this.getUserRegionPathListStr(enterpriseId, Arrays.asList(userId));
        //更新enterpriseUser表
        userDO.setUserRegionIds(userRegionPathListStr.get(Constants.INDEX_ZERO).getUserRegionIds());
    }

    /**
     * 新增未分组映射
     *
     * @return
     */
    public void addUserRegionMappingDO(String enterpriseId, String userId, String regionId, CurrentUser currentUser) {
        UserRegionMappingDO userRegionMappingDO = new UserRegionMappingDO();
        userRegionMappingDO.setUserId(userId);
        userRegionMappingDO.setRegionId(regionId);
        userRegionMappingDO.setCreateId(currentUser.getUserId());
        userRegionMappingDO.setUpdateId(currentUser.getUserId());
        //将用户添加到未分组
        userRegionMappingDAO.addUserRegionMapping(enterpriseId, userRegionMappingDO);
    }

    /**
     * 处理直接上级
     *
     * @param eid
     * @param currentUser
     */
    public void handleDirectSuperior(String eid, String userId, String directSuperiorId, CurrentUser currentUser) {
        //查询该用户的直属上级
        SubordinateMappingDO subordinateMappingDO = subordinateMappingDAO.selectByUserIdAndType(eid, userId);
        if (subordinateMappingDO != null && subordinateMappingDO.getPersonalId().equals(directSuperiorId)) {
            log.info("没有更改上级,不在处理userId:{}", userId);
            return;
        }
        if (subordinateMappingDO != null && !subordinateMappingDO.getPersonalId().equals(directSuperiorId)
                && SubordinateSourceEnum.AUTO.getCode().equals(subordinateMappingDO.getSource())) {
            EnterpriseUserDO directSuperior = enterpriseUserDao.selectByUserId(eid, subordinateMappingDO.getPersonalId());
            if (directSuperior != null) {
                throw new ServiceException(ErrorCodeEnum.USER_DIRECT_SUPERIOR_NOT_DELETE);
            }
        }
        //用户的直属上级id为null的时候直接删除 之前的直接上级映射
        if (StringUtils.isBlank(directSuperiorId)) {
            //直接删除当前人的直属上级
            subordinateMappingDAO.deletedByUserIdsAndType(eid, Lists.newArrayList(userId));
            return;
        }
        if (subordinateMappingDO != null) {
            //修改直属上级
            subordinateMappingDAO.updateByUserIdAndType(eid, userId, directSuperiorId, currentUser.getUserId());
        } else {
            // 新增直属上级
            SubordinateMappingDO subordinateMapping = new SubordinateMappingDO();
            subordinateMapping.setUserId(userId);
            subordinateMapping.setPersonalId(directSuperiorId);
            subordinateMapping.setType(Constants.INDEX_ONE);
            subordinateMapping.setCreateId(currentUser.getUserId());
            subordinateMapping.setUpdateId(currentUser.getUserId());
            subordinateMapping.setSource(SubordinateSourceEnum.SELECT.getCode());
            subordinateMappingDAO.batchInsertSubordinateMapping(eid, Lists.newArrayList(subordinateMapping));
        }
    }

    /**
     * 处理管辖用户
     *
     * @param eid
     * @param currentUser
     */
    public void handleSubordinateMappingNew(String eid, String userId, String subordinateUserRange, List<String> sourceList, List<MySubordinatesDTO> mySubordinates, CurrentUser currentUser, EnterpriseUserDO userDO) {
        //删除该用户的所有下属
        subordinateMappingDAO.deletedByUserIds(eid, Lists.newArrayList(userId));
        List<SubordinateMappingDO> subordinateMappingList = new ArrayList<>();
        userDO.setSubordinateChange(Boolean.TRUE);
        userDO.setSubordinateRange(subordinateUserRange);
        if (UserSelectRangeEnum.DEFINE.getCode().equals(subordinateUserRange)) {
            // 手动选择下属
            if (sourceList.contains(SubordinateSourceEnum.SELECT.getCode()) &&
                    CollectionUtils.isNotEmpty(mySubordinates)) {
                //新增该用户的所有节点
                List<String> regionIds = mySubordinates.stream().filter(x -> x.getRegionId() != null).map(MySubordinatesDTO::getRegionId).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(regionIds)) {
                    for (String regionId : regionIds) {
                        SubordinateMappingDO subordinateMappingDO = fillSubordinateMappingDO(userId, null, regionId, currentUser, UserSelectRangeEnum.DEFINE.getCode(), SubordinateSourceEnum.SELECT.getCode());
                        subordinateMappingList.add(subordinateMappingDO);
                    }
                }
                //新增该用户的下属人员
                List<String> personalIds = mySubordinates.stream().filter(x -> x.getPersonalId() != null).map(MySubordinatesDTO::getPersonalId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(personalIds)) {
                    for (String personalId : personalIds) {
                        SubordinateMappingDO subordinateMappingDO = fillSubordinateMappingDO(userId, personalId, null, currentUser, UserSelectRangeEnum.DEFINE.getCode(), SubordinateSourceEnum.SELECT.getCode());
                        subordinateMappingList.add(subordinateMappingDO);
                    }
                }
            }
            // 关联管辖区域权限
            if (sourceList.contains(SubordinateSourceEnum.AUTO.getCode())) {
                SubordinateMappingDO subordinateMappingDO = fillSubordinateMappingDO(userId, null, Constants.ZERO_STR, currentUser, UserSelectRangeEnum.DEFINE.getCode(), SubordinateSourceEnum.AUTO.getCode());
                subordinateMappingList.add(subordinateMappingDO);
            }
        }
        if (CollectionUtils.isNotEmpty(subordinateMappingList)) {
            subordinateMappingDAO.batchInsertSubordinateMapping(eid, subordinateMappingList);
        }
    }

    /**
     * 填充下属信息
     *
     * @param userId
     * @param personalId
     * @param regionId
     * @param currentUser
     * @param userRange
     * @param source
     * @return
     */
    private SubordinateMappingDO fillSubordinateMappingDO(String userId, String personalId, String regionId, CurrentUser currentUser, String userRange, String source) {
        SubordinateMappingDO subordinateMappingDO = new SubordinateMappingDO();
        subordinateMappingDO.setUserId(userId);
        if (StringUtils.isNotBlank(personalId)) {
            subordinateMappingDO.setPersonalId(personalId);
        }
        if (StringUtils.isNotBlank(regionId)) {
            subordinateMappingDO.setRegionId(regionId);
        }
        subordinateMappingDO.setCreateId(currentUser.getUserId());
        subordinateMappingDO.setUpdateId(currentUser.getUserId());
        subordinateMappingDO.setType(Constants.INDEX_ZERO);
        subordinateMappingDO.setUserRange(userRange);
        if (StringUtils.isNotBlank(source)) {
            subordinateMappingDO.setSource(source);
        }
        return subordinateMappingDO;
    }

    /**
     * 会删roleIdList中没有的角色
     *
     * @param eid
     * @param roleIdList
     * @param userId
     */
    private void updateUserRoleAndDeleteNotExistRole(String eid, List<Long> roleIdList, String userId, RoleSyncTypeEnum syncType) {
        //获得当前用户的角色列表
        List<EntUserRoleDTO> userRoles = enterpriseUserRoleMapper.selectUserRoleByUserId(eid, userId);
        List<Long> roleIds = ListUtils.emptyIfNull(userRoles).stream().map(EntUserRoleDTO::getRoleId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            for (Long roleId : roleIdList) {
                if (!roleIds.contains(roleId)) {
                    enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(String.valueOf(roleId), userId, syncType.getCode()));
                }
            }
        }
        List<Long> syncRoleIds = ListUtils.emptyIfNull(userRoles).stream().filter(o -> syncType.getCode().equals(o.getSyncType())).map(EntUserRoleDTO::getRoleId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(syncRoleIds)) {
            if (CollectionUtils.isEmpty(roleIdList)) {
                enterpriseUserRoleMapper.deleteBatchByUserIdAndRoleId(eid, userId, syncRoleIds);
                return;
            }
            List<Long> deleteUserRoleIds = syncRoleIds.stream().filter(id -> !roleIdList.contains(id)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deleteUserRoleIds)) {
                enterpriseUserRoleMapper.deleteBatchByUserIdAndRoleId(eid, userId, deleteUserRoleIds);
            }

        }
    }


    private void updateUserRole(String eid, String roleId, String userId, Boolean enableDingSync) {
        String[] roleIdArray = roleId.split(",");
        List<String> roleIdList = Arrays.asList(roleIdArray);
        List<Long> insertList = new ArrayList<>();
        //获得当前用户的角色列表
        List<EntUserRoleDTO> userRoles = enterpriseUserRoleMapper.selectUserRoleByUserId(eid, userId);
        Map<Long, EntUserRoleDTO> roleMap = (userRoles == null || userRoles.size() == 0) ? new HashMap<>() :
                userRoles.stream().collect(Collectors.toMap(EntUserRoleDTO::getRoleId, data -> data, (a, b) -> a));
        List<String> mainUserIdList = enterpriseUserMapper.getMainAdmin(eid).stream()
                .map(EnterpriseUserDO::getUserId)
                .collect(Collectors.toList());
        //新增的角色列表中没有管理员，但是原有的角色列表中有管理员
        Long masterRoleId = sysRoleService.getRoleIdByRoleEnum(eid, Role.MASTER.getRoleEnum());

        Boolean deleteAdmin = roleMap.get(masterRoleId) != null && !roleIdList.contains(masterRoleId.toString());
        //不能删除主管理员的管理员权限
        if (mainUserIdList.contains(userId) && deleteAdmin) {
            throw new ServiceException(ErrorCodeEnum.NOT_SUPPORTED_DELETE.getCode(), "主管理员不能移动");
        }
        roleIdList.forEach(id -> {
            EntUserRoleDTO check = roleMap.get(Long.valueOf(id));
            if (check == null) {
                insertList.add(Long.valueOf(id));
                enterpriseUserRoleMapper.save(eid, new EnterpriseUserRole(id, userId, RoleSyncTypeEnum.CREATE.getCode()));
            } else {
                userRoles.remove(check);
            }
        });
        if (insertList.size() != 0) {
            List<SysRoleDO> sysRoleList = sysRoleMapper.getRoleByRoleIds(eid, insertList);
            SysRoleDO dingRole = sysRoleList.stream()
                    .filter(userRole -> !PositionSourceEnum.CREATE.getValue().equals(userRole.getSource()))
                    .findFirst().orElse(null);
            if (dingRole != null && enableDingSync) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "不能添加钉钉角色");
            }
        }
        if (userRoles.size() != 0) {
            EntUserRoleDTO dingDTOList = userRoles.stream()
                    .filter(userRole -> !PositionSourceEnum.CREATE.getValue().equals(userRole.getSource()))
                    .findFirst().orElse(null);
            if (dingDTOList != null && enableDingSync) {
                throw new ServiceException(ErrorCodeEnum.NOT_SUPPORTED_DELETE.getCode(), "不能删除钉钉角色");
            }
            List<Long> userRoleIds = userRoles.stream().map(EntUserRoleDTO::getUserRoleId).collect(Collectors.toList());
            enterpriseUserRoleMapper.deleteBatchByPrimaryKey(eid, userRoleIds);
        }
    }

    @Override
    public void updateUserAuth(String eid, List<AuthRegionStoreUserDTO> authRegionStoreList, String userId, Boolean enableDingSync) {
        CurrentUser user = UserHolder.getUser();
        long now = System.currentTimeMillis();
        // 门店类型区域的门店id
        List<String> authRegionStoreIdList = ListUtils.emptyIfNull(authRegionStoreList).stream()
                .filter(data -> !data.getStoreFlag() && StringUtils.isNotBlank(data.getStoreId()))
                .map(data -> data.getStoreId()).collect(Collectors.toList());

        List<UserAuthMappingDO> userAuthMappingDOList = ListUtils.emptyIfNull(authRegionStoreList).stream()
                .filter(data -> !data.getStoreFlag() || (data.getStoreFlag() && !authRegionStoreIdList.contains(data.getId())))
                .map(data -> {
                    UserAuthMappingDO userAuthMappingDO = new UserAuthMappingDO();
                    userAuthMappingDO.setUserId(userId);
                    userAuthMappingDO.setMappingId(data.getId());
                    userAuthMappingDO.setType(data.getStoreFlag() ? UserAuthMappingTypeEnum.STORE.getCode()
                            : UserAuthMappingTypeEnum.REGION.getCode());
                    userAuthMappingDO.setCreateId(user.getUserId());
                    userAuthMappingDO.setCreateTime(now);
                    return userAuthMappingDO;
                })
                .collect(Collectors.toList());
        List<UserAuthMappingDO> userAuthList = userAuthMappingMapper.listUserAuthMappingByUserId(eid, userId);
        //TODO 校验是否有删除用户钉钉的区域门店权限 1.筛选出钉钉同步带来的权限
        List<UserAuthMappingDO> syncUserAuthList = userAuthList.stream()
                .filter(auth -> UserAuthMappingSourceEnum.SYNC.getCode().equals(auth.getSource()))
                .collect(Collectors.toList());
        List<String> syncUserAuths = syncUserAuthList.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
        //2.获取更新的权限
        List<String> updateAuths = userAuthMappingDOList.stream()
                .map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
        //如果更新的权限不包含全部的原有的钉钉权限且开启了钉钉同步，提升失败
        syncUserAuths.removeAll(updateAuths);
        //在剩下的区域权限中赛选有效的区域,先把mappingid的列表转换回实体列表
        List<UserAuthMappingDO> validAuth = userAuthList.stream().filter(data -> syncUserAuths.contains(data.getMappingId()))
                .collect(Collectors.toList());
        List<AuthRegionStoreUserDTO> validCheck = visualService.getAuthRegionStoreUserDTO(eid, validAuth);
        if (validCheck != null && validCheck.size() != 0 && enableDingSync) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "不能删除同步的区域权限");
        }
        //校验结束
        Map<String, UserAuthMappingDO> authMap = userAuthList.stream()
                .collect(Collectors.toMap(UserAuthMappingDO::getMappingId, data -> data, (a, b) -> a));
        List<UserAuthMappingDO> insertList = new ArrayList<>();
        userAuthMappingDOList.forEach(userAuth -> {
            UserAuthMappingDO check = authMap.get(userAuth.getMappingId());
            if (check == null) {
                insertList.add(userAuth);
            } else {
                userAuthList.remove(check);
            }
        });
        if (CollectionUtils.isNotEmpty(userAuthList)) {
            List<Long> deleteIds = userAuthList.stream().map(UserAuthMappingDO::getId).collect(Collectors.toList());
            userAuthMappingMapper.deleteAuthMappingByIds(eid, deleteIds);
            // 门店区域权限取消时，同时取消对应区域门店的权限
            cancelStoreRegionCorrespondAuth(eid, userId, userAuthList);
        }
        if(CollectionUtils.isNotEmpty(insertList)){
            userAuthMappingMapper.batchInsertUserAuthMapping(eid,insertList);
        }
    }

    @Override
    public Boolean getUserByMobile(String mobile) {
        List<EnterpriseUserDO> platformUser = enterpriseUserDao.getPlatformUserByMobile(mobile);
        if(CollectionUtils.isEmpty(platformUser)){
            return false;
        }
        List<EnterpriseUserDO> activeUser = platformUser.stream().filter(o->Objects.isNull(o.getActive()) || o.getActive()).collect(Collectors.toList());
        return CollectionUtils.isNotEmpty(activeUser);
    }

    private void addPromotionStore(String eid, String userId, List<UserAuthMappingDO> insertList) {
        //判断被修改人是不是促销员
        List<Long> userRoleIds = enterpriseUserRoleMapper.getUserRoleIds(eid, userId);
        if (userRoleIds.contains(PanasonicUserRoleEnum.PROMOTER.getCode())) {
            //查insertList下门店
            List<String> regionIds = insertList.stream().map(c -> c.getMappingId()).collect(Collectors.toList());
            List<StoreAreaDTO> storeAreaDTOS;
            if (CollectionUtils.isNotEmpty(regionIds)) {
                storeAreaDTOS = storeMapper.listStoreByRegionIdList(eid, regionIds);
                List<String> storeIds = storeAreaDTOS.stream().map(c -> c.getStoreId()).collect(Collectors.toList());
                //已有门店信息
                List<String> myStores = songXiaMapper.getPromoterStoreIdsByUserId(userId);
                //取差集 新的门店
                storeIds.removeAll(myStores);
                //查实需门店
                List<ActualStoreDO> dos = songXiaMapper.getActualStoreByStoreIds(storeIds);

                //查询促销员信息
                PromoterInfoDO user = songXiaMapper.getPanasonicPromoterInfoByUserId(eid,userId);
                if (CollectionUtils.isNotEmpty(dos)){
                    List<PromoterStoreInfoDO> insertDOList= new ArrayList<>();
                    List<String> managerStoreIds = dos.stream().map(ActualStoreDO::getStoreId).collect(Collectors.toList());
                    List<ManageStoreCategoryCodeDO> mappingByStoreIds = songXiaMapper.getCategoryMappingByStoreIds(managerStoreIds);
                    Map<String, List<ManageStoreCategoryCodeDO>> categoryMap = mappingByStoreIds.stream().collect(Collectors.groupingBy(ManageStoreCategoryCodeDO::getStoreId));
                    for (ActualStoreDO aDo : dos) {
                        //根据管理门店id查询对应品类
                        List<ManageStoreCategoryCodeDO> storeCategoryCodeDOS = categoryMap.get(aDo.getStoreId());
                        if (CollectionUtils.isNotEmpty(storeCategoryCodeDOS)){
                            for (ManageStoreCategoryCodeDO storeCategoryCodeDO : storeCategoryCodeDOS) {
                                //生成do
                                PromoterStoreInfoDO infoDO = new PromoterStoreInfoDO();
                                infoDO.setPromoterUserId(userId);
                                if (Objects.nonNull(user)){
                                    infoDO.setPromoterName(user.getPromoterName());
                                    infoDO.setPromoterNum(user.getPromoterNum());
                                    infoDO.setBusinessRegionCode(user.getBusinessRegionCode());
                                    infoDO.setBusinessRegionName(user.getBusinessRegionName());
                                    infoDO.setBusinessSegmentCode(user.getBusinessSegmentCode());
                                    infoDO.setBusinessSegmentName(user.getBusinessSegmentName());
                                    infoDO.setWithoutBasicPay(user.getWithoutBasicPay());
                                    infoDO.setWithoutCommission(user.getWithoutCommission());
                                    infoDO.setLocalWageScale(user.getBasicMonthlyWage());
                                    infoDO.setWageRatio(user.getWageRatio());
                                    infoDO.setSeniorityWage(user.getSeniorityWage());
                                    infoDO.setMinimumWagePlaceCode(user.getMinimumWagePlaceCode());
                                    infoDO.setMinimumWagePlaceName(user.getMinimumWagePlaceName());
                                    infoDO.setInsuredPlaceCode(user.getInsuredPlaceCode());
                                    infoDO.setInsuredPlaceName(user.getInsuredPlaceName());
                                    infoDO.setAdmissionDate(user.getJobDate());
                                    infoDO.setPromoterType(user.getPromoterType());
                                }
                                infoDO.setBusinessRegionCode(aDo.getBusinessRegionCode());
                                infoDO.setBusinessRegionName(aDo.getBusinessRegionName());
                                infoDO.setBusinessSegmentCode(aDo.getBusinessSegmentCode());
                                infoDO.setBusinessSegmentName(aDo.getBusinessSegmentName());
                                infoDO.setCategoryCode(storeCategoryCodeDO.getCategoryCode());
                                infoDO.setCategoryName(storeCategoryCodeDO.getCategoryName());
                                infoDO.setStoreId(aDo.getStoreId());
                                infoDO.setStoreName(aDo.getActualStoreName());
                                infoDO.setStoreLevel(aDo.getStoreLevel());
                                infoDO.setPhysicalStoreNum(aDo.getPhysicalStoreNum());
                                infoDO.setDimissionStatus("0");
                                infoDO.setCreateUserId(AIEnum.AI_USERID.getCode());
                                infoDO.setCreateTime(new Date());
                                insertDOList.add(infoDO);
                            }
                        }
                    }
                    songXiaMapper.batchInsertPromoterStoreInfo(insertDOList);
                }
            }
        }
    }

    @Override
    public UserDeptDTO getUserDeptByUserId(String eid, CurrentUser user) {
        UserDeptDTO userDeptByUserId = null;
        if(AIEnum.AI_USERID.getCode().equals(user.getUserId())){
            userDeptByUserId = AIUserTool.getAiUserDept();
        }else{
            userDeptByUserId = enterpriseUserDepartmentMapper.getUserDeptByUserId(eid, user.getUserId());
        }
//        SysRoleDO sysRoleDoByUserId = sysRoleMapper.getHighestPrioritySysRoleDoByUserId(eid, user.getUserId());
        //2021-7-1 获得用户最高优先级角色从查库改为查缓存
        SysRoleDO sysRoleDoByUserId = user.getSysRoleDO();
        RegionStoreListResp resp = regionService.regionStoreList(eid, null, user, Boolean.FALSE, Boolean.FALSE);
        if (CollectionUtils.isNotEmpty(resp.getRegionList())) {
            List<RegionDO> regionDo = resp.getRegionList().stream().map(data -> data.getRegion()).collect(Collectors.toList());
            userDeptByUserId.setDefaultRegion(regionDo);
        }
        if (CollectionUtils.isNotEmpty(resp.getStoreList())) {
            userDeptByUserId.setDefaultStore(resp.getStoreList().get(0).getStore());
            if (CollectionUtils.isEmpty(userDeptByUserId.getDefaultRegion())) {
                List<String> storeIds = resp.getStoreList().stream().filter(data -> data.getStore() != null).map(data -> data.getStore().getStoreId()).collect(Collectors.toList());
                List<RegionDO> regionDo = regionService.listRegionByStoreIds(eid, storeIds);
                userDeptByUserId.setDefaultRegion(regionDo);
            }
        } else if (userDeptByUserId.getDefaultRegion() != null) {
            List<String> regionIdList = userDeptByUserId.getDefaultRegion()
                    .stream()
                    .map(RegionDO::getRegionId)
                    .collect(Collectors.toList());
            List<RegionPathDTO> regionPathByList = regionService.getRegionPathByList(eid, regionIdList);
            List<String> fullRegionPathList = ListUtils.emptyIfNull(regionPathByList)
                    .stream()
                    .map(RegionPathDTO::getRegionPath)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(fullRegionPathList)) {
                StoreDO storeDO = storeMapper.getDefaultStoreDOByRegionPath(eid, fullRegionPathList);
                userDeptByUserId.setDefaultStore(storeDO);
            }
        }
        userDeptByUserId.setIsAdmin(sysRoleDoByUserId != null && StringUtils.equals(sysRoleDoByUserId.getRoleEnum(), Role.MASTER.getRoleEnum()));
        userDeptByUserId.setPositionType(sysRoleDoByUserId == null ? null : sysRoleDoByUserId.getPositionType());
        userDeptByUserId.setRoleAuth(sysRoleDoByUserId == null ? null : sysRoleDoByUserId.getRoleAuth());
        Long roleId = Optional.ofNullable(sysRoleDoByUserId).map(SysRoleDO::getId).orElse(null);
        String roleName = Optional.ofNullable(sysRoleDoByUserId).map(SysRoleDO::getRoleName).orElse("");
        userDeptByUserId.setRoleId(roleId);
        userDeptByUserId.setRoleName(roleName);
        return userDeptByUserId;
    }

    @Override
    public void updateUserMainAdmin(String eid, String userId) {
        enterpriseUserMapper.updateUserMainAdmin(eid, userId);
    }

    @Override
    public SelectUserInfoDTO selectUserInfo(String eid, String userId) {
        if (StrUtil.isBlank(userId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "请选择需要查询的用户");
        }
        DataSourceHelper.changeToMy();
        EnterpriseUserDO user = enterpriseUserDao.selectByUserId(eid, userId);
        if (user == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "找不到该用户");
        }
        // 用户基本信息
        SelectUserInfoDTO userInfo = SelectUserInfoDTO
                .builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .jobnumber(user.getJobnumber())
                .avatar(StrUtil.isBlank(user.getFaceUrl()) ? user.getAvatar() : user.getFaceUrl())
                .build();
        // 用户部门信息
        List<SelectUserDeptDTO> userDeptInfo = sysDepartmentMapper.selectUserDeptInfo(eid, userId);
        //过滤为null的情况 update by xuanfeng
        List<SelectUserDeptDTO> filterDeptInfo = ListUtils.emptyIfNull(userDeptInfo)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // 用户职位信息
        SelectUserRoleDTO userRoleInfo = sysRoleMapper.selectUserRoleInfo(eid, userId);
        userInfo.setPositionInfo(userRoleInfo).setDeptInfo(filterDeptInfo);
        //填充用户的区域，门店信息
        userInfo = selectionComponentService.supplementClickUserQueryResult(eid, userInfo);
        return userInfo;
    }

    @Override
    public List<EnterpriseUserDO> getMainAdmin(String eid) {
        return enterpriseUserMapper.getMainAdmin(eid);
    }

    @Override
    public Integer countUserAll(String eid) {
        return enterpriseUserMapper.countUserAll(eid);
    }

    @Override
    public EnterpriseUserDO selectByUnionid(String enterpriseId, String unionid) {
        return enterpriseUserMapper.selectByUnionid(enterpriseId, unionid);
    }

    @Override
    public List<EnterpriseUserDO> selectByUnionids(String enterpriseId, List<String> unionids) {
        return enterpriseUserMapper.selectByUnionids(enterpriseId, unionids);
    }

    @Override
    public ResponseResult improveUserInfo(ImproveUserInfoDTO param, CurrentUser currentUser) {
        String smsCodeKey = SmsCodeTypeEnum.IMPROVE_INFO + ":" + param.getMobile();
        String smsCode = param.getSmsCode();
        String codeValue = redisUtilPool.getString(smsCodeKey);
        if (StringUtils.isBlank(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_EXPIRE);
        }
        if (!smsCode.equals(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_ERROR);
        }
        String enterpriseId = currentUser.getEnterpriseId();
        String userId = currentUser.getUserId();
        String unionid = currentUser.getUnionid();
        DataSourceHelper.changeToMy();
        if (StringUtils.isNotBlank(param.getMobile())) {
            EnterpriseUserDO enterpriseUser = enterpriseUserMapper.getEnterpriseUserByMobile(enterpriseId, param.getMobile(), unionid);
            if (Objects.nonNull(enterpriseUser)) {
                throw new ServiceException(ErrorCodeEnum.MOBILE_USED, enterpriseUser.getName());
            }
        }
        EnterpriseUserDO user = enterpriseUserMapper.selectByUnionid(enterpriseId, unionid);
        if (Objects.isNull(user)) {
            return ResponseResult.fail(ErrorCodeEnum.USER_NON_EXISTENT);
        }
        EnterpriseUserDO update = new EnterpriseUserDO();
        update.setName(param.getName());
        update.setMobile(param.getMobile());
        update.setUserId(userId);
        update.setAvatar(param.getAvatar());
        update.setUnionid(user.getUnionid());
        update.setPassword(MD5Util.md5(param.getPassword() + Constants.USER_AUTH_KEY));
        enterpriseUserDao.updateEnterpriseUser(enterpriseId, update);
        DataSourceHelper.reset();
        enterpriseUserMapper.updateConfigEnterpriseUserByUnionId(update);
        redisUtilPool.delKey(smsCodeKey);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult modifyPassword(ModifyPasswordDTO param, String unionid) {
        String smsCodeKey = SmsCodeTypeEnum.MODIFY_PWD + ":" + param.getMobile();
        String smsCode = param.getSmsCode();
        String codeValue = redisUtilPool.getString(smsCodeKey);
        if (StringUtils.isBlank(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_EXPIRE);
        }
        if (!smsCode.equals(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_ERROR);
        }
        EnterpriseUserDO update = new EnterpriseUserDO();
        update.setUnionid(unionid);
        update.setPassword(MD5Util.md5(param.getPassword() + Constants.USER_AUTH_KEY));
        DataSourceHelper.reset();
        enterpriseUserMapper.updateConfigEnterpriseUserByUnionId(update);
        redisUtilPool.delKey(smsCodeKey);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult modifyPasswordByOriginalPassword(ModifyPasswordByOldDTO param, String unionid) {
        ResponseResult verifyResult = verifyOriginalPassword(param.getOriginalPassword(), unionid);
        if (ResponseCodeEnum.SUCCESS.getCode() != verifyResult.getCode()) {
            return verifyResult;
        }
        String newPassword = MD5Util.md5(param.getNewPassword() + Constants.USER_AUTH_KEY);
        EnterpriseUserDO update = new EnterpriseUserDO();
        update.setUnionid(unionid);
        update.setPassword(newPassword);
        enterpriseUserMapper.updateConfigEnterpriseUserByUnionId(update);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult verifyOriginalPassword(String originalPassword, String unionid) {
        EnterpriseUserDO user = enterpriseUserMapper.selectConfigUserByUnionid(unionid);
        if (ObjectUtil.isNull(user)) {
            return ResponseResult.fail(ErrorCodeEnum.USER_NOT_EXIST);
        }
        String password = MD5Util.md5(originalPassword + Constants.USER_AUTH_KEY);
        if (!password.equals(user.getPassword())) {
            return ResponseResult.fail(ErrorCodeEnum.ORIGINAL_PASSWORD_ERROR);
        }
        return ResponseResult.success();
    }

    @Override
    public ResponseResult addUser(UserAddDTO param, String enterpriseId, String dbName, CurrentUser currentUser) {
        //当前企业查找是否存在用户手机号相同的账号
        DataSourceHelper.changeToSpecificDataSource(dbName);
        if (StringUtils.isNotBlank(param.getMobile())) {
            EnterpriseUserDO enterpriseUser = enterpriseUserMapper.getEnterpriseUserByMobile(enterpriseId, param.getMobile(), null);
            if (Objects.nonNull(enterpriseUser)) {
                throw new ServiceException(ErrorCodeEnum.MOBILE_USED, enterpriseUser.getName());
            }
        }
        if (StringUtils.isNotBlank(param.getThirdOaUniqueFlag())) {
            EnterpriseUserDO exsitThirdOaUniqueFlag = enterpriseUserMapper.getEnterpriseUserByThirdOaUniqueFlag(enterpriseId, param.getThirdOaUniqueFlag(), null);
            if (Objects.nonNull(exsitThirdOaUniqueFlag)) {
                throw new ServiceException(ErrorCodeEnum.THIRDOAFLAG_USED, exsitThirdOaUniqueFlag.getName());
            }
        }
        if (Objects.isNull(param.getUserStatus())) {
            param.setUserStatus(UserStatusEnum.NORMAL.getCode());
        }
        if (Pattern.matches("[\u4E00-\u9FA5]+", param.getJobnumber())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "工号不能存在中文");
        }
        if (StringUtils.isNotBlank(param.getJobnumber())) {
            int num = enterpriseUserMapper.getNumByJobnumber(enterpriseId, param.getJobnumber(), null);
            if (num > 0) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "工号已经存在");
            }
        }
        String userId = "";
        String unionId = "";
        boolean isExternalUser = Objects.nonNull(param.getUserType()) && UserTypeEnum.EXTERNAL_USER.getCode() == param.getUserType();
        if (isExternalUser) {
            userId = "EXTERNAL" + UUIDUtils.get32UUID();
            unionId = "EXTERNAL" + UUIDUtils.get32UUID();
            if (CollectionUtils.isNotEmpty(param.getRegionIds())) {
                List<RegionDO> regionList = regionService.getRegionDOsByRegionIds(enterpriseId, param.getRegionIds());
                if (CollectionUtils.isEmpty(regionList)) {
                    throw new ServiceException(ErrorCodeEnum.REGION_IS_NULL);
                }
                List<RegionDO> externalRegionList = regionList.stream().filter(o -> o.getIsExternalNode()).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(externalRegionList)) {
                    throw new ServiceException(ErrorCodeEnum.EXTERNAL_USER_REGION_IS_ERROR);
                }
            }
        }
        //查询平台库用户信息
        DataSourceHelper.reset();
        EnterpriseSettingVO enterpriseSettingVO = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        Boolean enableDingSync = Objects.equals(enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId).getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) ||
                Objects.equals(enterpriseSettingVO.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD);
        List<EnterpriseUserDO> platformUser = enterpriseUserMapper.getPlatformUserByMobile(param.getMobile());
        boolean isAddUser = false;
        EnterpriseUserDO insert = new EnterpriseUserDO();
        String id = UUIDUtils.get32UUID();
        insert.setId(id);
        insert.setName(param.getName());
        insert.setMobile(param.getMobile());
        insert.setEmail(param.getEmail());
        insert.setAvatar(param.getAvatar());
        insert.setJobnumber(param.getJobnumber());
        insert.setRemark(param.getRemark());
        insert.setUserStatus(param.getUserStatus());
        if (StringUtils.isNotBlank(param.getThirdOaUniqueFlag())) {
            insert.setThirdOaUniqueFlag(param.getThirdOaUniqueFlag());
        }
        insert.setActive(true);
        if (CollectionUtils.isEmpty(platformUser)) {
            isAddUser = true;
        } else {
            List<EnterpriseUserDO> userList = platformUser.stream().filter(o -> o.getActive() && AppTypeEnum.APP.getValue().equals(o.getAppType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(userList)) {
                EnterpriseUserDO enterpriseUserDO = userList.get(0);
                userId = enterpriseUserDO.getUserId();
                unionId = enterpriseUserDO.getUnionid();
                id = enterpriseUserDO.getId();
            } else {
                isAddUser = true;
            }
        }
        if (isAddUser && !isExternalUser) {
            userId = AppTypeEnum.APP.getValue() + UUIDUtils.get32UUID();
            unionId = AppTypeEnum.APP.getValue() + UUIDUtils.get32UUID();
            insert.setSubordinateRange(enterpriseSettingVO.getManageUser());
        }
        insert.setUserId(userId);
        insert.setUnionid(unionId);
        insert.setAppType(AppTypeEnum.APP.getValue());
        insert.setDepartments("[1]");
        insert.setUserType(Objects.isNull(param.getUserType()) ? UserTypeEnum.INTERNAL_USER.getCode() : param.getUserType());
        insert.setCreateUserId(currentUser.getUserId());
        if (isAddUser) {
            List<EnterpriseUserDO> userAdds = new ArrayList<>();
            userAdds.add(insert);
            enterpriseUserDao.batchInsertPlatformUsers(userAdds);
        }
        enterpriseUserMappingService.insertEnterpriseUserMapping(enterpriseId, id, unionId, param.getUserStatus());
        //新增企业用户信息
        DataSourceHelper.changeToMy();
        handleUserRegionMapping(enterpriseId, param.getRegionIds(), userId, currentUser, insert);
        //处理直属上级
        handleDirectSuperior(enterpriseId, userId, param.getDirectSuperiorId(), currentUser);
        handleSubordinateMappingNew(enterpriseId, userId, param.getSubordinateUserRange(), param.getSourceList(), param.getMySubordinates(), currentUser, insert);
        insertEnterpriseUser(enterpriseId, insert);
        enterpriseUserRoleMapper.save(enterpriseId, new EnterpriseUserRole(Role.EMPLOYEE.getId(), userId, RoleSyncTypeEnum.CREATE.getCode()));
        enterpriseUserDepartmentMapper.save(enterpriseId, new EnterpriseUserDepartmentDO(userId, "1", Boolean.FALSE));
        //更新用户角色
        if (StringUtils.isNotEmpty(param.getRoleId())) {
            updateUserRole(enterpriseId, param.getRoleId(), userId, enableDingSync);
        }
        //更新权限门店、区域的信息
        updateUserAuth(enterpriseId, param.getAuthRegionStoreList(), userId, enableDingSync);
        // 更新用户分组
        if (CollectionUtils.isNotEmpty(param.getGroupIdList())) {
            enterpriseUserGroupService.updateUserGroup(enterpriseId, param.getGroupIdList(), userId, currentUser);
        }
        // 身份证号不为空
        if (StringUtil.isNotEmpty(param.getThirdOaUniqueFlag())) {
            SyncRequest syncRequest = SyncRequest.builder()
                    .employeeCode(param.getJobnumber())
                    .enterpriseId(enterpriseId)
                    .userId(userId)
                    .thirdOaUniqueFlag(param.getThirdOaUniqueFlag())
                    .build();
            simpleMessageService.send(JSONObject.toJSONString(syncRequest), RocketMqTagEnum.THIRD_OA_SYNC_SINGLE_QUEUE);
        }
        //用户数据新增，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(userId), ChangeDataOperation.ADD.getCode(), ChangeDataType.USER.getCode());
        return ResponseResult.success(true);
    }

    @Override
    public ResponseResult addUser(String enterpriseId, OpenApiAddUserDTO param) {
        String userId = param.getUserId();
        EnterpriseUserDO userDetail = enterpriseUserDao.selectByUserId(enterpriseId, userId);
        String id, unionId;
        if (Objects.nonNull(userDetail)) {
            id = userDetail.getId();
            unionId = userDetail.getUnionid();
        } else {
            id = UUIDUtils.get32UUID();
            unionId = UUIDUtils.get32UUID();
        }
        EnterpriseUserDO updateUser = new EnterpriseUserDO();
        updateUser.setId(id);
        updateUser.setUserId(param.getUserId());
        updateUser.setName(param.getUsername());
        updateUser.setMobile(param.getMobile());
        updateUser.setEmail(param.getEmail());
        updateUser.setAvatar(param.getAvatar());
        updateUser.setJobnumber(param.getJobnumber());
        updateUser.setIsAdmin(Objects.nonNull(param.getIsAdmin()) ? param.getIsAdmin() : false);
        updateUser.setThirdOaUniqueFlag(param.getThirdOaUniqueFlag());
        Integer userStatus = UserStatusEnum.NORMAL.getCode().equals(param.getUserStatus()) ? UserStatusEnum.NORMAL.getCode() : UserStatusEnum.FREEZE.getCode();
        updateUser.setUserStatus(userStatus);
        updateUser.setUserType(UserTypeEnum.INTERNAL_USER.getCode());
        updateUser.setUnionid(unionId);
        updateUser.setActive(true);
        if (Pattern.matches("[\u4E00-\u9FA5]+", param.getJobnumber())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "工号不能存在中文");
        }
        //校验手机号  第三方唯一标识 是否与其他用户冲突
        if (StringUtils.isNotBlank(param.getMobile())) {
            EnterpriseUserDO enterpriseUser = enterpriseUserMapper.getUserByMobile(enterpriseId, param.getMobile(), userId);
            if (Objects.nonNull(enterpriseUser)) {
                throw new ServiceException(ErrorCodeEnum.MOBILE_USED, enterpriseUser.getName());
            }
        }
        if (StringUtils.isNotBlank(param.getThirdOaUniqueFlag())) {
            EnterpriseUserDO thirdOaEnterpriseUser = enterpriseUserMapper.getUserByThirdOaUniqueFlag(enterpriseId, param.getThirdOaUniqueFlag(), userId);
            if (Objects.nonNull(thirdOaEnterpriseUser)) {
                throw new ServiceException(ErrorCodeEnum.MOBILE_USED, thirdOaEnterpriseUser.getName());
            }
        }
        if (CollectionUtils.isNotEmpty(param.getThirdDeptIdList())) {
            String regionPathString = null;
            List<RegionDO> regionList = regionMapper.getRegionIdByThirdDeptIds(enterpriseId, param.getThirdDeptIdList());
            List<String> regionIds = ListUtils.emptyIfNull(regionList).stream().map(RegionDO::getRegionId).distinct().collect(Collectors.toList());
            regionPathString = ListUtils.emptyIfNull(regionList).stream().map(e -> e.getFullRegionPath()).collect(Collectors.joining(Constants.COMMA));
            if (CollectionUtils.isEmpty(regionIds)) {
                regionIds = Arrays.asList(String.valueOf(FixedRegionEnum.DEFAULT.getId()));
                regionPathString = FixedRegionEnum.DEFAULT.getFullRegionPath();
            }
            updateUser.setUserRegionIds(Constants.SQUAREBRACKETSLEFT + regionPathString + Constants.SQUAREBRACKETSRIGHT);
            userRegionMappingDAO.batchAdd(enterpriseId, userId, regionIds);
        }
        List<Long> roleIds = null;
        if (CollectionUtils.isNotEmpty(param.getRoleNameList())) {
            List<SysRoleDO> roleList = sysRoleMapper.getRoleByRoleNames(enterpriseId, param.getRoleNameList(), RoleSourceEnum.CREATE.getCode());
            roleIds = ListUtils.emptyIfNull(roleList).stream().map(SysRoleDO::getId).distinct().collect(Collectors.toList());
            updateUser.setRoles(JSONObject.toJSONString(roleIds));
        }
        updateUserRoleAndDeleteNotExistRole(enterpriseId, roleIds, userId, RoleSyncTypeEnum.SYNC);
        if (CollectionUtils.isNotEmpty(param.getAuthDeptIdList())) {
            List<RegionDO> regionList = regionMapper.getRegionIdByThirdDeptIds(enterpriseId, param.getAuthDeptIdList());
            List<String> authRegionIds = regionList.stream().map(RegionDO::getRegionId).collect(Collectors.toList());
            userAuthMappingService.changeUserRegionAuth(enterpriseId, userId, authRegionIds);
        }
        if(Objects.nonNull(param.getUserStatus()) && UserStatusEnum.FREEZE.getCode().equals(param.getUserStatus())){
            updateUser.setMobile("");
        }
        enterpriseUserMapper.batchInsertUsers(enterpriseId, Arrays.asList(updateUser));
        //查询平台库用户信息
        DataSourceHelper.reset();
        List<EnterpriseUserDO> platformUserList = enterpriseUserMapper.getPlatformUserByMobile(param.getMobile());
        String platformId = null;
        if (CollectionUtils.isNotEmpty(platformUserList)) {
            List<EnterpriseUserDO> enterpriseUser = platformUserList.stream().filter(o -> o.getUserId().equals(userId)).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(enterpriseUser)){
                EnterpriseUserDO platformUser = enterpriseUser.get(0);
                platformId = platformUser.getId();
            }
        }
        if(StringUtils.isBlank(platformId)){
            platformId = id;
        }
        enterpriseUserDao.batchInsertPlatformUsers(Arrays.asList(updateUser));
        enterpriseUserMappingService.insertEnterpriseUserMapping(enterpriseId, platformId, unionId, UserStatusEnum.NORMAL.getCode());
        return ResponseResult.success(true);
    }

    @Override
    public ResponseResult deleteUser(String enterpriseId, List<String> userIds, String operator) {
        if (CollectionUtils.isEmpty(userIds)) {
            return ResponseResult.success();
        }
        // 1.更新用户信息
        List<EnterpriseUserDO> enterpriseUserList = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
        if (CollectionUtils.isEmpty(enterpriseUserList)) {
            return ResponseResult.success();
        }
        List<String> deleteUserIds = enterpriseUserList.stream().map(EnterpriseUserDO::getUserId).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(enterpriseUserList)) {
            log.info("没有找到对应的用户");
            return ResponseResult.success();
        }
        enterpriseUserMapper.batchDeleteUserIds(enterpriseId, deleteUserIds);
        // 2.删除用户角色映射关系
        //获得用户在cool中的角色列表
        List<Long> userRoleIds = enterpriseUserRoleMapper.selectIdsByUserIds(enterpriseId, userIds);
        // 3.删除该用户的部门信息
        List<Integer> userDepartmentIds = enterpriseUserDepartmentMapper.getIdsByUserIds(enterpriseId, userIds);
        //删除用户和区域的关联关系
        userRegionMappingDAO.deletedByUserIds(enterpriseId, userIds);
        //删除用户的下属
        subordinateMappingDAO.deletedByUserIds(enterpriseId, userIds);
        // 4.删除该用户的可见范围映射信息
        List<Long> userAuthIds = userAuthMappingMapper.selectIdsByUserIds(enterpriseId, userIds);
        if (userRoleIds != null) {
            Lists.partition(userRoleIds, Constants.BATCH_INSERT_COUNT).forEach(deleteUserRoleIds -> {
                enterpriseUserRoleMapper.deleteBatchByPrimaryKey(enterpriseId, deleteUserRoleIds);
            });
        }
        if (userDepartmentIds != null) {
            Lists.partition(userDepartmentIds, Constants.BATCH_INSERT_COUNT).forEach(userDeptDeleteList -> {
                enterpriseUserDepartmentMapper.deleteByIdList(enterpriseId, userDeptDeleteList);
            });
        }
        if (userAuthIds != null) {
            Lists.partition(userAuthIds, Constants.BATCH_INSERT_COUNT).forEach(deleteUserAuths -> {
                userAuthMappingMapper.deleteAuthMappingByIds(enterpriseId, deleteUserAuths);
            });
        }
        DataSourceHelper.reset();
        List<String> deleteIds = new ArrayList<>();
        for (EnterpriseUserDO enterpriseUser : enterpriseUserList) {
            EnterpriseUserDO platformUser = enterpriseUserMapper.getPlatformUserByUnionid(enterpriseUser.getUnionid());
            if (Objects.nonNull(platformUser)) {
                List<EnterpriseUserMappingDO> userAllEnterprise = enterpriseUserMappingService.getUserAllEnterpriseIdsByUserId(platformUser.getId());
                //只有单个企业的时候才删用户
                if (CollectionUtils.isEmpty(userAllEnterprise) || userAllEnterprise.size() == Constants.ONE) {
                    //设置用户为未激活
                    enterpriseUserDao.batchDeleteUserIdsConfig(Arrays.asList(enterpriseUser.getUserId()));
                }
                deleteIds.add(platformUser.getId());
            }
        }
        //删除用户企业映射信息
        enterpriseUserMappingService.deleteByUserIds(deleteIds, enterpriseId);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult<Boolean> batchUpdateUserStatus(BatchUserStatusDTO param, String enterpriseId, String dbName) {
        if (CollectionUtils.isEmpty(param.getUnionids())) {
            return ResponseResult.fail(ErrorCodeEnum.PARAM_MISSING);
        }
        //更新企业库用户状态
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<String> userIds = enterpriseUserDao.getUserIdsByUnionIds(enterpriseId, param.getUnionids());
        if (CollectionUtils.isEmpty(userIds)) {
            return ResponseResult.fail(ErrorCodeEnum.USER_NULL);
        }
        //判断是否存在管理员
        List<String> adminUserIds = enterpriseUserRoleMapper.selectUserIdByRoleId(enterpriseId, userIds, Role.MASTER.getId());
        if (CollectionUtils.isNotEmpty(adminUserIds)) {
            return ResponseResult.fail(ErrorCodeEnum.ADMIN_ACCOUNT_NO_OPERATE);
        }
        enterpriseUserMapper.batchUpdateUserStatusByUnionid(enterpriseId, param.getUnionids(), param.getUserStatus());
        //更新平台库enterprise_user_mapping的用户状态
        DataSourceHelper.reset();
        enterpriseUserMappingService.updateEnterpriseUserStatus(param.getUnionids(), enterpriseId, param.getUserStatus());
        return ResponseResult.success(true);
    }

    @Override
    public boolean getUserIsNeedImproveUserInfo(String unionid, String mobile, String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSetting = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
        if (Objects.nonNull(enterpriseSetting) && enterpriseSetting.isMultiLogin()) {
            EnterpriseUserDO enterpriseUser = enterpriseUserMapper.selectConfigUserByUnionid(unionid);
            if (Objects.isNull(enterpriseUser)) {
                throw new ServiceException(ErrorCodeEnum.USER_INFO_ERROR);
            }
            if (AIEnum.AI_UUID.getCode().equals(enterpriseUser.getUnionid())) {
                return false;
            }
            if (StringUtils.isAnyBlank(mobile, enterpriseUser.getMobile(), enterpriseUser.getPassword())) {
                return true;
            }
            if (!mobile.equals(enterpriseUser.getMobile())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ResponseResult forgetPassword(ModifyPasswordDTO param) {
        String smsCodeKey = SmsCodeTypeEnum.FORGOT_PWD + ":" + param.getMobile();
        String smsCode = param.getSmsCode();
        String codeValue = redisUtilPool.getString(smsCodeKey);
        if (StringUtils.isBlank(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_EXPIRE);
        }
        if (!smsCode.equals(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_ERROR);
        }
        DataSourceHelper.reset();
        enterpriseUserMapper.modifyPasswordByMobile(param.getMobile(), MD5Util.md5(param.getPassword() + Constants.USER_AUTH_KEY));
        redisUtilPool.delKey(smsCodeKey);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult updateUserCenterInfo(UpdateUserCenterDTO param, CurrentUser currentUser) {
//        if(Objects.isNull(param.getAvatar()) && Objects.isNull(param.getEmail()) && Objects.isNull(param.getName())){
//            return ResponseResult.fail(ErrorCodeEnum.PARAM_MISSING);
//        }
        String enterpriseId = currentUser.getEnterpriseId();
        String userId = currentUser.getUserId();
        String unionid = currentUser.getUnionid();
        DataSourceHelper.changeToSpecificDataSource(currentUser.getDbName());
        EnterpriseUserDO user = enterpriseUserMapper.selectByUnionid(enterpriseId, unionid);
        if (Objects.isNull(user)) {
            return ResponseResult.fail(ErrorCodeEnum.USER_NON_EXISTENT);
        }
        if (StringUtils.isNotBlank(param.getThirdOaUniqueFlag())) {
            EnterpriseUserDO exsitThirdOaUniqueFlag = enterpriseUserMapper.getEnterpriseUserByThirdOaUniqueFlag(enterpriseId, param.getThirdOaUniqueFlag(), user.getUnionid());
            if (Objects.nonNull(exsitThirdOaUniqueFlag)) {
                throw new ServiceException(ErrorCodeEnum.THIRDOAFLAG_USED, exsitThirdOaUniqueFlag.getName());
            }
        }
        EnterpriseUserDO update = new EnterpriseUserDO();
        update.setName(param.getName());
        update.setUserId(userId);
        update.setAvatar(param.getAvatar());
        update.setEmail(param.getEmail());
        update.setUnionid(user.getUnionid());
        update.setJobnumber(param.getJobnumber());
        update.setThirdOaUniqueFlag(param.getThirdOaUniqueFlag());
        update.setFaceUrl(param.getAvatar());
        enterpriseUserDao.updateEnterpriseUser(enterpriseId, update);
        DataSourceHelper.reset();
        enterpriseUserMapper.updateConfigEnterpriseUserByUnionId(update);
        return ResponseResult.success();
    }

    @Override
    public ResponseResult modifyUserMobile(ModifyUserMobileDTO param, CurrentUser currentUser) {
        String smsCodeKey = SmsCodeTypeEnum.IMPROVE_INFO + ":" + param.getMobile();
        String smsCode = param.getSmsCode();
        String codeValue = redisUtilPool.getString(smsCodeKey);
        if (StringUtils.isBlank(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_EXPIRE);
        }
        if (!smsCode.equals(codeValue)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_ERROR);
        }
        String enterpriseId = currentUser.getEnterpriseId();
        String userId = currentUser.getUserId();
        String unionid = currentUser.getUnionid();
        DataSourceHelper.changeToSpecificDataSource(currentUser.getDbName());
        if (StringUtils.isNotBlank(param.getMobile())) {
            EnterpriseUserDO enterpriseUser = enterpriseUserMapper.getEnterpriseUserByMobile(enterpriseId, param.getMobile(), unionid);
            if (Objects.nonNull(enterpriseUser)) {
                return ResponseResult.fail(ErrorCodeEnum.MOBILE_USED, enterpriseUser.getName());
            }
        }
        EnterpriseUserDO user = enterpriseUserMapper.selectByUnionid(enterpriseId, unionid);
        if (Objects.isNull(user)) {
            return ResponseResult.fail(ErrorCodeEnum.USER_NON_EXISTENT);
        }
        EnterpriseUserDO update = new EnterpriseUserDO();
        update.setMobile(param.getMobile());
        update.setUserId(userId);
        update.setUnionid(unionid);
        enterpriseUserDao.updateEnterpriseUser(enterpriseId, update);
        DataSourceHelper.reset();
        enterpriseUserMapper.updateConfigEnterpriseUserByUnionId(update);
        //跟新当前用户缓存中的手机号
        currentUser.setMobile(param.getMobile());
        String cacheKey = RedisConstant.ACCESS_TOKEN_PREFIX + currentUser.getAccessToken();
        Long expire = redisUtilPool.getExpire(cacheKey);
        if (expire <= 0) {
            expire = 10L;
        }
        redisUtilPool.setString(cacheKey, JSON.toJSONString(currentUser), expire.intValue());
        redisUtilPool.delKey(smsCodeKey);
        return ResponseResult.success(true);
    }

    @Override
    public ResponseResult inviteRegister(InviteUserRegisterDTO param) {
        String shareKey = redisUtilPool.getString(param.getShareKey());
        if (StringUtils.isBlank(shareKey)) {
            return ResponseResult.fail(ErrorCodeEnum.SHARE_KEY_EXPIRE);
        }
        String smsCodeKey = SmsCodeTypeEnum.USER_REGISTER + ":" + param.getMobile();
        String smsCode = redisUtilPool.getString(smsCodeKey);
        if (StringUtils.isBlank(smsCode)) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_EXPIRE);
        }
        if (!smsCode.equals(param.getSmsCode())) {
            return ResponseResult.fail(ErrorCodeEnum.SMS_CODE_ERROR);
        }
        String enterpriseId = param.getEnterpriseId();
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        if (StringUtils.isNotBlank(param.getMobile())) {
            EnterpriseUserDO enterpriseUser = enterpriseUserMapper.getEnterpriseUserByMobile(enterpriseId, param.getMobile(), null);
            if (Objects.nonNull(enterpriseUser)) {
                throw new ServiceException(ErrorCodeEnum.MOBILE_USED, enterpriseUser.getName());
            }
        }
        //查询平台库用户信息
        DataSourceHelper.reset();
        List<EnterpriseUserDO> platformUser = enterpriseUserMapper.getPlatformUserByMobile(param.getMobile());
        EnterpriseSettingVO setting = enterpriseSettingService.getEnterpriseSettingVOByEid(enterpriseId);
        boolean isAddUser = false;
        String userId = "";
        String unionId = "";
        EnterpriseUserDO insert = new EnterpriseUserDO();
        String id = UUIDUtils.get32UUID();
        insert.setId(id);
        insert.setName(param.getName());
        insert.setMobile(param.getMobile());
        insert.setEmail(param.getEmail());
        insert.setUserStatus(UserStatusEnum.WAIT_AUDIT.getCode());
        insert.setActive(true);
        if (CollectionUtils.isEmpty(platformUser)) {
            isAddUser = true;
        } else {
            List<EnterpriseUserDO> userList = platformUser.stream().filter(o -> AppTypeEnum.APP.getValue().equals(o.getAppType())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(userList)) {
                EnterpriseUserDO enterpriseUserDO = userList.get(0);
                userId = enterpriseUserDO.getUserId();
                unionId = enterpriseUserDO.getUnionid();
                id = enterpriseUserDO.getId();
            } else {
                isAddUser = true;
            }
        }
        if (isAddUser) {
            userId = AppTypeEnum.APP.getValue() + UUIDUtils.get32UUID();
            unionId = AppTypeEnum.APP.getValue() + UUIDUtils.get32UUID();
            insert.setSubordinateRange(setting.getManageUser());
        }
        insert.setUserId(userId);
        insert.setUnionid(unionId);
        insert.setAppType(AppTypeEnum.APP.getValue());
        insert.setDepartments("[1]");
        insert.setPassword(MD5Util.md5(param.getPassword() + Constants.USER_AUTH_KEY));
        if (isAddUser) {
            List<EnterpriseUserDO> userAdds = new ArrayList<>();
            userAdds.add(insert);
            enterpriseUserDao.batchInsertPlatformUsers(userAdds);
        }
        enterpriseUserMappingService.insertEnterpriseUserMapping(enterpriseId, id, unionId, UserStatusEnum.WAIT_AUDIT.getCode());
        //新增企业用户信息
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        insertEnterpriseUser(enterpriseId, insert);
        enterpriseUserRoleMapper.save(enterpriseId, new EnterpriseUserRole(Role.EMPLOYEE.getId(), userId, RoleSyncTypeEnum.CREATE.getCode()));
        enterpriseUserDepartmentMapper.save(enterpriseId, new EnterpriseUserDepartmentDO(userId, "1", Boolean.FALSE));
        redisUtilPool.delKey(smsCodeKey);
        //用户数据新增，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, Arrays.asList(userId), ChangeDataOperation.ADD.getCode(), ChangeDataType.USER.getCode());
        return ResponseResult.success();
    }

    @Override
    public PageInfo<EnterpriseUserBossVO> getUserListNew(String enterpriseId, EnterpriseUserQueryDTO enterpriseUserQueryDTO) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        PageHelper.startPage(enterpriseUserQueryDTO.getPage_num(), enterpriseUserQueryDTO.getPage_size());
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserMapper
                .fuzzyUsersByUserIdsAndUserName(enterpriseId, null, enterpriseUserQueryDTO.getUserName(), UserStatusEnum.NORMAL.getCode());

        if (CollectionUtils.isEmpty(enterpriseUserDOList)) {
            return new PageInfo<>();
        }
        PageInfo pageInfo = new PageInfo(enterpriseUserDOList);
        pageInfo.setList(enterpriseUserDOList.stream().map(e -> {
            EnterpriseUserBossVO vo = new EnterpriseUserBossVO();
            vo.setId(e.getId());
            vo.setUserId(e.getUserId());
            vo.setName(e.getName());
            return vo;
        }).collect(Collectors.toList()));
        return pageInfo;
    }

    @Override
    public Boolean getIsFirstLogin(String enterpriseId, CurrentUser user, String loginWay) {
        EnterpriseDO enterpriseDO = enterpriseMapper.selectById(enterpriseId);
        Date enterpriseCreateTime = enterpriseDO.getCreateTime();
        //不影响已开通的用户
        if (enterpriseCreateTime.before(DateUtil.parse("2021-12-31 10:20:00", "yyyy-MM-dd HH:mm:ss"))) {
            return false;
        }
        try {
            Date createTimeAfterTime = DateUtils.addDays(enterpriseCreateTime, 30);
            //开通超过30天，直接返回false
            if (createTimeAfterTime.before(new Date())) {
                return false;
            }

            String key = RedisConstant.FIRST_LOGIN + enterpriseId + "_" + user.getUserId() + "_" + loginWay;

            if (redisUtilPool.getString(key) == null) {
                //并且是管理员
                if (Role.MASTER.getRoleEnum().equals(user.getSysRoleDO().getRoleEnum())) {
                    return true;
                } else {
                    log.info("getIsFirstLoginRoleInfo:enterpriseId:{},userId: {},loginWay:{},enterpriseCreateTime:{},roleEnum:{}", enterpriseId, user.getUserId(), loginWay, enterpriseCreateTime, user.getSysRoleDO().getRoleEnum());
                }
            }
        } catch (Exception e) {
            log.info("getIsFirstLogin fail:enterpriseId:{},userId: {},loginWay:{},enterpriseCreateTime:{}", enterpriseId, user.getUserId(), loginWay, enterpriseCreateTime);
        }

        return false;
    }

    @Subscribe
    public void onUserNotAuthEvent(UserNotAuthEvent userNotAuthEvent) {

        if (null == userNotAuthEvent || StringUtils.isBlank(userNotAuthEvent.getCorpId())
                || org.springframework.util.CollectionUtils.isEmpty(userNotAuthEvent.getUserIds())) {
            return;
        }
        log.info("onUserNotEvent event={}", JSON.toJSONString(userNotAuthEvent));

        String corpId = userNotAuthEvent.getCorpId();
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByCorpId(corpId, userNotAuthEvent.getAppType());
        if (null == enterpriseConfig) {
            log.info("not found enterpriseConfig, corpId={}", corpId);
            return;
        }
        String enterpriseId = enterpriseConfig.getEnterpriseId();
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());

        List<String> dingUserIds = userNotAuthEvent.getUserIds();
        List<EnterpriseUserDO> enterpriseUsers = selectUsersByUserIds(enterpriseId, dingUserIds);
        if (org.springframework.util.CollectionUtils.isEmpty(enterpriseUsers)) {
            return;
        }
        List<String> userIds = enterpriseUsers.stream().map(EnterpriseUserDO::getUserId).collect(Collectors.toList());

        deleteEnterpriseByUserIds(userIds, enterpriseId);

        DataSourceHelper.reset();
        enterpriseUserMappingService.deleteByUserIds(userIds, enterpriseId);

    }

    @Override
    public void copyUserAuth(String eid, EnterpriseUserAuthCopyDTO enterpriseUserAuthCopyDTO, CurrentUser user) {
        List<String> userIds = enterpriseUserAuthCopyDTO.getUserIds();
        if (enterpriseUserAuthCopyDTO.getIsCover()) {
            //如果是覆盖更新，直接先删除用户原有的非同步权限
            userAuthMappingMapper.deleteAuthMappingByUserIdAndSource(eid, userIds, UserAuthMappingSourceEnum.CREATE.getCode());
        }
        //获得所有复制用户的现有权限
        List<UserAuthMappingDO> userAuthList = userAuthMappingMapper.listUserAuthMappingByUserIdList(eid, userIds);
        //拼接userId和mappingID,用于判断权限是否重复
        List<String> authChecks = userAuthList.stream().map(data -> data.getUserId() + data.getMappingId()).collect(Collectors.toList());
        long now = System.currentTimeMillis();
        List<UserAuthMappingDO> allUserAuth = new ArrayList<>();
        userIds.forEach(userId -> {
            List<UserAuthMappingDO> userAuthMappingDOList = ListUtils.emptyIfNull(enterpriseUserAuthCopyDTO.getAuthRegionStoreList()).stream()
                    .map(data -> {
                        UserAuthMappingDO userAuthMappingDO = new UserAuthMappingDO();
                        userAuthMappingDO.setUserId(userId);
                        userAuthMappingDO.setMappingId(data.getId());
                        userAuthMappingDO.setType(data.getStoreFlag() ? UserAuthMappingTypeEnum.STORE.getCode()
                                : UserAuthMappingTypeEnum.REGION.getCode());
                        userAuthMappingDO.setCreateId(user.getUserId());
                        userAuthMappingDO.setCreateTime(now);
                        return userAuthMappingDO;
                    })
                    .collect(Collectors.toList());
            allUserAuth.addAll(userAuthMappingDOList);
        });
        //筛选出已存在的权限
        List<UserAuthMappingDO> finalUserAuth = allUserAuth.stream()
                .filter(auth -> !authChecks.contains(auth.getUserId() + auth.getMappingId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(finalUserAuth)) {
            userAuthMappingMapper.batchInsertUserAuthMapping(eid, finalUserAuth);
        }

    }

    /**
     * 门店区域权限取消时，同时取消对应区域门店的权限corresponding
     *
     * @param eid
     * @param userId
     * @param removeUserAuthList
     */
    private void cancelStoreRegionCorrespondAuth(String eid, String userId, List<UserAuthMappingDO> removeUserAuthList) {
        List<String> deleteStoreIdList = removeUserAuthList.stream()
                .filter(e -> UserAuthMappingTypeEnum.STORE.getCode().equals(e.getType()))
                .map(UserAuthMappingDO::getMappingId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deleteStoreIdList)) {
            List<RegionDO> deleteStoreRegionList = regionService.listRegionByStoreIds(eid, deleteStoreIdList);
            if (CollectionUtils.isNotEmpty(deleteStoreRegionList)) {
                List<String> deleteStoreRegionIdList = deleteStoreRegionList.stream().map(data -> String.valueOf(data.getId())).collect(Collectors.toList());
                userAuthMappingMapper.deleteAuthMappingByUserIdAndTypeAndMappingIds(eid, userId, UserAuthMappingTypeEnum.REGION.getCode(), deleteStoreRegionIdList);
            }
        }
        List<Long> deleteRegionIdList = removeUserAuthList.stream()
                .filter(e -> UserAuthMappingTypeEnum.REGION.getCode().equals(e.getType()))
                .map(data -> Long.valueOf(data.getMappingId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deleteRegionIdList)) {
            List<RegionDO> deleteStoreRegionList = regionService.listStoreRegionByIds(eid, deleteRegionIdList);
            if (CollectionUtils.isNotEmpty(deleteStoreRegionList)) {
                List<String> deleteRegionStoreIdList = deleteStoreRegionList.stream()
                        .filter(data -> StringUtils.isNotBlank(data.getStoreId()))
                        .map(data -> data.getStoreId()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(deleteRegionStoreIdList)) {
                    userAuthMappingMapper.deleteAuthMappingByUserIdAndTypeAndMappingIds(eid, userId, UserAuthMappingTypeEnum.STORE.getCode(), deleteRegionStoreIdList);
                }
            }
        }
    }


    @Override
    public List<EnterpriseUserDTO> listUser(String enterpriseId, String userName, String deptId,
                                            String orderBy, String orderRule,
                                            Long roleId, Integer userStatus, Integer pageNum, Integer pageSize, String jobNumber, String regionId, Boolean hasPage, String mobile, Integer userType) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToMy();
        if (hasPage) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<EnterpriseUserDTO> enterpriseUserList = new ArrayList<>();
        //企微用户 用户名在数据表中加密，需要再次请求企业端接口根据名称去筛选数据
        if (AppTypeEnum.isQwType(config.getAppType())) {
            List<String> userIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(userName)) {
                userIdList = chatService.
                        searchUserOrDeptByName(config.getDingCorpId(), config.getAppType(), userName, Constants.ONE_VALUE_STRING, pageNum, pageSize).getKey();
                if (CollectionUtils.isEmpty(userIdList)) {
                    return enterpriseUserList;
                }
            }
            if (roleId != null) {
                enterpriseUserList = enterpriseUserMapper.fuzzyUsersByDepartment(enterpriseId, deptId, roleId, orderBy, orderRule, null, jobNumber, userStatus, userIdList, regionId, mobile, userType);
            } else {
                enterpriseUserList = enterpriseUserMapper.fuzzyUsersByNotRole(enterpriseId, deptId, orderBy, orderRule, null, jobNumber, userStatus, userIdList, regionId, mobile, userType);
            }
        } else {
            if (roleId != null) {
                enterpriseUserList = enterpriseUserMapper.fuzzyUsersByDepartment(enterpriseId, deptId, roleId, orderBy, orderRule, userName, jobNumber, userStatus, null, regionId, mobile, userType);
            } else {
                enterpriseUserList = enterpriseUserMapper.fuzzyUsersByNotRole(enterpriseId, deptId, orderBy, orderRule, userName, jobNumber, userStatus, null, regionId, mobile, userType);
            }
        }
        //填充角色信息如果存在角色信息
        List<String> userIdList = initUserRole(enterpriseId, enterpriseUserList);

        if (CollectionUtils.isEmpty(enterpriseUserList)) {
            return enterpriseUserList;
        }

        //根据人员查询该人员所在部门集合
        List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.listUserRegionMappingByUserId(enterpriseId, userIdList);
        //regionids 集合
        List<String> regionIds = ListUtils.emptyIfNull(userRegionMappingDOS).stream()
                .map(UserRegionMappingDO::getRegionId).distinct().collect(Collectors.toList());

        List<RegionDO> regionDOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(regionIds)) {
            regionDOs = regionService.getRegionDOsByRegionIds(enterpriseId, regionIds);
        }
        //部门map KV key-部门id  value-部门名称
        Map<String, String> regionMap = ListUtils.emptyIfNull(regionDOs)
                .stream()
                .filter(a -> a.getId() != null && a.getName() != null)
                .collect(Collectors.toMap(data -> data.getId().toString(), RegionDO::getName, (a, b) -> a));
        //人员所在部门map key-人员id  value-部门set集合（set去重）
        Map<String, Set<String>> userRegionMap = ListUtils.emptyIfNull(userRegionMappingDOS)
                .stream().filter(a -> a.getId() != null && a.getRegionId() != null)
                .collect(Collectors.groupingBy(UserRegionMappingDO::getUserId,
                        Collectors.mapping(UserRegionMappingDO::getRegionId, Collectors.toSet())));

        //填充门店总数以及权限区域列表
        List<AuthRegionStoreDTO> authRegionStoreDTOList = visualService.authRegionStoreByUserList(enterpriseId, userIdList);
        List<AuthStoreCountDTO> authStoreCountDTOS = visualService.authStoreCount(enterpriseId, userIdList, false);
        Map<String, AuthStoreCountDTO> storeCountMap = ListUtils.emptyIfNull(authStoreCountDTOS)
                .stream()
                .collect(Collectors.toMap(AuthStoreCountDTO::getUserId, data -> data, (a, b) -> a));
        Map<String, AuthRegionStoreDTO> authRegionStoreMap = ListUtils.emptyIfNull(authRegionStoreDTOList)
                .stream()
                .collect(Collectors.toMap(AuthRegionStoreDTO::getUserId, data -> data, (a, b) -> a));

        Map<String, List<UserGroupDTO>> userGroupMap = enterpriseUserGroupService.getUserGroupMap(enterpriseId, userIdList);
        Map<String, SubordinateUserRangeDTO> subordinateUserRangeMap = fillUserSubordinateNames(enterpriseId, userIdList);

        // 获取创建人id对应名称map
        List<String> createIdList = enterpriseUserList
                .stream().map(EnterpriseUserDTO::getCreateUserId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        Map<String, String> createUserNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, createIdList);

        enterpriseUserList.stream()
                .forEach(data -> {
                    if (MapUtils.isNotEmpty(authRegionStoreMap) && authRegionStoreMap.get(data.getUserId()) != null) {
                        AuthRegionStoreDTO authRegionStoreDTO = authRegionStoreMap.get(data.getUserId());
                        data.setAuthRegionStoreList(authRegionStoreDTO.getAuthRegionStoreUserList());
                    }
                    if (MapUtils.isNotEmpty(storeCountMap) && storeCountMap.get(data.getUserId()) != null) {
                        AuthStoreCountDTO authStoreCountDTO = storeCountMap.get(data.getUserId());
                        if (authStoreCountDTO.getStoreCount() != null) {
                            data.setStoreCount(authStoreCountDTO.getStoreCount());
                        } else {
                            data.setStoreCount(0);
                        }
                    }
                    if (MapUtils.isNotEmpty(userRegionMap) && MapUtils.isNotEmpty(regionMap)) {
                        Set<String> regions = userRegionMap.get(data.getUserId());

                        String deptNames = SetUtils.emptyIfNull(regions)
                                .stream()
                                .map(dept -> regionMap.get(dept.toString()))
                                .filter(StrUtil::isNotBlank)
                                .collect(Collectors.joining(Constants.COMMA));
                        data.setDepartment(deptNames);
                    }
                    //填充用户分组
                    data.setUserGroupList(userGroupMap.get(data.getUserId()));
                    // 填充下属用户
                    if (subordinateUserRangeMap.get(data.getUserId()) != null) {
                        data.setSubordinateUserRange(subordinateUserRangeMap.get(data.getUserId()).getSubordinateUserRange());
                        data.setSourceList(subordinateUserRangeMap.get(data.getUserId()).getSourceList());
                        data.setMySubordinates(subordinateUserRangeMap.get(data.getUserId()).getMySubordinates());
                    }
                    // 设置创建人名称
                    if (StringUtils.isNotBlank(data.getCreateUserId())) {
                        data.setCreateUserName(createUserNameMap.get(data.getCreateUserId()));
                    }
                });
        // 添加至常用联系人
        if (StrUtil.isNotBlank(userName)) {
            List<String> userIds = enterpriseUserList.stream().map(EnterpriseUserDTO::getUserId).collect(Collectors.toList());
            lruService.putRecentUseUser(enterpriseId, UserHolder.getUser().getUserId(), userIds);
        }
        return enterpriseUserList;
    }

    @Override
    public ResponseResult batchUpdateUserRegion(BatchUserRegionMappingDTO param, String enterpriseId, CurrentUser currentUser) {
        //如果regionId 为null 将用户放到未分组中
        //更新企业库用户状态
        DataSourceHelper.changeToSpecificDataSource(currentUser.getDbName());
        List<String> regionIds = param.getRegionIds();
        if (CollectionUtils.isEmpty(regionIds)) {
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        //获取userId
        List<String> userIds = enterpriseUserDao.getUserIdsByUnionIds(enterpriseId, param.getUnionIds());

        List<UserRegionMappingDO> userRegionMappingList = new ArrayList<>();
        //用户批量关联区域
        for (String userId : userIds) {
            getUserRegionMapping(userId, regionIds, currentUser, userRegionMappingList);
        }
        //先删除之前的人员部门关联数据
        userRegionMappingDAO.deletedByUserIds(enterpriseId, userIds);
        //添加新的映射
        userRegionMappingDAO.batchInsert(enterpriseId, userRegionMappingList);
        //查询该人员的最新部门情况 同步到enterpriseUser 表usereginIds表中
        List<EnterpriseUserDO> userRegionPathListStr = this.getUserRegionPathListStr(enterpriseId, userIds);
        enterpriseUserDao.batchUpdateDiffUserDiffRegionIds(enterpriseId, userRegionPathListStr);
        //用户数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, userIds, ChangeDataOperation.UPDATE.getCode(), ChangeDataType.USER.getCode());
        return ResponseResult.success(true);
    }

    /**
     * 遍历生成用户映射关系
     *
     * @param userId
     * @param regionIds
     * @param currentUser
     * @param userRegionMappingList
     */
    public void getUserRegionMapping(String userId, List<String> regionIds, CurrentUser currentUser, List<UserRegionMappingDO> userRegionMappingList) {
        for (String regionId : regionIds) {
            UserRegionMappingDO userRegionMappingDO = new UserRegionMappingDO();
            userRegionMappingDO.setUserId(userId);
            userRegionMappingDO.setRegionId(regionId);
            userRegionMappingDO.setCreateId(currentUser.getUserId());
            userRegionMappingDO.setUpdateId(currentUser.getUserId());
            userRegionMappingList.add(userRegionMappingDO);
        }
    }

    /**
     * 查询用户最新的部门(至少在未分组中)
     *
     * @param enterpriseId
     * @param userIds
     * @return
     */
    @Override
    public List<EnterpriseUserDO> getUserRegionPathListStr(String enterpriseId, List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Lists.newArrayList();
        }
        List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.listUserRegionMappingByUserId(enterpriseId, userIds);
        Map<String, List<UserRegionMappingDO>> userRegionMappingMap = ListUtils.emptyIfNull(userRegionMappingDOS)
                .stream()
                .collect(Collectors.groupingBy(UserRegionMappingDO::getUserId));
        List<String> regionIds = userRegionMappingDOS.stream()
                .map(UserRegionMappingDO::getRegionId).distinct().collect(Collectors.toList());
        List<RegionDO> regionDOS = regionService.getRegionDOsByRegionIds(enterpriseId, regionIds);
        Map<String, RegionDO> regionMap = regionDOS.stream()
                .collect(Collectors.toMap(RegionDO::getRegionId, data -> data));

        List<EnterpriseUserDO> enterpriseUserList = new ArrayList<>();
        for (String userId : userIds) {
            EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
            enterpriseUserDO.setUserId(userId);
            String regionPathString = "";
            List<RegionDO> regionDOList = new ArrayList<>();
            //如果没有对应部门 默认未null  正常情况下不会出现userRegionId不为null的情况
            enterpriseUserDO.setUserRegionIds(null);
            List<UserRegionMappingDO> userRegionMappingList = userRegionMappingMap.get(userId);

            //异常情况下处理
            if (CollectionUtils.isEmpty(userRegionMappingList)) {
                log.info("getUserRegionPathListStr exception 该人员没有任何部门");
                //查询未分组
                RegionDO unclassifiedRegionDO = regionService.getUnclassifiedRegionDO(enterpriseId);
                //将人添加到未分组中
                this.addUserRegionMappingDO(enterpriseId, userId, unclassifiedRegionDO.getRegionId(), new CurrentUser());
                regionDOList.add(unclassifiedRegionDO);
            }

            ListUtils.emptyIfNull(userRegionMappingList)
                    .stream()
                    .forEach(userRegionMappingDO -> {
                        RegionDO regionDO = regionMap.get(userRegionMappingDO.getRegionId());
                        if (Objects.nonNull(regionDO)) {
                            regionDOList.add(regionDO);
                        }
                    });

            regionPathString = regionDOList.stream()
                    .map(e -> e.getFullRegionPath()).collect(Collectors.joining(Constants.COMMA));
            if (StringUtils.isNotBlank(regionPathString)) {
                enterpriseUserDO.setUserRegionIds(Constants.SQUAREBRACKETSLEFT + regionPathString + Constants.SQUAREBRACKETSRIGHT);
            }
            enterpriseUserList.add(enterpriseUserDO);
        }
        return enterpriseUserList;
    }

    @Override
    public void updateUserRegionPathList(String enterpriseId, List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        //查询该人员的最新部门情况 同步到enterpriseUser 表usereginIds表中
        List<EnterpriseUserDO> userRegionPathListStr = this.getUserRegionPathListStr(enterpriseId, userIds);
        enterpriseUserDao.batchUpdateDiffUserDiffRegionIds(enterpriseId, userRegionPathListStr);
    }

    @Override
    public List<EnterpriseUserDTO> getUserByUserIds(String enterpriseId, List<String> userIds) {
        List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(enterpriseId, userIds);
        List<EnterpriseUserDTO> userDTOList = userDOList.stream().map(userDO -> {
            EnterpriseUserDTO userDTO = new EnterpriseUserDTO();
            userDTO.setId(userDO.getId());
            userDTO.setUserId(userDO.getUserId());
            userDTO.setName(userDO.getName());
            userDTO.setJobnumber(userDO.getJobnumber());
            userDTO.setDepartments(userDO.getDepartments());
            userDTO.setUnionid(userDO.getUnionid());
            userDTO.setCreateTime(userDO.getCreateTime());
            userDTO.setMobile(userDO.getMobile());
            userDTO.setFaceUrl(userDO.getFaceUrl());
            userDTO.setActive(userDO.getActive());
            userDTO.setRoles(userDO.getRoles());
            userDTO.setEmail(userDO.getEmail());
            userDTO.setRemark(userDO.getRemark());
            userDTO.setPosition(userDO.getPosition());
            userDTO.setAvatar(userDO.getAvatar());
            userDTO.setIsAdmin(userDO.getIsAdmin());
            userDTO.setUserStatus(userDO.getUserStatus());
            userDTO.setThirdOaUniqueFlag(userDO.getThirdOaUniqueFlag());
            return userDTO;
        }).collect(Collectors.toList());
        return this.fetchRoleAndDeptData(enterpriseId, userDTOList);
    }

    @Override
    public Map<String, String> getUserNameMap(String enterpriseId, List<String> userIds) {
        return enterpriseUserDao.getUserNameMap(enterpriseId, userIds);
    }

    @Override
    public OpenUserVO getOpenUserInfo(String enterpriseId, OpenApiUserDTO openApiUserDTO) {
        if (StringUtils.isBlank(openApiUserDTO.getUserId()) && StringUtils.isBlank(openApiUserDTO.getThirdOaUniqueFlag())) {
            throw new ServiceException(ErrorCodeEnum.REQUIRED_PARAM_MISSING);
        }
        EnterpriseUserDO userDO;
        if (StringUtils.isNotBlank(openApiUserDTO.getUserId())) {
            userDO = enterpriseUserDao.selectByUserId(enterpriseId, openApiUserDTO.getUserId());
        } else {
            userDO = enterpriseUserDao.selectByThirdOaUniqueFlag(enterpriseId, openApiUserDTO.getThirdOaUniqueFlag());
        }
        if (userDO == null) {
            throw new ServiceException(ErrorCodeEnum.USER_NON_EXISTENT);
        }
        OpenUserVO userVO = new OpenUserVO();
        userVO.setUserId(userDO.getUserId());
        userVO.setName(userDO.getName());
        userVO.setMobile(userDO.getMobile());
        userVO.setThirdOaUniqueFlag(userDO.getThirdOaUniqueFlag());
        return userVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUserRole(String enterpriseId, OpenApiUserDTO openApiUserDTO) {
        if (StringUtils.isBlank(openApiUserDTO.getUserId())) {
            throw new ServiceException(ErrorCodeEnum.REQUIRED_PARAM_MISSING);
        }
        List<SysRoleDO> roleDOList = sysRoleMapper.listRoleByUserId(enterpriseId, openApiUserDTO.getUserId());
        Map<String, Long> roleNameMap = ListUtils.emptyIfNull(roleDOList).stream().collect(Collectors.toMap(SysRoleDO::getRoleName, SysRoleDO::getId));
        if (CollectionUtils.isNotEmpty(openApiUserDTO.getRoleList())) {
            openApiUserDTO.getRoleList().forEach(sysRoleName -> {
                String roleId = roleNameMap.get(sysRoleName) == null ? null : String.valueOf(roleNameMap.get(sysRoleName));
                //判断系统中是否有该角色，没有，添加角色
                if (StringUtils.isBlank(roleId)) {
                    //判断系统中是否有该角色，没有，添加角色
                    List<SysRoleDO> titleList = sysRoleMapper.selectByRoleNameAndSource(enterpriseId, sysRoleName, PositionSourceEnum.CREATE.getValue());
                    if (CollectionUtils.isEmpty(titleList)) {
                        //库中无该职位信息，添加
                        SysRoleDO sysRoleDO = new SysRoleDO();
                        sysRoleDO.setId(System.currentTimeMillis() + new Random().nextInt(1000))
                                .setRoleName(sysRoleName)
                                .setRoleAuth(AuthRoleEnum.PERSONAL.getCode())
                                .setSource(PositionSourceEnum.CREATE.getValue())
                                .setPositionType(CoolPositionTypeEnum.STORE_OUTSIDE.getCode())
                                .setPriority(sysRoleService.getNormalRoleMaxPriority(enterpriseId) + 10)
                                .setCreateTime(new Date())
                                .setCreateUser(AIEnum.AI_USERID.getCode());
                        sysRoleMapper.addSystemRole(enterpriseId, sysRoleDO);
                        roleId = String.valueOf(sysRoleDO.getId());
                        // 给新增角色初始化移动端菜单
                        try {
                            sysRoleService.initMenuWhenSyncRole(enterpriseId, sysRoleDO.getId());
                        } catch (Exception e) {
                            log.error("updateUserRole给新增角色初始化移动端菜单,企业id:{},角色Id:{}", e, sysRoleDO.getId(), e);
                        }
                    } else {
                        roleId = String.valueOf(titleList.get(0).getId());
                    }
                } else {
                    roleNameMap.remove(sysRoleName);
                }
                EnterpriseUserRole existUserRole = enterpriseUserRoleMapper.selectByUserIdAndRoleId(enterpriseId, openApiUserDTO.getUserId(), roleId);
                if (existUserRole == null) {
                    enterpriseUserRoleMapper.save(enterpriseId, new EnterpriseUserRole(roleId, openApiUserDTO.getUserId(), RoleSyncTypeEnum.SYNC.getCode()));
                }
            });
            if (!roleNameMap.isEmpty()) {
                roleNameMap.values().stream().filter(roleId -> !Role.isContainsRoleId(String.valueOf(roleId))).forEach(roleId -> {
                    enterpriseUserRoleMapper.deleteByRoleIdAndUserId(enterpriseId, String.valueOf(roleId), openApiUserDTO.getUserId());
                });
            }
        } else {
            if (CollectionUtils.isNotEmpty(roleDOList)) {
                roleDOList.stream().filter(role -> !Role.isContainsRoleId(String.valueOf(role.getId()))).forEach(role -> {
                    enterpriseUserRoleMapper.deleteByRoleIdAndUserId(enterpriseId, String.valueOf(role.getId()), openApiUserDTO.getUserId());
                });
            }
        }
    }

    @Override
    public List<String> selectByUserIdsAndStatus(String eid, List<String> userIds, Integer userStatus) {
        return enterpriseUserDao.selectByUserIdsAndStatus(eid, userIds, userStatus);
    }

    /**
     * 填充用户管辖下属
     *
     * @param enterpriseId
     * @param userIdList
     * @return
     */
    @Override
    public Map<String, SubordinateUserRangeDTO> fillUserSubordinateNames(String enterpriseId, List<String> userIdList) {
        Map<String, SubordinateUserRangeDTO> subordinateUserRangeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(userIdList)) {
            //查询该用户的下属
            List<SubordinateMappingDO> subordinateMappingDOS = subordinateMappingDAO.selectByUserIds(enterpriseId, userIdList);

            List<EnterpriseUserDO> enterpriseUserDOS = this.selectUsersByUserIds(enterpriseId, userIdList);
            Map<String, String> subordinateRangeMap = ListUtils.emptyIfNull(enterpriseUserDOS).stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getSubordinateRange));

            List<SubordinateMappingDO> defineSelectMappingLit = subordinateMappingDOS.stream().filter(x -> SubordinateSourceEnum.SELECT.getCode().equals(x.getSource())).collect(Collectors.toList());
            Map<String, List<SubordinateMappingDO>> defineSelectMappingMap = ListUtils.emptyIfNull(defineSelectMappingLit).stream()
                    .collect(Collectors.groupingBy(SubordinateMappingDO::getUserId));
            userIdList.forEach(userId -> {
                SubordinateUserRangeDTO subordinateUserRangeDTO = new SubordinateUserRangeDTO();
                subordinateUserRangeMap.put(userId, subordinateUserRangeDTO);
                String subordinateUserRange = subordinateRangeMap.get(userId);
                if (StringUtils.isNotBlank(subordinateUserRange)) {
                    subordinateUserRangeDTO.setSubordinateUserRange(subordinateUserRange);
                    if (UserSelectRangeEnum.DEFINE.getCode().equals(subordinateUserRange)) {
                        List<SubordinateMappingDO> myDefineSelectMappingLit = defineSelectMappingMap.get(userId);
                        List<String> regionIds = ListUtils.emptyIfNull(myDefineSelectMappingLit).stream().filter(x -> StringUtils.isNotBlank(x.getRegionId()))
                                .map(SubordinateMappingDO::getRegionId).collect(Collectors.toList());
                        List<RegionPathDTO> regionPathByList = new ArrayList<>();
                        Map<String, String> regionMap = new HashMap<>();
                        //查看是否是老企业
                        boolean historyEnterprise = enterpriseService.isHistoryEnterprise(enterpriseId);
                        //不是老企业
                        if (CollectionUtils.isNotEmpty(regionIds) && !historyEnterprise) {
                            regionPathByList = regionService.getRegionPathByList(enterpriseId, regionIds);
                            regionMap = regionPathByList.stream().collect(Collectors.toMap(RegionPathDTO::getRegionId, RegionPathDTO::getRegionName));
                        }
                        //老企业
                        if (CollectionUtils.isNotEmpty(regionIds) && historyEnterprise) {
                            List<Long> collect = regionIds.stream().map(s -> Long.parseLong(s.trim()))
                                    .collect(Collectors.toList());
                            List<DeptNode> depListByDepName = sysDepartmentMapper.getDepListByDepName(enterpriseId, null, collect);
                            regionMap = depListByDepName.stream().collect(Collectors.toMap(DeptNode::getId, DeptNode::getDepartmentName));
                        }

                        List<String> personalIds = ListUtils.emptyIfNull(myDefineSelectMappingLit).stream().filter(x -> StringUtils.isNotBlank(x.getPersonalId()))
                                .map(SubordinateMappingDO::getPersonalId).collect(Collectors.toList());

                        List<EnterpriseUserDO> personalList = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(personalIds)) {
                            personalList = enterpriseUserMapper.selectUsersByUserIds(enterpriseId, personalIds);
                        }
                        Map<String, String> personalMap = personalList.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName));

                        List<MySubordinatesDTO> nodeTypeList = new ArrayList<>();

                        if (CollectionUtils.isNotEmpty(myDefineSelectMappingLit)) {
                            for (SubordinateMappingDO item : myDefineSelectMappingLit) {
                                MySubordinatesDTO nodeTypeDTO = new MySubordinatesDTO();
                                if (StringUtils.isNotBlank(item.getRegionId())) {
                                    String regionName = regionMap.get(item.getRegionId());
                                    nodeTypeDTO.setNodeType("region");
                                    nodeTypeDTO.setRegionName(regionName);
                                    nodeTypeDTO.setRegionId(item.getRegionId());
                                }
                                if (StringUtils.isNotBlank(item.getPersonalId())) {
                                    String personalName = personalMap.get(item.getPersonalId());
                                    nodeTypeDTO.setNodeType("personal");
                                    nodeTypeDTO.setPersonalName(personalName);
                                    nodeTypeDTO.setPersonalId(item.getPersonalId());
                                }
                                nodeTypeList.add(nodeTypeDTO);
                            }
                        }
                        List<String> sourceList = subordinateMappingDOS.stream().filter(x -> StringUtils.isNotBlank(x.getSource()))
                                .map(SubordinateMappingDO::getSource).distinct().collect(Collectors.toList());
                        subordinateUserRangeDTO.setSourceList(sourceList);
                        if (CollectionUtils.isEmpty(sourceList)) {
                            subordinateUserRangeDTO.setSourceList(Collections.singletonList(SubordinateSourceEnum.AUTO.getCode()));
                        }
                        subordinateUserRangeDTO.setMySubordinates(nodeTypeList);
                    }
                }
            });
        }
        return subordinateUserRangeMap;
    }

    /**
     * 获取人员所属部门
     *
     * @param enterpriseId
     * @param userIdList
     * @return
     */
    @Override
    public Map<String, String> getUserRegion(String enterpriseId, List<String> userIdList) {
        Map<String, String> resultMap = Maps.newHashMap();
        boolean historyEnterprise = enterpriseService.isHistoryEnterprise(enterpriseId);
        if (historyEnterprise) {
            List<EnterpriseUserDepartmentDO> enterpriseUserDepartmentDOS = enterpriseUserDepartmentMapper.selectEnterpriseUserDepartmentByUserList(enterpriseId, userIdList);
            List<String> deptIdList = ListUtils.emptyIfNull(enterpriseUserDepartmentDOS).stream()
                    .map(EnterpriseUserDepartmentDO::getDepartmentId)
                    .distinct()
                    .collect(Collectors.toList());
            List<SysDepartmentDO> departmentList = new ArrayList<>();
            //如果deptIdList不为空  在查询
            if (CollectionUtils.isNotEmpty(deptIdList)) {
                departmentList = sysDepartmentMapper.getAllDepartmentList(enterpriseId, deptIdList);
            }
            Map<String, String> deptMap = ListUtils.emptyIfNull(departmentList)
                    .stream()
                    .filter(a -> a.getId() != null && a.getName() != null)
                    .collect(Collectors.toMap(data -> data.getId().toString(), SysDepartmentDO::getName, (a, b) -> a));
            Map<String, Set<String>> userDeptMap = ListUtils.emptyIfNull(enterpriseUserDepartmentDOS)
                    .stream()
                    .collect(Collectors.groupingBy(EnterpriseUserDepartmentDO::getUserId,
                            Collectors.mapping(EnterpriseUserDepartmentDO::getDepartmentId, Collectors.toSet())));

            userIdList.stream()
                    .forEach(userId -> {
                        if (MapUtils.isNotEmpty(userDeptMap) && MapUtils.isNotEmpty(deptMap)) {
                            Set<String> departments = userDeptMap.get(userId);

                            String deptNames = SetUtils.emptyIfNull(departments)
                                    .stream()
                                    .map(dept -> deptMap.get(dept.toString()))
                                    .filter(StrUtil::isNotBlank)
                                    .collect(Collectors.joining(","));
                            resultMap.put(userId, deptNames);
                        }
                    });
        } else {
            //根据人员查询该人员所在部门集合
            List<UserRegionMappingDO> userRegionMappingDOS = userRegionMappingDAO.listUserRegionMappingByUserId(enterpriseId, userIdList);
            //regionids 集合
            List<String> regionIds = ListUtils.emptyIfNull(userRegionMappingDOS).stream()
                    .map(UserRegionMappingDO::getRegionId).distinct().collect(Collectors.toList());

            List<RegionDO> regionDOs = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(regionIds)) {
                regionDOs = regionService.getRegionDOsByRegionIds(enterpriseId, regionIds);
            }
            //部门map KV key-部门id  value-部门名称
            Map<String, String> regionMap = ListUtils.emptyIfNull(regionDOs)
                    .stream()
                    .filter(a -> a.getId() != null && a.getName() != null)
                    .collect(Collectors.toMap(data -> data.getId().toString(), RegionDO::getName, (a, b) -> a));
            //人员所在部门map key-人员id  value-部门set集合（set去重）
            Map<String, Set<String>> userRegionMap = ListUtils.emptyIfNull(userRegionMappingDOS)
                    .stream().filter(a -> a.getId() != null && a.getRegionId() != null)
                    .collect(Collectors.groupingBy(UserRegionMappingDO::getUserId,
                            Collectors.mapping(UserRegionMappingDO::getRegionId, Collectors.toSet())));
            userIdList.stream()
                    .forEach(userId -> {
                        if (MapUtils.isNotEmpty(userRegionMap) && MapUtils.isNotEmpty(regionMap)) {
                            Set<String> regions = userRegionMap.get(userId);

                            String deptNames = SetUtils.emptyIfNull(regions)
                                    .stream()
                                    .map(dept -> regionMap.get(dept.toString()))
                                    .filter(StrUtil::isNotBlank)
                                    .collect(Collectors.joining(Constants.COMMA));
                            resultMap.put(userId, deptNames);
                        }
                    });
        }
        return resultMap;
    }

    @Override
    public void updateUseRoleAndAuth(String corpId, String enterpriseId, OpenApiUpdateUserAuthDTO param) {
        if (CollectionUtils.isEmpty(param.getUpdateUserList())) {
            log.info("参数为空");
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        List<OpenApiUpdateUserAuthDTO.UpdateUserRoleAndAuth> updateUserList = param.getUpdateUserList().stream().filter(o->StringUtils.isNotBlank(o.getUserId())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(updateUserList)){
            log.info("参数为空");
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        //奥康dingDeptId即第三方唯一id
        List<String> userIdList = updateUserList.stream().map(OpenApiUpdateUserAuthDTO.UpdateUserRoleAndAuth::getUserId).collect(Collectors.toList());
        List<String> thirdDeptIds = updateUserList.stream().flatMap(o -> o.getUserAuthList().stream().map(OpenApiUpdateUserAuthDTO.UserAuth::getDingDeptId)).distinct().collect(Collectors.toList());
        List<String> sourceRoleIds = updateUserList.stream().flatMap(o -> o.getRoleList().stream().map(OpenApiUpdateUserAuthDTO.UserRole::getSourceRoleId)).distinct().collect(Collectors.toList());
        Map<String, String> regionIdThirdDeptIdMap = regionDao.getRegionIdByThirdDeptIds(enterpriseId, thirdDeptIds);
        Map<String, Long> roleIdThirdUniqueIdMap = sysRoleDao.getRoleIdByThirdUniqueIds(enterpriseId, sourceRoleIds);
        List<SysRoleDO> addRoles = new ArrayList<>();
        if ((MapUtils.isEmpty(roleIdThirdUniqueIdMap) && CollectionUtils.isNotEmpty(sourceRoleIds)) || roleIdThirdUniqueIdMap.size() != sourceRoleIds.size()) {
            Map<String, Long> finalRoleIdThirdUniqueIdMap = roleIdThirdUniqueIdMap;
            List<String> addRoleIds = sourceRoleIds.stream().filter(o -> !finalRoleIdThirdUniqueIdMap.containsKey(o)).collect(Collectors.toList());
            Map<String, String> roleNameMap = updateUserList.stream().flatMap(o -> o.getRoleList().stream()).collect(Collectors.toMap(OpenApiUpdateUserAuthDTO.UserRole::getSourceRoleId, OpenApiUpdateUserAuthDTO.UserRole::getRoleName, (k1, k2) -> k1));
            for (String roleId : addRoleIds) {
                String roleName = roleNameMap.get(roleId);
                SysRoleDO role = new SysRoleDO();
                role.setRoleName(roleName);
                role.setThirdUniqueId(roleId);
                role.setSource(RoleSourceEnum.EHR.getCode());
                role.setCreateTime(new Date());
                addRoles.add(role);
            }
            sysRoleDao.addRole(enterpriseId, addRoles);
            roleIdThirdUniqueIdMap = sysRoleDao.getRoleIdByThirdUniqueIds(enterpriseId, sourceRoleIds);
        }
        List<EnterpriseUserRole> userRoles = new ArrayList<>();
        List<EnterpriseUserRole> removeUserRoles = new ArrayList<>();
        List<EnterpriseUserRole> allExsitUserRoles = enterpriseUserRoleMapper.listsUserRoleByUserIdListAndSource(enterpriseId, userIdList, PositionSourceEnum.EHR.getValue());
        Map<String, List<EnterpriseUserRole>> exsitUserRoleMap = ListUtils.emptyIfNull(allExsitUserRoles)
                .stream().collect(Collectors.groupingBy(EnterpriseUserRole::getUserId));
        for (OpenApiUpdateUserAuthDTO.UpdateUserRoleAndAuth updateUserRoleAndAuth : updateUserList) {
            String userId = updateUserRoleAndAuth.getUserId();
            List<OpenApiUpdateUserAuthDTO.UserAuth> userAuthList = updateUserRoleAndAuth.getUserAuthList();
            List<OpenApiUpdateUserAuthDTO.UserRole> roleList = updateUserRoleAndAuth.getRoleList();
            if (CollectionUtils.isEmpty(userAuthList) && CollectionUtils.isEmpty(roleList)) {
                log.info("参数校验失败", JSONObject.toJSONString(updateUserRoleAndAuth));
                continue;
            }
            List<String> dingDeptIds = userAuthList.stream().map(OpenApiUpdateUserAuthDTO.UserAuth::getDingDeptId).collect(Collectors.toList());
            List<String> mappingIds = new ArrayList<>();
            for (String dingDeptId : dingDeptIds) {
                String mappingId = regionIdThirdDeptIdMap.get(dingDeptId);
                if (StringUtils.isNotBlank(mappingId)) {
                    mappingIds.add(mappingId);
                }
            }
            if (CollectionUtils.isNotEmpty(mappingIds)) {
                userAuthMappingService.addUserRegionAuth(enterpriseId, userId, mappingIds);
            }
            List<String> addRoleIdList = new ArrayList<>();
            for (OpenApiUpdateUserAuthDTO.UserRole userRole : roleList) {
                Long roleId = roleIdThirdUniqueIdMap.get(userRole.getSourceRoleId());
                if (Objects.nonNull(roleId)) {
                    EnterpriseUserRole enterpriseUserRole = new EnterpriseUserRole();
                    enterpriseUserRole.setRoleId(String.valueOf(roleId));
                    enterpriseUserRole.setUserId(userId);
                    userRoles.add(enterpriseUserRole);
                    addRoleIdList.add(String.valueOf(roleId));
                }
            }
            if (CollectionUtils.isNotEmpty(exsitUserRoleMap.get(userId))) {
                List<EnterpriseUserRole> exsitUserRoles = exsitUserRoleMap.get(userId);
                List<EnterpriseUserRole> delUserRoles = exsitUserRoles.stream().filter(a -> !addRoleIdList.contains(a.getRoleId())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(delUserRoles)) {
                    removeUserRoles.addAll(delUserRoles);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(userRoles)) {
            sysRoleMapper.insertBatchUserRole(enterpriseId, userRoles);
        }
        if (CollectionUtils.isNotEmpty(removeUserRoles)) {
            //删除对应的映射关系
            List<Long> deleteUserRoleIds = removeUserRoles.stream().map(EnterpriseUserRole::getId).collect(Collectors.toList());
            enterpriseUserRoleMapper.deleteBatchByPrimaryKey(enterpriseId, deleteUserRoleIds);
        }
        try {
            enterpriseInitConfigApiService.updateUseRoleAndAuth(corpId, param);
        } catch (ApiException e) {
            log.info("调用门店通更新权限失败", e);
        }

        if (MapUtils.isNotEmpty(roleIdThirdUniqueIdMap) && CollectionUtils.isNotEmpty(roleIdThirdUniqueIdMap.values())) {
            List<String> pushRoleIdList = roleIdThirdUniqueIdMap.values().stream()
                    .map(String::valueOf).collect(Collectors.toList());
            coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, pushRoleIdList, ChangeDataOperation.ADD.getCode(), ChangeDataType.POSITION.getCode());
        }
        coolCollegeIntegrationApiService.sendDataChangeMsg(enterpriseId, userIdList, ChangeDataOperation.UPDATE.getCode(), ChangeDataType.USER.getCode());

    }

    @Override
    public ImportTaskDO externalUserInfoExport(UserInfoExportRequest param) {
        String enterpriseId = param.getEnterpriseId();
        Long roleId = param.getRoleId();
        String orderBy = param.getOrderBy();
        String orderRule = param.getOrderRule();
        String userName = param.getUserName();
        String jobNumber = param.getJobNumber();
        Integer userStatus = param.getUserStatus();
        String regionId = param.getRegionId();
        String mobile = param.getMobile();
        Integer userType = param.getUserType();
        Long totalNum = null;
        if (roleId != null) {
            totalNum = enterpriseUserMapper.fuzzyUsersByDepartmentCOUNT(enterpriseId, null, roleId, orderBy, orderRule, userName, jobNumber, userStatus, null, regionId, mobile, userType);
        } else {
            totalNum = enterpriseUserMapper.fuzzyUsersByNotRoleCOUNT(enterpriseId, null, orderBy, orderRule, userName, jobNumber, userStatus, null, regionId, mobile, userType);
        }
        if (totalNum == 0) {
            throw new ServiceException("当前无记录可导出");
        }
        // 通过枚举获取文件名称
        String fileName = MessageFormat.format(ExportTemplateEnum.EXTERNAL_USER_LIST.getName(), DateUtil.format(new Date(), com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_MINUTE));
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ExportTemplateEnum.EXTERNAL_USER_LIST.getCode());
        // 构造异步导出参数
        ExportUserRequest msg = new ExportUserRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(param);
        msg.setTotalNum(totalNum);
        msg.setImportTaskDO(importTaskDO);
        msg.setUser(param.getUser());
        msg.setDbName(param.getUser().getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXTERNAL_USER_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public void updateUseRoleAndRegionAuth(String corpId, String enterpriseId, OpenApiUpdateUserRoleAndAuthDTO param) {
        if (CollectionUtils.isEmpty(param.getUpdateUserList())) {
            log.info("参数为空");
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        List<String> userIdList = param.getUpdateUserList().stream().map(o -> o.getUserId()).collect(Collectors.toList());
        List<EnterpriseUserRole> insertUserRoleList = Lists.newArrayList();
        List<EnterpriseUserRole> removeUserRoleList = Lists.newArrayList();
        List<EnterpriseUserRole> allExsitUserRoleList = enterpriseUserRoleMapper.listsUserRoleByUserIdListAndSource(enterpriseId, userIdList, PositionSourceEnum.CREATE.getValue());
        Map<String, List<EnterpriseUserRole>> allExsitUserRoleMap = ListUtils.emptyIfNull(allExsitUserRoleList)
                .stream().collect(Collectors.groupingBy(EnterpriseUserRole::getUserId));
        List<UserAuthMappingDO> userAuthMappingDOList = userAuthMappingMapper.listByUserIdListAndSource(enterpriseId, userIdList, UserAuthMappingSourceEnum.CREATE.getCode());
        Map<String, List<UserAuthMappingDO>> userAuthMappingDOMap = ListUtils.emptyIfNull(userAuthMappingDOList)
                .stream().collect(Collectors.groupingBy(UserAuthMappingDO::getUserId));
        for (OpenApiUpdateUserRoleAndAuthDTO.UpdateUserRoleAndAuth updateUserRoleAndAuth : param.getUpdateUserList()) {
            String userId = updateUserRoleAndAuth.getUserId();
            List<String> regionIdList = updateUserRoleAndAuth.getRegionIdList();
            List<String> roleIdList = updateUserRoleAndAuth.getRoleIdList();
            if (CollectionUtils.isEmpty(regionIdList) && CollectionUtils.isEmpty(roleIdList)) {
                log.info("参数校验失败updateUseRoleAndRegionAuth={}", JSONObject.toJSONString(updateUserRoleAndAuth));
                continue;
            }
            List<UserAuthMappingDO> exsitUserAuthMappingList = userAuthMappingDOMap.get(userId);
            Map<String, UserAuthMappingDO> exsitUserAuthMappingMap = ListUtils.emptyIfNull(exsitUserAuthMappingList).stream()
                    .collect(Collectors.toMap(UserAuthMappingDO::getMappingId, data -> data, (a, b) -> a));
            List<String> insertMappingIdList = new ArrayList<>();
            regionIdList.forEach(regionId -> {
                UserAuthMappingDO check = exsitUserAuthMappingMap.get(regionId);
                if (check == null) {
                    insertMappingIdList.add(regionId);
                } else {
                    exsitUserAuthMappingList.remove(check);
                }
            });
            if (CollectionUtils.isNotEmpty(insertMappingIdList)) {
                userAuthMappingService.addUserRegionAuth(enterpriseId, userId, insertMappingIdList);
            }
            if (CollectionUtils.isNotEmpty(exsitUserAuthMappingList)) {
                List<Long> deleteIds = exsitUserAuthMappingList.stream().map(UserAuthMappingDO::getId).collect(Collectors.toList());
                userAuthMappingMapper.deleteAuthMappingByIds(enterpriseId, deleteIds);
            }
            List<EnterpriseUserRole> exsitUserRoles = allExsitUserRoleMap.get(userId);
            Map<String, EnterpriseUserRole> exsitUserRoleMap = ListUtils.emptyIfNull(exsitUserRoles).stream()
                    .collect(Collectors.toMap(EnterpriseUserRole::getRoleId, data -> data, (a, b) -> a));
            roleIdList.forEach(roleId -> {
                EnterpriseUserRole check = exsitUserRoleMap.get(roleId);
                if (check == null) {
                    EnterpriseUserRole enterpriseUserRole = new EnterpriseUserRole();
                    enterpriseUserRole.setRoleId(roleId);
                    enterpriseUserRole.setUserId(userId);
                    enterpriseUserRole.setCreateTime(new Date());
                    insertUserRoleList.add(enterpriseUserRole);
                } else {
                    exsitUserRoles.remove(check);
                }
            });
            if (CollectionUtils.isNotEmpty(exsitUserRoles)) {
                removeUserRoleList.addAll(exsitUserRoles);
            }
        }
        if (CollectionUtils.isNotEmpty(insertUserRoleList)) {
            sysRoleMapper.insertBatchUserRole(enterpriseId, insertUserRoleList);
        }
        if (CollectionUtils.isNotEmpty(removeUserRoleList)) {
            //删除对应的映射关系
            List<Long> deleteUserRoleIds = removeUserRoleList.stream().map(EnterpriseUserRole::getId).collect(Collectors.toList());
            enterpriseUserRoleMapper.deleteBatchByPrimaryKey(enterpriseId, deleteUserRoleIds);
        }

    }

    @Override
    public void updateUserRegionPath(String enteprirseId, String regionId) {
        List<String> userIds = enterpriseUserMapper.selectUserByRegionId(enteprirseId, regionId);
        updateUserRegionPathList(enteprirseId, userIds);
    }

    @Override
    public void clearTokenByUserId(String eid, String userId) {
        loginUtil.clearTokenByUserId(eid, userId);
    }

    @Override
    public void clearTokenByRoleId(String eid, Long roleId) {
        loginUtil.clearTokenByRoleId(eid, String.valueOf(roleId));
    }

    @Override
    public PageDTO<UserInfoVO> getUserPage(String enterpriseId, OpenApiUserQueryDTO param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<EnterpriseUserDO> list = enterpriseUserMapper.getList(enterpriseId, param);
        PageInfo<EnterpriseUserDO> page = new PageInfo<>(list);
        PageDTO<UserInfoVO> result = new PageDTO<>();
        result.setPageNum(page.getPageNum());
        result.setPageSize(page.getPageSize());
        result.setTotal(page.getTotal());
        List<UserInfoVO> vos = CollStreamUtil.toList(list, v -> UserInfoVO.builder()
                .userId(v.getUserId())
                .name(v.getName())
                .mobile(v.getMobile())
                .jobnumber(v.getJobnumber())
                .regionIds(convertRegionIds(v.getUserRegionIds()))
                .build());
        result.setList(vos);
        return result;
    }

    @Override
    public List<UserInfoVO> getUserList(String enterpriseId, OpenApiUserQueryDTO param) {
        if (CollectionUtils.isEmpty(param.getUserIds())) {
            return Collections.emptyList();
        }
        List<EnterpriseUserDO> list = enterpriseUserMapper.getList(enterpriseId, param);
        return CollStreamUtil.toList(list, v -> UserInfoVO.builder()
                .userId(v.getUserId())
                .name(v.getName())
                .mobile(v.getMobile())
                .jobnumber(v.getJobnumber())
                .regionIds(convertRegionIds(v.getUserRegionIds()))
                .build());
    }

    @Override
    public ResponseResult<String> initPassword(InitPasswordDTO param, CurrentUser user) {
        if (StringUtils.isBlank(param.getUserId())) {
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        EnterpriseUserDTO userDetail = enterpriseUserMapper.getUserDetail(user.getEnterpriseId(), param.getUserId());
        if(Objects.isNull(userDetail)){
            throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
        }
        DataSourceHelper.reset();
        EnterpriseUserDO platformUser = enterpriseUserMapper.getPlatformUserByUnionid(userDetail.getUnionid());
        if (Objects.nonNull(platformUser)) {
            String originalPassword = RandomUtil.randomNumbers(6);
            String encryptedPassword = MD5Util.md5(originalPassword);
            EnterpriseUserDO update = new EnterpriseUserDO();
            update.setUnionid(userDetail.getUnionid());
            update.setPassword(MD5Util.md5(encryptedPassword + Constants.USER_AUTH_KEY));
            DataSourceHelper.reset();
            enterpriseUserMapper.updateConfigEnterpriseUserByUnionId(update);
            return ResponseResult.success(originalPassword);
        }
        return null;
    }

    public static void main(String[] args) {
        String s = RandomUtil.randomNumbers(6);
        System.out.println(s);
    }
      private List<String> convertRegionIds(String userRegionIds) {
        if (StringUtils.isBlank(userRegionIds)) {
            return Collections.emptyList();
        }
        return Arrays.stream(userRegionIds.substring(1, userRegionIds.length() - 1).split(","))
                .map(v -> {
                    String[] array = v.split("/");
                    return array.length > 0 ? array[array.length - 1] : null;
                })
                .distinct()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }
}
