package com.coolcollege.intelligent.facade.device;

import com.coolcollege.intelligent.common.enums.device.DeviceSceneEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceSourceEnum;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.dao.device.DeviceChannelMapper;
import com.coolcollege.intelligent.dao.device.DeviceMapper;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import com.coolcollege.intelligent.model.device.dto.DeviceMappingDTO;
import com.coolcollege.intelligent.model.device.dto.ChannelDTO;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolstore.base.enums.YunTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/08
 */
@Service
public class DeviceFacade {
    @Resource
    private DeviceChannelMapper deviceChannelMapper;

    @Resource
    private StoreSceneMapper storeSceneMapper;

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
                        });
                data.setChannelList(channelDTOS);
            }
            data.setHasChildDevice(data.getHasChildDevice()==null?false:data.getHasChildDevice());
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
        channelDTO.setHasPtz(data.getHasPtz()==null?false:data.getHasPtz());
        channelDTO.setId(data.getId());
        channelDTO.setStoreSceneId(data.getStoreSceneId());
        return channelDTO;
    }

}
