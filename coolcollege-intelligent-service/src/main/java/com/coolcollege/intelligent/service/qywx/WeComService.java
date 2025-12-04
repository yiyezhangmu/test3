package com.coolcollege.intelligent.service.qywx;

import com.coolcollege.intelligent.model.qywx.dto.ImportUserDTO;

/**
 * 企业微信业务service
 * @Author: xugangkun
 * @Date: 2021/6/9 14:38
 */
public interface WeComService {

    /**
     * 导入企业微信用户
     * @param importUserDTO
     * @param enterpriseId
     * @author: xugangkun
     * @return void
     * @date: 2021/6/9 15:20
     */
    void importWoComUser(ImportUserDTO importUserDTO, String enterpriseId);

    /**
     * 初始话企业微信开通企业的第一个用户
     * @param userId
     * @param corpId
     * @param openUserid
     * @param appType
     * @author: xugangkun
     * @return void
     * @date: 2021/6/10 16:35
     */
    void initFirstUser(String userId, String corpId, String openUserid, String appType, String name);

    void initFirstUser(String enterpriseId, String userId, String openUserid, String appType, String name, String mobile, String initRoleId);

    /**
     * 发送一条开通成功的消息
     * @param eid
     * @author: xugangkun
     * @return void
     * @date: 2021/12/27 22:38
     */
    void sendOpenSucceededMsg(String eid);

}
