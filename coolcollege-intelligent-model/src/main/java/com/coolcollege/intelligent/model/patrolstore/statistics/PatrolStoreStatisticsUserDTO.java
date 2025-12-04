package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.util.NumberFormatUtils;

/**
 * 用户执行力相关统计
 * 
 * @author jeffrey
 * @date 2020/12/10
 */
public class PatrolStoreStatisticsUserDTO extends BaseQuestionStatisticsDTO {

    private static final long serialVersionUID = 1L;

    private String userId;
    @Excel(name = "人员")
    private String userName;
    @Excel(name = "负责门店数")
    private int manageStoreNum;// 管理的门店总数
    @Excel(name = "检查门店数")
    private int patrolStoreNum;// 检查门店数
    @Excel(name = "巡店覆盖率")
    private String patrolStorePercent;
    @Excel(name = "巡店总时长(实际)")
    private String actualTotalPatrolStoreDuration;
    @Excel(name = "平均巡店总时长(实际)")
    private String actualAvgPatrolStoreDuration;
    @Excel(name = "未检查门店数")
    private int unPatrolStoreNum;// 未检查门店数
    @Excel(name ="门店检查次数")
    private int patrolNum;// 门店检查次数
    @Excel(name = "创建检查表数")
    private int createTableNum;//创建表数
    @Excel(name = "检查表使用次数")
    private int tableUsedTimes;//表被使用次数
    @Excel(name = "检查表使用数量")
    private int tableUsedNum;//表被使用数量
    @Excel(name = "所属部门")
    private String departmentName;

    private Long metaTableId;

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getManageStoreNum() {
        return manageStoreNum;
    }

    public void setManageStoreNum(int manageStoreNum) {
        this.manageStoreNum = manageStoreNum;
    }

    public int getPatrolStoreNum() {
        return patrolStoreNum;
    }

    public void setPatrolStoreNum(int patrolStoreNum) {
        this.patrolStoreNum = patrolStoreNum;
    }

    public int getUnPatrolStoreNum() {
        return unPatrolStoreNum;
    }

    public void setUnPatrolStoreNum(int unPatrolStoreNum) {
        this.unPatrolStoreNum = unPatrolStoreNum;
    }

    public int getPatrolNum() {
        return patrolNum;
    }

    public void setPatrolNum(int patrolNum) {
        this.patrolNum = patrolNum;
    }

    public int getCreateTableNum() {
        return createTableNum;
    }

    public void setCreateTableNum(int createTableNum) {
        this.createTableNum = createTableNum;
    }

    public int getTableUsedTimes() {
        return tableUsedTimes;
    }

    public Long getMetaTableId() {
        return metaTableId;
    }

    public void setMetaTableId(Long metaTableId) {
        this.metaTableId = metaTableId;
    }

    public void setTableUsedTimes(int tableUsedTimes) {
        this.tableUsedTimes = tableUsedTimes;
    }

    public int getTableUsedNum() {
        return tableUsedNum;
    }

    public void setTableUsedNum(int tableUsedNum) {
        this.tableUsedNum = tableUsedNum;
    }

    public String getPatrolStorePercent() {
        if(patrolStoreNum==0){
            return "0%";
        }else {
            return NumberFormatUtils.getPercentString(patrolStoreNum,  manageStoreNum);
        }
    }

    public void setPatrolStorePercent(String patrolStorePercent) {
        this.patrolStorePercent = patrolStorePercent;
    }

    public String getActualTotalPatrolStoreDuration() {
        return actualTotalPatrolStoreDuration;
    }

    public void setActualTotalPatrolStoreDuration(String actualTotalPatrolStoreDuration) {
        this.actualTotalPatrolStoreDuration = actualTotalPatrolStoreDuration;
    }

    public String getActualAvgPatrolStoreDuration() {
        return actualAvgPatrolStoreDuration;
    }

    public void setActualAvgPatrolStoreDuration(String actualAvgPatrolStoreDuration) {
        this.actualAvgPatrolStoreDuration = actualAvgPatrolStoreDuration;
    }
}
