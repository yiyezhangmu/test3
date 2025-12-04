package com.coolcollege.intelligent.service.achievement.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.achievement.AchievementTargetMonthEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.DataPageHelper;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.achievement.AchievementTargetDetailMapper;
import com.coolcollege.intelligent.dao.achievement.AchievementTargetMapper;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTargetDTO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTargetDetailDTO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDetailDO;
import com.coolcollege.intelligent.model.achievement.request.AchievementTargetExportRequest;
import com.coolcollege.intelligent.model.achievement.request.AchievementTargetRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetExportVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetStoreVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetTimeReqVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetVO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.enums.AchievementErrorEnum;
import com.coolcollege.intelligent.model.enums.AchievementKeyPrefixEnum;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.AchievementTargetService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.enums.ErrorCodeEnum.ACH_TARGET_STORE_NOT_EXIST;

/**
 * @Description: 业绩类型服务
 * @Author: mao
 * @CreateDate: 2021/5/21 11:12
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AchievementTargetServiceImpl implements AchievementTargetService {

    private final StoreMapper storeMapper;

    private final AchievementTargetMapper achievementTargetMapper;

    private final AchievementTargetDetailMapper achievementTargetDetailMapper;

    private final UserAuthMappingMapper userAuthMappingMapper;

    private final RegionMapper regionMapper;

    private final ExportUtil exportUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AchievementTargetVO saveAchievementTarget(String enterpriseId, AchievementTargetVO req, CurrentUser user) {
        checkSaveParam(req, true);
        StoreDO storeDO = getStoreById(enterpriseId, req.getStoreId());
        if(storeDO==null){
            throw new ServiceException(ACH_TARGET_STORE_NOT_EXIST);
        }
        req.setStoreName(storeDO.getStoreName());
        AchievementTargetDO achievementTargetDO = getAchievementTargetDO(req);
        achievementTargetDO.setStoreName(storeDO.getStoreName());
        achievementTargetDO.setStoreNum(storeDO.getStoreNum());
        achievementTargetDO.setRegionId(storeDO.getRegionId());
        achievementTargetDO.setRegionPath(storeDO.getRegionPath());
        achievementTargetDO.setCreateUserId(user.getUserId());
        achievementTargetDO.setCreateUserName(user.getName());
        achievementTargetDO.setUpdateUserId(user.getUserId());
        achievementTargetDO.setUpdateUserName(user.getName());
        int count = achievementTargetMapper.insertAchievementTarget(enterpriseId, achievementTargetDO);
        if (count <= 0) {
            throw new ServiceException(AchievementErrorEnum.TARGET_ADD_UNI.code,
                AchievementErrorEnum.TARGET_ADD_UNI.message);
        }
        req.setId(achievementTargetDO.getId());
        List<AchievementTargetDetailDO> details = ListUtils.emptyIfNull(req.getTargetDetail()).stream().map(t -> {
            AchievementTargetDetailDO targetDetail = getAchievementTargetDetailDO(achievementTargetDO);
            targetDetail.setStoreNum(storeDO.getStoreNum());
            targetDetail.setAchievementTarget(t.getAchievementTarget());
            targetDetail.setBeginDate(DateUtil.getFirstOfDayMonth(t.getBeginDate()));
            targetDetail.setEndDate(DateUtil.getLastOfDayMonth(t.getBeginDate()));
            targetDetail.setTimeType(AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type);
            return targetDetail;
        }).collect(Collectors.toList());
        achievementTargetDetailMapper.insertBatchTargetDetail(enterpriseId, details);
        List<AchievementTargetTimeReqVO> resDetails = getResDetails(details);
        req.setTargetDetail(resDetails);
        return req;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AchievementTargetVO updateAchievementTarget(String enterpriseId, AchievementTargetVO req, CurrentUser user) {
        checkSaveParam(req, false);
        StoreDO storeDO = getStoreById(enterpriseId, req.getStoreId());
        if (Objects.isNull(storeDO)) {
            throw new ServiceException(AchievementErrorEnum.TARGET_STORE_NULL.code,
                AchievementErrorEnum.TARGET_STORE_NULL.message);
        }
        if (!findStoreAuth(enterpriseId, user.getUserId(), storeDO.getStoreId(), storeDO.getRegionPath(),
            user.getRoleAuth())) {
            throw new ServiceException(AchievementErrorEnum.TARGET_AUH_FAIL.code,
                AchievementErrorEnum.TARGET_AUH_FAIL.message);
        }
        AchievementTargetDO achievementTargetDO = getAchievementTargetDO(req);
        Date editTime = new Date();
        achievementTargetDO.setEditTime(editTime);
        achievementTargetDO.setUpdateUserId(user.getUserId());
        achievementTargetDO.setUpdateUserName(user.getName());
        achievementTargetDO.setStoreNum(storeDO.getStoreNum());
        achievementTargetDO.setStoreName(storeDO.getStoreName());
        achievementTargetDO.setRegionId(storeDO.getRegionId());
        achievementTargetDO.setRegionPath(storeDO.getRegionPath());

        if(achievementTargetDO.getId() == null){
            achievementTargetDO.setCreateTime(editTime);
            achievementTargetDO.setCreateUserId(user.getUserId());
            achievementTargetDO.setCreateUserName(user.getName());
        }
        List<AchievementTargetDetailDO> newList = new ArrayList<>();
        List<AchievementTargetDetailDO> oldList = new ArrayList<>();

        if(achievementTargetDO.getId() == null){
            achievementTargetMapper.insertAchievementTarget(enterpriseId, achievementTargetDO);
        }else {
            achievementTargetMapper.updateTarget(enterpriseId, achievementTargetDO);
        }

        for (AchievementTargetTimeReqVO vo : req.getTargetDetail()) {
            AchievementTargetDetailDO detail = new AchievementTargetDetailDO();
            detail.setBeginDate(vo.getBeginDate());
            detail.setAchievementTarget(vo.getAchievementTarget());
            detail.setEditTime(editTime);
            detail.setUpdateUserId(user.getUserId());
            detail.setUpdateUserName(user.getName());
            detail.setStoreNum(storeDO.getStoreNum());
            detail.setStoreName(storeDO.getStoreName());
            detail.setStoreId(storeDO.getStoreId());
            detail.setRegionId(storeDO.getRegionId());
            detail.setRegionPath(storeDO.getRegionPath());
            if(vo.getId() == null ){
                detail.setCreateUserName(user.getName());
                detail.setCreateUserId(user.getUserId());
                detail.setTargetId(achievementTargetDO.getId());
                detail.setTimeType(AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type);
                detail.setAchievementYear(achievementTargetDO.getAchievementYear());
                detail.setEndDate(DateUtil.getLastOfDayMonth(vo.getBeginDate()));
                detail.setTargetId(achievementTargetDO.getId());
                newList.add(detail);
            }else {
                detail.setId(vo.getId());
                oldList.add(detail);
            }
        }

        if(CollectionUtils.isNotEmpty(oldList)){
            achievementTargetDetailMapper.updateTargetDetailBatch(enterpriseId, oldList);
        }
        if(CollectionUtils.isNotEmpty(newList)){
            achievementTargetDetailMapper.insertBatchTargetDetail(enterpriseId, newList);
        }
        return req;
    }

    @Override
    public PageInfo<AchievementTargetDTO> listTargetPages(String enterpriseId, AchievementTargetVO req) {
        if (Objects.isNull(req.getPageNum()) || Objects.isNull(req.getPageSize())) {
            throw new ServiceException(AchievementErrorEnum.PAGE_NULL.code, AchievementErrorEnum.PAGE_NULL.message);
        }
        String regionPath = null;
        RegionDO regionDO = null;
        if(req.getRegionId() != null){
            regionDO = regionMapper.getByRegionId(enterpriseId, req.getRegionId());
        }
        if(regionDO != null){
            regionPath = regionDO.getFullRegionPath();
        }
        DataPageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<StoreDO> storeDOList = storeMapper.listStoreAndShowCurrent(enterpriseId, req.getStoreName(),
                req.getRegionId(), regionPath, req.getStoreIds(), req.getShowCurrent(),  StoreIsDeleteEnum.EFFECTIVE.getValue());
        if(CollectionUtils.isEmpty(storeDOList)){
            return new PageInfo<>();
        }

        PageInfo pageInfo = new PageInfo<>(storeDOList);
        pageInfo.setList(getList(enterpriseId,req,storeDOList,regionPath));
        return pageInfo;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTarget(String enterpriseId, AchievementTargetVO req, CurrentUser user) {
        if (Objects.isNull(req.getId()) || Objects.isNull(req.getStoreId())) {
            throw new ServiceException(AchievementErrorEnum.TARGET_STORE_ID.code,
                AchievementErrorEnum.TARGET_STORE_ID.message);
        }
        StoreDO storeDO = getStoreById(enterpriseId, req.getStoreId());
        if (!findStoreAuth(enterpriseId, user.getUserId(), storeDO.getStoreId(), storeDO.getRegionPath(),
            user.getRoleAuth())) {
            throw new ServiceException(AchievementErrorEnum.TARGET_AUH_FAIL.code,
                AchievementErrorEnum.TARGET_AUH_FAIL.message);
        }
        achievementTargetMapper.deleteTargetById(enterpriseId, req.getId());
        achievementTargetDetailMapper.deleteDetailByTargetId(enterpriseId, req.getId());
    }

    @Override
    public List<AchievementTargetStoreVO> listByStoreAndTime(String eid, AchievementTargetRequest req) {
        req.setTimeType(AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type);
        req.setBeginDate(DateUtil.getFirstOfDayMonth(req.getBeginDate()));

        List<StoreDO> storeDOList = storeMapper.getByStoreIdList(eid, req.getStoreIds());
        if(CollectionUtils.isEmpty(storeDOList)){
            return new ArrayList<>();
        }

        List<AchievementTargetDetailDO> achievementTargetDetailDOS = achievementTargetDetailMapper.getTargetByStoreAndDate(eid,req);

        Map<String,AchievementTargetDetailDO> achievementTargetDetailDOMap = ListUtils.emptyIfNull(achievementTargetDetailDOS).stream()
                .collect(Collectors.toMap(AchievementTargetDetailDO::getStoreId, data -> data, (a, b) -> a));

        List<AchievementTargetDTO> achievementTargetDTOS = achievementTargetMapper.getByStoreIdsAndYear(eid, req.getStoreIds(), req.getAchievementYear());
        Map<String, AchievementTargetDTO> achievementTargetDTOMap = ListUtils.emptyIfNull(achievementTargetDTOS).stream()
                .collect(Collectors.toMap(AchievementTargetDTO::getStoreId, data -> data, (a, b) -> a));

        return storeDOList.stream().map(e -> {
            AchievementTargetStoreVO vo = new AchievementTargetStoreVO();
            vo.setStoreName(e.getStoreName());
            vo.setStoreId(e.getStoreId());
            vo.setStoreNum(e.getStoreNum());
            if(achievementTargetDetailDOMap.get(e.getStoreId()) != null){
                vo.setId(achievementTargetDetailDOMap.get(e.getStoreId()).getId());
                vo.setAchievementTarget(achievementTargetDetailDOMap.get(e.getStoreId()).getAchievementTarget());
                vo.setTargetId(achievementTargetDetailDOMap.get(e.getStoreId()).getTargetId());
            }
            if(achievementTargetDTOMap.get(e.getStoreId()) != null){
                vo.setYearAchievementTarget(achievementTargetDTOMap.get(e.getStoreId()).getYearAchievementTarget());
                vo.setTargetId(achievementTargetDTOMap.get(e.getStoreId()).getId());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateTargetDetailBatch(String eid, AchievementTargetRequest request,CurrentUser user) {

        List<AchievementTargetDetailDTO> targetDetailList = request.getTargetDetailList();
        if(CollectionUtils.isEmpty(targetDetailList)){
            return;
        }

        List<String> storeIds = request.getTargetDetailList().stream()
                .map(AchievementTargetDetailDTO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeDOList = storeMapper.getByStoreIdList(eid,storeIds);
        Map<String,StoreDO> storeDOMap = ListUtils.emptyIfNull(storeDOList).stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));
        List<AchievementTargetDO> targetNewList = new ArrayList<>();
        List<AchievementTargetDO> targetOldList = new ArrayList<>();

        List<AchievementTargetDetailDO> detailNewList = new ArrayList<>();
        List<AchievementTargetDetailDO> detailOldList = new ArrayList<>();
        Date NowDate = new Date();
        for (AchievementTargetDetailDTO dto : targetDetailList) {
            //如果目标为空，则跳过
            if(StringUtils.isEmpty(dto.getAchievementTarget())){
                continue;
            }
            if(storeDOMap.get(dto.getStoreId()) == null) {
                continue;
            }
                AchievementTargetDO targetDO = new AchievementTargetDO();
            targetDO.setId(dto.getTargetId());
            targetDO.setUpdateUserId(user.getUserId());
            targetDO.setUpdateUserName(user.getName());
            targetDO.setYearAchievementTarget(dto.getYearAchievementTarget());
            targetDO.setEditTime(NowDate);
            targetDO.setStoreId(dto.getStoreId());
            targetDO.setStoreName(storeDOMap.get(dto.getStoreId()).getStoreName());
            targetDO.setStoreNum(storeDOMap.get(dto.getStoreId()).getStoreNum());
            targetDO.setRegionPath(storeDOMap.get(dto.getStoreId()).getRegionPath());
            targetDO.setRegionId(storeDOMap.get(dto.getStoreId()).getRegionId());
            if(dto.getTargetId() == null){
                targetDO.setAchievementYear(request.getAchievementYear());

                targetDO.setCreateTime(NowDate);
                targetDO.setCreateUserId(user.getUserId());
                targetDO.setCreateUserName(user.getName());

                targetNewList.add(targetDO);
            }else {
                targetOldList.add(targetDO);
            }
        }

        if(CollectionUtils.isNotEmpty(targetNewList)){
            achievementTargetMapper.batchInsertAchievementTarget(eid, targetNewList);
        }
        if(CollectionUtils.isNotEmpty(targetOldList)){
            achievementTargetMapper.updateTargetBatch(eid,targetOldList);
        }
        List<AchievementTargetDTO> achievementTargetDTOList = achievementTargetMapper.getByStoreIdsAndYear(eid, storeIds, request.getAchievementYear());
        Map<String,AchievementTargetDTO> achievementTargetDOMap = ListUtils.emptyIfNull(achievementTargetDTOList).stream()
                .collect(Collectors.toMap(AchievementTargetDTO::getStoreId,data -> data,(a,b) -> a));

        for (AchievementTargetDetailDTO dto : targetDetailList) {
            //如果目标为空，则跳过
            if(StringUtils.isEmpty(dto.getAchievementTarget())){
                continue;
            }
            if(storeDOMap.get(dto.getStoreId()) == null){
                continue;
            }
            AchievementTargetDetailDO targetDetailDO = new AchievementTargetDetailDO();
            targetDetailDO.setId(dto.getId());
            targetDetailDO.setTargetId(dto.getTargetId());
            targetDetailDO.setAchievementTarget(dto.getAchievementTarget());
            targetDetailDO.setUpdateUserId(user.getUserId());
            targetDetailDO.setUpdateUserName(user.getName());
            targetDetailDO.setStoreName(storeDOMap.get(dto.getStoreId()).getStoreName());
            targetDetailDO.setStoreNum(storeDOMap.get(dto.getStoreId()).getStoreNum());
            targetDetailDO.setRegionPath(storeDOMap.get(dto.getStoreId()).getRegionPath());
            targetDetailDO.setRegionId(storeDOMap.get(dto.getStoreId()).getRegionId());
            if(dto.getId() == null){
                targetDetailDO.setStoreId(dto.getStoreId());
                targetDetailDO.setAchievementYear(request.getAchievementYear());
                targetDetailDO.setTimeType(AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type);
                targetDetailDO.setBeginDate(DateUtil.getFirstOfDayMonth(request.getBeginDate()));
                targetDetailDO.setEndDate(DateUtil.getLastOfDayMonth(request.getBeginDate()));
                targetDetailDO.setCreateUserId(user.getUserId());
                targetDetailDO.setCreateUserName(user.getName());
                if(achievementTargetDOMap.get(dto.getStoreId()) != null){
                    targetDetailDO.setTargetId(achievementTargetDOMap.get(dto.getStoreId()).getId());
                }
                detailNewList.add(targetDetailDO);
            }else {
                detailOldList.add(targetDetailDO);
            }
        }

        if(CollectionUtils.isNotEmpty(detailOldList)){
            achievementTargetDetailMapper.updateTargetDetailBatch(eid, detailOldList);
        }
        if(CollectionUtils.isNotEmpty(detailNewList)){
            achievementTargetDetailMapper.insertBatchTargetDetail(eid,detailNewList);
        }
    }

    @Override
    public AchievementTargetDTO getByStoreIdAndYear(String eid, String storeId,Integer achievementYear) {

        AchievementTargetDTO achievementTargetDTO = achievementTargetMapper.getByStoreIdAndYear(eid, storeId,achievementYear);
        if(achievementTargetDTO == null){
            return new AchievementTargetDTO();
        }
        List<AchievementTargetDetailDTO> achievementTargetDetailDTOS = achievementTargetMapper.getDetailByTargerId(eid, Collections.singletonList(achievementTargetDTO.getId()));
        achievementTargetDTO.setTargetDetail(achievementTargetDetailDTOS);

        return achievementTargetDTO;
    }

    @Override
    public ImportTaskDO downloadTemplate(String eid, AchievementTargetExportRequest exportRequest, CurrentUser user) {
        exportRequest.setExportServiceEnum(ExportServiceEnum.EXPORT_ACHIEVEMENT_TARGET);
        return exportUtil.exportFile(eid, exportRequest, UserHolder.getUser().getDbName());
    }

    @Override
    public ImportTaskDO exportTemplate(String enterpriseId, AchievementTargetExportRequest request, CurrentUser user) {
        request.setExportServiceEnum(ExportServiceEnum.SONGXIA_EXPORT_ACHIEVEMENT_TARGET);
        return exportUtil.exportFile(enterpriseId, request, UserHolder.getUser().getDbName());
    }

    @Override
    public void importTarget(String eid,List<AchievementTargetDO> achievementTargetDOS, Map<String, List<AchievementTargetDetailDO>> detaiListMap) {
        if(CollectionUtils.isEmpty(achievementTargetDOS)){
            return;
        }
        Lists.partition(achievementTargetDOS, Constants.MAX_INSERT_SIZE).forEach(f -> achievementTargetMapper.batchInsertAchievementTarget(eid,f));

        List<String> storeIds = achievementTargetDOS.stream().map(AchievementTargetDO::getStoreId).collect(Collectors.toList());

        List<AchievementTargetDTO> achievementTargetDTOS = achievementTargetMapper.listTargetQuery(eid, storeIds, null, null, null, null, null,null);

        List<AchievementTargetDetailDO> detailDOList = new ArrayList<>();
        List<AchievementTargetDetailDO> tempList;
        for (AchievementTargetDTO dto : achievementTargetDTOS) {
            tempList = detaiListMap.get(dto.getStoreId() + "-" + dto.getAchievementYear());
            if(CollectionUtils.isEmpty(tempList)){
                continue;
            }
            for (AchievementTargetDetailDO achievementTargetDetailDO : tempList) {
                achievementTargetDetailDO.setTargetId(dto.getId());
                detailDOList.add(achievementTargetDetailDO);
                if(detailDOList.size() > Constants.MAX_INSERT_SIZE){
                    achievementTargetDetailMapper.insertBatchTargetDetail(eid,detailDOList);
                    detailDOList.clear();
                }
            }
        }
        if(CollectionUtils.isNotEmpty(detailDOList)){
            achievementTargetDetailMapper.insertBatchTargetDetail(eid,detailDOList);
        }
    }

    @Override
    public List<AchievementTargetDTO> listTargets(String enterpriseId, AchievementTargetVO req) {
        String regionPath = null;
        RegionDO regionDO = null;
        if(req.getRegionId() != null){
            regionDO = regionMapper.getByRegionId(enterpriseId, req.getRegionId());
        }
        if(regionDO != null){
            regionPath = regionDO.getFullRegionPath();
        }
        List<StoreDO> storeDOList = storeMapper.listStoreAndShowCurrent(enterpriseId, req.getStoreName(),
                req.getRegionId(), regionPath, req.getStoreIds(), req.getShowCurrent(),  StoreIsDeleteEnum.EFFECTIVE.getValue());
        if(CollectionUtils.isEmpty(storeDOList)){
            return new ArrayList<>();
        }

        return getList(enterpriseId,req,storeDOList,regionPath);
    }

    @Override
    public void updateYearAchievementTarget(String eid) {
        achievementTargetMapper.updateYearAchievementTarget(eid);
    }

    public List<AchievementTargetDTO> getList(String enterpriseId, AchievementTargetVO req,List<StoreDO> storeDOList,String regionPath){
        List<String> storeIdList = storeDOList.stream().map(StoreDO::getStoreId).collect(Collectors.toList());

        List<AchievementTargetDTO> targetDTOList = achievementTargetMapper.listTargetQuery(enterpriseId,
                storeIdList, AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type,req.getShowCurrent(),regionPath,req.getAchievementYear(),req.getStoreName(),req.getRegionId());
        Map<String, AchievementTargetDTO> achievementTargetDTOMap = ListUtils.emptyIfNull(targetDTOList).stream().collect(Collectors.toMap(AchievementTargetDTO::getStoreId, data -> data, (a, b) -> a));

        List<Long> targerIdList = targetDTOList.stream().map(AchievementTargetDTO::getId).collect(Collectors.toList());

        List<AchievementTargetDetailDTO> achievementTargetDetailDTOS = achievementTargetMapper.getDetailByTargerId(enterpriseId,targerIdList);
        //目标明细分组
        Map<Long, List<AchievementTargetDetailDTO>> collect = achievementTargetDetailDTOS.stream().collect(Collectors.groupingBy(AchievementTargetDetailDTO::getTargetId));

        //查询区域，获取区域名称
        List<Long> regionIdList = storeDOList.stream().map(StoreDO::getRegionId).collect(Collectors.toList());
        List<RegionDO> regionDOS = regionMapper.listRegionByIds(enterpriseId, regionIdList);

        Map<Long, RegionDO> regionDOMap = ListUtils.emptyIfNull(regionDOS).stream().collect(Collectors.toMap(RegionDO::getId, data -> data, (a, b) -> a));

        //以门店为主
        List<AchievementTargetDTO> resultList = storeDOList.stream().map(e -> {
            AchievementTargetDTO dto = new AchievementTargetDTO();
            dto.setStoreId(e.getStoreId());
            dto.setStoreName(e.getStoreName());
            dto.setStoreNum(e.getStoreNum());
            dto.setAchievementYear(req.getAchievementYear());
            if(regionDOMap.get(e.getRegionId()) != null){
                dto.setRegionName(regionDOMap.get(e.getRegionId()).getName());
            }
            AchievementTargetDTO achievementTargetDTO = achievementTargetDTOMap.get(e.getStoreId());

            List<AchievementTargetDetailDTO> detailDTOS = new ArrayList<>();
            if(achievementTargetDTO != null){
                dto.setId(achievementTargetDTO.getId());
                dto.setYearAchievementTarget(achievementTargetDTO.getYearAchievementTarget());
            }else {
                dto.setYearAchievementTarget(new BigDecimal(0));
            }

            Map<Long, AchievementTargetDetailDTO> achievementTargetDetailDTOMap = new HashMap<>();
            if(achievementTargetDTO != null){
                achievementTargetDetailDTOMap = ListUtils.emptyIfNull(collect.get(achievementTargetDTO.getId())).stream().
                        collect(Collectors.toMap(a -> a.getBeginDate().getTime(), data -> data, (a, b) -> a));
            }
            LocalDate localDate;
            AchievementTargetDetailDTO tempDetail;
            //填充12个月的目标数据，没有数据填充0
            for (int i = 1; i < 13; i++) {
                localDate = LocalDate.of(req.getAchievementYear(), i,1);

                tempDetail = new AchievementTargetDetailDTO();
                tempDetail.setBeginDate(DateUtils.localDate2Date(localDate));
                //如果已设置目标
                if(achievementTargetDetailDTOMap.get(DateUtils.localDate2Date(localDate).getTime()) != null){
                    tempDetail.setAchievementTarget(achievementTargetDetailDTOMap.get(DateUtils.localDate2Date(localDate).getTime()).getAchievementTarget());
                }else {
                    tempDetail.setAchievementTarget(new BigDecimal(0));
                }
                detailDTOS.add(tempDetail);
            }
            dto.setTargetDetail(detailDTOS);
            return dto;
        }).collect(Collectors.toList());

        return resultList;
    }

    /**
     * 判断是否有权限
     *
     * @param enterpriseId
     * @param userId
     * @param storeId
     * @param regionPath
     * @param roleAuth
     * @return boolean
     * @author mao
     * @date 2021/5/28 11:40
     */
    public boolean findStoreAuth(String enterpriseId, String userId, String storeId, String regionPath,
        String roleAuth) {
        if (!StringUtils.isEmpty(roleAuth)) {
            if (AuthRoleEnum.ALL.getCode().equals(roleAuth)) {
                return true;
            }
        }
        List<UserAuthMappingDO> authList = userAuthMappingMapper.listUserAuthMappingByUserId(enterpriseId, userId);
        List<String> regionList = ListUtils.emptyIfNull(authList).stream().filter(s -> "region".equals(s.getType()))
            .map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
        List<String> storeList = ListUtils.emptyIfNull(authList).stream().filter(s -> "store".equals(s.getType()))
            .map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
        if (storeList.contains(storeId)) {
            return true;
        } else {
            if (StringUtils.isEmpty(regionPath)) {
                return false;
            }
            String[] paths = regionPath.split("/");
            for (String s : regionList) {
                boolean find = Arrays.stream(paths).filter(m -> m.equals(s)).findAny().isPresent();
                if (find) {
                    return true;
                }
            }
        }
        return false;
    }

    public void checkSaveParam(AchievementTargetVO req, boolean add) {
        if (StringUtils.isEmpty(req.getStoreId())) {
            throw new ServiceException(AchievementErrorEnum.TARGET_STORE_ID.code,
                AchievementErrorEnum.TARGET_STORE_ID.message);
        }
        if (add) {
            if (Objects.isNull(req.getAchievementYear())) {
                throw new ServiceException(AchievementErrorEnum.TARGET_STORE_YEAR.code,
                    AchievementErrorEnum.TARGET_STORE_YEAR.message);
            } else {
                int year = req.getAchievementYear().intValue();
                if (year < AchievementErrorEnum.TARGET_YEAR_MIN.code
                    || year > AchievementErrorEnum.TARGET_YEAR_MAX.code) {
                    throw new ServiceException(AchievementErrorEnum.TARGET_STORE_YEAR.code,
                        AchievementErrorEnum.TARGET_STORE_YEAR.message);
                }
            }
        }
        if (Objects.isNull(req.getYearAchievementTarget())) {
            throw new ServiceException(AchievementErrorEnum.TARGET_YEAR.code, AchievementErrorEnum.TARGET_YEAR.message);
        }
        if (Objects.isNull(req.getTargetDetail()) || req.getTargetDetail().isEmpty()) {
            throw new ServiceException(AchievementErrorEnum.TARGET_DETAIL_NULL.code,
                AchievementErrorEnum.TARGET_DETAIL_NULL.message);
        }
        for (AchievementTargetTimeReqVO one : req.getTargetDetail()) {
            if (Objects.isNull(one.getBeginDate()) || Objects.isNull(one.getAchievementTarget())) {
                throw new ServiceException(AchievementErrorEnum.TARGET_DETAIL_NULL.code,
                    AchievementErrorEnum.TARGET_DETAIL_NULL.message);
            }
        }
    }

    private AchievementTargetDO getAchievementTargetDO(AchievementTargetVO req) {
        AchievementTargetDO achievementTargetDO = new AchievementTargetDO();
        achievementTargetDO.setId(req.getId());
        achievementTargetDO.setStoreId(req.getStoreId());
        achievementTargetDO.setStoreName(req.getStoreName());
        achievementTargetDO.setAchievementYear(req.getAchievementYear());
        achievementTargetDO.setYearAchievementTarget(req.getYearAchievementTarget());
        return achievementTargetDO;
    }

    private AchievementTargetDetailDO getAchievementTargetDetailDO(AchievementTargetDO achievementTargetDO) {
        AchievementTargetDetailDO targetDetail = new AchievementTargetDetailDO();
        targetDetail.setCreateUserId(achievementTargetDO.getCreateUserId());
        targetDetail.setCreateUserName(achievementTargetDO.getCreateUserName());
        targetDetail.setUpdateUserId(achievementTargetDO.getUpdateUserId());
        targetDetail.setUpdateUserName(achievementTargetDO.getUpdateUserName());
        targetDetail.setTargetId(achievementTargetDO.getId());
        targetDetail.setStoreId(achievementTargetDO.getStoreId());
        targetDetail.setStoreName(achievementTargetDO.getStoreName());
        targetDetail.setRegionId(achievementTargetDO.getRegionId());
        targetDetail.setRegionPath(achievementTargetDO.getRegionPath());
        targetDetail.setAchievementYear(achievementTargetDO.getAchievementYear());
        return targetDetail;
    }

    private List<AchievementTargetTimeReqVO> getResDetails(List<AchievementTargetDetailDO> details) {
        return details.stream().map(t -> {
            AchievementTargetTimeReqVO target = new AchievementTargetTimeReqVO();
            target.setId(t.getId());
            target.setAchievementTarget(t.getAchievementTarget());
            target.setBeginDate(t.getBeginDate());
            return target;
        }).sorted(Comparator.comparing(AchievementTargetTimeReqVO::getBeginDate).reversed())
            .collect(Collectors.toList());
    }

    public StoreDO getStoreById(String enterpriseId, String id) {
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, id);
        if (Objects.isNull(storeDO)) {
            throw new ServiceException(AchievementErrorEnum.TARGET_STORE_NULL.code,
                AchievementErrorEnum.TARGET_STORE_NULL.message);
        }
        storeDO.setRegionPath(StringUtils.isEmpty(storeDO.getRegionPath()) ? "" : storeDO.getRegionPath());
        return storeDO;
    }

}
