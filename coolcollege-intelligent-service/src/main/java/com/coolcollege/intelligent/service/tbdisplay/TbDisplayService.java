package com.coolcollege.intelligent.service.tbdisplay;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbBatchApproveDisplayTaskParam;
import com.coolcollege.intelligent.model.tbdisplay.param.TbDisplayHandleParam;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayColumnReportVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;
import java.util.Map;

/**
 *
 * @author : WXP
 * @version : 1.0
 * @Description : 陈列任务操作
 * @date ：2020/11/18
 */
public interface TbDisplayService {


    TbDisplayTableRecordDO tbDisplayColumnHandle(String enterpriseId, TbDisplayHandleParam handleParam, CurrentUser user, boolean isAiCheck);


    /**
     * 处理任务
     *  @param enterpriseId
     * @param handleParam
     * @param user
     */
    void tableRecordHandleSubmit(String enterpriseId, TbDisplayHandleParam handleParam, CurrentUser user);
    /**
     * 根据子任务idList批量获取各子任务的陈列详情信息
     * 
     * @param enterpriseId
     * @param subTaskIdList
     * @return
     */
    Map<Long, TbDisplayTableRecordVO> detailGroupBySubTaskId(String enterpriseId, String userId, List<Long> subTaskIdList);

    /**
     * 根据子任务idList批量获取各子任务的陈列详情信息
     *
     * @param enterpriseId
     * @param taskStoreIdList
     * @return
     */
    Map<Long, TbDisplayTableRecordVO> detailGroupByTaskStoreId(String enterpriseId, String userId, List<Long> taskStoreIdList);
    /**
     * 根据子任务id获取陈列详情信息
     *
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    TbDisplayTableRecordVO detail(String enterpriseId,  String userId, Long subTaskId);

    TbDisplayTableRecordVO detailByTaskStoreId(String enterpriseId, String userId, Long taskStoreId);

    TbDisplayTableRecordVO detailByTaskIdAndStoreIdAndLoopCount(String enterpriseId, Long unifyTaskId, String storeId,Long loopCount);

    List<TbDisplayColumnReportVO> storeColumnExport(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount);

    ImportTaskDO exportDetailList(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount, String dbName);


    /**
     * 批量审核
     * @param enterpriseId
     * @param user
     * @param tbBatchApproveDisplayTaskParam
     */
    void batchApprove(String enterpriseId, CurrentUser user, TbBatchApproveDisplayTaskParam tbBatchApproveDisplayTaskParam);

    /**
     * 批量审核
     * @param enterpriseId
     * @param user
     * @param tbBatchApproveDisplayTaskParam
     */
    void batchScore(String enterpriseId, CurrentUser user, TbBatchApproveDisplayTaskParam tbBatchApproveDisplayTaskParam);

    void checkVideoHandel(List<TbDisplayTableDataColumnDO> tbDisplayTableDataColumnDOList, String enterpriseId, Integer uploadType);

}
