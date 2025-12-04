package com.coolcollege.intelligent.model.department.dto;

import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName DepartmentUpdateQueryDTO
 * @Description 更新人员、门店、岗位查询条件
 */
@Data
public class DepartmentUpdateQueryDTO {
    /**
     * 查询分类
     */
    private String type;
    /**
     * 新增的同步ids
     */
    private List<StoreDTO> syncIds;
    /**
     * 删除的ids
     */
    private List<StoreDTO> deleteIds;
}
