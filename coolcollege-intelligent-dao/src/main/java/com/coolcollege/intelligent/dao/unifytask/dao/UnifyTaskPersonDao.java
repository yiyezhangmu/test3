package com.coolcollege.intelligent.dao.unifytask.dao;

import com.coolcollege.intelligent.dao.unifytask.UnifyTaskPersonMapper;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskPersonDO;
import com.coolcollege.intelligent.model.unifytask.request.GetMiddlePageDataByPersonRequest;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 按人任务
 * @author zhangnan
 * @date 2022-04-14 15:28
 */
@Repository
public class UnifyTaskPersonDao {

    @Resource
    private UnifyTaskPersonMapper unifyTaskPersonMapper;

    /**
     * 新增按人任务
     * @param enterpriseId
     * @param unifyTaskPersonDO
     */
    public void insertTaskPerson(String enterpriseId, UnifyTaskPersonDO unifyTaskPersonDO) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(unifyTaskPersonDO)) {
            return;
        }
        unifyTaskPersonMapper.insertSelective(unifyTaskPersonDO, enterpriseId);
    }

    /**
     * 根据子任务id查询
     * @param enterpriseId
     * @param subTaskId
     * @return
     */
    public UnifyTaskPersonDO selectBySubTaskId(String enterpriseId, Long subTaskId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(subTaskId)) {
            return null;
        }
        return unifyTaskPersonMapper.selectBySubTaskId(enterpriseId, subTaskId);
    }

    /**
     * 查询中间页数据
     * @param enterpriseId
     * @param request
     * @return
     */
    public PageInfo<UnifyTaskPersonDO> selectMiddlePageData(String enterpriseId, GetMiddlePageDataByPersonRequest request) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(request)) {
            return new PageInfo<>();
        }
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        return new PageInfo<>(unifyTaskPersonMapper.selectMiddlePageData(enterpriseId, request));
    }

    /**
     * 根据父任务id删除
     * @param enterpriseId
     * @param unifyTaskId
     */
    public void deleteByUnifyTaskId(String enterpriseId, Long unifyTaskId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(unifyTaskId)) {
            return;
        }
        unifyTaskPersonMapper.deleteByUnifyTaskId(enterpriseId, unifyTaskId);
    }

    /**
     * 根据父任务id删除
     * @param enterpriseId
     * @param id
     */
    public void deleteByPrimaryKey(String enterpriseId, Long id) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)) {
            return;
        }
        unifyTaskPersonMapper.deleteByPrimaryKey(id, enterpriseId);
    }


    public void updateTaskPersonStoreIds(String enterpriseId, Long subTaskId, String storeIds){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(subTaskId) || StringUtils.isBlank(storeIds) ) {
            return;
        }
        unifyTaskPersonMapper.updateTaskPersonStoreIds(enterpriseId, subTaskId, storeIds);
    }

    public void updateTaskPersonCompleteStatus(String enterpriseId, Long subTaskId, String subStatus, Date completeTime){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(subTaskId) || StringUtils.isBlank(subStatus)  ) {
            return;
        }
        unifyTaskPersonMapper.updateTaskPersonCompleteStatus(enterpriseId, subTaskId, subStatus, completeTime);
    }

    public Integer countByTaskIdAndStatus(String enterpriseId, Long taskId, Long loopCount, String subStatus){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(taskId) || Objects.isNull(loopCount) || StringUtils.isBlank(subStatus)  ) {
            return 0;
        }
        return  unifyTaskPersonMapper.countByTaskIdAndStatus(enterpriseId, taskId, loopCount, subStatus);
    }

    public List<UnifyTaskPersonDO> listBySubTaskIdList(String enterpriseId, List<Long> subTaskIdList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(subTaskIdList)) {
            return Lists.newArrayList();
        }
        return unifyTaskPersonMapper.listBySubTaskIdList(enterpriseId, subTaskIdList);
    }

    public Integer countByTaskIdAndCreateTime(String enterpriseId, Long taskId, Date beginDate, Date endTime){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(taskId) || Objects.isNull(beginDate) || Objects.isNull(endTime)) {
            return 0;
        }
        return  unifyTaskPersonMapper.countByTaskIdAndCreateTime(enterpriseId, taskId, beginDate, endTime);
    }

    /**
     * 根据子任务id查询
     * @param enterpriseId
     * @return
     */
    public UnifyTaskPersonDO selectByUserIdAndLoopCount(String enterpriseId, Long taskId, String handleUserId, Long loopCount) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(taskId)) {
            return null;
        }
        return unifyTaskPersonMapper.selectByUserIdAndLoopCount(enterpriseId, taskId, handleUserId, loopCount);
    }

    public void updateTaskPersonById(String enterpriseId, Long subTaskId, String handleUserId, Long id){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(subTaskId) || StringUtils.isBlank(handleUserId) || id == null) {
            return;
        }
        unifyTaskPersonMapper.updateTaskPersonById(enterpriseId, subTaskId, handleUserId, id);
    }

    public List<UnifyTaskPersonDO> selectList(String enterpriseId, String subStatus, String handleUserId){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(subStatus) || StringUtils.isBlank(handleUserId)) {
            return new ArrayList<>();
        }
        return unifyTaskPersonMapper.selectList(enterpriseId, subStatus, handleUserId);
    }

}
