package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseOperateLogDO;

import java.util.Date;

/**
 * describe:
 *
 * @author wxp
 * @date 2021/3/25
 */
public interface EnterpriseOperateLogService {

    int insert(EnterpriseOperateLogDO record);

    int updateStatusById(Integer updatedStatus, Long id);

    int updateStatusAndOperateEndTimeById(Integer updatedStatus, Date updatedOperateEndTime, String remark, Long id);

    EnterpriseOperateLogDO getLatestLogByEnterpriseIdAndOptType(String enterpriseId, String operateType);

    EnterpriseOperateLogDO getLatestSuccessLog(String enterpriseId, String operateType, Integer status);

    /**
     * 更新同步日志表
     * @param updatedStatus
     * @param updatedOperateEndTime
     * @param remark
     * @param syncFailStage 同步异常阶段
     * @param id
     * @return
     */
    int updateStageStatusById(Integer updatedStatus, Date updatedOperateEndTime, String remark,String syncFailStage,Long id);

}
