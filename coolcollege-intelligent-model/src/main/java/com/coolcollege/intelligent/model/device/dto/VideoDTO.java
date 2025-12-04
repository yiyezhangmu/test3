package com.coolcollege.intelligent.model.device.dto;

import com.coolstore.base.enums.VideoProtocolTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author 邵凌志
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class VideoDTO {
    @ApiModelProperty("corpId")
    private String corpId;

    /**
     * 设备通道号
     */
    @ApiModelProperty("设备通道号")
    private String channelNo;

    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("结束时间")
    private String endTime;

    /**
     * 萤石云流播放协议，1-ezopen、2-hls、3-rtmp、4-flv，默认为1
     */
    @ApiModelProperty("")
    private VideoProtocolTypeEnum protocol;

    /**
     * 视频清晰度，1-高清（主码流）、2-流畅（子码流）	(不传入默认是1) 对接的各个平台 如果有三个清晰度取最高和最低
     */
    @ApiModelProperty("视频清晰度 1-高清（主码流）、2-流畅（子码流） 3 清晰  4超高清")
    private Integer quality;

    @ApiModelProperty("倍数播放 0.25、0.5、1、2、4倍速")
    private String speed;

    @ApiModelProperty("流id")
    private String streamId;

    @ApiModelProperty("请判断播放端是否要求播放视频为H265编码格式,1表示需要，0表示不要求")
    private String supportH265;

    @ApiModelProperty("录像类型，1-按周录像，2-按时间段录像")
    private String recType;

    @ApiModelProperty("过期时长，单位秒")
    private Integer expireTime;

    @ApiModelProperty("切片类型，ts，2-mp4")
    private String sliceType;

    @ApiModelProperty("tp 客户端类型，WindowsSDKClient：Windows平台，iOSSDKClient：iOS平台，AndroidSDKClient：安卓平台 基于浏览器web的SDK，取值暂无限制，一般传浏览器类型加版本号，例如chrome 99.0")
    private String clientType;

    @ApiModelProperty("video 预览， sdvod 回放， dtspk双向语音")
    private String streamType;


    public static int getTpQuality(Integer quality) {
        if(quality == null){
            return 1;
        }
        //视频清晰度 1-高清（主码流）、2-流畅（子码流） 3 清晰  4超高清
        if (quality == 1) {
            return 2;
        } else if (quality == 2) {
            return 0;
        } else if (quality == 3) {
            return 1;
        } else if (quality == 4) {
            return 3;
        }
        return 1;
    }


}
