package com.coolcollege.intelligent.service.tbdisplay;


import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;

/**
 *
 * @author byd
 */
public interface TbMetaDisplayTableColumnService {

    TbMetaDisplayTableColumnDO getByMetaColumnId(String enterpriseId, Long metaColumnId);


}
