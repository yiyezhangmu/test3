package com.coolcollege.intelligent.service.requestBody.store;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName StoreRequestBody
 * @Description 用一句话描述什么
 */
@Data
public class StoreRequestBody {
    private String store_id;
    private String store_name;
    private String store_num;
    /**
     * 门头照
     */
    private String avatar;
    private String store_area;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String county;
    private String store_address;
    private String location_address;
    /**
     * 经纬度
     */
    private String longitude_latitude;
    private String telephone;
    private String device_id;
    private String remark;

    /**
     * 营业时间
     */
    private String business_hours;
    /**
     * 门店面积
     */
    private String store_acreage;
    /**
     * 门店带宽
     */
    private String store_bandwidth;

    /**
     * 门店分组id
     */
    private String group_ids;

    /**
     * 门店状态（open：营业、closed：闭店、not_open：未开业）
     */
    private String store_status;

    /**
     * 动态扩展字段
     */
    private String extend_field;

    /**
     * 门店证照列表
     */
    private List<JSONObject> storeLicenseInstances;

    /**
     * 第三方管理唯一key
     */
    private String thirdDeptId;


    private String openDate;

    /**
     * 品牌id
     */
    private Long brandId;
}
