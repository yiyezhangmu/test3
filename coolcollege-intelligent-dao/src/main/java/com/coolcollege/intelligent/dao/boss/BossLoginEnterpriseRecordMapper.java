package com.coolcollege.intelligent.dao.boss;

import com.coolcollege.intelligent.model.boss.BossLoginEnterpriseRecordDO;

/**
 * @author xugangkun
 * @date 2022-04-07 04:04
 */
public interface BossLoginEnterpriseRecordMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-04-07 04:04
     */
    int insertSelective(BossLoginEnterpriseRecordDO record);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-04-07 04:04
     */
    BossLoginEnterpriseRecordDO selectByPrimaryKey(Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-04-07 04:04
     */
    int updateByPrimaryKeySelective(BossLoginEnterpriseRecordDO record);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-04-07 04:04
     */
    int deleteByPrimaryKey(Long id);
}