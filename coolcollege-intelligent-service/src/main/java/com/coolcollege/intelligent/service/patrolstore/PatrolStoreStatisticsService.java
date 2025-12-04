package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.query.*;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolStoreDetailRequest;
import com.coolcollege.intelligent.model.patrolstore.request.StatisticsStaColumnRequest;
import com.coolcollege.intelligent.model.patrolstore.statistics.*;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreOperationDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.List;

/**
 * 报表统计的类，这里做，sum ,group , count , 单行记录的那种报表放在各自的service中做。
 * 
 * @author jeffrey
 * @date 2020/12/09
 */
public interface PatrolStoreStatisticsService {

    /**
     * 按用户统计用户负责的门店数，检查的门店数， 未检查门店数， 检查门店总次数， 总问题数， 带处理问题数， 待复检的问题数， 已解决问题数，问题处理率，问题解决率
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    PageInfo statisticsUser(String enterpriseId, PatrolStoreStatisticsUserQuery query);

    /**
     * 门店维度的统计, 检查次数， 不通过项次数。总问题数， 带处理问题数， 待复检问题数， 已解决问题数， 问题处理率，问题解决率
     *
     *
     * select store_id,count(distinct task_id),count(*),sum(IF(check_result ='success', 1, 0))
     * passtimes,sum(IF(check_result ='fail', 1, 0)) failTimes, sum(IF(check_result ='unapplicableTimes', 1, 0))
     * unapplicableNum, sum(IF(task_question_id > 0,1,0)),sum(if(task_question_status = '带处理',
     * 1,0)),sum(if(task_question_status = '待复检', 1,0)),sum(if(task_question_status = '已解决', 1,0)) from
     * tb_data_sta_table_column_45f92210375346858b6b6694967f44de where store_id in (#storeIds) create_time >=beginDate
     * and create_time<=#{endDate} group by store_id;
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    PageInfo<PatrolStoreStatisticsStoreDTO> statisticsStore(String enterpriseId, PatrolStoreStatisticsStoreQuery query);

    List<PatrolStoreStatisticsRegionDTO> statisticsRegionById(String eid, PatrolStoreStatisticsRegionQuery query);

    List<PatrolStoreStatisticsRegionDTO> statisticsRegion(String enterpriseId, PatrolStoreStatisticsRegionQuery query);


    /**
     * 统计单个检查表的检查项：检查项，使用人数， 检查次数， 检查门店数，检查门店次数， 门店合格率， 总问题数， 待整改问题数， 待复检问题数，问题整改率，问题解决率
     * @param enterpriseId
     * @param request
     * @return
     */
    public PageInfo statisticsColumnPerTable(String enterpriseId, StatisticsStaColumnRequest request);

    /**
     * 统计: 指定父区域下面所有子区域的数据， 总门店数，被检查门店数量，未被检查门店数，检查百分比， 门店检查次数， 巡店人数， 总问题数， 待整改问题数， 待复检问题数，问题整改率，问题解决率
     *
     * @param enterpriseId
     * @param beginDate
     * @param endDate
     * @return
     */
    public List<PatrolStoreStatisticsRegionDTO> statisticsSbuRegion(String enterpriseId, Long fatherRegionId,
        Date beginDate, Date endDate);

    /**
     * 统计 指定区域列表
     *
     * @param enterpriseId
     * @param fatherRegionId
     * @param beginDate
     * @param endDate
     * @return
     */
    public List<PatrolStoreStatisticsRegionDTO> statisticsRegion(String enterpriseId, List<Long> fatherRegionId,
        Date beginDate, Date endDate);


    /**
     * 巡店历史，根据门店id获取巡店历史
     * @return
     */
    PatrolStoreStatisticsHistoryDTO historyByStore(String enterpriseId, PatrolStoreStatisticsHistoryQuery query);

    Object statisticsUserExport(String enterpriseId, PatrolStoreStatisticsUserQuery query);

    Object statisticsColumnPerTableExport(String enterpriseId, StatisticsStaColumnRequest request);

    /**
     * 门店巡店排名
     * @param enterpriseId
     * @param query
     * @return
     */
    @Deprecated
    Object statisticsStoreRank(String enterpriseId, PatrolStoreStatisticsRegionQuery query);

    /**
     * 门店巡店问题排名
     * @param enterpriseId
     * @param query
     * @return
     */
    List<PatrolStoreStatisticsProblemRankDTO> statisticsStoreProblemRank(String enterpriseId, PatrolStoreStatisticsRegionQuery query, CurrentUser user);

    /**
     * 门店巡店统计列表
     * @param enterpriseId
     * @param query
     * @return
     */
    Object storePatrolList(String enterpriseId, PatrolStoreStatisticsRegionQuery query);

    /**
     * App门店运营相关数据
     * @param enterpriseId
     * @param storeId
     * @return
     */
    StoreOperationDTO storeOperation(String enterpriseId, String storeId);


    Object defaultStorePatrolList(String enterpriseId, PatrolStoreStatisticsRegionQuery query,CurrentUser user);

    /**
     * 区域巡店排名
     * @param enterpriseId
     * @param request
     * @param user
     * @return
     */
    List<PatrolStoreStatisticsRankDTO> patrolStoreNumRank(String enterpriseId, PatrolStoreStatisticsRegionQuery request,CurrentUser user);

    /**
     * 区域巡店方式统计
     * @param enterpriseId
     * @param query
     * @return
     */
    PatrolStoreTypeStatisticsDTO statisticsPatrolType(String enterpriseId, PatrolStoreStatisticsRegionQuery query, CurrentUser user);

    /**
     * 区域任务统计
     * @param enterpriseId
     * @param query
     * @return
     */
    PatrolStoreTaskStatisticsDTO statisticsPatrolTask(String enterpriseId, PatrolStoreStatisticsRegionQuery query, CurrentUser user);


    PatrolStoreStatisticsRegionDTO regionsSummary(String enterpriseId, PatrolStoreStatisticsRegionQuery query, CurrentUser user);

    Object statisticsRegionExport(String enterpriseId, PatrolStoreStatisticsRegionQuery query);

    PageInfo<PatrolStoreStatisticsDataDefTableDTO> regionQuestionList(String enterpriseId, PatrolStoreStatisticsDataStaColumnQuery query);

    /**
     * 门店报表导出
     * @param enterpriseId
     * @param beginDate
     * @param endDate
     * @param storeDOList
     * @return
     */
    List<PatrolStoreStatisticsStoreDTO> statisticsStoreDataExport(String enterpriseId,
                                                                  Date beginDate, Date endDate, List<StoreDO> storeDOList);

    List<PatrolStoreStatisticsMetaDefTableDTO> statisticsMetaDefTableAllExport(String enterpriseId,List<TbMetaTableDO> tbMetaTableDOList, Date beginDate, Date endDate);

    ImportTaskDO taskStageRecordListExport(String enterpriseId, PatrolStoreStatisticsDataTableQuery query);

    ImportTaskDO taskStageRecordDetailListExport(String enterpriseId, Long businessId, String dbName);

    /**
     * 检查表报表详情-检查门店数
     * @Author chenyupeng
     * @Date 2021/7/6
     * @param enterpriseId
     * @param query
     * @param user
     * @return: PatrolStoreStatisticsTableDTO
     */
    PatrolStoreStatisticsTableVO getCheckedStore(String enterpriseId, PatrolStoreStatisticsTableQuery query, CurrentUser user);

    /**
     * 按完成率排行
     * @param enterpriseId
     * @param query
     * @param user
     * @return
     */
    PatrolStoreStatisticsTableLeLeTeaVO getLeLeTeaCheckedStore(String enterpriseId, PatrolStoreStatisticsTableQuery query, CurrentUser user);

    /**
     * 检查表报表详情-巡店结果和比例
     * @Author chenyupeng
     * @Date 2021/7/6
     * @param enterpriseId
     * @param query
     * @param user
     * @return: PatrolStoreStatisticsTableColumnVO
     */
    PatrolStoreStatisticsTableGradeVO getPatrolResultProportion(String enterpriseId, PatrolStoreStatisticsTableQuery query, CurrentUser user);


    /**
     * 检查表报表详情-工单数
     * @Author chenyupeng
     * @Date 2021/7/6
     * @param enterpriseId
     * @param query
     * @param user
     * @return: PatrolStoreStatisticsTableDTO
     */
    PatrolStoreStatisticsWorkOrderVO getWorkOrderInfo(String enterpriseId, PatrolStoreStatisticsTableQuery query, CurrentUser user);

    /**
     * 检查表报表图表-检查项统计
     * @Author chenyupeng
     * @Date 2021/7/8
     * @param enterpriseId
     * @param query
     * @param user
     * @return: com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsTableDTO
     */
    PatrolStoreStatisticsTableColumnVO getMetaColumnInfo(String enterpriseId, PatrolStoreStatisticsTableQuery query, CurrentUser user);

    /**
     * 获得10个检查项
     * @param eid
     * @author: xugangkun
     * @return java.util.List<com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsTableColumnDTO>
     * @date: 2021/12/30 21:36
     */
    List<PatrolStoreStatisticsTableColumnDTO> getMetaTableColumnList(String eid);

    List<PatrolStoreStatisticsStaColumnInfoDTO> statisticsStaColumnData(String enterpriseId,
                                                                        List<TbDataStaTableColumnDO> dataStaColumnDOList);

     ImportTaskDO getPatrolStoreDetailExport(String enterpriseId, PatrolStoreDetailRequest query,CurrentUser user);

    /**
     * 按用户统计用户负责的门店数，检查的门店数， 未检查门店数， 检查门店总次数， 总问题数， 带处理问题数， 待复检的问题数， 已解决问题数，问题处理率，问题解决率
     *
     * @param enterpriseId
     * @param query
     * @return
     */
    PageInfo recheckStatisticsUser(String enterpriseId, PatrolStoreRecheckStatisticsUserQuery query, String dbName);
}
