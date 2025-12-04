package com.coolcollege.intelligent.model.export.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 用户信息导出
 * @author ：xugangkun
 * @date ：2021/7/23 11:21
 */
@Data
public class UserInfoExportDTO {
    /**
     * 用户id
     */
    @Excel(name = "用户ID", orderNum = "1")
    private String userId;

    @Excel(name = "用户名称", orderNum = "2")
    private String name;

    @Excel(name = "企业工号", orderNum = "3")
    private String jobnumber;

    @Excel(name = "手机号码", orderNum = "4")
    private String mobile;

    @Excel(name = "用户职位", orderNum = "5")
    private String roleName;

    @Excel(name = "管辖区域名称", orderNum = "6")
    private String regionName;

    @Excel(name = "管辖门店名称", orderNum = "7")
    private String storeName;

    @Excel(name = "直属上级", orderNum = "8")
    private String directSuperior;

    @Excel(name = "所属部门", orderNum = "9")
    private String userRegions;

    @Excel(name = "用户分组", orderNum = "10")
    private String userGroups;

    @Excel(name = "用户邮箱", orderNum = "11")
    private String email;

    @Excel(name = "备注", orderNum = "12")
    private String remark;

    @Excel(name = "用户状态", orderNum = "13")
    private String userStatus;

    @Excel(name = "创建人", orderNum = "14")
    private String createUserName;

    @Excel(name = "创建时间", orderNum = "15")
    private String createTimeStr;

    @Excel(name = "更新时间", orderNum = "16")
    private String updateTimeStr;
}
