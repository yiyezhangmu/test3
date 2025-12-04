package com.coolcollege.intelligent.common.enums.meta;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wxp
 * @FileName: MengZiYuanTableWeightEnum
 * @Description: 蒙自源POC环境 指定检查表权重
 * @date 2024-05-17 19:23
 */
public enum MengZiYuanTableWeightEnum {

    PRODUCT(9L, "一：产品项稽核表", BigDecimal.valueOf(0.4)),
    ENVIRONMENT(10L, "二：环境项稽核表", BigDecimal.valueOf(0.4)),
    SERVICE(11L, "三：服务项稽核表", BigDecimal.valueOf(0.2)),
    FOOD_SAFETY(12L, "食品安全“F”项问题稽核表", BigDecimal.valueOf(-10)),
    ;

    MengZiYuanTableWeightEnum(Long id, String name, BigDecimal weight) {
        this.id = id;
        this.name = name;
        this.weight = weight;
    }

    private Long id;

    private String name;

    private BigDecimal weight;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    /**
     * 获取列表id
     * @return
     */
    public static Set<Long> getCustomTableIdSet(){
        Set<Long> resultList = new HashSet<>();
        resultList.add(PRODUCT.id);
        resultList.add(ENVIRONMENT.id);
        resultList.add(SERVICE.id);
        resultList.add(FOOD_SAFETY.id);
        return resultList;
    }
}
