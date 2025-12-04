package com.coolcollege.intelligent.model.enterprise.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @ClassName AddressBookUserDTO
 * @Description 通讯录
 * @author 首亮
 */
@Data
public class AddressBookUserDTO extends EnterpriseUserDTO{
    /**
     * 序号
     */
    @Excel(name = "序号", width = 10, orderNum = "1")
    private Integer orderNum;

    /**
     * 部门
     */
    @Excel(name = "部门", width = 30, orderNum = "5")
    private String depts;
    /**
     * 岗位
     */
    @Excel(name = "岗位", width = 30, orderNum = "6")
    private String positions;

    public String getDepts() {
        return CollectionUtils.isNotEmpty(getDepartmentNames()) ? String.join(",", getDepartmentNames()) : depts;
    }

    public String getPositions() {
        return CollectionUtils.isNotEmpty(getPositionNames()) ? String.join(",", getPositionNames()) : positions;
    }
}
