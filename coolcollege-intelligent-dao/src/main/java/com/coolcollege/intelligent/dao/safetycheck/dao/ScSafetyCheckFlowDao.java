package com.coolcollege.intelligent.dao.safetycheck.dao;

import com.coolcollege.intelligent.dao.safetycheck.ScSafetyCheckFlowMapper;
import com.coolcollege.intelligent.model.safetycheck.ScSafetyCheckFlowDO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class ScSafetyCheckFlowDao {

    @Resource
    ScSafetyCheckFlowMapper scSafetyCheckFlowMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(ScSafetyCheckFlowDO record, String enterpriseId){
        return scSafetyCheckFlowMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public ScSafetyCheckFlowDO selectByPrimaryKey(Long id, String enterpriseId){
        return scSafetyCheckFlowMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(ScSafetyCheckFlowDO record, String enterpriseId){
        return scSafetyCheckFlowMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId){
        return scSafetyCheckFlowMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public ScSafetyCheckFlowDO getByBusinessId(String enterpriseId, Long businessId){
        return scSafetyCheckFlowMapper.getByBusinessId(enterpriseId, businessId);
    }

    public void updateCurrentNodeAndCycleCount(String enterpriseId, String currentNodeNo,
                                     Integer cycleCount, Long businessId){
        scSafetyCheckFlowMapper.updateCurrentNodeAndCycleCount(enterpriseId, currentNodeNo, cycleCount, businessId);
    }

    public int updateDelByBusinessIds(String enterpriseId, List<Long> businessIds){
        if (CollectionUtils.isEmpty(businessIds)){
            return 0;
        }
        return scSafetyCheckFlowMapper.updateDelByBusinessIds(enterpriseId, businessIds);
    }

}