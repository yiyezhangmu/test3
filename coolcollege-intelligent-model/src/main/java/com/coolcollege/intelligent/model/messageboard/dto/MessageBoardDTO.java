package com.coolcollege.intelligent.model.messageboard.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author   wxp
 * @date   2024-07-29 16:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageBoardDTO implements Serializable {

    @NotNull(message = "业务id不能为空")
    @ApiModelProperty("业务id")
    private String businessId;

    @NotBlank(message = "业务类型不能为空")
    @ApiModelProperty("业务类型 店务storework 其它other")
    private String businessType;

    @NotBlank(message = "操作类型不能为空")
    @ApiModelProperty("操作类型 留言message 点赞like")
    private String operateType;

    @ApiModelProperty("留言内容")
    private String messageContent;
}