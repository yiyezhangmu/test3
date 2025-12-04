package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2020/1/16.
 * @author shoul
 */
@Mapper
public interface EnterpriseUserMappingMapper {

    /**
     * 根据企业id和用户id获得企业用户映射关系
     * @param eid
     * @param userId
     * @throws
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseUserMappingDO
     * @Author: xugangkun
     * @Date: 2021/3/29 10:34
     */
    EnterpriseUserMappingDO selectByEidAndUserId(@Param("eid") String eid, @Param("userId") String userId);

    /**
     * 保存
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    void save(@Param("entity") EnterpriseUserMappingDO entity);

    /**
     * 根据主键更新
     * @param entity 实体
     * @return: void
     * @Author: xugangkun
     */
    void updateByPrimaryKey(EnterpriseUserMappingDO entity);

    /**
     * 批量插入或删除人员映射
     * @param enterpriseUserMappingDOList
     */
    void batchInsertOrUpdate(@Param("list") List<EnterpriseUserMappingDO> enterpriseUserMappingDOList);

    /**
     * 批量删除人员映射
     * @param userIds
     * @param eid
     */
    void batchDeleteUserByUserIds(@Param("list") List<String> userIds, @Param("eid") String eid);

    /**
     * 根据userId 获取 enterpriseId
     * @param userIds
     * @return
     */
    List<EnterpriseUserMappingDO> getUserAllEnterpriseIdsByUserIds(@Param("userIds") List<String> userIds, @Param("enterpriseId") String enterpriseId);

    /**
     * 更新用户状态
     * @param unionids
     * @param enterpriseId
     * @param userStatus
     * @return
     */
    Integer updateEnterpriseUserStatus(@Param("unionids") List<String> unionids, @Param("enterpriseId") String enterpriseId, @Param("userStatus") Integer userStatus);

    /**
     * 根据unionid获取映射信息
     * @param enterpriseId
     * @param unionid
     * @return
     */
    EnterpriseUserMappingDO selectByEnterpriseIdAndUnionid(@Param("enterpriseId") String enterpriseId, @Param("unionid") String unionid);

    /**
     * 根据id删除映射关系
     * @param id
     * @return
     */
    Integer deleteUserMappingById(@Param("id")String id);

    /**
     * 获取企业映射关系
     * @param enterpriseId
     * @return
     */
    List<EnterpriseUserMappingDO> getUserMappingListByEnterpriseId(@Param("enterpriseId") String enterpriseId);

    /**
     * 根据unionid删除映射信息
     * @param enterpriseId
     * @param unoinid
     * @return
     */
    Integer deleteUserMappingByUnionid(@Param("enterpriseId") String enterpriseId, @Param("unoinid")String unoinid);

    /**
     * 批量删除
     * @param enterpriseId
     * @param unoinids
     * @return
     */
    Integer deleteUserMappingByUnionids(@Param("enterpriseId") String enterpriseId, @Param("unoinids")List<String> unoinids);

    /**
     * 更新usermapping unionid字段
     * @return
     */
    Integer updateUserMappingUnionid();

    /**
     * 获取用户所有的企业
     * @param userId
     * @return
     */
    List<EnterpriseUserMappingDO> getUserAllEnterpriseIdsByUserId(@Param("userId") String userId);
}