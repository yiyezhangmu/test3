package com.coolcollege.intelligent.service.login.impl;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.enums.user.UserTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.login.LoginRecordMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.enterprise.*;
import com.coolcollege.intelligent.model.login.SwitchEnterpriseDO;
import com.coolcollege.intelligent.model.login.UserLoginDO;
import com.coolcollege.intelligent.model.login.UserLoginDTO;
import com.coolcollege.intelligent.model.login.vo.UserBaseInfoVO;
import com.coolcollege.intelligent.model.login.vo.UserLoginEnterpriseVO;
import com.coolcollege.intelligent.model.login.vo.UserLoginVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.RefreshUser;
import com.coolcollege.intelligent.service.enterprise.*;
import com.coolcollege.intelligent.service.login.LoginService;
import com.coolcollege.intelligent.service.login.strategy.LoginStrategy;
import com.coolcollege.intelligent.util.LoginUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: LoginBaseService
 * @Description: 登录基本处理抽象类
 * @date 2021-07-16 11:07
 */
@Slf4j
public abstract class LoginBaseService implements LoginStrategy {
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysDepartmentService sysDepartmentService;
    @Resource
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private EnterpriseService enterpriseService;
    @Resource
    private EnterpriseUserMappingService enterpriseUserMappingService;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;
    @Resource
    private LoginService loginService;
    @Resource
    private LoginRecordMapper loginRecordMapper;
    @Resource
    private LoginUtil loginUtil;

    //密码最大重试次数
    private final int MAX_ERROR_PASSWORD_COUNT = 5;

    protected LoginBaseService() {
    }

    /**
     * 策略登录实现方法
     * @param param
     * @return
     */
    public abstract ResponseResult userLogin(UserLoginDTO param, List<UserLoginDO> allUsers);

    /**
     * 登录基础方法
     * @param param
     * @return
     */
    @Override
    public ResponseResult login(UserLoginDTO param){
        log.info("login:{}", param.toString());
        String errorPasswordCountKey = MessageFormat.format(RedisConstant.ERROR_PASSWORD_COUNT_KEY, LocalDate.now(), param.getMobile());
        String errorCount = redisUtilPool.getString(errorPasswordCountKey);
        //判断密码错误次数
        if(StringUtils.isNotBlank(errorCount)){
            if(Integer.valueOf(errorCount) >= MAX_ERROR_PASSWORD_COUNT){
                return ResponseResult.fail(ErrorCodeEnum.PASSWORD_ERROR_MAX_COUNT, errorCount);
            }
        }
        List<UserLoginDO> allUsers = getAllUserByMobile(param.getMobile());
        if(CollectionUtils.isEmpty(allUsers)){
            return ResponseResult.fail(ErrorCodeEnum.ACCOUNT_NOT_EXIST);
        }
        return userLogin(param, allUsers);
    }

    @Override
    public ResponseResult getCurrentLoginUserEnterpriseList(String mobile) {
        if(StringUtils.isBlank(mobile)){
            return ResponseResult.fail(ErrorCodeEnum.IMPROVE_USER_INFO);
        }
        List<UserLoginDO> allUserByMobile = getAllUserByMobile(mobile);
        if(CollectionUtils.isEmpty(allUserByMobile)){
            return ResponseResult.fail(ErrorCodeEnum.USER_NON_EXISTENT);
        }
        List<String> userIds = allUserByMobile.stream().map(UserLoginDO::getId).collect(Collectors.toList());
        Map<String, String> enterpriseIdUserIdMap = getUserAllEnterpriseIds(userIds, null);
        List<String> enterpriseIds = enterpriseIdUserIdMap.keySet().stream().collect(Collectors.toList());
        List<UserLoginEnterpriseVO> enterpriseList = getEnterpriseList(enterpriseIds);
        return ResponseResult.success(enterpriseList);
    }

    @Override
    public ResponseResult switchLoginEnterprise(SwitchEnterpriseDO param, String mobile){
        //根据手机号获取用户信息
        List<UserLoginDO> allUsers = getAllUserByMobile(mobile);
        if(CollectionUtils.isEmpty(allUsers)){
            return ResponseResult.fail(ErrorCodeEnum.USER_NON_EXISTENT);
        }
        return ResponseResult.success(getUserLoginVO(allUsers, param.getEnterpriseId()));
    }

    /**
     * 根据手机号获取所有用户
     * @param mobile
     * @return
     */
    public List<UserLoginDO> getAllUserByMobile(String mobile) {
        DataSourceHelper.reset();
        List<EnterpriseUserDO> userList = enterpriseUserMapper.getPlatformUserByMobile(mobile);
        List<UserLoginDO> resultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(userList)){
            for (EnterpriseUserDO enterpriseUser : userList) {
                resultList.add(new UserLoginDO(enterpriseUser.getId(), enterpriseUser.getUserId(), enterpriseUser.getMobile(), enterpriseUser.getPassword(), enterpriseUser.getUnionid(), enterpriseUser.getAppType()));
            }
        }
        return resultList;
    }


    /**
     * 根据平台库enterprise_user.id获取用户对应的所有企业id
     * @param userIds
     * @return
     */
    public Map<String, String> getUserAllEnterpriseIds(List<String> userIds, String loginEnterpriseId) {
        DataSourceHelper.reset();
        List<EnterpriseUserMappingDO> enterpriseUserList = enterpriseUserMappingService.getUserAllEnterpriseIdsByUserIds(userIds, loginEnterpriseId);
        if(CollectionUtils.isEmpty(enterpriseUserList) && StringUtils.isNotBlank(loginEnterpriseId)){
            throw new ServiceException(ErrorCodeEnum.USER_NOT_JOIN_CUR_ENTERPRISE);
        }
        if(CollectionUtils.isEmpty(enterpriseUserList)){
            throw new ServiceException(ErrorCodeEnum.USER_NOT_JOIN_ENTERPRISE);
        }
        List<EnterpriseUserMappingDO> normalUsers = enterpriseUserList.stream().filter(o -> UserStatusEnum.NORMAL.getCode().equals(o.getUserStatus())).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(normalUsers)){
            List<Integer> userStatus = enterpriseUserList.stream().filter(o->Objects.nonNull(o.getUserStatus())).map(EnterpriseUserMappingDO::getUserStatus).distinct().collect(Collectors.toList());
            if(CollectionUtils.isEmpty(userStatus) || userStatus.contains(UserStatusEnum.WAIT_AUDIT.getCode())){
                throw new ServiceException(ErrorCodeEnum.USER_WAIT_AUIT);
            }
            if(CollectionUtils.isEmpty(userStatus) || userStatus.contains(UserStatusEnum.FREEZE.getCode())){
                throw new ServiceException(ErrorCodeEnum.USER_FREEZE);
            }
        }
        return normalUsers.stream().filter(o-> StringUtils.isNotBlank(o.getUserId()) && StringUtils.isNotBlank(o.getEnterpriseId()))
                .collect(Collectors.toMap(k->k.getEnterpriseId(), v->v.getUserId(), (k1, k2)->k1));
    }


    /**
     * 根据企业id获取所有的企业信息
     * @param enterpriseIds
     * @return
     */
    public List<UserLoginEnterpriseVO> getEnterpriseList(List<String> enterpriseIds) {
        DataSourceHelper.reset();
        List<EnterpriseDO> enterpriseList = enterpriseService.getEnterpriseByIds(enterpriseIds);
        if(CollectionUtils.isEmpty(enterpriseList)){
            return Lists.newLinkedList();
        }
        List<EnterpriseConfigDO> enterpriseConfig = enterpriseConfigService.selectByEnterpriseIds(enterpriseIds);
        Map<String, EnterpriseConfigDO> enterpriseConfigMap = enterpriseConfig.stream().collect(Collectors.toMap(k -> k.getEnterpriseId(), Function.identity(), (k1, k2) -> k2));
        List<UserLoginEnterpriseVO> resultList = new ArrayList<>();
        for (EnterpriseDO enterprise : enterpriseList) {
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMap.get(enterprise.getId());
            String dingCorpId = Optional.ofNullable(enterpriseConfigDO).map(o->o.getDingCorpId()).orElse("");
            resultList.add(new UserLoginEnterpriseVO(enterprise.getCorpLogoUrl(), enterprise.getId(), enterprise.getName(), enterprise.getOriginalName(), enterprise.getIsVip(), dingCorpId,enterprise.getCreateTime(), enterprise.getAppType()));
        }
        return resultList;
    }

    /**
     * 生成token
     * @return
     */
    public String getToken() {
        RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        return randomNumberGenerator.nextBytes().toHex();
    }

    /**
     * 获取登录用户信息
     * @param userList
     * @param loginEnterpriseId
     * @return
     */
    public UserLoginVO getUserLoginVO(List<UserLoginDO> userList, String loginEnterpriseId) {
        long startTime = System.currentTimeMillis();
        Map<String, UserLoginDO> userInfoMap = userList.stream().collect(Collectors.toMap(k -> k.getId(), Function.identity(), (k1, k2) -> k1));
        List<String> userIds = userList.stream().map(UserLoginDO::getId).distinct().collect(Collectors.toList());
        List<String> unionIds = userList.stream().map(UserLoginDO::getUnionid).collect(Collectors.toList());
        Map<String, String> enterpriseIdUserIdMap = getUserAllEnterpriseIds(userIds, loginEnterpriseId);
        log.info("@1---->{}", System.currentTimeMillis() -startTime);
        List<String> enterpriseIds = enterpriseIdUserIdMap.keySet().stream().collect(Collectors.toList());
        List<UserLoginEnterpriseVO> enterpriseList = getEnterpriseList(enterpriseIds);
        log.info("@2---->{}", System.currentTimeMillis() -startTime);
        if(CollectionUtils.isEmpty(enterpriseList)){
            throw new ServiceException(ErrorCodeEnum.USER_NOT_JOIN_ENTERPRISE);
        }
        Map<String, UserLoginEnterpriseVO> enterpriseMap = enterpriseList.stream().collect(Collectors.toMap(k->k.getEnterpriseId(), Function.identity()));
        //loginEnterpriseId为空的话 默认登录第一个企业
        if(StringUtils.isBlank(loginEnterpriseId)){
            loginEnterpriseId = enterpriseList.get(0).getEnterpriseId();
        }
        if(!enterpriseMap.containsKey(loginEnterpriseId)){
            throw new ServiceException(ErrorCodeEnum.USER_NOT_JOIN_CUR_ENTERPRISE);
        }
        UserLoginEnterpriseVO currentEnterprise = enterpriseMap.get(loginEnterpriseId);
        String userId = enterpriseIdUserIdMap.get(loginEnterpriseId);
        UserLoginDO userInfo = userInfoMap.get(userId);
        String accessToken = getToken();
        UserBaseInfoVO userBaseInfo = new UserBaseInfoVO();
        CurrentUser currentUser = getCurrentUser(userInfo, accessToken, userBaseInfo, currentEnterprise, unionIds);
        redisUtilPool.setString(RedisConstant.ACCESS_TOKEN_PREFIX + accessToken, JSON.toJSONString(currentUser), 24 * 60 * 60);
        // 缓存用户id和token的关系，用于用户管理界面清空用户的token
        loginUtil.saveTokenUserIdAndRoleId(loginEnterpriseId, currentUser.getUserId(), String.valueOf(currentUser.getSysRoleDO().getId()), accessToken);
        String refreshToken = getToken();
        RefreshUser refreshUser = new RefreshUser();
        refreshUser.setUserId(currentUser.getUserId());
        refreshUser.setCorpId(currentUser.getDingCorpId());
        refreshUser.setEid(currentUser.getEnterpriseId());
        redisUtilPool.setString(RedisConstant.REFRESH_TOKEN_PREFIX + refreshToken,JSON.toJSONString(refreshUser), Constants.REFRESH_TOKEN_EXPIRE);
        UserLoginVO userLogin = new UserLoginVO();
        userLogin.setEnterpriseList(enterpriseList);
        userLogin.setCurrentEnterprise(currentEnterprise);
        userLogin.setAccessToken(accessToken);
        userLogin.setRefreshToken(refreshToken);
        userLogin.setUserInfo(userBaseInfo);
        userLogin.setIsNeedImproveUserInfo(getUserIsNeedImproveUserInfo(userInfo.getMobile(), userInfo.getEnterpriseUserMobile(), userInfo.getPassword(), loginEnterpriseId, userInfo.getUnionid()));
        try {
            DynamicDataSourceContextHolder.clearDataSourceType();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(currentEnterprise.getEnterpriseId());
            DynamicDataSourceContextHolder.setDataSourceType(enterpriseConfig.getDbName());
            loginRecordMapper.insertLoginRecording(currentUser.getEnterpriseId(),currentUser.getUserId(),System.currentTimeMillis());
        }catch (Exception e){
            log.error("insertLoginRecording Error,eid:{},userId:{}",currentUser.getEnterpriseId(),currentUser.getUserId(),e);
        }
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSetting = enterpriseSettingMapper.selectByEnterpriseId(loginEnterpriseId);
        Boolean enableExternalUser = Optional.ofNullable(enterpriseSetting).map(EnterpriseSettingDO::getEnableExternalUser).orElse(Boolean.FALSE);
        if(UserTypeEnum.EXTERNAL_USER.getCode() == currentUser.getUserType() && !enableExternalUser){
            throw new ServiceException(ErrorCodeEnum.EXTERNAL_USER_LOGIN_ERROR);
        }
        return userLogin;
    }

    /**
     *
     * @param userInfo
     * @param token
     * @param enterprise
     * @return
     */
    public CurrentUser getCurrentUser(UserLoginDO userInfo, String token, UserBaseInfoVO userBaseInfo, UserLoginEnterpriseVO enterprise, List<String> unionIds){
        long startTime = System.currentTimeMillis();
        CurrentUser currentUser = new CurrentUser();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterprise.getEnterpriseId());
        log.info("@1=======>{}", System.currentTimeMillis() -startTime);
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterprise.getEnterpriseId());
        log.info("@2=======>{}", System.currentTimeMillis() -startTime);
        // 切到企业库
        DynamicDataSourceContextHolder.setDataSourceType(enterpriseConfig.getDbName());
        // 查企业用户
        List<EnterpriseUserDO> enterpriseUserList = enterpriseUserService.selectByUnionids(enterprise.getEnterpriseId(), unionIds);
        log.info("@3=======>{}", System.currentTimeMillis() -startTime);
        if(CollectionUtils.isEmpty(enterpriseUserList)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "暂无权限，请联系管理员！");
        }
        EnterpriseUserDO enterpriseUser = enterpriseUserList.get(0);
        //等待审核
        if(UserStatusEnum.WAIT_AUDIT.getCode().equals(enterpriseUser.getUserStatus())){
            throw new ServiceException(ErrorCodeEnum.USER_ACCOUNT_WAIT_AUDIT);
        }
        userInfo.setEnterpriseUserMobile(enterpriseUser.getMobile());
        //账号冻结
        if(UserStatusEnum.FREEZE.getCode().equals(enterpriseUser.getUserStatus())){
            throw new ServiceException(ErrorCodeEnum.USER_FREEZE);
        }
        //
        SysRoleDO sysRoleDoByUserId = sysRoleMapper.getHighestPrioritySysRoleDoByUserId(enterprise.getEnterpriseId(), enterpriseUser.getUserId());
        log.info("@4=======>{}", System.currentTimeMillis() -startTime);
        if(sysRoleDoByUserId==null){
            // 如果没有最高优先级的，给未分配的角色
            sysRoleDoByUserId = sysRoleMapper.getRoleByRoleEnum(enterprise.getEnterpriseId(), Role.EMPLOYEE.getRoleEnum());
        }
        log.info("@5=======>{}", System.currentTimeMillis() -startTime);
        // 切回来
        DynamicDataSourceContextHolder.clearDataSourceType();
        currentUser.setId(enterpriseUser.getId());
        currentUser.setUserId(enterpriseUser.getUserId());
        currentUser.setAccount(enterpriseUser.getUserId());
        currentUser.setDbName(enterpriseConfig.getDbName());
        currentUser.setDepartmentIds(enterpriseUser.getDepartments());
        currentUser.setRoles(enterpriseUser.getRoles());
        currentUser.setLanguage(enterpriseUser.getLanguage());
        currentUser.setIsAdmin(enterpriseUser.getIsAdmin());
        currentUser.setGroupCorpId(enterpriseDO.getGroupCropId());
        //添加MainCorpId字段信息
        if(StringUtils.isNotBlank(enterpriseConfig.getMainCorpId())){
            currentUser.setMainCorpId(enterpriseConfig.getMainCorpId());
        }

        //设置当前登录人使用的企业相关信息
        currentUser.setRoleAuth(sysRoleDoByUserId.getRoleAuth());
        currentUser.setSysRoleDO(sysRoleDoByUserId);
        currentUser.setEnterpriseId(enterprise.getEnterpriseId());
        currentUser.setEnterpriseName(enterpriseDO.getName());
        currentUser.setLogoName(enterpriseDO.getLogoName());
        currentUser.setEnterpriseLogo(enterpriseDO.getCorpLogoUrl());
        currentUser.setIsVip(enterprise.getIsVip());
        currentUser.setActive(enterpriseUser.getActive());
        // 企业开通时间
        currentUser.setOpenTime(enterpriseDO.getCreateTime());

        currentUser.setAvatar(enterpriseUser.getAvatar());
        currentUser.setEmail(enterpriseUser.getEmail());
        // 水印增加员工工号
        if (StringUtils.isNotEmpty(enterpriseUser.getJobnumber())) {
            currentUser.setJobnumber(enterpriseUser.getJobnumber());
        }
        // 水印增加员工职位
        if (StringUtils.isNotEmpty(enterpriseUser.getPosition())) {
            currentUser.setPosition(enterpriseUser.getPosition());
        }
        currentUser.setMobile(userInfo.getMobile());
        currentUser.setName(enterpriseUser.getName());

        currentUser.setDingCorpId(enterpriseConfig.getDingCorpId());

        currentUser.setAccessToken(token);
        currentUser.setAppType(enterpriseConfig.getAppType());
        currentUser.setUnionid(userInfo.getUnionid());
        currentUser.setUserType(enterpriseUser.getUserType());

        userBaseInfo.setUserId(enterpriseUser.getUserId());
        userBaseInfo.setId(enterpriseUser.getId());
        userBaseInfo.setName(enterpriseUser.getName());
        userBaseInfo.setAvatar(enterpriseUser.getAvatar());
        userBaseInfo.setMobile(userInfo.getMobile());
        userBaseInfo.setIsAdmin(enterpriseUser.getIsAdmin());
        userBaseInfo.setEmail(enterpriseUser.getEmail());
        userBaseInfo.setRoles(enterpriseUser.getRoles());
        userBaseInfo.setLanguage(enterpriseUser.getLanguage());
        userBaseInfo.setAppType(enterpriseConfig.getAppType());
        userBaseInfo.setSysRoleDO(sysRoleDoByUserId);
        return currentUser;
    }

    /**
     * 判断是否需要完善用户信息
     * @param mobile
     * @param enterpriseUserMobile
     * @param password
     * @param enterpriseId
     * @return
     */
    public boolean getUserIsNeedImproveUserInfo(String mobile, String enterpriseUserMobile, String password, String enterpriseId, String unionId) {
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSetting = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
        if(Objects.nonNull(enterpriseSetting) && enterpriseSetting.isMultiLogin()){
            if(AIEnum.AI_UUID.getCode().equals(unionId)){
                return false;
            }
            if(StringUtils.isAnyBlank(mobile, enterpriseUserMobile, password)){
                return true;
            }
            if(!mobile.equals(enterpriseUserMobile)){
                return true;
            }
        }
        return false;
    }
}
