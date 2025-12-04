package com.coolcollege.intelligent.dao.activity;

import com.coolcollege.intelligent.model.activity.BroadcastCommentWarningLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BroadcastCommentWarningLogMapper {

    void insertSelective(@Param("logDO") BroadcastCommentWarningLogDO logDO,
                         @Param("enterpriseId") String eid);
}
