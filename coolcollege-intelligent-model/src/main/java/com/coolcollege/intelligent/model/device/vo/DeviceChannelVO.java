package com.coolcollege.intelligent.model.device.vo;

import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.model.device.DeviceChannelDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: DeviceChannelVO
 * @Description:
 * @date 2022-12-15 14:00
 */
@Data
public class DeviceChannelVO {

    @ApiModelProperty("通道id")
    private Long id;

    @ApiModelProperty("设备Id")
    private String deviceId;

    @ApiModelProperty("通道编号")
    private String channelNo;

    @ApiModelProperty("通道名称")
    private String channelName;

    @ApiModelProperty("是否支持云台")
    private Boolean hasPtz;

    @ApiModelProperty("offline离线, online 在线")
    private String status;

    @ApiModelProperty("父设备Id")
    private String parentDeviceId;

    @ApiModelProperty("门店场景id")
    private Long storeSceneId;

    public static List<DeviceChannelVO> convertVO(List<DeviceChannelDO> deviceChannel){
        List<DeviceChannelVO> resultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(deviceChannel)){
            for (DeviceChannelDO deviceChannelDO : deviceChannel) {
                DeviceChannelVO channelVO = new DeviceChannelVO();
                channelVO.setId(deviceChannelDO.getId());
                channelVO.setDeviceId(deviceChannelDO.getDeviceId());
                channelVO.setChannelNo(deviceChannelDO.getChannelNo());
                channelVO.setChannelName(deviceChannelDO.getChannelName());
                channelVO.setHasPtz(!Objects.isNull(deviceChannelDO.getHasPtz()) && deviceChannelDO.getHasPtz());
                channelVO.setStatus(Objects.isNull(deviceChannelDO.getStatus()) ? DeviceStatusEnum.ONLINE.getCode() : deviceChannelDO.getStatus());
                channelVO.setParentDeviceId(deviceChannelDO.getParentDeviceId());
                channelVO.setStoreSceneId(deviceChannelDO.getStoreSceneId());
                resultList.add(channelVO);
            }
        }
        return resultList;
    }

    public static DeviceChannelVO convert(DeviceChannelDO deviceChannelDO){
        if(Objects.isNull(deviceChannelDO)){
            return null;
        }
        DeviceChannelVO channelVO = new DeviceChannelVO();
        channelVO.setId(deviceChannelDO.getId());
        channelVO.setDeviceId(deviceChannelDO.getDeviceId());
        channelVO.setChannelNo(deviceChannelDO.getChannelNo());
        channelVO.setChannelName(deviceChannelDO.getChannelName());
        channelVO.setHasPtz(!Objects.isNull(deviceChannelDO.getHasPtz()) && deviceChannelDO.getHasPtz());
        channelVO.setStatus(Objects.isNull(deviceChannelDO.getStatus()) ? DeviceStatusEnum.ONLINE.getCode() : deviceChannelDO.getStatus());
        channelVO.setParentDeviceId(deviceChannelDO.getParentDeviceId());
        channelVO.setStoreSceneId(deviceChannelDO.getStoreSceneId());
        return channelVO;
    }
}
