package com.coolcollege.intelligent.dao.aliyun;

import com.coolcollege.intelligent.model.aliyun.PersonNotifyRecordDO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunPersonTraceVO;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsPersonHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/12
 */
@Mapper
public interface PersonNotifyRecordMapper {

    int insertPersonNotifyRecord(@Param("eid") String eid,
                                 @Param("personNotifyRecordDO") PersonNotifyRecordDO personNotifyRecordDO);

    List<AliyunVdsPersonHistoryVO> listPersonHistory(@Param("eid") String eid,
                                                     @Param("storeId") String storeId,
                                                     @Param("startTime") Long startTime,
                                                     @Param("endTime") Long endTime);

    /**
     * 访客次数不去重
     *
     * @param eid
     * @param storeId
     * @param startTime
     * @param endTime
     * @return
     */
    Integer countPersonHistory(@Param("eid") String eid,
                               @Param("storeId") String storeId,
                               @Param("startTime") Long startTime,
                               @Param("endTime") Long endTime);

    List<AliyunPersonTraceVO> listPersonTrace(@Param("eid") String eid,
                                              @Param("customerId") String storeId);
}
