package com.coolcollege.intelligent.model.device.request;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.device.dto.VideoDTO;
import com.coolstore.base.enums.VideoProtocolTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Objects;

@Data
public class OpenDeviceVideoRequest {

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("设备通道号")
    private String channelNo;

    @Min(1)
    @Max(2)
    @ApiModelProperty("1直播，2本地录像回放")
    private Integer type;

    @ApiModelProperty("播放协议 hls、rtmp、flv")
    private String protocol;

    @Min(1)
    @Max(2)
    @ApiModelProperty("视频清晰度，1-高清（主码流）、2-流畅（子码流）")
    private Integer quality;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开始时间")
    private String startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("倍数播放 0.25、0.5、1、2、4倍速")
    private String speed;

    @ApiModelProperty("是否支持H265编码，0-不支持（默认），1-支持")
    private String supportH265;

    public boolean check() {
        if (StringUtils.isAnyBlank(deviceId, channelNo) || Objects.isNull(type)) {
            return false;
        }
        if(!Constants.INDEX_ONE.equals(type)){
            if(StringUtils.isAnyBlank(startTime, endTime)){
                return false;
            }
            //必须是同一天
            return startTime.substring(0, 10).equals(endTime.substring(0, 10));
        }
        return true;
    }

    public static VideoDTO convert(OpenDeviceVideoRequest request){
        VideoDTO result = new VideoDTO();
        result.setDeviceId(request.getDeviceId());
        result.setChannelNo(request.getChannelNo());
        VideoProtocolTypeEnum protocolType = VideoProtocolTypeEnum.getProtocolType(request.getProtocol());
        result.setProtocol(protocolType);
        result.setQuality(Objects.isNull(request.getQuality()) ?  1 : request.getQuality());
        result.setStartTime(request.getStartTime());
        result.setEndTime(request.getEndTime());
        result.setSpeed(request.getSpeed());
        result.setSupportH265(request.getSupportH265());
        return result;
    }

}
