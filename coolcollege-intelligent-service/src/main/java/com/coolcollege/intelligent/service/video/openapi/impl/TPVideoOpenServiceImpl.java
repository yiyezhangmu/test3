package com.coolcollege.intelligent.service.video.openapi.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.device.response.tplink.*;
import com.coolcollege.intelligent.model.device.vo.DeviceVideoRecordVO;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.video.openapi.VideoOpenService;
import com.coolcollege.intelligent.service.video.status.ThirdPartyDeviceStatusCode;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TPVideoOpenServiceImpl implements VideoOpenService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    protected EnterpriseVideoSettingService enterpriseVideoSettingService;

    public final static String SK = "3fbcbf22f4244d8d8851f4f141c8234e";

    @Override
    public YunTypeEnum getYunTypeNum() {
        return YunTypeEnum.TP_LINK;
    }

    @Override
    public String getAccessToken(String enterpriseId, AccountTypeEnum accountType) {
        return "access_token_placeholder";
    }

    private static final String HOST = "api-smbcloud.tp-link.com.cn";
    // terminalId可用UUID生成，每个客户端每次发送请求不需要变化
    private final static String TERMINAL_ID = UUID.randomUUID().toString().replaceAll("-", "");
    // 当请求体为空，使用此常量作为默认的payload进行签名计算
    private final static String DEFAULT_EMPTY_PAYLOAD = "{}";

    @Override
    public OpenDeviceDTO getDeviceDetail(String enterpriseId, String deviceId, AccountTypeEnum accountType) {
        String path = "/tums/open/deviceManager/v1/getDeviceDetails";
        String payload = String.format("{\"qrCode\":\"%s\"}", deviceId);
        TpDeviceDetailResponse tpDeviceDetailResponse = sendPostRequest(enterpriseId, path, payload, TpDeviceDetailResponse.class);
        OpenDeviceDTO result = TpDeviceDetailResponse.convert(tpDeviceDetailResponse);
        DeviceCapacityDTO deviceCapacity = getDeviceCapacity(enterpriseId, deviceId, null);
        result.setDeviceCapacity(deviceCapacity);
        result.setHasPtz(YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzLeftRight()));
        if(StringUtils.isNotBlank(tpDeviceDetailResponse.getDeviceType()) && "NETWORKVIDEORECORDER".equals(tpDeviceDetailResponse.getDeviceType())){
            result.setChannelList(getChannelList(enterpriseId, deviceId));
        }
        return result;
    }

    public List<OpenChannelDTO> getChannelList(String enterpriseId, String deviceId){
        String path = "/tums/open/deviceManager/v1/getChannelDeviceList";
        String payload = String.format("{\"qrCode\":\"%s\"}", deviceId);
        TpDeviceChannelResponse tpDeviceDetailResponse = sendPostRequest(enterpriseId, path, payload, TpDeviceChannelResponse.class);
        List<OpenChannelDTO> channelList = TpDeviceChannelResponse.convert(deviceId, tpDeviceDetailResponse);
        for (OpenChannelDTO channelDTO : channelList) {
            DeviceCapacityDTO deviceCapacity = getDeviceCapacity(enterpriseId, deviceId, channelDTO.getChannelNo());
            channelDTO.setDeviceCapacity(deviceCapacity);
            channelDTO.setHasPtz(YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzTopBottom()) || YesOrNoEnum.YES.getCode().equals(deviceCapacity.getPtzLeftRight()));
        }
        return channelList;
    }

    @Override
    public PageInfo<OpenDevicePageDTO> getDeviceList(String enterpriseId, AccountTypeEnum accountType, Integer pageNum, Integer pageSize) {
        String path = "/tums/open/deviceManager/v1/getDeviceListInProjectApplication";
        String payload = String.format("{\"start\":%d,\"limit\":%d}", (pageNum - 1) * pageSize, pageSize);
        TPDeviceListResponse response = sendPostRequest(enterpriseId, path, payload, TPDeviceListResponse.class);

        // 解析 JSON 到 PageInfo 对象
        // 此处需要根据实际响应结构调整
        PageInfo<OpenDevicePageDTO> pageInfo = new PageInfo<>();
        List<OpenDevicePageDTO> list = new ArrayList<>();
        if (response != null && response.getList() != null) {
            for (TPDeviceListResponse.TPDeviceListDetail detail : response.getList()) {
                OpenDevicePageDTO dto = new OpenDevicePageDTO();
                dto.setDeviceId(detail.getQrCode());
                dto.setDeviceName(detail.getDeviceName());
                dto.setSource(getYunTypeNum().getCode());
                dto.setDeviceStatus(Constants.INDEX_ONE.equals(detail.getDeviceStatus())  ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
                list.add(dto);
            }
            pageInfo.setTotal(response.getTotal());
        }
        pageInfo.setList(list);
        return pageInfo;
    }

    public DeviceCapacityDTO getDeviceCapacity(String enterpriseId, String deviceId, String channelNo) {
        try {
            String path = "/vms/open/deviceConfig/v1/getPtzCapability";
            JSONObject requestBody = new JSONObject();
            requestBody.put("qrCode", deviceId);
            requestBody.put("channelId", channelNo == null || Constants.ZERO_STR.equals(channelNo)  ? "1" : channelNo);
            String payload = JSONObject.toJSONString(requestBody);
            JSONObject resultJson = sendPostRequest(enterpriseId, path, payload, JSONObject.class);
            String talkPath = "/vms/open/deviceConfig/v1/getTalkCapability";
            JSONObject talkResultJson = sendPostRequest(enterpriseId, path, payload, JSONObject.class);
            return DeviceCapacityDTO.convertTPDeviceCapacity(resultJson, talkResultJson);
        }catch (Exception e){
            log.info("数据获取异常",e);
        }
        return DeviceCapacityDTO.defaultDeviceCapacity();
    }

    @Override
    public LiveVideoVO getLiveUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        JSONObject requestBody = new JSONObject();
        if(Objects.isNull(param.getClientType())){
            String path = "/vms/open/httpFlvService/v1/getHttpFlvUrl";
            requestBody.put("qrCode", device.getDeviceId());
            requestBody.put("channelId", param.getChannelNo() == null || Constants.ZERO_STR.equals(param.getChannelNo())  ? "1" : param.getChannelNo());
            requestBody.put("type", "video");
            requestBody.put("resolution", Constants.INDEX_ONE.equals(param.getQuality()) ? "HD" : "VGA");
            requestBody.put("videoCode", Constants.ONE_STR.equals(param.getSupportH265())? "H265" : "H264");
            String payload = JSONObject.toJSONString(requestBody);
            return sendPostRequest(enterpriseId, path, payload, LiveVideoVO.class);
        }
        String path = "/vms/open/webServer/v1/requestStreamUrl";
        requestBody.put("qrCode", device.getDeviceId());
        requestBody.put("channelId", param.getChannelNo() == null || Constants.ZERO_STR.equals(param.getChannelNo())  ? "1" : param.getChannelNo());
        requestBody.put("streamType", StringUtils.isBlank(param.getStreamType()) ? "video" : param.getStreamType());
        requestBody.put("resolution", VideoDTO.getTpQuality(param.getQuality()));
        requestBody.put("clientType", param.getClientType());
        String payload = JSONObject.toJSONString(requestBody);
        TPVideoUrlResponse tpVideoUrlResponse = sendPostRequest(enterpriseId, path, payload, TPVideoUrlResponse.class);
        if(Objects.isNull(tpVideoUrlResponse)){
            return null;
        }
        LiveVideoVO result = new LiveVideoVO();
        result.setUrl(tpVideoUrlResponse.getSdkStreamUrl());
        return result;
    }

    @Override
    public LiveVideoVO getPastVideoUrl(String enterpriseId, DeviceDO device, VideoDTO param) {
        if(Objects.isNull(param.getClientType())){
            String path = "/vms/open/httpFlvService/v1/getHttpFlvUrl";
            JSONObject requestBody = new JSONObject();
            requestBody.put("qrCode", device.getDeviceId());
            requestBody.put("channelId", param.getChannelNo() == null || Constants.ZERO_STR.equals(param.getChannelNo())  ? "1" : param.getChannelNo());
            requestBody.put("type", "sdvod");
            // 定义日期格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 解析字符串为 LocalDateTime
            LocalDateTime startDateTime = LocalDateTime.parse(param.getStartTime(), formatter);
            // 转换为 Instant
            java.time.Instant startInstant = startDateTime.atZone(ZoneId.systemDefault()).toInstant();
            // 解析字符串为 LocalDateTime
            LocalDateTime endDateTime = LocalDateTime.parse(param.getEndTime(), formatter);
            // 转换为 Instant
            java.time.Instant endInstant = endDateTime.atZone(ZoneId.systemDefault()).toInstant();
            requestBody.put("playbackStartTime", startInstant.toEpochMilli());
            requestBody.put("playbackEndTime", endInstant.toEpochMilli());
            String payload = JSONObject.toJSONString(requestBody);
            return sendPostRequest(enterpriseId, path, payload, LiveVideoVO.class);
        }
        String streamType = StringUtils.isBlank(param.getStreamType()) ? "sdvod" : param.getStreamType();
        param.setStreamType(streamType);
        return getLiveUrl(enterpriseId, device, param);
    }

    @Override
    public void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList) {
        String path = "/tums/open/deviceManager/v1/deleteDeviceList";
        List<JSONObject> channelListJson = new ArrayList<>();
        JSONObject requestBody = new JSONObject();
        requestBody.put("qrCode", device.getDeviceId());
        requestBody.put("channelId", Constants.INDEX_ONE);
        channelListJson.add(requestBody);
        if(CollectionUtils.isNotEmpty(channelList)){
            for (DeviceChannelDO deviceChannelDO : channelList) {
                JSONObject channelJson = new JSONObject();
                channelJson.put("qrCode", device.getDeviceId());
                channelJson.put("channelId", deviceChannelDO.getChannelNo());
                channelListJson.add(channelJson);
            }
        }
        JSONObject request = new JSONObject();
        request.put("devList", channelListJson);
        String payload = JSONObject.toJSONString(request);
        TpDeleteDeviceResponse tpDeleteDeviceResponse = sendPostRequest(enterpriseId, path, payload, TpDeleteDeviceResponse.class);
        if(Objects.isNull(tpDeleteDeviceResponse) || tpDeleteDeviceResponse.getError_code() != 0){
            throw new ServiceException(ErrorCodeEnum.ERROR, "第三方删除设备失败");
        }
    }

    @Override
    public Boolean ptzStart(String enterpriseId, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration) {
        String path = "/vms/open/deviceConfig/v1/motionCtrl";
        JSONObject requestBody = new JSONObject();
        requestBody.put("qrCode", device.getDeviceId());
        requestBody.put("channelId", channelNo == null || Constants.ZERO_STR.equals(channelNo)  ? "1" : channelNo);
        requestBody.put("direction", getDirection(command));
        requestBody.put("speed", speed);
        String payload = JSONObject.toJSONString(requestBody);
        sendPostRequest(enterpriseId, path, payload, Boolean.class);
        return true;
    }

    private String getDirection(Integer command) {
        Map<Integer, Integer> DIRECTION_TO_TARGET = new HashMap<>();
        // ommand 操作命令：0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-物理放大，9-物理缩小，10-调整近焦距，11-调整远焦距，16-自动控制
        DIRECTION_TO_TARGET.put(0, 7);   // 上 → 向上
        DIRECTION_TO_TARGET.put(1, 3);   // 下 → 向下
        DIRECTION_TO_TARGET.put(2, 5);   // 左 → 向左
        DIRECTION_TO_TARGET.put(3, 1);   // 右 → 向右
        DIRECTION_TO_TARGET.put(4, 6);   // 左上 → 左上
        DIRECTION_TO_TARGET.put(5, 4);   // 左下 → 左下
        DIRECTION_TO_TARGET.put(6, 8);   // 右上 → 右上
        DIRECTION_TO_TARGET.put(7, 2);   // 右下 → 右下
        // 变焦/调焦控制
        DIRECTION_TO_TARGET.put(8, 10);  // 物理放大 → 变倍+
        DIRECTION_TO_TARGET.put(9, 11);  // 物理缩小 → 变倍-
        DIRECTION_TO_TARGET.put(10, 12); // 调整近焦距 → 调焦+
        DIRECTION_TO_TARGET.put(11, 13); // 调整远焦距 → 调焦-
        // 自动控制
        DIRECTION_TO_TARGET.put(16, 9); // 自动控制 → 扫描
        return DIRECTION_TO_TARGET.get(command) + "";
    }

    @Override
    public Boolean ptzStop(String enterpriseId, DeviceDO device, String channelNo) {
        String path = "/vms/open/deviceConfig/v1/motionCtrl";
        JSONObject requestBody = new JSONObject();
        requestBody.put("qrCode", device.getDeviceId());
        requestBody.put("channelId", channelNo == null || Constants.ZERO_STR.equals(channelNo)  ? "1" : channelNo);
        requestBody.put("direction", "0");
        String payload = JSONObject.toJSONString(requestBody);
        sendPostRequest(enterpriseId, path, payload, Boolean.class);
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
    public String capture(String enterpriseId, DeviceDO device, String channelNo, String quality) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
        String path = "/vms/open/videoFetchService/v1/submitCaptureImageTask";
        JSONObject requestBody = new JSONObject();
        requestBody.put("qrCode", device.getDeviceId());
        requestBody.put("channelId",  StringUtils.isBlank(channelNo) || Constants.ZERO_STR.equals(channelNo)  ? "1" : channelNo);
        requestBody.put("type", "1");
        requestBody.put("playbackStartTime", LocalDateTime.now().format(inputFormatter));
        String payload = JSONObject.toJSONString(requestBody);
        TPVideoTransCodeResponse response = sendPostRequest(enterpriseId, path, payload, TPVideoTransCodeResponse.class);
        String taskId = Optional.ofNullable(response).map(TPVideoTransCodeResponse::getTaskId).orElse(null);
        TPTaskInfoResponse taskInfo = getTaskInfo(enterpriseId, taskId);
        int tryCount = 0;
        while (Objects.isNull(taskInfo) || taskInfo.getState() != 10 && tryCount < 3){
            try {
                Thread.sleep(1000L);
                taskInfo = getTaskInfo(enterpriseId, taskId);
                tryCount++;
            } catch (Exception e) {
                log.error("抓图失败", e);
                throw new ServiceException(ErrorCodeEnum.ERROR, "第三方设备抓图失败");
            }
        }
        if(taskInfo.getState() != 10 || taskInfo.getError_code() != 0){
            log.error("第三方设备抓图失败，taskInfo:{}", taskInfo);
            throw new ServiceException(ErrorCodeEnum.ERROR, "第三方设备抓图失败");
        }
        List<String> videoDownloadUrl = getVideoDownloadUrl(enterpriseId, device, taskId);
        if(CollectionUtils.isEmpty(videoDownloadUrl)){
            log.error("获取下载链接失败，taskInfo:{}", taskInfo);
            throw new ServiceException(ErrorCodeEnum.ERROR, "第三方设备抓图失败");
        }
        return videoDownloadUrl.get(0);
    }

    @Override
    public String videoTransCode(String enterpriseId, DeviceDO device, VideoDTO param) {
        // 解析开始时间和结束时间
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime startDateTime = LocalDateTime.parse(param.getStartTime(), inputFormatter);
        LocalDateTime endDateTime = LocalDateTime.parse(param.getEndTime(), inputFormatter);
        if(!startDateTime.toLocalDate().isEqual(endDateTime.toLocalDate())){
            throw new ServiceException(ErrorCodeEnum.ERROR, "暂不支持跨天录制");
        }
        // 定义当天的 23:59:50
        LocalDateTime dayEnd = startDateTime.toLocalDate().atTime(23, 59, 50);
        // 定义第二天的 00:00:00
        LocalDateTime nextDayStart = startDateTime.toLocalDate().plusDays(1).atStartOfDay();
        if(!startDateTime.isBefore(dayEnd) && endDateTime.isAfter(nextDayStart.minusSeconds(1))){
            throw new ServiceException(ErrorCodeEnum.ERROR, "暂不支持23:59:50至00:00:00（包含）的回放录像");
        }
        String path = "/vms/open/videoFetchService/v1/submitCaptureVideoTask";
        JSONObject requestBody = new JSONObject();
        requestBody.put("qrCode", device.getDeviceId());
        requestBody.put("channelId",  StringUtils.isBlank(param.getChannelNo()) || Constants.ZERO_STR.equals(param.getChannelNo())  ? "1" : param.getChannelNo());
        requestBody.put("type", "102");
        requestBody.put("playbackStartTime", param.getStartTime());
        requestBody.put("playbackEndTime", param.getEndTime());
        String payload = JSONObject.toJSONString(requestBody);
        TPVideoTransCodeResponse response = sendPostRequest(enterpriseId, path, payload, TPVideoTransCodeResponse.class);
        return Optional.ofNullable(response).map(TPVideoTransCodeResponse::getTaskId).orElse(null);
    }

    @Override
    public VideoFileDTO getVideoFile(String enterpriseId, DeviceDO device, String fileId) {
        if(StringUtils.isBlank(fileId)){
            return null;
        }
        TPTaskInfoResponse response = getTaskInfo(enterpriseId, fileId);
        return TPTaskInfoResponse.convert(response);
    }

    public TPTaskInfoResponse getTaskInfo(String enterpriseId, String taskId){
        if(StringUtils.isBlank(taskId)){
            return null;
        }
        String path = "/vms/open/videoFetchService/v1/getTaskInfo";
        JSONObject requestBody = new JSONObject();
        requestBody.put("taskId", taskId);
        String payload = JSONObject.toJSONString(requestBody);
        return sendPostRequest(enterpriseId, path, payload, TPTaskInfoResponse.class);
    }

    @Override
    public List<String> getVideoDownloadUrl(String enterpriseId, DeviceDO device, String fileId) {
        if(StringUtils.isBlank(fileId)){
            return null;
        }
        String path = "/vms/open/videoFetchService/v1/getTaskFilePage";
        JSONObject requestBody = new JSONObject();
        requestBody.put("taskId", fileId);
        requestBody.put("pageIndex", 0);
        requestBody.put("pageSize", 50);
        requestBody.put("urlRequired", true);
        requestBody.put("urlTtl", 2592000);
        String payload = JSONObject.toJSONString(requestBody);
        TPTaskFilePageResponse response = sendPostRequest(enterpriseId, path, payload, TPTaskFilePageResponse.class);
        if(Objects.nonNull(response)){
            return ListUtils.emptyIfNull(response.getList()).stream().map(TPTaskFilePageResponse.TaskFileDetail::getUrls).flatMap(List::stream).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<DeviceVideoRecordVO> listDeviceRecordByTime(String enterpriseId, DeviceDO device, String channelNo, Long startTime, Long endTime) {
        String path = "/vms/open/webServer/v2/searchVideo";
        List<DeviceVideoRecordVO> result = new ArrayList<>();
        boolean isContinue = true;
        int startIdx = 0, endIdx = 99;
        while (isContinue){
            JSONObject requestBody = new JSONObject();
            requestBody.put("qrCode", device.getDeviceId());
            requestBody.put("channelId",  StringUtils.isBlank(channelNo) || Constants.ZERO_STR.equals(channelNo)  ? "1" : channelNo);
            requestBody.put("searchDay", DateUtil.format(startTime, "yyyyMMdd"));
            requestBody.put("startIdx", startIdx);
            requestBody.put("endIdx", endIdx);
            String payload = JSONObject.toJSONString(requestBody);
            TPLocalVideoResponse response = sendPostRequest(enterpriseId, path, payload, TPLocalVideoResponse.class);
            if(CollectionUtils.isEmpty(response.getVideos()) || response.getVideos().size() < 100){
                isContinue = false;
            }
            List<DeviceVideoRecordVO> deviceVideoRecordVOS = TPLocalVideoResponse.convertList(device.getDeviceId(), channelNo, response);
            if(CollectionUtils.isNotEmpty(deviceVideoRecordVOS)){
                result.addAll(deviceVideoRecordVOS);
            }
            startIdx = endIdx + 1;
            endIdx = startIdx + 99;
        }
        return result;
    }

    @Override
    public Boolean deviceReboot(String enterpriseId, DeviceDO device, String channelNo){
        String path = "/tums/open/deviceManager/v1/rebootDeviceList";
        JSONObject requestBody = new JSONObject();
        requestBody.put("qrCode", device.getDeviceId());
        JSONObject qrCodeList = new JSONObject();
        qrCodeList.put("devList", Collections.singletonList(requestBody));
        String payload = JSONObject.toJSONString(qrCodeList);
        String s = sendPostRequest(enterpriseId, path, payload, String.class);
        return true;
    }

    @Override
    public Boolean deviceStorageFormatting(String enterpriseId, DeviceDO device, String channelNo){
        String sdCardPath = "/vms/open/deviceConfig/v1/getDeviceSdCardInfo";
        JSONObject requestBody = new JSONObject();
        requestBody.put("qrCode", device.getDeviceId());
        requestBody.put("channelId", StringUtils.isBlank(channelNo) || Constants.ZERO_STR.equals(channelNo)  ? "1" : channelNo);
        String payload = JSONObject.toJSONString(requestBody);
        TPSdCardInfoResponse response = sendPostRequest(enterpriseId, sdCardPath, payload, TPSdCardInfoResponse.class);
        if(Objects.isNull(response) || CollectionUtils.isEmpty(response.getCardInfoList())){
            return false;
        }
        List<Integer> cardIndexList = response.getCardInfoList().stream().map(TPSdCardInfoResponse.TPSdCardInfo::getIndex).collect(Collectors.toList());
        String path = "/vms/open/deviceConfig/v1/formatDeviceSdCard";
        for (Integer index : cardIndexList){
            requestBody.put("cardIndex", index);
            payload = JSONObject.toJSONString(requestBody);
            String s = sendPostRequest(enterpriseId, path, payload, String.class);
            log.info("设备格式化结果：{}", s);
        }
        return true;
    }

    public HttpHeaders buildAuthorization(String enterpiseId, String method, String path, String payload) {
        HttpHeaders headers = null;
        try {
            if(StringUtils.isBlank(payload)){
                payload = "{}";
            }
            String dbName = DynamicDataSourceContextHolder.getDataSourceType();
            EnterpriseVideoSettingDTO videoSetting = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(enterpiseId, getYunTypeNum().getCode(), AccountTypeEnum.PRIVATE.getCode());
            DataSourceHelper.changeToSpecificDataSource(dbName);
            String accessKeyId = videoSetting.getAccessKeyId();
            String accessSecret = videoSetting.getSecret();
            String timestamp = String.valueOf(Instant.now().getEpochSecond());
            String nonce = UUID.randomUUID().toString().replaceAll("-", "");
            String algorithm = "HmacSHA256";
            String hashedPayload = DigestUtils.sha256Hex(payload);

            String credentialScope = method + " " + path + " tp-link_request";
            String stringToSign = algorithm + "\n" + timestamp + "\n" + credentialScope + "\n" + hashedPayload;

            byte[] secretDate = hmacSha256(accessSecret, timestamp);
            byte[] secretService = hmacSha256(secretDate, path);
            byte[] secretSigning = hmacSha256(secretService, "tp-link");

            String signature = hexEncode(hmacSha256(secretSigning, stringToSign));

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.set("X-Authorization", String.format(
                    "Timestamp=%s,Nonce=%s,AccessKey=%s,Signature=%s,TerminalId=%s",
                    timestamp, nonce, accessKeyId, signature, TERMINAL_ID));
            headers.set("Host", HOST);
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeEnum.ERROR, "获取签名异常");
        }

        return headers;
    }

    private static byte[] hmacSha256(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec spec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(spec);
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] hmacSha256(String key, String msg) throws Exception {
        return hmacSha256(key.getBytes(StandardCharsets.UTF_8), msg);
    }

    private static String hexEncode(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public void setAppMsgPushConfig(String enterpriseId, String callbackUrl){
        String path = "/tums/open/msgTranspond/v1/setAppMsgPushConfig";
        JSONObject requestBody = new JSONObject();
        requestBody.put("serverUrl", callbackUrl);
        requestBody.put("openMsgTransport", "1");
        requestBody.put("msgContentType", Arrays.asList("nvrDeviceOnline","ipcDeviceOnline", "nvrDeviceOffline", "ipcDeviceOffline"));
        String payload = JSONObject.toJSONString(requestBody);
        try {
            sendPostRequest(enterpriseId, path, payload, TPTaskInfoResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setAppMsgPushSk(String enterpriseId, String sk){
        String path = "/tums/open/msgTranspond/v1/setAppMsgPushSk";
        JSONObject requestBody = new JSONObject();
        requestBody.put("sk", sk);
        String payload = JSONObject.toJSONString(requestBody);
        try {
            sendPostRequest(enterpriseId, path, payload, TPTaskInfoResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject getAppMsgPushSk(String enterpriseId){
        String path = "/tums/open/msgTranspond/v1/getAppMsgPushSk";
        JSONObject requestBody = new JSONObject();
        try {
            return sendPostRequest(enterpriseId, path, "{}", JSONObject.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject getAppMsgPushConfig(String enterpriseId){
        String path = "/tums/open/msgTranspond/v1/getAppMsgPushConfig";
        try {
            return sendPostRequest(enterpriseId, path, "{}", JSONObject.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean modifyDeviceStorageStrategy(String enterpriseId, DeviceDO device, String channelNo, SetStorageStrategyDTO param) {
        try {
            String path = "/vms/open/deviceConfig/v1/modifyDeviceRecordLoopInfo";
            JSONObject requestBody = new JSONObject();
            requestBody.put("qrCode", device.getDeviceId());
            requestBody.put("channelId", channelNo == null || Constants.ZERO_STR.equals(channelNo)  ? "1" : channelNo);
            requestBody.put("ifLoop", param.getIsLoopStorage());
            String payload = JSONObject.toJSONString(requestBody);
            sendPostRequest(enterpriseId, path, payload, JSONObject.class);
            return true;
        }catch (Exception e){
            log.info("修改存储策略异常",e);
        }
        return false;
    }

    public  <T> T sendPostRequest(String enterpriseId, String path, String body, Class<T> responseType) {
        TPBaseResponse tResponseEntity = null;
        int tryCount = 0;
        while ((Objects.isNull(tResponseEntity) || tResponseEntity.getError_code() != 0) && tryCount < 3){
            HttpHeaders headers = buildAuthorization(enterpriseId, "POST", path, body);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            String url = "https://" + HOST + path;
            log.info("sendPostRequest: url={}, entity={}", url, JSONObject.toJSONString(entity));
            tResponseEntity = restTemplate.postForObject(url, entity, TPBaseResponse.class);
            tryCount++;
        }
        if(Objects.nonNull(tResponseEntity) && tResponseEntity.getError_code() != 0){
            log.info("sendPostRequest: tryCount:{}, {}", tryCount, JSONObject.toJSONString(tResponseEntity));
            String jfyErrorMessage = ThirdPartyDeviceStatusCode.getTpLinkErrorMessage(tResponseEntity.getError_code());
            throw new ServiceException(ErrorCodeEnum.ERROR, jfyErrorMessage);
        }
        log.info("sendPostResponse: tryCount:{}, {}", tryCount, JSONObject.toJSONString(tResponseEntity));
        return JSONObject.parseObject(JSONObject.toJSONString(tResponseEntity.getResult()), responseType);
    }

}
