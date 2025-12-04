package com.coolcollege.intelligent.dao.supervision.dao;

import com.coolcollege.intelligent.dao.supervision.SupervisionTaskParentMapper;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskParentDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Collections;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/1 14:27
 * @Version 1.0
 */
@Repository
public class SupervisionTaskParentDao {

    @Resource
    SupervisionTaskParentMapper supervisionTaskParentMapper;

    public int insertSelective(SupervisionTaskParentDO record, String enterpriseId){
        return supervisionTaskParentMapper.insertSelective(record,enterpriseId);
    }

    public SupervisionTaskParentDO selectByPrimaryKey(Long id,  String enterpriseId){
        return supervisionTaskParentMapper.selectByPrimaryKey(id,enterpriseId);
    }

    public int updateByPrimaryKeySelective(SupervisionTaskParentDO record, String enterpriseId){
        return supervisionTaskParentMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    public int deleteByPrimaryKey(Long id,  String enterpriseId){
        return supervisionTaskParentMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public List<SupervisionTaskParentDO> listByCondition(String enterpriseId, String keyWords, Date startTime, Date endTime, List<Integer> statusList,List<String> supervisionTaskPriorityList,List<String> taskGroupingList,List<String> tags){
        return supervisionTaskParentMapper.listByCondition(enterpriseId,keyWords,startTime,endTime,statusList,supervisionTaskPriorityList,taskGroupingList,tags);
    }



    public List<SupervisionTaskParentDO> listByTaskIdList(String enterpriseId, List<Long> taskIdList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(taskIdList)) {
            return Collections.emptyList();
        }
        return  supervisionTaskParentMapper.listByTaskIdList(enterpriseId, taskIdList);
    }

    /**
     * 任务取消
     * @param enterpriseId
     * @param taskId
     * @return
     */
    public int taskParentCancel(String enterpriseId, Long taskId){
        if (taskId==null){
            return -1;
        }
        return supervisionTaskParentMapper.taskParentCancel(enterpriseId,taskId);
    }

    public int updateFailureState(String enterpriseId, Long taskId){
        if (taskId==null){
            return -1;
        }
        return supervisionTaskParentMapper.updateFailureState(enterpriseId,taskId);
    }

    /**
     * 任务删除
     * @param enterpriseId
     * @param taskId
     * @return
     */
    public int taskParentDel(String enterpriseId, Long taskId){
        if (taskId==null){
            return 0;
        }
        return supervisionTaskParentMapper.taskParentDel(enterpriseId,taskId);
    }

}
