package com.coolcollege.intelligent.service.share;

import com.coolcollege.intelligent.model.share.dto.TaskShareDTO;
import com.coolcollege.intelligent.model.share.dto.TaskShareInsertDTO;

import java.util.List;
import java.util.Map;

public interface ShareService {
    /**
     * 添加分享
     * @param eId
     * @param taskShareList 任务Id列表
     * @return
     */
    Boolean addShare(String eId,List<TaskShareInsertDTO> taskShareList);

    /**
     * 获取分享
     * @param eId
     * @return
     */
    Map<String, Object> getShareList(String eId, String shareId, Integer pageSize, Integer pageNum, String searchKey);

    /**
     * 删除分享
     * @param eId
     * @param shareId
     * @return
     */
    Boolean delete(String eId,String shareId);

    /**
     * 更新分享
     * @param eId
     * @param shareId
     * @return
     */
    Boolean update(String eId,String shareId);

    /**
     * 批量分享
     * @param eId
     * @param taskShareInsertDTO
     * @return
     */
    Object batchShare(String eId, TaskShareInsertDTO taskShareInsertDTO);

    /**
     * 获取分享详情
     * @param eId
     * @param shareId
     * @return
     */
    TaskShareDTO getShareDetail(String eId, String shareId);


    Boolean singleShare(String eId, TaskShareInsertDTO taskShareInsertDTO);

    void patrolStoreShare(String eId, TaskShareInsertDTO taskShareInsertDTO);

    /**
     * 圈子分享
     * @param eId
     * @param taskShareInsert
     */
    void circlesShare(String eId, TaskShareInsertDTO taskShareInsert);
}
