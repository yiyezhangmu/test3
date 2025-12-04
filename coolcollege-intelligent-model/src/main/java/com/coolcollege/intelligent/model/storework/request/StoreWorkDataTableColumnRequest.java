package com.coolcollege.intelligent.model.storework.request;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/10/19 10:41
 * @Version 1.0
 */
@Data
public class StoreWorkDataTableColumnRequest {
    private List<Long> dataTableIds;

    private String businessId;

    private List<String> checkResultList;

    /**
     * AI检查结果列表
     */
    private List<String> aiCheckResultList;
}
