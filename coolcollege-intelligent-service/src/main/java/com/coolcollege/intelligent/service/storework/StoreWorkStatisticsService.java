package com.coolcollege.intelligent.service.storework;

import com.coolcollege.intelligent.model.homepage.vo.StoreWorkDataVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.storework.request.*;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkStatisticsExecutiveDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author byd
 * @Date 2022/9/8 15:22
 * @Version 1.0
 */
public interface StoreWorkStatisticsService {


    /**
     * 店务概况
     */
    StoreWorkDataVO getStoreWorkStatistic(String enterpriseId, StoreWorkDataStatisticRequest queryParam);

    /**
     * 店务概况
     */
    StoreWorkDataStoreDetailVO getStoreDetailWorkStatistic(String enterpriseId, StoreWorkStoreDetailStatisticRequest queryParam);


    /**
     * 门店执行力等级分布
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    StoreWorkStatisticsExecutiveDTO storeExecutiveStatistics(String enterpriseId, StoreWorkDataStatisticRequest queryParam);

    /**
     * 区域执行力排行
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    List<StoreWorkRegionRankDataVO> regionExecutiveRank(String enterpriseId, StoreWorkDataStatisticRequest queryParam);

    /**
     * 门店执行力排行
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    List<StoreWorkStoreRankDataVO> storeExecutiveRank(String enterpriseId, StoreWorkDataStatisticRequest queryParam);

    /**
     * 不合格项排行
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    List<StoreWorkColumnRankDataVO> columnFailRank(String enterpriseId, StoreWorkDataStatisticRequest queryParam);

    /**
     * 事项完成率排行
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    List<ColumnCompleteRateRankDataVO> completeRateRank(String enterpriseId, StoreWorkDataStatisticRequest queryParam);

    /**
     * 店务概况-折线图
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    List<StoreWorkDataVO> getStoreWorkCharStatistic(String enterpriseId, StoreWorkDataStatisticRequest queryParam);

    /**
     * 区域执行力明细列表
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    List<StoreWorkStatisticsOverviewVO> regionExecutiveList(String enterpriseId, StoreWorkDataListRequest queryParam);

    /**
     * 区域执行力汇总列表
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    List<StoreWorkStatisticsOverviewListVO> regionExecutiveSummaryList(String enterpriseId, StoreWorkDataListRequest queryParam);

    /**
     * 导出执行力汇总
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    ImportTaskDO exportRegionExecutiveSummaryList(String enterpriseId, StoreWorkDataListRequest queryParam, String dbName);


    /**
     * 导出执行力明细列表
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    ImportTaskDO exportRegionExecutiveList(String enterpriseId, StoreWorkDataListRequest queryParam, String dbName);


    /**
     * 区域执行力汇总列表
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    PageInfo<StoreWorkStoreSummaryVO> storeExecutiveSummaryList(String enterpriseId, StoreWorkDataListRequest queryParam);

    /**
     * 导出执行力汇总
     * @param enterpriseId
     * @param queryParam
     * @param currentUser
     * @return
     */
    ImportTaskDO exportStoreExecutiveSummaryList(String enterpriseId, StoreWorkDataListRequest queryParam, CurrentUser currentUser);

    /**
     * 区域执行力汇明细-门店详情
     * @param enterpriseId
     * @param queryParam
     * @param currentUser
     * @return
     */
    StoreWorkStoreDetailVO storeExecutiveDetail(String enterpriseId, StoreWorkDataListRequest queryParam, CurrentUser currentUser);


    /**
     * 区域执行力汇明细-门店详情-列表
     * @param enterpriseId
     * @param queryParam
     * @param currentUser
     * @return
     */
    PageInfo<StoreWorkDataTableDetailListVO> storeExecutiveDetailList(String enterpriseId, StoreWorkDataListRequest queryParam, CurrentUser currentUser);

    /**
     * 门店执行力红-门店详情-门店作业事项明细
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    List<StoreWorkDataTableDetailColumnListVO> storeExecutiveDetailColumnList(String enterpriseId, StoreWorkDataColumnListRequest queryParam);

    /**
     * 导出执行力汇总
     * @param enterpriseId
     * @param queryParam
     * @param currentUser
     * @return
     */
    ImportTaskDO exportStoreExecutiveDetailColumnList(String enterpriseId, StoreWorkDataColumnListRequest queryParam, CurrentUser currentUser);


    /**
     * 事项完成率列表
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    PageInfo<StoreWorkDataTableColumnListVO> columnCompleteRateList(String enterpriseId, StoreWorkDataListRequest queryParam, CurrentUser user);

    /**
     * 导出事项完成率列表
     * @param enterpriseId
     * @param queryParam
     * @param currentUser
     * @return
     */
    ImportTaskDO exportColumnCompleteRateList(String enterpriseId, StoreWorkDataListRequest queryParam, CurrentUser currentUser);

    /**
     * 事项完成率列表
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    PageInfo<StoreWorkColumnStoreListVO> columnStoreCompleteList(String enterpriseId, StoreWorkColumnDetailListRequest queryParam, CurrentUser user);

    /**
     * 导出事项完成率-检查项详情列表
     * @param enterpriseId
     * @param queryParam
     * @param currentUser
     * @return
     */
    ImportTaskDO exportColumnStoreCompleteList(String enterpriseId, StoreWorkColumnDetailListRequest queryParam, CurrentUser currentUser);

    /**
     * 运营概况-店务数据列表
     * @param enterpriseId
     * @param param
     * @return
     */
    List<StoreWorkDataStoreDetailList> getStoreDetailWorkStatisticList(String enterpriseId,
                                                                           StoreWorkStoreDetailStatisticRequest param);

    /**
     * 不合格项门店列表
     */
    PageInfo<StoreWorkFailColumnStoreListVO> failColumnStoreList(String enterpriseId, StoreDataListRequest param, CurrentUser currentUser);
}
