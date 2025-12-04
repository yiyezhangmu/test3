package com.coolcollege.intelligent.dao.metatable.dao;

import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 标准检查项
 * @author zhangnan
 * @date 2021-12-23 18:54
 */
@Repository
public class TbMetaStaTableColumnDao {

    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    /**
     * 根据检查项id列表查询
     * @param enterpriseId
     * @param ids
     * @return
     */
    public List<TbMetaStaTableColumnDO> selectByIds(String enterpriseId, List<Long> ids) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return tbMetaStaTableColumnMapper.selectByIds(enterpriseId, ids);
    }

    /**
     * 根据检查项id列表查询
     * @param enterpriseId
     * @param id
     * @return
     */
    public TbMetaStaTableColumnDO selectById(String enterpriseId, Long id) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)) {
            return null;
        }
        return tbMetaStaTableColumnMapper.selectByPrimaryKey(enterpriseId, id);
    }

    public Integer batchUpdateExecuteDemand(String enterpriseId, List<TbMetaStaTableColumnDO> list) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(list)) {
            return null;
        }
        return tbMetaStaTableColumnMapper.batchUpdateExecuteDemand(enterpriseId, list);
    }

}