package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreHistoryDo;
import com.coolcollege.intelligent.model.patrolstore.query.GetCheckUserVO;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreCheckQuery;
import com.coolcollege.intelligent.model.patrolstore.query.PatrolStoreStatisticsDataTableQuery;
import com.coolcollege.intelligent.model.patrolstore.query.SetCheckUserQuery;
import com.coolcollege.intelligent.model.patrolstore.request.*;
import com.coolcollege.intelligent.model.patrolstore.records.PatrolStoreRecordsTableAndPicDTO;
import com.coolcollege.intelligent.model.patrolstore.statistics.*;
import com.coolcollege.intelligent.model.patrolstore.vo.*;
import com.github.pagehelper.PageInfo;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author shuchang.wei
 * @date 2020/12/16
 */
public interface PatrolStoreRecordsService {

    /**
     * 检查记录表记录
     */
    @Deprecated
    PageInfo tableRecords(String enterpriseId, TableRecordsRequest tableRecordsRequest);

    @Deprecated
    Object  tableRecordsExport(String enterpriseId,TableRecordsRequest request);

    ImportTaskDO starRecordsExport(String enterpriseId, TableRecordsRequest request);

    Object tableRecordsAsyncExport(String enterpriseId,TableRecordsRequest request);

    @Deprecated
    PageInfo singleTableColumnsRecords(String enterpriseId, SingleTableColumnsRecordsRequest request);


    List<PatrolStoreRecordsTableAndPicDTO> tableRecordsListExport(String enterpriseId, TableRecordsRequest request);

    PageInfo potralRecordList(String enterpriseId, PatrolStoreStatisticsDataTableQuery query);

    ImportTaskDO potralRecordListExport(String enterpriseId, PatrolStoreStatisticsDataTableQuery query);

    PageInfo potralRecordDetailList(String enterpriseId, PatrolStoreStatisticsDataTableQuery query);

    Map<Long, String> getDataTableDwellTimeMap(String enterpriseId, List<Long> businessIds);

    ImportTaskDO potralRecordDetailListExport(String enterpriseId, PatrolStoreStatisticsDataTableQuery query);

    ResponseResult potralStoreSummarySave(String enterpriseId, PatrolRecordRequest query);

    ResponseResult patrolStoreSignatureSave( String enterpriseId, PatrolRecordRequest query);
    /**
     * 巡店记录补漏
     * @param enterpriseId
     * @param subTaskId
     * @param patrolType
     * @param settingDO
     */
    void makeUpPatrolMetaRecord(String enterpriseId, Long subTaskId, String patrolType, EnterpriseStoreCheckSettingDO settingDO, String storeId);

    PageInfo<PatrolStoreCheckVO> getPatrolStoreCheckList(String enterpriseId, PatrolStoreCheckQuery query) throws IOException;

    Boolean setCheckUser(String enterpriseId, SetCheckUserQuery query);

    GetCheckUserVO getCheckUser(String enterpriseId);

    PageInfo<DataStaTableColumnVO> getCheckDetailList(String enterpriseId, PatrolStoreCheckQuery query) throws IOException;

    PageInfo<CheckAnalyzeVO> getCheckAnalyzeList(String enterpriseId, PatrolStoreCheckQuery query);

    ImportTaskDO ExportCheckList(String enterpriseId, PatrolStoreCheckQuery query) throws IOException;

    PageInfo<ExportPatrolStoreCheckVO> exportPatrolStoreCheckList(PatrolStoreCheckQuery query) throws IOException;

    ImportTaskDO ExportCheckDetailList(String enterpriseId, PatrolStoreCheckQuery query) throws IOException;

    ImportTaskDO ExportCheckAnalyzeList(String enterpriseId, PatrolStoreCheckQuery query);

    PatrolStoreCheckRecordVO taskRecordInfo(String enterpriseId, Long id, Integer checkType);

    List<TableInfoDTO> dataTableInfoList(String enterpriseId, Long id, String userId, Integer checkType);

    List<TbPatrolStoreHistoryDo> selectPatrolStoreHistoryList(String enterpriseId, Long id);

    List<PatrolRecordStatusEveryDayVO> patrolRecordStatusEveryDay(String enterpriseId, PatrolRecordStatusRequest param);

    List<PatrolRecordListByDayVO> patrolRecordListByDay(String enterpriseId, PatrolRecordListByDayRequest param);

    /**
     * 门店验收记录
     * @param enterpriseId 企业id
     * @param request 门店验收查询
     * @return 脚手架门店验收记录VO
     */
    List<StoreAcceptanceVO> getStoreAcceptanceRecords(String enterpriseId, StoreAcceptanceRequest request);
}
