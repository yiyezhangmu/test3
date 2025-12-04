package com.coolcollege.intelligent.service.syslog.impl;

import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.util.LogUtil;
import com.coolcollege.intelligent.dao.sop.TaskSopMapper;
import com.coolcollege.intelligent.model.sop.param.TaskSopDelParam;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: hu hu
 * @Date: 2025/1/22 14:42
 * @Description:
 */
@Service
@Slf4j
public class SopFileResolve extends AbstractOpContentResolve{

    @Resource
    private TaskSopMapper taskSopMapper;

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.SOP_FILE;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        return super.insert(enterpriseId, sysLogDO);
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        return super.edit(enterpriseId, sysLogDO);
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        String content = "删除了运营手册%s";
        String names = "";
        TaskSopDelParam taskSopDelParam = LogUtil.paresString(sysLogDO.getReqParams(), "taskSopDelParam", TaskSopDelParam.class);
        if (Objects.nonNull(taskSopDelParam) && CollectionUtils.isNotEmpty(taskSopDelParam.getSopIdList())) {
            List<TaskSopVO> taskSopVOList = taskSopMapper.listByIdList(enterpriseId, taskSopDelParam.getSopIdList());
            names = taskSopVOList.stream().map(t -> String.format("「%s」", t.getFileName())).collect(Collectors.joining("、"));
        }
        return String.format(content, names);
    }
}
