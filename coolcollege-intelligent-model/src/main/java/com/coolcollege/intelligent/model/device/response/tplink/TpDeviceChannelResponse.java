package com.coolcollege.intelligent.model.device.response.tplink;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.device.DeviceStatusEnum;
import com.coolcollege.intelligent.model.device.dto.OpenChannelDTO;
import com.coolcollege.intelligent.model.device.dto.OpenDeviceDTO;
import com.coolstore.base.enums.YunTypeEnum;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Data
public class TpDeviceChannelResponse {

    private int channelNum;

    private int connectedChannelNum;

    private List<ChannelResponse> channelList;

    @Data
    public static class ChannelResponse{

        @ApiModelProperty(value = "设备名称")
        private String deviceName;

        @ApiModelProperty(value = "设备类型")
        private String deviceType;

        @ApiModelProperty(value = "设备状态 0 离线 ,1 在线,2 重启中，3 升级中,4 配置中，5 同步中,6 等待升级")
        private int deviceStatus;

        @ApiModelProperty(value = "0:受限关闭 1:适用中 2付费使用中")
        private int openStatus;

        @ApiModelProperty(value = "设备型号")
        private String deviceModel;

        @ApiModelProperty(value = "所属项目id")
        private String projectId;

        @ApiModelProperty(value = "ip地址")
        private String ip;

        @ApiModelProperty(value = "mac地址")
        private String mac;

        @ApiModelProperty(value = "通道号")
        private int channel;
    }

    public static List<OpenChannelDTO> convert(String parentDeviceId, TpDeviceChannelResponse response){
        if(response == null || CollectionUtils.isEmpty(response.getChannelList())){
            return null;
        }
        List<OpenChannelDTO> channels = Lists.newArrayList();
        for (ChannelResponse channelResponse : response.channelList) {
            OpenChannelDTO channel = new OpenChannelDTO();
            channel.setParentDeviceId(parentDeviceId);
            channel.setDeviceId(channelResponse.getMac());
            channel.setChannelNo(channelResponse.getChannel()+"");
            channel.setChannelName(channelResponse.getDeviceName());
            channel.setStatus(Constants.INDEX_ONE.equals(channelResponse.getDeviceStatus())  ? DeviceStatusEnum.ONLINE.getCode() : DeviceStatusEnum.OFFLINE.getCode());
            channel.setSource(YunTypeEnum.TP_LINK.getCode());
            channel.setSupportCapture(Constants.ZERO);
            channels.add(channel);
        }
        return channels;
    }

}
