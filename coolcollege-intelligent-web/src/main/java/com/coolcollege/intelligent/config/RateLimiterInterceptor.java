package com.coolcollege.intelligent.config;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseConstans;
import com.coolcollege.intelligent.common.response.ResponseResultMessage;
import com.coolcollege.intelligent.common.util.isv.Env;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RateLimiterInterceptor extends HandlerInterceptorAdapter implements InitializingBean {


    @Autowired
    private Env env;

    /**
     * 应用级别总限流
     */
    private RateLimiter reqRateLimiter;


    private static final Logger logger = LoggerFactory.getLogger(RateLimiterInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean acquired = reqRateLimiter.tryAcquire();
        String req = RequestHolder.getRequest();
        if (!acquired) {
            logger.warn("request be omitted by reqRateLimiter, request={}", req);
            ResponseResultMessage responseResultMessage = new ResponseResultMessage();
            responseResultMessage.setCode(ErrorCodeEnum.MANY_REQUEST.getCode());
            responseResultMessage.setMessage(ErrorCodeEnum.MANY_REQUEST.getMessage());
            ResponseConstans.responseErrResult(response, responseResultMessage);
            return false;
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reqRateLimiter = RateLimiter.create(env.getReqPerSecond());
    }

}
