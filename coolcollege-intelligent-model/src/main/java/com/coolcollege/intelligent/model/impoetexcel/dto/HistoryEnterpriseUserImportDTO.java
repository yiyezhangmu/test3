package com.coolcollege.intelligent.model.impoetexcel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: HistoryEnterpriseUserImportDTO
 * @Description:
 * @date 2023-09-18 14:46
 */
@Data
public class HistoryEnterpriseUserImportDTO {

    @Excel(name = "描述", width = 30)
    private String dec;

    @Excel(name = "用户ID", orderNum = "1", width = 10)
    private String userId;

    @Excel(name = "用户名称", orderNum = "2", width = 10)
    private String name;

    @Excel(name = "企业工号", orderNum = "3", width = 10)
    private String jobnumber;

    @Excel(name = "手机号码", orderNum = "4", width = 10)
    private String mobile;

    @Excel(name = "用户职位", orderNum = "5", width = 10)
    private String positionName;

    @Excel(name = "管辖区域名称", orderNum = "6", width = 10)
    private String regionName;

    @Excel(name = "管辖门店名称", orderNum = "7", width = 10)
    private String storeName;

    @Excel(name = "直属上级", orderNum = "8", width = 10)
    private String directSuperior;

    @Excel(name = "用户分组", orderNum = "9", width = 10)
    private String userGroups;

    @Excel(name = "用户邮箱", orderNum = "10", width = 10)
    private String email;

    @Excel(name = "备注", orderNum = "11", width = 50)
    private String remark;

    @Excel(name = "用户状态", orderNum = "12", width = 50)
    private String userStatusString;

    public static List<UserImportDTO> convertList(List<HistoryEnterpriseUserImportDTO> list){
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }
        List<UserImportDTO> resultList = new ArrayList<>();
        for (HistoryEnterpriseUserImportDTO historyEnterpriseUserImport : list) {
            UserImportDTO result = new UserImportDTO();
            result.setUserId(historyEnterpriseUserImport.getUserId());
            result.setName(historyEnterpriseUserImport.getName());
            result.setJobnumber(historyEnterpriseUserImport.getJobnumber());
            result.setMobile(historyEnterpriseUserImport.getMobile());
            result.setPositionName(historyEnterpriseUserImport.getPositionName());
            result.setRegionName(historyEnterpriseUserImport.getRegionName());
            result.setStoreName(historyEnterpriseUserImport.getStoreName());
            result.setDirectSuperior(historyEnterpriseUserImport.getDirectSuperior());
            result.setUserGroups(historyEnterpriseUserImport.getUserGroups());
            result.setEmail(historyEnterpriseUserImport.getEmail());
            result.setRemark(historyEnterpriseUserImport.getRemark());
            result.setUserStatusString(historyEnterpriseUserImport.getUserStatusString());
            resultList.add(result);
        }
        return resultList;
    }

    public static List<HistoryEnterpriseUserImportDTO> convert(List<UserImportDTO> list){
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }
        List<HistoryEnterpriseUserImportDTO> resultList = new ArrayList<>();
        for (UserImportDTO historyEnterpriseUserImport : list) {
            HistoryEnterpriseUserImportDTO result = new HistoryEnterpriseUserImportDTO();
            result.setDec(historyEnterpriseUserImport.getDec());
            result.setUserId(historyEnterpriseUserImport.getUserId());
            result.setName(historyEnterpriseUserImport.getName());
            result.setJobnumber(historyEnterpriseUserImport.getJobnumber());
            result.setMobile(historyEnterpriseUserImport.getMobile());
            result.setPositionName(historyEnterpriseUserImport.getPositionName());
            result.setRegionName(historyEnterpriseUserImport.getRegionName());
            result.setStoreName(historyEnterpriseUserImport.getStoreName());
            result.setDirectSuperior(historyEnterpriseUserImport.getDirectSuperior());
            result.setUserGroups(historyEnterpriseUserImport.getUserGroups());
            result.setEmail(historyEnterpriseUserImport.getEmail());
            result.setRemark(historyEnterpriseUserImport.getRemark());
            result.setUserStatusString(historyEnterpriseUserImport.getUserStatusString());
            resultList.add(result);
        }
        return resultList;
    }

}
