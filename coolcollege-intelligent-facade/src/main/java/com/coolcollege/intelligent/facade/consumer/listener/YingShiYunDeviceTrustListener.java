package com.coolcollege.intelligent.facade.consumer.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.enums.device.DeviceSceneEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.util.Base64Utils;
import com.coolcollege.intelligent.common.util.CoolListUtils;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.device.dao.EnterpriseDeviceInfoDAO;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.video.EnterpriseVideoMapper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.DeviceDO;
import com.coolcollege.intelligent.model.device.EnterpriseDeviceInfoDO;
import com.coolcollege.intelligent.model.device.dto.OpenChannelDTO;
import com.coolcollege.intelligent.model.device.dto.OpenDeviceDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.*;
import com.coolcollege.intelligent.service.video.YingshiDeviceService;
import com.coolcollege.intelligent.service.video.openapi.VideoServiceApi;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.coolstore.base.enums.YunTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.Constants.DEFAULT_STORE_ID;

/**
 * @author chenyupeng
 * @since 2022/2/28
 */
@Slf4j
@Service
public class YingShiYunDeviceTrustListener implements MessageListener {

    @Resource
    private EnterpriseVideoMapper enterpriseVideoMapper;

    @Autowired
    private YingshiDeviceService yingshiDeviceService;
    @Resource
    private VideoServiceApi videoServiceApi;


    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private DeviceChannelMapper deviceChannelMapper;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseDeviceInfoDAO enterpriseDeviceInfoDAO;
    @Override
    public Action consume(Message message, ConsumeContext context) {
        String text = new String(message.getBody());
        if(StringUtils.isBlank(text)){
            log.info("消息体为空,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }
        log.info("重试消费次数 messageId：{}，try times：{}", message.getMsgID(), message.getReconsumeTimes());

        String lockKey = "MqOpenEnterpriseAliyunListener:" + message.getMsgID();
        boolean lock = redisUtilPool.setNxExpire(lockKey, message.getMsgID(), CommonConstant.NORMAL_LOCK_TIMES);

        if(lock){
            try {
                onAuthMsg(text);
            }catch (Exception e){
                log.error("MqOpenEnterpriseAliyunListener consume error",e);
                return Action.ReconsumeLater;
            }finally {
                redisUtilPool.delKey(lockKey);
            }
            log.info("消费成功,tag:{},messageId:{}",message.getTag(),message.getMsgID());
            return Action.CommitMessage;
        }

        return Action.ReconsumeLater;
    }
    public void onAuthMsg(String msg) {

        log.info("####MQ_YINGSHI_DEVICE_MANAGE msg={}", msg);
        if (StringUtils.isBlank(msg)) {
            return;
        }
        YingshiAuthCallbackDTO yingshiAuthCallbackDTO = JSONObject.parseObject(msg, YingshiAuthCallbackDTO.class);
        YingshiCreateUrlStateDTO yingshiCreateUrlStateDTO = null;
        if (StringUtils.isNotBlank(yingshiAuthCallbackDTO.getState()) && !"null".equals(yingshiAuthCallbackDTO.getState())) {
            yingshiCreateUrlStateDTO = JSONObject.parseObject(Base64Utils.baseConvertStr(yingshiAuthCallbackDTO.getState()), YingshiCreateUrlStateDTO.class);
        }

        //获取设备信息
        List<String> deviceChannelList = StrUtil.splitTrim(yingshiAuthCallbackDTO.getDeviceSerials(), ",");
        List<YingshiAuthDeviceDTO> yingshiAuthDeviceDTOList = ListUtils.emptyIfNull(deviceChannelList)
                .stream()
                .map(data -> {
                    YingshiAuthDeviceDTO yingshiAuthDeviceDTO = new YingshiAuthDeviceDTO();
                    List<String> strings = StrUtil.splitTrim(data, ":");
                    if (CollectionUtils.isNotEmpty(strings)) {
                        String deviceId = strings.get(0);
                        String channelNo = strings.get(1);
                        yingshiAuthDeviceDTO.setDeviceId(deviceId);
                        yingshiAuthDeviceDTO.setChannelNo(channelNo);
                        return yingshiAuthDeviceDTO;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<String> deviceIdList = ListUtils.emptyIfNull(yingshiAuthDeviceDTOList)
                .stream()
                .map(YingshiAuthDeviceDTO::getDeviceId)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(deviceIdList)) {
            return;
        }
        //授权逻辑
        if (yingshiAuthCallbackDTO.getOptType().equals("device_authorize")) {
            if (yingshiCreateUrlStateDTO == null) {
                return;
            }
            String eid = yingshiCreateUrlStateDTO.getEid();
            List<OpenDeviceDTO> yingshiDeviceDTOList = ListUtils.emptyIfNull(deviceIdList).stream()
                    .map(deviceId -> videoServiceApi.getDeviceDetail(eid, deviceId, YunTypeEnum.YINGSHIYUN, AccountTypeEnum.PLATFORM))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            authDevice(eid, yingshiDeviceDTOList, yingshiCreateUrlStateDTO);
        }
        //取消授权逻辑(兼容控制台取消授权设备授权)
        if (yingshiAuthCallbackDTO.getOptType().equals("device_cancel")) {
            if (yingshiCreateUrlStateDTO == null) {
                //控制台取消授权
                DataSourceHelper.reset();
                List<String> enterpriseIdList = enterpriseVideoMapper.selectEnterpriseIdByDeviceIdList(deviceIdList);
                ListUtils.emptyIfNull(enterpriseIdList)
                        .forEach(eid->{
                            cancelAuth(eid, yingshiAuthDeviceDTOList);
                        });
            } else {
                //用户主动取消授权
                cancelAuth(yingshiCreateUrlStateDTO.getEid(), yingshiAuthDeviceDTOList);
            }
        }
    }

    private void authDevice(String enterpriseId, List<OpenDeviceDTO> deviceList, YingshiCreateUrlStateDTO yingshiCreateUrlStateDTO) {
        //插入平台库
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<DeviceChannelDO> deviceChannelDOList = new ArrayList<>();
        List<DeviceDO> deviceDOList = new ArrayList<>();
        for (OpenDeviceDTO deviceDetail : deviceList) {
            DeviceDO deviceDO = OpenDeviceDTO.convertDO(deviceDetail, "system");
            if(Objects.isNull(deviceDO)){
                continue;
            }
            deviceDO.setAccountType(AccountTypeEnum.PLATFORM.getCode());
            List<OpenChannelDTO> channelList = deviceDetail.getChannelList();
            deviceDO.setHasChildDevice(false);
            deviceDO.setDeviceScene(DeviceSceneEnum.OTHER.getCode());
            deviceDO.setResource(YunTypeEnum.YINGSHIYUN.getCode());
            deviceDO.setStoreSceneId(DEFAULT_STORE_ID);
            deviceDO.setBindStatus(Boolean.FALSE);
            JSONObject extendInfo = new JSONObject();
            extendInfo.put(DeviceDO.ExtendInfoField.DEVICE_CAPACITY, deviceDetail.getDeviceCapacity());
            deviceDO.setExtendInfo(JSONObject.toJSONString(extendInfo));
            if(CollectionUtils.isNotEmpty(channelList)){
                deviceDO.setHasChildDevice(true);
                List<DeviceChannelDO> collect = channelList.stream().map(channel -> mapDeviceChannelDO(deviceDO, channel)).collect(Collectors.toList());
                deviceChannelDOList.addAll(collect);
            }
            deviceDOList.add(deviceDO);
        }
        if (CollectionUtils.isNotEmpty(deviceChannelDOList)) {
            deviceChannelMapper.batchInsertOrUpdateDeviceChannel(enterpriseId, deviceChannelDOList);
        }
        List<String> deviceIdList = ListUtils.emptyIfNull(deviceDOList).stream().map(DeviceDO::getDeviceId).collect(Collectors.toList());
        //插入业务库设备
        deviceMapper.batchInsertOrUpdateDevices(enterpriseId, deviceDOList, DeviceTypeEnum.DEVICE_VIDEO.getCode());
        if (StringUtils.isNotBlank(yingshiCreateUrlStateDTO.getStoreId())) {
            StoreDTO storeDTO = storeMapper.getStoreBaseInfo(enterpriseId, yingshiCreateUrlStateDTO.getStoreId());
            deviceMapper.bathUpdateDeviceBindStoreId(enterpriseId, deviceIdList, DateUtil.getTimestamp(), storeDTO, true);
            //更新门店下是否有设备的字段
            storeMapper.updateCamera(enterpriseId, Collections.singletonList(yingshiCreateUrlStateDTO.getStoreId()), true);
        }
        List<EnterpriseDeviceInfoDO> enterpriseDeviceList = EnterpriseDeviceInfoDO.convertEnterpriseDeviceInfo(enterpriseId, deviceDOList, deviceChannelDOList);
        DataSourceHelper.reset();
        enterpriseDeviceInfoDAO.batchInsertOrUpdate(enterpriseDeviceList);
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
        return deviceChannelDO;
    }


    private void cancelAuth(String eid, List<YingshiAuthDeviceDTO> yingshiAuthDeviceDTOList) {
        /**
         * 1.修改平台设备状态改为删除状态。
         * 2.删除企业库未授权设备
         * 3.更改门店绑定状态
         */
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        if(enterpriseConfigDO==null){
            return;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<String> distinctDeviceIdList = yingshiAuthDeviceDTOList.stream()
                .map(YingshiAuthDeviceDTO::getDeviceId)
                .distinct().collect(Collectors.toList());
        List<DeviceDO> deviceMappingDOList = deviceMapper.getDeviceByDeviceIdList(eid, distinctDeviceIdList);
        if(CollectionUtils.isEmpty(deviceMappingDOList)){
            return;
        }
        Map<String, List<String>> deviceIdGroupMap = ListUtils.emptyIfNull(yingshiAuthDeviceDTOList)
                .stream()
                .collect(Collectors.groupingBy(YingshiAuthDeviceDTO::getDeviceId, Collectors.mapping(YingshiAuthDeviceDTO::getChannelNo, Collectors.toList())));
        //删除企业库设备
        deviceIdGroupMap.forEach((deviceId, channelNoIdList) -> {
            //删除device_channel
            deviceChannelMapper.batchDeleteChannelByChannelNo(eid, deviceId, channelNoIdList);
            //删除device,需要判断设备是否还有授权子通道号，
            List<DeviceChannelDO> deviceChannelDOList = deviceChannelMapper.listDeviceChannelByDeviceId(eid, Collections.singletonList(deviceId), null);
            if (CollectionUtils.isEmpty(deviceChannelDOList)) {
                deviceMapper.batchDeleteDevices(eid, Collections.singletonList(deviceId), DeviceTypeEnum.DEVICE_VIDEO.getCode());
            }
        });

        //更新门店中是否有摄像头的字段
        List<String> storeIdList = ListUtils.emptyIfNull(deviceMappingDOList).stream()
                .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                .map(DeviceDO::getBindStoreId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storeIdList)) {
            List<DeviceDO> deviceByStoreIdList = deviceMapper.getDeviceByStoreIdList(eid, storeIdList, null, null, null);
            List<String> bindDeviceStoreIdList = ListUtils.emptyIfNull(deviceByStoreIdList)
                    .stream()
                    .filter(e -> StringUtils.isNotEmpty(e.getBindStoreId()))
                    .map(DeviceDO::getBindStoreId)
                    .collect(Collectors.toList());
            List<String> reduceaListThanbList = CoolListUtils.getReduceaListThanbList(storeIdList, bindDeviceStoreIdList);
            if (CollectionUtils.isNotEmpty(reduceaListThanbList)) {
                storeMapper.updateCamera(eid, reduceaListThanbList, false);
            }
        }
        //删除平台库中的需求
        DataSourceHelper.reset();
        deviceIdGroupMap.forEach((deviceId, channelNoIdList) -> {
            enterpriseVideoMapper.deleteEnterpriseVideo(eid, deviceId, channelNoIdList);
        });

    }
}
