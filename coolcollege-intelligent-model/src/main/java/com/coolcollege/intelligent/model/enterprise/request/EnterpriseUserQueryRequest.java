package com.coolcollege.intelligent.model.enterprise.request;

import lombok.Data;


@Data
public class EnterpriseUserQueryRequest {

    private String userId;
    private Integer pageNum=1;
    private Integer pageSize=10;
}
