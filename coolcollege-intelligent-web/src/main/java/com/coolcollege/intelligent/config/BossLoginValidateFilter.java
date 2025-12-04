package com.coolcollege.intelligent.config;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.system.dto.BossLoginUserDTO;
import com.coolcollege.intelligent.model.userholder.BossUserHolder;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 管理用户登录过滤器
 *
 * @author byd
 */
@Order(3)
@WebFilter(filterName = "bossLoginFilter", urlPatterns = {"/boss/manage/*","/boss/menu/*"})
public class BossLoginValidateFilter implements Filter {

    @Autowired
    private RedisUtilPool redisUtilPool;


    private static AntPathMatcher matcher = new AntPathMatcher();

    private static List<String> patternList =

            Lists.newArrayList("/boss/manage/login/loginIn",
                    "/boss/manage/bossEnterpriseSetting/*/sendUpcomingFinish",
                    "/boss/manage/bossEnterpriseSetting/getAccessCoolCollegeSetting",
                    "/boss/manage/bossUser/changePasswordJudge", "/enterprise/getStoreCount");


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
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest reqs = (HttpServletRequest) servletRequest;
        String uri = reqs.getRequestURI();
        String method = reqs.getMethod();
        BossUserHolder.clearUser();
        if ("OPTIONS".equals(method)) {
            return;
        }

        if (!excludePath(uri)) {
            String accessToken = reqs.getParameter("access_token");
            if (StringUtils.isBlank(accessToken)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(JSON.toJSONString(
                        new ResponseResult(HttpStatus.UNAUTHORIZED.value(), "Invalid token", null)));
                return;
            }
            String userInfo = redisUtilPool.getString(Constants.BOSS_LOGIN_USER_KEY + accessToken);
            if (StringUtils.isBlank(userInfo)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write(JSON.toJSONString(
                        new ResponseResult(HttpStatus.UNAUTHORIZED.value(), "Invalid token", null)));
                return;
            }
            BossUserHolder.setUser(JSONUtil.toBean(userInfo, BossLoginUserDTO.class));
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            BossUserHolder.clearUser();
        }
    }

    @Override
    public void destroy() {

    }
}
