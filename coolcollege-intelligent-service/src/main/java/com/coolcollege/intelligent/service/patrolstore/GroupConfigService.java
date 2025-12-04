package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.page.PageBaseRequest;
import com.coolcollege.intelligent.model.patrolstore.dto.SendWXGroupMessageDTO;
import com.coolcollege.intelligent.model.patrolstore.request.AddGroupConfigRequest;
import com.coolcollege.intelligent.model.patrolstore.request.DeleteGroupConfigRequest;
import com.coolcollege.intelligent.model.patrolstore.request.UpdateGroupConfigRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.GroupConfigDetailVO;
import com.github.pagehelper.PageInfo;

/**
 * @Author: huhu
 * @Date: 2024/9/6 11:37
 * @Description:
 */
public interface GroupConfigService {

    /**
     * 新增群组推送配置
     * @param enterpriseId
     * @param userId
     * @param param
     * @return
     */
    Long addGroupConfig(String enterpriseId, String userId, AddGroupConfigRequest param);

    /**
     * 修改群组配置
     * @param enterpriseId
     * @param userId
     * @param param
     * @return
     */
    Boolean updateGroupConfig(String enterpriseId, String userId, UpdateGroupConfigRequest param);

    /**
     * 删除群组配置
     * @param enterpriseId
     * @param userId
     * @param param
     * @return
     */
    Boolean deleteGroupConfig(String enterpriseId, String userId, DeleteGroupConfigRequest param);

    /**
     * 获取群组详情
     * @param enterpriseId
     * @param groupId
     * @return
     */
    GroupConfigDetailVO getGroupConfigDetail(String enterpriseId, Long groupId);

    /**
     * 获取群组列表
     * @param enterpriseId
     * @param param
     * @return
     */
    PageInfo<GroupConfigDetailVO> getGroupConfigPage(String enterpriseId, PageBaseRequest param);

    /**
     * 发送微信群消息
     * @param param
     */
    void sendWXGroupMessage(SendWXGroupMessageDTO param);
}
