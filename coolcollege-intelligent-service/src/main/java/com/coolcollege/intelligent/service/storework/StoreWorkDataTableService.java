package com.coolcollege.intelligent.service.storework;

import com.coolcollege.intelligent.model.patrolstore.dto.StaColumnDTO;
import com.coolcollege.intelligent.model.storework.dto.SwStoreWorkDataUserDTO;
import com.coolcollege.intelligent.model.storework.dto.SwStoreWorkReturnDTO;
import com.coolcollege.intelligent.model.storework.request.*;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/16 17:01
 * @Version 1.0
 */
public interface StoreWorkDataTableService {

    /**
     * 店务记录 店务执行页表数据查询
     * @param enterpriseId
     * @param user
     * @param storeWorkTableRequest
     * @return
     */
    List<StoreWorkExecutionPageVO> getStoreWorkExecutionPage(String enterpriseId, CurrentUser user, StoreWorkTableRequest storeWorkTableRequest);

    /**
     * 当前用户指定日期指定门店日清、周清、月清是否点评完成
     * @param enterpriseId
     * @param user
     * @param storeWorkTableRequest
     * @return
     */
    Boolean currentUserStoreWorkAllCommentComplete(String enterpriseId, CurrentUser user, StoreWorkTableRequest storeWorkTableRequest);

    /**
     * 获取店务概况(所有店务汇总数据)
     * @param enterpriseId
     * @param user
     * @param storeWorkTableRequest
     * @return
     */
    StoreWorkOverviewVO getStoreWorkOverViewData(String enterpriseId, CurrentUser user, StoreWorkTableRequest storeWorkTableRequest);

    /**
     * 获取店务概况(表 汇总数据)
     * @param enterpriseId
     * @param user
     * @param   businessId
     * @return
     */
    List<StoreWorkOverviewVO> getStoreWorkOverViewDataList(String enterpriseId, String workCycle, String storeId, Long currentDate, CurrentUser user, String businessId);

    /**
     * 获取单个表的数据概况
     * @param enterpriseId
     * @param dataTableId
     * @return
     */
    StoreWorkOverviewVO getStoreWorkTableData(String enterpriseId,Long dataTableId);


    /**
     * 日清 周清 月清 是否完成统计
     * @param enterpriseId
     * @param user
     * @param storeWorkClearRequest
     * @return
     */
    List<StoreWorkClearVO> getStoreWorkClear(String enterpriseId, CurrentUser user, StoreWorkClearRequest storeWorkClearRequest);

    /**
     * * 转交 user的检查表任务给transferUserId
     * @param enterpriseId
     * @param user
     * @param transferUserId
     * @param storeWorkDataTableIds
     * @return
     */
    Boolean transferHandler(String enterpriseId,CurrentUser user,String transferUserId,List<Long> storeWorkDataTableIds);



    Boolean transferHandlerAndComment(String enterpriseId, CurrentUser user,List<TransferHandlerCommentRequest> requestList);


    /**
     * 查询数据表执行人点评人数据
     * @param enterpriseId
     * @param tcBusinessId
     * @param storeId
     * @return
     */
    SwStoreWorkReturnDTO getDataUser(String enterpriseId, String tcBusinessId, String storeId);
    /**
     * 店务表数据统计集合
     * @param enterpriseId
     * @param request
     * @return
     */
    List<StoreWorkDataTableStatisticsVO> storeWorkTableStatisticsList(String enterpriseId, StoreWorkDataListRequest request, CurrentUser user);

    /**
     * 执行也检查项    检查表下拉框
     * @param enterpriseId
     * @param businessId
     * @return
     */
    List<StoreWorkTableListVO> getStoreWorkTableList(String enterpriseId,String businessId);


    /**
     * 点评人点评
     * @param enterpriseId
     * @param user 缓存
     * @param requestList
     * @return
     */
    Boolean commentScore(String enterpriseId,CurrentUser user, List<CommentScoreRequest> requestList);

    StoreWorkTableAndRecordStatusInfo checkStoreWorkStatusAuth(String enterpriseId, CurrentUser user, String businessId, Long dataTableId);

    PageInfo<StoreWorkPictureListVO> getPictureCenterDataTableList(String enterpriseId, PictureCenterRequest request,CurrentUser user);

    StoreWorkDataTableSimpleVO selectDataTableByStoreWorkId(String enterpriseId, Long queryDate, String userId, Long storeWorkId);

    /**
     * 自动发起工单
     * @param enterpriseId 企业id
     * @param failStaColumnList 失败项DTO列表
     * @param dataTableId 数据表id
     * @param userId 用户id
     */
    void autoQuestionOrder(String enterpriseId, List<StaColumnDTO> failStaColumnList, Long dataTableId, String userId);
}
