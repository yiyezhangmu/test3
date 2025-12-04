package com.coolcollege.intelligent.service.achievement.qyy.impl;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserRoleDao;
import com.coolcollege.intelligent.dao.system.dao.SysRoleDao;
import com.coolcollege.intelligent.mapper.achieve.qyy.QyyConversationSceneDAO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.UpdateConversationAuthDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConversationAuthVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.UserConversationAuthVO;
import com.coolcollege.intelligent.model.qyy.QyyConversationSceneAuthDO;
import com.coolcollege.intelligent.model.qyy.QyyConversationSceneDO;
import com.coolcollege.intelligent.service.achievement.qyy.ConversationAuthService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: ConversationAuthServiceImpl
 * @Description: 群场景权限
 * @date 2023-04-14 16:31
 */
@Service
@Slf4j
public class ConversationAuthServiceImpl implements ConversationAuthService {

    @Resource
    private QyyConversationSceneDAO qyyConversationSceneDAO;
    @Resource
    private SysRoleDao sysRoleDao;
    @Resource
    private EnterpriseUserRoleDao enterpriseUserRoleDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConversationAuth(String enterpriseId, String userId, UpdateConversationAuthDTO param) {
        if(Objects.isNull(param) || StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(param.getSceneCode())){
            return false;
        }
        String sceneCode = param.getSceneCode();
        List<QyyConversationSceneAuthDO> insertList = new ArrayList<>();
        for (UpdateConversationAuthDTO.SceneAuth sceneAuth : param.getAuthList()) {
            List<Long> roleIds = sceneAuth.getRoleIds();
            if(CollectionUtils.isNotEmpty(roleIds)){
                for (Long roleId : roleIds) {
                    QyyConversationSceneAuthDO update = new QyyConversationSceneAuthDO();
                    update.setSceneCode(sceneCode);
                    update.setAuthCode(sceneAuth.getAuthCode());
                    update.setRoleId(roleId);
                    insertList.add(update);
                }
            }
        }
        qyyConversationSceneDAO.deleteSceneAuth(enterpriseId, sceneCode);
        qyyConversationSceneDAO.batchInsertSelective(enterpriseId, insertList);
        return true;
    }

    @Override
    public ConversationAuthVO getConversationAuth(String enterpriseId, String sceneCode) {
        List<QyyConversationSceneDO> conversationScene = qyyConversationSceneDAO.getConversationScene(enterpriseId, sceneCode);
        if(CollectionUtils.isEmpty(conversationScene)){
            return null;
        }
        List<QyyConversationSceneAuthDO> conversationSceneAuth = qyyConversationSceneDAO.getConversationSceneAuth(enterpriseId, sceneCode);
        Map<String, List<Long>> authRoleIdsMap = new HashMap<>();
        Map<Long, String> roleNameMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(conversationSceneAuth)){
            List<Long> roleIds = conversationSceneAuth.stream().map(QyyConversationSceneAuthDO::getRoleId).distinct().collect(Collectors.toList());
            roleNameMap = sysRoleDao.getRoleNameMap(enterpriseId, roleIds);
            authRoleIdsMap = conversationSceneAuth.stream().collect(Collectors.groupingBy(k -> (k.getSceneCode() + Constants.MOSAICS + k.getAuthCode()), Collectors.mapping(QyyConversationSceneAuthDO::getRoleId, Collectors.toList())));
        }
        ConversationAuthVO result = new ConversationAuthVO();
        QyyConversationSceneDO qyyConversationScene = conversationScene.get(Constants.INDEX_ZERO);
        result.setSceneCode(qyyConversationScene.getSceneCode());
        result.setSceneName(qyyConversationScene.getSceneName());
        List<ConversationAuthVO.ConversationAuth> authList = new ArrayList<>();
        for (QyyConversationSceneDO scene : conversationScene) {
            List<Long> roleIds = authRoleIdsMap.get(scene.getSceneCode() + Constants.MOSAICS + scene.getAuthCode());
            List<ConversationAuthVO.AuthRole> roleList = null;
            if(CollectionUtils.isNotEmpty(roleIds)){
                roleList = new ArrayList<>();
                for (Long roleId : roleIds) {
                    roleList.add(new ConversationAuthVO.AuthRole(roleId, roleNameMap.get(roleId)));
                }
            }
            authList.add(new ConversationAuthVO.ConversationAuth(scene.getAuthCode(), scene.getAuthName(), scene.getPriority(), roleList,scene.getSceneCardDesc()));
        }
        result.setAuthList(authList);
        return result;
    }

    @Override
    public List<UserConversationAuthVO> getUserConversationAuth(String enterpriseId, String userId) {
        Boolean isAdmin = enterpriseUserRoleDao.checkIsAdmin(enterpriseId, userId);
        if(isAdmin){
            List<QyyConversationSceneDO> allConversationScene = qyyConversationSceneDAO.getAllConversationScene(enterpriseId);
            List<UserConversationAuthVO> resultList = new ArrayList<>();
            for (QyyConversationSceneDO qyyConversationScene : allConversationScene) {
                resultList.add(new UserConversationAuthVO(qyyConversationScene.getSceneCode(), qyyConversationScene.getAuthCode(), qyyConversationScene.getPriority()));
            }
            return resultList;
        }
        List<Long> userRoleIds = enterpriseUserRoleDao.getUserRoleIds(enterpriseId, userId);
        List<QyyConversationSceneAuthDO> authRoleList = qyyConversationSceneDAO.getAuthByRoleIds(enterpriseId, userRoleIds);
        if(CollectionUtils.isEmpty(authRoleList)){
            return Lists.newArrayList();
        }
        List<String> authCodes = authRoleList.stream().map(QyyConversationSceneAuthDO::getAuthCode).collect(Collectors.toList());
        Map<String, Integer> authPriorityMap = qyyConversationSceneDAO.getAuthPriority(enterpriseId, authCodes);
        List<UserConversationAuthVO> resultList = new ArrayList<>();
        for (QyyConversationSceneAuthDO conversation : authRoleList) {
            resultList.add(new UserConversationAuthVO(conversation.getSceneCode(), conversation.getAuthCode(), authPriorityMap.get(conversation.getAuthCode())));
        }
        return resultList;
    }
}
