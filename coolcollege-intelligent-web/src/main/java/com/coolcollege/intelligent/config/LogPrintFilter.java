package com.coolcollege.intelligent.config;

import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.config.request.ParameterRequestWrapper;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.util.ParamFormatUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.utils.MDCUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author ydw
 * @Description 日志打印
 * @date 2020/1/15
 */
@Slf4j
@Component
@Order(2)
public class LogPrintFilter implements Filter {

    @Autowired
    private RedisUtilPool redis;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest reqs = (HttpServletRequest) servletRequest;
        String uri = reqs.getRequestURI();
        MDCUtils.put(Constants.REQUEST_ID, UUIDUtils.get32UUID());
        // 请求参数
        Map<String, String[]> parameterMap = reqs.getParameterMap();
        Map<String, Object> underMap = new HashMap<>();
        String userAgent = reqs.getHeader("User-Agent");
        StringBuilder params = new StringBuilder();
        params.append(uri);
        if (Objects.nonNull(parameterMap)) {
            params.append("?");
            parameterMap.forEach((k, v) -> {
                // 下划线转驼峰
                if (k.contains("_")) {
                    String underKey = ParamFormatUtil.UnderlineToHump(k);
                    if (!parameterMap.containsKey(underKey)) {
                        underMap.put(underKey, v);
                        params.append(underKey).append("=").append(v[0]).append("&");
                    }
                }
                params.append(k).append("=").append(v[0]).append("&");
            });
            underMap.putAll(parameterMap);
        }
        params.append("&Method=").append(reqs.getMethod()).append("&UA=").append(userAgent);
        String accessToken = reqs.getParameter("access_token");
        if (StringUtils.isNotEmpty(accessToken)) {
            String key = "access_token:" + accessToken;
            String userStr = redis.getString(key);
            if (StringUtils.isNotBlank(userStr)) {
                CurrentUser currentUser = JSON.parseObject(userStr, CurrentUser.class);
                params.append("&enterprise_id=")
                        .append(currentUser.getEnterpriseId())
                        .append("&user_id=").append(currentUser.getId());
            }
        }
        // 拼接打印数据

        //request body 支持。
        log.info(params.toString());
        ParameterRequestWrapper wrapper = new ParameterRequestWrapper(reqs, underMap);
        filterChain.doFilter(wrapper, response);
        if(response.getStatus()>=400) {
            log.warn("request error, status={}, request={}",response.getStatus() ,params.toString());
        }
        RequestHolder.setRequest(params.toString());

    }

    @Override
    public void destroy() {
        try {
            MDC.clear();
        } catch (Exception e) {
            log.info("LogPrintFilter destroy", e);
        }
    }
}
