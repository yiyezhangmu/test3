package com.coolcollege.intelligent.model.order;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/02
 */
@Data
public class EnterpriseOrderConsumerDO {
    private Long id;
    private String goodsCode;
    private String itemCode;
    private Long totalActualPayFee;
    private Date payTime;
    private String buyCorpId;
    private Integer quantity;
    private String status;
    private String bizOrderId;
    private String orderSource;
    private String handlerUserName;
    private String handlerUserId;
    private Integer dealStatus;
}
