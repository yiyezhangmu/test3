package com.coolcollege.intelligent.service.patrolstore.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.MapUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbPatrolStorePlanDao;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStorePlanDO;
import com.coolcollege.intelligent.model.patrolstore.dto.*;
import com.coolcollege.intelligent.model.region.dto.AuthStoreCountDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.SubordinateMappingService;
import com.coolcollege.intelligent.service.patrolstore.PatrolStorePlanService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_DAY;
import static java.util.Comparator.naturalOrder;

/**
 * @author byd
 * @date 2023-07-11 15:23
 */
@Service
public class PatrolStorePlanServiceImpl implements PatrolStorePlanService {

    @Resource
    private TbPatrolStorePlanDao patrolStorePlanDao;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private SubordinateMappingService subordinateMappingService;

    @Autowired
    private AuthVisualService visualService;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Override
    public TbPatrolStorePlanCountDTO planInfo(String eid, String userId, String planDate, String longitude, String latitude) {
        TbPatrolStorePlanCountDTO planCountDTO = new TbPatrolStorePlanCountDTO();
        planCountDTO.setPlanDate(planDate);
        planCountDTO.setTodayPatrolStoreNum(0L);
        planCountDTO.setTodayStoreNum(0L);
        planCountDTO.setWeekPatrolStoreNum(0L);
        planCountDTO.setWeekStoreNum(0L);
        TbPatrolStoreCountDTO dayCount = patrolStorePlanDao.getPlanCount(eid, userId, planDate, planDate);
        if(dayCount != null){
            planCountDTO.setTodayPatrolStoreNum(dayCount.getPatrolStoreNum());
            planCountDTO.setTodayStoreNum(dayCount.getStoreNum());
        }
        DateTime weekBeginDay = DateUtil.beginOfWeek(DateUtil.parseDate(planDate));
        DateTime weekEndDay = DateUtil.endOfWeek(DateUtil.parseDate(planDate));
        TbPatrolStoreCountDTO weekCount = patrolStorePlanDao.getPlanCount(eid, userId, weekBeginDay.toDateStr(), weekEndDay.toDateStr());
        if (weekCount != null) {
            planCountDTO.setWeekPatrolStoreNum(weekCount.getPatrolStoreNum());
            planCountDTO.setWeekStoreNum(weekCount.getStoreNum());
        }
        List<TbPatrolStorePlanDO> storePlanDOList = patrolStorePlanDao.getPlanList(eid, userId, planDate);
        List<TbPatrolStorePlanDTO> patrolStorePlanDTOList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(storePlanDOList)){
            List<String> storeIdList = storePlanDOList.stream().map(TbPatrolStorePlanDO::getStoreId).collect(Collectors.toList());
            DateTime monthBeginDay = DateUtil.beginOfMonth(DateUtil.parseDate(planDate));
            DateTime monthEndDay = DateUtil.endOfMonth(DateUtil.parseDate(planDate));
            List<TbPatrolStoreCountDTO> storeCountList = patrolStorePlanDao.getPlanTimesCount(eid, userId, monthBeginDay.toDateStr(), monthEndDay.toDateStr(), storeIdList);
            Map<String, TbPatrolStoreCountDTO> storeCountMap = ListUtils.emptyIfNull(storeCountList).stream()
                    .collect(Collectors.toMap(TbPatrolStoreCountDTO::getStoreId, data -> data, (a, b) -> a));

            List<StoreDO> storeDOList = storeMapper.getByStoreIdList(eid, storeIdList);
            Map<String, StoreDO> storeIdMap = ListUtils.emptyIfNull(storeDOList).stream()
                    .collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));
            storePlanDOList.forEach(patrolStorePlanDO -> {
                TbPatrolStorePlanDTO tbPatrolStorePlanDTO = new TbPatrolStorePlanDTO();
                tbPatrolStorePlanDTO.setId(patrolStorePlanDO.getId());
                tbPatrolStorePlanDTO.setPlanDate(patrolStorePlanDO.getPlanDate());
                tbPatrolStorePlanDTO.setStoreId(patrolStorePlanDO.getStoreId());
                tbPatrolStorePlanDTO.setStoreName(patrolStorePlanDO.getStoreName());
                tbPatrolStorePlanDTO.setSupervisorId(patrolStorePlanDO.getSupervisorId());
                tbPatrolStorePlanDTO.setSupervisorName(patrolStorePlanDO.getSupervisorName());
                tbPatrolStorePlanDTO.setStatus(patrolStorePlanDO.getStatus());
                StoreDO storeDO = storeIdMap.get(patrolStorePlanDO.getStoreId());
                if(storeDO != null){
                    tbPatrolStorePlanDTO.setStoreNum(storeDO.getStoreNum());
                    tbPatrolStorePlanDTO.setAvatar(storeDO.getAvatar());
                    tbPatrolStorePlanDTO.setLongitude(storeDO.getLongitude());
                    tbPatrolStorePlanDTO.setLatitude(storeDO.getLatitude());
                    tbPatrolStorePlanDTO.setLocationAddress(storeDO.getLocationAddress());
                    if(!StringUtils.isAnyBlank(longitude, latitude, storeDO.getLongitude(), storeDO.getLatitude())){
                        double distance = MapUtil.distance2(Double.parseDouble(longitude), Double.parseDouble(latitude),
                                Double.parseDouble(storeDO.getLongitude()), Double.parseDouble(storeDO.getLatitude()));
                        tbPatrolStorePlanDTO.setDistance(distance);
                    }
                }
                TbPatrolStoreCountDTO storeMonthCountDTO =  storeCountMap.get(patrolStorePlanDO.getStoreId());
                if(storeMonthCountDTO != null){
                    tbPatrolStorePlanDTO.setMonthPatrolNum(storeMonthCountDTO.getStoreNum());
                }
                patrolStorePlanDTOList.add(tbPatrolStorePlanDTO);
            });
            patrolStorePlanDTOList.sort(Comparator.comparing(TbPatrolStorePlanDTO::getDistance, Comparator.nullsLast(naturalOrder())));
            planCountDTO.setPlanStoreList(patrolStorePlanDTOList);
        }
        return planCountDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addPlanStore(String eid, String userId, TbPatrolStorePlanAddDTO patrolStorePlanAddDTO) {
        String supervisorId = patrolStorePlanAddDTO.getUserId();
        EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(eid, supervisorId);
        String supervisorName = enterpriseUserDO.getName();
        Date date = com.coolcollege.intelligent.common.util.DateUtil.parse(patrolStorePlanAddDTO.getPlanDate(), DateUtils.DATE_FORMAT_DAY);
        patrolStorePlanAddDTO.getStoreIdList().forEach(storeId -> {
            TbPatrolStorePlanDO patrolStorePlanDO = new TbPatrolStorePlanDO();
            patrolStorePlanDO.setPlanDate(date);
            patrolStorePlanDO.setStoreId(storeId);
            StoreDO storeDO = storeMapper.getByStoreId(eid, storeId);
            if (storeDO != null) {
                TbPatrolStorePlanDO patrolStorePlanCheck = patrolStorePlanDao.getPlanByUserId(eid, supervisorId, patrolStorePlanAddDTO.getPlanDate(), storeId);
                if(patrolStorePlanCheck != null){
                    throw new ServiceException(ErrorCodeEnum.STORE_APPROVE_PLAN.getCode(), storeDO.getStoreName() + "已存在，不能重复添加");
                }
                patrolStorePlanDO.setStoreName(storeDO.getStoreName());
                patrolStorePlanDO.setRegionId(storeDO.getRegionId());
                patrolStorePlanDO.setRegionPath(storeDO.getRegionPath());
                patrolStorePlanDO.setSupervisorId(supervisorId);
                patrolStorePlanDO.setSupervisorName(supervisorName);
                patrolStorePlanDO.setStatus(Constants.INDEX_ZERO);
                patrolStorePlanDO.setDeleted(false);
                patrolStorePlanDO.setCreateUserId(userId);
                patrolStorePlanDao.insertSelective(eid, patrolStorePlanDO);
            }
        });
    }

    @Override
    public void removePlanStore(String eid, Long planId) {
        TbPatrolStorePlanDO patrolStorePlanDO = new TbPatrolStorePlanDO();
        patrolStorePlanDO.setId(planId);
        patrolStorePlanDO.setDeleted(true);
        patrolStorePlanDao.updateByPrimaryKeySelective(eid, patrolStorePlanDO);
    }

    @Override
    public List<TbPatrolStorePeopleCountDTO> userRangeReportList(String eid, TbPatrolStorePeopleDTO patrolStorePeopleDTO) {
        List<String> userIdList = patrolStorePeopleDTO.getUserIdList();
        if(CollectionUtils.isEmpty(userIdList)){
            Boolean flag = subordinateMappingService.checkHaveAllSubordinateUser(eid, patrolStorePeopleDTO.getUserId());
            if(flag){
                //全部则默认取30条
                PageHelper.startPage(Constants.INDEX_ONE, Constants.INDEX_FORTY, false);
                userIdList = enterpriseUserMapper.selectAllUserIdsByActive(eid, true);
            }else {
                userIdList = subordinateMappingService.getSubordinateUserIdList(eid, patrolStorePeopleDTO.getUserId(), false);
            }
        }

        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        if (userIdList.size() > Constants.ONE_HUNDRED) {
            userIdList = userIdList.subList(0, Constants.ONE_HUNDRED);
        }
        List<EnterpriseUserDO> userDOList = enterpriseUserMapper.selectUsersByUserIds(eid, userIdList);

        userDOList = ListUtils.emptyIfNull(userDOList)
                .stream().filter(EnterpriseUserDO::getActive).collect(Collectors.toList());

        userIdList = userDOList.stream().map(EnterpriseUserDO::getUserId).collect(Collectors.toList());

        if (userIdList.size() > Constants.INDEX_THIRTY) {
            userIdList = userIdList.subList(0, Constants.INDEX_THIRTY);
        }

        List<AuthStoreCountDTO> authStoreCountList = visualService.authStoreCount(eid, userIdList, false);
        Map<String, AuthStoreCountDTO> storeCountMap = ListUtils.emptyIfNull(authStoreCountList)
                .stream()
                .collect(Collectors.toMap(AuthStoreCountDTO::getUserId, data -> data, (a, b) -> a));

        List<TbPatrolStoreCountDTO> storeCountDTOList = patrolStorePlanDao.getPlanPeopleTimesCount(eid, DateUtils.convertTimeToString(patrolStorePeopleDTO.getBeginTime(),
                DateUtils.DATE_FORMAT_DAY), DateUtils.convertTimeToString(patrolStorePeopleDTO.getEndTime(), DateUtils.DATE_FORMAT_DAY), userIdList);
        Map<String, TbPatrolStoreCountDTO> patrolStoreCountMap = ListUtils.emptyIfNull(storeCountDTOList)
                .stream()
                .collect(Collectors.toMap(TbPatrolStoreCountDTO::getSupervisorId, data -> data, (a, b) -> a));



        Map<String, EnterpriseUserDO> userMap = ListUtils.emptyIfNull(userDOList)
                .stream()
                .collect(Collectors.toMap(EnterpriseUserDO::getUserId, data -> data, (a, b) -> a));

        List<TbPatrolStorePeopleCountDTO> result = new ArrayList<>();

        userIdList.forEach(userIdCount -> {
            TbPatrolStorePeopleCountDTO peopleCountDTO = new TbPatrolStorePeopleCountDTO();
            peopleCountDTO.setUserId(userIdCount);
            if (MapUtils.isNotEmpty(storeCountMap) && storeCountMap.get(userIdCount) != null) {
                AuthStoreCountDTO authStoreCountDTO = storeCountMap.get(userIdCount);
                if (authStoreCountDTO.getStoreCount() != null) {
                    peopleCountDTO.setStoreCount(authStoreCountDTO.getStoreCount());
                } else {
                    peopleCountDTO.setStoreCount(0);
                }
            }
            if(peopleCountDTO.getStoreCount() == null){
                peopleCountDTO.setStoreCount(0);
            }
            TbPatrolStoreCountDTO patrolStoreCountDTO = patrolStoreCountMap.get(userIdCount);
            if (patrolStoreCountDTO != null) {
                peopleCountDTO.setPlanStoreCount(patrolStoreCountDTO.getStoreNum());
                peopleCountDTO.setCompleteStoreCount(patrolStoreCountDTO.getPatrolStoreNum());
            } else {
                peopleCountDTO.setPlanStoreCount(0L);
                peopleCountDTO.setCompleteStoreCount(0L);
            }

            EnterpriseUserDO enterpriseUserDO = userMap.get(userIdCount);
            if (enterpriseUserDO != null) {
                peopleCountDTO.setUserName(enterpriseUserDO.getName());
                peopleCountDTO.setJobNum(enterpriseUserDO.getJobnumber());
            }
            result.add(peopleCountDTO);
        });
        //管辖门店
        return result;
    }
}
