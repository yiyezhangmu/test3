package com.coolcollege.intelligent.model.enterpriseMenu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseMenuInfoDO {

    private Integer id;

    private String enterpriseId;

    private String menuInfo;

    private Date createTime;
}
