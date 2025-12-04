package com.coolcollege.intelligent.service.workHandover;

import com.coolcollege.intelligent.model.workHandover.request.WorkHandoverRequest;
import com.coolcollege.intelligent.model.workHandover.vo.WorkHandoverVO;
import com.github.pagehelper.PageInfo;

/**
 * @author byd
 * @date 2022-11-17 11:33
 */
public interface WorkHandoverService {

    /**
     * 分页获取列表
     * @param enterpriseId
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<WorkHandoverVO> list(String enterpriseId, String name, Integer pageNum, Integer pageSize);

    /**
     * 新建工作交接
     * @param enterpriseId
     * @param handoverRequest
     * @param userId
     * @return
     */
    Long addWorkHandover(String enterpriseId, WorkHandoverRequest handoverRequest, String userId);

    /**
     * 新建工作交接
     * @param enterpriseId
     * @param workHandoverId
     * @return
     */
    void againWorkHandover(String enterpriseId, Long workHandoverId);

    /**
     * 开始工作交接
     * @param enterpriseId
     * @param workHandoverId
     */
    void beginWorkHandover(String enterpriseId, Long workHandoverId);
}
