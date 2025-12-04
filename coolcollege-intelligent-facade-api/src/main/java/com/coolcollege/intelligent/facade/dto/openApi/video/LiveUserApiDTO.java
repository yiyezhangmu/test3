package com.coolcollege.intelligent.facade.dto.openApi.video;

import lombok.Data;

/**
 * @author byd
 * @date 2023-07-31 15:25
 */
@Data
public class LiveUserApiDTO {

    public String deptName;

    public String name;

    public String unionId;

    public String userId;

    public Long watchPlaybackTime;

    public Long watchProgressMs;

    public Long watchLiveTime;
}
