package com.coolcollege.intelligent.facade.dto.openApi.video;

import lombok.Data;

/**
 * @author byd
 * @date 2023-07-31 15:21
 */
@Data
public class LiveWatchDetailApiDTO {


    public Long avgWatchTime;

    public Integer liveUv;

    public Integer msgCount;

    public Integer playbackUv;

    public Integer praiseCount;

    public Integer pv;

    public Long totalWatchTime;

    public Integer uv;
}
