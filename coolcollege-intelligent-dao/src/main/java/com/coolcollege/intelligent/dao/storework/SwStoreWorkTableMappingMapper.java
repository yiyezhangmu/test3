package com.coolcollege.intelligent.dao.storework;

import com.coolcollege.intelligent.model.storework.SwStoreWorkTableMappingDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
public interface SwStoreWorkTableMappingMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    int insertSelective(@Param("record")SwStoreWorkTableMappingDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    SwStoreWorkTableMappingDO selectByPrimaryKey(@Param("enterpriseId") String enterpriseId, @Param("id")Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    int updateByPrimaryKeySelective(@Param("record")SwStoreWorkTableMappingDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    int deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    List<SwStoreWorkTableMappingDO> selectListByStoreWorkIds(@Param("enterpriseId") String enterpriseId,@Param("storeWorkIds") List<Long> storeWorkIds);
    List<SwStoreWorkTableMappingDO> selectListByStoreWorkIdsAndMappingId(@Param("enterpriseId") String enterpriseId,
                                                                         @Param("storeWorkIds") String storeWorkId,
                                                                         @Param("tableMappingId") String tableMappingId);

    List<SwStoreWorkTableMappingDO> selectListWithDelByStoreWorkIds(@Param("enterpriseId") String enterpriseId,@Param("storeWorkIds") List<Long> storeWorkIds);

    /**
     * 批量插入执行任务信息
     * @param enterpriseId
     * @param tableMappingDOList
     */
    Integer batchInsertOrUpdateStoreWorkTable(@Param("enterpriseId") String enterpriseId, @Param("list") List<SwStoreWorkTableMappingDO> tableMappingDOList);

    /**
     * 根据店务id删除检查表映射
     * @param enterpriseId
     * @param storeWorkId
     */
    Integer delTableMappingByStoreWorkId(@Param("enterpriseId") String enterpriseId,
                                            @Param("storeWorkId") Long storeWorkId);

    List<SwStoreWorkTableMappingDO> selectListByIds(@Param("enterpriseId") String enterpriseId,@Param("ids") List<Long> ids);

    List<SwStoreWorkTableMappingDO> listByStoreWorkId(@Param("enterpriseId") String enterpriseId,
                                                      @Param("storeWorkId") Long storeWorkId);


}