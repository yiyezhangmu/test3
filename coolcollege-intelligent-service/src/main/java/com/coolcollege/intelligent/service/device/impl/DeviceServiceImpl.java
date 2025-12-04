package com.coolcollege.intelligent.service.device.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.device.*;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.patrol.CapturePictureTypeEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.enums.video.UploadTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.CoolListUtils;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.device.*;
import com.coolcollege.intelligent.dao.device.dao.EnterpriseDeviceInfoDAO;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStorePictureMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.store.StoreDeviceMappingMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.mapper.device.EnterpriseAuthDeviceDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.device.*;
import com.coolcollege.intelligent.model.device.dto.*;
import com.coolcollege.intelligent.model.device.export.StoreDeviceExportEntity;
import com.coolcollege.intelligent.model.device.request.*;
import com.coolcollege.intelligent.model.device.vo.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enums.StoreStatusEnum;
import com.coolcollege.intelligent.model.fileUpload.FileUploadParam;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStorePictureDO;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthBaseVisualDTO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.setting.dto.EnterpriseVideoSettingDTO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.param.SmallVideoParam;
import com.coolcollege.intelligent.model.video.platform.hik.dto.VideoFileDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowConfigDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.PassengerFlowSwitchStatusDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.aliyun.AliyunService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.service.device.DeviceAuthService;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.fileUpload.FileUploadService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.setting.EnterpriseVideoSettingService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.video.YingshiDeviceService;
import com.coolcollege.intelligent.service.video.YushiDeviceService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.service.video.openapi.impl.YingShiGbOpenServiceImpl;
import com.coolcollege.intelligent.util.AESUtil;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.coolstore.base.enums.YunTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageSerializable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.Constants.DEFAULT_STORE_ID;

/**
 * @author wch
 * @ClassName DeviceServiceImpl
 * @Description 用一句话描述什么
 */
@Service(value = "deviceService")
@Slf4j
public class DeviceServiceImpl implements DeviceService {
    @Resource
    private DeviceMapper deviceMapper;
    @Lazy
    @Resource
    private StoreService storeService;
    @Autowired
    private RedisUtilPool redis;
    @Resource
    private StoreDao storeDao;
    @Resource
    private StoreDeviceMappingMapper storeDeviceMappingMapper;
    @Resource
    private StoreMapper storeMapper;
    @Autowired
    private AliyunService aliyunService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private AuthVisualService authVisualService;
    @Autowired
    private EnterpriseVideoSettingService enterpriseVideoSettingService;
    @Resource
    private DeviceChannelMapper deviceChannelMapper;
    @Resource
    private DeviceCaptureLibMapper deviceCaptureLibMapper;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private UserAuthMappingService userAuthMappingService;
    @Resource
    private TbPatrolStorePictureMapper tbPatrolStorePictureMapper;
    @Resource
    private FileUploadService fileUploadService;
    @Lazy
    @Resource
    private YushiDeviceService yushiDeviceService;
    @Lazy
    @Resource
    private YingshiDeviceService yingshiDeviceService;

    @Resource
    private VideoServiceApi videoServiceApi;
    @Resource
    private StoreSceneMapper storeSceneMapper;
    @Resource
    private EnterpriseMapper enterpriseMapper;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    DeviceDownloadCenterMapper deviceDownloadCenterMapper;
    @Resource
    private YingShiGbOpenServiceImpl yingShiOpenService;
    @Resource
    private EnterpriseDeviceInfoDAO enterpriseDeviceInfoDAO;
    @Resource
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private EnterpriseAuthDeviceDAO enterpriseAuthDeviceDAO;
    @Resource
    private DeviceAuthService deviceAuthService;
    @Resource
    private DeviceLicenseInfoMapper deviceLicenseInfoMapper;

    @Value("${device.encrypt.key}")
    private String encryptKey;

    @Override
    public DeviceInfoVO detail(String enterpriseId, String deviceId) {
        DeviceDO deviceDO = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if (Objects.isNull(deviceDO)) {
            return null;
        }
        DeviceInfoVO device = DeviceInfoVO.convertVO(deviceDO);
        StoreDTO storeByStoreId = storeMapper.getStoreByStoreId(enterpriseId, deviceDO.getBindStoreId());
        if (storeByStoreId != null) {
            device.setStoreName(storeByStoreId.getStoreName());
        }
        if(device.getHasChildDevice()){
            List<DeviceChannelDO> deviceChannel = deviceChannelMapper.getDeviceChannelByParentId(enterpriseId, deviceId);
            List<DeviceChannelVO> deviceChannelList = DeviceChannelVO.convertVO(deviceChannel);
            device.setChannelList(deviceChannelList);
        }
        return device;
    }

    @Override
    public PassengerConfigDTO passengerDetail(String eid, String deviceId) {
        PassengerConfigDTO passengerConfigDTO=new PassengerConfigDTO();
        PassengerFlowConfigDTO passengerFlowConfig = videoServiceApi.getPassengerFlowConfig(eid, deviceId, null);
        if(passengerFlowConfig!=null){
            passengerConfigDTO.setDirection(passengerFlowConfig.getDirection());
            passengerConfigDTO.setLine(passengerFlowConfig.getLine());
        }
        PassengerFlowSwitchStatusDTO passengerFlowSwitchStatusDTO = videoServiceApi.passengerFlowSwitchStatus(eid, deviceId);
        passengerConfigDTO.setDeviceId(deviceId);
        passengerConfigDTO.setEnable(passengerFlowSwitchStatusDTO.getEnable()==1);
        //同步设备是否开启客流分析
        deviceMapper.updateEnablePassengerByDeviceId(eid,passengerFlowSwitchStatusDTO.getEnable()==1,Collections.singletonList(deviceId));
        //图片抓拍
        String capture = videoServiceApi.capture(eid, deviceId, null, null);
        passengerConfigDTO.setDevicePicUrl(capture);
        return passengerConfigDTO;
    }

    @Override
    public Boolean setPassengerConfig(String eid, PassengerConfigDTO passengerConfigDTO) {

        if(passengerConfigDTO.getEnable()&&(passengerConfigDTO.getLine()==null||passengerConfigDTO.getDirection()==null)){
            throw new ServiceException(ErrorCodeEnum.PF_CONFIG_LING_NOT_EXIST);
        }
        if(passengerConfigDTO.getEnable()==null){
            throw new ServiceException(ErrorCodeEnum.PF_CONFIG_STATUS_NOTNULL);
        }
        videoServiceApi.savePassengerFlow(eid, passengerConfigDTO.getDeviceId(),null,passengerConfigDTO.getEnable());
        videoServiceApi.savePassengerFlowConfig(eid, passengerConfigDTO.getDeviceId(),null,
                JSONObject.toJSONString(passengerConfigDTO.getLine()), JSONObject.toJSONString(passengerConfigDTO.getDirection()));
        //同步设备是否开启客流分析
        deviceMapper.updateEnablePassengerByDeviceId(eid,passengerConfigDTO.getEnable(),Collections.singletonList(passengerConfigDTO.getDeviceId()));
        return true;
    }

    @Override
    public Boolean addVideo(String enterpriseId, String userId, DeviceAddRequest request) {
        String deviceId = request.getDeviceId();
        DeviceDO oldDeviceDO = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if (Objects.nonNull(oldDeviceDO)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "设备已经存在，请勿重复添加！");
        }
        Integer count = getDeviceCountAndChannelCount(enterpriseId);
        DataSourceHelper.reset();
        EnterpriseDO enterpriseDO = enterpriseMapper.selectById(enterpriseId);
        DataSourceHelper.changeToMy();
        Integer limitDeviceCount = Optional.ofNullable(enterpriseDO).map(EnterpriseDO::getLimitDeviceCount).orElse(Constants.ZERO);
        if(count >= limitDeviceCount){
            throw new ServiceException(ErrorCodeEnum.DEVICE_COUNT_LIMIT);
        }
        if (CollectionUtils.isEmpty(request.getStoreIds())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        StoreDTO storeDTO = storeMapper.getStoreBaseInfo(enterpriseId, request.getStoreIds().get(Constants.ZERO));
        if(Objects.isNull(storeDTO)){
            throw new ServiceException(ErrorCodeEnum.STORE_NOT_FIND);
        }
        AccountTypeEnum accountTypeEnum= AccountTypeEnum.PLATFORM;
        if (request.getAccountType()!=null){
            accountTypeEnum = request.getAccountType();
        }
        OpenDevicePageDTO targetDevice = getTargetDevice(enterpriseId, deviceId, request.getYunType(), accountTypeEnum);
        OpenDeviceDTO deviceDetail;
        //添加设备 默认是平台账号
        if (YunTypeEnum.JFY.equals(request.getYunType())) {
            deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, deviceId, request.getYunType(), accountTypeEnum, targetDevice.getUsername(), targetDevice.getPassword());
        } else {
            deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, deviceId, request.getYunType(), accountTypeEnum);
        }
        if(deviceDetail == null){
            throw new ServiceException(ErrorCodeEnum.DEVICE_GET_ERROR);
        }
        if(DeviceStatusEnum.OFFLINE.getCode().equals(deviceDetail.getDeviceStatus())){
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_ONLINE);
        }
        DeviceDO deviceDO = new DeviceDO();
        deviceDO.setDeviceId(deviceId);
        deviceDO.setDeviceName(request.getDeviceName());
        deviceDO.setRemark(request.getRemark());
        if (StringUtils.isBlank(request.getScene())) {
            deviceDO.setDeviceScene(DeviceSceneEnum.OTHER.getCode());
        } else {
            deviceDO.setDeviceScene(DeviceSceneEnum.getByCode(request.getScene()).getCode());
        }
        List<OpenChannelDTO> channelList = deviceDetail.getChannelList();
        deviceDO.setModel(deviceDetail.getModel());
        deviceDO.setDeviceStatus(deviceDetail.getDeviceStatus());
        deviceDO.setResource(request.getYunType().getCode());
        deviceDO.setSupportCapture(deviceDetail.getSupportCapture());
        deviceDO.setStoreSceneId(DEFAULT_STORE_ID);
        deviceDO.setSupportPassenger(deviceDetail.getSupportPassenger());
        deviceDO.setType(DeviceTypeEnum.DEVICE_VIDEO.getCode());
        deviceDO.setCreateName(userId);
        deviceDO.setCreateTime(System.currentTimeMillis());
        deviceDO.setBindStatus(true);
        deviceDO.setAccountType(accountTypeEnum.getCode());
        deviceDO.setBindStoreId(request.getStoreIds().get(Constants.ZERO));
        JSONObject extendInfo = new JSONObject();
        extendInfo.put(DeviceDO.ExtendInfoField.DEVICE_CAPACITY, deviceDetail.getDeviceCapacity());
        if (YunTypeEnum.JFY.equals(request.getYunType())) {
            extendInfo.put(DeviceDO.ExtendInfoField.USERNAME, targetDevice.getUsername());
            extendInfo.put(DeviceDO.ExtendInfoField.PASSWORD, targetDevice.getPassword());
        }
        deviceDO.setExtendInfo(JSONObject.toJSONString(extendInfo));
        //新增定时巡检 门店场景id
        deviceDO.setStoreSceneId(request.getStoreSceneId());
        deviceDO.setHasChildDevice(false);
        if (YunTypeEnum.HIKCLOUD.equals(request.getYunType())){
            deviceDO.setDataSourceId(deviceDetail.getDataSourceId());
        }
        if (StringUtils.isNotBlank(deviceDO.getModel()) && deviceDO.getModel().startsWith(DeviceModelEnum.ISAPI.getCode())) {
            deviceDO.setSupportPassenger(true);
            deviceDO.setResource(YunTypeEnum.YINGSHIYUN.getCode());
        }
        List<DeviceChannelDO> deviceChannelDOList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(channelList)){
            count = count + channelList.size();
            deviceDO.setHasChildDevice(true);
            List<DeviceChannelDO> collect = channelList.stream().map(channel -> mapDeviceChannelDO(deviceDO, channel)).collect(Collectors.toList());
            deviceChannelDOList.addAll(collect);
        }
        if(limitDeviceCount < count + Constants.INDEX_ONE){
            throw new ServiceException(ErrorCodeEnum.DEVICE_COUNT_LIMIT);
        }
        String bindStoreIds = request.getStoreIds().stream().collect(Collectors.joining(Constants.COMMA));
        deviceMapper.batchInsertOrUpdateDevices(enterpriseId, Collections.singletonList(deviceDO), DeviceTypeEnum.DEVICE_VIDEO.getCode());
        deviceMapper.bathUpdateDeviceBindStoreIds(enterpriseId,Collections.singletonList(deviceId), DateUtil.getTimestamp(), storeDTO,true,bindStoreIds);
        storeMapper.updateCamera(enterpriseId,request.getStoreIds(),Boolean.TRUE);
        if (CollectionUtils.isNotEmpty(deviceChannelDOList)) {
            deviceChannelMapper.batchInsertOrUpdateDeviceChannel(enterpriseId, deviceChannelDOList);
        }
        // 如果是 ISAPI协议 接入的设备 则标记多维客流设备
        if (StringUtils.isNotBlank(deviceDetail.getModel()) && deviceDetail.getModel().startsWith(DeviceModelEnum.ISAPI.getCode())) {
            String channelNo = "1";
            if (CollectionUtils.isNotEmpty(deviceChannelDOList)) {
                channelNo = deviceChannelDOList.get(0).getChannelNo();
            }
            videoServiceApi.markDeviceKit(enterpriseId, deviceDO, channelNo, storeDTO.getStoreNum(), request.getYunType(),accountTypeEnum);
        }
        // 更新设备license状态
        deviceLicenseInfoMapper.updateStatusNoNew(enterpriseId, Collections.singletonList(deviceId));
        List<EnterpriseDeviceInfoDO> enterpriseDeviceInfo = EnterpriseDeviceInfoDO.convertEnterpriseDeviceInfo(enterpriseId, deviceDO, deviceChannelDOList);
        DataSourceHelper.reset();
        enterpriseDeviceInfoDAO.batchInsertOrUpdate(enterpriseDeviceInfo);
        return true;
    }

    @Override
    public List<DeviceMappingDTO> deviceList(String enterpriseId, DeviceListRequest request,CurrentUser user) {
        //权限控制
        String userId = user.getUserId();
        AuthBaseVisualDTO baseAuth = authVisualService.baseAuth(enterpriseId, userId);
        List<String> authStoreIdList = baseAuth.getStoreIdList();
        List<String> authFullRegionPathList = baseAuth.getFullRegionPathList();

        Integer binStatusInteger = null;
        List<String> storeIdList = new ArrayList<>();
        //是否包含区域ID
        if (StringUtils.isNotBlank(request.getAreaId())) {
            List<StoreDO> storeDOList = storeMapper.listStoreByRegionId(enterpriseId, request.getAreaId());
            List<String> idList = ListUtils.emptyIfNull(storeDOList)
                    .stream()
                    .map(StoreDO::getStoreId)
                    .collect(Collectors.toList());
            storeIdList.addAll(idList);
            if (CollectionUtils.isEmpty(storeIdList)) {
                return null;
            }
        }
        if (StringUtils.isNotBlank(request.getStoreId())) {
            storeDao.checkStoreId(enterpriseId, Arrays.asList(request.getStoreId()));
            storeIdList.add(request.getStoreId());
        }
        if (StringUtils.equals(DeviceBindEnum.BIND.getCode(), request.getBindStatus())) {
            binStatusInteger = 1;
        }
        if (StringUtils.equals(DeviceBindEnum.UNBIND.getCode(), request.getBindStatus())) {
            binStatusInteger = 0;
        }
        //处理以设备为主的查询
        if (CollectionUtils.isEmpty(storeIdList)) {
            PageHelper.clearPage();
            //权限控制
            PageHelper.startPage(request.getPageNumber(), request.getPageSize());
            List<DeviceMappingDTO> device;
            if (baseAuth.getIsAllStore()) {
                device = deviceMapper.getDeviceListForNotStore(enterpriseId, request.getDeviceType(), request.getKeywords(), binStatusInteger, request.getDeviceStatus()
                        ,baseAuth.getIsAdmin(),authStoreIdList, authFullRegionPathList );
            } else {
                if (!baseAuth.getIsAllStore()&&CollectionUtils.isEmpty(authStoreIdList)&&CollectionUtils.isEmpty(authFullRegionPathList)) {
                    device = deviceMapper.getDeviceListForNotBind(enterpriseId, request.getDeviceType(), request.getKeywords(), binStatusInteger, request.getDeviceStatus());
                } else {
                    device = deviceMapper.getDeviceListForNotStore(enterpriseId, request.getDeviceType(), request.getKeywords(), binStatusInteger, request.getDeviceStatus()
                            ,baseAuth.getIsAdmin(),authStoreIdList, authFullRegionPathList);
                }
            }
            if (CollectionUtils.isEmpty(device)) {
                return Collections.emptyList();
            }
            Set<String> storeDeviceIdList = device
                    .stream()
                    .filter(data -> StringUtils.isNotBlank(data.getStoreId()))
                    .map(DeviceMappingDTO::getStoreId)
                    .collect(Collectors.toSet());
            device.forEach(x->{
                String storeIds = x.getStoreIds();
                if (StringUtils.isNotEmpty(storeIds)){
                    storeDeviceIdList.addAll(Arrays.asList(storeIds.split(Constants.COMMA)));
                }
            });
            if (CollectionUtils.isEmpty(storeDeviceIdList)) {
                this.facadeList(enterpriseId,device);
            }
            List<StoreDTO> storeListByStoreIds = storeMapper.getStoreListByStoreIds(enterpriseId, new ArrayList<>(storeDeviceIdList));
            Map<String, StoreDTO> storeMap = ListUtils.emptyIfNull(storeListByStoreIds)
                    .stream()
                    .collect(Collectors.toMap(StoreDTO::getStoreId, data -> data, (a, b) -> a));
            device.forEach(data -> {
                if (StringUtils.isNotBlank(data.getStoreId())) {
                    StoreDTO storeDTO = storeMap.get(data.getStoreId());
                    data.setStoreName(storeDTO == null ? null : storeDTO.getStoreName());
                    data.setStoreStatus(storeDTO == null ? null : storeDTO.getStoreStatus());
                    data.setStoreStatusName(storeDTO == null ? null : StoreStatusEnum.getName(storeDTO.getStoreStatus()));
                    data.setAliyunCorpId(storeDTO == null ? null : storeDTO.getAliyunCorpId());
                }
                if (StringUtils.isNotEmpty(data.getStoreIds())){
                    List<String> list = Arrays.asList(data.getStoreIds().split(Constants.COMMA));
                    List<StoreAreaDTO> storeAreaDTOS = new ArrayList<>();
                    list.forEach(x->{
                        StoreAreaDTO store = new StoreAreaDTO();
                        StoreDTO storeDTO = storeMap.getOrDefault(x,new StoreDTO());
                        store.setStoreId(x);
                        store.setStoreName(storeDTO.getStoreName());
                        store.setStoreStatus(storeDTO.getStoreStatus());
                        storeAreaDTOS.add(store);
                    });
                    data.setStoreAreaDTOS(storeAreaDTOS);
                }
            });
            return this.facadeList(enterpriseId,device);
        }
        PageHelper.clearPage();
        PageHelper.startPage(request.getPageNumber(), request.getPageSize());
        List<DeviceMappingDTO> deviceListForStore = deviceMapper.getDeviceListForStore(enterpriseId, storeIdList, request.getDeviceType(), request.getKeywords(), binStatusInteger, request.getDeviceStatus());
        return this.facadeList(enterpriseId,deviceListForStore);
    }


    @Override
    public Boolean bind(String enterpriseId, List<String> storeIds, List<String> deviceIdList) {
        //门店校验
        List<StoreDTO> storeDTOList= storeDao.checkStoreId(enterpriseId, storeIds);
        List<String> storeIdList = storeDTOList.stream().map(StoreDTO::getStoreId).collect(Collectors.toList());
        // 参数校验
        List<DeviceDO> deviceByDeviceIdList = deviceMapper.getDeviceByDeviceIdList(enterpriseId, deviceIdList);
        if (CollectionUtils.isEmpty(deviceByDeviceIdList)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "设备ID不正确");
        }
        List<DeviceDO> bindedList = deviceByDeviceIdList.stream().filter(e -> e.getBindStoreId() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(bindedList)) {
            String bindedDevice = bindedList.stream()
                    .map(e -> e.getDeviceName())
                    .collect(Collectors.joining(","));
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "设备已经被绑定:" + bindedDevice);
        }
        StoreDTO storeDTO = storeDTOList.get(0);
        String bindStoreIds = storeIdList.stream().collect(Collectors.joining(Constants.COMMA));
        //切分绑定B1和绑定摄像设备
        deviceMapper.bathUpdateDeviceBindStoreIds(enterpriseId, deviceIdList, DateUtil.getTimestamp(),storeDTO,true,bindStoreIds);
        storeMapper.updateCamera(enterpriseId,storeIdList,Boolean.TRUE);
        //更新设备license状态：已占用
        deviceLicenseInfoMapper.updateStatusNoNew(enterpriseId, deviceIdList);
        return true;
    }

    @Override
    public Boolean bindRegion(String enterpriseId, String vcsCorpId, String regionId) {

        List<String> deviceIdList = new ArrayList<>();
        if ("1".equals(regionId)) {
            List<DeviceMappingDTO> deviceListForStore = deviceMapper.getDeviceListForStore(enterpriseId, null, DeviceTypeEnum.DEVICE_VIDEO.getCode(), null, 1, null);
            if (CollectionUtils.isEmpty(deviceListForStore)) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "区域下没有设备！");
            }
            deviceIdList = deviceListForStore.stream()
                    .map(DeviceMappingDTO::getDeviceId)
                    .collect(Collectors.toList());
        } else {
            List<StoreDO> storeDOList = storeMapper.listStoreByRegionId(enterpriseId, regionId);
            if (CollectionUtils.isEmpty(storeDOList)) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "区域下没有门店！");
            }
            List<String> storeIdList = storeDOList.stream()
                    .map(StoreDO::getStoreId)
                    .collect(Collectors.toList());
            List<DeviceMappingDTO> deviceListForStore = deviceMapper.getDeviceListForStore(enterpriseId, storeIdList, DeviceTypeEnum.DEVICE_VIDEO.getCode(), null, 1, null);
            if (CollectionUtils.isEmpty(deviceListForStore)) {
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "区域下没有设备！");
            }
            deviceIdList = deviceListForStore.stream()
                    .map(DeviceMappingDTO::getDeviceId)
                    .collect(Collectors.toList());
        }
        SettingVO settingVO = enterpriseVideoSettingService.getSetting(enterpriseId,YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
        if (StringUtils.isBlank(settingVO.getRootVdsCorpId())) {
            String vdsCorpId = aliyunService.createVdsProject(enterpriseId + "_" + regionId);
            enterpriseVideoSettingService.updateVdsCorpId(settingVO.getId(), vdsCorpId);
        }
        aliyunService.bindDeviceToVds(enterpriseId, vcsCorpId, settingVO.getRootVdsCorpId(), deviceIdList);
        return true;
    }

    @Override
    public Boolean unbind(String enterpriseId, List<String> deviceIdList) {

        List<DeviceDO> deviceByDeviceList = deviceMapper.getDeviceByDeviceIdList(enterpriseId, deviceIdList);
        if (Objects.isNull(deviceByDeviceList)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "设备ID不正确！");
        }
        List<String> checkDeviceIdList = deviceByDeviceList.stream()
                .map(DeviceDO::getBindStoreId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(checkDeviceIdList)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "设备没有被绑定,无法解绑！");
        }
        unBindVideo(enterpriseId, deviceIdList, deviceByDeviceList);
        return Boolean.TRUE;
    }

    private void unBindVideo(String enterpriseId, List<String> deviceIdList,List<DeviceDO> deviceDOList) {

        Map<String, List<String>> storeDeviceMap = deviceDOList.stream()
                .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                .collect(Collectors.groupingBy(DeviceDO::getBindStoreId, Collectors.mapping(DeviceDO::getDeviceId, Collectors.toList())));
        List<String> storeIdList = new ArrayList<>();
        //一个设备配置了多个门店的情况下
        for (DeviceDO e : deviceDOList) {
            if (StringUtils.isNotEmpty(e.getBindStoreIds())) {
                List<String> list = Arrays.asList(e.getBindStoreIds().split(Constants.COMMA));
                storeIdList.addAll(list);
            }
        }
        List<StoreDTO> storeDOList = storeMapper.getStoreListByStoreIds(enterpriseId, storeIdList);
        Map<String, StoreDTO> storeMap = ListUtils.emptyIfNull(storeDOList)
                .stream()
                .collect(Collectors.toMap(StoreDTO::getStoreId, data -> data, (a, b) -> a));
        //切分不同的云台执行
        List<String> aliyunDeviceIdList = ListUtils.emptyIfNull(deviceDOList)
                .stream()
                .filter(data -> StringUtils.equals(YunTypeEnum.ALIYUN.getCode(), data.getResource()))
                .map(DeviceDO::getDeviceId)
                .collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(aliyunDeviceIdList)){
            SettingVO setting = enterpriseVideoSettingService.getSettingIncludeNull(enterpriseId,YunTypeEnum.ALIYUN, AccountTypeEnum.PLATFORM);
            storeDeviceMap.forEach((storeId, deviceList) -> {
                StoreDTO storeDTO = storeMap.get(storeId);
                List<String> aliyunDeviceList = ListUtils.emptyIfNull(deviceIdList)
                        .stream()
                        .filter(aliyunDeviceIdList::contains)
                        .collect(Collectors.toList());
                if (StringUtils.isNotBlank(storeDTO.getVdsCorpId())&&CollectionUtils.isNotEmpty(aliyunDeviceList)&&setting!=null) {
                    //解绑门店corp
                    if (StringUtils.isNotBlank(storeDTO.getVdsCorpId())) {
                        aliyunService.unbindDeviceToVds(storeDTO.getVdsCorpId(), aliyunDeviceList);
                    }
                    //解绑根节点 corp
                    if(StringUtils.isNotBlank(setting.getRootVdsCorpId())){
                        aliyunService.unbindDeviceToVds(setting.getRootVdsCorpId(), aliyunDeviceList);
                    }
                }
            });

        }
        //简化设备和门店的关系
        deviceMapper.bathUpdateDeviceBindStoreId(enterpriseId,deviceIdList,null,new StoreDTO(),false);
        //查询门店关联视频情况
        storeService.updateStoreCamera(enterpriseId,storeIdList);
    }

    @Override
    public Boolean updateDevice(String enterpriseId, DeviceUpdateRequest request) {
        /**
         *
         * 1.直接更新本地数据库
         * 摄像头设备更新
         * 1.是否更新名称 更新阿里云名称再更新本地
         * 2.直接更新本地场景和备注
         */
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, request.getDeviceId());
        if (Objects.isNull(device)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "设备不存在，请先前往应用内更新设备列表");
        }
        DeviceSceneEnum byCode = DeviceSceneEnum.getByCode(request.getScene());
        device.setDeviceName(request.getName());
        device.setRemark(request.getRemark());
        device.setUpdateTime(System.currentTimeMillis());
        device.setUpdateName(UserHolder.getUser().getUserId());
        // 定时巡检 新增门店场景
        device.setStoreSceneId(request.getStoreSceneId());
        if(request.getHasPtz()!=null){
            device.setHasPtz(request.getHasPtz());
        }
        if (byCode != null) {
            device.setDeviceScene(request.getScene());
        }
        device.setDeviceStatus(request.getDeviceStatus());
        deviceMapper.updateDevice(enterpriseId, device);
        //ipc同时更新channel
        if(device.getHasChildDevice() != null && !device.getHasChildDevice()){
            DeviceChannelDO deviceChannelDO = deviceChannelMapper.selectDeviceChannelByParentId(enterpriseId, device.getDeviceId(), Constants.ONE_STR);
            if (deviceChannelDO != null && deviceChannelDO.getDeviceId().equals(device.getDeviceId())) {
                deviceChannelMapper.updateDeviceChannelById(enterpriseId, deviceChannelDO.getId(), device.getDeviceName(), device.getHasPtz(),
                        device.getStoreSceneId(), device.getRemark(), null);
            }
        }
        // 如果是 ISAPI协议 接入的设备 则标记多维客流设备
        if (StringUtils.isNotBlank(device.getModel()) && device.getModel().startsWith(DeviceModelEnum.ISAPI.getCode())) {
            String channelNo = "1";
            StoreDTO storeDTO = storeService.getStoreByStoreId(enterpriseId, device.getBindStoreId());
            YunTypeEnum yunTypeEnum = YunTypeEnum.getByCode(device.getResource());
            AccountTypeEnum accountTypeEnum = AccountTypeEnum.getAccountType(device.getAccountType());
            videoServiceApi.markDeviceKit(enterpriseId, device, channelNo, storeDTO.getStoreNum(), yunTypeEnum, accountTypeEnum);
        }
        return true;
    }

    @Override
    public Boolean updateDeviceChannel(String enterpriseId, DeviceChannelUpdateRequest request) {
        Long id = request.getId();
        String channelName = request.getChannelName();
        Boolean hasPtz = request.getHasPtz();
        Long storeSceneId = request.getStoreSceneId();
        String remark = request.getRemark();
        DeviceChannelDO deviceChannelDO = deviceChannelMapper.selectDeviceChannelById(enterpriseId, id);
        if (Objects.isNull(deviceChannelDO)) {
            throw new ServiceException(ErrorCodeEnum.CHANNEL_NOT_FOUND);
        }
        deviceChannelMapper.updateDeviceChannelById(enterpriseId, id, channelName, hasPtz, storeSceneId, remark, request.getDeviceStatus());
        return true;
    }

    @Override
    //@Transactional(rollbackFor = Exception.class)
    public Boolean deleteDevice(String eid, List<String> deviceIdList, boolean isRemoteDelete) {
        if (CollectionUtils.isEmpty(deviceIdList)) {
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        List<DeviceDO> deviceList = deviceMapper.getDeviceByDeviceIdList(eid, deviceIdList);
        if (CollectionUtils.isEmpty(deviceList)) {
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_FOUND);
        }
        List<DeviceChannelDO> channelList = deviceChannelMapper.listDeviceChannelByDeviceId(eid, deviceIdList, null);
        List<DeviceDO> deviceMappingDOList = deviceList.stream().filter(e -> StringUtils.isNotBlank(e.getBindStoreId())).collect(Collectors.toList());
        deviceMapper.batchDeleteDevices(eid, deviceIdList, DeviceTypeEnum.DEVICE_VIDEO.getCode());
        deviceChannelMapper.batchDeleteDeviceChannelByDeviceId(eid, deviceIdList);
        List<String> storeIdList = deviceMappingDOList.stream().map(DeviceDO::getBindStoreId).distinct().collect(Collectors.toList());
        //更新门店是否有摄像头的字段
        List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(eid, storeIdList,null, null, null);
        List<String> bindDeviceStoreIdList = ListUtils.emptyIfNull(deviceByStoreIdList)
                .stream().map(DeviceDO::getBindStoreId).distinct().collect(Collectors.toList());
        List<String> reduceListThanList = CoolListUtils.getReduceaListThanbList(storeIdList, bindDeviceStoreIdList);
        if(CollectionUtils.isNotEmpty(reduceListThanList)){
            storeMapper.updateCamera(eid, reduceListThanList,false);
        }
        if(isRemoteDelete){
            videoServiceApi.cancelAuth(eid, deviceList, channelList);
        }
        return true;
    }

    @Override
    public List<String> deleteDeviceAndCancelAuth(String eid, List<DeviceDeleteRequest> deviceList, boolean isRemoteDelete) {
        if (CollectionUtils.isEmpty(deviceList)) {
            return Collections.emptyList();
        }
        List<DeviceDeleteRequest> ipcDevice = new ArrayList<>();
        List<DeviceDeleteRequest> channelDevice = new ArrayList<>();
        for (DeviceDeleteRequest device : deviceList) {
            if (StringUtils.isBlank(device.getChannelNo())) {
                ipcDevice.add(device);
            } else {
                channelDevice.add(device);
            }
        }
        List<String> allDeviceIds = new ArrayList<>(CollStreamUtil.toSet(deviceList, DeviceDeleteRequest::getDeviceId));
        DataSourceHelper.reset();
        List<EnterpriseAuthDeviceDO> deviceAuthList = enterpriseAuthDeviceDAO.getAuthDeviceListByDeviceIds(eid, allDeviceIds);
        Map<String, List<EnterpriseAuthDeviceDO>> authMap = CollStreamUtil.groupByKey(deviceAuthList, EnterpriseAuthDeviceDO::getDeviceId);
        DataSourceHelper.changeToMy();

        List<String> failedDeviceIds = new ArrayList<>();
        List<String> deleteDeviceIds = new ArrayList<>();
        // IPC设备删除
        deleteAndCancelAuthByIpcDevice(eid, ipcDevice, failedDeviceIds, deleteDeviceIds, authMap);
        // 通道删除
        deleteAndCancelAuthByChannel(eid, channelDevice, failedDeviceIds, deleteDeviceIds, authMap);
        // 删除设备
        if (CollectionUtils.isNotEmpty(deleteDeviceIds)) {
            deleteDevice(eid, deleteDeviceIds, isRemoteDelete);
        }
        return failedDeviceIds;
    }

    /**
     * 删除NVR下的通道
     * @param eid 企业id
     * @param channelDevice 通道列表
     * @param failedDeviceIds 失败设备id列表
     * @param deleteDeviceIds 待删除设备id列表
     * @param authMap 授权信息Map
     */
    private void deleteAndCancelAuthByChannel(String eid,
                                              List<DeviceDeleteRequest> channelDevice,
                                              List<String> failedDeviceIds,
                                              List<String> deleteDeviceIds,
                                              Map<String, List<EnterpriseAuthDeviceDO>> authMap) {
        if (CollectionUtils.isEmpty(channelDevice)) return;
        // NVR通道号删除
        Set<String> parentDeviceIds = CollStreamUtil.toSet(channelDevice, DeviceDeleteRequest::getDeviceId);
        List<DeviceChannelDO> channels = deviceChannelMapper.getByParentDeviceIds(eid, new ArrayList<>(parentDeviceIds), null);
        // 设备下所有通道号
        Map<String, Set<String>> channelMap = CollStreamUtil.groupBy(channels, DeviceChannelDO::getParentDeviceId, Collectors.mapping(DeviceChannelDO::getChannelNo, Collectors.toSet()));
        Map<String, Map<String, DeviceChannelDO>> channelDOMap = CollStreamUtil.groupBy(channels, DeviceChannelDO::getParentDeviceId, Collectors.toMap(DeviceChannelDO::getChannelNo, v -> v));
        List<Long> deleteChannelIds = new ArrayList<>();
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        for (DeviceDeleteRequest request : channelDevice) {
            Set<String> residueChannel = channelMap.getOrDefault(request.getDeviceId(), Collections.emptySet());
            try {
                if (residueChannel.contains(request.getChannelNo())) {
                    DeviceChannelDO deviceChannelDO = channelDOMap.get(request.getDeviceId()).get(request.getChannelNo());
                    List<EnterpriseAuthDeviceDO> authList = authMap.getOrDefault(request.getDeviceId(), Collections.emptyList())
                            .stream()
                            .filter(auth -> auth.getChannelNo().equals(request.getChannelNo()) && Constants.INDEX_ONE.equals(auth.getAuthStatus()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(authList)) {
                        authList.forEach(auth -> deviceAuthService.cancelDeviceAuth(eid, new DeviceCancelAuthDTO(auth.getAppId(), auth.getDeviceId(), auth.getChannelNo())));
                    }
                    // 如果删除了所有的通道则删除录像机
                    if (residueChannel.size() == 1) deleteDeviceIds.add(request.getDeviceId());
                    residueChannel.remove(request.getChannelNo());
                    deleteChannelIds.add(deviceChannelDO.getId());
                }
            } catch (Exception e) {
                log.info("设备取消授权失败, deviceId:{}, channelNo:{}, error:{}", request.getDeviceId(), request.getChannelNo(), e.getMessage());
                failedDeviceIds.add(request.getDeviceId());
            }
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        if (CollectionUtils.isNotEmpty(deleteChannelIds)) {
            deviceChannelMapper.batchDeleteDeviceChannelById(eid, deleteChannelIds);
        }
    }

    /**
     * 删除并取消ipc设备
     * @param eid 企业id
     * @param ipcDevice 设备列表
     * @param failedDeviceIds 失败设备id列表
     * @param deleteDeviceIds 待删除设备id列表
     * @param authMap 授权记录Map
     */
    private void deleteAndCancelAuthByIpcDevice(String eid,
                                                List<DeviceDeleteRequest> ipcDevice,
                                                List<String> failedDeviceIds,
                                                List<String> deleteDeviceIds,
                                                Map<String, List<EnterpriseAuthDeviceDO>> authMap) {
        if (CollectionUtils.isEmpty(ipcDevice)) return;
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        for (DeviceDeleteRequest request : ipcDevice) {
            List<EnterpriseAuthDeviceDO> authList = authMap.getOrDefault(request.getDeviceId(), Collections.emptyList())
                    .stream()
                    .filter(auth ->  Constants.INDEX_ONE.equals(auth.getAuthStatus()))
                    .collect(Collectors.toList());
            try {
                // 取消授权
                if (CollectionUtils.isNotEmpty(authList)) {
                    authList.forEach(auth -> deviceAuthService.cancelDeviceAuth(eid, new DeviceCancelAuthDTO(auth.getAppId(), auth.getDeviceId(), auth.getChannelNo())));
                }
                deleteDeviceIds.add(request.getDeviceId());
            } catch (Exception e) {
                log.info("设备取消授权失败, deviceId:{}, error:{}", request.getDeviceId(), e.getMessage());
                failedDeviceIds.add(request.getDeviceId());
            }
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
    }

    @Override
    public List<StoreDeviceExportEntity> getExportStore(String eId) {
        List<StoreDO> storeDOList = storeService.getAllStore(eId);
        List<StoreDeviceExportEntity> exportEntityList = storeDOList.stream()
                .map(storeDO -> {
                    StoreDeviceExportEntity entity = new StoreDeviceExportEntity();
                    entity.setStoreName(storeDO.getStoreName());
                    entity.setStoreId(storeDO.getStoreId());
                    return entity;
                }).collect(Collectors.toList());
        return exportEntityList;
    }

    @Override
    public ImportTaskDO getExportDevice(String eId,DeviceListRequest request,CurrentUser user) {
        // 查询导出数量，限流
        List<DeviceMappingDTO> deviceMappingDTOS = this.deviceList(eId, request,user);
        if (CollectionUtils.isEmpty(deviceMappingDTOS)) {
            throw new ServiceException("当前无记录可导出");
        }
        // 通过枚举获取文件名称
        String fileName = MessageFormat.format(ExportTemplateEnum.getByCode(ImportTaskConstant.DEVICE_LIST), DateUtil.format(new Date(), DateUtils.DATE_FORMAT_MINUTE));
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(eId, fileName, ImportTaskConstant.DEVICE_LIST);
        // 构造异步导出参数
        ExportDeviceRequest msg = new ExportDeviceRequest();
        msg.setEnterpriseId(eId);
        msg.setRequest(request);
        //msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setUser(user);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.DEVICE_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public TbPatrolStorePictureDO beginCapture(String eid, Long businessId,
                             Long id, Long deviceChannelId, String deviceId,
                             String channelNoStr,
                             Long storeSceneId, YunTypeEnum yunTypeEnum, CapturePictureTypeEnum capturePictureTypeEnum) {
        TbPatrolStorePictureDO patrolStorePictureDO = new TbPatrolStorePictureDO();
        patrolStorePictureDO.setDeviceId(id);
        patrolStorePictureDO.setDeviceChannelId(deviceChannelId);
        patrolStorePictureDO.setBusinessId(businessId);
        patrolStorePictureDO.setCreateTime(new Date());
        patrolStorePictureDO.setUpdateTime(new Date());
        patrolStorePictureDO.setStoreSceneId(storeSceneId == null ? 0 : storeSceneId);
        patrolStorePictureDO.setDeleted(false);
        patrolStorePictureDO.setCapturePictureType(capturePictureTypeEnum.getCode());
        //根据设备类型抓图

        String url = null;

        url = videoServiceApi.capture(eid, deviceId, channelNoStr, null);

        if(StringUtils.isNotBlank(url)){
            FileUploadParam fileUploadParam = fileUploadService.uploadBaseImage(url, eid, UserHolder.getUser().getAppType());
            patrolStorePictureDO.setPicture(fileUploadParam.getServer() + fileUploadParam.getFileNewName());
        }else {
            //抓拍失败默认图片
            patrolStorePictureDO.setPicture(Constants.DEFAULT_PICTURE_URL);
        }

        tbPatrolStorePictureMapper.insert(eid, patrolStorePictureDO);
        return patrolStorePictureDO;
    }

    @Override
    @Async("syncThreadPool")
    public Boolean refreshDeviceStatus(String eId, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        int pageSize = 100, pageNum = 0;
        boolean isContinue = true;
        while(isContinue){
            PageHelper.startPage(pageNum++, pageSize, false);
            //查询所有的摄像头
            List<DeviceDO> deviceDOS = deviceMapper.selectAllDevice(eId, DeviceTypeEnum.DEVICE_VIDEO.getCode());
            if(CollectionUtils.isEmpty(deviceDOS)){
                return true;
            }
            List<String> deviceIds = deviceDOS.stream().map(DeviceDO::getDeviceId).collect(Collectors.toList());
            List<DeviceChannelDO> deviceChannelDOS = deviceChannelMapper.listDeviceChannelByDeviceId(eId, deviceIds, null);
            Map<String, List<DeviceChannelDO>> channelMap = deviceChannelDOS.stream().collect(Collectors.groupingBy(DeviceChannelDO::getParentDeviceId));
            for (DeviceDO deviceDO : deviceDOS) {
                OpenDeviceDTO deviceDetail = null;
                try {
                    deviceDetail = videoServiceApi.getDeviceDetail(eId, deviceDO.getDeviceId());
                } catch (Exception e) {
                    log.info("获取设备详情", e);
                    continue;
                }
                if(Objects.isNull(deviceDetail)){
                    continue;
                }
                //如果设备状态不一致 ，更新设备状态
                if(!deviceDO.getDeviceStatus().equals(deviceDetail.getDeviceStatus())){
                    //更新设备状态 DeviceStatusEnum
                    deviceDO.setDeviceStatus(deviceDetail.getDeviceStatus());
                    deviceMapper.updateDeviceStatus(eId,deviceDO);
                }
                List<DeviceChannelDO> deviceChannels= channelMap.get(deviceDO.getDeviceId());
                List<OpenChannelDTO> channelList = deviceDetail.getChannelList();
                if (CollectionUtils.isNotEmpty(channelList)){
                    if(CollectionUtils.isEmpty(deviceChannels)){
                        List<DeviceChannelDO> addDeviceChannel = channelList.stream().map(channel -> mapDeviceChannelDO(deviceDO, channel)).collect(Collectors.toList());
                        if(CollectionUtils.isNotEmpty(addDeviceChannel)){
                            deviceChannelMapper.batchInsertOrUpdateDeviceChannel(eId, addDeviceChannel);
                        }
                    }else{
                        Map<String, DeviceChannelDO> deviceChannelMap = deviceChannels.stream().collect(Collectors.toMap(k -> k.getChannelNo(), Function.identity(), (k1, k2) -> k1));
                        List<String> deviceNosList = deviceChannels.stream().map(DeviceChannelDO::getChannelNo).collect(Collectors.toList());
                        List<String> remoteDeviceNos = channelList.stream().map(OpenChannelDTO::getChannelNo).collect(Collectors.toList());
                        deviceNosList.removeAll(remoteDeviceNos);
                        if(CollectionUtils.isNotEmpty(deviceNosList)){
                            deviceChannelMapper.batchDeleteChannelByChannelNo(eId, deviceDO.getDeviceId(), deviceNosList);
                        }
                        List<DeviceChannelDO> addChannel = new ArrayList<>();
                        for (OpenChannelDTO deviceChannelDO : channelList) {
                            DeviceChannelDO deviceChannel = deviceChannelMap.get(deviceChannelDO.getChannelNo());
                            if(Objects.isNull(deviceChannel)){
                                addChannel.add(mapDeviceChannelDO(deviceDO, deviceChannelDO));
                            }else{
                                //通道表中的deviceId是对应通道的通道ID 如果map没有对应的设备 说明设备下线或者异常状态码为0
                                deviceChannel.setStatus(deviceChannelDO.getStatus());
                            }
                        }
                        if(CollectionUtils.isNotEmpty(addChannel)){
                            deviceChannelMapper.batchInsertOrUpdateDeviceChannel(eId, addChannel);
                        }
                        //更新设备通道状态
                        deviceChannelMapper.updateDeviceChannelStatus(eId,deviceChannels);
                    }

                }
            }
            if (CollectionUtils.isEmpty(deviceDOS) || deviceDOS.size() < pageSize){
                isContinue = false;
            }
        }
        return true;
    }

    @Override
    public DeviceSummaryDataDTO getDeviceSummaryData(String eId, CurrentUser user, DeviceReportSearchRequest request) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        DeviceSummaryDataDTO deviceSummaryDataDTO =  new DeviceSummaryDataDTO();
        boolean isAdmin = sysRoleService.checkIsAdmin(eId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(eId, isAdmin, user.getUserId(), request.getRegionIds());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return deviceSummaryDataDTO;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        //查询单纯的IPC设备
        DeviceSummaryDataDTO deviceSummaryData = deviceMapper.getDeviceSummaryData(eId, request, regionPathList,Boolean.FALSE);

        DeviceSummaryDataDTO deviceSummary = deviceMapper.getDeviceSummaryData(eId, request, regionPathList,Boolean.TRUE);
        List<DeviceSummaryListDTO> deviceIdStoreIdsList = deviceMapper.getDeviceStoreIds(eId, request, regionPathList);
        if (CollectionUtils.isEmpty(deviceIdStoreIdsList)){
            deviceSummaryDataDTO.setIpcTotal(deviceSummaryData.getIpcTotal());
            deviceSummaryDataDTO.setIpcDeviceOnlineNum(deviceSummaryData.getIpcDeviceOnlineNum());
            deviceSummaryDataDTO.setIpcDeviceOfflineNum(deviceSummaryData.getIpcDeviceOfflineNum());
            return deviceSummaryDataDTO;
        }
        List<String> storeIds = deviceIdStoreIdsList.stream().map(DeviceSummaryListDTO::getStoreId).distinct().collect(Collectors.toList());
        List<String> deviceIdList = deviceMapper.getDeviceIdByStoreIds(eId, storeIds);
        //查询通道的数据 ipc数据
        DeviceSummaryDataDTO deviceChannelSummaryData = deviceChannelMapper.getDeviceChannelSummaryData(eId, deviceIdList);
        deviceSummaryData.setIpcDeviceOnlineNum(deviceSummaryData.getIpcDeviceOnlineNum()+deviceChannelSummaryData.getIpcDeviceOnlineNum());
        deviceSummaryData.setIpcDeviceOfflineNum(deviceSummaryData.getIpcDeviceOfflineNum()+deviceChannelSummaryData.getIpcDeviceOfflineNum());
        deviceSummaryData.setIpcTotal(deviceSummaryData.getIpcTotal()+deviceChannelSummaryData.getIpcTotal());
        if(Objects.nonNull(deviceSummary)){
            deviceSummaryData.setNvrTotal(deviceSummary.getIpcTotal());
            deviceSummaryData.setNvrDeviceOnlineNum(deviceSummary.getIpcDeviceOnlineNum());
            deviceSummaryData.setNvrDeviceOfflineNum(deviceSummary.getIpcDeviceOfflineNum());
        }
        return deviceSummaryData;
    }

    @Override
    public Boolean saveDeviceCapture(String eId, DeviceCaptureRequest request, CurrentUser user) {
        if (request.getStoreId()==null||request.getFileUrl()==null){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        DeviceCaptureLibDO deviceCaptureLibDO = new DeviceCaptureLibDO();
        deviceCaptureLibDO.setDeviceId(request.getDeviceId());
        deviceCaptureLibDO.setCreateTime(new Date());
        deviceCaptureLibDO.setStoreId(request.getStoreId());
        deviceCaptureLibDO.setCreateUserId(user.getUserId());
        deviceCaptureLibDO.setCreateUserName(user.getName());
        deviceCaptureLibDO.setFileType(request.getFileType());
        deviceCaptureLibDO.setFileUrl(request.getFileUrl());
        deviceCaptureLibDO.setName(request.getName());
        deviceCaptureLibDO.setSnapshotUrl(request.getSnapshotUrl());
        deviceCaptureLibMapper.insertSelective(deviceCaptureLibDO,eId);
        if("video".equals(request.getFileType())){
            deviceCaptureVideoHandel(request,deviceCaptureLibDO.getId(),eId);
        }
        return Boolean.TRUE;
    }


    /**
     *
     * @param request
     * @param enterpriseId
     */
    public void deviceCaptureVideoHandel(DeviceCaptureRequest request, Long id,String enterpriseId){

        if(StringUtils.isBlank(request.getFileUrl())){
            return;
        }
        SmallVideoDTO smallVideoDTO = JSONObject.parseObject(request.getFileUrl(), SmallVideoDTO.class);
        if(smallVideoDTO != null){
            String callbackCache;
            SmallVideoDTO smallVideoCache;
            SmallVideoParam smallVideoParam;
            //如果转码完成就不处理，直接修改
            if(smallVideoDTO.getStatus() != null && smallVideoDTO.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()){
                return;
            }
            callbackCache = redis.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideoDTO.getVideoId());
            if(StringUtils.isNotBlank(callbackCache)){
                smallVideoCache = JSONObject.parseObject(callbackCache,SmallVideoDTO.class);
                if(smallVideoCache !=null && smallVideoCache.getStatus() !=null && smallVideoCache.getStatus() >=3){
                    BeanUtils.copyProperties(smallVideoCache,smallVideoDTO);
                }else {
                    smallVideoParam = new SmallVideoParam();
                    setNotCompleteCache(smallVideoParam,smallVideoDTO,id,enterpriseId);
                }
            }else {
                smallVideoParam = new SmallVideoParam();
                setNotCompleteCache(smallVideoParam,smallVideoDTO,id,enterpriseId);
            }
        }
    }


    /**
     * 未完成缓存
     * @param smallVideoParam
     * @param smallVideo
     * @param id
     * @param enterpriseId
     */
    public void setNotCompleteCache(SmallVideoParam smallVideoParam,SmallVideoDTO smallVideo,Long id,String enterpriseId){
        smallVideoParam.setVideoId(smallVideo.getVideoId());
        smallVideoParam.setUploadType(UploadTypeEnum.DEVICE_CAPTURE.getValue());
        smallVideoParam.setBusinessId(id);
        smallVideoParam.setUploadTime(new Date());
        smallVideoParam.setEnterpriseId(enterpriseId);
        //存入未转码完成的map，vod回调的时候使用
        redis.hashSet(RedisConstant.VIDEO_NOT_COMPLETE_CACHE,smallVideo.getVideoId(),JSONObject.toJSONString(smallVideoParam));
    }



    @Override
    public Boolean delDeviceCapture(String eId,  List<Long> deviceCaptureIds) {
        if (CollectionUtils.isEmpty(deviceCaptureIds)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        deviceCaptureLibMapper.deleteByIds(deviceCaptureIds,eId);
        return Boolean.TRUE;
    }

    @Override
    public PageInfo<DeviceCaptureLibDTO> listByStoreId(String eId, String storeId, Integer pageSize, Integer pageNum,String beginTime,String endTime) {
        if (StringUtils.isEmpty(storeId)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        PageHelper.startPage(pageNum,pageSize);
        List<DeviceCaptureLibDO> deviceCaptureLibDOS = deviceCaptureLibMapper.listByStoreId(storeId, eId,beginTime,endTime);
        PageInfo<DeviceCaptureLibDO> deviceCaptureLibDOPageInfo = new PageInfo<>(deviceCaptureLibDOS);
        List<DeviceCaptureLibDTO> deviceCaptureLibDTOS = ListUtils.emptyIfNull(deviceCaptureLibDOS)
                .stream()
                .map(this::mapToDeviceCaptureLibDTO)
                .collect(Collectors.toList());
        PageInfo<DeviceCaptureLibDTO> deviceCaptureLibDTOPageInfo = new PageInfo<>(deviceCaptureLibDTOS);
        deviceCaptureLibDTOPageInfo.setTotal(deviceCaptureLibDOPageInfo.getTotal());
        return deviceCaptureLibDTOPageInfo;
    }

    @Override
    public PageInfo<DeviceSummaryListDTO> getDeviceSummaryGroupStoreId(String eId, CurrentUser user, DeviceReportSearchRequest request) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        List<String> regionIds = CollectionUtils.isEmpty(request.getRegionIds()) ? Lists.newArrayList() : request.getRegionIds();
        if (CollectionUtils.isNotEmpty(request.getStoreIds())){
            List<RegionDO> regionDOS = regionService.listRegionByStoreIds(eId, request.getStoreIds());
            List<String> regionIdList = new ArrayList<>();
            for (RegionDO regionDO : regionDOS) {
                regionIdList.add(String.valueOf(regionDO.getId()));
            }
            regionIds.addAll(regionIdList);
        }

        PageInfo<DeviceSummaryListDTO> result = new PageInfo<>();
        boolean isAdmin = sysRoleService.checkIsAdmin(eId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(eId, isAdmin, user.getUserId(), regionIds);
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return result;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        Integer totalSize = deviceMapper.deviceSummaryGroupStoreIdNum(eId, request, regionPathList);
        result.setTotal(totalSize);
        PageHelper.startPage(request.getPageNum(), request.getPageSize(), false);
        List<DeviceSummaryListDTO> deviceSummaryListDTOS = deviceMapper.getDeviceSummaryGroupStoreId(eId, request, regionPathList);
        List<String> storeIdList = deviceSummaryListDTOS.stream().map(DeviceSummaryListDTO::getStoreId).collect(Collectors.toList());
        List<DeviceDTO> deviceList = storeMapper.getStoreDeviceList(eId, storeIdList);
        Map<String, List<DeviceDTO>> storeIdDeviceMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(deviceList)){
            storeService.buildDeviceChannel(eId,deviceList);
            storeIdDeviceMap = deviceList.stream().collect(Collectors.groupingBy(data -> data.getStoreId()));
        }
        for (DeviceSummaryListDTO deviceSummaryListDTO:deviceSummaryListDTOS) {
            List<DeviceDTO> list = storeIdDeviceMap.getOrDefault(deviceSummaryListDTO.getStoreId(), Lists.newArrayList());
            if (CollectionUtils.isEmpty(list)){
                continue;
            }
            for (DeviceDTO dto:list) {
                if(Objects.nonNull(dto.getHasChildDevice()) && dto.getHasChildDevice()){
                    List<ChannelDTO> channelList = dto.getChannelList();
                    if (CollectionUtils.isNotEmpty(channelList)){
                        deviceSummaryListDTO.setIpcTotal(deviceSummaryListDTO.getIpcTotal()+(CollectionUtils.isNotEmpty(channelList)?channelList.size():0));
                        deviceSummaryListDTO.setIpcDeviceOnlineNum( channelList.stream().filter(x -> DeviceStatusEnum.ONLINE.getCode().equals(x.getStatus())).collect(Collectors.toList()).size()+deviceSummaryListDTO.getIpcDeviceOnlineNum());
                        deviceSummaryListDTO.setIpcDeviceOfflineNum(channelList.stream().filter(x -> DeviceStatusEnum.OFFLINE.getCode().equals(x.getStatus())).collect(Collectors.toList()).size()+deviceSummaryListDTO.getIpcDeviceOfflineNum());
                    }
                }

            }
            deviceSummaryListDTO.setIpcDeviceOnlineRate(new BigDecimal(deviceSummaryListDTO.getIpcDeviceOnlineNum()).divide(new BigDecimal(deviceSummaryListDTO.getIpcTotal()),2,BigDecimal.ROUND_HALF_UP));
        }
        List<StoreDO> storeDoList = storeDao.getByStoreIdList(eId, storeIdList);
        Map<String, String> storeMap = storeDoList.stream().filter(x->StringUtils.isNotEmpty(x.getAvatar())).collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getAvatar));
        List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(storeDoList)
                .stream()
                .map(data->{
                    StorePathDTO storePathDTO =new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionPath());
                    return storePathDTO;
                })
                .collect(Collectors.toList());
        Map<String, String> fullRegionNameMap = regionService.getFullRegionName(eId, storePathDTOList);
        for (DeviceSummaryListDTO deviceSummaryListDTO:deviceSummaryListDTOS) {
            deviceSummaryListDTO.setAllRegionName(fullRegionNameMap.get(deviceSummaryListDTO.getStoreId()));
            deviceSummaryListDTO.setAvatar(storeMap.getOrDefault(deviceSummaryListDTO.getStoreId(),""));
        }
        result.setList(deviceSummaryListDTOS);
        return result;
    }

    @Override
    public ImportTaskDO ExportDeviceSummaryGroupStoreId(String eId, CurrentUser user, DeviceReportSearchRequest request) {
        // 查询导出数量，限流
        Integer count = this.deviceSummaryGroupStoreIdNum(eId, user, request);
        if (count==0) {
            throw new ServiceException("当前无记录可导出");
        }
        // 通过枚举获取文件名称
        String fileName = MessageFormat.format(ExportTemplateEnum.getByCode(ImportTaskConstant.DEVICE_SUMMARY_LIST), DateUtil.format(new Date(), DateUtils.DATE_FORMAT_MINUTE));
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(eId, fileName, ImportTaskConstant.DEVICE_SUMMARY_LIST);
        // 构造异步导出参数
        ExportDeviceSummaryRequest msg = new ExportDeviceSummaryRequest();
        msg.setEnterpriseId(eId);
        msg.setRequest(request);
        msg.setTotalNum(count.longValue());
        msg.setImportTaskDO(importTaskDO);
        msg.setUser(user);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.DEVICE_SUMMARY_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public DevicePackageVO getDevicePackage(String enterpriseId) {
        Integer deviceCountAndChannelCount = getDeviceCountAndChannelCount(enterpriseId);
        DataSourceHelper.reset();
        EnterpriseDO enterpriseDO = enterpriseMapper.selectById(enterpriseId);
        Integer limitDeviceCount = Optional.ofNullable(enterpriseDO).map(o->o.getLimitDeviceCount()).orElse(Constants.ZERO);
        return new DevicePackageVO(limitDeviceCount, deviceCountAndChannelCount);
    }

    @Override
    public List<LastPatrolStoreVO> getLastPatrolStore(String enterpriseId, String userId) {
        List<String> storeIds = tbPatrolStoreRecordMapper.getLastSevenDayPatrolStoreIds(enterpriseId, userId);
        if(CollectionUtils.isEmpty(storeIds)){
            return Lists.newArrayList();
        }
        List<StoreDO> storeNames = storeMapper.getStoreNameByIds(enterpriseId, storeIds);
        List<String> deviceStoreIds = deviceMapper.getDeviceByStoreIds(enterpriseId, storeIds);
        Map<String, StoreDO> storeMap = storeNames.stream().collect(Collectors.toMap(k -> k.getStoreId(), Function.identity()));
        List<LastPatrolStoreVO> resultList = new ArrayList<>();
        for (String storeId : storeIds) {
            StoreDO storeDO = storeMap.get(storeId);
            if(Objects.isNull(storeDO)){
                continue;
            }
            LastPatrolStoreVO lastPatrolStore = new LastPatrolStoreVO(storeId, storeDO.getStoreName(), storeDO.getStoreNum(), deviceStoreIds.contains(storeId));
            resultList.add(lastPatrolStore);
        }
        return resultList;
    }

    @Override
    public Boolean deviceDownloadCenter(String enterpriseId, DeviceDownloadCenterRequest request,CurrentUser user) {
        if(Objects.isNull(request.getStartTime()) || Objects.isNull(request.getEndTime()) || Objects.isNull(request.getDeviceId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        //数据入库
        DeviceDownloadCenterDO deviceDownloadCenterDO = new DeviceDownloadCenterDO();
        deviceDownloadCenterDO.setDeviceId(request.getDeviceId());
        deviceDownloadCenterDO.setStoreId(request.getStoreId());
        deviceDownloadCenterDO.setName(request.getFileName());
        deviceDownloadCenterDO.setStartTime(request.getStartTime());
        deviceDownloadCenterDO.setEndTime(request.getEndTime());
        deviceDownloadCenterDO.setDuration((request.getEndTime().getTime() - request.getStartTime().getTime()) / Constants.THOUSAND);
        deviceDownloadCenterDO.setCreateUserId(user.getUserId());
        deviceDownloadCenterDO.setCreateUserName(user.getName());
        deviceDownloadCenterDO.setCreateTime(new Date());
        deviceDownloadCenterDO.setStatus(1);
        String startTime = DateUtils.dateConvertString(request.getStartTime());
        String endTime = DateUtils.dateConvertString(request.getEndTime());
        //通过接口调用设备开始录制视频
        VideoDTO param = VideoDTO.builder().deviceId(request.getDeviceId()).channelNo(request.getChannelNo()).startTime(startTime).endTime(endTime).protocol(request.getProtocolTypeEnum()).build();
        String fileId = videoServiceApi.videoTransCode(enterpriseId, param);
        if (StringUtils.isEmpty(fileId)){
            //失败
            deviceDownloadCenterDO.setStatus(2);
        } else {
            DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, request.getDeviceId());
            // 万店掌、杰峰云接口直接返回url
            if (YunTypeEnum.WDZ.getCode().equals(device.getResource()) || YunTypeEnum.JFY.getCode().equals(device.getResource())) {
                deviceDownloadCenterDO.setFileUrl(fileId);
                deviceDownloadCenterDO.setStatus(0);
            } else {
                deviceDownloadCenterDO.setFileId(fileId);
            }
        }

        deviceDownloadCenterMapper.insertSelective(deviceDownloadCenterDO,enterpriseId);
        if (StringUtils.isEmpty(fileId)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean deletedVideoRecord(String enterpriseId, Long id) {
       if (id!=null){
           deviceDownloadCenterMapper.deleteByPrimaryKey(id,enterpriseId);
       }
       return Boolean.TRUE;
    }

    @Override
    public PageInfo<DeviceDownloadCenterDTO> listDeviceDownloadCenter(String enterpriseId,Integer pageSize,Integer pageNum, CurrentUser user) {
        PageHelper.startPage(pageNum,pageSize);
        List<DeviceDownloadCenterDO> deviceDownloadCenterDOS = deviceDownloadCenterMapper.listByUserId(enterpriseId, user.getUserId());
        PageInfo<DeviceDownloadCenterDO> deviceDownloadCenterDOPageInfo = new PageInfo<>(deviceDownloadCenterDOS);
        List<DeviceDownloadCenterDTO> result = new ArrayList<>();
        for (DeviceDownloadCenterDO deviceDownloadCenterDO:deviceDownloadCenterDOS) {
            DeviceDownloadCenterDTO deviceDownloadCenterDTO = new DeviceDownloadCenterDTO();
            deviceDownloadCenterDTO.setId(deviceDownloadCenterDO.getId());
            deviceDownloadCenterDTO.setDuration(deviceDownloadCenterDO.getDuration());
            deviceDownloadCenterDTO.setFileId(deviceDownloadCenterDO.getFileId());
            deviceDownloadCenterDTO.setStartTime(deviceDownloadCenterDO.getStartTime());
            deviceDownloadCenterDTO.setFileUrl(deviceDownloadCenterDO.getFileUrl());
            deviceDownloadCenterDTO.setStatus(deviceDownloadCenterDO.getStatus());
            deviceDownloadCenterDTO.setName(deviceDownloadCenterDO.getName());
            deviceDownloadCenterDTO.setStoreId(deviceDownloadCenterDO.getStoreId());
            deviceDownloadCenterDTO.setDeviceId(deviceDownloadCenterDO.getDeviceId());
            deviceDownloadCenterDTO.setIsSyncCaptureLib(deviceDownloadCenterDO.getIsSyncCaptureLib());
            deviceDownloadCenterDTO.setErrorMsg(deviceDownloadCenterDO.getErrorMsg());
            result.add(deviceDownloadCenterDTO);
        }
        PageInfo<DeviceDownloadCenterDTO> centerDTOPageInfo = new PageInfo<>();
        centerDTOPageInfo.setList(result);
        centerDTOPageInfo.setTotal(deviceDownloadCenterDOPageInfo.getTotal());
        return centerDTOPageInfo;
    }

    @Override
    public String download(String enterpriseId, Long id) {
        DeviceDownloadCenterDO deviceDownloadCenterDO = deviceDownloadCenterMapper.selectByPrimaryKey(id, enterpriseId);
        DeviceDO device = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceDownloadCenterDO.getDeviceId());
        // 萤石云链接过期时间2小时，每次重新获取
        if (StringUtils.isNotEmpty(deviceDownloadCenterDO.getFileUrl()) && !(YunTypeEnum.YINGSHIYUN_GB.getCode().equals(device.getResource()) || YunTypeEnum.YINGSHIYUN.getCode().equals(device.getResource()))){
            return deviceDownloadCenterDO.getFileUrl();
        }
        //如果fileUrl没值 则需要远程获取
        VideoFileDTO videoFile = videoServiceApi.getVideoFile(enterpriseId, deviceDownloadCenterDO.getDeviceId(), deviceDownloadCenterDO.getFileId());
        if (ObjectUtil.isNull(videoFile)) return "";
        Integer status  = 0;
        String fileUrl  = "";
        DeviceDownloadCenterDO deviceDownloadCenter = new DeviceDownloadCenterDO();
        if (videoFile.getStatus()==0){
            // 正常 继续拿数据
            List<String> videoDownloadUrl = videoServiceApi.getVideoDownloadUrl(enterpriseId, deviceDownloadCenterDO.getDeviceId(), deviceDownloadCenterDO.getFileId());
            if (CollectionUtils.isNotEmpty(videoDownloadUrl)){
                fileUrl = CollectionUtil.join(videoDownloadUrl, ",");
            }
        }else if (videoFile.getStatus()==1){
            status = 1;
            //上传中 抛异常
        }else {
            //上传失败
            status = 2;
        }
        deviceDownloadCenter.setStatus(status);
        deviceDownloadCenter.setFileUrl(fileUrl);
        deviceDownloadCenter.setId(deviceDownloadCenterDO.getId());
        deviceDownloadCenter.setErrorMsg(videoFile.getErrorMsg());
        deviceDownloadCenterMapper.updateByPrimaryKeySelective(deviceDownloadCenter,enterpriseId);
        if (deviceDownloadCenter.getStatus()==1){
            throw new ServiceException(ErrorCodeEnum.VIDEO_UPLOADING);
        }else if (deviceDownloadCenter.getStatus()==2){
            if (StringUtils.isNotBlank(videoFile.getErrorMsg())) {
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000001, videoFile.getErrorMsg());
            } else {
                throw new ServiceException(ErrorCodeEnum.VIDEO_UPLOAD_FAIL);
            }
        }
        return fileUrl;
    }

    @Override
    public Boolean syncCaptureLib(String enterpriseId, String fileUrl, String deviceId, String storeId,String fileName,CurrentUser user,Long id) {
        DeviceCaptureLibDO deviceCaptureLibDO = new DeviceCaptureLibDO();
        deviceCaptureLibDO.setDeviceId(deviceId);
        deviceCaptureLibDO.setCreateTime(new Date());
        deviceCaptureLibDO.setStoreId(storeId);
        deviceCaptureLibDO.setCreateUserId(user.getUserId());
        deviceCaptureLibDO.setCreateUserName(user.getName());
        deviceCaptureLibDO.setFileType("video");
        //封装数据
        SmallVideoDTO smallVideoDTO = new SmallVideoDTO();
        smallVideoDTO.setStatus(3);
        smallVideoDTO.setSnapShotStatus(Boolean.TRUE);
        smallVideoDTO.setTransCodeStatus(Boolean.TRUE);
        smallVideoDTO.setTransCodeStatus(Boolean.TRUE);
        smallVideoDTO.setVideoUrl(fileUrl);
        smallVideoDTO.setVideoId("");
        deviceCaptureLibDO.setFileUrl(JSONObject.toJSONString(smallVideoDTO));
        deviceCaptureLibDO.setName(fileName);
        deviceCaptureLibMapper.insertSelective(deviceCaptureLibDO,enterpriseId);
        DeviceDownloadCenterDO deviceDownloadCenterDO = new DeviceDownloadCenterDO();
        deviceDownloadCenterDO.setId(id);
        deviceDownloadCenterDO.setIsSyncCaptureLib(1);
        deviceDownloadCenterMapper.updateByPrimaryKeySelective(deviceDownloadCenterDO,enterpriseId);
        return Boolean.TRUE;
    }

    @Override
    public Integer getDeviceCountAndChannelCount(String enterpriseId) {
        Integer count = deviceMapper.count(enterpriseId);
        Integer deviceChannelCount = deviceChannelMapper.getDeviceChannelCount(enterpriseId);
        return count + deviceChannelCount;
    }

    @Override
    public String getDeviceStatus(String enterpriseId, String deviceId, String channelNo) {
        OpenDeviceDTO deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, deviceId);
        if(Objects.isNull(deviceDetail)){
            throw new ServiceException(ErrorCodeEnum.DEVICE_GET_ERROR);
        }
        if (CollectionUtils.isEmpty(deviceDetail.getChannelList())){
            DeviceDO deviceDO = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
            if (StringUtils.isEmpty(deviceDO.getDeviceStatus())||(StringUtils.isNotEmpty(deviceDO.getDeviceStatus())&&!deviceDO.getDeviceStatus().equals(deviceDetail.getDeviceStatus()))){
                deviceDO.setDeviceStatus(deviceDetail.getDeviceStatus());
                deviceMapper.updateDeviceStatus(enterpriseId,deviceDO);
            }
            return deviceDetail.getDeviceStatus();
        }else {
            for (OpenChannelDTO openChannelDTO:deviceDetail.getChannelList()) {
                if (openChannelDTO.getChannelNo()!=null&&openChannelDTO.getChannelNo().equals(channelNo)){
                    DeviceChannelDO deviceChannelDO = deviceChannelMapper.selectDeviceChannelByParentId(enterpriseId, deviceId, String.valueOf(channelNo));
                    if (Objects.nonNull(deviceChannelDO)){
                        if (StringUtils.isEmpty(deviceChannelDO.getStatus())||(StringUtils.isNotEmpty(deviceChannelDO.getStatus())&&!deviceChannelDO.getStatus().equals(openChannelDTO.getStatus()))){
                            deviceChannelDO.setStatus(openChannelDTO.getStatus());
                            deviceChannelMapper.updateDeviceChannelStatus(enterpriseId,Arrays.asList(deviceChannelDO));
                        }
                    }
                    return openChannelDTO.getStatus();
                }
            }
        }
        return null;
    }

    @Override
    public Integer refreshDeviceStatusByStore(String enterpriseId, String storeId) {
        List<DeviceDO> deviceList = deviceMapper.getDeviceIdByStoreId(enterpriseId, storeId);
        if(CollectionUtils.isEmpty(deviceList)){
            return Constants.ZERO;
        }
        List<String> deviceIds = deviceList.stream().map(DeviceDO::getDeviceId).collect(Collectors.toList());
        List<DeviceChannelDO> deviceChannelDOS = deviceChannelMapper.listDeviceChannelByDeviceId(enterpriseId, deviceIds, null);
        Map<String, List<DeviceChannelDO>> channelMap = deviceChannelDOS.stream().collect(Collectors.groupingBy(DeviceChannelDO::getParentDeviceId));
        for (DeviceDO deviceDO : deviceList) {
            OpenDeviceDTO deviceDetail = null;
            try {
                deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, deviceDO.getDeviceId());
            } catch (Exception e) {
                log.info("获取设备详情异常", e);
                continue;
            }
            if(Objects.isNull(deviceDetail)){
                continue;
            }
            //更新设备状态 DeviceStatusEnum
            deviceDO.setDeviceStatus(deviceDetail.getDeviceStatus());
            deviceMapper.updateDeviceStatus(enterpriseId,deviceDO);
            List<DeviceChannelDO> deviceChannels= channelMap.get(deviceDO.getDeviceId());
            List<OpenChannelDTO> channelList = deviceDetail.getChannelList();
            if (CollectionUtils.isNotEmpty(channelList)){
                if(CollectionUtils.isEmpty(deviceChannels)){
                    List<DeviceChannelDO> addDeviceChannel = channelList.stream().map(channel -> mapDeviceChannelDO(deviceDO, channel)).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(addDeviceChannel)){
                        deviceChannelMapper.batchInsertOrUpdateDeviceChannel(enterpriseId, addDeviceChannel);
                    }
                }else{
                    Map<String, DeviceChannelDO> deviceChannelMap = deviceChannels.stream().collect(Collectors.toMap(k -> k.getChannelNo(), Function.identity(), (k1, k2) -> k1));
                    List<String> deviceNosList = deviceChannels.stream().map(DeviceChannelDO::getChannelNo).collect(Collectors.toList());
                    List<String> remoteDeviceNos = channelList.stream().map(OpenChannelDTO::getChannelNo).collect(Collectors.toList());
                    deviceNosList.removeAll(remoteDeviceNos);
                    if(CollectionUtils.isNotEmpty(deviceNosList)){
                        deviceChannelMapper.batchDeleteChannelByChannelNo(enterpriseId, deviceDO.getDeviceId(), deviceNosList);
                    }
                    List<DeviceChannelDO> addChannel = new ArrayList<>();
                    for (OpenChannelDTO deviceChannelDO : channelList) {
                        DeviceChannelDO deviceChannel = deviceChannelMap.get(deviceChannelDO.getChannelNo());
                        if(Objects.isNull(deviceChannel)){
                            addChannel.add(mapDeviceChannelDO(deviceDO, deviceChannelDO));
                        }else{
                            //通道表中的deviceId是对应通道的通道ID 如果map没有对应的设备 说明设备下线或者异常状态码为0
                            deviceChannel.setStatus(deviceChannelDO.getStatus());
                        }
                    }
                    if(CollectionUtils.isNotEmpty(addChannel)){
                        deviceChannelMapper.batchInsertOrUpdateDeviceChannel(enterpriseId, addChannel);
                    }
                    //更新设备通道状态
                    deviceChannelMapper.updateDeviceChannelStatus(enterpriseId,deviceChannels);
                }
            }
        }
        return null;
    }

    @Override
    public void checkAndUpdateYingShiVideoDownloadTaskStatus(String enterpriseId) {
        log.info("开始检查并更新回放录像下载状态，enterpriseId：{}", enterpriseId);
        // 进行中的记录
        List<DeviceDownloadCenterDO> deviceDownloadCenterDOS = deviceDownloadCenterMapper.selectOngoingRecords(enterpriseId);
        Set<String> deviceIds = CollStreamUtil.toSet(deviceDownloadCenterDOS, DeviceDownloadCenterDO::getDeviceId);
        if (CollectionUtils.isNotEmpty(deviceIds)) {
            List<DeviceDO> deviceList = deviceMapper.getDeviceByDeviceIdList(enterpriseId, ListUtil.toList(deviceIds));
            Map<String, DeviceDO> deviceMap = CollStreamUtil.toMap(deviceList, DeviceDO::getDeviceId, v -> v);
            for (DeviceDownloadCenterDO deviceDownloadCenterDO : deviceDownloadCenterDOS) {
                try {
                    checkAndUpdateYingShiVideoDownloadTaskStatus(enterpriseId, deviceMap.get(deviceDownloadCenterDO.getDeviceId()), deviceDownloadCenterDO);
                } catch (Exception e) {
                    log.info("回放录像下载状态更新失败，downloadId：{}", deviceDownloadCenterDO.getId());
                }
            }
        }
    }

    @Override
    public boolean callbackUpdateDeviceStatus(String deviceId) {
        DataSourceHelper.reset();
        List<String> enterpriseIds = enterpriseDeviceInfoDAO.getEnterpriseIdsByDeviceId(deviceId);
        if(CollectionUtils.isEmpty(enterpriseIds)){
            return false;
        }
        List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
        if(CollectionUtils.isEmpty(enterpriseConfigList)){
            return false;
        }
        for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
            callbackUpdateDeviceStatus(enterpriseConfig.getEnterpriseId(), enterpriseConfig.getDbName(), deviceId);
        }
        return true;
    }

    @Override
    public boolean callbackUpdateDeviceStatus(String enterpriseId, String dbName, String deviceId) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        DeviceDO deviceDO = deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
        if (Objects.isNull(deviceDO)) {
            return false;
        }
        List<DeviceChannelDO> deviceChannelDOS = deviceChannelMapper.listDeviceChannelByDeviceId(enterpriseId, Collections.singletonList(deviceDO.getDeviceId()), null);
        Map<String, List<DeviceChannelDO>> channelMap = deviceChannelDOS.stream().collect(Collectors.groupingBy(DeviceChannelDO::getParentDeviceId));

        OpenDeviceDTO deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, deviceDO.getDeviceId());
        if(Objects.isNull(deviceDetail)){
            return false;
        }
        //如果设备状态不一致 ，更新设备状态
        if(!deviceDO.getDeviceStatus().equals(deviceDetail.getDeviceStatus())){
            //更新设备状态 DeviceStatusEnum
            deviceDO.setDeviceStatus(deviceDetail.getDeviceStatus());
            deviceMapper.updateDeviceStatus(enterpriseId,deviceDO);
        }
        List<DeviceChannelDO> deviceChannels= channelMap.get(deviceDO.getDeviceId());
        List<OpenChannelDTO> channelList = deviceDetail.getChannelList();
        if (CollectionUtils.isNotEmpty(channelList)){
            if(CollectionUtils.isEmpty(deviceChannels)){
                List<DeviceChannelDO> addDeviceChannel = channelList.stream().map(channel -> mapDeviceChannelDO(deviceDO, channel)).collect(Collectors.toList());
                if(CollectionUtils.isNotEmpty(addDeviceChannel)){
                    deviceChannelMapper.batchInsertOrUpdateDeviceChannel(enterpriseId, addDeviceChannel);
                }
            }else{
                Map<String, DeviceChannelDO> deviceChannelMap = deviceChannels.stream().collect(Collectors.toMap(DeviceChannelDO::getChannelNo, Function.identity(), (k1, k2) -> k1));
                List<String> deviceNosList = deviceChannels.stream().map(DeviceChannelDO::getChannelNo).collect(Collectors.toList());
                List<String> remoteDeviceNos = channelList.stream().map(OpenChannelDTO::getChannelNo).collect(Collectors.toList());
                deviceNosList.removeAll(remoteDeviceNos);
                if(CollectionUtils.isNotEmpty(deviceNosList)){
                    deviceChannelMapper.batchDeleteChannelByChannelNo(enterpriseId, deviceDO.getDeviceId(), deviceNosList);
                }
                List<DeviceChannelDO> addChannel = new ArrayList<>();
                for (OpenChannelDTO deviceChannelDO : channelList) {
                    DeviceChannelDO deviceChannel = deviceChannelMap.get(deviceChannelDO.getChannelNo());
                    if(Objects.isNull(deviceChannel)){
                        addChannel.add(mapDeviceChannelDO(deviceDO, deviceChannelDO));
                    }else{
                        //通道表中的deviceId是对应通道的通道ID 如果map没有对应的设备 说明设备下线或者异常状态码为0
                        deviceChannel.setStatus(deviceChannelDO.getStatus());
                        if(DeviceStatusEnum.OFFLINE.getCode().equals(deviceDetail.getDeviceStatus())){
                            deviceChannel.setStatus(DeviceStatusEnum.OFFLINE.getCode());
                        }
                    }
                }
                if(CollectionUtils.isNotEmpty(addChannel)){
                    deviceChannelMapper.batchInsertOrUpdateDeviceChannel(enterpriseId, addChannel);
                }
                //更新设备通道状态
                deviceChannelMapper.updateDeviceChannelStatus(enterpriseId,deviceChannels);
            }
        }
        return true;
    }

    @Override
    public String getAccessToken(String enterpriseId, AccountTypeEnum accountType, YunTypeEnum yunType, String deviceId, Boolean refresh) {
        if (Boolean.TRUE.equals(refresh)) {
            videoServiceApi.deleteAccessToken(enterpriseId, deviceId, yunType, accountType);
        }
        String accessToken = videoServiceApi.getAccessToken(enterpriseId, accountType, yunType, deviceId);
        return AESUtil.encrypt(encryptKey, accessToken);
    }

    @Override
    public DeviceDO getDeviceByDeviceId(String enterpriseId, String deviceId) {
        return deviceMapper.getDeviceByDeviceId(enterpriseId, deviceId);
    }

    @Override
    public Boolean pictureFlip(String enterpriseId, DeviceConfigDTO configDTO) {
        return videoServiceApi.pictureFlip(enterpriseId, configDTO);
    }

    @Override
    public void refreshDevice(String enterpriseId, List<String> deviceIdList, String userId) {
        List<DeviceDO> deviceList = deviceMapper.getDeviceByDeviceIdList(enterpriseId, deviceIdList);
        if(CollectionUtils.isEmpty(deviceList)){
            return;
        }
        List<DeviceChannelDO> dbChannelList = deviceChannelMapper.getByParentDeviceIds(enterpriseId, deviceIdList, null);
        List<DeviceDO> updateList = new ArrayList<>();
        List<DeviceChannelDO> updateChannelList = new ArrayList<>();
        List<String> allChannelNos = new ArrayList<>();
        for (DeviceDO device : deviceList) {
            YunTypeEnum yunType = YunTypeEnum.getByCode(device.getResource());
            AccountTypeEnum accountType = AccountTypeEnum.getAccountType(device.getAccountType());
            String username = null, password = null;
            JSONObject extendInfo = JSONObject.parseObject(device.getExtendInfo());
            if(Objects.isNull(extendInfo)){
                extendInfo = new JSONObject();
            }
            if (YunTypeEnum.JFY.equals(yunType)) {
                username = extendInfo.getString(DeviceDO.ExtendInfoField.USERNAME);
                password = extendInfo.getString(DeviceDO.ExtendInfoField.PASSWORD);
            }
            OpenDeviceDTO deviceDetail = null;
            try {
                deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, device.getDeviceId(), yunType, accountType, username, password);
            } catch (Exception e) {
                continue;
            }
            if(Objects.isNull(deviceDetail)){
                continue;
            }
            DeviceDO deviceDO = OpenDeviceDTO.convertUpdateDO(deviceDetail, userId);
            if(Objects.isNull(deviceDO)){
                continue;
            }
            //刷新不更新设备名称
            deviceDO.setDeviceName(null);
            extendInfo.put(DeviceDO.ExtendInfoField.DEVICE_CAPACITY, deviceDetail.getDeviceCapacity());
            deviceDO.setExtendInfo(JSONObject.toJSONString(extendInfo));
            List<OpenChannelDTO> channelList = deviceDetail.getChannelList();
            deviceDO.setHasChildDevice(false);
            if(CollectionUtils.isNotEmpty(channelList)){
                deviceDO.setHasChildDevice(true);
                List<DeviceChannelDO> collect = channelList.stream().map(channel -> mapDeviceChannelDO(deviceDO, channel)).collect(Collectors.toList());
                allChannelNos.addAll(collect.stream().map(o->o.getParentDeviceId() +Constants.MOSAICS + o.getChannelNo()).collect(Collectors.toList()));
                updateChannelList.addAll(collect);
            }
            updateList.add(deviceDO);
        }
        if(CollectionUtils.isNotEmpty(updateList)){
            deviceMapper.batchUpdateDevices(enterpriseId, updateList);
        }
        if(CollectionUtils.isNotEmpty(updateChannelList)){
            deviceChannelMapper.batchInsertOrUpdateDeviceChannel(enterpriseId, updateChannelList);
        }
        List<Long> deleteChannelIds = ListUtils.emptyIfNull(dbChannelList).stream().filter(channel -> !allChannelNos.contains(channel.getParentDeviceId() + Constants.MOSAICS + channel.getChannelNo())).map(DeviceChannelDO::getId).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(deleteChannelIds)){
            deviceChannelMapper.batchDeleteDeviceChannelById(enterpriseId, deleteChannelIds);
        }
    }

    @Override
    public void refreshAllDevice(String enterpriseId) {
        boolean isContinue = true;
        int pageNum = 1, pageSize = 100;
        while (isContinue){
            PageHelper.startPage(pageNum, pageSize, false);
            List<String> deviceList = deviceMapper.getDeviceId(enterpriseId);
            if(CollectionUtils.isEmpty(deviceList) || deviceList.size() < pageSize){
                isContinue = false;
            }
            pageNum++;
            refreshDevice(enterpriseId, deviceList, null);
        }
    }

    @Override
    public DeviceChannelVO getDeviceChannel(String enterpriseId, String deviceId, String channelNo) {
        List<DeviceChannelDO> channelList = deviceChannelMapper.getDeviceChannelByParentId(enterpriseId, deviceId);
        DeviceChannelDO deviceChannelDO = ListUtils.emptyIfNull(channelList).stream().filter(channel -> channel.getChannelNo().equals(channelNo)).findFirst().orElse(null);
        return DeviceChannelVO.convert(deviceChannelDO);
    }

    @Override
    public int deleteLocalChannel(String enterpriseId, DeleteChannelRequest request) {
        return deviceChannelMapper.deleteChannel(enterpriseId, request.getChannelList());
    }

    @Override
    public List<DeviceVideoRecordVO> listDeviceRecordByTime(String enterpriseId, DeviceRecordQueryRequest request) {
        return videoServiceApi.listDeviceRecordByTime(enterpriseId, request);
    }

    @Override
    public DeviceTalkbackVO deviceTalkback(String enterpriseId, DeviceTalkbackDTO talkbackDTO) {
        return videoServiceApi.deviceTalkback(enterpriseId, talkbackDTO);
    }

    @Override
    public Boolean deviceReboot(String enterpriseId, String deviceId, String channelNo) {
        return videoServiceApi.deviceReboot(enterpriseId, deviceId, channelNo);
    }

    @Override
    public List<DeviceStorageInfoVO> deviceStorageInfo(String enterpriseId, String deviceId) {
        return videoServiceApi.deviceStorageInfo(enterpriseId, deviceId);
    }

    @Override
    public Boolean deviceStorageFormatting(String enterpriseId, String deviceId, String channelNo) {
        return videoServiceApi.deviceStorageFormatting(enterpriseId, deviceId, channelNo);
    }

    @Override
    public DeviceSoftHardwareInfoVO deviceSoftHardwareInfo(String enterpriseId, String deviceId) {
        return videoServiceApi.deviceSoftHardwareInfo(enterpriseId, deviceId);
    }

    @Override
    public Boolean updateVideoVencType(String enterpriseId, String deviceId, String vencType) {
        return videoServiceApi.updateVideoVencType(enterpriseId, deviceId, vencType);
    }


    /**
     * 检查并更新萤石云录像回放下载任务状态
     * @param enterpriseId 企业id
     * @param device 设备
     * @param deviceDownloadCenterDO 下载中心对象
     */
    private void checkAndUpdateYingShiVideoDownloadTaskStatus(String enterpriseId, DeviceDO device, DeviceDownloadCenterDO deviceDownloadCenterDO) {
        if (ObjectUtil.isNull(device) || !(YunTypeEnum.YINGSHIYUN_GB.getCode().equals(device.getResource()) || YunTypeEnum.YINGSHIYUN.getCode().equals(device.getResource()))) {
            return;
        }
        if (ObjectUtil.isNull(deviceDownloadCenterDO) || !deviceDownloadCenterDO.getStatus().equals(1) || ObjectUtil.isNull(deviceDownloadCenterDO.getFileId())) return;
        VideoFileDTO videoFile = videoServiceApi.getVideoFile(enterpriseId, device.getDeviceId(), deviceDownloadCenterDO.getFileId());
        if (ObjectUtil.isNull(videoFile)) return ;
        Integer status = 0;
        String fileUrl = "";
        if (Constants.INDEX_ZERO.equals(videoFile.getStatus())) {
            List<String> videoDownloadUrl = videoServiceApi.getVideoDownloadUrl(enterpriseId, device.getDeviceId(), deviceDownloadCenterDO.getFileId());
            if (CollectionUtils.isNotEmpty(videoDownloadUrl)) {
                fileUrl = CollectionUtil.join(videoDownloadUrl, ",");
            }
        } else if (videoFile.getStatus() == 1) {
            status = 1;
        } else {
            status = 2;
        }
        if (!status.equals(deviceDownloadCenterDO.getStatus())) {
            DeviceDownloadCenterDO updateDO = DeviceDownloadCenterDO.builder()
                    .status(status)
                    .fileUrl(fileUrl)
                    .id(deviceDownloadCenterDO.getId())
                    .errorMsg(videoFile.getErrorMsg())
                    .build();
            deviceDownloadCenterMapper.updateByPrimaryKeySelective(updateDO, enterpriseId);
        }
    }


    private Integer deviceSummaryGroupStoreIdNum(String eId, CurrentUser user, DeviceReportSearchRequest request){
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        boolean isAdmin = sysRoleService.checkIsAdmin(eId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(eId, isAdmin, user.getUserId(), request.getRegionIds());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return 0;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        Integer count = deviceMapper.deviceSummaryGroupStoreIdNum(eId, request, regionPathList);
        return count;
    }

    @Override
    @Async("syncThreadPool")
    public void syncDevice(String enterpriseId, YunTypeEnum yunTypeEnum, String userId, String dbName) {
        DataSourceHelper.reset();
        String lockKey = MessageFormat.format("syncDevice:{0}:{1}", enterpriseId, yunTypeEnum.getCode());
        if(!redis.setNxExpire(lockKey, String.valueOf(System.currentTimeMillis()), 3 * 60 * 1000)){
            throw new ServiceException(ErrorCodeEnum.LATER_SYNC);
        }
        EnterpriseDO enterpriseDO = enterpriseMapper.selectById(enterpriseId);
        Integer limitDeviceCount = Optional.ofNullable(enterpriseDO).map(EnterpriseDO::getLimitDeviceCount).orElse(Constants.ZERO);
        AccountTypeEnum accountType = AccountTypeEnum.PRIVATE;
        EnterpriseVideoSettingDTO enterpriseVideo = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(enterpriseId, yunTypeEnum.getCode(), accountType.getCode());
        if(Objects.isNull(enterpriseVideo)){
            redis.delKey(lockKey);
            throw new ServiceException(ErrorCodeEnum.YUN_TYPE_CONFIG_NOT_FOUND);
        }
        enterpriseVideoSettingService.updateLastSyncLastTime(enterpriseId, yunTypeEnum, DeviceSyncStatusEnum.SYNC_ING);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        YunTypeEnum yunType = YunTypeEnum.getByCode(enterpriseVideo.getYunType());
        int pageNum = Constants.INDEX_ONE, pageSize = Constants.FIFTY_INT;
        boolean isContinue = true;
        List<String> allStoreCodes = new ArrayList<>();
        List<String> notStoreCodes = new ArrayList<>();
        int notSyncDevice = 0, deviceCount = 0;
        while (isContinue){
            try {
                DataSourceHelper.changeToSpecificDataSource(dbName);
                Integer count = getDeviceCountAndChannelCount(enterpriseId);
                if(count >= limitDeviceCount){
                    redis.delKey(lockKey);
                    log.info("设备数量超限：count:{}, limitDeviceCount:{}", count, limitDeviceCount);
                    throw new ServiceException(ErrorCodeEnum.DEVICE_COUNT_LIMIT);
                }
                PageInfo<OpenDevicePageDTO> deviceInfo = videoServiceApi.getDeviceList(enterpriseId, yunType, AccountTypeEnum.PRIVATE, pageNum, pageSize);
                List<OpenDevicePageDTO> deviceList = Optional.ofNullable(deviceInfo).map(o->o.getList()).orElse(Lists.newArrayList());
                if(CollectionUtils.isEmpty(deviceList)){
                    log.info("设备列表为空");
                    break;
                }
                List<String> storeCodes = deviceList.stream().map(OpenDevicePageDTO::getStoreCode).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
                Map<String, String> storeNumStoreIdMap = storeDao.getStoreNumStoreIdMap(enterpriseId, storeCodes);
                if(deviceList.size() < pageSize){
                    isContinue = false;
                }
                pageNum++;
                List<String> storeIds = new ArrayList<>();
                List<DeviceDO> deviceDOList = new ArrayList<>();
                List<DeviceChannelDO> deviceChannelList = new ArrayList<>();
                deviceCount = deviceCount + deviceList.size();
                for (OpenDevicePageDTO openDevice : deviceList) {
                    String storeId = null;
                    if(YunTypeEnum.MYJ.equals(yunType) || YunTypeEnum.WDZ.equals(yunType)){
                        allStoreCodes.add(openDevice.getStoreCode());
                        storeId = storeNumStoreIdMap.get(openDevice.getStoreCode());
                        if(StringUtils.isBlank(storeId)){
                            notSyncDevice++;
                            notStoreCodes.add(openDevice.getStoreCode());
                            continue;
                        }
                        storeIds.add(storeId);
                    }
                    OpenDeviceDTO deviceDetail = null;
                    try {
                        if (YunTypeEnum.JFY.equals(yunType)) {
                            deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, openDevice.getDeviceId(), yunType, accountType, openDevice.getUsername(), openDevice.getPassword());
                        } else {
                            deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, openDevice.getDeviceId(), yunType, accountType);
                        }
                    } catch (Exception e) {
                        notSyncDevice++;
                        notStoreCodes.add(e.getMessage());
                        log.info("设备详情获取失败");
                    }
                    if(Objects.isNull(deviceDetail)){
                        continue;
                    }
                    if (YunTypeEnum.WDZ.equals(yunType)) {
                        deviceDetail.setHasPtz(openDevice.getHasPtz());
                    }
                    int channelCount = CollectionUtils.isEmpty(deviceDetail.getChannelList()) ? Constants.ZERO : deviceDetail.getChannelList().size();
                    DeviceDO deviceDO = OpenDeviceDTO.convertDO(deviceDetail, userId);
                    deviceDO.setDeviceName(StringUtils.isNotBlank(deviceDetail.getDeviceName()) ? deviceDetail.getDeviceName() : openDevice.getDeviceName());
                    if(YunTypeEnum.ULUCU.equals(yunType)){
                        deviceDO = OpenDevicePageDTO.convertDO(openDevice, userId, deviceDetail);
                        if(StringUtils.isNotBlank(openDevice.getUseStoreId())){
                            storeId = openDevice.getUseStoreId();
                            storeIds.add(storeId);
                        }
                    }
                    deviceDO.setBindStoreIds(storeId);
                    deviceDO.setBindStoreId(storeId);
                    if(StringUtils.isNotBlank(storeId)){
                        deviceDO.setBindStatus(Boolean.TRUE);
                    }else {
                        deviceDO.setBindStatus(Boolean.FALSE);
                    }
                    deviceDO.setAccountType(AccountTypeEnum.PRIVATE.getCode());
                    int syncCount = Constants.INDEX_ONE + channelCount;
                    DataSourceHelper.changeToSpecificDataSource(dbName);
                    if(count + syncCount >= limitDeviceCount){
                        log.info("设备数量超限：同步数量：{}", syncCount);
                        isContinue = false;
                        break;
                    }
                    List<OpenChannelDTO> channelList = deviceDetail.getChannelList();
                    deviceDO.setHasChildDevice(false);
                    if(CollectionUtils.isNotEmpty(channelList)){
                        deviceDO.setHasChildDevice(true);
                        DeviceDO finalDeviceDO = deviceDO;
                        List<DeviceChannelDO> collect = channelList.stream().map(channel -> mapDeviceChannelDO(finalDeviceDO, channel)).collect(Collectors.toList());
                        deviceChannelList.addAll(collect);
                    }
                    JSONObject extendInfo = new JSONObject();
                    extendInfo.put(DeviceDO.ExtendInfoField.DEVICE_CAPACITY, deviceDetail.getDeviceCapacity());
                    if (StringUtils.isNotBlank(openDevice.getUsername()) || StringUtils.isNotBlank(openDevice.getPassword())) {
                        extendInfo.put("username", openDevice.getUsername());
                        extendInfo.put("password", openDevice.getPassword());
                    }
                    deviceDO.setExtendInfo(JSONObject.toJSONString(extendInfo));
                    deviceDOList.add(deviceDO);
                }
                if(CollectionUtils.isNotEmpty(deviceDOList)){
                    deviceMapper.batchInsertOrUpdateDevices(enterpriseId, deviceDOList, DeviceTypeEnum.DEVICE_VIDEO.getCode());
                }
                if(CollectionUtils.isNotEmpty(deviceChannelList)){
                    deviceChannelMapper.batchInsertOrUpdateDeviceChannel(enterpriseId, deviceChannelList);
                }
                //更新门店存在设备
                storeDao.updateStoreHasDevice(enterpriseId, storeIds);
                //云眸拿到的是所有的数据列表 这里循环一次直接推出
                 if (YunTypeEnum.HIKCLOUD.getCode().equals(yunType.getCode()) || YunTypeEnum.WDZ.getCode().equals(yunType.getCode())){
                    isContinue = false;
                }
                 DataSourceHelper.reset();
                 enterpriseDeviceInfoDAO.batchInsertOrUpdate(EnterpriseDeviceInfoDO.convertEnterpriseDeviceInfo(enterpriseId, deviceDOList, deviceChannelList));
            } catch (Exception e) {
                log.info("设备同步失败", e);
                redis.delKey(lockKey);
                DataSourceHelper.reset();
                enterpriseVideoSettingService.updateLastSyncLastTime(enterpriseId, yunTypeEnum, DeviceSyncStatusEnum.SYNC_FAIL);
                throw new ServiceException(ErrorCodeEnum.DEVICE_SYNC_ERROR);
            }
        }
        allStoreCodes = allStoreCodes.stream().distinct().collect(Collectors.toList());
        notStoreCodes = notStoreCodes.stream().distinct().collect(Collectors.toList());
        log.info("storeCodes:所有门店：{}， 未匹配门店:{}, 总设备数：{}， 未匹配设备数：{}， 未匹配详情:{}", allStoreCodes.size(), notStoreCodes.size(), deviceCount, notSyncDevice, String.join(",", notStoreCodes));
        DataSourceHelper.reset();
        log.info("企业设备同步完成:{}", enterpriseId);
        enterpriseVideoSettingService.updateLastSyncLastTime(enterpriseId, yunTypeEnum, DeviceSyncStatusEnum.SYNC_SUCCESS);
    }

    @Override
    public boolean syncSingleDevice(String enterpriseId, YunTypeEnum yunTypeEnum, String deviceId, String storeId, AccountTypeEnum accountType, String deviceName) {
        String userId = UserHolder.getUser().getUserId();
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        EnterpriseDO enterpriseDO = enterpriseMapper.selectById(enterpriseId);
        Integer limitDeviceCount = Optional.ofNullable(enterpriseDO).map(EnterpriseDO::getLimitDeviceCount).orElse(Constants.ZERO);
        EnterpriseVideoSettingDTO enterpriseVideo = enterpriseVideoSettingService.getEnterpriseVideoSettingByYunType(enterpriseId, yunTypeEnum.getCode(), accountType.getCode());
        if(Objects.isNull(enterpriseVideo)){
            throw new ServiceException(ErrorCodeEnum.YUN_TYPE_CONFIG_NOT_FOUND);
        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        YunTypeEnum yunType = YunTypeEnum.getByCode(enterpriseVideo.getYunType());
        try {
            Integer count = getDeviceCountAndChannelCount(enterpriseId);
            if(count >= limitDeviceCount){
                log.info("设备数量超限：count:{}, limitDeviceCount:{}", count, limitDeviceCount);
                throw new ServiceException(ErrorCodeEnum.DEVICE_COUNT_LIMIT);
            }
            OpenDevicePageDTO targetDevice = getTargetDevice(enterpriseId, deviceId, yunTypeEnum, accountType);
            List<DeviceDO> deviceDOList = new ArrayList<>();
            List<DeviceChannelDO> deviceChannelList = new ArrayList<>();
            OpenDeviceDTO deviceDetail;
            if (YunTypeEnum.JFY.equals(yunType)) {
                deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, targetDevice.getDeviceId(), yunType, accountType, targetDevice.getUsername(), targetDevice.getPassword());
            } else {
                deviceDetail = videoServiceApi.getDeviceDetail(enterpriseId, targetDevice.getDeviceId(), yunType, accountType);
            }
            DeviceDO deviceDO = OpenDeviceDTO.convertDO(deviceDetail, userId);
            if (Objects.isNull(deviceDO)) {
                throw new ServiceException(ErrorCodeEnum.DEVICE_SYNC_ERROR);
            }
            if(DeviceStatusEnum.OFFLINE.getCode().equals(deviceDetail.getDeviceStatus())){
                throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_ONLINE);
            }
            deviceDO.setDeviceName(StringUtils.isNotBlank(deviceName) ? deviceName : StringUtils.isNotBlank(deviceDetail.getDeviceName()) ? deviceDetail.getDeviceName() : targetDevice.getDeviceName());
            deviceDO.setBindStoreIds(storeId);
            deviceDO.setBindStoreId(storeId);
            if(StringUtils.isNotBlank(storeId)){
                deviceDO.setBindStatus(Boolean.TRUE);
            }else {
                deviceDO.setBindStatus(Boolean.FALSE);
            }
            deviceDO.setAccountType(accountType.getCode());

            List<OpenChannelDTO> channelList = deviceDetail.getChannelList();
            deviceDO.setHasChildDevice(false);
            if(CollectionUtils.isNotEmpty(channelList)){
                deviceDO.setHasChildDevice(true);
                List<DeviceChannelDO> collect = channelList.stream().map(channel -> mapDeviceChannelDO(deviceDO, channel)).collect(Collectors.toList());
                deviceChannelList.addAll(collect);
            }
            JSONObject extendInfo = new JSONObject();
            extendInfo.put(DeviceDO.ExtendInfoField.DEVICE_CAPACITY, deviceDetail.getDeviceCapacity());
            if (StringUtils.isNotBlank(targetDevice.getUsername()) || StringUtils.isNotBlank(targetDevice.getPassword())) {
                extendInfo.put("username", targetDevice.getUsername());
                extendInfo.put("password", targetDevice.getPassword());
            }
            deviceDO.setExtendInfo(JSONObject.toJSONString(extendInfo));
            deviceDOList.add(deviceDO);

            if(CollectionUtils.isNotEmpty(deviceDOList)){
                deviceMapper.batchInsertOrUpdateDevices(enterpriseId, deviceDOList, DeviceTypeEnum.DEVICE_VIDEO.getCode());
            }
            if(CollectionUtils.isNotEmpty(deviceChannelList)){
                deviceChannelMapper.batchInsertOrUpdateDeviceChannel(enterpriseId, deviceChannelList);
            }
            if (YunTypeEnum.YUNSHITONG.equals(yunType) && StringUtils.isNotBlank(deviceName)) {
                // 云视通设备名称修改
                videoServiceApi.configureDevice(enterpriseId, DeviceConfigDTO.builder().deviceId(deviceId).deviceName(deviceName).build());
            }
            //更新门店存在设备
            storeDao.updateStoreHasDevice(enterpriseId, Collections.singletonList(storeId));
            DataSourceHelper.reset();
            enterpriseDeviceInfoDAO.batchInsertOrUpdate(EnterpriseDeviceInfoDO.convertEnterpriseDeviceInfo(enterpriseId, deviceDOList, deviceChannelList));
            return true;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.info("设备同步失败", e);
            throw new ServiceException(ErrorCodeEnum.DEVICE_SYNC_ERROR);
        }
    }

    private OpenDevicePageDTO getTargetDevice(String enterpriseId, String deviceId, YunTypeEnum yunType, AccountTypeEnum accountType) {
        // 雄迈的设备部分参数在列表接口中，因此调用列表接口
        if (YunTypeEnum.JFY.equals(yunType)) {
            int pageNum = Constants.INDEX_ONE, pageSize = Constants.FIFTY_INT;
            boolean isContinue = true;
            // 所有设备列表中查指定设备
            while (isContinue) {
                PageInfo<OpenDevicePageDTO> deviceInfo = videoServiceApi.getDeviceList(enterpriseId, yunType, accountType, pageNum++, pageSize);
                List<OpenDevicePageDTO> deviceList = Optional.ofNullable(deviceInfo).map(PageSerializable::getList).orElse(Lists.newArrayList());
                OpenDevicePageDTO targetDevice = ListUtils.emptyIfNull(deviceList).stream().filter(v -> v.getDeviceId().equals(deviceId)).findFirst().orElse(null);
                if (Objects.nonNull(targetDevice)) {
                    return targetDevice;
                }
                if(CollectionUtils.isEmpty(deviceList) || deviceList.size() < pageSize){
                    isContinue = false;
                }
            }
            log.error("设备列表中未查询到该设备");
            throw new ServiceException(ErrorCodeEnum.DEVICE_NOT_FOUND);
        } else {
            OpenDevicePageDTO result = new OpenDevicePageDTO();
            result.setDeviceId(deviceId);
            return result;
        }
    }

    @Override
    public List<DeviceYunTypeVO> getDeviceYunTypeList(String enterpriseId) {
        DataSourceHelper.reset();
        List<EnterpriseVideoSettingDTO> enterpriseVideoSetting = enterpriseVideoSettingService.getEnterpriseVideoSetting(enterpriseId);
        List<DeviceYunTypeVO> resultList = new ArrayList<>();
        for (EnterpriseVideoSettingDTO enterpriseVideoSettingVO : ListUtils.emptyIfNull(enterpriseVideoSetting)) {
            if(!enterpriseVideoSettingVO.getHasOpen()){
                continue;
            }
            DeviceYunTypeVO yunTypeVO = new DeviceYunTypeVO();
            yunTypeVO.setYunType(YunTypeEnum.getByCode(enterpriseVideoSettingVO.getYunType()));
            if(Objects.nonNull(enterpriseVideoSettingVO.getLastSyncTime())) {
                yunTypeVO.setLastSyncTime(DateUtil.format(enterpriseVideoSettingVO.getLastSyncTime(), DatePattern.NORM_DATETIME_MINUTE_PATTERN));
            }
            yunTypeVO.setSyncStatus(enterpriseVideoSettingVO.getSyncStatus());
            if(YunTypeEnum.TP_LINK.getCode().equals(enterpriseVideoSettingVO.getYunType())){
                yunTypeVO.setAccessSecret(enterpriseVideoSettingVO.getSecret());
                yunTypeVO.setAccessKeyId(enterpriseVideoSettingVO.getAccessKeyId());
            }
            resultList.add(yunTypeVO);
        }
        return resultList;
    }

    private DeviceCaptureLibDTO mapToDeviceCaptureLibDTO(DeviceCaptureLibDO deviceCaptureLibDO){
        DeviceCaptureLibDTO deviceCaptureLibDTO = new DeviceCaptureLibDTO();
        deviceCaptureLibDTO.setDeviceId(deviceCaptureLibDO.getDeviceId());
        deviceCaptureLibDTO.setCreateTime(deviceCaptureLibDO.getCreateTime());
        deviceCaptureLibDTO.setCreateUserId(deviceCaptureLibDO.getCreateUserId());
        deviceCaptureLibDTO.setFileType(deviceCaptureLibDO.getFileType());
        deviceCaptureLibDTO.setFileUrl(deviceCaptureLibDO.getFileUrl());
        deviceCaptureLibDTO.setCreateUserName(deviceCaptureLibDO.getCreateUserName());
        deviceCaptureLibDTO.setName(deviceCaptureLibDO.getName());
        deviceCaptureLibDTO.setId(deviceCaptureLibDO.getId());
        deviceCaptureLibDTO.setStoreId(deviceCaptureLibDO.getStoreId());
        deviceCaptureLibDTO.setSnapshotUrl(deviceCaptureLibDO.getSnapshotUrl());
        return deviceCaptureLibDTO;

    }

    /**
     * 权限
     * @param enterpriseId
     * @param isAdmin
     * @param userId
     * @param regionIdList
     * @return
     */
    private List<RegionPathDTO> getAuthRegionList(String enterpriseId, Boolean isAdmin, String userId, List<String> regionIdList){
        if (!isAdmin && CollectionUtils.isEmpty(regionIdList)) {
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingService.listUserAuthMappingByUserId(enterpriseId, userId);
            if (CollectionUtils.isNotEmpty(userAuthMappingList)) {
                regionIdList = userAuthMappingList.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
            }
        }
        List<RegionPathDTO> regionPathList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(regionIdList)){
            regionPathList = regionService.getRegionPathByList(enterpriseId, regionIdList);
        }
        return regionPathList;
    }


    public List<DeviceMappingDTO> facadeList(String enterpriseId, List<DeviceMappingDTO> devices) {
        //组合通道数据
        if (CollectionUtils.isEmpty(devices)) {
            return null;
        }
        //获取企业场景列表
        List<StoreSceneDo> storeSceneList = storeSceneMapper.getStoreSceneList(enterpriseId);
        List<String> deviceIdList = devices.stream()
                .filter(data -> data.getHasChildDevice() != null && data.getHasChildDevice())
                .map(DeviceMappingDTO::getDeviceId)
                .collect(Collectors.toList());
        List<String> deviceIds = devices.stream().map(DeviceMappingDTO::getDeviceId).collect(Collectors.toList());
        List<ChannelDTO> channelList = getChannelList(enterpriseId, deviceIdList);
        if (channelList!=null){
            channelList.forEach(data->{
                //子通道封装企业场景名称
                storeSceneList.stream().forEach(storeSceneDo -> {
                    if (storeSceneDo.getId().equals(data.getStoreSceneId())){
                        data.setStoreSceneName(storeSceneDo.getName());
                    }
                });
            });
        }
        DataSourceHelper.reset();
        List<EnterpriseAuthDeviceDO> authDeviceList = enterpriseAuthDeviceDAO.getAuthDeviceListByDeviceIds(enterpriseId, deviceIds);
        DataSourceHelper.changeToMy();
        Map<String, DeviceAuthStatusEnum> deviceAuthStatusMap = EnterpriseAuthDeviceDO.getAuthStatus(authDeviceList);
        Map<String, List<ChannelDTO>> deviceChannelMap = ListUtils.emptyIfNull(channelList)
                .stream()
                .collect(Collectors.groupingBy(ChannelDTO::getParentDeviceId));

        devices.forEach(data -> {
            //设备类型 有子设备的Video类型名称为录像机
            if(DeviceTypeEnum.getByCode(data.getDeviceType())==DeviceTypeEnum.DEVICE_VIDEO){
                if(data.getHasChildDevice()!=null&&data.getHasChildDevice()){
                    data.setDeviceTypeName("录像机");
                }else {
                    data.setDeviceTypeName(DeviceTypeEnum.getByCode(data.getDeviceType()).getDesc());
                }
            }else {
                data.setDeviceTypeName(DeviceTypeEnum.getByCode(data.getDeviceType()).getDesc());

            }
            //设备场景
            DeviceSceneEnum byCode = DeviceSceneEnum.getByCode(data.getScene());
            data.setScene(byCode != null ? byCode.getDesc() : null);

            //封装企业场景名称
            storeSceneList.stream().forEach(storeSceneDo -> {
                if (storeSceneDo.getId().equals(data.getStoreSceneId())){
                    data.setStoreSceneName(storeSceneDo.getName());
                }
            });

            //设备来源
            YunTypeEnum yunTypeEnum = YunTypeEnum.getByCode(data.getDeviceSource());
            data.setSource(yunTypeEnum != null ? yunTypeEnum.getMsg() : null);

            if (MapUtils.isNotEmpty(deviceChannelMap) && CollectionUtils.isNotEmpty(deviceChannelMap.get(data.getDeviceId()))) {
                List<ChannelDTO> channelDTOS = deviceChannelMap.get(data.getDeviceId());
                ListUtils.emptyIfNull(channelDTOS)
                        .forEach(channel->{
                            channel.setDeviceSource(data.getDeviceSource());
                            channel.setAuthList(DeviceAuthAppVO.convertList(data.getDeviceId() + Constants.MOSAICS + channel.getChannelNo(), deviceAuthStatusMap));
                        });
                data.setChannelList(channelDTOS);
            }
            if(Objects.nonNull(data.getStoreStatus())){
                data.setStoreStatusName(StoreStatusEnum.getName(data.getStoreStatus()));
            }
            data.setHasChildDevice(data.getHasChildDevice() != null && data.getHasChildDevice());
            data.setAuthList(DeviceAuthAppVO.convertList(data.getDeviceId() + Constants.MOSAICS + Constants.ZERO_STR, deviceAuthStatusMap));
        });
        return devices;
    }

    private List<ChannelDTO> getChannelList(String eid, List<String> deviceIdList) {
        if (CollectionUtils.isEmpty(deviceIdList)) {
            return null;
        }
        List<DeviceChannelDO> deviceChannelDOList = deviceChannelMapper.listDeviceChannelByDeviceId(eid, deviceIdList, null);
        return ListUtils.emptyIfNull(deviceChannelDOList)
                .stream()
                .map(this::mapChannelDTO)
                .collect(Collectors.toList());
    }

    private ChannelDTO mapChannelDTO(DeviceChannelDO data) {
        ChannelDTO channelDTO = new ChannelDTO();
        channelDTO.setUnionId(data.getParentDeviceId() + "_" + data.getChannelNo());
        channelDTO.setDeviceId(data.getDeviceId());
        channelDTO.setParentDeviceId(data.getParentDeviceId());
        channelDTO.setChannelNo(data.getChannelNo());
        channelDTO.setChannelName(data.getChannelName());
        channelDTO.setHasPtz(data.getHasPtz() != null && data.getHasPtz());
        channelDTO.setId(data.getId());
        channelDTO.setStatus(data.getStatus());
        channelDTO.setRemark(data.getRemark());
        channelDTO.setStoreSceneId(data.getStoreSceneId());
        channelDTO.setCreateTime(data.getCreateTime());
        return channelDTO;
    }

    private DeviceChannelDO mapDeviceChannelDO(DeviceDO deviceDO, OpenChannelDTO channel) {
        Date date = new Date();
        DeviceChannelDO deviceChannelDO = new DeviceChannelDO();
        deviceChannelDO.setParentDeviceId(channel.getParentDeviceId());
        deviceChannelDO.setDeviceId(channel.getDeviceId());
        deviceChannelDO.setChannelName(channel.getChannelName());
        deviceChannelDO.setChannelNo(channel.getChannelNo());
        deviceChannelDO.setCreateTime(date);
        deviceChannelDO.setUpdateTime(date);
        deviceChannelDO.setStatus(channel.getStatus());
        deviceChannelDO.setHasPtz(channel.getHasPtz());
        deviceChannelDO.setSupportCapture(Objects.nonNull(channel.getSupportCapture()) ? channel.getSupportCapture() : deviceDO.getSupportCapture());
        deviceChannelDO.setSupportPassenger(deviceDO.getSupportPassenger());
        deviceChannelDO.setStoreSceneId(DEFAULT_STORE_ID);
        JSONObject deviceCapacity = new JSONObject();
        deviceCapacity.put(DeviceDO.ExtendInfoField.DEVICE_CAPACITY, channel.getDeviceCapacity());
        deviceChannelDO.setExtendInfo(JSONObject.toJSONString(deviceCapacity));
        return deviceChannelDO;
    }
}
