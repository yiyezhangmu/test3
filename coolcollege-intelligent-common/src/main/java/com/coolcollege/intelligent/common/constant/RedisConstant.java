package com.coolcollege.intelligent.common.constant;

/**
 * redis key 类
 * @author byd
 * @date 2021-03-11 14:55
 */
public class RedisConstant {

    public static final String TB_DISPLAY_HANDLE = "tb_display_handle_";
    /**
     * 新陈列审核
     */
    public static final String TB_DISPLAY_APPROVE = "tb_display_approve_";

    /**
     * 新陈列复核
     */
    public static final String TB_DISPLAY_RECHECK = "tb_display_recheck";

    /**
     * 新陈列三级审批
     */
    public static final String TB_DISPLAY_THIRD_APPROVE = "tb_display_third_approve";

    /**
     * 新陈列四级审批
     */
    public static final String TB_DISPLAY_FOUR_APPROVE = "tb_display_four_approve";

    /**
     * 新陈列五级审批
     */
    public static final String TB_DISPLAY_FIVE_APPROVE = "tb_display_five_approve";

    /**
     * 区域/组织同步锁key 点击同步锁key
     */
    public static final String REGION_SYNC_LOCK = "region_sync_lock_";

    /**
     * 问题工单key
     */
    public static final String QUESTION_TASK_LOCK = "question_task_lock_";

    /**
     * 店务工单key
     */
    public static final String STORE_WORK_QUESTION_TASK_LOCK = "store_work_question_task_lock_";

    /**
     * 企业同步key 有效拦截
     */
    public static final String EID_SYNC_EFFECTIVE = "eid_sync_effective_";

    /**
     * 通知key做为拦截
     */
    public static final String TASK_STAGE_NOTICE = "task_stage_notice_";

    public static final String TASK_DEL_FLAG = "task_del_flag_";



    /**
     * 系统内区域key
     */
    public static final String REGION_SYNC_ALL= "region_sync_all_";

    /**
     * 系统内区域key
     */
    public static final String STORE_SYNC_ALL= "store_sync_all_";

    /**
     * 企业广告配置信息key前缀
     */
    public static final String ADVERT_SETTING_PREFIX= "advert_setting_";

    /**
     * 企业广告配置信息value
     */
    public static final String ADVERT_SETTING_VALUE= "1";

    /**
     * 企业广告配置信息value
     */
    public static final String COOL_TOKEN_PREFIX= "cool_token_";

    /**
     * 用户企业token前缀
     */
    public static final String ACCESS_TOKEN_PREFIX ="access_token:";

    /**
     * 用户企业token和用户id关联前缀
     */
    public static final String USER_ID_ACCESS_TOKEN_PREFIX ="access_token_user_id:{0}:{1}";

    /**
     * 用户企业token和角色id关联前缀
     */
    public static final String ROLE_ID_ACCESS_TOKEN_PREFIX ="access_token_role_id:{0}:{1}";

    /**
     * refresh token
     */
    public static final String REFRESH_TOKEN_PREFIX ="refresh_token:";

    /**
     * 用户账号密码错误次数key
     */
    public static final String ERROR_PASSWORD_COUNT_KEY = "errorPasswordCount_{0}_{1}";

    public static final String BOSS_ACCOUNT_LOGIN = "bossAccountLogin_{0}_{1}";

    /**
     * 未完成转码
     */
    public static final String VIDEO_NOT_COMPLETE_CACHE = "video_not_complete_cache";
    /**
     * 任务抓拍状态记录
     */
    public static final String CAPTURE_PICTURE_STATUS_PREFIX ="capture_picture_status:";

    /**
     * 视频转码回调
     */
    public static final String VIDEO_CALLBACK_CACHE = "video_callback_cache_";

    /**
     * 任务重新分配 防止重复提交
     */
    public static final String TASK_STORE_REALLOCATE = "task_store_reallocate_";

    /**
     * 是否返回区域对应门店权限redis控制key
     */
    public static final String SHOW_STORE_AUTH = "show_store_auth";

    /**
     * 是否返回区域对应门店权限redis控制key
     */
    public static final String FIRST_LOGIN = "first_login_";

    /**
     * 区域全路径名称缓存
     */
    public static final String REGION_ALL_NAME_CACHE = "region_all_name_cache_";

    /**
     * 获取菜单引导key
     */
    public static final String GUIDE_INFO = "guide_info_";

    /**
     * 组织架构模块是否第一次登录
     */
    public static final String OrgModuleFirstLogin = "OrgModuleFirstLogin:{0}:{1}";

    /**
     * 历史企业key
     */
    public static final String HISTORY_ENTERPRISE = "historyEnterprise";

    /**
     * 留资企业key
     */
    public static final String LEAVE_ENTERPRISE = "leaveEnterprise";

    /**
     * 留资开关
     */
    public static final String LEAVE_OPEN = "leaveOpen";

    /**
     * 工单分享key
     */
    public static final String QUESTION_SHARE_KEY = "question_share:{0}";

    /**
     * 企业配置信息
     */
    public static final String ENTERPRISE_CONFIG_KEY = "enterpriseConfig:{0}";

    public static final String ENTERPRISE_KEY = "enterpriseInfo:{0}";

    public static final String ENTERPRISE_NAME_KEY = "enterpriseName:{0}";

    public static final String DING_AUTH_KEY = "dingAuthKey:{0}";


    public static final String ENTERPRISE_DB_SERVER = "enterpriseDBServer:{0}";

    public static final String QUESTION_SETTING_CACHE_KEY = "questionSettingCacheKey:{0}";

    public static final String ENTERPRISE_SETTING_CACHE_KEY = "enterpriseSettingCacheKey:{0}";

    public static final String ENTERPRISE_STORE_SETTING_CACHE_KEY = "enterpriseStoreSettingCacheKey:{0}";

    public static final String QUESTION_CACHE_KEY = "questionDataCache:{0}:{1}:{2}_{3}";


    public static final String QUESTION_NOTICE_KEY = "questionNoticeCache:{0}:{1}:{2}:{3}_{4}_{5}_{6}";

    public static final String STOREWORK_NOTICE_KEY = "storeWorkNoticeCache:{0}:{1}:{2}:{3}";

    public static final String FOODCHECK_NOTICE_KEY = "foodCheckNoticeCache:{0}:{1}:{2}:{3}:{4}";


    public static final String INSERT_OR_UPDATE_ROLE_KEY = "insertOrUpdateSysRole_key:{0}";
    /**
     * 七天
     */
    public static final int SEVEN_DAY = 7 * 24 * 60 * 60;

    /**
     * 1天
     */
    public static final int ONE_DAY_SECONDS = 24 * 60 * 60;

    /**
     * 3天
     */
    public static final int THREE_DAY = 3 * 24 * 60 * 60 * 1000;

    /**
     * 1分钟
     */
    public static final int ONE_MINUTES = 60 * 1000;

    /**
     * 五分钟
     */
    public static final int THREE_MINUTES = 60 * 3 * 1000;

    public static final String UPCOMING_BACKLOG_ID = "upcoming_backlog:{0}:{1}:{2}";


    public static final String ENTERPRISE_OPEN_STATUS_KEY = "enterprise_open_status:{0}_{1}";

    public static final String STOREWORK_BUILD_CACHE_KEY = "storeworkBuildDataCache:{0}:{1}:{2}";

    public static final String STORE_WORK_COMMENT_CACHE_KEY = "storeWorkCommentCache:{0}:{1}:{2}";

    public static final String DEVICE_OPEN_TOKEN = "device_open_token:{0}:{1}:{2}";

    public static final String DEVICE_OPEN_TOKEN_SN = "device_open_token_sn:{0}:{1}:{2}";

    public static final String DEVICE_KEEP_LOGIN = "device_keep_login:{0}:{1}:{2}";

    public static final String VIDEO_SETTING_KEY = "video_setting_key:{0}:{1}:{2}";

    public static final String USERORDERTOP = "UserOrderTop:{0}";

    public static final String STORE_ORDER_TOP = "StoreOrderTop:{0}";

    public static final String COUNT_NEWSPAPER_HQ = "CountNewspaperHq:{0}";

    public static final String WECHAT_ACCESS_TOKEN = "wechat_access_token:{0}:{1}";

    public static final String WECHAT_TICKET = "wechat_ticket:{0}";

    public static final String WEEKLYNEWSPAPER_CACHE_KEY = "WeeklyNewspaperCache:{0}:{1}:{2}:{3}:{4}";

    public static final String TASK_STATUS_KEY = "task_status_key:";

    public static final String WECHAT_APP_ID_KEY = "wechat_app_id_key";

    public static final String WECHAT_MSG_TEMPLATE_DATA_KEY = "wechat_msg_template_data";

    /**
     * 萤石云视频录像下载API项目id redis key
     */
    public static final String YINGSHI_VIDEO_PROJECT_ID_KEY = "yingshi_video_project_id:{0}:{1}";

    /**
     * 小程序AccessToken key
     */
    public static final String APPLET_ACCESS_TOKEN_KEY = "applet_access_token:{0}";

    /**
     * 线上酷店掌accessToken
     */
    public static final String ONLINE_X_STORE_ACCESS_TOKEN = "online_x_store_access_token";

    /**
     * 设备抓拍图片key
     */
    public static final String DEVICE_CAPTURE_PICTURE_KEY = "device_capture_picture_key:{0}:{1}";

    /**
     * 设备抓拍图片key
     */
    public static final String DEVICE_CAPTURE_LOCK_PICTURE_KEY = "device_capture_lock_picture_key:{0}:{1}";

    /**
     * 门店检测合格次数
     */
    public static final String CAPTURE_CHECK_PASS_LOCK_PICTURE_KEY = "capture_check_pass_lock_picture_key:{0}:{1}";

    /**
     * 设备抓拍图片结果key
     */
    public static final String DEVICE_CAPTURE_PICTURE_RESULT_KEY = "device_capture_picture_result_key:{0}:{1}";


    public static final String REMOVE_ROLE_EID_KEY = "remove_role_eid_key";

    public static final String JIE_FENG_TOKEN= "jie_feng_flow_token" ;
    public static final String STORE_ARE_NON_LEAF_NODES = "storeAreNonLeafNodes";

}
