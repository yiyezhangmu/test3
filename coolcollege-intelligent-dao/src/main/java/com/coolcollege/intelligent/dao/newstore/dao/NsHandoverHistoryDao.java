package com.coolcollege.intelligent.dao.newstore.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.newstore.NsHandoverHistoryMapper;
import com.coolcollege.intelligent.model.newstore.NsHandoverHistoryDO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author zhangnan
 * @description: 新店交接记录Dao
 * @date 2022/3/6 10:06 PM
 */
@Repository
public class NsHandoverHistoryDao {

    @Resource
    private NsHandoverHistoryMapper nsHandoverHistoryMapper;

    /**
     * 批量新增
     * @param enterpriseId 企业id
     * @param handoverHistoryDOList List<NsHandoverHistoryDO>
     */
    public void batchInsert(String enterpriseId, List<NsHandoverHistoryDO> handoverHistoryDOList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(handoverHistoryDOList)) {
            return;
        }
        nsHandoverHistoryMapper.batchInsert(enterpriseId, handoverHistoryDOList);
    }

    /**
     * 分页查询交接记录
     * @param enterpriseId 企业id
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return PageInfo<NsHandoverHistoryDO>
     */
    public PageInfo<NsHandoverHistoryDO> selectPage(String enterpriseId, Integer pageNum, Integer pageSize) {
        if(StringUtils.isBlank(enterpriseId)) {
            return new PageInfo<>();
        }
        PageHelper.startPage(Optional.ofNullable(pageNum).orElse(Constants.INDEX_ONE)
                , Optional.ofNullable(pageSize).orElse(Constants.DEFAULT_PAGE_SIZE));
        return new PageInfo<>(nsHandoverHistoryMapper.selectAll(enterpriseId));
    }
}
