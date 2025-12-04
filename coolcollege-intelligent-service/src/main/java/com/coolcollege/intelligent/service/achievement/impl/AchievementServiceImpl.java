package com.coolcollege.intelligent.service.achievement.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.achievement.*;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.achievement.dto.*;
import com.coolcollege.intelligent.model.achievement.entity.*;
import com.coolcollege.intelligent.model.achievement.request.*;
import com.coolcollege.intelligent.model.achievement.vo.*;
import com.coolcollege.intelligent.model.enums.*;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.achievement.AchievementService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/5/20 10:33
 */
@Service
@Slf4j
public class AchievementServiceImpl implements AchievementService {
    @Resource
    private AchievementDetailMapper achievementDetailMapper;
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private AchievementTypeMapper achievementTypeMapper;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private AchievementTargetDetailMapper targetDetailMapper;

    @Resource
    private AchievementFormWorkMapper achievementFormWorkMapper;

    @Resource
    private RegionMapper regionMapper;

    @Autowired
    @Lazy
    private ExportUtil exportUtil;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private AchievementTargetDetailMapper achievement;

    @Resource
    private PanasonicMapper panasonicMapper;

    public static final Integer MAX_PAGE_SIZE = 500;

    @Resource
    private AchievementTaskRecordMapper achievementTaskRecordMapper;
    @Resource
    private TaskSubMapper taskSubMapper;
    @Resource
    private TaskStoreMapper taskStoreMapper;
    @Override
    public AchievementDetailDO uploadAchievementDetail(String enterpriseId, AchievementDetailDO achievementDetailDO, CurrentUser user) {
        StoreDO storeDO = storeMapper.getByStoreId(enterpriseId, achievementDetailDO.getStoreId());
        if (storeDO == null) {
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "门店不存在");
        }
        AchievementTypeDO achievementTypeDO = achievementTypeMapper.getTypeById(enterpriseId, achievementDetailDO.getAchievementTypeId());
        if (achievementTypeDO == null) {
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "业绩分类不存在");
        }
        if (achievementTypeDO.getLocked() == 0) {
            achievementTypeMapper.lockById(enterpriseId, achievementDetailDO.getAchievementTypeId());
        }
        achievementDetailDO.setStoreName(storeDO.getStoreName());
        achievementDetailDO.setRegionId(storeDO.getRegionId());
        String regionPath = storeDO.getRegionPath();
        achievementDetailDO.setRegionPath(regionPath);
        achievementDetailDO.setStoreName(storeDO.getStoreName());
        achievementDetailDO.setCreateTime(new Date(System.currentTimeMillis()));
        achievementDetailDO.setCreateUserId(user.getUserId());
        achievementDetailDO.setCreateUserName(user.getName());
        achievementDetailDO.setDeleted(Boolean.FALSE);
        achievementDetailMapper.batchInsert(enterpriseId, achievementDetailDO);
        return achievementDetailDO;
    }

    @Override
    public Boolean deleteAchievementDetail(String enterpriseId, Long id, CurrentUser user) {
        AchievementDetailDO achievementDetailDO = achievementDetailMapper.selectById(enterpriseId, id);
        if (achievementDetailDO == null) {
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "业绩详情不存在");
        }
        //获取七天前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 7);
        Date lastTime = calendar.getTime();
        if (achievementDetailDO.getCreateTime().compareTo(lastTime) < 0) {
            throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "仅能删除7天内上传的记录");
        }
        if (!AuthRoleEnum.ALL.getCode().equals(user.getRoleAuth())) {
            if (!user.getUserId().equals(achievementDetailDO.getCreateUserId())) {
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "仅能删除自己上传的记录");
            }
        }
        achievementDetailMapper.deleteAchievementDetail(enterpriseId, id);
        return Boolean.TRUE;
    }

    @Override
    public PageInfo<List<AchievementDetailListDTO>> listAchievementDetail(String enterpriseId, AchievementDetailListRequest request, CurrentUser user) {
        List<AchievementDetailListDTO> result = new ArrayList<>();
        String createUserId = null;
        //用户权限为仅自己时，仅能看到自己上传的数据
        if (AuthRoleEnum.PERSONAL.getCode().equals(user.getRoleAuth())) {
            createUserId = user.getUserId();
        }

        Date beginDate = request.getBeginDate();
        //获取结束日期
        Date endDate = calculateEndDate(beginDate, request.getType());
        //门店为空直接返回
        List<String> storeIds = request.getStoreIds();
        if (CollectionUtils.isEmpty(storeIds)) {
            return new PageInfo<>(new ArrayList<>());
        }
        PageHelper.startPage(request.getPageNum(), request.getPageSize(), false);
        List<AchievementDetailDO> achievementDetailDOS = achievementDetailMapper.listAchievementDetail(enterpriseId, beginDate, endDate, storeIds, createUserId);

        return wrapResult(enterpriseId, result, achievementDetailDOS);
    }

    @Override
    public List<AchievementDetailVO> listAppAchievementDetailNew(String enterpriseId, AchievementDetailListRequest request, CurrentUser user, Boolean isPage) {
        String createUserId = null;
        if (AuthRoleEnum.PERSONAL.getCode().equals(user.getRoleAuth())) {
            createUserId = user.getUserId();
        }
        if (isPage) {
            PageHelper.startPage(request.getPageNum(), request.getPageSize(), true);
        }
        List<AchievementDetailVO> achievementDetailVOList = achievementDetailMapper.pageAchievementDetail(enterpriseId,
                request.getBeginDate(), request.getEndDate(), request.getStoreIds(),null,null,null,null,createUserId,null,true,null,null);
        if (CollectionUtils.isEmpty(achievementDetailVOList)) {
            return achievementDetailVOList;
        }
        List<Long> formworkIdList = ListUtils.emptyIfNull(achievementDetailVOList)
                .stream()
                .map(AchievementDetailVO::getFormworkId)
                .collect(Collectors.toList());
        List<Long> achievementTypeIdList = ListUtils.emptyIfNull(achievementDetailVOList)
                .stream()
                .map(AchievementDetailVO::getAchievementTypeId)
                .collect(Collectors.toList());
        List<AchievementFormworkDO> achievementFormworkDOList = achievementFormWorkMapper.listFormworkById(enterpriseId, formworkIdList);
        Map<Long, String> formworkNameMap = ListUtils.emptyIfNull(achievementFormworkDOList)
                .stream()
                .collect(Collectors.toMap(AchievementFormworkDO::getId, AchievementFormworkDO::getName, (a, b) -> a));
        List<AchievementTypeDO> achievementTypeDOList = achievementTypeMapper.getListById(enterpriseId, achievementTypeIdList);
        Map<Long, String> achievementTypeNameMap = achievementTypeDOList
                .stream()
                .collect(Collectors.toMap(AchievementTypeDO::getId, AchievementTypeDO::getName, (a, b) -> a));
        achievementDetailVOList
                .forEach(data -> {
                    String typeName = achievementTypeNameMap.get(data.getAchievementTypeId());
                    data.setAchievementTypeName(typeName);
                    String formworkName = formworkNameMap.get(data.getFormworkId());
                    data.setFormworkName(formworkName);
                });
        return achievementDetailVOList;
    }

    @Override
    public PageInfo<List<AchievementDetailListDTO>> achievementDetailList(String enterpriseId, AchievementDetailListRequest request) {
        List<AchievementDetailListDTO> result = new ArrayList<>();
        Date beginDate = request.getBeginDate();
        //获取结束日期
        Date endDate = request.getEndDate();
        List<String> storeIds = request.getStoreIds();
        List<Long> typeIds = request.getAchievementTypeIds();
        List<String> produceUserIds = request.getProduceUserIds();
        Boolean isNullProduceUser = request.getIsNullProduceUser();
        //门店为空直接返回
        if (CollectionUtils.isEmpty(storeIds)) {
            return new PageInfo<>(new ArrayList<>());
        }
        PageHelper.startPage(request.getPageNum(), request.getPageSize(), true);
        List<AchievementDetailDO> achievementDetailDOS = achievementDetailMapper.achievementDetailList(enterpriseId, beginDate, endDate, storeIds, null,typeIds, produceUserIds, isNullProduceUser,null,null);
        return wrapResult(enterpriseId, result, achievementDetailDOS);
    }

    @Override
    public ImportTaskDO achievementDetailListExport(String enterpriseId, AchievementDetailListRequest request, CurrentUser user) {
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.EXPORT_ACHIEVEMENT_DETAIL);
        Date beginDate = request.getBeginDate();
        //获取结束日期
        Date endDate = request.getEndDate();
        List<String> storeIds = request.getStoreIds();
        List<Long> typeIds = request.getAchievementTypeIds();
        List<String> produceUserIds = request.getProduceUserIds();
        Boolean isNullProduceUser = request.getIsNullProduceUser();
        //门店为空直接返回
        if (CollectionUtils.isEmpty(storeIds)) {
            throw new ServiceException("当前无记录可导出");
        }
        Long count = achievementDetailMapper.countExportList(enterpriseId, beginDate, endDate, storeIds, typeIds, produceUserIds, isNullProduceUser);
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.EXPORT_ACHIEVEMENT_DETAIL);

        MsgUniteData msgUniteData = new MsgUniteData();

        AchievementDetailListExport exportRequest = new AchievementDetailListExport();
        exportRequest.setImportTaskDO(importTaskDO);
        exportRequest.setEnterpriseId(enterpriseId);
        exportRequest.setDbName(user.getDbName());
        exportRequest.setTotalNum(count);
        exportRequest.setRequest(request);

        msgUniteData.setData(JSONObject.toJSONString(exportRequest));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.ACHIEVEMENT_DETAIL_EXPORT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;

    }

    @Override
    public PageInfo<AchievementStoreVO> listByStore(String eid, AchievementRequest request) {

        //如果门店id为空，根据区域id获取门店id
        List<StoreDO> storeDOList = storeIdHandel(eid,request);
        if(CollectionUtils.isEmpty(storeDOList)){
            return new PageInfo<>();
        }
        List<String> storeIds = storeDOList.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
        Map<String,StoreDO> storeDOMap = ListUtils.emptyIfNull(storeDOList).stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));
        Date beginDate = DateUtil.getFirstOfDayMonth(request.getBeginDate());

        AchievementTargetRequest achievementTargetRequest = new AchievementTargetRequest();
        achievementTargetRequest.setStoreIds(storeIds);
        achievementTargetRequest.setTimeType(AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type);
        achievementTargetRequest.setBeginDate(beginDate);

        List<AchievementTargetDetailDO> targetDetailDOS = targetDetailMapper.getTargetByStoreAndDate(eid,achievementTargetRequest);
        Map<String,AchievementTargetDetailDO> targetDetailDOMap = ListUtils.emptyIfNull(targetDetailDOS).stream()
                .collect(Collectors.toMap(AchievementTargetDetailDO::getStoreId, data -> data, (a, b) -> a));

        String beginDateStr = DateUtil.format(beginDate);
        String endDateStr = DateUtil.format(DateUtil.getLastOfDayMonth(request.getBeginDate()));
        List<AchievementStoreAmountDTO> storeAmountDTOS = achievementDetailMapper.getAmountByStores(eid,request.getStoreIds(),beginDateStr,endDateStr);
        Map<String,AchievementStoreAmountDTO> storeAmountDTOMap = ListUtils.emptyIfNull(storeAmountDTOS).stream()
                .filter(e -> StringUtils.isNotBlank(e.getStoreId()))
                .collect(Collectors.toMap(AchievementStoreAmountDTO::getStoreId, data -> data, (a, b) -> a));

        List<AchievementStoreVO> achievementStoreVOS = storeDOList.stream().map(e ->{
            AchievementStoreVO vo = new AchievementStoreVO();
            vo.setStoreId(e.getStoreId());
            vo.setStoreName(e.getStoreName());
            vo.setBeginDate(beginDate);
            if(storeDOMap.get(e.getStoreId()) != null){
                vo.setStoreNum(storeDOMap.get(e.getStoreId()).getStoreNum());
            }
            if(targetDetailDOMap.get(e.getStoreId()) != null){
                vo.setAchievementTarget(targetDetailDOMap.get(e.getStoreId()).getAchievementTarget());
            }
            if(storeAmountDTOMap.get(e.getStoreId()) != null){
                vo.setAchievementAmount(storeAmountDTOMap.get(e.getStoreId()).getAchievementAmount());
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageInfo<>(achievementStoreVOS);
    }

    public List<StoreDO> storeIdHandel(String eid,AchievementRequest request){
        //如果区域id为空
        if(StringUtils.isBlank(request.getRegionId())){
            if(CollectionUtils.isEmpty(request.getStoreIds())){
                return new ArrayList<>();
            }else {
                //如果页数大于1，返回空
                if(request.getPageNum() > 1){
                    return new ArrayList<>();
                }
                if(request.getStoreIds().size() > MAX_PAGE_SIZE){
                    throw new ServiceException(ErrorCodeEnum.ACH_STATISTICS_STORE_MAX);
                }
                return  storeMapper.getByStoreIdList(eid,request.getStoreIds());
            }
        }else {
            RegionDO regionDO = regionMapper.getByRegionId(eid, Long.valueOf(request.getRegionId()));
            if(request.getShowCurrent()){
                if(request.getPageNum() > 1){
                    return new ArrayList<>();
                }
                //如果展示当前区域，最多展示500个门店
                PageHelper.startPage(1, MAX_PAGE_SIZE);
            }else {
                PageHelper.startPage(request.getPageNum(), request.getPageSize());
            }
            List<StoreDO> storeDOList = storeMapper.listStoreAndShowCurrent(eid, null,
                    Long.valueOf(regionDO.getRegionId()), regionDO.getFullRegionPath(), null, request.getShowCurrent(),  StoreIsDeleteEnum.EFFECTIVE.getValue());
            if (CollectionUtils.isEmpty(storeDOList)) {
                return new ArrayList<>();
            }else {
                return storeDOList;
            }
        }
    }

    @Override
    public List<AchievementUserVO> listGroupByUser(String eid, AchievementRequest request) {
        String beginDateStr = DateUtil.format(DateUtil.getFirstOfDayMonth(request.getBeginDate()));
        String endDateStr = DateUtil.format(DateUtil.getLastOfDayMonth(request.getBeginDate()));
        List<AchievementStoreAmountDTO> storeAmountDTOS = achievementDetailMapper.listGroupByUser(eid,request.getStoreIds(),beginDateStr,endDateStr);
        if(CollectionUtils.isEmpty(storeAmountDTOS)){
            return new ArrayList<>();
        }
        return storeAmountDTOS.stream().map(e ->{
            AchievementUserVO vo = new AchievementUserVO();
            vo.setProduceUserId(e.getProduceUserId());
            vo.setProduceUserName(e.getProduceUserName());
            vo.setAchievementAmount(e.getAchievementAmount());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public PageInfo<AchievementDetailVO> listDetail(String eid, AchievementRequest request) {
        PageHelper.startPage(request.getPageNum(),request.getPageSize(),true);
        List<AchievementDetailVO> achievementDetailVOList = achievementDetailMapper.pageAchievementDetail(
                eid,
                DateUtil.getFirstOfDayMonth(request.getBeginDate()),
                DateUtil.getLastOfDayMonth(request.getBeginDate()),
                request.getStoreIds(),
                null,
                null,
                null,
                null,
                null,
                null,
                true,
                null,
                null);

        List<AchievementFormworkDO> achievementFormworkDOS = achievementFormWorkMapper.listAll(eid, null);
        Map<Long,AchievementFormworkDO> formworkDOMap = ListUtils.emptyIfNull(achievementFormworkDOS).stream()
                .collect(Collectors.toMap(AchievementFormworkDO::getId, data -> data, (a, b) -> a));

        List<AchievementTypeDO> achievementTypeDOS = achievementTypeMapper.listAllTypes(eid);

        Map<Long,AchievementTypeDO> typeDOMap = ListUtils.emptyIfNull(achievementTypeDOS).stream()
                .collect(Collectors.toMap(AchievementTypeDO::getId, data -> data, (a, b) -> a));

        for (AchievementDetailVO vo : achievementDetailVOList) {
            if(formworkDOMap.get(vo.getFormworkId()) != null){
                vo.setFormworkName(formworkDOMap.get(vo.getFormworkId()).getName());
            }
            if(typeDOMap.get(vo.getAchievementTypeId()) != null){
                vo.setAchievementTypeName(typeDOMap.get(vo.getAchievementTypeId()).getName());
            }
        }

        return new PageInfo<>(achievementDetailVOList);
    }

    @Override
    public void report(String eid, AchievementRequest request, CurrentUser user) {
        log.info("report param -> eid:{},request:{},user:{}", eid, JSONObject.toJSONString(request), JSONObject.toJSONString(user));
        List<AchievementDetailDTO> achievementDetailList = request.getAchievementDetailList();
        if (CollectionUtils.isEmpty(achievementDetailList)) {
            throw new ServiceException(ErrorCodeEnum.ACH_PARAM_REPORT_INVALID);
        }
        List<String> storeIds = achievementDetailList.stream().map(AchievementDetailDTO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeDOList = storeMapper.getByStoreIdList(eid, storeIds);

        Map<String, StoreDO> storeDOMap = ListUtils.emptyIfNull(storeDOList).stream()
                .collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));


        List<AchievementDetailDO> detailDOList = achievementDetailList.stream().map(e -> {
            AchievementDetailDO detailDO = new AchievementDetailDO();
            detailDO.setStoreId(e.getStoreId());
            if (storeDOMap.get(e.getStoreId()) != null) {
                detailDO.setStoreName(storeDOMap.get(e.getStoreId()).getStoreName());
                detailDO.setRegionId(storeDOMap.get(e.getStoreId()).getRegionId());
                detailDO.setRegionPath(storeDOMap.get(e.getStoreId()).getRegionPath());
            }
            detailDO.setAchievementTypeId(e.getAchievementTypeId());
            detailDO.setAchievementAmount(e.getAchievementAmount());
            detailDO.setProduceTime(request.getProduceTime());
            detailDO.setProduceUserId(request.getProduceUserId());
            detailDO.setProduceUserName(request.getProduceUserName());
            detailDO.setAchievementFormworkId(request.getAchievementFormworkId());
            detailDO.setAchievementFormworkType(request.getAchievementFormworkType());
            detailDO.setCreateUserId(user.getUserId());
            detailDO.setCreateUserName(user.getName());
            if (StringUtils.isNotBlank(request.getGoodsType())) {
                detailDO.setGoodsType(request.getGoodsType());
            }
            if (StringUtils.isNotBlank(request.getExtendParam())) {
                detailDO.setExtendParam(request.getExtendParam());
            }
            if (StringUtils.isNotBlank(request.getGoodsNum())) {
                detailDO.setGoodsNum(request.getGoodsNum());
            }
            // todo增加线上企业
            if (("e17cd2dc350541df8a8b0af9bd27f77d".equals(eid) || "d188a425c97a4ca9a43b09d859b5ff75".equals(eid)
                    || "e95264a6c31b42f489f95662a985bc4f".equals(eid)|| "92625cf44d8e439e8edeadf3fd291c7c".equals(eid)) && StringUtils.isNotBlank(request.getGoodsType())
                    && StringUtils.isNotBlank(request.getGoodsNum())) {
                Long goodTypeId = panasonicMapper.panasonicFindByType(eid, e.getStoreId(), request.getGoodsType());
                List<AchievementTaskRecordDO> achievementTaskRecordDOS = achievementTaskRecordMapper.selectTaskRecordList(eid, e.getStoreId(), request.getGoodsType());
                if (CollectionUtils.isNotEmpty(achievementTaskRecordDOS)) {
                    achievementTaskRecordDOS.forEach(achievementTaskRecordDO -> {
                        Integer goodsNum = Integer.valueOf(request.getGoodsNum());
                        if(goodsNum > 0){
                           Integer leftGoodsNum = achievementTaskRecordDO.getGoodsNum() - goodsNum;
                           if(leftGoodsNum <= 0){
                               achievementTaskRecordDO.setGoodsNum(leftGoodsNum);
                               if(leftGoodsNum < 0){
                                   achievementTaskRecordDO.setGoodsNum(0);
                               }
                               //同一批次同一节点的同一的必是已完成
                               taskSubMapper.updateSubStatusComplete(eid, achievementTaskRecordDO.getUnifyTaskId(), achievementTaskRecordDO.getStoreId(), achievementTaskRecordDO.getLoopCount());
                               TaskStoreDO taskStoreDO = taskStoreMapper.selectByPrimaryKey(eid, achievementTaskRecordDO.getTaskStoreId());
                               achievementTaskRecordDO.setStatus(1);
                               taskStoreDO.setSubStatus(UnifyStatus.COMPLETE.getCode());
                               taskStoreDO.setNodeNo(UnifyNodeEnum.END_NODE.getCode());
                               taskStoreDO.setHandleTime(new Date());
                               taskStoreMapper.updateByPrimaryKey(eid, taskStoreDO);
                               //完成
                               panasonicMapper.updatePanasonicStatusByTypeId(eid, goodTypeId);
                           }else{
                               achievementTaskRecordDO.setGoodsNum(leftGoodsNum);
                               panasonicMapper.updatePanasonicByTypeId(eid, goodsNum, goodTypeId);
                           }
                            achievementTaskRecordMapper.updateByPrimaryKeySelective(achievementTaskRecordDO, eid);
                        }
                    });
                }
            }

            return detailDO;
        }).collect(Collectors.toList());

        achievementDetailMapper.insertBatchDetail(eid, detailDOList);
    }

    @Override
    public ImportTaskDO exportRegionStatistics(String eid,AchievementExportRequest request, CurrentUser user) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_ACHIEVEMENT_REGION);
        return exportUtil.exportFile(eid, request, UserHolder.getUser().getDbName());
    }

    @Override
    public ImportTaskDO exportRegionStatisticsMonth(String eid,AchievementExportRequest request, CurrentUser user) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_ACHIEVEMENT_REGION_MONTH);
        return exportUtil.exportFile(eid, request, UserHolder.getUser().getDbName());
    }

    @Override
    public ImportTaskDO exportStoreStatistic(String eid, AchievementExportRequest request, CurrentUser user) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_ACHIEVEMENT_STORE);
        return exportUtil.exportFile(eid, request, UserHolder.getUser().getDbName());
    }

    @Override
    public ImportTaskDO exportStoreStatisticMonth(String eid, AchievementExportRequest request, CurrentUser user) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_ACHIEVEMENT_STORE_MONTH);
        return exportUtil.exportFile(eid, request, UserHolder.getUser().getDbName());
    }

    @Override
    public ImportTaskDO exportAchievementType(String eid, AchievementExportRequest request, CurrentUser user) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_ACHIEVEMENT_TYPE);
        return exportUtil.exportFile(eid, request, UserHolder.getUser().getDbName());
    }

    @Override
    public ImportTaskDO exportAchievementAllDetail(String eid, AchievementExportRequest request, CurrentUser user) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_ACHIEVEMENT_ALL_DETAIL);
        return exportUtil.exportFile(eid, request, UserHolder.getUser().getDbName());
    }

    private PageInfo<List<AchievementDetailListDTO>> wrapResult(String enterpriseId, List<AchievementDetailListDTO> result, List<AchievementDetailDO> achievementDetailDOS) {
        //详情列表为空直接返回
        if (CollectionUtils.isEmpty(achievementDetailDOS)) {
            return new PageInfo(result);
        }
        List<Long> typeIdList = achievementDetailDOS.stream().map(data -> data.getAchievementTypeId()).collect(Collectors.toList());
        List<AchievementTypeDO> typeList = achievementTypeMapper.getListById(enterpriseId, typeIdList);
        Map<Long, AchievementTypeDO> typeMap = new HashMap<>();
        for (AchievementTypeDO achievementTypeDO : typeList) {
            typeMap.put(achievementTypeDO.getId(), achievementTypeDO);
        }
        achievementDetailDOS.stream().forEach(data -> {
            AchievementDetailListDTO achievementDetailListDTO = new AchievementDetailListDTO();
            achievementDetailListDTO.setAchievementDetail(data);
            achievementDetailListDTO.setAchievementTypeDO(typeMap.get(data.getAchievementTypeId()));
            result.add(achievementDetailListDTO);
        });
        PageInfo pageInfo = new PageInfo(achievementDetailDOS);
        pageInfo.setList(result);
        return pageInfo;
    }

    private Date calculateEndDate(Date beginDate, String type) {
        switch (type) {
            case "month":
                Calendar month = Calendar.getInstance();
                month.setTime(beginDate);
                month.set(Calendar.DAY_OF_MONTH, month.getActualMaximum(Calendar.DAY_OF_MONTH));
                month.set(Calendar.HOUR_OF_DAY, 23);
                month.set(Calendar.MINUTE, 59);
                month.set(Calendar.SECOND, 59);
                return month.getTime();
            case "day":
                Calendar day = Calendar.getInstance();
                day.setTime(beginDate);
                day.set(Calendar.HOUR_OF_DAY, 23);
                day.set(Calendar.MINUTE, 59);
                day.set(Calendar.SECOND, 59);
                return day.getTime();
            default:
                throw new ServiceException(ErrorCodeEnum.SERVER_ERROR.getCode(), "时间类型不能为空");
        }
    }

    @Override
    public SalesProfileResponse salesProfile(String enterpriseId,String mainClass) {
       List<AchieveCountDTO> countDTOList =  achievementDetailMapper.salesProfile(enterpriseId,mainClass);
        SalesProfileResponse response = new SalesProfileResponse();
        for (AchieveCountDTO achieveCountDTO : countDTOList) {
            if (achieveCountDTO.getPeriodType().equals("daily")){
                response.setTodaySales(achieveCountDTO.getTotalAmount());
            }
            if (achieveCountDTO.getPeriodType().equals("yesterday")){
                response.setYesterdaySales(achieveCountDTO.getTotalAmount());
            }
            if (achieveCountDTO.getPeriodType().equals("last_year_today")){
                response.setLastYearTodaySales(achieveCountDTO.getTotalAmount());
            }
            if (achieveCountDTO.getPeriodType().equals("monthly")){
                response.setMonthSales(achieveCountDTO.getTotalAmount());
                response.setMonthTarget(achieveCountDTO.getTotalAchievementTarget());
                response.setMonthPlanRate(calculateYoYGrowth(Double.valueOf(response.getMonthSales()),Double.valueOf(response.getMonthTarget())));

            }
            if (achieveCountDTO.getPeriodType().equals("last_month")){
                response.setAfterMonthSales(achieveCountDTO.getTotalAmount());
            }
            if  (achieveCountDTO.getPeriodType().equals("last_year_month")){
                response.setLastYearMonthSales(achieveCountDTO.getTotalAmount());
            }
            if (achieveCountDTO.getPeriodType().equals("weekly")){
                response.setWeekSales(achieveCountDTO.getTotalAmount());
            }
            if (achieveCountDTO.getPeriodType().equals("last_week")){
                response.setAfterWeekSales(achieveCountDTO.getTotalAmount());
            }
            if (achieveCountDTO.getPeriodType().equals("last_year_week")){
                response.setLastYearWeekSales(achieveCountDTO.getTotalAmount());
            }
            if(achieveCountDTO.getPeriodType().equals("quarterly")){
                response.setQuarterSales(achieveCountDTO.getTotalAmount());
                response.setQuarterTarget(achieveCountDTO.getTotalAchievementTarget());
                response.setQuarterPlanRate(calculateYoYGrowth(Double.valueOf(response.getQuarterSales()),Double.valueOf(response.getQuarterTarget())));
            }
            if (achieveCountDTO.getPeriodType().equals("last_quarter")){
                response.setAfterQuarterSales(achieveCountDTO.getTotalAmount());
            }
            if (achieveCountDTO.getPeriodType().equals("last_year_quarter")){
                response.setLastYearQuarterSales(achieveCountDTO.getTotalAmount());
            }
            if  (achieveCountDTO.getPeriodType().equals("yearly")){
                response.setYearSales(achieveCountDTO.getTotalAmount());
                response.setYearTarget(achieveCountDTO.getTotalAchievementTarget());
                response.setYearPlanRate(calculateYoYGrowth(Double.valueOf(response.getYearSales()),Double.valueOf(response.getYearTarget())));
            }
            if (achieveCountDTO.getPeriodType().equals("last_year")){
                response.setLastYearSales(achieveCountDTO.getTotalAmount());
            }

        }
        //计算环比
        response.setSalesDayMom(calculateYoYGrowth(Double.valueOf(response.getTodaySales()),Double.valueOf(response.getYesterdaySales())));
        response.setSalesWeekMom(calculateYoYGrowth(Double.valueOf(response.getWeekSales()),Double.valueOf(response.getAfterWeekSales())));
        response.setSalesMonthMom(calculateYoYGrowth(Double.valueOf(response.getMonthSales()),Double.valueOf(response.getAfterMonthSales())));
        response.setSalesQuarterMom(calculateYoYGrowth(Double.valueOf(response.getQuarterSales()),Double.valueOf(response.getAfterQuarterSales())));
        response.setSalesYearMom(calculateYoYGrowth(Double.valueOf(response.getYearSales()),Double.valueOf(response.getLastYearSales())));
        //计算同比
        response.setSalesDayYoy(calculateYoYGrowth(Double.valueOf(response.getTodaySales()),Double.valueOf(response.getLastYearTodaySales())));
        response.setSalesWeekYoy(calculateYoYGrowth(Double.valueOf(response.getWeekSales()),Double.valueOf(response.getLastYearWeekSales())));
        response.setSalesMonthYoy(calculateYoYGrowth(Double.valueOf(response.getMonthSales()),Double.valueOf(response.getLastYearMonthSales())));
        response.setSalesQuarterYoy(calculateYoYGrowth(Double.valueOf(response.getQuarterSales()),Double.valueOf(response.getLastYearQuarterSales())));
        response.setSalesYearYoy(calculateYoYGrowth(Double.valueOf(response.getYearSales()),Double.valueOf(response.getLastYearSales())));


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-01");
        String currentMonth = sdf.format(new Date());
        String target = achievement.getAllTargetByCurrentMonth(enterpriseId,currentMonth);
        double v = Math.round(Double.valueOf(response.getMonthSales()) / Double.valueOf(target)) * 100;
        response.setMonthRate(v + "%");
        return response;
    }

    public static String calculateYoYGrowth(double currentYearValue, double previousYearValue) {
        if (previousYearValue == 0) {
            return 0 + "%";
        }
        String value = String.valueOf(Math.round(((currentYearValue - previousYearValue) / previousYearValue) * 100));
        return value + "%";
    }


    // 计算环比
    public static double calculateMoM(double currentValue, double previousValue) {
        if (previousValue == 0) {
            return 0;
        }
        return ((currentValue - previousValue) / previousValue) * 100;
    }

    @Override
    public List<AchieveMonthTop5Response> monthTop5(String enterpriseId,Long startTime,Long endTime,String mainClass) {
        List<AchieveMonthTop5Response> numByFormwork = achievementDetailMapper.getNumByFormwork(enterpriseId,new Date(startTime),new Date(endTime),mainClass);
        return numByFormwork;
    }

    @Override
    public List<ChooseCategoryResponse> chooseCategory(String enterpriseId) {
        List<ChooseCategoryResponse> result = achievementFormWorkMapper.chooseCategory(enterpriseId);
        return result;
    }

    @Override
    public List<AchieveMonthMiddleTop5Response> monthMiddleTop5(String enterpriseId,String categoryId,Long startTime,Long endTime,String mainCLass) {
        List<AchieveMonthMiddleTop5Response> response = achievementDetailMapper.monthMiddleTop5(enterpriseId,categoryId,new Date(startTime),new Date(endTime),mainCLass);
        return response;
    }

    @Override
    public List<RegionTop5Response> regionTop5(String enterpriseId,Long startTime,Long endTime,String mainCLass) {
        //根据两个时间戳差判断是天还是周还是月
        long dif = endTime - startTime+1;
        // 一个季度的毫秒数（假设为3个月）
        long oneQuarterMillis = 3 * 30 * 24 * 60 * 60 * 1000L;
        // 一年的毫秒数（假设为12个月）
        long oneYearMillis = 12 * 30 * 24 * 60 * 60 * 1000L;
        String timeType="month";
        if (dif >= oneQuarterMillis) {
            timeType="quarter";
        } else if (dif >= oneYearMillis) {
            timeType="year";
        }
        List<RegionTop5Response> res = achievementDetailMapper.regionTop5(enterpriseId, new Date(startTime), new Date(endTime), mainCLass, timeType);
//        long count = storeMapper.getAllStoreId(enterpriseId).stream().count();
        //计算回转率 销售额/门店数
        for (RegionTop5Response re : res) {
            RegionDO regionDO = new RegionDO();
            String regionPath = re.getRegionPath();
            if (StringUtils.isNotEmpty(regionPath)){
                regionDO.setRegionPath(regionPath+re.getId());
            }
            List<RegionDO> list = CollectionUtil.emptyIfNull(regionMapper.getSubStoreByPath(enterpriseId, regionDO));
            //门店数量
            re.setStoreNum(String.valueOf(list.size()));
            String rate = String.valueOf(Math.round(Double.valueOf(re.getTotalAchievementAmount()) /list.size() ));
            //回转率
            re.setConversionRate(rate);
        }
        // 对 res 进行倒序排序
        Collections.sort(res, Collections.reverseOrder(Comparator.comparingDouble(response -> Double.parseDouble(response.getConversionRate()))));
        return res;
    }

    @Override
    public List<RegionTop5Response> storeTop5(String enterpriseId,Long startTime,Long endTime,String mainCLass) {
        //根据两个时间戳差判断是天还是周还是月
        long dif = endTime - startTime+1;
        // 一个季度的毫秒数（假设为3个月）
        long oneQuarterMillis = 3 * 30 * 24 * 60 * 60 * 1000L;
        // 一年的毫秒数（假设为12个月）
        long oneYearMillis = 12 * 30 * 24 * 60 * 60 * 1000L;
        String timeType="month";
        if (dif >= oneQuarterMillis) {
            timeType="quarter";
        } else if (dif >= oneYearMillis) {
            timeType="year";
        }
        return achievementDetailMapper.storeTop5(enterpriseId,new Date(startTime),new Date(endTime),mainCLass,timeType);
    }

    @Override
    public boolean panasonicAdd(String enterpriseId, PanasonicAddRequest request) {
        if (StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(request.getData())) {
            return false;
        }
        try {
            for (PanasonicAddRequest.innerClass datum : request.getData()) {
                datum.setStoreId(request.getStoreId());
            }
            panasonicMapper.panasonicAdd(enterpriseId, request.getData());
        } catch (Exception e) {
            log.error("panasonicAdd异常", e);
        }
        return true;
    }


    @Override
    public List<PanasonicFindResponse> panasonicFind(String enterpriseId, String storeId,String category) {
        if (StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(storeId)) {
            return null;
        }
        List<PanasonicFindResponse> sourceData = panasonicMapper.panasonicFind(enterpriseId,storeId,category);
        List<PanasonicFindResponse> response = buildTree(sourceData);
        return response;
    }

    private List<PanasonicFindResponse> buildTree(List<PanasonicFindResponse> products) {
        Map<String, PanasonicFindResponse> parentMap = new HashMap<>();
        Map<String, PanasonicFindResponse> parentMap2 = new HashMap<>();
        List<PanasonicFindResponse> rootPanasonic = new ArrayList<>();

        for (PanasonicFindResponse product : products) {
            String middleClass = product.getMiddleClass();
            String storeId = product.getStoreId();
            String category = product.getCategory();
            if (parentMap.containsKey(middleClass)) {
                // 如果已经有相同 middleClass 的父节点，则将当前节点添加到父节点的 child 列表中
                PanasonicFindResponse parent = parentMap.get(middleClass);
                parent.getChild().add(product);
            } else {
                // 如果没有相同 middleClass 的父节点，则创建一个新的父节点，并将当前节点作为其子节点
                PanasonicFindResponse parent = new PanasonicFindResponse();
                parent.setMiddleClass(middleClass);
                parent.setCategory(category);
                parent.setStoreId(storeId);
                parent.getChild().add(product);
                parentMap.put(middleClass, parent);
                rootPanasonic.add(parent);
            }
        }


        return rootPanasonic;
    }

    @Override
    public List<PanasonicFindResponse> panasonicFind2(String enterpriseId, String storeId,String name,String middleName) {
        if (StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(storeId)) {
            return null;
        }
        List<PanasonicFindResponse> list = panasonicMapper.panasonicFind2(enterpriseId,storeId,name,middleName);
        return list;
    }

    @Override
    public List<PanasonicFindResponse> getStructure(String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return null;
        }
        List<PanasonicTempDO> dos = panasonicMapper.getStructure(enterpriseId);

        return buildTreeWithThreeLevels(dos);
    }

    @Override
    public PageInfo<NewProductDataVO> getNewProductDataList(String enterpriseId, String category, String middleClass, String type,Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize,true);
        List<NewProductDataVO> list = Lists.newArrayList();
        //根据type做key，storeNum做value，统计多少家门店有这个型号
        List<PanasonicTempDO> dos = panasonicMapper.queryList(enterpriseId, PanasonicTempDO.builder().category(category)
                .middleClass(middleClass).type(type).id(1L).build());
        List<PanasonicTempDO> allList = panasonicMapper.queryList(enterpriseId, new PanasonicTempDO());

        Map<String, Long> allMap = allList.stream().collect(Collectors.groupingBy(c->c.getType(), Collectors.counting()));
        //统计多少家门店有并且上架了这个型号
        List<PanasonicTempDO> onList = panasonicMapper.queryList(enterpriseId, PanasonicTempDO.builder().offOnStatus(1).build());
        Map<String, Long> onMap = onList.stream().collect(Collectors.groupingBy(c->c.getType(), Collectors.counting()));
        //查询当前企业多少门店
        Integer countAllStore = storeMapper.countAllStore(enterpriseId);

        dos.stream().forEach(c->{
            Long amount= achievementDetailMapper.getAchievementAmountByType(enterpriseId,c.getType());
            NewProductDataVO vo = new NewProductDataVO();
            vo.setCategory(c.getCategory());
            vo.setMiddleClass(c.getMiddleClass());
            vo.setType(c.getType());
            vo.setSalesAmount(amount);
            vo.setStoreNum(allMap.get(c.getType())==null?0L:allMap.get(c.getType()));
            vo.setOnStoreNum(onMap.get(c.getType())==null?0L:onMap.get(c.getType()));
            vo.setAllStoreNum(countAllStore==null?0:countAllStore.longValue());
            list.add(vo);
        });
        return new PageInfo<>(list);
    }

    @Override
    public List<TOPTenVO> getTopTenList(String enterpriseId, String sortField, Date startTime,Date endTime,String mainClass,String storeId,Integer limit) {
        if (limit== null) {
            limit = 10;
        }
        return achievementDetailMapper.getTopTenList(enterpriseId, sortField, startTime,endTime,mainClass,storeId,limit);
    }

    @Override
    public List<String> getAllCategory(String enterpriseId) {
        return CollectionUtil.emptyIfNull(panasonicMapper.getAllCategory(enterpriseId));
    }

    @Override
    public List<String> getMiddleClassByCategory(String enterpriseId, String category) {
        return CollectionUtil.emptyIfNull(panasonicMapper.getMiddleClassByCategory(enterpriseId, category));
    }

    @Override
    public List<String> getTypeByCategoryAndMiddleClass(String enterpriseId, String category, String middleClass) {
        return CollectionUtil.emptyIfNull(panasonicMapper.getTypeByCategoryAndMiddleClass(enterpriseId, category, middleClass));
    }

    @Override
    public List<String> getMarketStoreList(String enterpriseId,String type) {
        return CollectionUtil.emptyIfNull(panasonicMapper.getMarketStoreList(enterpriseId,type));
    }

    @Override
    public List<String> getMiddleClassByMainClass(String enterpriseId, String mainClass) {
        return CollectionUtil.emptyIfNull(panasonicMapper.getMiddleClassByMainClass(enterpriseId, mainClass));
    }

    @Override
    public HomeSalesProfileResponse homeSalesProfile(String eid) {
        HomeSalesProfileResponse res= achievementDetailMapper.getHomeSalesProfile(eid);
        long count = storeMapper.getAllStoreId(eid).stream().count();
        //计算环比
        res.setSalesDayMom(calculateYoYGrowth(Double.valueOf(res.getTodaySales()),Double.valueOf(res.getYesterdaySales())));
        res.setSalesWeekMom(calculateYoYGrowth(Double.valueOf(res.getWeekSales()),Double.valueOf(res.getAfterWeekSales())));
        res.setSalesMonthMom(calculateYoYGrowth(Double.valueOf(res.getMonthSales()),Double.valueOf(res.getAfterMonthSales())));
        res.setSalesNumDayMom(calculateYoYGrowth(Double.valueOf(res.getTodaySalesNum()),Double.valueOf(res.getYesterdaySalesNum())));
        res.setSalesNumWeekMom(calculateYoYGrowth(Double.valueOf(res.getWeekSalesNum()),Double.valueOf(res.getAfterWeekSalesNum())));
        res.setSalesNumMonthMom(calculateYoYGrowth(Double.valueOf(res.getMonthSalesNum()),Double.valueOf(res.getLastMonthSalesNum())));
        //计算同比
        res.setSalesDayYoy(calculateYoYGrowth(Double.valueOf(res.getTodaySales()),Double.valueOf(res.getLastYearTodaySales())));
        res.setSalesWeekYoy(calculateYoYGrowth(Double.valueOf(res.getWeekSales()),Double.valueOf(res.getLastYearWeekSales())));
        res.setSalesMonthYoy(calculateYoYGrowth(Double.valueOf(res.getMonthSales()),Double.valueOf(res.getLastYearMonthSales())));
        res.setSalesNumDayYoy(calculateYoYGrowth(Double.valueOf(res.getTodaySalesNum()),Double.valueOf(res.getLastYearTodaySalesNum())));
        res.setSalesNumWeekYoy(calculateYoYGrowth(Double.valueOf(res.getWeekSalesNum()),Double.valueOf(res.getLastYearWeekSalesNum())));
        res.setSalesNumMonthYoy(calculateYoYGrowth(Double.valueOf(res.getMonthSalesNum()),Double.valueOf(res.getLastYearMonthSalesNum())));
        //门店数量
        res.setTodayStoreNum(String.valueOf(count));
        res.setYesterdayStoreNum(String.valueOf(count));
        res.setLastYearTodayStoreNum(String.valueOf(count));
        res.setStoreNumDayYoy(calculateYoYGrowth(Double.valueOf(res.getTodayStoreNum()),Double.valueOf(res.getLastYearTodayStoreNum())));
        res.setStoreNumDayMom(calculateYoYGrowth(Double.valueOf(res.getTodayStoreNum()),Double.valueOf(res.getYesterdayStoreNum())));

        //今日回转率
        res.setTodayConversionRate(String.valueOf(Math.round(Double.valueOf(res.getTodaySales())/ Long.valueOf(count))));
        res.setConversionRateDayYoy(calculateYoYGrowth(calculateMoM(Double.valueOf(res.getTodaySales()),count), calculateMoM(Double.valueOf(res.getLastYearTodaySales()),count)));
        res.setConversionRateDayMom(calculateYoYGrowth(calculateMoM(Double.valueOf(res.getTodaySales()),count), calculateMoM(Double.valueOf(res.getYesterdaySales()),count)));

        //本周回转率
        res.setWeekConversionRate(String.valueOf(Math.round(Double.valueOf(res.getWeekSales())/ Long.valueOf(count))));
        res.setConversionRateWeekYoy(calculateYoYGrowth(calculateMoM(Double.valueOf(res.getWeekSales()),count), calculateMoM(Double.valueOf(res.getLastYearWeekSales()),count)));
        res.setConversionRateWeekMom(calculateYoYGrowth(calculateMoM(Double.valueOf(res.getWeekSales()),count), calculateMoM(Double.valueOf(res.getAfterWeekSales()),count)));

        //本月回转率
        res.setMonthConversionRate(String.valueOf(Math.round(Double.valueOf(res.getMonthSales())/ Long.valueOf(count))));
        res.setConversionRateMonthYoy(calculateYoYGrowth(calculateMoM(Double.valueOf(res.getMonthSales()),count), calculateMoM(Double.valueOf(res.getLastYearMonthSales()),count)));
        res.setConversionRateMonthMom(calculateYoYGrowth(calculateMoM(Double.valueOf(res.getMonthSales()),count), calculateMoM(Double.valueOf(res.getAfterMonthSales()),count)));

        return res;
    }

    @Override
    public NewSalesProfileResponse newSalesProfile(String enterpriseId,String mainClass,Long startTime,Long endTime) {
        String timeType = getTimeType(startTime, endTime);
        NewSalesProfileResponse res= achievementDetailMapper.getNewSalesProfile(enterpriseId,timeType,mainClass);

        //查询门店数
        long count = storeMapper.getAllStoreId(enterpriseId).stream().count();
        res.setTodayStoreNum(String.valueOf(count));
        res.setYesterdayStoreNum(String.valueOf(count));
        res.setLastYearTodayStoreNum(String.valueOf(count));
        //回转率
        res.setTodayConversionRate(String.valueOf(Math.round(Double.valueOf(res.getTodaySales())/ Long.valueOf(count))));
        res.setYesterdayConversionRate(String.valueOf(Math.round(Double.valueOf(res.getLastYearTodaySales())/ Long.valueOf(count))));
        //计算环比
        res.setSalesDayMom(calculateYoYGrowth(Double.valueOf(res.getTodaySales()),Double.valueOf(res.getYesterdaySales())));
        res.setSalesNumDayMom(calculateYoYGrowth(Double.valueOf(res.getTodaySalesNum()),Double.valueOf(res.getYesterdaySalesNum())));
        res.setStoreNumDayMom(calculateYoYGrowth(Double.valueOf(res.getTodayStoreNum()),Double.valueOf(res.getYesterdayStoreNum())));
        res.setConversionRateDayMom(calculateYoYGrowth(calculateMoM(Double.valueOf(res.getTodaySales()),count), calculateMoM(Double.valueOf(res.getYesterdaySales()),count)));
        //计算同比
        res.setSalesDayYoy(calculateYoYGrowth(Double.valueOf(res.getTodaySales()),Double.valueOf(res.getLastYearTodaySales())));
        res.setSalesNumDayYoy(calculateYoYGrowth(Double.valueOf(res.getTodaySalesNum()),Double.valueOf(res.getLastYearTodaySalesNum())));
        res.setStoreNumDayYoy(calculateYoYGrowth(Double.valueOf(res.getTodayStoreNum()),Double.valueOf(res.getLastYearTodayStoreNum())));
        res.setConversionRateDayYoy(calculateYoYGrowth(calculateMoM(Double.valueOf(res.getTodaySales()),count), calculateMoM(Double.valueOf(res.getLastYearTodaySales()),count)));
        return res;
    }

    public String getTimeType(long startTime, long endTime) {
        long diffInMillis = endTime - startTime+1;
        // Convert milliseconds to days
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

        if (diffInDays >= 365) {
            return "year";
        } else if (diffInDays >= 90) {
            return "quarter";
        } else if (diffInDays >= 30) {
            return "month";
        } else if (diffInDays >= 7) {
            return "week";
        } else {
            return "day";
        }
    }

    //category作为根节点，middleClass作为子节点，型号作为第三级，构建三个层级关系
    private List<PanasonicFindResponse> buildTreeWithThreeLevels(List<PanasonicTempDO> products) {
        // 使用 Stream 构建 category、middleClass 和对应的型号的映射关系
        Map<String, Map<String, List<String>>> categoryMiddleClassTypeMap = products.stream()
                .collect(Collectors.groupingBy(
                        PanasonicTempDO::getCategory, // 第一级：category
                        Collectors.groupingBy(
                                PanasonicTempDO::getMiddleClass, // 第二级：middleClass
                                Collectors.mapping(PanasonicTempDO::getType, Collectors.toList()) // 第三级：type
                        )
                ));

        // 构建树结构
        List<PanasonicFindResponse> tree = new ArrayList<>();
        categoryMiddleClassTypeMap.forEach((category, middleClassTypeMap) -> {
            List<PanasonicFindResponse> middleClassNodes = middleClassTypeMap.entrySet().stream()
                    .map(entry -> {
                        String middleClass = entry.getKey();
                        List<String> types = entry.getValue(); // 直接获取第三级节点

                        // 创建 middleClass 节点
                        PanasonicFindResponse middleClassNode = new PanasonicFindResponse();
                        middleClassNode.setCategory(category);
                        middleClassNode.setMiddleClass(middleClass);
                        middleClassNode.setChild(buildTypeNodes(types)); // 构建第三级节点
                        return middleClassNode;
                    })
                    .collect(Collectors.toList());

            // 创建 category 节点
            PanasonicFindResponse categoryNode = new PanasonicFindResponse();
            categoryNode.setCategory(category);
            categoryNode.setMiddleClass(""); // 根节点没有 middleClass
            categoryNode.setChild(middleClassNodes); // 添加第二级节点
            tree.add(categoryNode);
        });

        return tree;
    }

    // 辅助方法，构建第三级节点
    private List<PanasonicFindResponse> buildTypeNodes(List<String> types) {
        return types.stream()
                .map(type -> {
                    PanasonicFindResponse typeNode = new PanasonicFindResponse();
                    typeNode.setCategory(""); // 第三级节点没有 category
                    typeNode.setMiddleClass(""); // 第三级节点没有 middleClass
                    typeNode.setType(type);
                    // 可以根据需要设置其他字段
                    return typeNode;
                })
                .collect(Collectors.toList());
    }
}
