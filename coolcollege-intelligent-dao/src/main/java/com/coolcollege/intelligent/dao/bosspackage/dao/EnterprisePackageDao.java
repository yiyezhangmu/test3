package com.coolcollege.intelligent.dao.bosspackage.dao;

import com.coolcollege.intelligent.dao.bosspackage.EnterprisePackageMapper;
import com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO;
import com.coolcollege.intelligent.model.bosspackage.vo.EnterprisePackageVO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xugangkun
 * @date 2022-03-22 04:10
 */
@Repository
public class EnterprisePackageDao {

    @Resource
    private EnterprisePackageMapper enterprisePackageMapper;

    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-03-22 04:10
     */
    public Long insertSelective(EnterprisePackageDO record) {
        return enterprisePackageMapper.insertSelective(record);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-03-22 04:10
     */
    public EnterprisePackageDO selectByPrimaryKey(Long id) {
        return enterprisePackageMapper.selectByPrimaryKey(id);
    }

    /**
     * 通过名称获取套餐
     * @param packageName
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.EnterprisePackageDO>
     * @date: 2022/3/29 15:08
     */
    public List<EnterprisePackageDO> selectByPackageName(String packageName) {
        return enterprisePackageMapper.selectByPackageName(packageName);
    }

    /**
     * 获得所有企业套餐
     * @param
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.vo.EnterprisePackageVO>
     * @date: 2022/3/24 14:12
     */
    public List<EnterprisePackageVO> selectAll() {
        return enterprisePackageMapper.selectAll();
    }

    /**
     * 获得套餐列表
     * @param status
     * @author: xugangkun
     * @return
     * @date: 2022/3/23 10:16
     */
    public List<EnterprisePackageVO> getPackageList(String status) {
        return enterprisePackageMapper.getPackageList(status);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-03-22 04:10
     */
    public int updateByPrimaryKeySelective(EnterprisePackageDO record) {
        return enterprisePackageMapper.updateByPrimaryKeySelective(record);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-03-22 04:10
     */
    public int deleteByPrimaryKey(Long id) {
        return enterprisePackageMapper.deleteByPrimaryKey(id);
    }
}