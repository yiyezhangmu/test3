package com.coolcollege.intelligent.model.metatable.dto;

import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnReasonDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnResultDO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/9/4 19:13
 */
@Data
@Builder
public class ColumnMergeDTO {
    private TbMetaQuickColumnDO columnDO;
    private NormalColumnImportDTO normalColumnImportDTO;

    private List<TbMetaQuickColumnResultDO> columnResultDOS;
    private List<TbMetaQuickColumnReasonDO> columnReasonDOS;
}
