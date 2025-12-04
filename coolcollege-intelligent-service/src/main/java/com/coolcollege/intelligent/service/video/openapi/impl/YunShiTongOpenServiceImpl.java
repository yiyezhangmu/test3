package com.coolcollege.intelligent.service.video.openapi.impl;

import cn.hutool.http.HttpUtil;
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
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.common.http.YunShiTongHttpClient;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.device.DevicePositionMapper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.DevicePositionDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.device.vo.DeviceVideoRecordVO;
import com.coolcollege.intelligent.model.device.vo.DeviceSoftHardwareInfoVO;
import com.coolcollege.intelligent.model.device.vo.DeviceStorageInfoVO;
import com.coolcollege.intelligent.model.device.vo.DeviceTalkbackVO;
import com.coolcollege.intelligent.model.enums.YunshitongStorageStatusEnum;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.AppKeyDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.openapi.VideoOpenService;
import com.coolcollege.intelligent.util.AESUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.VideoProtocolTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.coolstore.base.utils.CommonContextUtil;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: YunShiTongOpenServiceImpl
 * @Description: 云视通 对外接口
 * @date 2022-12-13 14:01
 */
@Slf4j
@Service
public class YunShiTongOpenServiceImpl implements VideoOpenService {

    @Resource
    private EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Resource
    private YunShiTongHttpClient yunShiTongHttpClient;
    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private DevicePositionMapper devicePositionMapper;
    @Autowired
    private HttpRestTemplateService httpRestTemplateService;

    @Value("${yunshitong.appKey}")
    private String appKey;
    @Value("${yunshitong.appSecret}")
    private String appSecret;
    @Value("${yunshitong.online-accessToken-url}")
    private String onlineAccessTokenUrl;
    @Value("${device.encrypt.key}")
    private String encryptKey;

    @Override
    public YunTypeEnum getYunTypeNum() {
        return YunTypeEnum.YUNSHITONG;
    }

    @Override
    public String getAccessToken(String eid, AccountTypeEnum accountType) {
        // 非线上环境服务都从线上获取token，保证token获取源只有一个
        String profileName = CommonContextUtil.getProfileName();
        if (!("online".equals(profileName) || "hd".equals(profileName))) {
            log.info("非线上环境，从线上获取中维token");
            return getOnlineYunshitongAccessToken(accountType);
        }
        String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, AccountTypeEnum.PLATFORM.equals(accountType) ? "unique" : eid, accountType.getCode(), getYunTypeNum().getCode());
        String accessToken = redisUtilPool.getString(cacheKey);
        if(StringUtils.isNotBlank(accessToken)){
            return accessToken;
        }
        String appKey = null, appSecret = null;
        if(AccountTypeEnum.PLATFORM.equals(accountType)){
            appKey = this.appKey;
            appSecret = this.appSecret;
        }else{
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(eid, getYunTypeNum().getCode(), accountType.getCode());
            DynamicDataSourceContextHolder.setDataSourceType(dbName);
            appKey = videoSetting.getAccessKeyId();
            appSecret = videoSetting.getSecret();
        }
        String url = "https://openapi.cloudsee.com/v1/api/token/get_token";
        Map<String, String> request = new HashMap<>();
        request.put("appKey", appKey);
        request.put("appSecret", appSecret);
        JSONObject jsonObject = yunShiTongHttpClient.postForObject(url, request, JSONObject.class);
        if(Objects.isNull(jsonObject)){
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        accessToken = jsonObject.getString("accessToken");
        Integer expire = jsonObject.getInteger("expire");
        redisUtilPool.setString(cacheKey, accessToken, expire - Constants.THREE_HUNDRED);
        return accessToken;
    }

    @Override
    public OpenDeviceDTO getDeviceDetail(String eid, String deviceId, AccountTypeEnum accountType) {
        String accessToken = getAccessToken(eid, accountType);
        String url = "https://openapi.cloudsee.com/v1/api/device/get_device_details";
        Map<String, String> request = new HashMap<>();
        request.put("deviceSn", deviceId);
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, request, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(eid, accountType, url, request, JSONObject.class, retryTimes);
            }
        }
        return convertDTO(jsonObject);
    }

    @Override
    public PageInfo<OpenDevicePageDTO> getDeviceList(String eid, AccountTypeEnum accountType, Integer pageNum, Integer pageSize) {
        String accessToken = getAccessToken(eid, accountType);
        String url = "https://openapi.cloudsee.com/v1/api/device/get_list";
        Map<String, Integer> request = new HashMap<>();
        request.put("pageStart", pageNum);
        request.put("pageSize", pageSize);
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, request, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(eid, accountType, url, request, JSONObject.class, retryTimes);
            }
        }
        if(Objects.isNull(jsonObject)){
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        JSONObject page = jsonObject.getJSONObject("page");
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        PageInfo pageInfo = new PageInfo();
        Integer total = page.getInteger("total");
        pageInfo.setTotal(total);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPageNum(pageNum);
        pageInfo.setPages(total % pageSize == 0 ? total / pageSize : total / pageSize + 1);
        if(!jsonArray.isEmpty()){
            List<OpenDevicePageDTO> list = new ArrayList<>();
            for (Object device : jsonArray) {
                JSONObject deviceObj = JSONObject.parseObject(JSONObject.toJSONString(device));
                OpenDevicePageDTO pageDevice = new OpenDevicePageDTO();
                pageDevice.setDeviceId(deviceObj.getString("deviceSn"));
                pageDevice.setDeviceName(deviceObj.getString("deviceName"));
                pageDevice.setHasChildDevice(DeviceCatalog.NVR.getCode().equals(deviceObj.getString("deviceType")));
                pageDevice.setSource(getYunTypeNum().getCode());
                pageDevice.setDeviceStatus(Constants.INDEX_ONE.equals(deviceObj.getInteger("deviceState"))  ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                list.add(pageDevice);
            }
            pageInfo.setList(list);
        }
        return pageInfo;
    }

    @Override
    public LiveVideoVO getLiveUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        String url = "https://openapi.cloudsee.com/v1/api/device/video/get_realtime_url_standard";
        String channelNo = param.getChannelNo();
        VideoProtocolTypeEnum protocolTypeEnum = param.getProtocol();
        Integer quality = param.getQuality();
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", device.getDeviceId());
        paramMap.put("channelId", StringUtils.isBlank(channelNo) ? Constants.STRING_ZERO : channelNo);
        paramMap.put("streamId", Objects.nonNull(quality) && Constants.INDEX_ONE.equals(quality) ? Constants.ZERO : Constants.INDEX_ONE);
        paramMap.put("protocolType", protocolTypeEnum.getCode());
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            } else {
                throw e;
            }
        }
        if(Objects.isNull(jsonObject)){
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        String liveUrl = jsonObject.getString("url");
        if(StringUtils.isNotBlank(param.getSliceType())){
            liveUrl = liveUrl.contains(Constants.QUESTION_MARK) ? liveUrl + "&sliceType=" + param.getSliceType() : liveUrl + "?sliceType="+ param.getSliceType();
        }
        LiveVideoVO liveVideoVO = new LiveVideoVO();
        liveVideoVO.setUrl(liveUrl);
        return liveVideoVO;
    }

    private <T> T  retry(String enterpriseId, AccountTypeEnum accountType, String url, Object request, Class<T> responseType, int retryTimes){
        log.info("token异常, enterpriseId:{}, 重试：{}", enterpriseId, retryTimes);
        deleteAccessToken(enterpriseId, null, accountType);
        T response = null;
        if(retryTimes < Constants.INDEX_TWO){
            String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, enterpriseId, accountType.getCode(), getYunTypeNum().getCode());
            redisUtilPool.delKey(cacheKey);
            String accessToken = getAccessToken(enterpriseId, accountType);
            try {
                response = yunShiTongHttpClient.postForObject(url, request, accessToken, responseType);
            } catch (ServiceException e) {
                if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                    retryTimes = retryTimes + 1;
                    response = retry(enterpriseId, accountType, url, request, responseType, retryTimes);
                } else {
                    throw e;
                }
            }
        }
        return response;
    }

    /**
     * 删除缓存
     */
    @Override
    public void deleteAccessToken(String eid, String deviceId, AccountTypeEnum accountType) {
        String cacheKey = MessageFormat.format(RedisConstant.DEVICE_OPEN_TOKEN, AccountTypeEnum.PLATFORM.equals(accountType) ? "unique" : eid, accountType.getCode(), getYunTypeNum().getCode());
        redisUtilPool.delKey(cacheKey);
    }

    @Override
    public LiveVideoVO getPastVideoUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        String startTime = param.getStartTime(), endTime = param.getEndTime(), channelNo = param.getChannelNo();
        if(StringUtils.isAnyBlank(startTime, endTime)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        startTime = DateUtil.format(DateUtil.parse(startTime, DateUtils.DATE_FORMAT_SEC), DateUtils.DATE_FORMAT_SEC_7);
        endTime = DateUtil.format(DateUtil.parse(endTime, DateUtils.DATE_FORMAT_SEC), DateUtils.DATE_FORMAT_SEC_7);
        boolean haveVideo = isHaveVideo(enterpriseId, startTime, endTime, device.getDeviceId(), channelNo, accountType);
        if(!haveVideo){
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_VIDEO);
        }
        String url = "https://openapi.cloudsee.com/v1/api/device/video/get_playback_standard_url_bytime";
        HashMap<String, Object> paramMap = new HashMap<>();
        if(StringUtils.isBlank(channelNo)){
            channelNo = Constants.ZERO_STR;
        }
        paramMap.put("deviceSn", device.getDeviceId());
        paramMap.put("channelId", channelNo);
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        paramMap.put("protocolType", param.getProtocol().getCode());
        String accessToken = getAccessToken(enterpriseId, accountType);
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(enterpriseId, accountType, url, paramMap, JSONObject.class, retryTimes);
            }
        }
        if(Objects.isNull(jsonObject)){
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        String liveUrl = jsonObject.getString("url");
        LiveVideoVO liveVideoVO = new LiveVideoVO();
        liveVideoVO.setUrl(liveUrl);
        return liveVideoVO;
    }

    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {
        String url = "https://openapi.cloudsee.com/v1/api/device/delete";
        HashMap paramMap = new HashMap();
        paramMap.put("deviceSn", device.getDeviceId());
        //取消托管
        yunShiTongHttpClient.postForObject(url, paramMap, getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType())), JSONObject.class);
    }

    @Override
    public Boolean ptzStart(String eid, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration) {
        //参数适配处理  command 转换
        String url = "https://openapi.cloudsee.com/v1/api/device/ptz/control_move_start";
        Integer panLeft = command == 2 ? 50 : command == 3 ? -50 : 0;
        Integer tiltUp = command == 0 ? 50 : command == 1 ? -50 : 0;;
        Integer zoomIn = command == 8 ? 10 : command == 9 ? -10 : 0;;
        HashMap<String, Object> paramMap = new HashMap<>();
        if(StringUtils.isBlank(channelNo)){
            channelNo = Constants.ZERO_STR;
        }
        paramMap.put("deviceSn", device.getDeviceId());
        paramMap.put("channelId", channelNo);
        paramMap.put("panLeft", panLeft);
        paramMap.put("tiltUp", tiltUp);
        paramMap.put("zoomIn", zoomIn);
        String accessToken = getAccessToken(eid, AccountTypeEnum.getAccountType(device.getAccountType()));
        try {
            yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                retry(eid, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            }
        }
        return true;
    }

    @Override
    public Boolean ptzStop(String eid, DeviceDO device, String channelNo) {
        String url = "https://openapi.cloudsee.com/v1/api/device/ptz/control_move_stop";
        HashMap<String, Object> paramMap = new HashMap<>();
        if(StringUtils.isBlank(channelNo)){
            channelNo = Constants.ZERO_STR;
        }
        paramMap.put("deviceSn", device.getDeviceId());
        paramMap.put("channelId", channelNo);
        String accessToken = getAccessToken(eid, AccountTypeEnum.getAccountType(device.getAccountType()));
        try {
            yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                retry(eid, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            }
        }
        return true;
    }

    @Override
    public String addPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String devicePositionName) {
        List<DevicePositionDO> devicePosition = devicePositionMapper.listDevicePositionByDeviceAndChannel(enterpriseId, device.getDeviceId(), null);
        int maxIndex= 0;
        if (CollectionUtils.isNotEmpty(devicePosition)){
            maxIndex=Integer.valueOf(devicePosition.get(0).getPositionIndex());
        }
        String url = "https://openapi.cloudsee.com/v1/api/device/trans_cmd";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", device.getDeviceId());
        JSONObject dataJson = new JSONObject();
        dataJson.put("method", "ptz_preset_set");
        JSONObject paramJson = new JSONObject();
        if(StringUtils.isBlank(channelNo)){
            channelNo = Constants.ZERO_STR;
        }
        paramJson.put("channelid", Integer.valueOf(channelNo));
        paramJson.put("name", devicePositionName);
        paramJson.put("presetno", maxIndex + 10);
        dataJson.put("param", paramJson);
        paramMap.put("data", dataJson);
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        paramMap.put("data", dataJson.toJSONString());
        try {
            yunShiTongHttpClient.postForObject(url, paramMap, accessToken, String.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            }
        }
        return String.valueOf(maxIndex);
    }

    @Override
    public Boolean deletePtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        String url = "https://openapi.cloudsee.com/v1/api/device/trans_cmd";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", device.getDeviceId());
        JSONObject dataJson = new JSONObject();
        dataJson.put("method", "ptz_preset_delete");
        JSONObject paramJson = new JSONObject();
        if(StringUtils.isBlank(channelNo)){
            channelNo = Constants.ZERO_STR;
        }
        paramJson.put("channelid", Integer.valueOf(channelNo));
        paramJson.put("presetno", Integer.valueOf(presetIndex));
        dataJson.put("param", paramJson);
        paramMap.put("data", dataJson.toJSONString());
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        try {
            yunShiTongHttpClient.postForObject(url, paramMap, accessToken, String.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            }
        }
        return null;
    }

    @Override
    public Boolean loadPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex) {
        String url = "https://openapi.cloudsee.com/v1/api/device/trans_cmd";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", device.getDeviceId());
        JSONObject dataJson = new JSONObject();
        dataJson.put("method", "ptz_preset_locate");
        JSONObject paramJson = new JSONObject();
        if(StringUtils.isBlank(channelNo)){
            channelNo = Constants.ZERO_STR;
        }
        paramJson.put("channelid", Integer.valueOf(channelNo));
        paramJson.put("presetno", Integer.valueOf(presetIndex));
        paramJson.put("movespeed", Constants.INDEX_ONE);
        dataJson.put("param", paramJson);
        paramMap.put("data", dataJson.toJSONString());
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        try {
            yunShiTongHttpClient.postForObject(url, paramMap, accessToken, String.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            }
        }
        return null;
    }

    @Override
    public String capture(String enterpriseId, DeviceDO device, String channelNo, String quality) {
        return captureSnapshot(enterpriseId, device, channelNo, null);
    }

    @Override
    public List<CapturePictureDTO> captureByTimeDict(String enterpriseId, DeviceDO deviceDO, String channelNo, List<String> captureTimes) {
        DateTimeFormatter from = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter to = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        List<CapturePictureDTO> result = new ArrayList<>();
        for (String captureTime : captureTimes) {
            LocalDateTime time = LocalDateTime.parse(captureTime, from);
            try {
                String url = captureSnapshot(enterpriseId, deviceDO, channelNo, time.format(to));
                result.add(new CapturePictureDTO(deviceDO.getDeviceId(), channelNo, time, url, null, null));
            } catch (ServiceException e) {
                result.add(new CapturePictureDTO(deviceDO.getDeviceId(), channelNo, time, null, String.valueOf(e.getErrorCode()), e.getErrorMessage()));
                break;
            }
        }
        return result;
    }

    /**
     * 抓图
     * @param enterpriseId 企业id
     * @param device 设备
     * @param channelNo 通道号
     * @param frameTime 抓图时间点，yyyyMMddHHmmss，不填抓实况图
     * @return 图片url
     */
    private String captureSnapshot(String enterpriseId, DeviceDO device, String channelNo, String frameTime) {
        String url = "https://openapi.cloudsee.com/v1/api/device/control_stream_snapshot";
        HashMap<String, Object> paramMap = new HashMap<>();
        if(StringUtils.isBlank(channelNo)){
            channelNo = Constants.ZERO_STR;
        }
        paramMap.put("deviceSn", device.getDeviceId());
        paramMap.put("channelId", channelNo);
        if (StringUtils.isNotBlank(frameTime)) {
            paramMap.put("frameTime", frameTime);
        }
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            } else {
                throw e;
            }
        }
        return Optional.ofNullable(jsonObject).map(o->o.getString("url")).orElse("");
    }

    @Override
    public String videoTransCode(String enterpriseId,  DeviceDO device, VideoDTO param) {
        String url = "https://openapi.cloudsee.com/v1/api/vds/video/download";
        Map<String, Object> paramMap = new HashMap<>();
        if (StringUtils.isBlank(param.getChannelNo())) {
            param.setChannelNo(Constants.ZERO_STR);
        }
        paramMap.put("deviceSn", device.getDeviceId());
        paramMap.put("channelId", param.getChannelNo());
        paramMap.put("beginTime", DateUtils.dateTimeFormat(param.getStartTime()));
        paramMap.put("endTime", DateUtils.dateTimeFormat(param.getEndTime()));
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        JSONObject jsonObject = executeAndRetry(enterpriseId, device, url, paramMap, accessToken, null);
        return Optional.ofNullable(jsonObject).map(o -> o.getString("taskId")).orElse("");

    }

    @Override
    public VideoFileDTO getVideoFile(String enterpriseId,DeviceDO device, String fileId) {
        return new VideoFileDTO(0);
    }

    @Override
    public List<String> getVideoDownloadUrl(String enterpriseId,DeviceDO device, String fileId) {
        String url = "https://openapi.cloudsee.com/v1/api/vds/video/get_task_list";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", device.getDeviceId());
        paramMap.put("taskId", fileId);
        paramMap.put("pageStart", 1);
        paramMap.put("pageSize", 500);
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        JSONObject jsonObject = executeAndRetry(enterpriseId, device, url, paramMap, accessToken, null);
        JSONArray taskList = jsonObject.getJSONArray("taskList");
        return ListUtils.emptyIfNull(taskList).stream().map(o->JSONObject.parseObject(JSONObject.toJSONString(o))).filter(o->o.getInteger("taskStatus") == 1).map(o->o.getString("filePath")).collect(Collectors.toList());
    }

    @Override
    public AppKeyDTO authentication(String eid, DeviceDO device) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(eid, accountType);
        EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(eid, getYunTypeNum().getCode(), accountType.getCode());
        String appKey = Optional.ofNullable(videoSetting).map(EnterpriseVideoSettingDTO::getAccessKeyId).orElse("");
        return new AppKeyDTO(appKey, accessToken);
    }


    private OpenDeviceDTO convertDTO(JSONObject jsonObject){
        if(Objects.isNull(jsonObject)){
            return null;
        }
        OpenDeviceDTO result = new OpenDeviceDTO();
        result.setDeviceId(jsonObject.getString("deviceSn"));
        result.setDeviceName(jsonObject.getString("deviceName"));
        Boolean isNvr = DeviceCatalog.NVR.getCode().equals(jsonObject.getString("deviceType"));
        result.setHasChildDevice(isNvr);
        result.setDeviceStatus(Constants.INDEX_ONE.equals(jsonObject.getInteger("deviceState"))  ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
        result.setSource(getYunTypeNum().getCode());
        result.setSupportPassenger(false);
        result.setSupportCapture(Constants.ONE);
        JSONArray ability = jsonObject.getJSONArray("ability");
        DeviceCapacityDTO deviceCapacity = DeviceCapacityDTO.convertYunShiTongDeviceCapacity(ability);
        result.setHasPtz(YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzLeftRight()));
        JSONArray videoChannels = jsonObject.getJSONArray("videoChannels");
        if(isNvr && !videoChannels.isEmpty()){
            List<OpenChannelDTO> channelList = new ArrayList<>();
            int index = 0;
            for (Object videoChannel : videoChannels) {
                JSONObject channel = JSONObject.parseObject(JSONObject.toJSONString(videoChannel));
                OpenChannelDTO channelDTO = new OpenChannelDTO();
                String channelGBId = channel.getString("channelGBId");
                String deviceId = channel.getString("deviceId");
                String subDeviceId = StringUtils.isBlank(channelGBId) ? deviceId + index++ : channelGBId;
                channelDTO.setDeviceId(subDeviceId);
                channelDTO.setParentDeviceId(deviceId);
                channelDTO.setChannelNo(channel.getString("channelId"));
                channelDTO.setChannelName(channel.getString("channelName"));
                Integer channelState = channel.getInteger("channelState");
                channelDTO.setStatus(Objects.nonNull(channelState) && Constants.INDEX_ONE.equals(channelState) ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                JSONArray channelAbility = channel.getJSONArray("channelAbility");
                DeviceCapacityDTO deviceCapacityDTO = DeviceCapacityDTO.convertYunShiTongDeviceCapacity(channelAbility);
                channelDTO.setDeviceCapacity(deviceCapacityDTO);
                channelDTO.setHasPtz(YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzLeftRight()));
                channelList.add(channelDTO);
            }
            result.setChannelList(channelList);
        }
        return result;
    }

    private boolean isHaveVideo(String enterpriseId, String startTime, String endTime, String deviceId, String channelNo, AccountTypeEnum accountTypeEnum){
        String url = "https://openapi.cloudsee.com/v1/api/device/record/get_record_list";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", deviceId);
        paramMap.put("channelId", channelNo);
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        paramMap.put("pageStart", Constants.INDEX_ONE);
        paramMap.put("pageSize", Constants.INDEX_ONE);
        paramMap.put("sortByTime", "asc");
        String accessToken = getAccessToken(enterpriseId, accountTypeEnum);
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(enterpriseId, accountTypeEnum, url, paramMap, JSONObject.class, retryTimes);
            }
        }
        if(Objects.isNull(jsonObject)){
            throw new ServiceException(ErrorCodeEnum.API_ERROR);
        }
        JSONArray items = jsonObject.getJSONArray("items");
        if(items.isEmpty()){
            return false;
        }
        JSONObject fileObj = JSONObject.parseObject(items.getObject(0, String.class));
        long fileStartTime = fileObj.getLong("startTime");
        return Long.valueOf(startTime) >= fileStartTime;
    }


    /**
     * 获取实况预览播放地址
     * @param enterpriseId
     * @param deviceId
     * @return
     */
    public JSONObject getDeviceDetail(String enterpriseId, String deviceId){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if(Objects.isNull(device)){
            return null;
        }
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);
        String url = "https://openapi.cloudsee.com/v1/api/device/get_device_details";
        Map<String, String> request = new HashMap<>();
        request.put("deviceSn", deviceId);
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, request, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, request, JSONObject.class, retryTimes);
            }
        }
        return jsonObject;
    }

    /**
     * 获取实况预览播放地址
     * @param enterpriseId
     * @param deviceId
     * @param channelId
     * @param streamId
     * @return
     */
    public JSONObject getRealtimeUrl(String enterpriseId, String deviceId, String channelId, String streamId) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if(Objects.isNull(device)){
            return null;
        }
        String url = "https://openapi.cloudsee.com/v1/api/device/video/get_realtime_url";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", deviceId);
        paramMap.put("channelId", StringUtils.isBlank(channelId) ? Constants.STRING_ZERO : channelId);
        paramMap.put("streamId", Objects.nonNull(streamId) ? streamId: Constants.ZERO);
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            }
        }
        return jsonObject;
    }


    /**
     * 获取设备本地录像地址,按时间段
     * @param enterpriseId
     * @param deviceId
     * @param channelId
     * @param startTime
     * @param endTime
     * @return
     */
    public JSONObject getPlaybackUrlByTime(String enterpriseId, String deviceId, String channelId, String startTime, String endTime) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if(Objects.isNull(device)){
            return null;
        }
        if(StringUtils.isBlank(startTime)){
            String yyyyMMdd = DateUtil.format(new Date(), "yyyyMMdd");
            startTime = yyyyMMdd + "0000000";
            endTime = yyyyMMdd + "235959";
        }
        String url = "https://openapi.cloudsee.com/v1/api/device/video/get_playback_url_bytime";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", deviceId);
        paramMap.put("channelId", StringUtils.isBlank(channelId) ? Constants.STRING_ZERO : channelId);
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            }
        }
        return jsonObject;
    }

    /**
     * 获取设备本地录像
     * @param enterpriseId
     * @param deviceId
     * @param channelId
     * @param startTime
     * @param endTime
     * @param pageStart
     * @param pageSize
     * @param sortByTime
     * @return
     */
    public JSONObject getRecordList(String enterpriseId, String deviceId, String channelId, String startTime, String endTime, Integer pageStart, Integer pageSize, String sortByTime) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if(Objects.isNull(device)){
            return null;
        }
        if(StringUtils.isBlank(startTime)){
            String yyyyMMdd = DateUtil.format(new Date(), "yyyyMMdd");
            startTime = yyyyMMdd + "0000000";
            endTime = yyyyMMdd + "235959";
        }
        String url = "https://openapi.cloudsee.com/v1/api/device/record/get_record_list";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", deviceId);
        paramMap.put("channelId", StringUtils.isBlank(channelId) ? Constants.STRING_ZERO : channelId);
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        paramMap.put("pageStart", pageStart);
        paramMap.put("pageSize", pageSize);
        paramMap.put("sortByTime", sortByTime);
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            }
        }
        return jsonObject;
    }


    /**
     * 获取云端录像
     * @param enterpriseId
     * @param deviceId
     * @param channelId
     * @param startTime
     * @param endTime
     * @param pageStart
     * @param pageSize
     * @param sortByTime
     * @return
     */
    public JSONObject getCloudRecordList(String enterpriseId, String deviceId, String channelId, String startTime, String endTime, Integer pageStart, Integer pageSize, String sortByTime) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if(Objects.isNull(device)){
            return null;
        }
        if(StringUtils.isBlank(startTime)){
            String yyyyMMdd = DateUtil.format(new Date(), "yyyyMMdd");
            startTime = yyyyMMdd + "0000000";
            endTime = yyyyMMdd + "235959";
        }
        String url = "https://openapi.cloudsee.com/v1/api/device/cloudstorage/get_record_list";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", deviceId);
        paramMap.put("channelId", StringUtils.isBlank(channelId) ? Constants.STRING_ZERO : channelId);
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        paramMap.put("pageStart", pageStart);
        paramMap.put("pageSize", pageSize);
        paramMap.put("sortByTime", sortByTime);
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        JSONObject jsonObject = null;
        try {
            jsonObject = yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
        } catch (ServiceException e) {
            if(e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())){
                int retryTimes = Constants.INDEX_ONE;
                jsonObject = retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            }
        }
        return jsonObject;
    }

    @Override
    public Boolean pictureFlip(String enterpriseId, DeviceDO device, DeviceConfigDTO configDTO) {
        if (!Boolean.TRUE.equals(configDTO.getFlip()) && !Boolean.TRUE.equals(configDTO.getMirror())) {
            return false;
        }
        int channelNo = StringUtils.isNotBlank(configDTO.getChannelNo()) ? Integer.parseInt(configDTO.getChannelNo()) : 0;

        Map<String, Object> getConfigParams = new HashMap<>();
        getConfigParams.put("channelid", channelNo);
        JSONObject imageConfig = deviceConfig(enterpriseId, device, "image_get_adjust_param", getConfigParams);
        imageConfig.put("channelid", channelNo);
        if (Boolean.TRUE.equals(configDTO.getFlip())) {
            imageConfig.put("bEnableST", !imageConfig.getBoolean("bEnableST"));
        }
        if (Boolean.TRUE.equals(configDTO.getMirror())) {
            imageConfig.put("bEnableMI", !imageConfig.getBoolean("bEnableMI"));
        }
        deviceConfig(enterpriseId, device, "image_set_adjust_param", imageConfig);
        return true;
    }

    @Override
    public Boolean configureDevice(String enterpriseId, DeviceDO device, DeviceConfigDTO configDTO) {
        if (StringUtils.isBlank(configDTO.getDeviceName())) {
            return false;
        }
        JSONObject deviceConfig = deviceConfig(enterpriseId, device, "dev_get_info", Collections.emptyMap());
        if (StringUtils.isNotBlank(configDTO.getDeviceName())) {
            deviceConfig.put("name", configDTO.getDeviceName());
        }
        deviceConfig(enterpriseId, device, "dev_set_info", deviceConfig);
        return true;
    }

    /**
     * 设备透传，直接与设备进行交互的接口，用户通过透传接口可以直接将设备协议发送到设备上，从而可以达到操作设备的目的
     * @param enterpriseId 企业id
     * @param device 设备
     * @param method 方法
     * @param param 参数
     * @return 响应data结果
     */
    private JSONObject deviceConfig(String enterpriseId, DeviceDO device, String method, Map<String, Object> param) {
        String url = "https://openapi.cloudsee.com/v1/api/device/trans_cmd";
        Map<String, Object> dataParam = new HashMap<>();
        dataParam.put("method",  method);
        dataParam.put("param", param);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", device.getDeviceId());
        paramMap.put("data", JSONObject.toJSONString(dataParam));
        String accessToken = getAccessToken(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()));
        JSONObject data = executeAndRetry(enterpriseId, device, url, paramMap, accessToken, true);
        if (Objects.nonNull(data)) {
            JSONObject error = data.getJSONObject("error");
            if (Objects.nonNull(error) && Constants.INDEX_ZERO.equals(error.getInteger("errorcode"))) {
                return data.getJSONObject("result");
            }
        }
        log.info("deviceConfig 第三方接口调用失败, paramMap:{}, response:{}", JSONObject.toJSONString(param), JSONObject.toJSONString(data));
        throw new ServiceException(ErrorCodeEnum.API_ERROR);
    }

    /**
     * 调用第三方接口
     */
    private JSONObject executeAndRetry(String enterpriseId, DeviceDO device, String url, Map<String, Object> paramMap, String accessToken, Boolean specialResult) {
        JSONObject data = null;
        try {
            if (Boolean.TRUE.equals(specialResult)) {
                String dataStr = yunShiTongHttpClient.postForObject(url, paramMap, accessToken, String.class).replace("\\\"", "\"");
                data = JSONObject.parseObject(dataStr);
            } else {
                data = yunShiTongHttpClient.postForObject(url, paramMap, accessToken, JSONObject.class);
            }
        } catch (ServiceException e) {
            if (e.getErrorCode().equals(ErrorCodeEnum.API_TOKEN_OVERDUE_ERROR.getCode())) {
                int retryTimes = Constants.INDEX_ONE;
                data = retry(enterpriseId, AccountTypeEnum.getAccountType(device.getAccountType()), url, paramMap, JSONObject.class, retryTimes);
            }else{
                throw e;
            }
        }
        return data;
    }

    @Override
    public List<DeviceVideoRecordVO> listDeviceRecordByTime(String enterpriseId, DeviceDO device, String channelNo, Long startTime, Long endTime) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String url = "https://openapi.cloudsee.com/v1/api/device/record/get_record_list";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", device.getDeviceId());
        paramMap.put("channelId", Integer.parseInt(channelNo));
        // yyyyMMddHHmmss
        paramMap.put("startTime", DateUtil.format(startTime, "yyyyMMddHHmmss"));
        paramMap.put("endTime", DateUtil.format(endTime, "yyyyMMddHHmmss"));
        paramMap.put("pageStart", 1);
        paramMap.put("pageSize", 500);
        paramMap.put("sortByTime", "asc");
        String accessToken = getAccessToken(enterpriseId, accountType);
        JSONObject data = executeAndRetry(enterpriseId, device, url, paramMap, accessToken, false);
        if (Objects.nonNull(data)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            return data.getJSONArray("items").stream().map(item -> {
                JSONObject itemObj = (JSONObject) item;
                long startMilli = LocalDateTime.parse(itemObj.getString("startTime"), formatter).atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
                long endMilli = LocalDateTime.parse(itemObj.getString("endTime"), formatter).atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
                String recordType = itemObj.getString("recordType");
                return new DeviceVideoRecordVO(null, 2, startMilli, endMilli, device.getDeviceId(), channelNo, "alarm".equals(recordType) ? "ALARM" : "TIMING");
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
    @Override
    public DeviceTalkbackVO deviceTalkback(String enterpriseId, DeviceDO device, DeviceTalkbackDTO talkbackDTO) {
        AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
        String accessToken = getAccessToken(enterpriseId, accountType);

        String url = "https://openapi.cloudsee.com/v1/api/device/video/get_voice_url_standard";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("deviceSn", device.getDeviceId());
        paramMap.put("channelId", Integer.parseInt(talkbackDTO.getChannelNo()));
        paramMap.put("protocolType", talkbackDTO.getProtocolType().getCode());
        paramMap.put("maxPlayDuration", 36000);
        JSONObject data = executeAndRetry(enterpriseId, device, url, paramMap, accessToken, false);
        if (Objects.nonNull(data)) {
            return new DeviceTalkbackVO(data.getString("url"));
        }
        log.info("deviceTalkback 第三方接口调用失败, paramMap:{}, response:{}", JSONObject.toJSONString(paramMap), JSONObject.toJSONString(data));
        throw new ServiceException(ErrorCodeEnum.API_ERROR);
    }

    @Override
    public DeviceSoftHardwareInfoVO deviceSoftHardwareInfo(String enterpriseId, DeviceDO device) {
        JSONObject hwInfo = deviceConfig(enterpriseId, device, "dev_get_hwinfo", Collections.emptyMap());
        JSONObject netConfig = deviceConfig(enterpriseId, device, "ifconfig_get_inet", Collections.emptyMap());
        JSONObject getParams = deviceConfig(enterpriseId, device, "stream_get_params", Collections.emptyMap());
        JSONObject eth = netConfig.getJSONObject("eth");
        JSONObject wifi = netConfig.getJSONObject("wifi");
        String ip = null, mac = null, vencType = null;
        if (Objects.nonNull(eth)) {
            ip = eth.getString("addr");
            mac = eth.getString("mac");
        }
        if (Objects.nonNull(wifi) && StringUtils.isBlank(ip)) {
            ip = wifi.getString("addr");
            mac = wifi.getString("mac");
        }
        if(Objects.nonNull(getParams)){
            JSONArray streams = getParams.getJSONArray("streams");
            if(Objects.nonNull(streams)){
                JSONObject stream = streams.getJSONObject(0);
                vencType = stream.getString("venctype");
            }
        }
        return DeviceSoftHardwareInfoVO.builder()
                .deviceModel(hwInfo.getString("model"))
                .hardwareVersion(hwInfo.getString("hardware"))
                .firmwareVersion(hwInfo.getString("firmware"))
                .ip(ip)
                .mac(mac).vencType(vencType)
                .build();
    }

    @Override
    public Boolean deviceStorageFormatting(String enterpriseId, DeviceDO device, String channelNo) {
        JSONObject storageInfo = deviceConfig(enterpriseId, device, "storage_get_info", Collections.emptyMap());
        JSONArray disks = storageInfo.getJSONArray("disk");
        if (Objects.isNull(disks)) return false;
        for (int i = 0; i < disks.size(); i++) {
            JSONObject disk = disks.getJSONObject(i);
            Integer diskNum = disk.getInteger("diskNum");
            String devName = disk.getString("devName");
            Map<String, Object> params = new HashMap<>();
            params.put("diskNum", diskNum);
            params.put("devName", devName);
            params.put("partionNum", "-1");
            deviceConfig(enterpriseId, device, "storage_format", params);
        }
        return true;
    }

    @Override
    public List<DeviceStorageInfoVO> deviceStorageInfo(String enterpriseId, DeviceDO device) {
        JSONObject storageInfo = deviceConfig(enterpriseId, device, "storage_get_info", Collections.emptyMap());
        JSONArray disk = storageInfo.getJSONArray("disk");
        if (Objects.isNull(disk)) return Collections.emptyList();
        return disk.toJavaList(JSONObject.class)
                .stream()
                .map(v -> {
                    Long totalSize = v.getLong("sizeMB");
                    Long usedSize = v.getLong("usedMB");
                    String status = v.getString("status");
                    return DeviceStorageInfoVO.builder()
                            .totalSize(totalSize)
                            .availableSize(totalSize - usedSize)
                            .type(YunshitongStorageStatusEnum.READONLY.getStatus().equals(status) ? 1 : 0)
                            .status(YunshitongStorageStatusEnum.getMsgByStatus(status))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Boolean deviceReboot(String enterpriseId, DeviceDO device, String channelNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("channelid", StringUtils.isNotBlank(channelNo) ? channelNo : "-1");
        param.put("delaymSec", 1000);
        deviceConfig(enterpriseId, device, "dev_reboot", param);
        return true;
    }

    @Override
    public Boolean updateVideoVencType(String enterpriseId, DeviceDO device, String vencType) {
        JSONObject getParams = deviceConfig(enterpriseId, device, "stream_get_params", Collections.emptyMap());
        if(Objects.nonNull(getParams)){
            JSONArray streams = new JSONArray();
            for (Object o : getParams.getJSONArray("streams")) {
                JSONObject stream = (JSONObject) o;
                stream.put("venctype", vencType);
                streams.add(stream);
            }
            JSONObject setParams = new JSONObject();
            setParams.put("streams", streams);
            deviceConfig(enterpriseId, device, "stream_set_params", setParams);
            return true;
        }
        return false;
    }

    /**
     * 获取线上云视通token
     */
    private String getOnlineYunshitongAccessToken(AccountTypeEnum accountType) {
        String accessToken = getOnlineAccessToken();
        String url = String.format("%s/v3/enterprises/140e9bf7acf445a08864d1afcc1814fa/devices/getAccessToken?accountType=%s&yunType=%s&access_token=%s", onlineAccessTokenUrl, accountType.name(), YunTypeEnum.YUNSHITONG.name(), accessToken);
        String responseStr = HttpUtil.createGet(url).execute().body();
        JSONObject response = JSONObject.parseObject(responseStr);
        log.info("getOnlineYunshitongAccessToken response:{}", responseStr);
        if (Objects.nonNull(response) && Integer.valueOf(200000).equals(response.getInteger("code"))) {
            String data = response.getString("data");
            return AESUtil.decrypt(encryptKey, data);
        }
        throw new ServiceException(ErrorCodeEnum.GET_ONLINE_ACCESS_TOKEN_ERROR);
    }

    /**
     * 获取线上token
     */
    private String getOnlineAccessToken() {
        String cacheKey = RedisConstant.ONLINE_X_STORE_ACCESS_TOKEN;
        String accessToken = redisUtilPool.getString(cacheKey);
        if(StringUtils.isNotBlank(accessToken)){
            return accessToken;
        }
        String url = onlineAccessTokenUrl + "/boss/api/bossController/getTokenByEidAndUserID?enterpriseId=140e9bf7acf445a08864d1afcc1814fa&userId=a100000001";
        String responseStr = HttpUtil.createGet(url).execute().body();
        JSONObject response = JSONObject.parseObject(responseStr);
        log.info("getOnlineAccessToken response:{}", responseStr);
        if (Objects.nonNull(response) && Integer.valueOf(200000).equals(response.getInteger("code"))) {
            JSONObject data = response.getJSONObject("data");
            if (Objects.nonNull(data)) {
                accessToken = data.getString("access_token");
                redisUtilPool.setString(cacheKey, accessToken, Constants.ACTION_TOKEN_EXPIRE - 300);
                return accessToken;
            }
        }
        throw new ServiceException(ErrorCodeEnum.GET_ONLINE_ACCESS_TOKEN_ERROR);
    }
}
