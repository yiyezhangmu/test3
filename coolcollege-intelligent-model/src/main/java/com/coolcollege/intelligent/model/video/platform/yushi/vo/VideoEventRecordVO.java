package com.coolcollege.intelligent.model.video.platform.yushi.vo;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/26
 */
@Data
public class VideoEventRecordVO {
    private Long id;

    //主键
    private Integer alarmCount;
    //门店ID
    private String storeId;
    /**
     * 门店
     */
    private String storeName;
}
