package com.coolcollege.intelligent.service.video.openapi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.ImouHttpClient;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.imou.request.ImouBaseRequest;
import com.coolcollege.intelligent.model.video.platform.imou.request.ImouSystemDTO;
import com.coolcollege.intelligent.model.video.platform.imou.response.ImouCaptureResponse;
import com.coolcollege.intelligent.model.video.platform.imou.response.ImouResultDTO;
import com.coolcollege.intelligent.model.video.platform.imou.response.TokenResponse;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.openapi.VideoOpenService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

/**
 * describe:乐橙
 *
 * @author zhouyiping
 * @date 2022/04/24
 */
@Service
@Slf4j
public class LeChengDeviceServiceImpl implements  VideoOpenService {
//   ImouDeviceService,

    @Value("${imou.url}")
    private String imouUrl;

    @Value("${imou.auth.url}")
    private String imouAuthUrl;

    @Value("${imou.app.id}")
    private String imouAppId;

    @Value("${imou.app.secret}")
    private String imouAppSecret;

    private final static String IMOU_ACCESS_TOKEN_KEY = "imou_access_token_key";
    private final static String IMOU_DEVICE_KIT_TOKEN_KEY = "imou_device_kit_token_key_";


    private final static Integer EXPIRE_TIME = 86400;

    private final static Integer KIT_EXPIRE_TIME = 3600;

    @Resource
    private RedisUtilPool redis;

    @Resource
    private EnterpriseVideoSettingService enterpriseVideoSettingService;

    private String getImouToken(String appId,String appSecret) {
        String tokenUrl = imouUrl + "/openapi/accessToken";
        ImouBaseRequest request = new ImouBaseRequest();
        Map<String, Object> params = new HashMap<>();
        request.setParams(params);
        sign(request,appId,appSecret);
        String resultStr = ImouHttpClient.post(tokenUrl, JSON.toJSONString(request));
        ImouResultDTO<TokenResponse> imouDataBaseResponseImouResultDTO = JSON.parseObject(resultStr, new TypeReference<ImouResultDTO<TokenResponse>>() {
        });
        String accessToken = imouDataBaseResponseImouResultDTO.getData().getAccessToken();
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        throw new ServiceException(ErrorCodeEnum.LECHENG_DEVICE_7400002);
    }

    private ImouBaseRequest sign(ImouBaseRequest request,String appId,String appSecret) {

        long timestamp = System.currentTimeMillis() / 1000;
        String nonce = UUIDUtils.get32UUID();
        String str = "time:" + timestamp + ",nonce:" + nonce + ",appSecret:" + appSecret;
        String sign = MD5Util.md5(str);
        ImouSystemDTO imouSystemDTO = new ImouSystemDTO();
        imouSystemDTO.setVer("1.0");
        imouSystemDTO.setSign(sign);
        imouSystemDTO.setAppId(appId);
        imouSystemDTO.setTime(Long.toString(timestamp));
        imouSystemDTO.setNonce(nonce);
        request.setSystem(imouSystemDTO);
        String id = UUIDUtils.get32UUID();
        request.setId(id);
        return request;
    }

    @Override
    public YunTypeEnum getYunTypeNum() {
        return YunTypeEnum.IMOU;
    }

    @Override
    public String getAccessToken(String enterpriseId, AccountTypeEnum accountType) {
        String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, enterpriseId, accountType.getCode(), getYunTypeNum().getCode());
        String accessToken = redis.getString(cacheKey);
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        String appKey = null, appSecret = null;
        if(AccountTypeEnum.PLATFORM.equals(accountType)){
            appKey = imouAppId;
            appSecret = imouAppSecret;
        }else{
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(enterpriseId, getYunTypeNum().getCode(), accountType.getCode());
            appKey = videoSetting.getAccessKeyId();
            appSecret = videoSetting.getSecret();
            DataSourceHelper.changeToSpecificDataSource(dbName);
        }
        String imouToken = getImouToken(appKey, appSecret);
        redis.setString(cacheKey, imouToken, EXPIRE_TIME);
        return imouToken;
    }

    @Override
    public OpenDeviceDTO getDeviceDetail(String enterpriseId, String deviceId, AccountTypeEnum accountType) {
        String url = imouUrl + "/openapi/listDeviceDetailsByIds";
        ImouBaseRequest request = new ImouBaseRequest();
        Map<String, Object> params = new HashMap<>();
        params.put("token", getAccessToken(enterpriseId, accountType));
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        params.put("deviceList", Arrays.asList(data));
        request.setParams(params);
        String appKey = null, appSecret = null;
        if(AccountTypeEnum.PLATFORM.equals(accountType)){
            appKey = imouAppId;
            appSecret = imouAppSecret;
        }else{
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(enterpriseId, getYunTypeNum().getCode(), accountType.getCode());
            appKey = videoSetting.getAccessKeyId();
            appSecret = videoSetting.getSecret();
            DataSourceHelper.changeToSpecificDataSource(dbName);
        }
        sign(request, appKey, appSecret);
        String resultStr = ImouHttpClient.post(url, JSON.toJSONString(request));
        JSONObject response = JSONObject.parseObject(resultStr);
        JSONArray list = response.getJSONObject("data").getJSONArray("deviceList");
        OpenDeviceDTO openDeviceDTO = new OpenDeviceDTO();
        if (CollectionUtils.isEmpty(list)){
            return openDeviceDTO;
        }
        JSONObject deviceInfo =list.getJSONObject(0);
        openDeviceDTO.setDeviceId(deviceInfo.getString("deviceId"));
        openDeviceDTO.setDeviceName(deviceInfo.getString("deviceName"));
        openDeviceDTO.setDataSourceId(deviceInfo.getString("productId"));
        openDeviceDTO.setDeviceStatus(DeviceStatusEnum.ONLINE.getCode().equals(deviceInfo.getString("deviceStatus")) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
        openDeviceDTO.setSource(getYunTypeNum().getCode());
        DeviceCapacityDTO deviceCapacity = DeviceCapacityDTO.convertLeChengDeviceCapacity(deviceInfo.getString("deviceAbility"));
        openDeviceDTO.setSupportCapture(deviceCapacity.getSupportCapture());
        openDeviceDTO.setHasPtz(YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzLeftRight()));
        openDeviceDTO.setDeviceCapacity(deviceCapacity);
        //非ipc才去拿通道
        String catalog = deviceInfo.getString("catalog");
        if (StringUtils.isNotBlank(catalog)&& !"IPC".equals(catalog)){
            List<JSONObject> channelList = deviceInfo.getJSONArray("channelList").toJavaList(JSONObject.class);
            List<OpenChannelDTO> channelDTOS = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(channelList)){
                channelList.stream().forEach(c->{
                    OpenChannelDTO openChannelDTO = new OpenChannelDTO();
                    openChannelDTO.setDeviceId(deviceInfo.getString("deviceId"));
                    openChannelDTO.setChannelNo(c.getString("channelId"));
                    openChannelDTO.setChannelId(c.getString("channelId"));
                    openChannelDTO.setChannelName(c.getString("channelName"));
                    openChannelDTO.setParentDeviceId(deviceInfo.getString("deviceId"));
                    openChannelDTO.setSource(getYunTypeNum().getCode());
                    openChannelDTO.setHasPtz(false);
                    openChannelDTO.setStatus(DeviceStatusEnum.ONLINE.getCode().equals(c.getString("channelStatus")) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                    openChannelDTO.setSupportCapture(deviceCapacity.getSupportCapture());
                    openChannelDTO.setHasPtz(YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzLeftRight()));
                    openChannelDTO.setDeviceCapacity(deviceCapacity);
                    channelDTOS.add(openChannelDTO);
                });
                openDeviceDTO.setChannelList(channelDTOS);
            }
        }
        return openDeviceDTO;
    }

    @Override
    public PageInfo<OpenDevicePageDTO> getDeviceList(String enterpriseId, AccountTypeEnum accountType, Integer pageNum, Integer pageSize) {
        String url = imouUrl + "/openapi/listDeviceDetailsByPage";
        ImouBaseRequest request = new ImouBaseRequest();
        Map<String, Object> params = new HashMap<>();
        params.put("token", getAccessToken(enterpriseId, accountType));
        params.put("page", pageNum);
        params.put("pageSize", pageSize);
        request.setParams(params);
        String appKey = null, appSecret = null;
        if(AccountTypeEnum.PLATFORM.equals(accountType)){
            appKey = imouAppId;
            appSecret = imouAppSecret;
        }else{
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(enterpriseId, getYunTypeNum().getCode(), accountType.getCode());
            appKey = videoSetting.getAccessKeyId();
            appSecret = videoSetting.getSecret();
            DataSourceHelper.changeToSpecificDataSource(dbName);
        }
        sign(request, appKey, appSecret);
        String resultStr = ImouHttpClient.post(url, JSON.toJSONString(request));
        List<OpenDevicePageDTO> openDevicePageDTOS=new ArrayList<>();
        if (StringUtils.isNotBlank(resultStr)){
            JSONObject data = JSONObject.parseObject(resultStr).getJSONObject("data");
            List<JSONObject> deviceList = data.getJSONArray("deviceList").toJavaList(JSONObject.class);
            if (CollectionUtils.isNotEmpty(deviceList)){
                deviceList.stream().forEach(device->{
                    OpenDevicePageDTO devicePageDTO = new OpenDevicePageDTO();
                    devicePageDTO.setDeviceId(device.getString("deviceId"));
                    devicePageDTO.setDeviceName(device.getString("deviceName"));
                    devicePageDTO.setHasChildDevice(false);
                    devicePageDTO.setSource(YunTypeEnum.ULUCU.getCode());
                    devicePageDTO.setUseStoreId(device.getString("userStoreId"));
                    String status = DeviceStatusEnum.ONLINE.getCode().equals(device.getString("deviceStatus")) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode();
                    devicePageDTO.setDeviceStatus(status);
                    openDevicePageDTOS.add(devicePageDTO);
                });
            }
        }
        PageInfo<OpenDevicePageDTO> pageInfo = new PageInfo<>();
        pageInfo.setList(openDevicePageDTOS);
        return pageInfo;
    }

    @Override
    public LiveVideoVO getLiveUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        LiveVideoVO pastVideoUrl = getPastVideoUrl(enterpriseId, device, param);
        return pastVideoUrl;
    }

    @Override
    public LiveVideoVO getPastVideoUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        String channelNo = param.getChannelNo(), startTime = param.getStartTime(), endTime = param.getEndTime();
        //先查询有没有直播地址
        String queryUrl=imouUrl+"/openapi/queryDeviceFlvLive";
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String token = getAccessToken(enterpriseId, accountType);
        ImouBaseRequest queryRequest = new ImouBaseRequest();
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("token", token);
        queryParams.put("deviceId", device.getDeviceId());
        queryParams.put("channelId", channelNo);
        queryRequest.setParams(queryParams);
        String appKey = null, appSecret = null;
        if(AccountTypeEnum.PLATFORM.equals(accountType)){
            appKey = imouAppId;
            appSecret = imouAppSecret;
        }else{
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(enterpriseId, getYunTypeNum().getCode(), accountType.getCode());
            appKey = videoSetting.getAccessKeyId();
            appSecret = videoSetting.getSecret();
            DataSourceHelper.changeToSpecificDataSource(dbName);
        }
        sign(queryRequest, appKey, appSecret);
        String resultStrForQuery="";
        try{
            resultStrForQuery = ImouHttpClient.post(queryUrl, JSON.toJSONString(queryRequest));
        }catch (Exception e){
            log.error("获取设备直播地址失败，请求参数：{}，返回结果：{}", JSON.toJSONString(queryRequest), resultStrForQuery);
        }
        if (StringUtils.isNotBlank(resultStrForQuery)){
            JSONObject data = JSONObject.parseObject(resultStrForQuery).getJSONObject("data");
            if (data!=null&&data.getString("flvHD")!=null){
                LiveVideoVO liveVideoVO = new LiveVideoVO();
                liveVideoVO.setUrl(data.getString("flvHD"));
                liveVideoVO.setToken(token);
                return liveVideoVO;
            }
        }
        //创建设备flv直播地址
        String url=imouUrl+"/openapi/createDeviceFlvLive";
        ImouBaseRequest request = new ImouBaseRequest();
        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("deviceId", device.getDeviceId());
        params.put("channelId", channelNo);
        if (StringUtils.isNotBlank(startTime)&& StringUtils.isNotBlank(endTime)){
            params.put("type","playback");
            params.put("startTime",startTime);
            params.put("endTime",endTime);
            params.put("recordType","localRecord");
        }
        request.setParams(params);
        sign(request, appKey, appSecret);
        String resultStr = ImouHttpClient.post(url, JSON.toJSONString(request));
        String flvUrl = JSONObject.parseObject(resultStr).getJSONObject("data").getString("flvHD");
        LiveVideoVO liveVideoVO = new LiveVideoVO();
        liveVideoVO.setUrl(flvUrl);
        liveVideoVO.setToken(token);
        return liveVideoVO;
    }

    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {

    }

    @Override
    public Boolean ptzStart(String enterpriseId, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration) {
        return true;
    }

    @Override
    public Boolean ptzStop(String enterpriseId, DeviceDO device, String channelNo) {
        return true;
    }

    @Override
    public String addPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String devicePositionName) {
        return null;
    }

    @Override
    public Boolean deletePtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        return Boolean.TRUE;
    }

    @Override
    public Boolean loadPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        return Boolean.TRUE;
    }

    @Override
    public String capture(String eid, DeviceDO device, String channelNo, String quality) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String url = imouUrl + "/openapi/setDeviceSnapEnhanced";
        ImouBaseRequest request = new ImouBaseRequest();
        Map<String, Object> params = new HashMap<>();
        params.put("token", getAccessToken(eid,accountType));
        params.put("deviceId", device.getDeviceId());
        params.put("channelId", StringUtils.isBlank(channelNo)?"0":channelNo);
        request.setParams(params);
        String appKey = null, appSecret = null;
        if(AccountTypeEnum.PLATFORM.equals(accountType)){
            appKey = imouAppId;
            appSecret = imouAppSecret;
        }else{
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(eid, getYunTypeNum().getCode(), accountType.getCode());
            appKey = videoSetting.getAccessKeyId();
            appSecret = videoSetting.getSecret();
            DataSourceHelper.changeToSpecificDataSource(dbName);
        }
        sign(request, appKey, appSecret);
        String resultStr = ImouHttpClient.post(url, JSON.toJSONString(request));
        ImouResultDTO<ImouCaptureResponse> captureResponse = JSON.parseObject(resultStr, new TypeReference<ImouResultDTO<ImouCaptureResponse>>() {
        });
        return captureResponse.getData().getUrl();
    }

    @Override
    public String videoTransCode(String enterpriseId, DeviceDO device, VideoDTO param) {
        return null;
    }

    @Override
    public VideoFileDTO getVideoFile(String enterpriseId, DeviceDO device, String fileId) {
        return null;
    }

    @Override
    public List<String> getVideoDownloadUrl(String enterpriseId, DeviceDO device, String fileId) {
        return null;
    }

}
