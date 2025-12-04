package com.coolcollege.intelligent.facade.consumer;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.PropertyValueConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.coolcollege.intelligent.common.config.rocketmq.RocketMqConfig;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RocketMqConstant;
import com.coolcollege.intelligent.facade.consumer.listener.*;
import com.coolstore.base.enums.RocketMqGroupEnum;
import com.google.common.collect.Maps;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author zhangchenbiao
 * @FileName: OrderlyConsumerClient
 * @Description: 普通消息消费client
 * @date 2021-12-21 11:35
 */
@Configuration
@DependsOn("commonContextUtil")
public class ConsumerClient {

    @Resource
    private RocketMqConfig rocketMqConfig;

    @Resource
    private SimpleMessageListener simpleMessageListener;

    @Resource
    private OrderMessageListener orderMessageListener;

    @Resource
    private EsDataDealListener esDataDealListener;

    @Resource
    private TaskParentUserListener taskParentUserListener;

    @Resource
    private DingMsgDealListener dingMsgDealListener;

    @Resource
    private PayMarketBuyListener payMarketBuyListener;

    @Resource
    private AuthQueueListener authQueueListener;

    @Resource
    private QwMsgDealListener qwMsgDealListener;

    @Resource
    private YingShiYunDeviceTrustListener yingShiYunDeviceTrustListener;

    @Resource
    private InitDeviceQueueListener initDeviceQueueListener;

    @Resource
    private DisplayCcUserQueueListener displayCcUserQueueListener;

    @Resource
    private StoreSubTaskDataQueueListener storeSubTaskDataQueueListener;

    @Resource
    private CombineUpcomingCancelListener combineUpcomingCancelListener;

    @Resource
    private PatrolStoreCapturePictureQueueListener patrolStoreCapturePictureQueueListener;

    @Resource
    private PatrolStoreScoreCountQueueListener patrolStoreScoreCountQueueListener;

    @Resource
    private UnifyTaskDisplayListener unifyTaskDisplayListener;

    @Resource
    private UnifyTaskPatrolListener unifyTaskPatrolListener;

    @Resource
    private UnifyTaskQuestionListener unifyTaskQuestionListener;

    @Resource
    private InformationCompletionListener informationCompletionListener;

    @Resource
    private OpenEnterpriseAliyunListener openEnterpriseAliyunListener;

    @Resource
    private EnterpriseInitListener enterpriseInitListener;

    @Resource
    private EnterpriseScriptListener enterpriseScriptListener;

    @Resource
    private EnterpriseInitDeptOrderListener enterpriseInitDeptOrderListener;
    @Resource
    private PersonSubTaskDataQueueListener personSubTaskDataQueueListener;
    @Resource
    private FullUserSyncListener fullUserSyncListener;
    @Resource
    private ImouListener imouListener;
    @Resource
    private GetCoolCollegeOpenResultListener getCoolCollegeOpenResultListener;
    @Resource
    private CoolStoreDataChangeListener coolStoreDataChangeListener;
    @Resource
    private StoreGroupEventListener storeGroupEventListener;
    @Resource
    private UserPushDelayedToCollegeListener userPushDelayedTOCollegeListener;
    @Resource
    private QuestionMessageListener questionMessageListener;
    @Resource
    private StoreWorkSubmitCommentListener storeWorkSubmitCommentListener;
    @Resource
    private StoreWorkMessageListener storeWorkMessageListener;
    @Resource
    private StoreWorkTaskResolveListener storeWorkTaskResolveListener;

    @Resource
    private WorkHandoverListener workHandoverListener;
    @Resource
    private LicenseNoticeDealListener licenseNoticeDealListener;
    @Resource
    private SupervisionResolveDelayListener supervisionResolveDelayListener;
    @Resource
    private SchedulerJobListener schedulerJobListener;

    @Resource
    private UnifyTaskReissueListener unifyTaskReissueListener;
    @Resource
    private ActivityListener activityListener;
    @Resource
    private AiInspectionCaptureListener aiInspectionCaptureListener;
    @Resource
    private AiInspectionQuestionListener aiInspectionQuestionListener;
    @Resource
    private DeviceListener deviceListener;
    @Resource
    private WorkflowListener workflowListener;
    @Resource
    private UnifyTaskAchievementListener unifyTaskAchievementListener;
    @Resource
    private WXGroupMessageListener wxGroupMessageListener;
    @Resource
    private SysLogListener sysLogListener;
    @Resource
    private AiResolveListener aiResolveListener;

    /**
     * 获取通用配置
     * @param groupEnum
     * @return
     */
    private Properties getCommonProperties(RocketMqGroupEnum groupEnum) {
        //配置文件
        Properties properties = rocketMqConfig.getMqProperties();
        //消费者需指定groupId  根据实际情况指定groupId
        properties.setProperty(PropertyKeyConst.GROUP_ID, RocketMqGroupEnum.getGroupId(groupEnum));
        //消费模式 集群消费
        properties.setProperty(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        //消息最大重试次数
        properties.setProperty(PropertyKeyConst.MaxReconsumeTimes, RocketMqConstant.MaxReconsumeTimes);
        //开启最大线程数
        properties.setProperty(PropertyKeyConst.ConsumeThreadNums,  Constants.SEVEN_STR);
        return properties;
    }

    /**
     * 获取通用订阅关系
     * @param groupEnum
     * @param listener
     * @return
     */
    public Map<Subscription, MessageListener> getCommonSubscriptionTable(RocketMqGroupEnum groupEnum, MessageListener listener) {
        Map<Subscription, MessageListener> subscriptionTable = Maps.newHashMap();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, listener);
        return subscriptionTable;
    }

      /**
     * es数据处理
     */
//    @Primary
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    public OrderConsumerBean esDataDealBean() {
//        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.ES_DATA_DEAL;
//        OrderConsumerBean orderConsumerBean = new OrderConsumerBean();
//        //配置文件
//        Properties properties = this.getCommonProperties(groupEnum);
//        orderConsumerBean.setProperties(properties);
//        //订阅关系
//        Map<Subscription, MessageOrderListener> subscriptionTable = new HashMap<Subscription, MessageOrderListener>();
//        Subscription subscription = new Subscription();
//        subscription.setTopic(rocketMqConfig.getOrderTopic());
//        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
//        subscriptionTable.put(subscription, esDataDealListener);
//        //订阅多个topic如上面设置
//        orderConsumerBean.setSubscriptionTable(subscriptionTable);
//        return orderConsumerBean;
//    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean taskParentUserListenerBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.TASK_PARENT_USER;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, taskParentUserListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    /**
     * 企业开通
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean enterpriseOpenBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.ENTERPRISE_OPEN_DATA_SYNC;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, enterpriseInitListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    /**
     * 企业库脚本开通
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean enterpriseScriptBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.ENTERPRISE_OPEN_ENTERPRISE_RUN_SCRIPT;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, enterpriseScriptListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    /**
     * 部门order顺序值
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean enterpriseInitDeptOrder() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.ENTERPRISE_INIT_DEPT_ORDER;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, enterpriseInitDeptOrderListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    /**
     * 授权队列
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean authQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.AUTH_QUEUE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, authQueueListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    /**
     * 钉钉消息监听
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean dingMsgDealBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.DING_MSG_DEAL;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, dingMsgDealListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    /**
     * 购买事件
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean payMarketBuyBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.PAY_MARKET_BUY;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, payMarketBuyListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    /**
     * 企微消息监听
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean qwMsgDealBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.QW_MSG_DEAL;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, qwMsgDealListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }


    /**
     * 萤石云设备托管
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean yingShiYunTrust() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.YINGSHI_DEVICE_MANAGE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, yingShiYunDeviceTrustListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 初始化设备
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean initDeviceQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.INIT_DEVICE_QUEUE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, initDeviceQueueListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 陈列抄送人处理
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean displayCcUserQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.DISPLAY_CC_USER_QUEUE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, displayCcUserQueueListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 门店子任务监听
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean storeSubTaskDataQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.STORE_SUB_TASK_DATA_QUEUE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        properties.setProperty(PropertyKeyConst.ConsumeThreadNums,  Constants.THREE_STR);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, storeSubTaskDataQueueListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 定时巡检抓拍
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean patrolManualStoreCapturePictureQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.PATROL_STORE_CAPTURE_PICTURE_QUEUE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, patrolStoreCapturePictureQueueListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 巡店分数计算
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean patrolStoreScoreCountQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.PATROL_STORE_SCORE_COUNT_QUEUE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, patrolStoreScoreCountQueueListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 陈列任务创建
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean unifyTaskDisplayBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.UNIFY_TASK_DISPLAY;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, unifyTaskDisplayListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 巡店任务创建
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean unifyTaskPatrolBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.UNIFY_TASK_PATROL;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, unifyTaskPatrolListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 工单任务创建
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean unifyTaskQuestionBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.UNIFY_TASK_QUESTION;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, unifyTaskQuestionListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 信息补全
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean informationCompletionBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.INFORMATION_COMPLETION;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, informationCompletionListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 阿里云开通门店
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean openEnterpriseAliyunBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.MQ_OPEN_ENTERPRISE_ALIYUN;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, openEnterpriseAliyunListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    /**
     * 全量同步用户接口
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean fullUserSyncBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.QW_SYNC_FULL_USER;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, fullUserSyncListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }
    /**
     * 按人子任务
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean personSubTaskDataQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.PERSON_SUB_TASK_DATA_QUEUE;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, personSubTaskDataQueueListener));
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean getOpenCoolCollegeResultQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.GET_OPEN_COOL_COLLEGE_RESULT;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, getCoolCollegeOpenResultListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean coolStoreDataChangeQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.COOL_STORE_DATA_CHANGE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, coolStoreDataChangeListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean imoutDevice() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.IMOU_DEVICE_CALLBACK;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, imouListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean storeGroupEvent() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.SHOP_STORE_GROUP_SYNC;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, storeGroupEventListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean userPushDelayedTOCollege() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.COLLEGE_SYNC_USER_DELAY;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, userPushDelayedTOCollegeListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean supervisionResolveDelay() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.SUPERVISION_RESOLVE_DELAY;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, supervisionResolveDelayListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }


    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean buildQuestionEvent() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.SHOP_STORE_GROUP_SYNC;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, storeGroupEventListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean questionMessage() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.QUESTION_EXPIRE_REMIND;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, questionMessageListener));
        return consumerBean;
    }

    /**
     * 店务消息提醒
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean storeworkMessage() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.STOREWORK_EXPIRE_REMIND;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, storeWorkMessageListener));
        return consumerBean;
    }


    /**
     * 店务提交、点评消息监听
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean storeWorkSubmitCommentQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.STOREWORK_SUBMIT_COMMENT_DATA_QUEUE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = rocketMqConfig.getMqProperties();
        //消费者需指定groupId  根据实际情况指定groupId
        properties.setProperty(PropertyKeyConst.GROUP_ID, RocketMqGroupEnum.getGroupId(groupEnum));
        //消费模式 集群消费
        properties.setProperty(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        //消息最大重试次数
        properties.setProperty(PropertyKeyConst.MaxReconsumeTimes, RocketMqConstant.MaxReconsumeTimes);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, storeWorkSubmitCommentListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    /**
     * 店务分解
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean storeWorkTaskResolveBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.STOREWORK_TASK_RESOLVE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = rocketMqConfig.getMqProperties();
        //消费者需指定groupId  根据实际情况指定groupId
        properties.setProperty(PropertyKeyConst.GROUP_ID, RocketMqGroupEnum.getGroupId(groupEnum));
        //消费模式 集群消费
        properties.setProperty(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
        //消息最大重试次数
        properties.setProperty(PropertyKeyConst.MaxReconsumeTimes, RocketMqConstant.MaxReconsumeTimes);
        properties.setProperty(PropertyKeyConst.ConsumeThreadNums,  Constants.FIVE_STR);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, storeWorkTaskResolveListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean workHandoverEvent() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.WORK_HANDOVER_TASK;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, workHandoverListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean licenseNoticeDeal() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.LICENSE_NOTICE_DEAL;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, licenseNoticeDealListener));
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean schedulerJob() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.SCHEDULER_CALLBACK;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, schedulerJobListener));
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean taskReissue() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.TASK_REISSUE;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, unifyTaskReissueListener));
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean activityStatisticCount() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.ACTIVITY;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, activityListener));
        return consumerBean;
    }

    /**
     * 合并通知取消监听
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean combineUpcomingCancelQueueBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.COMBINE_UPCOMING_CANCEL_QUEUE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, combineUpcomingCancelListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean deviceListenerClient() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.DEVICE;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, deviceListener));
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean workflowListenerClient() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.WORKFLOW_SEND_TOPIC;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, workflowListener));
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean wxGroupMessageListenerClient() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.SEND_WX_GROUP_MESSAGE;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, wxGroupMessageListener));
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean unifyTaskAchievementBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.ACHIEVEMENT_PRODUCT_TASK;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, unifyTaskAchievementListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    /**
     * 系统日志监听
     */
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean sysLogBean() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.SYS_LOG_RESOLVE;
        ConsumerBean consumerBean = new ConsumerBean();
        //配置文件
        Properties properties = this.getCommonProperties(groupEnum);
        properties.setProperty(PropertyKeyConst.ConsumeThreadNums,  Constants.THREE_STR);
        consumerBean.setProperties(properties);
        //订阅关系
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<Subscription, MessageListener>();
        Subscription subscription = new Subscription();
        subscription.setTopic(rocketMqConfig.getTopic());
        subscription.setExpression(RocketMqGroupEnum.getTag(groupEnum));
        subscriptionTable.put(subscription, sysLogListener);
        //订阅多个topic如上面设置
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean aiResolveListenerClient() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.AI_RESOLVE_MAIN;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, aiResolveListener));
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean aiInspectionCapture() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.AI_INSPECTION_TASK;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, aiInspectionCaptureListener));
        return consumerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean aiInspectionQuestion() {
        RocketMqGroupEnum groupEnum = RocketMqGroupEnum.AI_INSPECTION_QUESTION;
        ConsumerBean consumerBean = new ConsumerBean();
        consumerBean.setProperties(this.getCommonProperties(groupEnum));
        consumerBean.setSubscriptionTable(this.getCommonSubscriptionTable(groupEnum, aiInspectionQuestionListener));
        return consumerBean;
    }
}
