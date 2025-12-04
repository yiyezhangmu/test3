package com.coolcollege.intelligent.service.newstore.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.newstore.NsVisitRecordSignInStatusEnum;
import com.coolcollege.intelligent.common.enums.newstore.NsVisitRecordStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaDefTableColumnDao;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaTableDao;
import com.coolcollege.intelligent.dao.newstore.dao.NsDataVisitTableColumnDao;
import com.coolcollege.intelligent.dao.newstore.dao.NsStoreDao;
import com.coolcollege.intelligent.dao.newstore.dao.NsVisitRecordDao;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.NsVisitRecordDTO;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.newstore.NsDataVisitTableColumnDO;
import com.coolcollege.intelligent.model.newstore.NsVisitRecordDO;
import com.coolcollege.intelligent.model.newstore.dto.NsStoreDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsVisitTableDataColumnDTO;
import com.coolcollege.intelligent.model.newstore.request.*;
import com.coolcollege.intelligent.model.newstore.vo.*;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.newstore.NsVisitRecordService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 新店拜访记录
 * @author zhangnan
 * @date 2022-03-04 17:23
 */
@Service
public class NsVisitRecordServiceImpl implements NsVisitRecordService {

    @Resource
    private NsVisitRecordDao nsVisitRecordDao;
    @Resource
    private NsStoreDao nsStoreDao;
    @Resource
    private NsDataVisitTableColumnDao nsDataVisitTableColumnDao;
    @Resource
    private TbMetaTableDao tbMetaTableDao;
    @Resource
    private TbMetaDefTableColumnDao tbMetaDefTableColumnDao;
    @Lazy
    @Resource
    private RegionService regionService;

    @Override
    public NsVisitSignInVO signIn(String enterpriseId, NsVisitSignInRequest request, CurrentUser currentUser) {
        // 查询今日进行中的拜访记录
        NsVisitRecordDO todayOngoingVisitRecord = nsVisitRecordDao.selectUserOngoingRecordByNewStoreId(enterpriseId,
                request.getNewStoreId(), currentUser.getUserId());
        // 查询到今日有未完成拜访记录，直接返回提示
        if(Objects.nonNull(todayOngoingVisitRecord)) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_TODAY_HAS_ONGOING_RECORD);
        }
        // 查询新店
        NsStoreDTO nsStoreDTO = nsStoreDao.getNsStoreDTOById(enterpriseId, request.getNewStoreId());
        if(Objects.isNull(nsStoreDTO) || nsStoreDTO.getDeleted()) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_NOT_FOUND);
        }
        if(!nsStoreDTO.getDirectUserId().equals(currentUser.getUserId())) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_ERROR_DIRECT_USER);
        }
        // 新增拜访记录
        NsVisitRecordDO recordDO = new NsVisitRecordDO();
        recordDO.setDeleted(false);
        recordDO.setCreateTime(new Date());
        recordDO.setCreateDate(new Date());
        recordDO.setCreateUserId(currentUser.getUserId());
        recordDO.setCreateUserName(currentUser.getName());
        recordDO.setStatus(NsVisitRecordStatusEnum.ONGOING.getCode());
        recordDO.setDataTableStatus(Constants.ZERO);
        recordDO.setRegionId(nsStoreDTO.getRegionId());
        recordDO.setRegionPath(nsStoreDTO.getRegionPath());
        recordDO.setNewStoreCreateDate(nsStoreDTO.getCreateDate());
        recordDO.setNewStoreId(nsStoreDTO.getId());
        recordDO.setNewStoreType(nsStoreDTO.getType());
        recordDO.setNewStoreStatus(nsStoreDTO.getStatus());
        recordDO.setNewStoreName(nsStoreDTO.getName());
        recordDO.setNewStoreLongitudeLatitude(nsStoreDTO.getLongitude() + Constants.COMMA + nsStoreDTO.getLatitude());
        recordDO.setNewStoreLocationAddress(nsStoreDTO.getLocationAddress());
        recordDO.setSignInTime(new Date());
        recordDO.setSignInStatus(NsVisitRecordSignInStatusEnum.NORMAL.getCode());
        recordDO.setSignInAddress(request.getSignInAddress());
        recordDO.setSignInLongitudeLatitude(request.getSignInLocation());
        nsVisitRecordDao.insert(enterpriseId, recordDO);
        NsVisitSignInVO signInVO = new NsVisitSignInVO();
        signInVO.setRecordId(recordDO.getId());
        return signInVO;
    }

    @Override
    public NsTodayOngoingRecordVO getTodayOngoingRecord(String enterpriseId, Long newStoreId, String userId) {
        NsVisitRecordDO todayOngoingVisitRecord = nsVisitRecordDao.selectUserOngoingRecordByNewStoreId(enterpriseId, newStoreId, userId);
        // 没查到今日进行中的拜访，直接返回null
        if(Objects.isNull(todayOngoingVisitRecord)) {
            return null;
        }
        TbMetaTableDO metaTableDO = tbMetaTableDao.selectById(enterpriseId, todayOngoingVisitRecord.getMetaTableId());
        NsTodayOngoingRecordVO recordVO = new NsTodayOngoingRecordVO();
        recordVO.setId(todayOngoingVisitRecord.getId());
        recordVO.setDataTableStatus(todayOngoingVisitRecord.getDataTableStatus());
        recordVO.setNewStoreId(todayOngoingVisitRecord.getNewStoreId());
        recordVO.setNewStoreType(todayOngoingVisitRecord.getNewStoreType());
        recordVO.setNewStoreStatus(todayOngoingVisitRecord.getNewStoreStatus());
        recordVO.setNewStoreName(todayOngoingVisitRecord.getNewStoreName());
        recordVO.setNewStoreLongitudeLatitude(todayOngoingVisitRecord.getNewStoreLongitudeLatitude());
        recordVO.setSignInTime(todayOngoingVisitRecord.getSignInTime());
        recordVO.setSignInAddress(todayOngoingVisitRecord.getSignInAddress());
        recordVO.setSignInLongitudeLatitude(todayOngoingVisitRecord.getSignInLongitudeLatitude());
        recordVO.setMetaTableId(todayOngoingVisitRecord.getMetaTableId());
        if(Objects.nonNull(metaTableDO)) {
            recordVO.setMetaTableName(metaTableDO.getTableName());
        }
        return recordVO;
    }

    @Override
    public void updateRecord(String enterpriseId, NsVisitRecordUpdateRequest request, CurrentUser currentUser) {
        NsVisitRecordDO recordDO = nsVisitRecordDao.selectById(enterpriseId, request.getId());
        // 拜访记录不存在，提示未找到拜访记录
        if(Objects.isNull(recordDO)) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_VISIT_RECORD_NOT_FOUND);
        }
        // 新拜访表和已保存的拜访表是同一张表，直接返回
        if(request.getMetaTableId().equals(recordDO.getMetaTableId())) {
            return;
        }
        // 查询新店
        NsStoreDTO nsStoreDTO = nsStoreDao.getNsStoreDTOById(enterpriseId, recordDO.getNewStoreId());
        if(!nsStoreDTO.getDirectUserId().equals(currentUser.getUserId())) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_ERROR_DIRECT_USER);
        }
        // 查询检查表，锁表
        TbMetaTableDO metaTableDO = tbMetaTableDao.selectById(enterpriseId, request.getMetaTableId());
        if(Objects.isNull(metaTableDO)) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_VISIT_TABLE_NOT_FOUND);
        }
        // 查询检查项
        List<TbMetaDefTableColumnDO> tableColumnDOList = tbMetaDefTableColumnDao.selectByMetaTableId(enterpriseId, request.getMetaTableId());
        recordDO.setMetaTableId(request.getMetaTableId());
        recordDO.setUpdateTime(new Date());
        recordDO.setUpdateUserId(currentUser.getUserId());
        recordDO.setUpdateUserName(currentUser.getName());
        // 组装拜访表数据项
        List<NsDataVisitTableColumnDO> dataColumnDOList = tableColumnDOList.stream()
                .map(defTableColumnDO -> this.parseDefColumnDoToDataDo(defTableColumnDO, currentUser, recordDO))
                .collect(Collectors.toList());
        // 锁拜访表
        tbMetaTableDao.updateLockedByIds(enterpriseId, Lists.newArrayList(request.getMetaTableId()));
        // 更新拜访记录
        nsVisitRecordDao.update(enterpriseId, recordDO);
        // 删除旧拜访已保存的表数据
        nsDataVisitTableColumnDao.deleteByRecordId(enterpriseId, recordDO.getId());
        // 保存新拜访检查项数据
        nsDataVisitTableColumnDao.batchInsert(enterpriseId, dataColumnDOList);
    }

    @Override
    public void submitRecord(String enterpriseId, NsVisitRecordSubmitRequest request, CurrentUser currentUser) {
        NsVisitRecordDO recordDO = nsVisitRecordDao.selectById(enterpriseId, request.getId());
        // 拜访记录不存在，提示未找到拜访记录
        if(Objects.isNull(recordDO)) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_VISIT_RECORD_NOT_FOUND);
        }
        // 查询新店
        NsStoreDTO nsStoreDTO = nsStoreDao.getNsStoreDTOById(enterpriseId, recordDO.getNewStoreId());
        if(!nsStoreDTO.getDirectUserId().equals(currentUser.getUserId())) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_ERROR_DIRECT_USER);
        }
        recordDO.setStatus(NsVisitRecordStatusEnum.COMPLETED.getCode());
        recordDO.setProgress(request.getProgress());
        recordDO.setCompletedTime(new Date());
        recordDO.setUpdateTime(new Date());
        recordDO.setUpdateUserId(currentUser.getUserId());
        recordDO.setUpdateUserName(currentUser.getName());
        nsVisitRecordDao.update(enterpriseId, recordDO);
        // 跟新新店完成度，拜访时间
        nsStoreDao.updateProgressById(enterpriseId, recordDO.getNewStoreId(), recordDO.getProgress(), recordDO.getUpdateTime());
        // 更新拜访表数据状态
        nsDataVisitTableColumnDao.updateRecordStatusByRecordId(enterpriseId, request.getId(), NsVisitRecordStatusEnum.COMPLETED.getCode());
    }

    @Override
    public NsVisitTableInfoVO getVisitTableInfoByRecordId(String enterpriseId, Long recordId) {
        NsVisitRecordDO recordDO = nsVisitRecordDao.selectById(enterpriseId, recordId);
        // 拜访记录不存在，提示未找到拜访记录
        if(Objects.isNull(recordDO)) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_VISIT_RECORD_NOT_FOUND);
        }
        List<NsDataVisitTableColumnDO> dataColumnDOList = nsDataVisitTableColumnDao.selectByRecordId(enterpriseId, recordId);
        List<Long> metaColumnIds = dataColumnDOList.stream().map(NsDataVisitTableColumnDO::getMetaColumnId).collect(Collectors.toList());
        List<TbMetaDefTableColumnDO> visitColumnDOList = tbMetaDefTableColumnDao.selectByIds(enterpriseId, metaColumnIds);
        List<NsVisitTableDataColumnDTO> dataDtoList = Lists.newArrayList();
        for (NsDataVisitTableColumnDO dataColumnDO : dataColumnDOList) {
            NsVisitTableDataColumnDTO dataColumnDTO = new NsVisitTableDataColumnDTO();
            dataColumnDTO.setId(dataColumnDO.getId());
            dataColumnDTO.setMetaColumnId(dataColumnDO.getMetaColumnId());
            dataColumnDTO.setValue1(dataColumnDO.getValue1());
            dataColumnDTO.setValue2(dataColumnDO.getValue2());
            dataDtoList.add(dataColumnDTO);
        }
        NsVisitTableInfoVO tableInfoVO = new NsVisitTableInfoVO();
        tableInfoVO.setVisitColumns(visitColumnDOList.stream().sorted(Comparator.comparing(TbMetaDefTableColumnDO::getOrderNum)).collect(Collectors.toList()));
        tableInfoVO.setDataVisitColumns(dataDtoList);
        return tableInfoVO;
    }

    @Override
    public void saveVisitTableInfo(String enterpriseId, NsDataVisitTableColumnSaveRequest request, CurrentUser currentUser) {
        NsVisitRecordDO recordDO = nsVisitRecordDao.selectById(enterpriseId, request.getRecordId());
        // 拜访记录不存在，提示未找到拜访记录
        if(Objects.isNull(recordDO)) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_VISIT_RECORD_NOT_FOUND);
        }
        if(CollectionUtils.isEmpty(request.getDataVisitColumns())) {
            return;
        }
        // 组装拜访表检查项id，查询拜访表检查项
        List<Long> visitColumnIds = request.getDataVisitColumns().stream().map(NsVisitTableDataColumnDTO::getMetaColumnId).collect(Collectors.toList());
        List<TbMetaDefTableColumnDO> visitColumnDOList = tbMetaDefTableColumnDao.selectByIds(enterpriseId, visitColumnIds);
        Map<Long, TbMetaDefTableColumnDO> visitColumnDOMap = visitColumnDOList.stream().collect(Collectors.toMap(TbMetaDefTableColumnDO::getId, Function.identity()));
        // 检查表可以新增检查项，所以会有新增的情况，根据前端是否传id判断更新还是新增
        List<NsDataVisitTableColumnDO> insertDOList = Lists.newArrayList();
        List<NsDataVisitTableColumnDO> updateDOList = Lists.newArrayList();
        for (NsVisitTableDataColumnDTO dataVisitColumnDTO : request.getDataVisitColumns()) {
            if(Objects.isNull(dataVisitColumnDTO.getId())) {
                NsDataVisitTableColumnDO insertDO = this.parseDefColumnDoToDataDo(visitColumnDOMap.get(dataVisitColumnDTO.getMetaColumnId()), currentUser, recordDO);
                insertDO.setValue1(dataVisitColumnDTO.getValue1());
                insertDO.setValue2(dataVisitColumnDTO.getValue2());
                insertDOList.add(insertDO);
            }else if(Objects.nonNull(dataVisitColumnDTO.getValue1())) {
                updateDOList.add(this.parseDataDtoToDataDo(dataVisitColumnDTO));
            }
        }
        nsDataVisitTableColumnDao.batchInsert(enterpriseId, insertDOList);
        nsDataVisitTableColumnDao.batchUpdateValue(enterpriseId, updateDOList);
    }

    @Override
    public PageInfo<NsVisitRecordListVO> getRecordList(String enterpriseId, NsVisitRecordListRequest request) {
        PageInfo<NsVisitRecordDO> visitRecordDOPageInfo = nsVisitRecordDao.selectRecordDOPage(enterpriseId, request);
        PageInfo<NsVisitRecordListVO> voPageInfo = new PageInfo<>();
        voPageInfo.setTotal(visitRecordDOPageInfo.getTotal());
        voPageInfo.setPageNum(visitRecordDOPageInfo.getPageNum());
        voPageInfo.setPageSize(visitRecordDOPageInfo.getPageSize());
        voPageInfo.setPages(visitRecordDOPageInfo.getPages());
        if(CollectionUtils.isEmpty(visitRecordDOPageInfo.getList())) {
            voPageInfo.setList(Lists.newArrayList());
            return voPageInfo;
        }
        // 组装拜访表id列表，查询拜访表信息
        List<Long> metaTableIds = visitRecordDOPageInfo.getList().stream().map(NsVisitRecordDO::getMetaTableId).collect(Collectors.toList());
        List<TbMetaTableDO> metaTableDOList = tbMetaTableDao.selectByIds(enterpriseId, metaTableIds);
        Map<Long, TbMetaTableDO> metaTableDOMap = metaTableDOList.stream().collect(Collectors.toMap(TbMetaTableDO::getId, Function.identity()));
        // 组装区域id列表，查询区域名称
        List<String> regionIds = visitRecordDOPageInfo.getList().stream().map(NsVisitRecordDO::getRegionId).map(String::valueOf).collect(Collectors.toList());
        List<RegionDO> regionDOList = regionService.getRegionDOsByRegionIds(enterpriseId, regionIds);
        Map<Long, RegionDO> regionDOMap = regionDOList.stream().collect(Collectors.toMap(RegionDO::getId, Function.identity()));
        List<NsVisitRecordListVO> voList = Lists.newArrayList();
        for (NsVisitRecordDO nsVisitRecordDO : visitRecordDOPageInfo.getList()) {
            NsVisitRecordListVO recordListVO = new NsVisitRecordListVO();
            recordListVO.setId(nsVisitRecordDO.getId());
            recordListVO.setMetaTableId(nsVisitRecordDO.getMetaTableId());
            TbMetaTableDO tableDO = metaTableDOMap.get(nsVisitRecordDO.getMetaTableId());
            if(Objects.nonNull(tableDO)) {
                recordListVO.setMetaTableName(tableDO.getTableName());
            }
            RegionDO regionDO = regionDOMap.get(nsVisitRecordDO.getRegionId());
            recordListVO.setRegionId(nsVisitRecordDO.getRegionId());
            if(Objects.nonNull(regionDO)) {
                recordListVO.setRegionName(regionDO.getName());
            }
            recordListVO.setStatus(nsVisitRecordDO.getStatus());
            recordListVO.setNewStoreId(nsVisitRecordDO.getNewStoreId());
            recordListVO.setNewStoreName(nsVisitRecordDO.getNewStoreName());
            recordListVO.setNewStoreType(nsVisitRecordDO.getNewStoreType());
            recordListVO.setNewStoreLocationAddress(nsVisitRecordDO.getNewStoreLocationAddress());
            recordListVO.setNewStoreLongitudeLatitude(nsVisitRecordDO.getNewStoreLongitudeLatitude());
            recordListVO.setCreateUserId(nsVisitRecordDO.getCreateUserId());
            recordListVO.setCreateUserName(nsVisitRecordDO.getCreateUserName());
            recordListVO.setUpdateUserId(nsVisitRecordDO.getUpdateUserId());
            recordListVO.setUpdateUserName(nsVisitRecordDO.getUpdateUserName());
            recordListVO.setSignInTime(nsVisitRecordDO.getSignInTime());
            recordListVO.setSignInAddress(nsVisitRecordDO.getSignInAddress());
            recordListVO.setSignInLongitudeLatitude(nsVisitRecordDO.getSignInLongitudeLatitude());
            recordListVO.setProgress(nsVisitRecordDO.getProgress());
            recordListVO.setCompletedTime(nsVisitRecordDO.getCompletedTime());
            voList.add(recordListVO);
        }
        voPageInfo.setList(voList);
        return voPageInfo;
    }

    @Override
    public NsVisitRecordDetailVO getRecordDetail(String enterpriseId, Long recordId) {
        NsVisitRecordDO recordDO = nsVisitRecordDao.selectById(enterpriseId, recordId);
        // 拜访记录不存在，提示未找到拜访记录
        if(Objects.isNull(recordDO)) {
            throw new ServiceException(ErrorCodeEnum.NEW_STORE_VISIT_RECORD_NOT_FOUND);
        }
        TbMetaTableDO metaTableDO = tbMetaTableDao.selectById(enterpriseId, recordDO.getMetaTableId());
        NsVisitRecordDetailVO detailVO = new NsVisitRecordDetailVO();
        detailVO.setNewStoreId(recordDO.getNewStoreId());
        detailVO.setNewStoreStatus(recordDO.getNewStoreStatus());
        detailVO.setNewStoreName(recordDO.getNewStoreName());
        detailVO.setNewStoreType(recordDO.getNewStoreType());
        detailVO.setNewStoreLocationAddress(recordDO.getNewStoreLocationAddress());
        detailVO.setNewStoreLongitudeLatitude(recordDO.getNewStoreLongitudeLatitude());
        // 完整区域路径
        detailVO.setFullRegionName(regionService.getAllRegionName(enterpriseId, recordDO.getRegionId()).getAllRegionName());
        detailVO.setProgress(recordDO.getProgress());
        detailVO.setCreateUserId(recordDO.getCreateUserId());
        detailVO.setCreateUserName(recordDO.getCreateUserName());
        detailVO.setUpdateUserId(recordDO.getUpdateUserId());
        detailVO.setUpdateUserName(recordDO.getUpdateUserName());
        detailVO.setSignInTime(recordDO.getSignInTime());
        detailVO.setSignInAddress(recordDO.getSignInAddress());
        detailVO.setSignInLongitudeLatitude(recordDO.getSignInLongitudeLatitude());
        detailVO.setCompletedTime(recordDO.getCompletedTime());
        detailVO.setDataTableStatus(recordDO.getDataTableStatus());
        detailVO.setMetaTableId(recordDO.getMetaTableId());
        if(Objects.nonNull(metaTableDO)) {
            detailVO.setMetaTableName(metaTableDO.getTableName());
        }
        return detailVO;
    }

    @Override
    public PageDTO<NsVisitRecordListVO> getVisitRecordList(String enterpriseId, NsVisitRecordDTO param) {
        NsVisitRecordListRequest request = new NsVisitRecordListRequest();
        BeanUtils.copyProperties(param, request);
        PageInfo<NsVisitRecordListVO> recordList = this.getRecordList(enterpriseId, request);
        PageDTO<NsVisitRecordListVO> result = new PageDTO<>();
        result.setPageNum(recordList.getPageNum());
        result.setPageSize(recordList.getPageSize());
        result.setTotal(recordList.getTotal());
        result.setList(recordList.getList());
        return result;
    }

    /**
     * 拜访表检查项DO转拜访数据检查项DO
     * @param defTableColumnDO TbMetaDefTableColumnDO
     * @param currentUser CurrentUser
     * @param recordDO NsVisitRecordDO
     * @return NsDataVisitTableColumnDO
     */
    private NsDataVisitTableColumnDO parseDefColumnDoToDataDo(TbMetaDefTableColumnDO defTableColumnDO, CurrentUser currentUser, NsVisitRecordDO recordDO) {
        NsDataVisitTableColumnDO dataDO = new NsDataVisitTableColumnDO();
        dataDO.setDeleted(false);
        dataDO.setCreateTime(new Date());
        dataDO.setCreateDate(new Date());
        dataDO.setCreateUserId(currentUser.getUserId());
        dataDO.setNewStoreId(recordDO.getNewStoreId());
        dataDO.setNewStoreName(recordDO.getNewStoreName());
        dataDO.setRegionId(recordDO.getRegionId());
        dataDO.setRegionPath(recordDO.getRegionPath());
        dataDO.setRecordId(recordDO.getId());
        dataDO.setRecordStatus(NsVisitRecordStatusEnum.ONGOING.getCode());
        dataDO.setMetaTableId(defTableColumnDO.getMetaTableId());
        dataDO.setMetaColumnId(defTableColumnDO.getId());
        dataDO.setMetaColumnName(defTableColumnDO.getColumnName());
        dataDO.setDescription(defTableColumnDO.getDescription());
        dataDO.setSubmitStatus(Constants.INDEX_ZERO);
        return dataDO;
    }

    /**
     * 拜访表数据dto转拜访表do
     * @param dataColumnDTO NsVisitTableDataColumnDTO
     * @return NsDataVisitTableColumnDO
     */
    private NsDataVisitTableColumnDO parseDataDtoToDataDo(NsVisitTableDataColumnDTO dataColumnDTO) {
        NsDataVisitTableColumnDO dataVisitTableColumnDO = new NsDataVisitTableColumnDO();
        dataVisitTableColumnDO.setId(dataColumnDTO.getId());
        dataVisitTableColumnDO.setValue1(dataColumnDTO.getValue1());
        dataVisitTableColumnDO.setValue2(dataColumnDTO.getValue2());
        dataVisitTableColumnDO.setUpdateTime(new Date());
        return dataVisitTableColumnDO;
    }

}
