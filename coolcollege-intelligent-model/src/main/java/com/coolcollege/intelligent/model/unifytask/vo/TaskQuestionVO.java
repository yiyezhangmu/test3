package com.coolcollege.intelligent.model.unifytask.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.enums.TaskStatusEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.store.dto.BasicsStoreDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskQuestionVO {

    /**
     * ID
     */
    private Long id;
    /**
     * 任务名称
     */
    @Excel(name = "所属任务", orderNum = "3")
    private String taskName;

    /**
     * 区域名称
     */
    private String regionName;

    /**
     * 全区域路径名称
     */
    @Excel(name = "所属区域", orderNum = "0")
    private String fullRegionName;
    /**
     * 门店名称
     */
    @Excel(name = "所属门店", orderNum = "1")
    private String storeName;

    @Excel(name = "门店编号", orderNum = "2")
    private String storeNum;

    /**
     * 检查表名称
     */
    @Excel(name = "所属检查表", orderNum = "4")
    private String metaTableName;

    /**
     * 检查项名称
     */
    @Excel(name = "检查项名称", orderNum = "5")
    private String metaColumName;

    /**
     * 所属名称
     */
    @Excel(name = "所属名称", orderNum = "6")
    private String categoryName;

    /**
     * 检查项分值
     */
    @Excel(name = "检查项分值", orderNum = "7")
    private BigDecimal supportScore;

    /**
     * 罚款
     */
    @Excel(name = "罚款", orderNum = "8")
    private BigDecimal punishMoney;

    /**
     * 奖金
     */
    @Excel(name = "奖金", orderNum = "9")
    private BigDecimal awardMoney;

    /**
     * 标准图
     */
    @Excel(name = "标准图", orderNum = "10")
    private String standardPic;

    /**
     * 检查项标准
     */
    @Excel(name = "检查项标准", orderNum = "11")
    private String description;

    /**
     * 检查结果
     */
    private String checkResult;

    /**
     * 检查结果
     */
    @Excel(name = "结果", orderNum = "12")
    private String checkResultName;

    /**
     * 问题图片
     */
    @Excel(name = "问题图片", orderNum = "13")
    private String questionPicture;

    /**
     * 问题视频
     */
    private String questionVideo;

    /**
     * 工单描述
     */
    @Excel(name = "工单描述", orderNum = "14")
    private String taskDesc;

    /**
     * 开始时间
     */
    private Long beginTime;
    /**
     * 结束时间
     */
    private Long endTime;

    @Excel(name = "有效期", orderNum = "15")
    private String validTime;

    /**
     * 是否逾期
     */
    private Boolean expireFlag = false;

    @Excel(name = "是否逾期", orderNum = "16")
    private Boolean expireStr = false;

    /**
     * 状态
     */

    private String status;

    public String getExpireStr() {
        if (this.expireFlag) {
            return "已逾期";
        } else {
            return "未逾期";
        }
    }

    public String getStatusStr() {
        if (UnifyNodeEnum.END_NODE.getCode().equals(this.status)) {
            return "已完成";
        } else if (UnifyNodeEnum.SECOND_NODE.getCode().equals(this.status)) {
            return "待审批";
        } else {
            return "待处理";
        }
    }


    /**
     * 状态值
     */
    @Excel(name = "状态", orderNum = "17")
    private String statusStr;
    /**
     * 任务创建者
     */
    private String createUserId;
    /**
     * 创建人名称
     */
    @Excel(name = "创建人", orderNum = "18")
    private String createUserName;
    /**
     * 任务创建时间
     */

    private Long createTime;


    @Excel(name = "创建时间", orderNum = "19", format = "yyyy.MM.dd HH:mm")
    private Date createTimeDate;

    /**
     * 整改人
     */

    private String handerUserId;
    /**
     * 整改人名称
     */
    @Excel(name = "整改人", orderNum = "20")
    private String handerUserName;

    /**
     * 整改意见
     */


    /**
     * 处理结果
     */

    @Excel(name = "整改意见", orderNum = "22")
    private String handleActionyKeyStr;

    /**
     * 整改时间
     */
    @Excel(name = "整改时间", orderNum = "23", format = "yyyy.MM.dd HH:mm")
    private Date handleTime;



    /**
     * 整改图片
     */
    @Excel(name = "整改图片", orderNum = "24")
    private String handlePicture;


    @Excel(name = "整改备注", orderNum = "25")
    private String handleOpinion;
    /**
     * 处理结果
     */
    private String handleActionyKey;


    /**
     * 审批人
     */
    private String approveUserId;
    /**
     * 审批人名称
     */
    @Excel(name = "审批人", orderNum = "26")
    private String approveUserName;

    /**
     * 审批意见备注
     */

    private String approveOpinion;

    /**
     * 审核结果
     */
    private String approveActionyKey;

    /**
     * 审核结果
     */
    @Excel(name = "审批意见", orderNum = "27")
    private String approveActionyKeyStr;
    /**
     * 审批时间
     */
    @Excel(name = "审批时间", orderNum = "28", format = "yyyy.MM.dd HH:mm")
    private Date approveTime;

    /**
     * 工单完成时间
     */
    @Excel(name = "工单完成时间", orderNum = "29", format = "yyyy.MM.dd HH:mm")
    private Date completeTime;

    /**
     * 时长
     */
    @Excel(name = "工单总时长", orderNum = "30")
    private String totalDuration;

    /**
     * 时长
     */
    private Long totalDurationTime;

    public String getHandleActionyKeyStr(){
        if("pass".equals(this.handleActionyKey)){
            return "已整改";
        }if("reject".equals(this.handleActionyKey)){
            return "无问题，维持现状";
        }
        return null;
    }

    public String getApproveActionyKeyStr(){
        if("pass".equals(this.approveActionyKey)){
            return "已通过";
        }if("reject".equals(this.approveActionyKey)){
            return "拒绝";
        }
        return null;
    }

    public String getTotalDuration() {
        if (totalDurationTime == null) {
            return null;
        }
        long tmp = totalDurationTime;
        long day = tmp/(1000*60*60*24);
        tmp -=  day*1000*60*60*24;
        long hour = tmp/(1000*60*60);
        tmp -= hour*1000*60*60;
        long minute = tmp / (1000*60);
        tmp -= minute*1000*60;
        long second = tmp / 1000;

        if(day>0){
            return day+"天"+hour+"时"+minute+"分";
        }

        if(hour>0){
            return hour+"时"+minute+"分"+second+"秒";
        }

        return minute + "分" + second + "秒";
    }

    public static void main(String[] args) {
        Long totalDurationTime = 11726689L;
        long hour = totalDurationTime / 1000 / 60 / 60;
        long minute = totalDurationTime / 1000 / 60;
        long second = (totalDurationTime - minute * 1000 * 60) / 1000;
        System.out.printf(minute + "分" + second + "秒");
    }
}