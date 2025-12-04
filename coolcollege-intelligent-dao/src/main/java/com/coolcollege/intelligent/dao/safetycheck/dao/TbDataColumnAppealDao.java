package com.coolcollege.intelligent.dao.safetycheck.dao;

import com.coolcollege.intelligent.dao.safetycheck.TbDataColumnAppealMapper;
import com.coolcollege.intelligent.model.safetycheck.TbDataColumnAppealDO;
import com.coolcollege.intelligent.model.safetycheck.vo.DataColumnHasHistoryVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class TbDataColumnAppealDao {

    @Resource
    TbDataColumnAppealMapper tbDataColumnAppealMapper;

    /**
     * 默认插入方法，只会给有值的字段赋值
     * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(TbDataColumnAppealDO record, String enterpriseId) {
        return tbDataColumnAppealMapper.insertSelective(record, enterpriseId);
    }

    /**
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public TbDataColumnAppealDO selectByPrimaryKey(Long id, String enterpriseId) {
        return tbDataColumnAppealMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(TbDataColumnAppealDO record, String enterpriseId) {
        return tbDataColumnAppealMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    /**
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId) {
        return tbDataColumnAppealMapper.deleteByPrimaryKey(id, enterpriseId);
    }

    public TbDataColumnAppealDO selectByDataColumnId(String enterpriseId, Long dataColumnId) {
        return tbDataColumnAppealMapper.selectByDataColumnId(dataColumnId, enterpriseId);
    }

    public List<TbDataColumnAppealDO> getLatestAppeal(String enterpriseId, Long businessId) {
        if (businessId == null) {
            return null;
        }
        return tbDataColumnAppealMapper.getLatestAppeal(enterpriseId, businessId);
    }

    public List<TbDataColumnAppealDO> selectListByBusinessId(String enterpriseId, Long businessId) {
        return tbDataColumnAppealMapper.selectListByBusinessId(businessId, enterpriseId);
    }

    public List<TbDataColumnAppealDO> selectListByDataColumnId(String enterpriseId, Long dataColumnId) {
        return tbDataColumnAppealMapper.selectListByDataColumnId(dataColumnId, enterpriseId);
    }

    public List<TbDataColumnAppealDO> selectListByBusinessIdAndStatus(String enterpriseId, Long businessId,
                                                                      String status) {
        return tbDataColumnAppealMapper.selectListByBusinessIdAndStatus(businessId, enterpriseId, status);
    }

    public List<DataColumnHasHistoryVO> getAppealCount(String enterpriseId, Long businessId){
        return tbDataColumnAppealMapper.getAppealCount(enterpriseId,businessId);
    }

    public int updateDelByBusinessIds(String enterpriseId, List<Long> businessIds){
        if (CollectionUtils.isEmpty(businessIds)){
            return 0;
        }
        return tbDataColumnAppealMapper.updateDelByBusinessIds(enterpriseId, businessIds);
    }
}