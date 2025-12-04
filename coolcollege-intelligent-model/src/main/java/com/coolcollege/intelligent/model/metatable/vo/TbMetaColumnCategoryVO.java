package com.coolcollege.intelligent.model.metatable.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/4/2 10:00
 * @Version 1.0
 */
@Data
@ApiModel(value = "检查项分类VO")
public class TbMetaColumnCategoryVO {

    @ApiModelProperty("分类ID")
    private Long id;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("分类应用次数")
    private Integer refNum;

    @ApiModelProperty("是否默认分类")
    private Boolean isDefault;

    public TbMetaColumnCategoryVO(Long id, String categoryName, Integer refNum, Boolean isDefault) {
        this.id = id;
        this.categoryName = categoryName;
        this.refNum = refNum;
        this.isDefault = isDefault;
    }
}
