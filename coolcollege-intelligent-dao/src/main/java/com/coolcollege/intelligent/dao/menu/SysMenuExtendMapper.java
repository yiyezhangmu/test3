package com.coolcollege.intelligent.dao.menu;

import com.coolcollege.intelligent.model.menu.SysMenuExtendDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 * @date 2023-12-27 04:24
 */
public interface SysMenuExtendMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-12-27 04:24
     */
    int insertSelective(SysMenuExtendDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-12-27 04:24
     */
    SysMenuExtendDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-12-27 04:24
     */
    int updateByPrimaryKeySelective(SysMenuExtendDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-12-27 04:24
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    void deleteByMenuIdList(@Param("enterpriseId") String enterpriseId, @Param("menuIdList") List<Long> menuIdList);

    void batchInsertMenuExtend(@Param("eid") String enterpriseId,
                             @Param("menuExtendList") List<SysMenuExtendDO> menuExtendList);

    List<SysMenuExtendDO> listByMenuIdList(@Param("enterpriseId") String enterpriseId, @Param("menuIdList") List<Long> menuIdList);

}