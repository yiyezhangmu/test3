package com.coolcollege.intelligent.config;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.isv.SpringContextUtil;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.LoginUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

/**
 * @author ydw
 * @Description 权限校验
 * @date 2020/1/15
 */
@Component
@Order(3)
@Slf4j
public class TokenValidateFilter implements Filter {

    @Autowired
    private RedisUtilPool redis;
    @Resource
    private LoginUtil loginUtil;

    private static AntPathMatcher matcher = new AntPathMatcher();

    private static List<String> patternList =

            Lists.newArrayList("/web/check/ok","/check/ok",
                    "/datasources/nodes",
                    "/v2/isvLogin",
                    "/v2/isvLogin-admin",
                    "/v3/feiShuLogin",
                    "/v3/qyLogin",
                    "/v3/getUserInfoByToken",
                    "/v3/getUserInfoByUserIdAndCorpId",
                    "/v3/wx_qrcode_login",
                    "/v2/enterprises/*/subtables",
                    "/system/synchro/sync",
                    "/system/synchro/close",
                    "/v2/*/communication/*/*",
                    "/v2/*/communication/*/*/*",
                    "/v2/*/summaryVideo/*",
                    "/v2/enterprises/*/departments/syncInformation",
                    "/v2/enterprises/middle/stage/form*",
                    "/v2/enterprises/*/ding_message/store_task",
                    "/v2/*/setting/callBack/*",
                    "/v2/enterprises/form/init/*",
                    "/v2/enterprise/list",
                    "/v2/getMenus",
                    "/boss/menu/**",
                    "/boss/manage/**",
                    "/boss/api/**",
                    "/callback/test/**",
                    "/v2/enterprise/record/statistics",
                    "/v2/enterprises/videos/user_list_video",
                    "/v2/enterprises/videos/liveVideo",
                    "/v2/enterprises/videos/ridership","/v3/system/help/*",
                    "/v3/enterprises/aliyun/vds/*/*/webhook/callback",
                    "/v3/enterprises/aliyun/vds/*/webhook/callback/*",
                    "/v3/enterprises/*/yingshiyunMsgPush/webhook",
                    "/v2/enterprises/*/test/dingScopeUser",
                    "/v3/system/**",
                    "/v2/login/login/refreshLogin",
                    "/v3/enterprises/schedule/live/callback",
                    "/WW_verify_gbRti27HDz8hyIb0.txt",
                    "/WW_verify_LNFOo6kZmb7MWy1p.txt",
                    "/v3/login/accountLogin",
                    "/v3/sms/sendSmsCode",
                    "/v3/enterpriseUser/forgetPassword",
                    "/v3/enterpriseUser/inviteRegister",
                    "/v3/enterpriseUser/checkInviteUrlKey",
                    "/v3/enterprise/register",
                    "/v3/sms/sendSmsCode/test",
                    "/v3/enterprises/patrolstore/patrolStatistics/regionsCount",
                    "/v3/enterprises/*/patrolstore/patrolStore/historyExecutionQuery",
                    "/v3/enterprises/*/patrolstore/patrolStore/recordInfo",
                    "/v3/enterprises/*/patrolstore/patrolStore/dataTableInfoList",
                    "/v3/enterprises/*/patrolstore/patrolStore/getTaskDetail",
                    "/v3/enterprises/*/patrolstore/patrolStore/recordInfoShareExpire",
                    "/v3/enterprises/*/patrolStore/safetyCheckFlow/listDataColumnCommentHistory",
                    "/v3/enterprises/*/patrolStore/safetyCheckFlow/listDataColumnCheckHistory",
                    "/v3/enterprises/*/patrolStore/dataColumnAppeal/appealHistoryList",
                    "/v3/enterprises/*/patrolStore/dataColumnAppeal/appealList",
                    "/v3/enterprises/*/rowform/meta/getStaColumnTailByIds",
                    "/WW_verify_gbRti27HDz8hyIb0.txt",
                    "/v3/enterprises/*/passenger/callback",
                    "/vod/callback",
                    "/v3/enterprises/*/shareInfo/**",
                    "/swagger-ui.html",
                    "/**/swagger*/**",
                    "/doc.html",
                    "/**/webjars/**",
                    "/**/v2/api-docs*",
                    "/tmpOpenApi/*/testShare/*",
                    "/openApi/xfsg/*/*",
                    "/openApi/share/b2zg/*",
                    "/v3/system/help/info",
                    "/v3/system/help/changecontent",
                    "/**/v2/api-docs*",
                    "/tmpOpenApi/*/testShare/*",
                    "/v3/enterprises/*/homepage/getHomeTemplateByKey",
                    "/v3/enterprises/college/integration/sendMsg",
                    "/v3/enterprises/*/questionRecord/getQuestionShareDetail",
                    "/v3/enterprises/*/questionRecord/historyList",
                    "/v3/enterprises/*/questionParentInfo/detail",
                    "/v3/enterprises/*/storeWorkRecord/storeWorkRecordExpired",
                    "/v3/enterprises/*/storeWorkRecord/theSameExecutor",
                    "/v3/enterprises/*/storeWorkRecord/getStoreWorkBaseDetail",
                    "/v3/enterprises/*/storeWork/getStoreWorkDataTableColumn",
                    "/v3/enterprises/*/storeWork/getStoreWorkTableList",
                    "/v3/enterprises/*/storeWork/getStoreWorkTableDataList",
                    "/v3/enterprises/*/storeWork/currentUserStoreWorkOverViewData",
                    "/*/enterprises/*/stores/*/get",
                    "/v3/enterprise/enterpriseOpenLeaveInfo/leaveWithNoLogin",
                    "/v3/enterprise/enterpriseOpenLeaveInfo/checkUserLeaveInfoWithNoLogin",
                    "/v3/enterprises/college/integration/getLoginCoolCollegeTicketForOneParty",
                    "/v3/enterprises/*/supervision/batchUpdate",
                    "/v3/enterprises/conversation/card/refresh",
                    "/v3/enterprises/userJurisdiction/**",
                    "/upload/file",
                    "/v3/enterprises/*/test/cfjTest",
                    "/v3/enterprises/enterprise/newBelle/getAppType",
                    "/wechat/getSignature",
                    "/wechat/check",
                    "/v3/enterprises/oaPlugin/**",
                    "/v3/enterprises/*/stores/storeUserPositionList",
                    "/v3/enterprises/*/achievement/taskRecord/sendRemindMsg",
                    "/v3/enterprises/oaPlugin/**",
                    "/v3/enterprises/IM/card/**",
                    "/v3/enterprises/*/stores/*/xfsg/get",
                    "/v2/yuNiLogin",
                    "/test/**",
                    "/wechat/**",
                    "/v3/enterprises/*/questionParentInfo/workOrder/completionStatus",
                    "/video/download/share/*/*","/v3/enterprise/getStoreCount",
                    "/yingshiyun/webhook",
                    "/open/store/syncSingleStore",
                    "/**/syncDeptAndUser",
                    "/device/open/**",
                    "/v3/mclzLogin",
                    "/v3/mclzRegisteredVerify",
                    "/v3/data/correction/songxia/getStockInfo",
                    "/v3/askBotLogin",
                    "/v3/enterprises/*/aiInspection/dailyReportDetail",
                    "/v3/enterpriseUser/getUserByMobile","/v3/device/callback/**","/v1/afqi/callback/detectResult"
                    );


    /**
     * @param uri
     * @return boolean
     * @throws
     * @Title excludePath
     * @Description 是否是放行的请求
     */
    private boolean excludePath(String uri) {
        for (String pattern : patternList) {
            if (matcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest reqs = (HttpServletRequest) servletRequest;
        String uri = reqs.getRequestURI();
        String method = reqs.getMethod();
        String userStr = "";
        CurrentUser currentUser = null;
        boolean isInWhiteList = excludePath(uri);
        String accessToken = reqs.getParameter("access_token");
        String key = "access_token:" + accessToken;
        if(StringUtils.isNotBlank(accessToken)){
            userStr = redis.getString(key);
            if(StringUtils.isNotBlank(userStr)){
                currentUser = JSON.parseObject(userStr, CurrentUser.class);
            }
        }
        log.info("url:{}", uri);
        if ( !isInWhiteList && !method.equals("OPTIONS")) {
            if (StringUtils.isEmpty(accessToken)) {
                log.info("access_token is null");
                response.setStatus(HttpStatus.OK.value());
                response.getWriter().write(JSON.toJSONString(
                        ResponseResult.fail(ErrorCodeEnum.ACCESS_TOKEN_INVALID)));
                return;
            }
            if (Objects.isNull(currentUser)) {
                log.info("currentUser is null");
                response.setStatus(HttpStatus.OK.value());
                response.getWriter().write(JSON.toJSONString(
                        ResponseResult.fail(ErrorCodeEnum.ACCESS_TOKEN_INVALID)));
                return;
            }
            if (currentUser.getSysRoleDO() == null || currentUser.getSysRoleDO().getId() == null) {
                //如果当前用户的缓存中没有角色信息，填充
                DataSourceHelper.changeToSpecificDataSource(currentUser.getDbName());
                SysRoleMapper sysRoleMapper = SpringContextUtil.getBean("sysRoleMapper", SysRoleMapper.class);
                PageHelper.clearPage();
                SysRoleDO sysRoleDO = sysRoleMapper.getHighestPrioritySysRoleDoByUserId(currentUser.getEnterpriseId(), currentUser.getUserId());
                if (sysRoleDO == null) {
                    // 如果没有最高优先级的，给未分配的角色
                    SysRoleService sysRoleService = SpringContextUtil.getBean("sysRoleService", SysRoleService.class);
                    Long roleIdByRoleEnum = sysRoleService.getRoleIdByRoleEnum(currentUser.getEnterpriseId(), Role.EMPLOYEE.getRoleEnum());
                    sysRoleDO = sysRoleMapper.getRole(currentUser.getEnterpriseId(), roleIdByRoleEnum);
                }
                currentUser.setSysRoleDO(sysRoleDO);
                userStr = JSON.toJSONString(currentUser);
                DataSourceHelper.reset();
            }
            //更新临时token有效期
            // 为了不刷新永久token  暂时使用此方法  todo
            if(!key.equals("access_token:fix_token")) {
                redis.setString(key, userStr, Constants.ACTION_TOKEN_EXPIRE);
                // 更新缓存用户id和token的关系时间
                loginUtil.refreshTokenUserIdAndRoleId(currentUser.getEnterpriseId(), currentUser.getUserId(), String.valueOf(currentUser.getSysRoleDO().getId()));
            }
            log.info("url:{}, access_token:{}, userId:{}, username:{}, enterpriseId:{}", uri, accessToken, currentUser.getUserId(), currentUser.getName(), currentUser.getEnterpriseId());
        }
        if(StringUtils.isBlank(userStr) && !isInWhiteList){
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(JSON.toJSONString(
                    ResponseResult.fail(ErrorCodeEnum.ACCESS_TOKEN_INVALID)));
            return;
        }
        try {
            UserHolder.setUser(userStr);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserHolder.removeUser();
        }
    }

    @Override
    public void destroy() {

    }
}
