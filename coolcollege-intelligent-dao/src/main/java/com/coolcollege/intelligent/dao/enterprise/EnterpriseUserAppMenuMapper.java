package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserAppMenuDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
@Mapper
public interface EnterpriseUserAppMenuMapper {
    /**
     * 批量插入用户自定义移动端菜单
     *
     * @param eid
     * @param userAppMenuDOList
     * @return
     */
    Integer batchInsertEnterpriseUserAppMenu(@Param("eid") String eid,
                                             @Param("userAppMenuDOList") List<EnterpriseUserAppMenuDO> userAppMenuDOList);

    List<EnterpriseUserAppMenuDO> selectEnterpriseUserAppMenuByUser(@Param("eid") String eid,
                                                                    @Param("userId") String userId,
                                                                    @Param("menuLevel") Integer menuLevel);
    Integer deleteEnterpriseUserAppMenuByUser(@Param("eid") String eid,
                                              @Param("userId") String userId,
                                              @Param("menuLevel") Integer menuLevel);


    List<EnterpriseUserAppMenuDO> selectEnterpriseUserAppMenuByEid(@Param("eid") String eid, @Param("maxId")Long maxId);

    Integer batchInsert(@Param("eid") String eid,
                                             @Param("userAppMenuDOList") List<EnterpriseUserAppMenuDO> userAppMenuDOList);
}
