package com.coolcollege.intelligent.service.tbdisplay;


import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickColumnAddParam;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickColumnQueryParam;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickContentQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 *
 * @author wxp
 */
public interface TbMetaDisplayQuickColumnService {

    Boolean insertDisplayQuickColumn(String enterpriseId, TbMetaDisplayQuickColumnAddParam tbMetaDisplayQuickColumnAddParam);

    PageVO selectTaskSopList(String enterpriseId, TbMetaDisplayQuickColumnQueryParam query, Integer pageNum, Integer pageSize);

    Boolean deleteDisplayQuickColumn(String enterpriseId,  TbMetaDisplayQuickColumnQueryParam query);

    TbMetaTableDO createTableByColumnIdList(String enterpriseId, List<Long> columnIdList, CurrentUser user);

    Boolean batchInsert(String enterpriseId, TbMetaDisplayQuickContentQuery query);

    Boolean checkContentEdit(String enterpriseId, TbMetaDisplayQuickColumnQueryParam query);
}
