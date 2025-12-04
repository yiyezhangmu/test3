package com.coolcollege.intelligent.service.video.openapi;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceEncryptEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.device.DevicePositionMapper;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiVideoDTO;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.DevicePositionDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.device.request.DeviceRecordQueryRequest;
import com.coolcollege.intelligent.model.device.vo.DevicePositionVO;
import com.coolcollege.intelligent.model.device.vo.DeviceVideoRecordVO;
import com.coolcollege.intelligent.model.device.vo.DeviceSoftHardwareInfoVO;
import com.coolcollege.intelligent.model.device.vo.DeviceStorageInfoVO;
import com.coolcollege.intelligent.model.device.vo.DeviceTalkbackVO;
import com.coolcollege.intelligent.model.video.TaskFileDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.AppKeyDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudAreasDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.PassengerDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.*;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolcollege.intelligent.service.video.openapi.impl.LeChengDeviceServiceImpl;
import com.coolcollege.intelligent.service.video.openapi.impl.WdzDeviceServiceImpl;
import com.coolcollege.intelligent.service.video.openapi.impl.*;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.VideoProtocolTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.coolstore.base.utils.CommonContextUtil;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: VideoServiceApi
 * @Description:
 * @date 2022-12-13 16:16
 */
@Service
public class VideoServiceApi {

    @Resource
    private DeviceMapper deviceMapper;
    @Resource
    private DeviceChannelMapper deviceChannelMapper;
    @Resource
    private DevicePositionMapper devicePositionMapper;

    VideoOpenService getVideoOpenService(YunTypeEnum yunType){
        switch (yunType){
            case HIKCLOUD:
                return CommonContextUtil.getBean(HikCloudOpenServiceImpl.class);
            case YUSHIYUN:
                return CommonContextUtil.getBean(YuShiOpenServiceImpl.class);
            case YINGSHIYUN:
                return CommonContextUtil.getBean("yingShiOpenServiceImpl", YingShiOpenServiceImpl.class);
            case YINGSHIYUN_GB:
                return CommonContextUtil.getBean("yingShiGbOpenServiceImpl", YingShiGbOpenServiceImpl.class);
            case YUNSHITONG:
                return CommonContextUtil.getBean(YunShiTongOpenServiceImpl.class);
            case MYJ:
                return CommonContextUtil.getBean(MYJVideoOpenServiceImpl.class);
            case IMOU:
                return CommonContextUtil.getBean(LeChengDeviceServiceImpl.class);
            case ALIYUN:
                throw new ServiceException(ErrorCodeEnum.DEVICE_TYPE_NOT_SUPPORT);
            case ULUCU:
                return CommonContextUtil.getBean(ULucuOpenServiceImpl.class);
            case WDZ:
                return CommonContextUtil.getBean(WdzDeviceServiceImpl.class);
            case JFY:
                return CommonContextUtil.getBean(JfyOpenServiceImpl.class);
            case TP_LINK:
                return CommonContextUtil.getBean(TPVideoOpenServiceImpl.class);
            default:
                throw new ServiceException(ErrorCodeEnum.DEAL_ERROR);
        }
    }

    public OpenDeviceDTO getDeviceDetail(String eid, String deviceId, YunTypeEnum yunType, AccountTypeEnum accountType){
        return getVideoOpenService(yunType).getDeviceDetail(eid, deviceId, accountType);
    }

    public OpenDeviceDTO getDeviceDetail(String eid, String deviceId, YunTypeEnum yunType, AccountTypeEnum accountType, String username, String password){
        return getVideoOpenService(yunType).getDeviceDetail(eid, deviceId, accountType, username, password);
    }

    public Boolean getDecode(String eid, String deviceSerial, YunTypeEnum yunType, AccountTypeEnum accountType){
        return getVideoOpenService(yunType).getDecode(eid, deviceSerial, accountType);
    }

    public OpenDeviceDTO getDeviceDetail(String enterpriseId, String deviceId){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).getDeviceDetail(enterpriseId, deviceId, AccountTypeEnum.getAccountType(device.getAccountType()));
    }

    /**
     * 获取设备列表
     * @param eid
     * @param yunType
     * @param accountType
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageInfo<OpenDevicePageDTO> getDeviceList(String eid, YunTypeEnum yunType, AccountTypeEnum accountType, Integer pageNum, Integer pageSize){
        return getVideoOpenService(yunType).getDeviceList(eid, accountType, pageNum, pageSize);
    }

    /**
     * 获取直播流
     * @param enterpriseId
     * @param param
     * @return
     */
    public LiveVideoVO getLiveUrl(String enterpriseId, VideoDTO param){
        if(StringUtils.isBlank(param.getChannelNo())){
            param.setChannelNo(Constants.ZERO_STR);
        }
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, param.getDeviceId());
        if(Objects.isNull(device)){
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_FOUND);
        }
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).getLiveUrl(enterpriseId, device, param);
    }

    /**
     * 获取回放
     * @param enterpriseId
     * @param param
     * @return
     */
    public LiveVideoVO getPastVideoUrl(String enterpriseId, VideoDTO param){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, param.getDeviceId());
        if(StringUtils.isBlank(param.getChannelNo())){
            param.setChannelNo(Constants.ZERO_STR);
        }
        if(StringUtils.isNotBlank(param.getStartTime()) && StringUtils.isNotBlank(param.getEndTime())){
            if (param.getStartTime().compareTo(param.getEndTime()) >= 0) {
                throw new ServiceException(ErrorCodeEnum.COM_CANNOT_START_GREATER_THAN_END);
            }
        }
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).getPastVideoUrl(enterpriseId, device, param);
    }

    /**
     * 取消授权
     * @param enterpriseId
     * @param device
     */
    public void cancelAuth(String enterpriseId, DeviceDO device){
        getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).cancelAuth(enterpriseId, device, null);
    }

    /**
     * 批量取消
     * @param enterpriseId
     * @param deviceList
     */
    public void cancelAuth(String enterpriseId, List<DeviceDO> deviceList, List<DeviceChannelDO> channelList){
        if(CollectionUtils.isEmpty(deviceList)){
            return;
        }
        for (DeviceDO device : deviceList) {
            getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).cancelAuth(enterpriseId, device, channelList);
        }
    }

    /**
     * 开始云台控制
     * @param enterpriseId
     * @param deviceId
     * @param channelNo
     * @param command
     * @param speed
     * @param duration
     * @return
     */
    public Boolean ptzStart(String enterpriseId, String deviceId, String channelNo, Integer command, Integer speed, Long duration){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).ptzStart(enterpriseId, device, channelNo, command, speed, duration);
    }

    /**
     * 停止云台控制
     * @param enterpriseId
     * @param deviceId
     * @param channelNo
     * @return
     */
    public Boolean ptzStop(String enterpriseId, String deviceId, String channelNo){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).ptzStop(enterpriseId, device, channelNo);
    }

    /**
     * 新增预制点
     * @param enterpriseId
     * @param deviceId
     * @param id
     * @param channelNo
     * @param devicePositionName
     * @return
     */
    public String addOrUpdatePtzPreset(String enterpriseId, String deviceId, Long id, String channelNo, String devicePositionName, String user){
        if(StringUtils.isAnyBlank(deviceId, devicePositionName)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        if(Objects.nonNull(id)){
            DevicePositionDO oldDevicePosition = devicePositionMapper.selectDevicePositionById(enterpriseId, id);
            if (Objects.isNull(oldDevicePosition)) {
                return null;
            }
            if (StringUtils.isNotBlank(devicePositionName)) {
                oldDevicePosition.setDevicePositionName(devicePositionName);
            }
            oldDevicePosition.setUpdateUser(user);
            devicePositionMapper.updateDevicePosition(enterpriseId, oldDevicePosition);
            return oldDevicePosition.getPositionIndex();
        }
        if(StringUtils.isBlank(channelNo)){
            channelNo = Constants.ZERO_STR;
        }
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        String positionIndex = getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).addPtzPreset(enterpriseId, device, channelNo, devicePositionName);
        DevicePositionDO devicePositionDO =new DevicePositionDO();
        devicePositionDO.setDeviceId(deviceId);
        devicePositionDO.setChannelNo(channelNo);
        devicePositionDO.setDevicePositionName(devicePositionName);
        devicePositionDO.setPositionIndex(positionIndex);
        devicePositionDO.setYunType(device.getResource());
        devicePositionDO.setCreateUser(user);
        devicePositionMapper.insertDevicePosition(enterpriseId, devicePositionDO);
        return positionIndex;
    }


    /**
     * 删除预制点
     * @param enterpriseId
     * @return
     */
    public Boolean deletePtzPreset(String enterpriseId, Long id){
        DevicePositionDO devicePosition = devicePositionMapper.selectDevicePositionById(enterpriseId, id);
        if(Objects.isNull(devicePosition)){
            return false;
        }
        devicePositionMapper.deleteDevicePosition(enterpriseId, id);
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, devicePosition.getDeviceId());
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).deletePtzPreset(enterpriseId, device, devicePosition.getChannelNo(), devicePosition.getPositionIndex());
    }

    /**
     * 调用预制点
     * @param enterpriseId
     * @return
     */
    public Boolean loadPtzPreset(String enterpriseId, Long id){
        DevicePositionDO devicePosition = devicePositionMapper.selectDevicePositionById(enterpriseId, id);
        if(Objects.isNull(devicePosition)){
            return false;
        }
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, devicePosition.getDeviceId());
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).loadPtzPreset(enterpriseId, device, devicePosition.getChannelNo(), devicePosition.getPositionIndex());
    }

    /**
     * 获取
     * @param eid
     * @param deviceId
     * @param channelNo
     * @return
     */
    public List<DevicePositionVO> getDevicePositionList(String eid, String deviceId, String channelNo){
        if (StringUtils.isBlank(deviceId)) {
            return null;
        }
        if(StringUtils.isBlank(channelNo)){
            channelNo = Constants.ZERO_STR;
        }
        List<DevicePositionDO> devicePositionDOList = devicePositionMapper.listDevicePositionByDeviceAndChannel(eid, deviceId, channelNo);
        return ListUtils.emptyIfNull(devicePositionDOList).stream().map(DevicePositionVO::convertVO).collect(Collectors.toList());
    }



    /**
     * 抓图
     * @param enterpriseId
     * @param deviceId
     * @param channelNo
     * @param quality
     * @return
     */
    public String capture(String enterpriseId ,String deviceId, String channelNo, String quality){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if(Objects.isNull(device)){
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_FOUND);
        }
        if(Objects.nonNull(device.getHasChildDevice()) && !device.getHasChildDevice()){
            //校验设备是否在线
            if(!DeviceStatusEnum.ONLINE.getCode().equals(device.getDeviceStatus())){
                throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_ONLINE);
            }
            checkSupportCapture(device.getExtendInfo());
        }else {
            DeviceChannelDO deviceChannelDO = deviceChannelMapper.selectDeviceChannelByParentId(enterpriseId, deviceId, channelNo);
            if (Objects.isNull(deviceChannelDO)) {
                throw new ServiceException(ErrorCodeEnum.CHANNEL_NOT_FOUND);
            }
            //校验通道是否在线
            if(!DeviceStatusEnum.ONLINE.getCode().equals(deviceChannelDO.getStatus())){
                throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_ONLINE);
            }
            //校验通道能力集是否支持抓拍
            checkSupportCapture(deviceChannelDO.getExtendInfo());
        }
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).capture(enterpriseId, device, channelNo, quality);
    }

    public void checkSupportCapture(String extendInfo) {
        //校验设备能力集是否支持抓拍
        if(StringUtils.isBlank(extendInfo)){
            return;
        }
        JSONObject extendInfoJson = JSONObject.parseObject(extendInfo);
        String deviceCapacityStr = extendInfoJson.getString(DeviceDO.ExtendInfoField.DEVICE_CAPACITY);
        if(StringUtils.isNotBlank(deviceCapacityStr)){
            DeviceCapacityDTO deviceCapacity = JSONObject.parseObject(deviceCapacityStr, DeviceCapacityDTO.class);
            if(Objects.nonNull(deviceCapacity)){
                if (deviceCapacity.getSupportCapture() == 0) {
                    throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_SUPPORT_CAPTURE);
                }
            }
        }
    }

    /**
     * 时间点抓图
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @param channelNo 通道号
     * @param captureTimes 时间点列表, yyyy-MM-dd HH:mm:ss
     * @return 任务id
     */
    public String captureByTime(String enterpriseId, String deviceId, String channelNo, List<String> captureTimes) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).captureByTime(enterpriseId, device, channelNo, captureTimes);
    }

    /**
     * 时间点抓图，直接返回抓图结果
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @param channelNo 通道号
     * @param captureTimes 时间点列表, yyyy-MM-dd HH:mm:ss
     * @return 抓拍图片DTO列表
     */
    public List<CapturePictureDTO> captureByTimeDict(String enterpriseId, String deviceId, String channelNo, List<String> captureTimes) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).captureByTimeDict(enterpriseId, device, channelNo, captureTimes);
    }

    /**
     * 获取客流统计配置信息
     * @param enterpriseId
     * @param deviceId
     * @param channelNo
     * @return
     */
    public PassengerFlowConfigDTO getPassengerFlowConfig(String enterpriseId, String deviceId, Integer channelNo){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).getPassengerFlowConfig(enterpriseId, device, channelNo);
    }

    /**
     * 获取客流统计开关状态
     * @param enterpriseId
     * @param deviceId
     * @return
     */
    public PassengerFlowSwitchStatusDTO passengerFlowSwitchStatus(String enterpriseId, String deviceId){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).passengerFlowSwitchStatus(enterpriseId, device);
    }

    /**
     * 设置客流统计开关
     * @param enterpriseId
     * @param deviceId
     * @param channelNo
     * @param enable
     * @return
     */
    public Boolean savePassengerFlow(String enterpriseId, String deviceId, String channelNo, Boolean enable){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).savePassengerFlow(enterpriseId, device, channelNo, enable);
    }

    /**
     * 配置客流统计信息
     * @param enterpriseId
     * @param deviceId
     * @param channelNo
     * @param line
     * @param direction
     * @return
     */
    public Boolean savePassengerFlowConfig(String enterpriseId, String deviceId, String channelNo, String line, String direction){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).savePassengerFlowConfig(enterpriseId, device, channelNo, line, direction);
    }


    /**
     * 视频转码录制
     * @param enterpriseId
     * @param param
     * @return
     */
    public String videoTransCode(String enterpriseId, VideoDTO param){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, param.getDeviceId());
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).videoTransCode(enterpriseId, device, param);
    }


    /**
     * 接口用于查询指定云录制文件信息
     * @param enterpriseId
     * @param fileId
     * @return
     */
    public VideoFileDTO getVideoFile(String enterpriseId,  String deviceId, String fileId) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).getVideoFile(enterpriseId, device, fileId);
    }

    /**
     * 根据任务id查询任务文件
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @param taskId 任务id
     * @return 文件DTO列表
     */
    public List<TaskFileDTO> getTaskFiles(String enterpriseId, String deviceId, String taskId) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).getTaskFile(enterpriseId, device, taskId);
    }


    /**
     * 该接口用于获取指定云录制文件的下载地址，在文件上传完成成功后调用
     * @param enterpriseId
     * @param fileId
     * @return
     */
    public List<String> getVideoDownloadUrl(String enterpriseId, String deviceId,String fileId){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).getVideoDownloadUrl(enterpriseId, device, fileId);
    }

    /**
     * 查询APPKEY
     * @param enterpriseId
     * @param deviceId
     * @return
     */
    public AppKeyDTO authentication(String enterpriseId, String deviceId){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).authentication(enterpriseId, device);
    }


    /**
     * 查询所有账号组织机构第一节点列表
     * @param eid
     * @param yunType
     * @param accountType
     * @param pageNum
     * @param pageSize
     * @return
     */
    public List<HikCloudAreasDTO> getAllFirstNodeList(String eid, YunTypeEnum yunType, AccountTypeEnum accountType, Integer pageNum, Integer pageSize){
        return getVideoOpenService(yunType).getAllFirstNodeList(eid, accountType, pageNum, pageSize);
    }

    /**
     * 根据门店编号查询客流数据
     * @param eid
     * @param yunType
     * @param accountType
     * @param dateTime
     * @param storeNo
     * @return
     */
    public List<PassengerDTO> getPassengerData(String eid, YunTypeEnum yunType, AccountTypeEnum accountType, String dateTime, String storeNo){
        return getVideoOpenService(yunType).getPassengerData(eid, accountType, dateTime, storeNo);
    }

    public List<HKPassengerFlowAttributesDTO> getPassengerAttributesData(String eid, YunTypeEnum yunType, AccountTypeEnum accountType, String startTime, String endTime, String storeNo){
        return getVideoOpenService(yunType).getPassengerAttributesData(eid, accountType, startTime, endTime, storeNo);
    }

    public LiveVideoVO playbackSpeed(String enterpriseId, String deviceId, String channelNo, String streamId, String speed, VideoProtocolTypeEnum protocol) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).playbackSpeed(enterpriseId, device, channelNo, streamId, speed, protocol);
    }

    public Boolean markDeviceKit(String enterpriseId, DeviceDO device, String channelNo, String storeNum, YunTypeEnum yunType, AccountTypeEnum accountType){
        return getVideoOpenService(yunType).markDeviceKit(enterpriseId, device, channelNo, storeNum, accountType);
    }

    public YingshiDeviceKitPeoplecountingDTO statisticPeoplecounting(String enterpriseId, DeviceDO device, String storeNum, String startTime, String endTime, YunTypeEnum yunType) {
        return getVideoOpenService(yunType).statisticPeoplecounting(enterpriseId, device, storeNum, startTime, endTime);
    }

    public LiveVideoVO getVideoInfo(String enterpriseId, OpenApiVideoDTO param) {
        if(!param.check()){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        VideoProtocolTypeEnum accountType = VideoProtocolTypeEnum.getProtocolType(param.getProtocol());
        VideoDTO requestParam = VideoDTO.builder()
                .deviceId(param.getDeviceId())
                .channelNo(param.getChannelNo())
                .startTime(param.getStartTime())
                .endTime(param.getEndTime())
                .protocol(accountType)
                .supportH265(param.getSupportH265())
                .quality(param.getQuality())
                .speed(param.getSpeed()).build();
        return this.getLiveUrl(enterpriseId, requestParam);
    }

    public LiveVideoVO getPastVideoInfo(String enterpriseId, OpenApiVideoDTO param) {
        if(!param.check()){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(StringUtils.isAnyBlank(param.getStartTime(), param.getEndTime())){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        Date startTime = DateUtil.parse(param.getStartTime(), DateUtils.DATE_FORMAT_SEC);
        Date endTime= DateUtil.parse(param.getEndTime(), DateUtils.DATE_FORMAT_SEC);
        if(DateUtils.dayBetween(startTime, endTime) >= 1){
            throw new ServiceException(ErrorCodeEnum.TIME_INTERVAL_LONG);
        }
        VideoProtocolTypeEnum protocolTypeEnum = VideoProtocolTypeEnum.getProtocolType(param.getProtocol());
        VideoDTO requestParam = VideoDTO.builder()
                .deviceId(param.getDeviceId())
                .channelNo(param.getChannelNo())
                .quality(param.getQuality())
                .startTime(param.getStartTime())
                .endTime(param.getEndTime())
                .protocol(protocolTypeEnum)
                .speed(param.getSpeed()).build();
        return this.getPastVideoUrl(enterpriseId, requestParam);
    }

    /**
     * 获取accessToken
     * @param enterpriseId 企业id
     * @param deviceId
     * @return java.lang.String
     */
    public String getAccessToken(String enterpriseId, AccountTypeEnum accountType, YunTypeEnum yunType, String deviceId) {
        if (YunTypeEnum.JFY.equals(yunType)) {
            return getVideoOpenService(yunType).getAccessToken(enterpriseId, accountType, deviceId);
        } else {
            return getVideoOpenService(yunType).getAccessToken(enterpriseId, accountType);
        }
    }

    /**
     * 删除accessToken缓存
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @param accountType 账号类型
     * @param yunType 云类型
     */
    public void deleteAccessToken(String enterpriseId, String deviceId, YunTypeEnum yunType, AccountTypeEnum accountType) {
        getVideoOpenService(yunType).deleteAccessToken(enterpriseId, deviceId, accountType);
    }

    /**
     * 画面翻转
     * @param enterpriseId 企业id
     * @param configDTO 设备配置DTO
     * @return 是否成功
     */
    public Boolean pictureFlip(String enterpriseId, DeviceConfigDTO configDTO) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, configDTO.getDeviceId());
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).pictureFlip(enterpriseId, device, configDTO);
    }

    /**
     * 设备配置
     * @param enterpriseId 企业id
     * @param configDTO 设备配置DTO
     * @return 是否成功
     */
    public Boolean configureDevice(String enterpriseId, DeviceConfigDTO configDTO) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, configDTO.getDeviceId());
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).configureDevice(enterpriseId, device, configDTO);
    }

    /**
     * 查询设备录像文件列表
     * @param enterpriseId 企业id
     * @param request 请求对象
     * @return 设备录像文件VO列表
     */
    public List<DeviceVideoRecordVO> listDeviceRecordByTime(String enterpriseId, DeviceRecordQueryRequest request){
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, request.getDeviceSerial());
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).listDeviceRecordByTime(enterpriseId, device, request.getChannelNo(), request.getStartTime(), request.getEndTime());
    }

    /**
     * 获取设备对讲url
     * @param enterpriseId 企业id
     * @param talkbackDTO 设备对讲DTO
     * @return 设备对讲VO
     */
    public DeviceTalkbackVO deviceTalkback(String enterpriseId, DeviceTalkbackDTO talkbackDTO) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, talkbackDTO.getDeviceId());
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).deviceTalkback(enterpriseId, device, talkbackDTO);
    }

    /**
     * 设备重启
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @param channelNo 通道号
     * @return 是否成功
     */
    public Boolean deviceReboot(String enterpriseId, String deviceId, String channelNo) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).deviceReboot(enterpriseId, device, channelNo);
    }

    /**
     * 设备存储信息查询
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @return 设备存储信息VO
     */
    public List<DeviceStorageInfoVO> deviceStorageInfo(String enterpriseId, String deviceId) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).deviceStorageInfo(enterpriseId, device);
    }

    /**
     * 设备存储格式化
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @return 是否成功
     */
    public Boolean deviceStorageFormatting(String enterpriseId, String deviceId, String channelNo) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).deviceStorageFormatting(enterpriseId, device, channelNo);
    }

    /**
     * 设备软硬件信息查询
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @return 设备软硬件信息VO
     */
    public DeviceSoftHardwareInfoVO deviceSoftHardwareInfo(String enterpriseId, String deviceId) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).deviceSoftHardwareInfo(enterpriseId, device);
    }

    public Boolean updateVideoVencType(String enterpriseId, String deviceId, String vencType) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if(Objects.isNull(device)){
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_FOUND);
        }
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).updateVideoVencType(enterpriseId, device, vencType);
    }

    public Boolean modifyDeviceStorageStrategy(String enterpriseId, String deviceId, String channelNo, SetStorageStrategyDTO param) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if(Objects.isNull(device)){
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_FOUND);
        }
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).modifyDeviceStorageStrategy(enterpriseId, device, channelNo, param);
    }

    public Boolean deviceEncrypt(String enterpriseId, String deviceId, String channelNo, DeviceEncryptEnum param) {
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if(Objects.isNull(device)){
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_FOUND);
        }
        return getVideoOpenService(YunTypeEnum.getByCode(device.getResource())).deviceEncrypt(enterpriseId, device, channelNo, param);
    }
}
