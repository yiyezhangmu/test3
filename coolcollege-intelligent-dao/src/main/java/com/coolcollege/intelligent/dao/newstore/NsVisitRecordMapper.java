package com.coolcollege.intelligent.dao.newstore;

import com.coolcollege.intelligent.model.newstore.NsVisitRecordDO;
import com.coolcollege.intelligent.model.newstore.dto.NsCommonNumDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsVisitNumDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsVisitRecordCorrectionDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsVisitRecordQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author zhangnan
 * @date 2022-03-04 04:16
 */
public interface NsVisitRecordMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-03-04 04:16
     */
    int insertSelective(@Param("record")NsVisitRecordDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-03-04 04:16
     */
    NsVisitRecordDO selectByPrimaryKey(@Param("id")Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-03-04 04:16
     */
    int updateByPrimaryKeySelective(@Param("record")NsVisitRecordDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-03-04 04:16
     */
    int deleteByPrimaryKey(@Param("id")Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据创建日期和拜访记录状态查询
     * @param query 新店id，用户id， 创建日期，拜访记录状态
     * @return List<NsVisitRecordDO>
     */
    List<NsVisitRecordDO> selectAllByQuery(NsVisitRecordQueryDTO query);

    /**
     * 根据新店类型和状态统计拜访次数
     * @param enterpriseId 企业id
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param regionId 区域id
     * @return List<NsCommonCountDTO>
     */
    List<NsCommonNumDTO> selectCountByStoreTypeAndStatus(String enterpriseId, Date beginDate, Date endDate, Long regionId);

    /**
     * 根据新店id列表查询拜访次数
     * @param enterpriseId 企业id
     * @param newStoreIds 新店id列表
     * @return List<NsVisitNumDTO>
     */
    List<NsVisitNumDTO> selectNumByStoreIds(@Param("enterpriseId") String enterpriseId, @Param("list") List<Long> newStoreIds);

    /**
     * 根据新店id更新新店数据
     * @param enterpriseId 企业id
     * @param record NsVisitRecordCorrectionDTO
     */
    void updateNewStoreInfoByNewStoreId(@Param("enterpriseId") String enterpriseId, @Param("record") NsVisitRecordCorrectionDTO record);

    /**
     * 查询拜访记录数量
     * @param queryDTO NsVisitRecordQueryDTO
     * @return Long
     */
    Long selectVisitRecordCount(NsVisitRecordQueryDTO queryDTO);

    /**
     * 订正区域路径
     * @param enterpriseId 企业id
     */
    void correctRegionPath(@Param("enterpriseId") String enterpriseId);
}