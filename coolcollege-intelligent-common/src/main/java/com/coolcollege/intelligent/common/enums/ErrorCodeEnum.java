package com.coolcollege.intelligent.common.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *  * 通用错误返回处理，后续全部将使用这个返回，请遵守以下规范。
 *  * 1.枚举名必须要大写。
 *  * 2.枚举中包含属性：code,msg,en(英文尽量写)
 *  * 3.code的命名以系统模块划分，前三位为模块划分。逐级细分。（7位数字先到先取原则，细化不浪费）
 * @author zhangchenbiao
 * @FileName: ErrorCodeEnum
 * @Description: 返回响应状态和文案枚举
 * @date 2021-07-16 14:30
 */
public enum ErrorCodeEnum {

    /**
     * 000000 未知错误
     */
    UNKNOWN(000000, "未知错误", null),
    /**
     * 2000001 token与当前登录企业不匹配
     */
    TOKEN_ERROR(2000001,"请求异常，与当前登录企业不匹配",null),
    ERROR(2000001,"{0}",null),


    /**
     * 40/50全局错误
     */
    FAIL(400000, "FAIL", null),
    NOT_SUPPORT_FILE_TYPE(400, "不支持的文件类型", null),
    PARAMS_INVALID_ERROR(400001, "参数无效！", null),
    PARAMS_REQUIRED(400002, "参数缺失！", null),
    PARAMS_VALIDATE_ERROR(400003, "参数校验失败！", null),
    REFRESH_TOKEN_INVALID(400004, "refresh token invalid", null),
    ACCESS_TOKEN_INVALID(400005, "Invalid token", null),
    MANY_REQUEST(400006, "Too Many Requests！", null),
    INTERNAL_SERVER_ERROR(50000, "服务器异常", null),
    ABNORMAL_DATA(50001, "数据异常，无法访问", null),
    SERVER_ERROR(500001, "服务器异常", null),
    NOT_SUPPORTED_DELETE(50003, "不支持删除目前", null),
    DATA_ERROR(500005, "数据异常", null),
    UPLOAD_URL_NUM_ERROR(500006, "文件最多上传9个", null),
    FILE_PARSE_FAIL(500007, "文件解析失败！", null),
    LOGIN_ERROR(500008, "登录信息异常！", null),
    NO_AUTH(500009, "暂无权限！", null),
    TIME_DEAL_ERROR(500010, "时间处理错误！", null),
    TASK_ENTERPRISE_STATUS_ERROR(500011, "企业状态异常，任务生成失败", null),
    API_ERROR(500012, "第三方API请求异常", null),
    DEAL_ERROR(500013, "请求失败，找不到对应的处理器", null),
    DATE_STYLE_ERROR(500014, "时间格式错误", null),
    DATE_CONVERT_ERROR(500015, "时间转换错误", null),
    DATE_NULL(500016, "时间为空", null),
    FILE_NULL(500017, "文件为空", null),
    FILE_HEAD_ERROR(500018, "文件表头和模板不一致", null),
    THIRD_PARTY_INTERFACE_EXCEPTION(500019, "第三方接口请求异常", null),
    LINK_DEAL_ERROR(500020, "链接处理异常", null),
    USER_NOT_EXIST(500021, "用户不存在", null),
    //云视通 有错误特殊处理
    API_TOKEN_OVERDUE_ERROR(500022, "第三方API token过期", null),
    NOT_FOUND_ENTERPRISE(500012,"当前id企业信息不存在",null),
    ENTERPRISE_FROZEN(500030,"企业已冻结",null),
    LIVE_STREAM_ERROR(500023,"获取第三方播放流异常",null),
    PTZ_ERROR(500024,"调用第三方云台控制失败",null),
    INTERNAL_USER_NOT_ALLOW_DELETE(500025, "组织架构内部人员不允许删除", null),
    SIGN_ERROR(500026, "获取签名报错", null),
    SIGN_FAIL(500026, "验签失败", null),
    EXTERNAL_USER_LOGIN_ERROR(500027, "外部用户无法登录", null),
    APP_SECRET_ERROR(500028, "appSecret未配置", null),
    PATROL_PLAN_NOT_EXIST(500029, "行事历计划不存在", null),
    NOT_OPERATE(500030, "当前状态不允许该操作", null),
    OPERATE_FAIL_PATROL_PLAN_HAS_BEEN_APPROVED(500031, "操作失败，行事历已经审批通过", null),
    LOGIN_FAIL(500032, "免登失败，请重试", null),
    SELECT_QUERY_MONTH(500033, "请选择查询月份", null),

    APPID_ERROR(500025, "appId不正确", null),
    MESSAGE_CONSUMED(500028, "消息已被消费", null),
    MESSAGE_NOT_FOUND(500029, "消息不存在", null),
    POSITION_IS_FULL(500031, "您的套餐的职位人数已满，请联系商务经理", null),
    NO_RECORDS_TO_EXPORT(500032, "当前无记录可导出", null),
    EXPORT_DATA_OUT_OF_LIMIT(500033, "导出数据不能超过{0}条，请缩小导出范围", null),
    EXPORTING(500034, "请勿连续点击，一分钟后再重试", null),
    /**
     * 前端有特殊处理
     */
    ACTIVITY_NOT_AUTH(5000000, "无权限访问活动详情", null),
    ACTIVITY_NOT_EXISTS(5000001, "活动不存在", null),
    ACTIVITY_NO_START(5000002, "活动未开始", null),
    ACTIVITY_END(5000003, "活动已结束", null),
    ACTIVITY_STOP(5000004, "活动已停止", null),


    SCHEDULE_DELETE_ERROR(500014, "定时调度器删除失败", null),
    SCHEDULE_ADD_ERROR(500015, "定时调度器添加失败", null),

    /**
     *用户模块102
     */
    SMS_CODE_EXPIRE(1021001, "验证码已过期！",null),
    SMS_CODE_ERROR(1021002, "验证码错误！",null),
    SMS_CODE_MISSING(1021003, "验证码缺失！",null),
    USER_NON_EXISTENT(1021004, "账号不存在！",null),
    ACCOUNT_NOT_EXIST(1021004, "账户或密码错误！",null),
    USER_NOT_JOIN_ENTERPRISE(1021005, "当前用户未加入任何企业",null),
    PASSWORD_MISSING(1021006, "密码不能为空！",null),
    IMPROVE_USER_INFO(1021007,"请联系管理员，完善用户信息！",null),
    PASSWORD_ERROR(1021008, "密码输入错误",null),
    PASSWORD_ERROR_MAX_COUNT(1021009, "密码错误{0}次,今日账号已锁定",null),
    USER_ACCOUNT_WAIT_AUDIT(1021010, "账号信息等待审核",null),
    LOGIN_TYPE_NOT_SUPPORT(1021012, "当前登录方式暂不支持",null),
    MOBILE_USED(1021013, "当前手机号被【{0}】占用，请确认后再操作",null),
    PARAM_MISSING(1021014, "参数不能为空",null),
    SHARE_KEY_EXPIRE(1021015, "分享链接已失效",null),
    SEND_CODE_ERROR(1021016,"发送验证码失败",null),
    USER_INFO_ERROR(1021017,"用户信息异常",null),
    USER_WAIT_AUIT(1021018,"账号审核中，请联系企业管理员",null),
    USER_FREEZE(1021019,"账号被冻结，请联系管理员",null),
    CODE_SENDED(1021020, "验证码已发送，请稍后再试",null),
    SEND_SMS_LIMIT_COUNT(1021021, "短信发送失败，今日发送短信已达上限",null),
    MOBILE_NOT_MATCH(1021022, "修改失败，手机号与当前用户不匹配",null),
    USER_NOT_JOIN_CUR_ENTERPRISE(1021023, "当前用户未加入该企业",null),
    PASSWORD_ERROR_MULTI(1021024, "密码错误{0}次,请使用验证码登录",null),
    THIRDOAFLAG_USED(1021025, "第三方标识被【{0}】占用，请确认后再操作",null),
    ADMIN_ACCOUNT_NO_OPERATE(1021026, "管理员账号请勿操作",null),
    USER_NULL(1021027, "用户信息为空",null),
    USER_MAX_NUM(1021028, "批量修改的用户数量大于200",null),
    TASK_NOT_EXIST(1021040, "该任务不存在或者该任务已被删除",null),
    TASK_QUESTION_CREATE(1021029, "工单创建中，请稍等",null),
    ENTERPRISE_NOT_EXIST(1021030, "企业不存在",null),
    TASK_CAN_NOT_DELETE(1021031, "正在生成中，暂时不能删除",null),
    TASK_CAN_NOT_UPDATE(1021032, "正在生成门店任务，暂时不能编辑",null),
    TB_QUESTION_RECORD_EXIST_COLUMN_ERROR(10210303, "巡店工单不可以删除", null),
    TASK_NOT_HANDLE(10210305, "该任务已被其他人操作",null),
    TASK_NOT_GENERATE(1021033, "任务正在处理，请稍后查看",null),
    USERNAME_MISSING(1021036, "用户名不能为空！",null),
    UPDATE_NAME_ERROR_NOT_MASTER(1021038, "非管理无法更改名称！", null),
    TASK_SCHEDULE_NOT_EXIST(1021039, "循环任务已终止，无需再次终止",null),
    TASK_QUESTION_CREATE_ERROR(1021037, "工单创建失败",null),
    TASK_OVERDUE(1021036, "任务逾期不可执行",null),
    LOGIN_CODE_NOT_NULL(1021041, "登录授权码不能为空",null),
    LOGIN_STORE_COUNT_ERROR(1021042, "您的门店数量已超出套餐上限，请联系专属服务人员",null),
    GET_AUTH_SCOPE_ERROR(1021043, "获取应用授权范围失败",null),
    SYNC_DEPT_ERROR(1021044, "部门同步失败，部门id:{0}",null),
    SYNC_REGION_NOT_EXIST(1021045, "同步区域不存在",null),
    SUB_REGION_NULL(1021046, "第三方获取子节点为空",null),
    SAME_NAME_REGION_NULL(1021047, "区域下不存在{0}节点",null),
    STORE_SYNCING(1021048, "门店正在同步中，请稍后再试",null),
    SUB_TASK_NOT_EXIST(1021049, "子任务不存在",null),
    TASK_IS_STOP(1021051, "任务已停止",null),

    EID_NOT_EXIST(1021050, "企业id不能为空",null),
    CORP_NOT_EXIST(1021050, "corpId不能为空",null),
    USER_PERSONNEL_STATUS_EXIST(1021060, "该人事状态已存在",null),
    NEW_STORE_TYPE_EXIST(1021070, "该新店类型已存在",null),
    NEW_STORE_TODAY_HAS_ONGOING_RECORD(1021071, "该新店今日有未完成拜访，请查看拜访记录", null),
    NEW_STORE_VISIT_RECORD_NOT_FOUND(1021072, "拜访记录不存在", null),
    NEW_STORE_VISIT_TABLE_NOT_FOUND(1021073, "拜访表不存在", null),
    NEW_STORE_NOT_FOUND(1021074, "新店不存在", null),
    NEW_STORE_ERROR_DIRECT_USER(1021075, "您不是新店负责人，无法完成该操作", null),
    USER_GROUP_NAME_EXIST(1021076, "用户分组名称已存在", null),
    USER_GROUP_NOT_EXIST(1021077, "用户分组不存在", null),
    ORIGINAL_PASSWORD_ERROR(1021078, "原密码输入错误，请重新输入", null),
    NONSUPPORT_MOBILE(1021079, "暂不支持的手机号格式", null),
    MOBILE_NOT_MATCH2(1021080, "手机号与当前用户不匹配",null),
    ROLE_ALREADY_EXISTS(1021081, "职位名称已经存在",null),
    LOGIN_AUTH_ERROR(1021082, "授权失败", null),
    APPLET_MOBILE_AUTH_ERROR(1021083, "小程序手机号授权失败", null),
    APPLET_USER_INFO_ERROR(1021084, "小程序用户信息获取失败", null),
    APPLET_TOKEN_EXPIRE(1021085, "小程序accessToken过期", null),
    ASKBOT_TOKEN_ERROR(1021085, "果然单点登录token获取失败", null),


    /**
     * 区域模块 103
     */
    REGION_NAME_REPEAT(1031001, "同一节点下的区域名称不允许重复",null),
    REGION_USER_IS_NOT_NULL(1031002,"请删除此部门下的人员，再删除此部门",null),
    REGION_TO_STORE_FAIL(1031003,"不符合区域转门店要求",null),
    REGION_IS_NOT_EXIST(1031004,"不存在当前区域",null),
    REGION_NOT_EXIST(1031005, "区域不存在",null),
    META_COLUMN_NOT_EXIST(1031011, "检查项不存在",null),
    REGIOIN_IS_NOT_NULL(1031012, "区域不能为null，请选择区域",null),
    LIMIT_STORE_COUNT(1031013, "门店数量超出限制",null),
    REGION_NULL(1031014,"节点为空",null),
    NODE_IS_NOT_STORE(1031015,"当前节点不是门店",null),
    STORE_ID_IS_NULL(1031016,"门店id为空",null),
    STORE_NO_USER(1031017,"门店下没人",null),
    REGION_IS_NULL(1031018,"所属部门不能为空",null),
    EXTERNAL_USER_REGION_IS_ERROR(1031019,"外部用户应该在外部组织中",null),
    GET_DEPT_ERROR(1031020,"获取部门失败",null),

    /**
     * 业绩 104
     */
    ACH_PARAM_REPORT_INVALID(1041001,"上报业绩不能全部为空！",null),

    ACH_FORMWORK_NAME_REPEAT(1041002,"模板名称不能重复，请重新输入",null),
    ACH_TYPE_NAME_REPEAT(1041003,"类型名称不能重复，请重新输入",null),
    ACH_PARAM_FORMWORK_ADD_ERROR(1041004,"创建模板业绩类型不能为空！",null),
    ACH_DATE_BEGIN_NOT_NULL(1041005,"开始时间不能为空",null),
    ACH_TYPE_NAME_LENGTH(1041006,"类型名称不能超过64个字符，请重新输入",null),

    ACH_DATE_END_NOT_NULL(1041007,"结束时间不能为空",null),
    ACH_DATE_MORE_THAN_DAY(1041009,"时间间隔不能超过{0}天",null),
    ACH_FORMWORK_ID_NOT_NULL(1041011,"模板Id不能为空",null),
    ACH_STATISTICS_REGION_MIN(1041012, "区域不能为空",null),
    ACH_STATISTICS_REGION_MAX(1041013, "区域最多选择10个",null),
    ACH_STATISTICS_STORE_MAX(1041014, "拥有门店权限太多，最多500个",null),
    ACH_FORMWORK_TYPE_MAX(1041016, "模板最多能添加{0}个业绩类型",null),
    ACH_TARGET_STORE_NOT_EXIST(1041018, "门店不存在！",null),
    ACH_NO_DATA_EXPORT(1041019, "没有可导出的数据",null),
    NOT_SUPPORT_MODIFY(1041020,"当前日期不支持修改业绩目标", null),
    GOODS_CONFIG_ERROR(1041021,"商品信息配置有误", null),

    /**
     * 校验模块 106
     */
    VALIDATION_RULES_1060001(1060001,"请选择需要查询的区域",null),
    VALIDATION_RULES_1060002(1060002,"所选区域不能超过20个",null),
    VALIDATION_RULES_1060003(1060003,"所选区域不能为空",null),
    PERSON_LIMIT(1061004,"目前系统支持添加人员+职位最多{0}个",null),
    EMPTY_REPORT_PARAM(1061010,"必须选择一个用户或者职位",null),
    TIME_NOT_ALLOWED(1061011,"不能修改当天前的状态",null),
    TIME_OUT_OF_MAX(1061012,"超出最大选择时间范围",null),
    OUT_OF_MAX_TABLE_NUM(1061013,"最大选择检查表数为20",null),
    OUT_OF_MAX_PAGE_SIZE(1061014,"页面数量最大为20",null),
    INVALID_ENTERPRISE_PACKAGE(1061015,"无效的企业套餐",null),
    PACKAGES_IN_USE(1061016,"使用中的套餐无法删除",null),
    MODULE_IN_USE(1061017,"使用中的业务模块无法删除",null),
    MODULE_NAME_IN_USE(1061018,"业务模块名称重复",null),
    PACKAGES_NAME_IN_USE(1061019,"套餐名称重复",null),
    PARTROL_STORE_MODE(1061020,"请选择巡店模式",null),

    /**
     * 检查表 107
     */
    COLUMN_LIST_NULL(1071001,"检查项列表不能为空",null),
    CHECKTABLE_IS_NULL(1071002,"目标检查表不存在",null),
    TOP_COUNT_LIMIT(1071003,"检查项置顶数量限制10个",null),
    TABLE_REPEAT(1071004,"检查表名称不能重复",null),
    PATROL_STORE_RECORD_IS_NOT_NULL(1071005,"巡店记录不能为null",null),
    WEIGHT_PERCENT_TABLE(1071006,"权重表所有项权重和不为100",null),
    CHECK_TABLE_MOVE_SORT_FAIL(1071007,"检查表移动排序失败",null),
    CHECK_TABLE_COLUMN_COUNT(1071008,"表内检查项至少有一个设置为非冻结状态",null),
    PATROL_STORE_RECORD_IS_NULL(1071010,"巡店记录为空，请巡店之后再导出",null),
    TABLE_NO_ALL_AI_COLUMN(1071011, "AI检查表需全部为AI检查项", null),

    /**
     * 检查项108
     */
    COLUMN_NAME_NULL(1081001,"检查项名称不能为空",null),
    COLUMN_NAME_MAX_LENGTH(1081002,"检查项名称长度不能超过{0}",null),
    COLUMN_CATEGORY_NAME_REPETITION(1081003,"检查项分类名称重复，请重新输入",null),
    COLUMN_CATEGORY_NAME_TOO_LONG(1081004,"分类名称超过最大限制",null),
    COLUMN_CATEGORY_USING(1081005,"分类被关联，不能删除",null),
    COLUMN_CATEGORY_DEFAULT_NOT_UPDATE(1081006,"默认分类不能编辑和删除",null),
    COLUMN_STATUS_NOT_CORRECT(1081007,"状态不正确",null),
    COLUMN_CATEGORY_COUNT_MAX_LIMIT(1081008,"分类数量超过最大限制",null),
    COLUMN_TYPE_ERROR(1081009,"检查项属性不正确",null),
    COLUMN_RESULT_ISNULL(1081010,"结果项不能为空",null),
    COLUMN_RESULT_LIMIT(1081011,"结果项最少1条，最多10条",null),
    COLUMN_REPEAT(1081012,"检查项重复",null),
    DEF_COLUMN_NAME_MAX_LENGTH(1081013,"组件标题长度不能超过{0}",null),

    /**
     * 任务创建 109
     */
    UNIFY_TASK_STORE_SCOPE_NOT_NULL(1091001,"门店范围不能为空",null),
    UNIFY_TASK_STORE_SCOPE_NOT_AUTH(1091004,"没有门店权限",null),
    UNIFY_TASK_TASKCYCLE_RUNDATE__VALID(1091005,"循环周期需要在有效期内",null),

    PATROL_STORE_RECORD_CREATING(1091006, "巡店记录正在创建中，请稍后重试...", null),

    PATROL_STORE_PLAN_DELETED(1091007, "该任务已删除", null),
    STORE_OPEN_TASK_NOT_ABLE(1091008, "该任务已被停用",null),
    STORE_OPEN_TASK_ENABLE(1091009, "该任务已被启用",null),
    /**
     * 首页模板
     */
    HOME_TEMPLATE_NAME_IS_NOT_NULL(1101001, "模板名称不能为空", null),
    HOME_TEMPLATE_IS_NULL(1101002, "模板名称为空", null),
    HOME_TEMPLATE_ROLE_MAPPING_IS_NOT_NULL(1101003, "模板与角色绑定，暂时不能删除，请修改模板应用范围", null),
    TEMPLATE_NAME_LIMIT_LENGTH(1101004, "模板名称长度不能超过30", null),
    TEMPLATE_DESCRIPTION_LIMIT_LENGTH(1101005, "模板描述长度不能超过128", null),


    /**
     * 30百丽，森宇
     */
    BAI_LI_API_ERROR(3000004, "访问百丽接口错误", null),
    SEN_YU_API_ERROR(3000005, "访问森宇接口错误", null),

    /**
     * 301 阿里云
     */
    VIDEO_CLIENT(301001, "缺少参数：", null),
    VIDEO_SERVER(301002, "阿里服务异常：", null),
    VIDEO_RETURN(301003, "", null),

    // 302 证照管理 hz-coolstore-license

    /**
     * 401类通用返回码
     * 主要定义api授权验证错误返回码
     */
    NO_PERMISSION(401003, "无管理员权限！", null),
    AES_DECRYPT_FAIL(401004, "解密失败！", null),
    THIRD_TOKEN_EXPIRE(401005, "thirdToken过期！", null),


    /**
     * 设备 301
     */

    B1_INSTANCE_GROUP_CREATE_ERROR(3010001,"创建打卡组失败！",null),
    HIK_CLOUD_ACCESS_TOKEN_GET_ERROR(3010002,"获取海康云眸access_token失败，请联系相关人员配置！！",null),
    DEVICE_GET_ERROR(3010003,"远程获取设备异常",null),
    DEVICE_NOT_FOUND(3010004,"设备不存在",null),
    CHANNEL_NOT_FOUND(3010005,"设备通道不存在",null),
    YUN_TYPE_CONFIG_NOT_FOUND(3010006,"企业未配置该厂商的授权",null),
    CHANNEL_NOT_ONLINE(3010007, "通道不在线", null),
    DEVICE_NOT_ABILITY(3010008, "设备能力不支持", null),
    DEVICE_NOT_ONLINE(3010009, "设备不在线", null),
    VIDEO_RECORDING(30100010, "视频录制失败", null),
    VIDEO_UPLOADING(30100011, "视频文件正在生成中，请稍后", null),
    VIDEO_UPLOAD_FAIL(30100012, "抱歉，视频文件生成失败，请重新选择视频开始时间重新下载", null),
    DEVICE_COUNT_LIMIT(30100013, "您的企业无剩余可使用的设备数量，请联系客户经理添加设备", null),
    DEVICE_SYNC_ERROR(30100014, "设备同步失败", null),
    DEVICE_NOT_VIDEO(30100015, "该时间段没有录像", null),
    TIME_INTERVAL_LONG(30100016, "时间间隔超24小时", null),
    LATER_SYNC(30100017, "正在同步，请稍后同步", null),
    OPEN_API_DEVICE_ERROR(30100018, "{0}", null),
    DEVICE_TYPE_NOT_SUPPORT(30100019, "当前设备不支持该请求", null),
    ENTERPRISE_VIDEO_SETTING_FIRST_NODE(30100020, "私有云眸账号不同企业共用时,请先配置云眸一级菜单", null),
    AUTHENTICATION_ERROR(30100021, "取流认证接口调用失败", null),
    HIL_DEVICE_NOT_FOUND(3010022,"云眸系统不存在该设备",null),
    WDZ_ACCESS_TOKEN_GET_ERROR(3010023,"获取万店掌access_token失败，请联系相关人员配置！！",null),
    JFY_USERNAME_NOT_EXIST(3010024,"设备用户名不存在",null),
    NOT_EXISTS_LOCAL_PAST_VIDEO(3010025, "不存在本地录像", null),
    NOT_EXISTS_PLATFORM_ACCOUNT(3010026, "不存在平台账号", null),
    JFY_LOGIN_ERROR(3010027, "设备登录失败", null),
    SELECT_CHANNEL(3010028, "选择对应通道", null),
    DEVICE_AUTHED(3010029, "该设备已经授权，无需再次授权", null),
    NOT_AUTH_RECORD(3010030, "当前设备暂无授权信息", null),
    AUTH_EXPIRE(3010031, "当前设备授权信息已过期，需要重新授权", null),
    GET_LIVE_URL_ERROR(3010032, "远程获取直播流异常", null),
    GET_ONLINE_ACCESS_TOKEN_ERROR(3010033, "获取线上accessToken失败", null),


    /**
     * 设备模块
     */
    //设备场景
    STORE_SCENE_ADD_NAME_EXISTENT(2011001,"场景名称已存在，新增失败",null),
    STORE_SCENE_UPDATE_NAME_EXISTENT(2011008,"场景名称已存在，更新失败",null),
    STORE_SCENE_ADD_ID_NOTNULL(2011009,"更新设备场景Id不能为空",null),

    DEVICE_EXISTENT(2011010,"设备已经存在，请勿重复添加！",null),
    DEVICE_NOT_EXPORT(2011011,"没有设备要导出",null),
    HIKCLOUD_LIMIT(2011012,"触发云眸限流，请稍后重试",null),


    /**
     * 通用模块
     */
    COM_NOT_MORE_THAN_7_DAYS(3001002,"查询时间不能超过7天",null),
    COM_CANNOT_START_GREATER_THAN_END(3001006,"开始时间不能大于结束时间",null),

    /**
     * 客流分析错误码400****
     */
    PF_CANT_SCHEDULE(4001001,"调度器开启失败",null),
    PF_CONFIG_LING_NOT_EXIST(4001002,"客流配置的基准线未设置",null),
    PF_CONFIG_STATUS_NOTNULL(4001003,"客流配置开启状态不能为空",null),

    /**
     * 600 调用外部服务器错误返回码
     */
    WORK_FLOW_ERROR(600002,"",null),
    WORK_FLOW_TEMPLATE_ERROR(600100, "模板创建失败", null),
    WORK_FLOW_HISTORY_ERROR(600101, "查询历史失败", null),
    WORK_FLOW_CHECK_ERROR(600102, "节点提交校验失败", null),
    WORK_FLOW_QUERY_ERROR(600103, "查询失败", null),
    ENTERPRISE_ERROR(600104, "企业信息存在问题", null),
    USER_ERROR(600105, "用户信息存在问题", null),
    GET_COURSE_ERROR(600106, "获取课程信息错误", null),


    /**
     * 71萤石云错误模块 特殊模块，用于处理外部错误提示的转换
     */
    YS_DEVICE_7110002(7110002, "获取授权过期",null),
    YS_DEVICE_7110031(7110031, "子账户或萤石用户没有权限",null),
    YS_DEVICE_7120001(7120001, "设备子通道号不存在，没有注册到萤石云平台",null),
    YS_DEVICE_7120002(7120002, "设备不存在，没有注册到萤石云平台",null),
    YS_DEVICE_7120006(7120006, "网络异常",null),
    YS_DEVICE_7120007(7120007, "设备不在线",null),
    YS_DEVICE_7120008(7120008, "设备响应超时，请检测设备网络或重试",null),
    YS_DEVICE_7120015(7120015, "设备不支持该功能",null),
    YS_DEVICE_7120017(7120017, "当前设备正在格式化",null),
    YS_DEVICE_7120019(7120019, "设备没有本地存储和不支持云存储服务",null),
    YS_DEVICE_7149999(7149999, "萤石云数据异常",null),
    YS_DEVICE_7150000(7150000, "萤石云服务器异常",null),
    YS_DEVICE_7160000(7160000, "设备不支持云台控制",null),
    YS_DEVICE_7160001(7160001, "用户无云台控制权限",null),
    YS_DEVICE_7160002(7160002, "设备云台旋转达到上限位",null),
    YS_DEVICE_7160003(7160003, "设备云台旋转达到下限位",null),
    YS_DEVICE_7160004(7160004, "设备云台旋转达到左限位",null),
    YS_DEVICE_7160005(7160005, "设备云台旋转达到右限位",null),
    YS_DEVICE_7160006(7160006, "云台当前操作失败",null),
    YS_DEVICE_7160007(7160007, "预置点个数超过最大值",null),
    YS_DEVICE_7160009(7160009, "正在调用预置点",null),
    YS_DEVICE_7160010(7160010, "该预置点已经是当前位置",null),
    YS_DEVICE_7160011(7160011, "预置点不存在",null),
    YS_DEVICE_7160012(7160012, "未知错误",null),
    YS_DEVICE_7160019(7160019, "加密已开启,酷店掌暂不支持视频解密，请在萤石云关闭加密",null),
    YS_DEVICE_7160020(7160020, "不支持该命令", null),
    YS_DEVICE_7120018(7120018, "该用户不拥有该设备", null),

    //非萤石云暴露的错误，或者是不在转换的错误之中，返回的错误码。
    YS_DEVICE_7000000(7000000, "调用萤石云未知错误",null),
    YS_DEVICE_7000001(7000001, "视频文件生成失败：{0}", null),
    YS_DEVICE_7000003(7000003, "通道号最大256", null),
    YS_DEVICE_7000004(7000004, "该设备不是NVR", null),
    YS_DEVICE_7000005(7000005, "通道号已存在", null),
    YS_DEVICE_7000006(7000006, "通道名称已存在", null),
    YS_DEVICE_7000007(7000007, "下载失败：{0}", null),



    /**
     * 宇视错误码转换 72
     */
    YUS_DEVICE_7201000(7201000, "宇视云服务器内部错误!",null),
    YUS_DEVICE_7201001(7201001, "宇视云授权失败!",null),
    YUS_DEVICE_7201002(7201002, "宇视云授权过期!",null),
    YUS_DEVICE_7201003(7201003, "参数不正确!",null),
    YUS_DEVICE_7201009(7201009, "资源不存在!",null),
    YUS_DEVICE_7201010(7201010, "结束时间不能早于当前时间!",null),
    YUS_DEVICE_7201011(7201011, "结束时间不能早于开始时间!",null),
    YUS_DEVICE_7202009(7202009, "设备不属于当前用户!",null),
    YUS_DEVICE_7202601(7202601, "设备不支持云存储功能!",null),
    YUS_DEVICE_7202602(7202602, "设备未开通云存储功能!",null),
    YUS_DEVICE_7203400(7203400, "套餐不存在!",null),
    YUS_DEVICE_7203401(7203401, "套餐已被使用!",null),
    //非宇视云暴露的错误，或者是不在转换的错误之中，返回的错误码。
    YUS_DEVICE_7300000(7300000, "调用宇视云未知错误!",null),

    /**
     * 74乐橙错误
     */
    LECHENG_DEVICE_7400000(7400000, "调用乐橙云网络错误!",null),
    //原生乐橙错误
    LECHENG_DEVICE_7400001(7400001, "【{0}】",null),
    LECHENG_DEVICE_7400002(7400002, "获取授权失败",null),

    /**
     * 75杰峰云(雄迈)错误
     */
    // 设备错误码
    JFY_DEVICE_7500000(7500000, "调用杰峰云未知错误!",null),
    JFY_DEVICE_7500001(7500001, "未知错误",null),


    /**
     * 钉钉同步 企微同步
     */
    DING_TALK_SYNCING(1100001,"有节点正在同步中，请稍后再试",null),
    DING_TALK_LIMIT(1100002,"全量同步时,一天内只能同步一次",null),
    DING_TALK_NOT_OPEN(1100003,"未开启同步",null),


    /**
     * 钉钉-企微同步错误码
     */
    QW_SYNC_NO_DKF_ACCESS_TOKEN(3000001, "企微未授权代开发，无法进行同步", null),

    /**
     * 企微相关
     */
    MISSING_SELECTED_TICKET(3000002, "ticket列表为空",null),
    AUTHORIZATION_TYPE_EXCEPTION(3000003, "当前企业不是成员授权模式",null),
    TICKET_TOO_LONG(3000010, "ticket列表长度不能超过10条",null),

    DING_SERVICE_EXCEPTION(3000020, "钉钉服务调用异常！",null),
    QW_SERVICE_EXCEPTION(3000021, "企业微信服务调用异常！",null),
    QW_SERVICE_DEPT_SYNC_EXCEPTION(3000022, "企微同步部门用户失败！",null),

    DING_USERINFO_BYTOKEN_EXCEPTION(3000023, "获取钉钉用户授权信息服务调用异常！",null),
    /**
     * 集成酷学院模块，业培一体
     */
    COOL_STORE_OPEN_TRAINING(8000001, "请先开通业培一体的套餐后再尝试使用！",null),
    COOL_STORE_TYPE_ERROR(8000002, "请在酷店掌应用或者独立app应用中请求该数据！",null),
    COOL_STORE_USER_ID_ERROR(8000004, "门店端用户的userId有误，请核实！",null),
    COOL_COLLEGE_TODO_LIST_ERROR(8000003, "培训待办数据返回异常！",null),
    COOL_COLLEGE_GET_OPEN_TOKEN_ERROR(8000005, "获取开通的token异常！",null),
    COOL_COLLEGE_GET_ENTERPRISE_TOKEN_ERROR(8000006, "获取企业推送数据的token异常！",null),
    COOL_STORE_ENTERPRISE_ID_NOT_NULL(8000007, "企业id不能为空，请核实后重试！",null),
    COOL_COLLEGE_ADMIN_LOGIN_FAIL(8000008, "超登接口请求异常！",null),

    VOD_ERROR(9000001, "企业id不能为空，请核实后重试！",null),

    /**
     * 门店通套餐升级提醒：管理员 900
     */
    OP_9000000(9000000, "请先开通门店通！",null),

    OP_9000001(9000001, "最多新增{0}个文档，请升级套餐",null),
    OP_9000002(9000002, "最多发起{0}个工单，请升级套餐",null),
    OP_9000003(9000003, "最多上传{0}张图片，请升级套餐",null),
    OP_9000004(9000004, "最多新增{0}张检查表，请升级套餐",null),
    /**
     * 门店通套餐升级提醒：非管理员 901
     */
    OP_9010001(9010001, "最多新增{0}个文档，请联系管理员升级",null),
    OP_9010002(90100002, "最多发起{0}个工单，请联系管理员升级",null),
    OP_9010003(9010003, "最多上传{0}张图片，请联系管理员升级",null),
    OP_9010004(9010004, "最多新增{0}张检查表，请联系管理员升级",null),

    OP_9020001(9020001, "当月巡店数（{0}家门店）已超出限制",null),
    OP_9020002(9020002, "获取套餐信息失败，请稍后重试",null),

    /**
     * openAPI
     * code：xxxyzzz xxx表示所属应用 y表示业务  zzz表示具体code
     * xxx---100表示主应用
     * y 0-共用异常 1-表示巡店  2-陈列  3-工单   4-组织架构
     */

    MISSING_BEGIN_TIME(1000001,"开始时间参数不存在",null),
    MISSING_END_TIME(1000002,"结束时间参数不存在",null),
    LIMIT_QUERY_TIME_LENGTH(1000003,"最多可以查询30天数据",null),
    REQUIRED_PARAM_MISSING(1000004,"必填参数为null",null),
    PAGE_SIZE_LIMIT(1000005,"每页数据不超过100",null),
    LIMIT_QUERY_TIME_LENGTH_100(1000003,"最多可以查询100天数据",null),
    END_TIME_BEFORE_START_TIME(1000006, "结束时间不能早于开始时间", null),

    //开发平台 陈列异常 1002开头
    DISPLAY_RECORD_NOT_EXIST(1002001,"陈列记录不存在",null),
    //开放平台 工单异常 1003开头
    QUESTION_RECORD_NOT_EXIST(1003002,"工单记录不存在",null),

    STORE_NAME_LENGTH_LIMIT(1004004,"门店名称长度不能超过60个字符",null),
    REGION_NOT_FIND(1004001,"区域不存在",null),
    STORE_NOT_FIND(1004005,"门店不存在",null),
    UPDATE_ONLY_REGION(1004002,"修改的不是区域！",null),
    PARENT_REGION_NOT_FIND(1004003,"父区域不存在！",null),
    WORK_HANDOVER_NOT_FIND(1004009,"交接工作不存在！",null),
    WORK_HANDOVER_NOT_AGAIN(1004011,"只有交接失败，才能重新交接！",null),
    USER_ID_SAME(1004010,"移交人和接交人不能相同！",null),
    QUESTION_USER_NOT_NULL(1004012,"处理人不能为空！",null),

    /**
     * 店务
     */
    STORE_WORK_RECORD_IS_NOT_EXIST(1005001,"店务记录不存在！",null),
    STORE_WORK_RECORD_TABLE_IS_NOT_EXIST(1005002,"店务表数据不存在！",null),
    STORE_WORK_DATA_TABLE_COLUMN_IS_NOT_EXIST(1005003,"店务记录数据项不存在！",null),
    STORE_NO_PERSON(1005004,"门店没有人员，不分解任务！",null),
    OVERDUE_CONTINUE(1005005,"逾期不能执行，请确认",null),
    ROLL_BACK_FLAG(1005006,"店务记录没有对应的表(逾期过滤掉了)，数据回滚中",null),
    CHECK_TABLE_NOT_HANDLE(1005007,"检查表已点评，不能再执行",null),

    STORE_WORK_NOT_EXIST(1005008, "店务不存在", null),
    STORE_WORK_DELETE_AUTH(1005009, "非创建人或管理员，无删除权限", null),
    METATABLE_TEMPLATE_NOT_EXIST(1005010, "检查表模板不存在", null),
    NO_NEED_REMIND_HANDLE_USER(1005011, "该店务所选日期没有需要催办的执行人，请检查！", null),
    COMMENT(1005012, "该检查表已点评，不能再次点评", null),
    STORE_WORK_END_CHECK(1005013, "该店务已结束不可修改", null),
    STORE_WORK_COMPLETE(1005014, "该AI检查表已提交，不允许修改！", null),
    STORE_WORK_AI_PROCESSING(1005015, "该检查表正在进行AI分析", null),


    SUPERVISION_TASK_PARENT_NOT_EXIST(1006001,"督导助手父任务定义表不存在",null),
    SUPERVISION_TASK_NOT_EXIST(1006002,"督导任务不存在",null),
    SUPERVISION_TASK_HANDLE_SELF(1006003,"只能处理自己的任务",null),
    PAGE_SIZE_MAX(1006004,"一次最多查询100条数据",null),
    USER_DIRECT_SUPERIOR_NOT_DELETE(1006005,"自动同步的上级不能修改",null),
    SUPERVISION_USER_IS_NOT_NULL(1006006,"用户不能为空",null),
    SUPERVISION_STORE_UNION_IS_NULL(1006007,"门店交集为空",null),
    SUPERVISION_STORE_COUNT_LIMIT(1006008,"最多200家门店",null),
    SUPERVISION_TASK_TRANSFER_WAIT_SELF(1006009,"只能转交待处理的任务",null),
    SUPERVISION_TASK_TRANSFER_SELF(1006010,"只能转交自己的任务",null),
    NOT_APPROVE_USER(1006011,"你没有该任务的审批权限",null),
    SUPERVISION_TASK_TRANSFER_HAS(1006012,"选择的人已有该任务，无法转交",null),
    SUPERVISION_TASK_CANCEL_OR_DELETED(1006013,"任务取消或者删除 不能进行执行与审批操作",null),
    SUPERVISION_TASK_CURRENT_NODE_ERROR(1006014,"不是执行节点，不能执行改任务",null),
    SUPERVISION_TASK_TRANSFER_NOT_SELF(1006015,"不能转交该任务给自己",null),
    TASK_RESOLVE_ERROR(1006016, "当前任务正在分解，请稍后再试", null),
    TASK_RESOLVE_REFRESH_ERROR(1006017, "任务更新中，请勿重复操作", null),


    STORE_CODE_ERROR(600001, "storeCode错误!", null),



    STORE_SIGN_IN_ERROR(1007101,"已经完成签到，无法再次签到",null),
    STORE_SIGN_IN_BEFORE(1007102,"请先进行签到",null),
    STORE_SIGN_OUT_ERROR(1007103,"已经签退完成，无法再次签退",null),
    STORE_SIGN_NOT_EXIST(1007104,"门店报告不存在",null),
    STORE_SIGN_OUT_TASK_ERROR(1007105,"该门店当天无任务，无法签退",null),
    STORE_SIGN_OUT_TASK_COMPLETE_ERROR(1007106,"至少完成一条任务,否则无法签退",null),
    STORE_SIGN_IN_NOT_DAY_ERROR(1007107,"只能进行当天签到",null),
    STORE_APPROVE_NOT(1007108,"任务没有完成，无法进行复审",null),
    STORE_APPROVE_PLAN(1007110,"该门店已存在，请勿重复添加！",null),

    STORE_GROUP_NOT_EXIST(1007201,"门店分组不存在",null),

    SAFETYCHECK_SIGNATUREUSER_NOSELECT(1008001, "请选择签字人员", null),
    SAFETYCHECK_SIGNATUREUSER_NOAUTH(1008002, "您选择的签字人员，没有门店权限，请重新选择", null),
    SAFETYCHECK_FLOW_NOTREACH(1008003, "流程未流转到当前节点，无法操作", null),
    SAFETYCHECK_FLOW_NO_HANDLEAUTH(1008004, "您没有处理该门店稽核流程的权限", null),

    SOP_CHECK_LIST_IS_REPEATED(1008005,"SOP检查表分组重复,请重新输入",null),
    NO_INVESTMENT_MANAGER(1008006,"无招商经理角色",null),
    WARNING_TEXT(1008007,"含有敏感词,请重新填写",null),

    ENCRYPT_ERROR(1009000, "加密失败", null),
    STORE_TAGS_NOT_EXISTS(1008007, "门店标签{0}不存在", null),
    STORE_NOT_EXISTS(1008008,"门店{0}不存在",null),
    USER_NOT_EXISTS(1008009, "用户{0}不存在", null),

    BRAND_NAME_EXIST(1009000, "品牌名称已存在", null),
    BRAND_CODE_EXIST(1009001, "品牌code已存在", null),
    BRAND_INIT_COMPLETED(1009002, "品牌{0}已完成初始化，无法删除", null),

    AI_API_ERROR(1010000, "AI算法调用失败", null),
    AI_PICTURE_EMPTY(1010001, "AI分析图片为空", null),
    NOT_ALL_AI_CHECK(1010002, "检查项需全为AI检查项", null),
    AI_IS_ANALYZING(1010003, "AI正在分析中，请稍后", null),
    AI_RESULT_MATCH_FAIL(1010004, "AI结果匹配失败", null),
    AI_RESULT_JSON_ERROR(1010005, "AI结果JSON格式错误", null),
    AI_RESULT_SCORE_EMPTY(1010006, "AI结果分数为空", null),
    AI_PLATFORM_NOT_EXIST(1010007, "AI平台不存在", null),
    AI_NO_SUPPORT_GEN_STA_DESC(1010008, "该模型不支持生成检查标准", null),
    AI_MODEL_NOT_EXIST(1010009, "AI模型不存在", null),

    NOT_AUTH(1100001, "暂未授权", null),
    SIGN_CHECK_ERROR(1100002, "签名校验错误", null),
    DEVICE_NOT_SUPPORT_CAPTURE(1100003, "设备不支持抓拍", null);



    protected static final Map<Integer, ErrorCodeEnum> map = Arrays.stream(values()).collect(
            Collectors.toMap(ErrorCodeEnum::getCode, Function.identity(), (a, b)->a));
    private int code;

    private String message;
    private String en;

    ErrorCodeEnum(int code, String message, String en) {
        this.code = code;
        this.message = message;
        this.en=en;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorCodeEnum getByCode(Integer code) {
        return map.get(code);
    }


}
