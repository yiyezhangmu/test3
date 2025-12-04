package com.coolcollege.intelligent.model.supervision.vo;

import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.sop.dto.TaskSopDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.supervision.dto.ApproveInfoDTO;
import com.coolcollege.intelligent.model.supervision.dto.TimingInfoDTO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/8 18:49
 * @Version 1.0
 */
@Data
public class SupervisionTaskParentDetailVO {

    @ApiModelProperty("父任务ID")
    private Long id;

    @ApiModelProperty("业务ID")
    private String businessId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("开始时间")
    private Date taskStartTime;

    @ApiModelProperty("结束时间")
    private Date taskEndTime;

    @ApiModelProperty("执行人")
    private String executePersons;

    @ApiModelProperty("优先级 固定选项：紧急,一般")
    private String priority;

    @ApiModelProperty("检验code")
    private String checkCode;

    @ApiModelProperty("处理方式 {code:1,name:',id:'} code值 0 无需操作,1 填写表单,2 点击按钮")
    private String handleWay;

    @ApiModelProperty("任务描述")
    private String desc;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("检查门店ID")
    private String checkStoreIds;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("检查门店")
    private List<GeneralDTO> storeIds;

    @ApiModelProperty("标签（支持多个 逗号隔开）")
    private String tags;

    @ApiModelProperty("表单的ID")
    private String formId;

    private TbMetaTableDO tbMetaTableDO;

    @ApiModelProperty("任务创建者")
    private String createUserId;

    @ApiModelProperty("任务更新者")
    private String updateUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标识")
    private Boolean deleted;

    @ApiModelProperty("取消状态 0正常 1取消")
    private Integer cancelStatus;

    private List<GeneralDTO> storeRangeList;

    private List<TaskSopVO> taskSopVOList;

    private List<Long> sopIds;

    @ApiModelProperty("1.2新增_任务分组")
    private String taskGrouping;

    @ApiModelProperty("1.2新增_定时提醒信息")
    private TimingInfoDTO timingInfoDTO;

    @ApiModelProperty("1.2新增_审批流信息")
    private ApproveInfoDTO approveInfoDTO;
}
