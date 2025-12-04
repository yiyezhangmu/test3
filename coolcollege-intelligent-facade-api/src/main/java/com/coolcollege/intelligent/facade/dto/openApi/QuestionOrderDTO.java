package com.coolcollege.intelligent.facade.dto.openApi;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/7/15 13:49
 * @Version 1.0
 */
@Data
public class QuestionOrderDTO {

    /**
     * 企业id
     */
    private String eid;

    /**
     * 工号
     */
    private List<String> jobNumList;

    /**
     * 工单类型
     */
    private String questionOrderCode;
}
