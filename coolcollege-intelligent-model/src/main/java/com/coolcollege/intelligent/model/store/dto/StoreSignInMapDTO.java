package com.coolcollege.intelligent.model.store.dto;

import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/10/16 10:22
 */
@Data
public class StoreSignInMapDTO {

    /**
     * 门店id
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店代码
     */
    private String storeCode;

    /**
     * 门店编号
     */
    private String storeNum;

    /**
     * 精度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;

    /**
     * 门店地址
     */
    private String locationAddress;

    /**
     * 门店照
     */
    private String avatar;

    /**
     * 相对于传递经纬度的距离
     */
    private Double distance;

    /**
     * 门店是否有任务在进行
     */
    private Boolean hasTask;

    private Boolean hasB1;

    /**
     * 是否已巡
     */
    private Boolean hasPatroled;

    /**
     * 门店状态（open：营业；closed：闭店；not_open：未开业）
     */
    private String storeStatus;
}
