package com.coolcollege.intelligent.mapper.metatable;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnResultMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnResultDO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaQuickColumnResultVO;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: TbMetaQuickColumnResultDAO
 * @Description:
 * @date 2022-04-01 20:46
 */
@Service
public class TbMetaQuickColumnResultDAO {

    @Resource
    private TbMetaQuickColumnResultMapper tbMetaQuickColumnResultMapper;

    /**
     * 新增快速项
     * @param enterpriseId
     * @param columnResultList
     */
    public void addQuickColumnResult(String enterpriseId, List<TbMetaQuickColumnResultDO> columnResultList){
        if(CollectionUtils.isEmpty(columnResultList)){
            return;
        }
        for (TbMetaQuickColumnResultDO tbMetaQuickColumnResult : columnResultList) {
            tbMetaQuickColumnResultMapper.insertSelective(tbMetaQuickColumnResult, enterpriseId);
        }
    }

    /**
     *
     * @param enterpriseId
     * @param metaQuickColumnId
     * @param columnResultList
     */
    public void updateQuickColumnResult(String enterpriseId, Long metaQuickColumnId, List<TbMetaQuickColumnResultDO> columnResultList){
        if(CollectionUtils.isEmpty(columnResultList)){
            //删除改项下所有的结果项
            tbMetaQuickColumnResultMapper.deleteByMetaQuickColumnId(enterpriseId, metaQuickColumnId);
            return;
        }
        //获取项下所有的结果项
        List<Long> resultColumnIds = tbMetaQuickColumnResultMapper.getIdsByMetaQuickColumnId(enterpriseId, metaQuickColumnId);
        for (TbMetaQuickColumnResultDO tbMetaQuickColumnResult : columnResultList) {
            if(Objects.isNull(tbMetaQuickColumnResult.getId())){
                tbMetaQuickColumnResultMapper.insertSelective(tbMetaQuickColumnResult, enterpriseId);
            }else{
                tbMetaQuickColumnResultMapper.updateByPrimaryKeySelective(tbMetaQuickColumnResult, enterpriseId);
                resultColumnIds.remove(tbMetaQuickColumnResult.getId());
            }
        }
        //删除不用的项
        if(CollectionUtils.isNotEmpty(resultColumnIds)){
            tbMetaQuickColumnResultMapper.logicallyDeleteByIds(enterpriseId, resultColumnIds);
        }
    }

    /**
     * 获取项的结果项
     * @param enterpriseId
     * @param metaQuickColumnId
     * @return
     */
    public List<TbMetaQuickColumnResultVO> getColumnResultList(String enterpriseId, Long metaQuickColumnId){
        List<TbMetaQuickColumnResultVO> resultList = new ArrayList<>();
        List<TbMetaQuickColumnResultDO> columnResultList = tbMetaQuickColumnResultMapper.getColumnResultList(enterpriseId, Arrays.asList(metaQuickColumnId));
        for (TbMetaQuickColumnResultDO tbMetaQuickColumnResult : columnResultList) {
            JSONObject extendInfo = JSONObject.parseObject(tbMetaQuickColumnResult.getExtendInfo());
            resultList.add(TbMetaQuickColumnResultVO.builder().id(tbMetaQuickColumnResult.getId()).metaQuickColumnId(tbMetaQuickColumnResult.getMetaQuickColumnId()).resultName(tbMetaQuickColumnResult.getResultName())
                    .maxScore(tbMetaQuickColumnResult.getMaxScore()).minScore(tbMetaQuickColumnResult.getMinScore()).score(tbMetaQuickColumnResult.getScore()).money(tbMetaQuickColumnResult.getDefaultMoney())
                    .mappingResult(tbMetaQuickColumnResult.getMappingResult()).mustPic(tbMetaQuickColumnResult.getMustPic()).orderNum(tbMetaQuickColumnResult.getOrderNum())
                    .description(tbMetaQuickColumnResult.getDescription()).scoreIsDouble(tbMetaQuickColumnResult.getScoreIsDouble()).awardIsDouble(tbMetaQuickColumnResult.getAwardIsDouble())
                    .aiMaxScore(Objects.nonNull(extendInfo) ? extendInfo.getBigDecimal("aiMaxScore") : null)
                    .aiMinScore(Objects.nonNull(extendInfo) ? extendInfo.getBigDecimal("aiMinScore") : null).build());
        }
        return resultList;
    }

    /**
     * 获取项的结果项
     * @param enterpriseId
     * @param metaQuickColumnId
     * @return
     */
    public List<TbMetaQuickColumnResultDO> getColumnResultListById(String enterpriseId, Long metaQuickColumnId){
        return tbMetaQuickColumnResultMapper.getColumnResultList(enterpriseId, Collections.singletonList(metaQuickColumnId));
    }

    public Map<Long, List<TbMetaQuickColumnResultVO>> getColumnResultListMap(String enterpriseId, List<Long> metaQuickColumnIds){
        if(CollectionUtils.isEmpty(metaQuickColumnIds)){
            return Maps.newHashMap();
        }
        Map<Long, List<TbMetaQuickColumnResultVO>> resultMap = new HashMap<>();
        List<TbMetaQuickColumnResultDO> columnResultList = tbMetaQuickColumnResultMapper.getColumnResultList(enterpriseId, metaQuickColumnIds);
        Map<Long, List<TbMetaQuickColumnResultDO>> columnResultMap = columnResultList.stream().collect(Collectors.groupingBy(k -> k.getMetaQuickColumnId()));
        for (Long  metaQuickColumnId: columnResultMap.keySet()) {
            List<TbMetaQuickColumnResultDO> tbMetaQuickColumnResultDOS = columnResultMap.get(metaQuickColumnId);
            if(CollectionUtils.isEmpty(tbMetaQuickColumnResultDOS)){
                continue;
            }
            List<TbMetaQuickColumnResultVO> resultList = new ArrayList<>();
            for (TbMetaQuickColumnResultDO tbMetaQuickColumnResult : tbMetaQuickColumnResultDOS) {
                JSONObject extendInfo = JSONObject.parseObject(tbMetaQuickColumnResult.getExtendInfo());
                resultList.add(TbMetaQuickColumnResultVO.builder().id(tbMetaQuickColumnResult.getId()).metaQuickColumnId(tbMetaQuickColumnResult.getMetaQuickColumnId()).resultName(tbMetaQuickColumnResult.getResultName())
                        .maxScore(tbMetaQuickColumnResult.getMaxScore()).minScore(tbMetaQuickColumnResult.getMinScore()).score(tbMetaQuickColumnResult.getScore()).money(tbMetaQuickColumnResult.getDefaultMoney())
                        .mappingResult(tbMetaQuickColumnResult.getMappingResult()).mustPic(tbMetaQuickColumnResult.getMustPic()).orderNum(tbMetaQuickColumnResult.getOrderNum())
                        .description(tbMetaQuickColumnResult.getDescription()).scoreIsDouble(tbMetaQuickColumnResult.getScoreIsDouble()).awardIsDouble(tbMetaQuickColumnResult.getAwardIsDouble())
                        .aiMaxScore(Objects.nonNull(extendInfo) ? extendInfo.getBigDecimal("aiMaxScore") : null)
                        .aiMinScore(Objects.nonNull(extendInfo) ? extendInfo.getBigDecimal("aiMinScore") : null).build());
            }
            resultMap.put(metaQuickColumnId, resultList);
        }
        return resultMap;
    }

    public Integer batchInsert(String enterpriseId,List<TbMetaQuickColumnResultDO> list){
        return tbMetaQuickColumnResultMapper.batchInsert(list,enterpriseId);
    }

    public Integer deleteByMetaQuickColumnId(String enterpriseId, Long metaQuickColumnId){
        return tbMetaQuickColumnResultMapper.deleteByMetaQuickColumnId(enterpriseId,metaQuickColumnId);
    }

    public Integer deleteByMetaQuickColumnIds(String enterpriseId, List<Long> metaQuickColumnIds){
        return tbMetaQuickColumnResultMapper.deleteByMetaQuickColumnIds(enterpriseId,metaQuickColumnIds);
    }
}
