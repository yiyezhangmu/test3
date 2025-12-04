package com.coolcollege.intelligent.model.department.dto;

import com.coolcollege.intelligent.model.department.DeptNode;

import java.util.List;

public class DeptSearchRespDTO {
    private List<DeptNode> departments;

    public DeptSearchRespDTO(List<DeptNode> departments) {
        this.departments = departments;
    }

    public List<DeptNode> getDepartments() {
        return departments;
    }

    public void setDepartments(List<DeptNode> departments) {
        this.departments = departments;
    }
}
