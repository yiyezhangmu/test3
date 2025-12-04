package com.coolcollege.intelligent.model.patrolstore.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StatisticsStaColumnRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 门店区域id
     */
    private Long tableId;
    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 结束时间
     */
    private Date endDate;

    private Integer pageNum = 1;

    private Integer pageSize = 500;
}
