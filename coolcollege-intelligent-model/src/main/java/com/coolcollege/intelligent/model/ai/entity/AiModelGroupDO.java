package com.coolcollege.intelligent.model.ai.entity;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI算法分组
 * @author   zhangchenbiao
 * @date   2025-09-25 03:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiModelGroupDO implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}