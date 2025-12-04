package com.coolcollege.intelligent.model.question.dto;

import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/12/20 15:56
 * @Version 1.0
 */
@Data
public class StoreQuestionDTO {

    private String storeId;

    /**
     * 未处理工单数量
     */
    private Integer unHandleQuestionCount;
}
