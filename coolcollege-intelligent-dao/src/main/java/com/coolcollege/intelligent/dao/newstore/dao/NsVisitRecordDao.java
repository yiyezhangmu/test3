package com.coolcollege.intelligent.dao.newstore.dao;
import com.coolcollege.intelligent.model.export.request.NsStoreListExportRequest;
import com.coolcollege.intelligent.model.export.request.NsVisitRecordListExportRequest;
import com.google.common.collect.Lists;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.newstore.NsVisitRecordStatusEnum;
import com.coolcollege.intelligent.dao.newstore.NsVisitRecordMapper;
import com.coolcollege.intelligent.model.newstore.NsVisitRecordDO;
import com.coolcollege.intelligent.model.newstore.dto.NsCommonNumDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsVisitNumDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsVisitRecordCorrectionDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsVisitRecordQueryDTO;
import com.coolcollege.intelligent.model.newstore.request.NsVisitRecordListRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 新店拜访记录dao
 * @author zhangnan
 * @date 2022-03-04 17:21
 */
@Repository
public class NsVisitRecordDao {

    @Resource
    private NsVisitRecordMapper nsVisitRecordMapper;

    /**
     * 查询用户今日进行中的拜访记录
     * @param enterpriseId 企业id
     * @param newStoreId 新店id
     * @param createUserId 用户id
     * @return NsVisitRecordDO
     */
    public NsVisitRecordDO selectUserOngoingRecordByNewStoreId(String enterpriseId, Long newStoreId, String createUserId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(newStoreId)) {
            return null;
        }
        NsVisitRecordQueryDTO queryDTO = new NsVisitRecordQueryDTO();
        queryDTO.setEnterpriseId(enterpriseId);
        queryDTO.setNewStoreId(newStoreId);
        // 今日
        queryDTO.setCreateDate(new Date());
        // 进行中
        queryDTO.setStatus(NsVisitRecordStatusEnum.ONGOING.getCode());
        List<NsVisitRecordDO> recordDOList = nsVisitRecordMapper.selectAllByQuery(queryDTO);
        if(CollectionUtils.isEmpty(recordDOList)) {
            return null;
        }
        return recordDOList.get(0);
    }

    /**
     * 新增拜访记录
     * @param enterpriseId 企业id
     * @param recordDO NsVisitRecordDO
     */
    public void insert(String enterpriseId, NsVisitRecordDO recordDO) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(recordDO)) {
            return;
        }
        nsVisitRecordMapper.insertSelective(recordDO, enterpriseId);
    }

    /**
     * 根据id查询拜访记录
     * @param enterpriseId 企业id
     * @param id 拜访记录id
     * @returnNsVisitRecordDO
     */
    public NsVisitRecordDO selectById(String enterpriseId, Long id) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)) {
            return null;
        }
        return nsVisitRecordMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     * 更新拜访记录
     * @param enterpriseId 企业id
     * @param recordDO NsVisitRecordDO
     */
    public void update(String enterpriseId, NsVisitRecordDO recordDO) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(recordDO)) {
            return;
        }
        nsVisitRecordMapper.updateByPrimaryKeySelective(recordDO, enterpriseId);
    }

    /**
     * 分页查询
     * @param enterpriseId 企业id
     * @param request NsVisitRecordListRequest
     * @return PageInfo<NsVisitRecordDO>
     */
    public PageInfo<NsVisitRecordDO> selectRecordDOPage(String enterpriseId, NsVisitRecordListRequest request) {
        if(StringUtils.isBlank(enterpriseId)) {
            return new PageInfo<>();
        }
        PageHelper.startPage(Optional.ofNullable(request.getPageNum()).orElse(Constants.INDEX_ONE)
                , Optional.ofNullable(request.getPageSize()).orElse(Constants.DEFAULT_PAGE_SIZE));
        NsVisitRecordQueryDTO queryDTO = new NsVisitRecordQueryDTO();
        queryDTO.setEnterpriseId(enterpriseId);
        queryDTO.setNewStoreId(request.getNewStoreId());
        queryDTO.setStatus(request.getStatus());
        queryDTO.setRegionId(request.getRegionId());
        queryDTO.setNewStoreName(request.getNewStoreName());
        queryDTO.setNewStoreTypes(request.getNewStoreTypes());
        queryDTO.setMetaTableIds(request.getMetaTableIds());
        if(Objects.nonNull(request.getCompletedBeginTime())) {
            queryDTO.setCompletedBeginTime(new Date(request.getCompletedBeginTime()));
        }
        if(Objects.nonNull(request.getCompletedEndTime())){
            queryDTO.setCompletedEndTime(new Date(request.getCompletedEndTime()));
        }
        return new PageInfo<>(nsVisitRecordMapper.selectAllByQuery(queryDTO));
    }

    /**
     * 根据新店类型和状态统计拜访次数
     * @param enterpriseId 企业id
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param regionId 区域id
     * @return List<NsCommonCountDTO>
     */
    public List<NsCommonNumDTO> selectNumByStoreTypeAndStatus(String enterpriseId, Date beginDate, Date endDate, Long regionId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(beginDate) || Objects.isNull(endDate) || Objects.isNull(regionId)) {
            return Lists.newArrayList();
        }
        return nsVisitRecordMapper.selectCountByStoreTypeAndStatus(enterpriseId, beginDate, endDate, regionId);
    }

    /**
     * 根据新店id列表查询拜访次数
     * @param enterpriseId 企业id
     * @param newStoreIds 新店id列表
     * @return List<NsVisitNumDTO>
     */
    public List<NsVisitNumDTO> selectNumByStoreIds(String enterpriseId, List<Long> newStoreIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(newStoreIds)) {
            return Lists.newArrayList();
        }
        return nsVisitRecordMapper.selectNumByStoreIds(enterpriseId, newStoreIds);
    }

    /**
     * 根据新店id更新新店数据
     * @param enterpriseId 企业id
     * @param correctionDTO NsVisitRecordCorrectionDTO
     */
    public void updateNewStoreInfoByNewStoreId(String enterpriseId, NsVisitRecordCorrectionDTO correctionDTO) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(correctionDTO)) {
            return;
        }
        nsVisitRecordMapper.updateNewStoreInfoByNewStoreId(enterpriseId, correctionDTO);
    }

    /**
     * 查询拜访记录数量
     * @param request NsVisitRecordListExportRequest
     * @return Long
     */
    public Long selectVisitRecordCount(String enterpriseId, NsVisitRecordListExportRequest request) {
        if(StringUtils.isBlank(enterpriseId)) {
            return Constants.LONG_ZERO;
        }
        NsVisitRecordQueryDTO queryDTO = new NsVisitRecordQueryDTO();
        queryDTO.setEnterpriseId(enterpriseId);
        queryDTO.setNewStoreId(request.getNewStoreId());
        queryDTO.setStatus(request.getStatus());
        queryDTO.setRegionId(request.getRegionId());
        queryDTO.setNewStoreName(request.getNewStoreName());
        queryDTO.setNewStoreTypes(request.getNewStoreTypes());
        queryDTO.setMetaTableIds(request.getMetaTableIds());
        if(Objects.nonNull(request.getCompletedBeginTime())) {
            queryDTO.setCompletedBeginTime(new Date(request.getCompletedBeginTime()));
        }
        if(Objects.nonNull(request.getCompletedEndTime())){
            queryDTO.setCompletedEndTime(new Date(request.getCompletedEndTime()));
        }
        return nsVisitRecordMapper.selectVisitRecordCount(queryDTO);
    }
}
