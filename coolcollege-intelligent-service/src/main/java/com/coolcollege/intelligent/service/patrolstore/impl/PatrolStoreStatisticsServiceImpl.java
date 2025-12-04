package com.coolcollege.intelligent.service.patrolstore.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.AsyncExport;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.enums.region.FixedRegionEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.DistinctRegionPathUtil;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterprisePatrolLevelDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.UserRoleDTO;
import com.coolcollege.intelligent.model.enums.LevelRuleEnum;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.enums.UserAuthMappingTypeEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.patrolstore.TbDataStaTableColumnDO;
import com.coolcollege.intelligent.model.patrolstore.TbDataTableDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.dto.PatrolOverviewDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.PatrolOverviewUserDTO;
import com.coolcollege.intelligent.model.patrolstore.query.*;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.patrolstore.statistics.*;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.coolcollege.intelligent.model.question.dto.QuestionReportDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.*;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.BasicsStoreDTO;
import com.coolcollege.intelligent.model.store.dto.StoreOperationDTO;
import com.coolcollege.intelligent.model.unifytask.TaskMappingDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStoreStatisticsService;
import com.coolcollege.intelligent.service.question.QuestionOrderTaskService;
import com.coolcollege.intelligent.service.region.RegionService;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_SEC;
import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.PATROL_STORE_OFFLINE;
import static com.coolcollege.intelligent.model.enums.TaskTypeEnum.PATROL_STORE_ONLINE;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.BusinessTypeConstant.PATROL_STORE;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.CheckResultConstant.FAIL;
import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.CheckResultConstant.PASS;

/**
 * 报表统计的类，这里做，sum ,group , count , 单行记录的那种报表放在各自的service中做。
 *
 * @author jeffrey
 * @date 2020/12/09
 */
@Service
@Slf4j
public class PatrolStoreStatisticsServiceImpl implements PatrolStoreStatisticsService {

    @Resource
    private TbDataTableMapper tbDataTableMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private AuthVisualService authVisualService;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private RegionService regionService;
    @Resource
    private RegionMapper regionMapper;
    @Resource
    private TaskParentMapper taskParentMapper;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private QuestionOrderTaskService questionOrderTaskService;
    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Resource
    private TaskMappingMapper taskMappingMapper;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor EXECUTOR_SERVICE;

    @Resource
    private EnterpriseStoreCheckSettingMapper storeCheckSettingMapper;

    @Resource
    private PatrolStoreService patrolStoreService;

    @Resource
    private EnterpriseService enterpriseService;

    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;

    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Override
    public PageInfo statisticsUser(String enterpriseId, PatrolStoreStatisticsUserQuery patrolStoreStatisticsUserQuery) {
        List<String> userIdList = patrolStoreStatisticsUserQuery.getUserIdList();
        // 分页
        PageHelper.startPage(patrolStoreStatisticsUserQuery.getPageNum(), patrolStoreStatisticsUserQuery.getPageSize());
        userIdList = enterpriseUserMapper.selectUserIdsByUserList(enterpriseId,userIdList);
        PageInfo pageInfo = new PageInfo(userIdList);
        if(CollectionUtils.isEmpty(userIdList)){
            return pageInfo;
        }
        Date endDate = patrolStoreStatisticsUserQuery.getEndDate();
        Date beginDate = patrolStoreStatisticsUserQuery.getBeginDate();

        List<TbMetaTableDO> tableList =
                tbMetaTableMapper.getTableByCreateUserId(enterpriseId, userIdList, beginDate, endDate);


        List<Long> tableIdList = tableList.stream().map(TbMetaTableDO::getId).collect(Collectors.toList());
        Map<String, List<TbMetaTableDO>> tableMap =
                tableList.stream().collect(Collectors.groupingBy(TbMetaTableDO::getCreateUserId));

       //
        List<PatrolStoreStatisticsUserDTO> recordList  =
                tbDataTableMapper.getListByMetaTableIdListAndTimeGroupBy(enterpriseId, tableIdList, beginDate, endDate);

        Map<Long, Integer> recordCountMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(recordList)){
            recordCountMap = recordList.stream().collect(Collectors.toMap(
                    PatrolStoreStatisticsUserDTO::getMetaTableId, PatrolStoreStatisticsUserDTO::getPatrolNum, (a, b) -> a));
        }


        // 获取人员信息
        List<EnterpriseUserDTO> userDTOS = enterpriseUserMapper.getUserDetailList(enterpriseId, userIdList);
        Map<String, EnterpriseUserDTO> userMap = userDTOS.stream()
                .filter(a -> a.getUserId() != null && a.getName() != null)
                .collect(Collectors.toMap(EnterpriseUserDTO::getUserId, Function.identity(), (a, b) -> a));

        // 获取管理门店信息
        List<AuthStoreCountDTO> authStoreCountDTOList =
                authVisualService.authStoreCount(enterpriseId, userIdList, Boolean.TRUE);
        // 获取人员职位
        List<UserRoleDTO> userRoleDTOS = sysRoleMapper.userAndRolesByUserId(enterpriseId, userIdList);
        Map<String, String> userRoleMap = userRoleDTOS.stream()
                .filter(a -> a.getUserId() != null && a.getRoleName() != null)
                .collect(Collectors.toMap(UserRoleDTO::getUserId, UserRoleDTO::getRoleName, (a, b) -> a));
        Map<String,
                Integer> userStoreMap = CollectionUtil.emptyIfNull(authStoreCountDTOList).stream()
                .collect(Collectors.toMap(AuthStoreCountDTO::getUserId,
                        data -> CollectionUtils.emptyIfNull(data.getStoreList()).size(), (a, b) -> a));
        // 问题数
        List<PatrolStoreStatisticsUserDTO> questionStatisticDTOS =
                tbDataStaTableColumnMapper.statisticsUser(enterpriseId, userIdList, beginDate, endDate);
        Map<String, PatrolStoreStatisticsUserDTO> userIdQuestionStatisticMap = questionStatisticDTOS.stream()
                .collect(Collectors.toMap(PatrolStoreStatisticsUserDTO::getUserId, Function.identity(), (a, b) -> a));


        List<PatrolStoreStatisticsUserDTO> resultList =
                tbPatrolStoreRecordMapper.statisticsUser(enterpriseId, userIdList, beginDate, endDate);
        Map<String,PatrolStoreStatisticsUserDTO> resultMap = new HashMap<>();
        for(PatrolStoreStatisticsUserDTO dto : resultList){
            resultMap.put(dto.getUserId(),dto);
        }
        userIdList.stream().forEach(data -> {
            if(resultMap.get(data) == null){
                PatrolStoreStatisticsUserDTO patrolStoreStatisticsUserDTO = new PatrolStoreStatisticsUserDTO();
                patrolStoreStatisticsUserDTO.setUserId(data);
                patrolStoreStatisticsUserDTO.setPatrolNum(0);
                patrolStoreStatisticsUserDTO.setPatrolStoreNum(0);
                resultList.add(patrolStoreStatisticsUserDTO);
            }
        });
        Map<Long, Integer> finalRecordCountMap = recordCountMap;
        //填充用户角色
        //查看是否是老企业,获取对应部门信息
        boolean historyEnterprise = enterpriseService.isHistoryEnterprise(enterpriseId);
        Map<String, String> fullNameMap = new HashMap<>();
        Map<String, List<String>> userRegionMap = new HashMap<>();
        if (!historyEnterprise) {
            List<UserRegionMappingDO> regionMappingList = userRegionMappingDAO.getRegionIdsByUserIds(enterpriseId, userIdList);
            List<Long> regionIds = regionMappingList.stream().map(o->Long.valueOf(o.getRegionId())).distinct().collect(Collectors.toList());
            fullNameMap = regionService.getNoBaseNodeFullNameByRegionIds(enterpriseId, regionIds, Constants.SPRIT);
            userRegionMap = regionMappingList.stream().collect(Collectors.groupingBy(UserRegionMappingDO::getUserId, Collectors.mapping(UserRegionMappingDO::getRegionId, Collectors.toList())));
        }
        Map<String, List<String>> finalUserRegionMap = userRegionMap;
        Map<String, String> finalFullNameMap = fullNameMap;
        resultList.stream().forEach(dto -> {
            List<TbMetaTableDO> tableDOList = tableMap.get(dto.getUserId());
            dto.setCreateTableNum(0);
            dto.setTableUsedTimes(0);
            dto.setTableUsedNum(0);
            if (tableDOList != null) {
                dto.setCreateTableNum(tableDOList.size());
                tableDOList.forEach(data -> {
                    Integer patrolNum = finalRecordCountMap.get(data.getId());
                    int a = dto.getTableUsedNum();
                    int b = dto.getTableUsedTimes();
                    a += data.getLocked() == 1 ? 1 : 0;
                    b += patrolNum == null ? 0 : patrolNum;
                    dto.setTableUsedNum(a);
                    dto.setTableUsedTimes(b);
                });
            }
            String roleName = userRoleMap.get(dto.getUserId());
            String userName = userMap.get(dto.getUserId()).getName();
            String resultName = String.format("%s(%s)", userName, roleName);
            if(!historyEnterprise){
                dto.setDepartmentName(getUserRegionName(finalUserRegionMap.get(dto.getUserId()), finalFullNameMap));
            }
            dto.setUserName(resultName);
            int manageStoreNum = userStoreMap.get(dto.getUserId()) == null ? 0 : userStoreMap.get(dto.getUserId());
            dto.setManageStoreNum(manageStoreNum);
            dto.setUnPatrolStoreNum(manageStoreNum - dto.getPatrolStoreNum());
            PatrolStoreStatisticsUserDTO questionStatistic = userIdQuestionStatisticMap.get(dto.getUserId());
            if (questionStatistic != null) {
                dto.setTotalQuestionNum(questionStatistic.getTotalQuestionNum());
                dto.setTodoQuestionNum(questionStatistic.getTodoQuestionNum());
                dto.setUnRecheckQuestionNum(questionStatistic.getUnRecheckQuestionNum());
                dto.setFinishQuestionNum(questionStatistic.getFinishQuestionNum());
            }
        });
        pageInfo.setList(resultList);
        return pageInfo;
    }
    public String getUserRegionName(List<String> userRegionIds, Map<String, String> fullNameMap){
        StringBuilder regionName = new StringBuilder("");
        ListUtils.emptyIfNull(userRegionIds).forEach(regionId->{
            String name = fullNameMap.get(regionId);
            if(StringUtils.isBlank(name)){
                return;
            }
            String regionPathName = name.substring(Constants.INDEX_ONE, name.length() - Constants.INDEX_ONE);
            regionName.append(regionPathName).append(Constants.COMMA);
        });
        if(regionName.length() > 0){
            return regionName.substring(0, regionName.length()-Constants.INDEX_ONE);
        }
        return regionName.toString();
    }

    @Override
    public PageInfo<PatrolStoreStatisticsStoreDTO> statisticsStore(String enterpriseId,
                                                                   PatrolStoreStatisticsStoreQuery query) {
        List<StoreDO> storeDOList = null;
        PageInfo result = null;
        List<String> storeIdList = query.getStoreIdList();
        if (CollectionUtils.isNotEmpty(query.getStoreIdList())) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            storeDOList = storeMapper.getByStoreIdListAndStatus(enterpriseId, storeIdList, query.getStoreStatus());
            result = new PageInfo<>(storeDOList);
        }
        if (query.getRegionId() != null) {
            // 根据regionId获取regionPath
            String regionPath = regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId()));
            // 根据regionPath模糊，分页查询门店
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            storeDOList =
                    storeMapper.getByRegionPathLeftLike(enterpriseId, StringUtils.substringBeforeLast(regionPath, "]"), query.getStoreStatus());
            result = new PageInfo<>(storeDOList);
        }else if(CollectionUtils.isNotEmpty(query.getRegionIdList())){
            List<RegionPathDTO> regionPathDTOList = regionService.getRegionPathByList(enterpriseId, query.getRegionIdList());
            if (CollectionUtils.isEmpty(regionPathDTOList)) {
                return new PageInfo<>(Lists.newArrayList());
            }
            List<String> regionPathList = regionPathDTOList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
            // 根据regionPath模糊，分页查询门店
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            storeDOList =
                    storeMapper.getByRegionPathList(enterpriseId, regionPathList, query.getStoreStatus());
            result = new PageInfo<>(storeDOList);
        }
        if (CollectionUtils.isEmpty(storeDOList)) {
            return result;
        }
        List<PatrolStoreStatisticsStoreDTO> resultList = statisticsStoreData(enterpriseId, query, storeDOList);
        result.setList(resultList);
        return result;
    }

    /**
     * 门店统计数据组装
     */
    private List<PatrolStoreStatisticsStoreDTO> statisticsStoreData(String enterpriseId,
                                                                    PatrolStoreStatisticsStoreQuery query, List<StoreDO> storeDOList) {
        Set<String> storeIds = storeDOList.stream().map(StoreDO::getStoreId).collect(Collectors.toSet());
        // 门店统计数据问题
        List<PatrolStoreStatisticsStoreDTO> questionStatisticsDTOS =
                tbDataStaTableColumnMapper.patrolStoreStatisticsStore(enterpriseId, new ArrayList<>(storeIds),
                        query.getBeginDate(), query.getEndDate());
        Map<String, PatrolStoreStatisticsStoreDTO> storeIdQuestionStatisticsMap = questionStatisticsDTOS.stream()
                .collect(Collectors.toMap(PatrolStoreStatisticsStoreDTO::getStoreId, Function.identity(), (a, b) -> a));
        // 门店统计数据问题
        List<PatrolStoreStatisticsStoreDTO> patrolStoreStatisticsDTOS =
                tbPatrolStoreRecordMapper.patrolStoreStatisticsStore(enterpriseId, new ArrayList<>(storeIds),
                        query.getBeginDate(), query.getEndDate());
        Map<String, PatrolStoreStatisticsStoreDTO> storeIdPatrolStoreStatisticsMap = patrolStoreStatisticsDTOS.stream()
                .collect(Collectors.toMap(PatrolStoreStatisticsStoreDTO::getStoreId, Function.identity(), (a, b) -> a));
        // 返回值组装
        return storeDOList.stream().map(a -> {
            PatrolStoreStatisticsStoreDTO dto =
                    PatrolStoreStatisticsStoreDTO.builder().storeId(a.getStoreId()).storeName(a.getStoreName()).build();
            PatrolStoreStatisticsStoreDTO questionStatistics = storeIdQuestionStatisticsMap.get(a.getStoreId());
            if (questionStatistics != null) {
                dto.setTotalQuestionNum(questionStatistics.getTotalQuestionNum());
                dto.setTodoQuestionNum(questionStatistics.getTodoQuestionNum());
                dto.setUnRecheckQuestionNum(questionStatistics.getUnRecheckQuestionNum());
                dto.setFinishQuestionNum(questionStatistics.getFinishQuestionNum());
            }
            PatrolStoreStatisticsStoreDTO patrolStoreStatistics = storeIdPatrolStoreStatisticsMap.get(a.getStoreId());
            if (patrolStoreStatistics != null) {
                dto.setPatrolNum(patrolStoreStatistics.getPatrolNum());
                dto.setPatrolPersonNum(patrolStoreStatistics.getPatrolPersonNum());
            }
            dto.setStoreName(a.getStoreName());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PatrolStoreStatisticsRegionDTO> statisticsRegionById(String eid,
                                                                     PatrolStoreStatisticsRegionQuery query) {
        String regionId = query.getRegionId();
        if (StrUtil.isBlank(regionId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "区域id不能为空");
        }
        // 获取子区域列表
        List<RegionChildDTO> childRegions = regionMapper.getRegionByParentId(eid, Collections.singletonList(regionId), true);
        if (CollUtil.isEmpty(childRegions)) {
            return new ArrayList<>();
        }
        List<String> regionIds = childRegions.stream().map(RegionChildDTO::getId).collect(Collectors.toList());
        query.setRegionIds(regionIds);
        query.setGetChild(true);
        return statisticsRegion(eid, query);
    }

    @Override
    public List<PatrolStoreStatisticsRegionDTO> statisticsRegion(String eid, PatrolStoreStatisticsRegionQuery query) {

        List<String> regionIds = query.getRegionIds();
        if (CollUtil.isEmpty(regionIds)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "请选择需要查询的区域");
        }
        // 如果不是查询子区域列表 则限制每次最多20条
        if (!query.isGetChild() && regionIds.size() > 20) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "所选区域不能超过20个");
        }
        Date beginDate = query.getBeginDate();
        Date endDate = query.getEndDate();
        // 获取区域的路径
        List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(eid, regionIds);

        // 区域统计异步缓存
        Map<String, Future<PatrolStoreStatisticsRegionDTO>> idTaskMap = new HashMap<>();

        CurrentUser user = query.getUser();
        // 异步处理统计数据
        for (RegionPathDTO region : regionPathList) {
            String regionId = region.getRegionId();
            String regionPath = region.getRegionPath();

            boolean isRoot = regionId.equals(Constants.ROOT_REGION_ID);
            idTaskMap.put(regionId,
                    EXECUTOR_SERVICE.submit(() -> getRegionStatistics(eid, isRoot, regionPath, beginDate, endDate, user, null)));
        }
        List<PatrolStoreStatisticsRegionDTO> result = new ArrayList<>();
        // 获取结果
        for (RegionPathDTO region : regionPathList) {
            String regionId = region.getRegionId();
            Future<PatrolStoreStatisticsRegionDTO> future = idTaskMap.get(regionId);
            try {
                // 获取统计信息
                PatrolStoreStatisticsRegionDTO statisticsRegion = future.get();
                statisticsRegion.setRegionId(regionId);
                statisticsRegion.setName(region.getRegionName());
                statisticsRegion.setStoreNum(region.getStoreNum());

                result.add(statisticsRegion);
            } catch (Exception e) {
                log.error("统计区域出错：", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "获取统计数据异常");
            }
        }
        return result;
    }
    /**
     * 统计区域数据
     * @param eid
     * @param isRoot
     * @param regionPath
     * @param beginDate
     * @param endDate
     * @param user
     * @param storeIds
     * @return
     */
    private PatrolStoreStatisticsRegionDTO getRegionStatistics(String eid, boolean isRoot, String regionPath,
                                                               Date beginDate, Date endDate, CurrentUser user, List<String> storeIds) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        log.info("区域统计参数, eid={}, isRoot={}, regionPath={}, beginDate={}, endDate={}", eid, isRoot, regionPath, beginDate, endDate);
        // 获取巡店数据(巡店数、巡店人数、巡店门店数)
        PatrolStoreStatisticsRegionDTO statisticsRegionRecord =
                tbDataStaTableColumnMapper.patrolStoreStatisticsRegionRecord(eid, regionPath, isRoot, beginDate, endDate, storeIds);
        if (statisticsRegionRecord == null) {
            statisticsRegionRecord = new PatrolStoreStatisticsRegionDTO(0, 0, 0, 0, 0, 0, 0, 0);
        }
//            // 获取巡店问题相关数据
//            PatrolStoreStatisticsRegionDTO statisticsRegionColumn =
//                    tbDataStaTableColumnMapper.patrolStoreStatisticsRegionColumn(eid, regionPath, isRoot, beginDate, endDate, storeIds);
            // 获取巡店问题相关数据
        //根据巡店类型统计巡店数量
        List<PatrolStoreStatisticsGroupByPatrolTypeDto> numByPatrolTypeList = tbPatrolStoreRecordMapper.getNumByPatrolType(eid, regionPath, isRoot, beginDate, endDate, storeIds);
        for (PatrolStoreStatisticsGroupByPatrolTypeDto temp:numByPatrolTypeList) {
            if (UnifyTaskConstant.TaskType.PATROL_STORE_ONLINE.equals(temp.getPatrolType())){
                statisticsRegionRecord.setOnlineNum(temp.getNum()==null?0:temp.getNum());
            }else if (UnifyTaskConstant.TaskType.PATROL_STORE_OFFLINE.equals(temp.getPatrolType())){
                statisticsRegionRecord.setOfflineNum(temp.getNum()==null?0:temp.getNum());
            }else {
                statisticsRegionRecord.setPictureInspectionNum(temp.getNum()==null?0:temp.getNum());
            }
        }
        PatrolStoreStatisticsRegionDTO statisticsRegionColumn =
                    taskStoreMapper.patrolStoreStatisticsRegionColumn(eid, regionPath, isRoot, beginDate, endDate, storeIds);
        if (statisticsRegionColumn != null) {
            statisticsRegionRecord.setTotalQuestionNum(statisticsRegionColumn.getTotalQuestionNum());
            statisticsRegionRecord.setTodoQuestionNum(statisticsRegionColumn.getTodoQuestionNum());
            statisticsRegionRecord.setUnRecheckQuestionNum(statisticsRegionColumn.getUnRecheckQuestionNum());
            statisticsRegionRecord.setFinishQuestionNum(statisticsRegionColumn.getFinishQuestionNum());
        }
        log.info("区域统计结果==={},  isRoot={}, regionPath={}, beginDate={}, endDate={},  eid={}", JSON.toJSONString(statisticsRegionRecord), isRoot, regionPath, beginDate, endDate, eid);
        return statisticsRegionRecord;
    }



    /**
     * 自定义检查表模板统计
     */
    private List<PatrolStoreStatisticsMetaDefTableDTO> statisticsMetaDefTableData(String enterpriseId,
                                                                                  PatrolStoreStatisticsMetaTableQuery query, List<TbMetaTableDO> tbMetaTableDOList) {
        Set<Long> metaTableIds = tbMetaTableDOList.stream().map(TbMetaTableDO::getId).collect(Collectors.toSet());
        // 企业门店数
        Integer enterpriseStoreNum = storeMapper.countStore(enterpriseId);
        // Map:metaTableId门店统计数据
        List<PatrolStoreStatisticsMetaTableDTO> patrolStoreStatisticDTOList =
                tbDataTableMapper.patrolStoreStatisticsMetaTable(enterpriseId, new ArrayList<>(metaTableIds),
                        query.getBeginDate(), query.getEndDate());
        Map<Long, PatrolStoreStatisticsMetaTableDTO> metaTableIdStatisticMap =
                patrolStoreStatisticDTOList.stream().collect(
                        Collectors.toMap(PatrolStoreStatisticsMetaTableDTO::getMetaTableId, Function.identity(), (a, b) -> a));
        // Map:metaTableId->columnNum
        List<PatrolStoreStatisticsMetaDefTableDTO> columnNumList =
                tbMetaDefTableColumnMapper.statisticsColumnNum(enterpriseId, new ArrayList<>(metaTableIds));
        Map<Long, Integer> metaTableIdColumnNumMap =
                columnNumList.stream().collect(Collectors.toMap(PatrolStoreStatisticsMetaDefTableDTO::getMetaTableId,
                        PatrolStoreStatisticsMetaDefTableDTO::getColumnNum, (a, b) -> a));
        // 返回值组装
        return tbMetaTableDOList.stream().map(a -> {
            Long metaTableId = a.getId();
            PatrolStoreStatisticsMetaDefTableDTO dto =
                    PatrolStoreStatisticsMetaDefTableDTO.builder().metaTableId(metaTableId).tableName(a.getTableName())
                            .createUserName(a.getCreateUserName()).enterpriseStoreNum(enterpriseStoreNum)
                            .columnNum(metaTableIdColumnNumMap.getOrDefault(metaTableId, 0)).build();
            PatrolStoreStatisticsMetaTableDTO statistic = metaTableIdStatisticMap.get(metaTableId);
            if (statistic != null) {
                dto.setPatrolNum(statistic.getPatrolNum());
                dto.setPatrolStoreNum(statistic.getPatrolStoreNum());
                dto.setUsePersonNum(statistic.getUsePersonNum());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public PageInfo statisticsColumnPerTable(String enterpriseId, StatisticsStaColumnRequest request) {

        Long tableId = request.getTableId();
        Date beginDate = request.getBeginDate();
        Date endDate = request.getEndDate();
        TbMetaTableDO tableDO = tbMetaTableMapper.selectById(enterpriseId, tableId);
        if (tableDO == null) {
            return new PageInfo();
        }
        List<TbMetaStaTableColumnDO> columnDOList =
                tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Arrays.asList(tableId), Boolean.FALSE);
        List<Long> metaColumnIdList = columnDOList.stream().map(column -> column.getId()).collect(Collectors.toList());
        Map<Long, TbMetaStaTableColumnDO> columnMap =
                columnDOList.stream().collect(Collectors.toMap(data -> data.getId(), data -> data, (a, b) -> a));
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<PatrolStoreStatisticsColumnDTO> result =
                tbDataStaTableColumnMapper.statisticsColumnPerTable(enterpriseId, metaColumnIdList, beginDate, endDate);
        if (result == null) {
            return new PageInfo();
        }
        result.stream().forEach(data -> {
            TbMetaStaTableColumnDO columnDO = columnMap.get(data.getColumnId());
            data.setTableName(tableDO.getTableName());
            if (columnDO != null) {
                data.setCategoryName(columnDO.getCategoryName());
            }
        });
        return new PageInfo(result);
    }

    @Override
    public List<PatrolStoreStatisticsRegionDTO> statisticsSbuRegion(String enterpriseId, Long fatherRegionId,
                                                                    Date beginDate, Date endDate) {
        return null;
    }

    @Override
    public List<PatrolStoreStatisticsRegionDTO> statisticsRegion(String enterpriseId, List<Long> fatherRegionId,
                                                                 Date beginDate, Date endDate) {
        return null;
    }

    /**
     * 标准检查项统计数据
     */

    @Override
    public List<PatrolStoreStatisticsStoreDTO> statisticsStoreDataExport(String enterpriseId, Date beginDate, Date endDate, List<StoreDO> storeDOList) {
        PatrolStoreStatisticsStoreQuery query = new PatrolStoreStatisticsStoreQuery();
        query.setBeginDate(beginDate);
        query.setEndDate(endDate);
        return this.statisticsStoreData(enterpriseId,query,storeDOList);
    }

    @Override
    public List<PatrolStoreStatisticsMetaDefTableDTO> statisticsMetaDefTableAllExport(String enterpriseId, List<TbMetaTableDO> tbMetaTableDOList, Date beginDate, Date endDate) {
        PatrolStoreStatisticsMetaTableQuery query = new PatrolStoreStatisticsMetaTableQuery();
        query.setBeginDate(beginDate);
        query.setEndDate(endDate);
        return statisticsMetaDefTableData(enterpriseId, query, tbMetaTableDOList);


    }


    @Override
    @Deprecated
    @AsyncExport(type = ImportTaskConstant.EXPORT_USER)
    public Object statisticsUserExport(String enterpriseId,
                                       PatrolStoreStatisticsUserQuery query) {
        List<String> userIdList = query.getUserIdList();
        if (CollectionUtil.isEmpty(userIdList)) {
            userIdList = enterpriseUserMapper.selectAllUserIds(enterpriseId);
        }
        Date endDate = query.getEndDate();
        Date beginDate = query.getBeginDate();

        List<TbMetaTableDO> tableList =
                tbMetaTableMapper.getTableByCreateUserId(enterpriseId, userIdList, beginDate, endDate);
        List<Long> tableIdList = tableList.stream().map(TbMetaTableDO::getId).collect(Collectors.toList());
        Map<String, List<TbMetaTableDO>> tableMap =
                tableList.stream().collect(Collectors.groupingBy(TbMetaTableDO::getCreateUserId));

        List<TbPatrolStoreRecordDO> tableRecordDOS = tbPatrolStoreRecordMapper
                .getListByMetaTableIdListAndTime(enterpriseId, tableIdList, beginDate, endDate);
        /*Map<Long, List<TbPatrolStoreRecordDO>> recordMap =
                tableRecordDOS.stream().collect(Collectors.groupingBy(TbPatrolStoreRecordDO::getMetaTableId));*/
        List<Long> recordIdList = tableRecordDOS.stream().map(TbPatrolStoreRecordDO::getId).collect(Collectors.toList());
        Map<Long, List<TbDataTableDO>> dataTableMap = Maps.newHashMap();
        List<TbDataTableDO> dataTableList = tbDataTableMapper.getListByBusinessIdList(enterpriseId, new ArrayList<>(recordIdList), PATROL_STORE);
        if (CollectionUtils.isNotEmpty(dataTableList)) {
            dataTableMap = dataTableList.stream()
                    .collect(Collectors.groupingBy(TbDataTableDO::getMetaTableId));
        }

        // 获取人员信息
        List<EnterpriseUserDTO> userDTOS = enterpriseUserMapper.getUserDetailList(enterpriseId, userIdList);
        Map<String, String> userMap = userDTOS.stream()
                .filter(a -> a.getUserId() != null && a.getName() != null)
                .collect(Collectors.toMap(EnterpriseUserDTO::getUserId, EnterpriseUserDTO::getName, (a, b) -> a));
        // 获取管理门店信息
        List<AuthStoreCountDTO> authStoreCountDTOList =
                authVisualService.authStoreCount(enterpriseId, userIdList, Boolean.TRUE);
        // 获取人员职位
        List<UserRoleDTO> userRoleDTOS = sysRoleMapper.userAndRolesByUserId(enterpriseId, userIdList);
        Map<String, String> userRoleMap = userRoleDTOS.stream()
                .filter(a -> a.getUserId() != null && a.getRoleName() != null)
                .collect(Collectors.toMap(UserRoleDTO::getUserId, UserRoleDTO::getRoleName, (a, b) -> a));
        Map<String,
                Integer> userStoreMap = CollectionUtil.emptyIfNull(authStoreCountDTOList).stream()
                .collect(Collectors.toMap(AuthStoreCountDTO::getUserId,
                        data -> CollectionUtils.emptyIfNull(data.getStoreList()).size(), (a, b) -> a));
        // 问题数
        List<PatrolStoreStatisticsUserDTO> questionStatisticDTOS =
                tbDataStaTableColumnMapper.statisticsUser(enterpriseId, userIdList, beginDate, endDate);
        Map<String, PatrolStoreStatisticsUserDTO> userIdQuestionStatisticMap = questionStatisticDTOS.stream()
                .collect(Collectors.toMap(PatrolStoreStatisticsUserDTO::getUserId, Function.identity(), (a, b) -> a));

        List<PatrolStoreStatisticsUserDTO> resultList =
                tbPatrolStoreRecordMapper.statisticsUser(enterpriseId, userIdList, beginDate, endDate);

        Map<Long, List<TbDataTableDO>> finalDataTableMap = dataTableMap;
        resultList.forEach(dto -> {

            List<TbMetaTableDO> tableDOList = tableMap.get(dto.getUserId());
            dto.setCreateTableNum(0);
            dto.setTableUsedTimes(0);
            dto.setTableUsedNum(0);
            if (tableDOList != null) {
                dto.setCreateTableNum(tableDOList.size());
                tableDOList.forEach(data -> {
                    List<TbDataTableDO> tmpDataTableDOList = finalDataTableMap.get(data.getId());
                    int a = dto.getTableUsedNum();
                    int b = dto.getTableUsedTimes();
                    a += data.getLocked() == 1 ? 1 : 0;
                    b += tmpDataTableDOList == null ? 0 : tmpDataTableDOList.size();
                    dto.setTableUsedNum(a);
                    dto.setTableUsedTimes(b);
                });
            }
            String roleName = userRoleMap.get(dto.getUserId());
            String userName = userMap.get(dto.getUserId());
            String resultName = String.format("%s(%s)", userName, roleName);
            dto.setUserName(resultName);
            int manageStoreNum = userStoreMap.get(dto.getUserId());
            dto.setManageStoreNum(manageStoreNum);
            dto.setUnPatrolStoreNum(manageStoreNum - dto.getPatrolStoreNum());
            PatrolStoreStatisticsUserDTO questionStatistic = userIdQuestionStatisticMap.get(dto.getUserId());
            if (questionStatistic != null) {
                dto.setTotalQuestionNum(questionStatistic.getTotalQuestionNum());
                dto.setTodoQuestionNum(questionStatistic.getTodoQuestionNum());
                dto.setUnRecheckQuestionNum(questionStatistic.getUnRecheckQuestionNum());
                dto.setFinishQuestionNum(questionStatistic.getFinishQuestionNum());
            }
        });
        return resultList;
    }

    @Override
    @AsyncExport(type = ImportTaskConstant.EXPORT_CHECK_ITEM)
    public Object statisticsColumnPerTableExport(String enterpriseId,
                                                 StatisticsStaColumnRequest request) {
        List<PatrolStoreStatisticsColumnDTO> result = new ArrayList<>();
        Long tableId = request.getTableId();
        Date beginDate = request.getBeginDate();
        Date endDate = request.getEndDate();
        TbMetaTableDO tableDO = tbMetaTableMapper.selectById(enterpriseId, tableId);
        if (tableDO == null) {
            return result;
        }
        List<TbMetaStaTableColumnDO> columnDOList =
                tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Arrays.asList(tableId),Boolean.FALSE);
        List<Long> metaColumnIdList = columnDOList.stream().map(column -> column.getId()).collect(Collectors.toList());
        Map<Long, TbMetaStaTableColumnDO> columnMap =
                columnDOList.stream().collect(Collectors.toMap(data -> data.getId(), data -> data, (a, b) -> a));
        result =
                tbDataStaTableColumnMapper.statisticsColumnPerTable(enterpriseId, metaColumnIdList, beginDate, endDate);
        result.stream().forEach(data -> {
            TbMetaStaTableColumnDO columnDO = columnMap.get(data.getColumnId());
            data.setTableName(tableDO.getTableName());
            data.setCategoryName(columnDO.getCategoryName());
        });
        return result;
    }

    @Override
    public Object statisticsStoreRank(String eid, PatrolStoreStatisticsRegionQuery query) {
        String regionId = query.getRegionId();
        checkRegionId(regionId, null);
        boolean isRoot = regionId.equals(Constants.ROOT_REGION_ID);
        String regionPath = getRegionPath(eid, isRoot, regionId);
        // 获取门店列表
        List<PatrolStoreStatisticsRankDTO> storeList = storeMapper.getStoreByRegionPath(eid, isRoot, regionPath);
        // 获取区域巡店排行
        List<PatrolStoreStatisticsRankDTO> StoreRankList = tbDataStaTableColumnMapper.selectStorePatrolNum(eid, regionPath, isRoot, query.getBeginDate(), query.getEndDate());
        Map<String, Integer> storeIdForCount = StoreRankList.stream().collect(Collectors.toMap(PatrolStoreStatisticsRankDTO::getStoreId, PatrolStoreStatisticsRankDTO::getCount));
        // 填充数量
        storeList.forEach(f -> f.setCount(storeIdForCount.getOrDefault(f.getStoreId(), 0)));
        // 根据数量倒排
        storeList.sort(Comparator.comparing(PatrolStoreStatisticsRankDTO::getCount).reversed());
        return storeList;
    }

    private void checkRegionId(String regionId, String userId) {
        if (StrUtil.isBlank(regionId) && StrUtil.isBlank(userId)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "请选择查询条件");
        }
    }

    private String getRegionPath(String eid, boolean isRoot, String regionId) {
        String regionPath = null;
        if (!isRoot) {
            // 获取区域的路径
            regionPath = regionService.getRegionPathByList(eid, Collections.singletonList(regionId)).get(0).getRegionPath();
        }
        return regionPath;
    }

    @Override
    public List<PatrolStoreStatisticsProblemRankDTO> statisticsStoreProblemRank(String enterpriseId, PatrolStoreStatisticsRegionQuery query, CurrentUser user) {
        List<PatrolStoreStatisticsProblemRankDTO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(query.getRegionIds())) {
            List<String> regionIdList = defaultRegion(enterpriseId, user);
            query.setRegionIds(regionIdList);
        }
        if (CollectionUtils.isEmpty(query.getRegionIds())) {
            return new ArrayList<>();
        }
        // 获取区域的路径
        List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, query.getRegionIds());
        Date startTime = query.getBeginDate();
        Date endTime = query.getEndDate();
        //并发查询各个区域的巡店排行
        Map<String, Future<List<PatrolStoreStatisticsProblemRankDTO>>> tmpMap = new HashMap<>();
        regionPathList.stream().forEach(data -> {
            String regionPath = data.getRegionPath();
            tmpMap.put(data.getRegionId(), EXECUTOR_SERVICE.submit(() -> regionQuestionNumRank(enterpriseId, startTime, endTime, regionPath, user.getDbName())));
        });
        for (String key : tmpMap.keySet()) {
            try {
                result.addAll(tmpMap.get(key).get());
            } catch (Exception e) {
                log.error("区域问题排名异常", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "区域问题排名异常");
            }
        }
        if (CollectionUtils.isEmpty(result)) {
            List<BasicsStoreDTO> baseStoreList = storeMapper.getBaseStoreList(enterpriseId, 10);
            result = baseStoreList.stream().map(e ->{
                PatrolStoreStatisticsProblemRankDTO dto = new PatrolStoreStatisticsProblemRankDTO();
                dto.setStoreName(e.getStoreName());
                dto.setStoreId(e.getStoreId());
                return dto;
            }).collect(Collectors.toList());
            return result;
        }
        return handleRankResult(enterpriseId, result);
    }

    private List<PatrolStoreStatisticsProblemRankDTO> regionQuestionNumRank(String enterpriseId, Date startTime, Date endTime, String regionPath, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<PatrolStoreStatisticsProblemRankDTO> rank = taskStoreMapper.regionQuestionNumRank(enterpriseId, startTime, endTime, regionPath);
        return rank;
    }

    @Override
    public Object storePatrolList(String enterpriseId, PatrolStoreStatisticsRegionQuery query) {

        List<PatrolStoreStatisticsRankDTO> storeList = getStoreList(enterpriseId, query);
        if (CollUtil.isEmpty(storeList)) {
            return PageHelperUtil.getPageInfo(new PageInfo<>(new ArrayList<>()));
        }
        Map<String, Object> pageInfo = PageHelperUtil.getPageInfo(new PageInfo<>(storeList));
        List<String> storeIds = storeList.stream().map(PatrolStoreStatisticsRankDTO::getStoreId).collect(Collectors.toList());
        // 获取门店巡店列表
        List<PatrolStoreStatisticsDTO> storeStatisticsList = tbDataStaTableColumnMapper.selectStorePatrolList(enterpriseId, storeIds, query);

        Map<String, PatrolStoreStatisticsDTO> storeStatisticsMap = storeStatisticsList.stream().collect(Collectors.toMap(PatrolStoreStatisticsDTO::getStoreId, Function.identity()));
        // 填充数量
        List<PatrolStoreStatisticsDTO> result = storeList.stream().map(m -> {
            PatrolStoreStatisticsDTO storeStatistics = storeStatisticsMap.get(m.getStoreId());
            if (storeStatistics == null) {
                storeStatistics = new PatrolStoreStatisticsDTO();
                storeStatistics.setStoreId(m.getStoreId());
            }
            storeStatistics.setStoreName(m.getStoreName());
            return storeStatistics;
        }).collect(Collectors.toList());
        pageInfo.put("list", result);
        return pageInfo;
    }

    private List<PatrolStoreStatisticsRankDTO> getStoreList(String eid, PatrolStoreStatisticsRegionQuery query) {
        String regionId = query.getRegionId();
        String userId = query.getUserId();
        checkRegionId(regionId, userId);
        List<PatrolStoreStatisticsRankDTO> storeList;
        if (StrUtil.isNotBlank(regionId)) {
            boolean isRoot = regionId.equals(Constants.ROOT_REGION_ID);
            String regionPath = getRegionPath(eid, isRoot, regionId);
            // 获取门店列表
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            storeList = storeMapper.getStoreByRegionPath(eid, isRoot, regionPath);
        } else {
            AuthVisualDTO authVisual = authVisualService.authRegionStoreByRole(eid, userId);
            if (!authVisual.getIsAllStore() && CollUtil.isEmpty(authVisual.getStoreIdList())) {
                return new ArrayList<>();
            }
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            storeList = storeMapper.getStoreByStoreIds(eid, authVisual.getIsAllStore(), authVisual.getStoreIdList());
        }

        return storeList;
    }



    @Override
    public PatrolStoreStatisticsHistoryDTO historyByStore(String enterpriseId,
                                                          PatrolStoreStatisticsHistoryQuery query) {
        // 巡店历史统计数据
        List<PatrolStoreStatisticsStoreDTO> patrolStoreStatisticsStoreDTOList =
                tbDataStaTableColumnMapper.patrolStoreStatisticsStore(enterpriseId, Lists.newArrayList(query.getStoreId()),
                        query.getBeginDate(), query.getEndDate());
        if (CollectionUtils.isEmpty(patrolStoreStatisticsStoreDTOList)) {
            return new PatrolStoreStatisticsHistoryDTO();
        }
        PatrolStoreStatisticsStoreDTO patrolStoreStatisticsStoreDTO = patrolStoreStatisticsStoreDTOList.get(0);
        return PatrolStoreStatisticsHistoryDTO.builder().patrolNum(patrolStoreStatisticsStoreDTO.getPatrolNum())
                .totalQuestionNum(patrolStoreStatisticsStoreDTO.getTotalQuestionNum())
                .todoQuestionNum(patrolStoreStatisticsStoreDTO.getTodoQuestionNum()).build();

    }

    @Override
    public StoreOperationDTO storeOperation(String enterpriseId, String storeId) {
        ValidateUtil.validateString(storeId);
        StoreOperationDTO result = new StoreOperationDTO();
        result.setPatrolStoreNum(tbPatrolStoreRecordMapper.patrolStoreNum(enterpriseId, storeId));
        //总工单数
        List<TaskSubVO> allQuestion = taskSubMapper.selectTaskNum(enterpriseId, storeId, TaskTypeEnum.QUESTION_ORDER.getCode(), null);
        Integer allQuestionNum = allQuestion.size();
        result.setQuestionAllNum(allQuestionNum);
        //逾期进行中的工单
        List<TaskSubVO> ongoingQuestion = taskSubMapper.selectTaskNum(enterpriseId, storeId, TaskTypeEnum.QUESTION_ORDER.getCode(), "ongoing");
        Integer ongoingQuestionNum = ongoingQuestion.size();
        //逾期已结束的工单
        List<TaskSubVO> endQuestion = taskSubMapper.selectTaskNum(enterpriseId, storeId, TaskTypeEnum.QUESTION_ORDER.getCode(), "end");
        Integer endQuestionNum = endQuestion.size();
        Integer allOverQuestion = ongoingQuestionNum + endQuestionNum;
        result.setQuestionOverdueNum(allOverQuestion);

        //总任务数
        List<TaskSubVO> allTask = taskSubMapper.selectTaskNum(enterpriseId, storeId, "all", null);
        Integer allTaskNum = allTask.size();
        result.setTaskAllNum(allTaskNum);
        //逾期进行中的工单
        List<TaskSubVO> ongoingTask = taskSubMapper.selectTaskNum(enterpriseId, storeId, "all", "ongoing");
        Integer ongoingTaskNum = ongoingTask.size();
        //逾期已结束的工单
        List<TaskSubVO> endTask = taskSubMapper.selectTaskNum(enterpriseId, storeId, "all", "end");
        Integer endTaskNum = endTask.size();
        Integer allOverTask = ongoingTaskNum + endTaskNum;
        result.setTaskOverdueNum(allOverTask);
        return result;
    }


    @Override
    public Object defaultStorePatrolList(String enterpriseId, PatrolStoreStatisticsRegionQuery query, CurrentUser user) {
        if (AuthRoleEnum.ALL.getCode().equals(user.getRoleAuth())) {
            query.setRegionId(Constants.ROOT_REGION_ID);
            query.setUserId(user.getUserId());
            return this.storePatrolList(enterpriseId, query);
        }
        String regionId = null;
        List<String> storeIds = new ArrayList<>();
        List<UserAuthMappingDO> userAuthMappingList = userAuthMappingMapper.listUserAuthMappingByUserId(enterpriseId, user.getUserId());
        for (UserAuthMappingDO userAuthMappingDO : userAuthMappingList) {
            //取第一个区域
            if (UserAuthMappingTypeEnum.REGION.getCode().equals(userAuthMappingDO.getType())) {
                regionId = userAuthMappingDO.getMappingId();
                break;
            }
            if (UserAuthMappingTypeEnum.STORE.getCode().equals(userAuthMappingDO.getType())) {
                storeIds.add(userAuthMappingDO.getMappingId());
            }
        }
        if (regionId != null) {
            return this.storePatrolList(enterpriseId, query);
        }
        if (CollectionUtils.isNotEmpty(storeIds)) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            List<PatrolStoreStatisticsRankDTO> storeList = storeMapper.getStoreByStoreIds(enterpriseId, Boolean.FALSE, storeIds);
            Map<String, Object> pageInfo = PageHelperUtil.getPageInfo(new PageInfo<>(storeList));
            List<PatrolStoreStatisticsDTO> storeStatisticsList = tbDataStaTableColumnMapper.selectStorePatrolList(enterpriseId, storeIds, query);
            Map<String, PatrolStoreStatisticsDTO> storeStatisticsMap = storeStatisticsList.stream().collect(Collectors.toMap(PatrolStoreStatisticsDTO::getStoreId, Function.identity()));
            // 填充数量
            List<PatrolStoreStatisticsDTO> result = storeList.stream().map(m -> {
                PatrolStoreStatisticsDTO storeStatistics = storeStatisticsMap.get(m.getStoreId());
                if (storeStatistics == null) {
                    storeStatistics = new PatrolStoreStatisticsDTO();
                    storeStatistics.setStoreId(m.getStoreId());
                }
                storeStatistics.setStoreName(m.getStoreName());
                return storeStatistics;
            }).collect(Collectors.toList());
            pageInfo.put("list", result);
            return pageInfo;
        }
        return PageHelperUtil.getPageInfo(new PageInfo<>(new ArrayList<>()));
    }

    @Override
    public List<PatrolStoreStatisticsRankDTO> patrolStoreNumRank(String enterpriseId, PatrolStoreStatisticsRegionQuery request, CurrentUser user) {
        List<PatrolStoreStatisticsRankDTO> result = new ArrayList<>();

        if (CollectionUtils.isEmpty(request.getRegionIds())) {
            List<String> regionIdList = defaultRegion(enterpriseId, user);
            request.setRegionIds(regionIdList);
        }
        if (CollectionUtils.isEmpty(request.getRegionIds())) {
            return new ArrayList<>();
        }
        // 获取区域的路径
        List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, request.getRegionIds());
        Date startTime = request.getBeginDate();
        Date endTime = request.getEndDate();
        //并发查询各个区域的巡店排行
        Map<String, Future<List<PatrolStoreStatisticsRankDTO>>> tmpMap = new HashMap<>();
        regionPathList.stream().forEach(data -> {
            String regionPath = data.getRegionPath();
            tmpMap.put(data.getRegionId(), EXECUTOR_SERVICE.submit(() -> regionPatrolNumRank(enterpriseId, startTime, endTime, regionPath, user.getDbName())));
        });
        for (String key : tmpMap.keySet()) {
            try {
                result.addAll(tmpMap.get(key).get());
            } catch (Exception e) {
                log.error("区域巡店排名异常", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "区域巡店排名异常");
            }
        }
        if (CollectionUtils.isEmpty(result)) {
            return result;
        }
        return handleRankResult(enterpriseId, result);
    }

    @Override
    public PatrolStoreTypeStatisticsDTO statisticsPatrolType(String enterpriseId, PatrolStoreStatisticsRegionQuery query, CurrentUser user) {
        PatrolStoreTypeStatisticsDTO result = new PatrolStoreTypeStatisticsDTO();
        if (CollectionUtils.isEmpty(query.getRegionIds())) {
            List<String> regionIdList = defaultRegion(enterpriseId, user);
            query.setRegionIds(regionIdList);
        }
        // 获取区域的路径
        List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, query.getRegionIds());
        Date startTime = query.getBeginDate();
        Date endTime = query.getEndDate();
        //并发查询各个区域
        Map<String, Future<PatrolStoreTypeStatisticsDTO>> tmpMap = new HashMap<>();
        regionPathList.stream().forEach(data -> {
            String regionPath = data.getRegionPath();
            tmpMap.put(data.getRegionId(), EXECUTOR_SERVICE.submit(() -> statisticsPatrolTypeNum(enterpriseId, startTime, endTime, regionPath, user.getDbName(), query.getPatrolType())));
        });
        for (String key : tmpMap.keySet()) {
            try {
                PatrolStoreTypeStatisticsDTO tmp = tmpMap.get(key).get();
                result.setTotalNum(result.getTotalNum() + tmp.getTotalNum());
                result.setTaskNum(result.getTaskNum() + tmp.getTaskNum());
            } catch (Exception e) {
                log.error("区域巡店排名异常", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "区域巡店排名异常");
            }
        }
        return result;
    }

    private PatrolStoreTypeStatisticsDTO statisticsPatrolTypeNum(String enterpriseId, Date startTime, Date endTime, String regionPath, String dbName, String patrolType) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        PatrolStoreTypeStatisticsDTO result = tbPatrolStoreRecordMapper.statisticsPatrolTypeNum(enterpriseId, startTime, endTime, regionPath, patrolType);
        return result;
    }

    @Override
    public PatrolStoreTaskStatisticsDTO statisticsPatrolTask(String enterpriseId, PatrolStoreStatisticsRegionQuery query, CurrentUser user) {
        PatrolStoreTaskStatisticsDTO result = new PatrolStoreTaskStatisticsDTO();
        if (CollectionUtils.isEmpty(query.getRegionIds())) {
            List<String> regionIdList = defaultRegion(enterpriseId, user);
            query.setRegionIds(regionIdList);
        }
        // 获取区域的路径
        List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, query.getRegionIds());
        Date startTime = query.getBeginDate();
        Date endTime = query.getEndDate();
        //并发查询各个区域
        Map<String, Future<PatrolStoreTaskStatisticsDTO>> tmpMap = new HashMap<>();
        regionPathList.stream().forEach(data -> {
            String regionPath = data.getRegionPath();
            tmpMap.put(data.getRegionId(), EXECUTOR_SERVICE.submit(() -> statisticsPatrolTaskNum(enterpriseId, startTime, endTime, regionPath, user.getDbName(), query.getPatrolType())));
        });
        for (String key : tmpMap.keySet()) {
            try {
                PatrolStoreTaskStatisticsDTO tmp = tmpMap.get(key).get();
                result.setTotalNum(result.getTotalNum() + tmp.getTotalNum());
                result.setOnTimeNum(result.getOnTimeNum() + tmp.getOnTimeNum());
            } catch (Exception e) {
                log.error("区域巡店排名异常", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "区域巡店排名异常");
            }
        }
        return result;
    }

    @Override
    public PatrolStoreStatisticsRegionDTO regionsSummary(String enterpriseId, PatrolStoreStatisticsRegionQuery query, CurrentUser user) {

        List<String> regionIds = query.getRegionIds();
        if (CollUtil.isEmpty(regionIds)) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "请选择需要查询的区域");
        }
        // 如果不是查询子区域列表 则限制每次最多20条
        if (!query.isGetChild() && regionIds.size() > 20) {
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "所选区域不能超过20个");
        }
        Date beginDate = query.getBeginDate();
        Date endDate = query.getEndDate();
        // 获取区域的路径
        List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, regionIds);
        // 区域统计异步缓存
        Map<String, Future<PatrolStoreStatisticsRegionDTO>> idTaskMap = new HashMap<>();
        PatrolStoreStatisticsRegionDTO result = new PatrolStoreStatisticsRegionDTO();
        // 异步处理统计数据
        for (RegionPathDTO region : regionPathList) {
            String regionId = region.getRegionId();
            String regionPath = region.getRegionPath();
            boolean isRoot = regionId.equals(Constants.ROOT_REGION_ID);
            idTaskMap.put(regionId,
                    EXECUTOR_SERVICE.submit(() -> getRegionStatistics(enterpriseId, isRoot, regionPath, beginDate, endDate, user, null)));
        }
        // 获取结果
        for (RegionPathDTO region : regionPathList) {
            String regionId = region.getRegionId();
            Future<PatrolStoreStatisticsRegionDTO> future = idTaskMap.get(regionId);
            try {
                // 获取统计信息
                PatrolStoreStatisticsRegionDTO statisticsRegion = future.get();
                int consciousPatrolNum = statisticsRegion.getConsciousPatrolNum() + result.getConsciousPatrolNum();
                int patrolNum = statisticsRegion.getPatrolNum() + result.getPatrolNum();
                int storeNum = region.getStoreNum() + result.getStoreNum();
                int patrolStoreNum = statisticsRegion.getPatrolStoreNum() + result.getPatrolStoreNum();
                int patrolPersonNum = statisticsRegion.getPatrolPersonNum() + result.getPatrolPersonNum();
                int taskPatrolNum = statisticsRegion.getTaskPatrolNum() + result.getTaskPatrolNum();
                int totalQuestionNum = statisticsRegion.getTotalQuestionNum() + result.getTotalQuestionNum();
                int todoQuestionNum = statisticsRegion.getTodoQuestionNum() + result.getTodoQuestionNum();
                int unRecheckQuestionNum = statisticsRegion.getUnRecheckQuestionNum() + result.getUnRecheckQuestionNum();
                int finishQuestionNum = statisticsRegion.getFinishQuestionNum() + result.getUnRecheckQuestionNum();
                result.setConsciousPatrolNum(consciousPatrolNum);
                result.setStoreNum(storeNum);
                result.setPatrolNum(patrolNum);
                result.setPatrolPersonNum(patrolPersonNum);
                result.setUnRecheckQuestionNum(unRecheckQuestionNum);
                result.setTaskPatrolNum(taskPatrolNum);
                result.setTodoQuestionNum(todoQuestionNum);
                result.setUnRecheckQuestionNum(unRecheckQuestionNum);
                result.setFinishQuestionNum(finishQuestionNum);
                result.setTotalQuestionNum(totalQuestionNum);
                result.setPatrolStoreNum(patrolStoreNum);
            } catch (Exception e) {
                log.error("统计区域出错：", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "获取统计数据异常");
            }
        }
        return result;
    }

    @Override
    @AsyncExport(type = ImportTaskConstant.EXPORT_REGION)
    public Object statisticsRegionExport(String enterpriseId, PatrolStoreStatisticsRegionQuery query) {
        return this.statisticsRegion(enterpriseId, query);
    }

    @Override
    public ImportTaskDO taskStageRecordListExport(String enterpriseId, PatrolStoreStatisticsDataTableQuery query) {
        Long totalNum = tbPatrolStoreRecordMapper.statisticsDataTableCount(enterpriseId, query,  null);
        if(totalNum == null || totalNum == 0){
            throw new ServiceException("当前无记录可导出");
        }
        if(totalNum > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE +"条，请缩小导出范围");
        }
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_TASK_STAGE_LIST_RECORD);
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_TASK_STAGE_LIST_RECORD);

        MsgUniteData msgUniteData = new MsgUniteData();
        ExportTaskStageRecordListRequest msg = new ExportTaskStageRecordListRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(query);
        msg.setTotalNum(totalNum);
        msg.setImportTaskDO(importTaskDO);
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_TASK_STAGE_LIST_RECORD.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public ImportTaskDO taskStageRecordDetailListExport(String enterpriseId, Long businessId, String dbName){
        TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, businessId);
        if(recordDO == null){
            throw new ServiceException("巡店记录不存在");
        }
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_TASK_STAGE_LIST_RECORD_DETAIL);
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_TASK_STAGE_LIST_RECORD_DETAIL);
        importTaskDO.setFileName(recordDO.getTaskName() + "_" + recordDO.getStoreName() + "_" + fileName);

        MsgUniteData msgUniteData = new MsgUniteData();
        ExportTaskStageRecordListDetailRequest msg = new ExportTaskStageRecordListDetailRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setBusinessId(businessId);
        msg.setTotalNum(Constants.MAX_EXPORT_SIZE);
        msg.setDbName(dbName);
        msg.setImportTaskDO(importTaskDO);
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.EXPORT_TASK_STAGE_LIST_RECORD_DETAIL.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public PageInfo<PatrolStoreStatisticsDataDefTableDTO> regionQuestionList(String enterpriseId, PatrolStoreStatisticsDataStaColumnQuery query) {
        // 根据regionId获取regionPath
        String regionPathLeft = null;
        Long regionId = query.getRegionId();
        if (regionId != null) {
            regionPathLeft = StringUtils.substringBeforeLast(
                    regionService.getRegionPath(enterpriseId, String.valueOf(query.getRegionId())), "]");
        }
        // 检查项分页查询
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        Date beginDate = query.getBeginDate();
        Date endDate = query.getEndDate();
        List<TaskStoreDO> taskStoreList = taskStoreMapper.getTaskStoreByRegionPathOrStoreId(enterpriseId, regionPathLeft, null, beginDate, endDate,Boolean.FALSE,null,null, null, null, null, null);
        PageInfo result = new PageInfo<>(taskStoreList);
        if (CollectionUtils.isEmpty(taskStoreList)) {
            return result;
        }
        List<PatrolStoreStatisticsStaColumnInfoDTO> resultList = new ArrayList<>();
        List<Long> questionIs = taskStoreList.stream().map(data -> data.getUnifyTaskId()).collect(Collectors.toList());
        List<TbDataStaTableColumnDO> dataStaColumnDOList =
                tbDataStaTableColumnMapper.getListByQuestionIds(enterpriseId, questionIs);
        if (CollectionUtils.isNotEmpty(dataStaColumnDOList)){
            resultList.addAll(statisticsStaColumnData(enterpriseId, dataStaColumnDOList));
        }
        taskStoreList = taskStoreList.stream().filter(data -> !dataStaColumnDOList.stream().anyMatch(data1 -> data1.getTaskQuestionId().equals(data.getUnifyTaskId()))).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(taskStoreList)){
            resultList.addAll(wrapQuestionList(enterpriseId, taskStoreList));
        }
        result.setList(resultList);
        return result;
    }

    private List<PatrolStoreStatisticsStaColumnInfoDTO> wrapQuestionList(String enterpriseId, List<TaskStoreDO> taskStoreList) {
        List<Long> questionIdList = taskStoreList.stream().map(data -> data.getUnifyTaskId()).collect(Collectors.toList());
        //1.根据工单id查询metaColumnId
        List<TaskMappingDO> mappingDOList = taskMappingMapper.selectMappingByTaskIds(enterpriseId, questionIdList);
        //map:questionId -> metaColumnId
        Map<Long, Long> taskIdMetaColumnIdMap = mappingDOList.stream()
                .filter(a -> a.getUnifyTaskId() != null && a.getOriginMappingId() != null)
                .collect(Collectors.toMap(data -> data.getUnifyTaskId(), data -> data.getOriginMappingId(), (a, b) -> a));
        List<Long> metaColumnIds = mappingDOList.stream().map(data -> data.getOriginMappingId()).collect(Collectors.toList());
        List<TbMetaStaTableColumnDO> metaColumnList = tbMetaStaTableColumnMapper.selectByIds(enterpriseId, metaColumnIds);
        Map<Long,TbMetaStaTableColumnDO> metaColumnMap = metaColumnList.stream().collect(Collectors.toMap(data -> data.getId(),data -> data,(a,b)->a));
        //2.查询检查表详情
        List<Long> metaTableIds = metaColumnList.stream().map(data -> data.getMetaTableId()).collect(Collectors.toList());
        List<TbMetaTableDO> metaTableList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIds);
        //map:tableId -> table
        Map<Long, TbMetaTableDO> tableMap = metaTableList.stream().collect(Collectors.toMap(data -> data.getId(), data -> data, (a, b) -> a));
        //3.根据区域id查询区域名称
        List<Long> regionIds = taskStoreList.stream().map(data -> data.getRegionId()).collect(Collectors.toList());
        List<RegionDO> regionDOList = regionMapper.listRegionByIds(enterpriseId, regionIds);
        Map<Long, String> regionMap = regionDOList.stream()
                .filter(a -> a.getId() != null && a.getName() != null)
                .collect(Collectors.toMap(data -> data.getId(), data -> data.getName(), (a, b) -> a));
        //4.查询门店
        List<String> storeIds = taskStoreList.stream().map(data -> data.getStoreId()).collect(Collectors.toList());
        List<StoreDO> storeList = storeMapper.getByStoreIdList(enterpriseId, storeIds);
        //map:storeId -> storeName
        Map<String, String> storeMap = storeList.stream()
                .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                .collect(Collectors.toMap(data -> data.getStoreId(), data -> data.getStoreName(), (a, b) -> a));
        List<QuestionReportDTO> questionReportDTOList =
                questionOrderTaskService.getQuestionReportData(enterpriseId, questionIdList);
        Map<Long, QuestionReportDTO> taskIdQuestionReportMap = questionReportDTOList.stream()
                .collect(Collectors.toMap(QuestionReportDTO::getUnifyTaskId, Function.identity(), (a, b) -> a));

        return taskStoreList.stream().map(data -> {
            QuestionReportDTO questionReportDTO = taskIdQuestionReportMap.get(data.getUnifyTaskId());
            TbMetaStaTableColumnDO columnDO = metaColumnMap.get(taskIdMetaColumnIdMap.get(data.getUnifyTaskId()));
            PatrolStoreStatisticsStaColumnInfoDTO build = PatrolStoreStatisticsStaColumnInfoDTO.builder()
                    .questionPics(CollectionUtils.isEmpty(questionReportDTO.getQuestionPhoto()) ? null
                            : String.join(",", questionReportDTO.getQuestionPhoto()))
                    .handlePics(CollectionUtils.isEmpty(questionReportDTO.getHandlePhoto()) ? null
                            : String.join(",", questionReportDTO.getHandlePhoto()))
                    .handleDoneTime(
                            questionReportDTO.getCompleteTime() == null ? null : new Date(questionReportDTO.getCompleteTime()))
                    .overdue(questionReportDTO.getOverdueCompleteFlag())
                    .questionStatus(questionReportDTO.getStatus())
                    .checkUserName(questionReportDTO.getCreateUserName())
                    .handleUserName(questionReportDTO.getHandleUserName())
                    .reCheckUserName(questionReportDTO.getRecheckUserName())
                    .taskDesc(questionReportDTO.getTaskDesc())
                    .storeName(storeMap.get(data.getStoreId()))
                    .regionName(regionMap.get(data.getRegionId()))
                    .build();
            if(columnDO != null){
                build.setCategoryName(columnDO.getCategoryName());
                build.setColumnName(columnDO.getColumnName());
                build.setStandardPic(columnDO.getStandardPic());
                build.setDescription(columnDO.getDescription());
                TbMetaTableDO tableDO = tableMap.get(columnDO.getMetaTableId());
                build.setTableName(tableDO == null?"":tableDO.getTableName());
            }
            return build;
        }).collect(Collectors.toList());
    }

    private PatrolStoreTaskStatisticsDTO statisticsPatrolTaskNum(String enterpriseId, Date startTime, Date endTime, String regionPath, String dbName, String patrolType) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        PatrolStoreTaskStatisticsDTO result = tbPatrolStoreRecordMapper.statisticsPatrolTask(enterpriseId, startTime, endTime, regionPath, patrolType);
        return result;
    }

    private List<String> defaultRegion(String enterpriseId, CurrentUser user) {
        List<RegionNodeDTO> region = regionService.regionStoreList(enterpriseId, null, user, Boolean.FALSE, Boolean.FALSE).getRegionList();
        List<String> regionIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(region)) {
            int end = region.size() > 10 ? 9 : region.size() - 1;
            regionIdList = region.subList(0, end).stream().map(data -> data.getRegion().getId().toString()).collect(Collectors.toList());
        }
        return regionIdList;
    }

    private <T extends PatrolStoreStatisticsRankDTO> List<T> handleRankResult(String enterpriseId, List<T> result) {
        //内存排序
        Collections.sort(result, (o1, o2) -> o2.getCount() - o1.getCount());
        int lastIndex = result.size() < 10 ? result.size() : 10;
        result = result.subList(0, lastIndex);
        List<Long> regionId = result.stream().map(data -> data.getRegionId()).collect(Collectors.toList());

        List<String> storeIds = result.stream().map(data -> data.getStoreId()).collect(Collectors.toList());
        List<StoreDO> stores = storeMapper.getStoresByStoreIds(enterpriseId, storeIds);
        Map<String, String> storeIdNameMap = stores.stream()
                .filter(a -> a.getStoreId() != null && a.getStoreName() != null)
                .collect(Collectors.toMap(data -> data.getStoreId(), data -> data.getStoreName(), (a, b) -> a));
        //查询区域名称
        List<RegionDO> regionList = regionMapper.listRegionByIds(enterpriseId, regionId);
        //region : id -> name
        Map<Long, String> regionIdNameMap = regionList.stream()
                .filter(a -> a.getId() != null && a.getName() != null)
                .collect(Collectors.toMap(data -> data.getId(), data -> data.getName(), (a, b) -> a));
        result.stream().forEach(data -> {
            data.setRegionName(regionIdNameMap.get(data.getRegionId()));
            data.setStoreName(storeIdNameMap.get(data.getStoreId()));
        });
        return result;
    }

    private List<PatrolStoreStatisticsRankDTO> regionPatrolNumRank(String enterpriseId, Date startTime, Date endTime, String regionPath, String dbName) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        List<PatrolStoreStatisticsRankDTO> rank = tbPatrolStoreRecordMapper.regionPatrolNumRank(enterpriseId, startTime, endTime, regionPath);
        return rank;
    }

    @Override
    public PatrolStoreStatisticsTableVO getCheckedStore(String enterpriseId, PatrolStoreStatisticsTableQuery query, CurrentUser user) {
        if(CollectionUtils.isEmpty(query.getRegionIds()) && CollectionUtils.isEmpty(query.getStoreIds())){
            log.error("门店和区域为空");
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店和区域为空");
        }
        if(CollectionUtils.isNotEmpty(query.getRegionIds()) && CollectionUtils.isNotEmpty(query.getStoreIds())){
            log.error("不能同时选择区域和门店，RegionIds：{};StoreIds:{}", query.getRegionIds(),query.getStoreIds());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "不能同时选择区域和门店");
        }
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        PatrolStoreStatisticsTableVO resultVo = new PatrolStoreStatisticsTableVO();
        TbMetaTableDO table = tbMetaTableMapper.selectById(enterpriseId,query.getMetaTableId());
        resultVo.setTableName(table.getTableName());
        TbMetaColumnResultDO tbMetaColumnResultDo = tbMetaColumnResultMapper.selectIdByMetaTableId(enterpriseId,query.getMetaTableId());
        MetaTablePropertyEnum tablePropertyEnum = MetaTablePropertyEnum.getTablePropertyEnum(table.getTableProperty());
        if (tablePropertyEnum!=null){
            resultVo.setTableProperty(tablePropertyEnum.getName());
        }
        if(tbMetaColumnResultDo != null && tbMetaColumnResultDo.getId() != null){
            resultVo.setTableType("高级");
        }else {
            resultVo.setTableType("标准");
        }
        //检查项数
        Integer staColumnNum = tbMetaStaTableColumnMapper.countByMetaTableId(enterpriseId, Collections.singletonList(query.getMetaTableId()));
        resultVo.setStaColumnNum(staColumnNum);
        List<PatrolStoreStatisticsTableDTO> result = new ArrayList<>();
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        //并发查询
        Map<String, Future<PatrolStoreStatisticsTableDTO>> futureMap = new HashMap<>();
        List<RegionPathDTO> regionPathList = new ArrayList<>();
        Map<String, RegionPathDTO> regionPathMap = new HashMap<>();
        List<PatrolStoreStatisticsRankDTO> storeList;
        Map<String, PatrolStoreStatisticsRankDTO> storeMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(query.getRegionIds())){
            //region 支持单选 所以查询单个区域下的子区域或者门店 最多10个
            if(query.getRegionIds().size() == Constants.INDEX_ONE){
                PageHelper.startPage(Constants.INDEX_ONE, Constants.PAGE_SIZE_TEN);
                List<RegionChildDTO> regionByParentId = regionMapper.getRegionByParentId(enterpriseId, query.getRegionIds(), Boolean.FALSE);
                if(CollectionUtils.isNotEmpty(regionByParentId)){
                    query.setRegionIds(regionByParentId.stream().map(RegionChildDTO::getId).filter(o->!FixedRegionEnum.getExcludeRegionId().contains(o)).collect(Collectors.toList()));
                }
            }
            // 获取区域的路径
            regionPathList = regionService.getRegionPathByList(enterpriseId, query.getRegionIds());
            regionPathMap = ListUtils.emptyIfNull(regionPathList).stream()
                    .collect(Collectors.toMap(RegionPathDTO::getRegionPath, data -> data, (a, b) -> a));
            regionPathList.stream().forEach(data -> futureMap.put(data.getRegionPath(),EXECUTOR_SERVICE.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(user.getDbName());
                return tbDataTableMapper.getCheckedStore(enterpriseId,data.getRegionPath(),null,query);
            })));
        }else if(CollectionUtils.isNotEmpty(query.getStoreIds())){
            //获得门店名称
            storeList = storeMapper.getStoreByStoreIds(enterpriseId,false,query.getStoreIds());
            storeMap = ListUtils.emptyIfNull(storeList).stream()
                    .collect(Collectors.toMap(PatrolStoreStatisticsRankDTO::getStoreId, data -> data, (a, b) -> a));
            query.getStoreIds().stream().forEach(data -> futureMap.put(data,EXECUTOR_SERVICE.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(user.getDbName());
                return tbDataTableMapper.getCheckedStore(enterpriseId,null,data,query);
            })));
        }
        //区域/门店对比
        List<PatrolStoreStatisticsTableRankDTO> rankDtoList = new ArrayList<>();
        PatrolStoreStatisticsTableRankDTO rankDto;
        for (Map.Entry<String, Future<PatrolStoreStatisticsTableDTO>> entry : futureMap.entrySet()) {
            try {
                String key = entry.getKey();
                //区域/门店对比对象封装
                PatrolStoreStatisticsTableDTO dto = entry.getValue().get();
                dto.setRegionPath(key);
                rankDto = new PatrolStoreStatisticsTableRankDTO();
                if(CollectionUtils.isNotEmpty(query.getRegionIds())){
                    if(regionPathMap != null && regionPathMap.size() != 0 && regionPathMap.get(key) != null){
                        rankDto.setRegionName(regionPathMap.get(key).getRegionName());
                    }
                }else if(CollectionUtils.isNotEmpty(query.getStoreIds())){
                    rankDto.setStoreName(storeMap.get(key).getStoreName());
                }
                //平均分等于总分/检查次数
                BigDecimal score = new BigDecimal(0);
                if (!Constants.INDEX_ZERO.equals(dto.getCheckedTimes())){
                   score = dto.getSumScore().divide(new BigDecimal(dto.getCheckedTimes()), Constants.SCALE,BigDecimal.ROUND_HALF_UP);
                }
                rankDto.setScore(score);

                rankDtoList.add(rankDto);
                result.add(dto);
            } catch (Exception e) {
                log.error("检查表详情检查门店数统计异常", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "检查表详情检查门店数统计异常");
            }
        }
        //区域/门店对比
        resultVo.setScoreList(new ArrayList<>(rankDtoList));
        //区域/门店排行 分数排序
        resultVo.setScoreRankList(rankDtoList.stream().sorted(Comparator.comparing(PatrolStoreStatisticsTableRankDTO::getScore).reversed()).collect(Collectors.toList()));
        //所有的门店路径
        List<String> allPathList = regionPathList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
        //获取重复的门店路径
        Set<String> repeatPathSet = DistinctRegionPathUtil.getRepeatRegionPath(allPathList);
        Integer totalStoreNum;
        if(CollectionUtils.isNotEmpty(query.getRegionIds())){
            totalStoreNum= storeMapper.selectStoreCountByRegionPathList(enterpriseId, allPathList);
            if(query.getRegionIds().size() == Constants.INDEX_ONE){
                RegionDO queryRegion = regionMapper.getByRegionId(enterpriseId, Long.valueOf(query.getRegionIds().get(0)));
                totalStoreNum = queryRegion.getStoreNum();
            }
//            //从所有门店路径过滤掉重复路径然后统计总数
//            List<RegionPathDTO> tempRegionPathList = regionPathList.stream().filter(e -> !repeatPathSet.contains(e.getRegionPath())).collect(Collectors.toList());
//            //总门店数
//            totalStoreNum =  tempRegionPathList.stream().mapToInt(RegionPathDTO::getStoreNum).sum();
        }else{
            totalStoreNum = query.getStoreIds().size();
        }
        resultVo.setTotalStoreNum(totalStoreNum);
        if(CollectionUtils.isNotEmpty(result)){
            //检查门店数
            Integer checkedStore;
            //总分数
            BigDecimal sumScore;
            //检查次数
            Integer checkTimes;
            if(result.size() > 1){
                //从所有门店路径过滤掉重复路径然后统计总数
                List<PatrolStoreStatisticsTableDTO> tempSumList = result.stream().filter(e -> !repeatPathSet.contains(e.getRegionPath())).collect(Collectors.toList());

                //检查门店数
                checkedStore =  tempSumList.stream().mapToInt(PatrolStoreStatisticsTableDTO::getCheckedStore).sum();
                //总分数
                sumScore =  tempSumList.stream().map(PatrolStoreStatisticsTableDTO::getSumScore).reduce(BigDecimal.ZERO,BigDecimal::add);
                //检查次数
                checkTimes =  tempSumList.stream().mapToInt(PatrolStoreStatisticsTableDTO::getCheckedTimes).sum();

            }else {
                PatrolStoreStatisticsTableDTO tempDto = result.get(0);
                checkedStore = tempDto.getCheckedStore();
                sumScore = tempDto.getSumScore();
                checkTimes = tempDto.getCheckedTimes();
            }
            resultVo.setCheckedStore(checkedStore);
            resultVo.setCheckedTimes(checkTimes);
            resultVo.setCheckRatio(reserveDouble(divide(checkedStore,totalStoreNum)));
            resultVo.setAvgCheckedTimes(twoDecimal(divide(checkTimes,totalStoreNum)));
            BigDecimal avgScore = new BigDecimal(0);
            if (!Constants.INDEX_ZERO.equals(checkTimes)){
                avgScore = sumScore.divide(new BigDecimal(checkTimes),2,BigDecimal.ROUND_HALF_UP);
            }
            resultVo.setAvgScore(avgScore);
        }
        return resultVo;
    }

    @Override
    public PatrolStoreStatisticsTableLeLeTeaVO getLeLeTeaCheckedStore(String enterpriseId, PatrolStoreStatisticsTableQuery query, CurrentUser user) {

        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO storeCheckSettingDO =
                storeCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        DataSourceHelper.changeToMy();
        //校验参数
        if(CollectionUtils.isEmpty(query.getRegionIds()) && CollectionUtils.isEmpty(query.getStoreIds())){
            log.error("门店和区域为空");
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店和区域为空");
        }
        if(CollectionUtils.isNotEmpty(query.getRegionIds()) && CollectionUtils.isNotEmpty(query.getStoreIds())){
            log.error("不能同时选择区域和门店，RegionIds：{};StoreIds:{}", query.getRegionIds(),query.getStoreIds());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "不能同时选择区域和门店");
        }
        //切库
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        List<String> regionIds = query.getRegionIds();
        //检查表报表详情
        PatrolStoreStatisticsTableLeLeTeaVO resultVo = new PatrolStoreStatisticsTableLeLeTeaVO();
        //查询【检查表报表详情】的【检查表名称】
        TbMetaTableDO table = tbMetaTableMapper.selectById(enterpriseId,query.getMetaTableId());
        resultVo.setTableName(table.getTableName());
        //查询【标准检查项评价项配置表】信息
        TbMetaColumnResultDO tbMetaColumnResultDo = tbMetaColumnResultMapper.selectIdByMetaTableId(enterpriseId,query.getMetaTableId());
        //获取表属性（七种类型）
        MetaTablePropertyEnum tablePropertyEnum = MetaTablePropertyEnum.getTablePropertyEnum(table.getTableProperty());
        //校验表属性
        if (tablePropertyEnum!=null){
            resultVo.setTableProperty(tablePropertyEnum.getName());
        }
        //校验【标准检查项评价项配置表】信息
        if(tbMetaColumnResultDo != null && tbMetaColumnResultDo.getId() != null){
            resultVo.setTableType("高级");
        }else {
            resultVo.setTableType("标准");
        }
        //检查项数
        Integer staColumnNum = tbMetaStaTableColumnMapper.countByMetaTableId(enterpriseId, Collections.singletonList(query.getMetaTableId()));
        //设置【检查表报表详情】的【检查项数】
        resultVo.setStaColumnNum(staColumnNum);
        //----copy----
        List<PatrolStoreStatisticsTableDTO> result = new ArrayList<>();
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        //并发查询
        Map<String, Future<PatrolStoreStatisticsTableDTO>> futureMap = new HashMap<>();
        List<RegionPathDTO> regionPathList = new ArrayList<>();
        Map<String, RegionPathDTO> regionPathMap = new HashMap<>();
        List<PatrolStoreStatisticsRankDTO> storeList;
        Map<String, PatrolStoreStatisticsRankDTO> storeMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(query.getRegionIds())){
            //region 支持单选 所以查询单个区域下的子区域或者门店 最多10个
            if(query.getRegionIds().size() == Constants.INDEX_ONE){
                PageHelper.startPage(Constants.INDEX_ONE, query.getPageSize());
                List<RegionChildDTO> regionByParentId = regionMapper.getRegionByParentId(enterpriseId, query.getRegionIds(), Boolean.FALSE);
                RegionChildDTO rootRegion = new RegionChildDTO();
                for (String regionId : query.getRegionIds()) {
                    rootRegion.setId(regionId);
                }
                regionByParentId.add(rootRegion);
                if(CollectionUtils.isNotEmpty(regionByParentId)){
                    query.setRegionIds(regionByParentId.stream().map(RegionChildDTO::getId).collect(Collectors.toList()));
                }
            }
            // 获取区域的路径
            regionPathList = regionService.getRegionPathByList(enterpriseId, query.getRegionIds());
            regionPathMap = ListUtils.emptyIfNull(regionPathList).stream()
                    .collect(Collectors.toMap(RegionPathDTO::getRegionPath, data -> data, (a, b) -> a));
            regionPathList.stream().forEach(data -> futureMap.put(data.getRegionPath(),EXECUTOR_SERVICE.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(user.getDbName());
                return tbDataTableMapper.getCheckedStore(enterpriseId,data.getRegionPath(),null,query);
            })));
        }else if(CollectionUtils.isNotEmpty(query.getStoreIds())){
            //获得门店名称
            storeList = storeMapper.getStoreByStoreIds(enterpriseId,false,query.getStoreIds());
            storeMap = ListUtils.emptyIfNull(storeList).stream()
                    .collect(Collectors.toMap(PatrolStoreStatisticsRankDTO::getStoreId, data -> data, (a, b) -> a));
            query.getStoreIds().stream().forEach(data -> futureMap.put(data,EXECUTOR_SERVICE.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(user.getDbName());
                return tbDataTableMapper.getCheckedStore(enterpriseId,null,data,query);
            })));
        }
        //区域/门店对比
        List<PatrolStoreStatisticsTableRankLeLeTeaDTO> rankDtoList = new ArrayList<>();
        //乐乐茶特有
        PatrolStoreStatisticsTableRankLeLeTeaDTO rankDto = null;
        for (Map.Entry<String, Future<PatrolStoreStatisticsTableDTO>> entry : futureMap.entrySet()) {
            try {
                String key = entry.getKey();
                //区域/门店对比对象封装
                PatrolStoreStatisticsTableDTO dto = entry.getValue().get();
                dto.setRegionPath(key);
                rankDto = new PatrolStoreStatisticsTableRankLeLeTeaDTO();
                if(CollectionUtils.isNotEmpty(query.getRegionIds())){
                    if(regionPathMap != null && regionPathMap.size() != 0 && regionPathMap.get(key) != null){
                        rankDto.setRegionName(regionPathMap.get(key).getRegionName());
                        rankDto.setRegionId(regionPathMap.get(key).getRegionId());
                    }
                }else if(CollectionUtils.isNotEmpty(query.getStoreIds())){
                    rankDto.setStoreName(storeMap.get(key).getStoreName());
                    rankDto.setRegionId(String.valueOf(storeMap.get(key).getRegionId()));
                }

                //-----------------查出巡店记录表和分数--------------------
                BigDecimal score = new BigDecimal(0);
                if (!Constants.INDEX_ZERO.equals(dto.getCheckedTimes())){
                    score = dto.getPercent();
                }
                rankDto.setScore(score);

                rankDtoList.add(rankDto);
                result.add(dto);

            } catch (Exception e) {
                log.error("检查表详情检查门店数统计异常", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "检查表详情检查门店数统计异常");
            }
        }

        //区域/门店对比
        resultVo.setScoreList(new ArrayList<>(rankDtoList));
        //区域/门店排行 分数排序
        resultVo.setScoreRankList(rankDtoList.stream().sorted(Comparator.comparing(PatrolStoreStatisticsTableRankLeLeTeaDTO::getRealScore).reversed()).collect(Collectors.toList()));
        //所有的门店路径
        List<String> allPathList = regionPathList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
        //获取重复的门店路径
        Set<String> repeatPathSet = DistinctRegionPathUtil.getRepeatRegionPath(allPathList);
        Integer totalStoreNum;
        if(CollectionUtils.isNotEmpty(query.getRegionIds())){
            //从所有门店路径过滤掉重复路径然后统计总数
            List<RegionPathDTO> tempRegionPathList = regionPathList.stream().filter(e -> !repeatPathSet.contains(e.getRegionPath())).collect(Collectors.toList());
            //总门店数
            totalStoreNum =  tempRegionPathList.stream().mapToInt(RegionPathDTO::getStoreNum).sum();
        }else{
            totalStoreNum = query.getStoreIds().size();
        }
        resultVo.setTotalStoreNum(totalStoreNum);
        if(CollectionUtils.isNotEmpty(result)){
            //检查门店数
            Integer checkedStore;
            //总分数
            BigDecimal sumScore;
            //检查次数
            Integer checkTimes;
            if(result.size() > 1){
                //从所有门店路径过滤掉重复路径然后统计总数
                List<PatrolStoreStatisticsTableDTO> tempSumList = result.stream().filter(e -> !repeatPathSet.contains(e.getRegionPath())).collect(Collectors.toList());

                //检查门店数
                checkedStore =  tempSumList.stream().mapToInt(PatrolStoreStatisticsTableDTO::getCheckedStore).sum();
                //总分数
                sumScore =  tempSumList.stream().map(PatrolStoreStatisticsTableDTO::getSumScore).reduce(BigDecimal.ZERO,BigDecimal::add);
                //检查次数
                checkTimes =  tempSumList.stream().mapToInt(PatrolStoreStatisticsTableDTO::getCheckedTimes).sum();

            }else {
                PatrolStoreStatisticsTableDTO tempDto = result.get(0);
                checkedStore = tempDto.getCheckedStore();
                sumScore = tempDto.getSumScore();
                checkTimes = tempDto.getCheckedTimes();
            }
            resultVo.setCheckedStore(checkedStore);
            resultVo.setCheckedTimes(checkTimes);
            resultVo.setCheckRatio(reserveDouble(divide(checkedStore,totalStoreNum)));
            resultVo.setAvgCheckedTimes(twoDecimal(divide(checkTimes,totalStoreNum)));
            BigDecimal avgScore = new BigDecimal(0);
            if (!Constants.INDEX_ZERO.equals(checkTimes)){
                avgScore = sumScore.divide(new BigDecimal(checkTimes),2,BigDecimal.ROUND_HALF_UP);
            }
            resultVo.setAvgScore(avgScore);
        }
        return resultVo;
    }

    @Override
    public PatrolStoreStatisticsTableGradeVO getPatrolResultProportion(String enterpriseId, PatrolStoreStatisticsTableQuery query, CurrentUser user) {
        if(CollectionUtils.isNotEmpty(query.getRegionIds()) && CollectionUtils.isNotEmpty(query.getStoreIds())){
            log.error("不能同时选择区域和门店，RegionIds：{};StoreIds:{}", query.getRegionIds(),query.getStoreIds());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "不能同时选择区域和门店");
        }
        PatrolStoreStatisticsTableGradeVO resultVo = new PatrolStoreStatisticsTableGradeVO();
        List<PatrolStoreStatisticsTableDTO> result = new ArrayList<>();
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        //并发查询
        Map<String, Future<PatrolStoreStatisticsTableDTO>> futureMap = new HashMap<>();
        List<RegionPathDTO> regionPathList;
        TbMetaTableDO tbMetaTableDo = tbMetaTableMapper.selectById(enterpriseId,query.getMetaTableId());
        //获得总分
        Integer totalStore = null;
        if(StringUtils.isEmpty(tbMetaTableDo.getLevelInfo()) || StringUtils.isEmpty(tbMetaTableDo.getLevelRule())){
            log.info("检查表巡店等级配置LevelInfo或LevelRule为空：，MetaTableId：{}", query.getMetaTableId());
            return new PatrolStoreStatisticsTableGradeVO();
        }else {
            JSONObject levelInfoObj = JSONObject.parseObject(tbMetaTableDo.getLevelInfo());
            //根据巡店等级设置查询条件
            if(levelInfoObj.get("levelList") != null){
                List<EnterprisePatrolLevelDTO> enterprisePatrolLevelList = JSON.parseArray(levelInfoObj.get("levelList").toString(), EnterprisePatrolLevelDTO.class);
                Map<String,EnterprisePatrolLevelDTO> enterprisePatrolLevelMap = ListUtils.emptyIfNull(enterprisePatrolLevelList).stream()
                        .collect(Collectors.toMap(EnterprisePatrolLevelDTO::getKeyName, data -> data, (a, b) -> a));
                if(LevelRuleEnum.SCORING_RATE.getCode().equals(tbMetaTableDo.getLevelRule())){
                    //按分数统计
                    query.setLevelRule(LevelRuleEnum.SCORING_RATE.getCode());
                    query.setExcellentPercent(enterprisePatrolLevelMap.get(LevelRuleEnum.EXCELLENT.getCode()).getPercent());
                    query.setGoodPercent(enterprisePatrolLevelMap.get(LevelRuleEnum.GOOD.getCode()).getPercent());
                    query.setEligiblePercent(enterprisePatrolLevelMap.get(LevelRuleEnum.ELIGIBLE.getCode()).getPercent());
                    query.setDisqualificationPercent(enterprisePatrolLevelMap.get(LevelRuleEnum.DISQUALIFICATION.getCode()).getPercent());

                    //获得总分
                    totalStore = tbMetaStaTableColumnMapper.statisticsColumnMetaTableId(enterpriseId,query.getMetaTableId());
                    query.setTotalScore(totalStore);
                }else if(LevelRuleEnum.ITEM_NUM.getCode().equals(tbMetaTableDo.getLevelRule())){
                    //按检查项统计
                    query.setLevelRule(LevelRuleEnum.ITEM_NUM.getCode());
                    query.setExcellentNum(enterprisePatrolLevelMap.get(LevelRuleEnum.EXCELLENT.getCode()).getQualifiedNum());
                    query.setGoodNum(enterprisePatrolLevelMap.get(LevelRuleEnum.GOOD.getCode()).getQualifiedNum());
                    query.setEligibleNum(enterprisePatrolLevelMap.get(LevelRuleEnum.ELIGIBLE.getCode()).getQualifiedNum());
                    query.setDisqualificationNum(enterprisePatrolLevelMap.get(LevelRuleEnum.DISQUALIFICATION.getCode()).getQualifiedNum());
                }
            }else {
                log.info("检查表巡店等级配置levelList为空：，MetaTableId：{}", query.getMetaTableId());
                return new PatrolStoreStatisticsTableGradeVO();
            }

        }
        if(CollectionUtils.isNotEmpty(query.getRegionIds())){
            // 获取区域的路径
            regionPathList = regionService.getRegionPathByList(enterpriseId, query.getRegionIds());
            regionPathList.stream().forEach(data -> futureMap.put(data.getRegionPath(),EXECUTOR_SERVICE.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(user.getDbName());
                return tbDataTableMapper.getGradeInfo(enterpriseId,data.getRegionPath(),null,query);
            })));
        }else if(CollectionUtils.isNotEmpty(query.getStoreIds())){
            query.getStoreIds().stream().forEach(data -> futureMap.put(data,EXECUTOR_SERVICE.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(user.getDbName());
                return tbDataTableMapper.getGradeInfo(enterpriseId,null,data,query);
            })));
        }
        for (Map.Entry<String, Future<PatrolStoreStatisticsTableDTO>> entry : futureMap.entrySet()) {
            try {
                result.add(entry.getValue().get());
            } catch (Exception e) {
                log.error("检查表详情工单数统计异常", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "检查表详情工单数统计异常");
            }
        }
        if(CollectionUtils.isNotEmpty(result)){
            //优秀个数
            Integer excellent;
            //良好个数
            Integer good;
            //合格个数
            Integer eligible ;
            //不合格个数
            Integer disqualification;
            //总数
            Integer checkTimes;
            if(result.size() > 1){
                excellent =  result.stream().mapToInt(PatrolStoreStatisticsTableDTO::getExcellent).sum();
                good =  result.stream().mapToInt(PatrolStoreStatisticsTableDTO::getGood).sum();
                eligible =  result.stream().mapToInt(PatrolStoreStatisticsTableDTO::getEligible).sum();
                disqualification =  result.stream().mapToInt(PatrolStoreStatisticsTableDTO::getDisqualification).sum();
                checkTimes =  result.stream().mapToInt(PatrolStoreStatisticsTableDTO::getCheckedTimes).sum();
            }else {
                excellent = result.get(0).getExcellent();
                good = result.get(0).getGood();
                eligible = result.get(0).getEligible();
                disqualification = result.get(0).getDisqualification();
                checkTimes = result.get(0).getCheckedTimes();
            }
            //如果总分为0 全是不合格
            if(LevelRuleEnum.SCORING_RATE.getCode().equals(tbMetaTableDo.getLevelRule()) && totalStore == 0){
                disqualification = checkTimes;
            }
            resultVo.setExcellent( excellent);
            resultVo.setGood(good);
            resultVo.setEligible(eligible);
            resultVo.setDisqualification(disqualification);
            resultVo.setTotal(checkTimes);
            String gradeInfo;
            if(checkTimes != 0){
                gradeInfo = "优秀（"+reserveDouble(divide(excellent,checkTimes)) + "）" +
                        "良好（"+ reserveDouble(divide(good,checkTimes)) + "）" +
                        "合格（"+reserveDouble(divide(eligible,checkTimes)) + "）" +
                        "不合格（"+reserveDouble(divide(disqualification,checkTimes)) + "）";
            }else {
                gradeInfo = "优秀（0%）良好（0%）合格（0%）不合格（0%）";
            }

            resultVo.setGradeInfo(gradeInfo);
        }

        return resultVo;
    }
    public Double divide(Integer num1,Integer num2){
        if(num2 == 0){
            return 0.00;
        }
        return ((double)num1/num2);
    }
    public String reserveDouble(Double num){
        if(num == 0.0){
            return "0%";
        }
        return twoDecimal(num*100) + "%";
    }
    public Double twoDecimal(double num){
        BigDecimal bigDecimal = BigDecimal.valueOf(num);
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    public PatrolStoreStatisticsWorkOrderVO getWorkOrderInfo(String enterpriseId, PatrolStoreStatisticsTableQuery query, CurrentUser user) {
        if(CollectionUtils.isNotEmpty(query.getRegionIds()) && CollectionUtils.isNotEmpty(query.getStoreIds())){
            log.error("不能同时选择区域和门店，RegionIds：{};StoreIds:{}", query.getRegionIds(),query.getStoreIds());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "不能同时选择区域和门店");
        }
        PatrolStoreStatisticsWorkOrderVO resultVo = new PatrolStoreStatisticsWorkOrderVO();
        List<PatrolStoreStatisticsTableDTO> result = new ArrayList<>();
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        //并发查询
        Map<String, Future<PatrolStoreStatisticsTableDTO>> futureMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(query.getRegionIds())){
            // 获取区域的路径
            List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, query.getRegionIds());
            regionPathList.stream().forEach(data -> futureMap.put(data.getRegionPath(),EXECUTOR_SERVICE.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(user.getDbName());
                return tbDataStaTableColumnMapper.statisticWorkOrder(enterpriseId,data.getRegionPath(),null,query);
            })));
        }else if(CollectionUtils.isNotEmpty(query.getStoreIds())){
            query.getStoreIds().stream().forEach(data -> futureMap.put(data,EXECUTOR_SERVICE.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(user.getDbName());
                return tbDataStaTableColumnMapper.statisticWorkOrder(enterpriseId,null,data,query);
            })));
        }
        for (Map.Entry<String, Future<PatrolStoreStatisticsTableDTO>> entry : futureMap.entrySet()) {
            try {
                result.add(entry.getValue().get());
            } catch (Exception e) {
                log.error("检查表详情工单数统计异常", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "检查表详情工单数统计异常");
            }
        }

        //发起工单数
        Integer allWorkOrderNum;
        //已完成工单数
        Integer comWorkOrderNum;
        if(CollectionUtils.isNotEmpty(result)){
            if(result.size() > 1){
                allWorkOrderNum =  result.stream().mapToInt(PatrolStoreStatisticsTableDTO::getAllWorkOrderNum).sum();
                comWorkOrderNum =  result.stream().mapToInt(PatrolStoreStatisticsTableDTO::getComWorkOrderNum).sum();
            }else {
                allWorkOrderNum = result.get(0).getAllWorkOrderNum();
                comWorkOrderNum = result.get(0).getComWorkOrderNum();
            }
            resultVo.setAllWorkOrderNum(allWorkOrderNum);
            resultVo.setComWorkOrderNum(comWorkOrderNum);
            resultVo.setComWorkOrderRatio(reserveDouble(divide(comWorkOrderNum,allWorkOrderNum)));
        }
        return resultVo;
    }
    @Override
    public PatrolStoreStatisticsTableColumnVO getMetaColumnInfo(String enterpriseId, PatrolStoreStatisticsTableQuery query, CurrentUser user) {
        if(CollectionUtils.isNotEmpty(query.getRegionIds()) && CollectionUtils.isNotEmpty(query.getStoreIds())){
            log.error("不能同时选择区域和门店，RegionIds：{};StoreIds:{}", query.getRegionIds(),query.getStoreIds());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "不能同时选择区域和门店");
        }
        List<PatrolStoreStatisticsTableColumnDTO> result = new ArrayList<>();
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        List<TbMetaStaTableColumnDO> tableColumnDoList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId,Collections.singletonList(query.getMetaTableId()),Boolean.FALSE);
        Map<Long,TbMetaStaTableColumnDO> tableColumnDoMap = ListUtils.emptyIfNull(tableColumnDoList).stream()
                .collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, data -> data, (a, b) -> a));

        //并发查询
        Map<String, Future<List<PatrolStoreStatisticsTableColumnDTO>>> futureMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(query.getRegionIds())){
            // 获取区域的路径
            List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, query.getRegionIds());
            regionPathList.stream().forEach(data -> futureMap.put(data.getRegionPath(),EXECUTOR_SERVICE.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(user.getDbName());
                return tbDataStaTableColumnMapper.statisticMetaColumn(enterpriseId,data.getRegionPath(),null,query);
            })));
        }else if(CollectionUtils.isNotEmpty(query.getStoreIds())){
            query.getStoreIds().stream().forEach(data -> futureMap.put(data,EXECUTOR_SERVICE.submit(() -> {
                DataSourceHelper.changeToSpecificDataSource(user.getDbName());
                return tbDataStaTableColumnMapper.statisticMetaColumn(enterpriseId,null,data,query);
            })));
        }
        for (Map.Entry<String, Future<List<PatrolStoreStatisticsTableColumnDTO>>> entry : futureMap.entrySet()) {
            try {
                result.addAll(entry.getValue().get());
            } catch (Exception e) {
                log.error("检查表图表检查项统计异常", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "检查表图表检查项统计异常");
            }
        }


        PatrolStoreStatisticsTableColumnDTO dto = new PatrolStoreStatisticsTableColumnDTO();
        PatrolStoreStatisticsTableColumnVO vo = new PatrolStoreStatisticsTableColumnVO();

        if(CollectionUtils.isNotEmpty(result)){
            if(result.size() > 1){
                //最多不合格项
                List<PatrolStoreStatisticsTableColumnDTO> failList = result.stream().collect(
                        Collectors.groupingBy(PatrolStoreStatisticsTableColumnDTO::getMetaColumnId)).entrySet().stream().map(
                        e -> {
                            PatrolStoreStatisticsTableColumnDTO newColumn = new PatrolStoreStatisticsTableColumnDTO();
                            Integer sumFailTimes = e.getValue().stream().mapToInt(PatrolStoreStatisticsTableColumnDTO::getFailTimes).sum();
                            newColumn.setFailTimes(sumFailTimes);
                            newColumn.setMetaColumnId(e.getKey());
                            TbMetaStaTableColumnDO tempColumnDo = tableColumnDoMap.get(e.getKey());
                            if(tempColumnDo != null){
                                newColumn.setMetaColumnDescription(tempColumnDo.getDescription());
                                newColumn.setMetaColumnName(tempColumnDo.getColumnName());
                            }else {
                                log.error("获取检查项失败", e);
                                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "获取检查项失败");
                            }
                            return newColumn;
                        }).sorted(Comparator.comparing(PatrolStoreStatisticsTableColumnDTO::getFailTimes).reversed()).limit(3).collect(Collectors.toList());

                //最多失分项
                List<PatrolStoreStatisticsTableColumnDTO> scoreList = result.stream().collect(
                        Collectors.groupingBy(PatrolStoreStatisticsTableColumnDTO::getMetaColumnId)).entrySet().stream().map(
                        e -> {
                            PatrolStoreStatisticsTableColumnDTO newColumn = new PatrolStoreStatisticsTableColumnDTO();
                            BigDecimal sumCheckScores = e.getValue().stream().map(PatrolStoreStatisticsTableColumnDTO::getCheckScore).reduce(BigDecimal.ZERO, BigDecimal::add);
                            Integer sumCheckTimes = e.getValue().stream().mapToInt(PatrolStoreStatisticsTableColumnDTO::getCheckTimes).sum();
                            newColumn.setCheckScore(sumCheckScores);
                            newColumn.setMetaColumnId(e.getKey());
                            TbMetaStaTableColumnDO tempColumnDo = tableColumnDoMap.get(e.getKey());
                            if(tempColumnDo != null){
                                BigDecimal losePoint = tempColumnDo.getSupportScore().multiply(new BigDecimal(sumCheckTimes)).subtract(sumCheckScores);
                                newColumn.setLosePoints(losePoint);
                                newColumn.setMetaColumnDescription(tempColumnDo.getDescription());
                                newColumn.setMetaColumnName(tempColumnDo.getColumnName());
                            }else {
                                log.error("获取检查项失败", e);
                                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "获取检查项失败");
                            }
                            return newColumn;
                        }).sorted(Comparator.comparing(PatrolStoreStatisticsTableColumnDTO::getLosePoints).reversed()).limit(3).collect(Collectors.toList());

                vo.setFailList(failList);
                vo.setLostPointList(scoreList);

            }else {
                PatrolStoreStatisticsTableColumnDTO newColumn = result.get(0);
                TbMetaStaTableColumnDO tempColumnDo = tableColumnDoMap.get(newColumn.getMetaColumnId());
                if(tempColumnDo != null){
                    BigDecimal losePoint = tempColumnDo.getSupportScore().multiply(new BigDecimal(newColumn.getCheckTimes())).subtract(newColumn.getCheckScore());
                    dto.setLosePoints(losePoint);
                    dto.setMetaColumnDescription(tempColumnDo.getDescription());
                    dto.setMetaColumnName(tempColumnDo.getColumnName());
                }else {
                    log.error("获取检查项失败", newColumn.getMetaColumnId());
                    throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "获取检查项失败");
                }
                dto.setMetaColumnId(newColumn.getMetaColumnId());
                dto.setFailTimes(newColumn.getFailTimes());
                dto.setCheckScore(newColumn.getCheckScore());
                vo.setFailList(Collections.singletonList(dto));
                vo.setLostPointList(Collections.singletonList(dto));
            }
        } else {
            //如果数据为空，捞出3个检查项，设置各项数据为0
            List<PatrolStoreStatisticsTableColumnDTO> failList = new ArrayList<>();
            for (int i = 0; i < tableColumnDoList.size(); i++) {
                if (i > 2) {
                    break;
                }
                TbMetaStaTableColumnDO col = tableColumnDoList.get(i);
                PatrolStoreStatisticsTableColumnDTO colDto = new PatrolStoreStatisticsTableColumnDTO(col.getId(), col.getColumnName(),
                        col.getDescription(), Constants.INDEX_ZERO, new BigDecimal(Constants.INDEX_ZERO), Constants.INDEX_ZERO, new BigDecimal(Constants.INDEX_ZERO));
                failList.add(colDto);
            }
            vo.setFailList(failList);
            vo.setLostPointList(failList);
        }
        return vo;
    }

    @Override
    public List<PatrolStoreStatisticsTableColumnDTO> getMetaTableColumnList(String eid) {

        return null;
    }

    /**
     * 标准检查项统计数据
     */
    @Override
    public List<PatrolStoreStatisticsStaColumnInfoDTO> statisticsStaColumnData(String enterpriseId,
                                                                               List<TbDataStaTableColumnDO> dataStaColumnDOList) {
        // Map: metaTableId->metaTableDO
        Set<Long> metaTableIds =
                dataStaColumnDOList.stream().map(TbDataStaTableColumnDO::getMetaTableId).collect(Collectors.toSet());
        List<TbMetaTableDO> tbMetaTableDOList =
                tbMetaTableMapper.selectByIds(enterpriseId, new ArrayList<>(metaTableIds));
        Map<Long, TbMetaTableDO> idMetaTableMap = tbMetaTableDOList.stream()
                .collect(Collectors.toMap(TbMetaTableDO::getId, Function.identity(), (a, b) -> a));
        // Map: metaStaColumnId->metaStaColumnDO
        Set<Long> metaStaColumnIds =
                dataStaColumnDOList.stream().map(TbDataStaTableColumnDO::getMetaColumnId).collect(Collectors.toSet());
        List<TbMetaStaTableColumnDO> metaStaColumnDOList =
                tbMetaStaTableColumnMapper.selectByIds(enterpriseId, new ArrayList<>(metaStaColumnIds));
        Map<Long, TbMetaStaTableColumnDO> idMetaStaColumnMap = metaStaColumnDOList.stream()
                .collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity(), (a, b) -> a));
        // Map:id -> record
        Set<Long> businessIds =
                dataStaColumnDOList.stream().map(TbDataStaTableColumnDO::getBusinessId).collect(Collectors.toSet());
        List<TbPatrolStoreRecordDO> recordDOList =
                tbPatrolStoreRecordMapper.selectByIds(enterpriseId, new ArrayList<>(businessIds));
        Map<Long, TbPatrolStoreRecordDO> idRecordMap = recordDOList.stream()
                .collect(Collectors.toMap(TbPatrolStoreRecordDO::getId, Function.identity(), (a, b) -> a));
        // Map:id -> regionName
        Set<Long> regionIds =
                dataStaColumnDOList.stream().map(TbDataStaTableColumnDO::getRegionId).collect(Collectors.toSet());
        List<RegionDO> regionDOList = regionMapper.getByIds(enterpriseId, new ArrayList<>(regionIds));
        Map<Long, String> idRegionNameMap =
                regionDOList.stream()
                        .filter(a -> a.getId() != null && a.getName() != null)
                        .collect(Collectors.toMap(RegionDO::getId, RegionDO::getName, (a, b) -> a));
        // Map:id -> taskName
        Set<Long> unifyTaskIds =
                recordDOList.stream().map(TbPatrolStoreRecordDO::getTaskId).collect(Collectors.toSet());
        List<TaskParentDO> parentDOList = taskParentMapper.selectTaskByIds(enterpriseId, new ArrayList<>(unifyTaskIds));
        Map<Long, String> idTaskNameMap = parentDOList.stream()
                .filter(a -> a.getId() != null && a.getTaskName() != null)
                .collect(Collectors.toMap(TaskParentDO::getId, TaskParentDO::getTaskName, (a, b) -> a));

        // Map:taskQuestionId -> questionReportDTO
        Set<Long> taskQuestionIds = dataStaColumnDOList.stream().map(TbDataStaTableColumnDO::getTaskQuestionId)
                .filter(a -> a != 0).collect(Collectors.toSet());
        List<QuestionReportDTO> questionReportDTOList =
                questionOrderTaskService.getQuestionReportData(enterpriseId, new ArrayList<>(taskQuestionIds));
        Map<Long, QuestionReportDTO> taskIdQuestionReportMap = questionReportDTOList.stream()
                .collect(Collectors.toMap(QuestionReportDTO::getUnifyTaskId, Function.identity(), (a, b) -> a));
        // 返回值
        return dataStaColumnDOList.stream().map(a -> {
            TbMetaTableDO tbMetaTableDO = idMetaTableMap.get(a.getMetaTableId());
            TbMetaStaTableColumnDO metaStaColumn = idMetaStaColumnMap.get(a.getMetaColumnId());
            TbPatrolStoreRecordDO record = idRecordMap.get(a.getBusinessId());
            String regionName = idRegionNameMap.get(a.getRegionId());
            String taskName = idTaskNameMap.get(record.getTaskId());
            String patrolType = record.getPatrolType();
            String taskType = "";
            if (PATROL_STORE_OFFLINE.getCode().equals(patrolType)) {
                taskType = "线下巡店";
            }
            if (PATROL_STORE_ONLINE.getCode().equals(patrolType)) {
                taskType = "线上巡店";
            }
            if (TaskTypeEnum.PATROL_STORE_PICTURE_ONLINE.getCode().equals(patrolType)) {
                patrolType = "定时巡检";
            }
            String checkAwardPunish = "";
            String checkResult = a.getCheckResult();
            if (PASS.equals(checkResult)) {
                checkAwardPunish = String.format("奖:%s", metaStaColumn.getAwardMoney());
            }
            if (FAIL.equals(checkResult)) {
                checkAwardPunish = String.format("罚:%s", metaStaColumn.getPunishMoney());
            }
            // checkTime
            Date signStartTime = record.getSignStartTime();
            String signStartTimeStr =
                    signStartTime == null ? "" : DateUtils.convertTimeToString(signStartTime.getTime(), DATE_FORMAT_SEC);
            Date signEndTime = record.getSignEndTime();
            String signEndTimeStr =
                    signEndTime == null ? "" : DateUtils.convertTimeToString(signEndTime.getTime(), DATE_FORMAT_SEC);
            String checkTime = signStartTimeStr + "-" + signEndTimeStr;
            PatrolStoreStatisticsStaColumnInfoDTO build = PatrolStoreStatisticsStaColumnInfoDTO.builder()
                    .staColumnId(a.getId())
                    .taskType(taskType).regionName(regionName).storeName(a.getStoreName()).taskName(taskName)
                    .categoryName(a.getCategoryName())
                    .columnName(a.getMetaColumnName()).standardPic(metaStaColumn.getStandardPic())
                    .description(metaStaColumn.getDescription()).level(metaStaColumn.getLevel())
                    .supportScore(metaStaColumn.getSupportScore()).lowestScore(metaStaColumn.getLowestScore())
                    .awardPunish(String.format("奖:%s,罚:%s", metaStaColumn.getAwardMoney(), metaStaColumn.getPunishMoney()))
                    .checkResult(a.getCheckResult()).checkScore(a.getCheckScore()).checkAwardPunish(checkAwardPunish)
                    .checkText(a.getCheckText()).checkPics(a.getCheckPics()).checkTime(checkTime)
                    .questionStatus(a.getTaskQuestionStatus()).taskQuestionId(a.getTaskQuestionId()).build();
            if(tbMetaTableDO != null){
                build.setTableName(tbMetaTableDO.getTableName());
            }
            if (a.getTaskQuestionId() != 0) {
                QuestionReportDTO questionReportDTO = taskIdQuestionReportMap.get(a.getTaskQuestionId());
                build.setQuestionPics(CollectionUtils.isEmpty(questionReportDTO.getQuestionPhoto()) ? null
                        : String.join(",", questionReportDTO.getQuestionPhoto()));
                build.setHandlePics(CollectionUtils.isEmpty(questionReportDTO.getHandlePhoto()) ? null
                        : String.join(",", questionReportDTO.getHandlePhoto()));
                build.setHandleDoneTime(
                        questionReportDTO.getCompleteTime() == null ? null : new Date(questionReportDTO.getCompleteTime()));
                build.setOverdue(questionReportDTO.getOverdueCompleteFlag());
                build.setQuestionStatus(questionReportDTO.getStatus());
                build.setCheckUserName(questionReportDTO.getCreateUserName());
                build.setHandleUserName(questionReportDTO.getHandleUserName());
                build.setReCheckUserName(questionReportDTO.getRecheckUserName());
                build.setTaskDesc(questionReportDTO.getTaskDesc());
            }
            return build;
        }).collect(Collectors.toList());
    }

    @Override
    public ImportTaskDO getPatrolStoreDetailExport(String enterpriseId, PatrolStoreDetailRequest request,CurrentUser user) {
        if (CollectionUtils.isEmpty(request.getPatrolStoreMode())){
            throw new ServiceException(ErrorCodeEnum.PARTROL_STORE_MODE);
        }

        Integer patrolStoreMode = null;
        if (request.getPatrolStoreMode().size()==1){
            if (request.getPatrolStoreMode().get(0)==1){
                patrolStoreMode = 0;
            }
            if (request.getPatrolStoreMode().get(0)==2){
                patrolStoreMode = 1;
            }
        }
        Long totalNum = tbDataTableMapper.countPatrolStoreByCondition(enterpriseId, request.getBeginTime(),
                request.getEndTime(), request.getMetaTableId(), request.getPatrolTypeList(),patrolStoreMode);
        if(totalNum == null || totalNum == 0){
            throw new ServiceException("当前无记录可导出");
        }
        if(totalNum > Constants.MAX_EXPORT_SIZE){
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE +"条，请缩小导出范围");
        }

        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.PATROL_STORE_DETAIL);
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.PATROL_STORE_DETAIL);

        MsgUniteData msgUniteData = new MsgUniteData();
        ExportPatrolStoreDetailRequest msg = new ExportPatrolStoreDetailRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(request);
        msg.setDbName(user.getDbName());
        msg.setTotalNum(totalNum);
        msg.setImportTaskDO(importTaskDO);
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.PATROL_STORE_DETAIL.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public PageInfo recheckStatisticsUser(String enterpriseId, PatrolStoreRecheckStatisticsUserQuery query, String dbName) {
        List<String> userIdList = query.getUserIdList();
        // 分页
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        userIdList = enterpriseUserMapper.selectUserIdsByUserList(enterpriseId, userIdList);
        PageInfo pageInfo = new PageInfo(userIdList);
        if (CollectionUtils.isEmpty(userIdList)) {
            return pageInfo;
        }
        // 获取人员信息
        List<EnterpriseUserDTO> userDTOS = enterpriseUserMapper.getUserDetailList(enterpriseId, userIdList);
        if (CollectionUtils.isEmpty(userDTOS)) {
            return pageInfo;
        }
        Map<String, String> userMap = userDTOS.stream()
                .filter(a -> a.getUserId() != null && a.getName() != null)
                .collect(Collectors.toMap(EnterpriseUserDTO::getUserId, EnterpriseUserDTO::getName, (a, b) -> a));
        Map<String, Future<PatrolOverviewDTO>> idTaskMap = new HashMap<>();
        // 异步处理统计数据
        for (String userId : userIdList) {
            idTaskMap.put(userId,
                    EXECUTOR_SERVICE.submit(() -> patrolStoreService.recheckOverview(enterpriseId, query.getBeginTime(), query.getEndTime(), userId, dbName)));
        }
        List<PatrolOverviewUserDTO> overviewUserDTOList = new ArrayList<>();
        userIdList.forEach(userId -> {
            PatrolOverviewUserDTO patrolOverviewUserDTO = new PatrolOverviewUserDTO();
            Future<PatrolOverviewDTO> future = idTaskMap.get(userId);
            try {
                PatrolOverviewDTO patrolOverviewDTO = future.get();
                patrolOverviewUserDTO.setUserId(userId);
                patrolOverviewUserDTO.setUserName(userMap.get(userId));
                patrolOverviewUserDTO.setAlreadyRecheck(patrolOverviewDTO.getAlreadyRecheck());
                patrolOverviewUserDTO.setCanRecheck(patrolOverviewDTO.getCanRecheck());
                patrolOverviewUserDTO.setRecheckPercent(patrolOverviewDTO.getRecheckPercent());
                overviewUserDTOList.add(patrolOverviewUserDTO);
            } catch (Exception e) {
                log.error("统计区域出错：", e);
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "获取统计数据异常");
            }
        });
        pageInfo.setList(overviewUserDTOList);
        return pageInfo;
    }

}
