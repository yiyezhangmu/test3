package com.coolcollege.intelligent.model.newstore.vo;

import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.newstore.dto.NsVisitTableDataColumnDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangnan
 * @description: 拜访表信息VO
 * @date 2022/3/7 9:39 PM
 */
@Data
public class NsVisitTableInfoVO {

    @ApiModelProperty("拜访表-检查项列表")
    private List<TbMetaDefTableColumnDO> visitColumns;

    @ApiModelProperty("拜访表-数据项列表")
    private List<NsVisitTableDataColumnDTO> dataVisitColumns;
}
