package com.coolcollege.intelligent.model.achievement.qyy.message;

import lombok.Data;

/**
 * 大单笔数top
 */
@Data
public class BigOrderByTopTenDTO {

    private String storeName;

    private Long orderCount;

    private Long totalOrderCount;

    private String rankIcon;

}
