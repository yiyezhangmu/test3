package com.coolcollege.intelligent.model.video.platform;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/23
 */
@Data
public class LiveVideoDTO {

    private String eid;
    /**
     * 设备id
     */
    private String gbId;

    private String channelNo;

    /**
     * 开始时间 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     */
    private String startTime;

    /**
     * 结束时间    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     */
    private String endTime;

    private Integer protocol;


    /**
     * 视频清晰度，1-高清（主码流）、2-流畅（子码流）	(不传入默认是2)
     */
    private Integer quality;

    /**
     * 录像类型 1.本地回放 2.云录像 默认本地回放
     */
    private Integer recordType;



}
