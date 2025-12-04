package com.coolcollege.intelligent.dao.supervision;

import com.coolcollege.intelligent.model.supervision.SupervisionHistoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-04-10 03:56
 */
public interface SupervisionHistoryMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-04-10 03:56
     */
    int insertSelective(@Param("record") SupervisionHistoryDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-04-10 03:56
     */
    SupervisionHistoryDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-04-10 03:56
     */
    int updateByPrimaryKeySelective(SupervisionHistoryDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-04-10 03:56
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    int batchInsert(@Param("enterpriseId") String enterpriseId, @Param("records") List<SupervisionHistoryDO> records);

    /**
     * 历史记录列表
     * @param enterpriseId
     * @param taskId
     * @param type
     * @return
     */
    List<SupervisionHistoryDO> selectByTaskIdAndType(@Param("enterpriseId") String enterpriseId,
                                                     @Param("taskId") Long taskId,
                                                     @Param("type") String type,
                                                     @Param("onlyQueryReject") Boolean onlyQueryReject);

}