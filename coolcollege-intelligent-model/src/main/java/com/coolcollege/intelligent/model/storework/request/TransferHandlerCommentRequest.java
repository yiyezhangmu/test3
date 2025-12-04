package com.coolcollege.intelligent.model.storework.request;

import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/23 17:20
 * @Version 1.0
 */
@Data
public class TransferHandlerCommentRequest {

    private List<String> handleUserIds;

    private List<String> commentUserIds;

    private List<Long> storeWorkDataTableIds;

}
