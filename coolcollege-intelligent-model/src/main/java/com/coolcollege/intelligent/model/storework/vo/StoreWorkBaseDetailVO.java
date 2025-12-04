package com.coolcollege.intelligent.model.storework.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/10/14 10:56
 * @Version 1.0
 */
@Data
public class StoreWorkBaseDetailVO {

    private String tcBusinessId;

    private Long storeWorkId;

    private String workCycle;

    private Date storeWorkDate;

    private String storeId;

    private String storeName;
}
