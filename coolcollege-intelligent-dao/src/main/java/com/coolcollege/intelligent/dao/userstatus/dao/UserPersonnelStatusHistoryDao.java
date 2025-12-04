package com.coolcollege.intelligent.dao.userstatus.dao;

import com.coolcollege.intelligent.dao.userstatus.UserPersonnelStatusHistoryMapper;
import com.coolcollege.intelligent.model.user.UserPersonnelStatusHistoryDO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusHistoryVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/2 10:43
 */
@Service
public class UserPersonnelStatusHistoryDao {

    @Resource
    private UserPersonnelStatusHistoryMapper userPersonnelStatusHistoryMapper;


    /**
     * 主键查询
     * @param id
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    public UserPersonnelStatusHistoryDO selectById(String eid, Long id) {
        return userPersonnelStatusHistoryMapper.selectById(eid, id);
    }

    /**
     * 根据userId以及有效时间查询
     * @param eid
     * @param userId
     * @param effectiveTime
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    public UserPersonnelStatusHistoryDO selectByUserIdAndEffectiveTime(String eid, String userId, String effectiveTime) {
        return userPersonnelStatusHistoryMapper.selectByUserIdAndEffectiveTime(eid, userId, effectiveTime);
    }

    /**
     * 根据userId以及有效时间查询
     * @param eid
     * @param userIds
     * @param starTime
     * @param endTime
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    public List<UserPersonnelStatusHistoryVO> getStatusHistoryReport(String eid, List<String> userIds, String starTime, String endTime) {
        if(CollectionUtils.isEmpty(userIds)){
            return Lists.newArrayList();
        }
        return  userPersonnelStatusHistoryMapper.getStatusHistoryReport(eid, userIds, starTime, endTime);
    }

    /**
     * 保存
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    public void save(String eid, UserPersonnelStatusHistoryDO entity) {
        userPersonnelStatusHistoryMapper.save(eid, entity);
    }

    /**
     * 保存
     * @param list
     * @return: void
     * @Author: xugangkun
     */
    public void batchInsertOrUpdate(String eid, List<UserPersonnelStatusHistoryDO> list) {
        userPersonnelStatusHistoryMapper.batchInsertOrUpdate(eid, list);
    }

    /**
     * 根据主键更新
     * @param entity 实体
     * @return: void
     * @Author: xugangkun
     */
    public void updateById(String eid, UserPersonnelStatusHistoryDO entity) {
        userPersonnelStatusHistoryMapper.updateById(eid, entity);
    }
    /**
     * 根据主键删除
     * @param id
     * @return: void
     * @Author: xugangkun
     */
    public void deleteById(String eid, Long id) {
        userPersonnelStatusHistoryMapper.deleteById(eid, id);
    }
    /**
     * 根据主键批量删除
     * @Param:
     * @param ids id列表
     * @return: void
     * @Author: xugangkun
     */
    public void deleteBatchByIds(String eid, List<Long> ids) {
        userPersonnelStatusHistoryMapper.deleteBatchByIds(eid, ids);
    }



}
