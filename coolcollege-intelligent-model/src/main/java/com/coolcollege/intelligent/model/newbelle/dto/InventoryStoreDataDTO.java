package com.coolcollege.intelligent.model.newbelle.dto;

import lombok.Data;

/**
 * 百丽货品反馈-半年内有库存店铺数据
 */
@Data
public class InventoryStoreDataDTO {
    private String brand_no;    //品牌部名称
    private String brand_detail_no;    //品牌编码
    private String managing_city_no;    //管理城市编码
    private String managing_city_name;    //管理城市名称
    private String region_no;    //地区编码
    private String region_name;    //地区名称
    private String store_new_no;    //新店店铺编码/仓库编码
    private String store_name;    //店铺名称
    private String store_level_no;    //店铺级别编码
    private String store_level_name;    //店铺级别名称

}
