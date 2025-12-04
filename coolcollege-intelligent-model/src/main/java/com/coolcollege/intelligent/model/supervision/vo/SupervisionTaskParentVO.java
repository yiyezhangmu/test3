package com.coolcollege.intelligent.model.supervision.vo;

import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/2 15:37
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionTaskParentVO {
    @ApiModelProperty("任务名称")
    private String taskName;
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("业务ID")
    private Long businessId;
    @ApiModelProperty("业务类型")
    private String businessType;
    @ApiModelProperty("执行人")
    private String executePersons;
    @ApiModelProperty("任务开始时间")
    private Date taskStartTime;
    @ApiModelProperty("任务结束时间")
    private Date taskEndTime;
    @ApiModelProperty("取消状态 0正常 1 取消")
    private Integer cancelStatus;
    @ApiModelProperty("标签")
    private String tags;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("创建人ID")
    private String createUserId;
    @ApiModelProperty("创建人名称")
    private String createUserName;
    @ApiModelProperty("优先级 固定选项：紧急,一般")
    private String priority;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("检查门店ID 如果门店ID为空 按人员分解")
    private String checkStoreIds;

    @ApiModelProperty("任务状态")
    private String taskStatusStr;
    @ApiModelProperty("任务分组")
    private String taskGrouping;

}
