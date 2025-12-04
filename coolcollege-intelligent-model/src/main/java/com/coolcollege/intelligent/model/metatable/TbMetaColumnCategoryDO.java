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
 * @date   2022-04-01 08:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaColumnCategoryDO implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("排序值")
    private Integer orderNum;

    @ApiModelProperty("是否默认")
    private Boolean isDefault;

    @ApiModelProperty("创建人")
    private String createId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private String updateId;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}