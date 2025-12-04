package com.coolcollege.intelligent.model.supervision.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionDefDataColumnDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/28 19:28
 * @Version 1.0
 */
@Data
public class SupervisionStoreTaskDataVO {

    @ApiModelProperty("门店任务ID")
    private Long id;
    @ApiModelProperty("任务名称")
    private String taskName;
    @ApiModelProperty("按人任务ID")
    private Long supervisionTaskId;

    @ApiModelProperty("门店ID")
    private String storeId;

    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("任务接收人ID")
    private String supervisionUserId;

    @ApiModelProperty("任务接收人名称")
    private String supervisionUserName;

    @Excel(name = "执行人职位", width = 20, orderNum = "2")
    @ApiModelProperty("执行人职位")
    private String roleName;

    @Excel(name = "执行人所属部门", width = 20, orderNum = "3")
    @ApiModelProperty("执行人所属部门")
    private String department;

    @Excel(name = "完成状态", width = 20, orderNum = "4", replace = {"进行中_0", "已完成_1", "已逾期_2","_null"})
    @ApiModelProperty("完成状态")
    private Integer completeStatus;
    @ApiModelProperty("完成状态_中文")
    private String taskStatusStr;

    @Excel(name = "完成时间", width = 20, orderNum = "5", format = "yyyy.MM.dd HH:mm")
    @ApiModelProperty("完成时间 ")
    private Date completeTime;

    @Excel(name = "提交时间", width = 20, orderNum = "5", format = "yyyy.MM.dd HH:mm")
    @ApiModelProperty("提交时间 ")
    private Date submitTime;

    @ApiModelProperty("取消状态")
    private Integer cancelStatus;

    @ApiModelProperty("按人查询 数据列表")
    private List<SupervisionDefDataColumnDTO> supervisionDefDataColumnDTOS;
    @ApiModelProperty("按人查询 原始自定义检查表")
    private List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS;


    @ApiModelProperty("是否逾期")
    private Integer handleOverTimeStatus;

    @ApiModelProperty("是否转交 重新分配  1-转交  2-重新分配 0-正常")
    private Integer transferReassignFlag;

    @ApiModelProperty("执行人ID")
    private String supervisionHandleUserId;

    @ApiModelProperty("执行人名称")
    private String supervisionHandleUserName;
    @ApiModelProperty("当前节点")
    private Integer currentNode;

    private String handleOverTimeStatusStr;

    @ApiModelProperty("任务分配人职位")
    private String supervisionUserRoleName;
    @Excel(name = "执行人所属部门", width = 20, orderNum = "3")
    @ApiModelProperty("任务分配人所属部门")
    private String supervisionUserDepartment;

    private String tempName;
}
