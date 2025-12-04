package com.coolcollege.intelligent.dao.boss.dao;

import com.coolcollege.intelligent.dao.boss.BossUserLoginRecordMapper;
import com.coolcollege.intelligent.model.boss.BossUserLoginRecordDO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author xugangkun
 * @date 2022-04-07 04:04
 */
@Repository
public class BossUserLoginRecordDao {
    
    @Resource
    private BossUserLoginRecordMapper bossUserLoginRecordMapper;
    
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-04-07 04:04
     */
    public int insertSelective(BossUserLoginRecordDO record) {
        return bossUserLoginRecordMapper.insertSelective(record);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-04-07 04:04
     */
    public BossUserLoginRecordDO selectByPrimaryKey(Long id) {
        return bossUserLoginRecordMapper.selectByPrimaryKey(id);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-04-07 04:04
     */
    public int updateByPrimaryKeySelective(BossUserLoginRecordDO record) {
        return bossUserLoginRecordMapper.updateByPrimaryKeySelective(record);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-04-07 04:04
     */
    public int deleteByPrimaryKey(Long id) {
        return bossUserLoginRecordMapper.deleteByPrimaryKey(id);
    }
}