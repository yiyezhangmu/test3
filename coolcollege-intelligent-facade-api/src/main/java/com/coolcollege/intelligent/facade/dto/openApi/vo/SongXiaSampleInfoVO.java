package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongXiaSampleInfoVO {
    private String productModel;
    private String transactionCode;
    private String categoryCode;
    private String categoryName;
    private String yearMonth;
    private Integer amount;
}
