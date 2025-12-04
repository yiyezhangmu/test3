package com.coolcollege.intelligent.dao.unifytask.dao;

import com.coolcollege.intelligent.dao.unifytask.UnifyTaskParentUserMapper;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentCcUserDO;
import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentUserDO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 父任务处理人
 * @author zhangnan
 * @date 2022-02-23 10:44
 */
@Repository
public class UnifyTaskParentUserDao {

    @Resource
    private UnifyTaskParentUserMapper unifyTaskParentUserMapper;

    /**
     * 新增或更新父任务处理人
     * @param enterpriseId 企业id
     * @param userDOList List<UnifyTaskParentUserDO>
     */
    public void batchInsertOrUpdate(String enterpriseId, List<UnifyTaskParentUserDO> userDOList) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(userDOList)) {
            return;
        }
        unifyTaskParentUserMapper.batchInsertOrUpdate(enterpriseId, userDOList);
    }

    /**
     * 根据父任务id删除
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     */
    public void deleteByUnifyTaskId(String enterpriseId, Long unifyTaskId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(unifyTaskId)) {
            return;
        }
        unifyTaskParentUserMapper.deleteByUnifyTaskId(enterpriseId, unifyTaskId);
    }

    /**
     * 根据父任务id更新状态
     * @param enterpriseId 企业id
     * @param unifyTaskId 父任务id
     * @param status 父任务状态
     */
    public void updateParentStatusByUnifyTaskId(String enterpriseId, Long unifyTaskId, String status) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(unifyTaskId) || Objects.isNull(status)) {
            return;
        }
        unifyTaskParentUserMapper.updateParentStatusByUnifyTaskId(enterpriseId, unifyTaskId, status);
    }


    /**
     * 根据处理人，审批人，复审人查询父任务
     * @param enterpriseId 企业id
     * @param userId 操作人id
     * @param query DisplayQuery
     * @return List<UnifyTaskParentUserDO>
     */
    public PageInfo<UnifyTaskParentUserDO> selectPageByUserId(String enterpriseId, String userId, DisplayQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        return new PageInfo<UnifyTaskParentUserDO>(unifyTaskParentUserMapper.selectByUserId(enterpriseId, userId, query));
    }

    /**
     * 查询陈列父任务统计
     * @param enterpriseId 企业id
     * @param userId 操作人id
     * @param taskType 任务类型
     * @param status 任务状态
     * @return Integer
     */
    public Integer selectDisplayParentStatistics(String enterpriseId, String userId, String taskType, String status) {
        return unifyTaskParentUserMapper.selectDisplayParentStatistics(enterpriseId, userId, taskType, status);
    }
}
