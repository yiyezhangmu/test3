package com.coolcollege.intelligent.dao.supervision;

import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataDefTableColumnDO;
import com.coolcollege.intelligent.model.supervision.SupervisionDefDataColumnDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionDefDataColumnDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-02-27 02:58
 */
public interface SupervisionDefDataColumnMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-02-27 02:58
     */
    int insertSelective(SupervisionDefDataColumnDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-02-27 02:58
     */
    SupervisionDefDataColumnDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-02-27 02:58
     */
    int updateByPrimaryKeySelective(SupervisionDefDataColumnDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-02-27 02:58
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);


    List<SupervisionDefDataColumnDTO> getDataColumnListByTaskIdAndType(@Param("enterpriseId") String enterpriseId,
                                                                       @Param("taskIds")  List<Long> taskIds,
                                                                       @Param("type") String type);

    int batchInsert(@Param("enterpriseId") String enterpriseId,
                    @Param("list") List<SupervisionDefDataColumnDO> list);

    int batchUpdate(@Param("enterpriseId") String enterpriseId,
                    @Param("list") List<SupervisionDefDataColumnDO> supervisionDefDataColumnDO);

    /**
     *
     */
    int updateDelVideo(@Param("enterpriseId") String enterpriseId, @Param("id") Long id,
                       @Param("checkVideo") String checkVideo);

}