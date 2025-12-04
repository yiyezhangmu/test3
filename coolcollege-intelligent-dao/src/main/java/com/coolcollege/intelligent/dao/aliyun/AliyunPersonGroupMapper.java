package com.coolcollege.intelligent.dao.aliyun;

import com.coolcollege.intelligent.model.aliyun.AliyunPersonGroupDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyp
 */
@Mapper
public interface AliyunPersonGroupMapper {

    int insertAliyunPersonGroup(@Param("eid")String eid,
                                @Param("aliyunPersonGroupDO") AliyunPersonGroupDO aliyunPersonGroupDO);

    int updateAliyunPersonGroup(@Param("eid")String eid,
                                @Param("aliyunPersonGroupDO") AliyunPersonGroupDO aliyunPersonGroupDO);

    int deleteAliyunPersonGroup(@Param("eid")String eid,
                                @Param("groupId")String groupId);

    List<AliyunPersonGroupDO> listAliyunPersonGroup(@Param("eid")String eid);

    AliyunPersonGroupDO getAliyunPersonGroupById(@Param("eid")String eid,
                                             @Param("personGroupId")String personGroupId);

    List<AliyunPersonGroupDO> listAliyunPersonGroupById(@Param("eid")String eid,
                                                 @Param("personGroupIdList")List<String> personGroupIdList);

    AliyunPersonGroupDO getAliyunPersonGroupByName(@Param("eid")String eid,
                                             @Param("groupName")String groupName);





}
