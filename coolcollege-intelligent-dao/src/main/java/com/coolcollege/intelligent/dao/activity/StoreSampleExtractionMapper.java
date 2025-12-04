package com.coolcollege.intelligent.dao.activity;

import com.coolcollege.intelligent.model.activity.entity.StoreSampleExtractionDO;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchenbiao
 * @date 2024-07-16 03:39
 */
public interface StoreSampleExtractionMapper {
    /**
     * 根据门店id和商品型号查询
     * @param storeId 门店id
     * @param productModel 商品型号
     * @return
     */
    StoreSampleExtractionDO selectByStoreIdAndModel(@Param("storeId") String storeId, @Param("productModel")String productModel);


    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-07-16 03:39
     */
    int insertSelective(@Param("record") StoreSampleExtractionDO record);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-07-16 03:39
     */
    int updateByPrimaryKeySelective(@Param("record")StoreSampleExtractionDO record);

    StoreSampleExtractionDO selectByphysicalNumAndModel(String physicalNum, String productModel);
}