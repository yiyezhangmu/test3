package com.coolcollege.intelligent.service.boss.impl;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.boss.BossUserMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.boss.BossUserStatusUpdateDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.login.UserLoginDO;
import com.coolcollege.intelligent.model.login.vo.UserBaseInfoVO;
import com.coolcollege.intelligent.model.login.vo.UserLoginEnterpriseVO;
import com.coolcollege.intelligent.model.login.vo.UserLoginVO;
import com.coolcollege.intelligent.model.system.BossUserDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.dto.BossLoginUserDTO;
import com.coolcollege.intelligent.model.userholder.BossUserHolder;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.RefreshUser;
import com.coolcollege.intelligent.service.boss.BossUserService;
import com.coolcollege.intelligent.service.enterprise.*;
import com.coolcollege.intelligent.service.login.impl.strategy.DefaultLoginService;
import com.coolcollege.intelligent.util.AIUserTool;
import com.coolcollege.intelligent.util.LoginUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.dingtalk.api.response.OapiCallCalluserResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 系统用户管理
 *
 * @author byd
 * @date 2021-01-28 17:07
 */
@Service(value = "bossUserService")
@Slf4j
public class BossUserServiceImpl implements BossUserService {

    @Resource
    private BossUserMapper bossUserMapper;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Autowired
    private DingService dingService;

    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseUserMappingService enterpriseUserMappingService;
    @Resource
    private DefaultLoginService defaultLoginService;
    @Resource
    private LoginUtil loginUtil;


    @Override
    public PageVO getList(int pageSize, int pageNum, String username, Integer status) {

        PageHelper.startPage(pageNum, pageSize);
        List<BossUserDO> list = bossUserMapper.getList(username, status);
        return PageHelperUtil.getPageVO(new PageInfo<>(list));
    }

    @Override
    public BossUserDO detail(Long id) {
        return bossUserMapper.selectById(id);
    }

    @Override
    public int insertUser(BossUserDO userDO, String appType) {
        BossUserDO old = bossUserMapper.getUserByUsername(userDO.getUsername());
        if(old != null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "用户名已存在");
        }
        if(StringUtils.isBlank(userDO.getPassword())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "密码不能为空");
        }
        userDO.setPassword(MD5Util.md5(userDO.getPassword() + Constants.BOSS_PASSWORD_KEY));
        if(StringUtils.isNotBlank(userDO.getUserId())){
            Boolean aBoolean = dingService.setCallUser(Collections.singletonList(userDO.getUserId()), appType);
            //如果插入只能电话白名单失败则不插入都boss中
            if(!aBoolean){
                userDO.setUserId(null);
            }
        }
        return bossUserMapper.insertUser(userDO);
    }

    @Override
    public int updateUser(BossUserDO userDO, String appType) {
        BossUserDO old = bossUserMapper.getUserByUsername(userDO.getUsername());
        if(old != null && !old.getId().equals(userDO.getId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "用户名已存在");
        }
        if(StringUtils.isNotBlank(userDO.getUserId())){
            Boolean aBoolean = dingService.setCallUser(Collections.singletonList(userDO.getUserId()), appType);
            //如果插入只能电话白名单失败则不插入都boss中
            if(!aBoolean){
                userDO.setUserId(null);
            }
        }
        if(Objects.nonNull(userDO.getPassword())){
            userDO.setPassword(MD5Util.md5(userDO.getPassword() + Constants.BOSS_PASSWORD_KEY));
        }
        return bossUserMapper.updateByIdSelective(userDO);
    }

    @Override
    public int deleteById(Long userId) {
        return bossUserMapper.deleteById(userId);
    }

    @Override
    public BossUserDO getUserByUsername(String username) {
        return bossUserMapper.getUserByUsername(username);
    }

    @Override
    public Boolean callAdmin(String eid) {
        BossLoginUserDTO user = BossUserHolder.getUser();
        EnterpriseDO enterpriseDO = enterpriseService.selectById(eid);
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        if(StringUtils.isBlank(user.getUserId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "该账号无智能电话权限！");
        }
        if(StringUtils.isBlank(enterpriseDO.getAuthUserId())){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "未记录开通人！");
        }
        OapiCallCalluserResponse response = dingService.callUser(enterpriseConfigDO.getDingCorpId(), user.getUserId(),
                enterpriseDO.getAuthUserId(), enterpriseConfigDO.getAppType());
       if(response!=null&& response.isSuccess()){
           return true;
       }
       if(response==null){
           throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "调用智能电话服务失败！");
       }
        throw new ServiceException(ErrorCodeEnum.FAIL.getCode(),response.getErrmsg());
    }

    @Override
    public Boolean updateUserStatus(BossUserStatusUpdateDTO param) {
        BossUserDO update = new BossUserDO();
        update.setId(param.getId());
        update.setStatus(param.getStatus());
        return bossUserMapper.updateByIdSelective(update) > 0;
    }

    @Override
    public boolean usernameCheck(String username) {
        return Objects.nonNull(bossUserMapper.getUserByUsername(username));
    }

    @Override
    public ResponseResult bossGetTokenByEidAndUserID(String eid, String userId){
        if(StringUtils.isBlank(eid) || StringUtils.isBlank(userId)){
            return ResponseResult.fail(ErrorCodeEnum.PARAM_MISSING);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);
        if(enterpriseConfigDO == null){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        EnterpriseUserDO enterpriseUser = null;
        if(AIEnum.AI_USERID.getCode().equals(userId)){
            enterpriseUser = AIUserTool.getAiUser();
        }else{
            enterpriseUser = enterpriseUserDao.selectByUserId(eid, userId);
        }
        if(enterpriseUser == null){
            return ResponseResult.fail(ErrorCodeEnum.USER_NOT_JOIN_CUR_ENTERPRISE);
        }
        UserLoginDO userLoginDO = new UserLoginDO(enterpriseUser.getId(), enterpriseUser.getUserId(), enterpriseUser.getMobile(), enterpriseUser.getPassword(), enterpriseUser.getUnionid(), enterpriseConfigDO.getAppType());
        return ResponseResult.success(getUserLoginVO(userLoginDO,eid));
    }


    public UserLoginVO getUserLoginVO(UserLoginDO userLoginDO, String loginEnterpriseId) {
        List<UserLoginEnterpriseVO> enterpriseList = getEnterpriseList(Collections.singletonList(loginEnterpriseId));
        if(CollectionUtils.isEmpty(enterpriseList)){
            throw new ServiceException(ErrorCodeEnum.USER_NOT_JOIN_ENTERPRISE);
        }
        //loginEnterpriseId为空的话 默认登录第一个企业
        UserLoginEnterpriseVO currentEnterprise = enterpriseList.get(0);
        String accessToken = getToken();
        UserBaseInfoVO userBaseInfo = new UserBaseInfoVO();
        CurrentUser currentUser = getCurrentUser(userLoginDO, accessToken, userBaseInfo, currentEnterprise);
        redisUtilPool.setString(RedisConstant.ACCESS_TOKEN_PREFIX + accessToken, JSON.toJSONString(currentUser), 24 * 60 * 60);
        loginUtil.saveTokenUserIdAndRoleId(currentEnterprise.getEnterpriseId(), currentUser.getUserId(), String.valueOf(currentUser.getSysRoleDO().getId()), accessToken);
        String refreshToken = getToken();
        RefreshUser refreshUser = new RefreshUser();
        refreshUser.setUserId(currentUser.getUserId());
        refreshUser.setCorpId(currentUser.getDingCorpId());
        redisUtilPool.setString(RedisConstant.REFRESH_TOKEN_PREFIX + refreshToken,JSON.toJSONString(refreshUser), Constants.REFRESH_TOKEN_EXPIRE);
        UserLoginVO userLogin = new UserLoginVO();
        userLogin.setEnterpriseList(enterpriseList);
        userLogin.setCurrentEnterprise(currentEnterprise);
        userLogin.setAccessToken(accessToken);
        userLogin.setRefreshToken(refreshToken);
        userLogin.setUserInfo(userBaseInfo);
        userLogin.setCurrentUser(currentUser);
        return userLogin;
    }

    private CurrentUser getCurrentUser(UserLoginDO userInfo, String token, UserBaseInfoVO userBaseInfo, UserLoginEnterpriseVO enterprise){
        long startTime = System.currentTimeMillis();
        CurrentUser currentUser = new CurrentUser();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterprise.getEnterpriseId());
        log.info("@1=======>{}", System.currentTimeMillis() -startTime);
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterprise.getEnterpriseId());
        log.info("@2=======>{}", System.currentTimeMillis() -startTime);
        // 切到企业库
        DynamicDataSourceContextHolder.setDataSourceType(enterpriseConfig.getDbName());
        // 查企业用户
        EnterpriseUserDO enterpriseUser = null;
        if(AIEnum.AI_USERID.getCode().equals(userInfo.getUserId())){
            enterpriseUser = AIUserTool.getAiUser();
        }else{
            enterpriseUser = enterpriseUserService.selectByUnionid(enterprise.getEnterpriseId(), userInfo.getUnionid());
        }
        log.info("@3=======>{}", System.currentTimeMillis() -startTime);
        if(Objects.isNull(enterpriseUser)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "暂无权限，请联系管理员！");
        }
        //等待审核
        if(UserStatusEnum.WAIT_AUDIT.getCode().equals(enterpriseUser.getUserStatus())){
            throw new ServiceException(ErrorCodeEnum.USER_ACCOUNT_WAIT_AUDIT);
        }
        userInfo.setEnterpriseUserMobile(enterpriseUser.getMobile());
        //账号冻结
        if(UserStatusEnum.FREEZE.getCode().equals(enterpriseUser.getUserStatus())){
            throw new ServiceException(ErrorCodeEnum.USER_FREEZE);
        }
        SysRoleDO sysRoleDoByUserId = null;
        if(AIEnum.AI_USERID.getCode().equals(userInfo.getUserId())){
            sysRoleDoByUserId = sysRoleMapper.getRoleByRoleEnum(enterprise.getEnterpriseId(), Role.MASTER.getRoleEnum());
        }else{
            sysRoleDoByUserId = sysRoleMapper.getHighestPrioritySysRoleDoByUserId(enterprise.getEnterpriseId(), enterpriseUser.getUserId());
        }
        if(sysRoleDoByUserId==null){
            // 如果没有最高优先级的，给未分配的角色
            sysRoleDoByUserId = sysRoleMapper.getRoleByRoleEnum(enterprise.getEnterpriseId(), Role.EMPLOYEE.getRoleEnum());
        }
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

    public List<UserLoginEnterpriseVO> getEnterpriseList(List<String> enterpriseIds) {
        DataSourceHelper.reset();
        List<EnterpriseDO> enterpriseList = enterpriseService.getEnterpriseByIds(enterpriseIds);
        if(CollectionUtils.isEmpty(enterpriseList)){
            return Lists.newLinkedList();
        }
        List<EnterpriseConfigDO> enterpriseConfig = enterpriseConfigService.selectByEnterpriseIds(enterpriseIds);
        Map<String, EnterpriseConfigDO> enterpriseConfigMap = enterpriseConfig.stream().collect(Collectors.toMap(EnterpriseConfigDO::getEnterpriseId, Function.identity(), (k1, k2) -> k2));
        List<UserLoginEnterpriseVO> resultList = new ArrayList<>();
        for (EnterpriseDO enterprise : enterpriseList) {
            EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMap.get(enterprise.getId());
            String dingCorpId = Optional.ofNullable(enterpriseConfigDO).map(EnterpriseConfigDO::getDingCorpId).orElse("");
            resultList.add(new UserLoginEnterpriseVO(enterprise.getLogo(), enterprise.getId(), enterprise.getName(), enterprise.getOriginalName(), enterprise.getIsVip(), dingCorpId));
        }
        return resultList;
    }

    public String getToken() {
        RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
        return randomNumberGenerator.nextBytes().toHex();
    }

}
