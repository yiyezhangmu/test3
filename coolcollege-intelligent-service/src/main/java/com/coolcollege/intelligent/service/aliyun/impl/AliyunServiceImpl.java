package com.coolcollege.intelligent.service.aliyun.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.vcs.model.v20200515.*;
import com.aliyuncs.vcs.model.v20200515.GetDeviceLiveUrlRequest;
import com.aliyuncs.vcs.model.v20200515.GetDeviceLiveUrlResponse;
import com.aliyuncs.vcs.model.v20200515.GetDeviceVideoUrlRequest;
import com.aliyuncs.vcs.model.v20200515.GetDeviceVideoUrlResponse;
import com.aliyuncs.vcs.model.v20200515.ListEventAlgorithmResultsRequest;
import com.aliyuncs.vcs.model.v20200515.ListEventAlgorithmResultsResponse;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.model.aliyun.dto.AliyunEventDTO;
import com.coolcollege.intelligent.model.device.dto.VideoDTO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.service.aliyun.AliyunService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.util.AliyunUtilVideo;
import com.coolcollege.intelligent.model.aliyun.response.*;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author 邵凌志
 */
@Service
@Slf4j
public class AliyunServiceImpl implements AliyunService {

    @Value("${aliyun.api.video.keyId}")
    private String keyId;

    @Value("${aliyun.api.video.keySecret}")
    private String keySecret;

    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;


    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 阿里云接口成功返回码
     */
    protected static final String OK = "200";


    @Override
    public String getLiveUrl(VideoDTO videoDTO) {
        // 获取实时视频
        GetDeviceLiveUrlRequest request = new GetDeviceLiveUrlRequest();
        request.setCorpId(videoDTO.getCorpId());
        request.setGbId(videoDTO.getDeviceId());
        // 设为realtime，接入新平台的设备就会返回flv流，其他返回hls。当前只有拉比那路在新系统
        // 设为realtimesec，接入新平台的设备就会返回httpsflv流，其他返回httpshls
        request.setOutProtocol("httpshls");
        request.setStreamType(1);
        GetDeviceLiveUrlResponse response = (GetDeviceLiveUrlResponse) AliyunUtilVideo.handleRequest(request, keyId, keySecret);
        if (!response.getCode().equals(OK)) {
            log.error("aliyun直播错误,request={},result={}" ,request,JSONObject.toJSON(response));
            throw new ServiceException(ErrorCodeEnum.VIDEO_SERVER.getCode(), response.getMessage());
        }
        return response.getUrl();
    }

    @Override
    public String getPastVideoUrl(VideoDTO videoDTO) {
        // 获取往日视频
        GetDeviceVideoUrlRequest localRequest = new GetDeviceVideoUrlRequest();
        localRequest.setCorpId(videoDTO.getCorpId());
        localRequest.setGbId(videoDTO.getDeviceId());
        Long startTime = DateUtils.convertStringToLong(videoDTO.getStartTime());
        localRequest.setStartTime(startTime);
        Long endTime = DateUtils.convertStringToLong(videoDTO.getEndTime());
        localRequest.setEndTime(endTime);
        localRequest.setStorageType("1");
        localRequest.setOutProtocol("httpshls");
        //回放的时候默认获取本地存储 没有本地存储改为云存
        GetDeviceVideoUrlResponse localResponse = (GetDeviceVideoUrlResponse) AliyunUtilVideo.handleRequest(localRequest, keyId, keySecret);
        if (!localResponse.getCode().equals(OK)) {
            GetDeviceVideoUrlRequest yunRequest = new GetDeviceVideoUrlRequest();
            yunRequest.setCorpId(videoDTO.getCorpId());
            yunRequest.setGbId(videoDTO.getDeviceId());
            yunRequest.setStartTime( DateUtils.convertStringToLong(videoDTO.getStartTime()));
            yunRequest.setEndTime(DateUtils.convertStringToLong(videoDTO.getEndTime()));
            yunRequest.setStorageType("0");
            yunRequest.setOutProtocol("httpshls");
            GetDeviceVideoUrlResponse yunResponse = (GetDeviceVideoUrlResponse) AliyunUtilVideo.handleRequest(yunRequest, keyId, keySecret);
            if (yunResponse.getCode().equals(OK)) {
                return yunResponse.getUrl();
            }
            log.error("阿里云云存储回放返回结果：{},本地存储返回结果：{}", JSON.toJSONString(yunResponse), JSON.toJSONString(localResponse));
            throw new ServiceException(ErrorCodeEnum.VIDEO_SERVER.getCode(),"没有获取到本地存储和云存储中的数据！");
        }
        return localResponse.getUrl();
    }

    @Override
    public Object listEventAlgorithm(String corpId, AliyunEventDTO aliyunEventDTO) {
        ListEventAlgorithmResultsRequest request = new ListEventAlgorithmResultsRequest();
        request.setCorpId(corpId);
        request.setEventType(aliyunEventDTO.getEventType());
        request.setPageNumber(String.valueOf(aliyunEventDTO.getPageNumber()));
        request.setPageSize(String.valueOf(aliyunEventDTO.getPageSize()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        request.setStartTime(sdf.format(new Date(aliyunEventDTO.getStartTime())));
        request.setEndTime(sdf.format(new Date(aliyunEventDTO.getEndTime())));
        request.setDataSourceId(aliyunEventDTO.getDataSourceId());

        ListEventAlgorithmResultsResponse response = (ListEventAlgorithmResultsResponse) AliyunUtilVideo.handleRequest(request, keyId, keySecret);
        if (!response.getCode().equals(OK)) {
            log.error("阿里云事件请求参数：{},返回结果：{}",JSON.toJSON(request), JSON.toJSONString(response));
            throw new ServiceException(ErrorCodeEnum.VIDEO_RETURN.getCode(), response.getMessage());
        }
        return response.getData();
    }


    @Override
    public String createVdsProject(String projectName) {

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2020-11-01");
        request.setSysAction("CreateProject");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("Name", projectName);
        //"{\"CorpId\":\"cdrs3889210348936450\",\"RequestId\":\"E268A09F-E54D-4068-A06A-92890C00587F\",\"Message\":\"success\",\"Code\":\"SUCCESS\"}"
        String result = AliyunUtilVideo.handleCommonRequest(request, keyId, keySecret);
        VdsCreateResponse vdsCreateResponse = JSONObject.parseObject(result, VdsCreateResponse.class);
        if("SUCCESS".equals(vdsCreateResponse.getCode())){
            return vdsCreateResponse.getCorpId();
        }
        log.error("创建VDS项目出错:result={}",result);
        throw new ServiceException(ErrorCodeEnum.VIDEO_SERVER);

    }

    @Override
    public List<VdsBindDeviceResponse> bindDeviceToVds(String eid,String vcsCorpId,String vdsCorpId,List<String>deviceList) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2020-11-01");
        request.setSysAction("BindDevice");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("CorpId", vdsCorpId);
        for (int i = 1; i <=deviceList.size() ; i++) {
            request.putQueryParameter("Devices."+i+".CorpId", vcsCorpId);
            request.putQueryParameter("Devices."+i+".DeviceId", deviceList.get(i-1));
        }
        String result = AliyunUtilVideo.handleCommonRequest(request, keyId, keySecret);
        VdsResponse<List<VdsBindDeviceResponse>> response = JSONObject.parseObject(result, new TypeReference<VdsResponse<List<VdsBindDeviceResponse>>>(){});
        if("SUCCESS".equals(response.getCode())){
            return response.getData();
        }
        return null;
    }

    @Override
    public List<VdsBindDeviceResponse> unbindDeviceToVds(String vdsCorpId,List<String>deviceList) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2020-11-01");
        request.setSysAction("UnBindDevice");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("CorpId", vdsCorpId);
        request.putQueryParameter("DeviceIds",deviceList.stream().collect(Collectors.joining(",")));
        String result = AliyunUtilVideo.handleCommonRequest(request, keyId, keySecret);
        VdsResponse<List<VdsBindDeviceResponse>> response = JSONObject.parseObject(result, new TypeReference<VdsResponse<List<VdsBindDeviceResponse>>>(){});
        if("SUCCESS".equals(response.getCode())){
            return response.getData();
        }
        return null;
    }

    @Override
    public  VdsPageResponse<VdsProjectInfo>  paginateProject(String name ,String type,Boolean countTotalNum,Integer pageNumber ,Integer pageSize) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2020-11-01");
        request.setSysAction("PaginateProject");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        if(pageNumber!=null){
            request.putQueryParameter("PageNumber", pageNumber.toString());
        }
        if(pageSize!=null){
            request.putQueryParameter("PageSize", pageSize.toString());
        }
        if(countTotalNum!=null){
            request.putQueryParameter("CountTotalNum", countTotalNum.toString());
        }
        if(StringUtils.isNotBlank(type)){
            request.putQueryParameter("Type", type);
        }
        if(StringUtils.isNotBlank(name)){
            request.putQueryParameter("NameLike", name);
        }
        String result = AliyunUtilVideo.handleCommonRequest(request, keyId, keySecret);
        VdsResponse<VdsPageResponse<VdsProjectInfo>> response = JSONObject.parseObject(result, new TypeReference<VdsResponse<VdsPageResponse<VdsProjectInfo>>>(){});
        if("SUCCESS".equals(response.getCode())){
            return response.getData();
        }
        return new VdsPageResponse();
    }

    @Override
    public List<VdsCorpResponse> listDeviceRelation(String deviceId) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2020-11-01");
        request.setSysAction("ListDeviceRelation");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        if(StringUtils.isNotBlank(deviceId)){
            request.putQueryParameter("DeviceId", deviceId);
        }

        String result = AliyunUtilVideo.handleCommonRequest(request, keyId, keySecret);
        VdsResponse<List<VdsCorpResponse>> response = JSONObject.parseObject(result, new TypeReference<VdsResponse<List<VdsCorpResponse>>>(){});
        if("SUCCESS".equals(response.getCode())){
            return response.getData();
        }
        return null;
    }

    @Override
    public VdsPageResponse<VdsDeviceResponse> paginateDevice(String corpId, Integer pageNumber, Integer pageSize, Boolean countTotalNum) {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2020-11-01");
        request.setSysAction("PaginateDevice");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        if(pageNumber!=null){
            request.putQueryParameter("PageNumber", pageNumber.toString());
        }
        if(pageSize!=null){
            request.putQueryParameter("PageSize", pageSize.toString());
        }
        if(countTotalNum!=null){
            request.putQueryParameter("CountTotalNum", countTotalNum.toString());
        }
        if(StringUtils.isNotBlank(corpId)){
            request.putQueryParameter("CorpId", corpId);
        }

        String result = AliyunUtilVideo.handleCommonRequest(request, keyId, keySecret);
        VdsResponse<VdsPageResponse<VdsDeviceResponse>> response = JSONObject.parseObject(result,
                new TypeReference<VdsResponse<VdsPageResponse<VdsDeviceResponse>>>(){});
        if("SUCCESS".equals(response.getCode())){
            return response.getData();
        }
        return new VdsPageResponse();
    }

    @Override
    public  VdsPersonResultResponse<VdsPersonInfo> listPersonResult(String corpId, Long startTime, Long endTime, Integer pageNumber, Integer pageSize) {

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("cdrs.cn-hangzhou.aliyuncs.com");
        request.setSysVersion("2020-11-01");
        request.setSysAction("listPersonResult");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("Schema","vds_interior");
        if(pageNumber!=null){
            request.putQueryParameter("PageNumber", pageNumber.toString());
        }
        if(pageSize!=null){
            request.putQueryParameter("PageSize", pageSize.toString());
        }
        if(StringUtils.isNotBlank(corpId)){
            request.putQueryParameter("CorpId", corpId);
        }
        if(startTime!=null){
            String format = SDF.format(startTime);
            request.putQueryParameter("StartTime",format);
        }
        if(endTime!=null){
            String format = SDF.format(endTime);
            request.putQueryParameter("EndTime",format);
        }
        String result = AliyunUtilVideo.handleCommonRequest(request, keyId, keySecret);
        VdsPersonResultResponse<VdsPersonInfo> response = JSONObject.parseObject(result,
                new TypeReference< VdsPersonResultResponse<VdsPersonInfo>>(){});

        if(OK.equals(response.getCode())){
            return response;
        }
        return null;
    }

    @Override
    public String createDataSource(String eid) {
        AddDataSourceRequest request =new AddDataSourceRequest();
        request.setCorpId(getVcsCorpId(eid));
        request.setDataSourceName("PicDataSource");
        request.setDataSourceType("PIC");
        request.setDescription("yonghui");
        AddDataSourceResponse addDataSourceResponse = (AddDataSourceResponse) AliyunUtilVideo.handleRequest(request, keyId, keySecret);
        if(OK.equals(addDataSourceResponse.getCode())){
            return addDataSourceResponse.getData().getDataSourceId();
        }
        return null;
    }

    @Override
    public String getVcsCorpId(String eid) {
        SettingVO settingIncludeNull = enterpriseVideoSettingService.getSettingIncludeNull(eid, YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
       if(settingIncludeNull==null){
           return null;
       }
        return settingIncludeNull.getAliyunCorpId();
    }



}
