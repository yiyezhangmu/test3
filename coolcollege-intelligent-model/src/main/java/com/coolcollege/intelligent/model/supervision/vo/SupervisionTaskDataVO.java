package com.coolcollege.intelligent.model.supervision.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionDefDataColumnDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/3 14:39
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionTaskDataVO {
    @ApiModelProperty("任务接收人")
    @Excel(name = "任务接收人名称", width = 20, orderNum = "1")
    private String supervisionUserName;
    @ApiModelProperty("任务接收人ID")
    private String supervisionUserId;
    @ApiModelProperty("按人任务ID")
    private Long supervisionTaskId;
    @Excel(name = "任务接收人职位", width = 20, orderNum = "2")
    @ApiModelProperty("任务接收人职位")
    private String roleName;
    @Excel(name = "任务接收人所属部门", width = 20, orderNum = "3")
    @ApiModelProperty("任务接收人所属部门")
    private String department;
    @Excel(name = "完成状态", width = 20, orderNum = "4", replace = {"进行中_0", "已完成_1", "已逾期_2","_null"})
    @ApiModelProperty("完成状态 0 进行中 1 已完成 2 已逾期")
    private Integer completeStatus;

    private String taskStatusStr;

    @Excel(name = "完成时间", width = 20, orderNum = "5", format = "yyyy.MM.dd HH:mm")
    @ApiModelProperty("完成时间 ")
    private Date completeTime;
    @ApiModelProperty("提交时间 ")
    private Date submitTime;
    @Excel(name = "文字描述", width = 20, orderNum = "6")
    @ApiModelProperty("文字描述")
    private String manualText;
    @Excel(name = "图片", width = 20, orderNum = "7")
    @ApiModelProperty("图片")
    private String manualPics;
    @ApiModelProperty("附件")
    private String manualAttach;
    @ApiModelProperty("取消状态")
    private Integer cancelStatus;
    @ApiModelProperty("按人查询 数据列表")
    private List<SupervisionDefDataColumnDTO> supervisionDefDataColumnDTOS;
    @ApiModelProperty("按人查询 原始自定义检查表")
    private List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS;

    @ApiModelProperty("门店范围")
    private List<StoreDTO> storeDTOList;

    private String storeNameList;

    @ApiModelProperty("是否逾期")
    private Integer handleOverTimeStatus;

    private String handleOverTimeStatusStr;

    @ApiModelProperty("是否转交 重新分配  1-转交  2-重新分配 0-正常")
    private Integer transferReassignFlag;

    @ApiModelProperty("执行人ID")
    private String supervisionHandleUserId;

    @ApiModelProperty("执行人名称")
    private String supervisionHandleUserName;

    private Integer currentNode;

    @ApiModelProperty("任务分配人职位")
    private String supervisionUserRoleName;
    @Excel(name = "执行人所属部门", width = 20, orderNum = "3")
    @ApiModelProperty("任务分配人所属部门")
    private String supervisionUserDepartment;

    private String tempName;

    private String taskName;

}
