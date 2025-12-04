package com.coolcollege.intelligent.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * @author byd
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionDTO {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;
    /**
     * 父ID
     */
    private String parentId;


    /**
     * dinging部门id
     */
    private String synDingDeptId;

    /**
     * 是否删除标记
     */
    private Boolean deleted;

    /**
     * 区域门店数量
     */
    private Integer storeNum;

    /**
     * 区域门店范围是否
     */
    private Boolean storeRange = false;

    /**
     * 门店地址 非DO
     */
    private String address;

    /**
     * 门店经度 非DO
     */
    private String longitude;


    /**
     * 纬度 非DO
     */
    private String latitude;

    /**
     * 门店编号 非DO
     */
    private String storeCode;


    /**
     * 大区名称
     */
    private String zoneName;

    /**
     * brand 主品牌
     */
    private String brand;

    /**
     * 管理分区
     */
    private String mangerCity;

    /**
     * 经营城市
     */
    private String bizCity;

    /**
     * 省区
     */
    private String provinceName;

    /**
     * 营业状态（open：营业；closed：闭店；not_open：未开业）
     */
    private String storeStatus;

    /**
     * 开店日期
     */
    private String openDate;
    /**
     * 第三方区域类型
     */
    private String thirdRegionType;


    private String regionType;

}
