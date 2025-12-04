package com.coolcollege.intelligent.mapper.metatable;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaCategoryCountDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: TbMetaQuickColumnDAO
 * @Description: 快速检查项DAO
 * @date 2022-04-06 15:02
 */
@Service
public class TbMetaQuickColumnDAO {

    @Resource
    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;

    /**
     * 更新检查项状态
     * @param enterpriseId
     * @param id
     * @param statusEnum
     * @return
     */
    public Boolean updateStatus(String enterpriseId, Long id, MetaColumnStatusEnum statusEnum){
        if(Objects.isNull(statusEnum)){
            throw new ServiceException(ErrorCodeEnum.COLUMN_STATUS_NOT_CORRECT);
        }
        return tbMetaQuickColumnMapper.updateStatus(enterpriseId, id, statusEnum.getStatus()) > Constants.ZERO;
    }

    /**
     * 获取分类对应的项的数量
     * @param enterpriseId
     * @param categoryIds
     * @return
     */
    public Map<Long, Integer> getCategoryCountMap(String enterpriseId, List<Long> categoryIds){
        if(Objects.isNull(enterpriseId)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        if(CollectionUtils.isEmpty(categoryIds)){
            return Maps.newHashMap();
        }
        List<TbMetaCategoryCountDTO> categoryCount = tbMetaQuickColumnMapper.getCategoryCount(enterpriseId, categoryIds);
        return categoryCount.stream().collect(Collectors.toMap(k->k.getId(), v->v.getCnt()));
    }

    /**
     * 获取单个分类被关联的数量
     * @param enterpriseId
     * @param categoryId
     * @return
     */
    public Integer getCategoryCount(String enterpriseId, Long categoryId){
        if(Objects.isNull(enterpriseId) || Objects.isNull(categoryId)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        Map<Long, Integer> categoryCount = getCategoryCountMap(enterpriseId, Arrays.asList(categoryId));
        return categoryCount.get(categoryId);
    }

    /**
     * 更新分类
     * @param enterpriseId
     * @param fromCategoryId
     * @param toCategoryId
     * @param statusEnum
     * @return
     */
    public Integer updateColumnCategoryId(String enterpriseId, Long fromCategoryId, Long toCategoryId, MetaColumnStatusEnum statusEnum){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(fromCategoryId) || Objects.isNull(toCategoryId) || Objects.isNull(statusEnum)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        return tbMetaQuickColumnMapper.updateColumnCategoryId(enterpriseId, fromCategoryId, toCategoryId, statusEnum.getStatus());
    }

    /**
     * 获取同名的数量
     * @param enterpriseId
     * @param columnName
     * @param categoryId
     * @param columnType
     * @param excludeId
     * @return
     */
    public Integer getSameNameCount(String enterpriseId, String columnName, String createUser, Long categoryId, Integer columnType, Long excludeId){
        return tbMetaQuickColumnMapper.getSameNameCount(enterpriseId, columnName, createUser, categoryId, columnType, excludeId);
    }

    /**
     * 批量更新检查项状态
     * @param enterpriseId
     * @param ids
     * @param statusEnum
     * @return
     */
    public Boolean batchUpdateStatus(String enterpriseId, List<Long> ids, MetaColumnStatusEnum statusEnum) {
        if(Objects.isNull(statusEnum)){
            throw new ServiceException(ErrorCodeEnum.COLUMN_STATUS_NOT_CORRECT);
        }
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(ids)) {
            return Boolean.FALSE;
        }
        return tbMetaQuickColumnMapper.batchUpdateStatus(enterpriseId, ids, statusEnum.getStatus()) > Constants.ZERO;
    }

    /**
     * 配置检查表权限
     * @param enterpriseId
     * @param ids
     * @param tbMetaQuickColumnDO
     * @return
     */
    public Boolean batchUpdateQuickColumnAUth(String enterpriseId, List<Long> ids,TbMetaQuickColumnDO tbMetaQuickColumnDO){
        if (CollectionUtils.isEmpty(ids)||tbMetaQuickColumnDO==null){
            return Boolean.FALSE;
        }
        return tbMetaQuickColumnMapper.batchUpdateQuickColumnAUth(enterpriseId,ids,tbMetaQuickColumnDO);
    }

    public List<Long> getMetaColumnIdByCategoryId(String enterpriseId, Long categoryId,Long metaColumnId){
        if (categoryId==null&&metaColumnId==null ){
            return new ArrayList<>();
        }
        return tbMetaQuickColumnMapper.getMetaColumnIdByCategoryId(enterpriseId,categoryId,metaColumnId);
    }

    public List<TbMetaQuickColumnDO> getQuickColumnList(String enterpriseId){
        if(StringUtils.isBlank(enterpriseId)){
            return Lists.newArrayList();
        }
        return tbMetaQuickColumnMapper.getQuickColumnList(enterpriseId);
    }

    public Integer batchUpdateUseUserIds(String enterpriseId, List<TbMetaQuickColumnDO> updateList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(updateList)){
            return 0;
        }
        return tbMetaQuickColumnMapper.batchUpdateUseUserIds(enterpriseId, updateList);
    }

    public List<TbMetaQuickColumnDO> getByIds(String enterpriseId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return tbMetaQuickColumnMapper.selectByIds(enterpriseId, ids);
    }
}
