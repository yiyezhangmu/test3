package com.coolcollege.intelligent.model.patrolstore.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author shuchang.wei
 * @date 2021/7/8 16:12
 */
@Data
public class ColumnDetailListDTO {
    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 检查时间
     */
    private Date checkTime;

    /**
     * 检查状态
     */
    private String checkStatus;

    /**
     * 检查人
     */
    private String checkPersonName;

    /**
     * 检查人id
     */
    private String checkPersonId;
}
