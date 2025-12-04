package com.coolcollege.intelligent.service.metatable.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.mapper.metatable.TbMetaColumnCategoryDAO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaQuickColumnDAO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnCategoryDO;
import com.coolcollege.intelligent.model.metatable.request.ColumnCategoryRequest;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaColumnCategoryVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.metatable.ColumnCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/4/2 10:53
 * @Version 1.0
 */
@Service
@Slf4j
public class ColumnCategoryServiceImpl implements ColumnCategoryService {

    @Resource
    private TbMetaColumnCategoryDAO tbMetaColumnCategoryDAO;
    @Resource
    private TbMetaQuickColumnDAO tbMetaQuickColumnDAO;

    @Override
    public List<TbMetaColumnCategoryVO> getMetaColumnCategoryList(String enterpriseId, String categoryName) {
        List<TbMetaColumnCategoryDO> metaColumnCategoryList = tbMetaColumnCategoryDAO.getMetaColumnCategoryList(enterpriseId, categoryName);
        List<TbMetaColumnCategoryVO> resultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(metaColumnCategoryList)){
            List<Long> categoryIds = metaColumnCategoryList.stream().map(TbMetaColumnCategoryDO::getId).collect(Collectors.toList());
            Map<Long, Integer> categoryCountMap = tbMetaQuickColumnDAO.getCategoryCountMap(enterpriseId, categoryIds);
            for (TbMetaColumnCategoryDO tbMetaColumnCategory : metaColumnCategoryList) {
                Integer refNum = categoryCountMap.getOrDefault(tbMetaColumnCategory.getId(), Constants.ZERO);
                resultList.add(new TbMetaColumnCategoryVO(tbMetaColumnCategory.getId(), tbMetaColumnCategory.getCategoryName(), refNum, tbMetaColumnCategory.getIsDefault()));
            }
        }
        return resultList;
    }

    @Override
    public Long addMetaColumnCategory(String enterpriseId, ColumnCategoryRequest param) {
        Integer count = tbMetaColumnCategoryDAO.getAllCount(enterpriseId);
        if(count > Constants.ONE_MILLISECOND){
            throw new ServiceException(ErrorCodeEnum.COLUMN_CATEGORY_COUNT_MAX_LIMIT);
        }
        TbMetaColumnCategoryDO insert = new TbMetaColumnCategoryDO();
        insert.setCategoryName(param.getCategoryName());
        insert.setIsDefault(false);
        insert.setCreateId(UserHolder.getUser().getId());
        insert.setOrderNum(Constants.INDEX_ZERO);
        return tbMetaColumnCategoryDAO.addMetaColumnCategory(enterpriseId, insert);
    }

    @Override
    public Boolean updateMetaColumnCategory(String enterpriseId, ColumnCategoryRequest param) {
        TbMetaColumnCategoryDO tbMetaColumnCategory = tbMetaColumnCategoryDAO.selectByPrimaryKey(enterpriseId, param.getId());
        if(Objects.isNull(tbMetaColumnCategory)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        if(StringUtils.isBlank(param.getCategoryName())){
            log.info("updateMetaColumnCategory 分类为空");
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        if(tbMetaColumnCategory.getIsDefault()){
            throw new ServiceException(ErrorCodeEnum.COLUMN_CATEGORY_DEFAULT_NOT_UPDATE);
        }
        TbMetaColumnCategoryDO update = new TbMetaColumnCategoryDO();
        update.setId(param.getId());
        update.setCategoryName(param.getCategoryName());
        update.setUpdateId(UserHolder.getUser().getUserId());
        return tbMetaColumnCategoryDAO.updateMetaColumnCategory(enterpriseId, update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletedMetaColumnCategory(String enterpriseId, Long id) {
        TbMetaColumnCategoryDO tbMetaColumnCategory = tbMetaColumnCategoryDAO.selectByPrimaryKey(enterpriseId, id);
        if(Objects.isNull(tbMetaColumnCategory)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        Integer categoryCount = tbMetaQuickColumnDAO.getCategoryCount(enterpriseId, id);
        if(Objects.nonNull(categoryCount) && categoryCount > Constants.ZERO){
            throw new ServiceException(ErrorCodeEnum.COLUMN_CATEGORY_USING);
        }
        if(tbMetaColumnCategory.getIsDefault()){
            throw new ServiceException(ErrorCodeEnum.COLUMN_CATEGORY_DEFAULT_NOT_UPDATE);
        }
        Boolean result = tbMetaColumnCategoryDAO.deletedMetaColumnCategory(enterpriseId, id);
        //将已归档的检查项更新为其他分类
        Long otherCategoryId = tbMetaColumnCategoryDAO.getOtherCategoryId(enterpriseId);
        tbMetaQuickColumnDAO.updateColumnCategoryId(enterpriseId, id, otherCategoryId, MetaColumnStatusEnum.CLOSED);
        return result;
    }

    @Override
    public Boolean metaColumnCategorySort(String enterpriseId, List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return false;
        }
        int i = 0;
        List<TbMetaColumnCategoryDO> updateList = new ArrayList<>();
        for (Long id : ids) {
            TbMetaColumnCategoryDO update = new TbMetaColumnCategoryDO();
            update.setId(id);
            update.setOrderNum(i++);
            updateList.add(update);
        }
        tbMetaColumnCategoryDAO.bathUpdateMetaColumnCategory(enterpriseId, updateList);
        return true;
    }
}
