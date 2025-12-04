package com.coolcollege.intelligent.service.wechat.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.wechat.WechatAppEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.common.util.WxUtil;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserWxDao;
import com.coolcollege.intelligent.dto.EnterpriseConfigDTO;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiGetUserAccessTokenDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.UserAccessTokenVO;
import com.coolcollege.intelligent.model.coolcollege.CoolCollegeMsgDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserWxDO;
import com.coolcollege.intelligent.model.wechat.request.MiniProgramRequest;
import com.coolcollege.intelligent.model.wechat.request.WechatLoginRequest;
import com.coolcollege.intelligent.model.wechat.request.WechatMessageRequest;
import com.coolcollege.intelligent.model.wechat.vo.WechatLoginUserInfoVO;
import com.coolcollege.intelligent.model.wechat.vo.WechatSignatureVO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.login.LoginService;
import com.coolcollege.intelligent.service.wechat.WechatService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.coolcollege.intelligent.common.enums.wechat.WechatAppEnum.TEST;

/**
 * @author zhangchenbiao
 * @FileName: WechatServiceImpl
 * @Description:
 * @date 2023-10-17 14:59
 */
@Slf4j
@Service
public class WechatServiceImpl implements WechatService {

    @Resource
    private HttpRestTemplateService httpRestTemplateService;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseUserWxDao enterpriseUserWxDao;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private LoginService loginService;
    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    private static final String SEND_WX_MSG_EID = "wechat_send_msg_eid";

    @Override
    public WechatSignatureVO getSignature(String appId, String url) {
        String ticket = getTicket(appId);
        long timestamp = System.currentTimeMillis() / 1000;
        String nonceStr = UUIDUtils.get8UUID();
        String signStr = MessageFormat.format("jsapi_ticket={0}&noncestr={1}&timestamp={2}&url={3}", ticket, nonceStr, String.valueOf(timestamp), url);
        log.info("signStr:{}", signStr);
        String signature = null;
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.reset();
            sha1.update(signStr.getBytes("UTF-8"));
            Formatter formatter = new Formatter();
            for (byte b : sha1.digest()) {
                formatter.format("%02x", b);
            }
            signature = formatter.toString();
            formatter.close();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new ServiceException(ErrorCodeEnum.SIGN_ERROR);
        }
        return new WechatSignatureVO(timestamp, nonceStr, signature);
    }

    @Override
    public String getWechatAccessToken(String appId){
        String appSecret = WechatAppEnum.getAppSecret(appId);
        if(StringUtils.isBlank(appSecret)){
            throw new ServiceException(ErrorCodeEnum.APPID_ERROR);
        }
        String cacheKey = MessageFormat.format(RedisConstant.WECHAT_ACCESS_TOKEN, appId, appSecret);
        String accessToken = redisUtilPool.getString(cacheKey);
        if(StringUtils.isBlank(accessToken)){
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("appid", appId);
            requestMap.put("secret", appSecret);
            requestMap.put("grant_type", "client_credential");
            String url = "https://api.weixin.qq.com/cgi-bin/stable_token";
            JSONObject object = httpRestTemplateService.postForObject(url, requestMap, JSONObject.class);
            if(Objects.isNull(object)){
                throw new ServiceException(ErrorCodeEnum.API_ERROR);
            }
            accessToken = object.getString("access_token");
            Integer expiresIn = object.getInteger("expires_in");
            redisUtilPool.setString(cacheKey, accessToken, expiresIn);
        }
        return accessToken;
    }

    @Override
    public WechatLoginUserInfoVO getUserInfo(String enterpriseId, String appId, String code) {
        String appSecret = WechatAppEnum.getAppSecret(appId);
        if(StringUtils.isBlank(appSecret)){
            throw new ServiceException(ErrorCodeEnum.APPID_ERROR);
        }
        JSONObject accessAuthToken = WxUtil.getAccessAuthToken(code, appId, appSecret);
        WechatLoginUserInfoVO result = WechatLoginUserInfoVO.convert(accessAuthToken);
        DataSourceHelper.reset();
        // 切换数据库
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        String dbName = enterpriseConfig.getDbName();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        EnterpriseUserWxDO enterpriseUserWx = enterpriseUserWxDao.getByOpenId(result.getOpenid(), enterpriseId);
        if(Objects.isNull(enterpriseUserWx)){
            result.setIsBand(Boolean.FALSE);
        }else{
            OpenApiGetUserAccessTokenDTO openApiGetUserAccessTokenDTO = OpenApiGetUserAccessTokenDTO.builder().userAccount(enterpriseUserWx.getUserId()).accountType(2).build();
            UserAccessTokenVO userAccessToken = loginService.getUserAccessToken(enterpriseId, openApiGetUserAccessTokenDTO);
            result.setIsBand(Boolean.TRUE);
            result.setAccessToken(userAccessToken.getAccessToken());
        }
        return result;
    }

    @Override
    public WechatLoginUserInfoVO getLoginAccessToken(String enterpriseId, WechatLoginRequest param) {
        DataSourceHelper.reset();
        // 切换数据库
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        String dbName = enterpriseConfig.getDbName();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        // 查询是否注册
        EnterpriseUserWxDO enterpriseUserWxDO = enterpriseUserWxDao.getByOpenId(param.getOpenid(), enterpriseId);
        if (enterpriseUserWxDO == null) {
            if(StringUtils.isBlank(param.getMobile())){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
            }
            // 获取用户信息
            EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.getUserByMobile(enterpriseId, param.getMobile());
            if (enterpriseUserDO == null) {
                throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
            }
            // 注册用户
            enterpriseUserWxDO = EnterpriseUserWxDO.builder()
                    .id(UUIDUtils.get32UUID())
                    .openid(param.getOpenid()).userId(enterpriseUserDO.getUserId())
                    .build();
            enterpriseUserWxDao.insert(enterpriseUserWxDO, enterpriseId);
        }
        // 获取accessToken
        OpenApiGetUserAccessTokenDTO openApiGetUserAccessTokenDTO = OpenApiGetUserAccessTokenDTO.builder().userAccount(enterpriseUserWxDO.getUserId()).accountType(2).build();
        UserAccessTokenVO userAccessToken = loginService.getUserAccessToken(enterpriseId, openApiGetUserAccessTokenDTO);
        return WechatLoginUserInfoVO.builder()
                .openid(param.getOpenid())
                .accessToken(userAccessToken.getAccessToken())
                .isBand(true).build();
    }

    @Override
    public Boolean sendMessage(WechatMessageRequest param) {
        String wechatAccessToken = this.getWechatAccessToken(param.getAppId());
        String paramStr = JSONObject.toJSONString(param);
        String sendKey = MD5Util.md5(paramStr);
        boolean isSend = redisUtilPool.setNxExpire(sendKey, paramStr, 3 * 60 * 1000);
        if(!isSend){
            log.info("消息已发送过sendKey:{}，发送微信消息：{}", sendKey, paramStr);
            return true;
        }
        log.info("发送微信消息：{}", paramStr);
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + wechatAccessToken;
        JSONObject object = httpRestTemplateService.postForObject(url, param, JSONObject.class);
        if(Objects.isNull(object)){
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        return 0 == object.getInteger("errcode");
    }

    @Override
    @Async("noticeThreadPool")
    public void sendWXMsg(EnterpriseConfigDO enterpriseConfig, CoolCollegeMsgDTO dto, String urlParam) {
        String enterpriseId = redisUtilPool.hashGet(SEND_WX_MSG_EID, enterpriseConfig.getEnterpriseId());
        if(StringUtils.isBlank(enterpriseId)){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<String> openIds = enterpriseUserWxDao.getOpenIdsByUserIds(enterpriseId, dto.getUserIds());
        if(CollectionUtils.isEmpty(openIds)){
            return;
        }
        WechatAppEnum wechatAppEnum = getWechatAppEnum(enterpriseId);
        String title = dto.getMessageTitle(), content = dto.getMessageContent();
        if(StringUtils.isNotBlank(title) && title.length() > 20){
            title = dto.getMessageTitle().substring(0, 20);
        }
        if(StringUtils.isBlank(title)){
            title = "培训通知";
        }
        content = getTextTime(content);
        JSONObject noticeTemplate = getNoticeTemplate(wechatAppEnum.getAppId(), "coolCollege");
        if(Objects.isNull(noticeTemplate)){
            return;
        }
        String noticeTemplateId = noticeTemplate.getString("templateId");
        String msgData = noticeTemplate.getString("msgData");
        JSONObject jsonObject = JSON.parseObject(String.format(msgData, title, content));
        for (String openId : openIds){
            String url = "pages/notice/index?corpId={0}&appType={1}&openid={2}&enterpriseId={3}&noticeType=coolcollege&"+urlParam;
            String pagepath = MessageFormat.format(url, enterpriseConfig.getDingCorpId(), enterpriseConfig.getAppType(), openId, enterpriseId);
            WechatMessageRequest request = WechatMessageRequest.builder()
                    .appId(wechatAppEnum.getAppId())
                    .touser(openId)
                    .template_id(noticeTemplateId)
                    .miniprogram(MiniProgramRequest.builder().appid(wechatAppEnum.getMiniProgramAppId()).pagepath(pagepath).build())
                    .data(jsonObject)
                    .build();
            sendMessage(request);
        }
    }

    @Override
    @Async("noticeThreadPool")
    public void sendWXMsg(EnterpriseConfigDO enterpriseConfig, List<String> userIds, String taskName, String time, String urlParam, String outBusinessId, String storeId) {
        String enterpriseId = redisUtilPool.hashGet(SEND_WX_MSG_EID, enterpriseConfig.getEnterpriseId());
        if(StringUtils.isBlank(enterpriseId)){
            return;
        }
        if(StringUtils.isNotBlank(outBusinessId)){
            String lockKey = outBusinessId.endsWith("newTask") ? outBusinessId + "_" + storeId : outBusinessId;
            boolean isSend = redisUtilPool.setNxExpire("wx_msg:"+lockKey, enterpriseId, 30 * 60 * 1000);
            if(!isSend){
                log.info("消息重复发送：outBusinessId:{},storeId:{}", outBusinessId, storeId);
                return;
            }
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<String> openIds = enterpriseUserWxDao.getOpenIdsByUserIds(enterpriseId, userIds);
        if(CollectionUtils.isEmpty(openIds)){
            return;
        }
        WechatAppEnum wechatAppEnum = getWechatAppEnum(enterpriseId);
        if(StringUtils.isNotBlank(taskName) && taskName.length() > 20){
            taskName = taskName.substring(0, 20);
        }
        if(StringUtils.isNotBlank(time) && time.length() > 20){
            time = time.substring(0, 20);
        }
        JSONObject noticeTemplate = getNoticeTemplate(wechatAppEnum.getAppId(), "patrolStore");
        if(Objects.isNull(noticeTemplate)){
            return;
        }
        String noticeTemplateId = noticeTemplate.getString("templateId");
        String msgData = noticeTemplate.getString("msgData");
        JSONObject jsonObject = JSON.parseObject(String.format(msgData, taskName, time));
        for (String openId : openIds){
            String url = "pages/notice/index?corpId={0}&appType={1}&openid={2}&enterpriseId={3}&target="+urlParam;
            String pagepath = MessageFormat.format(url, enterpriseConfig.getDingCorpId(), enterpriseConfig.getAppType(), openId, enterpriseId);
            WechatMessageRequest request = WechatMessageRequest.builder()
                    .appId(wechatAppEnum.getAppId())
                    .touser(openId)
                    .template_id(noticeTemplateId)
                    .miniprogram(MiniProgramRequest.builder().appid(wechatAppEnum.getMiniProgramAppId()).pagepath(pagepath).build())
                    .data(jsonObject)
                    .build();
            sendMessage(request);
        }
    }

    @Override
    @Async("noticeThreadPool")
    public void sendWXMsg(String enterpriseId, List<String> userIds, String title, String content, String urlParam) {
        String eid = redisUtilPool.hashGet(SEND_WX_MSG_EID, enterpriseId);
        if(StringUtils.isBlank(eid)){
            return;
        }
        DataSourceHelper.reset();
        // 切换数据库
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            throw new ServiceException(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        sendWXMsg(enterpriseConfig, userIds, title, content, urlParam, null, null);
    }

    @Override
    @Async("noticeThreadPool")
    public void sendWXMsg(EnterpriseConfigDTO enterpriseConfig, List<String> userIds, String title, String content, String urlParam) {
        String enterpriseId = redisUtilPool.hashGet(SEND_WX_MSG_EID, enterpriseConfig.getEnterpriseId());
        if(StringUtils.isBlank(enterpriseId)){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        List<String> openIds = enterpriseUserWxDao.getOpenIdsByUserIds(enterpriseId, userIds);
        if(CollectionUtils.isEmpty(openIds)){
            return;
        }
        WechatAppEnum wechatAppEnum = getWechatAppEnum(enterpriseId);
        if(StringUtils.isNotBlank(title) && title.length() > 20){
            title = title.substring(0, 20);
        }
        if(StringUtils.isNotBlank(content) && content.length() > 20){
            content = content.substring(0, 20);
        }
        JSONObject noticeTemplate = getNoticeTemplate(wechatAppEnum.getAppId(), "patrolStore");
        if(Objects.isNull(noticeTemplate)){
            return;
        }
        String noticeTemplateId = noticeTemplate.getString("templateId");
        String msgData = noticeTemplate.getString("msgData");
        JSONObject jsonObject = JSON.parseObject(String.format(msgData, title, content));
        for (String openId : openIds){
            String url = "pages/notice/index?corpId={0}&appType={1}&openid={2}&enterpriseId={3}&target="+urlParam;
            String pagepath = MessageFormat.format(url, enterpriseConfig.getDingCorpId(), enterpriseConfig.getAppType(), openId, enterpriseId);
            WechatMessageRequest request = WechatMessageRequest.builder()
                    .appId(wechatAppEnum.getAppId())
                    .touser(openId)
                    .template_id(noticeTemplateId)
                    .miniprogram(MiniProgramRequest.builder().appid(wechatAppEnum.getMiniProgramAppId()).pagepath(pagepath).build())
                    .data(jsonObject)
                    .build();
            sendMessage(request);
        }
    }

    public static String getTextTime(String text){
        try {
            // 使用正则表达式匹配日期和时间
            Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2})");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                // 获取匹配的时间
                String time = matcher.group(1);
                return time;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    public WechatAppEnum getWechatAppEnum(String enterpriseId){
        String appData = redisUtilPool.hashGet(RedisConstant.WECHAT_APP_ID_KEY, enterpriseId);
        if(StringUtils.isBlank(appData)){
            return TEST;
        }
        WechatAppEnum wechatAppEnum = WechatAppEnum.fromJson(appData);
        return wechatAppEnum;
    }

    public JSONObject getNoticeTemplate(String appId, String noticeType){
        String templateData = redisUtilPool.hashGet(RedisConstant.WECHAT_MSG_TEMPLATE_DATA_KEY, appId);
        if(StringUtils.isBlank(templateData)){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(templateData);
        String noticeData = jsonObject.getString(noticeType);
        return JSONObject.parseObject(noticeData);
    }

    private String getTicket(String appId){
        String cacheKey = MessageFormat.format(RedisConstant.WECHAT_TICKET, appId);
        String ticket = redisUtilPool.getString(cacheKey);
        if(StringUtils.isBlank(ticket)){
            String wechatAccessToken = getWechatAccessToken(appId);
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("access_token", wechatAccessToken);
            requestMap.put("type", "jsapi");
            String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
            JSONObject object = httpRestTemplateService.getForObject(url, JSONObject.class, requestMap);
            if(Objects.isNull(object)){
                throw new ServiceException(ErrorCodeEnum.API_ERROR);
            }
            String errcode = object.getString("errcode");
            if("40001".equals(errcode)){
                //重新获取accessToken
                int retryTimes = 1;
                object = getRetry(url, appId, requestMap, JSONObject.class, retryTimes);
            }
            ticket = object.getString("ticket");
            Integer expiresIn = object.getInteger("expires_in");
            redisUtilPool.setString(cacheKey, ticket, expiresIn);
        }
        return ticket;
    }

    private <T> T  getRetry(String url, String appId, Map<String, String> request, Class<T> responseType, int retryTimes){
        log.info("token异常, enterpriseId:{}, 重试：{}", retryTimes);
        T response = null;
        if(retryTimes < Constants.INDEX_TWO){
            String appSecret = WechatAppEnum.getAppSecret(appId);
            String accessTokenCacheKey = MessageFormat.format(RedisConstant.WECHAT_ACCESS_TOKEN, appId, appSecret);
            redisUtilPool.delKey(accessTokenCacheKey);
            String wechatAccessToken = getWechatAccessToken(appId);
            request.put("access_token", wechatAccessToken);
            try {
                response = httpRestTemplateService.getForObject(url, responseType, request);
                if(Objects.isNull(response)){
                    throw new ServiceException(ErrorCodeEnum.API_ERROR);
                }
                JSONObject responseJson = JSONObject.parseObject(JSONObject.toJSONString(response));
                if("40001".equals(responseJson.getString("errcode"))){
                    retryTimes = retryTimes + 1;
                    getRetry(url, appId, request, responseType, retryTimes);
                }
            } catch (Exception e) {
                retryTimes = retryTimes + 1;
                getRetry(url, appId, request, responseType, retryTimes);
            }
        }
        return response;
    }

}
