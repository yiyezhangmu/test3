package com.coolcollege.intelligent.common.constant;

import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName Constants
 * @Description 用一句话描述什么
 */
public class Constants {
    public static final String ENTERPRISE_DATABASE_PREFIX = "coolcollege_enterprise_";
    public static final String UNDER_LINE = "_";

    public static final String QUESTION_MARK = "?";
    public static final String SLASH = "/";
    public static final String SPOT = ".";

    /**
     * 用户登录令牌有效期，单位秒。默认7200秒 -> 14400秒
     */
    public static final int ACTION_TOKEN_EXPIRE = 14400;

    public static final int REFRESH_TOKEN_EXPIRE = 60*60*24*30;

    public static final String TRUE = "true";

    public static final String FALSE = "false";

    public static final String STRING_ZERO = "0";

    public static final String ROOT_REGION_ID = "1";

    public static final Long MAX_EXPORT_SIZE = 100000L;

    public static final Integer MAX_QUERY_SIZE = 1000;

    public static final Integer ONE_HUNDRED = 100;

    public static final Integer PAGE_SIZE = 100;

    public static final Integer PAGE_SIZE_TEN = 10;

    public static final Integer THIRTY_DAY = 30;

    public static final Integer INDEX_THIRTY = 30;

    public static final Integer INDEX_FORTY = 40;

    public static final String SYSTEM = "system";

    public static final String ROOT_DEPT_ID_STR = "1";

    public static final Long ROOT_DEPT_ID = 1L;

    public static final String E_APP_LOGIN_IN_PAGE = "eapp://pages/common-web-view/index?routerUrl=notice&target=";

    public static final String E_APP_COOL_COLLEGE = "eapp://pages/common-web-view/index?routerUrl=notice&noticeType=coolcollege&";

    public static final String BACKLOG_URL = "dingtalk://dingtalkclient/action/open_micro_app?miniAppId=%s&appId=%s&corpId=%s&page=pages/router/router?target=";

    public static final String BACKLOG_NEW_URL_SUFFIX = "dd-noticemsg?miniAppId=%s&appId=%s&corpId=%s&appUrl=pages/router/router?target=";

    public static final String BACKLOG_NEW_URL = "dingtalk://dingtalkclient/action/open_micro_app?miniAppId=%s&appId=%s&corpId=%s&page=";

    public static final String PAGE_URL_PREFIX = "pages/common-web-view/index?routerUrl=notice&target=";

    /**
     * 隐藏引用待办跳转链接  跳到一方化
     */
    public static final String ONEPARTY_BACKLOG_URL = "dingtalk://dingtalkclient/page/link?pc_slide=true&url=";

    public static final String MDT_TRAIN_PAGE_LINK = "dingtalk://dingtalkclient/page/link?url=";

    public static final String ONEPARTY_OPEN_PLATFORM_LINK = "dingtalk://dingtalkclient/action/open_platform_link?pcLink=%s&mobileLink=%s";


    public static final String NOTICE_PAGE_PREFIX = "dd-noticemsg";

    public static class STATUS {

        private STATUS() {
        }

        /**
         * 删除状态
         */
        public static final int DELETE = -1;

        /**
         * 初始状态
         */
        public static final int INITIAL = 0;

        /**
         * 正常状态
         */
        public static final int NORMAL = 1;

        /**
         * 冻结状态
         */
        public static final int FREEZE = 100;


        /**
         * 所动状态
         */
        public static final int LOCK = 100;

        /**
         * 数据创建失败
         */
        public static final int FILED = 88;

        public static String getValue(int status) {
            switch (status) {
                case -1:
                    return "已删除";
                case 0:
                    return "初始";
                case 1:
                    return "正常";
                case 100:
                    return "冻结";
                case 88:
                    return "创建失败";
            }
            return "";
        }


    }

    /**
     * 付费企业
     */
    public static final Integer IS_VIP_VIP = 2;

    /**
     * 试用企业
     */
    public static final Integer IS_TEST_ENTERPRISE = 3;

    public static final String STRING_DELETE = "delete";

    /**
     * 钉钉端部门不存在的错误码
     */
    public static final String DING_ERR_CODE_DEPT_NOT_EXISTS = "60003";

    /**
     * 钉钉token失效错误码
     */
    public static final String DING_TOKEN_INVALID_CODE = "40014";

    /**
     * 企业授权的队列
     */
    public static final String MQ_QUEUE_NAME_AUTH = "authQueue";

    //企业通讯录变更事件队列
    public static final String MQ_QUEUE_NAME_ADDRESS_BOOK = "StoreAddressBookQueue";

    /**
     * 萤石云设备托管
     */
    public static final String MQ_YINGSHI_DEVICE_MANAGE = "yingshi_device_manage";

    /**
     * 购买队列
     */
    public static final String PAY_MARKET_BUY = "pay_market_buy";

    /**
     * 同步全量区域/职位/人员
     */
    public static final String MQ_DING_SYNC_ALL_DATA_QUEUE = "ding_sync_all_data_queue";

    /**
     * 更新区域路径消息
     */
    public static final String MQ_REGION_STORE_NUM_UPDATE_QUEUE = "region_store_num_update_queue";


    /**
     * 更新区域门店数量
     */
    public static final String MQ_PATROL_STORE_SCORE_COUNT_QUEUE = "patrol_store_score_count_queue";

    /**
     * 统一消息监听
     */
    public static final String MSG_UNITE_DATA_QUEUE = "msg_unite_data_queue";

    /**
     * 区域门店数量计算队列
     */
    public static final String MQ_RECURSION_REGION_STORE_NUM_QUEUE = "mq_recursion_region_store_num_queue";

    public static final String DEFAULT_USER_NAME = "公司";

    public static final Integer DEFAULT_DISTANCE = 100;

    public static final String DEPT_NAME_NONE = "空";

    /**
     * 待办消息结束队列
     */
    public static final String MQ_QUEUE_NAME_UPCOMING = "upcoming_finish";

    /**
     * 默认模板ID
     */
    public static final String DEFAULT_TEMPLATE_ID = "8cb238aa791243dcbe86dc0873467d6e";

    /**
     * 默认门店id
     */
    public static final String DEFAULT_INIT_STORE_ID = "default_store_id";

    /**
     * 批量保存数据每批数据量
     */
    public static final int BATCH_INSERT_COUNT = 200;
    /**
     * 默认钉钉岗位组ID
     */
    public static final String DEFAULT_DING_POSITION_ID = "50000000";
    /**
     * 通讯录导入最大限制条数
     */
    public static final int ADDRESS_BOOK_MAX_COUNT = 2000;
    /**
     * ES 数据插入到ES_队列
     */
    public static final String ES_SYNC_INDEX_DATA_QUEUE = "es_sync_index_data_queue";

    /**
     * ES 巡店数据加工
     */
    public static final String ES_DATA_WORKING_QUEUE = "es_data_working_queue";

    /**
     * ES 陈列数据加工
     */
    public static final String ES_DATA_DISPLAY_WORKING_QUEUE = "es_data_display_working_queue";

    /**
     * es 检查表类型 标准检查表
     */
    public static final String STANDARD = "STA";
    /**
     * es 检查表类型 自定义检查表
     */
    public static final String DEF = "DEF";

    public static final String CHECKED = "checked";

    public static final Integer LENGTH_SIZE = 1000;

    public static final Integer MSG_SIZE = 1000;

    public static final String UNCHECKED = "unchecked";



    // 阿里云开通
    public static final String MQ_OPEN_ENTERPRISE_ALIYUN = "OpenEnterpriseAliyun";


    public static class METHOD_RESULT_FLAG {
        private METHOD_RESULT_FLAG() {
        }

        public static final int SUCCESS = 0;
        public static final int FAIL = 1;

        public static String getValue(int flag) {
            switch (flag) {
                case 0:
                    return "成功";
                case 1:
                    return "失败";
            }
            return "";
        }
    }
    /**
     * 逗号
     */
    public static final String COMMA = ",";

    /**
     * 冒号
     */
    public static final String COLON = ":";
    /**
     * 中文逗号逗号
     */
    public static final String COMMA_CN = "，";
    /**
     * 顿号（中文）
     */
    public static final String PAUSE = "、";

    /**
     * 拼接符
     */
    public static final String MOSAICS = "#";

    /**
     * 拼接符 [
     */
    public static final String SQUAREBRACKETSLEFT = "[";

    /**
     * 拼接符 ]
     */
    public static final String SQUAREBRACKETSRIGHT = "]";

    /**
     * 系统用户id
     */
    public static final String SYSTEM_USER_ID = "system";
    /**
     * 系统用户id
     */
    public static final String SYSTEM_USER_NAME = "system";

    /**
     * 系统用户id
     */
    public static final String SYSTEM_USER_SEND_NAME = "系统";

    /**
     * 令牌前缀
     */
    public static final String BOSS_LOGIN_USER_KEY = "boss_user:";


    /**
     * 令牌前缀
     */
    public static final String BOSS_PASSWORD_KEY = "boss_auth_key";

    /**
     * 用户密码
     */
    public static final String USER_AUTH_KEY = "user_auth_key";

    /**
     * 巡店配置缓存
     */
    public static final String STORE_CHECK_SETTING_VO="STORE_CHECK_SETTING_VO_1_";
    public static final String STORE_CHECK_SETTING_DO="STORE_CHECK_SETTING_DO_";

    public static final String AI_USER_ID = "a100000001";

    public static final String SETTING_TOKEN = "setting_multi_token_";

    public static final String YUSHI_TOKEN = "yushi_token_";

    public static final String HIK_TOKEN = "hik_token_";

    public static final String HIK_URL = "hik_url:%s_%s";

    public static final String HIK_AUTH = "hik_auth:%s";

    public static final String ONE_VALUE_STRING = "1";

    public static final String TWO_VALUE_STRING = "2";

    public static final String FOUR_VALUE_STRING = "4";

    /**
     * 钉钉接口调用成功返回的错误码
     */
    public static final long DING_ERR_CODE_SUCCESS = 0;

    /**
     * 巡店类型  PATROL_STORE  线上巡店 线下巡店
     */
    public static final String PATROL_STORE_TYPE = "PATROL_STORE";

    public static final String ONCE = "ONCE";

    public static final Integer SUBMITSTATUS_TWO = 2;

    public static final Integer SUBMITSTATUS_FOUR = 4;

    /**
     * TbDataTable AI检查分析中标识
     */
    public static final Integer SUBMITSTATUS_EIGHT = 8;

    public static final Integer INDEX_ZERO = 0;

    public static final Integer INDEX_ONE = 1;

    public static final Integer INDEX_TWO = 2;

    public static final Integer INDEX_FOUR = 4;

    public static final Integer INDEX_TEN = 10;

    public static final Integer INDEX_TWENTY = 20;

    public static final Integer INDEX_THREE = 3;

    public static final Integer INDEX_FIVE = 5;

    public static final Integer INDEX_SIX = 6;

    public static final Integer TWO_HUNDRED = 200;

    public static final Integer ONE_THOUSAND = 10000;

    public static final Integer ONE_MILLISECOND = 1000;

    public static final Long TEN_MINUTE = 10 * 60 * 1000L;


    public static final String STR_ZERO = "0.00";

    public static final String STRING_INDEX_TWO = "2";
    public static final String STRING_INDEX_ONE = "1";

    public static final String BASE_STORE_TITLE="说明：\n" +
            "\n" +
            "1、门店ID唯一，由系统中自动生成，自行填写可能会造成无法识别；\n" +
            "\n" +
            "2、若没有相关字段信息，可不填写，批量导入时，系统会自动忽略；\n" +
            "\n" +
            "3、门店名称：必填，支持输入100个字，若超长，系统将会截取前100个字导入；\n" +
            "\n" +
            "4、门店编号：支持输入20位，不支持输入中文；\n" +
            "\n" +
            "5、门店分组支持多个导入，多个字段之间，请用逗号（英文半角逗号）隔开；门店分组必须是系统已存在的信息，如不存在，则会导入失败；\n" +
            "\n" +
            "6、门店地址：支持输入500个字；\n" +
            "\n" +
            "7、门店电话：支持输入50位，目前仅支持识别一个号码；\n" +
            "\n" +
            "8、营业开始/结束时间：输入格式xx:xx，例如：08:00；\n" +
            "\n" +
            "9、门店面积、带宽：支持输入20位；\n" +
            "\n" +
            "10、备注：支持输入400个字；\n" +
            "\n" +
            "11、门店状态：类型有营业、未开业、闭店 3种；\n" +
            "\n" +
            "注意：请勿合并单元格！请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败；";

    public static class STORE_STATUS {
        private STORE_STATUS() {
        }

        public static final String STORE_STATUS_OPEN = "open";
        public static final String STORE_STATUS_CLOSED = "closed";
        public static final String STORE_STATUS_NOT_OPEN = "not_open";
    }

    /**
     * 陈列图片数组
     */
    public static final String PHOTO_HANDLE_URL = "handleUrl";
    /**
     * 陈列图片数组(审批)
     */
    public static final String PHOTO_FINAL_URL = "finalUrl";


    /**
     * 文件类型:用户
     */
    public static final String USER_FILE_TYPE = "user";

    /**
     * 查重类型:手机号
     */
    public static final String MOBILE_FIELD = "mobile";


    /**
     * 不开启钉钉同步
     */
    public static final Integer ENABLE_DING_SYNC_NOT_OPEN = 0;
    /**
     * 开启钉钉同步
     */
    public static final Integer ENABLE_DING_SYNC_OPEN = 1;
    /**
     * 开启第三方同步
     */
    public static final Integer ENABLE_DING_SYNC_THIRD = 2;

    // 森宇促销员
    public static final String SENYU_ROLE_PROMOTION = "7";

    // 新店 测试门店id
    public static final Long DEFAULT_STORE_ID = 1L;
    // 线上环境 森宇企业id
    public static final String SENYU_ENTERPRISE_ID = "8f8bfac8df044930943c872fe84d11b2";

    // 线上环境 华莱士企业id
    public static final String HUALAISHI_ENTERPRISE_ID = "451c4fdf6b1645b79e439fea477c369e";

    // 区域
    public static final String REGION = "region";
    // 门店
    public static final String STORE = "store";


    public static final String PERSON_CHANGE_KEY_NEWADD = "newadd";
    public static final String PERSON_CHANGE_KEY_REMOVE = "remove";

    public static final String PICTURE_REPLACE_CODE = "picture_change_code";

    public static final String PICTURE_REPLACE_STORE_CODE = "picture_store_change_code";

    /**
     * eapp类型
     */
    public static final String E_APP = "e_app";

    /**
     * 图片类型
     */
    public static final String PNG = "png";
    public static final String JPG = "jpg";
    public static final String JPEG = "jpeg";
    public static final String MP4 = "mp4";

    /**
     * 多线程导出批量处理数量
     */
    public static final int BATCH_DEAL_EXPORT_NUM = 500;

    /**
     * get ticket aliyun sdk limit title length
     */
    public static final int LENGTH_EXCEEDED_MAX = 128;

    /**
     * 返回成功描述
     */
    public  static final String SUCCESS_STR = "success";

    /**
     * 转码格式
     */
    public static final String QYWX_USERDEPTINFO_COMPLETE_QUEUE = "qywx_userdeptinfo_complete_queue";


    /**
     * 抓拍不成功默认图片
     */
    public static final String DEFAULT_PICTURE_URL = "https://oss-cool.coolstore.cn/notice_pic/bea28cf5bcb740a78f59bba476c621d8.jpg";

    public static final String SPRIT = "/";

    /**
     * es hits count
     */
    public static final int ES_HITS_COUNT = 0;

    public static final int ZERO = 0;

    public static final int YES = 1;

    public static final int NO = 0;

    /**
     * 默认排名数
     */
    public static final int DEFAULT_RANKS = 10;

    public static final int TWENTY_RANKS = 20;

    public static final int THIRTY_RANKS = 30;

    public static final int FIFTY_INT = 50;

    public static final int TWO_HUNDROND = 200;



    public static final String TRANSCODE_VIDEO = "mp4";
    public static final String PRODUCT_REMIND = "product_remind";

    /**
     * get字符串
     */
    public static final String STRING_GET = "get";

    public static final String DEFAULT_STORE_NAME = "测试门店";

    public static final String DEFAULT_REGION_NAME_FIRST = "华东区";
    public static final String DEFAULT_REGION_NAME_SECOND = "华北区";
    public static final String DEFAULT_REGION_NAME_THIRD = "华南区";

    public static final String STORE_PATH_SPILT = "/";

    /**
     * 默认分页大小 10
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * Long型0
     */
    public static final Long LONG_ZERO = 0L;

    /**
     * Long型1
     */
    public static final Long LONG_ONE = 1L;

    /**
     * 下划线
     */
    public static final String UNDERLINE = "_";

    /**
     * 连接线
     */
    public static final String SPLIT_LINE = "-";

    /**
     * 用户导出白名单code
     */
    public static final String USER_EXPORT_WHITELIST = "user_export_whitelist";

    /**
     * 导出code
     */
    public static final String EXPORT = "export";

    /**
     * 导入code
     */
    public static final String IMPORT = "import";

    public static final Long USER_MANAGE_MENU_ID = 7L;

    /**
     * 不需要屏蔽的导出按钮
     */
    public static final Long CHECK_ITEM_MANAGE_MENU_ID = 9L;

    /**
     * 不需要屏蔽的导出按钮
     */
    public static final String AI_ROUT_PATH = "/operation/AIpictureInspection";


    /**
     * 永辉返回结果
     */
    public static final int SUCCESS_YONGHUI = 200;

    public static final String MQ_INIT_DEVICE="init_device_queue";

    public static final String QUESTION_CREATE_TYPE_KEY = "questionCreateType";

    public static final String CREATE = "create";

    public static final String MANAGE = "manage";

    public static final String DEFAULT_GROUP_NAME = "默认分组";

    public static final String SOP_NAME1 = "如何设置线下巡店任务.pptx";

    public static final String SOP_NAME2 = "如何配置门店区域.pptx";

    public static final String SOP_FILE_TYPE_PPTX = "pptx";

    public static final Integer COLUMN_NAME_MAX_LENGTH = 128;

    public static final Integer DEF_COLUMN_NAME_MAX_LENGTH = 100;

    /**
     * oss证照水印
     */
    public static final String OSS_IMAGE_WATER_MARK = "image/watermark,text_%s,rotate_330,fill_1,t_90,color_A7A7A7,size_32,shadow_50";

    public static final String LINE = "-";

    public static final String OPEN_SUCCEEDED_MSG_QUEUE = "open_succeeded_msg_queue";

    public static final String DEFAULT_AVATAR = "DefaultAvatar";

    public static final String XLSX_TYPE = "xlsx";

    /**
     * 用户人事状态查询条件统一前缀
     */
    public static final String PERSONNEL_STATUS_PRE = "personnel_status_query_";

    public static final String PERSONNEL_STATUS_NORMAL = "正常";

    public static final Integer MAX_INSERT_SIZE = 1000;
    public static final Integer PARTITION_MAX_SIZE=100;

    public static final Integer MAX_IMPORT_SIZE = 20000;

    /**
     * 是否返回区域对应门店权限redis控制key
     */
    public static final String WORK_FLOW = "work_flow_";

    public static final String HD_ENV = "hd";

    public static final String PRE_ENV = "pre";
    /**
     * 线上环境
     */
    public static final String ONLINE_ENV = "online";

    /**
     * 选择的人事状态最长时间
     */
    public static final int MAX_PERSONNEL_STATUS_TIME = 31;

    /**
     * 修改密码的周期
     */
    public static final int MAX_CHANGE_PASSWORD_TIME = 31;

    /**
     * 最多选择的检查表数
     */
    public static final int MAX_META_TABLE_NUM = 20;

    /**
     * 最大页面大小
     */
    public static final int MAX_PAGE_SIZE = 20;

    public static final String EMPTY_STRING = "";

    public static final String RMB = "￥";

    public static final String BR = "\n";

    /**
     * 新店分析表查询日期限制 31天
     */
    public static final Long NEW_STORE_STATISTICS_DAYS = 31L;

    /**
     * 上传文件签名url有效时长 3 小时
     */
    public static final int UPLOAD_URL_EXPIRE = 1000 * 60 * 60 * 3;

    /**
     *任务分解锁4小时
     */
    public static final int TASK_RESOLVE_LOCK = 1000 * 60 * 60 * 4;

    /**
     * 上传文件最大数量 9
     */
    public static final int UPLOAD_FILE_MAX_NUM = 9;

    /**
     * 周大福门店规则
     */
    public static final String ZHOU_STORE_REGX = ".*\\w+.*";

    /**
     * 微信自建应用跳转链接
     */
    public static final String WX_SELF_AUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
    /**
     * 选人限制
     */
    public static  final int PERSON_LIMIT = 200;
    /**
     * 业培一体的模块id
     */
    public static final Long TRAINING_BUSINESS_MODULE = 80000L;


    /**
     * 上传图片最大数量 9
     */
    public static final int UPLOAD_PIC_MAX_NUM = 9;

    /**
     * 一小时
     */
    public static final int ONE_HOUR = 60 * 60;

    /**
     * AI
     */
    public static final String AI = "AI";

    /**
     *
     */
    public static final String ZERO_STR = "0";

    public static final String ONE_STR = "1";

    /**
     * 长度限制
     */
    public static final int  STR_LENGTH_LIMIT= 128;

    /**
     * 全部
     */
    public static final String DEFAULT_ALL = "全部";

    public static final int FIVE_HUNDRED = 500;

    /**
     * 其他分类
     */
    public static final String OTHER_CATEGORY = "其他";

    /**
     * 分类长度限制
     */
    public static final Integer CATEGORY_NAME_MAX_LENGTH = 100;

    /**
     * 检查项长度限制
     */
    public static final Integer QUICK_COLUMN_NAME_MAX_LENGTH = 128;
    /**
     * 检查项结果长度限制
     */
    public static final Integer QUICK_COLUMN_RESULT_NAME_MAX_LENGTH = 20;
    /**
     * 检查项描述长度限制
     */
    public static final Integer QUICK_COLUMN_DESCRIPTION_NAME_MAX_LENGTH = 1000;
    /**
     * 分数限制
     */
    public static final Integer QUICK_COLUMN_MAX_SCORE = 100000;
    /**
     * 小数点数
     */
    public static final int SCALE = 2;

    /**
     * 负一
     */
    public static final int NEGATIVE = -1;

    /**
     * 检查项结果-最大奖罚
     */
    public static final BigDecimal QUICK_COLUMN_RESULT_MAX_MONEY = new BigDecimal(100000);

    /**
     * 检查项结果-最小奖罚
     */
    public static final BigDecimal QUICK_COLUMN_RESULT_MIN_MONEY = new BigDecimal(-100000);


    public static final String EXPORT_REGION_CODE = "key_export_region_code_";

    public static final String ROOT_REGION_PATH = "/1/";

    public static final String ROOT_DELETE_REGION_PATH = "/-1/";

    public static final int SIX_INT = 6;

    public static final int TWELVE= 12;

    public static final int THIRTY_ONE = 31;

    public static final int SEVEN = 7;

    public static final int SIX = 6;

    public static final int ONE = 1;

    public static final int TWO = 2;

    public static final String RECORDING_FILE_TYPE ="wav";

    /**
     * 请求id
     */
    public static final String REQUEST_ID = "requestId";

    /**
     * 消息id
     */
    public static final String MESSAGE_ID = "messageId";

    public static final Integer NORMAL_LOCK_TIMES = 30 * 60 * 60;






    public static final String PATROL_STORE_RANGE = "all";
    public static final String PATROL_STORE_RANGE_AUTH = "auth";
    public static final String STORE_STATUS_OPEN = "open";


    public static final BigDecimal Ten_Thousand = new BigDecimal(100000);
    public static final BigDecimal ONE_W = new BigDecimal(10000);

    /**
     * 周大福主管id
     */
    public static final String ZDF_MAIN_MANAGE_ROLE_ID = "1661135403360";
    /**
     * 周大福店长id
     */
    public static final String ZDF_DEPT_RANGE_SHOP_OWNER = "12";
    /**
     * 周大福b
     */
    public static final List<String> ZDF_DEPT_RANG_LIST = ImmutableList.of("6", "9", "10", "11").asList();

    public static final String THREE_STR = "3";

    public static final String FIVE_STR = "5";

    public static final String SEVEN_STR = "7";

    public static final int TEN = 10;

    public static final int ONE_THOUSAND_AND_SIX = 1006;

    public static final int THREE_HUNDRED = 300;

    public static final int THOUSAND = 1000;

    public static final String SUPERVISION_TYPE_PERSON = "person";

    public static final String SUPERVISION_TYPE_STORE = "store";

    public static final String ALL = "ALL";

    /**
     * 卡片前缀
     */
    public static final String PC_CARD_PREFIX_URL = "dingtalk://dingtalkclient/page/link?url={0}&pc_slide=true&bShowHeader=false";


    public static final String MOBILE_CARD_PREFIX_URL = "dingtalk://dingtalkclient/action/im_open_hybrid_panel?pageUrl={0}&hybridType=online&panelHeight=percent83";

    /**
     * 业绩分配
     */
    public static final String ASSIGN_USER_GOAL_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/store-performance-objectives?platform=h5&corpId={0}&synDingDeptId={1}&month={2}";

    /**
     * 开单播报链接
     */
    public static final String OPEN_ORDER_CARD_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/store-order-rank?platform=h5&corpId={0}&synDingDeptId={1}";

    /**
     * 畅销品
     */
    public static final String BEST_SELLER_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#cool-app/best-seller?platform=h5&corpId={0}&synDingDeptId={1}";


    /**
     * 门店大单笔数top10OPEN_ORDER_MONEY_CARD_URL
     */
    public static final String OPEN_ORDER_MONEY_CARD_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/store-amount-list?platform=h5&corpId={0}&synDingDeptId={1}";


    /**
     * 门店大单金额top10
     */
    public static final String OPEN_ORDER_NUM_CARD_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/store-order-number?platform=h5&corpId={0}&synDingDeptId={1}";

    /**
     * 周报统计
     */
    public static final String WEEKLY_STATISTICS_CARD = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/newspaper/list?platform=h5&active=statistics&corpId={0}&synDingDeptId={1}";
    /**
     * 周报提醒
     */
    public static final String WEEKLY_DING_CARD = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/newspaper/write?platform=h5&corpId={0}&synDingDeptId={1}";

    /**
     * 查看导购排行
     */
    public static final String VIEW_SHOPPER_RANK = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/shopping-guide-performance-ranking?platform=h5&corpId={0}&synDingDeptId={1}";

    /**
     * 分公司群的吊顶卡片
     */
    public static final String COMP_SUSPENDED_CARD_URL= "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/performance-summary?platform=h5&corpId={0}&synDingDeptId={1}";


    /**
     * 总部群业绩报告
     */
    public static final String HQ_ACHIEVE_CARD_URL ="https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/performance-summary/area?platform=h5&corpId={0}&synDingDeptId={1}";

    /**
     * 总部群业绩排行
     */
    public static final String HQ_ACHIEVE_RANK_CARD_URL ="https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/performance-completion-rate/area?platform=h5&corpId={0}&synDingDeptId={1}";

    /**
     * 信息反馈url
     */
    public static final String CONFIDENCE_FEEDBACK_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/confidence-feedback/components/fillIn?platform=h5&corpId={0}&synDingDeptId={1}";

    /**
     * 主推款链接
     */
    public static final String RECOMMEND_STYLE_CARD_URL="https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/main-promotion-funds-details?platform=h5&corpId={0}&id={1}";
    /**
     * 主推款学习链接
     */
    public static final String MAIN_PROMOTER_STUDY_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/main-promotion-funds-study?id={0}&corpId={1}";

    /**
     * 门店业绩简介卡片排行
     */
    public static final String STORE_ACHIEVE_SIMPLE_CARD_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/performance-completion-rate?platform=h5&&corpId={0}&synDingDeptId={1}";

    /**
     * 门店业绩丰富卡片
     */
    public static final String STORE_ACHIEVE_RICH_CARD_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/performance-summary?platform=h5&type=performanceRank&corpId={0}&synDingDeptId={1}";

    /**
     * 大单播报
     */
    public static final String BIG_ORDER_CARD_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/contact?corpId={0}&userId={1}";

    public static final String BIG_ORDER_DETAIL_URL = "https://app.aokang.com/go/coolstore/mdtcompmax/src/index.html?userId={0}";

    /**
     * 查看周报卡片
     */
    public static final String WEEKLY_PAPER_CARD_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/newspaper/detail?platform=h5&id={0}&corpId={1}&pc_slide=true&bShowHeader=false";

    /**
     * 信心反馈查看详情
     */
    public static final String FEEDBACK_CARD_URL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/confidence-feedback/components/feedbackInfo?platform=h5&id={0}&corpId={1}&pc_slide=true&bShowHeader=false";

    /**
     * 工作台
     */
    public static final String WORK_HOME = "dingtalk://dingtalkclient/action/open_micro_app?appId=78836&miniAppId=5000000000850657&page=%2Fpages%2Fcommon-web-view%2Findex%3FrouterUrl%3D%252Fhome&corpId={0}";

    /**
     * 巡店报告
     */
    public static final String POTRAL_PAPER = "https://store-h5.coolstore.cn/?storeCoolApp=true&fromPlat=H5&showPlat=mobile#/shopTourOffline/detail?recordId={0}&corpId={1}&openConversionId={2}&from=shop-record";

    /**
     * 巡店报告分享链接后缀
     */
    public static final String PATROL_STORE_RECORD_SHARE_SUFFIX = "?showPlat=mobile#/shopTourOffline/preview?recordId={0}&storeId={1}&enterpriseId={2}&type=allReport&tabKeys=tourOverView,checkItem";

    /**
     * 上升箭头
     */
    public static final String UP_ICON = "https://oss-cool.coolstore.cn/qyy/up.png";
    /**
     * 下降箭头
     */
    public static final String DOWN_ICON = "https://oss-cool.coolstore.cn/qyy/down.png";
    /**
     * 第一
     */
    public static final String NO_ONE_ICON = "https://oss-cool.coolstore.cn/qyy/no1.png";

    public static final String NO_TWO_ICON = "https://oss-cool.coolstore.cn/qyy/no2.png";

    public static final String NO_THREE_ICON = "https://oss-cool.coolstore.cn/qyy/no3.png";
    public static final String NO_FOUR_ICON = "https://oss-cool.coolstore.cn/qyy/no4.png";
    public static final String NO_FIVE_ICON = "https://oss-cool.coolstore.cn/qyy/no5.png";
    public static final String NO_SIX_ICON = "https://oss-cool.coolstore.cn/qyy/no6.png";
    public static final String NO_SEVEN_ICON = "https://oss-cool.coolstore.cn/qyy/no7.png";
    public static final String NO_EIGHT_ICON = "https://oss-cool.coolstore.cn/qyy/no8.png";
    public static final String NO_NINE_ICON = "https://oss-cool.coolstore.cn/qyy/no9.png";
    public static final String NO_TEN_ICON = "https://oss-cool.coolstore.cn/qyy/no10.png";


    public static final String NO_ONE_YELLOW = "https://oss-cool.coolstore.cn/qyy/yellow.png";

    public static final String NO_TWO_GRAY = "https://oss-cool.coolstore.cn/qyy/gray.png";

    public static final String NO_THREE_RED= "https://oss-cool.coolstore.cn/qyy/red.png";

    public static final String USER_DEFAULT_IMAGE = "https://oss-cool.coolstore.cn/qyy/user_default_avatar.jpg";

    public static final String JOSINY_USER_DEFAULT_IMAGE = "https://oss-cool.coolstore.cn/qyy/josiny_user_default_avatar.jpg";

    public static final String  AK_SHOPPER_DAILY= "https://app.aokang.com/go/coolstore/index.html?ddDeptId={0}&loginId={1}&type=2";

    public static final String  AK_STORE_HOME= "https://app.aokang.com/go/coolstore/index.html?ddDeptId={0}&type=1";

    public static final String DETAIL_FEEDBACK = "https://z1tykm.aliwork.com/APP_K2X1GIMCR7GAZ8R9CLMP/preview/FORM-IY966L71SNJA82YG7TQ3JD4TKY1E26P5AEIHLQ?navConfig.layout=1180&navConfig.type=none";

    public static final String STORE_REPORT = "门店巡店报告";

    public static final List<String> JOSINY_COMP_PARENT = Arrays.asList("区域", "代理区域");


    public static final List<String> PANASONIC_EIDS = Arrays.asList("f6708e77c53c4b45882ba4dd31bd5001", "cca977c6d84d47a38fe349216d686f82","220d678f532d4cbd884045ba819f2fb9");

    public static final String DAY = "day";

    public static final String MONTH = "month";

    public static final String WEEK = "week";

    public static final String NEW_BELLE_APPCODE = "5b7bac9a85cc498fb94ed5564065a7d3";

    public static final String NEW_BELLE_HEADER_PARAM = "X-DP-APPCODE";
    /**
     *货品反馈_半年内有库存店铺数据
     */
    public static final String NEW_BELLE_url_1 = "http://daas.bdc-kdz-prd.belle.net.cn/deepexi-daas-data-service/openapi/ee5f9dfa3c0a4196ae877816f55de46f/store_inv/ads_damai_ls/ads_services_6month_store_inv";
    /**
     * 公共基础类_商品信息
     */
    public static final String NEW_BELLE_url_2 = "http://daas.bdc-kdz-prd.belle.net.cn/deepexi-daas-data-service/openapi/ee5f9dfa3c0a4196ae877816f55de46f/dim/ads_bdc_services/dim_pro_info";

    public static final String NODE_NO_APPEAL_APPROVE = "appealApprove";

    public static final String DING_CARD_URL_ONE = "https://api.dingtalk.com/v1.0/im/interactiveCards/send";

    public static final String ROBOT_CODE = "4zShN1GYHLeAxc116318818082601008";

    //巡店报告卡片模板id
    public static final String PATRAL_MODE = "3260acd8-3ee3-44f7-8fb1-324007957c94.schema";
    //吊顶卡片模板
    public static final String DD_MODE = "23a05592-ccdd-48cc-8dab-fc438d9e9aa5.schema";

    //关闭吊顶卡片
    public static final String DING_CARD_CLOSE_DD = "https://api.dingtalk.com/v2.0/im/topBoxes/close";

    //酷应用app编码
    public static final String COOL_APP_CODE = "COOLAPP-1-102484ADDE022105A834000L";

    //吊顶卡片
    public static final String DING_CARD_URL_TWO = "https://api.dingtalk.com/v2.0/im/topBoxes";

    //销量top5
    public static final String SALE_TOP_5 = "sales";
    //库存top10
    public static final String INVENTORY_TOP_10 = "inventory";


    /**
     * 阿水大茶杯企业id
     */
    public static final String A_SHUI_EID = "ade0ea3b835f482e80bfa2fad31f57ae";

    /**
     * 根节点
     */
    public static final String REGION_TYPE_ROOT = "root";

    public static final String REGION_TYPE_PATH = "path";


    /**
     * 今日目标
     */
    public static final String TODAY_GOAL = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/ZSN/national-goals?platform=h5&corpId={0}&synDingDeptId={1}";

    /**
     *业绩报告
     */
    public static final String ACHIEVE_REPORT = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/ZSN/performance-report?platform=h5&corpId={0}&synDingDeptId={1}&type={2}&conversionType={3}";

    /**
     * 区域单产排行
     */
    public static final String REGION_TOP = " https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/ZSN/regional-yield-ranking?platform=h5&corpId={0}&synDingDeptId={1}";
    /**
     * FBA学习
     */
    public static final String FAB = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/ZSN/fba-study?platform=h5&corpId={0}&synDingDeptId={1}";
    /**
     * 畅销品
     */
    public static final String BEST_SELLER = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/ZSN/best-seller?platform=h5&corpId={0}&synDingDeptId={1}";
    /**
     * 门店业绩排行
     */
    public static final String STORE_ACHIEVE = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/ZSN/store-performance-ranking?platform=h5&corpId={0}&synDingDeptId={1}&type={2}";
    /**
     * 店铺商品快报
     */
    public static final String BULLETIN = "https://h5.dingtalk.com/h5-shop-enterprise/index.html/#/cool-app/ZSN/product-bulletin?platform=h5&corpId={0}&synDingDeptId={1}&type={2}";

    public static final String CHILD = "child";

    public static final String LEAF = "leaf";

    public static final String TASKNOTICECOMBINE = "taskNoticeCombine";

    public static final String MOBILE_HOME = "home";

    public static final Integer EXCEL_2007_MAX_TEXT_LENGTH = 1048576;

    public static final String PRODUCT = "product";

    public static final String INVESTMENT_MANAGER = "招商经理";
    public static final String HUMAN_TRAINING = "人训";
    public static final String STORE_ASSISTANCE = "店助";
    // 工单完成推子工单详情数据  福州开蚝屋餐饮管理 杭州酷店掌科技
    public static final List<String> SUB_WORK_ORDER_DETAIL_EIDS = Arrays.asList("54ed3352d69a43aa88b15ae6874c4a1f", "140e9bf7acf445a08864d1afcc1814fa");
    // 开蚝屋 店长职位id
    public static final String KAIHAOWU_SHOPOWNER_ID = "1712817456003";
    // 蒙自源POC环境
    public static final String MENGZIYUAN_ENTERPRISE_ID = "29ba9e4a44414f3bb58f1711e32fbf17";
    public static final String MENGZIYUAN2_ENTERPRISE_ID = "6eba983038b64567b7715495da5dd7f4";
    // 八马  同步挂在云眸  平台账号下的设备客流数据
    public static final List<String> SYNC_PLATFORM_PASSENGER_EIDS = Arrays.asList("f29f0a6644e24e54a8482725f0da76a6", "140e9bf7acf445a08864d1afcc1814fa", "e17cd2dc350541df8a8b0af9bd27f77d");
    /**
     * 美宜佳角色id 用户非门店职位
     */
    public static final String MYJ_ROLE_ID = "1734335758045";

    // 新任务重分配
    /**
     * 移除子任务id列表
     */
    public static final String REMOVE_SUB_TASK_ID_LIST = "removeSubTaskIdList";
    /**
     * 移除用户id列表
     */
    public static final String REMOVE_USER_ID_LIST = "removeUserIdList";
    /**
     * 新增子任务列表
     */
    public static final String ADD_SUB_TASK_LIST = "addSubTaskList";

    public static final String X_STORE = "好多店";

    /**
     * 检查项常量
     */
    public static class TableColumn {
        /**
         * 检查项描述是否必填
         */
        public static final String DESC_REQUIRED = "descRequired";

        /**
         * 检查项自动工单有效期
         */
        public static final String AUTO_QUESTION_TASK_VALIDITY = "autoQuestionTaskValidity";

        /**
         * 是否设置工单有效期
         */
        public static final String IS_SET_AUTO_QUESTION_TASK_VALIDITY = "isSetAutoQuestionTaskValidity";

        public static final String MIN_CHECK_PIC_NUM = "minCheckPicNum";

        public static final String MAX_CHECK_PIC_NUM = "maxCheckPicNum";

    }

    /**
     * 店务AI
     */
    public static class STORE_WORK_AI {
        /**
         * AI模型
         */
        public static String AI_MODEL = "aiModel";

        /**
         * AI场景id
         */
        public static String AI_SCENE_ID = "aiSceneId";

        /**
         * AI结果处理方式
         */
        public static String AI_RESULT_METHOD = "aiResultMethod";

        /**
         * 是否开启AI检查
         */
        public static String IS_AI_CHECK = "isAiCheck";

        /**
         * 不生产日清任务的日期范围
         */
        public static String NOT_GENERATE_RANGE = "notGenerateRange";

        /**
         * 使用AI日期
         */
        public static String USE_AI_RANGE = "useAiRange";
        
        /**
         * 使用AI门店方法
         */
        public static String AI_STORE_RANGE_METHOD = "aiStoreRangeMethod";

        /**
         * 所有门店使用AI
         */
        public static String ALL_STORE_RANGE = "all";

        /**
         * 自定义使用AI门店
         */
        public static String CUSTOM_STORE_RANGE = "custom";

        /**
         * 使用AI门店
         */
        public static String AI_STORE_RANGE = "aiStoreRange";

        /**
         * 配置项 AI点评不合格检查项自动发起工单策略key
         */
        public static String AUTO_SEND_PROBLEM = "aiAutoSendProblem";

        /**
         * 配置项 复核后自动发起工单value
         */
        public static String AFTER_RECHECK = "afterRecheck";

        /**
         * 配置项 自动发起工单value
         */
        public static String AUTO = "auto";

        /**
         * AI执行状态，AI已点评
         */
        public static Integer AI_STATUS_PROCESSED = 1;

        /**
         * AI执行状态，点评人已点评
         */
        public static Integer AI_STATUS_COMMENTED = 2;

        /**
         * AI分析失败
         */
        public static int AI_STATUS_FAIL = 4;

        /**
         * 检查项AI状态-分析中
         */
        public static Integer COLUMN_AI_STATUS_PROCESSING = 1;

        /**
         * 检查项AI状态-已完成
         */
        public static Integer COLUMN_AI_STATUS_COMPLETE = 2;

        /**
         * 检查项AI状态-失败
         */
        public static Integer COLUMN_AI_STATUS_FAIL = 3;

        public static String STORE_WORK_PROMPT_KEY = "store_work_ai_prompt:{0}";
    }

    public static class AI_OPEN_PLATFORM {
        /**
         * redis中各平台调用AI模型KEY
         */
        public static final String AI_MODELS_KEY = "ai_open_platform_models";

        /**
         * 巡店/店务提示词key
         */
        public static final String PATROL_AI_PROMPT_KEY = "patrol_ai_prompt";

        /**
         * 过程提示词hash key
         */
        public static final String PROCESS_PROMPT_KEY = "ai_open_platform_process_prompt";

        /**
         * 巡店/店务系统设置提示词0key
         */
        public static final String PATROL_STORE_SYSTEM_0_KEY = "patrol_store_system_0";

        /**
         * 传输图片提示词0key
         */
        public static final String PROCESS_IMAGE_TRANSFER_0_KEY = "process_image_transfer_0";

        /**
         * AI店报结束提示词0key
         */
        public static final String AI_REPORT_FINISH_0_KEY = "ai_report_finish_0";
    }

    public static class AI_MODEL_LIBRARY {
        /**
         * AI结果策略key
         */
        public static final String RESULT_STRATEGY_KEY = "aiResultStrategy";

        /**
         * 是否支持自定义提示词key
         */
        public static final String SUPPORT_CUSTOM_PROMPT_KEY = "supportCustomPrompt";
        
        /**
         * 特殊策略，结果仅PASS/FAIL/INAPPLICABLE字符
         */
        public static final String PASS_OR_FAIL_OR_INA = "passOrFailOrIna";

    }

    public static final String PLATFORM_PIC = "platform-pic";

    public static List<String> SHUZI_CODE_LIST = Arrays.asList(
            "WORK_WEAR_DETECT",
            "FACE_MASK_DETECT",
            "SMOKE_DETECT",
            "PLAY_PHONE_DETECT",
            "GARBAGE_CAN_DETECT"
    );

    public static final String FAIL_MESSAGE ="检查不合格";

    public static final String  CHE_ZHENG_CENTER = "厨政中心";

    public static final String  CHE_ZHENG_BACK_CENTER_NODE = "（后厨）";
}
