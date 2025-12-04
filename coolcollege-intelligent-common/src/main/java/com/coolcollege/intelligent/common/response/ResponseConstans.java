package com.coolcollege.intelligent.common.response;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseConstans {

    private static final Logger logger = LoggerFactory.getLogger(ResponseConstans.class);




    /**
     * send error response
     *
     * @param response
     * @param errContext
     */
    public static void responseErrResult(HttpServletResponse response, ResponseResultMessage errContext) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        addCors(response);
        try {
            response.getWriter().write(JSON.toJSONString(errContext.getMessage()));
        } catch (IOException ex) {
            logger.error("responseErrResult error", ex);
        }

    }

    private static void addCors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
    }

}
