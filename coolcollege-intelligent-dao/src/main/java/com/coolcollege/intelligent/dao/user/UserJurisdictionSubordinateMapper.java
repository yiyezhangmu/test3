package com.coolcollege.intelligent.dao.user;

import com.coolcollege.intelligent.model.user.UserJurisdictionSubordinateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/19 15:36
 */
@Mapper
public interface UserJurisdictionSubordinateMapper {
    Integer batchInsertUserJurisdictionSubordinate(@Param("eid") String enterpriseId, @Param("items") List<UserJurisdictionSubordinateDO> UserJurisdictionSubordinateDOS);

    //根据用户id删除记录
    Integer deleteUserJurisdictionSubordinateByUserId(@Param("enterpriseId") String enterpriseId, @Param("userIds") List<String> userIds);
}
