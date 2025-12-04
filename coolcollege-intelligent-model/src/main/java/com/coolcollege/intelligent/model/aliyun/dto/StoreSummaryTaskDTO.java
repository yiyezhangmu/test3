package com.coolcollege.intelligent.model.aliyun.dto;

import lombok.Data;

import java.util.concurrent.Future;

/**
 * @author 邵凌志
 * @date 2020/7/15 20:26
 */
@Data
public class StoreSummaryTaskDTO {

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
     * 异步任务
     */
    private Future futureResult;
}
