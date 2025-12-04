package com.coolcollege.intelligent.model.login;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/27
 */
@Data
public class EnterpriseLoginCountDTO {
    private String enterpriseId;
    private String enterpriseName;
    private String enterpriseMobile;
    private Integer userLoginCount;
    private Integer allUserCount;
    private Integer loginCount;
    private Long lastLoginTime;
    private String lastLoginStr;

}
