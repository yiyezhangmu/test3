package com.coolcollege.intelligent.mapper.achieve.qyy;

import com.coolcollege.intelligent.dao.qyy.QyyConversationSceneAuthMapper;
import com.coolcollege.intelligent.dao.qyy.QyyConversationSceneMapper;
import com.coolcollege.intelligent.model.qyy.QyyConversationSceneAuthDO;
import com.coolcollege.intelligent.model.qyy.QyyConversationSceneDO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: QyyConversationSceneDAO
 * @Description:群场景code
 * @date 2023-04-14 16:22
 */
@Service
@Slf4j
public class QyyConversationSceneDAO {

    @Resource
    private QyyConversationSceneMapper qyyConversationSceneMapper;
    @Resource
    private QyyConversationSceneAuthMapper qyyConversationSceneAuthMapper;

    /**
     * 获取场景
     * @param enterpriseId
     * @param sceneCode
     * @return
     */
    public List<QyyConversationSceneDO> getConversationScene(String enterpriseId, String sceneCode){
        if(StringUtils.isAnyBlank(enterpriseId, sceneCode)){
            return Lists.newArrayList();
        }
        return qyyConversationSceneMapper.getConversationScene(enterpriseId, sceneCode);
    }

    /**
     * 获取群场景权限
     * @param enterpriseId
     * @param sceneCode
     * @return
     */
    public List<QyyConversationSceneAuthDO> getConversationSceneAuth(String enterpriseId, String sceneCode){
        if(StringUtils.isAnyBlank(enterpriseId, sceneCode)){
            return Lists.newArrayList();
        }
        return qyyConversationSceneAuthMapper.getConversationSceneAuth(enterpriseId, sceneCode);
    }

    /**
     * 删除群场景权限
     * @param enterpriseId
     * @param sceneCode
     * @return
     */
    public int deleteSceneAuth(String enterpriseId, String sceneCode){
        if(StringUtils.isAnyBlank(enterpriseId, sceneCode)){
            return 0;
        }
        return qyyConversationSceneAuthMapper.deleteSceneAuth(enterpriseId, sceneCode);
    }

    /**
     * 批量插入
     * @param enterpriseId
     * @param insertList
     * @return
     */
    public int batchInsertSelective(String enterpriseId, List<QyyConversationSceneAuthDO> insertList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(insertList)){
            return 0;
        }
        return qyyConversationSceneAuthMapper.batchInsertSelective(insertList, enterpriseId);
    }

    /**
     *
     * @param enterpriseId
     * @param roleIds
     * @return
     */
    public List<QyyConversationSceneAuthDO> getAuthByRoleIds(String enterpriseId, List<Long> roleIds){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(roleIds)){
            return Lists.newArrayList();
        }
        return qyyConversationSceneAuthMapper.getAuthByRoleIds(enterpriseId, roleIds);
    }

    /**
     * 获取权限code
     * @param enterpriseId
     * @param authCodes
     * @return
     */
    public Map<String, Integer> getAuthPriority(String enterpriseId, List<String> authCodes){
        if(CollectionUtils.isEmpty(authCodes)){
            return Maps.newHashMap();
        }
        List<QyyConversationSceneDO> sceneAuthCodes = qyyConversationSceneMapper.getConversationSceneByAuthCodes(enterpriseId, authCodes);
        return sceneAuthCodes.stream().collect(Collectors.toMap(k->k.getAuthCode(), v->v.getPriority(), (k1, k2)->k1));
    }


    public List<QyyConversationSceneDO> getAllConversationScene(String enterpriseId){
        if(StringUtils.isBlank(enterpriseId)){
            return Lists.newArrayList();
        }
        return qyyConversationSceneMapper.getAllConversationScene(enterpriseId);
    }

}
