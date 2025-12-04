package com.coolcollege.intelligent.dao.storework.dao;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.storework.SwStoreWorkDataTableMapper;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkRecordDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkOverViewDataDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkQuestionDataDTO;
import com.coolcollege.intelligent.model.storework.dto.SwStoreWorkRecordStatisticsDTO;
import com.coolcollege.intelligent.model.storework.request.PictureCenterRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkClearDetailRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class SwStoreWorkDataTableDao {
    @Resource
    SwStoreWorkDataTableMapper swStoreWorkDataTableMapper;

    /**
     * 默认插入方法，只会给有值的字段赋值
     * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(SwStoreWorkDataTableDO record, String enterpriseId) {
        return swStoreWorkDataTableMapper.insertSelective(record, enterpriseId);
    }

    /**
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public SwStoreWorkDataTableDO selectByPrimaryKey(Long id, String enterpriseId) {
        return swStoreWorkDataTableMapper.selectByPrimaryKey(enterpriseId, id);
    }

    /**
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(SwStoreWorkDataTableDO record, String enterpriseId) {
        return swStoreWorkDataTableMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    /**
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId) {
        return swStoreWorkDataTableMapper.deleteByPrimaryKey(id, enterpriseId);
    }

    public int batchInsert(String enterpriseId, List<SwStoreWorkDataTableDO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return swStoreWorkDataTableMapper.batchInsert(enterpriseId, list);
    }

    public List<SwStoreWorkDataTableDO> selectSwStoreWorkDataTable(String enterpriseId, String queryDate, String workCycle, String userId, String storeId) {
        return swStoreWorkDataTableMapper.selectSwStoreWorkDataTable(enterpriseId, queryDate, workCycle, userId, storeId);
    }

    public SwStoreWorkDataTableDO selectDataTableByStoreWorkId(String enterpriseId, String storeWorkDate, String userId,
                                                               Long storeWorkId) {
        return swStoreWorkDataTableMapper.selectDataTableByStoreWorkId(enterpriseId, storeWorkDate, userId, storeWorkId);
    }

    public List<SwStoreWorkDataTableDO> selectSwStoreWorkDataTableByBusinessId(String enterpriseId, String businessId, String storeId) {
        if (StringUtils.isEmpty(businessId)) {
            return Collections.emptyList();
        }
        return swStoreWorkDataTableMapper.selectSwStoreWorkDataTableByBusinessId(enterpriseId, businessId, storeId);
    }

    public List<SwStoreWorkDataTableDO> selectSwStoreWorkDataTableByBusinessIds(String enterpriseId, List<String> businessIds) {
        if (CollectionUtils.isEmpty(businessIds)) {
            return Collections.emptyList();
        }
        return swStoreWorkDataTableMapper.selectSwStoreWorkDataTableByBusinessIds(enterpriseId, businessIds);
    }

    public List<SwStoreWorkDataTableDO> selectSwStoreWorkDataTableNoCommentByBusinessIds(String enterpriseId, List<String> businessIds, Long tableMappingId) {
        if (CollectionUtils.isEmpty(businessIds)) {
            return Collections.emptyList();
        }
        return swStoreWorkDataTableMapper.selectSwStoreWorkDataTableNoCommentByBusinessIds(enterpriseId, businessIds, tableMappingId);
    }

    /**
     * 检查表上次检查数据
     *
     * @param enterpriseId
     * @param storeId
     * @param metaTableId
     * @return
     */
    public SwStoreWorkDataTableDO getLastTimeDataTableDO(String enterpriseId, String storeId, Long metaTableId) {
        return swStoreWorkDataTableMapper.getLastTimeDataTableDO(enterpriseId, storeId, metaTableId);
    }

    /**
     * 指定日期 执行人指定门店任务未点评的数量
     *
     * @param enterpriseId
     * @param queryDate
     * @param workCycle
     * @param userId
     * @param storeId
     * @return
     */
    public List<SwStoreWorkDataTableDO> noCommentStoreWorkCount(String enterpriseId, String queryDate, String workCycle, String userId, String storeId) {
        return swStoreWorkDataTableMapper.noCommentStoreWorkCount(enterpriseId, queryDate, workCycle, userId, storeId);
    }

    /**
     * 查询当天店务记录
     *
     * @param enterpriseId
     * @param queryDate
     * @param workCycle
     * @param userId
     * @param storeId
     * @return
     */
    public List<SwStoreWorkDataTableDO> getSwStoreWorkDataTableList(String enterpriseId, String queryDate, String workCycle, String userId, String storeId) {
        return swStoreWorkDataTableMapper.getSwStoreWorkDataTableList(enterpriseId, queryDate, workCycle, userId, storeId);
    }

    public List<SwStoreWorkDataTableDO> getSwStoreWorkDataTableListByBusinessId(String enterpriseId, String userId, String businessId) {
        if (StringUtils.isEmpty(businessId)) {
            return Collections.emptyList();
        }
        return swStoreWorkDataTableMapper.getSwStoreWorkDataTableListByBusinessId(enterpriseId, userId, businessId);
    }


    /**
     * 门店店务数据统计
     *
     * @param enterpriseId
     * @param queryDate
     * @param workCycle
     * @param userId
     * @param storeId
     * @param excludeDefTable 是否排查自定义表
     * @return
     */
    public StoreWorkOverViewDataDTO getStoreWorkOverViewData(String enterpriseId, String queryDate, String workCycle, String userId, String storeId, Boolean excludeDefTable, String businessId) {
        return swStoreWorkDataTableMapper.getStoreWorkOverViewData(enterpriseId, queryDate, workCycle, userId, storeId, excludeDefTable, businessId);
    }


    /**
     * 查询指定时间指定门店当前指定人 一段时间任务是否全部完成
     *
     * @param enterpriseId
     * @param storeId
     * @param userId
     * @param queryTimes
     * @return
     */
    public List<SwStoreWorkDataTableDO> selectSpecialTimeNoCompleteStoreWork(String enterpriseId, String storeId, String userId, String workCycle, List<String> queryTimes) {
        if (StringUtils.isEmpty(storeId)) {
            return Collections.emptyList();
        }
        return swStoreWorkDataTableMapper.selectSpecialTimeNoCompleteStoreWork(enterpriseId, storeId, userId, workCycle, queryTimes);
    }

    public StoreWorkStatisticsOverviewVO storeWorkStoreStatisticsOverview(String enterpriseId, StoreWorkDataListRequest request) {
        return swStoreWorkDataTableMapper.storeWorkStoreStatisticsOverview(enterpriseId, request);
    }

    public Long countStoreWorkStoreStatistics(String enterpriseId, StoreWorkDataListRequest request) {
        Optional.ofNullable(request).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        return swStoreWorkDataTableMapper.countStoreWorkStoreStatistics(enterpriseId, request);
    }

    /**
     * 分页插件失效，导致列表次序变动，采用此方案临时解决
     * @param enterpriseId
     * @param request
     * @return
     */
    public List<StoreWorkDataDetailVO> storeWorkStoreStatisticsList(String enterpriseId, StoreWorkDataListRequest request) {
        Integer pageNumber = request.getPageNumber();
        Integer pageSize = request.getPageSize();
        pageNumber = (pageNumber - 1) * pageSize;
        return swStoreWorkDataTableMapper.storeWorkStoreStatisticsList(enterpriseId, request, pageNumber, pageSize);
    }

    public Integer storeWorkStoreStatisticsListCount(String enterpriseId, StoreWorkDataListRequest request) {
        return swStoreWorkDataTableMapper.storeWorkStoreStatisticsListCount(enterpriseId, request);
    }

    public List<String> getStoreWorkStoreIdList(String enterpriseId, StoreWorkDataListRequest request) {
        return swStoreWorkDataTableMapper.getStoreWorkStoreIdList(enterpriseId, request);
    }

    /**
     * 批量查询SwStoreWorkDataTableDO
     *
     * @param ids
     * @param enterpriseId
     * @return
     */
    public List<SwStoreWorkDataTableDO> selectByIds(List<Long> ids, String enterpriseId) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return swStoreWorkDataTableMapper.selectByIds(ids, enterpriseId);
    }

    /**
     * 批量更新
     *
     * @param enterpriseId
     * @param list
     * @return
     */
    public Boolean batchUpdate(String enterpriseId, List<SwStoreWorkDataTableDO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Boolean.TRUE;
        }
        return swStoreWorkDataTableMapper.batchUpdate(enterpriseId, list);
    }

    public int updateCommentUserIds(String enterpriseId, SwStoreWorkDataTableDO record) {
        return swStoreWorkDataTableMapper.updateCommentUserIds(enterpriseId, record);
    }


    public List<StoreWorkDataTableStatisticsVO> storeWorkTableStatisticsList(String enterpriseId, StoreWorkDataListRequest request) {
        if (request == null) {
            return new ArrayList<>();
        }
        return swStoreWorkDataTableMapper.storeWorkTableStatisticsList(enterpriseId, request);
    }


    public Long countStoreWorkDayStatistics(String enterpriseId, StoreWorkDataListRequest request) {
        Optional.ofNullable(request).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        return swStoreWorkDataTableMapper.countStoreWorkDayStatistics(enterpriseId, request);
    }

    public List<StoreWorkDayStatisticsVO> storeWorkDayStatisticsList(String enterpriseId, StoreWorkDataListRequest request) {
        if (request == null) {
            return new ArrayList<>();
        }
        return swStoreWorkDataTableMapper.storeWorkDayStatisticsList(enterpriseId, request);
    }

    public SwStoreWorkRecordStatisticsDTO statisticsWhenSubmit(String enterpriseId, String tcBusinessId) {
        return swStoreWorkDataTableMapper.statisticsWhenSubmit(enterpriseId, tcBusinessId);
    }

    public SwStoreWorkRecordStatisticsDTO statisticsWhenComment(String enterpriseId, String tcBusinessId) {
        return swStoreWorkDataTableMapper.statisticsWhenComment(enterpriseId, tcBusinessId);
    }

    public List<StoreWorkStatisticsOverviewVO> regionExecutiveSummaryList(String enterpriseId, StoreWorkDataListRequest request) {
        if (request == null) {
            return new ArrayList<>();
        }
        return swStoreWorkDataTableMapper.regionExecutiveSummaryList(enterpriseId, request);
    }

    public SwStoreWorkRecordDO statisticsByTcBusinessId(String enterpriseId, String tcBusinessId) {
//        return swStoreWorkDataTableMapper.statisticsByTcBusinessId(enterpriseId, tcBusinessId);
        return null;
    }

    public List<StoreWorkRecordVO> storeWorkRecordList(String enterpriseId, StoreWorkRecordListRequest request) {
        return swStoreWorkDataTableMapper.storeWorkRecordList(enterpriseId, request);
    }

    public Long countStoreWorkRecord(String enterpriseId, StoreWorkRecordListRequest request) {
        Optional.ofNullable(request).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED));
        return swStoreWorkDataTableMapper.countStoreWorkRecord(enterpriseId, request);
    }

    public List<SwStoreWorkDataTableDO> selectStoreWorkDataTableList(List<String> fullRegionPathList, String enterpriseId, Long storeWorkId, Long tableMappingId, String beginStoreWorkDate,
                                                                     String endStoreWorkDate, String businessId, String workCycle, String storeId, Integer completeStatus, Integer commentStatus) {
        return swStoreWorkDataTableMapper.selectStoreWorkDataTableList(fullRegionPathList, enterpriseId, storeWorkId, tableMappingId, beginStoreWorkDate, endStoreWorkDate, businessId, workCycle,
                storeId, completeStatus, commentStatus);
    }

    public Long countStoreWorkDataTableList(List<String> fullRegionPathList, String enterpriseId, Long storeWorkId, Long tableMappingId, String beginStoreWorkDate,
                                            String endStoreWorkDate, String businessId, String workCycle, String storeId, Integer completeStatus, Integer commentStatus) {
        return swStoreWorkDataTableMapper.countStoreWorkDataTableList(fullRegionPathList, enterpriseId, storeWorkId, tableMappingId, beginStoreWorkDate, endStoreWorkDate, businessId, workCycle,
                storeId, completeStatus, commentStatus);
    }

    public List<SwStoreWorkDataTableDO> listNeedRemindDataTable(String enterpriseId, StoreWorkDataListRequest request) {
        return swStoreWorkDataTableMapper.listNeedRemindDataTable(enterpriseId, request);
    }

    public StoreWorkQuestionDataDTO recordQuestionDate(String enterpriseId, String businessId) {
        return swStoreWorkDataTableMapper.recordQuestionDate(enterpriseId, businessId);
    }

    public List<SwStoreWorkDataTableDO> listNotCompleteDataTableByStoreWorkId(String enterpriseId, Long storeWorkId) {
        return swStoreWorkDataTableMapper.listNotCompleteDataTableByStoreWorkId(enterpriseId, storeWorkId);
    }

    public List<SwStoreWorkDataTableDO> listDataTableHasDelByStoreWorkId(String enterpriseId, Long storeWorkId) {
        return swStoreWorkDataTableMapper.listDataTableHasDelByStoreWorkId(enterpriseId, storeWorkId);
    }


    public Integer updateDelByStoreWorkId(String enterpriseId, Long storeWorkId) {
        return swStoreWorkDataTableMapper.updateDelByStoreWorkId(enterpriseId, storeWorkId);
    }

    /**
     * 店务表数据
     *
     * @param enterpriseId
     * @param request
     * @param beginTime
     * @param endTime
     * @param fullRegionPathList
     * @return
     */
    public List<SwStoreWorkDataTableDO> listDataTable(String enterpriseId, PictureCenterRequest request, String beginTime, String endTime, List<String> fullRegionPathList) {
        return swStoreWorkDataTableMapper.listDataTable(enterpriseId, request.getWorkCycle(), request.getStoreWorkId(),
                request.getMetaTableId(), request.getTableMappingId(), beginTime, endTime, fullRegionPathList);
    }

    public List<SwStoreWorkDataTableDO> selectSwStoreWorkDataTableByUserId(String enterpriseId,
                                                                           String userId) {
        return swStoreWorkDataTableMapper.selectSwStoreWorkDataTableByUserId(enterpriseId, userId);
    }

    public List<SwStoreWorkDataTableDO> selectCommentSwStoreWorkDataTableByUserId(String enterpriseId,
                                                                                  String userId) {
        return swStoreWorkDataTableMapper.selectCommentSwStoreWorkDataTableByUserId(enterpriseId, userId);
    }

    public List<SwStoreWorkDataTableDO> getResultByTcBusinessId(String enterpriseId, String tcBusinessId) {
        return swStoreWorkDataTableMapper.getResultByTcBusinessId(enterpriseId, tcBusinessId);
    }

    public PageInfo<SwStoreWorkRecordDO> selectSwStoreWorkDataTableById(String enterpriseId, String queryDate, StoreWorkClearDetailRequest request, List<String> list) {
        if (StringUtils.isBlank(enterpriseId)) {
            return new PageInfo<>();
        }
        List<SwStoreWorkRecordDO> swStoreWorkRecordDOS = swStoreWorkDataTableMapper.selectSwStoreWorkDataTableById(
                enterpriseId,
                request.getWorkCycle(),
                queryDate,
                request.getCompleteStatue(),
                request.getCommentStatus(),
                list,
                request.getEligibleStatus(),
                request.getMetaTableId());
        return new PageInfo<>(swStoreWorkRecordDOS);
    }


    public void delByStoreWorkIdAndStoreIdAndDate(String enterpriseId, Long storeWorkId, List<String> storeIds, String storeWorkDate) {
        if (StringUtils.isAnyBlank(enterpriseId, storeWorkDate) || Objects.isNull(storeWorkId) || CollectionUtils.isEmpty(storeIds)){
            return;
        }
        swStoreWorkDataTableMapper.delByStoreWorkIdAndStoreIdAndDate(enterpriseId, storeWorkId, storeIds, storeWorkDate);
    }
}