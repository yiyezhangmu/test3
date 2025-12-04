package com.coolcollege.intelligent.service.unifytask.impl;

import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.model.enums.UnifyTaskDataTypeEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.dao.unifytask.dao.TaskMappingDao;
import com.coolcollege.intelligent.model.unifytask.TaskDataMappingDO;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyTaskBuildDTO;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskDataMappingService;
import com.coolcollege.intelligent.util.patrolStore.TableTypeUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author byd
 * @date 2021-02-22 10:34
 */
@Service
@Slf4j
public class UnifyTaskDataMappingServiceImpl implements UnifyTaskDataMappingService {

    @Resource
    private TbMetaTableMapper tbMetaTableMapper;

    @Resource
    private TaskMappingDao taskMappingDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void insertDataTaskMappingNew(String enterpriseId, UnifyTaskBuildDTO task, Long taskId) {
        List<TaskDataMappingDO> checkList = getMappingSpecialData(enterpriseId, taskId, task.getForm());
        taskMappingDao.insertDataTaskMapping(enterpriseId, checkList);
    }

    @Override
    public void insertDataTaskMappingNew(String enterpriseId, TaskParentDO taskParentDO, List<GeneralDTO> form) {
        List<TaskDataMappingDO> checkList = getMappingSpecialData(enterpriseId, taskParentDO.getId(), form);
        taskMappingDao.insertDataTaskMapping(enterpriseId, checkList);
    }

    @Override
    public Map<Long, List<GeneralDTO>> getTaskDataMappingMap(String enterpriseId, List<Long> taskIds) {
        List<UnifyFormDataDTO> taskDateMapping = taskMappingDao.selectMappingData(enterpriseId, taskIds);
        Map<Long, List<GeneralDTO>> formMap = taskDateMapping.stream().collect(Collectors.groupingBy(UnifyFormDataDTO::getUnifyTaskId,
                Collectors.mapping(dataMapping -> {
                    GeneralDTO form = new GeneralDTO();
                    form.setType(dataMapping.getType());
                    form.setValue(dataMapping.getOriginMappingId());
                    form.setName(dataMapping.getMappingName());
                    form.setValid(dataMapping.getValid());
                    return form;
                }, Collectors.toList())));
        return formMap;
    }

    /**
     * 关系映射
     *
     * @param taskId
     * @param formList
     * @return
     */
    private List<TaskDataMappingDO> getMappingSpecialData(String enterpriseId, Long taskId, List<GeneralDTO> formList) {
        List<TaskDataMappingDO> mappingDOList = Lists.newArrayList();
        List<String> idList = formList.stream().filter(x -> StringUtils.isNotBlank(x.getValue())).map(GeneralDTO::getValue).collect(Collectors.toList());
        List<TbMetaTableDO> tbMetaTableDOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(idList)){
            tbMetaTableDOS = tbMetaTableMapper.selectByIds(enterpriseId, idList.stream().map(x -> Long.parseLong(x)).collect(Collectors.toList()));
        }
        Map<Long, Integer> tablePropertyMap = tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, TbMetaTableDO::getTableProperty));
        for (GeneralDTO item : formList) {
            String type = item.getType();
            Integer tableProperty = tablePropertyMap.getOrDefault(item.getValue(), null);
            //如果是自定义表
            if (tableProperty!=null&& TableTypeUtil.isUserDefinedTable(tableProperty,UnifyTaskDataTypeEnum.STANDARD.getCode())){
                 type = UnifyTaskDataTypeEnum.DEFINE.getCode();
            }
            TaskDataMappingDO data = new TaskDataMappingDO();
            data.setMappingName(item.getName());
            data.setOriginMappingId(item.getValue());
            data.setUnifyTaskId(taskId);
            data.setType(type);
            data.setCheckTable(item.getCheckTable() != null && item.getCheckTable());
            if(item.getAiAudit() != null){
                data.setAiAudit(item.getAiAudit());
            }
            ValidateUtil.validateString(data.getType());
            mappingDOList.add(data);
        }
        return mappingDOList;
    }

}
