package com.coolcollege.intelligent.model.supervision.dto;

import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/8 17:59
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionDataTaskDTO {

    @ApiModelProperty("用户列表 按用户查询是传 按门店查询不需要")
    private List<String> userIds;
    @ApiModelProperty("最多200个门店 按门店查询的时候传 其他时候不需要")
    private List<String> storeIds;
    @ApiModelProperty("区域 最多10个区域 按门店查询的时候传 其他时候不需要")
    private List<String> regionIds;

    private Long parentId;

    private Integer pageSize;

    private Integer pageNum;

    private List<SupervisionSubTaskStatusEnum> completeStatusList;

    @ApiModelProperty("按人任务ID")
    private Long taskId;
    @ApiModelProperty("执行人名称")
    private String userName;
    @ApiModelProperty("处理是否逾期 0-未逾期  1-逾期")
    private Integer handleOverTimeStatus;

}
