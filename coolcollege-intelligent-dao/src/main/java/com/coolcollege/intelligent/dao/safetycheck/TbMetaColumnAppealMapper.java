package com.coolcollege.intelligent.dao.safetycheck;

import com.coolcollege.intelligent.model.metatable.TbMetaColumnReasonDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnReasonDTO;
import com.coolcollege.intelligent.model.safetycheck.TbMetaColumnAppealDO;
import com.coolcollege.intelligent.model.safetycheck.dto.TbMetaColumnAppealDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxps
 * @date 2023-08-14 07:53
 */
public interface TbMetaColumnAppealMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-08-14 07:53
     */
    int insertSelective(@Param("record")TbMetaColumnAppealDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-08-14 07:53
     */
    TbMetaColumnAppealDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-08-14 07:53
     */
    int updateByPrimaryKeySelective(@Param("record")TbMetaColumnAppealDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-08-14 07:53
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    List<TbMetaColumnAppealDTO> getListByMetaTableId(@Param("enterpriseId") String enterpriseId, @Param("metaTableId")  Long metaTableId);

    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<TbMetaColumnAppealDO> list);

    List<TbMetaColumnAppealDTO> getListByColumnId(@Param("enterpriseId") String enterpriseId, @Param("staColumnId")  Long staColumnId);


    List<TbMetaColumnAppealDTO> getListByColumnIdList(@Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> idList);


}