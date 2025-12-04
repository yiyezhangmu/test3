package com.coolcollege.intelligent.dao.store;

import com.coolcollege.intelligent.model.store.StoreSignInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-05-18 02:14
 */
public interface StoreSignInfoMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-05-18 02:14
     */
    int insertSelective(@Param("record") StoreSignInfoDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-05-18 02:14
     */
    StoreSignInfoDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-05-18 02:14
     */
    int updateByPrimaryKeySelective(StoreSignInfoDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-05-18 02:14
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);


    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-05-18 02:14
     */
    StoreSignInfoDO selectByStoreIdAndSignDate(@Param("enterpriseId") String enterpriseId, @Param("storeId") String storeId, @Param("signDate") String signDate,
                                               @Param("userId") String userId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-05-18 02:14
     */
    List<StoreSignInfoDO> list(@Param("enterpriseId") String enterpriseId,
                               @Param("beginDate") String beginDate,
                               @Param("endDate") String endDate,
                               @Param("regionWays")List<String> regionWays,
                               @Param("storeName")String storeName,
                               @Param("userIdList") List<String> userIdList);
}