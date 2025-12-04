package com.coolcollege.intelligent.model.metatable;

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
 * @date   2023-06-05 02:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaColumnReasonDO implements Serializable {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("检查表ID")
    private Long metaTableId;

    @ApiModelProperty("检查项ID")
    private Long metaColumnId;

    @ApiModelProperty("创建者")
    private String createUserId;

    @ApiModelProperty("自定义名称")
    private String reasonName;

    @ApiModelProperty("统计维度:  FAIL:不合格 INAPPLICABLE:不适用")
    private String mappingResult;

    private Boolean deleted;
}