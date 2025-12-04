package com.coolcollege.intelligent.model.metatable.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/4/2 16:09
 * @Version 1.0
 */
@Data
public class TbMetaTableRequest {

    @ApiModelProperty("检查表ID")
    private Long id;

    /**
     * 项ID集合
     */
    @ApiModelProperty("项ID集合")
    private List<Long> columnIds;

    /**
     * 冻结状态 false 解除冻结  true 冻结
     */
    @ApiModelProperty("是否冻结")
    private Integer freezeStatus;

    @ApiModelProperty("分类名称集合")
    private List<String> categoryNameList;

    /**
     * 置顶状态  true 置顶  false 取消置顶
     */
    @ApiModelProperty("是否置顶")
    private Boolean topStatus;

    /**
     * 归档状态  true 归档  false 取消归档
     */
    @ApiModelProperty("是否归档")
    private Integer pigeonholeStatus;
}
