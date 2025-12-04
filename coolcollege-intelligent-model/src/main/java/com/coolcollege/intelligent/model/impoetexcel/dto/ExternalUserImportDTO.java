package com.coolcollege.intelligent.model.impoetexcel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @author 邵凌志
 * @date 2020/12/15 14:48
 */
@Data
public class ExternalUserImportDTO {

    @Excel(name = "描述", width = 30)
    private String dec;

    @Excel(name = "用户名称(必填)", orderNum = "2", width = 10)
    private String name;

    @Excel(name = "企业工号", orderNum = "3", width = 10)
    private String jobnumber;

    @Excel(name = "手机号码(必填)", orderNum = "4", width = 10)
    private String mobile;

    @Excel(name = "用户职位(必填)", orderNum = "5", width = 10)
    private String positionName;

    @Excel(name = "管辖区域名称", orderNum = "6", width = 10)
    private String regionName;

    @Excel(name = "管辖门店名称", orderNum = "7", width = 10)
    private String storeName;

    @Excel(name = "直属上级", orderNum = "8", width = 10)
    private String directSuperior;

    @Excel(name = "所属部门", orderNum = "9", width = 10)
    private String userRegions;

    @Excel(name = "用户分组", orderNum = "10", width = 10)
    private String userGroups;

    @Excel(name = "用户邮箱", orderNum = "11", width = 10)
    private String email;

    @Excel(name = "备注", orderNum = "12", width = 50)
    private String remark;

    @Excel(name = "用户状态", orderNum = "13", width = 50)
    private String userStatusString;
}
