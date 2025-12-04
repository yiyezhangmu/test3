package com.coolcollege.intelligent.dao.user;

import com.coolcollege.intelligent.model.user.UserJurisdictionStoreDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/19 15:36
 */
@Mapper
public interface UserJurisdictionStoreMapper {
    Integer batchInsertUserJurisdictionStore(@Param("eid") String enterpriseId, @Param("items") List<UserJurisdictionStoreDO> userJurisdictionStoreDOS);

    //根据用户id删除记录
    Integer deleteUserJurisdictionStoreByUserId(@Param("enterpriseId") String enterpriseId, @Param("userId") String userId);
}
