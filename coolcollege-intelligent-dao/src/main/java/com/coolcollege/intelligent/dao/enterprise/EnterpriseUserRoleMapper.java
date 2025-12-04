package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserPositionType;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole;
import com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserRoleDTO;
import com.coolcollege.intelligent.model.enterprise.dto.UserDingPositionDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author xugangkun
 * @email 670809626@qq.com
 * @date 2021-03-23 20:37:44
 */
@Mapper
public interface EnterpriseUserRoleMapper {
    /**
     * 主键查询
     * @param enterpriseId
     * @param id
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    EnterpriseUserRole selectByPrimaryKey(@Param("eid") String enterpriseId, @Param("id") String id);

    /**
     * 记录数量统计
     * @param enterpriseId
     * @return: int
     * @Author: xugangkun
     */
    int count(@Param("eid") String enterpriseId);

    /**
     * 保存
     * @param enterpriseId
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    void save(@Param("eid") String enterpriseId, @Param("entity") EnterpriseUserRole entity);

    /**
     * 根据主键更新
     * @param enterpriseId
     * @param entity 实体
     * @return: void
     * @Author: xugangkun
     */
    void updateByPrimaryKey(@Param("eid") String enterpriseId, @Param("entity") EnterpriseUserRole entity);
    /**
     * 根据主键删除
     * @param enterpriseId
     * @param id
     * @return: void
     * @Author: xugangkun
     */
    void deleteByPrimaryKey(@Param("eid") String enterpriseId, @Param("id") Long id);
    /**
     * 根据主键批量删除
     * @param enterpriseId
     * @param ids id列表
     * @return: void
     * @Author: xugangkun
     */
    void deleteBatchByPrimaryKey(@Param("eid") String enterpriseId, @Param("ids") List<Long> ids);

    /**
     * 根据角色id删除
     * @param enterpriseId
     * @param roleId
     * @return: void
     * @Author: xugangkun
     */
    void deleteByRoleId(@Param("eid") String enterpriseId, @Param("roleId") String roleId);

    /**
     * 根据角色id和用戶id删除
     * @param enterpriseId
     * @param roleId
     * @return: void
     * @Author: xugangkun
     */
    void deleteByRoleIdAndUserId(@Param("eid") String enterpriseId, @Param("roleId") String roleId, @Param("userId") String userId);
    /**
     * 根据角色id批量删除
     * @param enterpriseId
     * @param roleId
     * @return: void
     * @Author: xugangkun
     */
    void deleteBatchByRoleId(@Param("eid") String enterpriseId, @Param("roleIds") List<String> roleId);


    void deleteBatchByUserIdAndRoleId(@Param("eid") String enterpriseId, @Param("userId") String userId, @Param("roleIds") List<Long> roleId);

    /**
     * 根据userId查询该用户与钉钉角色、职位的映射关系
     * @param enterpriseId
     * @param userId
     * @return: java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole>
     * @Author: xugangkun
     * @Date: 2021/3/23 20:48
     */
    List<EnterpriseUserRole> selectDingRoleMappingByUserId(@Param("eid") String enterpriseId, @Param("userId") String userId);

    List<EnterpriseUserRole> listsUserRoleByUserIdListAndSource(@Param("eid") String enterpriseId, @Param("userIds") List<String> userIds, @Param("source") String source);


    List<EnterpriseUserRole> selectMdtRoleMappingByUserId(@Param("eid") String enterpriseId, @Param("userId") String userId);


    /**
     * 根据userId查询该用户的角色以及角色的来源
     * @param enterpriseId
     * @param userId
     * @return: java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole>
     * @Author: xugangkun
     * @Date: 2021/3/23 20:48
     */
    List<EntUserRoleDTO> selectUserRoleByUserId(@Param("eid") String enterpriseId, @Param("userId") String userId);

    List<EntUserRoleDTO> selectUserRoleByUserIds(@Param("eid") String enterpriseId, @Param("userIds") List<String> userIds);

    /**
     * 根据userId查询该用户的钉钉角色以及角色的来源
     * @author chenyupeng
     * @date 2021/9/10
     * @param enterpriseId
     * @param userId
     * @return java.util.List<com.coolcollege.intelligent.model.enterprise.dto.EntUserRoleDTO>
     */
    List<EntUserRoleDTO> selectUserRoleNameByUserId(@Param("eid") String enterpriseId, @Param("userId") String userId);

    /**
     * 根据角色id查询
     * @param enterpriseId
     * @param roleId
     * @return: java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole>
     * @Author: xugangkun
     * @Date: 2021/3/23 20:48
     */
    List<EnterpriseUserRole> selectByRoleId(@Param("eid") String enterpriseId, @Param("roleId") String roleId);


    /**
     * 根据userId查询
     * @param enterpriseId
     * @param userId
     * @return: java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole>
     * @Author: xugangkun
     * @Date: 2021/3/23 20:48
     */
    List<Long> selectIdsByUserId(@Param("eid") String enterpriseId, @Param("userId") String userId);


    Integer selectCountsByUserId(@Param("eid") String enterpriseId, @Param("userId") String userId);

    List<Long> selectIdsByUserIds(@Param("eid") String enterpriseId, @Param("userIds") List<String> userIds);

    /**
     * 查询当前用户在钉钉上的职位
     * @param enterpriseId
     * @param userId
     * @throws
     * @return: java.util.List<java.lang.String>
     * @Author: xugangkun
     * @Date: 2021/3/26 14:19
     */
    List<UserDingPositionDTO> selectUserDingPosition(@Param("eid") String enterpriseId, @Param("userId") String userId);

    /**
     * 统计管理员数量
     * @param enterpriseId
     * @param roleId
     * @throws
     * @return: java.lang.Integer
     * @Author: xugangkun
     * @Date: 2021/3/31 10:46
     */
    Integer countMainAdmin(@Param("eid") String enterpriseId, @Param("roleId") String roleId);

    /**
     * 根据用户id列表查询
     * @param enterpriseId
     * @param userIds
     * @return: java.util.List<com.coolcollege.intelligent.model.enterprise.EnterpriseUserRole>
     * @Author: xugangkun
     * @Date: 2021/3/23 20:48
     */
    List<EnterpriseUserRole> selectByUserIdList(@Param("eid") String enterpriseId, @Param("userIds") List<String> userIds);

    EnterpriseUserRole selectByUserIdAndRoleId(@Param("eid") String enterpriseId, @Param("userId") String userId, @Param("roleId") String roleId);

    List<EnterpriseUserRoleDTO> selectByUserIdsList(@Param("eid") String enterpriseId, @Param("userIds") List<String> userIds);
    /**
     * 获取某个角色的用户
     * @param enterpriseId
     * @param userIds
     * @param roleId
     * @return
     */
    List<String> selectUserIdByRoleId(@Param("eid") String enterpriseId, @Param("userIds") List<String> userIds, @Param("roleId") String roleId);

    /**
     * 获取用户有哪些角色
     * @param enterpriseId
     * @param userId
     * @return
     */
    List<Long> selectRoleIdsByUserId(@Param("eid") String enterpriseId, @Param("userId") String userId);

    /**
     * 获取角色下的用户
     * @param enterpriseId
     * @param roleId
     * @return
     */
    List<String> selectUserIdsByRoleId(@Param("eid") String enterpriseId, @Param("roleId") String roleId);

    /**
     * 获取角色列表下的用户
     * @param enterpriseId
     * @param roleIds
     * @return
     */
    List<String> selectUserIdsByRoleIdList(@Param("eid") String enterpriseId, @Param("roleIds") List<Long> roleIds);

    List<EnterpriseUserRole> getAllData(@Param("eid") String enterpriseId);

    List<String> getUserIdsByRoleIds(@Param("eid") String enterpriseId, @Param("roleIds")List<String> roleIds, @Param("userIds")List<String> userIds);

    List<Long> getUserRoleIds(@Param("eid") String enterpriseId, @Param("userId") String userId);

    List<String> getUserIdsByRoleIdList(@Param("eid") String enterpriseId, @Param("roleIdList")List<Long> roleIdList);

    /**
     * 获取有角色的用户
     * @param enterpriseId
     * @param userIds
     * @return
     */
    List<String> getHaveRoleUserIds(@Param("eid") String enterpriseId, @Param("userIds")List<String> userIds);

    List<EnterpriseUserPositionType> getUserIdAndPositionType(@Param("eid")String eid, @Param("userIds")List<String> userIds);

    List<EnterpriseUserRoleDTO> getByRoleIds(@Param("eid") String enterpriseId, @Param("roleIds") List<Long> roleIds);

    Integer deleteUserRoleByUserId(@Param("eid") String enterpriseId, @Param("userId") String userId, @Param("syncType") Integer syncType);
}
