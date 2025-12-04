package com.coolcollege.intelligent.model.storework.request;

import lombok.Data;

import java.util.Date;

@Data
public class PmdStoreWorkDataListRequest {

    private String timeType;

    private Date timeValue;
}
