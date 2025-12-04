package com.coolcollege.intelligent.model.impoetexcel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @Author: hu hu
 * @Date: 2025/1/14 16:40
 * @Description:
 */
@Data
public class RoleImportDTO {

    @Excel(name = "岗位编码", orderNum = "1")
    private String thirdUniqueId;

    @Excel(name = "岗位名称", orderNum = "2")
    private String roleName;
}
