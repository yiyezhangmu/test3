package com.coolcollege.intelligent.model.songXia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromoterInfoDO {
    private Long id;
    private String promoterUserId;
    private String promoterName;
    private String promoterNum;
    private String promoterType;
    private String categoryName;
    private String categoryCode;
    private String dimissionStatus;
    private String sex;
    private String withoutBasicPay;
    private String withoutCommission;
    private Date birthDate;
    private String phone;
    private String address;
    private String idNumber;
    private String actualStoreNum;
    private String physicalStoreNum;
    private String physicalStoreName;
    private String businessRegionName;
    private String businessRegionCode;
    private String businessSegmentName;
    private String businessSegmentCode;
    private Integer status;
    private String remark;
    private Date createTime;
    private String createUserId;
    private Date updateTime;
    private String updateUserId;
    private Boolean deleted;
    private String insuredPlaceCode;
    private String insuredPlaceName;
    private String minimumWagePlaceCode;
    private String minimumWagePlaceName;
    private String contractNature;
    private String contractNum;
    private Date jobDate;
    private Date resignationDate;
    private String openBank;
    private String openBankCard;
    private BigDecimal basicMonthlyWage;
    private BigDecimal wageRatio;
    private Integer seniorityWage;
    private Integer seniority;
}
