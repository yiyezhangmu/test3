package com.coolcollege.intelligent.dao.newstore.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.newstore.NsStoreMapper;
import com.coolcollege.intelligent.model.export.request.NsStoreListExportRequest;
import com.coolcollege.intelligent.model.newstore.NsStoreDO;
import com.coolcollege.intelligent.model.newstore.dto.NsCommonNumDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsStoreDTO;
import com.coolcollege.intelligent.model.newstore.dto.NsStoreQueryDTO;
import com.coolcollege.intelligent.model.newstore.request.NsStoreListRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhangnan
 * @description: 新店dao
 * @date 2022/3/6 1:28 PM
 */
@Repository
public class NsStoreDao {

    @Resource
    private NsStoreMapper nsStoreMapper;

    /**
     * 新店列表
     * @param enterpriseId
     * @param request
     * @return
     */
    public PageInfo<NsStoreDTO> selectRecordDOPage(String enterpriseId, NsStoreListRequest request) {
        if(StringUtils.isBlank(enterpriseId)) {
            return new PageInfo<>();
        }
        PageHelper.startPage(Optional.ofNullable(request.getPageNum()).orElse(Constants.INDEX_ONE)
                , Optional.ofNullable(request.getPageSize()).orElse(Constants.DEFAULT_PAGE_SIZE));
        NsStoreQueryDTO queryDTO = new NsStoreQueryDTO();
        if(request.getCreateTimeStart() != null){
            queryDTO.setCreateTimeStart(new Date(request.getCreateTimeStart()));
        }
        if(request.getCreateTimeEnd() != null){
            queryDTO.setCreateTimeEnd(new Date(request.getCreateTimeEnd()));
        }
        queryDTO.setRegionId(request.getRegionId());
        queryDTO.setName(request.getName());
        queryDTO.setTypeList(request.getTypeList());
        queryDTO.setStatusList(request.getStatusList());
        queryDTO.setDirectUserId(request.getDirectUserId());
        // 通过前端是否传经纬度 区分PC端 还是移动端  移动端会传经纬度
        if(StringUtils.isNotBlank(request.getLongitude())){
//            queryDTO.setCreateUserId(request.getCreateUserId());
            queryDTO.setLongitude(request.getLongitude());
            queryDTO.setLatitude(request.getLatitude());
        }
        return new PageInfo<>(nsStoreMapper.selectAllByQuery(enterpriseId, queryDTO));
    }

    /**
     * 新增新店
     * @param enterpriseId 企业id
     * @param recordDO NsStoreDO
     */
    public void insertNsStore(String enterpriseId, NsStoreDO recordDO) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(recordDO)) {
            return;
        }
        nsStoreMapper.insertSelective(recordDO, enterpriseId);
    }

    public Integer updateNsStore(String enterpriseId, NsStoreDO record) {
        return nsStoreMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    public void deleteNsStoreById(String eid, Long id) {
        NsStoreDO nsStoreDO = new NsStoreDO();
        nsStoreDO.setId(id);
        nsStoreDO.setDeleted(Boolean.TRUE);
        nsStoreMapper.updateByPrimaryKeySelective(nsStoreDO, eid);
    }

    /**
     * 根据id查询新店
     * @param enterpriseId 企业id
     * @param id 新店id
     * @return NsStoreDO
     */
    public NsStoreDO selectById(String enterpriseId, Long id) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)) {
            return null;
        }
        return nsStoreMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     * 获取新店信息含经纬度
     * @param enterpriseId
     * @param id
     * @return
     */
    public NsStoreDTO getNsStoreDTOById(String enterpriseId, Long id) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)) {
            return null;
        }
        return nsStoreMapper.getNsStoreDTOById(id, enterpriseId);
    }

    /**
     * 根据新店id列表查询
     * @param enterpriseId 企业id
     * @param newStoreIds 新店id列表
     * @return List<NsStoreDO>
     */
    public List<NsStoreDO> selectByIds(String enterpriseId, List<Long> newStoreIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(newStoreIds)) {
            return Lists.newArrayList();
        }
        return nsStoreMapper.selectByIds(enterpriseId, newStoreIds);
    }

    /**
     * 批量更新
     * @param enterpriseId 企业id
     * @param nsStoreDOList List<NsStoreDO>
     */
    public void batchUpdate(String enterpriseId, List<NsStoreDO> nsStoreDOList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(nsStoreDOList)) {
            return;
        }
        nsStoreMapper.batchUpdate(enterpriseId, nsStoreDOList);
    }

    /**
     * 根据新店类型和状态统计新店数量
     * @param enterpriseId 企业id
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param regionId 区域路径
     * @return List<NsCommonCountDTO>
     */
    public List<NsCommonNumDTO> selectCountByStoreTypeAndStatus(String enterpriseId, Date beginDate, Date endDate, Long regionId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(beginDate) || Objects.isNull(endDate) || Objects.isNull(regionId)) {
            return Lists.newArrayList();
        }
        return nsStoreMapper.selectCountByStoreTypeAndStatus(enterpriseId, beginDate, endDate, regionId);
    }

    /**
     * 根据新店id更新完成进度
     * @param enterpriseId 企业id
     * @param newStoreId 新店id
     * @param progress 完成进度
     * @param visitTime 拜访时间
     */
    public void updateProgressById(String enterpriseId, Long newStoreId, Long progress, Date visitTime) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(newStoreId) || Objects.isNull(progress)
            || Objects.isNull(visitTime)) {
            return;
        }
        NsStoreDO nsStoreDO = new NsStoreDO();
        nsStoreDO.setId(newStoreId);
        nsStoreDO.setProgress(progress);
        nsStoreDO.setVisitTime(visitTime);
        nsStoreMapper.updateByPrimaryKeySelective(nsStoreDO, enterpriseId);
    }

    /**
     * 查询新店数量
     * @param enterpriseId 企业id
     * @param request NsStoreListExportRequest
     * @return
     */
    public Long selectStoreCount(@Param("enterpriseId") String enterpriseId, NsStoreListExportRequest request) {
        if(StringUtils.isBlank(enterpriseId)) {
            return Constants.LONG_ZERO;
        }
        NsStoreQueryDTO queryDTO = new NsStoreQueryDTO();
        if(request.getCreateTimeStart() != null){
            queryDTO.setCreateTimeStart(new Date(request.getCreateTimeStart()));
        }
        if(request.getCreateTimeEnd() != null){
            queryDTO.setCreateTimeEnd(new Date(request.getCreateTimeEnd()));
        }
        queryDTO.setRegionId(request.getRegionId());
        queryDTO.setName(request.getName());
        queryDTO.setTypeList(request.getTypeList());
        queryDTO.setStatusList(request.getStatusList());
        queryDTO.setDirectUserId(request.getDirectUserId());
        return nsStoreMapper.selectStoreCount(enterpriseId, queryDTO);
    }

    /**
     * 根据创建人id和新店名称查询数量
     * @param enterpriseId 企业id
     * @param createUserId 创建人id
     * @param newStoreName 新店名称
     * @param newStoreId 新店id
     * @return Long
     */
    public Long selectCountByCreateUserIdAndStoreName(String enterpriseId, String createUserId, String newStoreName, Long newStoreId) {
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(createUserId) || StringUtils.isBlank(newStoreName)) {
            return Constants.LONG_ZERO;
        }
        return nsStoreMapper.selectCountByCreateUserIdAndStoreName(enterpriseId, createUserId, newStoreName, newStoreId);
    }
}
