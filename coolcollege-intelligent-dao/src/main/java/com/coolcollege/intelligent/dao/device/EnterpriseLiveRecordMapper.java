package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.EnterpriseLiveRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/12
 */
@Mapper
public interface EnterpriseLiveRecordMapper {

    void insertOrUpdateEnterpriseLiveRecord(@Param("enterpriseLiveRecordDO") EnterpriseLiveRecordDO enterpriseLiveRecordDO);



    List<EnterpriseLiveRecordDO> listExpireVideoList();


    void updateEnterpriseLiveRecordToStatus(@Param("enterpriseLiveRecordDO")EnterpriseLiveRecordDO enterpriseLiveRecordDO);

    void updateEnterpriseLiveRecordToLiveTime(@Param("eid")String eid,
                                              @Param("liveTime") Date liveTime,
                                              @Param("liveIdList")List<String> liveIdList);


}
