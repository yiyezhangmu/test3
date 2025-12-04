package com.coolcollege.intelligent.config;


import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.ResponseCodeEnum;
import com.coolcollege.intelligent.common.exception.BaseException;
import com.coolcollege.intelligent.common.exception.BusinessException;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.response.ResponseResultMessage;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author Aaron
 * @Description 异常处理器
 * @date 2019/12/20
 */
@ControllerAdvice(basePackages = "com.coolcollege.intelligent.controller")
@ResponseBody
@Slf4j
public class ExceptionHandlerAdvice {


    /**
     * 处理未捕获的Exception
     *
     * @param e
     * @return 统一响应体
     * @throws Exception
     * @Title handleException
     * @Description 处理未捕获的Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult handleException(Exception e) {
        //随机生成一个错误码返回给前段并答应
        String uuid = UUIDUtils.get8UUID();
//        log.error("requestId={},message={},exception={}", uuid, e.getMessage(), e);
        log.error("requestId="+uuid+",全局异常", e);
        return  ResponseResult.fail(ErrorCodeEnum.UNKNOWN.getCode(), ErrorCodeEnum.UNKNOWN.getMessage()+uuid,
                stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace()));
    }

    /**
     * 处理业务异常BaseException
     *
     * @param e
     * @return 统一响应体
     * @throws Exception
     * @Title handleBaseException
     * @Description 处理业务异常BaseException
     */
    @ExceptionHandler(BaseException.class)
    public void handleBaseException(BaseException e, HttpServletResponse httpServletResponse) {
        log.error(e.getMessage(), e);
        ErrorCodeEnum code = e.getResponseCodeEnum();
        ResponseResultMessage responseResultMessage = new ResponseResultMessage();
        responseResultMessage.setCode(code.getCode());
        responseResultMessage.setMessage(code.getMessage());
        httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseResult(httpServletResponse, responseResultMessage);
    }

    @ExceptionHandler(BusinessException.class)
    public void handleBusinessException(BusinessException e, HttpServletResponse httpServletResponse) {
        log.error(e.getMessage(), e);
        ErrorCodeEnum code = e.getResponseCodeEnum();
        httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseExceptionResult(httpServletResponse, ResponseResult.fail(code.getCode(), code.getMessage()));
    }


    /**
     * 自定义业务异常ServiceException
     *
     * @param e
     * @return 统一响应体
     * @throws Exception
     * @Title handleBaseException
     * @Description 处理业务异常BaseException
     */
    @ExceptionHandler(ServiceException.class)
    public void handleBaseException(ServiceException e, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.error(e.getMessage(), e);
        ResponseResultMessage responseResultMessage = new ResponseResultMessage();
        responseResultMessage.setCode(e.getErrorCode());
        if(e.getErrorCode() == null){
            responseResultMessage.setCode(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode());
        }
        responseResultMessage.setMessage(e.getErrorMessage());
        httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        responseResult(httpServletResponse, responseResultMessage);
    }


    @ExceptionHandler(BindException.class)
    public void handleBindException(BindException e, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.error(e.getMessage(), e);
        ResponseResultMessage responseResultMessage = new ResponseResultMessage();
        responseResultMessage.setCode(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode());
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        responseResultMessage.setMessage(message);
        httpServletResponse.setStatus(HttpStatus.PRECONDITION_FAILED.value());
        responseResult(httpServletResponse, responseResultMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.error(e.getMessage(), e);
        ResponseResultMessage responseResultMessage = new ResponseResultMessage();
        responseResultMessage.setCode(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode());
        String message = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
        responseResultMessage.setMessage(message);
        httpServletResponse.setStatus(HttpStatus.PRECONDITION_FAILED.value());
        responseResult(httpServletResponse, responseResultMessage);
    }

    private void responseResult(HttpServletResponse response, ResponseResultMessage result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        String requestId = MDC.get(Constants.REQUEST_ID);
        if(StringUtils.isBlank(requestId)){
            requestId = UUIDUtils.get32UUID();
        }
        result.setRequestId(requestId);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            log.error(ex.getMessage(),ex);
        }
    }

    private void responseExceptionResult(HttpServletResponse response, ResponseResult result) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        String requestId = MDC.get(Constants.REQUEST_ID);
        if(StringUtils.isBlank(requestId)){
            requestId = UUIDUtils.get32UUID();
        }
        result.setRequestId(requestId);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            log.error(ex.getMessage(),ex);
        }
    }

    /**
     * 转换异常信息为字符串
     *
     * @param exceptionName    异常名称
     * @param exceptionMessage 异常信息
     * @param elements         堆栈信息
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuilder strbuff = new StringBuilder();
        for (StackTraceElement stet : elements) {
            strbuff.append(stet).append("\n");
        }
        return exceptionName + ":" + exceptionMessage + "\n\t" + strbuff.toString();
    }

}
