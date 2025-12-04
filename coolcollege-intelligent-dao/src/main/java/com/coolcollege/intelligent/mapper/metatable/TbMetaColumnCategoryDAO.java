package com.coolcollege.intelligent.mapper.metatable;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnCategoryMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnCategoryDO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: TbMetaColumnCategoryDAO
 * @Description:
 * @date 2022-04-01 20:46
 */
@Service
@Slf4j
public class TbMetaColumnCategoryDAO {

    @Resource
    private TbMetaColumnCategoryMapper tbMetaColumnCategoryMapper;

    /**
     * 获取检查项分类
     * @param enterpriseId
     * @param categoryName
     * @return
     */
    public List<TbMetaColumnCategoryDO> getMetaColumnCategoryList(String enterpriseId, String categoryName){
        if(StringUtils.isBlank(enterpriseId)){
            return Lists.newArrayList();
        }
        return tbMetaColumnCategoryMapper.getMetaColumnCategoryList(enterpriseId, categoryName);
    }

    /**
     * 新增分类
     * @param enterpriseId
     * @param param
     * @return
     */
    public Long addMetaColumnCategory(String enterpriseId, TbMetaColumnCategoryDO param){
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(param.getCategoryName())){
            log.info("addMetaColumnCategory enterpriseId：{},categoryName：{}", enterpriseId, param.getCategoryName());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }

        if(param.getCategoryName().length() > Constants.STR_LENGTH_LIMIT){
            log.info("addMetaColumnCategory categoryName：{}， size:{}", enterpriseId, param.getCategoryName(), param.getCategoryName().length());
            throw new ServiceException(ErrorCodeEnum.COLUMN_CATEGORY_NAME_TOO_LONG);
        }
        if(isSameNameCategory(enterpriseId, param.getCategoryName(), null)){
            log.info("addMetaColumnCategory 重名了 ：{}", param.getCategoryName());
            throw new ServiceException(ErrorCodeEnum.COLUMN_CATEGORY_NAME_REPETITION);
        }
        tbMetaColumnCategoryMapper.insertSelective(param, enterpriseId);
        return param.getId();
    }

    public Boolean updateMetaColumnCategory(String enterpriseId, TbMetaColumnCategoryDO param){
        if(Objects.isNull(param.getId())){
            log.info("updateMetaColumnCategory：{}", JSONObject.toJSONString(param));
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(param.getCategoryName())){
            log.info("updateMetaColumnCategory enterpriseId：{},categoryName：{}", enterpriseId, param.getCategoryName());
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        if(param.getCategoryName().length() > Constants.STR_LENGTH_LIMIT){
            log.info("updateMetaColumnCategory categoryName：{}， size:{}", enterpriseId, param.getCategoryName(), param.getCategoryName().length());
            throw new ServiceException(ErrorCodeEnum.COLUMN_CATEGORY_NAME_TOO_LONG);
        }
        if(isSameNameCategory(enterpriseId, param.getCategoryName(), param.getId())){
            log.info("updateMetaColumnCategory 重名了 ：{}", param.getCategoryName());
            throw new ServiceException(ErrorCodeEnum.COLUMN_CATEGORY_NAME_REPETITION);
        }
        return tbMetaColumnCategoryMapper.updateByPrimaryKeySelective(param, enterpriseId) > Constants.ZERO;
    }

    /**
     * 重名判断
     * @param categoryName
     * @param excludeId
     * @return
     */
    public boolean isSameNameCategory(String enterpriseId, String categoryName, Long excludeId){
        return tbMetaColumnCategoryMapper.getSameNameCategoryCount(enterpriseId, categoryName, excludeId) > Constants.ZERO;
    }

    /**
     * 获取分类详情
     * @param enterpriseId
     * @param id
     * @return
     */
    public TbMetaColumnCategoryDO selectByPrimaryKey(String enterpriseId, Long id){
        if(Objects.isNull(id) || StringUtils.isBlank(enterpriseId)){
            log.info("selectByPrimaryKey：参数缺失，enterpriseId：{}， id:{}", enterpriseId, id);
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        return tbMetaColumnCategoryMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     * 删除分类
     * @param enterpriseId
     * @param id
     * @return
     */
    public Boolean deletedMetaColumnCategory(String enterpriseId, Long id){
        if(Objects.isNull(id) || StringUtils.isBlank(enterpriseId)){
            log.info("deletedMetaColumnCategory：参数缺失，enterpriseId：{}， id:{}", enterpriseId, id);
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        return tbMetaColumnCategoryMapper.deleteByPrimaryKey(id, enterpriseId) > Constants.ZERO;
    }

    /**
     * 获取所有数量
     * @param enterpriseId
     * @return
     */
    public Integer getAllCount(String enterpriseId){
        if(StringUtils.isBlank(enterpriseId)){
            log.info("getAllCount：参数缺失，enterpriseId：{}", enterpriseId);
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        return tbMetaColumnCategoryMapper.getAllCount(enterpriseId);
    }

    /**
     * 获取分类列表
     * @param enterpriseId
     * @param categoryIds
     * @return
     */
    public Map<Long, String> getCategoryNameMap(String enterpriseId, List<Long> categoryIds){
        if(CollectionUtils.isEmpty(categoryIds)){
            return Maps.newHashMap();
        }
        List<TbMetaColumnCategoryDO> categoryList = tbMetaColumnCategoryMapper.getCategoryList(enterpriseId, categoryIds);
        if(CollectionUtils.isEmpty(categoryList)){
            return Maps.newHashMap();
        }
        return categoryList.stream().collect(Collectors.toMap(k->k.getId(), v->v.getCategoryName(), (o1,o2)->o2));
    }

    /**
     * 获取 "其他" 分类的id
     * @param enterpriseId
     * @return
     */
    public Long getOtherCategoryId(String enterpriseId){
        Long categoryId = tbMetaColumnCategoryMapper.getCategoryIdByName(enterpriseId, Constants.OTHER_CATEGORY);
        if(Objects.isNull(categoryId)){
            TbMetaColumnCategoryDO insert = new TbMetaColumnCategoryDO();
            insert.setCategoryName(Constants.OTHER_CATEGORY);
            insert.setIsDefault(true);
            insert.setCreateId("system");
            insert.setCreateTime(new Date());
            tbMetaColumnCategoryMapper.insertSelective(insert, enterpriseId);
            return insert.getId();
        }
        return categoryId;
    }

    /**
     * 批量插入
     */
    public int batchInsertSelective(List<TbMetaColumnCategoryDO> list, String enterpriseId){
        return tbMetaColumnCategoryMapper.batchInsertSelective(list,enterpriseId);
    }

    /**
     * 批量更新
     * @param enterpriseId
     * @param updateList
     * @return
     */
    public Integer bathUpdateMetaColumnCategory(String enterpriseId, List<TbMetaColumnCategoryDO> updateList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(updateList)){
            log.info("bathUpdateMetaColumnCategory enterpriseId：{},categoryName：{}", enterpriseId);
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        return tbMetaColumnCategoryMapper.bathUpdateMetaColumnCategory(enterpriseId, updateList);
    }

    /**
     * 获取分类名称
     * @param enterpriseId
     * @param categoryId
     * @return
     */
    public String getCategoryName(String enterpriseId, Long categoryId){
        Map<Long, String> categoryNameMap = getCategoryNameMap(enterpriseId, Arrays.asList(categoryId));
        if(MapUtils.isEmpty(categoryNameMap)){
            return null;
        }
        return categoryNameMap.get(categoryId);
    }

}
