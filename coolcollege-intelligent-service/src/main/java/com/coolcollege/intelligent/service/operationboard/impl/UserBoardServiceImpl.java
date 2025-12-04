package com.coolcollege.intelligent.service.operationboard.impl;

import cn.hutool.core.util.ObjectUtil;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.question.dao.QuestionRecordDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.coolcollege.intelligent.model.enterprise.dto.UserRoleDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.operationboard.dto.PatrolTypeStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TaskStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.UserDetailStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.UserStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.query.UserDetailStatisticsQuery;
import com.coolcollege.intelligent.model.operationboard.query.UserStatisticsQuery;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreCountDTO;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.operationboard.UserBoardService;
import com.coolcollege.intelligent.service.patrolstore.impl.PatrolStoreStatisticsServiceImpl;
import com.coolcollege.intelligent.service.region.RegionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/1/8 14:03
 */
@Service
@Slf4j
public class UserBoardServiceImpl implements UserBoardService {
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private AuthVisualService authVisualService;
    @Resource
    private QuestionRecordDao questionRecordDao;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private EnterpriseService enterpriseService;

    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;

    @Resource
    private PatrolStoreStatisticsServiceImpl patrolStoreStatisticsService;

    @Resource
    private RegionService regionService;

    // 巡店次数排序
    private static final String PATROL_NUM = "patrolNum";
    // 创建问题数排序
    private static final String CREATE_QUESTION_NUM = "createQuestionNum";

    private static final String FINISH_STATUS = "FINISH";

    @Override
    public UserStatisticsDTO userStatistics(String enterpriseId, UserStatisticsQuery userStatisticsQuery) {
        if(log.isInfoEnabled()){
            log.info("移动端人员执行力汇总入参{},{}", enterpriseId, userStatisticsQuery);

        }
        UserStatisticsDTO userStatisticsDTO = new UserStatisticsDTO();
        Date beginDate = userStatisticsQuery.getBeginDate();
        Date endDate = userStatisticsQuery.getEndDate();
        Set<String> userIds = new HashSet<>(userStatisticsQuery.getUserIdList());
        List<Long> roleIdList = userStatisticsQuery.getRoleIdList();
        // 根据职位选人
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            userIds.addAll(sysRoleMapper.getUserIdListByRoleIdList(enterpriseId, roleIdList));
        }
        if (userIds.size() > 500) {
            throw new ServiceException("暂不支持统计500人以上的数据");
        }
        userStatisticsDTO.setPersonCount(userIds.size());
        if (CollectionUtils.isEmpty(userIds)) {
            return userStatisticsDTO;
        }
        userStatisticsDTO =
            tbDataStaTableColumnMapper.appUserStatistics(enterpriseId, new ArrayList<>(userIds), beginDate, endDate);
        if (ObjectUtil.isNull(userStatisticsDTO)) {
            return new UserStatisticsDTO();
        }
        List<TbMetaTableDO> tableDOList =
            tbMetaTableMapper.getTableByCreateUserId(enterpriseId, new ArrayList<>(userIds), beginDate, endDate);
        List<TbPatrolStoreRecordDO> recordDOList = tbPatrolStoreRecordMapper.getRecordList(enterpriseId, beginDate,
            endDate, null, new ArrayList<>(userIds), null, 1);
        Long subTaskId = 0L;
        Integer taskNum =
            Math.toIntExact(recordDOList.stream().filter(data -> !subTaskId.equals(data.getSubTaskId())).count());
        userStatisticsDTO.setTotalTaskNum(taskNum);
        userStatisticsDTO.setCreateTableCount(tableDOList.size());
        userStatisticsDTO.setPatrolStoreNum(recordDOList.size());
        userStatisticsDTO.setPersonCount(userIds.size());
        log.info("移动端人员执行力汇总出参{}", userStatisticsDTO);

        return userStatisticsDTO;
    }

    @Override
    public PageInfo<UserDetailStatisticsDTO> userDetailStatistics(String enterpriseId, UserDetailStatisticsQuery query) {
        Set<String> userIds = new HashSet<>(query.getUserIdList());
        Date beginDate = query.getBeginDate();
        Date endDate = query.getEndDate();
        String orderField = query.getOrderField();
        String orderType = query.getOrderType();
        List<Long> roleIdList = query.getRoleIdList();
        // 根据职位选人
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            userIds.addAll(sysRoleMapper.getUserIdListByRoleIdList(enterpriseId, roleIdList));
        }
        if (userIds.size() > 500) {
            throw new ServiceException("暂不支持统计500人以上的数据");
        }
        List<UserDetailStatisticsDTO> result;
        List<UserDetailStatisticsDTO> userCreateQuestionInfo = questionRecordDao.getUserCreateQuestionInfo(enterpriseId, userIds, Arrays.asList(QuestionTypeEnum.PATROL_STORE.getCode()), beginDate, endDate);
        Map<String, UserDetailStatisticsDTO> createQuestionMap = ListUtils.emptyIfNull(userCreateQuestionInfo).stream().collect(Collectors.toMap(k -> k.getUserId(), Function.identity()));
        // 按创建问题排名
        if (CREATE_QUESTION_NUM.equals(orderField)) {
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            result = tbDataStaTableColumnMapper.userDetailStatistics(enterpriseId, new ArrayList<>(userIds), beginDate,
                endDate, orderType);
            if(CollectionUtils.isNotEmpty(result)){
                List<String> finalUserIdList =
                        result.stream().map(UserDetailStatisticsDTO::getUserId).collect(Collectors.toList());
                Map<String,String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId,finalUserIdList);
                // 获取巡店记录
                List<UserDetailStatisticsDTO> recordDOList = tbPatrolStoreRecordMapper.getPatrolStoreNum(enterpriseId, beginDate, endDate, null, finalUserIdList, null, 1);
                Map<String, UserDetailStatisticsDTO> userPatrolNumMap = ListUtils.emptyIfNull(recordDOList).stream().collect(Collectors.toMap(k->k.getUserId(), Function.identity(), (k1, k2)->k1));
                // 获取管理门店数量
                List<AuthStoreCountDTO> storeCountDTOS =
                        authVisualService.authStoreCount(enterpriseId, finalUserIdList, Boolean.FALSE);
                // map:userId to storeNum
                Map<String, Integer> userStoreNumMap =
                        storeCountDTOS.stream().collect(Collectors.toMap(AuthStoreCountDTO::getUserId,
                                a -> a.getStoreCount() == null ? 0 : a.getStoreCount(), (a, b) -> a));

                // 获取人员职位
                List<UserRoleDTO> userRoleDTOS = sysRoleMapper.userAndRolesByUserId(enterpriseId, Lists.newArrayList(userIds));
                Map<String, String> userRoleMap = userRoleDTOS.stream()
                        .filter(a -> a.getUserId() != null && a.getRoleName() != null)
                        .collect(Collectors.toMap(UserRoleDTO::getUserId, UserRoleDTO::getRoleName, (a, b) -> a+","+b));
                //查看是否是老企业,获取对应部门信息
                boolean historyEnterprise = enterpriseService.isHistoryEnterprise(enterpriseId);
                Map<String, String> fullNameMap = new HashMap<>();
                Map<String, List<String>> userRegionMap = new HashMap<>();
                if (!historyEnterprise) {
                    List<UserRegionMappingDO> regionMappingList = userRegionMappingDAO.getRegionIdsByUserIds(enterpriseId, Lists.newArrayList(userIds));
                    List<Long> regionIds = regionMappingList.stream().map(o->Long.valueOf(o.getRegionId())).distinct().collect(Collectors.toList());
                    fullNameMap = regionService.getNoBaseNodeFullNameByRegionIds(enterpriseId, regionIds, Constants.SPRIT);
                    userRegionMap = regionMappingList.stream().collect(Collectors.groupingBy(k -> k.getUserId(), Collectors.mapping(v -> v.getRegionId(), Collectors.toList())));
                }
                Map<String, List<String>> finalUserRegionMap = userRegionMap;
                Map<String, String> finalFullNameMap = fullNameMap;
                result.forEach(data -> {
                    int storeNum = userStoreNumMap.get(data.getUserId());
                    UserDetailStatisticsDTO userDetailStatisticsDTO = createQuestionMap.getOrDefault(data.getUserId(), new UserDetailStatisticsDTO());
                    UserDetailStatisticsDTO userPatrolNum = userPatrolNumMap.getOrDefault(data.getUserId(), new UserDetailStatisticsDTO());
                    data.setManageStoreNum(storeNum);
                    data.setPatrolNum(userPatrolNum.getPatrolNum());
                    data.setUserName(userNameMap.get(data.getUserId()));
                    data.setPatrolStoreNum(userPatrolNum.getPatrolStoreNum());
                    data.setDefaultRoleList(query.getDefaultRoleList());
                    data.setFinishQuestionNum(userDetailStatisticsDTO.getFinishQuestionNum());
                    data.setCreateQuestionNum(userDetailStatisticsDTO.getCreateQuestionNum());
                    data.setRoleName(userRoleMap.get(data.getUserId()));
                    if(!historyEnterprise){
                        data.setDepartmentName(patrolStoreStatisticsService.getUserRegionName(finalUserRegionMap.get(data.getUserId()), finalFullNameMap));
                    }else {
                        data.setDepartmentName("");
                    }
                });
            }
        } else {
            // 按巡店次数排名
            PageHelper.startPage(query.getPageNum(), query.getPageSize());
            result = tbPatrolStoreRecordMapper.userDetailStatistics(enterpriseId, new ArrayList<>(userIds), beginDate,
                endDate, orderType);
            // 获取人员职位
            List<UserRoleDTO> userRoleDTOS = sysRoleMapper.userAndRolesByUserId(enterpriseId, Lists.newArrayList(userIds));
            Map<String, String> userRoleMap = userRoleDTOS.stream()
                    .filter(a -> a.getUserId() != null && a.getRoleName() != null)
                    .collect(Collectors.toMap(UserRoleDTO::getUserId, UserRoleDTO::getRoleName, (a, b) -> a+","+b));
            if(CollectionUtils.isNotEmpty(result)){
                List<String> finalUserIdList =
                        result.stream().map(UserDetailStatisticsDTO::getUserId).collect(Collectors.toList());
                // 获取管理门店数量
                List<AuthStoreCountDTO> storeCountDTOS =
                        authVisualService.authStoreCount(enterpriseId, finalUserIdList, Boolean.FALSE);
                // map:userId to storeNum
                Map<String, Integer> userStoreNumMap =
                        storeCountDTOS.stream().collect(Collectors.toMap(AuthStoreCountDTO::getUserId,
                                a -> a.getStoreCount() == null ? 0 : a.getStoreCount(), (a, b) -> a));
                //查看是否是老企业,获取对应部门信息
                boolean historyEnterprise = enterpriseService.isHistoryEnterprise(enterpriseId);
                Map<String, String> fullNameMap = new HashMap<>();
                Map<String, List<String>> userRegionMap = new HashMap<>();
                if (!historyEnterprise) {
                    List<UserRegionMappingDO> regionMappingList = userRegionMappingDAO.getRegionIdsByUserIds(enterpriseId, Lists.newArrayList(userIds));
                    List<Long> regionIds = regionMappingList.stream().map(o->Long.valueOf(o.getRegionId())).distinct().collect(Collectors.toList());
                    fullNameMap = regionService.getNoBaseNodeFullNameByRegionIds(enterpriseId, regionIds, Constants.SPRIT);
                    userRegionMap = regionMappingList.stream().collect(Collectors.groupingBy(k -> k.getUserId(), Collectors.mapping(v -> v.getRegionId(), Collectors.toList())));
                }
                Map<String, List<String>> finalUserRegionMap = userRegionMap;
                Map<String, String> finalFullNameMap = fullNameMap;
                result.forEach(data -> {
                    UserDetailStatisticsDTO userDetailStatisticsDTO = createQuestionMap.getOrDefault(data.getUserId(), new UserDetailStatisticsDTO());
                    data.setCreateQuestionNum(userDetailStatisticsDTO.getCreateQuestionNum());
                    data.setFinishQuestionNum(userDetailStatisticsDTO.getFinishQuestionNum());
                    // 门店数
                    Integer storeNum = userStoreNumMap.get(data.getUserId());
                    data.setManageStoreNum(storeNum == null ? 0 : storeNum);
                    data.setDefaultRoleList(query.getDefaultRoleList());
                    data.setRoleName(userRoleMap.get(data.getUserId()));
                    if(!historyEnterprise){
                        data.setDepartmentName(patrolStoreStatisticsService.getUserRegionName(finalUserRegionMap.get(data.getUserId()), finalFullNameMap));
                    }else {
                        data.setDepartmentName("");
                    }
                });
            }
        }

        if (CollectionUtils.isEmpty(result)) {
            List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserDao.selectUsersByStatusAndUserIds(enterpriseId, new ArrayList<>(userIds), null, null);
            //查看是否是老企业,获取对应部门信息
            boolean historyEnterprise = enterpriseService.isHistoryEnterprise(enterpriseId);
            Map<String, String> fullNameMap = new HashMap<>();
            Map<String, List<String>> userRegionMap = new HashMap<>();
            if (!historyEnterprise) {
                List<UserRegionMappingDO> regionMappingList = userRegionMappingDAO.getRegionIdsByUserIds(enterpriseId, Lists.newArrayList(userIds));
                List<Long> regionIds = regionMappingList.stream().map(o->Long.valueOf(o.getRegionId())).distinct().collect(Collectors.toList());
                fullNameMap = regionService.getNoBaseNodeFullNameByRegionIds(enterpriseId, regionIds, Constants.SPRIT);
                userRegionMap = regionMappingList.stream().collect(Collectors.groupingBy(k -> k.getUserId(), Collectors.mapping(v -> v.getRegionId(), Collectors.toList())));
            }
            Map<String, List<String>> finalUserRegionMap = userRegionMap;
            Map<String, String> finalFullNameMap = fullNameMap;

            // 获取人员职位
            List<UserRoleDTO> userRoleDTOS = sysRoleMapper.userAndRolesByUserId(enterpriseId, Lists.newArrayList(userIds));
            Map<String, String> userRoleMap = userRoleDTOS.stream()
                    .filter(a -> a.getUserId() != null && a.getRoleName() != null)
                    .collect(Collectors.toMap(UserRoleDTO::getUserId, UserRoleDTO::getRoleName, (a, b) -> a+","+b));
            result = enterpriseUserDOList.stream().map(e ->{
                UserDetailStatisticsDTO dto = new UserDetailStatisticsDTO();
                dto.setUserName(e.getName());
                dto.setUserId(e.getUserId());
                dto.setManageStoreNum(0);
                dto.setCreateQuestionNum(0);
                dto.setPatrolNum(0);
                dto.setFinishQuestionNum(0);
                dto.setPatrolStoreNum(0);
                dto.setRoleName(userRoleMap.get(e.getUserId()));
                if(!historyEnterprise){
                    dto.setDepartmentName(patrolStoreStatisticsService.getUserRegionName(finalUserRegionMap.get(e.getUserId()), finalFullNameMap));
                }else {
                    dto.setDepartmentName("");
                }
                return dto;
            }).collect(Collectors.toList());
            return new PageInfo(result);
        }
        return new PageInfo(result);
    }

    @Override
    public TaskStatisticsDTO taskStatistics(String enterpriseId, UserStatisticsQuery userStatisticsQuery) {
        TaskStatisticsDTO result = new TaskStatisticsDTO();
        Set<String> userIds = new HashSet<>(userStatisticsQuery.getUserIdList());
        Date beginDate = userStatisticsQuery.getBeginDate();
        Date endDate = userStatisticsQuery.getEndDate();
        List<Long> roleIdList = userStatisticsQuery.getRoleIdList();
        // 根据职位选人
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            userIds.addAll(sysRoleMapper.getUserIdListByRoleIdList(enterpriseId, roleIdList));
        }
        if (CollectionUtils.isEmpty(userIds)) {
            return result;
        }
        if (userIds.size() > 500) {
            throw new ServiceException("暂不支持统计500人以上的数据");
        }
        result = tbPatrolStoreRecordMapper.taskStatistics(enterpriseId, new ArrayList<>(userIds), beginDate, endDate);
        return result;
    }

    @Override
    public PatrolTypeStatisticsDTO patrolTypeStatistics(String enterpriseId, UserStatisticsQuery query) {
        PatrolTypeStatisticsDTO result = new PatrolTypeStatisticsDTO();
        Set<String> userIds = new HashSet<>(query.getUserIdList());
        Date beginDate = query.getBeginDate();
        Date endDate = query.getEndDate();
        List<Long> roleIdList = query.getRoleIdList();
        // 根据职位选人
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            userIds.addAll(sysRoleMapper.getUserIdListByRoleIdList(enterpriseId, roleIdList));
        }
        if (CollectionUtils.isEmpty(userIds)) {
            return result;
        }
        if (userIds.size() > 500) {
            throw new ServiceException("暂不支持统计500人以上的数据");
        }
         result =tbPatrolStoreRecordMapper.patrolTypeStatistics(enterpriseId, new ArrayList<>(userIds), beginDate,
            endDate);
        return result;
    }
}
