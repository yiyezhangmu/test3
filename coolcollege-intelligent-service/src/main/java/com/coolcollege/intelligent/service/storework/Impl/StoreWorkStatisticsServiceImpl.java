package com.coolcollege.intelligent.service.storework.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cool.store.enums.SortTypeEnum;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.RegionTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.region.FixedRegionEnum;
import com.coolcollege.intelligent.common.enums.storework.DateStatisticQueryTypeEnum;
import com.coolcollege.intelligent.common.enums.storework.SortFieldEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.TableInfoLabelUtil;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableColumnDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkDataTableDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkRecordDao;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkTableMappingDao;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
import com.coolcollege.intelligent.model.homepage.vo.StoreWorkDataVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableColumnDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDataTableDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkTableMappingDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkStatisticsExecutiveDTO;
import com.coolcollege.intelligent.model.storework.request.*;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.storework.StoreWorkService;
import com.coolcollege.intelligent.service.storework.StoreWorkStatisticsService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author byd
 * @Date 2022/9/8 15:22
 * @Version 1.0
 */
@Slf4j
@Service
public class StoreWorkStatisticsServiceImpl implements StoreWorkStatisticsService {

    @Resource
    private SwStoreWorkRecordDao swStoreWorkRecordDao;

    @Resource
    private UserAuthMappingService userAuthMappingService;

    @Resource
    private RegionService regionService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private SwStoreWorkDataTableColumnDao storeWorkDataTableColumnDao;

    @Resource
    private SwStoreWorkDataTableDao swStoreWorkDataTableDao;

    @Resource
    private ImportTaskService importTaskService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private StoreDao storeDao;

    @Resource
    private SwStoreWorkTableMappingDao swStoreWorkTableMappingDao;

    @Resource
    private StoreWorkService storeWorkService;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;


    @Override
    public StoreWorkDataVO getStoreWorkStatistic(String enterpriseId, StoreWorkDataStatisticRequest queryParam) {

        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        StoreWorkDataVO storeWorkDataVO = new StoreWorkDataVO();
        storeWorkDataVO.setCompleteRate(BigDecimal.ZERO);
        storeWorkDataVO.setAveragePassRate(BigDecimal.ZERO);
        storeWorkDataVO.setAverageScore(BigDecimal.ZERO);
        storeWorkDataVO.setAverageScoreRate(BigDecimal.ZERO);
        storeWorkDataVO.setAverageCommentRate(BigDecimal.ZERO);
        storeWorkDataVO.setQuestionNum(0L);
        storeWorkDataVO.setTotalNum(0L);
        storeWorkDataVO.setFinishNum(0L);
        storeWorkDataVO.setUnFinishNum(0L);
        storeWorkDataVO.setUnApproveQuestionNum(0L);
        storeWorkDataVO.setUnHandleQuestionNum(0L);
        storeWorkDataVO.setFinishQuestionNum(0L);
        //全部
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return storeWorkDataVO;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());

        storeWorkDataVO = swStoreWorkRecordDao.getStoreWorkStatistics(enterpriseId, beginDate,
                endDate, regionPathList, queryParam.getWorkCycle());
        return storeWorkDataVO;
    }

    @Override
    public List<StoreWorkDataVO> getStoreWorkCharStatistic(String enterpriseId, StoreWorkDataStatisticRequest queryParam) {
        //构造空数据

        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        List<String> dateList = DateUtils.getDayOfWeekWithinDateInterval(beginDate, endDate, queryParam.getWorkCycle());
        //全部
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());

        List<StoreWorkDataVO> resultList = swStoreWorkRecordDao.countStoreWorkStatistics(enterpriseId, beginDate,
                endDate, regionPathList, queryParam.getWorkCycle(), null);

        Map<String, StoreWorkDataVO> storeWorkDataMap = ListUtils.emptyIfNull(resultList)
                .stream().filter(a -> a.getStoreWorkDate() != null).collect(Collectors.toMap(StoreWorkDataVO::getStoreWorkDate, Function.identity()));
        resultList.clear();
        dateList.forEach(date -> {
            StoreWorkDataVO storeWorkDataVO = storeWorkDataMap.get(date);
            if(storeWorkDataVO == null){
                storeWorkDataVO = new StoreWorkDataVO();
                storeWorkDataVO.setCompleteRate(BigDecimal.ZERO);
                storeWorkDataVO.setAveragePassRate(BigDecimal.ZERO);
                storeWorkDataVO.setAverageScore(BigDecimal.ZERO);
                storeWorkDataVO.setAverageScoreRate(BigDecimal.ZERO);
                storeWorkDataVO.setAverageCommentRate(BigDecimal.ZERO);
                storeWorkDataVO.setQuestionNum(0L);
                storeWorkDataVO.setTotalNum(0L);
                storeWorkDataVO.setFinishNum(0L);
                storeWorkDataVO.setUnFinishNum(0L);
                storeWorkDataVO.setUnApproveQuestionNum(0L);
                storeWorkDataVO.setUnHandleQuestionNum(0L);
                storeWorkDataVO.setFinishQuestionNum(0L);
                storeWorkDataVO.setStoreWorkDate(date);
            }
            resultList.add(storeWorkDataVO);
        });
        return resultList;
    }

    @Override
    public StoreWorkStatisticsExecutiveDTO storeExecutiveStatistics(String enterpriseId, StoreWorkDataStatisticRequest queryParam) {
        StoreWorkStatisticsExecutiveDTO storeWorkDataVO = new StoreWorkStatisticsExecutiveDTO();
        storeWorkDataVO.setFirstStageNum(0L);
        storeWorkDataVO.setSecondStageNum(0L);
        storeWorkDataVO.setThirdStageNum(0L);
        storeWorkDataVO.setFourStageNum(0L);
        storeWorkDataVO.setTotalNum(0L);
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return storeWorkDataVO;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        if(DateStatisticQueryTypeEnum.COMPLETE_RATE.getCode().equals(queryParam.getSortField())){
            storeWorkDataVO = swStoreWorkRecordDao.storeExecutiveCompleteRateStatistics(enterpriseId, beginDate, endDate, regionPathList, queryParam.getWorkCycle());
        }else {
            storeWorkDataVO = swStoreWorkRecordDao.storeExecutivePassRateStatistics(enterpriseId, beginDate, endDate, regionPathList, queryParam.getWorkCycle());
        }
        return storeWorkDataVO;
    }

    @Override
    public List<StoreWorkRegionRankDataVO> regionExecutiveRank(String enterpriseId, StoreWorkDataStatisticRequest queryParam) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());

        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        List<StoreWorkRegionRankDataVO> regionRankDataVOList = new ArrayList<>();
        regionPathDTOList =  getRegionPathList(enterpriseId, isAdmin, regionPathDTOList, queryParam.getRegionIdList(), true);
        List<StoreWorkRegionRankDataVO> finalRegionRankDataVOList = new ArrayList<>();
        regionPathDTOList.stream().filter(e -> !RegionTypeEnum.STORE.getType().equals(e.getRegionType())
            && !FixedRegionEnum.getExcludeRegionId().contains(e.getRegionId())).forEach(regionPathDTO -> {
            StoreWorkDataVO dto = swStoreWorkRecordDao.getStoreWorkStatistics(enterpriseId, beginDate,
                    endDate, Collections.singletonList(regionPathDTO.getRegionPath()), queryParam.getWorkCycle());
            StoreWorkRegionRankDataVO dataVO = StoreWorkRegionRankDataVO.getDataVO(Long.valueOf(regionPathDTO.getRegionId()), regionPathDTO.getRegionName(), dto);
            finalRegionRankDataVOList.add(dataVO);
        });
        //非管理员且没有管辖区域
        if(CollectionUtils.isEmpty(finalRegionRankDataVOList)){
            return new ArrayList<>();
        }
        regionRankDataVOList = CollectionUtil.sortByProperty(finalRegionRankDataVOList, queryParam.getSortField());
        if(SortTypeEnum.DESC.name().equals(queryParam.getSortType())){
            CollectionUtil.reverse(regionRankDataVOList);
        }
        if(CollectionUtils.isNotEmpty(regionRankDataVOList) && regionRankDataVOList.size() > Constants.INDEX_TWENTY){
            regionRankDataVOList = regionRankDataVOList.subList(0, Constants.INDEX_TWENTY);
        }
        return regionRankDataVOList;
    }

    @Override
    public List<StoreWorkStoreRankDataVO> storeExecutiveRank(String enterpriseId, StoreWorkDataStatisticRequest queryParam) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin,  queryParam.getUserId(), queryParam.getRegionIdList());
        if(StringUtils.isBlank(queryParam.getSortField())){
            queryParam.setSortField(DateStatisticQueryTypeEnum.COMPLETE_RATE.getCode());
        }
        DateStatisticQueryTypeEnum dateStatisticQueryTypeEnum = DateStatisticQueryTypeEnum.getEnumByCode(queryParam.getSortField());
        if(dateStatisticQueryTypeEnum == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        return swStoreWorkRecordDao.storeExecutiveRank(enterpriseId, beginDate, endDate,
                regionPathList, queryParam.getWorkCycle(), queryParam.getSortField(), queryParam.getSortType(), null);
    }

    @Override
    public List<StoreWorkColumnRankDataVO> columnFailRank(String enterpriseId, StoreWorkDataStatisticRequest queryParam) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        return storeWorkDataTableColumnDao.countFailRank(enterpriseId, beginDate, endDate,
                regionPathList, queryParam.getWorkCycle());
    }

    @Override
    public List<ColumnCompleteRateRankDataVO> completeRateRank(String enterpriseId, StoreWorkDataStatisticRequest queryParam) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        return storeWorkDataTableColumnDao.completeRateRank(enterpriseId, beginDate, endDate,
                regionPathList, queryParam.getWorkCycle(), queryParam.getSortField(), queryParam.getSortType());
    }

    @Override
    public List<StoreWorkStatisticsOverviewVO> regionExecutiveList(String enterpriseId, StoreWorkDataListRequest queryParam) {
        if(queryParam.getSortField() == null){
            queryParam.setSortField(SortFieldEnum.finishPercent);
        }
        if(queryParam.getSortType() == null){
            queryParam.setSortType(com.coolcollege.intelligent.common.enums.storework.SortTypeEnum.DESC);
        }
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());

        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        //是否下探
        //是否是查询子节点数据
        if (queryParam.getChildRegion() && CollectionUtils.isNotEmpty(queryParam.getRegionIdList())){
            String parentId = queryParam.getRegionIdList().get(0);
            List<Long> nextRegionIdList = regionMapper.getRegionIdListByParentId(enterpriseId, Long.valueOf(parentId));
            if(CollectionUtils.isEmpty(nextRegionIdList)){
                regionPathDTOList = new ArrayList<>();
            }else {
                List<String> nextRegionIdStrList = nextRegionIdList.stream().map(String::valueOf).collect(Collectors.toList());
                regionPathDTOList = regionService.getRegionPathByList(enterpriseId, nextRegionIdStrList);
            }
        }else {
            regionPathDTOList =  getRegionPathList(enterpriseId, isAdmin, regionPathDTOList, queryParam.getRegionIdList(), false);
        }
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        //没有区域则直接返回空列表
        if(CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        List<StoreWorkStatisticsOverviewVO> storeWorkStatisticsOverviewVOList = new ArrayList<>();

        List<StoreWorkStatisticsOverviewVO> finalStoreWorkStatisticsOverviewVOList = storeWorkStatisticsOverviewVOList;
        queryParam.setBeginStoreWorkDate(DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        queryParam.setEndStoreWorkDate(DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        regionPathDTOList.stream().filter(e -> !RegionTypeEnum.STORE.getType().equals(e.getRegionType()) && !FixedRegionEnum.getExcludeRegionId().contains(e.getRegionId())).forEach(regionPathDTO -> {
            queryParam.setRegionPathList(Collections.singletonList(regionPathDTO.getRegionPath()));
            StoreWorkStatisticsOverviewVO storeWorkStatisticsOverviewVO;
            if(queryParam.getTableMappingId() != null){
                storeWorkStatisticsOverviewVO = swStoreWorkDataTableDao.storeWorkStoreStatisticsOverview(enterpriseId, queryParam);
            }else {
                storeWorkStatisticsOverviewVO = swStoreWorkRecordDao.storeWorkStoreStatisticsOverview(enterpriseId, queryParam);
            }
            if(storeWorkStatisticsOverviewVO == null){
                storeWorkStatisticsOverviewVO = new StoreWorkStatisticsOverviewVO();
                storeWorkStatisticsOverviewVO.setFinishPercent(BigDecimal.ZERO);
                storeWorkStatisticsOverviewVO.setTotalStoreNum(0L);
                storeWorkStatisticsOverviewVO.setUnFinishStoreNum(0L);
                storeWorkStatisticsOverviewVO.setFinishStoreNum(0L);
                storeWorkStatisticsOverviewVO.setAvgPassRate(BigDecimal.ZERO);
                storeWorkStatisticsOverviewVO.setAvgScore(BigDecimal.ZERO);
                storeWorkStatisticsOverviewVO.setAvgScoreRate(BigDecimal.ZERO);
                storeWorkStatisticsOverviewVO.setAvgCommentRate(BigDecimal.ZERO);
                storeWorkStatisticsOverviewVO.setQuestionNum(0L);
                storeWorkStatisticsOverviewVO.setFailColumnNum(0L);
                storeWorkStatisticsOverviewVO.setPassColumnNum(0L);
            }
            storeWorkStatisticsOverviewVO.setRegionId(Long.valueOf(regionPathDTO.getRegionId()));
            storeWorkStatisticsOverviewVO.setRegionName(regionPathDTO.getRegionName());
            storeWorkStatisticsOverviewVO.setFullRegionName(regionService.getAllRegionName(enterpriseId, Long.valueOf(regionPathDTO.getRegionId())).getAllRegionName());
            finalStoreWorkStatisticsOverviewVOList.add(storeWorkStatisticsOverviewVO);
        });
        storeWorkStatisticsOverviewVOList = CollectionUtil.sortByProperty(finalStoreWorkStatisticsOverviewVOList, queryParam.getSortField().name());
        if(SortTypeEnum.DESC.name().equals(queryParam.getSortType().name())){
            CollectionUtil.reverse(storeWorkStatisticsOverviewVOList);
        }
        return storeWorkStatisticsOverviewVOList;
    }

    @Override
    public ImportTaskDO exportRegionExecutiveList(String enterpriseId, StoreWorkDataListRequest queryParam, String dbName)  {
        // 查询导出数量，限流
        Long count = Constants.MAX_EXPORT_SIZE;
        queryParam.setBeginStoreWorkDate(DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        queryParam.setEndStoreWorkDate(DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY);
        // 通过枚举获取文件名称
        String fileName = ExportServiceEnum.REGION_EXECUTIVE_LIST_REPORT.getFileName() + Constants.SPLIT_LINE + beginDate;

        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());

        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());

        regionPathDTOList = ListUtils.emptyIfNull(regionPathDTOList).stream().filter(e -> !RegionTypeEnum.STORE.getType().equals(e.getRegionType())).collect(Collectors.toList());

            //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            throw new ServiceException(ErrorCodeEnum.ACH_NO_DATA_EXPORT);
        }
        queryParam.setExportServiceEnum(ExportServiceEnum.REGION_EXECUTIVE_LIST_REPORT);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName.replace("exportTime", DateUtil.format(new Date(), DateUtils.DATE_FORMAT_MINUTE)), ImportTaskConstant.REGION_EXECUTIVE_LIST_REPORT);
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(queryParam)));
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(dbName);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public List<StoreWorkStatisticsOverviewListVO> regionExecutiveSummaryList(String enterpriseId, StoreWorkDataListRequest queryParam) {
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());
        queryParam.setBeginStoreWorkDate(DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        queryParam.setEndStoreWorkDate(DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());
        regionPathDTOList = ListUtils.emptyIfNull(regionPathDTOList).stream().filter(e -> !RegionTypeEnum.STORE.getType().equals(e.getRegionType())).collect(Collectors.toList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        //是否下探
        //是否是查询子节点数据
        if (queryParam.getChildRegion() && CollectionUtils.isNotEmpty(queryParam.getRegionIdList())){
            String parentId = queryParam.getRegionIdList().get(0);
            List<Long> nextRegionIdList = regionMapper.getRegionIdListByParentId(enterpriseId, Long.valueOf(parentId));
            if(CollectionUtils.isEmpty(nextRegionIdList)){
                regionPathDTOList = new ArrayList<>();
            }else {
                List<String> nextRegionIdStrList = nextRegionIdList.stream().map(String::valueOf).collect(Collectors.toList());
                regionPathDTOList = regionService.getRegionPathByList(enterpriseId, nextRegionIdStrList);
            }
        }else {
            regionPathDTOList =  getRegionPathList(enterpriseId, isAdmin, regionPathDTOList, queryParam.getRegionIdList(), false);
        }
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        List<String> dateList =  DateUtils.getDayOfWeekWithinDateInterval(queryParam.getBeginStoreWorkDate(), queryParam.getEndStoreWorkDate(), queryParam.getWorkCycle());
        //没有区域则直接返回空列表
        if(CollectionUtils.isEmpty(regionPathDTOList)){
            return new ArrayList<>();
        }
        List<StoreWorkStatisticsOverviewListVO> resultList = new ArrayList<>();
        //todo byd 多线程处理
        regionPathDTOList.stream().filter(e -> !RegionTypeEnum.STORE.getType().equals(e.getRegionType())).forEach(regionPathDTO -> {
            StoreWorkStatisticsOverviewListVO storeWorkListVO = new StoreWorkStatisticsOverviewListVO();
            storeWorkListVO.setRegionId(Long.valueOf(regionPathDTO.getRegionId()));
            storeWorkListVO.setRegionName(regionPathDTO.getRegionName());
            storeWorkListVO.setFullRegionName(regionService.getAllRegionName(enterpriseId, Long.valueOf(regionPathDTO.getRegionId())).getAllRegionName());
            List<StoreWorkStatisticsOverviewVO> dateListResult;
            queryParam.setRegionPath(regionPathDTO.getRegionPath());
            if(queryParam.getTableMappingId() != null){
                dateListResult = swStoreWorkDataTableDao.regionExecutiveSummaryList(enterpriseId, queryParam);
            }else {
                dateListResult = swStoreWorkRecordDao.regionExecutiveSummaryList(enterpriseId, queryParam);
            }
            Map<String, StoreWorkStatisticsOverviewVO> storeWorkDataMap = ListUtils.emptyIfNull(dateListResult)
                    .stream().collect(Collectors.toMap(StoreWorkStatisticsOverviewVO::getStoreWorkDate, Function.identity()));
            dateListResult.clear();
            dateList.forEach(date -> {
                StoreWorkStatisticsOverviewVO storeWorkDataVO = storeWorkDataMap.get(date);
                if(storeWorkDataVO == null){
                    storeWorkDataVO = new StoreWorkStatisticsOverviewVO();
                    storeWorkDataVO.setFinishPercent(BigDecimal.ZERO);
                    storeWorkDataVO.setTotalStoreNum(0L);
                    storeWorkDataVO.setUnFinishStoreNum(0L);
                    storeWorkDataVO.setFinishStoreNum(0L);
                    storeWorkDataVO.setAvgPassRate(BigDecimal.ZERO);
                    storeWorkDataVO.setAvgScore(BigDecimal.ZERO);
                    storeWorkDataVO.setAvgScoreRate(BigDecimal.ZERO);
                    storeWorkDataVO.setAvgCommentRate(BigDecimal.ZERO);
                    storeWorkDataVO.setQuestionNum(0L);
                    storeWorkDataVO.setFailColumnNum(0L);
                    storeWorkDataVO.setPassColumnNum(0L);
                    storeWorkDataVO.setStoreWorkDate(date);
                }
                dateListResult.add(storeWorkDataVO);
            });
            storeWorkListVO.setStatisticsOverviewVOList(dateListResult);
            resultList.add(storeWorkListVO);
        });
        return resultList;
    }

    @Override
    public ImportTaskDO exportRegionExecutiveSummaryList(String enterpriseId, StoreWorkDataListRequest queryParam, String dbName)  {
        // 查询导出数量，限流
        Long count = Constants.MAX_EXPORT_SIZE;
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY);
        queryParam.setBeginStoreWorkDate(DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        queryParam.setEndStoreWorkDate(DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());

        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            throw new ServiceException(ErrorCodeEnum.ACH_NO_DATA_EXPORT);
        }
        // 通过枚举获取文件名称
        String fileName = ExportServiceEnum.REGION_EXECUTIVE_SUMMARY_LIST_REPORT.getFileName() + Constants.SPLIT_LINE + beginDate + Constants.SPLIT_LINE + queryParam.getEndStoreWorkDate();
        queryParam.setExportServiceEnum(ExportServiceEnum.REGION_EXECUTIVE_SUMMARY_LIST_REPORT);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ExportServiceEnum.REGION_EXECUTIVE_SUMMARY_LIST_REPORT.getCode());
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(queryParam)));
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(dbName);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public PageInfo<StoreWorkStoreSummaryVO> storeExecutiveSummaryList(String enterpriseId, StoreWorkDataListRequest queryParam) {
        queryParam.setSummary(true);
        queryParam.setBeginStoreWorkDate(DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        queryParam.setEndStoreWorkDate(DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        List<String> dateList =  DateUtils.getDayOfWeekWithinDateInterval(queryParam.getBeginStoreWorkDate(), queryParam.getEndStoreWorkDate(), queryParam.getWorkCycle());
        //全部
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, queryParam.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, queryParam.getUserId(), queryParam.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new PageInfo<>();
        }
        List<String> regionIdList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionId)
                .collect(Collectors.toList());
        PageHelper.startPage(queryParam.getPageNumber(), queryParam.getPageSize());
        queryParam.setRegionIdList(regionIdList);
        List<String> storeIdList = Lists.newArrayList();
        if(queryParam.getTableMappingId() != null){
            storeIdList = swStoreWorkDataTableDao.getStoreWorkStoreIdList(enterpriseId, queryParam);
        }else {
            storeIdList = swStoreWorkRecordDao.getStoreWorkStoreIdList(enterpriseId, queryParam);
        }
        if (CollectionUtils.isEmpty(storeIdList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        PageInfo pageInfo = new PageInfo<>(storeIdList);
        List<StoreWorkDataDetailVO> storeWorkRecordVOList = Lists.newArrayList();
        if(queryParam.getTableMappingId() != null){
            storeWorkRecordVOList = swStoreWorkDataTableDao.storeWorkStoreStatisticsList(enterpriseId, queryParam);
        }else {
            storeWorkRecordVOList = swStoreWorkRecordDao.storeWorkStoreStatisticsListNoPage(enterpriseId, queryParam);
        }
        //根据门店id分组
        Map<String, List<StoreWorkDataDetailVO>> dataGroup = ListUtils.emptyIfNull(storeWorkRecordVOList)
                .stream()
                .collect(Collectors.groupingBy(StoreWorkDataDetailVO::getStoreId));

        List<StoreWorkStoreSummaryVO> resultList = new ArrayList<>();
        // 查询门店
        List<StoreDO> storeDoList = storeDao.getExistStoreByStoreIdList(enterpriseId, storeIdList);
        List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(storeDoList)
                .stream()
                .map(data->{
                    StorePathDTO storePathDTO =new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionPath());
                    return storePathDTO;
                })
                .collect(Collectors.toList());
        Map<String, String> fullRegionNameMap = regionService.getFullRegionName(enterpriseId, storePathDTOList);

        List<RegionDO> storeRegionList = regionService.listRegionByStoreIds(enterpriseId, storeIdList);
        Map<String, RegionDO> storeRegionMap = ListUtils.emptyIfNull(storeRegionList).stream()
                .filter(a -> a.getStoreId() != null)
                .collect(Collectors.toMap(RegionDO::getStoreId, data -> data, (a, b) -> a));

        for (StoreDO storeDO : storeDoList) {
            List<StoreWorkDataDetailVO> dateListResult = dataGroup.getOrDefault(storeDO.getStoreId(), Lists.newArrayList());
            Map<String, StoreWorkDataDetailVO> storeWorkDataMap = dateListResult.stream().collect(Collectors.toMap(StoreWorkDataDetailVO::getStoreWorkDate, Function.identity()));
            dateListResult.clear();
            dateList.forEach(date -> {
                StoreWorkDataDetailVO storeWorkDataVO = storeWorkDataMap.get(date);
                if(storeWorkDataVO == null){
                    storeWorkDataVO = new StoreWorkDataDetailVO();
                    storeWorkDataVO.setFinishPercent(BigDecimal.ZERO);
                    storeWorkDataVO.setTotalColumnNum(0);
                    storeWorkDataVO.setUnFinishColumnNum(0);
                    storeWorkDataVO.setFinishColumnNum(0);
                    storeWorkDataVO.setAvgPassRate(BigDecimal.ZERO);
                    storeWorkDataVO.setAvgScore(BigDecimal.ZERO);
                    storeWorkDataVO.setAvgScoreRate(BigDecimal.ZERO);
                    storeWorkDataVO.setQuestionNum(0);
                    storeWorkDataVO.setFailColumnNum(0);
                    storeWorkDataVO.setPassColumnNum(0);
                    storeWorkDataVO.setStoreWorkDate(date);
                }
                dateListResult.add(storeWorkDataVO);
            });
            StoreWorkStoreSummaryVO storeWorkStoreSummaryVO = new StoreWorkStoreSummaryVO();
            storeWorkStoreSummaryVO.setStoreNum(storeDO.getStoreNum());
            storeWorkStoreSummaryVO.setFullRegionName(fullRegionNameMap.get(storeDO.getStoreId()));
            storeWorkStoreSummaryVO.setStoreId(storeDO.getStoreId());
            storeWorkStoreSummaryVO.setStoreName(storeDO.getStoreName());
            storeWorkStoreSummaryVO.setDetailList(dateListResult);
            RegionDO storeRegion = storeRegionMap.get(storeDO.getStoreId());
            if(storeRegion != null){
                storeWorkStoreSummaryVO.setStoreRegionId(storeRegion.getId());
            }
            resultList.add(storeWorkStoreSummaryVO);
        }
        pageInfo.setList(resultList);
        return pageInfo;
    }

    @Override
    public ImportTaskDO exportStoreExecutiveSummaryList(String enterpriseId, StoreWorkDataListRequest queryParam, CurrentUser currentUser)  {
        // 查询导出数量，限流
        Long count = Constants.MAX_EXPORT_SIZE;
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY);
        queryParam.setBeginStoreWorkDate(DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        queryParam.setEndStoreWorkDate(DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        queryParam.setPageSize(1);
        queryParam.setPageNumber(1);
        PageInfo<StoreWorkStoreSummaryVO> pageInfo = this.storeExecutiveSummaryList(enterpriseId, queryParam);
        if(CollectionUtils.isEmpty(pageInfo.getList())){
            throw new ServiceException(ErrorCodeEnum.ACH_NO_DATA_EXPORT);
        }
        // 通过枚举获取文件名称
        String fileName = ExportServiceEnum.STORE_EXECUTIVE_SUMMARY_LIST_REPORT.getFileName() + Constants.SPLIT_LINE + beginDate + Constants.SPLIT_LINE + endDate;
        queryParam.setExportServiceEnum(ExportServiceEnum.STORE_EXECUTIVE_SUMMARY_LIST_REPORT);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ExportServiceEnum.STORE_EXECUTIVE_SUMMARY_LIST_REPORT.getCode());
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(queryParam)));
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(currentUser.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public StoreWorkStoreDetailVO storeExecutiveDetail(String enterpriseId, StoreWorkDataListRequest queryParam, CurrentUser currentUser) {
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY);
        List<StoreWorkDataVO> pageHomeStoreWorkStatisticsDTOList = swStoreWorkRecordDao.countStoreWorkStatistics(enterpriseId, beginDate,
                endDate, null, queryParam.getWorkCycle(), queryParam.getStoreId());
        StoreWorkStoreDetailVO storeWorkDataVO= new StoreWorkStoreDetailVO();
        storeWorkDataVO.setFinishPercent(BigDecimal.ZERO);
        storeWorkDataVO.setStoreId(queryParam.getStoreId());
        StoreDTO store = storeDao.getStoreByStoreId(enterpriseId, queryParam.getStoreId());
        if(store != null){
            storeWorkDataVO.setStoreName(store.getStoreName());
            storeWorkDataVO.setStoreNum(store.getStoreNum());
            storeWorkDataVO.setFullRegionName(regionService.getAllRegionName(enterpriseId, store.getRegionId()).getAllRegionName());
        }
        if(CollectionUtils.isEmpty(pageHomeStoreWorkStatisticsDTOList)){
            return storeWorkDataVO;
        }
        StoreWorkDataVO pageHomeStoreWorkStatisticsDTO = pageHomeStoreWorkStatisticsDTOList.get(0);
        storeWorkDataVO.setFinishPercent(pageHomeStoreWorkStatisticsDTO.getCompleteRate());
        return storeWorkDataVO;
    }

    @Override
    public PageInfo<StoreWorkDataTableDetailListVO> storeExecutiveDetailList(String enterpriseId, StoreWorkDataListRequest queryParam, CurrentUser currentUser) {
        PageHelper.startPage(queryParam.getPageNumber(), queryParam.getPageSize());
        queryParam.setBeginStoreWorkDate(DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        queryParam.setEndStoreWorkDate(DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        //表数据
        List<SwStoreWorkDataTableDO> storeWorkDataTableList = swStoreWorkDataTableDao.selectStoreWorkDataTableList(null, enterpriseId, queryParam.getStoreWorkId(),
                queryParam.getTableMappingId(), queryParam.getBeginStoreWorkDate(), queryParam.getEndStoreWorkDate(), null,queryParam.getWorkCycle()
                , queryParam.getStoreId(), queryParam.getCompleteStatus(), queryParam.getCommentStatus());
        PageInfo pageInfo = new PageInfo<>(storeWorkDataTableList);
        if(CollectionUtils.isEmpty(storeWorkDataTableList)){
            return pageInfo;
        }
        List<StoreWorkDataTableDetailListVO> workDataTableDetailList= new ArrayList<>();
        List<Long> tableMappingIdList = storeWorkDataTableList.stream().map(SwStoreWorkDataTableDO::getTableMappingId).collect(Collectors.toList());
        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOList = swStoreWorkTableMappingDao.selectListByIds(enterpriseId, tableMappingIdList);
        Map<Long, String> personMap = ListUtils.emptyIfNull(swStoreWorkTableMappingDOList).stream().collect(Collectors.toMap(SwStoreWorkTableMappingDO::getId, SwStoreWorkTableMappingDO::getHandlePersonInfo));
        Map<Long, String> tableInfoMap = ListUtils.emptyIfNull(swStoreWorkTableMappingDOList).stream().collect(Collectors.toMap(SwStoreWorkTableMappingDO::getId, SwStoreWorkTableMappingDO::getTableInfo));

        storeWorkDataTableList.forEach(e -> {
            StoreWorkDataTableDetailListVO detailListVO = new StoreWorkDataTableDetailListVO();
            detailListVO.setStoreWorkId(e.getStoreWorkId());
            detailListVO.setWorkCycle(e.getWorkCycle());
            detailListVO.setStoreId(e.getStoreId());
            detailListVO.setStoreName(e.getStoreName());
            detailListVO.setTotalColumnNum(e.getTotalColumnNum());
            detailListVO.setUnFinishColumnNum(e.getTotalColumnNum() - e.getFinishColumnNum());
            detailListVO.setFinishColumnNum(e.getFinishColumnNum());
            detailListVO.setDataTableId(e.getId());
            detailListVO.setMetaTableId(e.getMetaTableId());
            detailListVO.setTableName(e.getTableName());
            detailListVO.setBeginTime(e.getBeginTime());
            detailListVO.setEndTime(e.getEndTime());
            detailListVO.setStoreWorkDate(e.getStoreWorkDate());
            detailListVO.setCheckTime(TableInfoLabelUtil.getLabel(tableInfoMap.get(e.getTableMappingId()), e.getWorkCycle()));
            if (e.getTotalColumnNum() != null && (e.getTotalColumnNum() != 0)) {
                detailListVO.setFinishPercent(NumberUtil.div(BigDecimal.valueOf(e.getFinishColumnNum()),
                        BigDecimal.valueOf(e.getTotalColumnNum()), Constants.INDEX_FOUR, RoundingMode.HALF_UP));
            }
            String handlePersonInfo = personMap.get(e.getTableMappingId());
            if(StringUtils.isNotBlank(handlePersonInfo)){
                List<StoreWorkCommonDTO> handlePersonList = JSONObject.parseArray(handlePersonInfo, StoreWorkCommonDTO.class);
                storeWorkService.fillPersonPositionName(enterpriseId, handlePersonList);
                detailListVO.setPersonList(handlePersonList);
            }
            workDataTableDetailList.add(detailListVO);
        });
        pageInfo.setList(workDataTableDetailList);
        return pageInfo;
    }

    @Override
    public List<StoreWorkDataTableDetailColumnListVO> storeExecutiveDetailColumnList(String enterpriseId, StoreWorkDataColumnListRequest queryParam) {
        List<SwStoreWorkDataTableColumnDO> dataTableColumnDOList = storeWorkDataTableColumnDao.selectColumnByDataTableId(enterpriseId, Arrays.asList(queryParam.getDataTableId()), null, queryParam.getSubmitStatus(), null);
        if(CollectionUtils.isEmpty(dataTableColumnDOList)){
            return new ArrayList<>();
        }
        SwStoreWorkDataTableDO dataTableDO = swStoreWorkDataTableDao.selectByPrimaryKey(queryParam.getDataTableId(), enterpriseId);
        if(dataTableDO == null){
            return new ArrayList<>();
        }
        SwStoreWorkTableMappingDO swStoreWorkTableMappingDO = swStoreWorkTableMappingDao.selectByPrimaryKey(dataTableDO.getTableMappingId(), enterpriseId);
        List<String> userIdList = dataTableColumnDOList.stream().map(SwStoreWorkDataTableColumnDO::getHandlerUserId).collect(Collectors.toList());
        Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIdList);
        List<StoreWorkDataTableDetailColumnListVO> resultList = new ArrayList<>();
        Map<String, String> finalUserNameMap = userNameMap;
        dataTableColumnDOList.forEach(tableColumnDO -> {
            StoreWorkDataTableDetailColumnListVO detailColumnListVO = new StoreWorkDataTableDetailColumnListVO();
            detailColumnListVO.setId(tableColumnDO.getId());
            detailColumnListVO.setStoreWorkId(tableColumnDO.getStoreWorkId());
            detailColumnListVO.setMetaColumnId(tableColumnDO.getMetaColumnId());
            detailColumnListVO.setMetaColumnName(tableColumnDO.getMetaColumnName());
            detailColumnListVO.setDataTableId(tableColumnDO.getDataTableId());
            detailColumnListVO.setMetaTableId(tableColumnDO.getMetaTableId());
            detailColumnListVO.setTableName(tableColumnDO.getTableName());
            detailColumnListVO.setHandlerUserId(tableColumnDO.getHandlerUserId());
            if(StringUtils.isNotBlank(tableColumnDO.getHandlerUserId())){
                detailColumnListVO.setHandlerUserName(finalUserNameMap.get(tableColumnDO.getHandlerUserId()));
            }
            detailColumnListVO.setStoreWorkDate(DateUtils.convertTimeToString(tableColumnDO.getStoreWorkDate().getTime(), DateUtils.DATE_FORMAT_DAY));
            detailColumnListVO.setSubmitStatus(tableColumnDO.getSubmitStatus());
            detailColumnListVO.setCheckTime(TableInfoLabelUtil.getLabel(swStoreWorkTableMappingDO.getTableInfo(), dataTableDO.getWorkCycle()));
            resultList.add(detailColumnListVO);
        });
        return resultList;
    }

    @Override
    public ImportTaskDO exportStoreExecutiveDetailColumnList(String enterpriseId, StoreWorkDataColumnListRequest queryParam, CurrentUser currentUser) {
        // 查询导出数量，限流
        Long count = Constants.MAX_EXPORT_SIZE;
        // 通过枚举获取文件名称
        String fileName = ExportServiceEnum.STORE_EXECUTIVE_DETAIL_LIST_REPORT.getFileName();
        queryParam.setExportServiceEnum(ExportServiceEnum.STORE_EXECUTIVE_DETAIL_LIST_REPORT);
        List<StoreWorkDataTableDetailColumnListVO> list = this.storeExecutiveDetailColumnList(enterpriseId, queryParam);
        if(CollectionUtils.isEmpty(list)){
            throw new ServiceException(ErrorCodeEnum.ACH_NO_DATA_EXPORT);
        }
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ExportServiceEnum.STORE_EXECUTIVE_DETAIL_LIST_REPORT.getCode());
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(queryParam)));
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(currentUser.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public PageInfo<StoreWorkDataTableColumnListVO> columnCompleteRateList(String enterpriseId, StoreWorkDataListRequest queryParam, CurrentUser user) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), queryParam.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new PageInfo<>(new ArrayList<>());
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        queryParam.setRegionPathList(regionPathList);
        PageHelper.startPage(queryParam.getPageNumber(), queryParam.getPageSize());
        queryParam.setBeginStoreWorkDate(DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        queryParam.setEndStoreWorkDate(DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        List<StoreWorkDataTableColumnListVO> list = storeWorkDataTableColumnDao.columnCompleteRateList(enterpriseId, queryParam);
        return new PageInfo<>(list);
    }

    @Override
    public ImportTaskDO exportColumnCompleteRateList(String enterpriseId, StoreWorkDataListRequest queryParam, CurrentUser currentUser) {
        queryParam.setPageNumber(1);
        queryParam.setPageSize(1);
        PageInfo<StoreWorkDataTableColumnListVO> pageInfo = this.columnCompleteRateList(enterpriseId, queryParam, currentUser);
        if(CollectionUtils.isEmpty(pageInfo.getList())){
            throw new ServiceException(ErrorCodeEnum.ACH_NO_DATA_EXPORT);
        }
        // 查询导出数量，限流
        Long count = Constants.MAX_EXPORT_SIZE;
        // 通过枚举获取文件名称
        String fileName = ExportServiceEnum.COLUMN_COMPLETE_RATE_LIST_REPORT.getFileName();
        queryParam.setExportServiceEnum(ExportServiceEnum.COLUMN_COMPLETE_RATE_LIST_REPORT);
        queryParam.setCurrentUser(currentUser);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ExportServiceEnum.COLUMN_COMPLETE_RATE_LIST_REPORT.getCode());
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(queryParam)));
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(currentUser.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public PageInfo<StoreWorkColumnStoreListVO> columnStoreCompleteList(String enterpriseId, StoreWorkColumnDetailListRequest queryParam, CurrentUser user) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), queryParam.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new PageInfo<>(new ArrayList<>());
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        queryParam.setRegionPathList(regionPathList);

        PageHelper.startPage(queryParam.getPageNumber(), queryParam.getPageSize());
        queryParam.setBeginStoreWorkDate(DateUtils.convertTimeToString(queryParam.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        queryParam.setEndStoreWorkDate(DateUtils.convertTimeToString(queryParam.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        List<StoreWorkColumnStoreListVO> list = storeWorkDataTableColumnDao.columnStoreCompleteList(enterpriseId, queryParam);
        if(CollectionUtils.isEmpty(list)){
            return new PageInfo<>(new ArrayList<>());
        }
        // 查询门店
        List<String> storeIdList = list.stream().map(StoreWorkColumnStoreListVO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeDoList = storeDao.getByStoreIdList(enterpriseId, storeIdList);
        List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(storeDoList)
                .stream()
                .map(data->{
                    StorePathDTO storePathDTO =new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionPath());
                    return storePathDTO;
                })
                .collect(Collectors.toList());
        Map<String, String> fullRegionNameMap = regionService.getFullRegionName(enterpriseId, storePathDTOList);
        for (StoreWorkColumnStoreListVO storeWorkColumnStoreListVO : list) {
            storeWorkColumnStoreListVO.setFullRegionName(fullRegionNameMap.get(storeWorkColumnStoreListVO.getStoreId()));
        }
        return new PageInfo<>(list);
    }

    @Override
    public ImportTaskDO exportColumnStoreCompleteList(String enterpriseId, StoreWorkColumnDetailListRequest queryParam, CurrentUser currentUser) {
        queryParam.setPageNumber(1);
        queryParam.setPageSize(1);
        PageInfo<StoreWorkColumnStoreListVO> pageInfo = this.columnStoreCompleteList(enterpriseId, queryParam, currentUser);
        if(CollectionUtils.isEmpty(pageInfo.getList())){
            throw new ServiceException(ErrorCodeEnum.ACH_NO_DATA_EXPORT);
        }
        // 查询导出数量，限流
        Long count = Constants.MAX_EXPORT_SIZE;
        // 通过枚举获取文件名称
        String fileName = ExportServiceEnum.COLUMN_COMPLETE_RATE_DETAIL_LIST_REPORT.getFileName();
        queryParam.setExportServiceEnum(ExportServiceEnum.COLUMN_COMPLETE_RATE_DETAIL_LIST_REPORT);
        queryParam.setCurrentUser(currentUser);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ExportServiceEnum.COLUMN_COMPLETE_RATE_DETAIL_LIST_REPORT.getCode());
        // 构造异步导出参数
        ExportMsgSendRequest msg = new ExportMsgSendRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(JSON.parseObject(JSONObject.toJSONString(queryParam)));
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(currentUser.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_FILE_COMMON.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public List<StoreWorkDataStoreDetailList> getStoreDetailWorkStatisticList(String enterpriseId, StoreWorkStoreDetailStatisticRequest queryParam) {
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        List<String> dateList =  DateUtils.getDayOfWeekWithinDateInterval(beginDate, endDate, queryParam.getWorkCycle());
        CollectionUtil.reverse(dateList);
        List<StoreWorkDataStoreDetailList> resultList = swStoreWorkRecordDao.getStoreDetailWorkStatisticList(enterpriseId, beginDate, endDate,
                 queryParam.getWorkCycle(),  queryParam.getStoreId());
        Map<String, StoreWorkDataStoreDetailList> storeWorkDataMap = ListUtils.emptyIfNull(resultList)
                .stream().filter(a -> a.getStoreWorkDate() != null).collect(Collectors.toMap(StoreWorkDataStoreDetailList::getStoreWorkDate, Function.identity()));
        resultList.clear();
        dateList.forEach(date -> {
            StoreWorkDataStoreDetailList storeWorkDataVO = storeWorkDataMap.get(date);
            if(storeWorkDataVO == null){
                storeWorkDataVO = new StoreWorkDataStoreDetailList();
                storeWorkDataVO.setTotalColumnNum(0L);
                storeWorkDataVO.setUnFinishColumnNum(0L);
                storeWorkDataVO.setFinishColumnNum(0L);
                storeWorkDataVO.setCompleteRate(BigDecimal.ZERO);
                storeWorkDataVO.setQuestionNum(0L);
                storeWorkDataVO.setPassRate(BigDecimal.ZERO);
                storeWorkDataVO.setStoreWorkDate(date);
            }
            StoreRankVO storeRankVO = swStoreWorkRecordDao.selectStoreRank(enterpriseId, queryParam.getWorkCycle(), queryParam.getStoreId(), date);
            if(storeRankVO != null){
                storeWorkDataVO.setRank(storeRankVO.getRank());
            }
            resultList.add(storeWorkDataVO);
        });
        return resultList;
    }

    @Override
    public PageInfo<StoreWorkFailColumnStoreListVO> failColumnStoreList(String enterpriseId, StoreDataListRequest queryParam, CurrentUser currentUser) {
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        queryParam.setBeginStoreWorkDate(beginDate);
        queryParam.setEndStoreWorkDate(endDate);
        PageHelper.startPage(queryParam.getPageNumber(), queryParam.getPageSize());
        List<StoreWorkFailColumnStoreListVO> resultList = storeWorkDataTableColumnDao.failColumnStoreList(enterpriseId, queryParam);
        PageInfo pageInfo = new PageInfo(resultList);
        if(CollectionUtils.isEmpty(resultList)){
            return pageInfo;
        }
        List<String> storeIdList = resultList.stream().map(StoreWorkFailColumnStoreListVO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeDOList = storeDao.getByStoreIdList(enterpriseId, storeIdList);
        Map<String, StoreDO> storeMap = storeDOList.stream()
                .filter(a -> a.getStoreId() != null)
                .collect(Collectors.toMap(StoreDO::getStoreId, Function.identity()));
        List<StorePathDTO> pathDTOList = ListUtils.emptyIfNull(storeDOList)
                .stream()
                .map(data->{
                    StorePathDTO storePathDTO=new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionPath());
                    return storePathDTO;
                })
                .collect(Collectors.toList());
        Map<String,String> fullRegionNamMap = regionService.getFullRegionName(enterpriseId, pathDTOList);
        resultList.forEach(failColumnStore -> {
            StoreDO storeDO = storeMap.get(failColumnStore.getFailColumnNum());
            if(storeDO != null){
                failColumnStore.setStoreNum(storeDO.getStoreNum());
            }
            failColumnStore.setFullRegionName(fullRegionNamMap.get(failColumnStore.getStoreId()));
        });
        return pageInfo;
    }

    @Override
    public StoreWorkDataStoreDetailVO getStoreDetailWorkStatistic(String enterpriseId, StoreWorkStoreDetailStatisticRequest queryParam) {
        String beginDate = DateUtils.convertTimeToString(queryParam.getBeginTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(queryParam.getEndTime(), DateUtils.DATE_FORMAT_DAY);
        List<StoreWorkStoreRankDataVO> resultList = swStoreWorkRecordDao.storeExecutiveRank(enterpriseId, beginDate, endDate,
                null, queryParam.getWorkCycle(), null, null, queryParam.getStoreId());
        StoreWorkDataStoreDetailVO storeDetailVO = new StoreWorkDataStoreDetailVO();
        storeDetailVO.setTotalNum(0L);
        storeDetailVO.setUnFinishNum(0L);
        storeDetailVO.setFinishNum(0L);
        storeDetailVO.setCompleteRate(BigDecimal.ZERO);
        storeDetailVO.setQuestionNum(0L);
        storeDetailVO.setAverageCommentRate(BigDecimal.ZERO);
        storeDetailVO.setAverageScore(BigDecimal.ZERO);
        storeDetailVO.setAveragePassRate(BigDecimal.ZERO);
        storeDetailVO.setAverageScoreRate(BigDecimal.ZERO);
        storeDetailVO.setUnApproveQuestionNum(0L);
        storeDetailVO.setFinishQuestionNum(0L);
        storeDetailVO.setUnHandleQuestionNum(0L);
        StoreRankVO storeRankVO = swStoreWorkRecordDao.selectStoreRank(enterpriseId, queryParam.getWorkCycle(), queryParam.getStoreId(), beginDate);
        if(storeRankVO != null){
            storeDetailVO.setRank(storeRankVO.getRank());
        }
        if(CollectionUtils.isEmpty(resultList)){
            return storeDetailVO;
        }
        StoreWorkStoreRankDataVO storeWorkStoreRankDataVO = resultList.get(0);
        BeanUtils.copyProperties(storeWorkStoreRankDataVO, storeDetailVO);
        storeDetailVO.setTotalNum(storeWorkStoreRankDataVO.getTotalColumnNum());
        storeDetailVO.setFinishNum(storeWorkStoreRankDataVO.getFinishColumnNum());
        storeDetailVO.setUnFinishNum(storeWorkStoreRankDataVO.getUnFinishColumnNum());
        return storeDetailVO;
    }

    private List<RegionPathDTO> getAuthRegionList(String enterpriseId, Boolean isAdmin, String userId, List<String> regionIdList){
        if (!isAdmin && CollectionUtils.isEmpty(regionIdList)) {
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingService.listUserAuthMappingByUserId(enterpriseId, userId);
            if (CollectionUtils.isNotEmpty(userAuthMappingList)) {
                regionIdList = userAuthMappingList.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
            }
        }
        List<RegionPathDTO> regionPathList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(regionIdList)){
            regionPathList = regionService.getRegionPathByList(enterpriseId, regionIdList);
        }
        return regionPathList;
    }

    private List<RegionPathDTO> getRegionPathList(String enterpriseId, boolean isAdmin, List<RegionPathDTO>  regionPathDTOList, List<String> regionIdList, boolean limit){
        boolean isNext = (isAdmin || (CollectionUtils.isNotEmpty(regionPathDTOList) && regionPathDTOList.size() == 1)) && CollectionUtils.isEmpty(regionIdList);
        //管理员默认下探
        if(isNext){
            List<Long> nextRegionIdList = new ArrayList<>();
            if(isAdmin){
                nextRegionIdList = regionMapper.getRegionIdListByParentId(enterpriseId, Constants.ROOT_DEPT_ID);
                if(CollectionUtils.isEmpty(nextRegionIdList)){
                    nextRegionIdList.add(Constants.ROOT_DEPT_ID);
                }
            }else {
                String parentId = regionPathDTOList.get(0).getRegionId();
                nextRegionIdList = regionMapper.getRegionIdListByParentId(enterpriseId, Long.valueOf(parentId));
                if(CollectionUtils.isEmpty(nextRegionIdList)){
                    nextRegionIdList.add(Long.valueOf(parentId));
                }
            }
            List<String> nextRegionIdStrList = nextRegionIdList.stream().map(String::valueOf).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(nextRegionIdStrList) && limit && nextRegionIdStrList.size() > Constants.INDEX_TWENTY){
                nextRegionIdStrList = nextRegionIdStrList.subList(0, Constants.INDEX_TWENTY);
            }
            regionPathDTOList = regionService.getRegionPathByList(enterpriseId, nextRegionIdStrList);
        }
        return regionPathDTOList;
    }

}
