package com.coolcollege.intelligent.dao.activity;


import com.coolcollege.intelligent.model.activity.entity.PromoterStoreInfoDO;

/**
 * @author zhangchenbiao
 * @date 2024-09-02 06:08
 */
public interface PromoterStoreInfoMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-09-02 06:08
     */
    int insertSelective(PromoterStoreInfoDO record);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-09-02 06:08
     */
    int updateByPrimaryKeySelective(PromoterStoreInfoDO record);
}