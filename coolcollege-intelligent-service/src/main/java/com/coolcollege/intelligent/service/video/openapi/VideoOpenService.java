package com.coolcollege.intelligent.service.video.openapi;

import com.coolcollege.intelligent.common.enums.device.DeviceEncryptEnum;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.device.vo.DeviceVideoRecordVO;
import com.coolcollege.intelligent.model.device.vo.DeviceSoftHardwareInfoVO;
import com.coolcollege.intelligent.model.device.vo.DeviceStorageInfoVO;
import com.coolcollege.intelligent.model.device.vo.DeviceTalkbackVO;
import com.coolcollege.intelligent.model.video.TaskFileDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.AppKeyDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.HikCloudAreasDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.PassengerDTO;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.HKPassengerFlowAttributesDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowConfigDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowSwitchStatusDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.YingshiDeviceKitPeoplecountingDTO;
import com.coolcollege.intelligent.model.video.vo.LiveVideoVO;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.VideoProtocolTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: VideoOpenService
 * @Description:
 * @date 2022-12-13 11:05
 */
public interface VideoOpenService {

    /**
     * 获取云类型
     * @return
     */
    YunTypeEnum getYunTypeNum();

    /**
     * 获取token
     * @param enterpriseId
     * @param accountType
     * @return
     */
    String getAccessToken(String enterpriseId, AccountTypeEnum accountType);

    /**
     * 删除token缓存
     */
    default void deleteAccessToken(String enterpriseId, String deviceId, AccountTypeEnum accountType) {
    }

    default String getAccessToken(String enterpriseId, AccountTypeEnum accountType, String deviceId) {
        return "";
    }

    /**
     * 获取设备详情
     * @param enterpriseId
     * @param deviceId
     * @param accountType
     * @return
     */
    OpenDeviceDTO getDeviceDetail(String enterpriseId, String deviceId, AccountTypeEnum accountType);

    default OpenDeviceDTO getDeviceDetail(String enterpriseId, String deviceId, AccountTypeEnum accountType, String username, String password) {
        return getDeviceDetail(enterpriseId, deviceId, accountType);
    }

    /**
     * 获取设备列表
     * @param enterpriseId
     * @param accountType
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<OpenDevicePageDTO> getDeviceList(String enterpriseId, AccountTypeEnum accountType, Integer pageNum, Integer pageSize);

    /**
    * @Description:  对设备进行解密
    * @Param: [deviceSerial, accountType]
    * @Author: tangziqi
    * @Date: 2023/5/30~14:01
    */
    default Boolean getDecode(String eid, String deviceSerial, AccountTypeEnum accountType){
        return null;
    }


    LiveVideoVO getLiveUrl(String enterpriseId, DeviceDO device, VideoDTO param);


    LiveVideoVO getPastVideoUrl(String enterpriseId, DeviceDO device, VideoDTO param);

    /**
     * 取消授权
     * @param enterpriseId
     * @param device
     */
    default void cancelAuth(String enterpriseId, DeviceDO device, List<DeviceChannelDO> channelList){

    }

    /**
     * 开始云台控制
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @param command 上0 下1 左2 右3 放大8 缩小9
     * @param speed
     * @param duration
     * @return
     */
    Boolean ptzStart(String enterpriseId, DeviceDO device, String channelNo, Integer command, Integer speed, Long duration);

    /**
     * 停止云台控制
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @return
     */
    Boolean ptzStop(String enterpriseId, DeviceDO device, String channelNo);

    /**
     * 新增预制点
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @param devicePositionName
     * @return 预制点id
     */
    String addPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String devicePositionName);


    /**
     * 删除预制点
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @param presetIndex
     * @return
     */
    Boolean deletePtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex);

    /**
     * 调用预制点
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @param presetIndex
     * @return
     */
    Boolean loadPtzPreset(String enterpriseId, DeviceDO device, String channelNo, String presetIndex);

    /**
     * 抓图
     * @param eid
     * @param device
     * @param channelNo
     * @param quality
     * @return
     */
    String capture(String eid ,DeviceDO device,String channelNo,String quality);

    /**
     * 创建时间点抓图任务，获取任务id
     * @param enterpriseId 企业id
     * @param deviceDO 设备
     * @param channelNo 通道号
     * @param captureTimes 时间点列表
     * @return 任务id
     */
    default String captureByTime(String enterpriseId, DeviceDO deviceDO, String channelNo, List<String> captureTimes) {
        throw new UnsupportedOperationException();
    }

    /**
     * 按时间点抓图，直接获取到图片url
     * @param enterpriseId 企业id
     * @param deviceDO 设备
     * @param channelNo
     * @param captureTimes
     * @return java.util.List<java.lang.String>
     */
    default List<CapturePictureDTO> captureByTimeDict(String enterpriseId, DeviceDO deviceDO, String channelNo, List<String> captureTimes) {
        return null;
    }

    /**
     * 获取客流统计配置信息
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @return
     */
    default PassengerFlowConfigDTO getPassengerFlowConfig(String enterpriseId, DeviceDO device, Integer channelNo){
        return null;
    }

    /**
     * 获取客流统计开关状态
     * @param enterpriseId
     * @param device
     * @return
     */
    default PassengerFlowSwitchStatusDTO passengerFlowSwitchStatus(String enterpriseId, DeviceDO device){
        return null;
    }

    /**
     * 设置客流统计开关
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @param enable
     * @return
     */
    default Boolean savePassengerFlow(String enterpriseId, DeviceDO device, String channelNo, Boolean enable){
        return null;
    }

    /**
     * 配置客流统计信息
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @param line
     * @param direction
     * @return
     */
    default Boolean savePassengerFlowConfig(String enterpriseId, DeviceDO device, String channelNo, String line, String direction){
        return null;
    }


    /**
     * 视频转码录制
     * @param enterpriseId
     * @param device
     * @param param
     * @return
     */
    String videoTransCode(String enterpriseId,DeviceDO device, VideoDTO param);


    /**
     * 接口用于查询指定云录制文件信息
     * @param enterpriseId
     * @param fileId
     * @return
     */
    VideoFileDTO getVideoFile(String enterpriseId,DeviceDO device,String fileId) ;


    /**
     * 该接口用于获取指定云录制文件的下载地址，在文件上传完成成功后调用
     * @param enterpriseId
     * @param fileId
     * @return
     */
    List<String> getVideoDownloadUrl(String enterpriseId,DeviceDO device,String fileId);

    /**
     * 根据任务id查询任务文件
     * @param enterpriseId 企业id
     * @param device 设备
     * @param taskId 任务id
     * @return 文件DTO列表
     */
    default List<TaskFileDTO> getTaskFile(String enterpriseId, DeviceDO device, String taskId) {
        return Collections.emptyList();
    }

    /**
     * 去流认证接口
     * @param eid
     * @param device
     * @return
     */
    default AppKeyDTO authentication(String eid, DeviceDO device){
        return null;
    };

    /**
     * 获取所有的一级菜单 不带分页
     * @param enterpriseId
     * @param accountType
     * @param pageNum
     * @param pageSize
     * @return
     */
    default List<HikCloudAreasDTO> getAllFirstNodeList(String enterpriseId ,AccountTypeEnum accountType,Integer pageNum,Integer pageSize){
        return Lists.newArrayList();
    };


    /**
     * 根据门店编号查询设备客流
     * @param eid
     * @param accountTypeEnum
     * @param dateTime
     * @param storeNo
     * @return
     */
    default List<PassengerDTO> getPassengerData(String eid ,AccountTypeEnum accountTypeEnum,String dateTime,String storeNo){
        return Lists.newArrayList();
    };

    /**
     * 查询指定门店客流属性数据
     * @param eid
     * @param accountTypeEnum
     * @param startTime
     * @param endTime
     * @param storeNo
     * @return
     */
    default List<HKPassengerFlowAttributesDTO> getPassengerAttributesData(String eid, AccountTypeEnum accountTypeEnum, String startTime, String endTime, String storeNo){
        return Lists.newArrayList();
    };

    default LiveVideoVO playbackSpeed(String enterpriseId, DeviceDO device, String channelNo, String streamId, String speed, VideoProtocolTypeEnum protocol){
        return new LiveVideoVO();
    };

    /**
     * 标记多维客流设备
     * @param enterpriseId
     * @param device
     * @param accountType
     * @return
     */
    default Boolean markDeviceKit(String enterpriseId, DeviceDO device, String channelNo, String storeNum, AccountTypeEnum accountType){
        return true;
    };

    default YingshiDeviceKitPeoplecountingDTO statisticPeoplecounting(String enterpriseId, DeviceDO device, String regionTag, String startTime, String endTime){
        return new YingshiDeviceKitPeoplecountingDTO();
    };

    /**
     * 画面翻转
     * @param enterpriseId 企业id
     * @param device 设备
     * @param configDTO 配置DTO
     * @return 是否成功
     */
    default Boolean pictureFlip(String enterpriseId, DeviceDO device, DeviceConfigDTO configDTO) {
        return false;
    }

    /**
     * 设备配置
     */
    default Boolean configureDevice(String enterpriseId, DeviceDO device, DeviceConfigDTO configDTO) {
        return false;
    }

    /**
     * 获取设备录像文件列表
     * @param enterpriseId 企业id
     * @param device 设备
     * @param channelNo 通道号
     * @param startTime 开始时间，毫秒时间戳
     * @param endTime 结束时间
     * @return 设备录像文件VO列表
     */
    default List<DeviceVideoRecordVO> listDeviceRecordByTime(String enterpriseId, DeviceDO device, String channelNo, Long startTime, Long endTime) {
        return Collections.emptyList();
    }

    /**
     * 设备对讲
     * @param enterpriseId 企业id
     * @param device 设备
     * @param talkbackDTO 设备对讲DTO
     * @return 设备对讲VO
     */
    default DeviceTalkbackVO deviceTalkback(String enterpriseId, DeviceDO device, DeviceTalkbackDTO talkbackDTO) {
        return null;
    }

    /**
     * 设备重启
     * @param enterpriseId 企业id
     * @param device 设备
     * @param channelNo 通道号
     * @return 是否成功
     */
    default Boolean deviceReboot(String enterpriseId, DeviceDO device, String channelNo) {
        return false;
    }

    /**
     * 设备存储信息查询
     * @param enterpriseId 企业id
     * @param device 设备
     * @return 设备存储信息VO
     */
    default List<DeviceStorageInfoVO> deviceStorageInfo(String enterpriseId, DeviceDO device) {
        return null;
    }

    /**
     * 设备存储格式化
     * @param enterpriseId 企业id
     * @param device 设备
     * @return 是否成功
     */
    default Boolean deviceStorageFormatting(String enterpriseId, DeviceDO device, String channelNo) {
        return false;
    }

    /**
     * 设备软硬件信息查询
     * @param enterpriseId 企业id
     * @param device 设备
     * @return 设备软硬件信息VO
     */
    default DeviceSoftHardwareInfoVO deviceSoftHardwareInfo(String enterpriseId, DeviceDO device) {
        return null;
    }

    /**
     * 更新视频编码类型
     * @param enterpriseId
     * @param device
     * @param vencType
     * @return
     */
    default Boolean updateVideoVencType(String enterpriseId, DeviceDO device, String vencType){return false;}

    /**
     * 修改设备存储策略
     * @param enterpriseId
     * @param device
     * @param channelNo
     * @param param
     */
    default Boolean modifyDeviceStorageStrategy(String enterpriseId, DeviceDO device, String channelNo, SetStorageStrategyDTO param) {
        return false;
    }

    default Boolean deviceEncrypt(String eid, DeviceDO device, String channelNo, DeviceEncryptEnum encryptEnum){
        return false;
    }
}
