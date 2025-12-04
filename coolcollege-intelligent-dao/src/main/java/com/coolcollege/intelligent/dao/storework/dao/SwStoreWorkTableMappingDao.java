package com.coolcollege.intelligent.dao.storework.dao;

import com.coolcollege.intelligent.dao.storework.SwStoreWorkTableMappingMapper;
import com.coolcollege.intelligent.model.storework.SwStoreWorkTableMappingDO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class SwStoreWorkTableMappingDao {
    @Resource
    SwStoreWorkTableMappingMapper swStoreWorkTableMappingMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(SwStoreWorkTableMappingDO record, String enterpriseId){
        return swStoreWorkTableMappingMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public SwStoreWorkTableMappingDO selectByPrimaryKey(Long id, String enterpriseId){
        return swStoreWorkTableMappingMapper.selectByPrimaryKey(enterpriseId, id);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(SwStoreWorkTableMappingDO record, String enterpriseId){
        return swStoreWorkTableMappingMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId){
        return swStoreWorkTableMappingMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public  List<SwStoreWorkTableMappingDO> selectListByStoreWorkIdsAndMappingId(String enterpriseId, String storeWorkId,String tableMappingId){
        return swStoreWorkTableMappingMapper.selectListByStoreWorkIdsAndMappingId(enterpriseId,storeWorkId,tableMappingId);
    }

    public  List<SwStoreWorkTableMappingDO> selectListByStoreWorkIds(String enterpriseId, List<Long> storeWorkIds){
        if (CollectionUtils.isEmpty(storeWorkIds)){
            return Collections.emptyList();
        }
        return swStoreWorkTableMappingMapper.selectListByStoreWorkIds(enterpriseId,storeWorkIds);
    }

    public  List<SwStoreWorkTableMappingDO> selectListWithDelByStoreWorkIds(String enterpriseId, List<Long> storeWorkIds){
        if (CollectionUtils.isEmpty(storeWorkIds)){
            return Collections.emptyList();
        }
        return swStoreWorkTableMappingMapper.selectListWithDelByStoreWorkIds(enterpriseId,storeWorkIds);
    }


    public Integer batchInsertOrUpdateStoreWorkTable( String enterpriseId, List<SwStoreWorkTableMappingDO>  tableMappingDOList){
        return  swStoreWorkTableMappingMapper.batchInsertOrUpdateStoreWorkTable(enterpriseId, tableMappingDOList);
    }

    public Integer delTableMappingByStoreWorkId( String enterpriseId, Long storeWorkId){
        return  swStoreWorkTableMappingMapper.delTableMappingByStoreWorkId(enterpriseId, storeWorkId);
    }

    public  List<SwStoreWorkTableMappingDO> selectListByIds(String enterpriseId, List<Long> ids){
        if (CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        return swStoreWorkTableMappingMapper.selectListByIds(enterpriseId,ids);
    }

    public  List<SwStoreWorkTableMappingDO> listByStoreWorkId(String enterpriseId, Long storeWorkId){
        return swStoreWorkTableMappingMapper.listByStoreWorkId(enterpriseId, storeWorkId);
    }

}