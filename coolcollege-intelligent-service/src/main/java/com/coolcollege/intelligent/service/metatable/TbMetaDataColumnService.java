package com.coolcollege.intelligent.service.metatable;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaStaTableColumnDetailVO;
import com.coolcollege.intelligent.model.oaPlugin.vo.OptionDataVO;
import com.coolcollege.intelligent.model.patrolstore.vo.QuickTableColumnListVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @author shuchang.wei
 * @date 2020-12-9
 */
public interface TbMetaDataColumnService {
    /**
     * 创建检查表检查项
     * @param enterpriseId 企业id
     * @param tbMetaStaTableColumnList 检查项列表
     * @return
     */
    Integer createStaTableColumn(String enterpriseId,List<TbMetaStaTableColumnDO> tbMetaStaTableColumnList);

    /**
     * 根据检查表的id删除检查表的检查项
     * @param enterpriseId
     * @param checkTableId
     * @return
     */
    Integer deleteStaTableColumn(String enterpriseId,Long checkTableId);


    /**
     * 批量获取检查表的检查项
     * @param enterpriseId
     * @param checkTableIdList
     * @return
     */
    List<TbMetaStaTableColumnDO> getTableColumn(String enterpriseId, List<Long> checkTableIdList,Boolean  filterFreezeColumn);

    PageInfo<QuickTableColumnListVO> getQuickTableColumnList(String enterpriseId, Integer pageSize, Integer pageNum, String columnName,
                                                             Integer columnType, Integer tableProperty, Long categoryId, Integer status, Integer orderBy, Boolean create,
                                                             String userId, Integer isAiCheck);

    List<String> getQuickTableColumnCategory(String enterpriseId);

    Boolean deleteQuickTableColumnCategory(String enterpriseId, String userId, List<Long> columnIdList);

    Boolean importQuickMetaColumn(String enterpriseId, List<Map<String, Object>> dataMapList, String originalFilename, CurrentUser user,
                                  ImportTaskDO task);

    Boolean exitQuickTableColumn(String enterpriseId, String columnName);

    List<TbMetaQuickColumnDO> getAllQuickMetaColumnList(String enterpriseId);

    TbMetaStaTableColumnDetailVO getMetaColumnById(String enterpriseId, Long metaColumnId);

    List<OptionDataVO> listColumnForOaPlugin(String enterpriseId);
}
