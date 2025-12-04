package com.coolcollege.intelligent.model.store.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 门店覆盖
 * @Author chenyupeng
 * @Date 2021/7/13
 * @Version 1.0
 */
@Data
public class StoreCoverVO {
    /**
     * 门店名称
     */
    String storeName;

    /**
     * 总巡店次数
     */
    Integer checkRecordNum;

    /**
     * 门店id
     */
    String storeId;

    /**
     * 最后巡店人
     */
    String userName;

    /**
     * 门店状态（open：营业、closed：闭店、not_open：未开业）
     */
    String storeStatus;

    /**
     * 最后巡店时间
     */
    private Date lastTime;
}
