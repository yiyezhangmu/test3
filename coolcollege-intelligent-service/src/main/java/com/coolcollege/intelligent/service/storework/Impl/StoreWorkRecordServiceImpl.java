package com.coolcollege.intelligent.service.storework.Impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cool.store.enums.SortTypeEnum;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.StoreWorkConstant;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.*;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.enums.storework.SortFieldEnum;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkCommentStatusEnum;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkFinishStatusEnum;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkNoticeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.NumberFormatUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionParentInfoDao;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.storework.dao.*;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dto.EnterpriseStoreWorkSettingsDTO;
import com.coolcollege.intelligent.model.ai.AIResolveDTO;
import com.coolcollege.intelligent.model.ai.AIConfigDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.dto.StaColumnDTO;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.TbQuestionRecordDO;
import com.coolcollege.intelligent.model.question.request.ExportStoreWorkDataRequest;
import com.coolcollege.intelligent.model.question.request.ExportStoreWorkRecordRequest;
import com.coolcollege.intelligent.model.question.vo.SubQuestionRecordListVO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.region.vo.RegionPathNameVO;
import com.coolcollege.intelligent.model.setting.vo.TableCheckSettingLevelVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.SingleStoreDTO;
import com.coolcollege.intelligent.model.store.dto.StorePathDTO;
import com.coolcollege.intelligent.model.storework.*;
import com.coolcollege.intelligent.model.storework.dto.*;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseSettingRpcService;
import com.coolcollege.intelligent.service.ai.AIService;
import com.coolcollege.intelligent.service.ai.AiModelLibraryService;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.metatable.calscore.CalColumnScoreDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableResultDTO;
import com.coolcollege.intelligent.service.metatable.calscore.CalTableScoreDTO;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.storework.StoreWorkDataTableService;
import com.coolcollege.intelligent.service.storework.StoreWorkRecordService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.CheckResultConstant.*;

/**
 * @Author wxp
 * @Date 2022/9/16 15:22
 * @Version 1.0
 */
@Service
@Slf4j
public class StoreWorkRecordServiceImpl implements StoreWorkRecordService {
    @Resource
    private QuestionRecordDao questionRecordDao;
    @Resource
    SwStoreWorkRecordDao swStoreWorkRecordDao;
    @Resource
    SwStoreWorkDataTableDao swStoreWorkDataTableDao;
    @Resource
    private StoreDao storeDao;
    @Resource
    SysRoleMapper sysRoleMapper;
    @Lazy
    @Resource
    private RegionService regionService;
    @Resource
    RegionMapper regionMapper;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor EXECUTOR_SERVICE;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    SwStoreWorkDataTableColumnDao swStoreWorkDataTableColumnDao;
    @Resource
    QuestionParentInfoDao questionParentInfoDao;
    @Resource
    QuestionRecordService questionRecordService;
    @Resource
    private SwStoreWorkDao swStoreWorkDao;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    SwStoreWorkTableMappingDao swStoreWorkTableMappingDao;
    @Resource
    TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private JmsTaskService jmsTaskService;
    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Resource
    private EnterpriseSettingRpcService enterpriseSettingRpcService;
    @Resource
    private UserAuthMappingService userAuthMappingService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Resource
    private AIService aiService;
    @Resource
    private StoreWorkDataTableService storeWorkDataTableService;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;
    @Resource
    private AiModelLibraryService aiModelLibraryService;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Override
    public StoreWorkStatisticsDTO countByStoreWorkId(String enterpriseId, Long storeWorkId) {
        return swStoreWorkRecordDao.countByStoreWorkId(enterpriseId, storeWorkId);
    }

    @Override
    public StoreWorkStatisticsOverviewVO storeWorkStoreStatisticsOverview(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        StoreWorkStatisticsOverviewVO storeWorkStatisticsOverviewVO = new StoreWorkStatisticsOverviewVO();
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), storeWorkDataListRequest.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return storeWorkStatisticsOverviewVO;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        storeWorkDataListRequest.setRegionPathList(regionPathList);
        if (storeWorkDataListRequest.getBeginStoreWorkTime() != null) {
            storeWorkDataListRequest.setBeginStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
        if (storeWorkDataListRequest.getEndStoreWorkTime() != null) {
            storeWorkDataListRequest.setEndStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }

        if (storeWorkDataListRequest.getTableMappingId() != null) {
            storeWorkStatisticsOverviewVO = swStoreWorkDataTableDao.storeWorkStoreStatisticsOverview(enterpriseId, storeWorkDataListRequest);
        } else {
            storeWorkStatisticsOverviewVO = swStoreWorkRecordDao.storeWorkStoreStatisticsOverview(enterpriseId, storeWorkDataListRequest);
        }
        return storeWorkStatisticsOverviewVO;
    }

    @Override
    public PageInfo<StoreWorkDataDetailVO> storeWorkStoreStatisticsList(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), storeWorkDataListRequest.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        storeWorkDataListRequest.setRegionPathList(regionPathList);
        if (storeWorkDataListRequest.getBeginStoreWorkTime() != null) {
            storeWorkDataListRequest.setBeginStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
        if (storeWorkDataListRequest.getEndStoreWorkTime() != null) {
            storeWorkDataListRequest.setEndStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
        PageHelper.clearPage();
        List<StoreWorkDataDetailVO> storeWorkRecordVOList = Lists.newArrayList();
        Integer storeWorkRecordVOListCountTotal = swStoreWorkDataTableDao.storeWorkStoreStatisticsListCount(enterpriseId, storeWorkDataListRequest);
        if (storeWorkDataListRequest.getTableMappingId() != null) {
            storeWorkRecordVOList = swStoreWorkDataTableDao.storeWorkStoreStatisticsList(enterpriseId, storeWorkDataListRequest);
        } else {
            storeWorkRecordVOListCountTotal = swStoreWorkRecordDao.storeWorkStoreStatisticsCount(enterpriseId, storeWorkDataListRequest);
            storeWorkRecordVOList = swStoreWorkRecordDao.storeWorkStoreStatisticsList(enterpriseId, storeWorkDataListRequest);
        }
        if (CollectionUtils.isEmpty(storeWorkRecordVOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        PageInfo pageInfo = new PageInfo<>(storeWorkRecordVOList);
        pageInfo.setTotal(storeWorkRecordVOListCountTotal);
        List<String> storeIdList = storeWorkRecordVOList.stream().map(StoreWorkDataDetailVO::getStoreId).collect(Collectors.toList());
        // 查询门店
        List<StoreDO> storeDoList = storeDao.getByStoreIdList(enterpriseId, storeIdList);
        Map<String, StoreDO> storeDOMap = storeDoList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity()));
        List<RegionDO> storeRegionList = regionService.listRegionByStoreIds(enterpriseId, storeIdList);
        Map<String, RegionDO> storeRegionMap = ListUtils.emptyIfNull(storeRegionList).stream()
                .filter(a -> a.getStoreId() != null)
                .collect(Collectors.toMap(data -> data.getStoreId(), data -> data, (a, b) -> a));

        List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(storeDoList)
                .stream()
                .map(data -> {
                    StorePathDTO storePathDTO = new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionPath());
                    return storePathDTO;
                })
                .collect(Collectors.toList());
        Map<String, String> fullRegionNameMap = regionService.getFullRegionName(enterpriseId, storePathDTOList);

        for (StoreWorkDataDetailVO storeWorkRecordVO : storeWorkRecordVOList) {
            StoreDO storeDO = storeDOMap.get(storeWorkRecordVO.getStoreId());
            RegionDO storeRegion = storeRegionMap.get(storeWorkRecordVO.getStoreId());
            if (storeDO != null) {
                storeWorkRecordVO.setStoreNum(storeDO.getStoreNum());
                storeWorkRecordVO.setStoreAddress(storeDO.getStoreAddress());
            }
            if (storeRegion != null) {
                storeWorkRecordVO.setStoreRegionId(storeRegion.getId());
            }
            storeWorkRecordVO.setFullRegionName(fullRegionNameMap.get(storeWorkRecordVO.getStoreId()));
            storeWorkRecordVO.setRegionNameList(Arrays.asList(fullRegionNameMap.get(storeWorkRecordVO.getStoreId()).split(Constants.SPLIT_LINE)));
        }
        return pageInfo;
    }

    @Override
    public PageInfo<StoreWorkDataDTO> StoreWorkDataList(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user) {

        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), storeWorkDataListRequest.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        storeWorkDataListRequest.setRegionPathList(regionPathList);
        //始终查询未点评的数据
        storeWorkDataListRequest.setCommentStatus(0);
        if (storeWorkDataListRequest.getBeginStoreWorkTime() != null) {
            storeWorkDataListRequest.setBeginStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
        if (storeWorkDataListRequest.getEndStoreWorkTime() != null) {
            storeWorkDataListRequest.setEndStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
//        PageHelper.startPage(storeWorkDataListRequest.getPageNumber(), storeWorkDataListRequest.getPageSize());
        List<StoreWorkDataDetailVO> storeWorkRecordVOList = Lists.newArrayList();
        Integer storeWorkRecordVOListCountTotal;

        if (storeWorkDataListRequest.getTableMappingId() != null) {
            storeWorkRecordVOListCountTotal=swStoreWorkDataTableDao.storeWorkStoreStatisticsListCount(enterpriseId, storeWorkDataListRequest);
            storeWorkRecordVOList = swStoreWorkDataTableDao.storeWorkStoreStatisticsList(enterpriseId, storeWorkDataListRequest);
        } else {
            storeWorkRecordVOListCountTotal=swStoreWorkRecordDao.storeWorkStoreStatisticsListCount(enterpriseId, storeWorkDataListRequest);
            storeWorkRecordVOList = swStoreWorkRecordDao.storeWorkStoreStatisticsList(enterpriseId, storeWorkDataListRequest);
        }
        if (CollectionUtils.isEmpty(storeWorkRecordVOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        PageInfo pageInfo = new PageInfo<>(storeWorkRecordVOList);
        pageInfo.setTotal(storeWorkRecordVOListCountTotal);
        List<String> storeIdList = storeWorkRecordVOList.stream().map(StoreWorkDataDetailVO::getStoreId).collect(Collectors.toList());

        List<String> businessIdList = storeWorkRecordVOList.stream().map(StoreWorkDataDetailVO::getTcBusinessId).collect(Collectors.toList());
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectSwStoreWorkDataTableNoCommentByBusinessIds(enterpriseId, businessIdList, storeWorkDataListRequest.getTableMappingId());
        //保留能点评的数据，保留本人的能点评的数据
        swStoreWorkDataTableDOS = swStoreWorkDataTableDOS.stream().filter(
                x -> (
                        (!MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                                && (x.getCompleteStatus() == 1 || (x.getCompleteStatus() == 0 && x.getEndTime().getTime() < System.currentTimeMillis()))
                                && (Optional.ofNullable(x.getCommentUserIds())
                                .map(commentUserIds -> commentUserIds.contains(user.getUserId()))
                                .orElse(false))
                )).collect(Collectors.toList());
        Map<String, List<SwStoreWorkDataTableDO>> map = swStoreWorkDataTableDOS.stream().collect(Collectors.groupingBy(SwStoreWorkDataTableDO::getStoreId));
        // 查询门店
        List<SingleStoreDTO> storeDoList = storeDao.getBasicStoreStoreIdList(enterpriseId, storeIdList);

        List<StoreWorkDataDTO> result = new ArrayList<>();
        storeDoList.forEach(x -> {
            StoreWorkDataDTO storeWorkDataDTO = new StoreWorkDataDTO();
            storeWorkDataDTO.setStoreId(x.getStoreId());
            storeWorkDataDTO.setStoreName(x.getStoreName());
            List<SwStoreWorkDataTableDO> sw = map.get(x.getStoreId());
            if (CollectionUtils.isNotEmpty(sw)) {
                List<Long> dataTableIds = sw.stream().map(SwStoreWorkDataTableDO::getId).collect(Collectors.toList());
                storeWorkDataDTO.setDataTableIds(dataTableIds);
                result.add(storeWorkDataDTO);
            }
        });
        pageInfo.setList(result);

        return pageInfo;
    }

    @Override
    public ImportTaskDO storeWorkStoreStatisticsListExport(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user) {
        if (storeWorkDataListRequest.getBeginStoreWorkTime() != null) {
            storeWorkDataListRequest.setBeginStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
        if (storeWorkDataListRequest.getEndStoreWorkTime() != null) {
            storeWorkDataListRequest.setEndStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
        // 查询导出数量，限流
        Long count = this.countStoreWorkStoreStatistics(enterpriseId, storeWorkDataListRequest, user);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称 ExportServiceEnum
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.STOREWORK_STORE_STATISTICS);
        if (StringUtils.isNotBlank(storeWorkDataListRequest.getStoreWorkDate())) {
            fileName = ExportServiceEnum.STORE_WORK_STORE_STATISTICS_LIST_EXPORT.getFileName() + Constants.SPLIT_LINE + storeWorkDataListRequest.getStoreWorkDate();
        }
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.STOREWORK_STORE_STATISTICS);
        // 构造异步导出参数
        ExportStoreWorkDataRequest msg = new ExportStoreWorkDataRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(storeWorkDataListRequest);
        msg.setUser(user);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.STOREWORK_STORE_STATISTICS.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    /**
     * 数据--区域统计
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    @Override
    public List<StoreWorkStatisticsOverviewVO> storeWorkRegionStatisticsList(String enterpriseId, StoreWorkDataListRequest request, CurrentUser user) {
        List<StoreWorkStatisticsOverviewVO> result = new ArrayList<>();

        if (request.getSortField() == null) {
            request.setSortField(SortFieldEnum.finishPercent);
        }
        if (request.getSortType() == null) {
            request.setSortType(com.coolcollege.intelligent.common.enums.storework.SortTypeEnum.DESC);
        }
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), request.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return result;
        }
        //是否下探
        //是否是查询子节点数据
        if (request.getChildRegion() && CollectionUtils.isNotEmpty(request.getRegionIdList())) {
            String parentId = request.getRegionIdList().get(0);
            List<Long> nextRegionIdList = regionMapper.getRegionIdListByParentId(enterpriseId, Long.valueOf(parentId));
            if (CollectionUtils.isEmpty(nextRegionIdList)) {
                regionPathDTOList = new ArrayList<>();
            } else {
                List<String> nextRegionIdStrList = nextRegionIdList.stream().map(String::valueOf).collect(Collectors.toList());
                regionPathDTOList = regionService.getRegionPathByList(enterpriseId, nextRegionIdStrList);
            }
        } else {
            regionPathDTOList = getRegionPathList(enterpriseId, isAdmin, regionPathDTOList, request.getRegionIdList());
        }
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return result;
        }
        //没有区域则直接返回空列表
        if (CollectionUtils.isEmpty(regionPathDTOList)) {
            return result;
        }
        regionPathDTOList = regionPathDTOList.stream().filter(e -> !RegionTypeEnum.STORE.getType().equals(e.getRegionPath())).collect(Collectors.toList());
        // 获取区域的路径
        Map<String, Future<StoreWorkStatisticsOverviewVO>> regionStoreWorkMap = new HashMap<>();
        for (RegionPathDTO region : regionPathDTOList) {
            String regionId = region.getRegionId();
            StoreWorkDataListRequest singleRegionRequest = new StoreWorkDataListRequest();
            BeanUtils.copyProperties(request, singleRegionRequest);
            singleRegionRequest.setRegionIdList(Collections.singletonList(regionId));
            regionStoreWorkMap.put(regionId,
                    EXECUTOR_SERVICE.submit(() -> storeWorkStoreStatisticsOverview(enterpriseId, singleRegionRequest, user)));
        }
        // 获取结果
        for (RegionPathDTO region : regionPathDTOList) {
            String regionId = region.getRegionId();
            Future<StoreWorkStatisticsOverviewVO> future = regionStoreWorkMap.get(regionId);
            try {
                // 获取统计信息
                StoreWorkStatisticsOverviewVO storeWorkStatisticsOverviewVO = future.get();
                storeWorkStatisticsOverviewVO.setRegionId(Long.valueOf(regionId));
                storeWorkStatisticsOverviewVO.setRegionName(region.getRegionName());
                storeWorkStatisticsOverviewVO.setFullRegionName(regionService.getAllRegionName(enterpriseId, Long.valueOf(regionId)).getAllRegionName());
                result.add(storeWorkStatisticsOverviewVO);
            } catch (Exception e) {
                log.error("店务区域统计：", e);
            }
        }
        result = CollectionUtil.sortByProperty(result, request.getSortField().name());
        if (SortTypeEnum.DESC.name().equals(request.getSortType().name())) {
            CollectionUtil.reverse(result);
        }
        return result;
    }

    @Override
    public ImportTaskDO storeWorkRegionStatisticsListExport(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user) {
        // 通过枚举获取文件名称
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.STOREWORK_REGION_STATISTICS) + Constants.SPLIT_LINE + storeWorkDataListRequest.getStoreWorkDate();
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.STOREWORK_REGION_STATISTICS);
        // 构造异步导出参数
        ExportStoreWorkDataRequest msg = new ExportStoreWorkDataRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(storeWorkDataListRequest);
        msg.setUser(user);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.STOREWORK_REGION_STATISTICS.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }


    @Override
    public PageInfo<StoreWorkDayStatisticsVO> storeWorkDayStatisticsList(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), storeWorkDataListRequest.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        storeWorkDataListRequest.setRegionPathList(regionPathList);
        if (storeWorkDataListRequest.getBeginStoreWorkTime() != null) {
            storeWorkDataListRequest.setBeginStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
        if (storeWorkDataListRequest.getEndStoreWorkTime() != null) {
            storeWorkDataListRequest.setEndStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
        List<StoreWorkDayStatisticsVO> storeWorkDayStatisticsVOList = Lists.newArrayList();
        PageHelper.startPage(storeWorkDataListRequest.getPageNumber(), storeWorkDataListRequest.getPageSize());
        if (storeWorkDataListRequest.getTableMappingId() != null) {
            storeWorkDayStatisticsVOList = swStoreWorkDataTableDao.storeWorkDayStatisticsList(enterpriseId, storeWorkDataListRequest);
        } else {
            storeWorkDayStatisticsVOList = swStoreWorkRecordDao.storeWorkDayStatisticsList(enterpriseId, storeWorkDataListRequest);
        }
        if (CollectionUtils.isEmpty(storeWorkDayStatisticsVOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        PageInfo pageInfo = new PageInfo<>(storeWorkDayStatisticsVOList);
        return pageInfo;
    }

    @Override
    public ImportTaskDO storeWorkDayStatisticsListExport(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user) {
        // 查询导出数量，限流
        Long count = this.countStoreWorkDayStatistics(enterpriseId, storeWorkDataListRequest, user);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        String beginDate = DateUtils.convertTimeToString(storeWorkDataListRequest.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY);
        String endDate = DateUtils.convertTimeToString(storeWorkDataListRequest.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY);
        // 通过枚举获取文件名称
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.STOREWORK_DAY_STATISTICS) + Constants.SPLIT_LINE + beginDate + Constants.SPLIT_LINE + endDate;

        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.STOREWORK_DAY_STATISTICS);
        // 构造异步导出参数
        ExportStoreWorkDataRequest msg = new ExportStoreWorkDataRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(storeWorkDataListRequest);
        msg.setUser(user);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.STOREWORK_DAY_STATISTICS.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    /**
     * 检查项提交时，异步计算表、店务记录上的状态和相关率数据
     *
     * @param enterpriseId
     * @param dataColumnId
     * @return
     */
    @Override
    public Boolean syncStatusWhenColumnSubmit(String enterpriseId, Long dataColumnId, EnterpriseConfigDO enterpriseConfigDO) {
        SwStoreWorkDataTableColumnDO storeWorkDataTableColumnDO = swStoreWorkDataTableColumnDao.selectByPrimaryKey(dataColumnId, enterpriseId);
        if (storeWorkDataTableColumnDO == null) {
            log.info("syncStatusWhenColumnSubmit数据检查项不存在dataColumnId:{},enterpriseId:{}", dataColumnId, enterpriseId);
            return false;
        }
        Long dataTableId = storeWorkDataTableColumnDO.getDataTableId();
        SwStoreWorkDataTableDO updateDataTable = swStoreWorkDataTableDao.selectByPrimaryKey(dataTableId, enterpriseId);
        // 检查表数据统计
        SwStoreWorkDataTableDO statisticsDataTable = swStoreWorkDataTableColumnDao.statisticsByDataTableId(enterpriseId, dataTableId);
        log.info("syncStatusWhenColumnSubmit检查表数据统计:{}", JSONObject.toJSONString(statisticsDataTable));
        updateDataTable.setBeginHandleTime(statisticsDataTable.getBeginHandleTime());
        updateDataTable.setFinishColumnNum(statisticsDataTable.getFinishColumnNum());
        // 判断该检查表是否完成
        boolean isFinish = statisticsDataTable.getFinishColumnNum().equals(statisticsDataTable.getTotalColumnNum());
        if (isFinish) {
            updateDataTable.setCompleteStatus(StoreWorkFinishStatusEnum.YES.getCode());
            updateDataTable.setEndHandleTime(new Date());
            updateDataTable.setActualHandleUserId(storeWorkDataTableColumnDO.getHandlerUserId());
        }
        // 更新表状态 未提交情况 也可能会有评分
        swStoreWorkDataTableDao.updateByPrimaryKeySelective(updateDataTable, enterpriseId);
        // 统计店的完成情况
        SwStoreWorkRecordStatisticsDTO storeWorkRecordStatisticsDTO = swStoreWorkDataTableDao.statisticsWhenSubmit(enterpriseId, storeWorkDataTableColumnDO.getTcBusinessId());
        if (storeWorkRecordStatisticsDTO.getFinishColumnNum().equals(storeWorkRecordStatisticsDTO.getTotalColumnNum())) {
            // 更新记录状态 根据businessid更新
            storeWorkRecordStatisticsDTO.setCompleteStatus(StoreWorkFinishStatusEnum.YES.getCode());
            storeWorkRecordStatisticsDTO.setEndHandleTime(new Date());
            storeWorkRecordStatisticsDTO.setSameHandleUser(Boolean.FALSE);
            List<String> actualHandleUserIdList = new ArrayList<>();
            if (StringUtils.isNotBlank(storeWorkRecordStatisticsDTO.getActualHandleUserId())) {
                actualHandleUserIdList = Arrays.stream(StringUtils.split(storeWorkRecordStatisticsDTO.getActualHandleUserId(), Constants.COMMA)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                if (Constants.INDEX_ONE.equals(actualHandleUserIdList.size())) {
                    storeWorkRecordStatisticsDTO.setSameHandleUser(Boolean.TRUE);
                }
            }
        }
        SwStoreWorkRecordDO storeWorkRecordDO = swStoreWorkRecordDao.getByTcBusinessId(enterpriseId, storeWorkDataTableColumnDO.getTcBusinessId());
        storeWorkRecordStatisticsDTO.setId(storeWorkRecordDO.getId());
        SwStoreWorkRecordDO updateRecord = new SwStoreWorkRecordDO();
        BeanUtils.copyProperties(storeWorkRecordStatisticsDTO, updateRecord);
        swStoreWorkRecordDao.updateByPrimaryKeySelective(updateRecord, enterpriseId);
        EnterpriseStoreWorkSettingsDTO storeWorkSetting = enterpriseSettingRpcService.getStoreWorkSetting(enterpriseId);
        // 表完成
        if (isFinish) {
            // 取消开始前提醒待办
            cancelUpcoming(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), dataTableId, StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate());
            // cancelUpcoming(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), dataTableId, StoreWorkNoticeEnum.TURN_NOTICE.getOperate());
            // 店务设置需要给点评人发消息
            if (storeWorkSetting.getAfterHandleRemindComment() != null && storeWorkSetting.getAfterHandleRemindComment()) {
                jmsTaskService.sendStoreWorkMessage(enterpriseId, updateDataTable.getId(), StoreWorkNoticeEnum.AFTER_HANDLE_REMIND_COMMENT.getOperate(), null);
            }
            // AI分析
            if (Constants.INDEX_ONE.equals(updateDataTable.getIsAiProcess())) {
                aiResolve(enterpriseId, updateDataTable, false);
            }
        }
        return true;
    }

    @Override
    public void aiRetry(String enterpriseId, Long dataTableId) {
        SwStoreWorkDataTableDO dataTableDO = swStoreWorkDataTableDao.selectByPrimaryKey(dataTableId, enterpriseId);
        // 已完成未提交可重试
        if (Constants.INDEX_ONE.equals(dataTableDO.getCompleteStatus()) && Constants.INDEX_ZERO.equals(dataTableDO.getCommentStatus())) {
            List<SwStoreWorkDataTableColumnDO> aiColumnList = swStoreWorkDataTableColumnDao.selectAiColumnByDataTableId(enterpriseId, dataTableDO.getId());
            // 更新项的AI状态为AI分析中
            List<SwStoreWorkDataTableColumnDO> updateColumns = aiColumnList.stream().filter(v -> Constants.STORE_WORK_AI.COLUMN_AI_STATUS_FAIL.equals(v.getAiStatus()))
                    .map(v -> SwStoreWorkDataTableColumnDO.builder().id(v.getId()).aiStatus(Constants.STORE_WORK_AI.COLUMN_AI_STATUS_PROCESSING).build())
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(updateColumns)) {
                swStoreWorkDataTableColumnDao.batchUpdate(enterpriseId, updateColumns);
                log.info("失败项进行AI分析重试");
                String dbName = DynamicDataSourceContextHolder.getDataSourceType();
                EXECUTOR_SERVICE.execute(() -> {
                    DataSourceHelper.changeToSpecificDataSource(dbName);
                    aiResolve(enterpriseId, dataTableDO, true);
                });
            }
        }
    }

    /**
     * AI分析处理
     * @param enterpriseId 企业id
     * @param dataTableDO 店务数据表
     * @param isRetry 是重试
     */
    private void aiResolve(String enterpriseId, SwStoreWorkDataTableDO dataTableDO, boolean isRetry) {
        String dbName = DynamicDataSourceContextHolder.getDataSourceType();
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(dbName);

        AIConfigDTO aiConfigDTO = JSONObject.parseObject(enterpriseSettingDO.getExtendField(), AIConfigDTO.class);
        if (Objects.isNull(aiConfigDTO) || !aiConfigDTO.aiEnable(AIBusinessModuleEnum.STORE_WORK)) {
            log.info("未开启店务AI");
            return;
        }
        String aiStyle = aiConfigDTO.aiStyle(AIBusinessModuleEnum.STORE_WORK);

        // 取出该表中所有的AI检查项
        List<SwStoreWorkDataTableColumnDO> allColumnList = swStoreWorkDataTableColumnDao.selectAiColumnByDataTableId(enterpriseId, dataTableDO.getId());
        // 过滤出未进行或失败的检查项
        List<SwStoreWorkDataTableColumnDO> processColumnList = allColumnList.stream().filter(v -> !Constants.STORE_WORK_AI.COLUMN_AI_STATUS_COMPLETE.equals(v.getAiStatus())).collect(Collectors.toList());;
        if (CollectionUtils.isEmpty(processColumnList)) {
            log.info("待处理AI检查项为空");
            return;
        }
        // 过滤出需要进行AI分析的检查项
        boolean processing = allColumnList.stream().anyMatch(v -> Constants.STORE_WORK_AI.COLUMN_AI_STATUS_PROCESSING.equals(v.getAiStatus()));
        // 重试进来的，已经将检查项的状态改为AI分析中，因此这里不需要处理
        if (processing && !isRetry) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_AI_PROCESSING);
        }
        if (!isRetry) {
            // 更新所有项的AI状态为AI进行中
            List<SwStoreWorkDataTableColumnDO> updateColumnStatusList = CollStreamUtil.toList(processColumnList, v -> SwStoreWorkDataTableColumnDO.builder().id(v.getId()).aiStatus(Constants.STORE_WORK_AI.COLUMN_AI_STATUS_PROCESSING).build());
            swStoreWorkDataTableColumnDao.batchUpdate(enterpriseId, updateColumnStatusList);
        }

        log.info("店务进行AI分析，dataTableId:{}", dataTableDO.getId());
        // ========== 数据准备 ==========
        List<Long> metaColumnIds = CollStreamUtil.toList(processColumnList, SwStoreWorkDataTableColumnDO::getMetaColumnId);
        List<TbMetaStaTableColumnDO> metaStaTableColumnDOs = tbMetaStaTableColumnMapper.getDetailByIdList(enterpriseId, metaColumnIds);
        List<Long> haveNoAiColumnTableIds = ListUtils.emptyIfNull(metaStaTableColumnDOs).stream().filter(o->YesOrNoEnum.NO.getCode().equals(o.getIsAiCheck())).map(TbMetaStaTableColumnDO::getMetaTableId).distinct().collect(Collectors.toList());
        List<TbMetaColumnResultDO> columnResultDOList = tbMetaColumnResultMapper.selectByColumnIds(enterpriseId, metaColumnIds);
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, dataTableDO.getMetaTableId());
        Map<Long, TbMetaStaTableColumnDO> metaStaColumnMap = CollStreamUtil.toMap(metaStaTableColumnDOs, TbMetaStaTableColumnDO::getId, v -> v);
        Map<Long, List<TbMetaColumnResultDO>> metaColumnResultMap = CollStreamUtil.groupByKey(columnResultDOList, TbMetaColumnResultDO::getMetaColumnId);
        Set<String> aiModelCodes = CollStreamUtil.toSet(metaStaTableColumnDOs, TbMetaStaTableColumnDO::getAiModel);
        Map<String, AiModelLibraryDO> aiModelMap = aiModelLibraryService.getModelMapByCodes(new ArrayList<>(aiModelCodes));
        // ========== 业务处理 ==========
        List<Future<StoreWorkAIResolveDTO>> futureList = new ArrayList<>();
        // 相当于AI做了一遍commentScore()
        for (SwStoreWorkDataTableColumnDO columnDO : processColumnList) {
            // AI分析，每个检查项进行一遍AI分析
            futureList.add(EXECUTOR_SERVICE.submit(() -> {
                Integer aiStatus = Constants.STORE_WORK_AI.COLUMN_AI_STATUS_COMPLETE;
                String aiFailReason = "";
                // 检查项
                TbMetaStaTableColumnDO metaColumn = metaStaColumnMap.get(columnDO.getMetaColumnId());
                if (Objects.isNull(metaColumn)) {
                    throw new ServiceException(ErrorCodeEnum.META_COLUMN_NOT_EXIST);
                }
                AIResolveDTO aiResolveDTO = null;
                try {
                    AiModelLibraryDO aiModel = aiModelMap.get(metaColumn.getAiModel());
                    // 结果项
                    List<TbMetaColumnResultDO> metaColumnResultList = metaColumnResultMap.get(columnDO.getMetaColumnId());
                    // 获取数据项的图片
                    JSONArray jsonArray = JSONObject.parseArray(columnDO.getCheckPics());
                    List<String> imageList = CollStreamUtil.toList(jsonArray, v -> ((JSONObject) v).getString("handle"));
                    // AI分析
                    aiResolveDTO = aiService.aiStoreWork(enterpriseId, aiModel, imageList, metaColumn, metaColumnResultList, aiStyle);
                } catch (Exception e) {
                    aiFailReason = e.getMessage();
                    aiStatus = Constants.STORE_WORK_AI.COLUMN_AI_STATUS_FAIL;
                }
                return new StoreWorkAIResolveDTO(columnDO.getId(), aiStatus, aiFailReason, aiResolveDTO);
            }));
        }
        List<StoreWorkAIResolveDTO> aiResultList = new ArrayList<>();
        futureList.forEach(v -> {
            try {
                aiResultList.add(v.get());
            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                log.error("AI分析异常", e.getCause());
                throw new ServiceException(ErrorCodeEnum.AI_API_ERROR);
            }
        });
        // 更新数据项
        SwStoreWorkDataTableDO currentDataTable = swStoreWorkDataTableDao.selectByPrimaryKey(dataTableDO.getId(), enterpriseId);
        // 因为AI的结果有延迟，如果这个情况下，完成了人工点评，则AI的结果不作为检查项结果。
        boolean aiResultAsCheckResult = Constants.INDEX_ONE.equals(tbMetaTableDO.getAiResultMethod()) && !Constants.INDEX_ONE.equals(currentDataTable.getCommentStatus());
        List<SwStoreWorkDataTableColumnDO> updateColumnList = new ArrayList<>();
        for (StoreWorkAIResolveDTO aiResult : aiResultList) {
            SwStoreWorkDataTableColumnDO updateColumn = SwStoreWorkDataTableColumnDO.builder()
                    .id(aiResult.getColumnId())
                    .aiStatus(aiResult.getAiStatus())
                    .aiFailReason(aiResult.getAiFailReason())
                    .build();
            if (Objects.nonNull(aiResult.getAiResolveDTO())) {
                setAiField(updateColumn, aiResult.getAiResolveDTO(), aiResultAsCheckResult);
            }
            updateColumnList.add(updateColumn);
        }
        if (CollectionUtils.isNotEmpty(updateColumnList)) {
            swStoreWorkDataTableColumnDao.batchUpdate(enterpriseId, updateColumnList);
        }
        // 所有检查项全部完成AI检查，更新检查表状态为AI已点评，否则改为AI分析失败
        Map<Long, SwStoreWorkDataTableColumnDO> updateColumnMap = CollStreamUtil.toMap(updateColumnList, SwStoreWorkDataTableColumnDO::getId, v -> v);
        boolean allColumnComplete = processColumnList.stream()
                .allMatch(v -> Objects.nonNull(updateColumnMap.get(v.getId())) && Constants.STORE_WORK_AI.COLUMN_AI_STATUS_COMPLETE.equals(updateColumnMap.get(v.getId()).getAiStatus()));
        int pendingAiStatus = allColumnComplete ? Constants.STORE_WORK_AI.AI_STATUS_PROCESSED : Constants.STORE_WORK_AI.AI_STATUS_FAIL;
        SwStoreWorkDataTableDO updateTable = SwStoreWorkDataTableDO.builder().id(dataTableDO.getId()).aiStatus(currentDataTable.getAiStatus() | pendingAiStatus).build();
        swStoreWorkDataTableDao.updateByPrimaryKeySelective(updateTable, enterpriseId);
        boolean isAllAiColumn = !haveNoAiColumnTableIds.contains(dataTableDO.getMetaTableId());
        if (allColumnComplete && isAllAiColumn && aiResultAsCheckResult) {
            // AI结果作为检查结果，更新数据检查表相关内容
            StoreWorkSubmitCommentMsgData storeWorkSubmitCommentMsgData = new StoreWorkSubmitCommentMsgData();
            storeWorkSubmitCommentMsgData.setEnterpriseId(enterpriseId);
            storeWorkSubmitCommentMsgData.setType(StoreWorkConstant.MsgType.COMMENT);
            storeWorkSubmitCommentMsgData.setDataTableId(dataTableDO.getId());
            storeWorkSubmitCommentMsgData.setActualCommentUserId(null);
            storeWorkSubmitCommentMsgData.setFromAi(true);
            simpleMessageService.send(JSONObject.toJSONString(storeWorkSubmitCommentMsgData), RocketMqTagEnum.STOREWORK_COMMENT_DATA_QUEUE);
            // 判断并自动发起工单
            aiAutoSendProblem(enterpriseId, dataTableDO, tbMetaTableDO, metaStaColumnMap);
        }
    }

    /**
     * AI结果自动发起工单
     * @param enterpriseId 企业id
     * @param dataTableDO 数据表
     * @param metaTable 检查表
     * @param metaColumnMap 检查项映射
     */
    public void aiAutoSendProblem(String enterpriseId,
                                  SwStoreWorkDataTableDO dataTableDO,
                                  TbMetaTableDO metaTable,
                                  Map<Long, TbMetaStaTableColumnDO> metaColumnMap) {
        EnterpriseStoreWorkSettingsDTO storeWorkSetting = enterpriseSettingRpcService.getStoreWorkSetting(enterpriseId);
        // 店务配置中是否自动发起工单
        log.info("店务是否自动发起工单：{}", storeWorkSetting.getAutoSendProblem());
        if (Boolean.TRUE.equals(storeWorkSetting.getAutoSendProblem())) {
            JSONObject extendInfo = JSONObject.parseObject(storeWorkSetting.getExtendInfo());
            if (Objects.nonNull(extendInfo)) {
                String autoSendProblem = extendInfo.getString(Constants.STORE_WORK_AI.AUTO_SEND_PROBLEM);
                log.info("店务AI配置是否自动发起工单：{}", autoSendProblem);
                // 店务AI配置是否自动发起工单
                if (Constants.STORE_WORK_AI.AUTO.equals(autoSendProblem)) {
                    boolean aiResultAsCheckResult = Constants.INDEX_ONE.equals(metaTable.getAiResultMethod());
                    log.info("dataTableId: {}, AI结果是否作为检查结果：{}", dataTableDO.getId(), aiResultAsCheckResult);
                    // AI结果作为检查结果的才发起工单
                    if (aiResultAsCheckResult) {
                        // 不合格检查项list
                        List<SwStoreWorkDataTableColumnDO> failColumns = swStoreWorkDataTableColumnDao.selectFailColumnByDataTableId(enterpriseId, dataTableDO.getId());
                        List<StaColumnDTO> failStaColumnList = failColumns.stream()
                                .filter(column -> CheckResultEnum.FAIL.getCode().equals(column.getCheckResult()))
                                .map(failColumn -> StaColumnDTO.builder().swStoreWorkDataTableColumnDODO(failColumn)
                                        .tbMetaStaTableColumnDO(metaColumnMap.get(failColumn.getMetaColumnId())).build())
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(failStaColumnList)) {
                            log.info("AI店务自动发起工单:{}", JSONUtil.toJsonStr(failStaColumnList));
                            storeWorkDataTableService.autoQuestionOrder(enterpriseId, failStaColumnList, dataTableDO.getId(), Constants.AI_USER_ID);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置AI字段
     */
    private void setAiField(SwStoreWorkDataTableColumnDO columnDO, AIResolveDTO aiResolveDTO, boolean aiResultAsCheckResult) {
        TbMetaColumnResultDO matchResult = aiResolveDTO.getColumnResult();
        if (aiResultAsCheckResult) {
            // 作为结果
            columnDO.setCheckResult(matchResult.getMappingResult());
            columnDO.setCheckResultId(matchResult.getId());
            columnDO.setCheckResultName(matchResult.getResultName());
            columnDO.setCommentContent(aiResolveDTO.getAiComment());
            columnDO.setCheckScore(aiResolveDTO.getAiScore());
        }
        columnDO.setAiCheckResult(matchResult.getMappingResult());
        columnDO.setAiCheckResultId(matchResult.getId());
        columnDO.setAiCheckResultName(matchResult.getResultName());
        columnDO.setAiCommentContent(aiResolveDTO.getAiComment());
        columnDO.setAiCheckScore(aiResolveDTO.getAiScore());
    }

    /**
     * 检查表点评时，异步店务记录上的状态和相关率数据
     *
     * @param enterpriseId
     * @param dataTableId
     * @return
     */
    @Override
    public Boolean syncStatusWhenTableComment(String enterpriseId, Long dataTableId, String actualCommentUserId, EnterpriseConfigDO enterpriseConfigDO) {
        SwStoreWorkDataTableDO storeWorkDataTableDO = swStoreWorkDataTableDao.selectByPrimaryKey(dataTableId, enterpriseId);
        if (storeWorkDataTableDO == null) {
            log.info("syncStatusWhenTableComment数据检查表不存在dataTableId:{},enterpriseId:{}", dataTableId, enterpriseId);
            return false;
        }
        // 更新表上的点评状态和点评人
        storeWorkDataTableDO.setActualCommentUserId(actualCommentUserId);
        storeWorkDataTableDO.setCommentStatus(StoreWorkCommentStatusEnum.YES.getCode());
        storeWorkDataTableDO.setCommentTime(new Date());
        swStoreWorkDataTableDao.updateByPrimaryKeySelective(storeWorkDataTableDO, enterpriseId);
        //计算得分
        countScore(enterpriseId, storeWorkDataTableDO);
        // 统计店的完成情况
        SwStoreWorkRecordStatisticsDTO storeWorkRecordStatisticsDTO = swStoreWorkDataTableDao.statisticsWhenComment(enterpriseId, storeWorkDataTableDO.getTcBusinessId());
        // 已经点评的表数量，== 需要 点评的表数量 说明 店的点评完成    除去自定义和 没有点评人的
        if (storeWorkRecordStatisticsDTO.getCommentTableNum().equals(storeWorkRecordStatisticsDTO.getNeedCommentTableNum())) {
            // 更新记录状态 根据businessid更新
            storeWorkRecordStatisticsDTO.setCommentStatus(StoreWorkCommentStatusEnum.YES.getCode());
        }
        SwStoreWorkRecordDO storeWorkRecordDO = swStoreWorkRecordDao.getByTcBusinessId(enterpriseId, storeWorkDataTableDO.getTcBusinessId());
        storeWorkRecordStatisticsDTO.setId(storeWorkRecordDO.getId());
        SwStoreWorkRecordDO updateRecord = new SwStoreWorkRecordDO();
        BeanUtils.copyProperties(storeWorkRecordStatisticsDTO, updateRecord);
        swStoreWorkRecordDao.updateByPrimaryKeySelective(updateRecord, enterpriseId);
        EnterpriseStoreWorkSettingsDTO storeWorkSetting = enterpriseSettingRpcService.getStoreWorkSetting(enterpriseId);
        // 表点评完成，根据店务设置发通知给执行人
        if (storeWorkSetting.getAfterCommentRemindHandler() != null && storeWorkSetting.getAfterCommentRemindHandler()) {
            jmsTaskService.sendStoreWorkMessage(enterpriseId, storeWorkDataTableDO.getId(), StoreWorkNoticeEnum.AFTER_COMMENT_REMIND_HANDLER.getOperate(), null);

        }
        // 取消待办
        // cancelUpcoming(enterpriseId, enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), dataTableId, StoreWorkNoticeEnum.AFTER_HANDLE_REMIND_COMMENT.getOperate());
        return true;
    }

    @Override
    public List<SubQuestionRecordListVO> getStoreWorkQuestionList(String enterpriseId, String businessId, CurrentUser user) {
        //查询工单ID
        List<SwStoreWorkDataTableColumnDO> tbDataStaTableColumnDOS = swStoreWorkDataTableColumnDao.selectByBusinessId(enterpriseId, businessId);
        if (CollectionUtils.isEmpty(tbDataStaTableColumnDOS)) {
            return Collections.emptyList();
        }
        //父任务IDs 去重
        List<Long> unifyParentIds = tbDataStaTableColumnDOS.stream().map(SwStoreWorkDataTableColumnDO::getTaskQuestionId).distinct().collect(Collectors.toList());
        //父任务定义表数据
        List<TbQuestionParentInfoDO> tbQuestionParentInfoDOS = questionParentInfoDao.selectByUnifyTaskIds(enterpriseId, unifyParentIds);
        List<Long> questionParentInfoIdList = tbQuestionParentInfoDOS.stream().map(TbQuestionParentInfoDO::getId).distinct().collect(Collectors.toList());
        return questionRecordService.questionDetailList(enterpriseId, questionParentInfoIdList, user.getUserId(), null, Boolean.FALSE, QuestionQueryTypeEnum.ALL.getCode());
    }

    @Override
    public PageInfo<StoreWorkTableVO> storeWorkTableList(String enterpriseId, StoreWorkRecordListRequest request, CurrentUser user) {
        //参数校验
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), request.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        request.setRegionPathList(regionPathList);
        PageHelper.startPage(request.getPageNumber(), request.getPageSize());
        if (request.getBeginStoreWorkTime() != null) {
            request.setBeginStoreWorkDate(DateUtils.convertTimeToString(request.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        if (request.getEndStoreWorkTime() != null) {
            request.setEndStoreWorkDate(DateUtils.convertTimeToString(request.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        //表数据
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectStoreWorkDataTableList(request.getRegionPathList(), enterpriseId, request.getStoreWorkId(),
                request.getTableMappingId(), request.getBeginStoreWorkDate(), request.getEndStoreWorkDate(), request.getBusinessId(), request.getWorkCycle(), null, request.getCompleteStatus(), request.getCommentStatus());
        PageInfo<SwStoreWorkDataTableDO> swStoreWorkDataTableDOPageInfo = new PageInfo<>(swStoreWorkDataTableDOS);

        List<String> storeIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getStoreId).collect(Collectors.toList());
        List<StoreDO> byStoreIdList = storeDao.getByStoreIdList(enterpriseId, storeIds);
        Map<String, String> storeNumMap = byStoreIdList.stream().filter(x -> StringUtils.isNotEmpty(x.getStoreNum())).collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreNum));

        List<Long> storeWorkIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getStoreWorkId).collect(Collectors.toList());
        List<SwStoreWorkDO> swStoreWorkDOS = swStoreWorkDao.listBystoreWorkIds(enterpriseId, storeWorkIds);
        Map<Long, String> workNameMap = swStoreWorkDOS.stream().filter(x -> StringUtils.isNotEmpty(x.getWorkName())).collect(Collectors.toMap(SwStoreWorkDO::getId, SwStoreWorkDO::getWorkName));

        List<String> userIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getActualHandleUserId).collect(Collectors.toList());

        Map<String, String> userMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);

        List<Long> tableMappingIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getTableMappingId).collect(Collectors.toList());
        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS = swStoreWorkTableMappingDao.selectListByIds(enterpriseId, tableMappingIds);
        Map<Long, String> tableInfoMap = swStoreWorkTableMappingDOS.stream().filter(x -> StringUtils.isNotEmpty(x.getTableInfo())).collect(Collectors.toMap(SwStoreWorkTableMappingDO::getId, SwStoreWorkTableMappingDO::getTableInfo));

        List<StoreWorkTableVO> storeWorkTableVOS = new ArrayList<>();
        swStoreWorkDataTableDOS.forEach(x -> {
            StoreWorkTableVO storeWorkTableVO = new StoreWorkTableVO();
            String beginTime = DateUtil.format(x.getBeginTime(), DateUtils.DATE_FORMAT_SEC);
            String endTime = DateUtil.format(x.getEndTime(), DateUtils.DATE_FORMAT_SEC);
            storeWorkTableVO.setBeginEndTime(String.format("%s~%s", beginTime, endTime));
            storeWorkTableVO.setBeginTime(x.getBeginTime());
            storeWorkTableVO.setEndTime(x.getEndTime());
            storeWorkTableVO.setMetaTableId(x.getMetaTableId());
            storeWorkTableVO.setMetaTableName(x.getTableName());
            storeWorkTableVO.setId(x.getId());
            storeWorkTableVO.setBusinessId(x.getTcBusinessId());
            storeWorkTableVO.setStoreId(x.getStoreId());
            storeWorkTableVO.setStoreName(x.getStoreName());
            storeWorkTableVO.setStoreNum(storeNumMap.getOrDefault(x.getStoreId(), ""));
            storeWorkTableVO.setStoreWorkName(workNameMap.getOrDefault(x.getStoreWorkId(), ""));
            storeWorkTableVO.setActualHandleUserId(x.getActualHandleUserId());
            storeWorkTableVO.setActualHandleUserName(userMap.getOrDefault(x.getActualHandleUserId(), ""));
            String allRegionName = "";
            RegionPathNameVO allRegion = regionService.getAllRegionName(enterpriseId, x.getRegionId());
            if (allRegion != null) {
                allRegionName = allRegion.getAllRegionName().replace("-", "/");
            }
            storeWorkTableVO.setAllRegionName(allRegionName);
            storeWorkTableVO.setStoreWorkDate(x.getStoreWorkDate());
            storeWorkTableVO.setCommentStatus(x.getCommentStatus());
            storeWorkTableVO.setCompleteStatus(x.getCompleteStatus());
            storeWorkTableVO.setTableInfo(tableInfoMap.getOrDefault(x.getTableMappingId(), ""));
            storeWorkTableVO.setBeginHandleTime(x.getBeginHandleTime());
            storeWorkTableVO.setEndHandleTime(x.getEndHandleTime());
            storeWorkTableVO.setScore(x.getScore());
            storeWorkTableVO.setPassColumnNum(x.getPassColumnNum());
            storeWorkTableVO.setFailColumnNum(x.getFailColumnNum());
            String percentString = NumberFormatUtils.getPercent(x.getPassColumnNum(), x.getTotalCalColumnNum());
            storeWorkTableVO.setPassRate(percentString);
            BigDecimal divide = new BigDecimal(Constants.INDEX_ZERO);
            if (x.getTotalScore().compareTo(new BigDecimal(Constants.INDEX_ZERO)) != 0) {
                divide = x.getScore().multiply(new BigDecimal(100)).divide(x.getTotalScore(), 2, BigDecimal.ROUND_HALF_UP);
            }
            storeWorkTableVO.setScoreRate(String.format("%s%%", divide));
            storeWorkTableVOS.add(storeWorkTableVO);
        });
        PageInfo<StoreWorkTableVO> result = new PageInfo<>();
        result.setList(storeWorkTableVOS);
        result.setTotal(swStoreWorkDataTableDOPageInfo.getTotal());
        return result;
    }

    @Override
    public ImportTaskDO storeWorkTableListExport(String enterpriseId, StoreWorkRecordListRequest request, CurrentUser user) {
        // 查询导出数量，限流
        Long count = this.countWorkTableList(enterpriseId, request, user);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.STORE_WORK_TABLE_LIST);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName.replace("exportTime", DateUtil.format(new Date(), DateUtils.DATE_FORMAT_MINUTE)), ImportTaskConstant.STORE_WORK_TABLE_LIST);
        // 构造异步导出参数
        ExportStoreWorkRecordRequest msg = new ExportStoreWorkRecordRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(request);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setUser(user);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.STOREWORK_TABLE_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    /**
     * 统计店务检查表数据
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    public Long countWorkTableList(String enterpriseId, StoreWorkRecordListRequest request, CurrentUser user) {
        Long count = 0L;
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), request.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return count;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        request.setRegionPathList(regionPathList);
        if (request.getBeginStoreWorkTime() != null) {
            request.setBeginStoreWorkDate(DateUtils.convertTimeToString(request.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        if (request.getEndStoreWorkTime() != null) {
            request.setEndStoreWorkDate(DateUtils.convertTimeToString(request.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        return swStoreWorkDataTableDao.countStoreWorkDataTableList(request.getRegionPathList(), enterpriseId, request.getStoreWorkId(),
                request.getTableMappingId(), request.getBeginStoreWorkDate(), request.getEndStoreWorkDate(), request.getBusinessId(), request.getWorkCycle(), null, request.getCompleteStatus(), request.getCommentStatus());
    }

    @Override
    public PageInfo<StoreWorkColumnVO> storeWorkColumnList(String enterpriseId, StoreWorkRecordListRequest request, CurrentUser user) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), request.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        request.setRegionPathList(regionPathList);
        PageHelper.startPage(request.getPageNumber(), request.getPageSize());
        if (request.getBeginStoreWorkTime() != null) {
            request.setBeginStoreWorkDate(DateUtils.convertTimeToString(request.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        if (request.getEndStoreWorkTime() != null) {
            request.setEndStoreWorkDate(DateUtils.convertTimeToString(request.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        //表数据
        List<SwStoreWorkDataTableColumnDO> tbDataStaTableColumnDOS = swStoreWorkDataTableColumnDao.selectStoreWorkDataColumnList(request.getRegionPathList(), enterpriseId, request.getStoreWorkId(), request.getTableMappingId(),
                request.getBeginStoreWorkDate(), request.getEndStoreWorkDate(), request.getWorkCycle(), request.getDataTableId());
        PageInfo<SwStoreWorkDataTableColumnDO> swStoreWorkDataTableDOPageInfo = new PageInfo<>(tbDataStaTableColumnDOS);

        List<String> storeIds = tbDataStaTableColumnDOS.stream().map(SwStoreWorkDataTableColumnDO::getStoreId).collect(Collectors.toList());
        List<StoreDO> byStoreIdList = storeDao.getByStoreIdList(enterpriseId, storeIds);
        Map<String, String> storeNumMap = byStoreIdList.stream().filter(x -> StringUtils.isNotEmpty(x.getStoreNum())).collect(Collectors.toMap(StoreDO::getStoreId, StoreDO::getStoreNum));

        List<Long> storeWorkIds = tbDataStaTableColumnDOS.stream().map(SwStoreWorkDataTableColumnDO::getStoreWorkId).collect(Collectors.toList());
        List<SwStoreWorkDO> swStoreWorkDOS = swStoreWorkDao.listBystoreWorkIds(enterpriseId, storeWorkIds);
        Map<Long, String> workNameMap = swStoreWorkDOS.stream().filter(x -> StringUtils.isNotEmpty(x.getWorkName())).collect(Collectors.toMap(SwStoreWorkDO::getId, SwStoreWorkDO::getWorkName));

        List<String> userIds = tbDataStaTableColumnDOS.stream().map(SwStoreWorkDataTableColumnDO::getHandlerUserId).collect(Collectors.toList());
        List<String> actualCommentUserIds = tbDataStaTableColumnDOS.stream().filter(x -> StringUtils.isNotEmpty(x.getActualCommentUserId())).map(SwStoreWorkDataTableColumnDO::getActualCommentUserId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(actualCommentUserIds)) {
            userIds.addAll(actualCommentUserIds);
        }
        Map<String, String> userMap = enterpriseUserDao.getUserNameMap(enterpriseId, userIds);

        List<Long> dataTableIds = tbDataStaTableColumnDOS.stream().map(SwStoreWorkDataTableColumnDO::getDataTableId).collect(Collectors.toList());
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectByIds(dataTableIds, enterpriseId);
        Map<Long, Date> commentTimeMap = swStoreWorkDataTableDOS.stream().filter(x -> x.getCommentTime() != null).collect(Collectors.toMap(SwStoreWorkDataTableDO::getId, SwStoreWorkDataTableDO::getCommentTime));

        List<Long> allMetaTableIds = tbDataStaTableColumnDOS.stream().map(SwStoreWorkDataTableColumnDO::getMetaTableId).collect(Collectors.toList());
        List<Long> defMetaTableIds = tbDataStaTableColumnDOS.stream().filter(x -> x.getColumnType().equals(Constants.SEVEN)).map(SwStoreWorkDataTableColumnDO::getMetaTableId).collect(Collectors.toList());
        List<Long> metaTableIds = tbDataStaTableColumnDOS.stream().filter(x -> !x.getColumnType().equals(Constants.SEVEN)).map(SwStoreWorkDataTableColumnDO::getMetaTableId).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = Lists.newArrayList();
        Map<Long, TbMetaTableDO> tableMap = Maps.newHashMap();
        Map<Long, TbMetaDefTableColumnDO> idMetaDefTableColumnMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(metaTableIds)) {
            tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, metaTableIds, Boolean.TRUE);
        }
        if (CollectionUtils.isNotEmpty(allMetaTableIds)) {
            List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectByIds(enterpriseId, allMetaTableIds);
            tableMap = tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, x -> x));
        }
        if (CollectionUtils.isNotEmpty(defMetaTableIds)) {
            List<TbMetaDefTableColumnDO> list = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, defMetaTableIds);
            idMetaDefTableColumnMap = list.stream()
                    .collect(Collectors.toMap(TbMetaDefTableColumnDO::getId, Function.identity(), (a, b) -> a));
        }
        Map<Long, String> staMap = tbMetaStaTableColumnDOS.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, TbMetaStaTableColumnDO::getStandardPic));

        List<StoreWorkColumnVO> storeWorkTableColumnVOS = new ArrayList<>();
        Map<Long, TbMetaTableDO> finalTableMap = tableMap;
        Map<Long, TbMetaDefTableColumnDO> finalIdMetaDefTableColumnMap = idMetaDefTableColumnMap;
        tbDataStaTableColumnDOS.forEach(x -> {
            StoreWorkColumnVO storeWorkColumnVO = new StoreWorkColumnVO();
            storeWorkColumnVO.setId(x.getId());
            storeWorkColumnVO.setMetaColumnName(x.getMetaColumnName());
            storeWorkColumnVO.setMetaColumnCategoryName(x.getCategoryName());
            storeWorkColumnVO.setStoreId(x.getStoreId());
            storeWorkColumnVO.setStoreName(x.getStoreName());
            storeWorkColumnVO.setStoreNum(storeNumMap.getOrDefault(x.getStoreId(), ""));
            storeWorkColumnVO.setCheckScore(x.getCheckScore());
            storeWorkColumnVO.setCheckText(x.getCheckText());
            storeWorkColumnVO.setCheckPics(x.getCheckPics());
            storeWorkColumnVO.setCheckVideo(x.getCheckVideo());
            storeWorkColumnVO.setHandlerUserId(x.getHandlerUserId());
            storeWorkColumnVO.setHandlerUserName(userMap.getOrDefault(x.getHandlerUserId(), ""));
            storeWorkColumnVO.setSubmitTime(x.getSubmitTime());
            storeWorkColumnVO.setActualCommentUserId(x.getActualCommentUserId());
            storeWorkColumnVO.setActualCommentUserName(userMap.getOrDefault(x.getActualCommentUserId(), ""));
            storeWorkColumnVO.setCommentContent(x.getCommentContent());
            storeWorkColumnVO.setCheckResult(x.getCheckResult());
            storeWorkColumnVO.setCommentTime(commentTimeMap.get(x.getDataTableId()));
            storeWorkColumnVO.setTableName(x.getTableName());
            storeWorkColumnVO.setStoreWorkName(workNameMap.getOrDefault(x.getStoreWorkId(), ""));
            storeWorkColumnVO.setWorkCycle(x.getWorkCycle());
            storeWorkColumnVO.setScore(x.getColumnMaxScore());
            storeWorkColumnVO.setStaPic(staMap.getOrDefault(x.getMetaColumnId(), ""));
            storeWorkColumnVO.setValue1(x.getValue1());
            storeWorkColumnVO.setValue2(x.getValue2());
            TbMetaTableDO tb = finalTableMap.getOrDefault(x.getMetaTableId(), new TbMetaTableDO());
            storeWorkColumnVO.setTableProperty(tb.getTableProperty());
            TbMetaDefTableColumnDO metaDefTableColumnDO = finalIdMetaDefTableColumnMap.get(x.getMetaColumnId());
            if (metaDefTableColumnDO != null) {
                storeWorkColumnVO.setFormat(metaDefTableColumnDO.getFormat());
            }
            String allRegionName = "";
            RegionPathNameVO allRegion = regionService.getAllRegionName(enterpriseId, x.getRegionId());
            if (allRegion != null) {
                allRegionName = allRegion.getAllRegionName().replace("-", "/");
            }
            storeWorkColumnVO.setAllRegionName(allRegionName);
            storeWorkColumnVO.setAiCheckResult(x.getAiCheckResult());
            storeWorkColumnVO.setAiCheckResultId(x.getAiCheckResultId());
            storeWorkColumnVO.setAiCheckResultName(x.getAiCheckResultName());
            storeWorkColumnVO.setAiCommentContent(x.getAiCommentContent());
            storeWorkColumnVO.setAiCheckScore(x.getAiCheckScore());
            storeWorkTableColumnVOS.add(storeWorkColumnVO);
        });
        PageInfo<StoreWorkColumnVO> result = new PageInfo<>();
        result.setList(storeWorkTableColumnVOS);
        result.setTotal(swStoreWorkDataTableDOPageInfo.getTotal());
        return result;
    }

    /**
     * 统计店务检查项数据
     *
     * @param enterpriseId
     * @param request
     * @return
     */
    public Long countStoreWorkColumn(String enterpriseId, StoreWorkRecordListRequest request, CurrentUser user) {
        Long count = 0L;
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), request.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return count;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        request.setRegionPathList(regionPathList);
        if (request.getBeginStoreWorkTime() != null) {
            request.setBeginStoreWorkDate(DateUtils.convertTimeToString(request.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        if (request.getEndStoreWorkTime() != null) {
            request.setEndStoreWorkDate(DateUtils.convertTimeToString(request.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        return swStoreWorkDataTableColumnDao.countStoreWorkDataColumnList(request.getRegionPathList(), enterpriseId, request.getStoreWorkId(), request.getTableMappingId(),
                request.getBeginStoreWorkDate(), request.getEndStoreWorkDate(), request.getWorkCycle(), request.getDataTableId());
    }

    @Override
    public ImportTaskDO storeWorkColumnListExport(String enterpriseId, StoreWorkRecordListRequest request, CurrentUser user) {
        // 查询导出数量，限流
        Long count = this.countStoreWorkColumn(enterpriseId, request, user);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.STORE_WORK_COLUMN_LIST);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName.replace("exportTime", DateUtil.format(new Date(), DateUtils.DATE_FORMAT_MINUTE)), ImportTaskConstant.STORE_WORK_COLUMN_LIST);
        // 构造异步导出参数
        ExportStoreWorkRecordRequest msg = new ExportStoreWorkRecordRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(request);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setUser(user);
        msg.setDbName(user.getDbName());
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.STOREWORK_COLUMN_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public StoreWorkBaseDetailVO getStoreWorkBaseDetail(String enterpriseId, String businessId) {
        SwStoreWorkRecordDO swStoreWorkRecordDO = swStoreWorkRecordDao.getByTcBusinessId(enterpriseId, businessId);
        if (swStoreWorkRecordDO == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_RECORD_IS_NOT_EXIST);
        }
        StoreWorkBaseDetailVO storeWorkBaseDetailVO = new StoreWorkBaseDetailVO();
        storeWorkBaseDetailVO.setStoreWorkId(swStoreWorkRecordDO.getStoreWorkId());
        storeWorkBaseDetailVO.setStoreWorkDate(swStoreWorkRecordDO.getStoreWorkDate());
        storeWorkBaseDetailVO.setStoreId(swStoreWorkRecordDO.getStoreId());
        storeWorkBaseDetailVO.setStoreName(swStoreWorkRecordDO.getStoreName());
        storeWorkBaseDetailVO.setWorkCycle(swStoreWorkRecordDO.getWorkCycle());
        storeWorkBaseDetailVO.setTcBusinessId(swStoreWorkRecordDO.getTcBusinessId());
        return storeWorkBaseDetailVO;
    }

    @Override
    public SameExecutorInfoVO theSameExecutor(String enterpriseId, String businessId) {

        SameExecutorInfoVO sameExecutorInfoVO = new SameExecutorInfoVO();
        if (StringUtils.isEmpty(businessId)) {
            return sameExecutorInfoVO;
        }
        SwStoreWorkRecordDO swStoreWorkRecordDO = swStoreWorkRecordDao.getByTcBusinessId(enterpriseId, businessId);
        if (swStoreWorkRecordDO == null) {
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_RECORD_IS_NOT_EXIST);
        }
        if (!swStoreWorkRecordDO.getSameHandleUser()) {
            return sameExecutorInfoVO;
        }
        String actualHandleUserId = swStoreWorkRecordDO.getActualHandleUserId();
        EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(enterpriseId, actualHandleUserId);
        HandlerUserVO handlerUserVO = new HandlerUserVO();
        if (Objects.nonNull(enterpriseUserDO)) {
            handlerUserVO.setUserId(enterpriseUserDO.getUserId());
            handlerUserVO.setAvatar(enterpriseUserDO.getAvatar());
            handlerUserVO.setUserName(enterpriseUserDO.getName());
            handlerUserVO.setUserMobile(enterpriseUserDO.getMobile());
            List<SysRoleDO> sysRoleList = sysRoleMapper.getSysRoleByUserId(enterpriseId, enterpriseUserDO.getUserId());
            if (CollectionUtils.isNotEmpty(sysRoleList)) {
                handlerUserVO.setUserRoles(sysRoleList);
            }
        }
        sameExecutorInfoVO.setHandlerUserVO(handlerUserVO);
        sameExecutorInfoVO.setBeginHandleTime(swStoreWorkRecordDO.getBeginHandleTime());
        sameExecutorInfoVO.setEndHanleTime(swStoreWorkRecordDO.getEndHandleTime());
        return sameExecutorInfoVO;
    }

    @Override
    public Boolean storeWorkRecordExpired(String enterpriseId, String businessId, String key) {
        try {
            String string = redisUtilPool.getString(key);
            if (StringUtils.isEmpty(string)) {
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            log.info("redis执行出错");
            return false;
        }
        return true;
    }

    @Override
    public SubQuestionRecordListVO getStoreWorkRecordByDataColumnId(String enterpriseId, Long dataColumnId) {
        SubQuestionRecordListVO recordListVO = new SubQuestionRecordListVO();
        TbQuestionRecordDO recordDO = questionRecordDao.getByDataColumnId(enterpriseId, dataColumnId, Boolean.TRUE);
        SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDO = swStoreWorkDataTableColumnDao.selectByPrimaryKey(dataColumnId, enterpriseId);
        recordListVO.setUnifyTaskId(swStoreWorkDataTableColumnDO.getTaskQuestionId());
        recordListVO.setStoreId(swStoreWorkDataTableColumnDO.getStoreId());
        recordListVO.setLoopCount(Constants.LONG_ONE);
        if (recordDO != null) {
            recordListVO.setId(recordDO.getId());
            recordListVO.setParentQuestionId(recordDO.getParentQuestionId());
            recordListVO.setTaskName(recordDO.getTaskName());
            recordListVO.setTaskStoreId(recordDO.getTaskStoreId());
            recordListVO.setTaskDesc(recordDO.getTaskDesc());
            // 检查项id不等于0，获取检查项数据
            recordListVO.setMetaColumnId(recordDO.getMetaColumnId());
            recordListVO.setSubEndTime(recordDO.getSubEndTime());
            recordListVO.setSubBeginTime(recordDO.getSubBeginTime());
            recordListVO.setLoopCount(recordDO.getLoopCount());
            recordListVO.setStoreId(recordDO.getStoreId());
            recordListVO.setUnifyTaskId(recordDO.getUnifyTaskId());
            // 判断逾期
            recordListVO.setStatus(recordDO.getStatus());
            // 用户
            recordListVO.setCreateUserId(recordDO.getCreateUserId());

            recordListVO.setHandleUserId(recordDO.getHandleUserId());
            recordListVO.setQuestionType(recordDO.getQuestionType());
        }
        return recordListVO;
    }

    @Override
    public Boolean storeWorkRecordInfoShare(String enterpriseId, String businessId, String key) {
        try {
            redisUtilPool.setString(key, businessId, 7 * 24 * 60 * 60);
        } catch (Exception e) {
            log.info("redis执行出错");
            return false;
        }
        return true;
    }


    @Override
    public PageInfo<StoreWorkRecordVO> storeWorkRecordList(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), storeWorkRecordListRequest.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        storeWorkRecordListRequest.setRegionPathList(regionPathList);
        if (storeWorkRecordListRequest.getBeginStoreWorkTime() != null) {
            storeWorkRecordListRequest.setBeginStoreWorkDate(DateUtils.convertTimeToString(storeWorkRecordListRequest.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        if (storeWorkRecordListRequest.getEndStoreWorkTime() != null) {
            storeWorkRecordListRequest.setEndStoreWorkDate(DateUtils.convertTimeToString(storeWorkRecordListRequest.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        List<StoreWorkRecordVO> storeWorkRecordVOList = Lists.newArrayList();
        PageHelper.startPage(storeWorkRecordListRequest.getPageNumber(), storeWorkRecordListRequest.getPageSize());
        if (storeWorkRecordListRequest.getTableMappingId() != null) {
            storeWorkRecordVOList = swStoreWorkDataTableDao.storeWorkRecordList(enterpriseId, storeWorkRecordListRequest);
        } else {
            storeWorkRecordVOList = swStoreWorkRecordDao.storeWorkRecordList(enterpriseId, storeWorkRecordListRequest);
        }
        if (CollectionUtils.isEmpty(storeWorkRecordVOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        PageInfo pageInfo = new PageInfo<>(storeWorkRecordVOList);

        List<Long> storeWorkIdList = storeWorkRecordVOList.stream().map(StoreWorkRecordVO::getStoreWorkId).collect(Collectors.toList());
        List<SwStoreWorkDO> storeWorkDOList = swStoreWorkDao.listBystoreWorkIds(enterpriseId, storeWorkIdList);
        Map<Long, SwStoreWorkDO> storeWorMap = storeWorkDOList.stream().collect(Collectors.toMap(SwStoreWorkDO::getId, Function.identity()));

        List<String> storeIdList = storeWorkRecordVOList.stream().map(StoreWorkRecordVO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeDoList = storeDao.getByStoreIdList(enterpriseId, storeIdList);
        Map<String, StoreDO> storeDOMap = storeDoList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity()));

        List<StorePathDTO> storePathDTOList = ListUtils.emptyIfNull(storeDoList)
                .stream()
                .map(data -> {
                    StorePathDTO storePathDTO = new StorePathDTO();
                    storePathDTO.setStoreId(data.getStoreId());
                    storePathDTO.setRegionPath(data.getRegionPath());
                    return storePathDTO;
                })
                .collect(Collectors.toList());
        Map<String, String> fullRegionNameMap = regionService.getFullRegionName(enterpriseId, storePathDTOList);

        List<String> actualHandleUserIdList = ListUtils.emptyIfNull(storeWorkRecordVOList)
                .stream()
                .map(data -> StrUtil.splitTrim(data.getActualHandleUserId(), Constants.COMMA))
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        Map<String, EnterpriseUserDO> userNameMap = enterpriseUserDao.getUserMap(enterpriseId, actualHandleUserIdList);
        for (StoreWorkRecordVO storeWorkRecordVO : storeWorkRecordVOList) {
            StoreDO storeDO = storeDOMap.get(storeWorkRecordVO.getStoreId());
            if (storeDO != null) {
                storeWorkRecordVO.setStoreNum(storeDO.getStoreNum());
                storeWorkRecordVO.setRegionPath(storeDO.getRegionPath());
            }
            storeWorkRecordVO.setFullRegionName(fullRegionNameMap.get(storeWorkRecordVO.getStoreId()));
            SwStoreWorkDO storeWorkDO = storeWorMap.get(storeWorkRecordVO.getStoreWorkId());
            storeWorkRecordVO.setWorkName(storeWorkDO.getWorkName());
            if (StringUtils.isNotBlank(storeWorkRecordVO.getActualHandleUserId())) {
                List<String> actualHandleUserNameList = new ArrayList<>();
                Map<String, EnterpriseUserDO> finalUserNameMap = userNameMap;
                StrUtil.splitTrim(storeWorkRecordVO.getActualHandleUserId(), Constants.COMMA).forEach(data -> {
                    EnterpriseUserDO enterpriseUserDO = finalUserNameMap.getOrDefault(data, new EnterpriseUserDO());
                    actualHandleUserNameList.add(enterpriseUserDO.getName());
                });
                storeWorkRecordVO.setActualHandleUserName(StringUtils.join(actualHandleUserNameList, Constants.COMMA));
            }
        }
        return pageInfo;
    }

    @Override
    public PageInfo<SwStoreWorkRecordDetailVO> storeWorkRecordDetailList(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), storeWorkRecordListRequest.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        storeWorkRecordListRequest.setRegionPathList(regionPathList);
        if (storeWorkRecordListRequest.getBeginStoreWorkTime() != null) {
            storeWorkRecordListRequest.setBeginStoreWorkDate(DateUtils.convertTimeToString(storeWorkRecordListRequest.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        if (storeWorkRecordListRequest.getEndStoreWorkTime() != null) {
            storeWorkRecordListRequest.setEndStoreWorkDate(DateUtils.convertTimeToString(storeWorkRecordListRequest.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        List<StoreWorkRecordVO> storeWorkRecordVOList = Lists.newArrayList();
        PageHelper.startPage(storeWorkRecordListRequest.getPageNumber(), storeWorkRecordListRequest.getPageSize());
        if (storeWorkRecordListRequest.getTableMappingId() != null) {
            storeWorkRecordVOList = swStoreWorkDataTableDao.storeWorkRecordList(enterpriseId, storeWorkRecordListRequest);
        } else {
            storeWorkRecordVOList = swStoreWorkRecordDao.storeWorkRecordList(enterpriseId, storeWorkRecordListRequest);
        }
        if (CollectionUtils.isEmpty(storeWorkRecordVOList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        PageInfo pageInfo = new PageInfo<>(storeWorkRecordVOList);

        List<String> storeIdList = storeWorkRecordVOList.stream().map(StoreWorkRecordVO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeDoList = storeDao.getByStoreIdList(enterpriseId, storeIdList);
        Map<String, StoreDO> storeDOMap = storeDoList.stream().collect(Collectors.toMap(StoreDO::getStoreId, Function.identity()));
        List<String> businessIds = storeWorkRecordVOList.stream().map(StoreWorkRecordVO::getTcBusinessId).collect(Collectors.toList());
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectSwStoreWorkDataTableByBusinessIds(enterpriseId, businessIds);
        Map<String, List<SwStoreWorkDataTableDO>> listMap = swStoreWorkDataTableDOS.stream().collect(Collectors.groupingBy(SwStoreWorkDataTableDO::getTcBusinessId));

        List<SwStoreWorkDataTableColumnDO> swStoreWorkDataTableColumnDOS = swStoreWorkDataTableColumnDao.selectByBusinessIds(enterpriseId, businessIds);
        Map<Long, List<SwStoreWorkDataTableColumnDO>> longListMap = swStoreWorkDataTableColumnDOS.stream().collect(Collectors.groupingBy(SwStoreWorkDataTableColumnDO::getDataTableId));


        List<SwStoreWorkRecordDetailVO> swStoreWorkRecordDetailVOS = new ArrayList<>();
        Map<String, List<String>> fullRegionNameMap = new HashMap<>();
        List<StorePathDTO> storePathDTOList = new ArrayList<>();
        for (StoreDO storeDO : storeDoList) {
            StorePathDTO storePathDTO = new StorePathDTO();
            storePathDTO.setStoreId(storeDO.getStoreId());
            storePathDTO.setRegionPath(storeDO.getRegionPath());
            storePathDTOList.add(storePathDTO);
        }
        fullRegionNameMap = regionService.getFullRegionNameList(enterpriseId, storePathDTOList);

        for (StoreWorkRecordVO storeWorkRecordVO : storeWorkRecordVOList) {
            SwStoreWorkRecordDetailVO swStoreWorkRecordDetailVO = new SwStoreWorkRecordDetailVO();
            swStoreWorkRecordDetailVO.setStoreName(storeWorkRecordVO.getStoreName());
            swStoreWorkRecordDetailVO.setStoreId(storeWorkRecordVO.getStoreId());
            swStoreWorkRecordDetailVO.setScore(storeWorkRecordVO.getTotalGetScore());
            swStoreWorkRecordDetailVO.setStoreNum(storeDOMap.getOrDefault(storeWorkRecordVO.getStoreId(), new StoreDO()).getStoreNum());
            swStoreWorkRecordDetailVO.setStoreWorkDate(DateUtils.getTime(storeWorkRecordVO.getStoreWorkDate()));

            swStoreWorkRecordDetailVO.setRegionNameList(fullRegionNameMap.get(storeWorkRecordVO.getStoreId()));

            List<SwStoreWorkDataTableDO> swList = listMap.get(storeWorkRecordVO.getTcBusinessId());
            List<SwStoreWorkTableDTO> list = new ArrayList();
            swList.forEach(swStoreWorkDataTableDO -> {
                SwStoreWorkTableDTO swStoreWorkTableDTO = new SwStoreWorkTableDTO();
                swStoreWorkTableDTO.setTableName(swStoreWorkDataTableDO.getTableName());
                swStoreWorkTableDTO.setBeginTime(swStoreWorkDataTableDO.getBeginTime());
                swStoreWorkTableDTO.setEndTime(swStoreWorkDataTableDO.getEndTime());
                swStoreWorkTableDTO.setTotalScore(swStoreWorkDataTableDO.getScore());
                swStoreWorkTableDTO.setId(swStoreWorkDataTableDO.getId());
                swStoreWorkTableDTO.setTableMappingId(swStoreWorkDataTableDO.getTableMappingId());

                List<SwStoreWorkDataTableColumnDO> swColumnList = longListMap.get(swStoreWorkDataTableDO.getId());
                List<SwStoreWorkColumnResultDTO> columnResultDTOS = new ArrayList<>();
                swColumnList.forEach(column -> {
                    SwStoreWorkColumnResultDTO swStoreWorkColumnResultDTO = new SwStoreWorkColumnResultDTO();
                    swStoreWorkColumnResultDTO.setColumnName(column.getMetaColumnName());
                    String status = "-";
                    if (column.getSubmitStatus() == 1) {
                        status = "已执行";
                    } else if (column.getSubmitStatus() == 0) {
                        status = "未执行";
                    }
                    String score = "-";
                    if (swStoreWorkDataTableDO.getCommentStatus() == 1) {
                        score = String.valueOf(column.getCheckScore());
                    }
                    swStoreWorkColumnResultDTO.setScore(score);
                    swStoreWorkColumnResultDTO.setHandleStatus(status);
                    swStoreWorkColumnResultDTO.setId(column.getId());
                    swStoreWorkColumnResultDTO.setTbMetaColumnId(column.getMetaColumnId());
                    columnResultDTOS.add(swStoreWorkColumnResultDTO);
                });
                swStoreWorkTableDTO.setSwStoreWorkColumnResultDTOS(columnResultDTOS);
                list.add(swStoreWorkTableDTO);
            });
            swStoreWorkRecordDetailVO.setSwStoreWorkTableDTOS(list);
            swStoreWorkRecordDetailVOS.add(swStoreWorkRecordDetailVO);
        }
        pageInfo.setList(swStoreWorkRecordDetailVOS);
        return pageInfo;
    }

    @Override
    public ImportTaskDO storeWorkRecordDetailListExport(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user) {
        // 查询导出数量，限流
        Long count = this.countStoreWorkRecord(enterpriseId, storeWorkRecordListRequest, user);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.STOREWORK_RECORD_DETAIL_LIST);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.STOREWORK_RECORD_DETAIL_LIST);
        // 构造异步导出参数
        ExportStoreWorkRecordRequest msg = new ExportStoreWorkRecordRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(storeWorkRecordListRequest);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        msg.setUser(user);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.STOREWORK_STORERECORD_DETAIL_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public ImportTaskDO storeWorkRecordListExport(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user) {
        // 查询导出数量，限流
        Long count = this.countStoreWorkRecord(enterpriseId, storeWorkRecordListRequest, user);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        // 通过枚举获取文件名称
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.STOREWORK_RECORD_LIST);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.STOREWORK_RECORD_LIST);
        // 构造异步导出参数
        ExportStoreWorkRecordRequest msg = new ExportStoreWorkRecordRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(storeWorkRecordListRequest);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        msg.setUser(user);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.STOREWORK_STORERECORD_LIST.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    public Long countStoreWorkRecord(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user) {
        Long count = 0L;
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), storeWorkRecordListRequest.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return count;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        storeWorkRecordListRequest.setRegionPathList(regionPathList);
        if (storeWorkRecordListRequest.getBeginStoreWorkTime() != null) {
            storeWorkRecordListRequest.setBeginStoreWorkDate(DateUtils.convertTimeToString(storeWorkRecordListRequest.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        if (storeWorkRecordListRequest.getEndStoreWorkTime() != null) {
            storeWorkRecordListRequest.setEndStoreWorkDate(DateUtils.convertTimeToString(storeWorkRecordListRequest.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_DAY));
        }
        if (storeWorkRecordListRequest.getTableMappingId() != null) {
            count = swStoreWorkDataTableDao.countStoreWorkRecord(enterpriseId, storeWorkRecordListRequest);
        } else {
            count = swStoreWorkRecordDao.countStoreWorkRecord(enterpriseId, storeWorkRecordListRequest);
        }
        return count;
    }

    private List<RegionPathDTO> getAuthRegionList(String enterpriseId, Boolean isAdmin, String userId, List<String> regionIdList) {
        if (!isAdmin && CollectionUtils.isEmpty(regionIdList)) {
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingService.listUserAuthMappingByUserId(enterpriseId, userId);
            if (CollectionUtils.isNotEmpty(userAuthMappingList)) {
                regionIdList = userAuthMappingList.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
            }
        }
        List<RegionPathDTO> regionPathList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(regionIdList)) {
            regionPathList = regionService.getRegionPathByList(enterpriseId, regionIdList);
        }
        return regionPathList;
    }

    private List<RegionPathDTO> getRegionPathList(String enterpriseId, boolean isAdmin, List<RegionPathDTO> regionPathDTOList, List<String> regionIdList) {
        boolean isNext = (isAdmin || (CollectionUtils.isNotEmpty(regionPathDTOList) && regionPathDTOList.size() == 1)) && CollectionUtils.isEmpty(regionIdList);
        //管理员默认下探
        if (isNext) {
            List<Long> nextRegionIdList = new ArrayList<>();
            if (isAdmin) {
                nextRegionIdList = regionMapper.getRegionIdListByParentId(enterpriseId, Constants.ROOT_DEPT_ID);
                if (CollectionUtils.isEmpty(nextRegionIdList)) {
                    nextRegionIdList.add(Constants.ROOT_DEPT_ID);
                }
            } else {
                String parentId = regionPathDTOList.get(0).getRegionId();
                nextRegionIdList = regionMapper.getRegionIdListByParentId(enterpriseId, Long.valueOf(parentId));
                if (CollectionUtils.isEmpty(nextRegionIdList)) {
                    nextRegionIdList.add(Long.valueOf(parentId));
                }
            }
            List<String> nextRegionIdStrList = nextRegionIdList.stream().map(String::valueOf).collect(Collectors.toList());
            /*if(CollectionUtils.isNotEmpty(nextRegionIdStrList) && nextRegionIdStrList.size() > Constants.INDEX_TEN){
                nextRegionIdStrList = nextRegionIdStrList.subList(0, Constants.INDEX_TEN);
            }*/
            regionPathDTOList = regionService.getRegionPathByList(enterpriseId, nextRegionIdStrList);
        }
        return regionPathDTOList;
    }

    public Long countStoreWorkStoreStatistics(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user) {
        Long count = 0L;
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), storeWorkDataListRequest.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return count;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        storeWorkDataListRequest.setRegionPathList(regionPathList);
        if (storeWorkDataListRequest.getTableMappingId() != null) {
            count = swStoreWorkDataTableDao.countStoreWorkStoreStatistics(enterpriseId, storeWorkDataListRequest);
        } else {
            count = swStoreWorkRecordDao.countStoreWorkStoreStatistics(enterpriseId, storeWorkDataListRequest);
        }
        return count;
    }

    public Long countStoreWorkDayStatistics(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user) {
        Long count = 0L;
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), storeWorkDataListRequest.getRegionIdList());
        //非管理员且没有管辖区域
        if (!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)) {
            return count;
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        storeWorkDataListRequest.setRegionPathList(regionPathList);
        if (storeWorkDataListRequest.getBeginStoreWorkTime() != null) {
            storeWorkDataListRequest.setBeginStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getBeginStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
        if (storeWorkDataListRequest.getEndStoreWorkTime() != null) {
            storeWorkDataListRequest.setEndStoreWorkDate(DateUtils.convertTimeToString(storeWorkDataListRequest.getEndStoreWorkTime(), DateUtils.DATE_FORMAT_SEC));
        }
        if (storeWorkDataListRequest.getTableMappingId() != null) {
            count = swStoreWorkDataTableDao.countStoreWorkDayStatistics(enterpriseId, storeWorkDataListRequest);
        } else {
            count = swStoreWorkRecordDao.countStoreWorkDayStatistics(enterpriseId, storeWorkDataListRequest);
        }
        return count;
    }

    /**
     * 店务算分
     *
     * @param eid
     * @param storeWorkDataTableDO
     */
    public void countScore(String eid, SwStoreWorkDataTableDO storeWorkDataTableDO) {
        List<SwStoreWorkDataTableColumnDO> staColumnList = new ArrayList<>();
        TbMetaTableDO tbMetaTable = tbMetaTableMapper.selectById(eid, storeWorkDataTableDO.getMetaTableId());
        //计算奖罚得分
        List<SwStoreWorkDataTableColumnDO> dataStaTableColumnList = swStoreWorkDataTableColumnDao.selectByDataTableId(eid, storeWorkDataTableDO.getId());
        // 结果项
        List<TbMetaColumnResultDO> columnResultDOList = tbMetaColumnResultMapper.selectByMetaTableId(eid, storeWorkDataTableDO.getMetaTableId());
        Map<Long, TbMetaColumnResultDO> columnIdResultMap = columnResultDOList.stream().collect(Collectors.toMap(TbMetaColumnResultDO::getId, Function.identity(), (a, b) -> a));
        List<TbMetaStaTableColumnDO> list = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(eid, Collections.singletonList(storeWorkDataTableDO.getMetaTableId()));

        Map<Long, TbMetaStaTableColumnDO> idMetaTableColumnMap = list.stream().collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity(), (a, b) -> a));
        //每一项最高奖金
        Map<Long, BigDecimal> columnMaxAwardMap = AbstractColumnObserver.getColumnMaxAwardMap(tbMetaTable, columnResultDOList);
        AtomicInteger failNum = new AtomicInteger();
        AtomicInteger passNum = new AtomicInteger();
        AtomicInteger inapplicableNum = new AtomicInteger();
        for (SwStoreWorkDataTableColumnDO a : dataStaTableColumnList) {
            SwStoreWorkDataTableColumnDO tableColumnDO = SwStoreWorkDataTableColumnDO.builder().id(a.getId()).build();
            TbMetaStaTableColumnDO tbMetaStaTableColumnDO = idMetaTableColumnMap.get(a.getMetaColumnId());
            // 修改标准检查项
            if (a.getCheckResultId() != null && a.getCheckResultId() > 0) {
                TbMetaColumnResultDO tbMetaColumnResultDO = columnIdResultMap.get(a.getCheckResultId());
                if (tbMetaColumnResultDO != null) {
                    tableColumnDO.setRewardPenaltMoney(tbMetaColumnResultDO.getMoney());
                }
            } else {
                if (tbMetaStaTableColumnDO != null) {
                    if (PASS.equals(a.getCheckResult())) {
                        tableColumnDO.setRewardPenaltMoney(tbMetaStaTableColumnDO.getAwardMoney());
                    }
                    if (FAIL.equals(a.getCheckResult())) {
                        tableColumnDO.setRewardPenaltMoney(tbMetaStaTableColumnDO.getPunishMoney().abs().multiply(new BigDecimal("-1")));
                    }
                }
            }
            BigDecimal columnMaxAward = new BigDecimal(Constants.ZERO_STR);
            if (columnMaxAwardMap.get(a.getMetaColumnId()) != null) {
                columnMaxAward = columnMaxAwardMap.get(a.getMetaColumnId());
            }
            if (tbMetaStaTableColumnDO != null) {
                tableColumnDO.setWeightPercent(tbMetaStaTableColumnDO.getWeightPercent());
                tableColumnDO.setColumnMaxScore(tbMetaStaTableColumnDO.getSupportScore());
                tableColumnDO.setColumnMaxAward(columnMaxAward);
            }
            //获取检查结果
            String checkResult = a.getCheckResult();
            //采集项不参与计算
            if (StringUtils.isNotBlank(checkResult) && tbMetaStaTableColumnDO != null &&
                    !MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(tbMetaStaTableColumnDO.getColumnType())) {
                //计算合格项数
                switch (checkResult) {
                    case PASS:
                        passNum.getAndIncrement();
                        break;
                    case FAIL:
                        failNum.getAndIncrement();
                        break;
                    case INAPPLICABLE:
                        inapplicableNum.getAndIncrement();
                        break;
                    default:
                }
            }
            //用于计算
            a.setWeightPercent(tableColumnDO.getWeightPercent());
            a.setColumnMaxScore(tableColumnDO.getColumnMaxScore());
            a.setColumnMaxAward(tableColumnDO.getColumnMaxAward());
            if (tableColumnDO.getRewardPenaltMoney() != null) {
                a.setRewardPenaltMoney(tableColumnDO.getRewardPenaltMoney());
            }
            staColumnList.add(tableColumnDO);
        }
        swStoreWorkDataTableColumnDao.batchUpdate(eid, staColumnList);
        //计算得分
        List<CalColumnScoreDTO> calColumnScoreList = buildCalColumnStore(dataStaTableColumnList, idMetaTableColumnMap);
        CalTableResultDTO checkResult = AbstractColumnObserver.getSingleTableResult(new CalTableScoreDTO(storeWorkDataTableDO.getId(), tbMetaTable, calColumnScoreList));
        //计算得分项数--
        SwStoreWorkDataTableDO recordDONew = new SwStoreWorkDataTableDO();
        recordDONew.setScore(checkResult.getResultScore());
        recordDONew.setTotalScore(checkResult.getCalTotalScore());
        recordDONew.setTotalCalColumnNum(checkResult.getTotalCalColumnNum());
        recordDONew.setPassColumnNum(passNum.get());
        recordDONew.setFailColumnNum(failNum.get());
        recordDONew.setInapplicableColumnNum(inapplicableNum.get());
        recordDONew.setId(storeWorkDataTableDO.getId());
        //计算巡店结果
        recordDONew.setCheckResultLevel(getCheckResultLevel(passNum.get(), tbMetaTable, checkResult.getResultScore(), checkResult.getCalTotalScore()));
        // 修改表上信息
        swStoreWorkDataTableDao.updateByPrimaryKeySelective(recordDONew, eid);
    }

    /**
     * 构建项分数信息
     *
     * @param dataStaTableColumnList
     * @param metaTableColumnMap
     * @return
     */
    public List<CalColumnScoreDTO> buildCalColumnStore(List<SwStoreWorkDataTableColumnDO> dataStaTableColumnList, Map<Long, TbMetaStaTableColumnDO> metaTableColumnMap) {
        if (CollectionUtils.isEmpty(dataStaTableColumnList)) {
            return null;
        }
        List<CalColumnScoreDTO> resultList = new ArrayList<>();
        for (SwStoreWorkDataTableColumnDO dataColumn : dataStaTableColumnList) {
            TbMetaStaTableColumnDO tbMetaStaTableColumn = metaTableColumnMap.get(dataColumn.getMetaColumnId());
            if (Objects.isNull(tbMetaStaTableColumn)) {
                continue;
            }
            CalColumnScoreDTO calColumnScore = CalColumnScoreDTO.builder().score(dataColumn.getCheckScore()).columnName(dataColumn.getMetaColumnName())
                    .scoreTimes(dataColumn.getScoreTimes()).awardTimes(dataColumn.getAwardTimes()).weightPercent(dataColumn.getWeightPercent())
                    .columnTypeEnum(MetaColumnTypeEnum.getColumnType(tbMetaStaTableColumn.getColumnType())).categoryName(tbMetaStaTableColumn.getCategoryName())
                    .checkResult(CheckResultEnum.getCheckResultEnum(dataColumn.getCheckResult())).columnMaxScore(dataColumn.getColumnMaxScore())
                    .rewardPenaltMoney(dataColumn.getRewardPenaltMoney()).build();
            resultList.add(calColumnScore);
        }
        return resultList;
    }

    private String getCheckResultLevel(Integer passNum, TbMetaTableDO tableDO, BigDecimal score, BigDecimal taskCalTotalScore) {
        if (StringUtils.isBlank(tableDO.getLevelInfo())) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(tableDO.getLevelInfo());
        List<TableCheckSettingLevelVO> levelList = JSONArray.parseArray(jsonObject.getString("levelList"), TableCheckSettingLevelVO.class);
        if (CollectionUtils.isEmpty(levelList)) {
            return null;
        }
        if (LevelRuleEnum.SCORING_RATE.getCode().equals(tableDO.getLevelRule()) && score != null && taskCalTotalScore != null) {
            levelList.sort(Comparator.comparingInt(TableCheckSettingLevelVO::getPercent).reversed());
            BigDecimal percent = BigDecimal.ZERO;
            if (new BigDecimal(Constants.ZERO_STR).compareTo(score) != 0 && new BigDecimal(Constants.ZERO_STR).compareTo(taskCalTotalScore) != 0) {
                percent = (score.divide(taskCalTotalScore, 2, RoundingMode.DOWN).multiply(new BigDecimal(Constants.ONE_HUNDRED)));
            }
            for (TableCheckSettingLevelVO levelVO : levelList) {
                if (percent.intValue() >= levelVO.getPercent()) {
                    return levelVO.getKeyName();
                }
            }
        } else {
            levelList.sort(Comparator.comparingInt(TableCheckSettingLevelVO::getQualifiedNum).reversed());
            for (TableCheckSettingLevelVO levelVO : levelList) {
                if (passNum >= levelVO.getQualifiedNum()) {
                    return levelVO.getKeyName();
                }
            }
        }
        return null;
    }

    public void cancelUpcoming(String enterpriseId, String dingCorpId, String appType, Long dataTableId, String operate) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("taskKey", DingMsgEnum.STOREWORK.getDesc() + "_" + operate + "_" + dataTableId);
        jsonObject.put("appType", appType);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

}
