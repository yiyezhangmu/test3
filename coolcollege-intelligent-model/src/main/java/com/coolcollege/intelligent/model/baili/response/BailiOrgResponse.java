package com.coolcollege.intelligent.model.baili.response;

import jodd.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/06
 */
@NoArgsConstructor
@Data
public class BailiOrgResponse {
    private Integer unitId;
    private String unitCode;
    private String unitLevelName;
    private Integer unitLevelId;
    private Integer parentId;
    private String name;
    private String enName;
    private String fullName;
    private Integer orgStatus;
    private Integer complement;
    private Integer totalEmployeeCount;
    private Integer acturyCompCount;
    private Integer acturyTotalempCount;
    private String ownerEmployeeName;
    private Integer ownerEmployeeId;
    private Integer sort;
    private Integer oaId;
    private Integer delflag;
    private Integer unitTypeId;
    private String unitTypeName;
    private Integer costCenterId;
    private String costCenter;
    private Integer functionalPropertyId;
    private String functionalProperty;
    private String cityNo;
    private String locationCity;
    private String zoneNo;
    private String zoneName;
    private String provinceNo;
    /**
     * 行政省区名称
     */
    private String provinceName;
    private String managerCityNo;
    private String mangerCity;
    private String bizCityNo;
    private String bizCity;
    private Integer isCenterCity;
    private Integer brandProperty;
    private String storeCode;
    private String storeName;
    private String brand;
    private String otherBrand;
    private Integer bussinessTypeId;
    private String bussinessType;
    private String companyNo;
    private String companyName;
    private String address;
    private String businessAddress;


    /**
     * 行政省区名称
     */
    private String executiveProvinceName;

    /**
     * 行政城市名称
     */
    private String executiveCityName;

    /**
     * 行政区名称
     */
    private String countyName;

    /**
     * 店铺经度
     */
    private String storeLatitude;

    /**
     * 门店维度
     */
    private String storeLongitude;


    /**
     * 是否需要
     */
    private Boolean isNeedEhr = false;

    /**
     * 是否门店
     */
    private Boolean isStoreEhr = false;

}
