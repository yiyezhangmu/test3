package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/07
 */
@Data
public class EnterpriseLoginDTO {
    private String enterpriseId;
    private String enterpriseName;
    private String userId;
    private String corpId;
    private String originalName;
    private String unionId;
    private String dbName;
}
