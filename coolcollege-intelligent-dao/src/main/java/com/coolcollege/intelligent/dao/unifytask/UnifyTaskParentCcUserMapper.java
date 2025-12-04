package com.coolcollege.intelligent.dao.unifytask;

import com.coolcollege.intelligent.model.unifytask.UnifyTaskParentCcUserDO;
import com.coolcollege.intelligent.model.unifytask.query.DisplayQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 父任务与抄送人映射表
 * 
 * @author xugangkun
 * @date 2021-11-17 15:06:44
 */
@Mapper
public interface UnifyTaskParentCcUserMapper {
    /**
     * 主键查询
     * @param eid
     * @param id
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    UnifyTaskParentCcUserDO selectById(@Param("eid") String eid, @Param("id") Long id);

    /**
     * 根据抄送人id查询
     * @param eid
     * @param userId
     * @param query
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    List<UnifyTaskParentCcUserDO> selectByCcUserId(@Param("eid") String eid, @Param("userId") String userId, @Param("query") DisplayQuery query);

    /**
     * 统计父信息
     * @param eid
     * @param userId
     * @param taskType
     * @param status
     * @author: xugangkun
     * @return java.lang.Integer
     * @date: 2021/11/19 14:30
     */
    Integer selectDisplayParentStatistics(@Param("eid") String eid, @Param("userId") String userId,
                                          @Param("taskType") String taskType,  @Param("status") String status);


    /**
     * 记录数量统计
     * @return: int
     * @Author: xugangkun
     */
    int count();

    /**
     * 保存
     * @param eid
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    void save(@Param("eid") String eid, @Param("entity") UnifyTaskParentCcUserDO entity);

    /**
     * 批量保存
     * @param eid
     * @param list
     * @return: void
     * @Author: xugangkun
     */
    void batchInsertOrUpdate(@Param("eid") String eid, @Param("list") List<UnifyTaskParentCcUserDO> list);

    /**
     * 根据主键删除
     * @param eid
     * @param id
     * @return: void
     * @Author: xugangkun
     */
    void deleteById(@Param("eid") String eid, @Param("id") Long id);

    /**
     * 根据父任务id删除
     * @param eid
     * @param unifyTaskId
     * @return: void
     * @Author: xugangkun
     */
    void deleteByUnifyTaskId(@Param("eid") String eid, @Param("unifyTaskId") Long unifyTaskId);

    /**
     * 更新抄送任务的状态
     * @param eid
     * @param taskId
     * @param parentStatus
     * @author: xugangkun
     * @return void
     * @date: 2021/11/19 14:34
     */
    void updateTaskParentStatus(@Param("eid") String eid, @Param("taskId") Long taskId, @Param("parentStatus") String parentStatus);

    /**
     * 根据父任务id列表查询
     * @param enterpriseId
     * @param unifyTaskIds
     * @return
     */
    List<UnifyTaskParentCcUserDO> selectByUnifyTaskIds(@Param("enterpriseId") String enterpriseId, @Param("unifyTaskIds") List<Long> unifyTaskIds);
}
