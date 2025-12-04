package com.coolcollege.intelligent.service.video.openapi.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import com.coolcollege.intelligent.common.http.YingshiHttpClient;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.DeviceGBLicenseApplyDTO;
import com.coolcollege.intelligent.model.device.gb28181.Channel;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.YingshiChannelDTO;
import com.coolcollege.intelligent.service.device.gb28181.GB28181Service;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: YingshiyunGbOpenServiceImpl
 * @Description:
 * @date 2022-12-16 14:15
 */
@Slf4j
@Service
public class YingShiGbOpenServiceImpl extends YingShiOpenServiceImpl implements GB28181Service {

    @Override
    public String getAccessToken(String eid, AccountTypeEnum accountType) {
        log.info("eid:{}, accountType:{}", eid, accountType.getCode());
        String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, eid, accountType.getCode(), getYunTypeNum().getCode());
        String accessToken = redisUtilPool.getString(cacheKey);
        if(StringUtils.isNotBlank(accessToken)){
            return accessToken;
        }
        String appKey = null, appSecret = null;
        if(AccountTypeEnum.PLATFORM.equals(accountType)){
            appKey = yingshiAppKey;
            appSecret = yingshiSecret;
        }else{
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(eid, getYunTypeNum().getCode(), accountType.getCode());
            appKey = videoSetting.getAccessKeyId();
            appSecret = videoSetting.getSecret();
            DataSourceHelper.changeToSpecificDataSource(dbName);
        }
        String url = yingshiUrl + "/api/lapp/token/get";
        Map<String, String> map = new HashMap<>(4);
        map.put("appKey", appKey);
        map.put("appSecret", appSecret);
        String resultStr = YingshiHttpClient.post(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        if(Objects.isNull(result.getJSONObject("data"))){
            log.info("获取accessToken失败：{}", resultStr);
            result = JSONObject.parseObject(resultStr);
        }
        Long expireTime = result.getJSONObject("data").getLong("expireTime");
        accessToken = result.getJSONObject("data").getString("accessToken");
        redisUtilPool.setString(cacheKey, accessToken, (int)(expireTime - System.currentTimeMillis())/1000);
        return accessToken;
    }


    @Override
    public YunTypeEnum getYunTypeNum() {
        return YunTypeEnum.YINGSHIYUN_GB;
    }


    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {
        try {
            String deviceSerial = device.getDeviceId();
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            String url = yingshiUrl + "/api/lapp/device/delete";
            Map<String, String> map = new HashMap<>(2);
            map.put("deviceSerial", deviceSerial);
            map.put("accessToken", accessToken);
            String resultStr = YingshiHttpClient.post(url, map);
            JSONObject result = JSONObject.parseObject(resultStr);
            if (OK.equals(result.getInteger("code"))) {
                log.info("主账号设备取消授权成功：{}", device.getDeviceId());
            }
        }catch (Exception e){
            log.info("#####yingshi.cancelAuth取消授权异常",e);
        }
    }

    @Override
    public List<YingshiChannelDTO> getChannelList(String eid, String deviceId, AccountTypeEnum accountType) {
        String url = yingshiUrl + "/api/lapp/device/camera/list";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", deviceId);
        map.put("accessToken", getAccessToken(eid, accountType));
        String resultStr = CoolHttpClient.sendPostFormRequest(url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        log.info("getChannelList:device={},result={}", deviceId, result);
        if (result != null && OK.equals(result.getInteger("code"))) {
            List<YingshiChannelDTO> deviceList = result.getObject("data", new TypeReference<List<YingshiChannelDTO>>() {});
            return ListUtils.emptyIfNull(deviceList).stream().filter(o-> Objects.nonNull(o.getRelatedIpc()) && o.getRelatedIpc()).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public String license(String eid, AccountTypeEnum accountType, DeviceGBLicenseApplyDTO applyDTO) {
        String url = yingshiUrl + "/api/service/gb/v3/deviceLicense/apply";
        Map<String, String> headers = Collections.singletonMap("accessToken", getAccessToken(eid, accountType));
        Map<String, Object> form = new HashMap<>();
        form.put("reqId", UUIDUtils.get32UUID());
        form.put("deviceCategory", "IPC".equals(applyDTO.getDeviceCategory()) ? 0 : 1);
        form.put("randomDeviceCode", true);
        form.put("deviceName", applyDTO.getDeviceName());
        form.put("deviceLicense", applyDTO.getPassword());
        if ("NVR".equals(applyDTO.getDeviceCategory())) {
            form.put("channelCount", 0);
        }
        String responseStr = YingshiHttpClient.postForm(url, headers, form);
        JSONObject response = JSONObject.parseObject(responseStr);
        return response.getString("data");
    }

    @Override
    public List<Channel> channel(String eid, AccountTypeEnum accountType, List<Channel> channels) {
        for (Channel channel : channels) {
            String url = yingshiUrl + "/api/service/gb/v3/channelLicense/apply";
            Map<String, String> headers = Collections.singletonMap("accessToken", getAccessToken(eid, accountType));
            Map<String, Object> form = new HashMap<>();
            form.put("reqId", UUIDUtils.get32UUID());
            form.put("randomDeviceCode", true);
            form.put("channelNo", channel.getChannelNo());
            form.put("channelName", channel.getChannelName());
            form.put("belongToDeviceCode", channel.getBelongToDeviceCode());
            String responseStr = YingshiHttpClient.postForm(url, headers, form);
            JSONObject response = JSONObject.parseObject(responseStr);
            channel.setChannelSerial(response.getString("data"));
        }
        return channels;
    }

    @Override
    public Integer deviceStatus(String eid, AccountTypeEnum accountType, String deviceSerial, Integer deviceType) {
        String url = String.format("%s/api/service/gb/v3/devices/page?pageIndex=0&pageSize=10&deviceSerial=%s&type=%d", yingshiUrl, deviceSerial, Constants.INDEX_ONE.equals(deviceType) ? 0 : 1);
        Map<String, String> headers = Collections.singletonMap("accessToken", getAccessToken(eid, accountType));
        String responseStr = YingshiHttpClient.get(url, headers);
        JSONObject response = JSONObject.parseObject(responseStr);
        JSONObject data = response.getJSONObject("data");
        if (Objects.nonNull(data)) {
            JSONArray dataArray = data.getJSONArray("data");
            Integer total = data.getInteger("total");
            if (Objects.isNull(total) || total == 0) {
                throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_FOUND);
            }
            return dataArray.getJSONObject(0).getInteger("status");
        }
        throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
    }

    @Override
    public boolean deleteGBDevice(String eid, AccountTypeEnum accountType, String deviceSerial) {
        String accessToken = getAccessToken(eid, accountType);
        String url = yingshiUrl + "/api/lapp/device/delete";
        Map<String, Object> map = new HashMap<>(2);
        map.put("deviceSerial", deviceSerial);
        map.put("accessToken", accessToken);
        YingshiHttpClient.postForm(url, null, map);
        return true;
    }

    @Override
    public List<Channel> channelStatus(String enterpriseId, AccountTypeEnum accountType, String deviceSerial) {
        String url = yingshiUrl + "/api/lapp/device/camera/list";
        Map<String, Object> map = new HashMap<>(4);
        map.put("deviceSerial", deviceSerial);
        map.put("accessToken", getAccessToken(enterpriseId, accountType));
        String responseStr = YingshiHttpClient.postForm(url, null, map);
        JSONObject response = JSONObject.parseObject(responseStr);
        List<YingshiChannelDTO> data = response.getObject("data", new TypeReference<List<YingshiChannelDTO>>() {});
        return CollStreamUtil.toList(data, v -> Channel.builder().status(v.getStatus()).channelNo(v.getChannelNo()).build());
    }
}
