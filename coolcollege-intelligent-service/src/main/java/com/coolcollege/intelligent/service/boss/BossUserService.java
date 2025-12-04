package com.coolcollege.intelligent.service.boss;

import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.boss.BossUserStatusUpdateDTO;
import com.coolcollege.intelligent.model.system.BossUserDO;

/**
 * 用户管理
 * @author byd
 * @date 2021-01-28 17:07
 */
public interface BossUserService {

    /**
     * 查询用户列表
     * @return
     */
    PageVO getList(int pageSize, int pageNum, String username, Integer status);

    /**
     * 查询用户列表
     * @return
     */
    BossUserDO detail(Long id);

    /**
     * 插入
     * @param userDO
     * @return
     */
    int insertUser(BossUserDO userDO, String appType);


    /**
     * 更新
     * @param userDO
     * @return
     */
    int updateUser(BossUserDO userDO, String appType);

    /**
     * 删除
     * @param userId
     * @return
     */
    int deleteById(Long userId);

    /**
     * 根据用户名查询
     * @param username
     * @return
     */
    BossUserDO getUserByUsername(String username);

    Boolean callAdmin(String eid);

    /**
     * 更新用户状态
     * @param param
     * @return
     */
    Boolean updateUserStatus(BossUserStatusUpdateDTO param);

    /**
     * 用户名check
     * @param username
     * @return
     */
    boolean usernameCheck(String username);

    /**
     * 通过企业id和用户id获取token,模拟登录,用做排查问题
     * @return
     */
    ResponseResult bossGetTokenByEidAndUserID(String eid, String userId);
}
