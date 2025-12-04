package com.coolcollege.intelligent.dao.video;

import com.coolcollege.intelligent.model.video.VideoPollingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/20
 */
@Mapper
public interface VideoPollingMapper {

    int insertVideoPolling(@Param("enterpriseId") String enterpriseId,
                           @Param("videoPollingDO") VideoPollingDO videoPollingDO);

    int updateVideoPolling(@Param("enterpriseId") String enterpriseId,
                           @Param("videoPollingDO") VideoPollingDO videoPollingDO);

    int deleteVideoPolling(@Param("enterpriseId") String enterpriseId, Long id);

    List<VideoPollingDO> listVideoPolling(@Param("enterpriseId") String enterpriseId);


    VideoPollingDO getVideoPollingById(@Param("enterpriseId") String enterpriseId,
                                       @Param("id") Long id);


}
