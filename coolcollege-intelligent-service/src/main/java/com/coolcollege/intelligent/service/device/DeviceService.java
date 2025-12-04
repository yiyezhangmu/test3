package com.coolcollege.intelligent.service.device;

import com.coolcollege.intelligent.common.enums.patrol.CapturePictureTypeEnum;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.YingShiWebHookMessage;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.device.export.StoreDeviceExportEntity;
import com.coolcollege.intelligent.model.device.request.*;
import com.coolcollege.intelligent.model.device.vo.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStorePictureDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageInfo;

import javax.validation.constraints.NotBlank;
import java.util.List;

public interface DeviceService {

    DeviceInfoVO detail(String enterpriseId, String deviceId);

    PassengerConfigDTO passengerDetail(String eid, String deviceId);

    Boolean setPassengerConfig(String eid,PassengerConfigDTO passengerConfigDTO);


    Boolean addVideo(String enterpriseId, String userId, DeviceAddRequest request);
    /**
     * 获取设备列表
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    List<DeviceMappingDTO> deviceList(String enterpriseId, DeviceListRequest request,CurrentUser user);

    /**
     * 门店绑定设备
     *
     * @param enterpriseId
     * @param storeId
     * @param deviceId
     * @return
     */
    Boolean bind(String enterpriseId, List<String> storeId, List<String> deviceId);

    Boolean bindRegion(String enterpriseId,String vcsCorpId,String regionId);
    /**
     * 解绑设备
     *
     * @param enterpriseId
     * @param deviceIdList
     * @return
     */
    Boolean unbind(String enterpriseId, List<String>  deviceIdList);

        /**
         * 更新设备
         *
         * @param enterpriseId
         * @param request
         * @return
         */
    Boolean updateDevice(String enterpriseId, DeviceUpdateRequest request);


    /**
     * 更新设备通道号
     * @param enterpriseId
     * @param id
     * @param newName
     * @param hasPtz
     * @return
     */
    Boolean updateDeviceChannel(String enterpriseId, DeviceChannelUpdateRequest request);

    /**
     * 设备信息
     *
     * @param eid
     * @return
     */
    Boolean deleteDevice(String eid, List<String> deviceIdList, boolean isRemoteDelete);

    /**
     * 删除设备并取消外卖平台授权
     * @param eid 企业id
     * @param deviceList 设备id列表
     * @param isRemoteDelete 是否取消视频平台授权(解绑)
     * @return java.lang.Boolean
     */
    List<String> deleteDeviceAndCancelAuth(String eid, List<DeviceDeleteRequest> deviceList, boolean isRemoteDelete);

    /**
     * 获取导出模板的门店
     * @param eId
     * @return
     */
    List<StoreDeviceExportEntity> getExportStore(String eId);

    /**
     * 导出设备
     */
    ImportTaskDO getExportDevice(String eId, DeviceListRequest request, CurrentUser user);

    TbPatrolStorePictureDO beginCapture(String eid, Long businessId,
                                        Long id, Long deviceChannelId, String deviceId,
                                        String channelNo,
                                        Long storeSceneId, YunTypeEnum yunTypeEnum, CapturePictureTypeEnum capturePictureTypeEnum);

    /**
     * 刷新设备状态 公有账户与私有账户
     * @param eId
     * @return
     */
    Boolean refreshDeviceStatus(String eId, String dbName);

    DeviceSummaryDataDTO getDeviceSummaryData(String eId,CurrentUser user,DeviceReportSearchRequest request);

    /**
     * 保存图片到图片库
     * @param eId
     * @param request
     * @param user
     * @return
     */
    Boolean saveDeviceCapture(String eId, DeviceCaptureRequest request,CurrentUser user);

    /**
     * 删除图片
     * @param eId
     * @param deviceCaptureIds
     * @return
     */
    Boolean delDeviceCapture(String eId, List<Long> deviceCaptureIds);

    /**
     * 查询门店图片
     * @param eId
     * @param storeId
     * @param pageSize
     * @param pageNum
     * @return
     */
    PageInfo<DeviceCaptureLibDTO> listByStoreId(String eId, String storeId, Integer pageSize, Integer pageNum,String beginTime,String endTime);

    /**
     * 分组获取每个门店的设备在线数
     * @param eId
     * @param regionIds
     * @param user
     * @param keyword
     * @return
     */
    PageInfo<DeviceSummaryListDTO> getDeviceSummaryGroupStoreId(String eId, CurrentUser user, DeviceReportSearchRequest request);

    /**
     * 设备同步
     * @param enterpriseId
     * @param yunTypeEnum
     */
    void syncDevice(String enterpriseId, YunTypeEnum yunTypeEnum, String userId, String dbName);

    /**
     * 同步单个设备
     * @param enterpriseId 企业id
     * @param yunTypeEnum 设备类型
     * @param deviceId 设备序列号
     * @param storeId 门店id
     * @param deviceName 设备名称
     */
    boolean syncSingleDevice(String enterpriseId,
                             YunTypeEnum yunTypeEnum,
                             String deviceId,
                             String storeId,
                             AccountTypeEnum accountType,
                             String deviceName);

    /**
     * 获取云类型
     * @param enterpriseId
     * @return
     */
    List<DeviceYunTypeVO> getDeviceYunTypeList(String enterpriseId);

    /**
     * 设备汇总数据导出
     */
    ImportTaskDO ExportDeviceSummaryGroupStoreId(String eId, CurrentUser user, DeviceReportSearchRequest request);

    /**
     * 获取设备套餐信息
     * @param enterpriseId
     * @return
     */
    DevicePackageVO getDevicePackage(String enterpriseId);

    /**
     * 获取用户最近巡店的门店
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<LastPatrolStoreVO> getLastPatrolStore(String enterpriseId, String userId);

    /**
     * 设备下载中心
     * @param enterpriseId
     * @param request
     * @return
     */
    Boolean deviceDownloadCenter(String enterpriseId, DeviceDownloadCenterRequest request,CurrentUser user);

    /**
     * 删除下载中心视频
     * @param enterpriseId
     * @param id
     * @return
     */
    Boolean deletedVideoRecord(String enterpriseId,Long id);


    /**
     * 下载中心列表
     * @param enterpriseId
     * @param pageSize
     * @param pageNum
     * @param user
     * @return
     */
    PageInfo<DeviceDownloadCenterDTO> listDeviceDownloadCenter(String enterpriseId,Integer pageSize,Integer pageNum,CurrentUser user);


    String download(String enterpriseId,Long id);

    /**
     * 同步图片库
     * @param enterpriseId
     * @param fileUrl
     * @param deviceId
     * @param storeId
     * @param fileName
     * @param user
     * @return
     */
    Boolean syncCaptureLib(String enterpriseId, String fileUrl, String deviceId, String storeId,String fileName,CurrentUser user,Long id);

    /**
     * 获取设备数量和通道数量
     * @param enterpriseId
     * @return
     */
    Integer getDeviceCountAndChannelCount(String enterpriseId);

    /**
     * 查询设备状态
     * @param enterpriseId
     * @param deviceId
     * @param channelNo
     * @return
     */
    String getDeviceStatus(String enterpriseId,String deviceId,String channelNo);


    Integer refreshDeviceStatusByStore(String enterpriseId, String storeId);

    /**
     * 检查并更新萤石云录像回放下载任务状态
     * @param enterpriseId 企业id
     */
    void checkAndUpdateYingShiVideoDownloadTaskStatus(String enterpriseId);

    /**
     * 萤石云回调消息
     * @param deviceId
     */
    boolean callbackUpdateDeviceStatus(String deviceId);

    boolean callbackUpdateDeviceStatus(String enterpriseId, String dbName, String deviceId);

    /**
     * 获取accessToken
     * @param enterpriseId 企业id
     * @param accountType 账号类型
     * @param yunType 云类型
     * @param deviceId 设备id
     * @param refresh 刷新token
     * @return accessToken
     */
    String getAccessToken(String enterpriseId, AccountTypeEnum accountType, YunTypeEnum yunType, String deviceId, Boolean refresh);

    /**
     * 获取设备信息
     * @param enterpriseId
     * @param deviceId
     * @return
     */
    DeviceDO getDeviceByDeviceId(String enterpriseId, String deviceId);

    /**
     * 画面翻转
     * @param enterpriseId 企业id
     * @param configDTO 设备配置DTO
     * @return java.lang.Boolean
     */
    Boolean pictureFlip(String enterpriseId, DeviceConfigDTO configDTO);
    /**
     * 设备刷新
     * @param enterpriseId
     * @param deviceIdList
     * @param userId
     */
    void refreshDevice(String enterpriseId, List<String> deviceIdList, String userId);


    void refreshAllDevice(String enterpriseId);

    DeviceChannelVO getDeviceChannel(String enterpriseId, String deviceId, String channelNo);

    /**
     * 刪除通道
     * @param enterpriseId
     * @param request
     * @return
     */
    int deleteLocalChannel(String enterpriseId, DeleteChannelRequest request);

    /**
     * 查询设备录像文件列表
     * @param enterpriseId 企业id
     * @param request 请求对象
     * @return 设备录像文件VO列表
     */
    List<DeviceVideoRecordVO> listDeviceRecordByTime(String enterpriseId, DeviceRecordQueryRequest request);
    /**
     * 获取设备对讲url
     * @param enterpriseId 企业id
     * @param talkbackDTO 设备对讲DTO
     * @return 设备对讲VO
     */
    DeviceTalkbackVO deviceTalkback(String enterpriseId, DeviceTalkbackDTO talkbackDTO);

    /**
     * 设备重启
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @param channelNo 通道号
     * @return 是否成功
     */
    Boolean deviceReboot(String enterpriseId, String deviceId, String channelNo);

    /**
     * 设备存储信息查询
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @return 设备存储信息VO
     */
    List<DeviceStorageInfoVO> deviceStorageInfo(String enterpriseId, String deviceId);

    /**
     * 设备存储格式化
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @return 是否成功
     */
    Boolean deviceStorageFormatting(String enterpriseId, String deviceId, String channelNo);

    /**
     * 设备软硬件信息查询
     * @param enterpriseId 企业id
     * @param deviceId 设备id
     * @return 设备软硬件信息VO
     */
    DeviceSoftHardwareInfoVO deviceSoftHardwareInfo(String enterpriseId, String deviceId);

    /**
     * 更新视频码流
     * @param enterpriseId
     * @param deviceId
     * @param vencType
     * @return
     */
    Boolean updateVideoVencType(String enterpriseId, String deviceId, String vencType);
}
