package com.coolcollege.intelligent.service.syslog.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.param.PatrolStoreOverParam;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.PATROL_STORE_FORM_INSERT_TEMPLATE;


/**
* describe: 表单巡店操作内容处理
*
* @author wangff
* @date 2025-02-12
*/
@Service
@Slf4j
public class PatrolStoreFormResolve extends AbstractOpContentResolve {
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.PATROL_STORE_FORM;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        PatrolStoreOverParam request = jsonObject.getObject("patrolStoreOverParam", PatrolStoreOverParam.class);
        TbPatrolStoreRecordDO recordDO = tbPatrolStoreRecordMapper.selectById(enterpriseId, request.getBusinessId());
        if (Objects.isNull(recordDO)) {
            log.info("insert#巡店记录为空");
            return null;
        }
        if (TaskTypeEnum.PATROL_STORE_FORM.getCode().equals(recordDO.getPatrolType())) {
            return SysLogHelper.buildContent(PATROL_STORE_FORM_INSERT_TEMPLATE, recordDO.getStoreName(), recordDO.getStoreId(), recordDO.getId().toString());
        } else {
            sysLogDO.setDelete(true);
        }
        return null;
    }


}
