package com.coolcollege.intelligent.facade.consumer.listener;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.baili.BailiEnterpriseEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.sync.conf.SyncConfig;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataType;
import com.coolcollege.intelligent.common.sync.vo.AddressBookChangeReqBody;
import com.coolcollege.intelligent.dao.order.EnterpriseOrderConsumerMapper;
import com.coolcollege.intelligent.facade.SyncSingleUserFacade;
import com.coolcollege.intelligent.facade.SyncUserFacade;
import com.coolcollege.intelligent.facade.enterprise.init.EnterpriseInitService;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.enterprise.*;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.sync.event.*;
import com.coolcollege.intelligent.service.sync.qywxEvent.*;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 钉钉消息监听
 *
 * @author chenyupeng
 * @since 2022/2/24
 */
@Slf4j
@Service
public class DingMsgDealListener implements MessageListener {

    @Autowired
    public EnterpriseService enterpriseService;

    @Autowired
    public EnterpriseConfigService enterpriseConfigService;

    @Resource
    private EnterpriseOrderConsumerMapper enterpriseOrderConsumerMapper;

    @Autowired
    private SyncSingleUserFacade syncSingleUserFacade;

    @Autowired
    private SyncUserFacade syncUserFacade;

    @Autowired
    private DingService dingService;

    @Autowired
    private RedisUtilPool redisUtilPool;
    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;
    @Autowired
    private EnterpriseInitService enterpriseInitService;
    @Autowired
    private EnterpriseSettingService enterpriseSettingService;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "DingMsgDealListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);
        if(lock){
            try {
                if(RocketMqTagEnum.DING_SINGLE_USER_SYNC.getTag().equals(message.getTag())) {
                    this.dingSingleUserSync(text);
                }else {
                    dealAddressBookChange(text);
                }
            }catch (Exception e){
                log.error("DingMsgDealListener consume dealAddressBookChange error",e);
                return Action.ReconsumeLater;
            } finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        return Action.ReconsumeLater;
    }

    public void dealAddressBookChange(String text) throws ApiException {
        log.info("deal dealAddressBookChange, reqBody={}", text);
        AddressBookChangeReqBody reqBody = JSONObject.parseObject(text, AddressBookChangeReqBody.class);
        if(Objects.isNull(reqBody)){
            return;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByCorpId(reqBody.getCorpId(), reqBody.getAppType());
        if(Objects.isNull(config)){
            log.info("未找到企业信息, dingCorpId={}", reqBody.getCorpId());
            return;
        }
        String enterpriseId = config.getEnterpriseId();
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseId);
        if(Objects.isNull(enterpriseDO) || EnterpriseStatusEnum.NORMAL.getCode() != enterpriseDO.getStatus()){
            log.info("企业被删除或者企业是非正常状态, enterpriseId={}，dingCorpId={}", enterpriseId, reqBody.getCorpId());
            return;
        }
        reqBody.setEnterpriseId(config.getEnterpriseId());
        reqBody.setDbName(config.getDbName());
        if(AppTypeEnum.FEI_SHU.getValue().equals(reqBody.getAppType())){
            enterpriseInitService.userUpdateEvent(reqBody);
            return;
        }
        BaseEvent event = BaseEvent.parse(reqBody);
        if(Objects.isNull(event)){
            log.info("不支持的事件类型, EventType={}", reqBody.getEventType());
            return;
        }

        if (event instanceof OaPluginEvent) {
            event.exec();
            return;
        }

        if(event instanceof CreateUserEvent || event instanceof UpdateUserEvent || event instanceof DeleteUserEvent || event instanceof CreatePartyEvent || event instanceof UpdatePartyEvent || event instanceof DeletePartyEvent){
            log.info("企业微信事件监听, EventType={}", event.getEventType());
            event.exec();
            return;
        }
        // 角色变更事件, 只处理门店通
        if (event instanceof RoleAddOrModifyEvent || event instanceof  RoleRemoveEvent || event instanceof  OpRoleAddRemoveUserEvent) {
            if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(reqBody.getAppType())) {
                event.exec();
            }
            return;
        }
        // 门店通：自定义通讯录节点变更事件
        if (event instanceof OpNodeAddOrModifyEvent || event instanceof OpNodeDeleteEvent) {
            event.exec();
            return;
        }
        EnterpriseSettingVO setting = enterpriseSettingService.getEnterpriseSettingVOByEid(config.getEnterpriseId());
        if(BailiEnterpriseEnum.EDS.getCode().equals(config.getEnterpriseId())
            || BailiEnterpriseEnum.XYW.getCode().equals(config.getEnterpriseId())){
            log.info("钉钉同步单个用户暂停eid:{}", config.getEnterpriseId());
            return;
        }
        String eid = config.getEnterpriseId();
        String dbName = config.getDbName();
        log.info("现版本的钉钉同步");
        log.info("消息队列参数--------------------------------");
        // 创建用户事件/用户信息变更事件
        if (event instanceof UserAddEvent || event instanceof UserModifyEvent || event instanceof OpUserModifyContactEvent
                || event instanceof OpUserModifyScopeEvent) {
            this.addOrUpdateUser(reqBody, event, config, setting);
        }
        // 用户离职事件
        if (event instanceof UserLeaveEvent) {
            this.removeUser(reqBody, eid, dbName);
        }


    }

    private void removeUser(AddressBookChangeReqBody reqBody, String eid, String dbName) {
        List<String> originUserIds = Arrays.stream(reqBody.getUserId().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "").split(",")).collect(Collectors.toList());
        log.info("用户离职事件");
        originUserIds.forEach(userId -> {
            try {
                syncUserFacade.syncDeleteUser(eid, userId, dbName);
            } catch (Exception e) {
                log.error("用户离职事件处理失败，userId:{}, eid:{}", userId, eid, e);
            }
        });
        //用户信息变更，推送变更消息,异步推送，如果是业培一体2.0 并且开通业培一体服务，做推送酷学院
        coolCollegeIntegrationApiService.sendDataChangeMsg(eid, originUserIds, ChangeDataOperation.DELETE.getCode(), ChangeDataType.USER.getCode());
    }

    private void addOrUpdateUser(AddressBookChangeReqBody reqBody, BaseEvent event, EnterpriseConfigDO config, EnterpriseSettingVO setting) {
        log.info("用户信息变更事件");
        String dbName = config.getDbName();
        String eid = config.getEnterpriseId();
        List<String> originUserIds = Arrays.stream(reqBody.getUserId().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\"", "").split(",")).collect(Collectors.toList());
        log.info("开始添加或者更新用户信息");
        originUserIds.forEach(userId -> {
            try {
                if(!AppTypeEnum.ONE_PARTY_APP.getValue().equals(config.getAppType())){
                    syncSingleUserFacade.syncUser(userId, config, setting, true);
                }else {
                    log.info("one party add or update user event:{}", event.getEventType());
                    // 只同步用户信息
                    int syncUserContactCode = SyncConfig.OP_USER_CONTACT_SYNC_ALL;
                    if(BaseEvent.OP_USER_MODIFY_CONTACT.equals(event.getEventType())) {
                        // 同步用户给所属区域
                        syncUserContactCode = SyncConfig.OP_USER_CONTACT_SYNC_NODE;
                    }else if(BaseEvent.OP_USER_MODIFY_AUTH_SCOPE.equals(event.getEventType())) {
                        // 同步用户给权限范围
                        syncUserContactCode = SyncConfig.OP_USER_CONTACT_SYNC_SCOPE;
                    }
                    log.info("one party add or update user syncUserContactCode:{}", syncUserContactCode);
                    syncSingleUserFacade.syncOnePartyUser(eid, dbName, userId, config.getDingCorpId(), AppTypeEnum.ONE_PARTY_APP.getValue(), syncUserContactCode);
                }
            } catch (Exception e) {
                log.error("添加或者更新用户失败，userId:{}, eid:{}", userId, eid, e);
            }
        });
        //用户信息变更，推送变更消息,异步推送，如果是业培一体2.0 并且开通业培一体服务，做推送酷学院
        coolCollegeIntegrationApiService.sendDataChangeMsg(eid, originUserIds, ChangeDataOperation.UPDATE.getCode(), ChangeDataType.USER.getCode());
    }

    /**
     * 同步单个用户
     * @param data
     */
    private void dingSingleUserSync(String data) throws ApiException {
        AddressBookChangeReqBody reqBody = JSONObject.parseObject(data, AddressBookChangeReqBody.class);
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByCorpId(reqBody.getCorpId(), reqBody.getAppType());
        syncSingleUserFacade.syncOnePartyUser(config.getEnterpriseId(), config.getDbName(), reqBody.getUserId(),
                config.getDingCorpId(), AppTypeEnum.ONE_PARTY_APP.getValue(), SyncConfig.OP_USER_CONTACT_SYNC_ALL);
    }
}
