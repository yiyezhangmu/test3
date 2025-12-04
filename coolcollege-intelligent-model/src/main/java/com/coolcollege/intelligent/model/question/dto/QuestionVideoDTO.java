package com.coolcollege.intelligent.model.question.dto;

import lombok.Data;

/**
 * @author byd
 * @date 2021-12-29 14:28
 */
@Data
public class QuestionVideoDTO {

    /**
     * 视频是否转码成功
     */
    private Boolean isComplete;

    /**
     * 返回的视频地址
     */
    private String videos;
}
