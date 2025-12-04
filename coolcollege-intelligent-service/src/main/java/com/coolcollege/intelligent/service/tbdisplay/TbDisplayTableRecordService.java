package com.coolcollege.intelligent.service.tbdisplay;

import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.DisplayDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DisplayRecordDetailVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.DisplayRecordVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableRecordDO;
import com.coolcollege.intelligent.model.tbdisplay.param.*;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordDeleteVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordPageVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTableRecordVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTaskDataVO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskMessageDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wxp
 */
public interface TbDisplayTableRecordService {

    boolean addTbDisplayTableRecord(TaskMessageDTO taskMessageDTO, List<TaskSubDO> newSubDOList);

    boolean buildTbDisplayTableRecord(String enterpriseId, TbDisplayTableRecordBuildParam param);

    boolean buildByTaskId(String enterpriseId, TbDisplayTableRecordBuildParam param);

    TbDisplayTableRecordVO getTbDisplayTableRecordVO(String enterpriseId, String userId, TaskSubDO taskSubDO);

    TbDisplayTableRecordVO getTableRecordByTaskIdAndStoreIdAndLoopCount(String enterpriseId, Long unifyTaskId, String storeId, Long loopCount);

    PageInfo tableRecordReportWithPage(String enterpriseId, TbDisplayReportQueryParam query, CurrentUser user);

    List<TbDisplayTaskDataVO> tableRecordReportExport(String enterpriseId, TbDisplayReportQueryParam query, CurrentUser user);

    Object displayHasPic(String enterpriseId, TbDisplayReportQueryParam query, CurrentUser user);
    /**
     * 单个任务审核
     *
     * @param tbApproveDisplayTaskParam
     */
    void approve(String enterpriseId, CurrentUser user, TbApproveDisplayTaskParam tbApproveDisplayTaskParam);

    /**
     * 评审修改
     * @param enterpriseId
     * @param user
     * @param tbApproveDisplayTaskParam
     */
    void score(String enterpriseId, CurrentUser user, TbApproveDisplayTaskParam tbApproveDisplayTaskParam);


    void turnTbDisplayTask(String enterpriseId, TaskSubDO oldTaskSubDo, TaskSubDO newTaskSubDo);

    // 完成巡店任务后删除或签的其他巡店任务
    boolean completeTbDisplayTask(TaskMessageDTO taskMessageDTO);

    TbDisplayTableRecordPageVO tableRecordList(String enterpriseId, TbDisplayReportQueryParam query);

    ImportTaskDO tableRecordListExport(String enterpriseId, TbDisplayReportQueryParam query);

    void reallocateTbDisplayTask(String enterpriseId, TaskStoreDO taskStoreDO, String operUserId);


    /**
     * 陈列记录列表
     * @param enterpriseId
     * @param displayDTO
     * @return
     */
    PageDTO<DisplayRecordVO> displayList(String enterpriseId, DisplayDTO displayDTO );

    /**
     * 陈列记录详情
     * @param enterpriseId
     * @param recordId
     * @return
     */
    DisplayRecordDetailVO displayRecordDetail(String enterpriseId,Long recordId);

    /**
     * 删除陈列门店任务
     * @param enterpriseId
     * @param tbDisplayDeleteParam
     * @return
     */
    void deleteRecord(String enterpriseId, TbDisplayDeleteParam tbDisplayDeleteParam, CurrentUser currentUser, String isDone, EnterpriseConfigDO config);


    /**
     * 批量删除
     * @param enterpriseId
     * @param displayDeleteParam
     * @param user
     */
    void batchDeleteRecord(String enterpriseId, TbDisplayBatchDeleteParam displayDeleteParam, CurrentUser user);

    /**
     * 删除陈列门店任务
     * @param enterpriseId
     * @param unifyTaskId
     * @return
     */
    PageInfo<TbDisplayTableRecordDeleteVO> getDeleteRecordList(String enterpriseId, Long unifyTaskId, Integer pageNum, Integer pageSize,String unifyTaskIds);
}
