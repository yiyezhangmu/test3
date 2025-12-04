package com.coolcollege.intelligent.config.aspect;


import com.alipay.sofa.rpc.context.RpcInvokeContext;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolstore.base.utils.MDCUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * 切面处理类，操作日志异常日志记录处理
 * @author jiangjixiang
 */
@Aspect
@Component
@Slf4j
public class RpcLogAspect {


    /**
     * 设置操作异常切入点记录异常日志 扫描所有controller包下操作
     */
    @Pointcut(value = "execution(public * com.coolcollege.intelligent.facade..*.*(..)) && !execution(* com.coolcollege.intelligent.facade.consumer..*.*(..)) && !execution(* com.coolcollege.intelligent.facade.open.api..*.*(..))")
    public void rpcLogMdc() {
    }


    @Pointcut(value = "@within(com.alipay.sofa.runtime.api.annotation.SofaService)")
    public void sofaServiceClass() {
    }

    @Pointcut(value = "execution(public * com.coolcollege.intelligent.rpc..*.*(..))")
    public void rpcRequestId() {}

    @Before(value = "rpcRequestId()")
    public void rpcBeforeLog(JoinPoint joinPoint) {
        try {
            String requestId = MDC.get(Constants.REQUEST_ID);
            if(StringUtils.isBlank(requestId)){
                requestId = UUIDUtils.get32UUID();
            }
            RpcInvokeContext.getContext().putRequestBaggage(Constants.REQUEST_ID, requestId);
        } catch (Exception e) {
            log.info("MDC mqBeforeLog", e);
        }
    }

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     */
    @Before(value = "rpcLogMdc() && sofaServiceClass()")
    public void clientBeforeLog(JoinPoint joinPoint) {
        try {
            String requestId = RpcInvokeContext.getContext().getRequestBaggage(Constants.REQUEST_ID);
            MDCUtils.putIfAbsent(Constants.REQUEST_ID, requestId);
        } catch (Exception e) {
            log.info("MDC mqBeforeLog", e);
        }
    }


    @AfterReturning(value = "rpcLogMdc() && sofaServiceClass()")
    public void doAfterReturning(JoinPoint joinPoint){
        try {
            MDC.clear();
        } catch (Exception e) {
            log.info("MDC doAfterReturning", e);
        }
    }
}