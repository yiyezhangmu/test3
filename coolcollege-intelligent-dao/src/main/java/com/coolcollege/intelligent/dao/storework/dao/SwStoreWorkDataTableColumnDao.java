package com.coolcollege.intelligent.dao.storework.dao;

import com.coolcollege.intelligent.dao.storework.SwStoreWorkDataTableColumnMapper;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkQuestionDataDTO;
import com.coolcollege.intelligent.model.storework.request.StoreDataListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkColumnDetailListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.vo.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author suzhuhong
 * @date 2022-09-08 02:01
 */
@Repository
public class SwStoreWorkDataTableColumnDao {
    @Resource
    SwStoreWorkDataTableColumnMapper swStoreWorkDataTableColumnMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(SwStoreWorkDataTableColumnDO record, String enterpriseId){
        return swStoreWorkDataTableColumnMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public SwStoreWorkDataTableColumnDO selectByPrimaryKey(Long id, String enterpriseId){
         return swStoreWorkDataTableColumnMapper.selectByPrimaryKey(enterpriseId, id);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(SwStoreWorkDataTableColumnDO record, @Param("enterpriseId") String enterpriseId){
        return swStoreWorkDataTableColumnMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId){
        return swStoreWorkDataTableColumnMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public int batchInsert(String enterpriseId, List<SwStoreWorkDataTableColumnDO> list){
        if (CollectionUtils.isEmpty(list)){
            return 0;
        }
        return swStoreWorkDataTableColumnMapper.batchInsert(enterpriseId,list);
    }

    public List<SwStoreWorkDataTableColumnDO> selectColumnByDataTableId(String enterpriseId,List<Long> dataTableIds,List<String> checkResultList, Integer submitStatus, List<String> aiCheckResultList){
        if (CollectionUtils.isEmpty(dataTableIds)){
            return new ArrayList<>();
        }
        return swStoreWorkDataTableColumnMapper.selectColumnByDataTableId(enterpriseId,dataTableIds,checkResultList, submitStatus, aiCheckResultList);
    }



    public List<SwStoreWorkDataTableColumnDO> selectByDataTableId(String enterpriseId, Long dataTableId){
        return swStoreWorkDataTableColumnMapper.selectByDataTableId(enterpriseId,dataTableId);
    }

    public List<StoreWorkColumnRankDataVO> countFailRank(String enterpriseId,
                                                         String storeWorkBeginDate,
                                                         String storeWorkEndDate,
                                                         List<String> regionPathList,
                                                         String workCycle) {
        return swStoreWorkDataTableColumnMapper.countFailRank(enterpriseId, storeWorkBeginDate, storeWorkEndDate, regionPathList, workCycle);
    }

    public List<ColumnCompleteRateRankDataVO> completeRateRank(String enterpriseId,
                                                         String storeWorkBeginDate,
                                                         String storeWorkEndDate,
                                                         List<String> regionPathList,
                                                         String workCycle,
                                                         String sortField,
                                                        String sortType) {
        return swStoreWorkDataTableColumnMapper.completeRateRank(enterpriseId, storeWorkBeginDate, storeWorkEndDate, regionPathList, workCycle, sortField, sortType);
    }

    /**
     * 批量更新
     * @param enterpriseId
     * @param list
     * @return
     */
    public Boolean batchUpdate(@Param("enterpriseId") String enterpriseId, @Param("list") List<SwStoreWorkDataTableColumnDO> list){
        if (CollectionUtils.isEmpty(list)){
            return Boolean.TRUE;
        }
        return swStoreWorkDataTableColumnMapper.batchUpdate(enterpriseId,list);
    }



    public SwStoreWorkDataTableDO statisticsByDataTableId(String enterpriseId, Long dataTableId) {
        return swStoreWorkDataTableColumnMapper.statisticsByDataTableId(enterpriseId, dataTableId);
    }

    public List<SwStoreWorkDataTableColumnDO> selectByBusinessId(String enterpriseId, String businessId){
        if (businessId==null){
            return Lists.newArrayList();
        }
        return swStoreWorkDataTableColumnMapper.selectByBusinessId(enterpriseId,businessId);
    }

    public List<SwStoreWorkDataTableColumnDO> selectByBusinessIds(String enterpriseId, List<String> businessIds){
        if (CollectionUtils.isEmpty(businessIds)){
            return Lists.newArrayList();
        }
        return swStoreWorkDataTableColumnMapper.selectByBusinessIds(enterpriseId,businessIds);
    }

    public List<SwStoreWorkDataTableColumnDO> selectStoreWorkDataColumnList(List<String> regionPathList, String enterpriseId, Long storeWorkId,
                                                                      Long tableMappingId, String beginStoreWorkDate, String endStoreWorkDate, String workCycle, Long dataTableId){
        return swStoreWorkDataTableColumnMapper.selectStoreWorkDataColumnList(regionPathList, enterpriseId, storeWorkId,
                tableMappingId, beginStoreWorkDate, endStoreWorkDate, workCycle, dataTableId);
    }

    public Long countStoreWorkDataColumnList(List<String> fullRegionPathList, String enterpriseId, Long storeWorkId,
                                            Long tableMappingId, String beginStoreWorkDate, String endStoreWorkDate, String workCycle, Long dataTableId){
        return swStoreWorkDataTableColumnMapper.countStoreWorkDataColumnList(fullRegionPathList, enterpriseId, storeWorkId,
                tableMappingId, beginStoreWorkDate, endStoreWorkDate, workCycle, dataTableId);
    }

    public List<StoreWorkDataTableColumnListVO> columnCompleteRateList(String enterpriseId, StoreWorkDataListRequest request) {
        return swStoreWorkDataTableColumnMapper.columnCompleteRateList(enterpriseId, request);
    }

    public List<StoreWorkColumnStoreListVO> columnStoreCompleteList(String enterpriseId, StoreWorkColumnDetailListRequest request) {
        return swStoreWorkDataTableColumnMapper.columnStoreCompleteList(enterpriseId, request);
    }


    public List<StoreWorkFailColumnStoreListVO> failColumnStoreList(String enterpriseId,
                                                                    StoreDataListRequest queryParam) {
        return swStoreWorkDataTableColumnMapper.failColumnStoreList(enterpriseId, queryParam);
    }

    public StoreWorkQuestionDataDTO tableQuestionDate(String enterpriseId, Long dataTableId) {
        return swStoreWorkDataTableColumnMapper.tableQuestionDate(enterpriseId, dataTableId);
    }

    public int delStoreWorkQuestion(String enterpriseId,Long taskQuestionId, Long dataColumnId) {
        if (dataColumnId==null){
            return 0;
        }
        return swStoreWorkDataTableColumnMapper.delStoreWorkQuestion(enterpriseId,taskQuestionId, dataColumnId);
    }

    public List<SwStoreWorkDataTableColumnDO> selectFailColumnByDataTableId(String enterpriseId, Long dataTableId) {
        if (dataTableId==null){
            return Lists.newArrayList();
        }
        return swStoreWorkDataTableColumnMapper.selectFailColumnByDataTableId(enterpriseId, dataTableId);
    }

    public int updateDelByStoreWorkId(String enterpriseId, Long storeWorkId) {
        return swStoreWorkDataTableColumnMapper.updateDelByStoreWorkId(enterpriseId, storeWorkId);
    }

    public List<SwStoreWorkDataTableColumnDO> selectByIds(String enterpriseId, List<Long> ids){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        return swStoreWorkDataTableColumnMapper.selectByIds(enterpriseId,ids);
    }

    public void delByStoreWorkIdAndStoreIdAndDate(String enterpriseId, Long storeWorkId, List<String> storeIds, String storeWorkDate) {
        if (StringUtils.isAnyBlank(enterpriseId, storeWorkDate) || Objects.isNull(storeWorkId) || CollectionUtils.isEmpty(storeIds)){
            return;
        }
        swStoreWorkDataTableColumnMapper.delByStoreWorkIdAndStoreIdAndDate(enterpriseId, storeWorkId, storeIds, storeWorkDate);
    }

    /**
     * 根据数据表id获取ai检查项
     * @param enterpriseId 企业id
     * @param dataTableId 数据表id
     * @return 检查项列表
     */
    public List<SwStoreWorkDataTableColumnDO> selectAiColumnByDataTableId(String enterpriseId, Long dataTableId) {
        return swStoreWorkDataTableColumnMapper.selectAiColumnByDataTableId(enterpriseId, dataTableId);
    }

    public List<SwStoreWorkDataTableColumnDO> selectAiColumn(String enterpriseId) {
        return swStoreWorkDataTableColumnMapper.selectAiColumn(enterpriseId);
    }


}