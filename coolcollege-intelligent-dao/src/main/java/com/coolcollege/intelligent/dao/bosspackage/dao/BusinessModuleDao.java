package com.coolcollege.intelligent.dao.bosspackage.dao;

import com.coolcollege.intelligent.dao.bosspackage.BusinessModuleMapper;
import com.coolcollege.intelligent.model.bosspackage.BusinessModuleDO;
import com.coolcollege.intelligent.model.bosspackage.vo.BusinessModuleVO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xugangkun
 * @date 2022-03-22 04:12
 */
@Repository
public class BusinessModuleDao {
    
    @Resource
    private BusinessModuleMapper businessModuleMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-03-22 04:12
     */
    public Long insertSelective(BusinessModuleDO record) {
        return businessModuleMapper.insertSelective(record);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-03-22 04:12
     */
    public BusinessModuleDO selectByPrimaryKey(Long id) {
        return businessModuleMapper.selectByPrimaryKey(id);
    }

    /**
     * 通过业务模块名称获得业务模块
     * @param moduleName
     * @author: xugangkun
     * @return com.coolcollege.intelligent.model.bosspackage.BusinessModuleDO
     * @date: 2022/3/29 15:01
     */
    public List<BusinessModuleDO> selectByModuleName(String moduleName) {
        return businessModuleMapper.selectByModuleName(moduleName);
    }

    /**
     * 获得业务模块列表
     * @param
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.bosspackage.BusinessModuleDO>
     * @date: 2022/3/23 10:16
     */
    public List<BusinessModuleVO> getModuleList(String status) {
        return businessModuleMapper.getModuleList(status);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-03-22 04:12
     */
    public int updateByPrimaryKeySelective(BusinessModuleDO record) {
        return businessModuleMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * 根据主键修改状态
     * @param status
     * @param moduleId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/23 14:22
     */
    public void updateModuleStatus(String status, Long moduleId) {
        businessModuleMapper.updateModuleStatus(status, moduleId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-03-22 04:12
     */
    public int deleteByPrimaryKey(Long id) {
        return businessModuleMapper.deleteByPrimaryKey(id);
    }
}