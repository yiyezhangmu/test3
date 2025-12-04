package com.coolcollege.intelligent.facade.dto.openApi.video;

import lombok.Data;

/**
 * @author byd
 * @date 2023-07-31 14:42
 */
@Data
public class VideLiveInfoApiDTO {


    public String coverUrl;

    public Long duration;

    public Long endTime;

    public String introduction;

    public String liveId;

    public String livePlayUrl;

    public Integer liveStatus;

    public Long playbackDuration;

    public Long startTime;

    public Integer subscribeCount;

    public String title;

    public String unionId;

    public String userId;

    public Integer uv;
}
