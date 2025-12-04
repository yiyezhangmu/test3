package com.coolcollege.intelligent.model.unifytask.query;

import lombok.Data;

import java.util.List;

/**
 * describe: 陈列任务ES查询
 *
 * @author wangff
 * @date 2024/11/5
 */
@Data
public class DisplayTaskQuery {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 开始日期，yyyy-MM-dd
     */
    private String startTime;

    /**
     * 结束日期，yyyy-MM-dd
     */
    private String endTime;

    /**
     * 店铺id
     */
    private String storeId;

    /**
     * 陈列门店任务状态
     */
    private String status;

    /**
     * 父任务id列表
     */
    private List<Long> unifyTaskIds;

    /**
     * 结果数量
     */
    private Integer returnLimit;
}
