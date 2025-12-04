package com.coolcollege.intelligent.model.aliyun;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邵凌志
 * @date 2020/7/15 20:02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreSummaryTaskDO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 租户id
     */
    private String corpId;

    /**
     * 摄像头id
     */
    private String videoId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 创建时间
     */
    private Long createDate;
}
