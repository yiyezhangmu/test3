package com.coolcollege.intelligent.service.video.openapi.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.enums.device.WdzApiMtEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.WdzResponse;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.AppKeyDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudAreasDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.PassengerDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
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
import com.coolcollege.intelligent.common.util.MD5Util;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * describe: 万店掌视频设备 服务实现类
 *
 * @author wangff
 * @date 2024/10/15
 */
@Slf4j
@Service
public class WdzDeviceServiceImpl implements VideoOpenService {

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Resource
    private RestTemplate restTemplate;

    @Value("${wdz.username}")
    private String wdzUsername;
    @Value("${wdz.password}")
    private String wdzPassword;
    @Value("${wdz.url}")
    private String wdzUrl;

    /**
     * 公共请求参数时间戳格式器
     */
    private final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public YunTypeEnum getYunTypeNum() {
        return YunTypeEnum.WDZ;
    }

    @Override
    public String getAccessToken(String eid, AccountTypeEnum accountType) {
        String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, eid, accountType.getCode(), getYunTypeNum().getCode());
        String accessToken = redisUtilPool.getString(cacheKey);
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        try {
            Map<String, Object> serviceParams = new HashMap<>();
            serviceParams.put("userName", wdzUsername);
            serviceParams.put("password", wdzPassword);
            WdzResponse exchange = wdzExchange(eid, accountType, WdzApiMtEnum.LOGIN, serviceParams, null, false);

            if (exchange.isOk()) {
                Object data = exchange.getData();
                if (ObjectUtil.isNotNull(data)) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(data));
                    accessToken = jsonObject.getString("token");
                    redisUtilPool.setString(cacheKey, accessToken, 24*60*60);
                }
            }
        } catch (Exception e) {
            log.error("getWdzAccessToken_error:{}", e);
        }
        return accessToken;
    }

    @Override
    public OpenDeviceDTO getDeviceDetail(String eid, String deviceId, AccountTypeEnum accountType) {
        Map<String, Object> serviceParams = new HashMap<>();
        serviceParams.put("id", deviceId);

        WdzResponse exchange = wdzExchange(eid, accountType, WdzApiMtEnum.DEVICE_STATES, serviceParams, null, true);
        if (exchange.isOk()) {
            Object data = exchange.getData();
            if (ObjectUtil.isNotNull(data)) {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(data));
                Integer online = jsonObject.getInteger("online");
                OpenDeviceDTO openDeviceDTO = new OpenDeviceDTO();
                openDeviceDTO.setDeviceId(deviceId);
                openDeviceDTO.setDeviceStatus(Constants.INDEX_ONE.equals(online) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                openDeviceDTO.setDeviceName(jsonObject.getString("name"));
                openDeviceDTO.setSource(getYunTypeNum().getCode());
                DeviceCapacityDTO deviceCapacity = DeviceCapacityDTO.convertWDZDeviceCapacity();
                openDeviceDTO.setSupportCapture(deviceCapacity.getSupportCapture());
                openDeviceDTO.setDeviceCapacity(deviceCapacity);
                return openDeviceDTO;
            }
        }
        return null;
    }

    @Override
    public PageInfo<OpenDevicePageDTO> getDeviceList(String eid, AccountTypeEnum accountType, Integer pageNum, Integer pageSize) {
        // 万店掌门店id，用于万店掌设备查询
        Map<String, Object> serviceParams = new HashMap<>();
        // 万店掌店铺id对应授权号
        String deviceWdzSyncStoreIdMapperIds = redisUtilPool.getString("device_wdz_sync_store_mapper_ids");
        List<String> storeIdsMapList = ListUtil.toList(StringUtil.split(deviceWdzSyncStoreIdMapperIds, ","));
        Map<String, String> storeIdsMap;
        if (CollectionUtil.isNotEmpty(storeIdsMapList)) {
            List<String[]> list = CollStreamUtil.toList(storeIdsMapList, v -> StringUtil.split(v, ":"));
            storeIdsMap = CollStreamUtil.toMap(list, v -> v[0], v -> v[1]);
        } else {
            storeIdsMap = Collections.emptyMap();
        }

        // 查询每个门店的监控设备
        List<OpenDevicePageDTO> result = new LinkedList<>();
        for (String wdzStoreId : storeIdsMap.keySet()) {
            serviceParams.clear();
            serviceParams.put("id", wdzStoreId);

            WdzResponse exchange = wdzExchange(eid, accountType, WdzApiMtEnum.DEVICE_LIST, serviceParams, null, true);
            if (exchange.isOk()) {
                Object data = exchange.getData();
                if (ObjectUtil.isNotNull(data)) {
                    WdzDeviceDataDTO dataDTO = JSONObject.parseObject(JSONObject.toJSONString(data), WdzDeviceDataDTO.class);
                    List<WdzDeviceDataDTO.WdzDeviceDetail> deviceDetailList = dataDTO.getList().stream().flatMap(v -> v.getDevices().stream()).collect(Collectors.toList());
                    List<OpenDevicePageDTO> list = CollStreamUtil.toList(deviceDetailList, v -> convertToOpenDevicePageDTO(v, storeIdsMap.getOrDefault(wdzStoreId, "")));
                    result.addAll(list);
                }
            }
        }
        PageInfo<OpenDevicePageDTO> pageInfo = new PageInfo<>();
        pageInfo.setList(result);
        return pageInfo;
    }

    /**
     * 万店掌设备详情对象转换为外部第三方设备列表对象
     *
     * @param deviceDetail 万店掌设备详情
     * @return 外部第三方设备列表对象
     */
    private OpenDevicePageDTO convertToOpenDevicePageDTO(WdzDeviceDataDTO.WdzDeviceDetail deviceDetail, String storeCode) {
        OpenDevicePageDTO pageDevice = new OpenDevicePageDTO();
        pageDevice.setDeviceId(deviceDetail.getId().toString());
//        pageDevice.setDeviceSerial();
        pageDevice.setDeviceName(deviceDetail.getName());
        pageDevice.setSource(getYunTypeNum().getCode());
        pageDevice.setStoreCode(storeCode);
        pageDevice.setDeviceStatus(Constants.INDEX_ONE.equals(deviceDetail.getOnline()) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
        pageDevice.setHasPtz(Constants.INDEX_ONE.equals(deviceDetail.getPtzEnable()));
        return pageDevice;
    }

    @Override
    public LiveVideoVO getLiveUrl(String eid, DeviceDO device, VideoDTO param) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        VideoProtocolTypeEnum protocolTypeEnum = param.getProtocol();
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("deviceId", device.getDeviceId());
        bodyParams.put("isSlave", 0);
//        bodyParams.put("timeout", 0);
//        bodyParams.put("permanent", true);

        WdzResponse exchange = wdzExchange(eid, accountType, WdzApiMtEnum.MEDIA_PLAY, null, bodyParams, true);
        String liveUrl = null;
        if (exchange.isOk()) {
            Object data = exchange.getData();
            if (ObjectUtil.isNotNull(data)) {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(data));
                if (VideoProtocolTypeEnum.HTTPS_FLV.equals(protocolTypeEnum)) {
                    liveUrl = jsonObject.getString("tlsflv");
                } else if (VideoProtocolTypeEnum.HLS.equals(protocolTypeEnum)) {
                    liveUrl = jsonObject.getString("hls");
                } else {
                    liveUrl = jsonObject.getString("flv");
                }
            }
        }
        LiveVideoVO result = new LiveVideoVO();
        result.setUrl(liveUrl);
        return result;
    }

    @Override
    public LiveVideoVO getPastVideoUrl(String eid, DeviceDO device, VideoDTO param) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        VideoProtocolTypeEnum protocolTypeEnum = param.getProtocol();
        String startTime = param.getStartTime(), endTime = param.getEndTime();
        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("deviceId", device.getDeviceId());
        bodyParams.put("startTime", startTime);
        bodyParams.put("endTime", endTime);
        bodyParams.put("playRate", 1);

        WdzResponse exchange = wdzExchange(eid, accountType, WdzApiMtEnum.PAST_PLAY, null, bodyParams, true);

        String liveUrl = null;
        if (exchange.isOk()) {
            Object data = exchange.getData();
            if (ObjectUtil.isNotNull(data)) {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(data));
                if (ObjectUtil.isNull(protocolTypeEnum)) {
                    protocolTypeEnum = VideoProtocolTypeEnum.FLV;
                }
                switch (protocolTypeEnum) {
                    case RTMP:
                        liveUrl = jsonObject.getString("rtmp");
                        break;
                    case HTTPS_FLV:
                        liveUrl = jsonObject.getString("tlsflv");
                        break;
                    case HLS:
                        liveUrl = jsonObject.getString("hls");
                        break;
                    case RTSP:
                        liveUrl = jsonObject.getString("rtsp");
                        break;
                    default:
                        liveUrl = jsonObject.getString("flv");
                        break;
                }
            }
        }
        LiveVideoVO result = new LiveVideoVO();
        result.setUrl(liveUrl);
        return result;
    }

    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {

    }

    @Override
    public Boolean ptzStart(String eid, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration) {
        int ptzCommand = command == 0 ? 1 : command == 1 ? 2 : command == 2 ? 3 : command == 3 ? 4 : command == 8 ? 7 : command == 9 ? 8 : 6;
        return ptzControl(eid, device, ptzCommand);
    }

    @Override
    public Boolean ptzStop(String eid, DeviceDO device, String channelNo) {
        return ptzControl(eid, device, 6);
    }

    /**
     *
     * @param eid 企业id
     * @param device 设备信息
     * @param ptzCommand 1:上、2:下、3:左、4:右、6:停止、7:拉近、8:拉远、15:跳转场景
     * @return 是否成功
     */
    private Boolean ptzControl(String eid, DeviceDO device, Integer ptzCommand) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());

        Map<String, Object> serviceParams = new HashMap<>();
        serviceParams.put("devicesId", device.getDeviceId());
        serviceParams.put("commandValue", ptzCommand);
        serviceParams.put("param1", 0);
        serviceParams.put("param2", 0);

        WdzResponse exchange = wdzExchange(eid, accountType, WdzApiMtEnum.PTZ_CTRL, serviceParams, null, true);

        if (exchange.isOk()) {
            Object data = exchange.getData();
            return Boolean.TRUE.equals(data);
        }
        return false;
    }

    @Override
    public String addPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String devicePositionName) {
        return "";
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
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());

        Map<String, Object> serviceParams = new HashMap<>();
        serviceParams.put("id", device.getDeviceId());

        WdzResponse exchange = wdzExchange(eid, accountTypeEnum, WdzApiMtEnum.CAPTURE, serviceParams, null, true);

        if (exchange.isOk()) {
            Object data = exchange.getData();
            if (ObjectUtil.isNotNull(data)) {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(data));
                return jsonObject.getString("url");
            }
        }
        return "";
    }

    @Override
    public String videoTransCode(String enterpriseId, DeviceDO device, VideoDTO param) {
        String startTime = DateUtils.dateTimeFormat(param.getStartTime());
        String endTime = DateUtils.dateTimeFormat(param.getEndTime());
        VideoProtocolTypeEnum protocolType = param.getProtocol();
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());

        Map<String, Object> bodyParams = new HashMap<>();
        bodyParams.put("deviceId", device.getDeviceId());
        bodyParams.put("startTime", startTime);
        bodyParams.put("endTime", endTime);
        WdzResponse exchange = wdzExchange(enterpriseId, accountType, WdzApiMtEnum.DOWNLOAD_VIDEO, null, bodyParams, true);
        if (exchange.isOk()) {
            Object data = exchange.getData();
            if (ObjectUtil.isNotNull(data)) {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(data));
                String url;
                if (VideoProtocolTypeEnum.HTTPS_FLV.equals(protocolType)) {
                    url = jsonObject.getString("tlsflv");
                } else if (VideoProtocolTypeEnum.FLV.equals(protocolType) || VideoProtocolTypeEnum.HTTP_FLV.equals(protocolType)) {
                    url = jsonObject.getString("flv");
                } else if (VideoProtocolTypeEnum.RTSP.equals(protocolType)) {
                    url = jsonObject.getString("rtsp");
                } else {
                    url = jsonObject.getString("tlsFmp4");
                }
                log.info("万店掌视频回放下载，url:{}", url);
                // 万店掌直接返回url
                return url;
            }
        }
        return "";
    }

    @Override
    public VideoFileDTO getVideoFile(String enterpriseId, DeviceDO device, String fileId) {
        return null;
    }

    @Override
    public List<String> getVideoDownloadUrl(String enterpriseId, DeviceDO device, String fileId) {
        return Collections.emptyList();
    }

    /**
     * 创建请求参数
     *
     * @param eid           企业id
     * @param mt            接口名称
     * @param serviceParams 业务参数
     * @param version       接口版本
     * @return 请求参数列表
     */
    private Map<String, Object> createRequestParams(
            String eid,
            AccountTypeEnum accountType,
            String mt,
            Map<String, Object> serviceParams,
            String version
    ) {
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(eid, getYunTypeNum().getCode(), accountType.getCode());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        if (videoSetting == null) {
            throw new ServiceException(ErrorCodeEnum.WDZ_ACCESS_TOKEN_GET_ERROR);
        }
        String appKey = videoSetting.getAccessKeyId();
        String appSecret = videoSetting.getSecret();
        // 公共参数
        Map<String, Object> params = new HashMap<>();
        params.put("_aid", "DC-000392");
        params.put("_akey", appKey);
        params.put("_mt", mt);
        params.put("_sm", "md5");
        params.put("_requestMode", "POST");
        params.put("_version", version);
        params.put("_timestamp", LocalDateTime.now().format(timestampFormatter));
        // 业务参数
        if (MapUtils.isNotEmpty(serviceParams)) {
            params.putAll(serviceParams);
        }
        String s = MapUtil.sortJoin(params, "", "", true);
        String sign = MD5Util.md5(appSecret + s + appSecret).toUpperCase();
        params.put("_sig", sign);
        return params;
    }

    /**
     * 创建请求头
     * @param accessToken token
     * @return 请求头
     */
    private HttpHeaders buildHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (StringUtils.isNotBlank(accessToken)) {
            headers.set("authenticator", accessToken);
        }
        return headers;
    }

    /**
     * 万店掌接口请求
     * @param eid 企业id
     * @param accountType 账号类型
     * @param apiType 接口类型枚举
     * @param serviceParams 业务参数
     * @param bodyParams body参数
     * @return 万店掌接口响应体
     */
    private WdzResponse wdzExchange(
            String eid,
            AccountTypeEnum accountType,
            WdzApiMtEnum apiType,
            Map<String, Object> serviceParams,
            Map<String, Object> bodyParams,
            boolean hasAccessToken
    ) {
        String accessToken = hasAccessToken ? getAccessToken(eid, accountType) : null;
        HttpHeaders headers = buildHeaders(accessToken);

        Map<String, Object> requestParams = createRequestParams(eid, accountType, apiType.getMt(), serviceParams, apiType.getVersion());
        String url = wdzUrl + "?" + MapUtil.join(requestParams, "&", "=");
        HttpEntity<Object> req = new HttpEntity<>(MapUtils.isEmpty(bodyParams) ? Collections.emptyMap() : bodyParams, headers);
        log.info("url:{}, req:{}", url, req);

        WdzResponse exchange = restTemplate.postForObject(url, req, WdzResponse.class);
        log.info(JSONObject.toJSONString(exchange));
        if (ObjectUtil.isNull(exchange)) {
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        return exchange;
    }
}
