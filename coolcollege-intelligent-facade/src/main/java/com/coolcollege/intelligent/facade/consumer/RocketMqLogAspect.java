package com.coolcollege.intelligent.facade.consumer;


import com.aliyun.openservices.ons.api.Message;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolstore.base.utils.MDCUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.Properties;

/**
 * 切面处理类，操作日志异常日志记录处理
 * @author jiangjixiang
 */
@Aspect
@Component
@Slf4j
public class RocketMqLogAspect {


    /**
     * 设置操作异常切入点记录异常日志 扫描所有controller包下操作
     */
    @Pointcut(value = "execution(public * com.coolcollege.intelligent.facade.consumer.listener..*.consume(..))")
    public void rocketMqMdc() {
    }

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     */
    @Before(value = "rocketMqMdc()")
    public void mqBeforeLog(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Message message = (Message)args[0];
            String messageId = message.getMsgID() + Constants.UNDERLINE + message.getReconsumeTimes();
            Properties userProperties = message.getUserProperties();
            //消息发送者线程的requestId
            String requestId = Optional.ofNullable(userProperties).map(o -> o.getProperty(Constants.REQUEST_ID)).orElse(UUIDUtils.get32UUID());
            MDCUtils.put(Constants.REQUEST_ID, requestId);
            MDCUtils.put(Constants.MESSAGE_ID, messageId);
        } catch (Exception e) {
            log.info("MDC mqBeforeLog", e);
        }
    }


    @AfterReturning(value = "rocketMqMdc()")
    public void doAfterReturning(JoinPoint joinPoint){
        try {
            MDC.clear();
        } catch (Exception e) {
            log.info("MDC doAfterReturning", e);
        }
    }
}