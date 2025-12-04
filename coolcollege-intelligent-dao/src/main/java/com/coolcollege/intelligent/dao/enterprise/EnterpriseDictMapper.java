package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseDictDO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户人事状态
 * 
 * @author xugangkun
 * @date 2022-03-02 10:31:57
 */
@Mapper
public interface EnterpriseDictMapper {
    /**
     * 主键查询
     * @param eid
     * @param id
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    EnterpriseDictDO selectById(@Param("eid") String eid, @Param("id") Long id);
    /**
     * 根据业务类型和业务详情查询
     * @param eid
     * @param businessType
     * @param businessValue
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    EnterpriseDictDO selectByTypeAndValue(@Param("eid") String eid, @Param("businessType") String businessType, @Param("businessValue") String businessValue);
    /**
     * 获得所有状态
     * @param eid
     * @param businessType 业务类型
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    List<UserPersonnelStatusVO> selectAllByType(@Param("eid") String eid, @Param("businessType") String businessType);

    /**
     * 保存
     * @param eid
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    void save(@Param("eid") String eid, @Param("entity") EnterpriseDictDO entity);

    /**
     * 根据主键更新
     * @param eid
     * @param entity 实体
     * @return: void
     * @Author: xugangkun
     */
    void updateById(@Param("eid") String eid, @Param("entity") EnterpriseDictDO entity);
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

    /**
     * 根据类型查询
     * @param eid 企业id
     * @param businessType 业务类型
     * @return List<EnterpriseDictDO>
     */
    List<EnterpriseDictDO> selectByType(String eid, String businessType);

}
