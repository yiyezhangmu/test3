package com.coolcollege.intelligent.service.achievement.qyy;

import com.coolcollege.intelligent.model.achievement.qyy.dto.UpdateConversationAuthDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConversationAuthVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.UserConversationAuthVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: ConversationAuthService
 * @Description: 群权限
 * @date 2023-04-14 16:30
 */
public interface ConversationAuthService {

    /**
     * 更新群场景权限
     * @param enterpriseId
     * @param userId
     * @param param
     * @return
     */
    Boolean updateConversationAuth(String enterpriseId, String userId, UpdateConversationAuthDTO param);

    /**
     * 获取某个群场景权限
     * @param enterpriseId
     * @param sceneCode
     * @return
     */
    ConversationAuthVO getConversationAuth(String enterpriseId, String sceneCode);

    /**
     * 获取用户的权限
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<UserConversationAuthVO> getUserConversationAuth(String enterpriseId, String userId);
}
