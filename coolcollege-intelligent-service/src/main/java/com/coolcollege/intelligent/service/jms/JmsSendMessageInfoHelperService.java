package com.coolcollege.intelligent.service.jms;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.service.jms.constans.MqQueueNameEnum;
import com.coolcollege.intelligent.service.jms.dto.AppExtraParamDTO;
import com.coolcollege.intelligent.service.jms.dto.AppPushMsgDTO;
import com.coolcollege.intelligent.service.jms.vo.JmsSendMessageVo;
import com.coolstore.base.enums.AppTypeEnum;
import jodd.util.StringUtil;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by ydw on 2017/11/2.
 */
@Service
public class JmsSendMessageInfoHelperService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JmsSendMessageSyncService jmsSendMessageSyncService;

//    @Value("${send_ding_pc_message_url}")
//    private String sendDingPcMessageUrl;

    /*@Value("${send_ding_pc_message_url_hd}")
    private String sendDingPcMessageUrlHd;*/

    @Value("${qywx.task.notice.url}")
    private String qywxUrl;

    @Value("${qywx.task.notice.url2}")
    private String qywxUrl2;

    /**
     * 门店通工作通知跳转url
     */
    @Value("${dingtalk.oneparty.notice.url}")
    private String onepartyNoticeUrl;

    @Value("${qywx.config.app.suiteId}")
    private String suiteId;

    @Value("${qywx.config.app.suiteId2}")
    private String suiteId2;

    @Value("${qywx.task.notice.oauth.url}")
    private String oauthUrl;

    @Value("${feishu.notice.url}")
    private String feiShuNoticeUrl;

    /**
     * 发送文本消息
     *
     * @param jmsSendMessageVo
     * @param isSync           是否同步发送，true:调用同步发送函数，false:调用异步发送函数
     */
    public void sendTextMessage(JmsSendMessageVo jmsSendMessageVo, boolean isSync) {
        JSONObject args = new JSONObject();
        args.put("content", jmsSendMessageVo.getContent());
        args.put("corpId", jmsSendMessageVo.getDingCorpId());
        args.put("messageType", "text");
        args.put("appType",jmsSendMessageVo.getAppType());
        sendMessageHelp(jmsSendMessageVo.getDingCorpId(),
                jmsSendMessageVo.getUserIds(),
                args,
                jmsSendMessageVo.getMqQueueName(), isSync);
    }

    /**
     * 发送动态OA消息,
     *
     * @param jmsSendMessageVo
     * @param isSync           是否同步发送，true:调用同步发送函数，false:调用异步发送函数
     */
    public void sendOAMessageLogicDynamic(JmsSendMessageVo jmsSendMessageVo, boolean isSync,boolean sendApp) {

        String content = jmsSendMessageVo.getContent();
        String corpId = jmsSendMessageVo.getDingCorpId();
        String mobileParam = jmsSendMessageVo.getMobileParam();
        String staticUrl = jmsSendMessageVo.getStaticUrl();
        boolean isStaticUrl = jmsSendMessageVo.getIsStaticUrl();
        boolean containPcUrl = jmsSendMessageVo.getContainPcUrl();
        String storeId = jmsSendMessageVo.getStoreId();

        JSONObject args = new JSONObject();
        args.put("content", content);
        args.put("corpId", corpId);
        args.put("outBusinessId", jmsSendMessageVo.getOutBusinessId());
        args.put("appType", jmsSendMessageVo.getAppType());
        args.put("storeId", storeId);
        String headTitle = "";
        JSONObject oaJson = new JSONObject();
        // OA特有参数
        String messageUrl = Constants.E_APP_LOGIN_IN_PAGE + mobileParam;
        if (isStaticUrl) {
            messageUrl = staticUrl;
        }
        //数智门店和酷店掌的url不同
        try {
            if (AppTypeEnum.WX_APP.getValue().equals(jmsSendMessageVo.getAppType())) {
                //先组装参数
                messageUrl = URLEncoder.encode(qywxUrl + mobileParam, "UTF-8");
                //再组装授权链接
                messageUrl = String.format(oauthUrl, suiteId, messageUrl);
            }
            if (AppTypeEnum.WX_APP2.getValue().equals(jmsSendMessageVo.getAppType())) {
                String noticeUrl = String.format(qywxUrl2, corpId, jmsSendMessageVo.getAppType());
                messageUrl = URLEncoder.encode(noticeUrl + mobileParam, "UTF-8");
                messageUrl = String.format(oauthUrl, suiteId2, messageUrl);
            }
            if (AppTypeEnum.isWxSelfAndPrivateType(jmsSendMessageVo.getAppType())) {
                String noticeUrl = String.format(qywxUrl2, corpId, jmsSendMessageVo.getAppType());
                messageUrl = URLEncoder.encode(noticeUrl + mobileParam, "UTF-8");
                messageUrl = String.format(Constants.WX_SELF_AUTH_URL, corpId, messageUrl);
                logger.info("appType:{},messageUrl:{}", jmsSendMessageVo.getAppType(), messageUrl);
            }
            if (AppTypeEnum.ONE_PARTY_APP.getValue().equals(jmsSendMessageVo.getAppType())
                || AppTypeEnum.ONE_PARTY_APP2.getValue().equals(jmsSendMessageVo.getAppType())) {
                messageUrl = MessageFormat.format(onepartyNoticeUrl, corpId, mobileParam);
            }
            if (AppTypeEnum.FEI_SHU.getValue().equals(jmsSendMessageVo.getAppType())) {
                messageUrl = URLEncoder.encode(mobileParam, "UTF-8");
                messageUrl = String.format(feiShuNoticeUrl, messageUrl);
            }
        } catch (Exception e) {
            logger.error("组装企微授权链接失败", e);
        }

        logger.info("isStaticUrl:{},staticUrl:{},messageUrl:{},containPcUrl:{},oaJson.toJSONString:{},staticUrl:{},containPcUrl:{}",isStaticUrl,staticUrl,messageUrl,containPcUrl,oaJson.toJSONString(),staticUrl,containPcUrl);

        oaJson.put("message_url", messageUrl);
        JSONObject headJson = new JSONObject();
        headJson.put("bgcolor", "FFBBBBBB");
        headJson.put("text", headTitle);
        oaJson.put("head", headJson);
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("title", jmsSendMessageVo.getTitle());
        bodyJson.put("content", content);
        bodyJson.put("image", jmsSendMessageVo.getPicUrl());
        oaJson.put("body", bodyJson);
        String outBusinessId = jmsSendMessageVo.getOutBusinessId();
        if(StringUtil.isBlank(outBusinessId)){
            outBusinessId = UUIDUtils.get32UUID();
        }
        args.put("outBusinessId", outBusinessId);
        args.put("oaJson", oaJson);
        args.put("cycleCount", jmsSendMessageVo.getCycleCount());
        //发送工作通知
        sendMessageHelp(corpId, jmsSendMessageVo.getUserIds(), args, jmsSendMessageVo.getMqQueueName(), isSync);
        //发送APP通知
        if(sendApp){
            AppPushMsgDTO appPushMsgDTO = new AppPushMsgDTO();
            appPushMsgDTO.setTitle(jmsSendMessageVo.getTitle());
            appPushMsgDTO.setContent(content);
            appPushMsgDTO.setPushType("ACCOUNT");
            String targetValue = String.join(",", ListUtils.emptyIfNull(jmsSendMessageVo.getUserIds()));
            appPushMsgDTO.setPushTarget(targetValue);
            AppExtraParamDTO appExtraParamDTO =new AppExtraParamDTO();
            appExtraParamDTO.setMessageId(UUIDUtils.get32UUID());
            appExtraParamDTO.setMessageUrl(messageUrl);
            appExtraParamDTO.setMessageType(2);
            appPushMsgDTO.setExtraParam(appExtraParamDTO);
            sendMessageHelp(corpId, jmsSendMessageVo.getUserIds(),(JSONObject)JSONObject.toJSON(appPushMsgDTO), MqQueueNameEnum.MQ_QUEUE_NAME_APP_PUSH.getValue(), isSync);
        }

    }

    /**
     * OA消息发送帮助函数
     *
     * @param corpId
     * @param userIds
     * @param args
     * @param queueName
     * @param isSync    是否同步发送，true:调用同步发送函数，false:调用异步发送函数
     */
    private void sendMessageHelp(String corpId, List<String> userIds, JSONObject args, String queueName, boolean isSync) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        if (isSync) { // 同步发送
            jmsSendMessageSyncService.sendMessageSync(corpId, userIds, args, queueName);
        } else {
            jmsSendMessageSyncService.sendMessageAsync(corpId, userIds, args, queueName);
        }
    }

    /**
     * URL转码
     *
     * @param url
     * @param type
     * @return
     */
    private String getURLEncoder(String url, String type) {
        String encodeUrl = "";
        try {
            encodeUrl = URLEncoder.encode(url, type);
        } catch (Exception e) {
            logger.error(url + ":转码失败:", e);
            encodeUrl = url;
        }
        return encodeUrl;
    }

    /**
     * 消息测试方法（正式逻辑禁用）
     *
     * @param corpId
     * @param userIdList
     * @param args
     * @param queueName
     */
    public void test(String corpId, List<String> userIdList, JSONObject args, String queueName) {
        this.sendMessageHelp(corpId, userIdList, args, queueName, false);
    }
}
