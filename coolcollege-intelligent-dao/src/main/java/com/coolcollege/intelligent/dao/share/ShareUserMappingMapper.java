package com.coolcollege.intelligent.dao.share;

import com.coolcollege.intelligent.model.share.dto.TaskShareInsertDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShareUserMappingMapper {
    /**
     * 批量插入映射
     * @param eId
     * @param shareId
     * @param userIdList
     * @return
     */
    Integer batchInsertMapping(@Param("eId")String eId,@Param("shareId") String shareId, @Param("userIdList") List<String> userIdList);

    /**
     * 通过分享id获取用户列表
     * @param eId
     * @param shareId
     * @return
     */
    List<String> getUserIdListByShareId(@Param("eId") String eId,@Param("shareId")String shareId);

    /**
     * 通过分享id列表批量删除映射关系
     * @param eId
     * @param shareId
     * @return
     */
    Integer deleteByShareId(@Param("eId")String eId,@Param("shareIdList") List<String> shareId);

    /**
     * 根据用户Id获取分享id
     * @param eId
     * @param userId
     * @return
     */
    List<String> getShareIdListByUserId(@Param("eId")String eId, @Param("userId") String userId);

    Integer batchInsertShareIdAndUserId(@Param("eId")String eId, @Param("entityList") List<TaskShareInsertDTO> entityList);
}
