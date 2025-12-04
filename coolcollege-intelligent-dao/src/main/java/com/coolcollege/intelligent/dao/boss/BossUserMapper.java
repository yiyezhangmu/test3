package com.coolcollege.intelligent.dao.boss;

import com.coolcollege.intelligent.model.system.BossUserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * boos管理用户
 *
 * @author byd
 * @date 2021-01-28 16:41
 */
@Mapper
public interface BossUserMapper {

    /**
     * 用户列表
     *
     * @return
     */
    List<BossUserDO> getList(@Param("username") String username, @Param("status") Integer status);

    /**
     * 详情
     *
     * @param id
     * @return
     */
    BossUserDO selectById(Long id);

    /**
     * 根据用户名查询
     *
     * @param username
     * @return
     */
    BossUserDO getUserByUsername(String username);

    /**
     * 插入用户
     *
     * @param bossUserDO
     * @return
     */
    int insertUser(BossUserDO bossUserDO);

    /**
     * 更新
     *
     * @param bossUserDO
     * @return
     */
    int updateByIdSelective(@Param("bossUserDO") BossUserDO bossUserDO);

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    int deleteById(Long userId);

}
