package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.page.PageBase;
import lombok.Data;

/**
 * @ClassName EnterpriseUserQueryDTO
 * @Description 部门查询条件
 * @author 首亮
 */
@Data
public class EnterpriseUserQueryDTO extends PageBase {

    private String userName;
}
