package com.coolcollege.intelligent.model.supervision.vo;

import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionDefDataColumnDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/28 16:40
 * @Version 1.0
 */
@Data
public class SupervisionDataColumnVO {
    @ApiModelProperty("数据列表")
    private List<SupervisionDefDataColumnDTO> supervisionDefDataColumnDTOS;
    @ApiModelProperty("原始自定义检查表")
    private List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS;
}
