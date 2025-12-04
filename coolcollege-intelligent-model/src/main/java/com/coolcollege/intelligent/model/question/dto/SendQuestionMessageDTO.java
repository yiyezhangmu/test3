package com.coolcollege.intelligent.model.question.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SendQuestionMessageDTO {

    private String enterpriseId;

    private Long unifyTaskId;

    private String storeId;

    private Long loopCount;

    private String operate;

    Map<String, String> paramMap;

}
