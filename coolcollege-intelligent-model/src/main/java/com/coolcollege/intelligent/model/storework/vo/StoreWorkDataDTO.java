package com.coolcollege.intelligent.model.storework.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/29 18:57
 * @Version 1.0
 */
@Data
public class StoreWorkDataDTO {

    private String storeId;

    private String storeName;

    private List<Long> dataTableIds;
}
