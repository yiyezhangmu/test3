package com.coolcollege.intelligent.service.video.openapi.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.YunShiTongHttpClient;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.model.video.platform.hik.HikAllDeviceResponse;
import com.coolcollege.intelligent.model.video.platform.hik.HikRegionStoreListResponse;
import com.coolcollege.intelligent.model.video.platform.hik.HikStoreResponse;
import com.coolcollege.intelligent.model.video.platform.hik.dto.*;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.HKPassengerFlowAttributesDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowConfigDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowSwitchStatusDTO;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.openapi.VideoOpenService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.VideoProtocolTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/12/13 14:34
 * @Version 1.0
 */
@Slf4j
@Service
public class HikCloudOpenServiceImpl implements VideoOpenService {

    private static  final  String BEARER = "Bearer ";

    @Resource
    private EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Resource
    private YunShiTongHttpClient yunShiTongHttpClient;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private DeviceChannelMapper deviceChannelMapper;
    @Value("${hik.appKey}")
    private String hikAppKey;
    @Value("${hik.secret}")
    private String hikSecret;
    @Autowired
    private RestTemplate restTemplate;

    private static final String hikUrl = "https://api2.hik-cloud.com";
    @Override
    public YunTypeEnum getYunTypeNum() {
        return YunTypeEnum.HIKCLOUD;
    }

    @Override
    public String getAccessToken(String eid, AccountTypeEnum accountType) {
        String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, eid, accountType.getCode(),getYunTypeNum().getCode());
        String accessToken = redisUtilPool.getString(cacheKey);
        if(StringUtils.isNotBlank(accessToken)){
            return accessToken;
        }
        String appKey = null, appSecret = null;
        if(AccountTypeEnum.PLATFORM.equals(accountType)){
            appKey = hikAppKey;
            appSecret = hikSecret;
        }else{
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(eid, getYunTypeNum().getCode(), accountType.getCode());
            if (videoSetting==null){
                throw new ServiceException(ErrorCodeEnum.HIK_CLOUD_ACCESS_TOKEN_GET_ERROR);
            }
            appKey = videoSetting.getAccessKeyId();
            appSecret = videoSetting.getSecret();
            DataSourceHelper.changeToSpecificDataSource(dbName);
        }
        ResponseEntity<JSONObject> exchange = null;
        try {
            String url = hikUrl + "/oauth/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // 设置请求参数
            MultiValueMap<String, String> map = new LinkedMultiValueMap();
            map.put("client_id", Collections.singletonList(appKey));
            map.put("client_secret",Collections.singletonList(appSecret));
            map.put("grant_type",Collections.singletonList("client_credentials"));
            map.put("scope",Collections.singletonList("app"));
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            log.info("url:{}, req:{}", url, JSONObject.toJSONString(req));
            exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            if (exchange.getBody()==null){
                return "";
            }
            accessToken = exchange.getBody().getString("access_token");
            Long expiresIn = exchange.getBody().getLong("expires_in");
            redisUtilPool.setString(cacheKey, accessToken, expiresIn.intValue());
        } catch (Exception e) {
            log.error("getHikCloudAccessToken_error:{}",e);
        }
        return accessToken;
    }

    @Override
    public OpenDeviceDTO getDeviceDetail(String eid, String deviceId, AccountTypeEnum accountType) {
        OpenDeviceDTO openDeviceDTO = new OpenDeviceDTO();
        List<HikCloudChannelsDTO> hikCloudChannelsDTOS = this.listByDevSerial(eid, deviceId,accountType);
        if (CollectionUtils.isEmpty(hikCloudChannelsDTOS)){
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_FOUND);
        }
        if (CollectionUtils.isNotEmpty(hikCloudChannelsDTOS)){
            List<String> channelList = hikCloudChannelsDTOS.stream().map(HikCloudChannelsDTO::getChannelId).collect(Collectors.toList());
            //开通标准流
            liveVideoOpen(eid,channelList,accountType);

            Integer deviceStatus = hikCloudChannelsDTOS.get(0).getDeviceStatus();
            openDeviceDTO.setDeviceStatus(deviceStatus == 1?DeviceStatusEnum.ONLINE.getCode():DeviceStatusEnum.OFFLINE.getCode());
            openDeviceDTO.setDeviceId(deviceId);
            openDeviceDTO.setDataSourceId(hikCloudChannelsDTOS.get(0).getChannelId());
            openDeviceDTO.setDeviceName(hikCloudChannelsDTOS.get(0).getDeviceName());
            openDeviceDTO.setStoreNo(hikCloudChannelsDTOS.get(0).getStoreNo());
            openDeviceDTO.setSource(YunTypeEnum.HIKCLOUD.getCode());
            openDeviceDTO.setHasChildDevice(Boolean.FALSE);
            DeviceCapacityDTO deviceCapacity = DeviceCapacityDTO.convertHikCloudDeviceCapacity();
            //海康云眸默认都支持抓拍
            openDeviceDTO.setSupportCapture(deviceCapacity.getSupportCapture());
            openDeviceDTO.setDeviceCapacity(deviceCapacity);
            openDeviceDTO.setHasPtz(YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzLeftRight()));
        }
        if (CollectionUtils.isNotEmpty(hikCloudChannelsDTOS)&&hikCloudChannelsDTOS.size()==1){
            return openDeviceDTO;
        }
        List<OpenChannelDTO> channelList = new ArrayList<>();
        openDeviceDTO.setHasChildDevice(Boolean.TRUE);
        for (HikCloudChannelsDTO hikCloudChannelsDTO:hikCloudChannelsDTOS) {
            if (hikCloudChannelsDTO.getIsUse() == 1) {
                OpenChannelDTO openChannelDTO = new OpenChannelDTO();
                openChannelDTO.setDeviceId(hikCloudChannelsDTO.getChannelId());
                openChannelDTO.setParentDeviceId(hikCloudChannelsDTO.getDeviceSerial());
                openChannelDTO.setChannelNo(hikCloudChannelsDTO.getChannelNo());
                openChannelDTO.setChannelName(hikCloudChannelsDTO.getChannelName());
                openChannelDTO.setSource(getYunTypeNum().getCode());
                openChannelDTO.setStatus(Constants.ONE_STR.equals(hikCloudChannelsDTO.getChannelStatus()) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                DeviceCapacityDTO deviceCapacity = DeviceCapacityDTO.convertHikCloudDeviceCapacity();
                openDeviceDTO.setHasPtz(YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzLeftRight()));
                openChannelDTO.setDeviceCapacity(deviceCapacity);
                channelList.add(openChannelDTO);
            }
        }
        openDeviceDTO.setChannelList(channelList);
        //暂时无获取设备详情接口
        return openDeviceDTO;
    }

    @Override
    public PageInfo<OpenDevicePageDTO> getDeviceList(String eid, AccountTypeEnum accountType, Integer pageNum, Integer pageSize) {
        ResponseEntity<JSONObject> exchange = null;
        List<OpenDevicePageDTO> result = new ArrayList<>();
        //先查询设备配置表中是否配置了syncDeviceFlag
        SettingVO setting = enterpriseVideoSettingService.getSetting(eid, YunTypeEnum.HIKCLOUD, accountType);
        PageInfo pageInfo = new PageInfo();
        if (StringUtils.isNotEmpty(setting.getHikCloudFristNodeId())){
            //查询门店列表
            List<HikStoreDTO> storeInfo = getStoreInfo(eid, accountType, pageNum, pageSize);
            if (CollectionUtils.isEmpty(storeInfo)){
                return pageInfo;
            }
            for (HikStoreDTO store:storeInfo) {
                //查询每个门店的设备列表
                List<OpenDevicePageDTO> nodeDeviceList = getNodeDeviceList(eid, accountType, store.getStoreId());
                if (CollectionUtils.isNotEmpty(nodeDeviceList)){
                    result.addAll(nodeDeviceList);
                }
            }
        }else {
            String hikCloudAccessToken = this.getAccessToken(eid,accountType);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            String url = hikUrl + "/v1/customization/stores/cameraList?pageNo="+pageNum+"&pageSize="+pageSize;
            try {
                exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);

                if (exchange.getStatusCode().value()==200){
                    Map data = (Map) exchange.getBody().get("data");
                    HikAllDeviceResponse hikAllDeviceResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikAllDeviceResponse.class);
                    Integer total = hikAllDeviceResponse.getTotal();
                    if (total!=0){
                        result.addAll(hikAllDeviceResponse.getRows());
                    }
                    int i = (total - 1) / pageSize + 1;
                    while (i - pageNum > 0) {
                        pageNum = pageNum + 1;
                        List<OpenDevicePageDTO> allDevicePage = getAllDevicePage(eid,accountType, pageNum, pageSize);
                        if (CollectionUtils.isNotEmpty(allDevicePage)) {
                            result.addAll(allDevicePage);
                        }
                    }
                    log.info("allDevicePage result:{} size:{}",JSONObject.toJSONString(result) ,result.size());
                }
            } catch (Exception e) {
                log.info("设备列表获取异常e:{}",e);
                if (((HttpClientErrorException.TooManyRequests) e).getStatusCode().value() == 429){
                    try {
                        log.info("准备休息1分钟==========================");
                        Thread.sleep(60000);
                        exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
                        log.info("value=========================="+ exchange.getStatusCode().value());
                        if (exchange.getStatusCode().value()==200){
                            Map data = (Map) exchange.getBody().get("data");
                            HikAllDeviceResponse hikAllDeviceResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikAllDeviceResponse.class);
                            Integer total = hikAllDeviceResponse.getTotal();
                            if (total!=0){
                                result.addAll(hikAllDeviceResponse.getRows());
                            }
                        }
                    } catch (Exception exception) {
                        log.info("exception:{}",e);
                    }
                }
            }
        }
        //设备特殊处理
        Set<String> sets = new HashSet<>();
        List<OpenDevicePageDTO> res = new ArrayList<>();
        for (OpenDevicePageDTO openDevicePageDTO:result) {
            openDevicePageDTO.setSource(YunTypeEnum.HIKCLOUD.getCode());
            openDevicePageDTO.setDeviceId(openDevicePageDTO.getDeviceSerial());
            //设备列表 设备状态=通道状态(每个通道为一个设备) 通过门店ID查询设备的时候 没有返回DeviceStatus 有返回channelStatus 统一处理 都用channelStatus
            openDevicePageDTO.setDeviceStatus(openDevicePageDTO.getChannelStatus());
            if (!sets.contains(openDevicePageDTO.getDeviceSerial())){
                sets.add(openDevicePageDTO.getDeviceSerial());
                res.add(openDevicePageDTO);
            }
        }
        pageInfo.setList(res);
        return pageInfo;
    }

    /**
     * @Description:  云眸设备解码
     * @Param: [eid, deviceSerial, accountType]
     * @Author: tangziqi
     * @Date: 2023/5/30~18:46
     */

    @Override
    public Boolean getDecode(String eid, String deviceSerial, AccountTypeEnum accountType) {
        if (StringUtils.isNotEmpty(deviceSerial)) {
            ResponseEntity<JSONObject> exchange = null;
            String url = hikUrl + "/v1/customization/liveStudio/actions/encryptTurnOffOrOn";

            HttpHeaders headers = new HttpHeaders();
            String hikCloudAccessToken = this.getAccessToken(eid,AccountTypeEnum.PRIVATE);
            headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            headers.add("Authorization",BEARER+hikCloudAccessToken);

            // 设置请求参数
            // 设置请求参数
            Map<String, Object> map = new HashMap<>();
            map.put("deviceSerial",deviceSerial);
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            try {
                log.info("url:{}, req:{}", url, JSONObject.toJSONString(req));
                exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
                log.info(JSONObject.toJSONString(exchange));
                if (exchange.getStatusCode().value()==200){
                    return Boolean.TRUE;
                }
            } catch (Exception e) {
                log.error("设备" + deviceSerial +"解密失败:{}",e);
                if (((HttpClientErrorException.TooManyRequests) e).getStatusCode().value() == 429){

                    try {
                        log.info("准备休息1分钟==========================");
                        Thread.sleep(60000);
                        exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
                        log.info(JSONObject.toJSONString(exchange));
                        if (exchange.getStatusCode().value()==200){
                            return Boolean.TRUE;
                        }
                    } catch (Exception ex) {
                        log.info("InterruptedException:{}",ex);
                    }
                }
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 分页获取设备
     * @param eid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<OpenDevicePageDTO>  getAllDevicePage(String eid,AccountTypeEnum accountType,Integer pageNo, Integer pageSize) {
        ResponseEntity<JSONObject> exchange = null;
        List<OpenDevicePageDTO> result = new ArrayList<>();
        String url = hikUrl + "/v1/customization/stores/cameraList?pageNo="+pageNo+"&pageSize="+pageSize;
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        try {
            String hikCloudAccessToken = this.getAccessToken(eid,accountType);
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                Map data = (Map) exchange.getBody().get("data");
                HikAllDeviceResponse hikAllDeviceResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikAllDeviceResponse.class);
                result.addAll(hikAllDeviceResponse.getRows());
            }
        } catch (Exception e) {
            log.info("设备列表获取异常e:{}",e);
            try {
                Thread.sleep(60000);
                exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
                if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                    Map data = (Map) exchange.getBody().get("data");
                    HikAllDeviceResponse hikAllDeviceResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikAllDeviceResponse.class);
                    result.addAll(hikAllDeviceResponse.getRows());
                }
            } catch (Exception ex) {
                log.info("设备列表获取异常e:{}",ex);
            }
        }
        return result;
    }

    @Override
    public LiveVideoVO getLiveUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        HikCloudLiveAddressV2DTO data = new HikCloudLiveAddressV2DTO();
        String channelNo = param.getChannelNo();
        VideoProtocolTypeEnum protocolTypeEnum = param.getProtocol();
        try {
            DataSourceHelper.reset();
            String hikCloudAccessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            Map<String, String> map = new HashMap<>();
            map.put("deviceSerial", device.getDeviceId());
            if (protocolTypeEnum != null) {
                map.put("protocol", VideoProtocolTypeEnum.getHikCloudProtocol(protocolTypeEnum));
            } else {
                map.put("protocol", "2");
            }
            //是否有通道号
            if (channelNo != null) {
                map.put("channelNo", Integer.valueOf(channelNo) == 0 ? "1" : channelNo);
            }
            //是否查看历史视频
            if (StringUtils.isNotBlank(param.getStartTime())) {
                map.put("startTime", param.getStartTime());
                map.put("type", "2");
            } else {
                map.put("type", "1");
            }
            if (StringUtils.isNotBlank(param.getEndTime())) {
                map.put("stopTime", param.getEndTime());
            }
            if(Objects.nonNull(param.getSupportH265())){
                map.put("supportH265", param.getSupportH265());
            }
            if(Objects.nonNull(param.getQuality())){
                map.put("quality", param.getQuality().toString());
            }
            String url = hikUrl + "/v1/customization/liveStudio/actions/getLiveAddress";
            log.info("url:{}, headers:{}, requestMap:{}", url, JSONObject.toJSONString(headers), JSONObject.toJSONString(map));
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(map, headers), JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            JSONObject body = exchange.getBody();
            if (body == null || body.get("data") == null){
                if(body != null && body.getInteger("code") != null && body.getInteger("code") != 200 && body.getInteger("code") != 0 && body.get("message") != null){
                    throw new ServiceException(ErrorCodeEnum.ERROR, exchange.getBody().get("message").toString());
                }
                throw new ServiceException(ErrorCodeEnum.HIL_DEVICE_NOT_FOUND);
            }
            data = JSONObject.parseObject(JSONObject.toJSONString(body.get("data")), HikCloudLiveAddressV2DTO.class);
        } catch (RestClientException e) {
            log.error("getLiveAddress_error:{}",e);
            if (((HttpClientErrorException.TooManyRequests) e).getStatusCode().value() == 429){
                throw new ServiceException(ErrorCodeEnum.HIKCLOUD_LIMIT);
            }
        }
        LiveVideoVO liveVideoVO = new LiveVideoVO();
        liveVideoVO.setUrl(data.getUrl());
        return liveVideoVO;
    }

    @Override
    public LiveVideoVO getPastVideoUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        return getLiveUrl(enterpriseId, device, param);
    }

    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {

    }

    @Override
    public Boolean ptzStart(String eid, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration) {
        ResponseEntity<JSONObject> exchange = null;
        String channelId = "";
        //如果是0表示是ipc设备 直接取设备表中的通道ID
        if (StringUtils.isBlank(channelNo) || "0".equals(channelNo)){
            channelId = device.getDataSourceId();
        }else {
            DeviceChannelDO deviceChannelDO = deviceChannelMapper.selectDeviceChannelByParentId(eid, device.getDeviceId(), channelNo);
            //通道表中的设备ID对应NVE通道的通道ID
            channelId = deviceChannelDO.getDeviceId();
        }
        try {
            DataSourceHelper.reset();
            String hikCloudAccessToken = getAccessToken(eid, AccountTypeEnum.getAccountType(device.getAccountType()));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            MultiValueMap<String, String> map = new LinkedMultiValueMap();
            map.put("direction", Collections.singletonList(String.valueOf(command)));
            map.put("speed", Collections.singletonList(String.valueOf(speed)));
            map.put("mode", Collections.singletonList("0"));
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            String url = hikUrl + "/v1/customization/channels/"+channelId+"/ptz/start";
            exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            if (exchange!=null&exchange.getBody().get("code").equals(HttpStatus.OK.value())){
                return Boolean.TRUE;
            }
        } catch (RestClientException e) {
            log.info("设备云台停止异常e:{}",e);
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean ptzStop(String eid, DeviceDO device, String channelNo) {
        ResponseEntity<JSONObject> exchange = null;
        String channelId = "";
        //如果是0表示是ipc设备 直接取设备表中的通道ID
        if (StringUtils.isBlank(channelNo) || "0".equals(channelNo)){
            channelId = device.getDataSourceId();
        }else {
            DeviceChannelDO deviceChannelDO = deviceChannelMapper.selectDeviceChannelByParentId(eid, device.getDeviceId(), channelNo);
            //通道表中的设备ID对应NVE通道的通道ID
            channelId = deviceChannelDO.getDeviceId();
        }
        try {
            DataSourceHelper.reset();
            String hikCloudAccessToken = getAccessToken(eid, AccountTypeEnum.getAccountType(device.getAccountType()));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            String url = hikUrl + "/v1/customization/channels/"+channelId+"/ptz/stop";
            exchange = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(headers), JSONObject.class);
            log.info("ptzStop_{}",JSONObject.toJSONString(exchange));
            if (exchange!=null&exchange.getBody().get("code").equals(HttpStatus.OK.value())){
                return Boolean.TRUE;
            }
        } catch (RestClientException e) {
            log.info("设备云台停止异常e:{}",e);
        }
        return Boolean.FALSE;
    }

    @Override
    public String addPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String devicePositionName) {
        ResponseEntity<JSONObject> exchange = null;
        String channelId = "";
        //如果是0表示是ipc设备 直接取设备表中的通道ID
        if (StringUtils.isBlank(channelNo) || "0".equals(channelNo)){
            channelId = device.getDataSourceId();
        }else {
            DeviceChannelDO deviceChannelDO = deviceChannelMapper.selectDeviceChannelByParentId(enterpriseId, device.getDeviceId(), channelNo);
            //通道表中的设备ID对应NVE通道的通道ID
            channelId = deviceChannelDO.getDeviceId();
        }
        try {
            DataSourceHelper.reset();
            String hikCloudAccessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            MultiValueMap<String, String> map = new LinkedMultiValueMap();
            map.put("channelId", Collections.singletonList(channelId));
            map.put("presetName", Collections.singletonList(devicePositionName));
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            String url = hikUrl + "/v1/customization/channels/presets/add";
            exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()&&!exchange.getBody().get("code").equals(519999)){
                JSONObject data = (JSONObject) exchange.getBody().get("data");
                return (String) data.get("presetId");
            }
        } catch (RestClientException e) {
            log.info("设备云台停止异常e:{}",e);
        }
        return "";
    }

    @Override
    public Boolean deletePtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        ResponseEntity<JSONObject> exchange = null;
        try {
            DataSourceHelper.reset();
            String hikCloudAccessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            String url = hikUrl + "/v1/customization/channels/presets/delete?presetId="+presetIndex;
            exchange = restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), JSONObject.class);
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                return Boolean.TRUE;
            }
        } catch (RestClientException e) {
            log.info("设备云台停止异常e:{}",e);
        }
        return Boolean.FALSE;
    }

    @Override
    public Boolean loadPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        ResponseEntity<JSONObject> exchange = null;
        try {
            DataSourceHelper.reset();
            String hikCloudAccessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            MultiValueMap<String, String> map = new LinkedMultiValueMap();
            map.put("presetId", Collections.singletonList(presetIndex));
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            String url = hikUrl + "v1/customization/channels/presets/actions/move";
            exchange = restTemplate.exchange(url, HttpMethod.DELETE, req, JSONObject.class);
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                return Boolean.TRUE;
            }
        } catch (RestClientException e) {
            log.info("设备云台停止异常e:{}",e);
        }
        return Boolean.FALSE;
    }

    @Override
    public String capture(String eid, DeviceDO device, String channelNo, String quality) {
        ResponseEntity<JSONObject> exchange = null;
        try {
            String url = hikUrl + "/v1/customization/capture";
            HttpHeaders headers = new HttpHeaders();
            String hikCloudAccessToken = getAccessToken(eid, AccountTypeEnum.getAccountType(device.getAccountType()));
            headers.add("Content-Type", "application/x-www-form-urlencoded");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            // 设置请求参数
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("deviceSerial", device.getDeviceId());
            map.add("channelNo", StringUtils.isBlank(channelNo) ? "1" : channelNo);
            HttpEntity<MultiValueMap> req = new HttpEntity<>(map, headers);
            log.info("url:{}, req:{}", url, JSONObject.toJSONString(req));
            exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            log.info("url:{}, req:{}, res:{}", url, JSONObject.toJSONString(req), JSONObject.toJSONString(exchange));
            if (exchange.getBody()==null||exchange.getBody().get("data")==null){
                return "";
            }
            return (String) ((Map)exchange.getBody().get("data")).get("picUrl");
        } catch (Exception e) {
            log.error("getHikCloudAccessToken_error",   e);
        }
        return "";
    }

    @Override
    public String videoTransCode(String enterpriseId,  DeviceDO device, VideoDTO param) {
        ResponseEntity<JSONObject> exchange = null;
        String channelNo = param.getChannelNo(), startTime = param.getStartTime(), endTime = param.getEndTime();
        if ("0".equals(channelNo)){
            channelNo = "1";
        }
        try {
            String url = hikUrl + "/v1/customization/record/deviceVideo/saveDeviceVideo";
            HttpHeaders headers = new HttpHeaders();
            String hikCloudAccessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            // 设置请求参数
            Map<String, Object> map = new HashMap<>();
            map.put("deviceSerial",device.getDeviceId());
            map.put("channelNo",Integer.valueOf(channelNo));
            map.put("startTime",startTime);
            map.put("endTime",endTime);
            map.put("recType","");
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            log.info("url:{}, req:{}", url, JSONObject.toJSONString(req));
            exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            if (exchange.getBody()==null||exchange.getBody().get("data")==null&&!exchange.getBody().get("code").equals(200)){
                throw new ServiceException((String) exchange.getBody().get("message"));
            }
            return (String) ((Map)exchange.getBody().get("data")).get("fileId");
        } catch (Exception e) {
            log.error("getHikCloudAccessToken_error:{}",e);
            if (e instanceof ServiceException){
                throw new ServiceException((((ServiceException) e).getErrorMessage()));
            }
        }
        return "";
    }

    @Override
    public VideoFileDTO getVideoFile(String enterpriseId,DeviceDO device, String fileId) {
        ResponseEntity<JSONObject> exchange = null;
        VideoFileDTO videoFileDTO = new VideoFileDTO();
        try {
            String url = hikUrl + "/v1/customization/record/deviceVideo/queryVideoFileById?fileId="+fileId;
            HttpHeaders headers = new HttpHeaders();
            String hikCloudAccessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            HttpEntity<Object> req = new HttpEntity<>(headers);
            exchange = restTemplate.exchange(url, HttpMethod.GET, req, JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            if (exchange.getBody()==null||exchange.getBody().get("data")==null){
                videoFileDTO.setStatus(2);
                return videoFileDTO;
            }
            videoFileDTO = JSONObject.parseObject(JSONObject.toJSONString(exchange.getBody().get("data")), VideoFileDTO.class);
            return videoFileDTO;
        } catch (Exception e) {
            log.error("listByDevSerial_error:{}",e);
        }
        return videoFileDTO;
    }

    @Override
    public List<String> getVideoDownloadUrl(String enterpriseId,DeviceDO device, String fileId) {
        ResponseEntity<JSONObject> exchange = null;
        try {
            String url = hikUrl + "/v1/customization/record/deviceVideo/getVideoDownloadUrl?fileId="+fileId;
            HttpHeaders headers = new HttpHeaders();
            String hikCloudAccessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            HttpEntity<Object> req = new HttpEntity<>(headers);
            exchange = restTemplate.exchange(url, HttpMethod.GET, req, JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            if (exchange.getBody()==null||exchange.getBody().get("data")==null){
                return Lists.newArrayList();
            }
            List<String> list = JSONObject.parseArray(JSONObject.toJSONString(((Map)exchange.getBody().get("data")).get("urls")), String.class);
            return list;
        } catch (Exception e) {
            log.error("listByDevSerial_error:{}",e);
        }
        return Lists.newArrayList();
    }


    @Override
    public AppKeyDTO authentication(String eid, DeviceDO device) {
        ResponseEntity<JSONObject> exchange = null;
        AppKeyDTO hIkAppKeyDTO = new AppKeyDTO();
        String hik_auth_key = String.format(Constants.HIK_AUTH, eid);
        String string = redisUtilPool.getString(hik_auth_key);
        if (StringUtils.isNotEmpty(string)){
            return JSONObject.parseObject(string, AppKeyDTO.class);
        }
        try {
            String hikCloudAccessToken = getAccessToken(eid, AccountTypeEnum.getAccountType(device.getAccountType()));
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            String url = hikUrl + "/v1/ezviz/account/info";
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);

            if (exchange.getBody()==null||exchange.getBody().get("data")==null&&!exchange.getBody().get("code").equals(200)){
                throw new ServiceException(ErrorCodeEnum.AUTHENTICATION_ERROR);
            }
        } catch (RestClientException e) {
            log.info("视频取流时需要的认证信息获取异常:{}",JSONObject.toJSONString(exchange), e);
            throw new ServiceException(ErrorCodeEnum.AUTHENTICATION_ERROR);
        }
        Map data = (HashMap) exchange.getBody().get("data");
        String token = (String) data.get("token");
        String appKey = (String) data.get("appKey");
        hIkAppKeyDTO.setAppKey(appKey);
        hIkAppKeyDTO.setToken(token);
        //缓存2小时
        redisUtilPool.setNxExpire(hik_auth_key,JSONObject.toJSONString(hIkAppKeyDTO),60*60*24);
        return hIkAppKeyDTO;
    }

    private  List<HikCloudChannelsDTO> listByDevSerial(String eid, String deviceSerial,AccountTypeEnum accountTypeEnum) {
        ResponseEntity<JSONObject> exchange = null;
        String url = hikUrl + "/v1/customization/devices/channels/actions/listByDevSerial?deviceSerial="+deviceSerial;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> req = null;
        try {
            String hikCloudAccessToken = getAccessToken(eid,accountTypeEnum);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            req = new HttpEntity<>(headers);
            exchange = restTemplate.exchange(url, HttpMethod.GET, req, JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            if (exchange.getBody()==null||exchange.getBody().get("data")==null){
                return Lists.newArrayList();
            }
            List<HikCloudChannelsDTO> hik = JSONObject.parseArray(JSONObject.toJSONString(exchange.getBody().get("data")), HikCloudChannelsDTO.class);
            return hik;
        } catch (Exception e) {
            log.error("listByDevSerial_error:{}",e);
            if (((HttpClientErrorException.TooManyRequests) e).getStatusCode().value() == 429){
                try {
                    Thread.sleep(60000);
                    exchange = restTemplate.exchange(url, HttpMethod.GET, req, JSONObject.class);
                    log.info(JSONObject.toJSONString(exchange));
                    if (exchange.getBody()==null||exchange.getBody().get("data")==null){
                        return Lists.newArrayList();
                    }
                    List<HikCloudChannelsDTO> hik = JSONObject.parseArray(JSONObject.toJSONString(exchange.getBody().get("data")), HikCloudChannelsDTO.class);
                    return hik;
                } catch (InterruptedException interruptedException) {
                    log.info("InterruptedException:{}",e);
                }
            }
        }
        return Lists.newArrayList();
    }

    /**
     * 开通标准流
     * @param eid
     * @param channelLists
     * @param accountTypeEnum
     * @return
     */
    private boolean liveVideoOpen(String eid ,List<String> channelLists,AccountTypeEnum accountTypeEnum){
        ResponseEntity<JSONObject> exchange = null;
        try {
            if (CollectionUtils.isEmpty(channelLists)){
                return Boolean.TRUE;
            }
            String url = hikUrl + "/v1/customization/liveVideoOpen";
            HttpHeaders headers = new HttpHeaders();
            String hikCloudAccessToken = this.getAccessToken(eid,accountTypeEnum);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            // 设置请求参数
            MultiValueMap<String, String> map = new LinkedMultiValueMap();
            map.put("channelIds",channelLists);
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
        } catch (Exception e) {
            log.error("liveVideoOpen_error:{}",e);
        }
        return Boolean.TRUE;
    }

    /**
     * 查询所有的一级菜单
     * @param eid
     * @param accountTypeEnum
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<HikCloudAreasDTO> getAllFirstNodeList(String eid ,AccountTypeEnum accountTypeEnum,Integer pageNo,Integer pageSize){
        List<HikCloudAreasDTO> result = new ArrayList<>();
        ResponseEntity<JSONObject> exchange = null;
        try {
            String hikCloudAccessToken = this.getAccessToken(eid,accountTypeEnum);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            //查询根节点区域ID
            String url = hikUrl + "/v1/customization/areas/list?pageNo=1&pageSize=1";
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);

            if (exchange.getStatusCode().value()==200){
                Map data = (Map) exchange.getBody().get("data");
                HikRegionStoreListResponse hikRegionStoreListResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikRegionStoreListResponse.class);
                List<HikCloudAreasDTO> rows = hikRegionStoreListResponse.getRows();
                String rootId = "" ;
                if (CollectionUtils.isNotEmpty(rows)){
                    rootId = rows.get(Constants.INDEX_ZERO).getAreaId();
                }
                Boolean flag = hikRegionStoreListResponse.getHasNextPage();
                while (flag) {
                    //从第一页开始查询
                    pageNo = pageNo + 1;
                    HikRegionStoreListResponse hikRegionStoreList = getAreas(eid, accountTypeEnum, pageNo, pageSize);
                    List<HikCloudAreasDTO> areas = hikRegionStoreList.getRows();
                    flag = hikRegionStoreList.getHasNextPage();
                    //过滤掉非一级菜单
                    String finalRootId = rootId;
                    areas = areas.stream().filter(x-> finalRootId.equals(x.getParentId())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(areas)) {
                        result.addAll(areas);
                    }
                }
            }
        } catch (RestClientException e) {
            log.info("设备列表获取异常e:{}",e);
        }
        return result;
    }

    @Override
    public List<PassengerDTO> getPassengerData(String eid, AccountTypeEnum accountTypeEnum, String dateTime, String storeNo) {
        ResponseEntity<JSONObject> exchange = null;
        List<PassengerDTO> passengerList = new ArrayList<>();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String url = hikUrl + "/v1/customization/store/passenger_hour_flow?dateTime="+dateTime+"&storeNo="+storeNo;
        try {
            String hikCloudAccessToken = this.getAccessToken(eid,accountTypeEnum);
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            log.info("url:{},Authorization:{},getPassengerData:{}",url, BEARER+hikCloudAccessToken, JSONObject.toJSONString(exchange));
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                passengerList = JSONObject.parseArray(JSONObject.toJSONString(exchange.getBody().get("data")), PassengerDTO.class);
            }
        } catch (RestClientException e) {
            log.info("通过门店编号查询客流数据失败:{}",e);
            if (((HttpClientErrorException.TooManyRequests) e).getStatusCode().value() == 429){
                try {
                    Thread.sleep(60000);
                    exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
                    if (exchange.getStatusCode().value()==200) {
                        passengerList = JSONObject.parseArray(JSONObject.toJSONString(exchange.getBody().get("data")), PassengerDTO.class);
                    }
                } catch (InterruptedException interruptedException) {
                    log.info("InterruptedException:{}",e);
                }
            }
        }
        return passengerList;
    }

    @Override
    public List<HKPassengerFlowAttributesDTO> getPassengerAttributesData(String eid, AccountTypeEnum accountTypeEnum, String startTime, String endTime, String storeNo) {
        ResponseEntity<JSONObject> exchange = null;
        List<HKPassengerFlowAttributesDTO> passengerAttributesList = new ArrayList<>();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String url = hikUrl + "/v1/customization/passenger/attributesByStore?startTime="+startTime +"&endTime="+endTime+"&storeNo="+storeNo;
        try {
            String hikCloudAccessToken = this.getAccessToken(eid,accountTypeEnum);
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            log.info("url:{},Authorization:{},getPassengerAttributesData:{}",url, BEARER+hikCloudAccessToken, JSONObject.toJSONString(exchange));
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                passengerAttributesList = JSONObject.parseArray(JSONObject.toJSONString(exchange.getBody().get("data")), HKPassengerFlowAttributesDTO.class);
            }
        } catch (RestClientException e) {
            log.info("通过门店编号查询指定门店客流属性数据失败:{}",e);
            if (((HttpClientErrorException.TooManyRequests) e).getStatusCode().value() == 429){
                try {
                    Thread.sleep(60000);
                    exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
                    if (exchange.getStatusCode().value()==200) {
                        passengerAttributesList = JSONObject.parseArray(JSONObject.toJSONString(exchange.getBody().get("data")), HKPassengerFlowAttributesDTO.class);
                    }
                } catch (InterruptedException interruptedException) {
                    log.info("InterruptedException:{}",e);
                }
            }
        }
        return passengerAttributesList;
    }


    /**
     * 分页查询门店区域组织节点全量列表
     * @param eid
     * @param accountType
     * @param pageNo
     * @param pageSize
     * @return
     */
    private HikRegionStoreListResponse getAreas(String eid, AccountTypeEnum accountType, Integer pageNo, Integer pageSize) {
        ResponseEntity<JSONObject> exchange = null;
        HikRegionStoreListResponse hikRegionStoreListResponse = new HikRegionStoreListResponse();
        try {
            String hikCloudAccessToken = this.getAccessToken(eid,accountType);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            String url = hikUrl + "/v1/customization/areas/list?pageNo="+pageNo+"&pageSize="+pageSize;
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                Map data = (Map) exchange.getBody().get("data");
                hikRegionStoreListResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikRegionStoreListResponse.class);
            }
        } catch (RestClientException e) {
            log.info("设备列表获取异常e:{}",e);
        }
        return hikRegionStoreListResponse;
    }


    /**
     * 查询指定企业下门店
     * @param eid
     * @param accountType
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<HikStoreDTO> getStoreInfo(String eid, AccountTypeEnum accountType, Integer pageNo, Integer pageSize){
        SettingVO setting = enterpriseVideoSettingService.getSetting(eid, YunTypeEnum.HIKCLOUD, accountType);
        if (StringUtils.isEmpty(setting.getHikCloudFristNodeId())){
            return Collections.emptyList();
        }
        List<HikStoreDTO> result = new ArrayList<>();
        ResponseEntity<JSONObject> exchange = null;
        try {
            String hikCloudAccessToken = this.getAccessToken(eid,accountType);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            //查询根节点区域ID
            String url = hikUrl + "/v1/customization/storeInfo?pageNo="+pageNo+"&pageSize="+pageSize;
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);

            if (exchange.getStatusCode().value()==200){
                Map data = (Map) exchange.getBody().get("data");
                HikStoreResponse hikStoreResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikStoreResponse.class);
                List<HikStoreDTO> rows = hikStoreResponse.getRows();
                String areaPath = setting.getHikCloudFristNodeId();
                rows = rows.stream().filter(x-> x.getAreaPath().startsWith(areaPath)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(rows)) {
                    result.addAll(rows);
                }
                Boolean flag = hikStoreResponse.getHasNextPage();
                while (flag) {
                    //从第一页开始查询
                    pageNo = pageNo + 1;
                    HikStoreResponse hikStoreRes = getStoreList(eid, accountType, pageNo, pageSize);
                    List<HikStoreDTO> storeList = hikStoreRes.getRows();
                    flag = hikStoreRes.getHasNextPage();
                    //过滤掉非一级菜单

                    storeList = storeList.stream().filter(x-> x.getAreaPath().startsWith(areaPath)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(storeList)) {
                        result.addAll(storeList);
                    }
                }
            }
        } catch (RestClientException e) {
            log.info("设备列表获取异常e:{}",e);
        }
        log.info("store_size:{}",result.size());
        return result;
    }


    /**
     * 查询门店列表
     * @param eid
     * @param accountType
     * @param pageNo
     * @param pageSize
     * @return
     */
    private HikStoreResponse getStoreList(String eid, AccountTypeEnum accountType, Integer pageNo, Integer pageSize) {
        ResponseEntity<JSONObject> exchange = null;
        HikStoreResponse hikStoreResponse = new HikStoreResponse();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String url = hikUrl + "/v1/customization/storeInfo?pageNo="+pageNo+"&pageSize="+pageSize;
        try {
            String hikCloudAccessToken = this.getAccessToken(eid,accountType);
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                Map data = (Map) exchange.getBody().get("data");
                hikStoreResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikStoreResponse.class);
            }
        } catch (RestClientException e) {
            log.info("门店列表获取异常e:{}",e);
            //如果超限 1分钟之后重新请求
            if (((HttpClientErrorException.TooManyRequests) e).getStatusCode().value() == 429){
                try {
                    Thread.sleep(60000);
                    exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
                    if (exchange.getStatusCode().value()==200) {
                        Map data = (Map) exchange.getBody().get("data");
                        hikStoreResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikStoreResponse.class);
                    }
                } catch (InterruptedException interruptedException) {
                    log.info("InterruptedException:{}",e);
                }
            }
        }
        return hikStoreResponse;
    }


    /**
     * 获取指定门店设备
     * @param eid
     * @param accountType
     * @return
     */
    private  List<OpenDevicePageDTO> getNodeDeviceList(String eid, AccountTypeEnum accountType,String hikStoreId){
        //先查询设备配置表中是否配置了syncDeviceFlag
        SettingVO setting = enterpriseVideoSettingService.getSetting(eid, YunTypeEnum.HIKCLOUD, accountType);
        PageInfo<OpenDevicePageDTO> resultInfo = new PageInfo<>();
        if (StringUtils.isEmpty(setting.getHikCloudFristNodeId())){
            return Collections.emptyList();
        }
        Integer pageNo = Constants.INDEX_ONE;
        Integer pageSize = Constants.PAGE_SIZE;
        //查询出所有的门店
        List<OpenDevicePageDTO> result = new ArrayList<>();
        ResponseEntity<JSONObject> exchange = null;
        String url = null;
        String hikCloudAccessToken = this.getAccessToken(eid,accountType);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json;charset=utf-8");
        headers.add("Authorization",BEARER+hikCloudAccessToken);
        try {
            url = hikUrl + "/v1/customization/cameraList?pageNo="+pageNo+"&pageSize="+pageSize+"&storeId="+hikStoreId;
            log.info("______________url:{}",url);
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            if (exchange.getStatusCode().value()==200){
                Map data = (Map) exchange.getBody().get("data");
                HikAllDeviceResponse hikAllDeviceResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikAllDeviceResponse.class);
                List<OpenDevicePageDTO> rows = hikAllDeviceResponse.getRows();
                if (CollectionUtils.isNotEmpty(rows)) {
                    result.addAll(rows);
                }
                Boolean flag = hikAllDeviceResponse.getHasNextPage();
                while (flag) {
                    //从第一页开始查询
                    pageNo = pageNo + 1;
                    HikAllDeviceResponse hikAllDevice = getStoreDeviceByStoreId(eid, accountType, pageNo, pageSize,hikStoreId);
                    List<OpenDevicePageDTO> storeList = hikAllDevice.getRows();
                    flag = hikAllDevice.getHasNextPage();
                    if (CollectionUtils.isNotEmpty(storeList)) {
                        result.addAll(storeList);
                    }
                }
            }
        } catch (RestClientException e) {
            log.info("设备列表获取异常e:{}",e);
            if (((HttpClientErrorException.TooManyRequests) e).getStatusCode().value() == 429){
                try {
                    Thread.sleep(60000);
                    exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
                    if (exchange.getStatusCode().value()==200) {
                        Map data = (Map) exchange.getBody().get("data");
                        HikAllDeviceResponse hikAllDeviceResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikAllDeviceResponse.class);
                        List<OpenDevicePageDTO> rows = hikAllDeviceResponse.getRows();
                        if (CollectionUtils.isNotEmpty(rows)) {
                            result.addAll(rows);
                        }
                    }
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
        return result;
    }


    /**
     * 通过门店ID查询门店下设备
     * @param eid
     * @param accountType
     * @param pageNo
     * @param pageSize
     * @param hikStoreId
     * @return
     */
    private HikAllDeviceResponse getStoreDeviceByStoreId(String eid, AccountTypeEnum accountType, Integer pageNo, Integer pageSize,String hikStoreId) {
        ResponseEntity<JSONObject> exchange = null;
        HikAllDeviceResponse hikAllDeviceResponse = new HikAllDeviceResponse();
        try {
            String hikCloudAccessToken = this.getAccessToken(eid,accountType);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            String url = hikUrl + "/v1/customization/cameraList?pageNo="+pageNo+"&pageSize="+pageSize+"&storeId="+hikStoreId;
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                Map data = (Map) exchange.getBody().get("data");
                hikAllDeviceResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikAllDeviceResponse.class);
            }
        } catch (RestClientException e) {
            log.info("根据门店获取设备列表异常e:{}",e);
        }
        return hikAllDeviceResponse;
    }




}
