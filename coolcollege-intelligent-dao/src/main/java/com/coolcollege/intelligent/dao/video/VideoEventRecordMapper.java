package com.coolcollege.intelligent.dao.video;


import com.coolcollege.intelligent.model.video.platform.yushi.DO.VideoEventRecordDO;
import com.coolcollege.intelligent.model.video.platform.yushi.vo.VideoEventRecordStoreVO;
import com.coolcollege.intelligent.model.video.platform.yushi.vo.VideoEventRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zyp
 */
@Mapper
public interface VideoEventRecordMapper {

    List<VideoEventRecordVO> groupVideoEventRecordByStoreId(@Param("eid") String eid,
                                                            @Param("storeId") String storeId);

    List<VideoEventRecordStoreVO> listVideoEventRecordByStoreId(@Param("eid") String eid,
                                                                @Param("alarmType") String alarmType,
                                                                @Param("storeIdList") List<String> storeIdList,
                                                                @Param("startTime") Long startTime,
                                                                @Param("endTime") Long endTime,
                                                                @Param("regionPathList") List<String> regionPathList);
    void insertVideoEventRecord(@Param("eid") String eid,
                                @Param("videoEventDO") VideoEventRecordDO videoEventRecordDO);
}
