package com.coolcollege.intelligent.facade.request.patrolstore;

import com.coolcollege.intelligent.facade.request.PageRequest;
import lombok.Data;

/**
 * @author byd
 * @date 2022-07-11 10:30
 */
@Data
public class PatrolStoreDetailListRequest extends PageRequest {

    /**
     * 巡店记录id
     */
    private Long recordId;

    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 统计维度
     * 合格:PASS
     * 不合格:FAIL
     * 不适用:INAPPLICABLE
     */
    private String checkResult;

    /**
     * 开始时间
     */
    private Long beginTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 是否已完成 1：是  0 ：否
     */
    private Boolean isComplete;


    /**
     * 检查表类型
     * 自定义检查表 DEFINE
     * 标准检查表  STANDARD
     */
    private String tableType;
}
