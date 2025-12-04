package com.coolcollege.intelligent.model.storework.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StoreWorkSendMessageDTO {

    private String enterpriseId;

    private Long dataTableId;

    private String operate;

    Map<String, String> paramMap;
}
