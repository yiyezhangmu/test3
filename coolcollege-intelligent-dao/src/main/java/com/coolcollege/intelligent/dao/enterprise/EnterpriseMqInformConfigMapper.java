package com.coolcollege.intelligent.dao.enterprise;


import com.coolcollege.intelligent.model.enterprise.EnterpriseMqInformConfigDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 企业mq配置表(EnterpriseMqInform)
 * @author CFJ
 * @Date 2023-08-03 13:42:44
 */
@Mapper
public interface EnterpriseMqInformConfigMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param enterpriseId 主键
     * @return 实例对象
     */
    EnterpriseMqInformConfigDO queryById(@Param("eid") String enterpriseId);


    EnterpriseMqInformConfigDO queryByStatus(@Param("eid") String enterpriseId, @Param("status") Integer status);

    /**
     * 统计总行数
     *
     * @param enterpriseMqInformDO 查询条件
     * @return 总行数
     */
    Long count(EnterpriseMqInformConfigDO enterpriseMqInformDO);

    /**
     * 新增数据
     *
     * @param enterpriseMqInformDO 实例对象
     * @return 影响行数
     */
    Integer insert(EnterpriseMqInformConfigDO enterpriseMqInformDO);


    /**
     * 修改数据
     *
     * @param enterpriseMqInformDO 实例对象
     * @return 影响行数
     */
    Integer update(EnterpriseMqInformConfigDO enterpriseMqInformDO);

    /**
     * 通过主键删除数据
     *
     * @param enterpriseId 主键
     * @return 影响行数
     */
    Integer deleteById(@Param("eid") String enterpriseId);

}

