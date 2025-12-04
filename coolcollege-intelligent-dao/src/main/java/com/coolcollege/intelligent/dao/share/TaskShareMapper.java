package com.coolcollege.intelligent.dao.share;

import com.coolcollege.intelligent.model.share.TaskShareDO;
import com.coolcollege.intelligent.model.share.dto.TaskShareDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskShareMapper {
    /**
     * 批量增加分享
     * @param eId
     * @param taskShareDOList
     * @return
     */
    Integer batchInsertTaskShareDO(@Param("eId")String eId, @Param("entityList")List<TaskShareDO> taskShareDOList);

    /**
     * 批量删除分享
     * @param eId
     * @param shareIdList
     * @return
     */
    void deleteByShareIdList(@Param("eId")String eId,@Param("shareIdList")List<String> shareIdList);

    /**
     * 批量修改分享
     * @param eId
     * @param taskShareDOList
     * @return
     */
    Integer updateTaskShareList(@Param("eId")String eId,@Param("entityList")List<TaskShareDO> taskShareDOList);

    /**
     * 批量获取分享
     * @param eId
     * @param shareIdList
     * @param searchKey
     * @return
     */
    List<TaskShareDTO> selectByShareIdList(@Param("eId")String eId, @Param("shareIdList")List<String>shareIdList,@Param("key")String searchKey);


    String getDetail(@Param("eId") String eId, @Param("shareId") String shareId);

    List<String> getAllVisibleRangeShare(@Param("eId") String eId);
}
