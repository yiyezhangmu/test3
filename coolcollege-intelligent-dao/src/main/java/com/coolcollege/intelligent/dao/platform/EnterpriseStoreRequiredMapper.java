package com.coolcollege.intelligent.dao.platform;

import com.coolcollege.intelligent.model.platform.EnterpriseStoreRequiredDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/09/21
 */
@Mapper
public interface EnterpriseStoreRequiredMapper {

    /**
     * 保存门店必填项
     * @param id
     * @param required
     * @return
     */
    int batchInsertStoreRequired(@Param("id") String id, @Param("fields") List<EnterpriseStoreRequiredDO> required);


    /**
     * 保存门店必填项(初始化)
     * @param required
     * @return
     */
    int batchInsertStoreRequiredByInit( @Param("fields") List<EnterpriseStoreRequiredDO> required);

    /**
     * 获取门店必填项
     * @param id
     * @return
     */
    List<EnterpriseStoreRequiredDO> getStoreRequired(@Param("id") String id);

    /**
     * 删除门店必填项
     * @param id
     * @return
     */
    int deleteStoreRequired(@Param("id") String id);
    /**
     * 查询出所有企业的配置
     * @return
     */
    List<EnterpriseStoreRequiredDO> selectStoreRequiredAll();



}
