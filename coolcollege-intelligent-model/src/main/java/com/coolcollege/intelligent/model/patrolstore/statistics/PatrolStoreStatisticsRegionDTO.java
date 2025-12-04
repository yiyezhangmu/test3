package com.coolcollege.intelligent.model.patrolstore.statistics;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.common.util.NumberFormatUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 区域相关统计
 *
 * @author jeffrey
 * @date 2020/12/10
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatrolStoreStatisticsRegionDTO {

    private static final long serialVersionUID = 1L;
    private String regionId; // 区域id
    private String parentId;
    @Excel(name = "区域名称", orderNum = "1")
    private String name; // 区域名称
    @Excel(name = "总门店数", orderNum = "2")
    private int storeNum; // 区域门店数量
    @Excel(name = "巡店覆盖门店数", orderNum = "3")
    private int patrolStoreNum;// 巡查门店数量
    private String storeId;
    private String storeName;
    private String regionType;
    @Deprecated
    @Excel(name = "未覆盖门店数", orderNum = "4")
    private int unpatrolStoreNum;// 未巡店门店数量
    @Deprecated
    @Excel(name = "巡店覆盖率", orderNum = "5")
    private String patrolStorePercent;// 巡检的门店百分比
    @Excel(name = "巡店次数", orderNum = "6")
    private int patrolNum;// 巡店次数

    //平均巡店次数
    @Excel(name = "平均巡店次数", orderNum = "7")
    private String avgPatrolStoreNum;

    //总的巡店时长(规则)
    @Excel(name = "巡店总时长(规则)", orderNum = "8")
    private String totalPatrolStoreDuration;
    //平均巡店时长(规则)
    @Excel(name = "平均巡店时长(规则)", orderNum = "9")
    private String avgPatrolStoreDuration;
    //总的实际巡店时长
    @Excel(name = "巡店总时长(实际)", orderNum = "10")
    private String actualTotalPatrolStoreDuration;
    //平均实际巡店时长
    @Excel(name = "平均巡店总时长(实际)", orderNum = "11")
    private String actualAvgPatrolStoreDuration;

    //自主巡店数
    @Excel(name = "自主巡店数", orderNum = "12")
    private int consciousPatrolNum;

    @Excel(name = "交叉巡店数", orderNum = "13")
    private int selfCheckNum;
    //任务巡店次数
    @Excel(name = "任务巡店数", orderNum = "15")
    private int taskPatrolNum;
    //自主巡店数
    @Excel(name = "线上巡店数", orderNum = "16")
    private int onlineNum;

    //自主巡店数
    @Excel(name = "线下巡店数", orderNum = "17")
    private int offlineNum;

    //自主巡店数
    @Excel(name = "定时巡检数", orderNum = "18")
    private int pictureInspectionNum;
    //表单巡店数
    private int formPatrolNum;
    @Excel(name = "巡店人数", orderNum = "19")
    private int patrolPersonNum;// 巡店总人数

    private long questionId;
    @Excel(name = "总工单数", orderNum = "20")
    private int totalQuestionNum;// 总问题数
    @Excel(name = "待整改工单数", orderNum = "21")
    private int todoQuestionNum;// 待整改问题数
    @Excel(name = "待复检工单数", orderNum = "22")
    private int unRecheckQuestionNum;// 待复检问题数
    @Excel(name = "已解决工单数", orderNum = "23")
    private int finishQuestionNum;// 已经解决的问题数量
    @Deprecated
    @Excel(name = "工单整改率", orderNum = "24")
    private String doneQuestionPercent;// 问题整改率, 待复检问题数/totalQuestionNum
    // 问题整改率=(总问题数-待整改问题数)/总问题数*100%
    @Deprecated
    @Excel(name = "工单解决率", orderNum = "25")
    private String finishQuestionPercent;// 问题解决率
    @Deprecated
    private String unRecheckPercent;//问题复检查率

    private String pathName;

    private Integer sortNo;


    public long getQuestionId() {
        return questionId;
    }
    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }
    public int getTotalQuestionNum() {
        return totalQuestionNum;
    }
    public void setTotalQuestionNum(int totalQuestionNum) {
        this.totalQuestionNum = totalQuestionNum;
    }
    public int getTodoQuestionNum() {
        return todoQuestionNum;
    }
    public void setTodoQuestionNum(int todoQuestionNum) {
        this.todoQuestionNum = todoQuestionNum;
    }
    public int getUnRecheckQuestionNum() {
        return unRecheckQuestionNum;
    }
    public void setUnRecheckQuestionNum(int unRecheckQuestionNum) {
        this.unRecheckQuestionNum = unRecheckQuestionNum;
    }
    public int getFinishQuestionNum() {
        return finishQuestionNum;
    }

    public void setFinishQuestionNum(int finishQuestionNum) {
        this.finishQuestionNum = finishQuestionNum;
    }

    public String getDoneQuestionPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return  NumberFormatUtils.getPercentString(totalQuestionNum-todoQuestionNum,totalQuestionNum);
    }
    public String getFinishQuestionPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return  NumberFormatUtils.getPercentString(finishQuestionNum,totalQuestionNum);
    }
    public String getUnRecheckPercent() {
        if(totalQuestionNum<=0) {
            return "0%";
        }
        return  NumberFormatUtils.getPercentString(unRecheckQuestionNum,totalQuestionNum);
    }


    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStoreNum() {
        return storeNum;
    }
    public void setStoreNum(int storeNum) {
        this.storeNum = storeNum;
    }
    public int getPatrolNum() {
        return patrolNum;
    }
    public void setPatrolNum(int patrolNum) {
        this.patrolNum = patrolNum;
    }
    public int getPatrolStoreNum() {
        return patrolStoreNum;
    }
    public void setPatrolStoreNum(int patrolStoreNum) {
        this.patrolStoreNum = patrolStoreNum;
    }
    public int getUnpatrolStoreNum() {
         int unpatrolStoreNumTmp = storeNum - patrolStoreNum;
         if(unpatrolStoreNumTmp < 0){
             unpatrolStoreNumTmp = 0;
         }
        return unpatrolStoreNumTmp;
    }
    public int getPatrolPersonNum() {
        return patrolPersonNum;
    }
    public void setPatrolPersonNum(int patrolPersonNum) {
        this.patrolPersonNum = patrolPersonNum;
    }

    public String getPatrolStorePercent() {
        if(storeNum<=0) {
            return "0%";
        }else if(patrolStoreNum > storeNum){
            return "100%";
        }
        return  NumberFormatUtils.getPercentString(patrolStoreNum,storeNum);
    }

    public PatrolStoreStatisticsRegionDTO(int storeNum, int patrolNum, int patrolStoreNum, int patrolPersonNum,
                                          int totalQuestionNum, int todoQuestionNum, int unRecheckQuestionNum, int finishQuestionNum) {
        this.totalQuestionNum = totalQuestionNum;
        this.todoQuestionNum = todoQuestionNum;
        this.unRecheckQuestionNum = unRecheckQuestionNum;
        this.finishQuestionNum = finishQuestionNum;
        this.storeNum = storeNum;
        this.patrolNum = patrolNum;
        this.patrolStoreNum = patrolStoreNum;
        this.patrolPersonNum = patrolPersonNum;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }


    public Integer getTaskPatrolNum() {
        return taskPatrolNum;
    }

    public void setTaskPatrolNum(int taskPatrolNum) {
        this.taskPatrolNum = taskPatrolNum;
    }

    public int getConsciousPatrolNum() {
        return patrolNum-taskPatrolNum-selfCheckNum-formPatrolNum;
    }

    public void setConsciousPatrolNum(int consciousPatrolNum) {
        this.consciousPatrolNum = consciousPatrolNum;
    }

    public int getOnlineNum() {
        return onlineNum;
    }

    public void setOnlineNum(int onlineNum) {
        this.onlineNum = onlineNum;
    }

    public int getOfflineNum() {
        return offlineNum;
    }

    public void setOfflineNum(int offlineNum) {
        this.offlineNum = offlineNum;
    }

    public int getPictureInspectionNum() {
        return pictureInspectionNum;
    }

    public void setPictureInspectionNum(int pictureInspectionNum) {
        this.pictureInspectionNum = pictureInspectionNum;
    }

    public String getAvgPatrolStoreNum() {
        return avgPatrolStoreNum;
    }

    public void setAvgPatrolStoreNum(String avgPatrolStoreNum) {
        this.avgPatrolStoreNum = avgPatrolStoreNum;
    }

    public String getTotalPatrolStoreDuration() {
        return totalPatrolStoreDuration;
    }

    public void setTotalPatrolStoreDuration(String totalPatrolStoreDuration) {
        this.totalPatrolStoreDuration = totalPatrolStoreDuration;
    }

    public String getAvgPatrolStoreDuration() {
        return avgPatrolStoreDuration;
    }

    public void setAvgPatrolStoreDuration(String avgPatrolStoreDuration) {
        this.avgPatrolStoreDuration = avgPatrolStoreDuration;
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

    public Integer getSelfCheckNum() {
        return selfCheckNum;
    }

    public void setSelfCheckNum(Integer selfCheckNum) {
        this.selfCheckNum = selfCheckNum;
    }

    public int getFormPatrolNum() {
        return formPatrolNum;
    }

    public void setFormPatrolNum(int formPatrolNum) {
        this.formPatrolNum = formPatrolNum;
    }

    public String getRegionType() {
        return regionType;
    }

    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
