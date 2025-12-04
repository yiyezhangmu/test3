package com.coolcollege.intelligent.service.video.openapi.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.YunShiTongHttpClient;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.OpenDeviceDTO;
import com.coolcollege.intelligent.model.device.dto.OpenDevicePageDTO;
import com.coolcollege.intelligent.model.device.dto.VideoDTO;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.YingshiChannelDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.openapi.VideoOpenService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.VideoProtocolTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.net.URL;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/12/13 14:36
 * @Version 1.0
 */
@Slf4j
@Service
public class ULucuOpenServiceImpl implements VideoOpenService {

    @Value("${ulucu.url}")
    protected String ulucuUrl;
    private final static Integer OK = 0;
    private final static Integer DEVICE_OFFLINE = 1002;
    @Resource
    protected EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Resource
    private YunShiTongHttpClient yunShiTongHttpClient;
    @Resource
    protected RedisUtilPool redisUtilPool;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private StoreMapper storeMapper;

    @Override
    public YunTypeEnum getYunTypeNum() {
        return YunTypeEnum.ULUCU;
    }

    @Override
    public String getAccessToken(String eid, AccountTypeEnum accountType) {
        return null;
    }

    @Override
    public OpenDeviceDTO getDeviceDetail(String eid, String deviceId, AccountTypeEnum accountType) {
        String url = "/h/KjNmK/device/get_device_status";
        Map<String, Object> params = new HashMap<>();
        params.put("device_sn", deviceId);
        String resultStr = this.uLucuGet(url, params, eid);
        JSONObject result = JSONObject.parseObject(resultStr);

        if (OK.equals(result.getInteger("code"))) {
            OpenDeviceDTO openDeviceDTO = new OpenDeviceDTO();
            String status = result.getJSONObject("data").getString("status");
            String deviceStatus = "1".equals(status) ? "online" : "offline";
            openDeviceDTO.setDeviceId(deviceId);
            openDeviceDTO.setDeviceStatus(deviceStatus);
            return openDeviceDTO;
        }
        return null;
    }

    @Override
    public PageInfo<OpenDevicePageDTO> getDeviceList(String eid, AccountTypeEnum accountType, Integer pageNum, Integer pageSize) {
        if (pageNum > Constants.INDEX_ONE) {
            return new PageInfo<>();
        }
        String url = "/h/LpGAe/auth/visible_stores";
        String resultStr = this.uLucuGet(url, null, eid);
        log.info("visible_stores result={}", resultStr);
        JSONObject result = JSONObject.parseObject(resultStr);
        List<JSONObject> deviceObjList = new ArrayList<>();
        if (OK.equals(result.getInteger("code"))) {
            JSONArray dataArray = result.getJSONArray("data");
            if (Objects.nonNull(dataArray) && !dataArray.isEmpty()) {
                for (Object store : dataArray) {
                    JSONObject storeObj = (JSONObject) store;
                    String storeId = storeObj.getString("store_id");
                    String storeName = storeObj.getString("store");
                    String userStoreId = null;
                    if (StringUtils.isNotBlank(storeName)) {
                        StoreDO storeDO = storeMapper.selectStoreNameByName(eid, storeName);
                        if(storeDO != null){
                            userStoreId = storeDO.getStoreId();
                        }
                    }
                    String deviceUrl = "/h/LpGAf/auth/store_device_list";
                    Map<String, Object> params = new HashMap<>();
                    params.put("store_id", storeId);
                    params.put("type", 3);

                    String deviceResultStr = this.uLucuGet(deviceUrl, params, eid);
                    JSONObject deviceResult = JSONObject.parseObject(deviceResultStr);

                    if (OK.equals(deviceResult.getInteger("code"))) {
                        JSONArray dataDeviceArray = deviceResult.getJSONArray("data");
                        if (Objects.nonNull(dataDeviceArray) && !dataDeviceArray.isEmpty()) {
                            for (Object device : dataDeviceArray) {
                                JSONObject deviceObj = (JSONObject) device;
                                deviceObj.put("userStoreId", userStoreId);
                                deviceObjList.add(deviceObj);
                            }
                        }
                    }
                }
            }
        }
        List<OpenDevicePageDTO> resultList = new ArrayList<>();
        deviceObjList.forEach(device -> {
            OpenDevicePageDTO devicePageDTO = new OpenDevicePageDTO();
            devicePageDTO.setDeviceId(device.getString("sn"));
            devicePageDTO.setDeviceName(device.getString("alias"));
            devicePageDTO.setHasChildDevice(false);
            devicePageDTO.setSource(YunTypeEnum.ULUCU.getCode());
            devicePageDTO.setUseStoreId(device.getString("userStoreId"));
            String status = device.getString("status");
            String deviceStatus = "1".equals(status) ? "online" : "offline";
            devicePageDTO.setDeviceStatus(deviceStatus);
            resultList.add(devicePageDTO);
        });
        PageInfo<OpenDevicePageDTO> pageInfo = new PageInfo<>();
        pageInfo.setList(resultList);
        return pageInfo;
    }

    @Override
    public LiveVideoVO getLiveUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        String channelNo = param.getChannelNo();
        Integer quality = param.getQuality();
        String url = "/h/KeNcI/auth/weburl";
        Map<String, Object> params = new HashMap<>();
        params.put("device_id", device.getDeviceId());

        params.put("channel_idx", StringUtils.isNotBlank(channelNo) && !"0".equals(channelNo) ? channelNo : 1);
        params.put("play_type", getPlayType(param.getProtocol()));


        if (quality == null) {
            params.put("rate", 1000);
        }
        if (quality != null && quality == 0) {
            params.put("rate", 700);
        }
        if (quality != null && quality == 1) {
            params.put("rate", 1000);
        }
        String deviceResultStr = this.uLucuGet(url, params, enterpriseId);
        JSONObject deviceResult = JSONObject.parseObject(deviceResultStr);
        String playUrl = null;
        if (OK.equals(deviceResult.getInteger("code"))) {
            playUrl = deviceResult.getJSONObject("data").getString("play_url");
            String domainListStr = deviceResult.getJSONObject("data").getString("domain_list");
            if (StringUtils.isNotBlank(domainListStr)) {
                String domain = domainListStr.split(",")[0];
                try {
                    URL urlDomain = new URL(playUrl);
                    String oldDomain = urlDomain.getHost();
                    playUrl = playUrl.replace(oldDomain, domain);
                    if (playUrl.startsWith("http://")) {
                        playUrl = playUrl.replace("http://", "https://");
                    }
                } catch (Exception e) {
                    log.error("getLiveUrl error", e);
                }
            }
        } else if (DEVICE_OFFLINE.equals(deviceResult.getInteger("code"))) {
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_ONLINE);
        }
        LiveVideoVO liveVideoVO = new LiveVideoVO();
        liveVideoVO.setToken(null);
        liveVideoVO.setUrl(playUrl);
        return liveVideoVO;
    }

    @Override
    public LiveVideoVO getPastVideoUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        String channelNo = param.getChannelNo(), startTime = param.getStartTime(), endTime = param.getEndTime();
        String url = "/h/KeOcw/playweb/back_url";
        Map<String, Object> params = new HashMap<>();
        params.put("sn", device.getDeviceId());
        params.put("channel_id", StringUtils.isNotBlank(channelNo) && !"0".equals(channelNo) ? channelNo : 1);
        params.put("play_type", getPlayType(param.getProtocol()));
        params.put("rate", 1000);
        Date date = DateUtil.parse(startTime, DatePattern.NORM_DATETIME_PATTERN);
        params.put("start_time", date.getTime() / 1000);

        String deviceResultStr = this.uLucuPost(url, params, enterpriseId);
        JSONObject deviceResult = JSONObject.parseObject(deviceResultStr);
        String playUrl = null;
        if (OK.equals(deviceResult.getInteger("code"))) {
            playUrl = deviceResult.getJSONObject("data").getString("play_url");
            String domainListStr = deviceResult.getJSONObject("data").getString("domain_list");
            if (StringUtils.isNotBlank(domainListStr)) {
                String domain = domainListStr.split(",")[0];
                try {
                    URL urlDomain = new URL(playUrl);
                    String oldDomain = urlDomain.getHost();
                    playUrl = playUrl.replace(oldDomain, domain);
                    if (playUrl.startsWith("http://")) {
                        playUrl = playUrl.replace("http://", "https://");
                    }
                } catch (Exception e) {
                    log.error("getLiveUrl error", e);
                }
            }
        }
        LiveVideoVO liveVideoVO = new LiveVideoVO();
        liveVideoVO.setToken(null);
        liveVideoVO.setUrl(playUrl);
        return liveVideoVO;
    }

    public String getPlayType(VideoProtocolTypeEnum protocolType){
        if(VideoProtocolTypeEnum.HLS.equals(protocolType)){
            return "s_hls";
        }
        if(VideoProtocolTypeEnum.RTMP.equals(protocolType)){
            return "rtmp";
        }
        if(VideoProtocolTypeEnum.FLV.equals(protocolType)){
            return "s_hflv";
        }
        return "s_hflv";
    }

    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {
    }

    @Override
    public Boolean ptzStart(String eid, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration) {

        return true;
    }

    @Override
    public Boolean ptzStop(String eid, DeviceDO device, String channelNo) {

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

    private List<YingshiChannelDTO> getChannelList(String eid, String deviceId, AccountTypeEnum accountType) {
        return null;
    }

    public DeviceCapacityDTO getDeviceCapaticy(String enterpriseId, String deviceId, AccountTypeEnum accountTypeEnum) {
        DeviceCapacityDTO deviceCapacity = new DeviceCapacityDTO();
        deviceCapacity.setDeviceSerial(deviceId);
        deviceCapacity.setSupportCapture(Constants.ZERO);
        deviceCapacity.setSupportFlowStatistics(Constants.ZERO);
        return deviceCapacity;
    }


    /**
     * 心跳监测
     * @param enterpriseId
     * @param param 媒体类型。
     * ->> 可选项 [1-实时，2-回放]
     *  param   视频源。
     * *播放请求时会返回source，取返回source的值；没有返回source字段，就用播放请求的入参字段拼接下 device_id-channel_idx-rate
     * @return
     */
    public boolean heartbeat(String enterpriseId, VideoDTO param) {
        String url = "/h/KeNfI/auth/media_heartbeat";
        Map<String, Object> params = new HashMap<>();
        params.put("type", 1);
        if(StringUtils.isNotBlank(param.getStartTime())){
            params.put("type", 2);
        }
        params.put("play_type", "hflv");
        String source = param.getDeviceId() + Constants.SPLIT_LINE + "1";
        if (param.getQuality() == null) {
            source = source + Constants.SPLIT_LINE + "700";
        }
        if (param.getQuality() != null && param.getQuality() == 0) {
            source = source + Constants.SPLIT_LINE + "700";
        }
        if (param.getQuality() != null && param.getQuality() == 1) {
            source = source + Constants.SPLIT_LINE + "1000";
        }
        params.put("source", source);
        String result = this.uLucuGet(url, params, enterpriseId);
        JSONObject deviceResult = JSONObject.parseObject(result);
        String playUrl = null;
        if (OK.equals(deviceResult.getInteger("code"))) {
            return true;
        }
        return false;
    }


    /**
     * 媒体播放探测接口
     * @param enterpriseId
     * @param playUrl
     * @return
     */
    public String mediaCheck(String enterpriseId, String playUrl) {
        String url = "/h/KeNeI/auth/media_check";
        Map<String, Object> params = new HashMap<>();
        params.put("play_url", playUrl);
        String result = this.uLucuGet(url, params, enterpriseId);
        JSONObject deviceResult = JSONObject.parseObject(result);
        if (OK.equals(deviceResult.getInteger("code"))) {
            return deviceResult.getJSONObject("data").getString("result");
        }else {
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
    }


    public String uLucuPost(String uri, Map<String, Object> params, String eid) {

        AccountTypeEnum accountType = AccountTypeEnum.PRIVATE;


        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(eid, getYunTypeNum().getCode(), accountType.getCode());
        String ulucuAppId = videoSetting.getAccessKeyId();
        String ulucuSecret = videoSetting.getSecret();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        // MD5加密
        // 确定计算方法
        try {
            String queryStr = null;
            if (params != null && !params.isEmpty()) {
                queryStr = params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
            }
            // 需要加密的字符串
            String str = ulucuSecret + ulucuAppId + uri + queryStr;
            BASE64Encoder base64en = new BASE64Encoder();

            // 加密后的字符串,生成401认证token
            String newStr = base64en.encode((ulucuAppId + ":" + MD5(str)).getBytes());

            // 发送POST请求
            String postUrl = ulucuUrl + uri;
            if (params != null && !params.isEmpty()) {
                postUrl = postUrl + "?" + queryStr;
            }

            HttpRequest httpRequest = HttpUtil.createPost(postUrl);
            httpRequest.header("Authorization", "Basic " + newStr);
            httpRequest.header("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            httpRequest.form(params);

            HttpResponse httpResponse = httpRequest.execute();
            log.info("sendPostJsonRequest-url:{}", postUrl);
            log.info("sendPostJsonRequest-body:{}", httpResponse.body());
            return httpResponse.body();
        } catch (Exception e) {
            log.error("uLucuPost", e);
        }
        return null;
    }


    public String uLucuGet(String uri, Map<String, Object> params, String eid) {

        AccountTypeEnum accountType = AccountTypeEnum.PRIVATE;


        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(eid, getYunTypeNum().getCode(), accountType.getCode());
        String ulucuAppId = videoSetting.getAccessKeyId();
        String ulucuSecret = videoSetting.getSecret();
        DataSourceHelper.changeToSpecificDataSource(dbName);

        // MD5加密
        // 确定计算方法
        try {
            String queryStr = null;
            if(params != null &&!params.isEmpty()){
                queryStr =  params.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
            }
            // 需要加密的字符串
            String str = ulucuSecret + ulucuAppId + uri;
            if(StringUtils.isNotBlank(queryStr)){
                str = str + queryStr;
            }
            BASE64Encoder base64en = new BASE64Encoder();

            // 加密后的字符串,生成401认证token
            String newStr = base64en.encode((ulucuAppId + ":" + MD5(str)).getBytes());

            // 发送POST请求
            String postUrl = ulucuUrl + uri;
            if(StringUtils.isNotBlank(queryStr)){
                postUrl = postUrl + "?" + queryStr;
            }
            HttpRequest httpRequest = HttpUtil.createGet(postUrl);
            httpRequest.header("Authorization", "Basic " + newStr);
            httpRequest.header("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");

            HttpResponse httpResponse = httpRequest.execute();
            String body = httpResponse.body();
            log.info("httpResponse-url:{}", postUrl);
            log.info("httpResponse-body:{}", body);
            return httpResponse.body();
        } catch (Exception e) {
            log.error("uLucuGet", e);
        }
        return null;
    }

    private static String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {

        final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }
}
