package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoolAppCardDTO {
    private String storeName;
    private String potralTime;
    private BigDecimal countSum;
    private BigDecimal scoreRate;
    private Integer countColumn;
    private Integer qualifiedItem;
    private Integer nonconformingItem;
    private String PCcheckUrl;
    private String AndroidcheckUrl;
    private String IOScheckUrl;
    private String workHome;
}
