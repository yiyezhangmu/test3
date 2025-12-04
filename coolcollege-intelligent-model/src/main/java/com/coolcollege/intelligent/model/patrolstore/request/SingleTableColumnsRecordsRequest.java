package com.coolcollege.intelligent.model.patrolstore.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SingleTableColumnsRecordsRequest {

    private List<Long> tableId;


    private Integer pageSize;

    private Integer pageNum;

    private Date  beginDate;

    private Date endDate;

    private String tableType;
}
