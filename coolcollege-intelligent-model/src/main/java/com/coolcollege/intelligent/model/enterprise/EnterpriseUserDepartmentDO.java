package com.coolcollege.intelligent.model.enterprise;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/17
 */
@Data
public class EnterpriseUserDepartmentDO {
    private Integer id ;
    private String userId;
    private String departmentId;
    private Date createTime;
    private Date updateTime;
    private Boolean isHasAuth;
    public EnterpriseUserDepartmentDO(String userId, String departmentId, Boolean isHasAuth) {
        this.userId = userId;
        this.departmentId = departmentId;
        this.isHasAuth = isHasAuth;
    }
    public EnterpriseUserDepartmentDO() {}
}
