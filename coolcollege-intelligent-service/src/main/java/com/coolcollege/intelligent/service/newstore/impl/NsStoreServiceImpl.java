package com.coolcollege.intelligent.service.newstore.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.CommonConstant;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.newstore.NsStoreStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.newstore.dao.NsHandoverHistoryDao;
import com.coolcollege.intelligent.dao.newstore.dao.NsStoreDao;
import com.coolcollege.intelligent.dao.newstore.dao.NsVisitRecordDao;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.export.request.NsStoreExportStatisticsRequest;
import com.coolcollege.intelligent.model.export.request.NsStoreListExportRequest;
import com.coolcollege.intelligent.model.newstore.NsHandoverHistoryDO;
import com.coolcollege.intelligent.model.newstore.NsStoreDO;
import com.coolcollege.intelligent.model.newstore.dto.NsCommonNumDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsStoreDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsVisitNumDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsVisitRecordCorrectionDTO;
import com.coolcollege.intelligent.model.newstore.request.NsBatchHandoverRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreAddOrUpdateRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreGetStatisticsRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreListRequest;
import com.coolcollege.intelligent.model.newstore.vo.NsHandoverHistoryVO;
import com.coolcollege.intelligent.model.newstore.vo.NsStoreGetStatisticsVO;
import com.coolcollege.intelligent.model.newstore.vo.NsStoreVO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.newstore.NsStoreService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangnan
 * @description: 新店管理ServiceImpl
 * @date 2022/3/6 9:53 PM
 */
@Service
@Slf4j
public class NsStoreServiceImpl implements NsStoreService {

    @Resource
    private NsStoreDao nsStoreDao;
    @Resource
    private NsHandoverHistoryDao nsHandoverHistoryDao;
    @Resource
    private NsVisitRecordDao nsVisitRecordDao;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private RegionService regionService;

    @Override
    public void batchHandOver(String enterpriseId, NsBatchHandoverRequest request, CurrentUser currentUser) {
        if(Objects.isNull(request) || CollectionUtils.isEmpty(request.getNewStoreIds())) {
            return;
        }
        // 查询交接人
        EnterpriseUserDO newDirectUser = enterpriseUserDao.selectByUserId(enterpriseId, request.getNewDirectUserId());
        if(Objects.isNull(newDirectUser)) {
            throw new ServiceException(ErrorCodeEnum.USER_INFO_ERROR);
        }
        // 查询交接门店列表，没查到不处理
        List<NsStoreDO> nsStoreDOList = nsStoreDao.selectByIds(enterpriseId, request.getNewStoreIds());
        if(CollectionUtils.isEmpty(nsStoreDOList)) {
            return;
        }
        List<NsHandoverHistoryDO> handoverHistoryDOList = Lists.newArrayList();
        for (NsStoreDO newStoreDO : nsStoreDOList) {
            // 组装交接记录
            NsHandoverHistoryDO handoverHistoryDO = new NsHandoverHistoryDO();
            handoverHistoryDO.setOldDirectUserId(newStoreDO.getDirectUserId());
            handoverHistoryDO.setOldDirectUserName(newStoreDO.getDirectUserName());
            handoverHistoryDO.setNewDirectUserId(newDirectUser.getUserId());
            handoverHistoryDO.setNewDirectUserName(newDirectUser.getName());
            handoverHistoryDO.setNewStoreId(newStoreDO.getId());
            handoverHistoryDO.setCreateTime(new Date());
            handoverHistoryDO.setCreateUserId(currentUser.getUserId());
            handoverHistoryDO.setCreateUserName(currentUser.getName());
            handoverHistoryDOList.add(handoverHistoryDO);
            // 新店负责人交接数据组装
            newStoreDO.setDirectUserId(newDirectUser.getUserId());
            newStoreDO.setDirectUserName(newDirectUser.getName());
            newStoreDO.setUpdateTime(new Date());
            newStoreDO.setUpdateUserId(currentUser.getUserId());
            newStoreDO.setUpdateUserName(currentUser.getName());
        }
        // 批量更新新店
        nsStoreDao.batchUpdate(enterpriseId, nsStoreDOList);
        // 保存交接记录数据
        nsHandoverHistoryDao.batchInsert(enterpriseId, handoverHistoryDOList);
    }

    @Override
    public PageInfo<NsHandoverHistoryVO> getHandOverHistoryList(String enterpriseId, Integer pageNum, Integer pageSize) {
        PageInfo<NsHandoverHistoryDO> doPageInfo = nsHandoverHistoryDao.selectPage(enterpriseId, pageNum, pageSize);
        PageInfo<NsHandoverHistoryVO> voPageInfo = new PageInfo<>();
        voPageInfo.setTotal(doPageInfo.getTotal());
        voPageInfo.setPageNum(doPageInfo.getPageNum());
        voPageInfo.setPageSize(doPageInfo.getPageSize());
        voPageInfo.setPages(doPageInfo.getPages());
        if(CollectionUtils.isEmpty(doPageInfo.getList())) {
            voPageInfo.setList(Lists.newArrayList());
            return voPageInfo;
        }
        // 根据新店ids查询新店, 获取新店名称
        List<Long> newStoreIds = doPageInfo.getList().stream().map(NsHandoverHistoryDO::getNewStoreId).collect(Collectors.toList());
        List<NsStoreDO> storeDOList = nsStoreDao.selectByIds(enterpriseId, newStoreIds);
        Map<Long, String> storeNameMap = storeDOList.stream().collect(Collectors.toMap(NsStoreDO::getId, NsStoreDO::getName));
        List<NsHandoverHistoryVO> voList = Lists.newArrayList();
        for (NsHandoverHistoryDO handoverHistoryDO : doPageInfo.getList()) {
            NsHandoverHistoryVO nsHandoverHistoryVO = new NsHandoverHistoryVO();
            nsHandoverHistoryVO.setId(handoverHistoryDO.getId());
            nsHandoverHistoryVO.setOldDirectUserId(handoverHistoryDO.getOldDirectUserId());
            nsHandoverHistoryVO.setOldDirectUserName(handoverHistoryDO.getOldDirectUserName());
            nsHandoverHistoryVO.setNewDirectUserId(handoverHistoryDO.getNewDirectUserId());
            nsHandoverHistoryVO.setNewDirectUserName(handoverHistoryDO.getNewDirectUserName());
            nsHandoverHistoryVO.setNewStoreId(handoverHistoryDO.getNewStoreId());
            nsHandoverHistoryVO.setNewStoreName(storeNameMap.get(handoverHistoryDO.getNewStoreId()));
            nsHandoverHistoryVO.setCreateTime(handoverHistoryDO.getCreateTime());
            nsHandoverHistoryVO.setCreateUserId(handoverHistoryDO.getCreateUserId());
            nsHandoverHistoryVO.setCreateUserName(handoverHistoryDO.getCreateUserName());
            voList.add(nsHandoverHistoryVO);
        }
        voPageInfo.setList(voList);
        return voPageInfo;
    }

    @Override
    public List<NsStoreGetStatisticsVO> getStatistics(String enterpriseId, NsStoreGetStatisticsRequest request) {
        Date beginDate = new Date(request.getBeginDate());
        Date endDate = new Date(request.getEndDate());
        // 日期限制 31天
        DateUtils.checkDayInterval(request.getBeginDate(), request.getEndDate(), Constants.NEW_STORE_STATISTICS_DAYS);
        // 查询门店数量
        List<NsCommonNumDTO> storeCounts = nsStoreDao.selectCountByStoreTypeAndStatus(enterpriseId, beginDate, endDate,
                request.getRegionId());
        // 没有门店直接返回
        if(CollectionUtils.isEmpty(storeCounts)) {
            return Lists.newArrayList();
        }
        // 查询拜访次数
        List<NsCommonNumDTO> visitCounts = nsVisitRecordDao.selectNumByStoreTypeAndStatus(enterpriseId, beginDate, endDate,
                request.getRegionId());
        // 拜访列表根据新店类型转map，用作组装数据
        Map<String, NsCommonNumDTO> visitCountMap = visitCounts.stream().collect(Collectors.toMap(NsCommonNumDTO::getNewStoreType, Function.identity()));
        List<NsStoreGetStatisticsVO> statisticsVOList = Lists.newArrayList();
        for (NsCommonNumDTO storeCount : storeCounts) {
            NsStoreGetStatisticsVO statisticsVO = new NsStoreGetStatisticsVO();
            statisticsVO.setNewStoreType(storeCount.getNewStoreType());
            statisticsVO.setOngoingStoreNum(Optional.ofNullable(storeCount.getOngoingNum()).orElse(Constants.ZERO));
            statisticsVO.setCompletedStoreNum(Optional.ofNullable(storeCount.getCompletedNum()).orElse(Constants.ZERO));
            statisticsVO.setFailedStoreNum(Optional.ofNullable(storeCount.getFailedNum()).orElse(Constants.ZERO));
            // 根据新店类型获取拜访次数
            NsCommonNumDTO visitCount = visitCountMap.get(storeCount.getNewStoreType());
            // 如果没有查到拜访次数，拜访次数均为0
            if(Objects.isNull(visitCount)) {
                visitCount = new NsCommonNumDTO();
            }
            statisticsVO.setOngoingVisitNum(Optional.ofNullable(visitCount.getOngoingNum()).orElse(Constants.ZERO));
            statisticsVO.setCompletedVisitNum(Optional.ofNullable(visitCount.getCompletedNum()).orElse(Constants.ZERO));
            statisticsVO.setFailedVisitNum(Optional.ofNullable(visitCount.getFailedNum()).orElse(Constants.ZERO));
            statisticsVOList.add(statisticsVO);
        }
        return statisticsVOList;
    }

    @Override
    public PageInfo<NsStoreVO> getNsStoreList(String enterpriseId, NsStoreListRequest request) {
        PageInfo<NsStoreDTO> nsStoreDOPageInfo = nsStoreDao.selectRecordDOPage(enterpriseId, request);
        PageInfo<NsStoreVO> voPageInfo = new PageInfo<>();
        voPageInfo.setTotal(nsStoreDOPageInfo.getTotal());
        voPageInfo.setPageNum(nsStoreDOPageInfo.getPageNum());
        voPageInfo.setPageSize(nsStoreDOPageInfo.getPageSize());
        voPageInfo.setPages(nsStoreDOPageInfo.getPages());
        if(CollectionUtils.isEmpty(nsStoreDOPageInfo.getList())) {
            voPageInfo.setList(Lists.newArrayList());
            return voPageInfo;
        }
        List<String> regionIds = nsStoreDOPageInfo.getList().stream().map(data -> String.valueOf(data.getRegionId())).collect(Collectors.toList());
        List<RegionDO> regionDOList = regionService.getRegionDOsByRegionIds(enterpriseId, regionIds);
        Map<Long, RegionDO> regionDOMap = regionDOList.stream().collect(Collectors.toMap(RegionDO::getId, Function.identity()));
        // 组装新店id列表，查询拜访次数
        List<Long> newStoreIds = nsStoreDOPageInfo.getList().stream().map(NsStoreDTO::getId).collect(Collectors.toList());
        List<NsVisitNumDTO> visitNumDTOList = nsVisitRecordDao.selectNumByStoreIds(enterpriseId, newStoreIds);
        Map<Long, Integer> visitNumMap =  visitNumDTOList.stream().collect(Collectors.toMap(NsVisitNumDTO::getNewStoreId, NsVisitNumDTO::getVisitNum));
        List<NsStoreVO> voList = Lists.newArrayList();
        for (NsStoreDTO nsStoreDTO : nsStoreDOPageInfo.getList()) {
            NsStoreVO nsStoreVO = transToVOByDTO(nsStoreDTO);
            nsStoreVO.setVisitNum(Optional.ofNullable(visitNumMap.get(nsStoreVO.getId())).orElse(Constants.INDEX_ZERO));
            RegionDO regionDO = regionDOMap.get(nsStoreDTO.getRegionId());
            if(!Objects.isNull(regionDO)) {
                nsStoreVO.setRegionName(regionDO.getName());
            }
            voList.add(nsStoreVO);
        }
        voPageInfo.setList(voList);
        return voPageInfo;
    }

    /**
     * 新增新店
     * @param enterpriseId
     * @param nsStoreAddOrUpdateRequest
     * @return
     */
    @Override
    public Long addNsStore(String enterpriseId, NsStoreAddOrUpdateRequest nsStoreAddOrUpdateRequest, CurrentUser user) {
        log.info("addNsStore enterpriseId={},nsStoreAddOrUpdateRequest={}", enterpriseId, JSONObject.toJSONString(nsStoreAddOrUpdateRequest));
        // 字段校验
        checkNsStoreField(enterpriseId, nsStoreAddOrUpdateRequest, CommonConstant.OPER_ADD, user);
        NsStoreDO nsStoreDO = transNsStoreDO(enterpriseId, nsStoreAddOrUpdateRequest, CommonConstant.OPER_ADD);
        nsStoreDO.setAvatar(nsStoreAddOrUpdateRequest.getAvatar());
        nsStoreDO.setDeleted(Boolean.FALSE);
        nsStoreDO.setProgress(Constants.LONG_ZERO);
        nsStoreDO.setStatus(NsStoreStatusEnum.ONGOING.getCode());
        Date now = new Date();
        nsStoreDO.setCreateTime(now);
        nsStoreDO.setCreateDate(now);
        nsStoreDO.setCreateUserId(user.getUserId());
        nsStoreDO.setCreateUserName(user.getName());
        nsStoreDO.setDirectUserId(user.getUserId());
        nsStoreDO.setDirectUserName(user.getName());
        nsStoreDao.insertNsStore(enterpriseId, nsStoreDO);
        return nsStoreDO.getId();
    }
    /**
     * 新店编辑
     * @param enterpriseId
     * @param nsStoreAddOrUpdateRequest
     * @return
     */
    @Override
    public Boolean updateNsStore(String enterpriseId, NsStoreAddOrUpdateRequest nsStoreAddOrUpdateRequest, CurrentUser user) {
        log.info("updateNsStore enterpriseId={},nsStoreAddOrUpdateRequest={}", enterpriseId, JSONObject.toJSONString(nsStoreAddOrUpdateRequest));
        // 字段校验
        checkNsStoreField(enterpriseId, nsStoreAddOrUpdateRequest, CommonConstant.OPER_UPDATE, user);
        NsStoreDO oldNsStore = nsStoreDao.selectById(enterpriseId, nsStoreAddOrUpdateRequest.getId());
        if (Objects.isNull(oldNsStore) || oldNsStore.getDeleted()) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "新店不存在");
        }
        NsStoreDO nsStoreDO = transNsStoreDO(enterpriseId, nsStoreAddOrUpdateRequest, CommonConstant.OPER_UPDATE);
        nsStoreDO.setStatus(nsStoreAddOrUpdateRequest.getStatus());
        Date now = new Date();
        nsStoreDO.setUpdateTime(now);
        nsStoreDO.setUpdateUserId(user.getUserId());
        nsStoreDO.setUpdateUserName(user.getName());
        Boolean result = nsStoreDao.updateNsStore(enterpriseId, nsStoreDO) > 0;
        // 订正拜访记录数据
        this.correctionVisitRecordData(enterpriseId, oldNsStore, nsStoreDO);
        return result;
    }

    @Override
    public void deleteNsStoreById(String eid, Long id){
        nsStoreDao.deleteNsStoreById(eid, id);
    }

    @Override
    public NsStoreVO getNsStoreDetailById(String enterpriseId, Long id) {
        NsStoreDTO nsStoreDTO = nsStoreDao.getNsStoreDTOById(enterpriseId, id);
        // 拜访记录不存在，提示未找到拜访记录
        if(Objects.isNull(nsStoreDTO) || nsStoreDTO.getDeleted()) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_NOT_FOUND);
        }
        // 查询拜访次数
        List<NsVisitNumDTO> visitNumDTOList = nsVisitRecordDao.selectNumByStoreIds(enterpriseId, Lists.newArrayList(id));
        NsStoreVO nsStoreVO = transToVOByDTO(nsStoreDTO);
        nsStoreVO.setVisitNum(CollectionUtils.isEmpty(visitNumDTOList) ? Constants.INDEX_ZERO : visitNumDTOList.get(Constants.INDEX_ZERO).getVisitNum());
        RegionNode regionNode = regionService.getRegionById(enterpriseId, String.valueOf(nsStoreDTO.getRegionId()));
        // 全路径区域名称
        if(!Objects.isNull(regionNode)) {
            nsStoreVO.setRegionName(regionService.getAllRegionName(enterpriseId, nsStoreDTO.getRegionId()).getAllRegionName());
        }
        return nsStoreVO;
    }

    @Override
    public Long getNsStoreCount(String enterpriseId, NsStoreExportStatisticsRequest request) {
        NsStoreListExportRequest queryRequest = new NsStoreListExportRequest();
        queryRequest.setRegionId(request.getRegionId());
        queryRequest.setCreateTimeStart(request.getBeginDate());
        queryRequest.setCreateTimeEnd(request.getEndDate());
        return nsStoreDao.selectStoreCount(enterpriseId, queryRequest);
    }

    private NsStoreVO transToVOByDTO(NsStoreDTO nsStoreDTO) {
        NsStoreVO nsStoreVO = new NsStoreVO();
        nsStoreVO.setId(nsStoreDTO.getId());
        nsStoreVO.setName(nsStoreDTO.getName());
        nsStoreVO.setRegionId(nsStoreDTO.getRegionId());
        nsStoreVO.setType(nsStoreDTO.getType());
        nsStoreVO.setLocationAddress(nsStoreDTO.getLocationAddress());
        nsStoreVO.setContactName(nsStoreDTO.getContactName());
        nsStoreVO.setContactPhone(nsStoreDTO.getContactPhone());
        nsStoreVO.setAvatar(nsStoreDTO.getAvatar());
        nsStoreVO.setStatus(nsStoreDTO.getStatus());
        nsStoreVO.setProgress(nsStoreDTO.getProgress());
        nsStoreVO.setVisitTime(nsStoreDTO.getVisitTime());
        nsStoreVO.setCreateTime(nsStoreDTO.getCreateTime());
        nsStoreVO.setCreateDate(nsStoreDTO.getCreateDate());
        nsStoreVO.setCreateUserName(nsStoreDTO.getCreateUserName());
        nsStoreVO.setDirectUserId(nsStoreDTO.getDirectUserId());
        nsStoreVO.setDirectUserName(nsStoreDTO.getDirectUserName());
        nsStoreVO.setLongitude(nsStoreDTO.getLongitude());
        nsStoreVO.setLatitude(nsStoreDTO.getLatitude());
        return  nsStoreVO;
    }

    private NsStoreDO transNsStoreDO(String enterpriseId, NsStoreAddOrUpdateRequest nsStoreAddOrUpdateRequest, String operFlag) {
        NsStoreDO nsStoreDO = new NsStoreDO();
        String addressPoint = nsStoreAddOrUpdateRequest.getAddressPoint();
        if(StringUtils.isNotBlank(addressPoint)){
            List<String> list = Arrays.asList(addressPoint.split(","));
            nsStoreDO.setAddressPoint("POINT("+list.get(0)+" "+list.get(1)+")");
        }
        // 所属区域id
        Long regionId = nsStoreAddOrUpdateRequest.getRegionId();
        if(CommonConstant.OPER_UPDATE.equals(operFlag)){
            if(regionId != null){
                nsStoreDO.setRegionId(regionId);
                String regionPath = buildRegionPath(enterpriseId, String.valueOf(regionId));
                nsStoreDO.setRegionPath(regionPath);
            }
        }else {
            if(regionId == null){
                nsStoreDO.setRegionId(1L);
                nsStoreDO.setRegionPath("/1/");
            }else {
                nsStoreDO.setRegionId(regionId);
                String regionPath = buildRegionPath(enterpriseId, String.valueOf(regionId));
                nsStoreDO.setRegionPath(regionPath);
            }
        }
        nsStoreDO.setName(nsStoreAddOrUpdateRequest.getName());
        nsStoreDO.setType(nsStoreAddOrUpdateRequest.getType());
        nsStoreDO.setStoreAddress(nsStoreAddOrUpdateRequest.getStoreAddress());
        nsStoreDO.setLocationAddress(nsStoreAddOrUpdateRequest.getLocationAddress());
        nsStoreDO.setContactName(nsStoreAddOrUpdateRequest.getContactName());
        nsStoreDO.setContactPhone(nsStoreAddOrUpdateRequest.getContactPhone());
        nsStoreDO.setId(nsStoreAddOrUpdateRequest.getId());
        return nsStoreDO;
    }

    private String buildRegionPath(String enterpriseId, String areaId) {
        return regionService.getRegionPath(enterpriseId, areaId);
    }

    /**
     * 字段校验
     * @param enterpriseId
     * @param nsStoreAddOrUpdateRequest
     * @param operFlag
     * @param user
     */
    private void checkNsStoreField(String enterpriseId, NsStoreAddOrUpdateRequest nsStoreAddOrUpdateRequest, String operFlag, CurrentUser user) {
        if(nsStoreAddOrUpdateRequest.getRegionId() == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "所属区域不能为空！");
        }else if(StringUtils.isBlank(nsStoreAddOrUpdateRequest.getContactName())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "联系人不能为空！");
        }else if(StringUtils.isBlank(nsStoreAddOrUpdateRequest.getContactPhone())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "联系电话不能为空！");
        }
        // 同一个创建人的门店名称不允许重复
        Long sameStoreNameNum = nsStoreDao.selectCountByCreateUserIdAndStoreName(enterpriseId, user.getUserId(), nsStoreAddOrUpdateRequest.getName(), nsStoreAddOrUpdateRequest.getId());
        if(CommonConstant.OPER_ADD.equals(operFlag)){
            if(Objects.nonNull(sameStoreNameNum) && sameStoreNameNum > Constants.LONG_ZERO) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "门店名称已存在！");
            }
            if(StringUtils.isBlank(nsStoreAddOrUpdateRequest.getName())){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "门店名称不能为空！");
            }
            if(StringUtils.isBlank(nsStoreAddOrUpdateRequest.getType())){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "门店类型不能为空！");
            }
            if(StringUtils.isBlank(nsStoreAddOrUpdateRequest.getAddressPoint()) || StringUtils.isBlank(nsStoreAddOrUpdateRequest.getLocationAddress())){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "GPS定位不能为空！");
            }
            if(StringUtils.isBlank(nsStoreAddOrUpdateRequest.getAvatar())){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "门头照不能为空！");
            }
            if(nsStoreAddOrUpdateRequest.getName().length() > 150){
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "门店名称不能超过150个字符");
            }
        }else if(CommonConstant.OPER_UPDATE.equals(operFlag)){
            if(nsStoreAddOrUpdateRequest.getId() == null){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "新店ID不能为空！");
            }
            if(Objects.nonNull(sameStoreNameNum) && sameStoreNameNum > Constants.LONG_ZERO) {
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "门店名称已存在！");
            }
            if(StringUtils.isBlank(nsStoreAddOrUpdateRequest.getStatus())){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "状态不能为空！");
            }

        }
    }

    /**
     * 订正拜访记录新店相关字段
     * @param enterpriseId 企业id
     * @param oldNsStore NsStoreDO
     * @param nsStoreDO NsStoreDO
     */
    private void correctionVisitRecordData(String enterpriseId, NsStoreDO oldNsStore, NsStoreDO nsStoreDO) {
        NsVisitRecordCorrectionDTO correctionDTO = null;
        if(!oldNsStore.getRegionId().equals(nsStoreDO.getRegionId())
                || !oldNsStore.getName().equals(nsStoreDO.getName())
                || !oldNsStore.getType().equals(nsStoreDO.getType())
                || !oldNsStore.getStatus().equals(nsStoreDO.getStatus())) {
            correctionDTO = new NsVisitRecordCorrectionDTO();
            correctionDTO.setNewStoreId(nsStoreDO.getId());
            correctionDTO.setRegionId(nsStoreDO.getRegionId());
            correctionDTO.setRegionPath(nsStoreDO.getRegionPath());
            correctionDTO.setNewStoreName(nsStoreDO.getName());
            correctionDTO.setNewStoreType(nsStoreDO.getType());
            correctionDTO.setNewStoreStatus(nsStoreDO.getStatus());
        }
        nsVisitRecordDao.updateNewStoreInfoByNewStoreId(enterpriseId, correctionDTO);
    }

}
