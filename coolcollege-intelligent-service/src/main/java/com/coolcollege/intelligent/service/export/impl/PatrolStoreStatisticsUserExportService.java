package com.coolcollege.intelligent.service.export.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataStaTableColumnMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbDataTableMapper;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.mapper.user.UserRegionMappingDAO;
import com.coolcollege.intelligent.model.enterprise.UserRegionMappingDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.UserRoleDTO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.PatrolStoreStatisticsUserExportRequest;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.patrolstore.statistics.PatrolStoreStatisticsUserDTO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreCountDTO;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/6/7 16:47
 */
@Service
public class PatrolStoreStatisticsUserExportService implements BaseExportService {
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private AuthVisualService authVisualService;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private TbDataStaTableColumnMapper tbDataStaTableColumnMapper;
    @Resource
    private TbDataTableMapper tbDataTableMapper;

    @Resource
    private EnterpriseService enterpriseService;

    @Resource
    private UserRegionMappingDAO userRegionMappingDAO;

    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private RegionService regionService;
    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {
        PatrolStoreStatisticsUserExportRequest request = (PatrolStoreStatisticsUserExportRequest) fileExportBaseRequest;
        if(request.getBeginDate() == null || request.getEndDate() == null){
            throw new ServiceException("开始时间和结束时间不能为空");
        }
        Integer days = DateUtil.getBetweenDays(request.getBeginDate().getTime(),request.getEndDate().getTime());
        if(days>60){
            throw new ServiceException("不能导出超过两个月的数据");
        }
        if(CollectionUtils.emptyIfNull(request.getUserIdList()).size()>500){
            throw new ServiceException("暂不支持导出500人以上的数据");
        }
    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        PatrolStoreStatisticsUserExportRequest exportRequest = (PatrolStoreStatisticsUserExportRequest) request;
        List<String> userIdList = exportRequest.getUserIdList();
        userIdList = enterpriseUserMapper.selectUserIdsByUserList(enterpriseId,userIdList);
        return new Long(userIdList.size());
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_USER;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        PatrolStoreStatisticsUserExportRequest exportRequest = JSONObject.toJavaObject(request,PatrolStoreStatisticsUserExportRequest.class);
        List<String> userIdList = exportRequest.getUserIdList();
        PageHelper.startPage(pageNum,pageSize,false);
        userIdList = enterpriseUserMapper.selectUserIdsByUserList(enterpriseId,userIdList);
        if(CollectionUtils.isEmpty(userIdList)){
            return new ArrayList<>();
        }
        Date endDate = exportRequest.getEndDate();
        Date beginDate = exportRequest.getBeginDate();

        List<TbMetaTableDO> tableList =
                tbMetaTableMapper.getTableByCreateUserId(enterpriseId, userIdList, beginDate, endDate);
        List<Long> tableIdList = tableList.stream().map(TbMetaTableDO::getId).collect(Collectors.toList());
        Map<String, List<TbMetaTableDO>> tableMap =
                tableList.stream().collect(Collectors.groupingBy(TbMetaTableDO::getCreateUserId));

        List<PatrolStoreStatisticsUserDTO> recordList  =
                tbDataTableMapper.getListByMetaTableIdListAndTimeGroupBy(enterpriseId, tableIdList, beginDate, endDate);

        Map<Long, Integer> recordCountMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(recordList)){
            recordCountMap = recordList.stream().collect(Collectors.toMap(
                    PatrolStoreStatisticsUserDTO::getMetaTableId, PatrolStoreStatisticsUserDTO::getPatrolNum, (a, b) -> a));
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
        resultList.forEach(dto -> {
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
            String userName = userMap.get(dto.getUserId());
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
        return resultList;
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
}
