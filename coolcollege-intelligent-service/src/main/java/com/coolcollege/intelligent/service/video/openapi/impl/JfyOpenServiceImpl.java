package com.coolcollege.intelligent.service.video.openapi.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceCatalog;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.device.vo.DeviceVideoRecordVO;
import com.coolcollege.intelligent.model.device.vo.DeviceSoftHardwareInfoVO;
import com.coolcollege.intelligent.model.device.vo.DeviceStorageInfoVO;
import com.coolcollege.intelligent.model.device.vo.DeviceTalkbackVO;
import com.coolcollege.intelligent.model.enums.JfyStorageStatusEnum;
import com.coolcollege.intelligent.model.enums.JfyVideoProtocolTypeEnum;
import com.coolcollege.intelligent.model.fileUpload.FileUploadParam;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.video.TaskFileDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.fileUpload.FileUploadService;
import com.coolcollege.intelligent.service.fileUpload.OssClientService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.openapi.VideoOpenService;
import com.coolcollege.intelligent.service.video.status.ThirdPartyDeviceStatusCode;
import com.coolcollege.intelligent.util.JfySignatureUtil;
import com.coolcollege.intelligent.util.JfyTimeMillisUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.VideoProtocolTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 杰峰云
 * </p>
 *
 * @author wangff
 * @since 2025/5/6
 */
@Service
@Slf4j
public class JfyOpenServiceImpl implements VideoOpenService {

    @Resource
    protected RedisUtilPool redisUtilPool;
    @Resource
    protected EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private DeviceMapper deviceMapper;

    @Value("${jfy.url}")
    private String jfyUrl;
    @Value("${jfy.accessKey}")
    private String accessKey;
    @Value("${jfy.secretKey}")
    private String secretKey;
    @Value("${jfy.uuid}")
    private String uuid;
    @Value("${api.domain.url}")
    private String apiDomainUrl;

    @Resource
    private FileUploadService fileUploadService;

    @Override
    public YunTypeEnum getYunTypeNum() {
        return YunTypeEnum.JFY;
    }

    private VideoSettingDTO getVideoSetting(String enterpriseId, AccountTypeEnum accountType) {
        String appKey, appSecret, uuid = null;
        if (AccountTypeEnum.PLATFORM.equals(accountType)) {
            appKey = accessKey;
            appSecret = secretKey;
            uuid = this.uuid;
        } else {
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(enterpriseId, getYunTypeNum().getCode(), accountType.getCode());
            appKey = videoSetting.getAccessKeyId();
            appSecret = videoSetting.getSecret();
            JSONObject extendInfo = JSONObject.parseObject(videoSetting.getExtendInfo());
            if (Objects.nonNull(extendInfo)) {
                uuid = extendInfo.getString("uuid");
            }
            DataSourceHelper.changeToSpecificDataSource(dbName);
        }
        return new VideoSettingDTO(appKey, appSecret, uuid);
    }

    @Override
    public String getAccessToken(String enterpriseId, AccountTypeEnum accountType) {
        return "";
    }

    /**
     * 获取设备token
     */
    @Override
    public String getAccessToken(String enterpriseId, AccountTypeEnum accountType, String deviceId) {
        log.info("enterpriseId:{}, accountType:{}, deviceId:{}", enterpriseId, accountType.getCode(), deviceId);
        String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN_SN, enterpriseId, accountType.getCode(), deviceId);
        String accessToken = redisUtilPool.getString(cacheKey);
        if (StringUtils.isNotBlank(accessToken)) {
            return accessToken;
        }
        VideoSettingDTO setting = getVideoSetting(enterpriseId, accountType);
        String url = jfyUrl + "/gwp/v3/rtc/device/token";
        HttpHeaders headers = buildHeaders(setting.getUuid(), setting.getAppKey(), setting.getAppSecret());
        Map<String, Object> body = new HashMap<>();
        body.put("sns", Collections.singletonList(deviceId));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        JSONObject response = restTemplate.postForObject(url, httpEntity, JSONObject.class);
        if (Objects.nonNull(response) && response.getInteger("code").equals(2000)) {
            JSONObject data = response.getJSONArray("data").getJSONObject(0);
            String token = data.getString("token");
            redisUtilPool.setString(cacheKey, token, 72000);
            return token;
        } else {
            log.error("getAccessToken 杰峰云接口调用失败, deviceId:{}, url:{}, httpEntity:{}, response:{}", deviceId, url, JSONObject.toJSONString(httpEntity), JSONObject.toJSONString(response));
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
    }

    @Override
    public OpenDeviceDTO getDeviceDetail(String enterpriseId, String deviceId, AccountTypeEnum accountType) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        DeviceUserDTO deviceUserDTO = getDeviceUserInfo(device);
        return getDeviceDetail(enterpriseId, deviceId, accountType, deviceUserDTO.getUsername(), deviceUserDTO.getPassword());
    }

    @Override
    public OpenDeviceDTO getDeviceDetail(String enterpriseId, String deviceId, AccountTypeEnum accountType, String username, String password) {
        String url = jfyUrl + "/gwp/v3/rtc/device/status";
        VideoSettingDTO setting = getVideoSetting(enterpriseId, accountType);

        HttpHeaders headers = buildHeaders(setting.getUuid(), setting.getAppKey(), setting.getAppSecret());
        Map<String, Object> body = new HashMap<>();
        String accessToken = getAccessToken(enterpriseId, accountType, deviceId);
        body.put("deviceTokenList", Collections.singletonList(accessToken));

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        JSONObject response = restTemplate.postForObject(url, httpEntity, JSONObject.class);
        if (Objects.nonNull(response) && response.getInteger("code").equals(2000)) {
            JSONArray data = response.getJSONArray("data");
            JSONObject device = data.getJSONObject(0);
            OpenDeviceDTO deviceDTO = new OpenDeviceDTO();
            deviceDTO.setDeviceId(deviceId);
            deviceDTO.setDeviceStatus("online".equals(device.getString("status")) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
            deviceDTO.setSource(getYunTypeNum().getCode());
            JSONArray channelList = device.getJSONArray("channel");
            List<OpenChannelDTO> channelDTOList = null;
            // 登录设备
            DeviceStatusDTO deviceStatusDTO = deviceLogin(enterpriseId, setting, deviceId, username, password, accountType);
            // 不是IPC设备的话，存在通道信息
            if (Boolean.TRUE.equals(deviceStatusDTO.getLogin()) && !DeviceCatalog.IPC.getCode().equals(deviceStatusDTO.getDeviceType())) {
                channelDTOList = CollStreamUtil.toList(channelList, v -> {
                    JSONObject channel = new JSONObject((Map) v);
                    String channelNo = channel.getString("channel");
                    OpenChannelDTO channelDTO = new OpenChannelDTO();
                    channelDTO.setParentDeviceId(deviceId);
                    channelDTO.setDeviceId(deviceId + "_" + channelNo);
                    channelDTO.setChannelNo(channelNo);
                    channelDTO.setChannelName(String.format("D%02d", Integer.parseInt(channelNo) + 1));
                    channelDTO.setStatus(channel.getInteger("main").equals(0) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                    channelDTO.setSource(getYunTypeNum().getCode());
                    if (DeviceStatusEnum.ONLINE.getCode().equals(channelDTO.getStatus())) {
                        DeviceCapacityDTO ability = getDeviceAbility(enterpriseId, deviceId, channelNo, accessToken, setting, username, password, accountType);
                        channelDTO.setHasPtz(YesOrNoEnum.YES.getCode().equals(ability.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(ability.getPtzLeftRight()));
                        channelDTO.setSupportCapture(ability.getSupportCapture());
                        deviceDTO.setDeviceCapacity(ability);
                    }
                    return channelDTO;
                });
            }
            deviceDTO.setChannelList(channelDTOList);
            // 如果不是nvr设备，获取设备能力集
            if (CollectionUtils.isEmpty(channelDTOList) && DeviceStatusEnum.ONLINE.getCode().equals(deviceDTO.getDeviceStatus())) {
                DeviceCapacityDTO ability = getDeviceAbility(enterpriseId, deviceId, null, accessToken, setting, username, password, accountType);
                deviceDTO.setHasPtz(YesOrNoEnum.YES.getCode().equals(ability.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(ability.getPtzLeftRight()));
                deviceDTO.setSupportCapture(ability.getSupportCapture());
                deviceDTO.setDeviceCapacity(ability);
            }
            return deviceDTO;
        } else if (Objects.nonNull(response) && Integer.valueOf(4101).equals(response.getInteger("code"))) {
            log.info("设备:{}不在线", deviceId);
            OpenDeviceDTO openDeviceDTO = new OpenDeviceDTO();
            openDeviceDTO.setDeviceId(deviceId);
            openDeviceDTO.setDeviceStatus(DeviceStatusEnum.OFFLINE.getCode());
            return openDeviceDTO;
        } else {
            log.error("getDeviceDetail 杰峰云接口调用失败, deviceId:{}, url:{}, httpEntity:{}, response:{}", deviceId, url, JSONObject.toJSONString(httpEntity), JSONObject.toJSONString(response));
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
    }

    /**
     * 获取设备能力集
     */
    private DeviceCapacityDTO getDeviceAbility(String enterpriseId, String deviceId, String channelNo, String accessToken, VideoSettingDTO setting, String username, String password, AccountTypeEnum accountType) {
        JSONObject deviceAbility = getDeviceAbilityByJfy(enterpriseId, deviceId, channelNo, accessToken, setting, username, password, accountType);
        return DeviceCapacityDTO.convertJFYDeviceCapacity(deviceAbility);
    }

    /**
     * 设备系统能力
     */
    private JSONObject getDeviceAbilityByJfy(String enterpriseId, String deviceId, String channelNo, String accessToken, VideoSettingDTO setting, String username, String password, AccountTypeEnum accountType) {
        keepLogin(enterpriseId, setting, deviceId, username, password, accountType);

        boolean isNvr = StringUtils.isNotBlank(channelNo);
        String url = jfyUrl + (isNvr ? "/gwp/v3/rtc/device/getchannelability/" : "/gwp/v3/rtc/device/getability/") + accessToken;
        HttpHeaders headers = buildHeaders(setting.getUuid(), setting.getAppKey(), setting.getAppSecret());
        Map<String, Object> body = new HashMap<>();
        body.put("Name", "SystemFunction");
        if (isNvr) {
            body.put("Channel", channelNo);
        }
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        JSONObject response = restTemplate.postForObject(url, httpEntity, JSONObject.class);
        if (Objects.nonNull(response) && response.getInteger("code").equals(2000)) {
            JSONObject data = response.getJSONObject("data");
            if (Objects.nonNull(data) && data.getInteger("Ret").equals(100)) {
                return data.getJSONObject("SystemFunction");
            }
        }
        log.info("getDeviceAbilityByJfy 杰峰云接口调用失败, deviceId:{}, url:{}, httpEntity:{}, response:{}", deviceId, url, JSONObject.toJSONString(httpEntity), JSONObject.toJSONString(response));
        return null;
    }

    @Override
    public PageInfo<OpenDevicePageDTO> getDeviceList(String enterpriseId, AccountTypeEnum accountType, Integer pageNum, Integer pageSize) {
        String url = jfyUrl + "/gwp/v3/rtc/device/list";
        VideoSettingDTO setting = getVideoSetting(enterpriseId, accountType);
        HttpHeaders headers = buildHeaders(setting.getUuid(), setting.getAppKey(), setting.getAppSecret());
        Map<String, Object> body = new HashMap<>();
        body.put("page", pageNum);
        body.put("limit", pageSize);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        JSONObject response = restTemplate.postForObject(url, httpEntity, JSONObject.class);
        if (Objects.nonNull(response) && response.getInteger("code").equals(2000)) {
            JSONObject data = response.getJSONObject("data");
            JSONArray deviceList = data.getJSONArray("deviceList");
            PageInfo<OpenDevicePageDTO> pageInfo = new PageInfo<>();
            if (CollectionUtils.isNotEmpty(deviceList)) {
                List<OpenDevicePageDTO> list = CollStreamUtil.toList(deviceList, v -> {
                    JSONObject device = new JSONObject((Map) v);
                    OpenDevicePageDTO dto = new OpenDevicePageDTO();
                    dto.setDeviceId(device.getString("sn"));
                    dto.setDeviceName(device.getString("nickname"));
                    dto.setSource(getYunTypeNum().getCode());
                    dto.setUsername(device.getString("username"));
                    dto.setPassword(device.getString("password"));
                    dto.setHasPtz(true);
                    return dto;
                });
                pageInfo.setList(list);
            }
            return pageInfo;
        } else {
            log.error("getDeviceList 杰峰云接口调用失败, url:{}, httpEntity:{}, response:{}", url, JSONObject.toJSONString(httpEntity), JSONObject.toJSONString(response));
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
    }

    @Override
    public LiveVideoVO getLiveUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String channelNo = param.getChannelNo();
        Integer quality = param.getQuality();
        VideoProtocolTypeEnum protocolTypeEnum = param.getProtocol();
        String accessToken = getAccessToken(enterpriseId, accountType, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/livestream/" + accessToken;

        Map<String, Object> body = new HashMap<>();
        body.put("channel", channelNo);
        body.put("stream", Constants.INDEX_ONE.equals(quality) ? "0" : "1");
        body.put("protocol", JfyVideoProtocolTypeEnum.getByVideoProtocolTypeEnum(protocolTypeEnum).getCode());
        // 填充设备用户名和密码
        DeviceUserDTO deviceUserDTO = getDeviceUserInfo(device);
        body.put("username", deviceUserDTO.getUsername());
        body.put("password", deviceUserDTO.getPassword());
        // 过期时间设置为当前时间往后720天
        body.put("expireTime", String.valueOf(System.currentTimeMillis() + 720 * 24 * 3600 * 1000L));
        JSONObject data = sendPost(enterpriseId, device, url, body, false, false);
        String liveUrl = data.getString("url");
        LiveVideoVO result = new LiveVideoVO();
        result.setUrl(liveUrl);
        return result;
    }

    @Override
    public LiveVideoVO getPastVideoUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        return getPastVideo(enterpriseId, device, param, 0);
    }

    /**
     * 获取回放地址或回放下载地址
     * @param enterpriseId 企业id
     * @param device 设备
     * @param param 通道号
     * @param download 0回放，1下载
     * @return com.coolcollege.intelligent.model.video.vo.LiveVideoVO
     */
    private LiveVideoVO getPastVideo(String enterpriseId, DeviceDO device, VideoDTO param, int download) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String channelNo = param.getChannelNo(), startTime = param.getStartTime(), endTime = param.getEndTime();
        Integer quality = param.getQuality();
        VideoProtocolTypeEnum protocolTypeEnum = param.getProtocol();

        String accessToken = getAccessToken(enterpriseId, accountType, device.getDeviceId());

        // 检查本地录像回放文件，不存在则无回放
        boolean existsPastVideo = existsPastVideo(enterpriseId, device, accessToken, channelNo, quality, startTime, endTime);
        if (!existsPastVideo) {
            throw new ServiceException(ErrorCodeEnum.NOT_EXISTS_LOCAL_PAST_VIDEO);
        }

        String url = jfyUrl + "/gwp/v3/rtc/device/playbackUrl/" + accessToken;

        Map<String, Object> body = new HashMap<>();
        body.put("channel", Integer.parseInt(channelNo));
        body.put("streamType", Constants.INDEX_ONE.equals(quality) ? 0 : 1);
        body.put("protocol", JfyVideoProtocolTypeEnum.getByVideoProtocolTypeEnum(protocolTypeEnum).getCode());

        body.put("startTime", startTime);
        body.put("endTime", endTime);
        body.put("download", download);

        DeviceUserDTO deviceUserDTO = getDeviceUserInfo(device);
        body.put("username", deviceUserDTO.getUsername());
        body.put("password", deviceUserDTO.getPassword());
        body.put("playPrioritize", 8);

        JSONObject data = sendPost(enterpriseId, device, url, body, false, false);
        String liveUrl = data.getString("url");
        LiveVideoVO result = new LiveVideoVO();
        result.setUrl(liveUrl);
        return result;
    }

    /**
     * 是否存在本地录像
     */
    private boolean existsPastVideo(String enterprise, DeviceDO device, String accessToken, String channelNo, Integer quality, String startTime, String endTime) {
        String url = jfyUrl + "/gwp/v3/rtc/device/opdev/" + accessToken;

        Map<String, Object> body = new HashMap<>();
        body.put("Name", "OPFileQuery");
        Map<String, Object> opFileQuery = new HashMap<>();
        opFileQuery.put("BeginTime", startTime);
        opFileQuery.put("EndTime", endTime);
        opFileQuery.put("Channel", Integer.parseInt(channelNo));
        opFileQuery.put("DriverTypeMask", "0x0000FFFF");
        opFileQuery.put("Event", "*");
        opFileQuery.put("StreamType", Constants.INDEX_ONE.equals(quality) ? "0" : "1");
        opFileQuery.put("Type", "h264");
        body.put("OPFileQuery", opFileQuery);
        JSONObject data = sendPost(enterprise, device, url, body, true, true);
        return Constants.ONE_HUNDRED.equals(data.getInteger("Ret"));
    }

    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {
        log.info("取消设备授权, deviceId:{}", device.getDeviceId());
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        VideoSettingDTO setting = getVideoSetting(enterpriseId, accountType);

        String accessToken = getAccessToken(enterpriseId, accountType, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/unbind/" + accessToken;
        HttpHeaders headers = buildHeaders(setting.getUuid(), setting.getAppKey(), setting.getAppSecret());
        Map<String, Object> body = new HashMap<>();
        body.put("sn", device.getDeviceId());

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        JSONObject response = restTemplate.postForObject(url, httpEntity, JSONObject.class);
        if (!(Objects.nonNull(response) && response.getInteger("code").equals(2000))) {
            log.error("cancelAuth 杰峰云接口调用失败, deviceId:{}, url:{}, httpEntity:{}, response:{}", device.getDeviceId(), url, JSONObject.toJSONString(httpEntity), JSONObject.toJSONString(response));
        }
    }

    @Override
    public Boolean ptzStart(String enterpriseId, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration) {
        // command: 0上 1下 2左 3右 8拉近 9拉远 其他停止
        String jfyCommand = command == 0 ? "DirectionUp" : command == 1 ? "DirectionDown" : command == 2 ? "DirectionLeft" : command == 3 ? "DirectionRight" : "";
        return ptzControl(enterpriseId, device, channelNo, jfyCommand, StringUtils.isNotBlank(jfyCommand) ? 0 : -1, speed);
    }

    @Override
    public Boolean ptzStop(String enterpriseId, DeviceDO device, String channelNo) {
        return ptzControl(enterpriseId, device, channelNo, "DirectionUp", -1, 0);
    }

    /**
     * 云台控制
     * @param enterpriseId 企业id
     * @param device 设备
     * @param channelNo 通道号
     * @param command DirectionUp上/DirectionDown下/DirectionLeft左/DirectionRight右/DirectionLeftUp左上/DirectionLeftDown左下/DirectionRightUp右上/DirectionRightDown右下
     * @param preset 0开始运动，-1停止运动
     * @return java.lang.Boolean
     */
    private Boolean ptzControl(String enterpriseId, DeviceDO device, String channelNo, String command, int preset, int speed) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/opdev/" + accessToken;

        Map<String, Object> body = new HashMap<>();
        body.put("Name", "OPPTZControl");
        Map<String, Object> control = new HashMap<>();
        control.put("Command", command);
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("Preset", preset);
        parameter.put("Channel", channelNo);
        parameter.put("Step", speed);
        control.put("Parameter", parameter);
        body.put("OPPTZControl", control);
        JSONObject data = sendPost(enterpriseId, device, url, body, true, true);
        return Constants.ONE_HUNDRED.equals(data.getInteger("Ret"));
    }

    /**
     * 设备登录
     */
    private DeviceStatusDTO deviceLogin(String enterpriseId, VideoSettingDTO setting, String deviceId, String username, String password, AccountTypeEnum accountType) {
        return deviceLogin(enterpriseId, setting, deviceId, username, password, accountType, 0);
    }

    /**
     * 设备登录
     */
    private DeviceStatusDTO deviceLogin(String enterpriseId, VideoSettingDTO setting, String deviceId, String username, String password, AccountTypeEnum accountType, int retryTimes) {
        String accessToken = getAccessToken(enterpriseId, accountType, deviceId);
        String url = jfyUrl + "/gwp/v3/rtc/device/login/" + accessToken;
        if (Objects.isNull(setting)) {
            setting = getVideoSetting(enterpriseId, accountType);
        }
        HttpHeaders headers = buildHeaders(setting.getUuid(), setting.getAppKey(), setting.getAppSecret());

        Map<String, Object> body = new HashMap<>();
        // 填充设备用户名和密码
        body.put("username", username);
        body.put("password", password);

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        JSONObject response = restTemplate.postForObject(url, httpEntity, JSONObject.class);
        log.info("deviceLogin, deviceId:{}, httpEntity:{}, response:{}", deviceId, JSONObject.toJSONString(httpEntity), JSONObject.toJSONString(response));
        if (Objects.nonNull(response) && response.getInteger("code").equals(2000)) {
            JSONObject data = response.getJSONObject("data");
            Integer ret = data.getInteger("Ret");
            String deviceType = data.getString("DeviceType");
            if (Constants.ONE_HUNDRED.equals(ret)) {
                return new DeviceStatusDTO(true, deviceType);
            } else if (Integer.valueOf(137).equals(ret)) {
                log.info("设备登录令牌失效");
                if (retryTimes < 3) {
                    log.info("设备登录重试, times:{}", retryTimes + 1);
                    // 删除设备token缓存后重试
                    deleteAccessToken(enterpriseId, deviceId, accountType);
                    return deviceLogin(enterpriseId, setting, deviceId, username, password, accountType, retryTimes + 1);
                }
            }
        } else if (Objects.nonNull(response) && (Integer.valueOf(4101).equals(response.getInteger("code")) || Integer.valueOf(4118).equals(response.getInteger("code")))) {
            // 设备离线时也会返回4118(连接超时)
            log.info("设备离线或连接超时");
        } else {
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        return new DeviceStatusDTO(false, null);
    }

    /**
     * 删除accessToken缓存
     */
    @Override
    public void deleteAccessToken(String enterpriseId, String deviceId, AccountTypeEnum accountType) {
        String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN_SN, enterpriseId, accountType.getCode(), deviceId);
        redisUtilPool.delKey(cacheKey);
    }

    /**
     * 设备保活
     */
    private Boolean keepAlive(String enterpriseId, VideoSettingDTO setting, String deviceId, String username, String password, AccountTypeEnum accountType) {
        String accessToken = getAccessToken(enterpriseId, accountType, deviceId);
        String url = jfyUrl + "/gwp/v3/rtc/device/keepalive/" + accessToken;
        if (Objects.isNull(setting)) {
            setting = getVideoSetting(enterpriseId, accountType);
        }
        HttpHeaders headers = buildHeaders(setting.getUuid(), setting.getAppKey(), setting.getAppSecret());

        Map<String, Object> body = new HashMap<>();
        body.put("Name", "KeepAlive");

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        JSONObject response = restTemplate.postForObject(url, httpEntity, JSONObject.class);
        if (Objects.nonNull(response)) {
            Integer code = response.getInteger("code");
            if (code.equals(2000)) {
                JSONObject data = response.getJSONObject("data");
                Integer ret = data.getInteger("Ret");
                if (Constants.ONE_HUNDRED.equals(ret)) {
                    return true;
                }
            } else if (code.equals(4100)) {
                return deviceLogin(enterpriseId, setting, deviceId, username, password, accountType).getLogin();
            }
            log.error("杰峰云设备保活失败, response:{}", response);
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        } else {
            log.error("keepAlive 杰峰云接口调用失败, deviceId:{}, url:{}, httpEntity:{}, response:{}", deviceId, url, JSONObject.toJSONString(httpEntity), JSONObject.toJSONString(response));
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
    }

    /**
     * 保持设备登录
     */
    private void keepLogin(String enterpriseId, VideoSettingDTO setting, String deviceId, String username, String password, AccountTypeEnum accountType) {
        String cacheKey = MessageFormat.format(RedisConstant.DEVICE_KEEP_LOGIN, enterpriseId, accountType.getCode(), deviceId);
        String accessToken = redisUtilPool.getString(cacheKey);
        Boolean status;
        if (StringUtils.isNotBlank(accessToken)) {
            // 设备处于登录状态，调用保活接口
            status = keepAlive(enterpriseId, setting, deviceId, username, password, accountType);
        } else {
            // 设备登录
            status = deviceLogin(enterpriseId, setting, deviceId, username, password, accountType).getLogin();
        }
        if (Boolean.FALSE.equals(status)) {
            log.error("杰峰云设备登录失败");
            throw new ServiceException(ErrorCodeEnum.JFY_LOGIN_ERROR);
        }
    }

    private void keepLogin(String enterpriseId, VideoSettingDTO setting, DeviceDO device) {
        DeviceUserDTO deviceUserDTO = getDeviceUserInfo(device);
        keepLogin(enterpriseId, setting, device.getDeviceId(), deviceUserDTO.getUsername(), deviceUserDTO.getPassword(), AccountTypeEnum.getAccountType(device.getAccountType()));
    }

    /**
     * 获取设备用户信息
     */
    private DeviceUserDTO getDeviceUserInfo(DeviceDO device) {
        JSONObject extendInfo = JSONObject.parseObject(device.getExtendInfo());
        if (Objects.isNull(extendInfo) || !extendInfo.containsKey("username")) {
            throw new ServiceException(ErrorCodeEnum.JFY_USERNAME_NOT_EXIST);
        }
        String username = extendInfo.getString("username");
        String password = extendInfo.getString("password");
        return new DeviceUserDTO(username, password);
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
    public String capture(String enterpriseId, DeviceDO device, String channelNo, String quality) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/capture/" + accessToken;
        Map<String, Object> body = new HashMap<>();
        body.put("Name", "OPSNAP");
        Map<String, Object> opsnap = new HashMap<>();
        if(StringUtils.isNotBlank(channelNo)){
            opsnap.put("Channel", Integer.parseInt(channelNo));
        }
        opsnap.put("PicType", 0);
        body.put("OPSNAP", opsnap);

        JSONObject data = sendPost(enterpriseId, device, url, body, true, false);
        return data.getString("image");
    }

    @Override
    public String captureByTime(String enterpriseId, DeviceDO device, String channelNo, List<String> captureTimes) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType, device.getDeviceId());
        JSONObject extendInfo = JSONObject.parseObject(device.getExtendInfo());
        String username = extendInfo.getString(DeviceDO.ExtendInfoField.USERNAME);
        String password = extendInfo.getString(DeviceDO.ExtendInfoField.PASSWORD);
        String url = jfyUrl + "/gwp/v3/rtc/device/captureFrame/" + accessToken;
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("recType", "local");
        body.put("timePoint", captureTimes);
        body.put("pictureUploadUrl", apiDomainUrl + "/v3/device/callback/" + enterpriseId + "/jfy/capturePicture/"+ device.getDeviceId());
        body.put("streamType", 0);
        if(StringUtils.isNotBlank(channelNo)){
            body.put("channel", Integer.parseInt(channelNo));
        }
        body.put("frameModel", 0);

        JSONObject data = sendPost(enterpriseId, device, url, body, true, true);
        return data.getString("taskId");
    }

    @Override
    public List<TaskFileDTO> getTaskFile(String enterpriseId, DeviceDO device, String taskId) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType, device.getDeviceId());
        JSONObject extendInfo = JSONObject.parseObject(device.getExtendInfo());
        String username = extendInfo.getString(DeviceDO.ExtendInfoField.USERNAME);
        String password = extendInfo.getString(DeviceDO.ExtendInfoField.PASSWORD);
        String url = jfyUrl + "/gwp/v3/rtc/device/getFrameUrl/" + accessToken;
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("taskId", taskId);
        JSONObject data = sendPost(enterpriseId, device, url, body, true, true);
        log.info("抓拍图片:{}", JSONObject.toJSONString(data));
        String status = data.getString("status");
        if ("complete".equals(status)) {
            List<TaskFileDTO> result = JSONObject.parseArray(JSONObject.toJSONString(data.getJSONArray("data")), TaskFileDTO.class);;
            if(CollectionUtils.isNotEmpty(result)){
                //上传到自己oss
                for (TaskFileDTO taskFileDTO : result) {
                    FileUploadParam fileUploadParam = fileUploadService.uploadBaseImage(taskFileDTO.getUrl(), enterpriseId, null);
                    taskFileDTO.setUrl(fileUploadParam.getFileUrl());
                }
            }
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    public String videoTransCode(String enterpriseId, DeviceDO device, VideoDTO param) {
        String startTime = DateUtils.dateTimeFormat(param.getStartTime());
        String endTime = DateUtils.dateTimeFormat(param.getEndTime());
        param.setStartTime(startTime);
        param.setEndTime(endTime);
        param.setQuality(1);
        param.setProtocol(null);
        LiveVideoVO pastVideo = getPastVideo(enterpriseId, device, param, 1);
        return pastVideo.getUrl();
    }

    @Override
    public VideoFileDTO getVideoFile(String enterpriseId, DeviceDO device, String fileId) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType, device.getDeviceId());
        JSONObject extendInfo = JSONObject.parseObject(device.getExtendInfo());
        String username = extendInfo.getString(DeviceDO.ExtendInfoField.USERNAME);
        String password = extendInfo.getString(DeviceDO.ExtendInfoField.PASSWORD);
        String url = jfyUrl + "/gwp/v3/rtc/device/getFrameUrl/" + accessToken;
        Map<String, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("taskId", fileId);
        JSONObject data = sendPost(enterpriseId, device, url, body, true, true);
        log.info("获取视频文件信息:{}", JSONObject.toJSONString(data));
        String status = data.getString("status");
        VideoFileDTO videoFileDTO = new VideoFileDTO();
        videoFileDTO.setFileId(fileId);
        videoFileDTO.setStatus(1);
        if ("complete".equals(status)) {
            videoFileDTO.setStatus(0);
        }
        return videoFileDTO;
    }

    @Override
    public List<String> getVideoDownloadUrl(String enterpriseId, DeviceDO device, String fileId) {
        return Collections.emptyList();
    }

    /**
     * 创建请求头
     */
    private HttpHeaders buildHeaders(String uuid, String appKey, String appSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("uuid", uuid);
        headers.add("appKey", appKey);
        String timeMillis = JfyTimeMillisUtil.getTimMillis();
        headers.add("timeMillis", timeMillis);
        headers.add("signature", JfySignatureUtil.getEncryptStr(uuid, appKey, appSecret, timeMillis, 2));
        return headers;
    }

    @Override
    public Boolean pictureFlip(String enterpriseId, DeviceDO device, DeviceConfigDTO configDTO) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/setconfig/" + accessToken;

        Map<String, Object> body = new HashMap<>();
        body.put("Name", "Camera.Param");
        if (Boolean.TRUE.equals(device.getHasChildDevice()) && StringUtils.isNotBlank(configDTO.getChannelNo())) {
            body.put("Channel", configDTO.getChannelNo());
        }
        List<Map> config = getConfig(enterpriseId, device, configDTO).toJavaList(Map.class);
        for (Map map : config) {
            if (Boolean.TRUE.equals(configDTO.getFlip()) && map.containsKey("PictureFlip")) {
                String pictureFlip = MapUtils.getString(map, "PictureFlip");
                map.put("PictureFlip", pictureFlip.equals("0x00000000") ? "0x00000001" : "0x00000000");
            }
            if (Boolean.TRUE.equals(configDTO.getMirror()) && map.containsKey("PictureMirror")) {
                String pictureMirror = MapUtils.getString(map, "PictureMirror");
                map.put("PictureMirror", pictureMirror.equals("0x00000000") ? "0x00000001" : "0x00000000");
            }
        }
        body.put("Camera.Param", config);

        JSONObject data = sendPost(enterpriseId, device, url, body, true, true);
        return Constants.ONE_HUNDRED.equals(data.getInteger("Ret"));
    }

    /**
     * 获取设备配置
     */
    public JSONArray getConfig(String enterpriseId, DeviceDO device, DeviceConfigDTO configDTO) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/getconfig/" + accessToken;

        Map<String, Object> body = new HashMap<>();
        body.put("Name", "Camera.Param");
        if (Boolean.TRUE.equals(device.getHasChildDevice()) && StringUtils.isNotBlank(configDTO.getChannelNo())) {
            body.put("Channel", configDTO.getChannelNo());
        }
        JSONObject data = sendPost(enterpriseId, device, url, body, true, false);
        return data.getJSONArray("Camera.Param");
    }

    @Override
    public DeviceTalkbackVO deviceTalkback(String enterpriseId, DeviceDO device, DeviceTalkbackDTO talkbackDTO) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());

        JSONObject extendInfo = JSONObject.parseObject(device.getExtendInfo());
        String username = extendInfo.getString(DeviceDO.ExtendInfoField.USERNAME);
        String password = extendInfo.getString(DeviceDO.ExtendInfoField.PASSWORD);

        String accessToken = getAccessToken(enterpriseId, accountTypeEnum, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/talkbackUrl/" + accessToken;
        Map<String, Object> body = new HashMap<>();
        body.put("protocol", talkbackDTO.getProtocolType().getCode());
        body.put("channel", talkbackDTO.getChannelNo());
        body.put("username", username);
        body.put("password", password);
        JSONObject data = sendPost(enterpriseId, device, url, body, false, false);
        return new DeviceTalkbackVO(data.getString("url"));
    }

    @Override
    public Boolean deviceReboot(String enterpriseId, DeviceDO device, String channelNo) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountTypeEnum, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/opdev/" + accessToken;
        Map<String, Object> body = new HashMap<>();
        body.put("Name", "OPMachine");
        Map<String, Object> opMachine = new HashMap<>();
        opMachine.put("Action", "Reboot");
        body.put("OPMachine", opMachine);
        sendPost(enterpriseId, device, url, body, true, false);
        return true;
    }

    @Override
    public List<DeviceStorageInfoVO> deviceStorageInfo(String enterpriseId, DeviceDO device) {
        JSONObject data = getDeviceStorageInfo(enterpriseId, device);
        return data.getJSONArray("StorageInfo").toJavaList(JSONObject.class)
                .stream()
                .filter(v -> v.getInteger("PartNumber") > 0)
                .flatMap(v -> v.getJSONArray("Partition").toJavaList(JSONObject.class).stream())
                .map(v -> DeviceStorageInfoVO.builder()
                        .totalSize(toDic(v.getString("TotalSpace")))
                        .availableSize(toDic(v.getString("RemainSpace")))
                        .type(v.getInteger("DirverType"))
                        .status(JfyStorageStatusEnum.getMsgByCode(v.getInteger("Status")))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取设备存储信息data
     */
    private JSONObject getDeviceStorageInfo(String enterpriseId, DeviceDO device) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountTypeEnum, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/getinfo/" + accessToken;
        Map<String, Object> body = Collections.singletonMap("Name", "StorageInfo");
        return sendPost(enterpriseId, device, url, body, true, false);
    }

    private Long toDic(String hex) {
        if (StringUtils.isBlank(hex)) return null;
        if (hex.startsWith("0x") || hex.startsWith("0X")) {
            return Long.valueOf(hex.substring(2), 16);
        } else {
            return Long.valueOf(hex);
        }
    }

    @Override
    public Boolean deviceStorageFormatting(String enterpriseId, DeviceDO device, String channelNo) {
        JSONObject data = getDeviceStorageInfo(enterpriseId, device);
        List<JSONObject> storageInfos = data.getJSONArray("StorageInfo").toJavaList(JSONObject.class);
        for (int i = 0; i < storageInfos.size(); i++) {
            JSONObject storageInfo = storageInfos.get(i);
            Integer partNumber = storageInfo.getInteger("PartNumber");
            if (Objects.nonNull(partNumber) && partNumber > 0) {
                List<JSONObject> partitions = storageInfo.getJSONArray("Partition").toJavaList(JSONObject.class);
                Map<Integer, JSONObject> partitionMap = CollStreamUtil.toMap(partitions, v -> v.getInteger("DirverType"), v -> v);
                // 获取录像分区
                JSONObject recordPartition = partitionMap.get(0);
                Integer logicSerialNo = recordPartition.getInteger("LogicSerialNo");
                formattingByPart(enterpriseId, device, i, logicSerialNo);
            }
        }
        return true;
    }

    private void formattingByPart(String enterpriseId, DeviceDO device, Integer partNo, Integer logicSerialNo) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountTypeEnum, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/opdev/" + accessToken;
        Map<String, Object> body = new HashMap<>();
        body.put("Name", "OPStorageManager");
        Map<String, Object> opStorageManager = new HashMap<>();
        opStorageManager.put("Action", "Clear");
        opStorageManager.put("PartNo", partNo);
        opStorageManager.put("SerialNo", logicSerialNo);
        body.put("OPStorageManager", opStorageManager);
        sendPost(enterpriseId, device, url, body, true, false);
    }

    @Override
    public DeviceSoftHardwareInfoVO deviceSoftHardwareInfo(String enterpriseId, DeviceDO device) {
        JSONObject netConfig = getNetConfig(enterpriseId, device).getJSONObject("NetWork.NetCommon");
        JSONObject abilityConfig = getAbilityConfig(enterpriseId, device);
        return DeviceSoftHardwareInfoVO.builder()
                .deviceModel(abilityConfig.getString("model"))
                .hardwareVersion(abilityConfig.getString("hw"))
                .firmwareVersion(abilityConfig.getString("sw"))
                .ip(Objects.nonNull(netConfig) ? convertIp(netConfig.getString("HostIP")) : null)
                .mac(Objects.nonNull(netConfig) ? netConfig.getString("MAC") : null)
                .build();
    }

    private String convertIp(String ip) {
        if (StringUtils.isBlank(ip)) return null;
        if (ip.startsWith("0x") || ip.startsWith("0X")) {
            long hexValue = Long.parseLong(ip.substring(2), 16);
            // 提取每个字节并转换为十进制
            int firstByte = (int) (hexValue >> 24) & 0xFF;
            int secondByte = (int) (hexValue >> 16) & 0xFF;
            int thirdByte = (int) (hexValue >> 8) & 0xFF;
            int fourthByte = (int) hexValue & 0xFF;
            // 拼接成IP地址格式
            return String.format("%d.%d.%d.%d", firstByte, secondByte, thirdByte, fourthByte);
        } else {
            return ip;
        }
    }

    /**
     * 获取网络基础配置
     */
    private JSONObject getNetConfig(String enterpriseId, DeviceDO device) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountTypeEnum, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/getconfig/" + accessToken;
        Map<String, Object> body = new HashMap<>();
        body.put("Name", "NetWork.NetCommon");
        return sendPost(enterpriseId, device, url, body, true, false);
    }

    /**
     * 获取设备增值服务能力集
     */
    private JSONObject getAbilityConfig(String enterpriseId, DeviceDO device) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountTypeEnum, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/getabilityconfig/" + accessToken;
        Map<String, Object> body = new HashMap<>();
        body.put("sn", device.getDeviceId());
        // 使接口不返回caps数据，避免返回数据过大
        body.put("caps", Collections.singletonList("1"));
        return sendPost(enterpriseId, device, url, body, false, false);
    }

    /**
     * 杰峰云请求
     * @param enterpriseId 企业id
     * @param device 设备
     * @param url 请求地址
     * @param body 请求体
     * @param needKeepLogin 是否需要保持登录
     * @param noVerifyRet 不校验设备状态码
     * @return com.alibaba.fastjson.JSONObject
     */
    private JSONObject sendPost(String enterpriseId, DeviceDO device, String url, Map<String, Object> body, boolean needKeepLogin, boolean noVerifyRet) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());
        VideoSettingDTO setting = getVideoSetting(enterpriseId, accountTypeEnum);
        if (needKeepLogin) {
            keepLogin(enterpriseId, setting, device);
        }
        HttpHeaders headers = buildHeaders(setting.getUuid(), setting.getAppKey(), setting.getAppSecret());
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        JSONObject response = restTemplate.postForObject(url, httpEntity, JSONObject.class);
        log.info("jfy api request, deviceId:{}, url:{}, httpEntity:{}, response:{}", device.getDeviceId(), url, JSONObject.toJSONString(httpEntity), JSONObject.toJSONString(response));
        if (Objects.nonNull(response)) {
            Integer code = response.getInteger("code");
            if (Integer.valueOf(2000).equals(code)) {
                JSONObject data = response.getJSONObject("data");
                if (Objects.nonNull(data)) {
                    if (noVerifyRet) {
                        return data;
                    } else if (Constants.ONE_HUNDRED.equals(data.getInteger("Ret")) || Constants.TWO_HUNDRED.equals(data.getInteger("ret"))) {
                        return data;
                    }
                }
            }else {
                String jfyErrorMessage = ThirdPartyDeviceStatusCode.getJfyErrorMessage(code);
                throw new ServiceException(ErrorCodeEnum.ERROR, jfyErrorMessage);
            }
        }
        throw new ServiceException(ErrorCodeEnum.API_ERROR);
    }

    @Override
    public List<DeviceVideoRecordVO> listDeviceRecordByTime(String enterpriseId, DeviceDO device, String channelNo, Long startTime, Long endTime) {
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountTypeEnum, device.getDeviceId());
        String url = jfyUrl + "/gwp/v3/rtc/device/opdev/" + accessToken;

        Map<String, Object> body = new HashMap<>();
        body.put("Name", "OPFileQuery");
        Map<String, Object> opFileQuery = new HashMap<>();
        opFileQuery.put("BeginTime", DateUtil.format(startTime, "yyyy-MM-dd HH:mm:ss"));
        opFileQuery.put("EndTime", DateUtil.format(endTime, "yyyy-MM-dd HH:mm:ss"));
        opFileQuery.put("Channel", Integer.parseInt(channelNo));
        opFileQuery.put("DriverTypeMask", "0x0000FFFF");
        opFileQuery.put("Event", "*");
        opFileQuery.put("StreamType", "0x00000000");
        opFileQuery.put("Type", "h264");
        body.put("OPFileQuery", opFileQuery);
        try {
            JSONObject data = sendPost(enterpriseId, device, url, body, true, false);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return data.getJSONArray("OPFileQuery").toJavaList(JSONObject.class)
                    .stream().map(item -> {
                        LocalDateTime fileStartTime = LocalDateTime.parse(item.getString("BeginTime"), formatter);
                        LocalDateTime fileEndTime = LocalDateTime.parse(item.getString("EndTime"), formatter);
                        return new DeviceVideoRecordVO(null, 2, DateUtil.toMillis(fileStartTime), DateUtil.toMillis(fileEndTime), device.getDeviceId(), channelNo, "TIMING");
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            log.info("录像文件列表获取失败", e);
        }
        return Collections.emptyList();
    }
}
