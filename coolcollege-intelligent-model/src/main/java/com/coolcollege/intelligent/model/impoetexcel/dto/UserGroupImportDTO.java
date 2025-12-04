package com.coolcollege.intelligent.model.impoetexcel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;


/**
 * @author byd
 */
@Data
public class UserGroupImportDTO {

    @Excel(name = "账号ID", orderNum = "1", width = 20)
    private String userId;

    @Excel(name = "企业工号", orderNum = "2", width = 15)
    private String jobNumber;

    @Excel(name = "用户名称", orderNum = "3", width = 10)
    private String name;

    @Excel(name = "用户组", orderNum = "4", width = 10)
    private String userGroupName;

    @Excel(name = "描述", orderNum = "0", width = 10)
    private String desc;
}
