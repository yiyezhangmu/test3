package com.coolcollege.intelligent.model.export.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息导出
 * @author ：xugangkun
 * @date ：2021/7/23 11:21
 */
@Data
public class ExternalUserInfoExportDTO {

    @Excel(name = "用户名称", orderNum = "1")
    private String name;

    @Excel(name = "企业工号", orderNum = "2")
    private String jobnumber;

    @Excel(name = "手机号码", orderNum = "3")
    private String mobile;

    @Excel(name = "用户职位", orderNum = "4")
    private String roleName;

    @Excel(name = "管辖区域名称", orderNum = "5")
    private String regionName;

    @Excel(name = "管辖门店名称", orderNum = "6")
    private String storeName;

    @Excel(name = "直属上级", orderNum = "7")
    private String directSuperior;

    @Excel(name = "所属部门", orderNum = "8")
    private String userRegions;

    @Excel(name = "用户分组", orderNum = "9")
    private String userGroups;

    @Excel(name = "用户邮箱", orderNum = "10")
    private String email;

    @Excel(name = "备注", orderNum = "11")
    private String remark;

    @Excel(name = "用户状态", orderNum = "12")
    private String userStatus;

    public static List<ExternalUserInfoExportDTO> convertList(String rootName, List<UserInfoExportDTO> list){
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }
        List<ExternalUserInfoExportDTO> resultList = new ArrayList<>();
        for (UserInfoExportDTO userInfoExport : list) {
            ExternalUserInfoExportDTO result = new ExternalUserInfoExportDTO();
            result.setName(userInfoExport.getName());
            result.setJobnumber(userInfoExport.getJobnumber());
            result.setMobile(userInfoExport.getMobile());
            result.setRoleName(userInfoExport.getRoleName());
            result.setRegionName(userInfoExport.getRegionName());
            result.setStoreName(userInfoExport.getStoreName());
            result.setDirectSuperior(userInfoExport.getDirectSuperior());
            result.setUserRegions(userInfoExport.getUserRegions().replaceAll(rootName, ""));
            result.setUserGroups(userInfoExport.getUserGroups());
            result.setEmail(userInfoExport.getEmail());
            result.setRemark(userInfoExport.getRemark());
            result.setUserStatus(userInfoExport.getUserStatus());
            resultList.add(result);
        }
        return resultList;
    }
}
