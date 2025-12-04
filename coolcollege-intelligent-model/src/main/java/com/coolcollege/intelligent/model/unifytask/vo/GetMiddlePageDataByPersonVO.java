package com.coolcollege.intelligent.model.unifytask.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangnan
 * @description: 按人任务中间页vo
 * @date 2022/4/17 7:03 PM
 */
@Data
public class GetMiddlePageDataByPersonVO {

    @ApiModelProperty(value = "子任务id")
    private Long subTaskId;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户职位")
    private String roleNames;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "用户所属部门")
    private String departments;

    @ApiModelProperty(value = "巡店要求数量")
    private Integer patrolStoreNum;

    @ApiModelProperty(value = "已巡门店")
    private String patrolledStores;

    @ApiModelProperty(value = "已巡门店数量")
    private Integer patrolledStoreNum;

}
