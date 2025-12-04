package com.coolcollege.intelligent.dao.sysconf;

import com.coolcollege.intelligent.model.system.sysconf.SysDictDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/3/25 20:43
 */
@Mapper
public interface SysDictMapper {
    /**
     * 批量插入
     * @param list
     * @return
     */
    Integer batchInsert(@Param("dictList") List<SysDictDO> list);

    /**
     * 批量查询字典项
     * @param businessType
     * @param key
     * @return
     */
    List<SysDictDO> listByTypeKey(@Param("type") String businessType, @Param("key") String key);

    /**
     * 根据Id删除数据
     * @param id
     * @return
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据key和type或id更新value
     * @param dict
     * @return
     */
    int updateValueByIdOrKeyType(@Param("dict") SysDictDO dict);

}
