package com.coolcollege.intelligent.model.newstore.request;

import com.coolcollege.intelligent.model.newstore.dto.NsVisitTableDataColumnDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zhangnan
 * @description: 拜访表数据项保存Request
 * @date 2022/3/7 8:55 PM
 */
@Data
public class NsDataVisitTableColumnSaveRequest {

    @ApiModelProperty("检查项id")
    private Long recordId;

    @ApiModelProperty("拜访表-数据项列表")
    private List<NsVisitTableDataColumnDTO> dataVisitColumns;
}
