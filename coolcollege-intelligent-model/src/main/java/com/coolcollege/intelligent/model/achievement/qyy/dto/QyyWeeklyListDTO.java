package com.coolcollege.intelligent.model.achievement.qyy.dto;

import lombok.Data;

import java.util.List;

@Data
public class QyyWeeklyListDTO {
    private String beginDate;

    private  String endDate;

    private Integer pageNum;

    private Integer pageSize;

    private String conversationId;

    private List<String> regionId;

    private List<String> storeId;

    private String storeName;

}
