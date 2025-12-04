package com.coolcollege.intelligent.controller.login;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.AIEnum;
import com.coolcollege.intelligent.common.enums.DingLoginErrorEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.UrlUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.login.LoginRecordMapper;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseLoginDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.login.YNUserLoginDTO;
import com.coolcollege.intelligent.model.login.request.AskBotLoginRequest;
import com.coolcollege.intelligent.model.login.request.FeiShuLoginDTO;
import com.coolcollege.intelligent.model.login.request.MclzLoginRequest;
import com.coolcollege.intelligent.model.login.request.QwLoginRequest;
import com.coolcollege.intelligent.model.login.vo.UserLoginVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.RefreshUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.boss.BossUserService;
import com.coolcollege.intelligent.service.login.LoginService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolcollege.intelligent.common.util.MD5Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * @ClassName LoginController
 * @Description 用一句话描述什么
 */
@Api(tags = "登录相关接口")
@RestController
@BaseResponse
@Slf4j
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${ding.token.userId}")
    private String ding_token_userId;

    @Autowired
    private LoginService loginService;

    @Resource
    private EnterpriseMapper enterpriseMapper;

    @Resource
    private LoginRecordMapper loginRecordMapper;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Autowired
    private BossUserService bossUserService;

    @Resource
    private RedisUtilPool redis;

    private final String REFRESH_TOKEN_KEY = "refresh_token";


    @PostMapping(value = "/v2/isvLogin")
    public Object isvLoginV2(@RequestParam(value = "appType", required = false) String appType,
                             @RequestBody(required = false) JSONObject jsonObject, HttpServletRequest request) {
        log.info("isvLoginV2 data={} ,appType:{}", jsonObject.toJSONString(), appType);
        String code = jsonObject.getString("code");
        String corpId = jsonObject.getString("corpId");

        String userId = "";
        try {
            String value = "corpId=" + corpId + "&code=" + code;
            if (StringUtils.isNotBlank(appType)) {
                value = value + "&appType=" + appType;
            }
            log.info("url:{}", ding_token_userId + value);
            JSONObject userInfo = JSON.parseObject(HttpRequest.sendGet(ding_token_userId, value));
            userId = userInfo.getString("user_id");
            String errorCode = userInfo.getString("error_code");
            if (StringUtils.isBlank(userId) && StringUtils.isNotBlank(errorCode)) {
                DingLoginErrorEnum dingLoginErrorEnum = DingLoginErrorEnum.getByCode(Integer.getInteger(errorCode));
                if (dingLoginErrorEnum != null) {
                    throw new ServiceException(dingLoginErrorEnum.getCode(), dingLoginErrorEnum.getMsg());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof ServiceException) {
                throw e;
            } else {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "用户不存在");
            }
        }

        try {
            return loginService.isvLogin(userId, corpId, Boolean.TRUE, appType, StringUtils.EMPTY);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "登陆失败");
        }
    }

    @PostMapping(value = "/v3/feiShuLogin")
    public Object feiShuLogin(@RequestBody FeiShuLoginDTO param) {
        log.info("isvLoginV2 data={}", JSONObject.toJSONString(param));
        String code = param.getCode();
        String appId = param.getAppId();

        String userId = "", corpId = "", appType = AppTypeEnum.FEI_SHU.getValue();
        try {
            String value = "code=" + code + "&appType=" + appType + "&appId=" + appId;
            log.info("url:{}", ding_token_userId + value);
            JSONObject userInfo = JSON.parseObject(HttpRequest.sendGet(ding_token_userId, value));
            if(Objects.isNull(userInfo)){
                throw new ServiceException(ErrorCodeEnum.LOGIN_FAIL);
            }
            logger.info("userInfo:{}", JSONObject.toJSONString(userInfo));
            userId = userInfo.getString("openId");
            corpId = userInfo.getString("corpId");
            String errorCode = userInfo.getString("error_code");
            if (StringUtils.isBlank(userId) && StringUtils.isNotBlank(errorCode)) {
                DingLoginErrorEnum dingLoginErrorEnum = DingLoginErrorEnum.getByCode(Integer.getInteger(errorCode));
                if (dingLoginErrorEnum != null) {
                    throw new ServiceException(dingLoginErrorEnum.getCode(), dingLoginErrorEnum.getMsg());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e instanceof ServiceException) {
                throw e;
            } else {
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "用户不存在");
            }
        }

        try {
            return loginService.isvLogin(userId, corpId, Boolean.TRUE, appType, StringUtils.EMPTY);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "登陆失败");
        }
    }

    @PostMapping(value = "/v2/isvLogin-admin")
    public Object isvLoginDev(@RequestParam(value = "appType", required = false) String appType,
                              @RequestBody(required = false) JSONObject jsonObject, HttpServletRequest request) {
        log.info("isvLoginV2 data={}", jsonObject.toJSONString());
        String mobile = jsonObject.getString("mobile");
        String password = jsonObject.getString("password");
        String enterpriseId = jsonObject.getString("enterprise_id");
        if (StringUtils.isBlank(mobile)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "手机不能为空");
        }
        if(AIEnum.AI_MOBILE.getCode().equals(mobile)){
            ResponseResult responseResult = bossUserService.bossGetTokenByEidAndUserID(enterpriseId, AIEnum.AI_USERID.getCode());
            UserLoginVO userLogin = (UserLoginVO) responseResult.getData();
            JSONObject responseJSON = new JSONObject();
            responseJSON.put("action_token", userLogin.getAccessToken());
            responseJSON.put("user", userLogin.getCurrentUser());
            responseJSON.put("isNeedImproveUserInfo", false);
            responseJSON.put("expire", Constants.ACTION_TOKEN_EXPIRE);
            return responseJSON;
        }
        String corpId = null;
        String userId = null;
        DataSourceHelper.reset();
        List<EnterpriseLoginDTO> enterpriseLoginDTOList = enterpriseMapper.getEnterpriseByMobile(mobile, MD5Util.md5(password + Constants.USER_AUTH_KEY));
        if (CollectionUtils.isEmpty(enterpriseLoginDTOList)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "密码错误或者没有找到企业");
        }
        if (enterpriseLoginDTOList.size() == 1) {
            EnterpriseLoginDTO enterpriseLoginDTO = enterpriseLoginDTOList.get(0);
            corpId = enterpriseLoginDTO.getCorpId();
            userId = getLoginAdminUserId(enterpriseLoginDTO);
        } else {
            if (StringUtils.isNotBlank(enterpriseId)) {
                EnterpriseLoginDTO enterpriseLoginDTO = enterpriseLoginDTOList.stream()
                        .filter(data -> data.getEnterpriseId().equals(enterpriseId))
                        .findFirst().orElse(null);
                if (enterpriseLoginDTO != null) {
                    corpId = enterpriseLoginDTO.getCorpId();
                    userId = getLoginAdminUserId(enterpriseLoginDTO);
                }
            } else {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("loginStatus", Boolean.FALSE);
                jsonResponse.put("enterpriseList", enterpriseLoginDTOList);
                return jsonResponse;
            }
        }
        try {
            return loginService.isvLogin(userId, corpId, Boolean.TRUE, appType, StringUtils.EMPTY);
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "登陆失败");
        }
    }

    private String getLoginAdminUserId(EnterpriseLoginDTO enterpriseLoginDTO) {
        String userId;
        String unionId = enterpriseLoginDTO.getUnionId();
        DataSourceHelper.changeToSpecificDataSource(enterpriseLoginDTO.getDbName());
        EnterpriseUserDTO userDetailByUnionId = enterpriseUserMapper.getUserDetailByUnionId(enterpriseLoginDTO.getEnterpriseId(), unionId);
        if (userDetailByUnionId == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业中未找到该员工！");
        }
        userId = userDetailByUnionId.getUserId();
        return userId;
    }

    @PostMapping(value = "/v2/isvLogin/app/record")
    public ResponseResult loginRecord(@RequestBody(required = false) JSONObject jsonObject, HttpServletRequest request) {
        CurrentUser user = UserHolder.getUser();
        try {
            DataSourceHelper.changeToMy();
            loginRecordMapper.insertLoginRecording(user.getEnterpriseId(), user.getUserId(), System.currentTimeMillis());
        } catch (Exception e) {
            log.info("登陆记录失败,enterprise={},userId={}", user.getEnterpriseId(), user.getUserId());
        }
        return ResponseResult.success(true);

    }
    @PostMapping(value = "/v2/login/login/refreshLogin")
    public Object refreshLogin(@RequestBody(required = false) JSONObject jsonObject, HttpServletRequest request,
                               @RequestParam(value = "appType", required = false) String appType){
        log.info("refreshLogin data={}  appType:{}", jsonObject.toJSONString(), appType);
        String refreshToken = jsonObject.getString("refresh_token");
        String refreshStr = redis.getString("refresh_token:"+refreshToken);
        String loginWay = jsonObject.getString("loginWay");
        if(StringUtils.isBlank(refreshStr)){
            return ResponseResult.fail(ErrorCodeEnum.REFRESH_TOKEN_INVALID);
//            throw new ServiceException(ErrorCodeEnum.REFRESH_TOKEN_INVALID);
        }
        RefreshUser refreshUser = JSONObject.parseObject(refreshStr,RefreshUser.class);
        if(StringUtils.isBlank(refreshUser.getEid())){
            return ResponseResult.fail(ErrorCodeEnum.REFRESH_TOKEN_INVALID);
        }
        try {
            JSONObject result = (JSONObject) loginService.refreshLogin(refreshUser.getUserId(), refreshUser.getEid(), request, null, Boolean.FALSE, appType, loginWay);
            result.put("refresh_token",refreshToken);
            redis.setString(REFRESH_TOKEN_KEY+":"+refreshToken,refreshStr, Constants.REFRESH_TOKEN_EXPIRE);
            return result;
        } catch (ServiceException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCodeEnum.REFRESH_TOKEN_INVALID);
        }
    }

    /**
     * 企业微信免登接口
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "企业微信免登接口")
    @ApiImplicitParam(name = "appType", value = "appType:qw,qw2,qw_self_*(自建应用),qw_private_*(私部应用)", required = true)
    @RequestMapping(value = "/v3/qyLogin", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Object qyLogin(@RequestParam(value = "appType", required = false) String appType,
                          @RequestBody QwLoginRequest request, HttpServletRequest httpRequest) {
        String code = request.getCode();
        if(StringUtils.isBlank(code)){
            throw new ServiceException(ErrorCodeEnum.LOGIN_CODE_NOT_NULL);
        }
        String corpId = request.getCorpId();
        /*if(StringUtils.isBlank(corpId)){
            throw new ServiceException(ErrorCodeEnum.CORP_NOT_EXIST);
        }*/
        //如果入参是code数组，截取最后一个code
        code = getCode(code);
        String loginType = request.getLoginType();
        String loginWay = request.getLoginWay();
        logger.info("code:{},corpId:{},loginType:{}, appType:{}", code, corpId, loginType, appType);
        return loginService.wxIsvLogin(code, corpId, appType, httpRequest, loginWay);
    }

    private String getCode(String code) {
        if (code.contains(Constants.COMMA)) {
            code = code.substring(1);
            code = code.substring(0, code.length() - 1);
            code = code.replace(" ","");
            String[] codeList = code.split(Constants.COMMA);
            return codeList[codeList.length - 1];
        }
        return code;
    }



    /**
     * 企业微信 扫码登录
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "企业微信扫码登录接口", notes = "2022/03/28周大福私部需求：新增corpId参数")
    @ApiImplicitParam(name = "appType", value = "appType:qw,qw2,qw_self_*(自建应用),qw_private_*(私部应用)", required = true)
    @PostMapping(value = "/v3/wx_qrcode_login", produces = "application/json;charset=UTF-8")
    public Object wxQrcodeLogin(@RequestParam(value = "appType", required = false) String appType,
                                @RequestBody QwLoginRequest request, HttpServletRequest httpRequest) {
        logger.info("wxQrcodeLogin param:{}, appType:{}", JSONObject.toJSONString(request), appType);
        String code = request.getCode();
        String loginWay = request.getLoginWay();
        String corpId = request.getCorpId();
        return loginService.wxQrcodeLogin(code, corpId, appType, httpRequest,loginWay);
    }

    /**
     * 根据token获取用户信息
     * @param accessToken
     * @return
     */
    @GetMapping("/v3/getUserInfoByToken")
    public CurrentUser getUserInfoByToken(@RequestParam(value = "accessToken",required = true)String accessToken) {
        String key = "access_token:" + accessToken;
        String userStr = redis.getString(key);
        CurrentUser currentUser = null;
        if (StrUtil.isNotEmpty(userStr)) {

            currentUser = JSON.parseObject(userStr, CurrentUser.class);
        } else {
            throw new ServiceException(ErrorCodeEnum.ACCESS_TOKEN_INVALID);
        }
        return currentUser;
    }

    /**
     * 根据userId、corpId获取用户信息
     * @param userid corpId
     * @return
     */
    @GetMapping("/v3/getUserInfoByUserIdAndCorpId")
    public Object getUserInfoByUserIdAndCorpId(@RequestParam(value = "userid",required = true)String userid
                                            , @RequestParam(value = "corpId",required = true) String corpId,
                                             @RequestParam(value = "appType", required = false) String appType,
                                               HttpServletRequest request) {
        log.info("getUserInfoByUserIdAndCorpId userId={}, corpId={}", userid, corpId);
        return loginService.isvLogin(userid, corpId, Boolean.TRUE, appType, StringUtils.EMPTY);
    }



    @ApiOperation("鱼你在一起免登")
    @PostMapping("/v2/yuNiLogin")
    public ResponseResult yuNiLogin(@RequestBody @Validated YNUserLoginDTO param){
        if(!param.getSign().equals(sign(param))){
            throw new ServiceException(ErrorCodeEnum.SIGN_FAIL);
        }
        return ResponseResult.success(loginService.yuNiLogin(param));
    }

    @ApiOperation("明厨亮灶注册及登录")
    @PostMapping("/v3/mclzLogin")
    public ResponseResult mclzLogin(@RequestBody @Valid MclzLoginRequest request) {
        return ResponseResult.success(loginService.mclzLogin(request));
    }

    @ApiOperation("明厨亮灶用户是否存在")
    @PostMapping("/v3/mclzRegisteredVerify")
    public ResponseResult mclzRegisteredVerify(@RequestBody @Valid MclzLoginRequest request) {
        return ResponseResult.success(loginService.mclzRegisteredVerify(request));
    }

    @ApiOperation("果然AskBot单点登录")
    @PostMapping("/v3/askBotLogin")
    public ResponseResult<String> askBotLogin(@RequestBody @Valid AskBotLoginRequest request) {
        return ResponseResult.success(loginService.askBotLogin(request));
    }

    public static String sign(YNUserLoginDTO requestParam){
        Map<String, String> appSecretMap = new HashMap<>();
        appSecretMap.put("118bc3c221294bdbb772dc430b9dfcb5", "2f46be76ffb5438d91546015731a2277");
        appSecretMap.put("6d7c080bd55c4768807a0793fe4f3b8a", "2f46be76ffb5438d91546015731a2277");
        String appSecret = appSecretMap.get(requestParam.getEnterpriseId());
        if(StringUtils.isBlank(appSecret)){
            throw new ServiceException(ErrorCodeEnum.APP_SECRET_ERROR);
        }
        log.info("鱼你登录参数为：{}", JSONObject.toJSONString(requestParam));
        HashMap<String, String> reqMap = JSON.parseObject(JSON.toJSONString(requestParam), LinkedHashMap.class, Feature.OrderedField);
        reqMap.remove("sign");
        String sortReqStr = MapUtil.sortJoin(reqMap, "&", "=", true);
        log.info("并按照参数名ASCII字典序排序如下：{}", sortReqStr);
        sortReqStr = sortReqStr + "&key=" + appSecret;
        String sign = SecureUtil.hmacSha256(appSecret).digestHex(sortReqStr).toUpperCase();
        log.info("加签后的sign为：{}", sign);
        return sign;
    }

    public static void main(String[] args) {
        String url = "appId=452can9dt09h46o1&enterpriseId=118bc3c221294bdbb772dc430b9dfcb5&requestId=2024090511554410203&signType=HMAC-SHA256&timestamp=1725508545193&userName=13093783517&userType=1&sign=E80B4EFFC84BA27DDB2FEBE7B05013F67E87510D8EBB9176DC0E5F15052F0C3E";
        Map<String, Object> urlParams = UrlUtil.getUrlParams(url);
        YNUserLoginDTO requestParam = JSON.parseObject(JSON.toJSONString(urlParams), YNUserLoginDTO.class);
        sign(requestParam);
        log.info("加签前的sign为：{}", urlParams.get("sign"));
    }


}
