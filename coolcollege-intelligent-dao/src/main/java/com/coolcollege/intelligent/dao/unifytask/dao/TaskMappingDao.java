package com.coolcollege.intelligent.dao.unifytask.dao;

import com.coolcollege.intelligent.dao.unifytask.TaskMappingMapper;
import com.coolcollege.intelligent.model.enums.UnifyTableEnum;
import com.coolcollege.intelligent.model.unifytask.TaskDataMappingDO;
import com.coolcollege.intelligent.model.unifytask.TaskMappingDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.UnifyFormDataDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangnan
 * @description: 任务关联数据
 * @date 2022/4/17 1:00 PM
 */
@Repository
public class TaskMappingDao {

    @Resource
    private TaskMappingMapper taskMappingMapper;

    /**
     * 插入任务关联数据
     * @param enterpriseId
     * @param mappingDOList
     */
    public void insertDataTaskMapping(String enterpriseId, List<TaskDataMappingDO> mappingDOList){
        taskMappingMapper.insertDataTaskMapping(enterpriseId, mappingDOList);
    }

    /**
     * 获取task关联data数据
     * @param enterpriseId
     * @param taskIdList
     * @return
     */
    public List<UnifyFormDataDTO> selectMappingData(String enterpriseId, List<Long> taskIdList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(taskIdList)) {
            return Lists.newArrayList();
        }
        return taskMappingMapper.selectMappingData(enterpriseId, taskIdList);
    }

    public List<GeneralDTO> selectMappingByTaskId(String enterpriseId, UnifyTableEnum unifyTableEnum, Long taskId){
        List<TaskMappingDO> mappingDOList = taskMappingMapper.selectMappingByTaskId(enterpriseId, unifyTableEnum.getCode(), taskId);
        List<GeneralDTO> storeGeneralList = mappingDOList.stream().map(m -> {
            GeneralDTO generalDTO = new GeneralDTO();
            generalDTO.setValue(m.getMappingId());
            generalDTO.setType(m.getType());
            generalDTO.setFilterRegionId(m.getFilterRegionId());
            return generalDTO;
        }).collect(Collectors.toList());
        return storeGeneralList;
    }
}
