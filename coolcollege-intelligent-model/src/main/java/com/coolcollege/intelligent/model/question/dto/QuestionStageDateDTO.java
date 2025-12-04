package com.coolcollege.intelligent.model.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author suzhuhong
 * @Date 2022/8/16 11:11
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionStageDateDTO {

    private String status;

    private Integer stageQuestionCount;

    private Integer passCount;

    private Integer rejectCount;

}
