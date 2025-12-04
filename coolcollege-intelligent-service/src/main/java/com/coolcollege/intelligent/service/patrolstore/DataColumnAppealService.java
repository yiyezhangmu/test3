package com.coolcollege.intelligent.service.patrolstore;

import com.coolcollege.intelligent.model.patrolstore.dto.BatchDataColumnAppealDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbDataColumnAppealHistoryDTO;
import com.coolcollege.intelligent.model.patrolstore.dto.TbDataColumnAppealListDTO;
import com.coolcollege.intelligent.model.patrolstore.param.DataColumnAppealParam;

import java.util.List;

/**
 * @author byd
 * @date 2023-08-17 15:28
 */
public interface DataColumnAppealService {

    /**
     * 申诉
     * @param eid
     * @param userId
     * @param batchDataColumnAppealDTO
     */
    void appeal(String eid, String userId, String userName, BatchDataColumnAppealDTO batchDataColumnAppealDTO);


    /**
     * 申诉
     * @param eid
     * @param userId
     * @param businessId
     */
    List<TbDataColumnAppealListDTO> appealList(String eid, String userId, Long businessId);

    /**
     * 申诉审核
     * @param eid
     * @param userId
     * @param dataColumnAppealParam
     */
    void appealApprove(String eid, String userId, String userName, DataColumnAppealParam dataColumnAppealParam, String dingCorpId,String appType);

    /**
     * 申诉审核
     * @param eid
     * @param dataColumnId
     */
    List<TbDataColumnAppealHistoryDTO> appealHistoryList(String eid, Long dataColumnId);

    /**
     * 申诉
     * @param eid
     * @param userId
     * @param appealId
     */
    TbDataColumnAppealListDTO appealDetail(String eid, String userId, Long appealId);
}
