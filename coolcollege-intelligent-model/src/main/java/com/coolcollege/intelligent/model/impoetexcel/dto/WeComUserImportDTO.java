package com.coolcollege.intelligent.model.impoetexcel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 导入用户excel对应类
 * @author ：xugangkun
 * @date ：2021/6/10 14:34
 */
@Data
public class WeComUserImportDTO {

    @Excel(name = "描述", width = 30)
    private String dec;

    @Excel(name = "姓名", orderNum = "1", width = 10)
    private String name;

    @Excel(name = "帐号", orderNum = "2", width = 10)
    private String userId;

    @Excel(name = "别名", orderNum = "3", width = 10)
    private String alias;

    @Excel(name = "职务", orderNum = "4", width = 10)
    private String position;

    @Excel(name = "部门", orderNum = "5", width = 10)
    private String department;

    @Excel(name = "性别", orderNum = "6", width = 10)
    private String gender;

    @Excel(name = "手机", orderNum = "7", width = 10)
    private String mobile;

    @Excel(name = "座机", orderNum = "8", width = 10)
    private String telephone;

    @Excel(name = "个人邮箱", orderNum = "9", width = 10)
    private String email;

    @Excel(name = "地址", orderNum = "10", width = 10)
    private String address;

    @Excel(name = "激活状态", orderNum = "11", width = 10)
    private String status;

    @Excel(name = "禁用状态", orderNum = "12", width = 10)
    private String disabledState;

    @Excel(name = "微信插件", orderNum = "13", width = 10)
    private String wechatPlug;

}
