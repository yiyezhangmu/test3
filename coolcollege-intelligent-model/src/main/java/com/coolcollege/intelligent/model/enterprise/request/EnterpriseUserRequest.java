package com.coolcollege.intelligent.model.enterprise.request;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName EnterpriseUserDO
 * @Description 用于封装EnterpriseUserDO和部门字段
 * @author 首亮
 */
@Data
public class EnterpriseUserRequest {

    private EnterpriseUserDO enterpriseUserDO;

    private String department;

    private String departments;

    private List<String> departmentLists;

    private List<String> leaderInDepts;
}
