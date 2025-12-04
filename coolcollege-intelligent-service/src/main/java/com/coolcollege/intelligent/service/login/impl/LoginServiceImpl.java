package com.coolcollege.intelligent.service.login.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.user.UserStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpHelper;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.sync.vo.AddressBookChangeReqBody;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.login.LoginRecordMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiGetUserAccessTokenDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.UserAccessTokenVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.login.YNUserLoginDTO;
import com.coolcollege.intelligent.model.login.request.AskBotLoginRequest;
import com.coolcollege.intelligent.model.login.request.MclzLoginRequest;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.RefreshUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.enterprise.*;
import com.coolcollege.intelligent.service.login.LoginService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.qywx.WeComService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.LoginUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * @ClassName LoginServiceImpl
 * @Description 用一句话描述什么
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Autowired
    private RedisUtilPool redis;

    @Autowired
    private DingService dingService;

    @Autowired
    private SysDepartmentService sysDepartmentService;

    @Resource
    private LoginRecordMapper loginRecordMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private WeComService weComService;

    @Autowired
    private SysRoleService sysRoleService;
    @Resource
    private EnterpriseInitConfigApiService configApiService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private StoreService storeService;
    @Resource
    private LoginUtil loginUtil;
    @Resource
    private ChatService chatService;

    @Value("${isv.url}")
    private String isv_url;

    /**
     * 明厨亮灶appid/secret
     */
    @Value("${mclz.applet.appid}")
    private String mclzAppletAppid;
    @Value("${mclz.applet.secret}")
    private String mclzAppletSecret;

    /**
     * 果然单点登录appid/secret
     */
    @Value("${askBot.appId}")
    private String askBotAppId;
    @Value("${askBot.appSecret}")
    private String askBotAppSecret;


    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    @Override
    public Object isvLogin(String userId, String corpId, Boolean needRefreshToken,
                           String appType, String avatar) {
        log.info("isvLogin, corpId={}, userId={}, appType={}", corpId, userId, appType);
        DynamicDataSourceContextHolder.clearDataSourceType();
        if (StringUtils.isEmpty(userId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "用户不存在");
        }
        if (StringUtils.isEmpty(corpId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "非法的企业信息");
        }

        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(corpId, appType);

        return checkEnterprise(userId, needRefreshToken, appType, enterpriseConfigDO, avatar);
    }

    @Override
    public Object refreshLogin(String userId, String eid, HttpServletRequest request, String loginType, Boolean needRefreshToken, String appType, String loginWay) {
        log.info("isvLogin, eid={}, userId={}", eid, userId);
        DynamicDataSourceContextHolder.clearDataSourceType();
        if (StringUtils.isEmpty(userId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "用户不存在");
        }
        if (StringUtils.isEmpty(eid)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "非法的企业信息");
        }

        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(eid);

        return checkEnterprise(userId, needRefreshToken, enterpriseConfigDO.getAppType(), enterpriseConfigDO, StringUtils.EMPTY);
    }

    private Object checkEnterprise(String userId, Boolean needRefreshToken,
                                   String appType, EnterpriseConfigDO enterpriseConfigDO, String avatar) {
        if (enterpriseConfigDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业正在初始化，请稍后访问！");
        }
        appType = StrUtil.isEmpty(appType) ? AppTypeEnum.DING_DING.getValue() : appType;

        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseConfigDO.getEnterpriseId());

        if (enterpriseDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业正在初始化，请稍后访问！");
        }

        if (enterpriseDO.getStatus() == Constants.STATUS.INITIAL) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "系统正在同步初始化，请稍后访问!");
        }
        return enterpriseUserLogin(userId, enterpriseDO, enterpriseConfigDO,needRefreshToken, appType, avatar);
    }

    private Object enterpriseUserLogin(String userId, EnterpriseDO enterprise, EnterpriseConfigDO enterpriseConfig,Boolean needRefreshToken, String appType, String avatar) {
        CurrentUser currentUser = new CurrentUser();
        RefreshUser refreshUser = new RefreshUser();
        String dbName = enterpriseConfig.getDbName();
        // 切到企业库
        DynamicDataSourceContextHolder.setDataSourceType(dbName);
        // 查企业用户
        EnterpriseUserDO enterpriseUser = enterpriseUserDao.selectByUserId(enterprise.getId(), userId);
        //
        SysRoleDO sysRoleDoByUserId = sysRoleMapper.getHighestPrioritySysRoleDoByUserId(enterprise.getId(), userId);
        if(sysRoleDoByUserId==null){
            // 如果没有最高优先级的，给未分配的角色
            sysRoleDoByUserId = sysRoleService.getRoleByRoleEnum(enterprise.getId(), Role.EMPLOYEE.getRoleEnum());
            // throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "未配置职位信息，无法登录！");
        }
        if(enterpriseUser == null){
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType)) {
                // 门店通用户没有在任何部门下，单独同步此用户信息
                AddressBookChangeReqBody reqBody = new AddressBookChangeReqBody();
                reqBody.setAppType(enterpriseConfig.getAppType());
                reqBody.setCorpId(enterpriseConfig.getDingCorpId());
                reqBody.setUserId(userId);
                simpleMessageService.send(JSONObject.toJSONString(reqBody), RocketMqTagEnum.DING_SINGLE_USER_SYNC);
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "用户信息更新，请稍后重试！");
            }
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "暂无权限，请联系管理员！");
        }
        if(UserStatusEnum.FREEZE.getCode().equals(enterpriseUser.getUserStatus())){
            throw new ServiceException(ErrorCodeEnum.USER_FREEZE);
        }
        if(UserStatusEnum.WAIT_AUDIT.getCode().equals(enterpriseUser.getUserStatus())){
            throw new ServiceException(ErrorCodeEnum.USER_WAIT_AUIT);
        }
        Integer limitStoreCount = storeService.getLimitStoreCount(enterprise.getId());
        Integer storeCount = storeService.countAllStore(enterprise.getId());
        if(limitStoreCount < storeCount && !sysRoleService.checkIsAdmin(enterprise.getId(), userId)){
            //非管理员 门店数量超过
            throw new ServiceException(ErrorCodeEnum.LOGIN_STORE_COUNT_ERROR);
        }
        //用户是否已有头像
        Boolean hasAvatar = StringUtils.isNotEmpty(enterpriseUser.getAvatar()) && !enterpriseUser.getAvatar().contains(Constants.DEFAULT_AVATAR);
        String finalAvatar = hasAvatar ? enterpriseUser.getAvatar() : avatar;
        enterpriseUser.setAvatar(finalAvatar);
        enterpriseUser.setFaceUrl(finalAvatar);
        if (!hasAvatar) {
            enterpriseUserDao.updateEnterpriseUser(enterprise.getId(), enterpriseUser);
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
        currentUser.setGroupCorpId(enterprise.getGroupCropId());
        currentUser.setUnionid(enterpriseUser.getUnionid());
        //refreshToken信息
        refreshUser.setUserId(enterpriseUser.getUserId());
        refreshUser.setCorpId(enterpriseConfig.getDingCorpId());
        refreshUser.setEid(enterpriseConfig.getEnterpriseId());
        //添加MainCorpId字段信息
        if(StringUtils.isNotBlank(enterpriseConfig.getMainCorpId())){
            currentUser.setMainCorpId(enterpriseConfig.getMainCorpId());
        }

        //设置当前登录人使用的企业相关信息
        currentUser.setRoleAuth(sysRoleDoByUserId.getRoleAuth());
        currentUser.setSysRoleDO(sysRoleDoByUserId);
        currentUser.setEnterpriseId(enterprise.getId());
        currentUser.setEnterpriseName(enterprise.getName());
        currentUser.setLogoName(enterprise.getLogoName());
        currentUser.setEnterpriseLogo(enterprise.getCorpLogoUrl());
        currentUser.setIsVip(enterprise.getIsVip());
        currentUser.setActive(enterpriseUser.getActive());
        // 企业开通时间
        currentUser.setOpenTime(enterprise.getCreateTime());
        //生成令牌
        RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

        String token = randomNumberGenerator.nextBytes().toHex();
        String refreshToken = randomNumberGenerator.nextBytes().toHex();
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
        currentUser.setMobile(enterpriseUser.getMobile());
        if(StringUtils.isNotBlank(enterpriseUser.getUnionid()) && StringUtils.isBlank(enterpriseUser.getMobile())){
            DataSourceHelper.reset();
            EnterpriseUserDO enterpriseConfigUser = enterpriseUserService.selectConfigUserByUnionid(enterpriseUser.getUnionid());;
            String configUserMobile = Optional.ofNullable(enterpriseConfigUser).map(o->o.getMobile()).orElse(enterpriseUser.getMobile());
            currentUser.setMobile(configUserMobile);
        }
        currentUser.setName(enterpriseUser.getName());

        currentUser.setDingCorpId(enterpriseConfig.getDingCorpId());

        currentUser.setAccessToken(token);
        currentUser.setAppType(appType);
        //登陆信息中添加企业授权类型
        currentUser.setLicenseType(enterpriseConfig.getLicenseType());

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("action_token", token);
        jsonObject.put("user", currentUser);
        jsonObject.put("isNeedImproveUserInfo", enterpriseUserService.getUserIsNeedImproveUserInfo(enterpriseUser.getUnionid(), enterpriseUser.getMobile(), enterprise.getId()));
        redis.setString(ACCESS_TOKEN_KEY + ":" + token, JSON.toJSONString(currentUser), Constants.ACTION_TOKEN_EXPIRE);
        jsonObject.put("expire", Constants.ACTION_TOKEN_EXPIRE);

        // 缓存用户id和token的关系，用于用户管理界面清空用户的token
        loginUtil.saveTokenUserIdAndRoleId(enterprise.getId(), userId, String.valueOf(currentUser.getSysRoleDO().getId()), token);
        //冻结登录  超登用户过滤
        if (enterprise.getStatus() == Constants.STATUS.FREEZE&&!StringUtils.equals(enterpriseUser.getUserId(), AIEnum.AI_USERID.getCode())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业被冻结");
        }
        try {
            //记录登陆的次数
            DataSourceHelper.changeToSpecificDataSource(dbName);
            loginRecordMapper.insertLoginRecording(currentUser.getEnterpriseId(),currentUser.getUserId(),System.currentTimeMillis());
        }catch (Exception e){
            log.error("insertLoginRecording Error,eid:{},userId:{}",currentUser.getEnterpriseId(),currentUser.getUserId(),e);
        }
        if(needRefreshToken){
            redis.setString(REFRESH_TOKEN_KEY+":"+refreshToken,JSON.toJSONString(refreshUser),Constants.REFRESH_TOKEN_EXPIRE);
            jsonObject.put("refresh_token",refreshToken);
        }
        log.info("[" + enterpriseUser.getName() + "; action_token："+ token + "; userId：" + currentUser.getUserId()  +"]登入系统成功");
        return jsonObject;
    }

    @Override
    public Object yuNiLogin(YNUserLoginDTO param) {
        String enterpriseId = param.getEnterpriseId();
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        EnterpriseDO enterprise = enterpriseService.selectById(enterpriseId);
        String dbName = enterpriseConfig.getDbName();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        Integer userType = param.getUserType();
        EnterpriseUserDO enterpriseUser = null;
        if(Constants.INDEX_ONE.equals(userType)){
            enterpriseUser = enterpriseUserDao.getUserInfoByMobile(param.getEnterpriseId(), param.getUserName());
        }
        if(Constants.INDEX_TWO.equals(userType)){
            enterpriseUser = enterpriseUserDao.selectByUserId(param.getEnterpriseId(), param.getUserName());
        }
        if(Objects.isNull(enterpriseUser)){
            throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
        }
        return enterpriseUserLogin(enterpriseId, enterpriseUser, enterpriseConfig, enterprise, Boolean.TRUE);
    }

    @Override
    public UserAccessTokenVO getUserAccessToken(String enterpriseId, OpenApiGetUserAccessTokenDTO param) {
        String cacheKeyPrefix = "getUserAccessToken:{0}";
        if(StringUtils.isBlank(enterpriseId)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(StringUtils.isBlank(param.getUserAccount())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        EnterpriseDO enterprise = enterpriseService.selectById(enterpriseId);
        if(Objects.isNull(enterprise)){
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        String dbName = enterpriseConfig.getDbName();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        Integer accountType = Objects.isNull(param.getAccountType()) ? 1 : param.getAccountType();
        EnterpriseUserDO enterpriseUser = null;
        if(Constants.INDEX_ONE.equals(accountType)){
            enterpriseUser = enterpriseUserDao.getUserInfoByMobile(enterpriseId, param.getUserAccount());
        }
        if(Constants.INDEX_TWO.equals(accountType)){
            enterpriseUser = enterpriseUserDao.selectByUserId(enterpriseId, param.getUserAccount());
        }
        if(Constants.INDEX_THREE.equals(accountType)){
            enterpriseUser = enterpriseUserDao.selectByThirdOaUniqueFlag(enterpriseId, param.getUserAccount());
        }
        if(Objects.isNull(enterpriseUser) || !enterpriseUser.getActive()){
            throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
        }
        String cacheKey = MessageFormat.format(cacheKeyPrefix, enterpriseUser.getUserId());
        String accessTokenCache = redis.getString(cacheKey);
        if(StringUtils.isNotBlank(accessTokenCache)){
            Long expire = redis.getExpire(ACCESS_TOKEN_KEY + ":" + accessTokenCache);
            return new UserAccessTokenVO(accessTokenCache, expire);
        }
        JSONObject result = enterpriseUserLogin(enterpriseId, enterpriseUser, enterpriseConfig, enterprise, Boolean.TRUE);
        String accessToken = result.getString("accessToken");
        Long expireTime = result.getLong("expire");
        //提前半小时过期
        redis.setString(cacheKey, accessToken, (int)(expireTime - 30*60));
        return new UserAccessTokenVO(accessToken, expireTime);
    }

    private JSONObject enterpriseUserLogin(String enterpriseId, EnterpriseUserDO enterpriseUser, EnterpriseConfigDO enterpriseConfig, EnterpriseDO enterprise, Boolean needRefreshToken) {
        CurrentUser currentUser = new CurrentUser();
        RefreshUser refreshUser = new RefreshUser();
        String dbName = enterpriseConfig.getDbName();
        String userId = enterpriseUser.getUserId();
        // 切到企业库
        DynamicDataSourceContextHolder.setDataSourceType(dbName);
        //
        SysRoleDO sysRoleDoByUserId = sysRoleMapper.getHighestPrioritySysRoleDoByUserId(enterpriseId, userId);
        if(sysRoleDoByUserId==null){
            // 如果没有最高优先级的，给未分配的角色
            sysRoleDoByUserId = sysRoleService.getRoleByRoleEnum(enterpriseId, Role.EMPLOYEE.getRoleEnum());
        }
        if(UserStatusEnum.FREEZE.getCode().equals(enterpriseUser.getUserStatus())){
            throw new ServiceException(ErrorCodeEnum.USER_FREEZE);
        }
        if(UserStatusEnum.WAIT_AUDIT.getCode().equals(enterpriseUser.getUserStatus())){
            throw new ServiceException(ErrorCodeEnum.USER_WAIT_AUIT);
        }
        Integer limitStoreCount = storeService.getLimitStoreCount(enterpriseId);
        Integer storeCount = storeService.countAllStore(enterpriseId);
        if(limitStoreCount < storeCount && !sysRoleService.checkIsAdmin(enterpriseId, userId)){
            //非管理员 门店数量超过
            throw new ServiceException(ErrorCodeEnum.LOGIN_STORE_COUNT_ERROR);
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
        currentUser.setUnionid(enterpriseUser.getUnionid());
        //refreshToken信息
        refreshUser.setUserId(enterpriseUser.getUserId());
        refreshUser.setCorpId(enterpriseConfig.getDingCorpId());
        refreshUser.setEid(enterpriseConfig.getEnterpriseId());
        //添加MainCorpId字段信息
        if(StringUtils.isNotBlank(enterpriseConfig.getMainCorpId())){
            currentUser.setMainCorpId(enterpriseConfig.getMainCorpId());
        }

        //设置当前登录人使用的企业相关信息
        currentUser.setRoleAuth(sysRoleDoByUserId.getRoleAuth());
        currentUser.setSysRoleDO(sysRoleDoByUserId);
        currentUser.setEnterpriseId(enterpriseId);
        currentUser.setEnterpriseName(enterprise.getName());
        currentUser.setLogoName(enterprise.getLogoName());
        currentUser.setEnterpriseLogo(enterprise.getCorpLogoUrl());
        currentUser.setIsVip(enterprise.getIsVip());
        currentUser.setActive(enterpriseUser.getActive());
        // 企业开通时间
        currentUser.setOpenTime(enterprise.getCreateTime());
        //生成令牌
        RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();

        String token = randomNumberGenerator.nextBytes().toHex();
        String refreshToken = randomNumberGenerator.nextBytes().toHex();
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
        currentUser.setMobile(enterpriseUser.getMobile());
        if(StringUtils.isNotBlank(enterpriseUser.getUnionid()) && StringUtils.isBlank(enterpriseUser.getMobile())){
            DataSourceHelper.reset();
            EnterpriseUserDO enterpriseConfigUser = enterpriseUserService.selectConfigUserByUnionid(enterpriseUser.getUnionid());;
            String configUserMobile = Optional.ofNullable(enterpriseConfigUser).map(o->o.getMobile()).orElse(enterpriseUser.getMobile());
            currentUser.setMobile(configUserMobile);
        }
        currentUser.setName(enterpriseUser.getName());

        currentUser.setDingCorpId(enterpriseConfig.getDingCorpId());

        currentUser.setAccessToken(token);
        currentUser.setAppType(enterpriseConfig.getAppType());
        //登陆信息中添加企业授权类型
        currentUser.setLicenseType(enterpriseConfig.getLicenseType());

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("action_token", token);
        jsonObject.put("accessToken", token);
        jsonObject.put("user", currentUser);
        jsonObject.put("isNeedImproveUserInfo", enterpriseUserService.getUserIsNeedImproveUserInfo(enterpriseUser.getUnionid(), enterpriseUser.getMobile(), enterprise.getId()));
        redis.setString(ACCESS_TOKEN_KEY + ":" + token, JSON.toJSONString(currentUser), Constants.ACTION_TOKEN_EXPIRE);
        jsonObject.put("expire", Constants.ACTION_TOKEN_EXPIRE);
        // 缓存用户id和token的关系，用于用户管理界面清空用户的token
        loginUtil.saveTokenUserIdAndRoleId(enterprise.getId(), userId, String.valueOf(currentUser.getSysRoleDO().getId()), token);
        //冻结登录  超登用户过滤
        if (enterprise.getStatus() == Constants.STATUS.FREEZE && !StringUtils.equals(enterpriseUser.getUserId(), AIEnum.AI_USERID.getCode())) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业被冻结");
        }
        try {
            //记录登陆的次数
            DataSourceHelper.changeToSpecificDataSource(dbName);
            loginRecordMapper.insertLoginRecording(currentUser.getEnterpriseId(),currentUser.getUserId(),System.currentTimeMillis());
        }catch (Exception e){
            log.error("insertLoginRecording Error,eid:{},userId:{}",currentUser.getEnterpriseId(),currentUser.getUserId(),e);
        }
        if(needRefreshToken){
            redis.setString(REFRESH_TOKEN_KEY+":"+refreshToken,JSON.toJSONString(refreshUser),Constants.REFRESH_TOKEN_EXPIRE);
            jsonObject.put("refresh_token",refreshToken);
        }
        log.info("[" + enterpriseUser.getName() + "; action_token："+ token + "; userId：" + currentUser.getUserId()  +"]登入系统成功");
        return jsonObject;
    }

    /**
     * 企业微信ISV免登
     *
     * @param code
     * @param request
     * @return
     */
    @Override
    public Object wxIsvLogin(String code, String corpId, String appType, HttpServletRequest request, String loginWay) {
        if(AppTypeEnum.isWxSelfAndPrivateType(appType)) {
            return this.wxIsvLoginForSelf(corpId, appType, code, loginWay, request);
        }
        String url = isv_url + "/qywxisv/suite_access_tokens/get";
        if (StringUtils.isNotBlank(appType)) {
            url = url + "?appType=" + appType;
        }

        JSONObject suiteToken = HttpHelper.httpGetCrm(url);

        String suite_access_token = suiteToken.getString("suite_access_token");
        JSONObject responseDetail = getUserInfo3rd(suite_access_token, code);
        log.info("wxIsvLogin responseDetail : {}", responseDetail.toString());
        int errcode = Integer.parseInt(responseDetail.get("errcode").toString());
        String errmsg = responseDetail.getString("errmsg");
        String avatar = responseDetail.getString("avatar");

        if (errcode != Constants.DING_ERR_CODE_SUCCESS) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业微信授权登录失败");
        }
        corpId = responseDetail.getString("corpid");
        String userId = responseDetail.getString("userid");
        String openUserid = responseDetail.getString("open_userid");
        userId = corpId + "_" + userId;
        weComService.initFirstUser(userId, corpId, openUserid, appType, null);
        return isvLogin(userId, corpId, Boolean.FALSE, appType, avatar);
    }

    /**
     * 企微自建应用/私部应用isv登录
     * @param corpId
     * @param appType
     * @param code
     * @param loginWay
     * @param request
     * @return
     */
    public Object wxIsvLoginForSelf(String corpId, String appType, String code, String loginWay, HttpServletRequest request) {
        EnterpriseUserDTO enterpriseUserDTO = null;
        try {
            enterpriseUserDTO = configApiService.getUserDetailByCode(corpId, appType, code);
        } catch (ApiException e) {
            log.error("wxIsvLoginForSelf error", e);
            throw new ServiceException(ErrorCodeEnum.QW_SERVICE_EXCEPTION);
        }
        if(Objects.isNull(enterpriseUserDTO)) {
            throw new ServiceException(ErrorCodeEnum.USER_INFO_ERROR);
        }
        weComService.initFirstUser(enterpriseUserDTO.getUserId(), corpId, enterpriseUserDTO.getOpenUserid(), appType, enterpriseUserDTO.getName());
        return isvLogin(enterpriseUserDTO.getUserId(), corpId, Boolean.FALSE, appType, enterpriseUserDTO.getAvatar());
    }

    /**
     * 获取访问用户userid
     *
     * 请求地址：https://qyapi.weixin.qq.com/cgi-bin/service/getuserinfo3rd?suite_access_token=SUITE_ACCESS_TOKEN&code=CODE
     * 请求方式： GET
     * @parm1 SUITE_ACCESS_TOKEN
     * @parm2 oauth2授权成功返回的code
     */
    public JSONObject getUserInfo3rd(String suite_access_token, String code) {

        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/getuserinfo3rd?suite_access_token="+suite_access_token+"&code="+code;

        JSONObject jsonObj = HttpHelper.httpGet(url);
        log.info("getUserInfo3rd : {}", jsonObj.toJSONString());
        JSONObject result = getUserDetail3rd(suite_access_token, jsonObj.getString("user_ticket"));
        int errcode = Integer.parseInt(result.get("errcode").toString());
        if (errcode != Constants.DING_ERR_CODE_SUCCESS) {
            //如果返回结果报错,直接使用这里的用户信息.信息只使用到userid, corpid, open_userid
            jsonObj.put("corpid", jsonObj.getString("CorpId"));
            jsonObj.put("userid", jsonObj.getString("UserId"));
            return jsonObj;
        }

        return  result;
    }

    /**
     * 获取访问用户敏感信息  https://work.weixin.qq.com/api/doc/90001/90143/91122
     *
     * 请求地址：https://qyapi.weixin.qq.com/cgi-bin/service/getuserdetail3rd?suite_access_token=SUITE_ACCESS_TOKEN
     * 请求方式： POST
     * @parm1 SUITE_ACCESS_TOKEN
     * @parm2 getUserInfo3rd获得的user_ticket
     */
    public JSONObject getUserDetail3rd(String suite_access_token, String user_ticket) {
        log.info("getUserDetail3rd  suite_access_token: {}, user_ticket : {}", suite_access_token, user_ticket);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/service/getuserdetail3rd?suite_access_token="+suite_access_token;
        JSONObject jsonParms = new JSONObject();
        jsonParms.put("user_ticket", user_ticket);
        JSONObject jsonObj = HttpHelper.post(url,jsonParms);
        log.info("getUserDetail3rdresponse : {}", jsonObj.toJSONString());
        return jsonObj;
    }

    /**
     * 企业微信ISV免登
     *
     * @param authCode
     * @param appType
     * @param request
     * @return
     */
    @Override
    public Object wxQrcodeLogin(String authCode, String corpId, String appType, HttpServletRequest request,String loginWay) {
        AppTypeEnum appTypeEnum = AppTypeEnum.getAppType(appType);
        if(AppTypeEnum.isWxSelfAndPrivateType(appType)) {
            return this.wxIsvLoginForSelf(corpId, appType, authCode, loginWay, request);
        }
        log.info("微信扫码登录authCode:{}", authCode);
        String url = isv_url + "/qywxisv/provider-access-tokens/get";
        if (StringUtils.isNotBlank(appType)) {
            url = url + "?appType=" + appType;
        }
        //获取用户信息
        JSONObject tokenJson = HttpHelper.httpGetCrm(url);
        String providerAccessToken = tokenJson.getString("provider_access_token");
        log.info("微信扫码登录providerAccessToken:{}", providerAccessToken);
        String userUrl = "https://qyapi.weixin.qq.com/cgi-bin/service/get_login_info";
        HashMap<String, String> userParam = new HashMap<>();
        userParam.put("access_token", providerAccessToken);
        userParam.put("auth_code", authCode);
        userUrl = userUrl + "?access_token=" + providerAccessToken;
        JSONObject userInfo = HttpHelper.post(userUrl, userParam);
        log.info("微信扫码登录userInfo:{}", userInfo.toString());
        if (org.springframework.util.StringUtils.isEmpty(userInfo.getString("errcode")) || "0".equals(userInfo.getString("errcode"))) {
            //执行登录方法7
            corpId = userInfo.getJSONObject("corp_info").getString("corpid");
            String userId = userInfo.getJSONObject("user_info").getString("userid");
            userId = corpId + "_" + userId;
            return isvLogin(userId, corpId, Boolean.FALSE, appType, StringUtils.EMPTY);
        } else {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "扫码登录失败");
        }
    }

    @Override
    public Boolean mclzRegisteredVerify(MclzLoginRequest request) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(request.getEnterpriseId());
        if(Objects.isNull(enterpriseConfig)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());

        JSONObject userInfo = chatService.getAppletUserInfo(request.getCode(), mclzAppletAppid, mclzAppletSecret);
        String openId = userInfo.getString("openid");
        EnterpriseUserDO entUser = enterpriseUserDao.selectByUserIdIgnoreActive(request.getEnterpriseId(), openId);
        return Objects.nonNull(entUser);
    }

    @Override
    public Object mclzLogin(MclzLoginRequest request) {
        String enterpriseId = request.getEnterpriseId();
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());

        String accessToken = chatService.getAppletToken(enterpriseId, mclzAppletAppid, mclzAppletSecret, false);
        JSONObject userInfo = chatService.getAppletUserInfo(request.getCode(), mclzAppletAppid, mclzAppletSecret);
        String mobile = null;
        if (StringUtils.isNotBlank(request.getMobileCode())) {
            try {
                mobile = chatService.getAppletMobile(request.getMobileCode(), accessToken);
            } catch (ServiceException e) {
                if (e.getErrorCode().equals(ErrorCodeEnum.APPLET_TOKEN_EXPIRE.getCode())) {
                    accessToken = chatService.getAppletToken(enterpriseId, mclzAppletAppid, mclzAppletSecret, true);
                    mobile = chatService.getAppletMobile(request.getMobileCode(), accessToken);
                } else {
                    throw e;
                }
            }
        }
//        String unionId = userInfo.getString("unionid");
        String openId = userInfo.getString("openid");

        // 初始化用户及配置店长职位
        weComService.initFirstUser(enterpriseId, openId, openId, request.getAppType(), openId, mobile, Role.SHOPOWNER.getId());
        DataSourceHelper.reset();
        return checkEnterprise(openId, Boolean.FALSE, request.getAppType(), enterpriseConfig, null);
    }

    @Override
    public String askBotLogin(AskBotLoginRequest request) {
        String enterpriseId = request.getEnterpriseId();
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        String accessToken = chatService.getAskBotLoginToken(request.getUserId(), request.getEnterpriseId(), enterpriseDO.getName(),
                askBotAppId, askBotAppSecret);
        return accessToken;
    }

}
