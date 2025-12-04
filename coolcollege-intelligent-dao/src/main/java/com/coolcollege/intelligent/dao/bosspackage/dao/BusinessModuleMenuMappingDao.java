package com.coolcollege.intelligent.dao.bosspackage.dao;

import com.coolcollege.intelligent.dao.bosspackage.BusinessModuleMenuMappingMapper;
import com.coolcollege.intelligent.model.bosspackage.BusinessModuleMenuMappingDO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xugangkun
 * @date 2022-03-22 04:12
 */
@Repository
public class BusinessModuleMenuMappingDao {

    @Resource
    private BusinessModuleMenuMappingMapper businessModuleMenuMappingMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-03-22 04:12
     */
    public int insertSelective(BusinessModuleMenuMappingDO record) {
        return businessModuleMenuMappingMapper.insertSelective(record);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-03-22 04:12
     */
    public BusinessModuleMenuMappingDO selectByPrimaryKey(Long id) {
        return businessModuleMenuMappingMapper.selectByPrimaryKey(id);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-03-22 04:12
     */
    public int updateByPrimaryKeySelective(BusinessModuleMenuMappingDO record) {
        return businessModuleMenuMappingMapper.updateByPrimaryKeySelective(record);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-03-22 04:12
     */
    public int deleteByPrimaryKey(Long id) {
        return businessModuleMenuMappingMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据模块id删除菜单
     * @param moduleId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/23 15:48
     */
    public void deleteByModuleId(Long moduleId) {
        businessModuleMenuMappingMapper.deleteByModuleId(moduleId);
    }

    /**
     * 批量添加业务模块菜单映射
     * @param moduleId
     * @param menuIds
     * @param platform
     * @param createUserId
     * @author: xugangkun
     * @return void
     * @date: 2022/3/23 16:18
     */
    public void batchInsert(Long moduleId, List<Long> menuIds, String platform, String createUserId) {
        if (CollectionUtils.isEmpty(menuIds)) {
            return;
        }
        businessModuleMenuMappingMapper.batchInsert(moduleId, menuIds, platform, createUserId);
    }

    /**
     * 根据模块id获得菜单列表
     * @param moduleId
     * @param platformType
     * @author: xugangkun
     * @return java.util.List<java.lang.Long>
     * @date: 2022/3/28 10:59
     */
    public List<Long> selectMenuIdsByModuleId(Long moduleId, String platformType) {
        return businessModuleMenuMappingMapper.selectMenuIdsByModuleId(moduleId, platformType);
    }

    /**
     * 根据模块id列表获得菜单列表
     * @param moduleIds
     * @param platformType
     * @author: xugangkun
     * @return java.util.List<java.lang.Long>
     * @date: 2022/3/28 11:00
     */
    public List<Long> selectMenuIdsByModuleIds(List<Long> moduleIds, String platformType) {
        if (CollectionUtils.isEmpty(moduleIds)) {
            return new ArrayList<>();
        }
        return businessModuleMenuMappingMapper.selectMenuIdsByModuleIds(moduleIds, platformType);
    }

}