package com.coolcollege.intelligent.dao.workHandover;


import com.coolcollege.intelligent.model.workHandover.WorkHandoverDO;
import com.coolcollege.intelligent.model.workHandover.vo.WorkHandoverVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2022-11-16 11:39
 */
@Mapper
public interface WorkHandoverMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-11-16 11:39
     */
    int insertSelective(@Param("params") WorkHandoverDO record);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-11-16 11:39
     */
    WorkHandoverDO selectByPrimaryKey(@Param("id")Long id);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-11-16 11:39
     */
    int updateByPrimaryKeySelective(@Param("params")WorkHandoverDO record);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-11-16 11:39
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 查询列表
     * @param enterpriseId
     * @param name
     * @return
     */
    List<WorkHandoverDO> selectList(@Param("enterpriseId") String enterpriseId, @Param("name") String name);
}