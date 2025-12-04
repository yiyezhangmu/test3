package com.coolcollege.intelligent.facade.dto.user;

import lombok.Data;

import java.util.Date;

@Data
public class EnterpriseUserAllFacadeDTO {
    private String userId;

    private String name;

    private String jobNumber;

    private String avatar;

    private Date updateTime;
}
