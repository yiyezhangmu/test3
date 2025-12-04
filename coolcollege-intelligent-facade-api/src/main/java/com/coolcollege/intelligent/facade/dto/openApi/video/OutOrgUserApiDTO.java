package com.coolcollege.intelligent.facade.dto.openApi.video;

import lombok.Data;

/**
 * @author byd
 * @date 2023-07-31 15:30
 */
@Data
public class OutOrgUserApiDTO {


    public String name;

    public Long watchLiveTime;

    public Long watchPlaybackTime;

    public Long watchProgressMs;
}
