package com.coolcollege.intelligent.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 用AliyunUtil 視頻專用
 */
@Slf4j
public class AliyunUtilVideo {

    /**
     * 待改造线程池
     * 线程池不允许使用Executors去创建，而是通过ThreadPoolExecutor的方式，这样的处理方式让写的同学更加明确线程池的运行规则，
     * 规避资源耗尽的风险。 说明：Executors返回的线程池对象的弊端如下：
     * 1）FixedThreadPool和SingleThreadPool:
     *   允许的请求队列长度为Integer.MAX_VALUE，可能会堆积大量的请求，从而导致OOM。
     * 2）CachedThreadPool:
     *   允许的创建线程数量为Integer.MAX_VALUE，可能会创建大量的线程，从而导致OOM。
     */
    private static ExecutorService executorService = new ThreadPoolExecutor(20, 50, 60L, TimeUnit.SECONDS,  new LinkedBlockingQueue<Runnable>(100));

    private volatile static IAcsClient client;

    private AliyunUtilVideo(){}

    /**
     * 阿里云接口异常统一处理
     *
     * @param request
     * @param keyId
     * @param keySecret
     * @return
     */
    public static AcsResponse handleRequest(RpcAcsRequest request, String keyId, String keySecret) {

        AliyunUtilProfileVideo profile = AliyunUtilProfileVideo.getProfile("cn-hangzhou", keyId, keySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        AcsResponse acsResponse=null;
        try {
            acsResponse = client.getAcsResponse(request);
            return acsResponse;
        } catch (ServerException e) {
            log.error("阿里云服务端报错,所有阿里云请求入参：{},阿里云请求回参：{}", JSON.toJSON(request), JSONObject.toJSON(acsResponse),e);
            throw new ServiceException(ErrorCodeEnum.VIDEO_SERVER.getCode(), ErrorCodeEnum.VIDEO_SERVER.getMessage() + e.getMessage());
        } catch (ClientException e) {
            log.error("阿里云客户端报错,所有阿里云请求入参：{},阿里云请求回参：{}", JSON.toJSON(request), JSONObject.toJSON(acsResponse),e);
            throw new ServiceException(ErrorCodeEnum.VIDEO_CLIENT.getCode(), ErrorCodeEnum.VIDEO_CLIENT.getMessage() + e.getErrCode());
        }
    }

    public static String handleCommonRequest(CommonRequest request, String keyId, String keySecret) {

//        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", keyId, keySecret);
        IAcsClient client = getSingleClient(keyId, keySecret);
        CommonResponse commonResponse=null;
        try {
            log.info("阿里云common请求入参：{}", JSON.toJSON(request));
            commonResponse= client.getCommonResponse(request);
            log.info("阿里云common请求回参：{}", commonResponse.getData());
            return commonResponse.getData();
        } catch (ServerException e) {
            log.error("阿里云common服务端报错,所有阿里云请求入参：{},阿里云请求回参：{}", JSON.toJSON(request), JSONObject.toJSON(commonResponse),e);
            throw new ServiceException(ErrorCodeEnum.VIDEO_SERVER.getCode(), ErrorCodeEnum.VIDEO_SERVER.getMessage() + e.getMessage());
        } catch (ClientException e) {
            log.error("阿里云common客户端报错,所有阿里云请求入参：{},阿里云请求回参：{}", JSON.toJSON(request), JSON.toJSON(commonResponse),e);
            throw new ServiceException(ErrorCodeEnum.VIDEO_CLIENT.getCode(), ErrorCodeEnum.VIDEO_CLIENT.getMessage() + e.getErrCode());
        }
    }

    public static Future submit(Callable task) {
        return executorService.submit(task);
    }


    /**
     * 获取阿里视觉服务连接客户端
     * @param keyId
     * @param keySecret
     * @return
     */
    public static IAcsClient getSingleClient(String keyId, String keySecret) {
        AliyunUtilProfileVideo profile = AliyunUtilProfileVideo.getProfile("cn-hangzhou", keyId, keySecret);
        if (client == null) {
            synchronized (AliyunUtilVideo.class) {
                if (client == null) {
                    client = new DefaultAcsClient(profile);
                }
            }
        }
        return client;
    }

    public static IAcsClient getClient(String keyId, String keySecret) {
        AliyunUtilProfileVideo profile = AliyunUtilProfileVideo.getProfile("cn-hangzhou", keyId, keySecret);
        return new DefaultAcsClient(profile);
    }

}
