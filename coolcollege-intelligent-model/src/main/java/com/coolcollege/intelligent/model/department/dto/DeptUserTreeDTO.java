package com.coolcollege.intelligent.model.department.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author 邵凌志
 * @date 2020/7/27 9:39
 */
@Data
public class DeptUserTreeDTO implements Serializable {

    /**
     * 部门id/userId
     */
    private String id;

    /**
     * 部门id（人员列表使用）
     */
    private String deptId;

    /**
     * 人员数量
     */
    private long userCount;

    /**
     * 字母分组
     */
    private String key;

    /**
     * 名称
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 父节点
     */
    private String parentId;

    /**
     * 子节点
     */
    private List<DeptUserTreeDTO> children;

    /**
     * 是否是人员
     */
    private boolean userFlag = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeptUserTreeDTO that = (DeptUserTreeDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
