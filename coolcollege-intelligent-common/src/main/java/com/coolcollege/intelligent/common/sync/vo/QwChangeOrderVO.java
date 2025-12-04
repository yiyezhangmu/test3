package com.coolcollege.intelligent.common.sync.vo;

import lombok.Data;

/**
 * 企微改单实体
 * @author xugk
 */
@Data
public class QwChangeOrderVO {

    private String suiteId;

    private String corpId;

    private Long timeStamp;

    private String oldOrderId;

    private String newOrderId;

    private String appType;


}
