package com.coolcollege.intelligent.model.metatable.vo;

import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/4/2 15:34
 * @Version 1.0
 */
@Data
public class MetaTableTypeVO {

    @ApiModelProperty("检查项属性值")
    private Integer code;

    @ApiModelProperty("表类型")
    private String name;

    @ApiModelProperty("能使用项属性集合")
    private List<MetaColumnTypeVO> columnTypes;
}
