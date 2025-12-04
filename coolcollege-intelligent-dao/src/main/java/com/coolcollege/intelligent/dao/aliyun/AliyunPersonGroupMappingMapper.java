package com.coolcollege.intelligent.dao.aliyun;

import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupDO;
import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupMappingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyp
 */
@Mapper
public interface AliyunPersonGroupMappingMapper {

    int insertAliyunPersonMappingGroup(@Param("eid") String eid,
                                @Param("aliyunPersonGroupMappingDO") AliyunPersonGroupMappingDO aliyunPersonGroupMappingDO);
    int updateAliyunPersonMappingGroup(@Param("eid") String eid,
                                       @Param("aliyunPersonGroupMappingDO") AliyunPersonGroupMappingDO aliyunPersonGroupMappingDO);

    AliyunPersonGroupMappingDO getAliyunPersonMappingByCustomer(@Param("eid") String eid,
                                                                @Param("customerId") String customerId);

    List<AliyunPersonGroupMappingDO> listAliyunPersonMappingByGroup(@Param("eid") String eid,
                                                                @Param("groupId") String groupId);

    int deleteAliyunPerosnMappingGroupByCustomer(@Param("eid") String eid,
                                                 @Param("customerId") String customerId);
    int deleteAliyunPerosnMappingGroupByGroup(@Param("eid") String eid,
                                       @Param("groupId") String groupId);


}
