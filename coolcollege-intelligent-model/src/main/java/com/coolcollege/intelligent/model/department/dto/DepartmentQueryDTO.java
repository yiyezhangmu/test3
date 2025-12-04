package com.coolcollege.intelligent.model.department.dto;

import com.coolcollege.intelligent.model.page.PageBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName DepartmentQueryDTO
 * @Description 部门查询条件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentQueryDTO extends PageBase {

    private String keyword;
    /**
     * 是否查询用户数量
     * true/false
     */
    private String queryUserCount;

    /**
     * 查询用户的激活类型
     * active/unactive
     */
    private String userType;

    /**
     * 部门ID
     */
    private String deptId;
    /**
     * 多个门店id列表
     */
    private List<String> ids;

    /**
     * 用户id列表
     */
    private List<String> userIds;
    /**
     * 是否选取运营匹配
     */
    private Boolean operator;
    /**
     * 是否匹配店员
     */
    private Boolean clerk;
    /**
     * 是否选择同步店长
     */
    private String user;

}
