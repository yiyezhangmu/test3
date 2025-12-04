package com.coolcollege.intelligent.config;

import cn.hutool.json.JSONUtil;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhangchenbiao
 * @FileName: RequestInterceptor
 * @Description: 请求拦截器
 * @date 2021-10-14 14:59
 */
@Component
public class RequestInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

    @Resource
    private EnterpriseConfigApiService enterpriseConfigApiService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpServletRequest reqs = (HttpServletRequest) request;
        CurrentUser user = UserHolder.getUser();
        //获取@PathVariable的值
        Map pathVariables = (Map) reqs.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if(Objects.isNull(pathVariables)){
            if(Objects.isNull(user)){
                UserHolder.setUser(JSONUtil.toJsonStr(new CurrentUser()));
            }
            return super.preHandle(request, response, handler);
        }
        String enterpriseId = (String)pathVariables.get("enterprise-id");
        String currentLoginEnterpriseId = Optional.ofNullable(user).map(o->o.getEnterpriseId()).orElse(null);
        //请求路径中的enterprise-id与当前登录用户的enterprise-id做比较 不一致则抛出异常
        if(StringUtils.isNotBlank(enterpriseId) && StringUtils.isNotBlank(currentLoginEnterpriseId) && !enterpriseId.equals(currentLoginEnterpriseId)){
            throw new ServiceException(ErrorCodeEnum.TOKEN_ERROR);
        }
        if(StringUtils.isNotBlank(enterpriseId) && Objects.nonNull(user) && StringUtils.isBlank(user.getDbName())){
            user.setDbName(enterpriseConfigApiService.getEnterpriseDbName(enterpriseId));
            UserHolder.setUser(JSONUtil.toJsonStr(user));
        }
        if(Objects.isNull(user) && StringUtils.isBlank(enterpriseId)){
            user = new CurrentUser();
            UserHolder.setUser(JSONUtil.toJsonStr(user));
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

}
