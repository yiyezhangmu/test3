package com.coolcollege.intelligent.model.metatable.response;

import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import lombok.Data;

import java.util.List;
@Data
public class MetaTableMetaColumnResp {
    /**
     * 自定义检查项列表
     */
    private List<TbMetaDefTableColumnDO> defMetaColumnList;
    /**
     * 标准检查项列表
     */
    private List<TbMetaStaTableColumnDO> staMetaColumnList;
}
