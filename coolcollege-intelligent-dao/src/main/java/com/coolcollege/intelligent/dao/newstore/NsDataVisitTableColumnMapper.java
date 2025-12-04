package com.coolcollege.intelligent.dao.newstore;

import com.coolcollege.intelligent.model.newstore.NsDataVisitTableColumnDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangnan
 * @date 2022-03-04 04:16
 */
public interface NsDataVisitTableColumnMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-03-04 04:16
     */
    int insertSelective(@Param("record")NsDataVisitTableColumnDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-03-04 04:16
     */
    NsDataVisitTableColumnDO selectByPrimaryKey(@Param("id")Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-03-04 04:16
     */
    int updateByPrimaryKeySelective(@Param("record")NsDataVisitTableColumnDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-03-04 04:16
     */
    int deleteByPrimaryKey(@Param("id")Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 批量新增
     * @param enterpriseId 企业id
     * @param columnDOList List<NsDataVisitTableColumnDO>
     */
    void batchInsert(@Param("enterpriseId") String enterpriseId, @Param("list") List<NsDataVisitTableColumnDO> columnDOList);

    /**
     * 根据拜访记录id删除
     * @param enterpriseId 企业id
     * @param recordId 拜访记录id
     */
    void deleteByRecordId(@Param("enterpriseId") String enterpriseId, @Param("recordId") Long recordId);

    /**
     * 根据拜访记录id查询
     * @param enterpriseId 企业id
     * @param recordId 拜访记录id
     * @return List<NsDataVisitTableColumnDO>
     */
    List<NsDataVisitTableColumnDO> selectByRecordId(@Param("enterpriseId") String enterpriseId, @Param("recordId") Long recordId);

    /**
     * 批量更新数据
     * @param enterpriseId 企业id
     * @param updateDOList List<NsDataVisitTableColumnDO>
     */
    void batchUpdateValue(@Param("enterpriseId") String enterpriseId, @Param("list") List<NsDataVisitTableColumnDO> updateDOList);

    /**
     * 根据拜访记录id更新拜访状态
     * @param enterpriseId 企业id
     * @param recordId 拜访记录id
     * @param recordStatus 拜访记录状态
     */
    void updateRecordStatusByRecordId(@Param("enterpriseId") String enterpriseId, @Param("recordId") Long recordId, @Param("recordStatus") String recordStatus);
}