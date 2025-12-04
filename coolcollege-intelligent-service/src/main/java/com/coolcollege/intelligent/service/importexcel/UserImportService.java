package com.coolcollege.intelligent.service.importexcel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataType;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportUserGroupEnum;
import com.coolcollege.intelligent.common.enums.position.PositionSourceEnum;
import com.coolcollege.intelligent.common.enums.region.FixedRegionEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.enums.user.UserTypeEnum;
import com.coolcollege.intelligent.common.exception.BaseException;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.*;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.SubordinateMappingDAO;
import com.coolcollege.intelligent.dao.importexcel.ImportTaskMapper;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupDao;
import com.coolcollege.intelligent.dao.usergroup.dao.EnterpriseUserGroupMappingDao;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportConstants;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.impoetexcel.dto.*;
import com.coolcollege.intelligent.model.impoetexcel.vo.ImportDistinctVO;
import com.coolcollege.intelligent.model.qywx.dto.ImportUserDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.system.dto.RoleDTO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupDO;
import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupMappingDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.qywx.WeComService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.MobileUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/12/14 11:18
 */
@Service
@Slf4j
public class UserImportService extends ImportBaseService{
    @Resource
    private ImportTaskMapper importTaskMapper;

    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Resource
    private RegionMapper regionMapper;
    @Resource
    private StoreMapper storeMapper;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private EnterpriseUserMapper userMapper;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Autowired
    private GenerateOssFileService generateOssFileService;

    @Autowired
    private WeComService weComService;

    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    private EnterpriseUserMappingMapper enterpriseUserMappingMapper;

    @Resource
    private EnterpriseUserDepartmentMapper enterpriseUserDepartmentMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    @Lazy
    private UserImportService userImportService;

    @Autowired
    private RegionService regionService;

    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Resource
    private EnterpriseUserGroupDao enterpriseUserGroupDao;
    @Resource
    private EnterpriseUserGroupMappingDao enterpriseUserGroupMappingDao;
    @Resource
    private SubordinateMappingDAO subordinateMappingDAO;
    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;

    private static final String USER_NOT_EXIST = "用户不存在";
    private static final String NULL_USERID = "用户ID为空";
    private static final String EXIST_USERID = "用户ID重复";
    private static final String NULL_NAME = "用户名称为空";
    private static final String NULL_NUM = "企业工号为空";
    private static final String NULL_MOBILE = "手机号为空";
    private static final String NULL_USER = "企业用户不存在";
    private static final String NOT_EXIST_NAME = "用户名称不存在";
    private static final String NOT_EXIST_NUM = "企业工号不存在";
    private static final String NOT_EXIST_MOBILE = "手机号码不存在";
    private static final String EXIST_NAME = "导入的用户名重复";
    private static final String MUCH_NAME = "该用户名存在多个，请按照工号或手机号去重导入";
    private static final String MUCH_NUM = "该企业工号已在系统中存在多个，请先到系统中订正数据";
    private static final String EXIST_NUM = "企业工号已在系统中";
    private static final String EXIST_EXCEL_NUM = "企业工号重复";
    private static final String MUCH_MOBILE = "该手机号码已在系统中存在多个，请先到系统中订正数据";
    private static final String EXIST_MOBILE = "手机号码已在系统中";
    private static final String EXIST_EXCEL_MOBILE = "手机号码重复";
    private static final String NULL_POSITION = "职位不存在";
    private static final String NULL_REGION = "[%s]区域不存在";
    private static final String NULL_STORE = "[%s]门店不存在";
    private static final String MUCH_STORE = "[%s]门店存在多个";

    private static final String USER_TITLE = "说明：\n" +
            "1、多个门店名称，门店区域之间请用逗号（英文半角字符）隔开； \n" +
            "2、手机号码，用户邮箱暂只支持一个； \n" +
            "3、企业工号不支持中文；\n" +
            "4、备注最多支持 400 个字 \n" +
            "5、请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败 \n" +
            "6、仅能给用户配置企业在系统内创建的职位";

    private static final String QY_USER_TITLE = "说明：\n" +
                    "1、请从第9行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败\"； \n" +
                    "2、手机号码，用户邮箱暂只支持一个； \n" +
                    "3、企业中不存在该员工；\n";

    private static final String GROUP_USER_TITLE = "\"填写须知：\n" +
            "1. 请勿修改表格，从第3行开始填写，标红单元格字段为必填项。\n" +
            "2. 用户组：必填，输入用户组名，多个用户组请用英文半角“,”隔开，导入时不会影响成员或部门已加入的用户组\n" +
            "3. 当用户名称有相同且表格中只有1个该用户时，用户档案中所有相同的用户都会被导入进用户组。\"\t";

    private static final String NAME = "userName";
    private static final String NUM = "jobnumber";
    private static final String MOBILE = "mobile";


    @Async("importExportThreadPool")
    public void importData(String eid, ImportDistinctVO distinct, CurrentUser user, Future<List<UserImportDTO>> importTask, String contentType, ImportTaskDO task) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        try {
            boolean lock = lock(eid, ImportConstants.USER_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<UserImportDTO> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }

            log.info("总条数：{}", importList.size());
            // 统一手机号格式
            importList.forEach(v -> v.setMobile(MobileUtil.unifyMobile(v.getMobile())));
            importUser(eid, distinct, user, importList, contentType, task, Boolean.FALSE);
        } catch (BaseException e) {
            log.error("人员文件上传失败：{}"+ eid, e);
            DataSourceHelper.changeToSpecificDataSource(user.getDbName());
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());
            importTaskMapper.update(eid, task);
        }catch (Exception e) {
            DataSourceHelper.changeToSpecificDataSource(user.getDbName());
            log.error("人员文件上传失败：{}", eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("人员文件上传失败");
            importTaskMapper.update(eid, task);
        } finally {
            unlock(eid, ImportConstants.USER_KEY);
        }
    }

    @Async("importExportThreadPool")
    public void importExternalUser(String eid, ImportDistinctVO distinct, CurrentUser user, Future<List<ExternalUserImportDTO>> importTask, String contentType, ImportTaskDO task) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        try {
            boolean lock = lock(eid, ImportConstants.USER_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<ExternalUserImportDTO> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }

            log.info("总条数：{}", importList.size());
            // 统一手机号格式
            importList.forEach(v -> v.setMobile(MobileUtil.unifyMobile(v.getMobile())));
            importExternalUser(eid, distinct, user, importList, contentType, task, Boolean.FALSE);
        } catch (BaseException e) {
            log.error("人员文件上传失败：{}"+ eid, e);
            DataSourceHelper.changeToSpecificDataSource(user.getDbName());
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());
            importTaskMapper.update(eid, task);
        }catch (Exception e) {
            DataSourceHelper.changeToSpecificDataSource(user.getDbName());
            log.error("人员文件上传失败：{}", eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("人员文件上传失败");
            importTaskMapper.update(eid, task);
        } finally {
            unlock(eid, ImportConstants.USER_KEY);
        }
    }

    @Async("importExportThreadPool")
    public void historyEnterpriseImportData(String eid, ImportDistinctVO distinct, CurrentUser user, Future<List<HistoryEnterpriseUserImportDTO>> importTask, String contentType, ImportTaskDO task) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        try {
            boolean lock = lock(eid, ImportConstants.USER_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<HistoryEnterpriseUserImportDTO> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }

            log.info("总条数：{}", importList.size());
            List<UserImportDTO> userImportList = HistoryEnterpriseUserImportDTO.convertList(importList);
            // 统一手机号格式
            userImportList.forEach(v -> v.setMobile(MobileUtil.unifyMobile(v.getMobile())));
            importUser(eid, distinct, user, userImportList, contentType, task, Boolean.TRUE);
        } catch (BaseException e) {
            log.error("人员文件上传失败：{}"+ eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());
            importTaskMapper.update(eid, task);
        }catch (Exception e) {
            log.error("人员文件上传失败：{}", eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("人员文件上传失败");
            importTaskMapper.update(eid, task);
        } finally {
            unlock(eid, ImportConstants.USER_KEY);
        }
    }

    @Async("importExportThreadPool")
    public void importWeComData(String eid, ImportDistinctVO distinct, String dbName, Future<List<WeComUserImportDTO>> importTask, String contentType, ImportTaskDO task) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        try {
            boolean lock = lock(eid, ImportConstants.USER_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<WeComUserImportDTO> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }

            log.info("{},总条数：{}", eid, importList.size());
            List<WeComUserImportDTO> errorList = new ArrayList<>();
            importList.forEach(importUser -> {
                try {
                    ImportUserDTO importUserDTO = new ImportUserDTO();
                    BeanUtils.copyProperties(importUser, importUserDTO);
                    weComService.importWoComUser(importUserDTO, eid);
                } catch (Exception e) {
                    if (e instanceof ServiceException) {
                        importUser.setDec(NULL_USER);
                    }
                    errorList.add(importUser);
                    log.error("weComService.importWoComUs error", e);
                }
            });
            DataSourceHelper.changeToSpecificDataSource(dbName);
            if (errorList.size() != 0) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                String url = generateOssFileService.generateOssExcel(errorList, eid, QY_USER_TITLE, "出错人员列表", contentType, WeComUserImportDTO.class);
                task.setFileUrl(url);
            } else {
                task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
            }
            task.setTotalNum(importList.size());
            task.setSuccessNum(task.getTotalNum() - errorList.size());
            importTaskMapper.update(eid, task);
        } catch (BaseException e) {
            log.error("人员文件上传失败：{}"+ eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());
            importTaskMapper.update(eid, task);
        }catch (Exception e) {
            log.error("人员文件上传失败：{}", eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("人员文件上传失败");
            importTaskMapper.update(eid, task);
        } finally {
            unlock(eid, ImportConstants.USER_KEY);
        }
    }


    public void importUser(String eid, ImportDistinctVO distinct, CurrentUser user, List<UserImportDTO> importList, String contentType, ImportTaskDO task, boolean isHistoryEnterprise) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        EnterpriseSettingDO settingDO = enterpriseSettingMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        // 需要新增的数据
        List<EnterpriseUserDO> users = new ArrayList<>();
        // 有错误的数据
        List<UserImportDTO> errorList = new ArrayList<>();
        // 人员职位关系
        List<EnterpriseUserRole> userRoles = new ArrayList<>();
        // 人员与区域/门店映射
        List<UserAuthMappingDO> userAuthList = new ArrayList<>();
        List<RegionDO> allRegion = regionMapper.getAllRegion(eid);
        List<EnterpriseUserGroupDO> userGroupList = enterpriseUserGroupDao.listUserGroup(eid, null);
        Map<String, String> groupMap = userGroupList.stream().collect(Collectors.toMap(EnterpriseUserGroupDO::getGroupName, EnterpriseUserGroupDO::getGroupId, (k1, k2) -> k1));
        // 区域名称id映射
        Map<String, Long> regionNameIdMap = allRegion.stream()
                .filter(a -> a.getName() != null && a.getId() != null)
                .collect(Collectors.toMap(RegionDO::getName, RegionDO::getId,(a,b)->a));
        Map<String, String> regionFullPathMap = allRegion.stream()
                .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getFullRegionPath,(a,b)->a));
        List<RoleDTO> roles = roleMapper.fuzzyRoleBySource(eid, null, PositionSourceEnum.CREATE.getValue());
        if (Constants.PANASONIC_EIDS.contains(eid)){
            if (CollectionUtils.isEmpty(roles)){
                roles = new ArrayList<>();
            }
            List<RoleDTO> roleDTOS = roleMapper.fuzzyRoleBySource(eid, null, PositionSourceEnum.SYNC.getValue());
            if (CollectionUtils.isNotEmpty(roleDTOS)){
                roles.addAll(roleDTOS);
            }
        }
        // 职位名称-id映射
        Map<String, Long> roleNameMap = roles.stream()
                .filter(a -> a.getRoleName() != null && a.getId() != null)
                .collect(Collectors.toMap(RoleDTO::getRoleName, RoleDTO::getId,(a,b)->a));

        List<RegionDO> allStores = regionMapper.getAllStore(eid);
        // 区域完整路径映射需要包含门店
        Map<String, String> storeFullPathMap = CollStreamUtil.toMap(allStores, RegionDO::getRegionId, RegionDO::getFullRegionPath);
        regionFullPathMap.putAll(storeFullPathMap);

        // 门店名称与id映射关系
        Map<String, List<String>> storeNameIdMap = allStores.stream().collect(Collectors.groupingBy(RegionDO::getName,
                Collectors.mapping(RegionDO::getRegionId, Collectors.toList())));
        // 用户id列表
        List<String> userIdList = new ArrayList<>();
        List<EnterpriseUserDO> allUserList = userMapper.selectAllList(eid);
        Map<String, String> fullNameMap = new HashMap<>();
        if(!isHistoryEnterprise){
            fullNameMap = regionService.getFullNameMapRegionId(eid, Constants.SPRIT);
        }
        List<String> allUserIds = allUserList.stream().map(EnterpriseUserDO::getUserId).collect(Collectors.toList());
        // 人员工号-id映射
        Map<String, List<String>> userNumMap = allUserList.stream().filter(u -> StrUtil.isNotBlank(u.getJobnumber()))
                .collect(Collectors.groupingBy(EnterpriseUserDO::getJobnumber, Collectors.mapping(EnterpriseUserDO::getUserId, Collectors.toList())));
        // 人员手机号-id映射
        Map<String, List<String>> mobileMap = allUserList.stream().filter(u -> StrUtil.isNotBlank(u.getMobile()))
                .collect(Collectors.groupingBy(EnterpriseUserDO::getMobile, Collectors.mapping(EnterpriseUserDO::getUserId, Collectors.toList())));

        Map<String, String> unionIdMap = allUserList.stream().filter(u -> StrUtil.isNotBlank(u.getUnionid()))
                .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getUnionid, (k1, k2)->k1));

        Map<String, String> newNumMap = new HashMap<>();
        Map<String, String> newNameMap = new HashMap<>();
        Map<String, String> newMobileMap = new HashMap<>();
        long currTime = System.currentTimeMillis();
        //需要新增的用户列表
        List<UserImportDTO> addUserList = new ArrayList<>();
        boolean cover = distinct.isCover();
        List<String> deleteUserDirectList = new ArrayList<>();
        List<String> deleteUserGroupList = new ArrayList<>();
        List<SubordinateMappingDO> directList = new ArrayList<>();
        List<EnterpriseUserGroupMappingDO> addUserGroupList = new ArrayList<>();
        List<UserRegionMappingDO> userRegionList = new ArrayList<>();
        RegionDO unclassifiedRegionDO = regionService.getUnclassifiedRegionDO(eid);
        regionNameIdMap.put(unclassifiedRegionDO.getName(), unclassifiedRegionDO.getId());
        Set<String> uniqueUserIds = new HashSet<>();
        for (UserImportDTO importUser : importList) {
            String userId = importUser.getUserId();
            if (uniqueUserIds.contains(userId)){
                importUser.setDec(EXIST_USERID);
                errorList.add(importUser);
                continue;
            }
            uniqueUserIds.add(userId);
            if (StrUtil.isBlank(userId)) {
                importUser.setDec(NULL_USERID);
                errorList.add(importUser);
                continue;
            }
            boolean isAdd = false;
            if(!allUserIds.contains(userId)){
                if(AppTypeEnum.APP.getValue().equals(config.getAppType())){
                    String regex = "^[A-Za-z0-9]+$";
                    boolean matches = Pattern.matches(regex, userId);
                    if(!matches){
                        importUser.setDec("用户id格式不符合要求");
                        errorList.add(importUser);
                        continue;
                    }
                    if(userId.length() > 128){
                        importUser.setDec("用户id长度太长");
                        errorList.add(importUser);
                        continue;
                    }
                    importUser.setSubordinateRange(settingDO.getManageUser());
                    isAdd = true;
                }else{
                    importUser.setDec(USER_NOT_EXIST);
                    errorList.add(importUser);
                    continue;
                }
            }
            String remark = importUser.getRemark();
            if (StrUtil.isNotBlank(remark)) {
                if (remark.length() > 400){
                    importUser.setDec("备注不能超过400字");
                    errorList.add(importUser);
                    continue;
                }
            }
            String jobnumber = importUser.getJobnumber();
            String mobile = importUser.getMobile();
            if(StringUtils.isNotBlank(jobnumber)){
                // 判断工号是否有效
                boolean checkNum = checkData(jobnumber, userNumMap, importUser, true, errorList, userId, newNumMap);
                if (!checkNum) {
                    continue;
                }
                newNameMap.put(jobnumber, userId);
            }
            String unionId = unionIdMap.get(userId);
            if (StrUtil.isNotBlank(mobile)) {
                if(!MobileUtil.validateMobile(mobile)){
                    importUser.setDec("手机号格式不正确");
                    errorList.add(importUser);
                    continue;
                }
                // 判断手机号是否有效
                boolean checkMobile = checkData(mobile, mobileMap, importUser, false, errorList, userId, newMobileMap);
                if (!checkMobile) {
                    continue;
                }
                newMobileMap.put(mobile, userId);
            }
            if(StringUtils.isBlank(importUser.getName())){
                importUser.setDec("用户名称不能为空");
                errorList.add(importUser);
                continue;
            }
            // 校验职位
            log.info("校验职位信息，时间：" + System.currentTimeMillis());
            String positionNames = importUser.getPositionName();
            if(StringUtils.isBlank(positionNames)){
                importUser.setDec("职位不能为空");
                errorList.add(importUser);
                continue;
            }
            String[] positionNameArray = new String[0];
            try {
                positionNameArray = positionNames.split(",");
            } catch (Exception e) {
                importUser.setDec("用户职位数据格式不正确");
                errorList.add(importUser);
                continue;
            }
            List<Long> roleIds = new ArrayList<>();
            StringBuilder errorPosition = new StringBuilder();
            for (String positionName : positionNameArray) {
                Long positionId = roleNameMap.get(positionName.trim());
                if (positionId == null) {
                    errorPosition.append(positionName).append(Constants.PAUSE);
                    continue;
                } else {
                    roleIds.add(positionId);
                }
            }
            if (!Constants.PANASONIC_EIDS.contains(eid)){
                if(errorPosition.length() > 0){
                    importUser.setDec("职位："+ errorPosition.substring(0, errorPosition.length()-1) + "不存在");
                    errorList.add(importUser);
                    continue;
                }
            }

            if(CollectionUtils.isNotEmpty(roleIds)){
                userRoles.addAll(EnterpriseUserRole.convertList(roleIds, userId));
            }
            // 校验区域
            log.info("校验区域信息，时间：" + System.currentTimeMillis());
            String regionNames = importUser.getRegionName();
            if (StringUtils.isNotBlank(regionNames)) {
                String[] regionNameArray = new String[0];
                try {
                    regionNameArray = regionNames.split(",");
                } catch (Exception e) {
                    importUser.setDec("管辖区域名称数据格式不正确");
                    errorList.add(importUser);
                    continue;
                }
                List<String> errorRegionInfo = new ArrayList<>();
                for (String regionName : regionNameArray) {
                    Long regionId = regionNameIdMap.get(regionName.trim());
                    // 如果没有区域
                    if (regionId == null) {
                        errorRegionInfo.add(String.format(NULL_REGION, regionName));
                    } else {
                        UserAuthMappingDO userAuthMappingDO = UserAuthMappingDO.builder().mappingId(regionId.toString())
                                .userId(userId).createId(user.getUserId()).createTime(currTime).type("region").build();
                        userAuthList.add(userAuthMappingDO);
                    }
                }
                // 拼装错误信息
                if (CollUtil.isNotEmpty(errorRegionInfo)) {
                    importUser.setDec(String.join(",", errorRegionInfo));
                    errorList.add(importUser);
                    continue;
                }
            }
            // 校验门店
            log.info("校验门店信息，时间：" + System.currentTimeMillis());
            String storeNames = importUser.getStoreName();
            if (StringUtils.isNotBlank(storeNames)) {
                String[] storeNameArray = new String[0];
                try {
                    storeNameArray = storeNames.split(",");
                } catch (Exception e) {
                    importUser.setDec("管辖门店名称数据格式不正确");
                    errorList.add(importUser);
                    continue;
                }
                List<String> errorStoreInfo = new ArrayList<>();
                for (String storeName : storeNameArray) {
                    List<String> storeIds = storeNameIdMap.get(storeName.trim());
                    // 如果没有对应的门店id
                    if (CollUtil.isEmpty(storeIds)) {
                        errorStoreInfo.add(String.format(NULL_STORE, storeName));
                    } else {
                        // 如果门店名称存在多个
                        if (storeIds.size() > 1) {
                            errorStoreInfo.add(String.format(MUCH_STORE, storeName));
                        } else {
                            UserAuthMappingDO userAuthMappingDO = UserAuthMappingDO.builder().mappingId(storeIds.get(0))
                                    .userId(userId).createId(user.getUserId()).createTime(currTime).type("region").build();
                            userAuthList.add(userAuthMappingDO);
                        }
                    }
                }
                if (CollUtil.isNotEmpty(errorStoreInfo)) {
                    importUser.setDec(String.join(",", errorStoreInfo));
                    errorList.add(importUser);
                    continue;
                }
            }
            String directSuperior = importUser.getDirectSuperior();
            if(StringUtils.isNotBlank(directSuperior)){
                String directUserId = null;
                try {
                    directUserId = directSuperior.substring(0, directSuperior.indexOf("("));
                } catch (Exception e) {
                    importUser.setDec("直属上级数据格式不正确");
                    errorList.add(importUser);
                    continue;
                }
                if(!allUserIds.contains(directUserId)){
                    importUser.setDec("直属上级不存在");
                    errorList.add(importUser);
                    continue;
                }
                directList.add(SubordinateMappingDO.convertDirect(userId, directUserId, user.getUserId()));
            }else if(cover){
                //删除现有的上级
                deleteUserDirectList.add(userId);
            }
            String userGroups = importUser.getUserGroups();
            if(StringUtils.isNotBlank(userGroups)){
                String[] userGroupArray = new String[0];
                try {
                    userGroupArray = userGroups.split(",");
                } catch (Exception e) {
                    importUser.setDec("用户分组数据格式不正确");
                    errorList.add(importUser);
                    continue;
                }
                StringBuilder notExistGroup = new StringBuilder();
                List<String> groupIds = new ArrayList<>();
                for (String groupName : userGroupArray) {
                    String groupId = groupMap.get(groupName);
                    if(StringUtils.isBlank(groupId)){
                        notExistGroup.append(groupName).append(Constants.COMMA);
                        continue;
                    }
                    groupIds.add(groupId);
                }
                if(notExistGroup.length() > 0){
                    importUser.setDec("分组："+ notExistGroup.substring(0, notExistGroup.length()-1) + "不存在");
                    errorList.add(importUser);
                    continue;
                }
                if(CollectionUtils.isNotEmpty(groupIds)){
                    addUserGroupList.addAll(EnterpriseUserGroupMappingDO.convertDO(userId, groupIds, user.getUserId()));
                }
            }else if(cover){
                //删除现有分组
                deleteUserGroupList.add(userId);
            }
            String userRegions = importUser.getUserRegions();
            List<String> userRegionPathList = null;
            if(!isHistoryEnterprise){
                userRegionPathList = new ArrayList<>();
                if(StringUtils.isNotBlank(userRegions)) {
                    List<String> userRegionIds = new ArrayList<>();
                    String[] userRegionArray = new String[0];
                    try {
                        userRegionArray = userRegions.split(",");
                    } catch (Exception e) {
                        importUser.setDec("所属部门数据格式不正确");
                        errorList.add(importUser);
                        continue;
                    }
                    StringBuilder errorRegion = new StringBuilder();
                    for (String region : userRegionArray) {
                        String regionName = region;
                        if (!region.startsWith(Constants.SPRIT)) {
                            regionName = Constants.SPRIT + regionName;
                        }
                        if (!region.endsWith(Constants.SPRIT)) {
                            regionName = regionName + Constants.SPRIT;
                        }
                        String regionId = fullNameMap.get(regionName);
                        if (StringUtils.isBlank(regionId)) {
                            errorRegion.append(region).append(Constants.COMMA);
                            continue;
                        }
                        userRegionIds.add(regionId);
                    }
                    if (errorRegion.length() > 0) {
                        importUser.setDec("部门：" + errorRegion.substring(0, errorRegion.length() - 1) + "不存在");
                        errorList.add(importUser);
                        continue;
                    }
                    if (CollectionUtils.isNotEmpty(userRegionIds)) {
                        userRegionList.addAll(UserRegionMappingDO.convertList(userId, userRegionIds, user.getUserId()));
                    }
                    for (String userRegionId : userRegionIds) {
                        String regionPath = regionFullPathMap.get(userRegionId);
                        userRegionPathList.add(regionPath);
                    }
                }else if(cover){
                    userRegionList.add(UserRegionMappingDO.convertDO(userId, unclassifiedRegionDO.getRegionId(), user.getUserId()));
                    userRegionPathList.add(unclassifiedRegionDO.getFullRegionPath());
                }
            }
            if(StringUtils.isNotBlank(importUser.getEmail())){
                if(importUser.getEmail().length() > 64){
                    importUser.setDec("用户邮箱太长");
                    errorList.add(importUser);
                    continue;
                }
                String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
                if(!Pattern.matches(regex, importUser.getEmail())){
                    importUser.setDec("用户邮箱格式不正确");
                    errorList.add(importUser);
                    continue;
                }
            }
            // 拼装人员信息
            EnterpriseUserDO newUser = new EnterpriseUserDO();
            BeanUtil.copyProperties(importUser, newUser);
            if (StrUtil.isNotBlank(importUser.getUserStatusString())) {
                Integer userStatus = UserStatusEnum.getCodeByMessage(importUser.getUserStatusString());
                if(Objects.isNull(userStatus)){
                    importUser.setDec("用户状态不正确");
                    errorList.add(importUser);
                    continue;
                }
                newUser.setUserStatus(userStatus);
            }else{
                newUser.setUserStatus(UserStatusEnum.NORMAL.getCode());
            }
            newUser.setUserId(userId);
            newUser.setUnionid(unionId);
            if(Objects.nonNull(userRegionPathList)){
                newUser.setUserRegionIds(Constants.SQUAREBRACKETSLEFT + String.join(Constants.COMMA, userRegionPathList) + Constants.SQUAREBRACKETSRIGHT);
            }
            if(isAdd){
                addUserList.add(importUser);
            }
            users.add(newUser);
            userIdList.add(userId);
        }
        if (CollectionUtils.isNotEmpty(addUserList)) {
            addUser(eid, addUserList, config.getDbName(), regionNameIdMap, roleNameMap, storeNameIdMap, user, errorList);
        }
        dealUserDirect(eid, directList, deleteUserDirectList);
        dealUserGroup(eid, deleteUserGroupList, addUserGroupList);
        dealUserRegion(eid, userRegionList);
        userImportService.updateData(eid, importList.size(), users, errorList, userRoles, userAuthList, userIdList, contentType, task, cover, isHistoryEnterprise);
        DataSourceHelper.reset();
        enterpriseUserDao.updateConfigEnterpriseUserList(users);
    }

    public void importExternalUser(String eid, ImportDistinctVO distinct, CurrentUser user, List<ExternalUserImportDTO> importList, String contentType, ImportTaskDO task, boolean isHistoryEnterprise) {
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        // 需要新增的数据
        List<EnterpriseUserDO> users = new ArrayList<>();
        // 有错误的数据
        List<ExternalUserImportDTO> errorList = new ArrayList<>();
        // 人员职位关系
        List<EnterpriseUserRole> userRoles = new ArrayList<>();
        // 人员与区域/门店映射
        List<UserAuthMappingDO> userAuthList = new ArrayList<>();
        List<RegionDO> allRegion = regionMapper.getAllRegion(eid);
        RegionDO root = allRegion.stream().filter(o-> Constants.ROOT_DEPT_ID_STR.equals(o.getRegionId())).findFirst().get();
        List<EnterpriseUserGroupDO> userGroupList = enterpriseUserGroupDao.listUserGroup(eid, null);
        Map<String, String> groupMap = userGroupList.stream().collect(Collectors.toMap(EnterpriseUserGroupDO::getGroupName, EnterpriseUserGroupDO::getGroupId, (k1, k2) -> k1));
        // 区域名称id映射
        Map<String, Long> regionNameIdMap = allRegion.stream()
                .filter(a -> a.getName() != null && a.getId() != null)
                .collect(Collectors.toMap(RegionDO::getName, RegionDO::getId,(a,b)->a));
        Map<String, String> regionFullPathMap = allRegion.stream()
                .collect(Collectors.toMap(RegionDO::getRegionId, RegionDO::getFullRegionPath,(a,b)->a));

        List<RoleDTO> roles = roleMapper.fuzzyRoleBySource(eid, null, PositionSourceEnum.CREATE.getValue());
        // 职位名称-id映射
        Map<String, Long> roleNameMap = roles.stream()
                .filter(a -> a.getRoleName() != null && a.getId() != null)
                .collect(Collectors.toMap(RoleDTO::getRoleName, RoleDTO::getId,(a,b)->a));

        List<StoreDO> allStores = storeMapper.getAllStoreIds(eid, StoreIsDeleteEnum.EFFECTIVE.getValue());
        // 门店名称与id映射关系
        Map<String, List<String>> storeNameIdMap = allStores.stream().collect(Collectors.groupingBy(StoreDO::getStoreName,
                Collectors.mapping(StoreDO::getStoreId, Collectors.toList())));
        // 用户id列表
        List<String> userIdList = new ArrayList<>();
        List<EnterpriseUserDO> allUserList = userMapper.selectAllList(eid);
        Map<String, RegionDO> fullNameMap = regionService.getFullNameMapRegion(eid, Constants.SPRIT);
        List<String> allUserIds = allUserList.stream().map(EnterpriseUserDO::getUserId).collect(Collectors.toList());
        // 人员工号-id映射
        Map<String, List<String>> jobNumberMap = allUserList.stream().filter(u -> StrUtil.isNotBlank(u.getJobnumber()))
                .collect(Collectors.groupingBy(EnterpriseUserDO::getJobnumber, Collectors.mapping(EnterpriseUserDO::getUserId, Collectors.toList())));
        // 人员手机号-id映射
        Map<String, List<String>> mobileMap = allUserList.stream().filter(u -> StrUtil.isNotBlank(u.getMobile()))
                .collect(Collectors.groupingBy(EnterpriseUserDO::getMobile, Collectors.mapping(EnterpriseUserDO::getUserId, Collectors.toList())));
        Map<String, EnterpriseUserDO> userMap = allUserList.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, Function.identity()));

        Map<String, String> unionIdMap = allUserList.stream().filter(u -> StrUtil.isNotBlank(u.getUnionid()))
                .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getUnionid, (k1, k2)->k1));
        Map<String, String> newJobNumberMap = new HashMap<>();
        Map<String, String> newMobileMap = new HashMap<>();
        long currTime = System.currentTimeMillis();
        boolean cover = distinct.isCover();
        List<String> deleteUserDirectList = new ArrayList<>();
        List<String> deleteUserGroupList = new ArrayList<>();
        List<SubordinateMappingDO> directList = new ArrayList<>();
        List<EnterpriseUserGroupMappingDO> addUserGroupList = new ArrayList<>();
        List<UserRegionMappingDO> userRegionList = new ArrayList<>();
        RegionDO unclassifiedRegionDO = regionService.getUnclassifiedRegionDO(eid);
        regionNameIdMap.put(unclassifiedRegionDO.getName(), unclassifiedRegionDO.getId());
        for (ExternalUserImportDTO importUser : importList) {
            String mobile = Optional.ofNullable(importUser.getMobile()).map(o->o.trim()).orElse(null);
            String userName = Optional.ofNullable(importUser.getName()).map(o->o.trim()).orElse(null);
            String positionName = Optional.ofNullable(importUser.getPositionName()).map(o->o.trim()).orElse(null);
            String jobnumber = Optional.ofNullable(importUser.getJobnumber()).map(o->o.trim()).orElse(null);
            if(StringUtils.isBlank(mobile)){
                importUser.setDec("手机号码必填");
                errorList.add(importUser);
                continue;
            }
            if(StringUtils.isBlank(userName)){
                importUser.setDec("用户名称必填");
                errorList.add(importUser);
                continue;
            }
            if(userName.length() > 128){
                importUser.setDec("用户名称长度过长");
                errorList.add(importUser);
                continue;
            }
            if(StringUtils.isBlank(positionName)){
                importUser.setDec("用户职位必填");
                errorList.add(importUser);
                continue;
            }
            if(!MobileUtil.validateMobile(mobile)){
                importUser.setDec("手机号格式不正确");
                errorList.add(importUser);
                continue;
            }
            if(newMobileMap.containsKey(mobile)){
                importUser.setDec("手机号重复");
                errorList.add(importUser);
                continue;
            }
            List<String> mobileUserIds = mobileMap.get(mobile);
            String userId = null;
            if(CollectionUtils.isEmpty(mobileUserIds)){
                userId = "EXTERNAL" + UUIDUtils.get32UUID();
            }else{
                if(mobileUserIds.size() > 1){
                    importUser.setDec(MUCH_MOBILE);
                    errorList.add(importUser);
                    continue;
                }
                if(mobileUserIds.size() == 1){
                    userId = mobileUserIds.get(0);
                    EnterpriseUserDO enterpriseUser = userMap.get(userId);
                    if(UserTypeEnum.INTERNAL_USER.getCode() == enterpriseUser.getUserType()){
                        importUser.setDec("该号码被其他用户占用");
                        errorList.add(importUser);
                        continue;
                    }
                }
            }
            newMobileMap.put(mobile, userId);
            if(StringUtils.isNotBlank(jobnumber)){
                String regex = "[^\\u4e00-\\u9fa5]+";
                if(!Pattern.matches(regex, jobnumber)){
                    importUser.setDec("企业工号不能包含中文");
                    errorList.add(importUser);
                    continue;
                }
                if(newJobNumberMap.containsKey(jobnumber)){
                    importUser.setDec("工号重复");
                    errorList.add(importUser);
                    continue;
                }
                List<String> jobnumberUserIds = jobNumberMap.get(jobnumber);
                if(CollectionUtils.isNotEmpty(jobnumberUserIds)){
                    if(jobnumberUserIds.size() > 1){
                        importUser.setDec("该企业工号已在系统中存在多个，请先到系统中订正数据");
                        errorList.add(importUser);
                        continue;
                    }
                    String jobnumberUserId = jobnumberUserIds.get(0);
                    if(!jobnumberUserId.equals(userId)){
                        importUser.setDec("企业工号已在系统中");
                        errorList.add(importUser);
                        continue;
                    }
                }
                newJobNumberMap.put(jobnumber, userId);
            }

            String remark = importUser.getRemark();
            if (StrUtil.isNotBlank(remark) && remark.length() > 400) {
                importUser.setDec("备注不能超过400字");
                errorList.add(importUser);
                continue;
            }
            String unionId = unionIdMap.get(userId);
            if(StringUtils.isBlank(unionId)){
                unionId = "EXTERNAL" + UUIDUtils.get32UUID();
            }
            if(StringUtils.isBlank(importUser.getName())){
                importUser.setDec("用户名称不能为空");
                errorList.add(importUser);
                continue;
            }
            // 校验职位
            log.info("校验职位信息，时间：" + System.currentTimeMillis());
            String[] positionNameArray = new String[0];
            try {
                positionNameArray = positionName.split(",");
            } catch (Exception e) {
                importUser.setDec("用户职位数据格式不正确");
                errorList.add(importUser);
                continue;
            }
            List<Long> roleIds = new ArrayList<>();
            StringBuilder errorPosition = new StringBuilder();
            for (String roleName : positionNameArray) {
                Long positionId = roleNameMap.get(roleName.trim());
                if (positionId == null) {
                    errorPosition.append(roleName).append(Constants.PAUSE);
                    continue;
                } else {
                    roleIds.add(positionId);
                }
            }
            if(errorPosition.length() > 0){
                importUser.setDec("职位："+ errorPosition.substring(0, errorPosition.length()-1) + "不存在");
                errorList.add(importUser);
                continue;
            }
            if(CollectionUtils.isNotEmpty(roleIds)){
                userRoles.addAll(EnterpriseUserRole.convertList(roleIds, userId));
            }
            // 校验区域
            log.info("校验区域信息，时间：" + System.currentTimeMillis());
            String regionNames = importUser.getRegionName();
            if (StringUtils.isNotBlank(regionNames)) {
                String[] regionNameArray = new String[0];
                try {
                    regionNameArray = regionNames.split(",");
                } catch (Exception e) {
                    importUser.setDec("管辖区域名称数据格式不正确");
                    errorList.add(importUser);
                    continue;
                }
                List<String> errorRegionInfo = new ArrayList<>();
                for (String regionName : regionNameArray) {
                    Long regionId = regionNameIdMap.get(regionName.trim());
                    // 如果没有区域
                    if (regionId == null) {
                        errorRegionInfo.add(String.format(NULL_REGION, regionName));
                    } else {
                        UserAuthMappingDO userAuthMappingDO = UserAuthMappingDO.builder().mappingId(regionId.toString())
                                .userId(userId).createId(user.getUserId()).createTime(currTime).type("region").build();
                        userAuthList.add(userAuthMappingDO);
                    }
                }
                // 拼装错误信息
                if (CollUtil.isNotEmpty(errorRegionInfo)) {
                    importUser.setDec(String.join(",", errorRegionInfo));
                    errorList.add(importUser);
                    continue;
                }
            }
            // 校验门店
            log.info("校验门店信息，时间：" + System.currentTimeMillis());
            String storeNames = importUser.getStoreName();
            if (StringUtils.isNotBlank(storeNames)) {
                String[] storeNameArray = new String[0];
                try {
                    storeNameArray = storeNames.split(",");
                } catch (Exception e) {
                    importUser.setDec("管辖门店名称数据格式不正确");
                    errorList.add(importUser);
                    continue;
                }
                List<String> errorStoreInfo = new ArrayList<>();
                for (String storeName : storeNameArray) {
                    List<String> storeIds = storeNameIdMap.get(storeName.trim());
                    // 如果没有对应的门店id
                    if (CollUtil.isEmpty(storeIds)) {
                        errorStoreInfo.add(String.format(NULL_STORE, storeName));
                    } else {
                        // 如果门店名称存在多个
                        if (storeIds.size() > 1) {
                            errorStoreInfo.add(String.format(MUCH_STORE, storeName));
                        } else {
                            UserAuthMappingDO userAuthMappingDO = UserAuthMappingDO.builder().mappingId(storeIds.get(0))
                                    .userId(userId).createId(user.getUserId()).createTime(currTime).type("store").build();
                            userAuthList.add(userAuthMappingDO);
                        }
                    }
                }
                if (CollUtil.isNotEmpty(errorStoreInfo)) {
                    importUser.setDec(String.join(",", errorStoreInfo));
                    errorList.add(importUser);
                    continue;
                }
            }
            String directSuperior = importUser.getDirectSuperior();
            if(StringUtils.isNotBlank(directSuperior)){
                String directUserId = directSuperior;
                if(!allUserIds.contains(directUserId)){
                    importUser.setDec("直属上级不存在");
                    errorList.add(importUser);
                    continue;
                }
                directList.add(SubordinateMappingDO.convertDirect(userId, directUserId, user.getUserId()));
            }else if(cover){
                //删除现有的上级
                deleteUserDirectList.add(userId);
            }
            String userGroups = importUser.getUserGroups();
            if(StringUtils.isNotBlank(userGroups)){
                String[] userGroupArray = new String[0];
                try {
                    userGroupArray = userGroups.split(",");
                } catch (Exception e) {
                    importUser.setDec("用户分组数据格式不正确");
                    errorList.add(importUser);
                    continue;
                }
                StringBuilder notExistGroup = new StringBuilder();
                List<String> groupIds = new ArrayList<>();
                for (String groupName : userGroupArray) {
                    String groupId = groupMap.get(groupName);
                    if(StringUtils.isBlank(groupId)){
                        notExistGroup.append(groupName).append(Constants.COMMA);
                        continue;
                    }
                    groupIds.add(groupId);
                }
                if(notExistGroup.length() > 0){
                    importUser.setDec("分组："+ notExistGroup.substring(0, notExistGroup.length()-1) + "不存在");
                    errorList.add(importUser);
                    continue;
                }
                if(CollectionUtils.isNotEmpty(groupIds)){
                    addUserGroupList.addAll(EnterpriseUserGroupMappingDO.convertDO(userId, groupIds, user.getUserId()));
                }
            }else if(cover){
                //删除现有分组
                deleteUserGroupList.add(userId);
            }
            String userRegions = importUser.getUserRegions();
            List<String> userRegionPathList = new ArrayList<>();
            if(StringUtils.isNotBlank(userRegions)) {
                List<String> userRegionIds = new ArrayList<>();
                List<RegionDO> userRegionsList = new ArrayList<>();
                String[] userRegionArray = new String[0];
                try {
                    userRegionArray = userRegions.split(",");
                } catch (Exception e) {
                    importUser.setDec("所属部门数据格式不正确");
                    errorList.add(importUser);
                    continue;
                }
                StringBuilder errorRegion = new StringBuilder();
                StringBuilder notExternalRegion = new StringBuilder();
                for (String region : userRegionArray) {
                    String regionName = region;
                    if (!region.startsWith(Constants.SPRIT)) {
                        regionName = Constants.SPRIT + regionName;
                    }
                    if (!region.endsWith(Constants.SPRIT)) {
                        regionName = regionName + Constants.SPRIT;
                    }
                    RegionDO regionDO = fullNameMap.get(Constants.SPRIT + root.getName() + regionName);
                    if (Objects.isNull(regionDO)) {
                        errorRegion.append(region).append(Constants.COMMA);
                        continue;
                    }
                    if (!regionDO.getIsExternalNode()) {
                        notExternalRegion.append(region).append(Constants.COMMA);
                        continue;
                    }
                    userRegionIds.add(regionDO.getRegionId());
                    userRegionsList.add(regionDO);
                }
                if (errorRegion.length() > 0) {
                    importUser.setDec("部门：" + errorRegion.substring(0, errorRegion.length() - 1) + "不存在");
                    errorList.add(importUser);
                    continue;
                }
                if (notExternalRegion.length() > 0) {
                    importUser.setDec("部门：" + notExternalRegion.substring(0, notExternalRegion.length() - 1) + "是内部节点不存在");
                    errorList.add(importUser);
                    continue;
                }
                if (CollectionUtils.isNotEmpty(userRegionIds)) {
                    userRegionList.addAll(UserRegionMappingDO.convertList(userId, userRegionIds, user.getUserId()));
                }
                for (RegionDO userRegionDO : userRegionsList) {
                    userRegionPathList.add(userRegionDO.getFullRegionPath());
                }
            }else{
                userRegionList.addAll(UserRegionMappingDO.convertList(userId, Arrays.asList(String.valueOf(FixedRegionEnum.EXTERNAL_USER.getId())), user.getUserId()));
                userRegionPathList.add(FixedRegionEnum.EXTERNAL_USER.getFullRegionPath());
            }
            if(StringUtils.isNotBlank(importUser.getEmail())){
                if(importUser.getEmail().length() > 64){
                    importUser.setDec("用户邮箱太长");
                    errorList.add(importUser);
                    continue;
                }
                String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
                if(!Pattern.matches(regex, importUser.getEmail())){
                    importUser.setDec("用户邮箱格式不正确");
                    errorList.add(importUser);
                    continue;
                }
            }
            // 拼装人员信息
            EnterpriseUserDO newUser = new EnterpriseUserDO();
            BeanUtil.copyProperties(importUser, newUser);
            if (StrUtil.isNotBlank(importUser.getUserStatusString())) {
                Integer userStatus = UserStatusEnum.getCodeByMessage(importUser.getUserStatusString());
                if(Objects.isNull(userStatus)){
                    importUser.setDec("用户状态不正确");
                    errorList.add(importUser);
                    continue;
                }
                newUser.setUserStatus(userStatus);
            }else{
                newUser.setUserStatus(UserStatusEnum.NORMAL.getCode());
            }
            newUser.setUserId(userId);
            newUser.setUnionid(unionId);
            newUser.setUserType(UserTypeEnum.EXTERNAL_USER.getCode());
            newUser.setId(UUIDUtils.get32UUID());
            newUser.setActive(Boolean.TRUE);
            newUser.setIsAdmin(Boolean.FALSE);
            newUser.setMainAdmin(Boolean.FALSE);
            if(CollectionUtils.isNotEmpty(userRegionPathList)){
                newUser.setUserRegionIds(Constants.SQUAREBRACKETSLEFT + String.join(Constants.COMMA, userRegionPathList) + Constants.SQUAREBRACKETSRIGHT);
            }
            users.add(newUser);
            userIdList.add(userId);
        }
        dealUserDirect(eid, directList, deleteUserDirectList);
        dealUserGroup(eid, deleteUserGroupList, addUserGroupList);
        dealUserRegion(eid, userRegionList);
        userImportService.updateExternalData(eid, importList.size(), users, errorList, userRoles, userAuthList, userIdList, contentType, task);
        DataSourceHelper.reset();
        enterpriseUserDao.batchInsertPlatformUsers(users);
        List<EnterpriseUserMappingDO> userMappingList = new ArrayList<>();
        users.forEach(o->{
            EnterpriseUserMappingDO userMapping = new EnterpriseUserMappingDO();
            userMapping.setId(UUIDUtils.get32UUID());
            userMapping.setUserId(o.getId());
            userMapping.setEnterpriseId(eid);
            userMapping.setUnionid(o.getUnionid());
            userMapping.setUserStatus(1);
            userMappingList.add(userMapping);
        });
        enterpriseUserMappingMapper.batchInsertOrUpdate(userMappingList);
    }

    public void dealUserDirect(String enterpriseId, List<SubordinateMappingDO> directList, List<String> deleteUserDirectList){
        subordinateMappingDAO.deletedByUserIdsAndType(enterpriseId, deleteUserDirectList);
        if(CollectionUtils.isEmpty(directList)){
            return;
        }
        List<String> userIds = directList.stream().map(SubordinateMappingDO::getUserId).distinct().collect(Collectors.toList());
        subordinateMappingDAO.deletedByUserIdsAndType(enterpriseId, userIds);
        subordinateMappingDAO.batchInsertSubordinateMapping(enterpriseId, directList);
    }

    public void dealUserRegion(String enterpriseId, List<UserRegionMappingDO> userRegionList){
        if(CollectionUtils.isEmpty(userRegionList)){
            return;
        }
        List<String> userIds = userRegionList.stream().map(UserRegionMappingDO::getUserId).collect(Collectors.toList());
        Map<String, UserRegionMappingDO> userRegionMap = userRegionList.stream().collect(Collectors.toMap(k -> k.getUserId() + Constants.MOSAICS + k.getRegionId(), Function.identity(), (k1, k2) -> k1));
        List<UserRegionMappingDO> regionIdsByUserIds = userRegionMappingDAO.getRegionIdsByUserIds(enterpriseId, userIds);
        List<Integer> deleteIds = new ArrayList<>();
        List<String> existRegion = new ArrayList<>();
        for (UserRegionMappingDO regionIdsByUserId : regionIdsByUserIds) {
            String key = regionIdsByUserId.getUserId() + Constants.MOSAICS + regionIdsByUserId.getRegionId();
            UserRegionMappingDO regionMapping = userRegionMap.get(key);
            if(Objects.isNull(regionMapping)){
                //删除
                deleteIds.add(regionIdsByUserId.getId());
                continue;
            }
            existRegion.add(key);
        }
        userRegionList = userRegionList.stream().filter(k->!existRegion.contains(k.getUserId() + Constants.MOSAICS + k.getRegionId())).collect(Collectors.toList());
        userRegionMappingDAO.deletedByIds(enterpriseId, deleteIds);
        userRegionMappingDAO.batchInsertRegionMapping(enterpriseId, userRegionList);
    }

    public void dealUserGroup(String enterpriseId, List<String> deleteUserGroupList, List<EnterpriseUserGroupMappingDO> addUserGroupList){
        //删除用户不在的分组
        enterpriseUserGroupMappingDao.deleteMappingByUserIdList(enterpriseId, deleteUserGroupList);
        if(CollectionUtils.isEmpty(addUserGroupList)){
            return;
        }
        Map<String, EnterpriseUserGroupMappingDO> userGroupMap = addUserGroupList.stream().collect(Collectors.toMap(k -> k.getUserId() + Constants.MOSAICS + k.getGroupId(), Function.identity(), (k1, k2)->k1));
        List<String> userIds = addUserGroupList.stream().map(EnterpriseUserGroupMappingDO::getUserId).distinct().collect(Collectors.toList());
        List<EnterpriseUserGroupMappingDO> userGroupMappingList = enterpriseUserGroupMappingDao.listByUserIdList(enterpriseId, userIds);
        List<Long> deleteIds = new ArrayList<>();
        for (EnterpriseUserGroupMappingDO userGroup : userGroupMappingList) {
            String key = userGroup.getUserId() + Constants.MOSAICS + userGroup.getGroupId();
            EnterpriseUserGroupMappingDO enterpriseUserGroupMapping = userGroupMap.get(key);
            if(Objects.isNull(enterpriseUserGroupMapping)){
                deleteIds.add(userGroup.getId());
            }
        }
        enterpriseUserGroupMappingDao.deleteMappingByIdList(enterpriseId, deleteIds);
        enterpriseUserGroupMappingDao.batchInsertOrUpdateUserGroupMapping(enterpriseId, addUserGroupList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateData(String eid, int totalNum, List<EnterpriseUserDO> users, List<UserImportDTO> errorList, List<EnterpriseUserRole> userRoles,
                           List<UserAuthMappingDO> userAuthList, List<String> userIds, String contentType, ImportTaskDO task, boolean cover, Boolean isHistoryEnterprise) {

        if (CollUtil.isNotEmpty(users)) {
            log.info("更新人员信息，时间：" + System.currentTimeMillis());
            if (!cover) {
                Lists.partition(users, 1000).forEach(f -> enterpriseUserDao.nonOverwriteUpdateImportUser(eid, f));
            } else {
                Lists.partition(users, 1000).forEach(f -> enterpriseUserDao.batchUpdateImportUser(eid, f));
            }
        }
        if (CollUtil.isNotEmpty(userIds)) {
            // 更新人员权限
            log.info("删除原来的人员权限，时间：" + System.currentTimeMillis());
            if (!cover) {
                //如果是非覆盖更新，不能删除没填区域、门店信息的人的对应权限
                List<UserAuthMappingDO> storeAuths = new ArrayList<>();
                List<UserAuthMappingDO> areaAuths = new ArrayList<>();
                userAuthList.forEach(userAuth -> {
                    if (UserAuthMappingTypeEnum.STORE.getCode().equals(userAuth.getType())) {
                        storeAuths.add(userAuth);
                    } else {
                        areaAuths.add(userAuth);
                    }
                });
                List<String> storeUserIds = storeAuths.stream().map(UserAuthMappingDO::getUserId).collect(Collectors.toList());
                List<String> areaUserIds = areaAuths.stream().map(UserAuthMappingDO::getUserId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(storeUserIds)) {
                    Lists.partition(storeUserIds, 1000).forEach(
                        f -> userAuthMappingMapper.deleteAuthMappingByUserIdsAndType(eid, f, UserAuthMappingTypeEnum.STORE.getCode()));
                }
                if (CollectionUtils.isNotEmpty(areaUserIds)) {
                    Lists.partition(areaUserIds, 1000).forEach(
                        f -> userAuthMappingMapper.deleteAuthMappingByUserIdsAndType(eid, f, UserAuthMappingTypeEnum.REGION.getCode()));
                }
            } else {
                Lists.partition(userIds, 1000).forEach(f -> userAuthMappingMapper.deleteAuthMappingByUserIds(eid, f));
            }
            if (CollUtil.isNotEmpty(userAuthList)) {
                List<String> storeIdListTmp = ListUtils.emptyIfNull(userAuthList).stream()
                        .filter(item -> UserAuthMappingTypeEnum.STORE.getCode().equals(item.getType()))
                        .map(UserAuthMappingDO::getMappingId)
                        .collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(storeIdListTmp)){
                    List<RegionDO> storeRegionList = regionService.listRegionByStoreIds(eid, storeIdListTmp);
                    Map<String, RegionDO> storeRegionMap = ListUtils.emptyIfNull(storeRegionList).stream()
                            .filter(a -> a.getStoreId() != null)
                            .collect(Collectors.toMap(RegionDO::getStoreId, data -> data, (a, b) -> a));
                    userAuthList.forEach(userAuth -> {
                        if (UserAuthMappingTypeEnum.STORE.getCode().equals(userAuth.getType())) {
                            userAuth.setType(UserAuthMappingTypeEnum.REGION.getCode());
                            userAuth.setMappingId(String.valueOf(storeRegionMap.get(userAuth.getMappingId()).getId()));
                        }
                    });
                }
                log.info("绑定新的人员权限，时间：" + System.currentTimeMillis());
                Lists.partition(userAuthList, 1000).forEach(f -> userAuthMappingMapper.batchInsertUserAuthMapping(eid, f));
            }
            // 更新人员职位 需要过滤主管理员
            List<EnterpriseUserDO> mainAdminList = userMapper.getMainAdmin(eid);
            List<String> validUserIds;
            List<EnterpriseUserRole> validUserRoles;
            if (CollectionUtils.isNotEmpty(mainAdminList)) {
                log.info("主管理员信息：{}", mainAdminList);
                List<String> mainUserIdList = mainAdminList.stream()
                        .map(EnterpriseUserDO::getUserId)
                        .collect(Collectors.toList());
                validUserIds = userIds.stream().filter(f -> !mainUserIdList.contains(f)).collect(Collectors.toList());
                validUserRoles = userRoles.stream().filter(f -> !mainUserIdList.contains(f.getUserId())).collect(Collectors.toList());
            } else {
                validUserIds = userIds;
                validUserRoles = userRoles;
            }
            if (!cover) {
                //如果是非覆盖更新，只删除导入表中填了职位信息的用户角色
                validUserIds = validUserRoles.stream().map(EnterpriseUserRole::getUserId).collect(Collectors.toList());
            }
            if (CollUtil.isNotEmpty(validUserIds)) {
                log.info("删除原来的人员-职位映射，时间：" + System.currentTimeMillis());
                Lists.partition(validUserIds, 1000).forEach(f -> roleMapper.deleteRolesByPerson(eid, f, false));
            }
            if (CollUtil.isNotEmpty(validUserRoles)) {
                log.info("绑定新的人员职位映射，时间：" + System.currentTimeMillis());
                Lists.partition(validUserRoles, 1000).forEach(f -> roleMapper.insertBatchUserRole(eid, f));
            }

        }
        int successNum = totalNum - errorList.size();
        if (CollUtil.isNotEmpty(errorList)) {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            String url = null;
            if(isHistoryEnterprise){
                List<HistoryEnterpriseUserImportDTO> errorDataList = HistoryEnterpriseUserImportDTO.convert(errorList);
                url = generateOssFileService.generateOssExcel(errorDataList, eid, USER_TITLE, "出错人员列表", contentType, HistoryEnterpriseUserImportDTO.class);
            }else{
                url = generateOssFileService.generateOssExcel(errorList, eid, USER_TITLE, "出错人员列表", contentType, UserImportDTO.class);
            }
            task.setFileUrl(url);
        } else {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        }
        task.setSuccessNum(successNum);
        task.setTotalNum(totalNum);
        importTaskMapper.update(eid, task);
        //用户数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(eid, userIds, ChangeDataOperation.UPDATE.getCode(), ChangeDataType.USER.getCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateExternalData(String eid, int totalNum, List<EnterpriseUserDO> users, List<ExternalUserImportDTO> errorList, List<EnterpriseUserRole> userRoles,
                           List<UserAuthMappingDO> userAuthList, List<String> userIds, String contentType, ImportTaskDO task) {

        if (CollUtil.isNotEmpty(users)) {
            log.info("更新人员信息，时间：" + System.currentTimeMillis());
            Lists.partition(users, 1000).forEach(f -> userMapper.batchInsertOrUpdate(f, eid));
        }
        if (CollUtil.isNotEmpty(userIds)) {
            // 更新人员权限
            log.info("删除原来的人员权限，时间：" + System.currentTimeMillis());
            Lists.partition(userIds, 1000).forEach(f -> userAuthMappingMapper.deleteAuthMappingByUserIds(eid, f));
            if (CollUtil.isNotEmpty(userAuthList)) {
                List<String> storeIdListTmp = ListUtils.emptyIfNull(userAuthList).stream()
                        .filter(item -> UserAuthMappingTypeEnum.STORE.getCode().equals(item.getType()))
                        .map(UserAuthMappingDO::getMappingId)
                        .collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(storeIdListTmp)){
                    List<RegionDO> storeRegionList = regionService.listRegionByStoreIds(eid, storeIdListTmp);
                    Map<String, RegionDO> storeRegionMap = ListUtils.emptyIfNull(storeRegionList).stream()
                            .filter(a -> a.getStoreId() != null)
                            .collect(Collectors.toMap(data -> data.getStoreId(), data -> data, (a, b) -> a));
                    userAuthList.forEach(userAuth -> {
                        if (UserAuthMappingTypeEnum.STORE.getCode().equals(userAuth.getType())) {
                            userAuth.setType(UserAuthMappingTypeEnum.REGION.getCode());
                            userAuth.setMappingId(String.valueOf(storeRegionMap.get(userAuth.getMappingId()).getId()));
                        }
                    });
                }
                log.info("绑定新的人员权限，时间：" + System.currentTimeMillis());
                Lists.partition(userAuthList, 1000).forEach(f -> userAuthMappingMapper.batchInsertUserAuthMapping(eid, f));
            }
            // 更新人员职位 需要过滤主管理员
            List<EnterpriseUserDO> mainAdminList = userMapper.getMainAdmin(eid);
            List<String> validUserIds;
            List<EnterpriseUserRole> validUserRoles;
            if (CollectionUtils.isNotEmpty(mainAdminList)) {
                log.info("主管理员信息：{}", mainAdminList);
                List<String> mainUserIdList = mainAdminList.stream()
                        .map(EnterpriseUserDO::getUserId)
                        .collect(Collectors.toList());
                validUserIds = userIds.stream().filter(f -> !mainUserIdList.contains(f)).collect(Collectors.toList());
                validUserRoles = userRoles.stream().filter(f -> !mainUserIdList.contains(f.getUserId())).collect(Collectors.toList());
            } else {
                validUserIds = userIds;
                validUserRoles = userRoles;
            }
            if (CollUtil.isNotEmpty(validUserIds)) {
                log.info("删除原来的人员-职位映射，时间：" + System.currentTimeMillis());
                Lists.partition(validUserIds, 1000).forEach(f -> roleMapper.deleteRolesByPerson(eid, f, false));
            }
            if (CollUtil.isNotEmpty(validUserRoles)) {
                log.info("绑定新的人员职位映射，时间：" + System.currentTimeMillis());
                Lists.partition(validUserRoles, 1000).forEach(f -> roleMapper.insertBatchUserRole(eid, f));
            }
        }
        int successNum = totalNum - errorList.size();
        if (CollUtil.isNotEmpty(errorList)) {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            String url = generateOssFileService.generateOssExcel(errorList, eid, USER_TITLE, "导入错误人员列表", contentType, ExternalUserImportDTO.class);
            task.setFileUrl(url);
        } else {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        }
        task.setSuccessNum(successNum);
        task.setTotalNum(totalNum);
        importTaskMapper.update(eid, task);
        //用户数据修改，推送酷学院，发送mq消息，异步操作
        coolCollegeIntegrationApiService.sendDataChangeMsg(eid, userIds, ChangeDataOperation.UPDATE.getCode(), ChangeDataType.USER.getCode());
    }


    public String importDataGroupUser(String eid, CurrentUser user, Future<List<UserGroupImportDTO>> importTask, String contentType, ImportTaskDO task,String importType) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        try {
            boolean lock = lock(eid, ImportConstants.USER_GROUP_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return null;
            }
            List<UserGroupImportDTO> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return null;
            }

            log.info("总条数：{}", importList.size());
            return importGroupUser(eid, user, importList, contentType, task,importType);
        } catch (BaseException e) {
            log.error("importGroupUser上传失败：{}"+ eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());
            importTaskMapper.update(eid, task);
        }catch (Exception e) {
            log.error("importGroupUser失败：{}", eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("用户分组文件上传失败");
            importTaskMapper.update(eid, task);
        } finally {
            unlock(eid, ImportConstants.USER_GROUP_KEY);
        }
        return null;
    }

    public String importGroupUser(String eid, CurrentUser user, List<UserGroupImportDTO> importList, String contentType, ImportTaskDO task,String importType) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        // 错误数据
        List<UserGroupImportDTO> errorList = new ArrayList<>();

        List<EnterpriseUserGroupDO> userGroupDOList = enterpriseUserGroupDao.listUserGroup(eid, null);
        Map<String, String> userGroupMap = userGroupDOList.stream().collect(Collectors.toMap(EnterpriseUserGroupDO::getGroupName, EnterpriseUserGroupDO::getGroupId, (k1, k2) -> k1));
        StringBuilder errorMsg = new StringBuilder();
        AtomicInteger num = new AtomicInteger();
        importList.forEach(userGroupImportDTO -> {
            num.getAndIncrement();
            if (StringUtils.isBlank(userGroupImportDTO.getName())) {
                userGroupImportDTO.setDesc("用户名称为空");
            }
            if (importType.equals(ImportUserGroupEnum.USER_ID_TYPE.getImportType())){
                if (StringUtils.isBlank(userGroupImportDTO.getUserId())) {
                    userGroupImportDTO.setDesc("账号ID为空");
                }
            }else if (importType.equals(ImportUserGroupEnum.JOB_NUMBER_TYPE.getImportType())){
                if (StringUtils.isBlank(userGroupImportDTO.getJobNumber())) {
                    userGroupImportDTO.setDesc("用户工号为空");
                }
            }
            if (StringUtils.isBlank(userGroupImportDTO.getUserGroupName())) {
                userGroupImportDTO.setDesc((StringUtils.isBlank(userGroupImportDTO.getDesc() ) ? "" : userGroupImportDTO.getDesc()) + " 用户分组名称为空");
            }
            if (StringUtils.isNotBlank(userGroupImportDTO.getDesc())) {
                errorList.add(userGroupImportDTO);
                errorMsg.append("第").append(num).append("行：").append(userGroupImportDTO.getDesc()).append(";").append("<br/>");
                return;
            }

//            List<String> userIdList = userMapper.selectUserIdByUserName(eid, userGroupImportDTO.getName());
            List<String> userIdList = enterpriseUserDao.selectUserIdsByUserIdOrJobNumber(eid,importType,userGroupImportDTO.getUserId(),userGroupImportDTO.getJobNumber());
            if (CollUtil.isEmpty(userIdList)) {
                userGroupImportDTO.setDesc("用户不存在");
                errorMsg.append("第").append(num).append("行：").append(userGroupImportDTO.getDesc()).append(";").append("<br/>");
                errorList.add(userGroupImportDTO);
                return;
            }
            List<String> groupNameList = Arrays.asList(userGroupImportDTO.getUserGroupName().split(","));
            List<EnterpriseUserGroupMappingDO> userGroupMappingDOList = new ArrayList<>();
            List<String> notExistGroupList = new ArrayList<>();
            userIdList.forEach(userId -> {
                groupNameList.forEach(groupName -> {
                    if (userGroupMap.containsKey(groupName)) {
                        EnterpriseUserGroupMappingDO userGroupMappingDO = new EnterpriseUserGroupMappingDO();
                        userGroupMappingDO.setGroupId(userGroupMap.get(groupName));
                        userGroupMappingDO.setUserId(userId);
                        userGroupMappingDO.setCreateTime(new Date());
                        userGroupMappingDO.setUpdateTime(new Date());
                        userGroupMappingDO.setCreateUserId(user.getUserId());
                        userGroupMappingDO.setDeleted(false);
                        userGroupMappingDOList.add(userGroupMappingDO);
                    } else {
                        notExistGroupList.add(groupName);
                    }
                });
            });
            if (CollUtil.isNotEmpty(notExistGroupList)) {
                userGroupImportDTO.setDesc("用户分组" + StringUtils.join(notExistGroupList) + "不存在");
                errorMsg.append("第").append(num).append("行：").append(userGroupImportDTO.getDesc()).append(";").append("<br/>");
                errorList.add(userGroupImportDTO);
                return;
            }
            if (CollUtil.isNotEmpty(userGroupMappingDOList)) {
                enterpriseUserGroupMappingDao.batchInsertOrUpdateUserGroupMapping(eid, userGroupMappingDOList);
            }
        });
        int successNum = importList.size() - errorList.size();
        task.setSuccessNum(successNum);
        task.setTotalNum(importList.size());
        if (CollectionUtils.isNotEmpty(errorList)) {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            String url = generateOssFileService.generateOssExcel(errorList, eid, GROUP_USER_TITLE, "用户分组导入出错列表", contentType, UserGroupImportDTO.class);
            task.setFileUrl(url);
            importTaskMapper.update(eid, task);
        } else {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
            importTaskMapper.update(eid, task);
        }
        return errorMsg.toString();
    }


    private boolean checkData(String check, Map<String, List<String>> checkMap, UserImportDTO importUser, boolean isNum,
                              List<UserImportDTO> errorList, String userId, Map<String, String> newCheckMap) {
        if (StrUtil.isNotBlank(check)) {
            check = check.trim();
            // 先校验系统数据
            List<String> checkList = checkMap.get(check);
            if (CollUtil.isNotEmpty(checkList)) {
                // 如果存在多个
                if (checkList.size() > 1) {
                    importUser.setDec(isNum ? MUCH_NUM : MUCH_MOBILE);
                    errorList.add(importUser);
                    return false;
                }
                // 如果已存在的企业工号/手机号码不是当前人员
                String checkUserId = checkList.get(0);
                if (!userId.equals(checkUserId)) {
                    importUser.setDec(isNum ? EXIST_NUM: EXIST_MOBILE);
                    errorList.add(importUser);
                    return false;
                }
            }
            // 再校验excel数据
            if (newCheckMap.get(check) != null) {
                importUser.setDec(isNum ? EXIST_EXCEL_NUM : EXIST_EXCEL_MOBILE);
                errorList.add(importUser);
                return false;
            }
        }
        return true;
    }

    /**
     * app端导入新增用户
     * @param eid
     * @param addUserList
     * @author: xugangkun
     * @return void
     * @date: 2021/7/22 18:51
     */
    private void addUser(String eid, List<UserImportDTO> addUserList, String dbName, Map<String, Long> regionNameIdMap,
                         Map<String, Long> roleNameMap, Map<String, List<String>> storeNameIdMap, CurrentUser user,
                         List<UserImportDTO> errorList) {
        try {
            long currTime = System.currentTimeMillis();
            List<EnterpriseUserDO> addUsers = new ArrayList<>();
            List<EnterpriseUserMappingDO> userMappingDOS = new ArrayList<>();
            List<EnterpriseUserRole> userRoles = new ArrayList<>();
            List<EnterpriseUserDepartmentDO> userDeptList = new ArrayList<>();
            List<UserAuthMappingDO> userAuthList = new ArrayList<>();
            String appType = AppTypeEnum.APP.getValue();
            for (UserImportDTO addUser : addUserList) {
                EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
                String userId = addUser.getUserId();
                if(StringUtils.isBlank(userId)){
                    userId = appType + UUIDUtils.get32UUID();
                }
                String unionId = appType + UUIDUtils.get32UUID();
                enterpriseUserDO.setId(UUIDUtils.get32UUID());
                enterpriseUserDO.setJobnumber(addUser.getJobnumber());
                enterpriseUserDO.setUserId(userId);
                enterpriseUserDO.setName(addUser.getName());
                enterpriseUserDO.setUnionid(unionId);
                enterpriseUserDO.setMobile(addUser.getMobile());
                enterpriseUserDO.setEmail(addUser.getEmail());
                enterpriseUserDO.setDepartments("[1]");
                enterpriseUserDO.setActive(true);
                enterpriseUserDO.setIsAdmin(false);
                enterpriseUserDO.setMainAdmin(false);
                enterpriseUserDO.setRoles(Role.EMPLOYEE.getId());
                enterpriseUserDO.setLanguage("zh_cn");
                enterpriseUserDO.setUserStatus(UserStatusEnum.NORMAL.getCode());
                enterpriseUserDO.setAppType(appType);
                enterpriseUserDO.setSubordinateRange(addUser.getSubordinateRange());
                enterpriseUserDO.setCreateUserId(user.getUserId());
                addUsers.add(enterpriseUserDO);
                //用户企业映射关系
                EnterpriseUserMappingDO enterpriseUserMappingDO = new EnterpriseUserMappingDO();
                enterpriseUserMappingDO.setId(UUIDUtils.get32UUID());
                enterpriseUserMappingDO.setEnterpriseId(eid);
                enterpriseUserMappingDO.setUserId(enterpriseUserDO.getId());
                enterpriseUserMappingDO.setUserStatus(enterpriseUserDO.getUserStatus());
                enterpriseUserMappingDO.setUnionid(enterpriseUserDO.getUnionid());
                userMappingDOS.add(enterpriseUserMappingDO);
//                //默认把用户添加到根部门下
//                userDeptList.add(new EnterpriseUserDepartmentDO(enterpriseUserDO.getUserId(), 1L, Boolean.FALSE));
                // 校验职位
                log.info("校验职位信息，时间：" + System.currentTimeMillis());
                String positionName = addUser.getPositionName();
                if (StrUtil.isNotBlank(positionName)) {
                    Long positionId = roleNameMap.get(positionName.trim());
                    if (positionId == null) {
                        addUser.setDec(NULL_POSITION);
                        errorList.add(addUser);
                        continue;
                    } else {
                        EnterpriseUserRole userRole = new EnterpriseUserRole(positionId.toString(), userId);
                        userRoles.add(userRole);
                    }
                } else {
                    EnterpriseUserRole userRole = new EnterpriseUserRole(Role.EMPLOYEE.getId(), userId);
                    userRoles.add(userRole);
                }
                // 校验区域
                log.info("校验区域信息，时间：" + System.currentTimeMillis());
                String regionNames = addUser.getRegionName();
                if (StrUtil.isNotBlank(regionNames)) {
                    String[] regionNameArray = regionNames.split(",");
                    List<String> errorRegionInfo = new ArrayList<>();
                    for (String regionName : regionNameArray) {
                        Long regionId = regionNameIdMap.get(regionName.trim());
                        // 如果没有区域
                        if (regionId == null) {
                            errorRegionInfo.add(String.format(NULL_REGION, regionName));
                        } else {
                            UserAuthMappingDO userAuthMappingDO = UserAuthMappingDO.builder().mappingId(regionId.toString())
                                    .userId(userId).createId(user.getUserId()).createTime(currTime).type("region").build();
                            userAuthList.add(userAuthMappingDO);
                        }
                    }
                    // 拼装错误信息
                    if (CollUtil.isNotEmpty(errorRegionInfo)) {
                        addUser.setDec(String.join(",", errorRegionInfo));
                        errorList.add(addUser);
                        continue;
                    }
                }
                // 校验门店
                log.info("校验门店信息，时间：" + System.currentTimeMillis());
                String storeNames = addUser.getStoreName();
                if (StrUtil.isNotBlank(storeNames)) {
                    String[] storeNameArray = storeNames.split(",");
                    List<String> errorStoreInfo = new ArrayList<>();
                    for (String storeName : storeNameArray) {
                        List<String> storeIds = storeNameIdMap.get(storeName.trim());
                        // 如果没有对应的门店id
                        if (CollUtil.isEmpty(storeIds)) {
                            errorStoreInfo.add(String.format(NULL_STORE, storeName));
                        } else {
                            // 如果门店名称存在多个
                            if (storeIds.size() > 1) {
                                errorStoreInfo.add(String.format(MUCH_STORE, storeName));
                            } else {
                                UserAuthMappingDO userAuthMappingDO = UserAuthMappingDO.builder().mappingId(storeIds.get(0))
                                        .userId(userId).createId(user.getUserId()).createTime(currTime).type("region").build();
                                userAuthList.add(userAuthMappingDO);
                            }
                        }
                    }
                    if (CollUtil.isNotEmpty(errorStoreInfo)) {
                        addUser.setDec(String.join(",", errorStoreInfo));
                        errorList.add(addUser);
                        continue;
                    }
                }
            }
            //切到平台库
            DataSourceHelper.reset();
            Lists.partition(addUsers, 1000).forEach(f -> {
                enterpriseUserDao.batchInsertPlatformUsers(f);
            });
            Lists.partition(userMappingDOS, 1000).forEach(f -> enterpriseUserMappingMapper.batchInsertOrUpdate(f));
            //切到企业库
            DataSourceHelper.changeToSpecificDataSource(dbName);
            Lists.partition(addUsers, 1000).forEach(f -> {
                enterpriseUserDao.batchInsertOrUpdate(addUsers, eid);
            });
            Lists.partition(userRoles, 1000).forEach(f -> roleMapper.insertBatchUserRole(eid, f));
            Lists.partition(userDeptList, 1000).forEach(f -> enterpriseUserDepartmentMapper.batchInsert(eid, f));
            Lists.partition(userAuthList, 1000).forEach(f -> userAuthMappingMapper.batchInsertUserAuthMapping(eid, f));
            //用户数据修改，推送酷学院，发送mq消息，异步操作
            List<String> userIds = addUsers.stream()
                    .map(EnterpriseUserDO::getUserId)
                    .collect(Collectors.toList());
            coolCollegeIntegrationApiService.sendDataChangeMsg(eid, userIds, ChangeDataOperation.ADD.getCode(), ChangeDataType.USER.getCode());
        } catch (Exception e) {
            log.error("adduser error ", e);
        }
    }

}
