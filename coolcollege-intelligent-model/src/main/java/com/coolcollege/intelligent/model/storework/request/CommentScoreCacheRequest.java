package com.coolcollege.intelligent.model.storework.request;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/10/11 17:49
 * @Version 1.0
 */
@Data
public class CommentScoreCacheRequest {

    private Long dataTableId;

    List<CommentScoreRequest> requestList;

}
