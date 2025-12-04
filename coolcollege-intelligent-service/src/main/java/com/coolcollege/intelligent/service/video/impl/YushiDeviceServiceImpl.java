package com.coolcollege.intelligent.service.video.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vcs.model.v20200515.UploadFileRequest;
import com.aliyuncs.vcs.model.v20200515.UploadFileResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceSceneEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceSourceEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.enums.video.AlarmTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.YushiHttpClient;
import com.coolcollege.intelligent.common.util.CoolListUtils;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.video.VideoEventRecordMapper;
import com.coolcollege.intelligent.model.callback.CallbackRequest;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.video.platform.yushi.DO.VideoEventRecordDO;
import com.coolcollege.intelligent.model.video.platform.yushi.DTO.YushiChannelDTO;
import com.coolcollege.intelligent.model.video.platform.yushi.response.*;
import com.coolcollege.intelligent.service.fileUpload.FileUploadService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.video.YushiDeviceService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2021-02-05 14:30
 */
@Slf4j
@Service
public class YushiDeviceServiceImpl implements YushiDeviceService {

    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private DeviceChannelMapper deviceChannelMapper;
    @Autowired
    private RedisUtilPool redisUtil;
    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private VideoEventRecordMapper videoEventRecordMapper;
    @Lazy
    @Autowired
    private StoreService storeService;
    @Autowired
    private FileUploadService fileUploadService;
    @Value("${yushi.url}")
    private String yushiUrl;

    private String getYushiToken(String accessKeyId, String accessKeySecret) {

        String url = yushiUrl + "/openapi/user/app/token/get";
        JSONObject json = new JSONObject();
        json.put("appId", accessKeyId);
        json.put("secretKey", accessKeySecret);
        String resultStr = YushiHttpClient.post(url, json);
        JSONObject result = JSONObject.parseObject(resultStr);
        return result.getJSONObject("data").getString("accessToken");
    }

    @Override
    public String getRedisToken(String eid) {

        String token = redisUtil.getString(getKey(eid));
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        SettingVO setting = enterpriseVideoSettingService.getSetting(eid, YunTypeEnum.YUSHIYUN, AccountTypeEnum.PLATFORM);
        token = getYushiToken(setting.getAccessKeyId(), setting.getSecret());
        if (StringUtils.isBlank(token)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "获取token失败，请联系销售人员配置！");
        }
        redisUtil.setString(getKey(eid), token, 3600);
        return token;
    }

    private String getKey(String eid) {
        return Constants.YUSHI_TOKEN + eid;
    }

    @Override
    public String getLiveUrl(String deviceId, String channelNo, String streamType, Integer streamIndex,
                             Long startTime, Long endTime, Integer recordTypes, String token) {

        String videourl = yushiUrl + "/openapi/cdn/video/get";
        JSONObject videoJson = new JSONObject();
        videoJson.put("channelNo", channelNo);
        videoJson.put("streamIndex", streamIndex);
        if (startTime != null) {
            videoJson.put("startTime", startTime);
            videoJson.put("endTime", endTime);
        }
        videoJson.put("deviceSerial", deviceId);
        videoJson.put("streamType", streamType);
        if (recordTypes != null) {
            videoJson.put("recordTypes", recordTypes);
        }
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        String s = YushiHttpClient.post(videourl, videoJson, header);
        JSONObject videoResult = JSONObject.parseObject(s);
        String url = videoResult.getJSONObject("data").getString("flvUrl");
        return url.replace("http://", "https://");
    }

    @Override
    public List<YonghuiDeviceResponse> getDeviceList(String eid) {

        String token = getRedisToken(eid);
        String url = yushiUrl + "/openapi/device/list";
        JSONObject json = new JSONObject();
        Integer pageNo = 1;
        Integer pageSize = 100;
        List<YonghuiDeviceResponse> deviceResponses = new ArrayList<>();
        json.put("pageNo", pageNo);
        json.put("pageSize", pageSize);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        String resultStr = YushiHttpClient.post(url, json, header);
        YonghuiResponse<YonghuiDeviceListResponse> yonghuiResponse = JSONObject.parseObject(resultStr,
                new TypeReference<YonghuiResponse<YonghuiDeviceListResponse>>() {
                });
        if (yonghuiResponse.getCode() == 200) {
            YonghuiDeviceListResponse data = yonghuiResponse.getData();
            Integer total = data.getTotal();
            if (total != 0) {
                deviceResponses.addAll(data.getDeviceList());
                //计算分页访问次数并循环访问
                int i = (total - 1) / pageSize + 1;
                while (i - pageNo > 0) {
                    pageNo = pageNo + 1;
                    List<YonghuiDeviceResponse> deviceListPage = getDeviceListPage(pageNo, pageSize, token);
                    if (CollectionUtils.isNotEmpty(deviceListPage)) {
                        deviceResponses.addAll(deviceListPage);
                    }
                }
            }
            return deviceResponses;
        }
        return deviceResponses;
    }

    private List<YonghuiDeviceResponse> getDeviceListPage(Integer pageNo, Integer pageSize, String token) {

        String url = yushiUrl + "/openapi/device/list";
        JSONObject json = new JSONObject();
        json.put("pageNo", pageNo);
        json.put("pageSize", pageSize);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        String resultStr = YushiHttpClient.post(url, json, header);
        YonghuiResponse<YonghuiDeviceListResponse> yonghuiResponse = JSONObject.parseObject(resultStr,
                new TypeReference<YonghuiResponse<YonghuiDeviceListResponse>>() {
                });
        return yonghuiResponse.getData().getDeviceList();
    }

    @Override
    public List<YushiChannelDTO> getChannelList(String eid, String deviceId) {

        String url = yushiUrl + "/openapi/device/channel/list";
        JSONObject json = new JSONObject();
        json.put("pageNo", 1);
        json.put("pageSize", 100);
        json.put("deviceSerial", deviceId);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", getRedisToken(eid));
        String resultStr = YushiHttpClient.post(url, json, header);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        String channelListStr = jsonObject.getJSONObject("data").getString("channelList");
        List<YushiChannelDTO> yushiChannelDTOList = JSONObject.parseObject(channelListStr,
                new TypeReference<List<YushiChannelDTO>>() {
                });
        ListUtils.emptyIfNull(yushiChannelDTOList)
                .forEach(data -> data.setParentDeviceId(deviceId));
        return yushiChannelDTOList;

    }

    @Override
    public Boolean ptzStart(String deviceId, Integer channelNo, Integer command, Integer speed, String token) {

        String url = yushiUrl + "/openapi/device/ptz/start";
        JSONObject json = new JSONObject();
        json.put("deviceSerial", deviceId);
        json.put("channelNo", channelNo == null ? 0 : channelNo);
        json.put("command", command);
        json.put("speed", speed);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        String resultStr = YushiHttpClient.post(url, json, header);
        return true;
    }

    @Override
    public Boolean ptzStop(String deviceId, Integer channelNo, String token) {

        String url = yushiUrl + "/openapi/device/ptz/stop";
        JSONObject json = new JSONObject();
        json.put("deviceSerial", deviceId);
        json.put("channelNo", channelNo == null ? 0 : channelNo);
        json.put("command", 1);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        String resultStr = YushiHttpClient.post(url, json, header);
        return true;
    }

    @Override
    public String capture(String deviceId, Integer channelNo, String token) {

        String url = yushiUrl + "/openapi/device/capture/get";
        JSONObject json = new JSONObject();
        json.put("deviceSerial", deviceId);
        json.put("channelNo", channelNo == null ? 0 : channelNo);
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", token);
        String resultStr = YushiHttpClient.post(url, json, header);
        JSONObject jsonObject = JSONObject.parseObject(resultStr);
        return jsonObject.getJSONObject("data").getString("url");
    }

}
