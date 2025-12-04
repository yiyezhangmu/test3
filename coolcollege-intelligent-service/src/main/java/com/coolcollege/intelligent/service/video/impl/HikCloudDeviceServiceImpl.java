package com.coolcollege.intelligent.service.video.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceSceneEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceSourceEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.CoolListUtils;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.OpenDevicePageDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.model.video.platform.hik.HikAllDeviceResponse;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudChannelsDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudDeviceDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudLiveAddressDTO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.video.HikCloudDeviceService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/8/23 15:15
 * @Version 1.0
 */
@Slf4j
@Service
public class HikCloudDeviceServiceImpl implements HikCloudDeviceService {

    private static  final  String BEARER = "Bearer ";

    @Autowired
    private RedisUtilPool redisUtil;
    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Autowired
    private RestTemplate restTemplate;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private StoreService storeService;
    @Resource
    private DeviceChannelMapper deviceChannelMapper;

    private static final String hikUrl = "https://api2.hik-cloud.com";

    @Override
    public String getHikCloudRedisToken(String eid) {
        String token = redisUtil.getString(getKey(eid));
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        SettingVO setting = enterpriseVideoSettingService.getSetting(eid, YunTypeEnum.HIKCLOUD, AccountTypeEnum.PRIVATE);
        token = getHikCloudAccessToken(setting.getAccessKeyId(), setting.getSecret());
        if (StringUtils.isBlank(token)) {
            throw new ServiceException(ErrorCodeEnum.HIK_CLOUD_ACCESS_TOKEN_GET_ERROR);
        }
        redisUtil.setString(getKey(eid), token, 3600);
        return token;
    }

    @Override
    public List<OpenDevicePageDTO>  getAllDeviceList(String eid) {
        ResponseEntity<JSONObject> exchange = null;
        List<OpenDevicePageDTO> result = new ArrayList<>();
        try {
            Integer pageNo = 1;
            Integer pageSize = 10;
            String hikCloudAccessToken = this.getHikCloudRedisToken(eid);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            String url = hikUrl + "/v1/customization/stores/cameraList?pageNo="+pageNo+"&pageSize="+pageSize;
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            if (exchange.getStatusCode().value()==200){
                Map data = (Map) exchange.getBody().get("data");
                log.info("-------------{}",JSONObject.toJSONString(data));
                HikAllDeviceResponse hikAllDeviceResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikAllDeviceResponse.class);
                Integer total = hikAllDeviceResponse.getTotal();
                if (total!=0){
                    result.addAll(hikAllDeviceResponse.getRows());
                }
                int i = (total - 1) / pageSize + 1;
                while (i - pageNo > 0) {
                    pageNo = pageNo + 1;
                    List<OpenDevicePageDTO> allDevicePage = getAllDevicePage(eid, pageNo, pageSize);
                    if (CollectionUtils.isNotEmpty(allDevicePage)) {
                        result.addAll(allDevicePage);
                    }
                }
            }
        } catch (RestClientException e) {
            log.info("设备列表获取异常e:{}",e);
        }
        return result;
    }


    /**
     * 分页获取设备
     * @param eid
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<OpenDevicePageDTO>  getAllDevicePage(String eid,Integer pageNo, Integer pageSize) {
        ResponseEntity<JSONObject> exchange = null;
        List<OpenDevicePageDTO> result = new ArrayList<>();
        try {
            String hikCloudAccessToken = this.getHikCloudRedisToken(eid);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            String url = hikUrl + "/v1/customization/stores/cameraList?pageNo="+pageNo+"&pageSize="+pageSize;
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                Map data = (Map) exchange.getBody().get("data");
                HikAllDeviceResponse hikAllDeviceResponse = JSONObject.parseObject(JSONObject.toJSONString(data), HikAllDeviceResponse.class);
                result.addAll(hikAllDeviceResponse.getRows());
            }
        } catch (RestClientException e) {
            log.info("设备列表获取异常e:{}",e);
        }
        return result;
    }
    @Override
    public Boolean liveVideoOpen(String eid,List<String> channelIds) {
        ResponseEntity<JSONObject> exchange = null;
        try {
            String url = hikUrl + "/v1/customization/liveVideoOpen";
            HttpHeaders headers = new HttpHeaders();
            String hikCloudAccessToken = this.getHikCloudRedisToken("");
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            // 设置请求参数
            MultiValueMap<String, String> map = new LinkedMultiValueMap();
            map.put("channelIds",channelIds);
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
        } catch (Exception e) {
            log.error("liveVideoOpen_error:{}",e);
        }
        return Boolean.TRUE;
    }

    @Override
    public HikCloudLiveAddressDTO getLiveAddress(String eid, String channelId) {
        HikCloudLiveAddressDTO data = null;
        try {
            String hikCloudAccessToken = this.getHikCloudRedisToken(eid);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            String url = hikUrl + "/v1/customization/liveAddress?channelId=1a4753365366402db1816439e0c03f07";
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            if (exchange.getBody()==null||exchange.getBody().get("data")==null){
                return new HikCloudLiveAddressDTO();
            }
            data = JSONObject.parseObject(JSONObject.toJSONString(exchange.getBody().get("data")), HikCloudLiveAddressDTO.class);
        } catch (RestClientException e) {
            log.error("getLiveAddress_error:{}",e);
        }
        return data;
    }

    @Override
    public String capture(String eid ,String deviceSerial,String channelNo,String quality) {
        ResponseEntity<JSONObject> exchange = null;
        try {
            String url = hikUrl + "/v1/customization/capture";
            HttpHeaders headers = new HttpHeaders();
            String hikCloudAccessToken = this.getHikCloudRedisToken( eid);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            // 设置请求参数
            MultiValueMap<String, Object> map = new LinkedMultiValueMap();
            map.put("deviceSerial",Collections.singletonList(deviceSerial));
            map.put("channelNo",Collections.singletonList(channelNo));
            map.put("quality",Collections.singletonList(quality));
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            JSONObject body = exchange.getBody();
            if (exchange.getBody()==null||exchange.getBody().get("data")==null){
                return "";
            }
            return (String) ((Map)exchange.getBody().get("data")).get("picUrl");
        } catch (Exception e) {
            log.error("getHikCloudAccessToken_error:{}",e);
        }
        return "";
    }

    @Override
    public void asyncDevice(String eid, String userId) {
//        DataSourceHelper.reset();
//        EnterpriseConfigDO config = enterpriseConfigMapper.selectByEnterpriseId(eid);
//        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
//        //查询海康云眸平台上所有使用得设备
//        List<HikCloudDeviceDTO> allDeviceList = getAllDeviceList(eid);
//        //在线设备的序列号集合
//        List<String> newDeviceIdList = ListUtils.emptyIfNull(allDeviceList)
//                .stream()
//                .filter(data -> data.getDeviceStatus() == 1)
//                .map(HikCloudDeviceDTO::getDeviceSerial)
//                .collect(Collectors.toList());
//        //所有的设备序列号map
//        Map<String, HikCloudDeviceDTO> deviceMap = ListUtils.emptyIfNull(allDeviceList)
//                .stream()
//                .collect(Collectors.toMap(HikCloudDeviceDTO::getDeviceSerial, data -> data, (a, b) -> a));
//        //根据设备序列号分组
//        Map<String, List<HikCloudDeviceDTO>> deviceMapGroupBySerial = ListUtils.emptyIfNull(allDeviceList)
//                .stream()
//                .collect(Collectors.groupingBy(HikCloudDeviceDTO::getDeviceSerial));
//        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
//        //所有的宇视云设备
//        List<String> olderVideoIdList = deviceMapper.getDeviceIdByType(eid, DeviceTypeEnum.DEVICE_VIDEO.getCode(), DeviceSourceEnum.HIKCLOUD.getCode());
//        //判断删除和添加设备
//        List<String> deleteDeviceIdList = CoolListUtils.getReduceaListThanbList(olderVideoIdList, newDeviceIdList);
//        //在线的设备(新增或者更新的设备)
//        List<DeviceDO> addDeviceDOList = ListUtils.emptyIfNull(newDeviceIdList)
//                .stream()
//                .map(data -> mapDeviceDO( userId, deviceMap, data, deviceMapGroupBySerial.get(data).size()))
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
//        if (CollectionUtils.isNotEmpty(addDeviceDOList)) {
//            ListUtils.partition(addDeviceDOList, 100)
//                    .forEach(data -> {
//                                deviceMapper.batchInsertOrUpdateDevices(eid, data, DeviceTypeEnum.DEVICE_VIDEO.getCode());
//                            }
//                    );
//        }
//        //子通道的设备序列号
//        List<HikCloudDeviceDTO> nvrDeviceIdList = new ArrayList<>();
//        Set<String> list = new TreeSet<>();
//        allDeviceList.forEach(x->{
//            if (deviceMapGroupBySerial.get(x.getDeviceSerial()).size()>1){
//                if(!list.contains(x.getDeviceSerial())){
//                    list.add(x.getDeviceSerial());
//                    nvrDeviceIdList.addAll(deviceMapGroupBySerial.get(x.getDeviceSerial()));
//                }
//            }
//        });
//        List<DeviceChannelDO> deviceChannelDOList = ListUtils.emptyIfNull(nvrDeviceIdList)
//                .stream()
//                .filter(Objects::nonNull)
//                .map(data -> {
//                    DeviceChannelDO deviceChannelDO = new DeviceChannelDO();
//                    deviceChannelDO.setParentDeviceId(data.getDeviceSerial());
//                    deviceChannelDO.setDeviceId(data.getChannelId());
//                    deviceChannelDO.setChannelName(data.getChannelName());
//                    deviceChannelDO.setChannelNo(String.valueOf(data.getChannelNo()));
//                    deviceChannelDO.setStatus(data.getDeviceStatus());
//                    //NVR 设备全部支持抓拍，直接设置为1
//                    deviceChannelDO.setSupportCapture(1);
//                    deviceChannelDO.setHasPtz(false);
//                    return deviceChannelDO;
//                })
//                .collect(Collectors.toList());
//        if (CollectionUtils.isNotEmpty(deviceChannelDOList)) {
//            deviceChannelMapper.batchInsertOrUpdateDeviceChannel(eid, deviceChannelDOList);
//        }
//        if (CollectionUtils.isNotEmpty(deleteDeviceIdList)) {
//            ListUtils.partition(deleteDeviceIdList, 100).forEach(data -> {
//                //更新门店是否绑定摄像头的状态
//                List<DeviceDO> deviceMappingDOList = deviceMapper.getDeviceByDeviceIdList(eid, data);
//
//                List<String> storeIdList = ListUtils.emptyIfNull(deviceMappingDOList).stream()
//                        .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
//                        .map(DeviceDO::getBindStoreId)
//                        .collect(Collectors.toList());
//                if (CollectionUtils.isNotEmpty(storeIdList)) {
//                    storeService.updateStoreCamera(eid, storeIdList);
//                }
//                deviceMapper.batchDeleteDevices(eid, data, DeviceTypeEnum.DEVICE_VIDEO.getCode());
//                deviceChannelMapper.batchDeleteDeviceChannelByDeviceId(eid, data);
//            });
//        }


    }

    @Override
    public String authentication(String eid) {
        ResponseEntity<JSONObject> exchange = null;
        List<HikCloudDeviceDTO> result = new ArrayList<>();
        try {
            String hikCloudAccessToken = this.getHikCloudRedisToken(eid);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add("Content-Type", "application/json;charset=utf-8");
            headers.add("Authorization",BEARER+hikCloudAccessToken);
            String url = hikUrl + "/v1/ezviz/account/info";
            exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), JSONObject.class);
            if (exchange.getStatusCode().value()==HttpStatus.OK.value()){
                Map data = (HashMap) exchange.getBody().get("data");
                String token = (String) data.get("token");
                return token;
            }
        } catch (RestClientException e) {
            log.info("视频取流时需要的认证信息获取异常e:{}",e);
        }
        return "";
    }

    @Override
    public List<HikCloudChannelsDTO> listByDevSerial(String eid, String deviceSerial) {
        ResponseEntity<JSONObject> exchange = null;
        try {
            String url = hikUrl + "/v1/customization/devices/channels/actions/listByDevSerial?deviceSerial="+deviceSerial;
            HttpHeaders headers = new HttpHeaders();
            String hikCloudAccessToken = this.getHikCloudRedisToken( eid);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("Authorization",BEARER+hikCloudAccessToken);

            HttpEntity<Object> req = new HttpEntity<>(headers);
            exchange = restTemplate.exchange(url, HttpMethod.GET, req, JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            if (exchange.getBody()==null||exchange.getBody().get("data")==null){
                return Lists.newArrayList();
            }
            List<HikCloudChannelsDTO> hik = JSONObject.parseArray(JSONObject.toJSONString(exchange.getBody().get("data")), HikCloudChannelsDTO.class);
            return hik;
        } catch (Exception e) {
            log.error("listByDevSerial_error:{}",e);
        }
        return Lists.newArrayList();
    }


    private DeviceDO mapDeviceDO(String userId, Map<String,HikCloudDeviceDTO> deviceMap, String deviceSerial,Integer num){
        if (MapUtils.isEmpty(deviceMap)) {
            return null;
        }
        HikCloudDeviceDTO hikCloudDeviceDTO = deviceMap.get(deviceSerial);
        DeviceDO deviceDO = new DeviceDO();
        deviceDO.setDeviceId(hikCloudDeviceDTO.getDeviceSerial());
        deviceDO.setDeviceName(hikCloudDeviceDTO.getDeviceName());
        deviceDO.setType(DeviceTypeEnum.DEVICE_VIDEO.getCode());
        deviceDO.setCreateTime(System.currentTimeMillis());
        deviceDO.setCreateName(userId);
        deviceDO.setRemark("海康云眸手动同步");
        deviceDO.setDataSourceId(hikCloudDeviceDTO.getDeviceId());
        deviceDO.setUpdateTime(System.currentTimeMillis());
        deviceDO.setBindStatus(false);
        deviceDO.setStoreSceneId(Constants.DEFAULT_STORE_ID);
        if (hikCloudDeviceDTO.getDeviceStatus() == 1) {
            deviceDO.setDeviceStatus(DeviceStatusEnum.ONLINE.getCode());
        } else {
            deviceDO.setDeviceStatus(DeviceStatusEnum.OFFLINE.getCode());
        }
        //默认都支持抓拍
        deviceDO.setSupportCapture(1);
        deviceDO.setDeviceScene(DeviceSceneEnum.OTHER.getCode());
        deviceDO.setResource(YunTypeEnum.HIKCLOUD.getCode());
        if (num>1){
            deviceDO.setHasChildDevice(true);
        }else {
            deviceDO.setHasChildDevice(false);
        }
        deviceDO.setHasPtz(false);
        return deviceDO;
    }

    /**
     * 获取hik cloud 存储token的key
     * @param eid
     * @return
     */
    private String getKey(String eid) {
        return Constants.HIK_TOKEN + eid;
    }

    /**
     * 获取海康云眸认证token
     * @param accessKeyId
     * @param accessKeySecret
     * @return
     */
    private String getHikCloudAccessToken(String accessKeyId, String accessKeySecret) {
        ResponseEntity<JSONObject> exchange = null;
        try {
            String url = hikUrl + "/oauth/token";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            // 设置请求参数
            MultiValueMap<String, String> map = new LinkedMultiValueMap();
            map.put("client_id",Collections.singletonList(accessKeyId));
            map.put("client_secret",Collections.singletonList(accessKeySecret));
            map.put("grant_type",Collections.singletonList("client_credentials"));
            map.put("scope",Collections.singletonList("app"));
            HttpEntity<Object> req = new HttpEntity<>(map, headers);
            exchange = restTemplate.exchange(url, HttpMethod.POST, req, JSONObject.class);
            log.info(JSONObject.toJSONString(exchange));
            if (exchange.getBody()==null){
                return "";
            }
            return exchange.getBody().getString("access_token");
        } catch (Exception e) {
            log.error("getHikCloudAccessToken_error:{}",e);
        }
        return "";
    }

}
