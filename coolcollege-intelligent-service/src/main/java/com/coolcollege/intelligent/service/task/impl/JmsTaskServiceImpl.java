package com.coolcollege.intelligent.service.task.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.rpc.common.json.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.StoreWorkConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.constant.i18n.I18nMessageKeyEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.safetycheck.FoodCheckNoticeEnum;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkCycleEnum;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkNoticeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.*;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionHistoryDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.safetycheck.dao.ScSafetyCheckFlowDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkTableMappingDao;
import com.coolcollege.intelligent.dao.supervision.dao.SupervisionStoreTaskDao;
import com.coolcollege.intelligent.dao.supervision.dao.SupervisionTaskDao;
import com.coolcollege.intelligent.dao.supervision.dao.SupervisionTaskParentDao;
import com.coolcollege.intelligent.dto.EnterpriseConfigDTO;
import com.coolcollege.intelligent.dto.EnterpriseQuestionSettingsDTO;
import com.coolcollege.intelligent.dto.EnterpriseSettingsDTO;
import com.coolcollege.intelligent.dto.EnterpriseStoreWorkSettingsDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.msg.MessageDealDTO;
import com.coolcollege.intelligent.model.msg.StoreWorkMessageDTO;
import com.coolcollege.intelligent.model.msg.SupervisionTaskMessageDTO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.question.TbQuestionHistoryDO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.safetycheck.ScSafetyCheckFlowDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkTableMappingDO;
import com.coolcollege.intelligent.model.supervision.SupervisionStoreTaskDO;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskDO;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskParentDO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionTaskVO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.rpc.config.EnterpriseSettingRpcService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.jms.JmsSendMessageLogicService;
import com.coolcollege.intelligent.service.jms.dto.AppExtraParamDTO;
import com.coolcollege.intelligent.service.jms.dto.AppPushMsgDTO;
import com.coolcollege.intelligent.service.jms.dto.SendMessageDTO;
import com.coolcollege.intelligent.service.jms.dto.SendTextMessageDTO;
import com.coolcollege.intelligent.service.jms.vo.JmsContentParamsVo;
import com.coolcollege.intelligent.service.jms.vo.JmsSendMessageVo;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskParentService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskSubService;
import com.coolcollege.intelligent.service.wechat.WechatService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.TaskCacheManager;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/7/24 14:58
 */
@Service
@Slf4j
public class JmsTaskServiceImpl implements JmsTaskService {

    @Resource
    private EnterpriseConfigMapper configMapper;
    @Resource
    private EnterpriseMapper enterpriseMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Autowired
    private JmsSendMessageLogicService jmsSendMessageLogicService;
    @Resource
    private StoreMapper storeMapper;
    @Autowired
    private EnterpriseSettingService enterpriseSettingService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    private UnifyTaskParentService unifyTaskParentService;
    @Resource
    private EnterpriseUserService enterpriseUserService;
    @Resource
    private UnifyTaskSubService unifyTaskSubService;
    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    private EnterpriseSettingRpcService enterpriseSettingRpcService;
    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;
    @Resource
    private UnifyTaskStoreService unifyTaskStoreService;
    @Resource
    private QuestionRecordDao questionRecordDao;
    @Resource
    private QuestionParentInfoDao questionParentInfoDao;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private QuestionHistoryDao questionHistoryDao;
    @Resource
    private SwStoreWorkDao swStoreWorkDao;
    @Resource
    private SwStoreWorkDataTableDao swStoreWorkDataTableDao;
    @Resource
    private SwStoreWorkTableMappingDao swStoreWorkTableMappingDao;
    @Resource
    private SupervisionTaskDao supervisionTaskDao;
    @Resource
    private SupervisionTaskParentDao supervisionTaskParentDao;
    @Resource
    private SupervisionStoreTaskDao supervisionStoreTaskDao;
    @Resource
    private ScSafetyCheckFlowDao scSafetyCheckFlowDao;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private WechatService wechatService;


    @Value("${app.mini.id}")
    private String appMiniId;

    @Value("${app.id}")
    private String appId;

    @Value("${cool.app.mini.id}")
    private String coolAppMiniId;

    @Value("${cool.app.id}")
    private String coolAppId;

    /**
     * 门店通工作通知跳转url
     */
    @Value("${dingtalk.oneparty.notice.url}")
    private String onepartyNoticeUrl;

    @Value("${qywx.task.notice.url}")
    private String qywxUrl;

    @Value("${qywx.task.notice.url2}")
    private String qywxUrl2;

    @Value("${qywx.config.app.suiteId}")
    private String suiteId;

    @Value("${qywx.config.app.suiteId2}")
    private String suiteId2;

    @Value("${qywx.task.notice.oauth.url}")
    private String oauthUrl;

    @Value("${coolstore.page.domain}")
    private String coolStoreDomainUrl;

    @Value("${coolcollege.page.domain}")
    private String coolCollegeDomainUrl;

    @Value("${feishu.notice.url}")
    private String feiShuNoticeUrl;


    /**
     * 待办业务ID前缀(SZMD【数智门店首字母大写】的16进制)
     */
    private static final String PREFIX = "0x535a4d44_";

    @Override
    @Async("noticeThreadPool")
    public void sendUnifyTaskJms(String taskType, List<String> handleUserId, String nodeStr, String eid, String storeName, Long unifyTaskSubId, String createUserName, Long endTime, String taskName, Boolean isTransmit,
                                 Long beginTime, String storeId, String outBusinessId, Boolean isCC, Long unifyTaskId, Long cycleCount, Long loopCount, Long businessId) {
        if(TaskTypeEnum.QUESTION_ORDER.getCode().equals(taskType)){
            return;
        }
        if(StringUtils.isBlank(outBusinessId)){
            outBusinessId = eid + "_" + unifyTaskSubId + "_" + nodeStr + "_" +MD5Util.md5(JSONUtil.toJsonStr(handleUserId));
        }
        Boolean isCombineNotice = StringUtils.isNotBlank(outBusinessId) && outBusinessId.startsWith(Constants.TASKNOTICECOMBINE);
        log.info("任务消息参数：DisplayTask  子任务id：{}，处理人：{}，当前节点：{}，企业id：{}, outBusinessId :{}", unifyTaskSubId, handleUserId.get(0), nodeStr, eid, outBusinessId);
        JmsSendMessageVo messageVo = new JmsSendMessageVo();
        messageVo.setCycleCount(cycleCount);
        messageVo.setStoreId(storeId);
        DataSourceHelper.reset();
        EnterpriseConfigDO config = TaskCacheManager.getEnterpriseConfig(eid, unifyTaskId, () -> configMapper.selectByEnterpriseId(eid));
        EnterpriseSettingDO setting = TaskCacheManager.getEnterpriseSetting(eid, unifyTaskId, () -> enterpriseSettingService.selectByEnterpriseId(eid));
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        List<JmsContentParamsVo> contentVos = Lists.newArrayListWithCapacity(1);
        // 钉钉corpId
        messageVo.setDingCorpId(config.getDingCorpId());
        messageVo.setAppType(config.getAppType());
        if (AppTypeEnum.isQwType(config.getAppType())) {
            createUserName = "$userName=" + createUserName + "$";
        }
        /**
         * 此处进行if判断
         */
        switch (nodeStr) {
            case "1":
                if(TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskType)) {
                    contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.PATROL_STORE_PLAN, new String[]{createUserName, taskName, DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_MINUTE)}));
                    break;
                }
                //待处理
                if(isCombineNotice){
                    contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.TASK_HANDLE_COMBINE, new String[]{createUserName, taskName, DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_MINUTE)}));
                }else {
                    contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.DISPLAY_TASK_HANDLE, new String[]{storeName, createUserName, taskName, DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_MINUTE)}));
                }
                break;
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
                //待审批
                contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.DISPLAY_TASK_APPROVE, new String[]{storeName, createUserName, taskName, DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_MINUTE)}));
                break;
            case "endNode":
                //完成
                contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.DISPLAY_TASK_COMPLETE, new String[]{taskName}));
                break;
            case "cc":
                //完成
                contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.TASK_CC, new String[]{}));
                break;
            case "cc_ps":
                //自主巡店抄送任务
                contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.CC_PS, new String[]{storeName, createUserName}));
                break;
            case "cc_online":
                //线上巡店
                contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.CC_ONLINE, new String[]{createUserName}));
                break;
            case "product_remind":
                //自主巡店抄送任务
                contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.ACHIEVEMENT_TASK_REMIND, new String[]{storeName, createUserName, DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_MINUTE)}));
                break;
            default:
                return;
        }
        if (isTransmit) {
            String key = "transmitTask:" + eid + "_" + unifyTaskSubId + "_" + storeId + "_" + unifyTaskId + "_" + handleUserId.get(0);
            String param = redisUtilPool.getString(key);
            log.info("transmitTaskParam：{}",JSONObject.toJSONString(param));
            if (StringUtils.isBlank(param)){
                contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.TRANSMIT_TASK, new String[]{param}));
            }else {
                contentVos.add(new JmsContentParamsVo(I18nMessageKeyEnum.TRANSMIT_TASK2, new String[]{param}));
            }
            redisUtilPool.delKey(key);
        }
        StringBuilder mobileParamBuilder = new StringBuilder();
        mobileParamBuilder.append(DingMsgEnum.getByCode(taskType));
        if(Objects.nonNull(unifyTaskSubId)){
            mobileParamBuilder.append("&unifyTaskSubId=").append(unifyTaskSubId);
        }
        String taskStatusStr = UnifyTaskConstant.TASK_STATUS_MAP.get(nodeStr);
        if(StringUtils.isNotBlank(taskStatusStr)){
            mobileParamBuilder.append("&nodeStr=").append(taskStatusStr);
        }
        if(Objects.nonNull(loopCount)){
            mobileParamBuilder.append("&loopCount=").append(loopCount);
        }
        if(Objects.nonNull(businessId)){
            mobileParamBuilder.append("&businessId=").append(businessId);
        }
        mobileParamBuilder.append("&unifyTaskId=").append(unifyTaskId).append("&currTime=").append(System.currentTimeMillis()).append("&eid=").append(eid).append("&appType=").append(config.getAppType()).append("&corpId=").append(config.getDingCorpId());
        String mobileParam = mobileParamBuilder.toString();
        // 巡店计划任务（按人任务）不需要storeId、storeName和isCC参数
        if (TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskType)) {
            messageVo.setMobileParam(mobileParam);
        }else {
            messageVo.setMobileParam(mobileParam + "&storeId=" + storeId + "&storeName=" + storeName + "&isCC=" + isCC);
        }
        // 合并通知参数
        if(isCombineNotice){
            String[] taskInfo = outBusinessId.split(Constants.UNDERLINE);
            Long taskLoopCount = Long.parseLong(taskInfo[3]);
            messageVo.setMobileParam(DingMsgEnum.TASKNOTICECOMBINE.getDesc() + "&unifyTaskId="+ unifyTaskId+ "&loopCount="+ taskLoopCount+"&nodeStr=" + nodeStr + "&currTime=" + System.currentTimeMillis()+"&eid=" + eid + "&appType="+config.getAppType()+"&isCC="+isCC+"&corpId="+config.getDingCorpId());
            storeName = taskName;
        }
        // 图片地址
        messageVo.setPicUrl(UnifyTaskPicUrlEnum.getByCode(taskType));
        // 小程序标题
        messageVo.setTitle(taskName);
        // 小程序标题  不为空则按照此标题
        List<JmsContentParamsVo> titleVos = Lists.newArrayListWithCapacity(1);
        // 小程序内容

        List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(eid, handleUserId);
        //设置业务消息id标识
        messageVo.setOutBusinessId(outBusinessId);
        // 创建钉钉待办
        //添加企业id与子任务id，存储redis时作为键值对
        log.info("EnterpriseSettingDO={},messageVo1234 = {}", JSON.toJSONString(setting), JSON.toJSONString(messageVo));
        //判断是否是需要发送待办
        if (setting != null
            && setting.getSendUpcoming()
            && !UnifyNodeEnum.CC.getCode().equals(nodeStr)
            && !Constants.PRODUCT_REMIND.equals(nodeStr)
            && AppTypeEnum.isDingType(config.getAppType())
            && !TaskTypeEnum.SELF_PATROL_STORE.getCode().equals(taskType)
            && endTime != null) {
            //当获取的setting不为null且获取的发送待办标志为true时，发送待办 钉钉平台发送待办
            sendBacklog(eid,unifyTaskSubId,config.getDingCorpId(), handleUserId, taskType, messageVo.getMobileParam(), storeName, createUserName, endTime,config.getAppType(), unifyTaskId, storeId, isCombineNotice, outBusinessId);
        }
        log.info("sendOAMessageLogic...titleVos:{},contentVos:{},userList:{},messageVo:{}",
                JSONObject.toJSONString(titleVos),
                JSONObject.toJSONString(contentVos),
                JSONObject.toJSONString(userList),
                JSONObject.toJSONString(messageVo)
                );
        jmsSendMessageLogicService.sendOAMessageLogic(titleVos, contentVos, userList, messageVo);
        wechatService.sendWXMsg(config, handleUserId, taskName, DateUtil.format(beginTime, DatePattern.NORM_DATETIME_MINUTE_PATTERN), messageVo.getMobileParam(), outBusinessId, storeId);
    }

    @Override
    @Async("noticeThreadPool")
    public void sendUnifyTaskJms(String taskType, List<String> handleUserId, String nodeStr, String eid, String storeName, Long unifyTaskSubId, String createUserName, Long endTime, String taskName, Boolean isTransmit, Long beginTime, String storeId, String outBusinessId, Boolean isCC, Long unifyTaskId, Long cycleCount) {
        sendUnifyTaskJms(taskType, handleUserId, nodeStr, eid, storeName, unifyTaskSubId, createUserName, endTime, taskName, isTransmit, beginTime, storeId, outBusinessId, isCC, unifyTaskId, cycleCount, null, null);
    }


    @Override
    public void sendQuestionMessage(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount, String operate, Map<String, String> paramMap) {
        //删除操作不走该逻辑
        if(UnifyTaskConstant.TaskMessage.OPERATE_DELETE.equals(operate)){
            return;
        }
        if(Objects.isNull(paramMap)){
            paramMap = new HashMap<>();
        }
        EnterpriseConfigDTO config = null;
        DataSourceHelper.reset();
        EnterpriseQuestionSettingsDTO questionSetting = enterpriseSettingRpcService.getQuestionSetting(enterpriseId);
        EnterpriseSettingsDTO enterpriseSetting = enterpriseSettingRpcService.getEnterpriseSetting(enterpriseId);
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
        }
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        TbQuestionRecordDO questionRecord = questionRecordDao.selectByTaskIdAndStoreId(enterpriseId, unifyTaskId, storeId, loopCount);
        TaskStoreDO taskStore = unifyTaskStoreService.getTaskStoreDetail(enterpriseId, unifyTaskId, storeId, loopCount);
        TbQuestionParentInfoDO questionParentInfo = questionParentInfoDao.selectByUnifyTaskId(enterpriseId, unifyTaskId);
        if(Objects.isNull(config) || Objects.isNull(questionSetting) || Objects.isNull(taskStore) || Objects.isNull(questionRecord) || Objects.isNull(questionParentInfo)){
            log.info("相关数据不存在, 发送失败");
            return;
        }
        Map<String, List<String>> nodePersonMap = unifyTaskStoreService.selectTaskStorAllNodePerson(enterpriseId, unifyTaskId, storeId, loopCount);
        String taskType = taskStore.getTaskType();
        String taskName = UnifyTaskConstant.TaskMessage.OPERATE_ADD.equals(operate) ? questionRecord.getParentQuestionName() : questionRecord.getTaskName();
        String createUserId = taskStore.getCreateUserId();
        String nodeNo = taskStore.getNodeNo();
        //获取当前前端对应的人
        List<String> sendUserIds = getSendUserIds(enterpriseId, nodePersonMap, operate, nodeNo, questionRecord.getId());
        List<String> userIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(sendUserIds)){
            userIds.addAll(sendUserIds);
        }
        userIds.add(createUserId);
        if(UnifyTaskConstant.TaskMessage.OPERATE_TURN.equals(operate)){
            userIds.add(paramMap.get("fromUserId"));
            userIds.add(paramMap.get("toUserId"));
        }
        String handlerUserId = questionRecord.getHandleUserId();
        if(StringUtils.isNotBlank(handlerUserId)){
            userIds.add(questionRecord.getHandleUserId());
        }
        Map<String, String> userNameMap = enterpriseUserService.getUserNameMap(enterpriseId, userIds);
        String createUserName = userNameMap.get(createUserId);
        if(StringUtils.isNotBlank(handlerUserId)){
            paramMap.put("handlerUserName", userNameMap.get(handlerUserId));
        }
        String storeName = taskStore.getStoreName();
        Long parentQuestionId = questionRecord.getParentQuestionId();
        paramMap.put("taskName", taskName);
        paramMap.put("storeName", storeName);
        paramMap.put("createUserName", createUserName);
        paramMap.put("createTime", DateUtil.format(taskStore.getCreateTime(), DateUtils.DATE_FORMAT_MINUTE));
        paramMap.put("endTime",DateUtil.format(questionRecord.getSubEndTime(), DateUtils.DATE_FORMAT_MINUTE));
        if(UnifyTaskConstant.TaskMessage.OPERATE_TURN.equals(operate)){
            paramMap.put("fromUserName", userNameMap.get(paramMap.get("fromUserId")));
            paramMap.put("toUserName", userNameMap.get(paramMap.get("toUserId")));
        }
        String mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&storeId={4}&unifyTaskId={5}&loopCount={6}&currTime={7}",
                DingMsgEnum.getByCode(taskType), enterpriseId, config.getDingCorpId(),config.getAppType(), storeId, String.valueOf(unifyTaskId), String.valueOf(loopCount), String.valueOf(System.currentTimeMillis()));;
        if(UnifyTaskConstant.TaskMessage.OPERATE_ADD.equals(operate)){
            mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&questionParentInfoId={4}&totalNum={5}&currTime={6}",
                    DingMsgEnum.getByCode(taskType), enterpriseId, config.getDingCorpId(),config.getAppType(), String.valueOf(parentQuestionId), String.valueOf(questionParentInfo.getTotalNum()), String.valueOf(System.currentTimeMillis()));
        }
        boolean isSendMessage = isSendMessageHandlerAndApproveUser(questionSetting, nodeNo, operate);
        boolean isSendBackLog = Optional.ofNullable(enterpriseSetting).map(EnterpriseSettingsDTO::getSendUpcoming).orElse(Boolean.FALSE);
        //获取消息标题和内容
        MessageDealDTO messageTitleAndContent = getQuestionMessageTitleAndContent(config.getDingCorpId(), nodeNo, operate, SendUserTypeEnum.HANDLER_USER, paramMap, config.getAppType());
        if(Objects.isNull(messageTitleAndContent)){
            return;
        }
        String content = messageTitleAndContent.getContent();
        String title = messageTitleAndContent.getTitle();
        if(isSendMessage && CollectionUtils.isNotEmpty(sendUserIds)){
            sendUserIds = distinctUserIds(enterpriseId, operate, sendUserIds, parentQuestionId, questionRecord.getId(), questionRecord.getApproveRejectCount(), nodeNo);
            String outBusinessId = enterpriseId + "_" + parentQuestionId + "_" + nodeNo + "_" + System.currentTimeMillis();
            log.info("#########---> 处理人:{}, title:{}, content:{}", JSONObject.toJSONString(sendUserIds), title, content);
            sendMessage(config.getDingCorpId(), sendUserIds, outBusinessId, config.getAppType(), title, content, mobileParam, UnifyTaskPicUrlEnum.getByCode(taskType));
            wechatService.sendWXMsg(config, sendUserIds, taskName, DateUtil.format(questionRecord.getSubBeginTime(), DatePattern.NORM_DATETIME_MINUTE_PATTERN), mobileParam);
        }
        boolean isExpire = UnifyTaskConstant.TaskMessage.EXPIRE_BEFORE_REMIND.equals(operate) || UnifyTaskConstant.TaskMessage.EXPIRE_REMIND.equals(operate);
        //判断是否是需要发送待办
        if (isSendMessage && isSendBackLog && !UnifyNodeEnum.CC.getCode().equals(nodeNo) && !isExpire) {
            //当获取的setting不为null且获取的发送待办标志为true时，发送待办 钉钉平台发送待办
            sendQuestionBacklog(enterpriseId, config.getDingCorpId(), sendUserIds, taskType, mobileParam, storeName, createUserName, questionRecord.getSubEndTime().getTime(), config.getAppType(), questionRecord.getParentQuestionId());
        }
        //是否给抄送人和创建人发消息
        Pair<Boolean, Boolean> isSendCreateUserAndCCUser = isSendMessageCreateUserAndCCUser(questionSetting, nodeNo, operate);
        log.info("isSendCreateUserAndCCUser:创建人：{}，抄送人：{}", isSendCreateUserAndCCUser.getLeft(), isSendCreateUserAndCCUser.getRight());
        if(isSendCreateUserAndCCUser.getRight()){
            List<String> ccUserIds = distinctUserIds(enterpriseId, operate, nodePersonMap.get(UnifyNodeEnum.CC.getCode()), parentQuestionId, questionRecord.getId(), questionRecord.getApproveRejectCount(), nodeNo);
            messageTitleAndContent = getQuestionMessageTitleAndContent(config.getDingCorpId(), nodeNo, operate, SendUserTypeEnum.CC_USER, paramMap, config.getAppType());
            if(Objects.nonNull(messageTitleAndContent) && CollectionUtils.isNotEmpty(ccUserIds)){
                content = messageTitleAndContent.getContent();
                title = messageTitleAndContent.getTitle();
                String outBusinessId = enterpriseId + "_" + parentQuestionId + "_" + nodeNo + "_" + System.currentTimeMillis();
                log.info("#########--->抄送人:{},title:{}, content:{}", JSONObject.toJSONString(ccUserIds), title, content);
                mobileParam = mobileParam + "&msgType=" + SendUserTypeEnum.CC_USER.getCode();
                sendMessage(config.getDingCorpId(), ccUserIds, outBusinessId, config.getAppType(), title, content, mobileParam, UnifyTaskPicUrlEnum.getByCode(taskType));
                List<String> finalSendUserIds = sendUserIds;
                if(CollectionUtils.isNotEmpty(finalSendUserIds)){
                    wechatService.sendWXMsg(config, ccUserIds.stream().filter(o->!finalSendUserIds.contains(o)).collect(Collectors.toList()), taskName, DateUtil.format(questionRecord.getSubBeginTime(), DatePattern.NORM_DATETIME_MINUTE_PATTERN), mobileParam);
                }
            }
        }
        if(isSendCreateUserAndCCUser.getLeft()){
            messageTitleAndContent = getQuestionMessageTitleAndContent(config.getDingCorpId(), nodeNo, operate, SendUserTypeEnum.CREATE_USER, paramMap, config.getAppType());
            if(Objects.nonNull(messageTitleAndContent)){
                content = messageTitleAndContent.getContent();
                title = messageTitleAndContent.getTitle();
                String outBusinessId = enterpriseId + "_" + parentQuestionId + "_" + nodeNo + "_" +MD5Util.md5(JSONUtil.toJsonStr(Arrays.asList(createUserId)));
                List<String> createUserIds = distinctUserIds(enterpriseId, operate, Arrays.asList(createUserId), parentQuestionId, questionRecord.getId(), questionRecord.getApproveRejectCount(), nodeNo);
                log.info("#########--->发起人:{}, title:{}, content:{}",JSONObject.toJSONString(createUserIds), title, content);
                sendMessage(config.getDingCorpId(), createUserIds, outBusinessId, config.getAppType(), title, content, mobileParam, UnifyTaskPicUrlEnum.getByCode(taskType));
                List<String> finalSendUserIds = sendUserIds;
                if(CollectionUtils.isNotEmpty(createUserIds) && CollectionUtils.isNotEmpty(finalSendUserIds)){
                    wechatService.sendWXMsg(config, createUserIds.stream().filter(o->!finalSendUserIds.contains(o)).collect(Collectors.toList()), taskName, DateUtil.format(questionRecord.getSubBeginTime(), DatePattern.NORM_DATETIME_MINUTE_PATTERN), mobileParam);
                }
            }
        }
    }

    /**
     * 发送消息去重
     * @param operate
     * @param sendUserIds
     * @param parentQuestionId
     * @param questionId
     * @return
     */
    public List<String> distinctUserIds(String enterpriseId, String operate, List<String> sendUserIds, Long parentQuestionId, Long questionId, Integer rejectCount, String nodeNo){
        List<String> resultList = new ArrayList<>();
        if(CollectionUtils.isEmpty(sendUserIds)){
            return resultList;
        }
        for (String sendUserId : sendUserIds) {
            String key = null;
            if(UnifyTaskConstant.TaskMessage.OPERATE_ADD.equals(operate)){
                key = MessageFormat.format(RedisConstant.QUESTION_NOTICE_KEY, enterpriseId, String.valueOf(parentQuestionId), operate, rejectCount,nodeNo,  "null", sendUserId);
            }else{
                key = MessageFormat.format(RedisConstant.QUESTION_NOTICE_KEY,enterpriseId, String.valueOf(parentQuestionId), operate, rejectCount, nodeNo, String.valueOf(questionId), sendUserId);
            }
            boolean isSuccess = redisUtilPool.setNxExpire(key, sendUserId, RedisConstant.THREE_DAY);
            if(isSuccess){
                resultList.add(sendUserId);
            }
        }
        return resultList;
    }

    @Override
    public void sendQuestionReminder(String enterpriseId, Long parentQuestionId, Long unifyTaskId, String storeId, Long loopCount, List<String> userIds, String title, String content) {
        EnterpriseConfigDTO config = null;
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
        }
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        TbQuestionParentInfoDO questionParentInfo = questionParentInfoDao.selectByUnifyTaskId(enterpriseId, unifyTaskId);
        if(Objects.isNull(questionParentInfo)){
            return;
        }
        String dingCorpId = config.getDingCorpId();
        String appType = config.getAppType();
        String mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&questionParentInfoId={4}&totalNum={5}&currTime={6}",
                DingMsgEnum.getByCode(TaskTypeEnum.QUESTION_ORDER.getCode()), enterpriseId, dingCorpId, appType, String.valueOf(parentQuestionId), String.valueOf(questionParentInfo.getTotalNum()), String.valueOf(System.currentTimeMillis()));

        if(Objects.nonNull(unifyTaskId) && StringUtils.isNotBlank(storeId) && Objects.nonNull(loopCount)){
            mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&unifyTaskId={4}&storeId={5}&loopCount={6}&currTime={7}",
                    DingMsgEnum.getByCode(TaskTypeEnum.QUESTION_ORDER.getCode()), enterpriseId, dingCorpId, appType, String.valueOf(unifyTaskId), storeId, String.valueOf(loopCount), String.valueOf(System.currentTimeMillis()));
        }
        log.info("$$$$:{}，userIds:{}", content, JSONObject.toJSONString(userIds));
        String outBusinessId = enterpriseId + "_" + parentQuestionId  + MD5Util.md5(JSONUtil.toJsonStr(mobileParam));
        //数智门店和酷店掌的url不同
        String messageUrl = getMessageUrl(dingCorpId, appType, mobileParam);
        SendMessageDTO messageDTO = new SendMessageDTO();
        messageDTO.setCorpId(dingCorpId);
        messageDTO.setUserIds(String.join(Constants.COMMA, userIds));
        messageDTO.setOutBusinessId(outBusinessId);
        messageDTO.setAppType(appType);
        JSONObject map = new JSONObject();
        map.put("message_url", messageUrl);
        JSONObject headJson = new JSONObject();
        headJson.put("bgcolor", "FFBBBBBB");
        map.put("head", headJson);
        JSONObject body = new JSONObject();
        body.put("title", title);
        body.put("image", UnifyTaskPicUrlEnum.QUESTION_ORDER.getDesc());
        body.put("content", content);
        map.put("body",body);
        messageDTO.setOaJson(map);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
        wechatService.sendWXMsg(config, userIds, questionParentInfo.getQuestionName(), DateUtil.format(questionParentInfo.getCreateTime(), DatePattern.NORM_DATETIME_MINUTE_PATTERN), mobileParam);
    }

    @Override
    public void sendDeleteQuestionReminder(String enterpriseId, Map<SendUserTypeEnum, List<String>> nodePersonMap, String title, String content, Long parentQuestionId, Long questionRecordId) {
        log.info("删除工单：enterpriseId：{}，nodePersonMap：{}，title：{}，content：{}", enterpriseId, JSONObject.toJSONString(nodePersonMap), title, content);
        EnterpriseQuestionSettingsDTO questionSetting = enterpriseSettingRpcService.getQuestionSetting(enterpriseId);
        if(Objects.isNull(questionSetting)){
            return;
        }
        if(Objects.isNull(nodePersonMap)){
            return;
        }
        List<String> userIds = new ArrayList<>();
        //是否发送消息
        List<Boolean> deleteQuestionRemind = questionSetting.getDeleteQuestionRemind();
        nodePersonMap.forEach((k, v) ->{
            Boolean isSend = deleteQuestionRemind.get(k.getCode());
            if(isSend && CollectionUtils.isNotEmpty(v)){
                for (String userId : v) {
                    if(StringUtils.isBlank(userId)){
                        continue;
                    }
                    String key = MessageFormat.format(RedisConstant.QUESTION_NOTICE_KEY, enterpriseId, String.valueOf(parentQuestionId), UnifyTaskConstant.TaskMessage.OPERATE_DELETE, 0, 0,  String.valueOf(questionRecordId), userId);
                    boolean isSuccess = redisUtilPool.setNxExpire(key, userId, RedisConstant.THREE_DAY);
                    if(isSuccess){
                        userIds.add(userId);
                    }
                }
            }
        });
        if(CollectionUtils.isEmpty(userIds)){
            return;
        }
        EnterpriseConfigDTO config = null;
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
        }
        String dingCorpId = config.getDingCorpId();
        String appType = config.getAppType();
        String outBusinessId = UUIDUtils.get8UUID();
        SendTextMessageDTO messageDTO = new SendTextMessageDTO();
        messageDTO.setCorpId(dingCorpId);
        messageDTO.setUserIds(String.join(Constants.COMMA, userIds));
        messageDTO.setOutBusinessId(outBusinessId);
        messageDTO.setAppType(appType);
        messageDTO.setMessageType("text");
        messageDTO.setTitle(title);
        messageDTO.setContent(content);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
        //wechatService.sendWXMsg(config, userIds, title, content, null);
    }

    @Override
    public void sendLicenseMessage(String enterpriseId, String dingCorpId, String appType, List<String> userIds, String title, String content, String imageUrl, Long noticeSettingId, String type) {
        SendMessageDTO messageDTO = new SendMessageDTO();
        messageDTO.setCorpId(dingCorpId);
        messageDTO.setUserIds(String.join(Constants.COMMA, userIds));
        messageDTO.setOutBusinessId(UUIDUtils.get32UUID());
        messageDTO.setAppType(appType);
        JSONObject map = new JSONObject();
        String mobileParam = MessageFormat.format("licence&eid={0}&corpId={1}&appType={2}&noticeSettingId={3}&noticeDate={4}&type={5}", enterpriseId, dingCorpId, appType, noticeSettingId, DateUtil.format(new Date(), DateUtils.DATE_FORMAT_SEC), type);
        String messageUrl = getMessageUrl(dingCorpId, appType, mobileParam);
        map.put("message_url", messageUrl);
        JSONObject headJson = new JSONObject();
        headJson.put("bgcolor", "FFBBBBBB");
        map.put("head", headJson);
        JSONObject body = new JSONObject();
        body.put("title", title);
        body.put("image", imageUrl);
        body.put("content", content);
        map.put("body",body);
        messageDTO.setOaJson(map);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
        //wechatService.sendWXMsg(enterpriseId, userIds, title, content, mobileParam);
    }

    @Override
    public void sendTextMessage(String enterpriseId, List<String> userIds, String title, String content) {
        EnterpriseConfigDTO config = null;
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
        }
        SendTextMessageDTO messageDTO = new SendTextMessageDTO();
        messageDTO.setCorpId(config.getDingCorpId());
        messageDTO.setUserIds(String.join(Constants.COMMA, userIds));
        messageDTO.setOutBusinessId(UUIDUtils.get32UUID());
        messageDTO.setAppType(config.getAppType());
        messageDTO.setMessageType("text");
        messageDTO.setTitle(title);
        messageDTO.setContent(content);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
    }

    @Override
    public void sendPatrolStoreReminder(String enterpriseId, TaskParentDO taskParentDO, List<String> userIds, String content) {
        EnterpriseConfigDTO config = null;
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
        }
        String dingCorpId = config.getDingCorpId();
        String appType = config.getAppType();
        String mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&unifyTaskId={4}&taskType={5}&taskName={6}&currTime={7}",
                DingMsgEnum.URGE_TASK.getDesc(), enterpriseId, config.getDingCorpId(),config.getAppType(), taskParentDO.getId(), taskParentDO.getTaskType(), taskParentDO.getTaskName(), String.valueOf(System.currentTimeMillis()));

        log.info("sendPatrolStoreReminder巡店提醒内容:{}，userIds:{}", content, JSONObject.toJSONString(userIds));
        String outBusinessId = enterpriseId + "_" + taskParentDO.getId()  + MD5Util.md5(JSONUtil.toJsonStr(mobileParam));
        //数智门店和酷店掌的url不同
        String messageUrl = getMessageUrl(dingCorpId, appType, mobileParam);
        SendMessageDTO messageDTO = new SendMessageDTO();
        messageDTO.setCorpId(dingCorpId);
        messageDTO.setUserIds(String.join(Constants.COMMA, userIds));
        messageDTO.setOutBusinessId(outBusinessId);
        messageDTO.setAppType(appType);
        JSONObject map = new JSONObject();
        map.put("message_url", messageUrl);
        JSONObject headJson = new JSONObject();
        headJson.put("bgcolor", "FFBBBBBB");
        map.put("head", headJson);
        JSONObject body = new JSONObject();
        body.put("title", taskParentDO.getTaskName());
        body.put("image", UnifyTaskPicUrlEnum.PATROL_STORE_OFFLINE.getDesc());
        body.put("content", content);
        map.put("body",body);
        messageDTO.setOaJson(map);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
    }

    public List<String> getSendUserIds(String enterpriseId, Map<String, List<String>> nodePersonMap, String operate, String nodeNo, Long questionRecordId){
        if(Objects.isNull(nodePersonMap)){
            return Lists.newArrayList();
        }
        List<String> sendUserIds = nodePersonMap.get(nodeNo);
        if(UnifyTaskConstant.TaskMessage.OPERATE_REJECT.equals(operate)){
            TbQuestionHistoryDO questionHistory = questionHistoryDao.selectLatestHistoryListByRecordId(enterpriseId, questionRecordId, UnifyNodeEnum.FIRST_NODE.getCode());
            String handlerUserId = Optional.ofNullable(questionHistory).map(o -> o.getOperateUserId()).orElse("");
            return new ArrayList<>(Arrays.asList(handlerUserId));
        }
        return sendUserIds;
    }

    /**
     * 是否给处理人或审批人发消息
     * @param questionSetting
     * @param nodeNo
     * @param operate
     * @return
     */
    private boolean isSendMessageHandlerAndApproveUser(EnterpriseQuestionSettingsDTO questionSetting, String nodeNo, String operate){
        boolean isSendMessage = false;
        //创建，转交、重新分配 都回收到提醒
        if(UnifyNodeEnum.FIRST_NODE.getCode().equals(nodeNo) || UnifyTaskConstant.TaskMessage.OPERATE_TURN.equals(operate) ||  UnifyTaskConstant.TaskMessage.OPERATE_REALLOCATE.equals(operate)){
            isSendMessage = true;
        }
        //审批动作
        if(UnifyNodeEnum.isApproveNode(nodeNo)){
            List<Boolean> handleSubmitRemind = questionSetting.getHandleSubmitRemind();
            isSendMessage = handleSubmitRemind.get(SendUserTypeEnum.APPROVE_USER.getCode());
        }
        //逾期前提醒
        if(UnifyTaskConstant.TaskMessage.EXPIRE_BEFORE_REMIND.equals(operate)){
            boolean approveNode = UnifyNodeEnum.isApproveNode(nodeNo);
            SendUserTypeEnum sendUserType = approveNode ? SendUserTypeEnum.APPROVE_USER : SendUserTypeEnum.HANDLER_USER;
            List<Boolean> handleSubmitRemind = questionSetting.getExpireBeforeQuestionRemind();
            isSendMessage = handleSubmitRemind.get(sendUserType.getCode());
        }
        //逾期提醒
        if(UnifyTaskConstant.TaskMessage.EXPIRE_REMIND.equals(operate)){
            boolean approveNode = UnifyNodeEnum.isApproveNode(nodeNo);
            SendUserTypeEnum sendUserType = approveNode ? SendUserTypeEnum.APPROVE_USER : SendUserTypeEnum.HANDLER_USER;
            List<Boolean> handleSubmitRemind = questionSetting.getExpireQuestionRemind();
            isSendMessage = handleSubmitRemind.get(sendUserType.getCode());
        }
        return isSendMessage;
    }

    private Pair<Boolean, Boolean> isSendMessageCreateUserAndCCUser(EnterpriseQuestionSettingsDTO questionSetting, String nodeNo, String operate){
        boolean isSendCCUser = false, isSendCreateUser = false;
        //抄送人
        if(UnifyNodeEnum.FIRST_NODE.getCode().equals(nodeNo)){
            //创建工单
            isSendCCUser = questionSetting.getCreateQuestionRemind().get(SendUserTypeEnum.CC_USER.getCode());
        }
        if(UnifyNodeEnum.isApproveNode(nodeNo)){
            //整改人提交
            isSendCCUser = questionSetting.getHandleSubmitRemind().get(SendUserTypeEnum.CC_USER.getCode());
            isSendCreateUser = questionSetting.getHandleSubmitRemind().get(SendUserTypeEnum.CREATE_USER.getCode());
        }
        if(UnifyNodeEnum.END_NODE.getCode().equals(nodeNo)){
            //工单完成
            isSendCCUser = questionSetting.getFinishQuestionRemind().get(SendUserTypeEnum.CC_USER.getCode());
            isSendCreateUser = questionSetting.getFinishQuestionRemind().get(SendUserTypeEnum.CREATE_USER.getCode());
        }
        if(UnifyTaskConstant.TaskMessage.OPERATE_DELETE.equals(operate)){
            //工单删除
            isSendCCUser = questionSetting.getDeleteQuestionRemind().get(SendUserTypeEnum.CC_USER.getCode());
            isSendCreateUser = questionSetting.getDeleteQuestionRemind().get(SendUserTypeEnum.CREATE_USER.getCode());
        }
        if(UnifyTaskConstant.TaskMessage.OPERATE_TURN.equals(operate)){
            //工单转交
            isSendCCUser = questionSetting.getTurnQuestionRemind().get(SendUserTypeEnum.CC_USER.getCode());
            isSendCreateUser = questionSetting.getTurnQuestionRemind().get(SendUserTypeEnum.CREATE_USER.getCode());
        }
        if(UnifyTaskConstant.TaskMessage.OPERATE_REALLOCATE.equals(operate)){
            //重新分配
            isSendCCUser = questionSetting.getReallocateQuestionRemind().get(SendUserTypeEnum.CC_USER.getCode());
            isSendCreateUser = questionSetting.getReallocateQuestionRemind().get(SendUserTypeEnum.CREATE_USER.getCode());
        }
        //逾期前提醒
        if(UnifyTaskConstant.TaskMessage.EXPIRE_BEFORE_REMIND.equals(operate)){
            List<Boolean> expireBeforeQuestionRemind = questionSetting.getExpireBeforeQuestionRemind();
            isSendCCUser = expireBeforeQuestionRemind.get(SendUserTypeEnum.CC_USER.getCode());
            isSendCreateUser = expireBeforeQuestionRemind.get(SendUserTypeEnum.CREATE_USER.getCode());
        }
        //逾期提醒
        if(UnifyTaskConstant.TaskMessage.EXPIRE_REMIND.equals(operate)){
            List<Boolean> expireQuestionRemind = questionSetting.getExpireQuestionRemind();
            isSendCCUser = expireQuestionRemind.get(SendUserTypeEnum.CC_USER.getCode());
            isSendCreateUser = expireQuestionRemind.get(SendUserTypeEnum.CREATE_USER.getCode());
        }
        return Pair.of(isSendCreateUser, isSendCCUser);
    }

    /**
     * 获取工单通知标题和内容
     * @param nodeNo
     * @param operate
     * @param sendUserType
     * @param param
     * @return
     */
    private MessageDealDTO getQuestionMessageTitleAndContent(String dingCorpId, String nodeNo, String operate, SendUserTypeEnum sendUserType, Map<String, String> param, String appType){
        String title = MessageDealDTO.QUESTION_TITLE;
        String content = AppTypeEnum.isQwType(appType) ? StringUtil.format(MessageDealDTO.QW_QUESTION_CONTENT, param) : StringUtil.format(MessageDealDTO.QUESTION_CONTENT, param);
        if(AppTypeEnum.qwIsGetUserName(appType)){
            content = StringUtil.format(MessageDealDTO.QW_DKF_QUESTION_CONTENT, param);
        }
        String storeName = param.get("storeName");
        //抄送人
        if(SendUserTypeEnum.CC_USER.equals(sendUserType)){
            //收到工单  抄送人
            title = MessageDealDTO.QUESTION_CC_TITLE;
            if(UnifyNodeEnum.isApproveNode(nodeNo) && !UnifyTaskConstant.TaskMessage.OPERATE_REJECT.equals(operate)){
                //收到工单  抄送人
                title = storeName;
                content = AppTypeEnum.isQwType(appType) ? StringUtil.format(MessageDealDTO.QW_QUESTION_CC_CONTENT, param) : StringUtil.format(MessageDealDTO.QUESTION_CC_CONTENT, param);
            }
            if(UnifyNodeEnum.END_NODE.getCode().equals(nodeNo)){
                title = storeName;
                content = StringUtil.format("【{storeName}】的工单【{taskName}】已完成整改，点击可查看详情。", param);
            }
            if(UnifyTaskConstant.TaskMessage.OPERATE_DELETE.equals(operate)){
                title = storeName;
                content = AppTypeEnum.isQwType(appType) ? "【{storeName}】的工单【{taskName}】已被【$userName={handlerUserName}$】删除，请知悉~" : "【{storeName}】的工单【{taskName}】已被【{handlerUserName}】删除，请知悉~" ;
                content = StringUtil.format(content, param);
            }
            if(UnifyTaskConstant.TaskMessage.OPERATE_TURN.equals(operate)){
                title = storeName;
                content = AppTypeEnum.isQwType(appType) ? "【{storeName}】的工单【{taskName}】已被【$userName={fromUserName}$】转交给【$userName={toUserName}$】，点击可查看详情。" : "【{storeName}】的工单【{taskName}】已被【{fromUserName}】转交给【{toUserName}】，点击可查看详情。";
                content = StringUtil.format(content, param);
            }
            if(UnifyTaskConstant.TaskMessage.OPERATE_REALLOCATE.equals(operate)){
                title = storeName;
                content = StringUtil.format("【{storeName}】的工单【{taskName}】指派人员已重新分配，点击可查看详情。", param);
            }
            //逾期前提醒
            if(UnifyTaskConstant.TaskMessage.EXPIRE_BEFORE_REMIND.equals(operate)){
                title = storeName;
                content = StringUtil.format("【{storeName}】的工单【{taskName}】即将逾期，点击可查看详情。", param);
            }
            //逾期提醒
            if(UnifyTaskConstant.TaskMessage.EXPIRE_REMIND.equals(operate)){
                title = storeName;
                content = StringUtil.format("【{storeName}】的工单【{taskName}】逾期未处理，点击可查看详情。", param);
            }
        }
        //发起人
        if(SendUserTypeEnum.CREATE_USER.equals(sendUserType)){
            //收到工单  抄送人
            title = storeName;
            if(UnifyTaskConstant.TaskMessage.OPERATE_ADD.equals(operate)){
                return null;
            }
            if(UnifyNodeEnum.isApproveNode(nodeNo)){
                content = AppTypeEnum.isQwType(appType)? "【{storeName}】的工单【{taskName}】已被【$userName={handlerUserName}$】处理，点击可查看详情" : "【{storeName}】的工单【{taskName}】已被【{handlerUserName}】处理，点击可查看详情";
                content = StringUtil.format(content, param);
            }
            if(UnifyNodeEnum.END_NODE.getCode().equals(nodeNo)){
                content = StringUtil.format("【{storeName}】的工单【{taskName}】已完成整改，点击可查看详情。", param);
            }
            if(UnifyTaskConstant.TaskMessage.OPERATE_DELETE.equals(operate)){
                content = AppTypeEnum.isQwType(appType) ? "【{storeName}】的工单【{taskName}】已被【$userName={handlerUserName}$】删除，请知悉~" : "【{storeName}】的工单【{taskName}】已被【{handlerUserName}】删除，请知悉~";
                content = StringUtil.format(content, param);
            }
            if(UnifyTaskConstant.TaskMessage.OPERATE_TURN.equals(operate)){
                content = AppTypeEnum.isQwType(appType) ? "【{storeName}】的工单【{taskName}】已被【$userName={fromUserName}$】转交给【$userName={toUserName}$】，点击可查看详情。" : "【{storeName}】的工单【{taskName}】已被【{fromUserName}】转交给【{toUserName}】，点击可查看详情。";
                content = StringUtil.format(content, param);
            }
            if(UnifyTaskConstant.TaskMessage.OPERATE_REALLOCATE.equals(operate)){
                content = StringUtil.format("【{storeName}】的工单【{taskName}】指派人员已重新分配，点击可查看详情。", param);
            }
            //逾期前提醒
            if(UnifyTaskConstant.TaskMessage.EXPIRE_BEFORE_REMIND.equals(operate)){
                title = storeName;
                content = StringUtil.format("【{storeName}】的工单【{taskName}】即将逾期，点击可查看详情。", param);
            }
            //逾期提醒
            if(UnifyTaskConstant.TaskMessage.EXPIRE_REMIND.equals(operate)){
                title = storeName;
                content = StringUtil.format("【{storeName}】的工单【{taskName}】逾期未处理，点击可查看详情。", param);
            }
        }
        //整改人
        if(SendUserTypeEnum.HANDLER_USER.equals(sendUserType) || SendUserTypeEnum.APPROVE_USER.equals(sendUserType)){
            if(UnifyTaskConstant.TaskMessage.OPERATE_DELETE.equals(operate)){
                title = storeName;
                content = AppTypeEnum.isQwType(appType) ? "【{storeName}】的工单【{taskName}】已被【$userName={handlerUserName}$】删除，请知悉~" : "【{storeName}】的工单【{taskName}】已被【{handlerUserName}】删除，请知悉~";
                content = StringUtil.format(content, param);
            }
            if(UnifyTaskConstant.TaskMessage.OPERATE_TURN.equals(operate)){
                title = MessageDealDTO.QUESTION_TITLE;
            }
            if(UnifyTaskConstant.TaskMessage.OPERATE_REALLOCATE.equals(operate)){
                title = MessageDealDTO.QUESTION_TITLE;
            }
            //逾期前提醒
            if(UnifyTaskConstant.TaskMessage.EXPIRE_BEFORE_REMIND.equals(operate)){
                title = storeName;
                content = StringUtil.format("您有【{storeName}】的工单【{taskName}】即将到期，请尽快处理。", param);
            }
            //逾期提醒
            if(UnifyTaskConstant.TaskMessage.EXPIRE_REMIND.equals(operate)){
                title = storeName;
                content = StringUtil.format("您有【{storeName}】的工单【{taskName}】逾期未处理。", param);
            }
            if (UnifyTaskConstant.TaskMessage.OPERATE_REJECT.equals(operate)){
                title="您有1个工单被驳回，请尽快处理";
                content=AppTypeEnum.isQwType(appType) ? StringUtil.format("工单名称：{taskName}\n" + "截至时间：{endTime}",param) :StringUtil.format("##### 工单名称：{taskName}\n" + "##### 截至时间：{endTime}",param);
            }
        }
        return new MessageDealDTO(title, content.replace(dingCorpId + Constants.UNDERLINE,""));
    }

    /**
     *
     * @param dingCorpId
     * @param userIds
     * @param outBusinessId
     * @param appType
     * @param title
     * @param content
     * @param mobileParam
     * @param imageUrl
     */
    private void sendMessage(String dingCorpId, List<String> userIds, String outBusinessId, String appType, String title, String content, String mobileParam, String imageUrl){
        if(CollectionUtils.isEmpty(userIds)){
            return;
        }
        userIds = userIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(userIds)){
            return;
        }
        //数智门店和酷店掌的url不同
        String messageUrl = getMessageUrl(dingCorpId, appType, mobileParam);
        SendMessageDTO messageDTO = new SendMessageDTO();
        messageDTO.setCorpId(dingCorpId);
        messageDTO.setUserIds(String.join(Constants.COMMA, userIds));
        messageDTO.setOutBusinessId(outBusinessId);
        messageDTO.setAppType(appType);
        JSONObject map = new JSONObject();
        map.put("message_url", messageUrl);
        JSONObject headJson = new JSONObject();
        headJson.put("bgcolor", "FFBBBBBB");
        map.put("head", headJson);
        JSONObject body = new JSONObject();
        body.put("title", title);
        body.put("image", imageUrl);
        body.put("content", content);
        map.put("body",body);
        messageDTO.setOaJson(map);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
        if(AppTypeEnum.APP.getValue().equals(appType)){
            AppPushMsgDTO appPushMsgDTO = new AppPushMsgDTO();
            appPushMsgDTO.setTitle(title);
            appPushMsgDTO.setContent(content);
            appPushMsgDTO.setPushType("ACCOUNT");
            String targetValue = String.join(",", ListUtils.emptyIfNull(userIds));
            appPushMsgDTO.setPushTarget(targetValue);
            AppExtraParamDTO appExtraParamDTO =new AppExtraParamDTO();
            appExtraParamDTO.setMessageId(UUIDUtils.get32UUID());
            appExtraParamDTO.setMessageUrl(messageUrl);
            appExtraParamDTO.setMessageType(2);
            appPushMsgDTO.setExtraParam(appExtraParamDTO);
            simpleMessageService.send(JSONObject.toJSONString(appPushMsgDTO), RocketMqTagEnum.APP_PUSH_QUEUE);
        }
    }

    /**
     * 获取消息url
     * @param dingCorpId
     * @param appType
     * @param mobileParam
     * @return
     */
    private String getMessageUrl(String dingCorpId, String appType, String mobileParam){

        String messageUrl = Constants.E_APP_LOGIN_IN_PAGE + mobileParam;;
        try {
            if (AppTypeEnum.DING_DING.getValue().equals(appType)) {
                messageUrl = coolCollegeDomainUrl + Constants.NOTICE_PAGE_PREFIX + "?miniAppId={0}&appId={1}&corpId={2}&appUrl=" + URLEncoder.encode(Constants.PAGE_URL_PREFIX + mobileParam, StandardCharsets.UTF_8.name());
                messageUrl = MessageFormat.format(messageUrl, appMiniId, appId, dingCorpId);
            }
            if (AppTypeEnum.DING_DING2.getValue().equals(appType)) {
                messageUrl = coolStoreDomainUrl + Constants.NOTICE_PAGE_PREFIX + "?miniAppId={0}&appId={1}&corpId={2}&appUrl=" + URLEncoder.encode(Constants.PAGE_URL_PREFIX + mobileParam, StandardCharsets.UTF_8.name());
                messageUrl = MessageFormat.format(messageUrl, coolAppMiniId, coolAppId, dingCorpId);
            }
            if (AppTypeEnum.WX_APP.getValue().equals(appType)) {
                //先组装参数
                messageUrl = URLEncoder.encode(qywxUrl + mobileParam, "UTF-8");
                //再组装授权链接
                messageUrl = String.format(oauthUrl, suiteId, messageUrl);
            }
            if (AppTypeEnum.WX_APP2.getValue().equals(appType)) {
                String noticeUrl = String.format(qywxUrl2, dingCorpId, appType);
                messageUrl = URLEncoder.encode(noticeUrl + mobileParam, "UTF-8");
                messageUrl = String.format(oauthUrl, suiteId2, messageUrl);
            }
            if (AppTypeEnum.isWxSelfAndPrivateType(appType)) {
                String noticeUrl = String.format(qywxUrl2, dingCorpId, appType);
                messageUrl = URLEncoder.encode(noticeUrl + mobileParam, "UTF-8");
                messageUrl = String.format(Constants.WX_SELF_AUTH_URL, dingCorpId, messageUrl);
            }
            if (AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType) || AppTypeEnum.ONE_PARTY_APP2.getValue().equals(appType)) {
                messageUrl = MessageFormat.format(onepartyNoticeUrl, dingCorpId, mobileParam);
            }
            if (AppTypeEnum.FEI_SHU.getValue().equals(appType)) {
                messageUrl = URLEncoder.encode(mobileParam, "UTF-8");
                messageUrl = String.format(feiShuNoticeUrl, messageUrl);
            }
        } catch (Exception e) {
            log.error("组装企微授权链接失败", e);
        }
        return messageUrl;
    }

    public void sendQuestionBacklog(String eid,String corpId, List<String> userIds, String taskType, String url, String storeName, String createUserName, Long endTime, String appType, Long parentQuestionId) {
        if(!AppTypeEnum.isDingType(appType)){
            return;
        }
        JSONObject send = UrlUtil.getUrlJSONObject(url);
        // 业务端自定义id  消除代办消息时用
        String backlogId = MD5Util.md5(url);
        String title = TaskTypeEnum.getByCode(taskType).getDesc();
        // 企业的corpId  isv服务获取token用
        send.put("corpId", corpId);
        // 待办标题
        send.put("title", title);
        // 待办人列表
        send.put("userIds", userIds);
        // 门店信息
        JSONObject storeItem = new JSONObject();
        storeItem.put("title", "门店名称");
        storeItem.put("content", storeName);
        // 发起人信息
        JSONObject sendUserItem = new JSONObject();
        sendUserItem.put("title", "发起人");
        sendUserItem.put("content", createUserName);
        // 截止时间信息
        JSONObject endDateItem = new JSONObject();
        endDateItem.put("title", "截止时间");
        endDateItem.put("content", DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_MINUTE));
        // 消息来源
        JSONObject sourceItem = new JSONObject();
        sourceItem.put("title", "来源");
        //来源判断
        AppTypeEnum appTypeEnum = AppTypeEnum.parseValue(appType);
        sourceItem.put("content", appTypeEnum.getMessage());
        send.put("appType",appType);
        send.put("formItemList", Arrays.asList(storeItem, sendUserItem, endDateItem,sourceItem));
        send.put("taskKey", taskType + "_" + parentQuestionId);
        // 小程序跳转链接
        log.info("待办消息类型：{}", AppTypeEnum.DING_DING.getValue().equals(appTypeEnum.getValue()));
        /*messageUrl = coolCollegeDomainUrl + Constants.NOTICE_PAGE_PREFIX + "?miniAppId={0}&appId={1}&corpId={2}&appUrl=" + URLEncoder.encode(Constants.PAGE_URL_PREFIX + mobileParam, StandardCharsets.UTF_8.name());
        String fixPrefix = String.format(Constants.BACKLOG_NEW_URL, AppTypeEnum.DINGDING.getValue().equals(appTypeEnum.getValue())?appMiniId:coolAppMiniId, AppTypeEnum.DINGDING.getValue().equals(appTypeEnum.getValue())?appId:coolAppId, corpId);*/
        String messageUrl = getMessageUrl(corpId, appType, url);
        // 业务自定义id
        send.put("backlogId", backlogId);
        //企业id
        send.put("enterpriseId", eid);
        messageUrl = messageUrl + "&backlogId=" + backlogId;
        send.put("url", messageUrl);
        log.info("待办消息内容：{}", send.toJSONString());
        simpleMessageService.send(send.toString(), RocketMqTagEnum.STORE_BACK_LOG);
    }

    // 发送督导钉钉待办
    public void sendSupervisionTaskBacklog(String eid,String corpId, List<String> userIds, String taskType, String url, String taskName,
                                           String desc, Long endTime, String appType, Long supervisionTaskId,String currentNodeStr) {
        if(!AppTypeEnum.isDingType(appType)){
            return;
        }
        JSONObject send = UrlUtil.getUrlJSONObject(url);
        // 业务端自定义id  消除代办消息时用
        String backlogId = MD5Util.md5(url);
        String title = DingMsgEnum.SUPERVISION.getDesc();
        // 企业的corpId  isv服务获取token用
        send.put("corpId", corpId);
        // 待办标题
        send.put("title", taskName);
        // 待办人列表
        send.put("userIds", userIds);
        // 任务描述
        JSONObject storeItem = new JSONObject();
        if (StringUtils.isEmpty(desc)){
            desc = "-";
        }
        storeItem.put("title", "任务描述");
        storeItem.put("content", desc);

        // 当前节点
        JSONObject currentNodeStrItem = new JSONObject();
        currentNodeStrItem.put("title", "处理节点");
        currentNodeStrItem.put("content", currentNodeStr);
        // 截止时间信息
        JSONObject endDateItem = new JSONObject();
        endDateItem.put("title", "截止时间");
        endDateItem.put("content", DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_MINUTE));
        // 消息来源
        JSONObject sourceItem = new JSONObject();
        sourceItem.put("title", "来源");
        //来源判断
        AppTypeEnum appTypeEnum = AppTypeEnum.parseValue(appType);
        sourceItem.put("content", appTypeEnum.getMessage());
        send.put("appType",appType);
        send.put("formItemList", Arrays.asList(storeItem, endDateItem,sourceItem,currentNodeStrItem));
        send.put("taskKey", taskType + "_" + supervisionTaskId);
        // 小程序跳转链接
        log.info("待办消息类型：{}", AppTypeEnum.DING_DING.getValue().equals(appTypeEnum.getValue()));
        /*messageUrl = coolCollegeDomainUrl + Constants.NOTICE_PAGE_PREFIX + "?miniAppId={0}&appId={1}&corpId={2}&appUrl=" + URLEncoder.encode(Constants.PAGE_URL_PREFIX + mobileParam, StandardCharsets.UTF_8.name());
        String fixPrefix = String.format(Constants.BACKLOG_NEW_URL, AppTypeEnum.DINGDING.getValue().equals(appTypeEnum.getValue())?appMiniId:coolAppMiniId, AppTypeEnum.DINGDING.getValue().equals(appTypeEnum.getValue())?appId:coolAppId, corpId);*/
        String messageUrl = getMessageUrl(corpId, appType, url);
        // 业务自定义id
        send.put("backlogId", backlogId);
        //企业id
        send.put("enterpriseId", eid);
        messageUrl = messageUrl + "&backlogId=" + backlogId;
        send.put("url", messageUrl);
        log.info("督导待办消息内容：{}", send.toJSONString());
        //钉钉待办延迟1秒
        simpleMessageService.send(send.toString(), RocketMqTagEnum.STORE_BACK_LOG,System.currentTimeMillis() + 1000);
    }

    /**
     * 发送待办
     * @param eid
     * @param unifyTaskSubId
     * @param corpId
     * @param userIds
     * @param taskType
     * @param url
     * @param storeName
     * @param createUserName
     * @param endTime
     * @param appType
     * @param unifyTaskId
     * @param storeId
     */
    public void sendBacklog(String eid,Long unifyTaskSubId,String corpId, List<String> userIds, String taskType, String url, String storeName,
                            String createUserName, Long endTime,String appType, Long unifyTaskId, String storeId, Boolean isCombineNotice, String outBusinessId) {
        if(!AppTypeEnum.isDingType(appType)){
            return;
        }
        String title = TaskTypeEnum.getByCode(taskType).getDesc();
        JSONObject send = new JSONObject();
        // 企业的corpId  isv服务获取token用
        send.put("corpId", corpId);
        // 待办标题
        send.put("title", title);
        // 待办人列表
        send.put("userIds", userIds);

        // 门店信息
        JSONObject storeItem = new JSONObject();
        if(TaskTypeEnum.PATROL_STORE_PLAN.getCode().equals(taskType)) {
            storeItem.put("title", "计划名称");
        }else {
            if(isCombineNotice){
                storeItem.put("title", "任务名称");
            }else {
                storeItem.put("title", "门店名称");
            }
        }
        storeItem.put("content", storeName);
        // 发起人信息
        JSONObject sendUserItem = new JSONObject();
        sendUserItem.put("title", "发起人");
        sendUserItem.put("content", createUserName);
        // 截止时间信息
        JSONObject endDateItem = new JSONObject();
        endDateItem.put("title", "截止时间");
        endDateItem.put("content", DateUtils.convertTimeToString(endTime, DateUtils.DATE_FORMAT_MINUTE));
        // 消息来源
        JSONObject sourceItem = new JSONObject();
        sourceItem.put("title", "来源");
        //来源判断
        AppTypeEnum appTypeEnum = AppTypeEnum.parseValue(appType);
        sourceItem.put("content", appTypeEnum.getMessage());
        send.put("appType",appType);
        send.put("formItemList", Arrays.asList(storeItem, sendUserItem, endDateItem,sourceItem));
        if(isCombineNotice){
            send.put("taskKey", outBusinessId);
            send.put("outBusinessId", outBusinessId);
        }
        // 防止待办业务id与其他企业冲突
//        send.put("biz_id", PREFIX + System.currentTimeMillis());
        // 小程序跳转链接
        log.info("待办消息类型：{}", AppTypeEnum.DING_DING.getValue().equals(appTypeEnum.getValue()));
        String fixPrefix = String.format(Constants.BACKLOG_URL, AppTypeEnum.DING_DING.getValue().equals(appTypeEnum.getValue())?appMiniId:coolAppMiniId, AppTypeEnum.DING_DING.getValue().equals(appTypeEnum.getValue())?appId:coolAppId, corpId);
        // 隐藏应用待办跳转门店通
        if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(appTypeEnum.getValue())
            || AppTypeEnum.ONE_PARTY_APP2.getValue().equals(appTypeEnum.getValue())) {
            fixPrefix = Constants.ONEPARTY_BACKLOG_URL;
            url = MessageFormat.format(onepartyNoticeUrl, corpId, url);
        }
        // 业务端自定义id  消除代办消息时用
        String backlogId = MD5Util.md5(url);
        // 业务自定义id
        send.put("backlogId", backlogId);
        //子任务id
        send.put("unifyTaskSubId", unifyTaskSubId);
        //任务id
        send.put("unifyTaskId", unifyTaskId);
        //门店id
        send.put("storeId", storeId);
        //企业id
        send.put("enterpriseId", eid);
        url = url + "&backlogId=" + backlogId;
        try {
            send.put("url", fixPrefix + URLEncoder.encode(url, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            log.error("后缀链接encoding错误", e);
            return;
        }
        log.info("待办消息内容：{}", send.toJSONString());
        simpleMessageService.send(send.toString(), RocketMqTagEnum.STORE_BACK_LOG);
    }

    @Override
    public void sendStoreWorkMessage(String enterpriseId, Long dataTableId, String operate, Map<String, String> paramMap) {
        if(Objects.isNull(paramMap)){
            paramMap = new HashMap<>();
        }
        log.info("店务消息发送传参enterpriseId:{}, dataTableId:{}, operate:{}, paramMap:{}", enterpriseId, dataTableId, operate, JSONObject.toJSONString(paramMap));

        EnterpriseConfigDTO config = null;
        EnterpriseStoreWorkSettingsDTO storeWorkSetting = enterpriseSettingRpcService.getStoreWorkSetting(enterpriseId);
        EnterpriseSettingsDTO enterpriseSetting = enterpriseSettingRpcService.getEnterpriseSetting(enterpriseId);
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
        }
        //门店通不发消息
        /*if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(config.getAppType())){
            return;
        }*/
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        SwStoreWorkDataTableDO storeWorkDataTableDO = swStoreWorkDataTableDao.selectByPrimaryKey(dataTableId, enterpriseId);
        SwStoreWorkTableMappingDO storeWorkTableMappingDO = swStoreWorkTableMappingDao.selectByPrimaryKey(storeWorkDataTableDO.getTableMappingId(), enterpriseId);
        if(Objects.isNull(config) || Objects.isNull(storeWorkSetting) || Objects.isNull(storeWorkDataTableDO) || Objects.isNull(storeWorkTableMappingDO)){
            log.info("相关店务数据不存在, 发送失败");
            return;
        }
        String timeLabel = TableInfoLabelUtil.getLabel(storeWorkTableMappingDO.getTableInfo(), storeWorkDataTableDO.getWorkCycle());
        String dutyName = timeLabel + " " +storeWorkDataTableDO.getTableName();
        String cycleName = StoreWorkCycleEnum.getByCode(storeWorkDataTableDO.getWorkCycle());
        String createUserId = storeWorkDataTableDO.getCreateUserId();
        //获取发送人
        List<String> sendUserIds = getStoreWorkSendUserIds(storeWorkDataTableDO, operate, paramMap);
        List<String> userIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(sendUserIds)){
            userIds.addAll(sendUserIds);
        }
        userIds.add(createUserId);
        if(StoreWorkNoticeEnum.TURN_NOTICE.getOperate().equals(operate)){
            userIds.add(paramMap.get("fromUserId"));
            userIds.add(paramMap.get("toUserId"));
        }
        Map<String, String> userNameMap = enterpriseUserService.getUserNameMap(enterpriseId, userIds);
        String createUserName = userNameMap.get(createUserId);
        String storeName = storeWorkDataTableDO.getStoreName();
        paramMap.put("dutyName", dutyName);
        paramMap.put("cycleName", cycleName);
        paramMap.put("storeName", storeName);
        paramMap.put("createUserName", createUserName);
        if(StoreWorkNoticeEnum.TURN_NOTICE.getOperate().equals(operate)){
            paramMap.put("fromUserName", userNameMap.get(paramMap.get("fromUserId")));
            paramMap.put("toUserName", userNameMap.get(paramMap.get("toUserId")));
        }else if(StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate().equals(operate)){
            long betweenTime = cn.hutool.core.date.DateUtil.between(new Date(), storeWorkDataTableDO.getBeginTime(), DateUnit.MINUTE);
            betweenTime = betweenTime + 1;
            paramMap.put("minute", String.valueOf(betweenTime));
        }else if(StoreWorkNoticeEnum.BEFORE_END_REMIND.getOperate().equals(operate)){
            long betweenTime = cn.hutool.core.date.DateUtil.between(new Date(), storeWorkDataTableDO.getEndTime(), DateUnit.MINUTE);
            betweenTime = betweenTime + 1;
            paramMap.put("minute", String.valueOf(betweenTime));
        }else if(StoreWorkNoticeEnum.AFTER_COMMENT_REMIND_HANDLER.getOperate().equals(operate)){
            paramMap.put("score", String.valueOf(storeWorkDataTableDO.getScore()));
        }
        String action = getStoreWorkActionByOperate(operate);
        String mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&businessId={4}&dataTableId={5}&workCycle={6}&storeWorkDate={7}&storeId={8}&action={9}&currTime={10}&storeWorkId={11}",
                DingMsgEnum.STOREWORK.getDesc(), enterpriseId, config.getDingCorpId(),config.getAppType(), storeWorkDataTableDO.getTcBusinessId(), String.valueOf(storeWorkDataTableDO.getId()),storeWorkDataTableDO.getWorkCycle(), String.valueOf(storeWorkDataTableDO.getStoreWorkDate().getTime()), storeWorkDataTableDO.getStoreId(), action, String.valueOf(System.currentTimeMillis()), String.valueOf(storeWorkDataTableDO.getStoreWorkId()));
        boolean isSendMessage = isSendStoreWorkMessageHandlerAndApproveUser(storeWorkSetting, operate);
        boolean isSendBackLog = Optional.ofNullable(enterpriseSetting).map(EnterpriseSettingsDTO::getSendUpcoming).orElse(Boolean.FALSE);
        //获取消息标题和内容
        StoreWorkMessageDTO messageTitleAndContent = getStoreWorkMessageTitleAndContent(operate, paramMap);
        if(Objects.isNull(messageTitleAndContent)){
            return;
        }
        String content = messageTitleAndContent.getContent();
        String title = messageTitleAndContent.getTitle();
        if(isSendMessage && CollectionUtils.isNotEmpty(sendUserIds)){
            sendUserIds = distinctStoreWorkUserIds(enterpriseId, operate, sendUserIds, storeWorkDataTableDO.getId());
            String outBusinessId = enterpriseId + "_" + storeWorkDataTableDO.getId() + "_" + operate + "_" + System.currentTimeMillis();
            log.info("#########---> 店务接收人:{}, title:{}, content:{}, dataTableId:{}", JSONObject.toJSONString(sendUserIds), title, content, dataTableId);
            sendMessage(config.getDingCorpId(), sendUserIds, outBusinessId, config.getAppType(), title, content, mobileParam, getImageUrlByWorkCycle(storeWorkDataTableDO.getWorkCycle()));
        }
        // 只有开始前提醒发送待办
        boolean canSendBackLog = StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate().equals(operate);
        //判断是否是需要发送待办
        if (isSendMessage && isSendBackLog && canSendBackLog && CollectionUtils.isNotEmpty(sendUserIds)) {
            //当获取的setting不为null且获取的发送待办标志为true时，发送待办 钉钉平台发送待办
            //sendStoreWorkBacklog(enterpriseId, config.getDingCorpId(), sendUserIds, DingMsgEnum.STOREWORK.getDesc(), operate, cycleName, mobileParam, storeName, createUserName, storeWorkDataTableDO.getEndTime().getTime(), config.getAppType(), storeWorkDataTableDO.getId());
        }
        //wechatService.sendWXMsg(config, userIds, title, content, mobileParam);
    }

    @Override
    public void sendStoreWorkReminder(String enterpriseId, Long storeWorkId, List<String> userIds, String title, String content) {
        EnterpriseConfigDTO config = null;
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
        }
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        SwStoreWorkDO storeWorkDO = swStoreWorkDao.selectByPrimaryKey(storeWorkId, enterpriseId);
        String dingCorpId = config.getDingCorpId();
        String appType = config.getAppType();
        String mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&storeWorkId={4}&workCycle={5}&action={6}&currTime={7}",
                DingMsgEnum.STOREWORK.getDesc(), enterpriseId, config.getDingCorpId(),config.getAppType(), storeWorkId, storeWorkDO.getWorkCycle() ,StoreWorkConstant.ACTION.ACTION_HANDLE, String.valueOf(System.currentTimeMillis()));

        log.info("sendStoreWorkReminder店务提醒内容:{}，userIds:{}", content, JSONObject.toJSONString(userIds));
        String outBusinessId = enterpriseId + "_" + storeWorkId  + MD5Util.md5(JSONUtil.toJsonStr(mobileParam));
        //数智门店和酷店掌的url不同
        String messageUrl = getMessageUrl(dingCorpId, appType, mobileParam);
        SendMessageDTO messageDTO = new SendMessageDTO();
        messageDTO.setCorpId(dingCorpId);
        messageDTO.setUserIds(String.join(Constants.COMMA, userIds));
        messageDTO.setOutBusinessId(outBusinessId);
        messageDTO.setAppType(appType);
        JSONObject map = new JSONObject();
        map.put("message_url", messageUrl);
        JSONObject headJson = new JSONObject();
        headJson.put("bgcolor", "FFBBBBBB");
        map.put("head", headJson);
        JSONObject body = new JSONObject();
        body.put("title", title);
        body.put("image", getImageUrlByWorkCycle(storeWorkDO.getWorkCycle()));
        body.put("content", content);
        map.put("body",body);
        messageDTO.setOaJson(map);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
        //wechatService.sendWXMsg(config, userIds, title, content, mobileParam);
    }

    @Override
    public void sendSupervisionTaskBacklogByTaskId(String enterpriseId, Long supervisionTaskId) {
        log.info("督导待办传参enterpriseId:{}, supervisionTaskId:{}", enterpriseId, supervisionTaskId);
        EnterpriseConfigDTO config = null;
        EnterpriseSettingsDTO enterpriseSetting = enterpriseSettingRpcService.getEnterpriseSetting(enterpriseId);
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
        }
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        SupervisionTaskDO supervisionTaskDO = supervisionTaskDao.selectByPrimaryKey(supervisionTaskId, enterpriseId);
        if (supervisionTaskDO == null) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
        }
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionTaskDO.getTaskParentId(), enterpriseId);

        if(Objects.isNull(config) || Objects.isNull(supervisionTaskDO) || Objects.isNull(supervisionTaskParentDO)){
            log.info("相关督导数据不存在, 发送失败");
            return;
        }
        SupervisionTaskVO.HandleWay handleWay = JSONObject.parseObject(supervisionTaskParentDO.getHandleWay(), SupervisionTaskVO.HandleWay.class);
        //获取发送人
        String code = "supervisionApproval";
        List<String> userIds = new ArrayList<>();
        String currentNodeStr = "待审批";
        if (supervisionTaskDO.getCurrentNode()==1){
            String[] split = supervisionTaskDO.getFirstApprove().substring(1,supervisionTaskDO.getFirstApprove().length()-1).split(Constants.COMMA);
            userIds = Arrays.asList(split);
        }else if (supervisionTaskDO.getCurrentNode()==2){
            String[] split = supervisionTaskDO.getSecondaryApprove().substring(1,supervisionTaskDO.getSecondaryApprove().length()-1).split(Constants.COMMA);
            userIds = Arrays.asList(split);
        }else if (supervisionTaskDO.getCurrentNode()==3){
            String[] split = supervisionTaskDO.getThirdApprove().substring(1,supervisionTaskDO.getThirdApprove().length()-1).split(Constants.COMMA);
            userIds = Arrays.asList(split);
        }else if (supervisionTaskDO.getCurrentNode()==0){
            currentNodeStr = "待完成";
            code = "supervision";
            userIds = Arrays.asList(supervisionTaskDO.getSupervisionHandleUserId());
        }

        Integer businessType = 0;
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getCheckStoreIds())){
            businessType = 1;
        }
        String versionType  = "1.1";
        String mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&supervisionTaskId={4}&currTime" +
                        "={5}&handleCode={6}&taskName={7}&versionType={8}&businessType={9}",
                code, enterpriseId, config.getDingCorpId(),config.getAppType(),
                String.valueOf(supervisionTaskId), String.valueOf(System.currentTimeMillis()),String.valueOf(handleWay.getCode()),supervisionTaskParentDO.getTaskName(),versionType,businessType);
        boolean isSendBackLog = Optional.ofNullable(enterpriseSetting).map(EnterpriseSettingsDTO::getSendUpcoming).orElse(Boolean.FALSE);
        //判断是否是需要发送待办
        if (isSendBackLog) {
            //当获取的setting不为null且获取的发送待办标志为true时，发送待办 钉钉平台发送待办
            sendSupervisionTaskBacklog(enterpriseId, config.getDingCorpId(), userIds, DingMsgEnum.SUPERVISION.getCode().toLowerCase(), mobileParam,
                    supervisionTaskParentDO.getTaskName(), supervisionTaskParentDO.getDescription(), supervisionTaskDO.getTaskEndTime().getTime(), config.getAppType(), supervisionTaskId,currentNodeStr);
        }
    }

    public List<String> getStoreWorkSendUserIds(SwStoreWorkDataTableDO storeWorkDataTableDO, String operate, Map<String, String> paramMap){
        List<String> sendUserIds = Lists.newArrayList();
        if(StoreWorkNoticeEnum.TURN_NOTICE.getOperate().equals(operate)){
            String toUserId = paramMap.get("toUserId");
            return new ArrayList<>(Arrays.asList(toUserId));
        }else if(StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate().equals(operate)
                || StoreWorkNoticeEnum.BEFORE_END_REMIND.getOperate().equals(operate)
                || StoreWorkNoticeEnum.AFTER_COMMENT_REMIND_HANDLER.getOperate().equals(operate)){
            List<String> handleUserIdList = Arrays.asList(StringUtils.split(storeWorkDataTableDO.getHandleUserIds(), Constants.COMMA));
            return new ArrayList<String>(handleUserIdList);
        }else if(StoreWorkNoticeEnum.AFTER_HANDLE_REMIND_COMMENT.getOperate().equals(operate)){
            List<String> commentUserIdList = Arrays.asList(StringUtils.split(storeWorkDataTableDO.getCommentUserIds(), Constants.COMMA));
            return new ArrayList<String>(commentUserIdList);
        }
        return sendUserIds;
    }

    /**
     * 是否给店务执行人、点评人、执行人发工作通知
     * @param storeWorkSetting
     * @param operate
     * @return
     */
    private boolean isSendStoreWorkMessageHandlerAndApproveUser(EnterpriseStoreWorkSettingsDTO storeWorkSetting, String operate){
        boolean isSendMessage = false;
        Boolean startWorkRemind = storeWorkSetting.getStartWorkRemind();
        Boolean endWorkRemind = storeWorkSetting.getEndWorkRemind();
        Boolean afterHandleRemindComment = storeWorkSetting.getAfterHandleRemindComment();
        Boolean afterCommentRemindHandler = storeWorkSetting.getAfterCommentRemindHandler();
        if(StoreWorkNoticeEnum.TURN_NOTICE.getOperate().equals(operate)){
            isSendMessage = true;
        }else if(StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate().equals(operate)
                && startWorkRemind != null && startWorkRemind){
            isSendMessage = true;
        }else if(StoreWorkNoticeEnum.BEFORE_END_REMIND.getOperate().equals(operate)
                && endWorkRemind != null && endWorkRemind){
            isSendMessage = true;
        }else if(StoreWorkNoticeEnum.AFTER_HANDLE_REMIND_COMMENT.getOperate().equals(operate)
                && afterHandleRemindComment != null && afterHandleRemindComment){
            isSendMessage = true;
        }else if(StoreWorkNoticeEnum.AFTER_COMMENT_REMIND_HANDLER.getOperate().equals(operate)
                && afterCommentRemindHandler != null && afterCommentRemindHandler){
            isSendMessage = true;
        }
        return isSendMessage;
    }

    private String getStoreWorkActionByOperate(String operate){
        String action = StoreWorkConstant.ACTION.ACTION_VIEW;
        if(StoreWorkNoticeEnum.TURN_NOTICE.getOperate().equals(operate)
                ||StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate().equals(operate)
                || StoreWorkNoticeEnum.BEFORE_END_REMIND.getOperate().equals(operate)){
            action = StoreWorkConstant.ACTION.ACTION_HANDLE;
        }else if(StoreWorkNoticeEnum.AFTER_HANDLE_REMIND_COMMENT.getOperate().equals(operate)){
            action = StoreWorkConstant.ACTION.ACTION_COMMENT;
        }else if(StoreWorkNoticeEnum.AFTER_COMMENT_REMIND_HANDLER.getOperate().equals(operate)){
            action = StoreWorkConstant.ACTION.ACTION_VIEW;
        }
        return action;
    }

    private String getImageUrlByWorkCycle(String workCycle){
        String imageUrl = UnifyTaskPicUrlEnum.STORE_WORK_DAY.getDesc();
        if(StoreWorkCycleEnum.DAY.getCode().equals(workCycle)){
            imageUrl = UnifyTaskPicUrlEnum.STORE_WORK_DAY.getDesc();
        }else if(StoreWorkCycleEnum.WEEK.getCode().equals(workCycle)){
            imageUrl = UnifyTaskPicUrlEnum.STORE_WORK_WEEK.getDesc();
        }else if(StoreWorkCycleEnum.MONTH.getCode().equals(workCycle)){
            imageUrl = UnifyTaskPicUrlEnum.STORE_WORK_MONTH.getDesc();
        }
        return imageUrl;
    }

    /**
     * 获取店务通知标题和内容
     * @param operate
     * @param param
     * @return
     */
    private StoreWorkMessageDTO getStoreWorkMessageTitleAndContent(String operate, Map<String, String> param){
        StoreWorkNoticeEnum storeWorkNoticeEnum = StoreWorkNoticeEnum.getByOperate(operate);
        if(StringUtils.isNotBlank(param.get("reissueFlag"))){
            storeWorkNoticeEnum = StoreWorkNoticeEnum.START_REMIND;
        }
        String title = storeWorkNoticeEnum.getTitle();
        String content = StringUtil.format(storeWorkNoticeEnum.getContent(), param);
        return new StoreWorkMessageDTO(title, content);
    }

    public List<String> distinctStoreWorkUserIds(String enterpriseId, String operate, List<String> sendUserIds, Long dataTableId){
        List<String> resultList = new ArrayList<>();
        if(CollectionUtils.isEmpty(sendUserIds)){
            return resultList;
        }
        for (String sendUserId : sendUserIds) {
            String key = MessageFormat.format(RedisConstant.STOREWORK_NOTICE_KEY, enterpriseId, String.valueOf(dataTableId), operate, sendUserId);
            boolean isSuccess = redisUtilPool.setNxExpire(key, sendUserId, RedisConstant.THREE_DAY);
            if(isSuccess){
                resultList.add(sendUserId);
            }
        }
        return resultList;
    }

    @Override
    public void sendSupervisionTaskTextMessage(String enterpriseId, Long supervisionTaskId, List<String> handleUserIdList, String title, String content) {
        String dingCorpId = null;
        String appType = null;
        try {
            EnterpriseConfigDTO config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            dingCorpId = config.getDingCorpId();
            appType = config.getAppType();
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
            return;
        }
        String outBusinessId = enterpriseId + "_" + supervisionTaskId + "_" + "0" + "_" + System.currentTimeMillis();
        log.info("#########---> 处理人:{}, title:{}, content:{}", JSONObject.toJSONString(handleUserIdList), title, content);
        SendTextMessageDTO messageDTO = new SendTextMessageDTO();
        messageDTO.setCorpId(dingCorpId);
        messageDTO.setUserIds(String.join(Constants.COMMA, handleUserIdList));
        messageDTO.setOutBusinessId(outBusinessId);
        messageDTO.setAppType(appType);
        messageDTO.setMessageType("text");
        messageDTO.setTitle(title);
        messageDTO.setContent(content);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
    }

    @Override
    public void sendSupervisionTaskMessage(String enterpriseId, SupervisionTaskMessageDTO taskMessageDTO) {
        String dingCorpId = null;
        String appType = null;
        try {
            EnterpriseConfigDTO config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            dingCorpId = config.getDingCorpId();
            appType = config.getAppType();
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
            return;
        }
        String code = "supervisionApproval";
        String versionType = "1.2";
        if (taskMessageDTO.getTaskState()==0){
            code = "supervision";
        }
        String mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&supervisionTaskId={4}&currTime" +
                        "={5}&handleCode={6}&taskName={7}&versionType={8}&businessType={9}&taskState={10}",
                code, enterpriseId, dingCorpId, appType,
                String.valueOf(taskMessageDTO.getSupervisionTaskId()), String.valueOf(System.currentTimeMillis()), String.valueOf(taskMessageDTO.getHandleWay().getCode()),
                taskMessageDTO.getTaskName(), versionType, taskMessageDTO.getBusinessType(), taskMessageDTO.getTaskState());
        String outBusinessId = enterpriseId + "_" + taskMessageDTO.getSupervisionTaskId() + "_." + "0" + "_" + System.currentTimeMillis();
        log.info("#########---> 处理人:{}, title:{}, content:{}", JSONObject.toJSONString(taskMessageDTO.getHandleUserIdList()), taskMessageDTO.getTitle(), taskMessageDTO.getContent());

        //数智门店和酷店掌的url不同
        String messageUrl = getMessageUrl(dingCorpId, appType, mobileParam);
        SendMessageDTO messageDTO = new SendMessageDTO();
        messageDTO.setCorpId(dingCorpId);
        messageDTO.setUserIds(String.join(Constants.COMMA, taskMessageDTO.getHandleUserIdList()));
        messageDTO.setOutBusinessId(outBusinessId);
        messageDTO.setAppType(appType);
        JSONObject map = new JSONObject();
        map.put("message_url", messageUrl);
        JSONObject headJson = new JSONObject();
        headJson.put("bgcolor", "FFBBBBBB");
        map.put("head", headJson);
        JSONObject body = new JSONObject();
        body.put("title", taskMessageDTO.getTitle());
        body.put("image", UnifyTaskPicUrlEnum.SUPERVISION.getDesc());
        body.put("content", taskMessageDTO.getContent());
        map.put("body", body);
        messageDTO.setOaJson(map);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
        //wechatService.sendWXMsg(enterpriseId, JSONObject.parseArray(messageDTO.getUserIds(), String.class), messageDTO.getTitle(), messageDTO.getContent(), mobileParam);
    }

    @Override
    public void sendSupervisionStoreTaskBacklogByTaskId(String enterpriseId, Long supervisionStoreTaskId) {
        log.info("督导待办传参enterpriseId:{}, supervisionStoreTaskId:{}", enterpriseId, supervisionStoreTaskId);
        EnterpriseConfigDTO config = null;
        EnterpriseSettingsDTO enterpriseSetting = enterpriseSettingRpcService.getEnterpriseSetting(enterpriseId);
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
        }
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        SupervisionStoreTaskDO supervisionStoreTaskDO = supervisionStoreTaskDao.selectByPrimaryKey(supervisionStoreTaskId, enterpriseId);
        if (supervisionStoreTaskDO == null) {
            throw new ServiceException(ErrorCodeEnum.SUPERVISION_TASK_NOT_EXIST);
        }
        SupervisionTaskParentDO supervisionTaskParentDO = supervisionTaskParentDao.selectByPrimaryKey(supervisionStoreTaskDO.getTaskParentId(), enterpriseId);

        if(Objects.isNull(config) || Objects.isNull(supervisionStoreTaskDO) || Objects.isNull(supervisionTaskParentDO)){
            log.info("相关督导数据不存在, 发送失败");
            return;
        }
        String createUserId = supervisionTaskParentDO.getCreateUserId();
        SupervisionTaskVO.HandleWay handleWay = JSONObject.parseObject(supervisionTaskParentDO.getHandleWay(), SupervisionTaskVO.HandleWay.class);
        //获取发送人
        List<String> userIds = new ArrayList<>();
        String currentNodeStr = "待审批";
        if (supervisionStoreTaskDO.getCurrentNode()==1){
            String[] split = supervisionStoreTaskDO.getFirstApprove().substring(1,supervisionStoreTaskDO.getFirstApprove().length()-1).split(Constants.COMMA);
            userIds = Arrays.asList(split);
        }else if (supervisionStoreTaskDO.getCurrentNode()==2){
            String[] split = supervisionStoreTaskDO.getSecondaryApprove().substring(1,supervisionStoreTaskDO.getSecondaryApprove().length()-1).split(Constants.COMMA);
            userIds = Arrays.asList(split);
        }else if (supervisionStoreTaskDO.getCurrentNode()==3){
            String[] split = supervisionStoreTaskDO.getThirdApprove().substring(1,supervisionStoreTaskDO.getThirdApprove().length()-1).split(Constants.COMMA);
            userIds = Arrays.asList(split);
        }else if (supervisionStoreTaskDO.getCurrentNode()==0){
            currentNodeStr = "待完成";
            userIds = Arrays.asList(supervisionStoreTaskDO.getSupervisionUserId());
        }

        Integer businessType = 0;
        if (StringUtils.isNotEmpty(supervisionTaskParentDO.getCheckStoreIds())){
            businessType = 1;
        }
        String versionType  = "1.1";
        String mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&supervisionTaskId={4}&currTime" +
                        "={5}&handleCode={6}&taskName={7}&versionType={8}&businessType={9}",
                "supervisionApproval", enterpriseId, config.getDingCorpId(),config.getAppType(),
                String.valueOf(supervisionStoreTaskId), String.valueOf(System.currentTimeMillis()),String.valueOf(handleWay.getCode()),supervisionTaskParentDO.getTaskName(),versionType,businessType);
        boolean isSendBackLog = Optional.ofNullable(enterpriseSetting).map(EnterpriseSettingsDTO::getSendUpcoming).orElse(Boolean.FALSE);
        //判断是否是需要发送待办
        if (isSendBackLog) {
            //当获取的setting不为null且获取的发送待办标志为true时，发送待办 钉钉平台发送待办
            sendSupervisionTaskBacklog(enterpriseId, config.getDingCorpId(), userIds, DingMsgEnum.SUPERVISION_STORE.getCode().toLowerCase(), mobileParam,
                    supervisionTaskParentDO.getTaskName(), supervisionTaskParentDO.getDescription(), supervisionStoreTaskDO.getTaskEndTime().getTime(), config.getAppType(), supervisionStoreTaskId,currentNodeStr);
        }
    }

    @Override
    public void sendNoticeUnifyTask(String taskType, List<String> handleUserIdList, String nodeStr, String eid, String storeName, Long unifyTaskSubId,  Long endTime,
                                    Long beginTime, String storeId,  Long unifyTaskId, String content, String taskName) {
        log.info("任务消息参数：sendNoticeUnifyTask  子任务id：{}，处理人：{}，当前节点：{}，企业id：{}}", unifyTaskSubId, handleUserIdList.get(0), nodeStr, eid);
        JmsSendMessageVo messageVo = new JmsSendMessageVo();
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(eid);

        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        // 钉钉corpId
        messageVo.setDingCorpId(config.getDingCorpId());
        messageVo.setAppType(config.getAppType());
        String beginTimeParam = "";
        if (beginTime != null) {
            beginTimeParam = "&beginTime=" + beginTime;
        }
        String mobileParam = DingMsgEnum.getByCode(taskType) + "&unifyTaskSubId="
                + unifyTaskSubId + "&storeId=" + storeId
                + "&nodeStr=" + UnifyTaskConstant.TASK_STATUS_MAP.get(nodeStr)
                + beginTimeParam + "&endTime=" + endTime
                + "&currTime=" + System.currentTimeMillis()
                + "&eid=" + eid
                + "&appType=" + config.getAppType()
                + "&isCC=" + false
                + "&unifyTaskId=" + unifyTaskId
                + "&corpId=" + config.getDingCorpId();
        log.info("mobileParam:{}", mobileParam);
        //数智门店和酷店掌的url不同
        String messageUrl = getMessageUrl(config.getDingCorpId(), config.getAppType(), mobileParam);
        log.info("messageUrl:{}", messageUrl);
        SendMessageDTO messageDTO = new SendMessageDTO();
        messageDTO.setCorpId(config.getDingCorpId());
        messageDTO.setUserIds(String.join(Constants.COMMA, handleUserIdList));
        messageDTO.setOutBusinessId(eid + Constants.UNDERLINE + unifyTaskId + Constants.UNDERLINE + System.currentTimeMillis());
        messageDTO.setAppType(config.getAppType());
        JSONObject map = new JSONObject();
        map.put("message_url", messageUrl);
        JSONObject headJson = new JSONObject();
        headJson.put("bgcolor", "FFBBBBBB");
        map.put("head", headJson);
        JSONObject body = new JSONObject();
        body.put("title", Constants.STORE_REPORT);
        body.put("image", UnifyTaskPicUrlEnum.PATROL_STORE_REPORT.getDesc());
        body.put("content", content);
        map.put("body", body);
        messageDTO.setOaJson(map);
        log.info("开始发送");
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
        wechatService.sendWXMsg(config, handleUserIdList, taskName, DateUtil.format(beginTime, DatePattern.NORM_DATETIME_MINUTE_PATTERN), mobileParam, null, null);
    }

    @Override
    public void sendStoreReportNoticeUnifyTask(String eid, String taskType, List<String> handleUserIdList, String storeName, String storeId, Long reportId, String content){
        log.info("任务消息参数：sendStoreReportNoticeUnifyTask  ，处理人：{}，企业id：{}, reportId:{}}", handleUserIdList.get(0), eid, reportId);
        JmsSendMessageVo messageVo = new JmsSendMessageVo();
        DataSourceHelper.reset();
        EnterpriseConfigDO config = configMapper.selectByEnterpriseId(eid);

        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        // 钉钉corpId
        messageVo.setDingCorpId(config.getDingCorpId());
        messageVo.setAppType(config.getAppType());
        String mobileParam = DingMsgEnum.getByCode(taskType) + "&storeId=" + storeId + "&currTime="
                + System.currentTimeMillis() + "&eid=" + eid
                + "&appType=" + config.getAppType() + "&corpId=" + config.getDingCorpId() + "&reportId=" + reportId;

        //数智门店和酷店掌的url不同
        String messageUrl = getMessageUrl(config.getDingCorpId(), config.getAppType(), mobileParam);
        SendMessageDTO messageDTO = new SendMessageDTO();
        messageDTO.setCorpId(config.getDingCorpId());
        messageDTO.setUserIds(String.join(Constants.COMMA, handleUserIdList));
        messageDTO.setOutBusinessId(eid + Constants.UNDERLINE + reportId + Constants.UNDERLINE + System.currentTimeMillis());
        messageDTO.setAppType(config.getAppType());
        JSONObject map = new JSONObject();
        map.put("message_url", messageUrl);
        JSONObject headJson = new JSONObject();
        headJson.put("bgcolor", "FFBBBBBB");
        map.put("head", headJson);
        JSONObject body = new JSONObject();
        body.put("title", Constants.STORE_REPORT);
        body.put("image", UnifyTaskPicUrlEnum.PATROL_STORE_REPORT.getDesc());
        body.put("content", content);
        map.put("body", body);
        messageDTO.setOaJson(map);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
        wechatService.sendWXMsg(config, handleUserIdList, storeName, content, mobileParam, null, null);
    }

    @Override
    public void sendUnifyTaskReminder(String enterpriseId, String dingCorpId, String appType, Long unifyTaskId, Long subTaskId, String taskType, List<String> handleUserIds, Long loopCount, String nodeNo, Map<String, String> paramMap) {
        String imageUrl = UnifyTaskPicUrlEnum.getByCode(taskType);
        String content = String.format("有一个来自【%s】的【%s】任务，请您于%s前完成", paramMap.get("createUserName"), paramMap.get("taskName"), paramMap.get("handEndTime"));
        if(!"1".equals(nodeNo)){
            content = String.format("有一个来自【%s】的【%s】审批任务，请您于%s前完成", paramMap.get("createUserName"), paramMap.get("taskName"), paramMap.get("handEndTime"));
        }
        String mobileParam = DingMsgEnum.getByCode(taskType) + "&unifyTaskSubId="+ subTaskId +"&unifyTaskId="+unifyTaskId + "&nodeStr=" + UnifyTaskConstant.TASK_STATUS_MAP.get(nodeNo)
                + "&currTime=" + System.currentTimeMillis()+"&eid=" + enterpriseId;
        //数智门店和酷店掌的url不同
        String messageUrl = getMessageUrl(dingCorpId, appType, mobileParam);
        SendMessageDTO messageDTO = new SendMessageDTO();
        messageDTO.setCorpId(dingCorpId);
        messageDTO.setUserIds(String.join(Constants.COMMA, handleUserIds));
        messageDTO.setOutBusinessId(UUIDUtils.get32UUID());
        messageDTO.setAppType(appType);
        JSONObject map = new JSONObject();
        map.put("message_url", messageUrl);
        JSONObject headJson = new JSONObject();
        headJson.put("bgcolor", "FFBBBBBB");
        map.put("head", headJson);
        JSONObject body = new JSONObject();
        body.put("title", paramMap.get("taskName"));
        body.put("image", imageUrl);
        body.put("content", content);
        map.put("body",body);
        messageDTO.setOaJson(map);
        simpleMessageService.send(JSONObject.toJSONString(messageDTO), RocketMqTagEnum.STORE_DING_QUEUE);
    }

    @Override
    public void sendAiAnalysisReportMessage(String enterpriseId, Long reportId, String storeId, String storeName, LocalDate date, List<String> userIds,
                                            String appType, String dingCorpId) {
        String outBusinessId = enterpriseId + "_" + reportId  + "_" + System.currentTimeMillis();
        String title = "AI店报";
        String content = String.format("「%s」「%s」AI分析报告已生成，请查收", storeName, date.toString());
        String mobileParam = DingMsgEnum.AI_ANALYSIS_REPORT.getDesc() + "&id=" + reportId + "&storeName=" + storeName
                + "&currTime=" + System.currentTimeMillis() + "&eid=" + enterpriseId + "&storeId=" + storeId;
        sendMessage(dingCorpId, userIds, outBusinessId, appType, title, content, mobileParam, UnifyTaskPicUrlEnum.AI_ANALYSIS_REPORT.getDesc());
    }

    @Override
    public void sendMessage(String enterpriseId, String outBusinessId, DingMsgEnum dingMsgEnum, List<String> userIds, String title, String content, Map<String, String> paramMap) {
        try {
            EnterpriseConfigDTO enterpriseConfig = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
            if(Objects.isNull(enterpriseConfig)){
                return;
            }
            sendMessage(enterpriseConfig.getDingCorpId(), userIds, outBusinessId, enterpriseConfig.getAppType(), title, content, dingMsgEnum.getMobileParam(paramMap), dingMsgEnum.getImageUrl());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sendSafetyCheckMessage(String enterpriseId, Long businessId, String node, List<String> nodeUserList) {
        log.info("食安稽核消息发送传参enterpriseId:{}, businessId:{}, node:{}", enterpriseId, businessId, node);
        EnterpriseConfigDTO config = null;
        EnterpriseSettingsDTO enterpriseSetting = enterpriseSettingRpcService.getEnterpriseSetting(enterpriseId);
        try {
            config = enterpriseConfigApiService.getEnterpriseConfig(enterpriseId);
        } catch (ApiException e) {
            log.info("远程接口调用失败相关数据不存在, 发送失败");
        }

        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        ScSafetyCheckFlowDO safetyCheckFlowDO = scSafetyCheckFlowDao.getByBusinessId(enterpriseId, businessId);
        TbPatrolStoreRecordDO tbPatrolStoreRecordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);

        if(Objects.isNull(config) || Objects.isNull(safetyCheckFlowDO)){
            log.info("相关稽核数据不存在, 发送失败");
            return;
        }
        String storeName = tbPatrolStoreRecordDO.getStoreName();
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("storeName", storeName);
        String mobileParam = MessageFormat.format("{0}&eid={1}&corpId={2}&appType={3}&businessId={4}&storeId={5}&currTime={6}",
                DingMsgEnum.FOODCHECK.getDesc(), enterpriseId, config.getDingCorpId(),config.getAppType(), String.valueOf(businessId), tbPatrolStoreRecordDO.getStoreId(), String.valueOf(System.currentTimeMillis()));
        boolean isSendBackLog = Optional.ofNullable(enterpriseSetting).map(EnterpriseSettingsDTO::getSendUpcoming).orElse(Boolean.FALSE);
        //获取消息标题和内容
        FoodCheckNoticeEnum foodCheckNoticeEnum = FoodCheckNoticeEnum.getByNode(node);
        String title = foodCheckNoticeEnum.getTitle();
        String content = StringUtil.format(foodCheckNoticeEnum.getContent(), paramMap);

        if(CollectionUtils.isNotEmpty(nodeUserList)){
            // 可以反复申诉 反复签字 反复大店长审批
            if(!FoodCheckNoticeEnum.APPEALAPPROVE.getNode().equals(node)
                    && !FoodCheckNoticeEnum.APPEALRESULTCCINFO.getNode().equals(node)
                    && !FoodCheckNoticeEnum.SIGNATURE.getNode().equals(node)
                    && !FoodCheckNoticeEnum.BIGSTOREMANAGERAPPROVE.getNode().equals(node)){
                nodeUserList = distinctFoodCheckUserIds(enterpriseId, nodeUserList, businessId, safetyCheckFlowDO.getCycleCount(), node);
            }
            String outBusinessId = enterpriseId + "_" + businessId + "_" + safetyCheckFlowDO.getCycleCount() + "_" + node + "_" + System.currentTimeMillis();
            sendMessage(config.getDingCorpId(), nodeUserList, outBusinessId, config.getAppType(), title, content, mobileParam, UnifyTaskPicUrlEnum.PATROL_STORE_SAFETY_CHECK.getDesc());
        }
        //判断是否是需要发送待办

        if (isSendBackLog && CollectionUtils.isNotEmpty(nodeUserList)
                && !FoodCheckNoticeEnum.AFTERHANDLECCINFO.getNode().equals(node)
                && !FoodCheckNoticeEnum.AFTERAPPROVECCINFO.getNode().equals(node)
                && !FoodCheckNoticeEnum.APPEALRESULTCCINFO.getNode().equals(node)) {
            //当获取的setting不为null且获取的发送待办标志为true时，发送待办 钉钉平台发送待办
            sendSafetyCheckBacklog(enterpriseId, config.getDingCorpId(), nodeUserList, mobileParam, storeName, title, content, config.getAppType(), businessId, safetyCheckFlowDO.getCycleCount(), node);
        }
        //wechatService.sendWXMsg(config, nodeUserList, storeName, content, mobileParam);
    }

    public List<String> distinctFoodCheckUserIds(String enterpriseId, List<String> sendUserIds, Long businessId, Integer cycleCount, String node){
        List<String> resultList = new ArrayList<>();
        if(CollectionUtils.isEmpty(sendUserIds)){
            return resultList;
        }
        for (String sendUserId : sendUserIds) {
            String key = MessageFormat.format(RedisConstant.FOODCHECK_NOTICE_KEY, enterpriseId, String.valueOf(businessId), cycleCount, node, sendUserId);
            boolean isSuccess = redisUtilPool.setNxExpire(key, sendUserId, RedisConstant.THREE_DAY);
            if(isSuccess){
                resultList.add(sendUserId);
            }
        }
        return resultList;
    }

    // 稽核钉钉待办
    public void sendSafetyCheckBacklog(String eid, String corpId, List<String> userIds, String url, String storeName, String title, String content, String appType, Long businessId, Integer cycleCount, String currentNodeNo) {
        if(!AppTypeEnum.isDingType(appType)){
            return;
        }
        JSONObject send = UrlUtil.getUrlJSONObject(url);
        // 业务端自定义id  消除代办消息时用
        String backlogId = MD5Util.md5(url);
        // 企业的corpId  isv服务获取token用
        send.put("corpId", corpId);
        // 待办标题
        send.put("title", title);
        // 待办人列表
        send.put("userIds", userIds);
        // 门店信息
        JSONObject storeItem = new JSONObject();
        storeItem.put("title", "门店名称");
        storeItem.put("content", storeName);
        // 截止时间信息
        JSONObject contentDateItem = new JSONObject();
        contentDateItem.put("title", "待办事项");
        contentDateItem.put("content", content);
        // 消息来源
        JSONObject sourceItem = new JSONObject();
        sourceItem.put("title", "来源");
        //来源判断
        AppTypeEnum appTypeEnum = AppTypeEnum.parseValue(appType);
        sourceItem.put("content", appTypeEnum.getMessage());
        send.put("appType",appType);
        send.put("formItemList", Arrays.asList(storeItem, contentDateItem,sourceItem));
        send.put("taskKey", DingMsgEnum.FOODCHECK.getDesc() + "_" + businessId + "_" + cycleCount + "_" + currentNodeNo);

        // 小程序跳转链接
        log.info("待办消息类型：{}", AppTypeEnum.DING_DING.getValue().equals(appTypeEnum.getValue()));
        /*messageUrl = coolCollegeDomainUrl + Constants.NOTICE_PAGE_PREFIX + "?miniAppId={0}&appId={1}&corpId={2}&appUrl=" + URLEncoder.encode(Constants.PAGE_URL_PREFIX + mobileParam, StandardCharsets.UTF_8.name());
        String fixPrefix = String.format(Constants.BACKLOG_NEW_URL, AppTypeEnum.DINGDING.getValue().equals(appTypeEnum.getValue())?appMiniId:coolAppMiniId, AppTypeEnum.DINGDING.getValue().equals(appTypeEnum.getValue())?appId:coolAppId, corpId);*/
        String messageUrl = getMessageUrl(corpId, appType, url);
        // 业务自定义id
        send.put("backlogId", backlogId);
        //企业id
        send.put("enterpriseId", eid);
        messageUrl = messageUrl + "&backlogId=" + backlogId;
        send.put("url", messageUrl);
        log.info("待办消息内容：{}", send.toJSONString());
        simpleMessageService.send(send.toString(), RocketMqTagEnum.STORE_BACK_LOG);
    }


}
