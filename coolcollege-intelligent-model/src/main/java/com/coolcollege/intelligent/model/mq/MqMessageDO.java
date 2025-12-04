package com.coolcollege.intelligent.model.mq;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2024-02-20 03:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MqMessageDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("子任务id")
    private Long subTaskId;

    @ApiModelProperty("消息id")
    private String msgId;

    @ApiModelProperty("消息")
    private String message;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("处理时间")
    private Date handleTime;
}