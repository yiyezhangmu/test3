package com.coolcollege.intelligent.dao.storework;

import com.coolcollege.intelligent.model.storework.SwStoreWorkRangeDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
public interface SwStoreWorkRangeMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    int insertSelective(@Param("record")SwStoreWorkRangeDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    SwStoreWorkRangeDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id")Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    int updateByPrimaryKeySelective(@Param("record")SwStoreWorkRangeDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    List<SwStoreWorkRangeDO> selectListByStoreWorkIds(@Param("enterpriseId") String enterpriseId,@Param("storeWorkIds") List<Long> storeWorkIds);

    /**
     * 批量插入门店范围
     * @param enterpriseId
     * @param storeRangeList
     */
    Integer batchInsertStoreWorkRange(@Param("enterpriseId") String enterpriseId, @Param("list") List<SwStoreWorkRangeDO> storeRangeList);

    List<SwStoreWorkRangeDO> listBystoreWorkIds(@Param("enterpriseId") String enterpriseId, @Param("storeWorkIdList") List<Long> storeWorkIdList);

    /**
     * 根据店务id删除检查表映射
     * @param enterpriseId
     * @param storeWorkId
     */
    Integer delStoreRangeByStoreWorkId(@Param("enterpriseId") String enterpriseId,
                                         @Param("storeWorkId") Long storeWorkId);
}