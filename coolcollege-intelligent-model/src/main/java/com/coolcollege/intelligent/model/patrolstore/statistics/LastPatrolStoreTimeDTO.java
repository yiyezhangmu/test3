package com.coolcollege.intelligent.model.patrolstore.statistics;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/12/27 11:17
 * @Version 1.0
 */
@Data
public class LastPatrolStoreTimeDTO {

    private String storeId;

    private String storeName;

    private Date signEndTime;
}
