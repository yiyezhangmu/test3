package com.coolcollege.intelligent.dao.video;


import com.coolcollege.intelligent.model.video.platform.EnterpriseVideoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyp
 */
@Mapper
public interface EnterpriseVideoMapper {


    void batchInsertOrUpdateEnterpriseVideo(@Param("eid") String eid,
                                            @Param("enterpriseVideoDOList") List<EnterpriseVideoDO> enterpriseVideoDOList);

    void deleteEnterpriseVideo(@Param("eid") String eid,
                               @Param("deviceId") String deviceId,
                               @Param("channelNoList") List<String> channelNoList);
    List<String> selectEnterpriseIdByDeviceIdList(@Param("deviceIdList")List<String>deviceIdList);

}
