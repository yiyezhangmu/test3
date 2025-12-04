package com.coolcollege.intelligent.dao.metatable.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 检查表
 * @author zhangnan
 * @date 2021-12-23 18:54
 */
@Repository
public class TbMetaTableDao {

    @Resource
    private TbMetaTableMapper tbMetaTableMapper;

    /**
     * 根据检查表id列表查询
     * @param enterpriseId 企业id
     * @param metaTableIds 检查表id列表
     * @return List<TbMetaTableDO>
     */
    public List<TbMetaTableDO> selectByIds(String enterpriseId, List<Long> metaTableIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(metaTableIds)) {
            return Lists.newArrayList();
        }
        return tbMetaTableMapper.selectByIds(enterpriseId, metaTableIds);
    }

    /**
     * 根据检查表id查询
     * @param enterpriseId 企业id
     * @param metaTableId 检查表id
     * @return TbMetaTableDO
     */
    public TbMetaTableDO selectById(String enterpriseId, Long metaTableId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(metaTableId)) {
            return null;
        }
        return tbMetaTableMapper.selectById(enterpriseId, metaTableId);
    }

    /**
     * 更新检查表
     * @param enterpriseId 企业id
     * @param ids List<Long>
     */
    public void updateLockedByIds(String enterpriseId, List<Long> ids) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(ids)) {
            return;
        }
        tbMetaTableMapper.updateLockedByIds(enterpriseId, ids);
    }

    /**
     * 查询检查表数量
     * @param enterpriseId
     * @return
     */
    public Integer count(String enterpriseId, String tableType, Integer status) {
        if(StringUtils.isBlank(enterpriseId)) {
            return Constants.ZERO;
        }
        return tbMetaTableMapper.count(enterpriseId, tableType, status);
    }
}
