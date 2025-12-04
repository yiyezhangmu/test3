package com.coolcollege.intelligent.service.video.openapi.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceCatalog;
import com.coolcollege.intelligent.common.enums.device.DeviceEncryptEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.CoolHttpClientResult;
import com.coolcollege.intelligent.common.http.YingshiHttpClient;
import com.coolcollege.intelligent.common.http.YunShiTongHttpClient;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.EnterpriseDeviceInfoDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.device.vo.DeviceVideoRecordVO;
import com.coolcollege.intelligent.model.device.vo.DeviceSoftHardwareInfoVO;
import com.coolcollege.intelligent.model.device.vo.DeviceStorageInfoVO;
import com.coolcollege.intelligent.model.enums.YingShiTaskStatusEnum;
import com.coolcollege.intelligent.model.enums.YingshiErrorCodeEnum;
import com.coolcollege.intelligent.model.enums.YingshiStorageStatusEnum;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.video.TaskFileDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.*;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.openapi.VideoOpenService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.VideoProtocolTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/12/13 14:36
 * @Version 1.0
 */
@Slf4j
@Service
public class YingShiOpenServiceImpl implements VideoOpenService {

    @Value("${yingshi.openAuth.url}")
    protected String yingshiAuthUrl;
    @Value("${yingshi.url}")
    protected String yingshiUrl;
    @Value("${yingshi.appKey}")
    protected String yingshiAppKey;
    @Value("${yingshi.secret}")
    protected String yingshiSecret;
    protected final static Integer OK = 200;

    @Resource
    protected EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Resource
    private YunShiTongHttpClient yunShiTongHttpClient;
    @Resource
    protected RedisUtilPool redisUtilPool;
    @Resource
    private DeviceMapper deviceMapper;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 时间格式器
     */
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final int MAX_RETRY_TIMES = 3;

    @Override
    public YunTypeEnum getYunTypeNum() {
        return YunTypeEnum.YINGSHIYUN;
    }

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
        String url = yingshiUrl + "/api/lapp/trust/device/v2/token/get";
        Map<String, String> map = new HashMap<>(4);
        map.put("appKey", appKey);
        map.put("appSecret", appSecret);
        String resultStr = post(eid, accountType, url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        Long expireTime = result.getJSONObject("data").getLong("expireTime");
        accessToken = result.getJSONObject("data").getString("accessToken");
        redisUtilPool.setString(cacheKey, accessToken, (int)(expireTime - System.currentTimeMillis())/1000);
        return accessToken;
    }

    @Override
    public OpenDeviceDTO getDeviceDetail(String eid, String deviceId, AccountTypeEnum accountType) {
        String accessToken = getAccessToken(eid, accountType);
        String url = yingshiUrl + "/api/lapp/device/info";
        Map<String, String> map = new HashMap<>(4);
        map.put("accessToken", accessToken);
        map.put("deviceSerial", deviceId);

        String resultStr = post(eid, accountType, url, map);
        log.info("yingshiDeviceInfo.getYingshiDeviceInfo accountType:{}, accessToken:{}, result={}",accountType, accessToken, resultStr);
        if (StringUtils.isBlank(resultStr)) {
            return null;
        }
        JSONObject result = JSONObject.parseObject(resultStr);
        YingshiDeviceDTO data = new YingshiDeviceDTO();
        if (OK.equals(result.getInteger("code"))) {
             data = result.getObject("data", YingshiDeviceDTO.class);
        }else{
            String msg = result.getString("msg");
            log.info("getDeviceDetail:{}, errorMessage:{}",deviceId, msg);
            throw new ServiceException(ErrorCodeEnum.OPEN_API_DEVICE_ERROR, msg);
        }
        OpenDeviceDTO openDeviceDTO = new OpenDeviceDTO();
        openDeviceDTO.setDeviceId(data.getDeviceSerial());
        openDeviceDTO.setDeviceName(data.getDeviceName());
        openDeviceDTO.setModel(data.getModel());
        openDeviceDTO.setDeviceStatus(Constants.INDEX_ONE.equals(data.getStatus()) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
        openDeviceDTO.setSource(getYunTypeNum().getCode());
        DeviceCapacityDTO deviceCapacity = getDeviceCapacity(eid, deviceId, accountType);
        openDeviceDTO.setDeviceCapacity(deviceCapacity);
        Integer supportCapture = Optional.ofNullable(deviceCapacity).map(DeviceCapacityDTO::getSupportCapture).orElse(Constants.ZERO);
        Boolean supportPassenger = Optional.ofNullable(deviceCapacity).map(o->Constants.INDEX_ONE.equals(o.getSupportFlowStatistics())).orElse(false);
        openDeviceDTO.setSupportCapture(supportCapture);
        openDeviceDTO.setSupportPassenger(supportPassenger);
        openDeviceDTO.setHasPtz(YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzLeftRight()));
        List<YingshiChannelDTO> channelList = getChannelList(eid, deviceId, accountType);
        if(CollectionUtils.isNotEmpty(channelList) && channelList.size() > Constants.INDEX_ONE){
            openDeviceDTO.setHasChildDevice(true);
            List<OpenChannelDTO> channelListDTO = new ArrayList<>();
            for (YingshiChannelDTO yingshiChannelDTO : channelList) {
                OpenChannelDTO channel = new OpenChannelDTO();
                channel.setParentDeviceId(yingshiChannelDTO.getDeviceSerial());
                channel.setDeviceId(yingshiChannelDTO.getIpcSerial());
                channel.setChannelNo(yingshiChannelDTO.getChannelNo());
                channel.setChannelName(yingshiChannelDTO.getChannelName());
                channel.setStatus(Constants.INDEX_ONE.equals(yingshiChannelDTO.getStatus()) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                channel.setSource(getYunTypeNum().getCode());
                channel.setDeviceCapacity(deviceCapacity);
                channelListDTO.add(channel);
            }
            openDeviceDTO.setChannelList(channelListDTO);
        }
        return openDeviceDTO;
    }

    @Override
    public PageInfo<OpenDevicePageDTO> getDeviceList(String eid, AccountTypeEnum accountType, Integer pageNum, Integer pageSize) {
        String accessToken = getAccessToken(eid, accountType);
        String url = yingshiUrl + "/api/lapp/device/list";
        Map<String, String> map = new HashMap<>(4);
        map.put("pageStart", String.valueOf(pageNum));
        map.put("pageSize", String.valueOf(pageSize));
        map.put("accessToken", accessToken);
        String resultStr = CoolHttpClient.sendPostFormRequest(url, map);
        log.info("getDeviceList result={}",resultStr);
        PageInfo pageInfo = new PageInfo();
        JSONObject result = JSONObject.parseObject(resultStr);
        if (OK.equals(result.getInteger("code"))) {
            JSONObject page = result.getJSONObject("page");
            Integer total = page.getInteger("total");
            pageInfo.setTotal(total);
            pageInfo.setPageSize(pageSize);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPages(total % pageSize == 0 ? total / pageSize : total / pageSize + 1);
            JSONArray dataArray = result.getJSONArray("data");
            List<OpenDevicePageDTO> list = new ArrayList<>();
            if(Objects.nonNull(dataArray) && !dataArray.isEmpty()){
                for (Object device : dataArray) {
                    JSONObject deviceObj = JSONObject.parseObject(JSONObject.toJSONString(device));
                    OpenDevicePageDTO pageDevice = new OpenDevicePageDTO();
                    pageDevice.setDeviceId(deviceObj.getString("deviceSerial"));
                    pageDevice.setDeviceName(deviceObj.getString("deviceName"));
                    pageDevice.setHasChildDevice(DeviceCatalog.NVR.getCode().equals(deviceObj.getString("deviceType")));
                    pageDevice.setSource(getYunTypeNum().getCode());
                    pageDevice.setDeviceStatus(Constants.INDEX_ONE.equals(deviceObj.getInteger("status"))  ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                    list.add(pageDevice);
                }
            }
            pageInfo.setList(list);
        }
        return pageInfo;
    }

    public PageInfo<OpenTrustDevicePageDTO> getTrustDeviceList(String eid, String accessToken, Integer pageNum, Integer pageSize) {
        String url = yingshiUrl + "/api/lapp/trust/device/list";
        Map<String, String> map = new HashMap<>(4);
        map.put("pageStart", String.valueOf(pageNum));
        map.put("pageSize", String.valueOf(pageSize));
        map.put("version", "3.0");
        map.put("accessToken", accessToken);
        log.info("getTrustDeviceList:request map={}", JSONObject.toJSONString(map));
        JSONObject result = new JSONObject();
        PageInfo pageInfo = new PageInfo();
        while (!OK.equals(result.getInteger("code"))){
            String resultStr = CoolHttpClient.sendPostFormRequest(url, map);
            log.info("getTrustDeviceList result={}",resultStr);
            result = JSONObject.parseObject(resultStr);
        }
        if (OK.equals(result.getInteger("code"))) {
            JSONObject page = result.getJSONObject("page");
            Integer total = page.getInteger("total");
            pageInfo.setTotal(total);
            pageInfo.setPageSize(pageSize);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPages(total % pageSize == 0 ? total / pageSize : total / pageSize + 1);
            JSONArray dataArray = result.getJSONArray("data");
            List<OpenTrustDevicePageDTO> list = new ArrayList<>();
            if(Objects.nonNull(dataArray) && !dataArray.isEmpty()){
                List<OpenTrustDevicePageDTO> deviceList = dataArray.toJavaList(OpenTrustDevicePageDTO.class);
                if(CollectionUtils.isNotEmpty(deviceList)){
                    list.addAll(deviceList);
                }
            }
            pageInfo.setList(list);
        }
        return pageInfo;
    }

    @Override
    public LiveVideoVO getLiveUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        return getPastVideoUrl(enterpriseId,device,param);
    }

    @Override
    public LiveVideoVO getPastVideoUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String channelNo = param.getChannelNo(), startTime = param.getStartTime(), endTime = param.getEndTime();
        VideoProtocolTypeEnum protocolTypeEnum = param.getProtocol();
        Integer quality = param.getQuality();

        String url = yingshiUrl + "/api/lapp/v2/live/address/get";
        Map<String, String> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("deviceSerial", device.getDeviceId());
        if(Objects.nonNull(param.getExpireTime())){
            map.put("expireTime", String.valueOf(param.getExpireTime()));
        }
        if (protocolTypeEnum != null) {
            map.put("protocol", VideoProtocolTypeEnum.getYingShiYunProtocol(protocolTypeEnum));
        } else {
            map.put("protocol", "3");
        }
        //是否有通道号
        if (channelNo != null) {
            map.put("channelNo", StringUtils.isBlank(channelNo) || Integer.parseInt(channelNo) == 0 ? "1" : channelNo);
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
        if(Objects.nonNull(param.getSupportH265())){
            map.put("supportH265", param.getSupportH265());
        }
        map.put("quality", quality.toString());
        LiveVideoVO liveVideoVO = new LiveVideoVO();
        liveVideoVO.setToken(accessToken);
        //直播流获取
        if (StringUtils.equals(map.get("type"), "1")) {
            log.info("request1:{}", JSONObject.toJSONString(map));
            String yunResultStr = post(enterpriseId, accountType, url, map);
            log.info("response3:{}", yunResultStr);
            JSONObject yunResult = JSONObject.parseObject(yunResultStr);
            liveVideoVO.setUrl(yunResult.getJSONObject("data").getString("url"));
            liveVideoVO.setExpireTime(yunResult.getJSONObject("data").getLong("expireTime"));
            return liveVideoVO;
        } else {
            //回放获取：先获取本地录像，如果本地录像没有则获取云存录像map = {HashMap@33165}  size = 8
            map.put("type", "2");
            log.info("request2:{}", JSONObject.toJSONString(map));
            String resultStr = CoolHttpClient.sendPostFormRequest(url, map);
            log.info("response2:{}", resultStr);
            JSONObject yunResult = JSONObject.parseObject(resultStr);

            if (yunResult != null && OK.equals(yunResult.getInteger("code"))) {
                liveVideoVO.setUrl(yunResult.getJSONObject("data").getString("url"));
                liveVideoVO.setExpireTime(yunResult.getJSONObject("data").getLong("expireTime"));
                return liveVideoVO;
            }
            map.put("type", "3");
            log.info("request3:{}", JSONObject.toJSONString(map));
            String yunResultStr = post(enterpriseId, accountType, url, map);
            log.info("response3:{}", resultStr);
            JSONObject recordResult = JSONObject.parseObject(yunResultStr);
            liveVideoVO.setUrl(recordResult.getJSONObject("data").getString("url"));
            liveVideoVO.setExpireTime(recordResult.getJSONObject("data").getLong("expireTime"));
            return liveVideoVO;
        }
    }

    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {
        try {
            String deviceSerial = device.getDeviceId();
            StringJoiner requestDeviceSerial = new StringJoiner(Constants.COMMA);
            List<String> channelNoList = new ArrayList<>();
           if(CollectionUtils.isEmpty(channelList)){
               channelNoList = new ArrayList<>(Arrays.asList("0","1"));
           }else {
               channelNoList = channelList.stream().map(DeviceChannelDO::getChannelNo).collect(Collectors.toList());
           }
            channelNoList.forEach(channelNo -> {
               requestDeviceSerial.add(deviceSerial + ":" + channelNo);
           });
            AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
            String accessToken = getAccessToken(enterpriseId, accountType);
            String url = yingshiUrl + "/api/lapp/trust/cancel";
            Map<String, String> map = new HashMap<>(2);
            map.put("deviceSerials", requestDeviceSerial.toString());
            map.put("accessToken", accessToken);
            String resultStr = post(enterpriseId, accountType, url, map);
            JSONObject result = JSONObject.parseObject(resultStr);
            if ( OK.equals(result.getInteger("code"))) {
                log.info("取消授权成功：{}", device.getDeviceId());
            }
        }catch (Exception e){
            log.info("#####yingshi.cancelAuth取消授权异常",e);
        }
    }

    public List<String> cancelAuth(String accessToken, List<EnterpriseDeviceInfoDO> deviceList) {
        try {
            StringJoiner requestDeviceSerial = new StringJoiner(Constants.COMMA);
            for (EnterpriseDeviceInfoDO deviceInfo : deviceList) {
                if("ipc".equals(deviceInfo.getDeviceType())){
                    requestDeviceSerial.add(deviceInfo.getDeviceId() + ":" + 0);
                    requestDeviceSerial.add(deviceInfo.getDeviceId() + ":" + 1);
                }
                if("nvr_ipc".equals(deviceInfo.getDeviceType())){
                    requestDeviceSerial.add(deviceInfo.getParentDeviceId() + ":" + deviceInfo.getChannelNo());
                }
            }
            String url = yingshiUrl + "/api/lapp/trust/cancel";
            Map<String, String> map = new HashMap<>(2);
            map.put("deviceSerials", requestDeviceSerial.toString());
            map.put("accessToken", accessToken);
            String resultStr = YingshiHttpClient.post(url, map);
            JSONObject result = JSONObject.parseObject(resultStr);
            List<String> errorList = new ArrayList<>();
            if (!result.getJSONArray("data").isEmpty()) {
                JSONArray data = result.getJSONArray("data");
                for (Object datum : data) {
                    JSONObject item = (JSONObject) datum;
                    if(!item.getBoolean("isSuccess")){
                        errorList.add(item.getString("deviceSerial"));
                    }
                }
            }
            log.info("cancelAuth:{}",resultStr);
            return errorList;
        }catch (Exception e){
            log.info("#####yingshi.cancelAuth取消授权异常",e);
        }
        return null;
    }

    @Override
    public Boolean ptzStart(String eid, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(eid, accountType);
        String url = yingshiUrl + "/api/lapp/device/ptz/start";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", device.getDeviceId());
        if (channelNo != null) {
            map.put("channelNo", channelNo);
        }else {
            map.put("channelNo", "1");
        }
        map.put("direction", command.toString());
        map.put("speed", speed.toString());
        map.put("accessToken", accessToken);
        post(eid, accountType, url, map);
        return true;
    }

    @Override
    public Boolean ptzStop(String eid, DeviceDO device, String channelNo) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(eid, accountType);
        String url = yingshiUrl + "/api/lapp/device/ptz/stop";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", device.getDeviceId());
        if (StringUtils.isNotBlank(channelNo)) {
            map.put("channelNo", channelNo);
        }else {
            map.put("channelNo", "1");
        }
        map.put("accessToken", accessToken);
        String resultStr = post(eid, accountType, url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        return true;
    }

    @Override
    public String addPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String devicePositionName) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/lapp/device/preset/add";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", device.getDeviceId());
        if (channelNo != null) {
            map.put("channelNo", channelNo);
        }else {
            map.put("channelNo", "1");
        }
        map.put("accessToken", accessToken);
        String resultStr = post(enterpriseId, accountType, url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        return result.getJSONObject("data").getInteger("index").toString();
    }

    @Override
    public Boolean deletePtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/lapp/device/preset/clear";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", device.getDeviceId());
        if (channelNo != null) {
            map.put("channelNo", channelNo);
        }else {
            map.put("channelNo", "1");
        }
        map.put("index", presetIndex.toString());
        map.put("accessToken", accessToken);
        String resultStr = post(enterpriseId, accountType, url, map);
        return Boolean.TRUE;
    }

    @Override
    public Boolean loadPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/lapp/device/preset/move";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", device.getDeviceId());
        if (channelNo != null) {
            map.put("channelNo", channelNo);
        }else {
            map.put("channelNo", "1");
        }
        map.put("index", presetIndex);
        map.put("accessToken", accessToken);
        String resultStr = post(enterpriseId, accountType, url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        return Boolean.TRUE;
    }

    @Override
    public String capture(String eid, DeviceDO device, String channelNo, String quality) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(eid, accountType);
        String url = yingshiUrl + "/api/lapp/device/capture";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", device.getDeviceId());
        if (channelNo != null) {
            map.put("channelNo", Integer.valueOf(channelNo) == 0 ? "1" : channelNo);
        }else {
            map.put("channelNo", "1");
        }
        map.put("accessToken", accessToken);
        String resultStr = post(eid, accountType, url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        return result.getJSONObject("data").getString("picUrl");
    }

    @Override
    public String captureByTime(String enterpriseId, DeviceDO deviceDO, String channelNo, List<String> captureTimes) {
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(deviceDO.getAccountType()));
            String projectId = getProjectId(enterpriseId, deviceDO);
            String url = yingshiUrl + "/api/v3/open/cloud/video/frame/timing/start";
            Map<String, Object> map = new HashMap<>();
            map.put("accessToken", accessToken);
            map.put("projectId", projectId);
            map.put("deviceSerial", deviceDO.getDeviceId());
            map.put("channelNo", StringUtils.isBlank(channelNo) ? "1" : ("0".equals(channelNo) ? "1" : channelNo));
            map.put("recType", "local");
            if(deviceDO.getDeviceId().startsWith("33011055992287789105")){
                map.put("devProto", "gb28181");
            }
            String timingPoints = captureTimes.stream().map(time -> time.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", ""))
                    .collect(Collectors.joining(Constants.COMMA));
            map.put("timingPoints", timingPoints);
            log.info("request:{}", JSONObject.toJSONString(map));
            cn.hutool.http.HttpRequest httpRequest = HttpUtil.createPost(url);
            httpRequest.form(map);
            HttpResponse httpResponse = httpRequest.execute();
            if (Objects.nonNull(httpResponse) && httpResponse.getStatus() == HttpStatus.OK.value()) {
                log.info("设备id:{}, captureByTime:{}", deviceDO.getDeviceId(), JSONObject.toJSONString(httpResponse.body()));
                JSONObject body = JSONObject.parseObject(httpResponse.body());
                if (Objects.nonNull(body)) {
                    JSONObject meta = body.getJSONObject("meta");
                    if (Objects.nonNull(meta) && meta.getInteger("code") == HttpStatus.OK.value()) {
                        JSONObject data = body.getJSONObject("data");
                        String taskId = data.getString("taskId");
                        log.info("视频抽帧，taskId:{}", taskId);
                        return taskId;
                    }
                }
            }
        } catch (Exception e) {
            log.error("萤石云时间点抽帧失败", e);
        }
        return null;
    }

    @Override
    public PassengerFlowConfigDTO getPassengerFlowConfig(String enterpriseId, DeviceDO device, Integer channelNo) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/lapp/passengerflow/config/get";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", device.getDeviceId());
        map.put("accessToken", accessToken);
        if (channelNo != null) {
            map.put("channelNo", String.valueOf(channelNo));
        }else {
            map.put("channelNo", "1");
        }
        String resultStr = post(enterpriseId, accountType, url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        if (OK.equals(result.getInteger("code"))) {
            return result.getObject("data", new TypeReference<PassengerFlowConfigDTO>() {
            });
        }
        return null;
    }

    @Override
    public PassengerFlowSwitchStatusDTO passengerFlowSwitchStatus(String enterpriseId, DeviceDO device) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/lapp/passengerflow/switch/status";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", device.getDeviceId());
        map.put("accessToken", accessToken);
        String resultStr = post(enterpriseId, accountType, url, map);
        JSONObject result = JSONObject.parseObject(resultStr);
        if (OK.equals(result.getInteger("code"))) {
            return result.getObject("data", new TypeReference<PassengerFlowSwitchStatusDTO>() {
            });
        }
        return null;
    }

    @Override
    public Boolean savePassengerFlow(String enterpriseId, DeviceDO device, String channelNo, Boolean enable) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/lapp/passengerflow/switch/set";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", device.getDeviceId());
        if (channelNo != null) {
            map.put("channelNo", channelNo);
        }else {
            map.put("channelNo", "1");
        }
        map.put("enable",enable?"1":"0");
        map.put("accessToken", accessToken);
        String resultStr = post(enterpriseId, accountType, url, map);
        return true;
    }

    @Override
    public Boolean savePassengerFlowConfig(String enterpriseId, DeviceDO device, String channelNo, String line, String direction) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/lapp/passengerflow/config/set";
        Map<String, String> map = new HashMap<>(4);
        map.put("deviceSerial", device.getDeviceId());
        map.put("accessToken", accessToken);
        if (channelNo != null) {
            map.put("channelNo", channelNo);
        }else {
            map.put("channelNo", "1");
        }
        map.put("line", line);
        map.put("direction", direction);
        post(enterpriseId, accountType, url, map);
        return true;
    }

    @Override
    public String videoTransCode(String enterpriseId,  DeviceDO device, VideoDTO param) {
        String startTime = param.getStartTime(), endTime = param.getEndTime(), channelNo = param.getChannelNo();
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            String projectId = getProjectId(enterpriseId, device);
            String url = yingshiUrl + "/api/open/cloud/v1/rec/video/save";
            Map<String, Object> map = new HashMap<>();
            map.put("recType", "local");
            map.put("startTime", startTime.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", ""));
            map.put("endTime", endTime.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", ""));
            map.put("sliceDuration", 30);
            map.put("projectId", projectId);
            if (YunTypeEnum.YINGSHIYUN_GB.equals(getYunTypeNum())) {
                map.put("devProto", "gb28181");
            }

            cn.hutool.http.HttpRequest httpRequest = HttpUtil.createPost(url);
            httpRequest.header("accessToken", accessToken);
            httpRequest.header("Content-Type", "application/x-www-form-urlencoded");
            httpRequest.header("deviceSerial", device.getDeviceId());
            if (StringUtils.isNotBlank(channelNo)) {
                httpRequest.header("localIndex", Integer.valueOf(channelNo) == 0 ? "1" : channelNo);
            }
            httpRequest.form(map);
            HttpResponse httpResponse = httpRequest.execute();
            if (Objects.nonNull(httpResponse) && httpResponse.getStatus() == HttpStatus.OK.value()) {
                log.info("设备id:{}, videoTransCode:{}", device.getDeviceId(), JSONObject.toJSONString(httpResponse.body()));
                JSONObject body = JSONObject.parseObject(httpResponse.body());
                if (Objects.nonNull(body)) {
                    JSONObject meta = body.getJSONObject("meta");
                    if (Objects.nonNull(meta) && meta.getInteger("code") == HttpStatus.OK.value()) {
                        JSONObject data = body.getJSONObject("data");
                        String taskId = data.getString("taskId");
                        log.info("萤石新建视频回放下载任务，taskId:{}", taskId);
                        return taskId;
                    } else {
                        throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000001, meta.getString("message"));
                    }
                } else {
                    throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
                }
            }
        } catch (ServiceException e) {
          throw e;
        } catch (Exception e) {
            log.error("下载视频回放异常：", e);
            throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000000);
        }
        return "";
    }

    /**
     * 获取项目id
     * @param enterpriseId 企业id
     * @param device 设备信息
     * @return 项目id
     */
    private String getProjectId(String enterpriseId, DeviceDO device) {
        String storeId = Optional.ofNullable(device.getBindStoreId()).orElse("default_project");
        String cacheKey = MessageFormat.format(RedisConstant.YINGSHI_VIDEO_PROJECT_ID_KEY, enterpriseId, storeId);
        String projectId = redisUtilPool.getString(cacheKey);
        if (StringUtils.isNotBlank(projectId)) {
            return projectId;
        }
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            String url = yingshiUrl + "/api/open/cloud/v1/project/" + storeId;
            Map<String, Object> map = new HashMap<>();
            map.put("accessToken", accessToken);
            map.put("projectName", storeId);
//            map.put("expireDays", 30);
            cn.hutool.http.HttpRequest httpRequest = HttpUtil.createPost(url);
            httpRequest.form(map);
            HttpResponse httpResponse = httpRequest.execute();
            if (Objects.nonNull(httpResponse) && httpResponse.getStatus() == HttpStatus.OK.value()) {
                log.info("设备id:{}, getProjectId:{}", device.getDeviceId(), JSONObject.toJSONString(httpResponse.body()));
                JSONObject body = JSONObject.parseObject(httpResponse.body());
                if (Objects.nonNull(body)) {
                    JSONObject meta = body.getJSONObject("meta");
                    int code = meta.getIntValue("code");
                    String message = meta.getString("message");
                    if (HttpStatus.OK.value() == code || message.endsWith("已存在")) {
                        projectId = storeId;
                    } else {
                        throw new ServiceException(ErrorCodeEnum.API_ERROR);
                    }
                    redisUtilPool.setString(cacheKey, projectId);
                    return projectId;
                }
            }
        } catch (Exception e) {
            log.error("获取项目id异常，", e);
        }
        throw new ServiceException(ErrorCodeEnum.API_ERROR);
    }

    @Override
    public VideoFileDTO getVideoFile(String enterpriseId,DeviceDO device, String fileId) {
        VideoFileDTO videoFileDTO = new VideoFileDTO();
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            String url = yingshiUrl + "/api/v3/open/cloud/task/" + fileId;
            cn.hutool.http.HttpRequest httpRequest = HttpUtil.createGet(url);
            httpRequest.header("accessToken", accessToken);
            HttpResponse httpResponse = httpRequest.execute();
            if (Objects.nonNull(httpResponse) && httpResponse.getStatus() == HttpStatus.OK.value()) {
                log.info("设备id:{}, getVideoFile:{}", device.getDeviceId(), JSONObject.toJSONString(httpResponse.body()));
                JSONObject body = JSONObject.parseObject(httpResponse.body());
                if (Objects.nonNull(body)) {
                    JSONObject data = body.getJSONObject("data");
                    Integer taskStatus = data.getInteger("taskStatus");
                    String errorCode = data.getString("errorCode");
                    videoFileDTO.setErrorMsg(YingshiErrorCodeEnum.getMsgByCode(errorCode));
                    videoFileDTO.setErrorCode(errorCode);
                    videoFileDTO.setStatus(taskStatusTransToVideoFieldStatus(taskStatus));
                }
            }
        } catch (Exception e) {
            log.error("萤石云获取任务状态异常：", e);
        }
        return videoFileDTO;
    }

    /**
     * 萤石云任务状态转换
     * @param taskStatus 任务状态
     * @return 状态
     */
    private Integer taskStatusTransToVideoFieldStatus(Integer taskStatus) {
        if (YingShiTaskStatusEnum.COMPLETE.getCode().equals(taskStatus)) {
            return 0;
        } else if (YingShiTaskStatusEnum.WAITING.getCode().equals(taskStatus) || YingShiTaskStatusEnum.PROCESSING.getCode().equals(taskStatus) || YingShiTaskStatusEnum.NOT_START.getCode().equals(taskStatus)) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public List<String> getVideoDownloadUrl(String enterpriseId,DeviceDO device, String fileId) {
        List<JSONObject> fileList = getFileByTaskId(enterpriseId, device, fileId);
        if (CollectionUtils.isNotEmpty(fileList)) {
            return fileList.stream()
                    .flatMap(v -> v.getJSONArray("downloadUrls").toJavaList(String.class).stream())
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<TaskFileDTO> getTaskFile(String enterpriseId, DeviceDO device, String taskId) {
        List<JSONObject> fileList = getFileByTaskId(enterpriseId, device, taskId);
        if (CollectionUtils.isNotEmpty(fileList)) {
            return fileList.stream()
                    .flatMap(v -> {
                        String timePoint = v.getString("timePoint").replace("T", " ");
                        return v.getJSONArray("downloadUrls").toJavaList(String.class)
                                .stream()
                                .map(url -> new TaskFileDTO(timePoint, url));
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 根据任务id查询文件列表
     */
    private List<JSONObject> getFileByTaskId(String enterpriseId, DeviceDO device, String taskId) {
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            Map<String, Object> map = new HashMap<>();
            map.put("accessToken", accessToken);
            map.put("taskId", taskId);
            map.put("pageNumber", 0);
            map.put("pageSize", 50);
            map.put("hasUrl", true);
            String params = MapUtil.join(map, "&", "=");
            String url = yingshiUrl + "/api/v3/open/cloud/task/files?" + params;
            cn.hutool.http.HttpRequest httpRequest = HttpUtil.createGet(url);
            HttpResponse httpResponse = httpRequest.execute();
            if (Objects.nonNull(httpResponse) && httpResponse.getStatus() == HttpStatus.OK.value()) {
                log.info("设备id:{}, getVideoDownloadUrl:{}", device.getDeviceId(), JSONObject.toJSONString(httpResponse.body()));
                JSONObject body = JSONObject.parseObject(httpResponse.body());
                if (Objects.nonNull(body)) {
                    JSONArray dataArray = body.getJSONArray("data");
                    if (CollectionUtils.isNotEmpty(dataArray)) {
                        List<JSONObject> list = dataArray.toJavaList(JSONObject.class);
                        boolean isExpire = list.stream().anyMatch(obj -> {
                            String expireTimeStr = obj.getString("expireTime");
                            return StringUtils.isNotBlank(expireTimeStr) && LocalDateTime.parse(expireTimeStr.replaceAll("T", " "), dateTimeFormatter).isBefore(LocalDateTime.now());
                        });
                        if (isExpire) {
                            throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000007, "文件过期");
                        }
                        return list;
                    }
                }
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("萤石云根据任务id查询文件异常", e);
        }
        return null;
    }

    @Override
    public Boolean markDeviceKit(String enterpriseId, DeviceDO device, String channelNo, String storeNum, AccountTypeEnum accountType) {
        /*YingshiDeviceKitDTO devicekit = findDevicekit(enterpriseId, device, channelNo);
        if(devicekit != null){
            return false;
        }*/
        // 1、标记多维客流设备
        addDevicekit(enterpriseId, device, channelNo, storeNum);
        // 2、添加客流统计模板
        addTemplate(enterpriseId, device, storeNum);
        // 3、客流统计模板下发设备
        issuedTemplate(enterpriseId, device);
        // 4、开启设备消息订阅
        subscribeEvent(enterpriseId, device);
        return Boolean.TRUE;
    }

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
            List<String> channoList = getTrustChannel(eid, deviceId, accountType);
            return ListUtils.emptyIfNull(deviceList).stream().filter(o->channoList.contains(o.getChannelNo())).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Boolean deviceEncrypt(String eid, DeviceDO device, String channelNo, DeviceEncryptEnum encryptEnum){
        String url = yingshiUrl + "/api/lapp/device/encrypt/off";
        if(DeviceEncryptEnum.ON.equals(encryptEnum)){
            url = yingshiUrl + "/api/lapp/device/encrypt/on";
        }
        Map<String, String> map = new HashMap<>(2);
        map.put("deviceSerial", device.getDeviceId());
        map.put("accessToken", getAccessToken(eid, AccountTypeEnum.PLATFORM));
        String s = CoolHttpClient.sendPostFormRequest(url, map);
        log.info("deviceEncrypt:device={},result={}", device.getDeviceId(), s);
        return true;
    }

    private List<String> getTrustChannel(String enterpriseId, String deviceId, AccountTypeEnum accountType){
        String trustUrl = yingshiUrl + "/api/v3/open/device/metadata/channel/status";
        Map<String, String> headMap = new HashMap<>(2);
        headMap.put("deviceSerial", deviceId);
        headMap.put("accessToken", getAccessToken(enterpriseId, accountType));
        try {
            CoolHttpClientResult resultStr = CoolHttpClient.doGet(trustUrl, headMap, null);
            JSONObject result = JSONObject.parseObject(resultStr.getContent());
            if (result != null) {
                JSONObject resultJson = result.getJSONObject("result");
                if(Objects.nonNull(resultJson)&& OK.equals(resultJson.getInteger("code"))){
                    JSONObject data = resultJson.getJSONObject("data");
                    JSONArray channelInfoList = data.getJSONArray("channelInfoList");
                    if(channelInfoList != null && !channelInfoList.isEmpty()){
                        return channelInfoList.stream().filter(obj -> obj instanceof JSONObject)
                                .filter(obj -> ((JSONObject) obj).getInteger("status") != 2).map(o -> ((JSONObject) o).getString("superDevChannel")).collect(Collectors.toList());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Lists.newArrayList();
    }

    public DeviceCapacityDTO getDeviceCapacity(String enterpriseId, String deviceId, AccountTypeEnum accountTypeEnum) {
        try {
            String url = yingshiUrl + "/api/lapp/device/capacity";
            Map<String, String> map = new HashMap<>(2);
            map.put("deviceSerial", deviceId);
            map.put("accessToken", getAccessToken(enterpriseId, accountTypeEnum));
            String resultStr = post(enterpriseId, accountTypeEnum, url, map);
            JSONObject result = JSONObject.parseObject(resultStr);
            if (OK.equals(result.getInteger("code"))) {
                log.info("YingshiDeviceCapaticyDTO-->{}",JSONObject.toJSONString(result));
                JSONObject data = result.getJSONObject("data");
                return DeviceCapacityDTO.convertYingShiDeviceCapacity(data);
            }
        }catch (Exception e){
            log.info("数据获取异常",e);
        }
        return DeviceCapacityDTO.defaultDeviceCapacity();
    }

    /**
     * 标记多维客流设备
     * 将设备标记为多维客流设备，绑定相应区域
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @param regionTag
     * @return
     */
    private Boolean addDevicekit(String enterpriseId, DeviceDO device, String channelNo, String regionTag) {
        ResponseEntity<JSONObject> exchange = null;
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            headers.add("accessToken", accessToken);
            headers.add("deviceSerial", device.getDeviceId());
            headers.add("channelNo", channelNo);
            MultiValueMap<String, String> map = new LinkedMultiValueMap();
            map.put("regionTag", Collections.singletonList(regionTag));
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            String url = yingshiUrl + "/api/service/devicekit/peoplecounting/add";
            exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            log.info("设备id:{},addDevicekit:{}", device.getDeviceId(), JSONObject.toJSONString(exchange));
            if (Objects.nonNull(exchange) && exchange.getStatusCodeValue()== HttpStatus.OK.value()){
                return Boolean.TRUE;
            }
        } catch (RestClientException e) {
            log.error("标记多维客流设备异常：",e);
        }
        return Boolean.FALSE;
    }

    /**
     * 多维客流设备查询（GET）
     * 查询多维客流设备详细信息
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @return
     */
    private YingshiDeviceKitDTO findDevicekit(String enterpriseId, DeviceDO device, String channelNo){
        ResponseEntity<JSONObject> exchange = null;
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("accessToken", accessToken);
            headers.add("deviceSerial", device.getDeviceId());
            headers.add("channelNo", channelNo);
            String url = yingshiUrl + "/api/service/devicekit/peoplecounting/find";
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            log.info("设备id:{},findDevicekit:{}", device.getDeviceId(), JSONObject.toJSONString(exchange));
            if (Objects.nonNull(exchange) && exchange.getStatusCodeValue() == HttpStatus.OK.value()){
                JSONObject body = exchange.getBody();
                if(Objects.nonNull(body)){
                    JSONObject data = body.getJSONObject("data");
                    YingshiDeviceKitDTO deviceKitDTO = JSONObject.parseObject(JSONObject.toJSONString(data), YingshiDeviceKitDTO.class);
                    return  deviceKitDTO;
                }
            }
        } catch (RestClientException e) {
            log.error("多维客流设备查询异常：",e);
        }
        return null;
    }

    /**
     * 添加客流统计模板（POST）
     * 可定义客流统计模板（定义布防周期、绑定区域、告警阈值、开始、结束统计时间）并进行添加
     * @param enterpriseId
     * @param device
     * @param regionTag
     * @return
     */
    private Boolean addTemplate(String enterpriseId, DeviceDO device, String regionTag) {
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            Map<String, Object> params = new HashMap<>();
            params.put("regionTag", regionTag);
            params.put("dayOfWeek", "1,2,3,4,5,6,7");
            params.put("alarmThreshold", "100");
            params.put("startCountingTime", "00:00:00");
            params.put("endCountingTime", "23:59:59");
            String url = yingshiUrl + "/api/service/devicekit/peoplecounting/template/add";
            cn.hutool.http.HttpRequest httpRequest = HttpUtil.createPost(url);
            httpRequest.header("accessToken", accessToken);
            httpRequest.header("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            httpRequest.form(params);
            HttpResponse httpResponse = httpRequest.execute();
            log.info("设备id:{},addTemplate:{}", device.getDeviceId(), JSONObject.toJSONString(httpResponse.body()));
            if (Objects.nonNull(httpResponse) && httpResponse.getStatus() == HttpStatus.OK.value()){
                JSONObject body = JSONObject.parseObject(httpResponse.body());
                if(Objects.nonNull(body)){
                    JSONObject data = body.getJSONObject("data");
                    String templateId = data.getString("templateId");
                    if (StringUtils.isNotBlank(templateId)) {
                        return Boolean.TRUE;
                    }
                }
            }
        } catch (Exception e) {
            log.error("添加客流统计模板异常：",e);
        }
        return Boolean.FALSE;
    }

    /**
     * 客流统计模板下发设备
     * http://open.ys7.com/api/hikvision/ISAPI/System/Video/inputs/channels/1/counting
     * @param enterpriseId
     * @param device
     * @return
     */
    private Boolean issuedTemplate(String enterpriseId, DeviceDO device) {
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/xml");
            RequestBody body = RequestBody.create(mediaType, "<Counting version=\"2.0\" xmlns=\"http://www.isapi.org/ver20/XMLSchema\">\r\n    <enabled>true</enabled>\r\n    <CountingRegionType>region</CountingRegionType>\r\n    <CountingRegionList>\r\n        <CountingRegion>\r\n            <id>1</id>\r\n            <direction>\r\n                <startPoint>\r\n                    <positionX>1</positionX>\r\n                    <positionY>1</positionY>\r\n                </startPoint>\r\n                <endPoint>\r\n                    <positionX>900</positionX>\r\n                    <positionY>900</positionY>\r\n                </endPoint>\r\n            </direction>\r\n            <RuleRegionCoordinatesList>\r\n                <RegionCoordinates>\r\n                    <positionX>100</positionX>\r\n                    <positionY>900</positionY>\r\n                </RegionCoordinates>\r\n                <RegionCoordinates>\r\n                    <positionX>900</positionX>\r\n                    <positionY>900</positionY>\r\n                </RegionCoordinates>\r\n                <RegionCoordinates>\r\n                    <positionX>900</positionX>\r\n                    <positionY>100</positionY>\r\n                </RegionCoordinates>\r\n                <RegionCoordinates>\r\n                    <positionX>100</positionX>\r\n                    <positionY>100</positionY>\r\n                </RegionCoordinates>\r\n            </RuleRegionCoordinatesList>\r\n        </CountingRegion>\r\n    </CountingRegionList>\r\n    <dataUploadCycle>10</dataUploadCycle>\r\n    <SECUploadEnabled>false</SECUploadEnabled>\r\n    <dailyResetTime>00:00:00</dailyResetTime>\r\n    <streamOverlayRuleInfos>false</streamOverlayRuleInfos>\r\n    <ChildFilter>\r\n        <enabled>true</enabled>\r\n        <heightThreshold>150</heightThreshold>\r\n    </ChildFilter>\r\n    <TrajectoryCountFilter>\r\n        <enabled>false</enabled>\r\n        <movementDisplacement>40</movementDisplacement>\r\n        <residenceTime>1.0</residenceTime>\r\n    </TrajectoryCountFilter>\r\n</Counting>");
            Request request = new Request.Builder()
                    .url("http://open.ys7.com/api/hikvision/ISAPI/System/Video/inputs/channels/1/counting")
                    .method("PUT", body)
                    .addHeader("EZO-AccessToken", accessToken)
                    .addHeader("EZO-DeviceSerial", device.getDeviceId())
                    .addHeader("Content-Type", "application/xml")
                    .build();
            Response response = client.newCall(request).execute();
            String resultStr = response.body().string();
            log.info("设备id:{},issuedTemplate:{}", device.getDeviceId(), resultStr);
            if (StringUtils.isNotBlank(resultStr) && resultStr.contains("Counting")) {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            log.error("客流统计模板下发设备异常：",e);
        }
        return Boolean.FALSE;
    }

    /**
     * 开启设备消息订阅
     * @param enterpriseId
     * @param device
     * @return
     */
    private Boolean subscribeEvent(String enterpriseId, DeviceDO device) {
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            Map<String, Object> params = new HashMap<>();
            params.put("accessToken", accessToken);
            params.put("deviceSerial", device.getDeviceId());
            params.put("method", "POST");
            String url = yingshiUrl + "/api/open/cloud/ISAPI/Event/notification/subscribeEvent";
            cn.hutool.http.HttpRequest httpRequest = HttpUtil.createPost(url);
            httpRequest.header("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            httpRequest.form(params);
            HttpResponse httpResponse = httpRequest.execute();
            log.info("设备id:{},subscribeEvent:{}", device.getDeviceId(), JSONObject.toJSONString(httpResponse.body()));
            if (Objects.nonNull(httpResponse) && httpResponse.getStatus() == HttpStatus.OK.value()){
                JSONObject body = JSONObject.parseObject(httpResponse.body());
                if(Objects.nonNull(body)){
                    String data = body.getString("data");
                    if (StringUtils.isNotBlank(data) && (data.contains("<statusString>OK") || data.contains("SubscribeEventResponse"))) {
                        return Boolean.TRUE;
                    }
                }
            }
        } catch (RestClientException e) {
            log.error("开启设备消息订阅：",e);
        }
        return Boolean.FALSE;
    }

    /**
     * 区域客流数据统计查询（POST）
     * 对指定区域的详细客流数据进行统计查询
     * https://open.ys7.com/help/1555
     * @param enterpriseId
     * @param device
     * @param regionTag
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public YingshiDeviceKitPeoplecountingDTO statisticPeoplecounting(String enterpriseId, DeviceDO device, String regionTag, String startTime, String endTime){
        try {
            String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            Map<String, Object> params = new HashMap<>();
            params.put("regionTag", regionTag);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            String url = yingshiUrl + "/api/service/devicekit/peoplecounting/statistic/region";
            cn.hutool.http.HttpRequest httpRequest = HttpUtil.createPost(url);
            httpRequest.header("accessToken", accessToken);
            httpRequest.header("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            httpRequest.form(params);
            HttpResponse httpResponse = httpRequest.execute();
            log.info("设备id:{},statisticPeoplecounting:{}", device.getDeviceId(), JSONObject.toJSONString(httpResponse.body()));
            JSONObject body = JSONObject.parseObject(httpResponse.body());
            if(Objects.nonNull(body)){
                JSONObject data = body.getJSONObject("data");
                YingshiDeviceKitPeoplecountingDTO peoplecountingDTO = JSONObject.parseObject(JSONObject.toJSONString(data), YingshiDeviceKitPeoplecountingDTO.class);
                return  peoplecountingDTO;
            }
        } catch (Exception e) {
            log.error("区域客流数据统计查询异常：",e);
        }
        return null;
    }

    @Override
    public List<DeviceVideoRecordVO> listDeviceRecordByTime(String enterpriseId, DeviceDO device, String channelNo, Long startTime, Long endTime) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        List<DeviceVideoRecordVO> recordDTOList = Lists.newArrayList();
        try {
            String url = yingshiUrl + "/api/lapp/video/by/time";
            Map<String, String> map = new HashMap<>();
            map.put("deviceSerial", device.getDeviceId());
            map.put("channelNo", channelNo);
            map.put("startTime", String.valueOf(startTime));
            map.put("endTime", String.valueOf(endTime));
            map.put("recType", "2");
            map.put("version", "2.0");
            map.put("accessToken", getAccessToken(enterpriseId, accountType));
            String resultStr = post(enterpriseId, accountType, url, map);
            JSONObject result = JSONObject.parseObject(resultStr);
            if (OK.equals(result.getInteger("code"))) {
                log.info("listDeviceRecordByTime-->{}", JSONObject.toJSONString(result));
                JSONArray data = result.getJSONArray("data");
                recordDTOList = data.toJavaList(DeviceVideoRecordVO.class);
                return recordDTOList;
            }
        } catch (Exception e) {
            log.info("数据获取异常", e);
        }
        return recordDTOList;
    }
    @Override
    public DeviceSoftHardwareInfoVO deviceSoftHardwareInfo(String enterpriseId, DeviceDO device) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/lapp/device/info";
        Map<String, String> form = new HashMap<>();
        form.put("accessToken", accessToken);
        form.put("deviceSerial", device.getDeviceId());
        String responseStr = post(enterpriseId, accountType, url, form);
        JSONObject response = JSONObject.parseObject(responseStr);
        JSONObject data = response.getJSONObject("data");
        String firmwareVersion = getFirmwareVersion(enterpriseId, device);
        return DeviceSoftHardwareInfoVO.builder()
                .deviceModel(data.getString("model"))
                .ip(data.getString("netAddress"))
                .firmwareVersion(firmwareVersion)
                .build();
    }

    /**
     * 获取固件版本
     */
    private String getFirmwareVersion(String enterpriseId, DeviceDO device) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/lapp/device/version/info";
        Map<String, String> form = new HashMap<>();
        form.put("accessToken", accessToken);
        form.put("deviceSerial", device.getDeviceId());
        String responseStr = post(enterpriseId, accountType, url, form);
        JSONObject response = JSONObject.parseObject(responseStr);
        JSONObject data = response.getJSONObject("data");
        return data.getString("currentVersion");
    }

    @Override
    public Boolean deviceStorageFormatting(String enterpriseId, DeviceDO device, String channelNo) {
        JSONObject storageStatusResponse = getStorageStatus(enterpriseId, device);
        JSONObject data = storageStatusResponse.getJSONObject("data");
        if (Objects.nonNull(data)) {
            List<JSONObject> storageStatus = data.getJSONArray("storageStatus").toJavaList(JSONObject.class);
            for (JSONObject storage : storageStatus) {
                String index = storage.getString("index");
                storageFormatting(enterpriseId, device, index);
            }
        }
        return true;
    }

    /**
     * 存储介质格式化
     */
    private void storageFormatting(String enterpriseId, DeviceDO device, String diskIndex) {
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        String url = yingshiUrl + "/api/v3/device/format/disk";
        Map<String, String> headers = Collections.singletonMap("accessToken", accessToken);
        Map<String, Object> form = new HashMap<>();
        form.put("diskIndex", diskIndex);
        form.put("deviceSerial", device.getDeviceId());
        YingshiHttpClient.putForm(url, headers, form);
    }

    @Override
    public List<DeviceStorageInfoVO> deviceStorageInfo(String enterpriseId, DeviceDO device) {
        long[] storageSpaces = getStorageSpace(enterpriseId, device);
        JSONObject storageStatusResponse = getStorageStatus(enterpriseId, device);
        JSONObject data = storageStatusResponse.getJSONObject("data");
        if (Objects.nonNull(data)) {
            List<JSONObject> storageStatus = data.getJSONArray("storageStatus").toJavaList(JSONObject.class);
            return CollStreamUtil.toList(storageStatus, storage -> {
                Integer index = storage.getInteger("index");
                return DeviceStorageInfoVO.builder()
                        .totalSize(Objects.nonNull(storageSpaces) && storageSpaces.length > index ? storageSpaces[index - 1] : null)
                        .type(0)
                        .status(YingshiStorageStatusEnum.getMsgByCode(storage.getString("status")))
                        .build();
            });
        }
        return Collections.emptyList();
    }

    /**
     * 查询设备存储空间
     */
    private long[] getStorageSpace(String enterpriseId, DeviceDO device) {
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        String url = yingshiUrl + "/api/v3/device/diskCapacity?deviceSerial=" + device.getDeviceId();
        Map<String, String> headers = Collections.singletonMap("accessToken", accessToken);
        String responseStr = YingshiHttpClient.get(url, headers);
        JSONObject response = JSONObject.parseObject(responseStr);
        JSONObject data = response.getJSONObject("data");
        if (Objects.nonNull(data)) {
            String diskCapacity = data.getString("diskCapacity");
            if (StringUtils.isNotBlank(diskCapacity)) {
                return Arrays.stream(diskCapacity.split(",")).mapToLong(Long::parseLong).toArray();
            }
        }
        return null;
    }

    /**
     * 查询存储介质状态
     */
    private JSONObject getStorageStatus(String enterpriseId, DeviceDO device) {
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        String url = yingshiUrl + "/api/v3/device/format/status?deviceSerial=" + device.getDeviceId();
        Map<String, String> headers = Collections.singletonMap("accessToken", accessToken);
        String responseStr = YingshiHttpClient.get(url, headers);
        return JSONObject.parseObject(responseStr);
    }

    @Override
    public Boolean deviceReboot(String enterpriseId, DeviceDO device, String channelNo) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/v3/device/systemOperate";
        Map<String, String> headers = new HashMap<>();
        headers.put("accessToken", accessToken);
        headers.put("deviceSerial", device.getDeviceId());
        Map<String, Object> form = new HashMap<>();
        form.put("systemOperation", "REBOOT");
        YingshiHttpClient.postForm(url, headers, form);
        return true;
    }

    @Override
    public Boolean pictureFlip(String enterpriseId, DeviceDO device, DeviceConfigDTO configDTO) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = yingshiUrl + "/api/lapp/device/ptz/mirror";
        Map<String, String> form = new HashMap<>();
        boolean flip = Boolean.TRUE.equals(configDTO.getFlip());
        boolean mirror = Boolean.TRUE.equals(configDTO.getMirror());
        form.put("accessToken", accessToken);
        form.put("deviceSerial", device.getDeviceId());
        form.put("channelNo", "0".equals(configDTO.getChannelNo()) ? "1" : configDTO.getChannelNo());
        form.put("command", flip && mirror ? "2" : flip ? "0" : mirror ? "1" : "-1");
        try {
            post(enterpriseId, accountType, url, form);
        } catch (ServiceException e) {
            if (!(flip && mirror) && e.getErrorCode().equals(ErrorCodeEnum.YS_DEVICE_7160020.getCode())) {
                log.info("设备不支持翻转或镜像，尝试进行中心翻转");
                form.put("command", "2");
                post(enterpriseId, accountType, url, form);
            } else {
                throw e;
            }
        }
        return true;
    }

    /**
     * 重试
     * @param enterpriseId
     * @param accountType
     * @param url
     * @param map
     * @return
     */
    public String post(String enterpriseId, AccountTypeEnum accountType, String url, Map<String, String> map){
        boolean isFail = true;
        int retryTimes = 0;
        String resultStr = null;
        while (isFail && retryTimes < MAX_RETRY_TIMES){
            try {
                retryTimes++;
                log.info("重试次数：{}", retryTimes);
                resultStr = YingshiHttpClient.post(url, map);
                isFail = false;
            } catch (ServiceException e) {
                if(e.getErrorCode() == ErrorCodeEnum.YS_DEVICE_7110002.getCode()){
                    String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, enterpriseId, accountType.getCode(), getYunTypeNum().getCode());
                    redisUtilPool.delKey(cacheKey);
                    String accessToken = getAccessToken(enterpriseId, accountType);
                    map.put("accessToken", accessToken);
                    continue;
                }
                throw e;
            }
        }
        return resultStr;
    }
}
