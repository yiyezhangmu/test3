package com.coolcollege.intelligent.dao.safetycheck.dao;

import com.coolcollege.intelligent.dao.safetycheck.ScSafetyCheckUpcomingMapper;
import com.coolcollege.intelligent.model.safetycheck.ScSafetyCheckUpcomingDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class ScSafetyCheckUpcomingDao {

    @Resource
    ScSafetyCheckUpcomingMapper scSafetyCheckUpcomingMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(ScSafetyCheckUpcomingDO record, String enterpriseId){
        return scSafetyCheckUpcomingMapper.insertSelective(record,enterpriseId);
    }

    public int batchInsert(String enterpriseId, List<ScSafetyCheckUpcomingDO> list){
        if (CollectionUtils.isEmpty(list)){
            return 0;
        }
        return scSafetyCheckUpcomingMapper.batchInsert(enterpriseId,list);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public ScSafetyCheckUpcomingDO selectByPrimaryKey(Long id, String enterpriseId){
        return scSafetyCheckUpcomingMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(ScSafetyCheckUpcomingDO record, String enterpriseId){
        return scSafetyCheckUpcomingMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId){
        return scSafetyCheckUpcomingMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public void updateUpcomingStatus(String enterpriseId, String status, Long businessId,
                                     Integer cycleCount, String nodeNo){
        scSafetyCheckUpcomingMapper.updateUpcomingStatus(enterpriseId, status, businessId, cycleCount, nodeNo);
    }

    public List<ScSafetyCheckUpcomingDO> totoList(String enterpriseId, String userId, List<String> storeIdList){
        return scSafetyCheckUpcomingMapper.totoList(enterpriseId, userId, storeIdList);
    }

    public Long totoListCount(String enterpriseId, String userId){
        return scSafetyCheckUpcomingMapper.totoListCount(enterpriseId, userId);
    }

    public List<String> getByUserIdList(String enterpriseId,
                                        Long businessId,
                                        List<String> userIdList){
        return scSafetyCheckUpcomingMapper.getByUserIdList(enterpriseId, businessId, userIdList);
    }

    public int deleteByBusinessIds(String enterpriseId, List<Long> businessIds){
        if (CollectionUtils.isEmpty(businessIds)){
            return 0;
        }
        return scSafetyCheckUpcomingMapper.deleteByBusinessIds(enterpriseId, businessIds);
    }
}