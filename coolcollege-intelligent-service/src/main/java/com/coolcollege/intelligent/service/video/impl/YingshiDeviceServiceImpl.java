package com.coolcollege.intelligent.service.video.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.YingshiHttpClient;
import com.coolcollege.intelligent.common.util.Base64Utils;
import com.coolcollege.intelligent.model.video.platform.yingshi.*;
import com.coolcollege.intelligent.model.video.platform.yingshi.YingshiChannelDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.YingshiCreateUrlStateDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.YingshiDeviceDTO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.YingshiDeviceService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/31
 */
@Service
@Slf4j
public class YingshiDeviceServiceImpl implements YingshiDeviceService {
    @Value("${yingshi.openAuth.url}")
    private String yingshiAuthUrl;

    @Value("${yingshi.url}")
    private String yingshiUrl;

    @Value("${yingshi.appKey}")
    private String yingshiAppKey;
    @Value("${yingshi.secret}")
    private String yingshiSecret;


    @Autowired
    private RedisUtilPool redisUtil;

    private static final String YINGSHI_TOKEN = "yingshiyun_token_trust_";

    private static final String YINGSHI_ACCESS_TOKEN  = "yingshiyun_access_token";
    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;

    private final static Integer OK = 200;


    @Override
    public String createYingshiAuthUrl(String eid, String storeId, String userId) {
        if (StringUtils.isBlank(eid)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "企业ID不能为空");
        }
        //验证是否开启萤石云服务
        enterpriseVideoSettingService.getSetting(eid, YunTypeEnum.YINGSHIYUN, AccountTypeEnum.PLATFORM);

        YingshiCreateUrlStateDTO yingshiCreateUrlStateDTO = new YingshiCreateUrlStateDTO();
        yingshiCreateUrlStateDTO.setEid(eid);
        yingshiCreateUrlStateDTO.setStoreId(storeId);
        yingshiCreateUrlStateDTO.setUserId(userId);
        return yingshiAuthUrl + "/trust/device?client_id=" + yingshiAppKey + "&response_type=code&state="
                + Base64Utils.strConvertBase(JSONObject.toJSONString(yingshiCreateUrlStateDTO));
    }


    private String getYingshiToken(String appKey, String appSecret) {
        String url = yingshiUrl + "/api/lapp/trust/device/v2/token/get";
        Map<String, String> map = new HashMap<>(4);
        map.put("appKey", appKey);
        map.put("appSecret", appSecret);
        String resultStr = YingshiHttpClient.post(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        return result.getJSONObject("data").getString("accessToken");
    }

    @Override
    public YingshiDeviceDTO getYingshiDeviceInfo(String deviceId, String token) {
        String url = yingshiUrl + "/api/lapp/device/info";
        Map<String, String> map = new HashMap<>(4);
        map.put("accessToken", token);
        map.put("deviceSerial", deviceId);
        String resultStr = CoolHttpClient.sendPostFormRequest(url, map);
        log.info("yingshiDeviceInfo.getYingshiDeviceInfo result={}",resultStr);
        JSONObject result = JSONObject.parseObject(resultStr);
        if (OK.equals(result.getInteger("code"))) {
            return result.getObject("data", YingshiDeviceDTO.class);
        }
        return null;
    }


    @Override
    public String getRedisToken(String eid) {
        String token = redisUtil.getString(getKey(eid));
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        token = getYingshiToken(yingshiAppKey, yingshiSecret);
        redisUtil.setString(getKey(eid), token, 3600);
        return token;
    }

    @Override
    public String getLiveUrl(String deviceId, Integer channelNo, Integer quality, Integer protocol, String startTime,
                             String endTime, String token) {

        String url = yingshiUrl + "/api/lapp/v2/live/address/get";
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", token);
        map.put("deviceSerial", deviceId);
        if (protocol != null) {
            map.put("protocol", protocol.toString());
        } else {
            map.put("protocol", "3");
        }
        //是否有通道号
        if (channelNo != null) {
            map.put("channelNo", channelNo == 0 ? "1" : channelNo.toString());
        }
        //是否查看历史视频
        if (StringUtils.isNotBlank(startTime)) {
            map.put("startTime", startTime);
            map.put("type", "2");
        } else {
            map.put("type", "1");
        }
        if (StringUtils.isNotBlank(endTime)) {
            map.put("stopTime", endTime);
        }
        map.put("quality", quality.toString());

        //直播流获取
        if (StringUtils.equals(map.get("type"), "1")) {
            log.info("request1:{}", JSONObject.toJSONString(map));
            String yunResultStr = YingshiHttpClient.post(url, map);
            log.info("response3:{}", yunResultStr);
            JSONObject yunResult = JSONObject.parseObject(yunResultStr);
            return yunResult.getJSONObject("data").getString("url");
        } else {
            //回放获取：先获取本地录像，如果本地录像没有则获取云存录像map = {HashMap@33165}  size = 8
            map.put("type", "2");
            log.info("request2:{}", JSONObject.toJSONString(map));
            String resultStr = CoolHttpClient.sendPostFormRequest(url, map);
            log.info("response2:{}", resultStr);
            JSONObject yunResult = JSONObject.parseObject(resultStr);

            if (yunResult != null && OK.equals(yunResult.getInteger("code"))) {
                return yunResult.getJSONObject("data").getString("url");
            }
            map.put("type", "3");
            log.info("request3:{}", JSONObject.toJSONString(map));
            String yunResultStr = YingshiHttpClient.post(url, map);
            log.info("response3:{}", resultStr);
            JSONObject recordResult = JSONObject.parseObject(yunResultStr);
            return recordResult.getJSONObject("data").getString("url");
        }
    }

    private String getKey(String eid) {
        return YINGSHI_TOKEN + eid;
    }

    @Override
    public List<YingshiChannelDTO> getChannelList(String eid, String deviceId, String token) {

        String url = yingshiUrl + "/api/lapp/device/camera/list";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        map.put("accessToken", token);
        String resultStr = CoolHttpClient.sendPostFormRequest(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        log.info("getChannelList:device={},result={}", deviceId, result);
        if (OK.equals(result.getInteger("code"))) {
            List<YingshiChannelDTO> deviceList = result.getObject("data", new TypeReference<List<YingshiChannelDTO>>() {});
            return ListUtils.emptyIfNull(deviceList).stream().filter(o-> Objects.nonNull(o.getRelatedIpc()) && o.getRelatedIpc()).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Boolean ptzStart(String deviceId, Integer channelNo, Integer direction, Integer speed, String token) {

        String url = yingshiUrl + "/api/lapp/device/ptz/start";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }else {
            map.put("channelNo", "1");
        }
        map.put("direction", direction.toString());
        map.put("speed", speed.toString());
        map.put("accessToken", token);
        String resultStr = YingshiHttpClient.post(url, map);
        return true;
    }

    @Override
    public Boolean ptzStop(String deviceId, Integer channelNo, String token) {

        String url = yingshiUrl + "/api/lapp/device/ptz/stop";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }else {
            map.put("channelNo", "1");
        }
        map.put("accessToken", token);
        String resultStr = YingshiHttpClient.post(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        return true;
    }

    @Override
    public Integer addPreset(String deviceId, Integer channelNo, String presetIndexName, String token) {

        String url = yingshiUrl + "/api/lapp/device/preset/add";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }
        map.put("accessToken", token);
        String resultStr = YingshiHttpClient.post(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        return result.getJSONObject("data").getInteger("index");
    }

    @Override
    public Boolean deletePreset(String deviceId, Integer channelNo, String presetIndex, String token) {

        String url = yingshiUrl + "/api/lapp/device/preset/clear";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }
        map.put("index", presetIndex);
        map.put("accessToken", token);
        String resultStr = YingshiHttpClient.post(url, map);
        return true;
    }

    @Override
    public Boolean invokePreset(String deviceId, Integer channelNo, String presetIndex, String token) {

        String url = yingshiUrl + "/api/lapp/device/preset/move";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }
        map.put("index", presetIndex.toString());
        map.put("accessToken", token);
        String resultStr = YingshiHttpClient.post(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        return true;
    }

    @Override
    public PassengerFlowSwitchStatusDTO passengerFlowSwitchStatus(String deviceId, String token) {

        String url = yingshiUrl + "/api/lapp/passengerflow/switch/status";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        map.put("accessToken", token);
        String resultStr = YingshiHttpClient.post(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        if (OK.equals(result.getInteger("code"))) {
            return result.getObject("data", new TypeReference<PassengerFlowSwitchStatusDTO>() {
            });
        }
        return null;
    }

    @Override
    public Boolean savePassengerFlow(String deviceId, Integer channelNo, Boolean enable, String token) {

        String url = yingshiUrl + "/api/lapp/passengerflow/switch/set";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }
        map.put("enable",enable?"1":"0");
        map.put("accessToken", token);
        String resultStr = YingshiHttpClient.post(url, map);
        return true;
    }

    @Override
    public PassengerFlowDailyDTO passengerFlowDaily(String deviceId, Integer channelNo, Long date, String token) {

        String url = yingshiUrl + "/api/lapp/passengerflow/daily";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        map.put("accessToken", token);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }else {
            map.put("channelNo", "1");
        }
        map.put("date", date.toString());
        String resultStr = CoolHttpClient.sendPostFormRequest(url, map);

        JSONObject result = JSONObject.parseObject(resultStr);
        if (OK.equals(result.getInteger("code"))) {
            return result.getObject("data", new TypeReference<PassengerFlowDailyDTO>() {
            });
        }
        return null;
    }

    @Override
    public List<PassengerFlowHourlyDTO> passengerFlowHourly(String deviceId, Integer channelNo, Long date, String token) {

        String url = yingshiUrl + "/api/lapp/passengerflow/hourly";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        map.put("accessToken", token);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }else {
            map.put("channelNo", "1");

        }
        map.put("date", date.toString());
        String resultStr = CoolHttpClient.sendPostFormRequest(url, map);

        JSONObject result = JSONObject.parseObject(resultStr);
        if (OK.equals(result.getInteger("code"))) {
            return result.getObject("data", new TypeReference<List<PassengerFlowHourlyDTO>>() {
            });
        }
        return null;
    }

    @Override
    public Boolean savePassengerFlowConfig(String deviceId, Integer channelNo, String line, String direction, String token) {

        String url = yingshiUrl + "/api/lapp/passengerflow/config/set";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        map.put("accessToken", token);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }
        map.put("line", line);
        map.put("direction", direction);
        String resultStr = YingshiHttpClient.post(url, map);
        return true;
    }

    @Override
    public PassengerFlowConfigDTO getPassengerFlowConfig(String deviceId, Integer channelNo, String token) {

        String url = yingshiUrl + "/api/lapp/passengerflow/config/get";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        map.put("accessToken", token);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }
        String resultStr = YingshiHttpClient.post(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        if (OK.equals(result.getInteger("code"))) {
            return result.getObject("data", new TypeReference<PassengerFlowConfigDTO>() {
            });
        }
        return null;
    }
    @Override
    public String capture(String deviceId, Integer channelNo, String token) {
        String url = yingshiUrl + "/api/lapp/device/capture";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        if (channelNo != null) {
            map.put("channelNo", channelNo.toString());
        }
        map.put("accessToken", token);
        String resultStr = YingshiHttpClient.post(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        return result.getJSONObject("data").getString("picUrl");
    }

    @Override
    public DeviceCapacityDTO getYingshiDeviceCapaticy(String deviceId, String token) {
        try {
            String url = yingshiUrl + "/api/lapp/device/capacity";
            Map<String, String> map = new HashMap<>(2);
            map.put("deviceSerial", deviceId);
            map.put("accessToken", token);
            String resultStr = YingshiHttpClient.post(url, map);
            JSONObject result = JSONObject.parseObject(resultStr);
            if ( OK.equals(result.getInteger("code"))) {
                log.info("YingshiDeviceCapaticyDTO-->{}",JSONObject.toJSONString(result));
                return result.getObject("data", DeviceCapacityDTO.class);
            }
        }catch (Exception e){
            log.info("数据获取异常",e);
        }
        return null;
    }

    @Override
    public Boolean cancelAuth(String deviceSerials, String token) {
        try {
            String url = yingshiUrl + "/api/lapp/trust/cancel";
            Map<String, String> map = new HashMap<>(2);
            map.put("deviceSerials", deviceSerials);
            map.put("accessToken", token);
            String resultStr = YingshiHttpClient.post(url, map);
            JSONObject result = JSONObject.parseObject(resultStr);
            if ( OK.equals(result.getInteger("code"))) {
                return true;
            }
        }catch (Exception e){
            log.info("#####yingshi.cancelAuth取消授权异常",e);
        }
        return false;
    }

    @Override
    public String getAccessToken() {
        String token = redisUtil.getString(getYingshiAccessTokenKey());
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        //有效期设置6天
        token = getYingshiAccessToken(yingshiAppKey, yingshiSecret);
        redisUtil.setString(getYingshiAccessTokenKey(), token, 6*24*60*60);
        return token;
    }

    private String getYingshiAccessToken(String appKey, String appSecret) {
        String url = yingshiUrl + "/api/lapp/token/get";
        Map<String, String> map = new HashMap<>(4);
        map.put("appKey", appKey);
        map.put("appSecret", appSecret);
        String resultStr = YingshiHttpClient.post(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        return result.getJSONObject("data").getString("accessToken");
    }

    private String getYingshiAccessTokenKey() {
        return YINGSHI_ACCESS_TOKEN;
    }

}
