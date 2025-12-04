package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.department.dto.DeptUserTreeDTO;
import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/9/25 11:45
 */
@Data
public class DeptUserDTO {

    /**
     * 部门id
     */
    private String deptId;

    /**
     * 部门人员列表
     */
    private List<DeptUserTreeDTO> deptUsers;
}
