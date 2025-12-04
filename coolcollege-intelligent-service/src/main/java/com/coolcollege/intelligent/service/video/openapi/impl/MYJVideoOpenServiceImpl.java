package com.coolcollege.intelligent.service.video.openapi.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.common.response.YunShiTongResponse;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.AppKeyDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudAreasDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.PassengerDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowConfigDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowSwitchStatusDTO;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.openapi.VideoOpenService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.VideoProtocolTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: MYJVideoOpenServiceImpl
 * @Description:美宜佳视频对接
 * @date 2023-10-10 10:23
 */
@Slf4j
@Service
public class MYJVideoOpenServiceImpl  implements VideoOpenService {

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Autowired
    private HttpRestTemplateService httpRestTemplateService;

    private static final String OPEN_URL = "https://open.myj.com.cn";

    @Override
    public YunTypeEnum getYunTypeNum() {
        return YunTypeEnum.MYJ;
    }

    public Pair<String, String> getVideoSetting(String enterpriseId, AccountTypeEnum accountType) {
        String cacheKey = MessageFormat.format(RedisConstant.VIDEO_SETTING_KEY, enterpriseId, accountType.getCode(), getYunTypeNum().getCode());
        String value = redisUtilPool.getString(cacheKey);
        if (StringUtils.isNotBlank(value)) {
            EnterpriseVideoSettingDTO enterpriseVideoSetting = JSONObject.parseObject(value, EnterpriseVideoSettingDTO.class);
            return Pair.of(enterpriseVideoSetting.getAccessKeyId(), enterpriseVideoSetting.getSecret());
        }
        String dataSourceType = DynamicDataSourceContextHolder.getDataSourceType();
        EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(enterpriseId, getYunTypeNum().getCode(), accountType.getCode());
        if (videoSetting == null) {
            throw new ServiceException(ErrorCodeEnum.HIK_CLOUD_ACCESS_TOKEN_GET_ERROR);
        }
        DataSourceHelper.changeToSpecificDataSource(dataSourceType);
        redisUtilPool.setString(cacheKey, JSONObject.toJSONString(videoSetting), 10 * 60);
        return Pair.of(videoSetting.getAccessKeyId(), videoSetting.getSecret());
    }

    @Override
    public String getAccessToken(String enterpriseId, AccountTypeEnum accountType) {
        String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, enterpriseId, accountType.getCode(), getYunTypeNum().getCode());
        String accessToken = redisUtilPool.getString(cacheKey);
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        String appKey = null, appSecret = null;
        Pair<String, String> videoSetting = getVideoSetting(enterpriseId, accountType);
        appKey = videoSetting.getKey();
        appSecret = videoSetting.getValue();
        YunShiTongResponse exchange = null;
        try {
            String url = OPEN_URL + "/open/getAccessToken";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            // 设置请求参数
            Map<String, String> map = new HashMap<>();
            map.put("appId", appKey);
            map.put("appSecret", MD5Util.md5(appSecret));
            log.info("url:{}, req:{}", url, JSONObject.toJSONString(map));
            exchange = httpRestTemplateService.postForObject(url, map, YunShiTongResponse.class);
            log.info(JSONObject.toJSONString(exchange));
            if (exchange.getData() == null) {
                return "";
            }
            Object data = exchange.getData();
            if(Objects.nonNull(data)){
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(data));
                accessToken = jsonObject.getString("accessToken");
                Long expiresIn = jsonObject.getLong("expiresIn");
                redisUtilPool.setString(cacheKey, accessToken, expiresIn.intValue());
            }
        } catch (Exception e) {
            log.error("美宜佳获取token异常 getAccessToken_error:{}", e);
        }
        return accessToken;
    }

    @Override
    public OpenDeviceDTO getDeviceDetail(String enterpriseId, String deviceId, AccountTypeEnum accountType) {
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = OPEN_URL + "/aiot/api/sipDevice/get";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("id", deviceId);
        String urlParam = dealRequestParam(requestMap);
        String signUrl = url;
        if(Objects.nonNull(urlParam)){
            signUrl = url + "?" + urlParam;
        }
        long timestamp = System.currentTimeMillis() / 1000;
        Pair<String, String> videoSetting = getVideoSetting(enterpriseId, accountType);
        String md5AppSecret = MD5Util.md5(videoSetting.getValue());
        String sign = hmacSha1(md5AppSecret, signUrl.toLowerCase() + "&" + timestamp + "&" + md5AppSecret);
        HashMap<String, String> headMap = new HashMap();
        headMap.put("token", accessToken);
        headMap.put("api_version", "v1");
        headMap.put("timestamp", String.valueOf(timestamp));
        headMap.put("sign", sign);
        JSONObject response = httpRestTemplateService.getForObject(url, JSONObject.class, requestMap, headMap);
        if(Objects.isNull(response)){
            return null;
        }
        Integer code = response.getInteger("code");
        if("11810101".equals(String.valueOf(code))){
            int retryTimes = Constants.INDEX_ONE;
            response = getRetry(enterpriseId, accountType, url, requestMap, JSONObject.class, headMap, retryTimes);
        }
        OpenDeviceDTO openDeviceDTO = new OpenDeviceDTO();
        openDeviceDTO.setDeviceId(response.getString("id"));
        openDeviceDTO.setDeviceName(response.getString("name"));
        openDeviceDTO.setDataSourceId(response.getString("sn"));
        openDeviceDTO.setDeviceStatus(Constants.INDEX_TWO.equals(response.getInteger("status")) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
        openDeviceDTO.setSource(getYunTypeNum().getCode());
        List<OpenChannelDTO> channelList = getChannelList(enterpriseId, deviceId, accountType);
        openDeviceDTO.setChannelList(channelList);
        return openDeviceDTO;
    }

    private List<OpenChannelDTO> getChannelList(String enterpriseId, String deviceId, AccountTypeEnum accountType) {
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = OPEN_URL + "/aiot/api/sipChannel/page";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("DeviceId", deviceId);
        requestMap.put("MaxResultCount", "99");
        String urlParam = dealRequestParam(requestMap);
        String signUrl = url;
        if(Objects.nonNull(urlParam)){
            signUrl = url + "?" + urlParam;
        }
        long timestamp = System.currentTimeMillis() / 1000;
        Pair<String, String> videoSetting = getVideoSetting(enterpriseId, accountType);
        String md5AppSecret = MD5Util.md5(videoSetting.getValue());
        String sign = hmacSha1(md5AppSecret, signUrl.toLowerCase() + "&" + timestamp + "&" + md5AppSecret);
        HashMap<String, String> headMap = new HashMap();
        headMap.put("token", accessToken);
        headMap.put("api_version", "v1");
        headMap.put("timestamp", String.valueOf(timestamp));
        headMap.put("sign", sign);
        JSONObject response = httpRestTemplateService.getForObject(url, JSONObject.class, requestMap, headMap);
        if(Objects.isNull(response)){
            return null;
        }
        Integer code = response.getInteger("code");
        if("11810101".equals(String.valueOf(code))){
            int retryTimes = Constants.INDEX_ONE;
            response = getRetry(enterpriseId, accountType, url, requestMap, JSONObject.class, headMap, retryTimes);
        }
        JSONArray items = response.getJSONArray("items");
        List<OpenChannelDTO> list = new ArrayList<>();
        if (Objects.nonNull(items) && !items.isEmpty()) {
            for (Object item : items) {
                JSONObject deviceObj = JSONObject.parseObject(JSONObject.toJSONString(item));
                OpenChannelDTO channel = new OpenChannelDTO();
                channel.setParentDeviceId(deviceObj.getString("deviceId"));
                channel.setDeviceId(deviceObj.getString("id"));
                channel.setChannelNo(deviceObj.getString("channel"));
                channel.setChannelName(deviceObj.getString("name"));
                channel.setHasPtz(deviceObj.getBoolean("isPtz"));
                channel.setStatus(Constants.INDEX_ONE.equals(deviceObj.getInteger("status")) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                channel.setSource(getYunTypeNum().getCode());
                channel.setChannelId(deviceObj.getString("deviceSn"));
               list.add(channel);
            }
        }
        return list;
    }

    @Override
    public PageInfo<OpenDevicePageDTO> getDeviceList(String enterpriseId, AccountTypeEnum accountType, Integer pageNum, Integer pageSize) {
        String accessToken = getAccessToken(enterpriseId, accountType);
        Integer skipCount = (pageNum - 1) * pageSize + 1;
        String url = OPEN_URL + "/aiot/api/sipDevice/page";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("SkipCount", String.valueOf(skipCount));
        requestMap.put("MaxResultCount", String.valueOf(pageSize));
        String urlParam = dealRequestParam(requestMap);
        String signUrl = url;
        if(Objects.nonNull(urlParam)){
            signUrl = url + "?" + urlParam;
        }
        long timestamp = System.currentTimeMillis() / 1000;
        Pair<String, String> videoSetting = getVideoSetting(enterpriseId, accountType);
        String md5AppSecret = MD5Util.md5(videoSetting.getValue());
        String sign = hmacSha1(md5AppSecret, signUrl.toLowerCase() + "&" + timestamp + "&" + md5AppSecret);
        HashMap<String, String> headMap = new HashMap();
        headMap.put("token", accessToken);
        headMap.put("api_version", "v1");
        headMap.put("timestamp", String.valueOf(timestamp));
        headMap.put("sign", sign);
        JSONObject response = httpRestTemplateService.getForObject(url, JSONObject.class, requestMap, headMap);
        Integer code = response.getInteger("code");
        if("11810101".equals(String.valueOf(code))){
            int retryTimes = Constants.INDEX_ONE;
            response = getRetry(enterpriseId, accountType, url, requestMap, JSONObject.class, headMap, retryTimes);
            if(Objects.isNull(response)){
                throw new ServiceException(ErrorCodeEnum.API_ERROR);
            }
        }
        PageInfo pageInfo = new PageInfo();
        if (Objects.nonNull(response)) {
            Integer totalCount = response.getInteger("totalCount");
            pageInfo.setTotal(totalCount);
            pageInfo.setPageSize(pageSize);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPages(totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1);
            JSONArray items = response.getJSONArray("items");
            List<OpenDevicePageDTO> list = new ArrayList<>();
            if (Objects.nonNull(items) && !items.isEmpty()) {
                for (Object item : items) {
                    JSONObject deviceObj = JSONObject.parseObject(JSONObject.toJSONString(item));
                    OpenDevicePageDTO pageDevice = new OpenDevicePageDTO();
                    pageDevice.setDeviceId(deviceObj.getString("id"));
                    pageDevice.setDeviceSerial(deviceObj.getString("sn"));
                    pageDevice.setDeviceName(deviceObj.getString("name"));
                    pageDevice.setSource(getYunTypeNum().getCode());
                    pageDevice.setStoreCode(deviceObj.getString("storeCode"));
                    pageDevice.setDeviceStatus(Constants.INDEX_TWO.equals(deviceObj.getInteger("status")) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                    list.add(pageDevice);
                }
            }
            pageInfo.setList(list);
        }
        return pageInfo;
    }

    @Override
    public LiveVideoVO getLiveUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        String deviceSn = device.getDataSourceId(), channelNo = param.getChannelNo();
        VideoProtocolTypeEnum protocolTypeEnum = param.getProtocol();
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String url = OPEN_URL + "/aiot/api/sipStream/liveStart";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("deviceId", deviceSn);
        if(Objects.nonNull(device.getHasChildDevice()) && device.getHasChildDevice()){
            requestMap.put("channelId", channelNo);
        }
        String signUrl = url;
        long timestamp = System.currentTimeMillis() / 1000;
        Pair<String, String> videoSetting = getVideoSetting(enterpriseId, accountType);
        String md5AppSecret = MD5Util.md5(videoSetting.getValue());
        String sign = hmacSha1(md5AppSecret, signUrl.toLowerCase() + "&" + timestamp + "&" + md5AppSecret);
        String accessToken = getAccessToken(enterpriseId, accountType);
        HashMap<String, String> headMap = new HashMap();
        headMap.put("token", accessToken);
        headMap.put("api_version", "v1");
        headMap.put("timestamp", String.valueOf(timestamp));
        headMap.put("sign", sign);
        JSONObject response = httpRestTemplateService.postForObject(url, requestMap, JSONObject.class, headMap);
        if(Objects.isNull(response)){
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        Integer code = response.getInteger("code");
        if("11810101".equals(String.valueOf(code))){
            int retryTimes = Constants.INDEX_ONE;
            response = retry(enterpriseId, accountType, url, requestMap, JSONObject.class, headMap, retryTimes);
            if(Objects.isNull(response)){
                throw new ServiceException(ErrorCodeEnum.API_ERROR);
            }
            code = response.getInteger("code");
        }
        if(!Constants.INDEX_ZERO.equals(code)){
            throw new ServiceException(ErrorCodeEnum.LIVE_STREAM_ERROR);
        }
        JSONObject data = response.getJSONObject("data");
        if(Objects.isNull(data)){
            return null;
        }
        LiveVideoVO result = new LiveVideoVO();
        String liveUrl = data.getString("url");
        if(VideoProtocolTypeEnum.WSS.equals(protocolTypeEnum)){
            liveUrl = data.getString("wsFlv");
        }
        if(VideoProtocolTypeEnum.HTTP_FLV.equals(protocolTypeEnum) || VideoProtocolTypeEnum.FLV.equals(protocolTypeEnum) || VideoProtocolTypeEnum.HTTPS_FLV.equals(protocolTypeEnum)){
            liveUrl = data.getString("flv");
        }
        if(VideoProtocolTypeEnum.RTSP.equals(protocolTypeEnum)){
            liveUrl = data.getString("rtsp");
        }
        String streamId = data.getString("streamId");
        result.setUrl(liveUrl);
        result.setStreamId(streamId);
        return result;
    }

    @Override
    public LiveVideoVO getPastVideoUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        String startTime = param.getStartTime(), endTime = param.getEndTime(), channelNo = param.getChannelNo();
        VideoProtocolTypeEnum protocolTypeEnum = param.getProtocol();
        String deviceSn = device.getDataSourceId();
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String url = OPEN_URL + "/aiot/api/sipStream/playbackStart";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("deviceId", deviceSn);
        requestMap.put("beginTime", startTime);
        requestMap.put("endTime", endTime);
        requestMap.put("playType", "1");
        if(Objects.nonNull(device.getHasChildDevice()) && device.getHasChildDevice()){
            requestMap.put("channelId", channelNo);
        }
        String signUrl = url;
        long timestamp = System.currentTimeMillis() / 1000;
        Pair<String, String> videoSetting = getVideoSetting(enterpriseId, accountType);
        String md5AppSecret = MD5Util.md5(videoSetting.getValue());
        String sign = hmacSha1(md5AppSecret, signUrl.toLowerCase() + "&" + timestamp + "&" + md5AppSecret);
        String accessToken = getAccessToken(enterpriseId, accountType);
        HashMap<String, String> headMap = new HashMap();
        headMap.put("token", accessToken);
        headMap.put("api_version", "v1");
        headMap.put("timestamp", String.valueOf(timestamp));
        headMap.put("sign", sign);
        JSONObject response = httpRestTemplateService.postForObject(url, requestMap, JSONObject.class, headMap);
        if(Objects.isNull(response)){
            return null;
        }
        Integer code = response.getInteger("code");
        if("11810101".equals(String.valueOf(code))){
            int retryTimes = Constants.INDEX_ONE;
            response = retry(enterpriseId, accountType, url, requestMap, JSONObject.class, headMap, retryTimes);
            if(Objects.isNull(response)){
                throw new ServiceException(ErrorCodeEnum.API_ERROR);
            }
            code = response.getInteger("code");
        }
        if(!Constants.INDEX_ZERO.equals(code)){
            throw new ServiceException(ErrorCodeEnum.LIVE_STREAM_ERROR);
        }
        JSONObject data = response.getJSONObject("data");
        if(Objects.isNull(data)){
            return null;
        }
        LiveVideoVO result = new LiveVideoVO();
        String liveUrl = data.getString("url");
        if(VideoProtocolTypeEnum.WSS.equals(protocolTypeEnum)){
            liveUrl = data.getString("wsFlv");
        }
        if(VideoProtocolTypeEnum.HTTP_FLV.equals(protocolTypeEnum) || VideoProtocolTypeEnum.FLV.equals(protocolTypeEnum) || VideoProtocolTypeEnum.HTTPS_FLV.equals(protocolTypeEnum)){
            liveUrl = data.getString("flv");
        }
        if(VideoProtocolTypeEnum.RTSP.equals(protocolTypeEnum)){
            liveUrl = data.getString("rtsp");
        }
        String streamId = data.getString("streamId");
        result.setUrl(liveUrl);
        result.setStreamId(streamId);
        return result;
    }

    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {

    }

    @Override
    public Boolean ptzStart(String enterpriseId, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration) {
        String deviceSn = device.getDataSourceId();
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String url = OPEN_URL + "/aiot/api/sipStream/ptzCtrl";
        int ptzCommandType = command == 0 ? 1 : command == 1 ? 4 : command == 2 ? 7 : command == 3 ? 8 : command == 8 ? 11 : command == 9 ? 12 : 0;
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("deviceId", deviceSn);
        requestMap.put("ptzCommandType", String.valueOf(ptzCommandType));
        if(Objects.nonNull(device.getHasChildDevice()) && device.getHasChildDevice()){
            requestMap.put("channelId", channelNo);
        }
        String signUrl = url;
        long timestamp = System.currentTimeMillis() / 1000;
        Pair<String, String> videoSetting = getVideoSetting(enterpriseId, accountType);
        String md5AppSecret = MD5Util.md5(videoSetting.getValue());
        String sign = hmacSha1(md5AppSecret, signUrl.toLowerCase() + "&" + timestamp + "&" + md5AppSecret);
        String accessToken = getAccessToken(enterpriseId, accountType);
        HashMap<String, String> headMap = new HashMap();
        headMap.put("token", accessToken);
        headMap.put("api_version", "v1");
        headMap.put("timestamp", String.valueOf(timestamp));
        headMap.put("sign", sign);
        JSONObject response = httpRestTemplateService.postForObject(url, requestMap, JSONObject.class, headMap);
        if(Objects.isNull(response)){
            return false;
        }
        Integer code = response.getInteger("code");
        if("11810101".equals(String.valueOf(code))){
            int retryTimes = Constants.INDEX_ONE;
            response = retry(enterpriseId, accountType, url, requestMap, JSONObject.class, headMap, retryTimes);
            if(Objects.isNull(response)){
                throw new ServiceException(ErrorCodeEnum.API_ERROR);
            }
            code = response.getInteger("code");
        }
        if(!Constants.INDEX_ZERO.equals(code)){
            throw new ServiceException(ErrorCodeEnum.PTZ_ERROR);
        }
        JSONObject data = response.getJSONObject("data");
        if(Objects.isNull(data)){
            return false;
        }
        return true;
    }

    @Override
    public Boolean ptzStop(String enterpriseId, DeviceDO device, String channelNo) {
        String deviceSn = device.getDataSourceId();
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String url = OPEN_URL + "/aiot/api/sipStream/ptzCtrl";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("deviceId", deviceSn);
        requestMap.put("ptzCommandType", "0");
        if(Objects.nonNull(device.getHasChildDevice()) && device.getHasChildDevice()){
            requestMap.put("channelId", channelNo);
        }
        String signUrl = url;
        long timestamp = System.currentTimeMillis() / 1000;
        Pair<String, String> videoSetting = getVideoSetting(enterpriseId, accountType);
        String md5AppSecret = MD5Util.md5(videoSetting.getValue());
        String sign = hmacSha1(md5AppSecret, signUrl.toLowerCase() + "&" + timestamp + "&" + md5AppSecret);
        String accessToken = getAccessToken(enterpriseId, accountType);
        HashMap<String, String> headMap = new HashMap();
        headMap.put("token", accessToken);
        headMap.put("api_version", "v1");
        headMap.put("timestamp", String.valueOf(timestamp));
        headMap.put("sign", sign);
        JSONObject response = httpRestTemplateService.postForObject(url, requestMap, JSONObject.class, headMap);
        if(Objects.isNull(response)){
            return false;
        }
        Integer code = response.getInteger("code");
        if("11810101".equals(String.valueOf(code))){
            int retryTimes = Constants.INDEX_ONE;
            response = retry(enterpriseId, accountType, url, requestMap, JSONObject.class, headMap, retryTimes);
            if(Objects.isNull(response)){
                throw new ServiceException(ErrorCodeEnum.API_ERROR);
            }
            code = response.getInteger("code");
        }
        if(!Constants.INDEX_ZERO.equals(code)){
            throw new ServiceException(ErrorCodeEnum.PTZ_ERROR);
        }
        JSONObject data = response.getJSONObject("data");
        if(Objects.isNull(data)){
            return false;
        }
        return true;
    }

    @Override
    public String addPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String devicePositionName) {
        return null;
    }

    @Override
    public Boolean deletePtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        return null;
    }

    @Override
    public Boolean loadPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        return null;
    }

    @Override
    public String capture(String eid, DeviceDO device, String channelNo, String quality) {
        return null;
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

    @Override
    public LiveVideoVO playbackSpeed(String enterpriseId, DeviceDO device, String channelNo, String streamId, String speed, VideoProtocolTypeEnum protocol) {
        String deviceSn = device.getDataSourceId();
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String url = OPEN_URL + "/aiot/api/sipStream/playbackSpeed";
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("deviceId", deviceSn);
        requestMap.put("channelId", channelNo);
        requestMap.put("streamId", streamId);
        requestMap.put("scale", speed);
        if(Objects.nonNull(device.getHasChildDevice()) && device.getHasChildDevice()){
            requestMap.put("channelId", channelNo);
        }
        String signUrl = url;
        long timestamp = System.currentTimeMillis() / 1000;
        Pair<String, String> videoSetting = getVideoSetting(enterpriseId, accountType);
        String md5AppSecret = MD5Util.md5(videoSetting.getValue());
        String sign = hmacSha1(md5AppSecret, signUrl.toLowerCase() + "&" + timestamp + "&" + md5AppSecret);
        String accessToken = getAccessToken(enterpriseId, accountType);
        HashMap<String, String> headMap = new HashMap();
        headMap.put("token", accessToken);
        headMap.put("api_version", "v1");
        headMap.put("timestamp", String.valueOf(timestamp));
        headMap.put("sign", sign);
        JSONObject response = httpRestTemplateService.postForObject(url, requestMap, JSONObject.class, headMap);
        if(Objects.isNull(response)){
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        Integer code = response.getInteger("code");
        if("11810101".equals(String.valueOf(code))){
            int retryTimes = Constants.INDEX_ONE;
            response = retry(enterpriseId, accountType, url, requestMap, JSONObject.class, headMap, retryTimes);
            if(Objects.isNull(response)){
                throw new ServiceException(ErrorCodeEnum.API_ERROR);
            }
            code = response.getInteger("code");
        }
        if(!Constants.INDEX_ZERO.equals(code)){
            throw new ServiceException(ErrorCodeEnum.LIVE_STREAM_ERROR);
        }
        JSONObject data = response.getJSONObject("data");
        if(Objects.isNull(data)){
            return null;
        }
        LiveVideoVO result = new LiveVideoVO();
        String liveUrl = data.getString("url");
        if(VideoProtocolTypeEnum.WSS.equals(protocol)){
            liveUrl = data.getString("wsFlv");
        }
        if(VideoProtocolTypeEnum.HTTP_FLV.equals(protocol) || VideoProtocolTypeEnum.FLV.equals(protocol) || VideoProtocolTypeEnum.HTTPS_FLV.equals(protocol)){
            liveUrl = data.getString("flv");
        }
        if(VideoProtocolTypeEnum.RTSP.equals(protocol)){
            liveUrl = data.getString("rtsp");
        }
        result.setUrl(liveUrl);
        result.setStreamId(streamId);
        return result;
    }

    public static String dealRequestParam(Map<String, String> requestMap) {
        if(requestMap.isEmpty()){
            return null;
        }
        TreeMap<String, String> treeMap = new TreeMap<>(requestMap);
        StringBuilder url = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : treeMap.entrySet()) {
                String key = URLEncoder.encode(entry.getKey(), "UTF-8");
                String value = URLEncoder.encode(entry.getValue(), "UTF-8");
                url.append(key).append("=").append(value).append("&");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url.substring(0, url.length() - 1);
    }

    public static String hmacSha1(String key, String data) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());

            StringBuilder hmacBuilder = new StringBuilder();
            for (byte b : rawHmac) {
                hmacBuilder.append(String.format("%02x", b));
            }
            return hmacBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> T  retry(String enterpriseId, AccountTypeEnum accountType, String url, Object request, Class<T> responseType, HashMap<String, String> headMap, int retryTimes){
        log.info("token异常, enterpriseId:{}, 重试：{}", enterpriseId, retryTimes);
        T response = null;
        if(retryTimes < Constants.INDEX_TWO){
            String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, enterpriseId, accountType.getCode(), getYunTypeNum().getCode());
            redisUtilPool.delKey(cacheKey);
            String accessToken = getAccessToken(enterpriseId, accountType);
            headMap.put("token", accessToken);
            try {
                response = httpRestTemplateService.postForObject(url, request, responseType, headMap);
                if(Objects.isNull(response)){
                    throw new ServiceException(ErrorCodeEnum.API_ERROR);
                }
                JSONObject responseJson = JSONObject.parseObject(JSONObject.toJSONString(response));
                if("11810101".equals(responseJson.getString("code"))){
                    retryTimes = retryTimes + 1;
                    retry(enterpriseId, accountType, url, request, responseType, headMap, retryTimes);
                }
            } catch (ServiceException e) {
                if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                    retryTimes = retryTimes + 1;
                    retry(enterpriseId, accountType, url, request, responseType, headMap, retryTimes);
                }
            }
        }
        return response;
    }

    private <T> T  getRetry(String enterpriseId, AccountTypeEnum accountType, String url, Map<String, ?> request, Class<T> responseType, HashMap<String, String> headMap, int retryTimes){
        log.info("token异常, enterpriseId:{}, 重试：{}", enterpriseId, retryTimes);
        T response = null;
        if(retryTimes < Constants.INDEX_TWO){
            String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, enterpriseId, accountType.getCode(), getYunTypeNum().getCode());
            redisUtilPool.delKey(cacheKey);
            String accessToken = getAccessToken(enterpriseId, accountType);
            headMap.put("token", accessToken);
            try {
                response = httpRestTemplateService.getForObject(url, responseType, request, headMap);
                if(Objects.isNull(response)){
                    throw new ServiceException(ErrorCodeEnum.API_ERROR);
                }
                JSONObject responseJson = JSONObject.parseObject(JSONObject.toJSONString(response));
                if("11810101".equals(responseJson.getString("code"))){
                    retryTimes = retryTimes + 1;
                    getRetry(enterpriseId, accountType, url, request, responseType, headMap, retryTimes);
                }
            } catch (Exception e) {
                retryTimes = retryTimes + 1;
                getRetry(enterpriseId, accountType, url, request, responseType, headMap, retryTimes);
            }
        }
        return response;
    }

    public static void main(String[] args) {
        try {
            String url = "https://open.myj.com.cn/aiot/api/sipDevice/page".toLowerCase();
            System.out.println(url);
            System.out.println(hmacSha1("66aa6d9986144fbbb2bcbb504c249b7a", url + "&1697773424&66aa6d9986144fbbb2bcbb504c249b7a"));
            String str = "";
            List<String> storeCode = JSONObject.parseArray(str).stream().map(o -> JSONObject.parseObject(JSONObject.toJSONString(o)).getString("storeCode")).collect(Collectors.toList());
            System.out.println(String.join(",", storeCode));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
