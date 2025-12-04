package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseOperateLogMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseOperateLogDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseOperateLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * describe:
 *
 * @author wxp
 * @date 2021/3/25
 */
@Service
public class EnterpriseOperateLogServiceImpl implements EnterpriseOperateLogService {

    @Resource
    private EnterpriseOperateLogMapper enterpriseOperateLogMapper;


    @Override
    public int insert(EnterpriseOperateLogDO record) {
        return enterpriseOperateLogMapper.insert(record);
    }

    @Override
    public int updateStatusById(Integer updatedStatus, Long id) {
        return enterpriseOperateLogMapper.updateStatusById(updatedStatus, id);
    }

    @Override
    public int updateStatusAndOperateEndTimeById(Integer updatedStatus, Date updatedOperateEndTime, String remark, Long id) {
        return enterpriseOperateLogMapper.updateStatusAndOperateEndTimeById(updatedStatus, updatedOperateEndTime, remark, id);
    }

    @Override
    public EnterpriseOperateLogDO getLatestLogByEnterpriseIdAndOptType(String enterpriseId, String operateType) {
        return enterpriseOperateLogMapper.getLatestLogByEnterpriseIdAndOptType(enterpriseId, operateType);
    }

    @Override
    public EnterpriseOperateLogDO getLatestSuccessLog(String enterpriseId, String operateType, Integer status) {
        return enterpriseOperateLogMapper.getLatestSuccessLog(enterpriseId, operateType, status);
    }

    @Override
    public int updateStageStatusById(Integer updatedStatus, Date updatedOperateEndTime, String remark, String syncFailStage, Long id) {
        return enterpriseOperateLogMapper.updateStageStatusById(updatedStatus,updatedOperateEndTime,remark,syncFailStage,id);
    }

}
