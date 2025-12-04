package com.coolcollege.intelligent.model.supervision.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/4/11 18:45
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionTransferRequest {
    @ApiModelProperty("按人任务ID")
    private Long supervisionTaskId;
    @ApiModelProperty("门店任务ID列表 纯人任务传空")
    private List<Long> supervisionStoreTaskIds;
    @ApiModelProperty("转交接收人ID")
    private String transferUserId;
    @ApiModelProperty("转交接收人名称")
    private String transferUserName;
    @ApiModelProperty("person/store")
    private String type;
}
