package com.coolcollege.intelligent.dao.metatable.dao;

import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangnan
 * @description: 自定义检查项Dao
 * @date 2022/3/6 9:01 PM
 */
@Repository
public class TbMetaDefTableColumnDao {

    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;

    /**
     * 根据检查表id查询
     * @param enterpriseId 企业id
     * @param metaTableId 检查表id
     * @return List<TbMetaDefTableColumnDO>
     */
    public List<TbMetaDefTableColumnDO> selectByMetaTableId(String enterpriseId, Long metaTableId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(metaTableId)) {
            return Lists.newArrayList();
        }
        return tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, metaTableId);
    }


    /**
     * 根据id列表查询
     * @param enterpriseId 企业id
     * @param ids 检查项id列表
     * @return List<TbMetaDefTableColumnDO>
     */
    public List<TbMetaDefTableColumnDO> selectByIds(String enterpriseId, List<Long> ids) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return tbMetaDefTableColumnMapper.selectByIds(enterpriseId, ids);
    }
}
