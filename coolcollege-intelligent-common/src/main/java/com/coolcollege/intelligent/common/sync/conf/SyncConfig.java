package com.coolcollege.intelligent.common.sync.conf;

import com.coolcollege.intelligent.common.constant.Constants;
import jodd.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public class SyncConfig {

    public static final String USER_DETAIL_API = "https://oapi.dingtalk.com/user/get?access_token=%s&userid=%s";

    public static final String DEPT_DETAIL_API = "https://oapi.dingtalk.com/department/get?access_token=%s&id=%d";

    public static final String DEPT_SUB_LIST_API = "https://oapi.dingtalk.com/topapi/v2/department/listsub";

    public static final String DEPT_SUB_DETAIL_API = "https://oapi.dingtalk.com/topapi/v2/department/get";

    /**
     * 获取用户详情请求链接
     */
    public static final String USER_SUB_DETAIL_API = "https://oapi.dingtalk.com/topapi/v2/user/get";

    public static final String DEPT_USER_LIST_API = "https://oapi.dingtalk.com/topapi/user/listid";


    public static final String AUTH_SCOPES_API = "https://oapi.dingtalk.com/auth/scopes";

    public static final String DEPT_SUB_ID = "https://oapi.dingtalk.com/topapi/v2/department/listsubid";

    /**
     * 根部门id
     */
    public static final String ROOT_DEPT_ID = "1";

    public static final Long ROOT_REGION_ID = 1L;

    /**
     * 未分组区域名称
     */
    public static final String UNGROUPED_DEPT_NAME = "默认分组";
    /**
     * 未分组区域id
     */
    public static final Long UNGROUPED_DEPT_ID = -2L;
    /**
     * 根部门id
     */
    public static final String ROOT_DEPT_ID_STR = "1";

    /**
     * 删除区域顶级id
     */
    public static final String DELETE_DEPT_ID = "-1";

    /**
     * 遍历最高层级
     */
    public static final int DEPT_MAX_LEVEL = 20;

    /**
     * 区域同步最大数量
     */
    public static final Long MAX_REGION_SIZE = 200000L;

    /**
     * 超级管理员用户id
     */
    public static final Long SUPER_USER_ID = 10L;

    public static final int DEFAULT_BATCH_SIZE = 100;

    public static final int DEFAULT_BATCH_MAX_SIZE = 500;

    /**
     * enterprise id在redis中存放的key
     */
    public static final String ENTERPRISE_CONFIG_ID_KEY = "enterprise_id";

    /**
     * 钉钉接口调用成功返回的状态码
     */
    public static final int DING_NORMAL_CODE = 0;

    /**
     * 企业员工不在授权范围
     */
    public static final int DING_USER_NOT_AUTHED = 50002;

    /**
     * 企业部门不在授权范围
     */
    public static final int DING_DEPT_NOT_AUTHED = 50004;

    /**
     * 没有调用该接口的权限
     */
    public static final int DING_DEPT_NOT_AUTHORITY = 60011;

    /**
     * 无效token
     */
    public static final int DING_ACCESS_TOKEN_INVALID = 40014;

    /**
     * 当前所有钉钉应用调用该接口次数过多，超出了该接口承受的最大qps，请求被暂时限制了，建议错开整点时刻调用该接口
     */
    public static final int API_EXCEED_LIMIT = 90002;

    /**
     * 系统繁忙
     */
    public static final int DING_SYS_ERROR = -1;

    /**
     * 鉴权异常
     */
    public static final int DING_AUTH_ERROR = 88;

    /**
     * 钉钉远程服务超时
     */
    public static final int DING_TIME_OUT = 15;

    /**
     * 默认的密码
     */
    public static final String DEFAULT_PWD = "111111";
    public static final int PWD_TYPE = 0;

    public static final String ROLE_ADMIN = "主管理员";
    public static final String ROLE_EMPLOYEE = "普通员工";
    public static final String ROLE_DEPTLEADER = "部门负责人";

    public static final String ROLE_SUB_ADMIN = "子管理员";
    public static final String ROLE_LEADER = "负责人";


    /**
     * 同步进度失效时间
     */
    public static final Integer PROCESS_EXPIRE = 7200;

    /**
     * 数字1
     */
    public static final Integer ONE = 1;

    /**
     * 数字0
     */
    public static final Integer ZERO = 0;

    /**
     * 数字2
     */
    public static final Integer TWO = 2;

    /**
     * 数字3
     */
    public static final Integer THREE = 3;

    /**
     * 插入标识
     */
    public static final String INSERT = "insert";
    /**
     * 删除标识
     */
    public static final String DELETE = "delete";

    // 门店同步规则配置 {"code":"","value":"关键字或正则表达式"}  endString 默认以关键字“店”结尾   customRegular自定义  allLeaf 所有叶子节点
    public static final String DING_SYNC_STORE_RULE_ENDSTRING = "endString";
    public static final String DING_SYNC_STORE_RULE_CUSTOMREGULAR = "customRegular";
    public static final String DING_SYNC_STORE_RULE_ALLLEAF = "allLeaf";
    public static final String DING_SYNC_STORE_RULE_STORELEAF = "storeLeaf";
    public static final String DING_SYNC_STORE_RULE_ENDSTRING_VALUE = "店";
    //以门店结尾的叶子节点
    public static final String DING_SYNC_STORE_RULE_STORE_ENDSTRING = "storeEndString";

    // 1钉钉中的角色  2钉钉中的职位  3钉钉中的角色+职位
    public static final int DING_SYNC_ROLE_RULE_ROLE = 1;
    public static final int DING_SYNC_ROLE_RULE_POSITION = 2;
    public static final int DING_SYNC_ROLE_RULE_ROLEANDPOSITION = 3;

    // 钉钉同步类型 1手动同步 2自动同步
    public static final int SYNC_TYPE_MANUAL = 1;
    public static final int SYNC_TYPE_AUTO = 2;

    // 钉钉同步状态 0未开启同步 1同步中 2同步成功 3同步失败
    public static final int SYNC_STATUS_NOTOPEN = 0;
    public static final int SYNC_STATUS_ONGOING = 1;
    public static final int SYNC_STATUS_SUCCESS = 2;
    public static final int SYNC_STATUS_FAIL = 3;


    //记录同步失败所在节点
    public static final String SYNC_STAGE_DEPT_NODE = "deptNodeFail";
    public static final String SYNC_STAGE_ROLE_NODE = "roleNodeFail";
    public static final String SYNC_STAGE_USER_NODE = "userNodeFail";
    public static final String SYNC_STAGE_SUCCESS_NODE = "successNode";



    //  企业操作日志类型 开通open  关闭close  同步sync
    public static final String ENTERPRISE_OPERATE_LOG_OPEN = "open";
    public static final String ENTERPRISE_OPERATE_LOG_CLOSE = "close";
    public static final String ENTERPRISE_OPERATE_LOG_SYNC = "sync";
    public static final String ENTERPRISE_OPERATE_LOG_SCHEDULER = "scheduler";
    /**
     * 华莱士定制的同步
     */
    public static final String WALLACE_EID = "451c4fdf6b1645b79e439fea477c369e";
    public static final String WALLACE_POSITION_NAMES = "餐厅经理,餐厅助理（代管）,餐厅训练员（代管）";

    /**
     * 集成酷学院模块
     */
    /**
     * 开通酷学院鉴权app_id
     */
    public static final String OPEN_ENTERPRISE_ID = "78836";
    /**
     * 开通酷学院鉴权的secret
     */
    public static final String SECRET = "81c44d8e73bb4735ac125ab2f15690d3";
    /**
     * 开通酷学院标记类型
     */
    public static final String OPEN_SOURCE = "cool_store";
    /**
     * 酷学院返回的接口状态 200 正常
     */
    public static final String STATUS_200 = "200";

    public static final String STATUS_500 = "500";
    /**
     * 酷学院返回的接口状态
     */
    public static final String STATUS_870007 = "870007";
    /**
     * 酷学院返回的接口状态 870001 鉴权失败
     */
    public static final String STATUS_870001 = "870001";
    /**
     * 酷学院返回的接口状态 870012 参数错误
     */
    public static final String STATUS_870012 = "870012";
    /**
     * 酷学院返回的接口状态 870013 企业未开通
     */
    public static final String STATUS_870013 = "870013";
    /**
     * 酷学院返回的接口状态 870014 开通中
     */
    public static final String STATUS_870014 = "870014";

    /**
     * 酷学院消息类型，1代表文本 2代表oa链接消息
     */
    public static final Integer MESSAGE_TYPE = 1;


    /**
     * 门店通：同步用户通讯录所有信息（所在区域&权限范围）
     */
    public static final int OP_USER_CONTACT_SYNC_ALL = 0;

    /**
     * 门店通：同步用户信息
     */
    public static final int OP_USER_CONTACT_SYNC_INFO = 1;

    /**
     * 门店通：同步用户通讯录所在区域
     */
    public static final int OP_USER_CONTACT_SYNC_SCOPE = 2;

    /**
     * 门店通：同步用户通讯录权限范围
     */
    public static final int OP_USER_CONTACT_SYNC_NODE = 3;

    /**
     * 门店通：通讯录并发同步时缓存key
     */
    public static final String SYNC_NODE_CONCURRENT_CACHE_KEY = "sync:oneparty:node:{0}:{1}";

    /**
     * 判断角色是否为子管理员或负责人
     * @param roleName
     * @throws
     * @return: java.lang.Boolean
     * @Author: xugangkun
     * @Date: 2021/3/27 20:50
     */
    public static Boolean checkSubManage(String roleName) {
        return SyncConfig.ROLE_SUB_ADMIN.equals(roleName) || SyncConfig.ROLE_LEADER.equals(roleName);
    }
    /**
     * 如果角色同步规则不为空，并且同步的角色名称不在规则内，则返回false.其他情况返回true
     * @author chenyupeng
     * @date 2021/8/18
     * @param roleName 同步的角色名称
     * @param dingSyncRoleRuleDetail 同步的角色规则，以逗号分隔，如：（店长，店员，营运）
     * @return java.lang.Boolean
     */
    public static Boolean checkRoleRule(String roleName,String dingSyncRoleRuleDetail) {
        //如果配置了同步的角色规则，只有规则内的角色才进行同步
        if(StringUtil.isNotEmpty(dingSyncRoleRuleDetail)){
            List<String> dingSyncRoleRuleList = Arrays.asList(dingSyncRoleRuleDetail.split(Constants.COMMA));
            if(!(CollectionUtils.isNotEmpty(dingSyncRoleRuleList) && dingSyncRoleRuleList.contains(roleName))){
                return false;
            }
        }
        return true;
    }
}
