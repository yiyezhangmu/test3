package com.coolcollege.intelligent.model.question.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 子工单详情表
 * @Author suzhuhong
 * @Date 2022/8/15 10:24
 * @Version 1.0
 */
@Data
public class SubQuestionDetailVO {

    @ApiModelProperty("子工单记录id")
    private Long id;

    @ApiModelProperty("工单来源")
    @Excel(name = "工单来源",orderNum = "0")
    private String questionType;

    @ApiModelProperty("父工单记录id")
    @Excel(name = "工单编号",orderNum = "1")
    private Long parentQuestionId;

    @ApiModelProperty("工单名称")
    @Excel(name = "工单名称",orderNum = "2")
    private String questionName;

    @ApiModelProperty(value = "发起人ID")
    private String createUserId;

    @ApiModelProperty(value = "发起人名称")
    @Excel(name = "发起人名称",orderNum = "3",width = 15)
    private String createUserName;

    @ApiModelProperty(value = "发起时间")
    @Excel(name = "发起时间",orderNum = "4",exportFormat = "yyyy-MM-dd HH:mm:ss" ,width = 30)
    private Date beginCreateDate;

    @ApiModelProperty("子工单编号")
    @Excel(name = "子工单编号",orderNum = "5",width = 15)
    private Long subQuestionCode;

    @ApiModelProperty("子工单名称")
    @Excel(name = "子工单名称",orderNum = "6",width = 15)
    private String subQuestionName;

    @ApiModelProperty("工单说明")
    @Excel(name = "工单说明",orderNum = "7")
    private String taskDesc;

    @ApiModelProperty("附件地址")
    private String attachUrl;

    @ApiModelProperty("培训内容")
    @Excel(name = "培训内容",orderNum = "8")
    private String attachName;

    @ApiModelProperty("检查项表检查项ID")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    @Excel(name = "检查表名称",orderNum = "9",width = 15)
    private String metaTableName;

    @ApiModelProperty("检查项检查项ID")
    private Long metaColumnId;

    @ApiModelProperty("检查项检查项名称")
    @Excel(name = "检查项名称",orderNum = "10",width = 15)
    private String metaColumnName;

    @ApiModelProperty("检查项检查结果")
    @Excel(name = "检查结果",orderNum = "11")
    private String columnCheckResult;

    @ApiModelProperty("门店ID")
    private String storeId;

    @ApiModelProperty("门店名称")
    @Excel(name = "门店名称",orderNum = "12")
    private String storeName;

    @ApiModelProperty("所属区域")
    private Long regionId;

    @ApiModelProperty("所属区域名称")
    private String regionName;

    @ApiModelProperty("所属区域名称")
    @Excel(name = "所属区域",orderNum = "13",width = 30)
    private String fullRegionName;

    @ApiModelProperty("子工单截止时间")
    @Excel(name = "子工单截止时间",orderNum = "14",exportFormat = "yyyy-MM-dd HH:mm:ss" ,width = 30)
    private Date subEndTime;

    @ApiModelProperty("工单状态, 1 : 待处理 2:待审核 endNode:已完成")
    private String status;

    @ApiModelProperty("工单状态, 1 : 待处理 2:待审核 endNode:已完成")
    @Excel(name = "工单状态",orderNum = "15")
    private String statusStr;

    @ApiModelProperty("是否逾期")
    @Excel(name = "是否逾期",orderNum = "16")
    private String overDueStr;

    @ApiModelProperty("是否逾期")
    private Boolean overDue;

    /**
     * 父工单ID
     */
    @ApiModelProperty("父工单ID")
    private Long unifyTaskId;

    /**
     * 轮次
     */
    @ApiModelProperty("轮次")
    private Long loopCount;

    @ApiModelProperty("培训内容")
    private String taskInfo;

    private BigDecimal checkScore;

    private String rewardPenaltMoney;

    @ApiModelProperty("是否能处理")
    private Boolean isCanHandled;

    public String getAttachName(){
        if(StringUtils.isBlank(this.taskInfo)){
            return "";
        }
        QuestionTaskInfoDTO questionTaskInfoDTO = JSONObject.parseObject(this.taskInfo, QuestionTaskInfoDTO.class);
        List<String> nameList = new ArrayList<>();
        if(StringUtils.isNotBlank(questionTaskInfoDTO.getAttachUrl())){
            TaskSopVO taskSopVO = JSONObject.parseObject(questionTaskInfoDTO.getAttachUrl(), TaskSopVO.class);
            nameList.add(taskSopVO.getFileName());
        }
        if(CollectionUtils.isNotEmpty(questionTaskInfoDTO.getCourseList())){
            for(CoolCourseVO coolCourseVO : questionTaskInfoDTO.getCourseList()){
                nameList.add(coolCourseVO.getTitle());
            }
        }
        if(CollectionUtils.isEmpty(nameList)){
            return "";
        }
        return StringUtils.join(nameList, Constants.BR);
    }

    public String getColumnCheckResult(){
        if(this.checkScore != null){
            this.columnCheckResult = this.columnCheckResult + "(" + this.checkScore +  "分)";
        }
        if(StringUtils.isNotBlank(this.rewardPenaltMoney)){
            this.columnCheckResult = this.columnCheckResult + "(" + this.rewardPenaltMoney +  ")";
        }
        return this.columnCheckResult;
    }
}
