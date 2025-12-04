package com.coolcollege.intelligent.dao.unifytask;

import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2022-08-04 11:41
 */
public interface UnifyTaskParentItemMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-08-04 11:41
     */
    int insertSelective(@Param("enterpriseId") String enterpriseId, @Param("record") UnifyTaskParentItemDO record);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-08-04 11:41
     */
    UnifyTaskParentItemDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-08-04 11:41
     */
    int updateByPrimaryKeySelective(@Param("record") UnifyTaskParentItemDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-08-04 11:41
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    List<UnifyTaskParentItemDO> list( @Param("enterpriseId") String eid,  @Param("unifyTaskId") Long unifyTaskId);

    Integer deleteByUnifyTaskId( @Param("enterpriseId") String eid,  @Param("unifyTaskId") Long unifyTaskId);

    Integer deleteByUnifyTaskIdAndStoreIdAndLoopCount(@Param("enterpriseId") String eid,  @Param("unifyTaskId") Long unifyTaskId,
                                                      @Param("storeId") String storeId,  @Param("loopCount") Long loopCount);


    UnifyTaskParentItemDO getByUnifyTaskIdAndStoreIdAndLoopCount(@Param("enterpriseId") String eid,  @Param("unifyTaskId") Long unifyTaskId,
                                                      @Param("storeId") String storeId,  @Param("loopCount") Long loopCount);
}