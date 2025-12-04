package com.coolcollege.intelligent.model.patrolstore.request;

import com.coolcollege.intelligent.model.page.PageRequest;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/6 14:33
 * @Version 1.0
 */
@Data
public class PatrolStoreDetailRequest extends PageRequest {

    /**
     * 检查表ID
     */
    private Long metaTableId;

    /**
     * 开始时间
     */
    private Date beginTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 巡店类型
     */
    private List<String> patrolTypeList;

    /**
     * 巡店方式 1 自主巡店 2 巡店任务
     */
    private List<Integer> patrolStoreMode;



}
