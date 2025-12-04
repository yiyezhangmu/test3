package com.coolcollege.intelligent.facade.dto.openApi.video;

import lombok.Data;

/**
 * @author byd
 * @date 2023-07-31 11:02
 */
@Data
public class VideoCreateApiDTO {
    public String liveId;
    private String unionId;
    private String title;
    private String introduction;
    private String coverUrl;
    private Long preStartTime;
    private Long preEndTime;
    private String userId;
    private Long publicType;
}
