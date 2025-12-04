package com.coolcollege.intelligent.dao.storework.dao;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.storework.SwStoreWorkRecordMapper;
import com.coolcollege.intelligent.model.homepage.vo.StoreWorkDataVO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkRecordDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkStatisticsDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkStatisticsExecutiveDTO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkClearDetailRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class SwStoreWorkRecordDao {
    @Resource
    SwStoreWorkRecordMapper swStoreWorkRecordMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(SwStoreWorkRecordDO record,  String enterpriseId){
        return  swStoreWorkRecordMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public SwStoreWorkRecordDO selectByPrimaryKey(Long id,  String enterpriseId){
        return  swStoreWorkRecordMapper.selectByPrimaryKey(enterpriseId, id);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(SwStoreWorkRecordDO record,  String enterpriseId){
        return  swStoreWorkRecordMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id,  String enterpriseId){
        return  swStoreWorkRecordMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public void batchInsert(String enterpriseId,List<SwStoreWorkRecordDO> entityList){
        if (CollectionUtils.isEmpty(entityList)){
            return;
        }
        swStoreWorkRecordMapper.batchInsert(enterpriseId,entityList);
    }

    public StoreWorkStatisticsDTO countByStoreWorkId(String enterpriseId, Long storeWorkId){
        if(StringUtils.isBlank(enterpriseId) || storeWorkId == null ) {
            return null;
        }
        return swStoreWorkRecordMapper.countByStoreWorkId(enterpriseId,storeWorkId);
    }



    public List<StoreWorkDataVO> countStoreWorkStatistics(String enterpriseId, String storeWorkBeginDate, String storeWorkEndDate,
                                                          List<String> regionPathList, String workCycle, String storeId){
        return swStoreWorkRecordMapper.countStoreWorkStatistics(enterpriseId, storeWorkBeginDate, storeWorkEndDate, regionPathList, workCycle, storeId);
    }

    public StoreWorkDataVO getStoreWorkStatistics(String enterpriseId, String storeWorkBeginDate,  String storeWorkEndDate,
                                                                         List<String> regionPathList, String workCycle){
        return swStoreWorkRecordMapper.getStoreWorkStatistics(enterpriseId, storeWorkBeginDate, storeWorkEndDate, regionPathList, workCycle);
    }


    public StoreWorkStatisticsExecutiveDTO storeExecutiveCompleteRateStatistics(String enterpriseId, String storeWorkBeginDate, String storeWorkEndDate,
                                                                                List<String> regionPathList, String workCycle){
        return swStoreWorkRecordMapper.storeExecutiveCompleteRateStatistics(enterpriseId, storeWorkBeginDate, storeWorkEndDate, regionPathList, workCycle);
    }

    public StoreWorkStatisticsExecutiveDTO storeExecutivePassRateStatistics(String enterpriseId, String storeWorkBeginDate, String storeWorkEndDate,
                                                                                List<String> regionPathList, String workCycle){
        return swStoreWorkRecordMapper.storeExecutivePassRateStatistics(enterpriseId, storeWorkBeginDate, storeWorkEndDate, regionPathList, workCycle);
    }

    public StoreWorkStatisticsOverviewVO storeWorkStoreStatisticsOverview(String enterpriseId, StoreWorkDataListRequest request){
        return swStoreWorkRecordMapper.storeWorkStoreStatisticsOverview(enterpriseId, request);
    }

    public Long countStoreWorkStoreStatistics(String enterpriseId, StoreWorkDataListRequest request) {
        Optional.ofNullable(request).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        return swStoreWorkRecordMapper.countStoreWorkStoreStatistics(enterpriseId, request);
    }

    public Integer storeWorkStoreStatisticsCount(String enterpriseId, StoreWorkDataListRequest request) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0;
        }
        return swStoreWorkRecordMapper.storeWorkStoreStatisticsCount(enterpriseId, request);
    }

    public List<StoreWorkDataDetailVO> storeWorkStoreStatisticsList(String enterpriseId, StoreWorkDataListRequest request) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        Integer pageNumber = request.getPageNumber();
        Integer pageSize = request.getPageSize();
        pageNumber = (pageNumber-1)*pageSize;
        return swStoreWorkRecordMapper.storeWorkStoreStatisticsList(enterpriseId, request,pageNumber,pageSize);
    }

    public List<StoreWorkDataDetailVO> storeWorkStoreStatisticsListNoPage(String enterpriseId, StoreWorkDataListRequest request) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return swStoreWorkRecordMapper.storeWorkStoreStatisticsList(enterpriseId, request,null,null);
    }

    public List<String> getStoreWorkStoreIdList(String enterpriseId, StoreWorkDataListRequest request) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return swStoreWorkRecordMapper.getStoreWorkStoreIdList(enterpriseId, request);
    }



    public PageInfo<SwStoreWorkRecordDO> selectStoreWorkRecord(String enterpriseId,String queryDate, StoreWorkClearDetailRequest request, List<String> list) {
        if (StringUtils.isBlank(enterpriseId)) {
            return new PageInfo<>();
        }
        return new PageInfo<>(swStoreWorkRecordMapper.selectStoreWorkRecord(enterpriseId,
                request.getWorkCycle(),
                queryDate,request.getCompleteStatue(),
                request.getCommentStatus(),
                list,
                request.getEligibleStatus()));
    }

    public StoreWorkRecordStatisticsVO getStoreWorkRecordStatistics(String enterpriseId, String queryDate, StoreWorkClearDetailRequest request, List<String> list) {
        if (StringUtils.isBlank(enterpriseId)) {
            return new StoreWorkRecordStatisticsVO();
        }
        return swStoreWorkRecordMapper.getStoreWorkRecordStatistics(enterpriseId, request.getWorkCycle(),queryDate,request.getCompleteStatue(),request.getCommentStatus(),list);
    }

    public List<SwStoreWorkRecordDO> selectSwStoreWorkRecord(String enterpriseId, String queryDate, String workCycle) {
        return swStoreWorkRecordMapper.selectSwStoreWorkRecord(enterpriseId,queryDate,workCycle);
    }

    public Long countStoreWorkDayStatistics(String enterpriseId, StoreWorkDataListRequest request) {
        Optional.ofNullable(request).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        return swStoreWorkRecordMapper.countStoreWorkDayStatistics(enterpriseId, request);
    }

    public List<StoreWorkDayStatisticsVO> storeWorkDayStatisticsList(String enterpriseId, StoreWorkDataListRequest request) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return swStoreWorkRecordMapper.storeWorkDayStatisticsList(enterpriseId, request);
    }


    public List<StoreWorkStoreRankDataVO> storeExecutiveRank(String enterpriseId, String storeWorkBeginDate,
                                                       String storeWorkEndDate,
                                                       List<String> regionPathList,
                                                       String workCycle,
                                                       String sortField,
                                                       String sortType,
                                                       String storeId) {
        return swStoreWorkRecordMapper.storeExecutiveRank(enterpriseId, storeWorkBeginDate, storeWorkEndDate, regionPathList, workCycle, sortField, sortType, storeId);
    }

    public SwStoreWorkRecordDO getByTcBusinessId(String enterpriseId, String tcBusinessId){
        return  swStoreWorkRecordMapper.getByTcBusinessId(enterpriseId, tcBusinessId);
    }

    public List<SwStoreWorkRecordDO> getByTcBusinessIds(String enterpriseId, List<String> tcBusinessIds){
        if (CollectionUtils.isEmpty(tcBusinessIds)){
            return Collections.emptyList();
        }
        return  swStoreWorkRecordMapper.getByTcBusinessIds(enterpriseId, tcBusinessIds);
    }

    public List<StoreWorkStatisticsOverviewVO> regionExecutiveSummaryList(String enterpriseId, StoreWorkDataListRequest request){
        return swStoreWorkRecordMapper.regionExecutiveSummaryList(enterpriseId, request);
    }

    public List<StoreWorkRecordVO> storeWorkRecordList(String enterpriseId, StoreWorkRecordListRequest request) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return swStoreWorkRecordMapper.storeWorkRecordList(enterpriseId, request);
    }

    public Long countStoreWorkRecord(String enterpriseId, StoreWorkRecordListRequest request) {
        Optional.ofNullable(request).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        return swStoreWorkRecordMapper.countStoreWorkRecord(enterpriseId, request);
    }

    public List<StoreWorkDataStoreDetailList> getStoreDetailWorkStatisticList(String enterpriseId,
                                                                              String storeWorkBeginDate,
                                                                              String storeWorkEndDate,
                                                                              String workCycle,
                                                                              String storeId) {
        return swStoreWorkRecordMapper.getStoreDetailWorkStatisticList(enterpriseId, storeWorkBeginDate, storeWorkEndDate, workCycle, storeId);
    }

    public StoreRankVO selectStoreRank(String enterpriseId,
                                       String workCycle, String storeId,
                                       String storeWorkDate) {
        return swStoreWorkRecordMapper.selectStoreRank(enterpriseId, workCycle, storeId, storeWorkDate);
    }

    public int updateDelByStoreWorkId(String enterpriseId, Long storeWorkId) {
        return swStoreWorkRecordMapper.updateDelByStoreWorkId(enterpriseId, storeWorkId);
    }

    public SwStoreWorkRecordDO getStoreWorkOverViewData(String enterpriseId, String queryDate, String workCycle, String storeId, Boolean excludeDefTable, String businessId){
        return swStoreWorkRecordMapper.getStoreWorkOverViewData(enterpriseId,queryDate,workCycle,storeId,excludeDefTable,businessId);
    }

    public boolean updateCheckResultByTcBusinessId(String enterpriseId, String tcBusinessId, String checkResult) {
        return swStoreWorkRecordMapper.updateBytcBusinessId(enterpriseId,tcBusinessId,checkResult);
    }

    public Integer storeWorkStoreStatisticsListCount(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest) {
        return swStoreWorkRecordMapper.storeWorkStoreStatisticsListCount(enterpriseId,storeWorkDataListRequest);
    }

    public void delByStoreWorkIdAndStoreIdAndDate(String enterpriseId, Long storeWorkId, List<String> storeIds, String storeWorkDate) {
        if (StringUtils.isAnyBlank(enterpriseId, storeWorkDate) || Objects.isNull(storeWorkId) || CollectionUtils.isEmpty(storeIds)){
            return;
        }
        swStoreWorkRecordMapper.delByStoreWorkIdAndStoreIdAndDate(enterpriseId, storeWorkId, storeIds, storeWorkDate);
    }
}