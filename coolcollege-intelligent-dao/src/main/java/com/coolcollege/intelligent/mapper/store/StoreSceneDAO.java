package com.coolcollege.intelligent.mapper.store;

import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: StoreSceneDAO
 * @Description:
 * @date 2022-04-27 15:54
 */
@Service
@Slf4j
public class StoreSceneDAO {

    @Resource
    private StoreSceneMapper storeSceneMapper;

    /**
     * 获取场景map
     * @param enterpriseId
     * @param idList
     * @return
     */
    public Map<Long, String> getStoreSceneNameMap(String enterpriseId, List<Long> idList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(idList)){
            return Maps.newHashMap();
        }
        List<StoreSceneDo> storeSceneList = storeSceneMapper.getStoreSceneListForName(enterpriseId, idList);
        if(CollectionUtils.isEmpty(storeSceneList)){
            return Maps.newHashMap();
        }
        return storeSceneList.stream().collect(Collectors.toMap(k->k.getId(), v->v.getName(), (k1, k2)-> k2));
    }

    /**
     * 获取场景名称
     * @param enterpriseId
     * @param id
     * @return
     */
    public String getStoreSceneName(String enterpriseId, Long id){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)){
            return null;
        }
        Map<Long, String> storeSceneNameMap = getStoreSceneNameMap(enterpriseId, Arrays.asList(id));
        if(MapUtils.isEmpty(storeSceneNameMap)){
            return null;
        }
        return storeSceneNameMap.get(id);
    }
}
