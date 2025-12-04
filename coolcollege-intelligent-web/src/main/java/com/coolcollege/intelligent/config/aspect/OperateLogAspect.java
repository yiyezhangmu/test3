package com.coolcollege.intelligent.config.aspect;


import com.alibaba.fastjson.JSON;
import com.alipay.sofa.rpc.context.RpcInvokeContext;
import com.coolcollege.intelligent.common.annotation.OperateLog;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.common.annotation.SysLog;
import com.coolcollege.intelligent.common.enums.ResponseCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.log.LogMapper;
import com.coolcollege.intelligent.dao.syslog.SysLogMapper;
import com.coolcollege.intelligent.model.log.ExceptionLogDO;
import com.coolcollege.intelligent.model.log.OperationLogDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.model.syslog.dto.SysLogResolveDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.syslog.IOpContentResolve;
import com.coolcollege.intelligent.service.syslog.OpContentContext;
import com.coolcollege.intelligent.util.SysLogHelper;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolstore.base.utils.MDCUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 切面处理类，操作日志异常日志记录处理
 * @author jiangjixiang
 */
@Aspect
@Component
@Slf4j
public class OperateLogAspect {

    @Resource
    private  LogMapper logMapper;

    @Resource
    private SysLogMapper sysLogMapper;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private OpContentContext opContentContext;

    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.coolcollege.intelligent.common.annotation.OperateLog)")
    public void operLogPoinCut() {
    }


    /**
     * 设置操作异常切入点记录异常日志 扫描所有controller包下操作
     */
    @Pointcut("execution(* com.coolcollege.intelligent.controller..*.*(..))")
    public void operExceptionLogPoinCut() {
    }

    @Pointcut("execution(* com.coolcollege.intelligent.facade.open.api..*.*(..))")
    public void openApiLogPointCut() {
    }

    @Around(value = "openApiLogPointCut()")
    public Object  openApiLog(ProceedingJoinPoint proceedingJoinPoint){
        String requestId = RpcInvokeContext.getContext().getRequestBaggage("requestId");
        String enterpriseId = RpcInvokeContext.getContext().getRequestBaggage("enterpriseId");
        String targetURL = RpcInvokeContext.getContext().getTargetURL();
        MDCUtils.putIfAbsent(Constants.REQUEST_ID, requestId);
        log.info("进入开放平台@@@@@:{}", targetURL);
        long startTime= System.currentTimeMillis();
        try {
            DataSourceHelper.reset();
            RpcLocalHolder.setEnterpriseId(enterpriseId);
            Object result = proceedingJoinPoint.proceed();
            log.info("开放平台结束，耗时：{} 毫秒", System.currentTimeMillis() - startTime);
            return result;
        } catch (Throwable throwable) {
            log.error("开放平台异常了");
            throwable.printStackTrace();
        }finally {
            MDC.clear();
        }
        return null;
    }

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     *
     * @param joinPoint 切入点
     * @param keys      返回结果
     */
    @AfterReturning(value = "operLogPoinCut()", returning = "keys")
    public void saveOperLog(JoinPoint joinPoint, Object keys) {
        CurrentUser user = UserHolder.getUser();
        addOperateLog(joinPoint, keys, user);
    }

    @Async("taskExecutor")
    public void addOperateLog(JoinPoint joinPoint, Object keys, CurrentUser user) {
        String db = user.getDbName();
        String enterpriseId = user.getEnterpriseId();
        if(StringUtils.isAnyEmpty(enterpriseId, db)){
            return;
        }
        DataSourceHelper.reset();
        DataSourceHelper.changeToSpecificDataSource(db);
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);

        OperationLogDO operlogDO = new OperationLogDO();
        try {
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            OperateLog opLog = method.getAnnotation(OperateLog.class);
            if (opLog != null) {
                operlogDO.setOperateModule(opLog.operateModule()); // 操作模块
                operlogDO.setOperateType(opLog.operateType()); // 操作类型
                operlogDO.setOperateDesc(opLog.operateDesc()); // 操作描述
            }
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName;
            operlogDO.setOperateMethod(methodName);
            // 请求的参数
            // 将参数所在的数组转换成json
            String params = JSON.toJSONString(covertMap(joinPoint));
            // 请求参数
            operlogDO.setRequestParam(params);
            // 返回结果
            operlogDO.setResponseParam(JSON.toJSONString(keys));
            // 请求用户ID
            operlogDO.setUserId(user.getUserId());
            // 请求用户名称
            operlogDO.setUserName(user.getName());
            // 请求IP
            operlogDO.setIp(getIpAddress(request));
            // 请求URI
            operlogDO.setUrl(request.getRequestURI());
            // 创建时间
            operlogDO.setCreateTime(new Date());
            logMapper.insertLogOperate(enterpriseId, operlogDO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     *
     * @param joinPoint 切入点
     * @param e         异常信息
     */
    @AfterThrowing(pointcut = "operExceptionLogPoinCut()", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Throwable e) {
        CurrentUser user = UserHolder.getUser();
        addExpectionLog(joinPoint, e, user);
    }

    @Async("taskExecutor")
    public void addExpectionLog(JoinPoint joinPoint, Throwable e, CurrentUser user) {
        String db = user.getDbName();
        String enterpriseId = user.getEnterpriseId();
        if(StringUtils.isAnyEmpty(enterpriseId, db)){
            return;
        }
        DataSourceHelper.reset();
        DataSourceHelper.changeToSpecificDataSource(db);
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);

        ExceptionLogDO excepLog = new ExceptionLogDO();
        try {
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName;
            // 请求的参数
            // 将参数所在的数组转换成json
            // 将参数所在的数组转换成json
            String params = JSON.toJSONString(covertMap(joinPoint));
            excepLog.setRequestParam(params);
            // 请求方法名
            excepLog.setOperateMethod(methodName);
            // 异常名称
            excepLog.setExceptionName(e.getClass().getName());
            // 异常信息
            excepLog.setExceptionMessage(stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace()));
            // 操作员ID
            excepLog.setUserId(user.getUserId());
            // 操作员名称
            excepLog.setUserName(user.getName());
            // 操作URI
            excepLog.setUrl(request.getRequestURI());
            // 操作员IP
            excepLog.setIp(getIpAddress(request));
            // 发生异常时间
            excepLog.setCreateTime(new Date());
            logMapper.insertExceptionOperate(enterpriseId, excepLog);
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    @Around(value = "@annotation(sysLog)")
    public Object sysLogAround(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        Map<String, Object> reqParams = covertMap(joinPoint);
        // 系统日志预处理，用于查询旧数据等
        String preprocessResult = sysLogPreprocess(reqParams, sysLog);
        // 原方法执行
        Object result = joinPoint.proceed();
        // 新增日志
        addSysLog(reqParams, result, preprocessResult, sysLog);
        return result;
    }

    /**
     * 系统日志预处理
     * @param reqParams 请求参数
     * @param sysLog 系统日志注释
     * @return 处理结果
     */
    private String sysLogPreprocess(Map<String, Object> reqParams, SysLog sysLog) {
        if (!sysLog.preprocess()) return null;
        try {
            CurrentUser user = UserHolder.getUser();
            String db = user.getDbName();
            String enterpriseId = user.getEnterpriseId();
            if (StringUtils.isAnyEmpty(enterpriseId, db)) {
                return null;
            }
            DataSourceHelper.reset();
            DataSourceHelper.changeToSpecificDataSource(db);

            IOpContentResolve contentResolve = opContentContext.getContentResolve(sysLog.opModule());
            if (Objects.nonNull(contentResolve)) {
                return contentResolve.preprocess(enterpriseId, reqParams, sysLog.opType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加系统日志
     * @param reqParams 请求参数列表
     * @param returns 返回结果
     * @param preprocessResult 预处理结果
     * @param sysLog 系统日志
     */
    public void addSysLog(Map<String, Object> reqParams, Object returns, String preprocessResult, SysLog sysLog) {
        try {
            if (returns instanceof ResponseResult && ((ResponseResult) returns).getCode() != ResponseCodeEnum.SUCCESS.getCode()) {
                return;
            }
            CurrentUser user = UserHolder.getUser();
            String enterpriseId = user.getEnterpriseId();

            // 从RequestAttributes中获取HttpServletRequest的信息
            HttpServletRequest request = (HttpServletRequest) RequestContextHolder.getRequestAttributes()
                    .resolveReference(RequestAttributes.REFERENCE_REQUEST);

            SysLogResolveDTO sysLogResolveDTO = SysLogResolveDTO.builder()
                    .enterpriseId(enterpriseId)
                    .menus(StringUtils.isNotBlank(sysLog.menus()) ? sysLog.menus() : sysLog.opModule().getMenus())
                    .func(sysLog.func())
                    .subFunc(sysLog.subFunc())
                    .opType(sysLog.opType())
                    .opModule(sysLog.opModule())
                    .resolve(sysLog.resolve())
                    .opUserId(user.getUserId())
                    .opUserName(user.getName())
                    .opUserMobile(user.getMobile())
                    .opUserJobnumber(user.getJobnumber())
                    .opTime(new Date())
                    .opIp(getIpAddress(request))
                    .deviceInfo(getUserAgent(request))
                    .reqParams(JSON.toJSONString(reqParams))
                    .respParams(JSON.toJSONString(returns))
                    .url(request.getRequestURI())
                    .extendInfo(SysLogHelper.buildExtendInfoStrByPreprocessResult(preprocessResult))
                    .build();
            simpleMessageService.send(JSON.toJSONString(sysLogResolveDTO), RocketMqTagEnum.SYS_LOG_RESOLVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * map转换
     * @param joinPoint
     * @return
     */
    public Map<String,Object> covertMap(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
        Map<String,Object> rtnMap= Maps.newHashMap();
        for (int i = 0; i < paramNames.length; i++) {
            Object arg = args[i];
            if(Objects.isNull(arg) ||  arg instanceof MultipartFile || arg instanceof  HttpServletRequest || arg instanceof HttpServletResponse){
                continue;
            }
            rtnMap.put(paramNames[i],args[i]);
        }
        return rtnMap;
    }

    /**
     * 转换异常信息为字符串
     *
     * @param exceptionName    异常名称
     * @param exceptionMessage 异常信息
     * @param elements         堆栈信息
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuffer strbuff = new StringBuffer();
        for (StackTraceElement stet : elements) {
            strbuff.append(stet + "\n");
        }
        String message = exceptionName + ":" + exceptionMessage + "\n\t" + strbuff.toString();
        return message;
    }

    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取User-Agent
     * @param request 请求对象
     * @return User-Agent
     */
    public String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}