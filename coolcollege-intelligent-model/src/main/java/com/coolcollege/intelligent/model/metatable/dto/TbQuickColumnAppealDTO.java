package com.coolcollege.intelligent.model.metatable.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-06-05 02:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbQuickColumnAppealDTO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("快捷检查项ID")
    private Long metaQuickColumnId;

    @ApiModelProperty("自定义名称")
    private String appealName;
}