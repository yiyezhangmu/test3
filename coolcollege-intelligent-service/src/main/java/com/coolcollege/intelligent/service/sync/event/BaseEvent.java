package com.coolcollege.intelligent.service.sync.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.sync.vo.AddressBookChangeReqBody;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.service.enterprise.DingService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.qywx.ChatService;
import com.coolcollege.intelligent.service.sync.AddressBookTask;
import com.coolcollege.intelligent.service.sync.SyncThreadPoolExecutorService;
import com.coolcollege.intelligent.service.sync.qywxEvent.*;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class BaseEvent {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected String corpId;
    protected String eid;
    protected String dbName;
    protected String appType;
    protected Boolean  isSync;
    private final static Map<String, String> CORP4EID = new ConcurrentHashMap<>();
    private final static Map<String, String> CORP4DB = new ConcurrentHashMap<>();
    private final static Map<String, Boolean> CORP4SYNC = new ConcurrentHashMap<>();

    static final String USER_ADD_ORG = "user_add_org";
    static final String USER_MODIFY_ORG = "user_modify_org";
    static final String USER_LEAVE_ORG = "user_leave_org";
    static final String ORG_DEPT_CREATE = "org_dept_create";
    static final String ORG_DEPT_MODIFY = "org_dept_modify";
    static final String ORG_DEPT_REMOVE = "org_dept_remove";
    static final String ROLE_ADD_OR_MODIFY = "role_add_or_modify";
    static final String ROLE_REMOVE = "org_role_remove";

    /**
     * 企业微信监听事件
     */
    public static final String CREATE_USER = "create_user";
    public static final String UPDATE_USER = "update_user";
    public static final String DELETE_USER = "delete_user";
    public static final String CREATE_PARTY = "create_party";
    public static final String UPDATE_PARTY = "update_party";
    public static final String DELETE_PARTY = "delete_party";


    /**
     * 门店通：用户所属部门变更
     */
    public static final String OP_USER_MODIFY_CONTACT = "op_user_modify_contact";

    /**
     * 门店通：用户权限范围变更
     */
    public static final String OP_USER_MODIFY_AUTH_SCOPE = "op_user_modify_auth_scope";

    public static final String OP_USER_CONTACT_SYNC_ALL = "op_user_contact_sync_all";

    /**
     * 门店通：自定义通讯录节点新增/修改
     */
    public static final String OP_NODE_MODIFY = "op_node_modify";

    /**
     * 门店通：自定义通讯录节点删除
     */
    public static final String OP_NODE_DELETE = "op_node_delete";

    /**
     * 门店通：角色加人移除人员
     */
    public static final String OP_ROLE_ADD_REMOVE_USER = "op_role_add_remove_user";

    public static final String WORKFLOW_INSTANCE_CHANGE_DIRECTED = "workflow_instance_change_directed";
    public static final String WORKFLOW_TASK_CHANGE_DIRECTED = "workflow_task_change_directed";

    public abstract String getEventType();

    public abstract void doEvent();

    public void exec() {
        log.info("@@exec");
        AddressBookTask addressBookTask = new AddressBookTask(this, MDC.get(Constants.REQUEST_ID));
        SyncThreadPoolExecutorService syncThreadPoolExecutorService = SpringContextUtil.getBean("syncThreadPoolExecutorService", SyncThreadPoolExecutorService.class);
        syncThreadPoolExecutorService.submitAddressBookTask(addressBookTask);
    }

    protected String getEid() {

        if (eid == null) {
            eid = CORP4EID.get(corpId);
            if (StrUtil.isBlank(eid)) {
                DataSourceHelper.reset();
                EnterpriseConfigService enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigService.class);
                EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByEnterpriseId(eid);
                if (enterpriseConfig != null) {
                    this.eid = enterpriseConfig.getEnterpriseId();
                    CORP4EID.put(corpId, eid);
                    this.dbName = enterpriseConfig.getDbName();
                    CORP4DB.put(corpId, dbName);
                } else {
                    throw new IllegalArgumentException(String.format("incorrect corpId, can't find in db, corpId=%s", corpId));
                }
            }

        }
        return eid;
    }

    protected String getDbName() {

        if (dbName == null) {
            dbName = CORP4DB.get(corpId);
            if (StrUtil.isBlank(dbName)) {
                DataSourceHelper.reset();
                EnterpriseConfigService enterpriseConfigService = SpringContextUtil.getBean("enterpriseConfigService", EnterpriseConfigService.class);
                EnterpriseConfigDO enterpriseConfig = enterpriseConfigService.selectByCorpId(corpId, appType);
                if (enterpriseConfig != null) {
                    this.dbName = enterpriseConfig.getDbName();
                    CORP4DB.put(corpId, dbName);
                    this.eid = enterpriseConfig.getEnterpriseId();
                    CORP4EID.put(corpId, eid);
                } else {
                    throw new IllegalArgumentException(String.format("incorrect corpId, can't find in db, corpId=%s", corpId));
                }
            }

        }
        return dbName;
    }

    protected String getAccessToken() {

        try {
            //老版部门同步已弃用，直接写死为fdingding
            return SpringContextUtil.getBean("dingService", DingService.class).getAccessToken(corpId, AppTypeEnum.DING_DING.getValue());
        } catch (Exception e) {
            log.error("get accessToken error, event={}, corpId={}", JSON.toJSONString(this), corpId);
        }
        return null;
    }

    protected Boolean getEnableDingSync(){
        isSync = CORP4SYNC.get(corpId);
        if (isSync == null) {
            DataSourceHelper.reset();
            EnterpriseService enterpriseService = SpringContextUtil.getBean("enterpriseService", EnterpriseService.class);
            EnterpriseSettingDO enterpriseConfig = enterpriseService.getEnterpriseSettings(getEid());
            if (enterpriseConfig != null) {
                this.isSync = Objects.equals(enterpriseConfig.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN) || Objects.equals(enterpriseConfig.getEnableDingSync(), Constants.ENABLE_DING_SYNC_THIRD) ;
                CORP4SYNC.put(corpId, isSync);
            } else {
                throw new IllegalArgumentException(String.format("incorrect corpId, can't find in db, corpId=%s", corpId));
            }
        }
        return isSync;
    }

    /**
     * 获得授权token，区分企业内部应用和第三方应用, 2021-10-28 添加获得企业代开发应用token
     * @param corpSecret
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/8/6 11:45
     */
    protected String getPyAccessToken(String corpSecret) {
        try {
            if (StringUtils.isNotBlank(corpSecret)) {
                return getInsideAccessToken(corpSecret);
            } else {
                return getPyAccessToken();
            }
        } catch (Exception e) {
            log.error("get getPyAccessToken error, event={}, corpId={}", JSON.toJSONString(this), corpId);
        }
        return null;
    }

    protected String getPyAccessToken() {
        try {
            return SpringContextUtil.getBean("chatService", ChatService.class).getPyAccessToken(corpId, appType);
        } catch (Exception e) {
            log.error("get getPyAccessToken error, event={}, corpId={}", JSON.toJSONString(this), corpId);
        }
        return null;
    }

    /**
     * 获得企微token，如果存在代开发token,则返回代开发token
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/10/28 11:31
     */
    protected String getDkfOrQwAccessToken() {
        try {
            return SpringContextUtil.getBean("chatService", ChatService.class).getDkfOrQwAccessToken(corpId, appType);
        } catch (Exception e) {
            log.error("get getDkfOrQwAccessToken error, event={}, corpId={}", JSON.toJSONString(this), corpId);
        }
        return null;
    }

    /**
     * 获得授权token，区分企业内部应用和第三方应用
     * @param corpSecret
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/8/6 11:45
     */
    protected String getInsideAccessToken(String corpSecret) {
        try {
            log.info("getInsideAccessToken start");
            return SpringContextUtil.getBean("chatService", ChatService.class).getInsideAccessToken(corpId, corpSecret, appType);
        } catch (Exception e) {
            log.error("get getInsideAccessToken error, event={}, eid:{}", JSON.toJSONString(this), eid);
        }
        return null;
    }

    public static BaseEvent parse(AddressBookChangeReqBody reqBody) {
        log.info("reqBody===={}",reqBody);
        BaseEvent event = null;
        switch (reqBody.getEventType()) {
            case USER_ADD_ORG:
                event = new UserAddEvent(reqBody.getCorpId(), reqBody.getUserId());
                break;
            case USER_MODIFY_ORG:
                event = new UserModifyEvent(reqBody.getCorpId(), reqBody.getUserId());
                break;
            case OP_USER_MODIFY_CONTACT:
                event = new OpUserModifyContactEvent(reqBody.getCorpId(), reqBody.getUserId());
                break;
            case OP_USER_CONTACT_SYNC_ALL:
                event = new UserModifyEvent(reqBody.getCorpId(), reqBody.getUserId());
                break;
            case OP_USER_MODIFY_AUTH_SCOPE:
                event = new OpUserModifyScopeEvent(reqBody.getCorpId(), reqBody.getUserId());
                break;
            case USER_LEAVE_ORG:
                event = new UserLeaveEvent(reqBody.getCorpId(), reqBody.getUserId());
                break;
            case ORG_DEPT_CREATE:
//                event = new DeptCreateEvent(reqBody.getCorpId(), reqBody.getDeptId());
                break;
            case ORG_DEPT_MODIFY:
//                event = new DeptModifyEvent(reqBody.getCorpId(), reqBody.getDeptId());
                break;
            case ORG_DEPT_REMOVE:
//                event = new DeptRemoveEvent(reqBody.getCorpId(), reqBody.getDeptId());
                break;
            case ROLE_ADD_OR_MODIFY:
                event = new RoleAddOrModifyEvent(reqBody.getCorpId(), reqBody.getRoleInfo(), reqBody.getAppType());
                break;
            case ROLE_REMOVE:
                event = new RoleRemoveEvent(reqBody.getCorpId(), reqBody.getRoleInfo(), reqBody.getAppType());
                break;
            case OP_ROLE_ADD_REMOVE_USER:
                event = new OpRoleAddRemoveUserEvent(reqBody.getCorpId(), reqBody.getRoleInfo(), reqBody.getAppType());
                break;
            case OP_NODE_MODIFY:
                String[] nodeInfo = StringUtils.split(reqBody.getDeptId(), Constants.COMMA);
                event = new OpNodeAddOrModifyEvent(reqBody.getCorpId(), reqBody.getAppType(), nodeInfo[Constants.INDEX_ONE], nodeInfo[Constants.INDEX_ZERO]);
                break;
            case OP_NODE_DELETE:
                event = new OpNodeDeleteEvent(reqBody.getCorpId(), reqBody.getAppType(), reqBody.getDeptId());
                break;
            /**
             * 企业微信Mq消费分发事件
             */
            case CREATE_USER:
                event = new CreateUserEvent(reqBody.getCorpId(), reqBody.getUserId(), reqBody.getAppType());
                break;
            case UPDATE_USER:
                event = new UpdateUserEvent(reqBody.getCorpId(), reqBody.getUserId(), reqBody.getAppType());
                break;
            case DELETE_USER:
                event = new DeleteUserEvent(reqBody.getCorpId(), reqBody.getUserId(), reqBody.getAppType());
                break;
            case CREATE_PARTY:
                event = new CreatePartyEvent(reqBody.getCorpId(), reqBody.getDeptId(), reqBody.getAppType());
                break;
            case UPDATE_PARTY:
                event = new UpdatePartyEvent(reqBody.getCorpId(), reqBody.getDeptId(), reqBody.getAppType());
                break;
            case DELETE_PARTY:
                event = new DeletePartyEvent(reqBody.getCorpId(), reqBody.getDeptId(), reqBody.getAppType());
                break;
            case WORKFLOW_INSTANCE_CHANGE_DIRECTED:
                event = new OaPluginEvent(reqBody.getCorpId(), reqBody.getBizData(), reqBody.getAppType(), reqBody.getEventType());
                break;
            case WORKFLOW_TASK_CHANGE_DIRECTED:
                event = new OaPluginEvent(reqBody.getCorpId(), reqBody.getBizData(), reqBody.getAppType(), reqBody.getEventType());
                break;
            default:
                throw new IllegalArgumentException(String.format("incorrect event type %s", reqBody.getEventType()));
        }

        return event;
    }

}
