package com.coolcollege.intelligent.service.patrolstore.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.dto.ColumnAnalyzeDTO;
import com.coolcollege.intelligent.model.patrolstore.request.ColumnDetailListRequest;
import com.coolcollege.intelligent.model.patrolstore.request.ColumnStatisticsRequest;
import com.coolcollege.intelligent.model.patrolstore.statistics.ColumnQuestionTrendDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreColumnStatisticsDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreResultAnalyzeDTO;
import com.coolcollege.intelligent.model.patrolstore.vo.QuestionListVO;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreColumnStatisticsService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/7/8 9:44
 */
@Service
@Slf4j
public class PatrolStoreColumnStatisticsServiceImpl implements PatrolStoreColumnStatisticsService {
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private StoreSceneMapper storeSceneMapper;
    @Resource
    private QuestionRecordDao questionRecordDao;

    @Override
    public List<PatrolStoreColumnStatisticsDTO> columnStatisticsDetail(String enterpriseId, ColumnStatisticsRequest request, String dbName) {
        List<PatrolStoreColumnStatisticsDTO> result = new ArrayList<>();
        List<Long> regionIds = request.getRegionIds();
        if (Objects.nonNull(request.getPageNum()) && Objects.nonNull(request.getPageSize())) {
            //导出和列表调用同一个service 导出有分页调用  列表没有分页
            PageHelper.startPage(request.getPageNum(), request.getPageSize(), false);
        }
        List<TbMetaStaTableColumnDO> metaColumnList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Collections.singletonList(request.getMetaTableId()), Boolean.FALSE);
        if (CollectionUtils.isEmpty(metaColumnList)) {
            throw new ServiceException("检查表的检查项不存在");
        }
        //查询所有得门店场景
        List<StoreSceneDo> storeSceneList = storeSceneMapper.getStoreSceneList(enterpriseId);
        Map<Long, String> storeSceneMap = storeSceneList.stream().collect(Collectors.toMap(StoreSceneDo::getId, StoreSceneDo::getName));
        metaColumnList.stream().forEach(data -> {
            if (storeSceneMap.containsKey(data.getStoreSceneId())) {
                data.setStoreSceneName(storeSceneMap.get(data.getStoreSceneId()));
            }
        });
        List<Long> metaColumnIds = metaColumnList.stream().map(o -> o.getId()).collect(Collectors.toList());
        //区域不为空并发查询区域
        if (CollectionUtils.isNotEmpty(regionIds)) {
            List<RegionDO> regionList = regionMapper.listRegionByIds(enterpriseId, regionIds);
            regionList = dealRegionList(regionList);
            List<Future<List<PatrolStoreColumnStatisticsDTO>>> futureList = new ArrayList<>();
            regionList.stream().forEach(data -> {

                futureList.add(executor.submit(() -> {
                    DataSourceHelper.changeToSpecificDataSource(dbName);
                    return tbDataStaTableColumnMapper.statisticsColumnDetail(enterpriseId, request.getMetaTableId(), null, data.getFullRegionPath(), request.getBeginDate(), request.getEndDate(), metaColumnIds);
                }));
            });
            Map<Long, PatrolStoreColumnStatisticsDTO> tmpMap = new HashMap<>();
            for (Future<List<PatrolStoreColumnStatisticsDTO>> future : futureList) {
                try {
                    List<PatrolStoreColumnStatisticsDTO> list = future.get();

                    if (CollectionUtils.isEmpty(result)) {
                        result.addAll(list);
                    } else {
                        for (PatrolStoreColumnStatisticsDTO patrolStoreColumnStatisticsDTO : result) {
                            tmpMap.put(patrolStoreColumnStatisticsDTO.getMetaColumnId(), patrolStoreColumnStatisticsDTO);
                        }
                        for (PatrolStoreColumnStatisticsDTO data : list) {
                            if (tmpMap.get(data.getMetaColumnId()) == null) {
                                tmpMap.put(data.getMetaColumnId(), data);
                                result.add(data);
                            } else {
                                PatrolStoreColumnStatisticsDTO patrolStoreColumnStatisticsDTO = tmpMap.putIfAbsent(data.getMetaColumnId(), new PatrolStoreColumnStatisticsDTO());
                                patrolStoreColumnStatisticsDTO.setCheckNum(patrolStoreColumnStatisticsDTO.getCheckNum() + data.getCheckNum());
                                patrolStoreColumnStatisticsDTO.setCompleteQuestionNum(patrolStoreColumnStatisticsDTO.getCompleteQuestionNum() + data.getCompleteQuestionNum());
                                patrolStoreColumnStatisticsDTO.setQualifiedNum(patrolStoreColumnStatisticsDTO.getQualifiedNum() + data.getQualifiedNum());
                                patrolStoreColumnStatisticsDTO.setRealTotalScore(patrolStoreColumnStatisticsDTO.getRealTotalScore().add(data.getRealTotalScore()));
                                patrolStoreColumnStatisticsDTO.setTotalQuestionNum(patrolStoreColumnStatisticsDTO.getTotalQuestionNum() + data.getTotalQuestionNum());
                                patrolStoreColumnStatisticsDTO.setUnqualifiedNum(patrolStoreColumnStatisticsDTO.getUnqualifiedNum() + data.getUnqualifiedNum());
                                patrolStoreColumnStatisticsDTO.setUnsuitableNum(patrolStoreColumnStatisticsDTO.getUnsuitableNum() + data.getUnsuitableNum());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("检查项报表统计错误", e);
                    throw new ServiceException("检查项报表统计错误");
                }
            }
        } else if (CollectionUtils.isNotEmpty(request.getStoreIds())) {
            //查询报表数据
            result = tbDataStaTableColumnMapper.statisticsColumnDetail(enterpriseId, request.getMetaTableId(), request.getStoreIds(), null, request.getBeginDate(), request.getEndDate(), metaColumnIds);
        }
        Map<Long, PatrolStoreColumnStatisticsDTO> resultMap = new HashMap<>();
        for (PatrolStoreColumnStatisticsDTO data : result) {
            resultMap.put(data.getMetaColumnId(), data);
        }
        for (TbMetaStaTableColumnDO columnDO : metaColumnList) {
            PatrolStoreColumnStatisticsDTO data = resultMap.get(columnDO.getId());
            if (data == null) {
                data = new PatrolStoreColumnStatisticsDTO();
                data.setMetaColumnId(columnDO.getId());
                data.setColumnName(columnDO.getColumnName());
                data.setCategory(columnDO.getCategoryName());
                data.setStoreSceneName(columnDO.getStoreSceneName());
                result.add(data);
            }
            if (columnDO != null) {
                data.setTotalScore(columnDO.getSupportScore().multiply(new BigDecimal(data.getCheckNum())));
                data.setMetaScore(columnDO.getSupportScore());
                BigDecimal realTotalScore = Objects.isNull(data.getRealTotalScore()) ? new BigDecimal(Constants.ZERO_STR) : data.getRealTotalScore();
                data.setLostScore(data.getTotalScore().subtract(realTotalScore));
                switch (columnDO.getLevel() == null ? "" : columnDO.getLevel()) {
                    case "general":
                        data.setLevel("一般");
                        break;
                    case "redline":
                        data.setLevel("红线");
                        break;
                    case "important":
                        data.setLevel("重要");
                        break;
                    default:
                }
            }
        }
        List<PatrolStoreColumnStatisticsDTO> collect = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result)) {
            double rate = 0;
            for (PatrolStoreColumnStatisticsDTO patrolStoreColumnStatisticsDTO : result) {
                //(qualifiedNum*1d)/checkNum
                rate = patrolStoreColumnStatisticsDTO.getQualifiedNum()*1d / patrolStoreColumnStatisticsDTO.getCheckNum();
                patrolStoreColumnStatisticsDTO.setRate(rate);
            }
            collect = result.stream()
//                    .sorted(Comparator.comparing(PatrolStoreColumnStatisticsDTO::getQualifiedRating).reversed())
                    .sorted(Comparator.comparing(PatrolStoreColumnStatisticsDTO::getRate).reversed())
                    .collect(Collectors.toList());
        }
        return collect;
    }

    private List<RegionDO> dealRegionList(List<RegionDO> regionList) {
        Set<RegionDO> result = new HashSet<>();
        regionList.stream().forEach(data -> {
            if (result.size() == 0) {
                result.add(data);
            } else {
                boolean isExitParent = false;
                for (RegionDO region : result) {
                    //确保子节点不会加入result集合
                    if (data.getFullRegionPath().contains(region.getFullRegionPath())) {
                        isExitParent = true;
                        break;
                    }
                    //如果是父节点，先移除字节点，再把父节点加入集合，然后继续遍历，确保所有子节点都被替换掉
                    if (region.getFullRegionPath().contains(data.getFullRegionPath())) {
                        result.remove(region);
                        result.add(data);
                    }
                }
                if (!isExitParent) {
                    result.add(data);
                }
            }
        });
        return new ArrayList<>(result);
    }

    @Override
    public PatrolStoreResultAnalyzeDTO patrolStoreResultAnalyze(String enterpriseId, ColumnStatisticsRequest request, String dbName) {
        PatrolStoreResultAnalyzeDTO result = new PatrolStoreResultAnalyzeDTO();
        List<Long> regionIds = request.getRegionIds();
        List<String> storeIds = request.getStoreIds();
        if (CollectionUtils.isNotEmpty(regionIds)) {
            List<Future<PatrolStoreResultAnalyzeDTO>> futureList = new ArrayList<>();
            List<RegionDO> regionList = regionMapper.listRegionByIds(enterpriseId, regionIds);
            regionList = dealRegionList(regionList);
            regionList.stream().forEach(data -> {
                futureList.add(executor.submit(() -> {
                    DataSourceHelper.changeToSpecificDataSource(dbName);
                    PatrolStoreResultAnalyzeDTO patrolStoreResultAnalyzeDTO = tbDataStaTableColumnMapper.statisticsPatrolStoreResult(enterpriseId, request.getMetaTableId(), request.getBeginDate(), request.getEndDate(), null, data.getFullRegionPath());
                    if (patrolStoreResultAnalyzeDTO == null) {
                        patrolStoreResultAnalyzeDTO = new PatrolStoreResultAnalyzeDTO();
                    }
                    return patrolStoreResultAnalyzeDTO;
                }));
            });
            for (Future<PatrolStoreResultAnalyzeDTO> future : futureList) {
                try {
                    PatrolStoreResultAnalyzeDTO patrolStoreResultAnalyzeDTO = future.get();
                    result.setQualifiedNum(patrolStoreResultAnalyzeDTO.getQualifiedNum() + result.getQualifiedNum());
                    result.setTotalNum(patrolStoreResultAnalyzeDTO.getTotalNum() + result.getTotalNum());
                    result.setUnqualifiedNum(patrolStoreResultAnalyzeDTO.getUnqualifiedNum() + result.getUnqualifiedNum());
                    result.setUnsuitableNum(patrolStoreResultAnalyzeDTO.getUnsuitableNum() + result.getUnsuitableNum());
                } catch (Exception e) {
                    log.error("检查项巡店结果分析错误", e);
                    throw new ServiceException("检查项巡店结果分析错误");
                }
            }
        } else if (CollectionUtils.isNotEmpty(storeIds)) {
            result = tbDataStaTableColumnMapper.statisticsPatrolStoreResult(enterpriseId, request.getMetaTableId(), request.getBeginDate(), request.getEndDate(), storeIds, null);
        }
        return result;
    }

    @Override
    public List<ColumnQuestionTrendDTO> columnQuestionTrend(String enterpriseId, ColumnStatisticsRequest request, String dbName) {
        LinkedList<ColumnQuestionTrendDTO> result = new LinkedList<>();
        List<Long> regionIds = request.getRegionIds();
        List<String> storeIds = request.getStoreIds();
        if (CollectionUtils.isNotEmpty(regionIds)) {
            List<RegionDO> regionList = regionMapper.listRegionByIds(enterpriseId, regionIds);
            regionList = dealRegionList(regionList);
            List<Future<List<ColumnQuestionTrendDTO>>> futureList = new ArrayList<>();
            regionList.stream().forEach(data -> {
                DataSourceHelper.changeToSpecificDataSource(dbName);
                futureList.add(executor.submit(() -> {
                    DataSourceHelper.changeToSpecificDataSource(dbName);
                    return tbDataStaTableColumnMapper.columnQuestionTrend(enterpriseId, request.getMetaTableId(), request.getBeginDate(), request.getEndDate(), data.getFullRegionPath(), null);
                }));
            });
            Map<Date, ColumnQuestionTrendDTO> map = new HashMap<>();
            for (Future<List<ColumnQuestionTrendDTO>> future : futureList) {
                try {
                    List<ColumnQuestionTrendDTO> trendList = future.get();
                    for (ColumnQuestionTrendDTO trend : trendList) {
                        if (!map.containsKey(trend.getDate())) {
                            map.put(trend.getDate(), trend);
                            result.add(trend);
                        } else {
                            ColumnQuestionTrendDTO tmpTrend = map.get(trend.getDate());
                            trend.setQuestionNum(trend.getQuestionNum() + tmpTrend.getQuestionNum());
                        }
                    }
                } catch (Exception e) {
                    log.error("检查项问题工单趋势统计异常", e);
                    throw new ServiceException("检查项问题工单趋势统计异常");
                }
            }
        } else if (CollectionUtils.isNotEmpty(storeIds)) {
            result = tbDataStaTableColumnMapper.columnQuestionTrend(enterpriseId, request.getMetaTableId(), request.getBeginDate(), request.getEndDate(), null, storeIds);
        }
        Comparator<ColumnQuestionTrendDTO> comparator = Comparator.comparing(ColumnQuestionTrendDTO::getDate);
        //对结果集由大到小进行排序
        Collections.sort(result, comparator);
        int days = daysBetween(request.getBeginDate(), request.getEndDate());
        int j = 0;
        for (int i = 0; i <= days; i++) {
            Date date = calculateDate(request, i);
            //当天的数据在结果集中不存在,则直接插入
            if (j >= result.size()) {
                result.addLast(new ColumnQuestionTrendDTO(date));
            } else {
                ColumnQuestionTrendDTO columnQuestionTrendDTO = result.get(j);
                //当天的数据在结果集种不存在
                if (date.compareTo(columnQuestionTrendDTO.getDate()) != 0) {
                    result.add(j, new ColumnQuestionTrendDTO(date));
                }
            }
            j++;
        }
        return result;
    }


    @Override
    public PageInfo<QuestionListVO> questionList(String enterpriseId, ColumnDetailListRequest request, String dbName) {
        List<TbDataStaTableColumnDO> columnDOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getRegionIds())) {
            RegionDO regionDO = regionMapper.getByRegionId(enterpriseId, request.getRegionIds().get(0));
            if (regionDO != null) {
                PageHelper.startPage(request.getPageNum(), request.getPageSize(), false);
                columnDOList = tbDataStaTableColumnMapper.questionList(enterpriseId, request.getBeginDate(), request.getEndDate(),
                        request.getMetaTableId(), regionDO.getFullRegionPath(), null, request.getGetDirectStore(), request.getStatus(), regionDO.getRegionId());
            }
        } else if (CollectionUtils.isNotEmpty(request.getStoreIds())) {
            PageHelper.startPage(request.getPageNum(), request.getPageSize(), false);
            columnDOList = tbDataStaTableColumnMapper.questionList(enterpriseId, request.getBeginDate(), request.getEndDate(), request.getMetaTableId(), null,
                    request.getStoreIds(), Boolean.FALSE, request.getStatus(), null);
        }
        if (CollectionUtils.isEmpty(columnDOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        //子任务id
        List<Long> taskIdList = new ArrayList<>();
        //子任务id
        List<Long> dataColumnList = new ArrayList<>();
        //门店id
        List<String> finalStoreIdList = new ArrayList<>();
        List<Long> columnIdList = new ArrayList<>();

        columnDOList.forEach(data -> {
            taskIdList.add(data.getTaskQuestionId());
            finalStoreIdList.add(data.getStoreId());
            dataColumnList.add(data.getId());
            columnIdList.add(data.getMetaColumnId());
        });
        //门店
        List<StoreDO> storeDOList = storeMapper.getByStoreIds(enterpriseId, finalStoreIdList);
        //map:storeId -> storeDO
        Map<String, StoreDO> storeMap = storeDOList.stream().collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));

        // Map:taskId->taskParentDO
        List<TaskParentDO> taskParentDOS = taskParentMapper.selectTaskByIds(enterpriseId, taskIdList);
        Map<Long, TaskParentDO> taskIdTaskMap =
                taskParentDOS.stream().collect(Collectors.toMap(TaskParentDO::getId, Function.identity(), (a, b) -> a));

        // Map:userId->UserDO,工单创建人
        Set<String> createUserIds = taskParentDOS.stream().map(TaskParentDO::getCreateUserId).collect(Collectors.toSet());
        List<EnterpriseUserDO> userDOS =
                enterpriseUserMapper.selectUsersByUserIds(enterpriseId, new ArrayList<>(createUserIds));
        Map<String, EnterpriseUserDO> userIdUserMap =
                userDOS.stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, data -> data, (a, b) -> a));

        // Map:taskId->doneTime
        Map<Long, Date> taskIdDoneTimeMap = new HashMap<>();
        List<TbQuestionRecordDO> questionRecordDOList = questionRecordDao.selectListDataColumnIdList(enterpriseId, dataColumnList);
        Map<Long, Long> dataColumnIdTaskStoreIdMap =
                questionRecordDOList.stream().collect(Collectors.toMap(TbQuestionRecordDO::getDataColumnId, TbQuestionRecordDO::getTaskStoreId, (a, b) -> a));
        List<Long> taskStoreIdList = questionRecordDOList.stream().map(TbQuestionRecordDO::getTaskStoreId).collect(Collectors.toList());
        List<TaskStoreDO> taskStores = taskStoreMapper.listByUnifyIds(enterpriseId, taskStoreIdList, null);
        Map<Long, TaskStoreDO> taskStoreMap = new HashMap<>();
        for (TaskStoreDO taskStoreDO : taskStores) {
            taskStoreMap.put(taskStoreDO.getId(), taskStoreDO);
            taskIdDoneTimeMap.put(taskStoreDO.getId(), taskStoreDO.getHandleTime());
        }

        Map<Long, TbMetaStaTableColumnDO> metaColumnMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(columnIdList)) {
            List<TbMetaStaTableColumnDO> columnDOS = tbMetaStaTableColumnMapper.selectByIds(enterpriseId, columnIdList);
            for (TbMetaStaTableColumnDO columnDO : columnDOS) {
                metaColumnMap.put(columnDO.getId(), columnDO);
            }
        }
        List<QuestionListVO> result =
                columnDOList.stream().map(a -> {
                    QuestionListVO questionListVO = new QuestionListVO();
                    questionListVO.setColumnDO(a);
                    TaskParentDO taskParentDO = taskIdTaskMap.get(a.getTaskQuestionId());
                    if (taskParentDO != null) {
                        questionListVO.setTaskParentDO(taskParentDO);
                        EnterpriseUserDO user = userIdUserMap.get(taskParentDO.getCreateUserId());
                        questionListVO.setUser(user);
                    }
                    Long taskStoreId = dataColumnIdTaskStoreIdMap.get(a.getId());
                    if (taskStoreId != null) {
                        questionListVO.setTaskStoreDO(taskStoreMap.get(taskStoreId));
                        questionListVO.setDoneTime(taskIdDoneTimeMap.get(taskStoreId));
                    }
                    questionListVO.setStore(storeMap.get(a.getStoreId()));
                    questionListVO.setMetaColumn(metaColumnMap.get(a.getMetaColumnId()));
                    return questionListVO;
                }).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(columnDOList);
        pageInfo.setList(result);
        pageInfo.setTotal(result.size());
        return pageInfo;
    }

    @Override
    public ColumnAnalyzeDTO columnAnalyze(String enterpriseId, ColumnDetailListRequest request, String dbName) {
        ColumnAnalyzeDTO result = new ColumnAnalyzeDTO();
        List<TbMetaStaTableColumnDO> columnList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Collections.singletonList(request.getMetaTableId()), Boolean.FALSE);
        if (CollectionUtils.isNotEmpty(request.getRegionIds())) {
            List<RegionDO> regionList = regionMapper.listRegionByIds(enterpriseId, request.getRegionIds());
            regionList = dealRegionList(regionList);
            List<Future<ColumnAnalyzeDTO>> futureList = new ArrayList<>();
            regionList.stream().forEach(data -> {
                futureList.add(executor.submit(() -> {
                    DataSourceHelper.changeToSpecificDataSource(dbName);
                    ColumnAnalyzeDTO columnAnalyzeDTO = tbDataStaTableColumnMapper.columnAnalyze(enterpriseId, data.getFullRegionPath(), null, request.getBeginDate(), request.getEndDate(), request.getMetaTableId());
                    if (columnAnalyzeDTO == null) {
                        columnAnalyzeDTO = new ColumnAnalyzeDTO();
                    }
                    columnAnalyzeDTO.setStoreNum(data.getStoreNum() == null ? 0 : data.getStoreNum());
                    return columnAnalyzeDTO;
                }));
            });
            for (Future<ColumnAnalyzeDTO> future : futureList) {
                try {
                    ColumnAnalyzeDTO columnAnalyzeDTO = future.get();
                    result.setCheckNum(result.getCheckNum() + columnAnalyzeDTO.getCheckNum());
                    result.setCheckStoreNum(result.getCheckStoreNum() + columnAnalyzeDTO.getCheckStoreNum());
                    result.setQualifiedNum(result.getQualifiedNum() + columnAnalyzeDTO.getQualifiedNum());
                    result.setQuestionNum(result.getQuestionNum() + columnAnalyzeDTO.getQuestionNum());
                    result.setStoreNum(result.getStoreNum() + columnAnalyzeDTO.getStoreNum());
                } catch (Exception e) {
                    log.error("检查项分析错误异常", e);
                    throw new ServiceException("检查项分析错误异常");
                }
            }
        } else if (CollectionUtils.isNotEmpty(request.getStoreIds())) {
            ColumnAnalyzeDTO columnAnalyzeDTO = tbDataStaTableColumnMapper.columnAnalyze(enterpriseId, null, request.getStoreIds(), request.getBeginDate(), request.getEndDate(), request.getMetaTableId());
            if (columnAnalyzeDTO != null) {
                result = columnAnalyzeDTO;
            }
            result.setStoreNum(request.getStoreIds().size());
        }
        result.setColumnNum(columnList.size());
        return result;
    }

    private Date calculateDate(ColumnStatisticsRequest request, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(request.getBeginDate());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, i);
        return calendar.getTime();
    }

    public int daysBetween(Date smdate, Date bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            log.error("时间解析错误", e);
            throw new ServiceException("时间解析错误");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }


}
