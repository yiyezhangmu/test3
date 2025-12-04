package com.coolcollege.intelligent.service.login.impl.strategy;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.user.UserTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.login.LoginRecordMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.login.UserLoginDO;
import com.coolcollege.intelligent.model.login.UserLoginDTO;
import com.coolcollege.intelligent.model.login.vo.UserBaseInfoVO;
import com.coolcollege.intelligent.model.login.vo.UserLoginEnterpriseVO;
import com.coolcollege.intelligent.model.login.vo.UserLoginVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.RefreshUser;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.login.impl.LoginBaseService;
import com.coolcollege.intelligent.util.LoginUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: PasswordLoginService
 * @Description: 账号密码登录实现类
 * @date 2021-07-16 11:45
 */
@Service("switchLoginService")
@Slf4j
public class SwitchLoginService extends LoginBaseService {

    @Resource
    private RedisUtilPool redisUtilPool;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private LoginRecordMapper loginRecordMapper;

    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;
    @Autowired
    private LoginUtil loginUtil;

    @Override
    public ResponseResult login(UserLoginDTO param) {
        log.info("login:{}", param.toString());
        List<UserLoginDO> allUsers = getAllUserByUserId(param.getEnterpriseId(), param.getUserId());
        if (CollectionUtils.isEmpty(allUsers)) {
            return ResponseResult.fail(ErrorCodeEnum.ACCOUNT_NOT_EXIST);
        }
        return userLogin(param, allUsers);
    }

    /**
     * 根据手机号获取所有用户
     *
     * @param userId
     * @return
     */
    public List<UserLoginDO> getAllUserByUserId(String eid, String userId) {
        EnterpriseUserDO userDO = enterpriseUserDao.selectByUserId(eid, userId);
        List<UserLoginDO> resultList = new ArrayList<>();
        if (userDO != null) {
            resultList.add(new UserLoginDO(userDO.getId(), userDO.getUserId(), userDO.getMobile(), userDO.getPassword(), userDO.getUnionid(), userDO.getAppType()));
        }
        return resultList;
    }

    @Override
    public ResponseResult userLogin(UserLoginDTO param, List<UserLoginDO> allUsers) {
        if (StringUtils.isBlank(param.getUserId())) {
            return ResponseResult.fail(ErrorCodeEnum.PARAMS_INVALID_ERROR);
        }
        //获取密码相同的用户所在的企业
        return ResponseResult.success(this.getUserLoginVO(allUsers, param.getEnterpriseId()));
    }


    /**
     * 获取登录用户信息
     * @param userList
     * @param loginEnterpriseId
     * @return
     */
    @Override
    public UserLoginVO getUserLoginVO(List<UserLoginDO> userList, String loginEnterpriseId) {
        long startTime = System.currentTimeMillis();
        Map<String, UserLoginDO> userInfoMap = userList.stream().collect(Collectors.toMap(UserLoginDO::getId, Function.identity(), (k1, k2) -> k1));
        List<String> userIds = userList.stream().map(UserLoginDO::getId).distinct().collect(Collectors.toList());
        List<String> unionIds = userList.stream().map(UserLoginDO::getUnionid).collect(Collectors.toList());
        Map<String, String> enterpriseIdUserIdMap = new HashMap<>();
        enterpriseIdUserIdMap.put(loginEnterpriseId, userIds.get(0));
        log.info("@1---->{}", System.currentTimeMillis() -startTime);
        List<String> enterpriseIds = enterpriseIdUserIdMap.keySet().stream().collect(Collectors.toList());
        List<UserLoginEnterpriseVO> enterpriseList = getEnterpriseList(enterpriseIds);
        log.info("@2---->{}", System.currentTimeMillis() -startTime);
        if(CollectionUtils.isEmpty(enterpriseList)){
            throw new ServiceException(ErrorCodeEnum.USER_NOT_JOIN_ENTERPRISE);
        }
        Map<String, UserLoginEnterpriseVO> enterpriseMap = enterpriseList.stream().collect(Collectors.toMap(k->k.getEnterpriseId(), Function.identity()));

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

}
