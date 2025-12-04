package com.coolcollege.intelligent.dao.usergroup;

import com.coolcollege.intelligent.model.usergroup.EnterpriseUserGroupMappingDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 * @date 2022-12-28 07:23
 */
public interface EnterpriseUserGroupMappingMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-12-28 07:23
     */
    int insertSelective(@Param("record")EnterpriseUserGroupMappingDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-12-28 07:23
     */
    EnterpriseUserGroupMappingDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-12-28 07:23
     */
    int updateByPrimaryKeySelective(@Param("record")EnterpriseUserGroupMappingDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-12-28 07:23
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    void deleteUserGroupMappingByGroupId(@Param("enterpriseId")String enterpriseId, @Param("groupId") String groupId);

    Integer batchInsertMapping(@Param("enterpriseId") String enterpriseId, @Param("userIdList") List<String> userIdList, @Param("groupId") String groupId);

    Integer deleteMappingByGroupIdList(@Param("enterpriseId") String enterpriseId, @Param("groupId") String groupId, @Param("userIdList") List<String> userIdList);

    Integer deleteMappingByUserIdList(@Param("enterpriseId") String enterpriseId, @Param("userIdList") List<String> userIdList);

    List<EnterpriseUserGroupMappingDO> listByGroupIdList(@Param("enterpriseId") String enterpriseId, @Param("groupIdList") List<String> groupIdList);

    List<String> getUserIdsByGroupIdList(@Param("enterpriseId") String enterpriseId, @Param("groupIdList") List<String> groupIdList);

    List<EnterpriseUserGroupMappingDO> listByUserIdList(@Param("enterpriseId") String enterpriseId, @Param("userIdList") List<String> userIdList);

    void batchInsertOrUpdateUserGroupMapping(@Param("enterpriseId") String enterpriseId,@Param("userGroupMappingList") List<EnterpriseUserGroupMappingDO> userGroupMappingList);


    void deleteMappingByIdList(@Param("enterpriseId") String enterpriseId, @Param("idList") List<Long> idList);


}