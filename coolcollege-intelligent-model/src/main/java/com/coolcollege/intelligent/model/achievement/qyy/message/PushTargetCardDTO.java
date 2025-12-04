package com.coolcollege.intelligent.model.achievement.qyy.message;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PushTargetCardDTO {
    private BigDecimal goalAmt;

    private BigDecimal unitYieldTarget;

    private BigDecimal salesTarget;

    private String pcUrl;

    private String iosUrl;

    private String androidUrl;

    private String pepTalk;
}
