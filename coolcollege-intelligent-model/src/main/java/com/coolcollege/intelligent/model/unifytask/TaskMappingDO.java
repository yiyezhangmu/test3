package com.coolcollege.intelligent.model.unifytask;

import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2020/10/26 16:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskMappingDO {

    /**
     * ID
     */
    private Long id;
    /**
     * 任务ID
     */
    private Long unifyTaskId;
    /**
     * 映射主键
     */
    private String mappingId;
    /**
     * 映射类型
     */
    private String type;
    /**
     * 对应审批节点（人员专属）
     */
    private String node;
    /**
     * 任务角色
     * 目前只有审批者一种角色 approval
     */
    private String taskRole;

    private Long originMappingId;

    private String filterRegionId;

    public TaskMappingDO(Long unifyTaskId, String mappingId, String type) {
        this.unifyTaskId = unifyTaskId;
        this.mappingId = mappingId;
        this.type = type;
    }

    public TaskMappingDO(Long unifyTaskId, String mappingId, String type, String filterRegionId) {
        this.unifyTaskId = unifyTaskId;
        this.mappingId = mappingId;
        this.type = type;
        this.filterRegionId = filterRegionId;
    }

    public TaskMappingDO(Long unifyTaskId, String mappingId, String type, String node, String taskRole) {
        this.unifyTaskId = unifyTaskId;
        this.mappingId = mappingId;
        this.type = type;
        this.node = node;
        this.taskRole = taskRole;
    }

    public static Set<String> getNodeUserList(List<TaskMappingDO> personList, String storeId, UnifyNodeEnum nodeEnum){
        if(CollectionUtils.isEmpty(personList)){
            return Sets.newHashSet();
        }
        Map<String, List<TaskMappingDO>> collectMap = personList.stream().filter(f -> nodeEnum.getCode().equals(f.getNode())).collect(Collectors.groupingBy(TaskMappingDO::getType));
        if(Objects.isNull(collectMap) || CollectionUtils.isEmpty(collectMap.get(storeId))){
            return Sets.newHashSet();
        }
        return collectMap.get(storeId).stream().map(TaskMappingDO::getMappingId).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    }


    public static Set<String> getNodeUserList(Map<String, List<TaskMappingDO>> nodeUserMap, UnifyNodeEnum nodeEnum){
        if(Objects.isNull(nodeUserMap)){
            return Sets.newHashSet();
        }
        List<TaskMappingDO> userList = nodeUserMap.get(nodeEnum.getCode());
        if(Objects.isNull(userList) || CollectionUtils.isEmpty(userList)){
            return Sets.newHashSet();
        }
        return userList.stream().map(TaskMappingDO::getMappingId).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
    }

}
