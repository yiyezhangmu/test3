package com.coolcollege.intelligent.common.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/11/19 20:27
 */
public class UnifyTaskConstant {


    /**
     * 统一任务对应处理表编码
     */
    public static final HashMap<String, String > TASK_HANDLE_MAP = new HashMap<String, String>(){{
        put("DISPLAY_TASK","DISPLAY_HANDLE");
        put("PATROL_STORE_INFORMATION","SELF");
        put("QUESTION_ORDER","SELF");
    }};
    /**
     * 任务角色-创建
     */
    public static final String ROLE_CREATE = "create";
    /**
     * 任务角色-流程审批或处理者
     */
    public static final String ROLE_CC = "cc";
    /**
     * 任务角色-流程审批或处理者
     */
    public static final String ROLE_APPROVAL = "approval";
    /**
     * 任务角色-处理转交
     */
    public static final String ROLE_TRANSFERRED = "transferred";
    /**
     * 流程状态-初始化init
     */
    public static final String FLOW_INIT = "init";
    /**
     * 流程状态-进行中
     */
    public static final String FLOW_PROCESSED = "processed";
    /**
     * 无检查表类通用任务bizcode
     */
    public static final String SELF_BIZCODE = "SELF";

    /**
     * 分享
     */
    public static final String TASK_SHARE = "TASK_SHARE:";

    public static final List<String> PATROL_LIST = Arrays.asList("PATROL_STORE_ONLINE", "PATROL_STORE_OFFLINE", "PATROL_STORE_PICTURE_ONLINE",
            "PATROL_STORE_AI","PRODUCT_FEEDBACK");

    /**
     * 推送消息节点判断数组
     */
    public static final List<String> SEND_MESSAGE_NODE = Arrays.asList("2", "3", "4", "5", "6");

    public static final HashMap<String, String > TASK_STATUS_MAP = new HashMap<String, String>(){{
        put("1","handle");
        put("2","approve");
        put("3","recheck");
        put("endNode","complete");
    }};

    /**
     * 任务消息相关
     */
    public static final class TaskMessage {
        public static final String OPERATE_ADD = "ADD";
        public static final String OPERATE_TURN = "TURN";
        public static final String OPERATE_PASS = "PASS";
        public static final String OPERATE_REJECT = "REJECT";
        public static final String OPERATE_COMPLETE = "COMPLETE";
        public static final String OPERATE_DELETE = "DELETE";
        public static final String OPERATE_REALLOCATE = "REALLOCATE";
        public static final String EXPIRE_REMIND= "EXPIRE_REMIND";
        public static final String EXPIRE_BEFORE_REMIND= "EXPIRE_BEFORE_REMIND";
        public static final String OPERATE_LOG = "OPERATE_LOG";
        public static final String PRIMARY_KEY = "primary_key";
        public static final String OPERATE_KEY = "operate";
        public static final String UNIFY_TASK_ID_KEY = "unify_task_id";
        public static final String TASK_TYPE_KEY = "task_type";
        public static final String CREATE_USER_ID_KEY = "create_user_id";
        public static final String CREATE_TIME_KEY = "create_time";
        public static final String DATA_KEY = "data";
        public static final String ENTERPRISE_ID_KEY = "enterprise_id";
        public static final String TASK_INFO = "task_info";
        public static final String ATTACH_URL = "attach_url";
        public static final String TASK_HANDLE_DATA_KEY = "task_handle_data";
        public static final String NODE_NO_KEY = "node_no";
        public static final String TASK_PARENT_ITEM_ID = "task_parent_item_id";
        public static final String LOOP_COUNT = "loop_count";
        public static final String QUESTION_RECORD_ID = "question_record_id";
        public static final String STORE_ID = "store_id";
        public static final String QUESTION_TYPE = "question_type";
    }

    /**
     * 任务类型
     */
    public static final class TaskType{
        public static final String PATROL_STORE_ONLINE = "PATROL_STORE_ONLINE";
        public static final String PATROL_STORE_OFFLINE = "PATROL_STORE_OFFLINE";
        public static final String PATROL_STORE_PICTURE_ONLINE = "PATROL_STORE_PICTURE_ONLINE";
        public static final String PATROL_STORE_INFORMATION = "PATROL_STORE_INFORMATION";
        public static final String PATROL_STORE_AI = "PATROL_STORE_AI";
        public static final String QUESTION_ORDER = "QUESTION_ORDER";
        public static final String DISPLAY_TASK = "DISPLAY_TASK";
        public static final String PRODUCT_FEEDBACK = "PRODUCT_FEEDBACK";
    }

    public static final class FormType{
        public static final String DISPLAY_PG = "DISPLAY_PG";
        public static final String DEFINE = "DEFINE";
        public static final String STANDARD = "STANDARD";
        public static final String STA_COLUMN = "STA_COLUMN";
        public static final String STANDARD_COLUMN = "STANDARD_COLUMN";
    }

    public static final class ApproveType{
        public static final String ANY = "any";
        public static final String ALL = "all";
    }

    public static final class PersonType{
        public static final String PERSON = "person";
        public static final String POSITION = "position";
        public static final String USER_GROUP = "userGroup";
        public static final String ORGANIZATION = "organization";
    }

    public static final class StoreType{
        public static final String REGION = "region";
        public static final String STORE = "store";
        public static final String GROUP = "group";
        public static final String GROUP_REGION = "groupRegion";
    }

    public static final class OperateType{
        public static final String HANDLE = "handle";
        public static final String APPROVE = "approve";
        public static final String TURN = "turn";
        public static final String REALLOCATE = "reallocate";
    }

    public static final class TaskInfo{
        public static final String PHOTOS = "photos";
        public static final String VIDEOS = "videos";
        public static final String SOUND_RECORDING_LIST = "soundRecordingList";
    }

    public static final String TASK_TYPE_QUESTION_RECORD = "问题工单";
}
