package com.coolcollege.intelligent.model.export.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO;
import lombok.Data;

import java.util.List;

/**
 * 分组用户信息导出
 * @author ：wxp
 * @date ：2023/1/4 11:21
 */
@Data
public class GroupUserInfoExportDTO {

    @Excel(name = "姓名")
    private String name;

    @Excel(name = "用户分组")
    private String groupName;

    @Excel(name = "区域权限名称")
    private String regionName;

    @Excel(name = "门店权限名称")
    private String storeName;

    @Excel(name = "用户权限")
    private String authUserName;

    @Excel(name = "职位")
    private String roleName;

    @Excel(name = "部门")
    private String department;

    @Excel(name = "手机号")
    private String mobile;

    @Excel(name = "邮箱")
    private String email;

    @Excel(name = "门店数")
    private Integer storeCount;

    @Excel(name = "企业工号")
    private String jobnumber;

    @Excel(name = "账号ID")
    private String userId;
}
