package com.coolcollege.intelligent.dao.safetycheck.dao;

import com.coolcollege.intelligent.dao.safetycheck.TbDataColumnHistoryMapper;
import com.coolcollege.intelligent.model.safetycheck.TbDataColumnHistoryDO;
import com.coolcollege.intelligent.model.safetycheck.vo.DataColumnHasHistoryVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class TbDataColumnHistoryDao {

    @Resource
    TbDataColumnHistoryMapper tbDataColumnHistoryMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(TbDataColumnHistoryDO record, String enterpriseId){
        return tbDataColumnHistoryMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public TbDataColumnHistoryDO selectByPrimaryKey(Long id, String enterpriseId){
        return tbDataColumnHistoryMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(TbDataColumnHistoryDO record, String enterpriseId){
        return tbDataColumnHistoryMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId){
        return tbDataColumnHistoryMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public List<TbDataColumnHistoryDO> listDataColumnCheckHistory(String enterpriseId, Long businessId, Long dataColumnId){
        if (businessId == null || dataColumnId == null){
            return Lists.newArrayList();
        }
        return tbDataColumnHistoryMapper.listDataColumnCheckHistory(enterpriseId, businessId, dataColumnId);
    }

    public List<TbDataColumnHistoryDO> getLatestSubmitInfo(String enterpriseId, Long businessId){
        if (businessId == null){
            return null;
        }
        return tbDataColumnHistoryMapper.getLatestSubmitInfo(enterpriseId, businessId);
    }

    public int batchInsert(String enterpriseId, List<TbDataColumnHistoryDO> list){
        if (CollectionUtils.isEmpty(list)){
            return 0;
        }
        return tbDataColumnHistoryMapper.batchInsert(enterpriseId,list);
    }

    public List<DataColumnHasHistoryVO> getColumnCheckCount(String enterpriseId, Long businessId){
        return tbDataColumnHistoryMapper.getColumnCheckCount(enterpriseId,businessId);
    }

    public Long countByBusinessId(String enterpriseId, Long businessId, Long dataTableId){
        if (businessId == null){
            return 0L;
        }
        return tbDataColumnHistoryMapper.countByBusinessId(enterpriseId, businessId, dataTableId);
    }

    public int updateDelByBusinessIds(String enterpriseId, List<Long> businessIds){
        if (CollectionUtils.isEmpty(businessIds)){
            return 0;
        }
        return tbDataColumnHistoryMapper.updateDelByBusinessIds(enterpriseId, businessIds);
    }


}