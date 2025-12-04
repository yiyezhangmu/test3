package com.coolcollege.intelligent.model.patrolstore.records;

import lombok.Data;

import java.util.Date;

@Data
public class PatrolRecordsTableQuery {
    /**
     * 门店区域id
     */
    private String areaId;
    /**
     * 开始时间
     */
    private Date beginDate;

    /**
     * 结束时间
     */
    private Date endDate;

}
