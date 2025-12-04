package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongXiaSalesInfoVO {
    private String productModel;
    private String categoryName;
    private String categoryCode;
    private String physicalStoreNum;
    private String transactionCode;
    private String reportDate;
    private String reportType;
    private Integer amount;
    private String money;
}
