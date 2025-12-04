package com.coolcollege.intelligent.service.storework;

import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.question.vo.SubQuestionRecordListVO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkStatisticsDTO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDataListRequest;
import com.coolcollege.intelligent.model.storework.request.StoreWorkRecordListRequest;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Author wxp
 * @Date 2022/9/16 9:39
 * @Version 1.0
 */
public interface StoreWorkRecordService {

    /**
     * 统计一个店务门店完成情况
     * @param enterpriseId
     * @param storeWorkId
     * @return
     */
    StoreWorkStatisticsDTO countByStoreWorkId(String enterpriseId, Long storeWorkId);

    /**
     * 数据--门店统计 -- 数据概况
     * @param enterpriseId
     * @param storeWorkDataListRequest
     * @return
     */
    StoreWorkStatisticsOverviewVO storeWorkStoreStatisticsOverview(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user);

    /**
     * 数据--门店统计
     * @param enterpriseId
     * @param storeWorkDataListRequest
     * @return
     */
    PageInfo<StoreWorkDataDetailVO> storeWorkStoreStatisticsList(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user);


    PageInfo<StoreWorkDataDTO>  StoreWorkDataList(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user);


    ImportTaskDO storeWorkStoreStatisticsListExport(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user);


    /**
     * 数据--区域统计
     * @param enterpriseId
     * @param request
     * @return
     */
    List<StoreWorkStatisticsOverviewVO> storeWorkRegionStatisticsList(String enterpriseId, StoreWorkDataListRequest request, CurrentUser user);

    ImportTaskDO storeWorkRegionStatisticsListExport(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user);


    PageInfo<StoreWorkDayStatisticsVO> storeWorkDayStatisticsList(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user);

    /**
     * 日报表统计导出
     * @param enterpriseId
     * @param storeWorkDataListRequest
     * @param user
     * @return
     */
    ImportTaskDO storeWorkDayStatisticsListExport(String enterpriseId, StoreWorkDataListRequest storeWorkDataListRequest, CurrentUser user);

    /**
     * 检查项提交时，异步计算表、店务记录上的状态和相关率数据
     * @param enterpriseId
     * @param dataColumnId
     * @return
     */
    Boolean syncStatusWhenColumnSubmit(String enterpriseId, Long dataColumnId, EnterpriseConfigDO enterpriseConfigDO);

    /**
     * 检查表点评时，异步店务记录上的状态和相关率数据
     * @param enterpriseId
     * @param dataTableId
     * @return
     */
    Boolean syncStatusWhenTableComment(String enterpriseId, Long dataTableId, String actualCommentUserId, EnterpriseConfigDO enterpriseConfigDO);

    PageInfo<StoreWorkRecordVO> storeWorkRecordList(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user);

    ImportTaskDO storeWorkRecordListExport(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user);

    PageInfo<SwStoreWorkRecordDetailVO> storeWorkRecordDetailList(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user);

    ImportTaskDO storeWorkRecordDetailListExport(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user);

    /**
     * 工单追踪
     * @param enterpriseId
     * @param businessId
     * @param user
     * @return
     */
    List<SubQuestionRecordListVO> getStoreWorkQuestionList(String enterpriseId, String businessId,CurrentUser user);

    PageInfo<StoreWorkTableVO> storeWorkTableList(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest,CurrentUser user);

    ImportTaskDO storeWorkTableListExport(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user);

    PageInfo<StoreWorkColumnVO> storeWorkColumnList(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user);

    ImportTaskDO storeWorkColumnListExport(String enterpriseId, StoreWorkRecordListRequest storeWorkRecordListRequest, CurrentUser user);

    StoreWorkBaseDetailVO getStoreWorkBaseDetail(String enterpriseId,String businessId);

    /**
     * 店务是否是同一个执行人执行完成
     * @param enterpriseId
     * @param businessId
     * @return
     */
    SameExecutorInfoVO theSameExecutor(String enterpriseId, String businessId);

    Boolean storeWorkRecordInfoShare(String enterpriseId, String businessId,String key);

    Boolean storeWorkRecordExpired(String enterpriseId, String businessId,String key);

    SubQuestionRecordListVO getStoreWorkRecordByDataColumnId(String enterpriseId, Long dataColumnId);

    /**
     * 店务检查表AI分析重试
     * @param enterpriseId 企业id
     * @param dataTableId 数据表id
     */
    void aiRetry(String enterpriseId, Long dataTableId);
}
