package com.coolcollege.intelligent.facade.meta;

import com.coolcollege.intelligent.facade.dto.BaseResultDTO;

/**
 * @author zhangchenbiao
 * @FileName: MetaTableColumnFacade
 * @Description:
 * @date 2023-12-04 17:14
 */
public interface MetaTableColumnFacade {

    /**
     * 更新检查项相关的人
     * @param enterpriseId
     * @return
     */
    BaseResultDTO updateQuickColumnUseUser(String enterpriseId);

    /**
     * 更新检查表相关的人
     * @param enterpriseId
     * @return
     */
    BaseResultDTO updateMetaTableUser(String enterpriseId);
}
