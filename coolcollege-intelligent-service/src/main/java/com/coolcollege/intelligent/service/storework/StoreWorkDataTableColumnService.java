package com.coolcollege.intelligent.service.storework;

import com.coolcollege.intelligent.model.storework.request.CommentScoreRequest;
import com.coolcollege.intelligent.model.storework.request.SingleExecutionRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataTableColumnRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkTableRequest;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableColumnVO;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableDetailVO;
import com.coolcollege.intelligent.model.storework.vo.StoreWorkDataTableVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/19 11:26
 * @Version 1.0
 */
public interface StoreWorkDataTableColumnService {

    /**
     * 查询检查表对应检查项数据
     * @param enterpriseId
     * @param request
     * @return
     */
    List<StoreWorkDataTableVO>  getStoreWorkDataTableColumn(String enterpriseId, StoreWorkDataTableColumnRequest request, String userId);

    /**
     * 单项提交
     * @param enterpriseId
     * @param singleExecutionRequest
     * @return
     */
    Boolean singleColumnSubmit(String enterpriseId, CurrentUser user,SingleExecutionRequest singleExecutionRequest);

    /**
     * 查询指定检查表对应检查项数据
     * @param enterpriseId
     * @param dataTableId
     * @return
     */
    StoreWorkDataTableDetailVO getTableColumn(String enterpriseId, CurrentUser user, Long dataTableId);

    /**
     * 缓存点评人数据
     * @param enterpriseId
     * @param user
     * @param requestList
     * @return
     */
    Boolean setTableColumnCache(String enterpriseId,CurrentUser user,Long dataTableId,List<CommentScoreRequest> requestList);

    /**
     * 更新工单数据
     * @param enterpriseId
     * @param dataColumnId
     */
    void updateQuestionData(String enterpriseId,Long dataColumnId);

}
