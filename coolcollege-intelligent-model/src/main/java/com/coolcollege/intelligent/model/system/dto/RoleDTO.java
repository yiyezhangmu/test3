package com.coolcollege.intelligent.model.system.dto;

import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/12/08
 */
@Data
public class RoleDTO extends SysRoleDO {
    /**
     * 人员数量
     */
    private Integer userCount;

    /**
     * 是否可以被删除
     */
    private Boolean isDelete;

    /**
     * 职位类型名称
     */
    private String positionTypeName;

    /**
     * 可视化范围名称
     */
    private String roleAuthName;

    private String createUserName;

    private String updateUserName;

    private Integer homeTemplateId;

    private String homeTemplateName;
}
