package com.coolcollege.intelligent.common.constant;
/**
 * 通用常量
 * @ClassName  CommonConstant
 * @Description 通用常量
 * @author Aaron
 */
public class CommonConstant {

    /**
     * 企业ID
     */
    public static final String  ENTERPRISE_ID = "enterpriseId";

    public static final String  MSG_TYPE = "msgtype";

    public static final String  STORE_NAME = "storeName";

    public static final String  SIGN_IN_TIME = "signInTime";

    public static final String  SIGN_OUT_TIME = "signOutTime";

    public static final String  STORE_ID = "storeId";

    public static final String  FEI_SHU_SEND_CARD_ENTERPRISE = "fei_shu_send_card_enterprise";

    /**
     * 企业前缀
     */
    public static final String  SUB_TABLE_SUFFIX = "authority_group_content_mapping_";

    public static final int MAX_EXPORT_LIST = 50000;

    /**
     * 企业开通锁存活时间
     */
    public static final int ENTERPRISE_OPEN_LOCK_TIMES = 4 * 60 * 60 * 1000;

    /**
     * 日志相关
     */
    public static final String LOG_ADD="ADD";
    public static final String LOG_UPDATE="UPDATE";
    public static final String LOG_DELETE="DELETE";

    public static final int IS_DELETE_YES = 1;//已删除
    public static final int IS_DELETE_NO = 0;//未删除

    public static final class Function {
        public static final String TASK = "任务";
        public static final String TEMPLATE = "检查表";
        public static final String STATIC_PERSON = "静态人员库";
        public static final String STATIC_PERSON_GROUP = "静态人员库分组";
        public static final String ROLE = "角色";
        public static final String DEVICE = "设备";
        public static final String DISPLAY_TASK = "陈列任务";
        public static final String ENTERPRISE = "企业设置";
        public static final String PASSENGER_FLOW_MANAGEMENT= "客流管理";

        public static final String USER = "用户";
        public static final String IMPORT = "导入";
        public static final String CHECK_TABLE = "检查表";
        public static final String PATROL_TASK = "巡店任务";
        public static final String POSITION = "岗位";
        public static final String REGION = "区域";
        public static final String SHARE = "分享";
        public static final String STORE = "门店";
        public static final String NSSTORE = "新店";
        public static final String VIDEO = "摄像头";
        public static final String SOP = "SOP文档";
        public static final String EXTEND_FIELD = "动态扩展字段";

        public static final String STOREWORK = "店务";
        public static final String RECOMMEND_STYLE = "群应用-主推款";

    }
    public static final String OPER_ADD="ADD";
    public static final String OPER_UPDATE="UPDATE";

    public static final String DEFAULT_DB = "coolcollege_intelligent_config";
    public static final Integer NORMAL_LOCK_TIMES = 30 * 60 * 1000;
    public static final Integer SYNC_LOCK_TIMES = 2 * 60 * 60  * 1000;
    public static final Integer PATROL_LOCK_TIMES = 60 * 1000;

    /**
     * 扩展信息
     */
    public static final class ExtendInfo {
        /**
         * 陈列审批附件图片列表
         */
        public static final String DISPLAY_APPROVE_IMAGE_LIST = "displayApproveImageList";
    }
}
