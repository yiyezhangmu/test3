package com.coolcollege.intelligent.controller.systemadmin;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;

/**
 * 实时调整日志级别
 * 
 * @author jeffrey
 * @date 2020/12/15
 */
@RestController
@RequestMapping("/systemadmin/log")
@BaseResponse
public class LogController {
    private Logger log = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private HttpServletRequest request;

    /**
     * 修改项目日志输出级别
     *
     */
    @GetMapping("changeLevel")
    public ResponseResult changeLevel(@RequestParam(value = "level") String level,
        @RequestParam(value = "packageName") String packageName) {

        ch.qos.logback.classic.LoggerContext loggerContext =
            (ch.qos.logback.classic.LoggerContext)LoggerFactory.getILoggerFactory();

        if (packageName == null || packageName.length() < 2) {
            // 默认值-1，更改全局日志级别；否则按传递的包名或类名修改日志级别。
            loggerContext.getLogger("root").setLevel(ch.qos.logback.classic.Level.toLevel(level));
        } else {
            loggerContext.getLogger(packageName).setLevel(ch.qos.logback.classic.Level.valueOf(level));
        }
        return ResponseResult.success(true);
    }

    @GetMapping("testLog")
    public ResponseResult testLog() {

        log.error("log.isInfoEnabled() ==" + log.isInfoEnabled());
        log.debug("debug log 0000000000000:" + System.currentTimeMillis());
        log.debug("debug log 1111111111111:" + System.currentTimeMillis());
        log.info("info log 22222222222:" + System.currentTimeMillis());
        log.warn("warn log 33333333333:" + System.currentTimeMillis());
        log.error("error log 44444444444:" + System.currentTimeMillis());
        return ResponseResult
            .success(" log.isInfoEnabled()" + log.isInfoEnabled() + ",  log.isWarnEnabled()=" + log.isWarnEnabled());
    }
    

    @GetMapping("dostart")
    public ResponseResult dostart(@RequestParam(value = "type", required = false) String type) {
        ServletContext application = request.getServletContext();
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(application);
        String profile = webApplicationContext.getEnvironment().getActiveProfiles()[0];
        if(profile!=null &&(profile.startsWith("dev") ||  profile.startsWith("local"))) {
            try {
                Runtime.getRuntime().exec("nohup /opt/deployproject/coolcollege-intelligent/start-with-up.sh  >> /opt/deployproject/coolcollege-intelligent/logs/log.log & ");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                 e.printStackTrace();
            }
        }
        return ResponseResult.success("环境是"+profile);

    }

}
