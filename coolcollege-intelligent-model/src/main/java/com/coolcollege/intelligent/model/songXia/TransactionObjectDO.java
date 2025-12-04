package com.coolcollege.intelligent.model.songXia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionObjectDO {

    private Long id;
    private String transactionName;
    private String transactionCode;
    private String channelName;
    private String channelCode;
    private String cropName;
    private String cropCode;
    private String businessRegionName;
    private String businessRegionCode;
    private String businessSegmentName;
    private String businessSegmentCode;
    private Date createTime;
    private String createUserId;
    private Date updateTime;
    private String updateUserId;
}
