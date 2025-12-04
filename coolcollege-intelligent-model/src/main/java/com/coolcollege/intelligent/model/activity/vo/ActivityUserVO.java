package com.coolcollege.intelligent.model.activity.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/7/6 11:31
 * @Version 1.0
 */
@Data
public class ActivityUserVO {
    @Excel(name = "活动主题" , width = 20,orderNum = "1")
    private String activityTitle;
    @Excel(name = "员工工号",orderNum = "2")
    private String jobNumber;
    @Excel(name = "员工名称",orderNum = "3")
    private String userName;
    @Excel(name = "参与状态",orderNum = "4" ,replace = {"参与_true","未参与_false"})
    private Boolean participateFlag;
    @Excel(name = "活动参与次数",orderNum = "5")
    private Integer participateCount;
    @Excel(name = "所属部门",orderNum = "5")
    private String fullRegionPathName;
    @Excel(name = "所属分公司",orderNum = "6")
    private String thirdDept;
    @Excel(name = "直属部门/门店",width = 30,orderNum = "7")
    private String deptName;
    @Excel(name = "门店编码",width = 20,orderNum = "8")
    private String storeNum;

    private String userId;
}
