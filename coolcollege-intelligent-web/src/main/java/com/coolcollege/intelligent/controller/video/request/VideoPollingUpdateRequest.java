package com.coolcollege.intelligent.controller.video.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/20
 */
@Data
public class VideoPollingUpdateRequest {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("device_id_list")
    private List<String> deviceIdList;

    @JsonProperty("video_polling_name")
    private String videoPollingName;

    @JsonProperty("split_screen_num")
    private int splitScreenNum;

    @JsonProperty("play_interval")
    private int playInterval;
}
