package com.coolcollege.intelligent.dao.storework.dao;

import com.coolcollege.intelligent.dao.storework.SwStoreWorkRangeMapper;
import com.coolcollege.intelligent.model.storework.SwStoreWorkRangeDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class SwStoreWorkRangeDao {
    @Resource
    SwStoreWorkRangeMapper swStoreWorkRangeMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(SwStoreWorkRangeDO record,  String enterpriseId){
        return  swStoreWorkRangeMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public SwStoreWorkRangeDO selectByPrimaryKey(Long id,  String enterpriseId){
        return  swStoreWorkRangeMapper.selectByPrimaryKey(enterpriseId, id);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(SwStoreWorkRangeDO record,  String enterpriseId){
        return  swStoreWorkRangeMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id,  String enterpriseId){
        return  swStoreWorkRangeMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public List<SwStoreWorkRangeDO> selectListByStoreWorkIds( String enterpriseId,List<Long>  storeWorkIds){
        if (CollectionUtils.isEmpty(storeWorkIds)){
            return Collections.emptyList();
        }
        return  swStoreWorkRangeMapper.selectListByStoreWorkIds(enterpriseId,storeWorkIds);
    }

    public Integer batchInsertStoreWorkRange( String enterpriseId, List<SwStoreWorkRangeDO>  storeRangeList){
        return  swStoreWorkRangeMapper.batchInsertStoreWorkRange(enterpriseId,storeRangeList);
    }

    public List<SwStoreWorkRangeDO> listBystoreWorkIds( String enterpriseId, List<Long>  storeWorkIdList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(storeWorkIdList)) {
            return Collections.emptyList();
        }
        return  swStoreWorkRangeMapper.listBystoreWorkIds(enterpriseId, storeWorkIdList);
    }

    public Integer delStoreRangeByStoreWorkId( String enterpriseId, Long storeWorkId){
        return  swStoreWorkRangeMapper.delStoreRangeByStoreWorkId(enterpriseId, storeWorkId);
    }


}