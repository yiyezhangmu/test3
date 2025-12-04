package com.coolcollege.intelligent.dao.newstore;

import com.coolcollege.intelligent.model.newstore.NsStoreDO;
import com.coolcollege.intelligent.model.newstore.dto.NsCommonNumDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsStoreDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsStoreQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author zhangnan
 * @date 2022-03-04 04:16
 */
public interface NsStoreMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-03-04 04:16
     */
    int insertSelective(@Param("record")NsStoreDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-03-04 04:16
     */
    NsStoreDO selectByPrimaryKey(@Param("id")Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-03-04 04:16
     */
    int updateByPrimaryKeySelective(@Param("record")NsStoreDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-03-04 04:16
     */
    int deleteByPrimaryKey(@Param("id")Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据新店id列表查询
     * @param enterpriseId 企业id
     * @param newStoreIds 新店id列表
     * @return List<NsStoreDO>
     */
    List<NsStoreDO> selectByIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> newStoreIds);

    /**
     * 批量更新
     * @param enterpriseId 企业id
     * @param nsStoreDOList List<NsStoreDO>
     */
    void batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("list") List<NsStoreDO> nsStoreDOList);


    /**
     * 根据新店类型和状态统计新店数量
     * @param enterpriseId 企业id
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param regionId 区域id
     * @return List<NsCommonCountDTO>
     */
    List<NsCommonNumDTO> selectCountByStoreTypeAndStatus(@Param("enterpriseId") String enterpriseId, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate,
                                                         @Param("regionId") Long regionId);

    /**
     * 新店列表
     * @param enterpriseId
     * @param query
     * @return
     */
    List<NsStoreDTO> selectAllByQuery(@Param("enterpriseId") String enterpriseId, @Param("query") NsStoreQueryDTO query);

    NsStoreDTO getNsStoreDTOById(@Param("id")Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 查询新店数量
     * @param enterpriseId 企业id
     * @param query NsStoreQueryDTO
     * @return Long
     */
    Long selectStoreCount(@Param("enterpriseId") String enterpriseId, @Param("query") NsStoreQueryDTO query);

    /**
     * 订正区域路径
     * @param enterpriseId 企业id
     */
    void correctRegionPath(@Param("enterpriseId") String enterpriseId);

    /**
     * 根据创建人id和新店名称查询数量
     * @param enterpriseId 企业id
     * @param createUserId 创建人id
     * @param newStoreName 新店名称
     * @param newStoreId 新店id
     * @return Long
     */
    Long selectCountByCreateUserIdAndStoreName(@Param("enterpriseId") String enterpriseId, @Param("createUserId") String createUserId,
                                               @Param("newStoreName") String newStoreName, @Param("newStoreId") Long newStoreId);
}