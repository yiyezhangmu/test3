package com.coolcollege.intelligent.service.aliyun.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.http.MethodType;
import com.coolcollege.intelligent.service.aliyun.AliyunVdsMonitorService;
import com.coolcollege.intelligent.util.AliyunUtil;
import com.coolcollege.intelligent.model.aliyun.response.monitor.VdsAddMonitorInfo;
import com.coolcollege.intelligent.model.aliyun.response.VdsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/19
 */
@Service
@Slf4j
public class AliyunVdsMonitorServiceImpl implements AliyunVdsMonitorService {

    @Value("${aliyun.api.video.keyId}")
    private String keyId;

    @Value("${aliyun.api.video.keySecret}")
    private String keySecret;

    @Value("${scheduler.callback.task.url}")
    private String appUrl;

    private final static String VDS_OK="200";
    @Override
    public String addCdrsMonitor(String corpId) {

//        CommonRequest request = new CommonRequest();
//        request.setSysMethod(MethodType.POST);
//        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
//        request.setSysVersion("2020-11-01");
//        request.setSysAction("AddMonitor");
//        request.putQueryParameter("RegionId", "cn-hangzhou");
//
//        if(StringUtils.isNotBlank(corpId)){
//            request.putQueryParameter("CorpId", corpId);
//        }
//        request.putQueryParameter("MonitorType", "face");
//        request.putQueryParameter("AlgorithmVendor", "damo");
//
//        String result = AliyunUtil.handleCommonRequest(request, keyId, keySecret);
//        VdsResponse<VdsAddMonitorInfo> response = JSONObject.parseObject(result,
//                new TypeReference< VdsResponse<VdsAddMonitorInfo>>(){});
//
//        if(VDS_OK.equals(response.getCode())&&response.getData()!=null){
//            return response.getData().getTaskId();
//        }
        return null;
    }

    @Override
    public Object updateCdrsMonitor(String enterpriseId,String corpId, String customerId,String taskId,String picOperateType, List<String> picList) {
//        CommonRequest request = new CommonRequest();
//        request.setSysMethod(MethodType.POST);
//        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
//        request.setSysVersion("2020-11-01");
//        request.setSysAction("UpdateMonitor");
//        request.putQueryParameter("RegionId", "cn-hangzhou");
//
//        if(StringUtils.isNotBlank(corpId)){
//            request.putQueryParameter("CorpId", corpId);
//        }
//        request.putQueryParameter("MonitorType", "face");
//        request.putQueryParameter("AlgorithmVendor", "damo");
//        request.putQueryParameter("TaskId", taskId);
//        request.putQueryParameter("AlgorithmVendor", "damo");
//        request.putQueryParameter("RuleName", "black_image_match_10634");
//        request.putQueryParameter("PicOperateType", picOperateType);
//        request.putQueryParameter("PicList", picList.stream().collect(Collectors.joining(",")));
//        request.putQueryParameter("NotifierType", "webhook");
//
//        request.putQueryParameter("NotifierUrl", appUrl+"/v3/enterprises/aliyun/vds/"+enterpriseId+
//                "/webhook/callback"+"/"+customerId);
//
//        request.putQueryParameter("NotifierTimeOut", "3000");
//        String result = AliyunUtil.handleCommonRequest(request, keyId, keySecret);
//        VdsResponse<String> response = JSONObject.parseObject(result,
//                new TypeReference< VdsResponse<String>>(){});
//
//        if(VDS_OK.equals(response.getCode())&&response.getData()!=null){
//            return true;
//        }
        return null;
    }

    @Override
    public Object stopCdrsMonitor(String taskId,String corpId) {
//        CommonRequest request = new CommonRequest();
//        request.setSysMethod(MethodType.POST);
//        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
//        request.setSysVersion("2020-11-01");
//        request.setSysAction("StopMonitor");
//        request.putQueryParameter("RegionId", "cn-hangzhou");
//        request.putQueryParameter("TaskId", taskId);
//        request.putQueryParameter("AlgorithmVendor","damo");
//        request.putQueryParameter("CorpId", corpId);
//        String result = AliyunUtil.handleCommonRequest(request, keyId, keySecret);
//        VdsResponse<String> response = JSONObject.parseObject(result,
//                new TypeReference< VdsResponse<String>>(){});
//
//        if(VDS_OK.equals(response.getCode())&&response.getData()!=null){
//            return true;
//        }
        return null;
    }
}
