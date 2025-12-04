package com.coolcollege.intelligent.dao.userstatus;

import com.coolcollege.intelligent.model.user.UserPersonnelStatusHistoryDO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusHistoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户人事状态历史表
 * 
 * @author xugangkun
 * @date 2022-03-02 10:31:57
 */
@Mapper
public interface UserPersonnelStatusHistoryMapper {
    /**
     * 主键查询
     * @param eid
     * @param id
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    UserPersonnelStatusHistoryDO selectById(@Param("eid") String eid, @Param("id") Long id);
    /**
     * 根据userId以及有效时间查询
     * @param eid
     * @param userId
     * @param effectiveTime
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    UserPersonnelStatusHistoryDO selectByUserIdAndEffectiveTime(@Param("eid") String eid, @Param("userId") String userId,
                                                                @Param("effectiveTime") String effectiveTime);
    /**
     * 根据userId以及时间段查询
     * @param eid
     * @param userIds
     * @param starTime
     * @param endTime
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    List<UserPersonnelStatusHistoryVO> getStatusHistoryReport(@Param("eid") String eid, @Param("userIds") List<String> userIds,
                                                              @Param("starTime") String starTime, @Param("endTime") String endTime);

    /**
     * 保存
     * @param eid
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    void save(@Param("eid") String eid, @Param("entity") UserPersonnelStatusHistoryDO entity);

    /**
     * 保存
     * @param eid
     * @param list
     * @return: void
     * @Author: xugangkun
     */
    void batchInsertOrUpdate(@Param("eid") String eid, @Param("list") List<UserPersonnelStatusHistoryDO> list);

    /**
     * 根据主键更新
     * @param eid
     * @param entity 实体
     * @return: void
     * @Author: xugangkun
     */
    void updateById(@Param("eid") String eid, @Param("entity") UserPersonnelStatusHistoryDO entity);
    /**
     * 根据主键删除
     * @param eid
     * @param id
     * @return: void
     * @Author: xugangkun
     */
    void deleteById(@Param("eid") String eid, @Param("id") Long id);
    /**
     * 根据主键批量删除
     * @param eid
     * @param ids id列表
     * @return: void
     * @Author: xugangkun
     */
    void deleteBatchByIds(@Param("eid") String eid, @Param("ids") List<Long> ids);

}
