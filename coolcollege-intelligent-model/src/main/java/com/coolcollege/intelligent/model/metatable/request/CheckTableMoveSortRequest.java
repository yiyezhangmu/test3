package com.coolcollege.intelligent.model.metatable.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2022/4/11 11:05
 * @Version 1.0
 */
@Data
public class CheckTableMoveSortRequest {
    /**
     * 页码
     */
    @ApiModelProperty("页码")
    private Integer pageNum;

    /**
     * 条数
     */
    @ApiModelProperty("条数")
    private Integer pageSize;

    /**
     * 对象所属当前的 下标 索引位置
     */
    @ApiModelProperty("对象所属当前的 下标 索引位置")
    private Integer startIndex;

    /**
     * 对象所属 拖拽后所属的 下标 索引的 位置
     */
    @ApiModelProperty("对象所属 拖拽后所属的 下标 索引的 位置")
    private Integer endIndex;

    /**
     *  targetId(拖拽的对象)
     */
    @ApiModelProperty("targetId(拖拽的对象)")
    private Long targetId;

    /**
     * 检查表名称
     */
    private String name;

    /**
     * 表属性类型
     */
    @ApiModelProperty("表属性类型")
    private String tableProperty;

}
