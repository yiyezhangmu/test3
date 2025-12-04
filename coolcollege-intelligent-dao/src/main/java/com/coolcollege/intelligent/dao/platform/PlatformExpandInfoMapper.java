package com.coolcollege.intelligent.dao.platform;

import com.coolcollege.intelligent.model.platform.PlatformExpandInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台拓展信息配置表
 * 
 * @author xugangkun
 * @date 2021-12-01 14:26:21
 */
@Mapper
public interface PlatformExpandInfoMapper {
    /**
     * 主键查询
     * @param id
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    PlatformExpandInfoDO selectById(@Param("id") Long id);
    /**
     * 主键查询
     * @param code
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    PlatformExpandInfoDO selectByCode(@Param("code") String code);

    /**
     * 保存
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    void save(@Param("entity") PlatformExpandInfoDO entity);

    /**
     * 根据主键更新
     * @param entity 实体
     * @return: void
     * @Author: xugangkun
     */
    void updateById(@Param("entity") PlatformExpandInfoDO entity);

    /**
     * 根据code更新
     * @param entity 实体
     * @return: void
     * @Author: xugangkun
     */
    void updateByCode(@Param("entity") PlatformExpandInfoDO entity);
    /**
     * 根据主键删除
     * @param id
     * @return: void
     * @Author: xugangkun
     */
    void deleteById(@Param("id") Long id);
    /**
     * 根据主键批量删除
     * @Param:
     * @param ids id列表
     * @return: void
     * @Author: xugangkun
     */
    void deleteBatchByIds(@Param("ids") List<Long> ids);

}
