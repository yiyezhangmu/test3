package com.coolcollege.intelligent.model.device.response.tplink;

import com.coolcollege.intelligent.model.device.vo.DeviceVideoRecordVO;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class TPLocalVideoResponse {

    private String userId;

    private List<TPLocalFileResponse> videos;

    public static List<DeviceVideoRecordVO> convertList(String deviceId, String channelNo, TPLocalVideoResponse response){
        if(Objects.isNull(response)){
            return null;
        }
        return response.getVideos().stream().map(item -> convert(deviceId, channelNo, response.getUserId(), item)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static DeviceVideoRecordVO convert(String deviceId, String channelNo, String userId, TPLocalFileResponse response){
        if(Objects.isNull(response)){
            return null;
        }
        DeviceVideoRecordVO result = new DeviceVideoRecordVO();
        result.setChannelNo(channelNo);
        result.setDeviceSerial(deviceId);
        result.setStartTime(response.getStartTime());
        result.setEndTime(response.getEndTime());
        result.setRecType(2);
        result.setUserId(userId);
        return result;
    }

}
