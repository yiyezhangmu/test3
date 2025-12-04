package com.coolcollege.intelligent.dao.order;

import com.coolcollege.intelligent.model.order.EnterpriseOrderConsumerDO;
import com.coolcollege.intelligent.model.order.EnterpriseOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/02
 */
@Mapper
public interface EnterpriseOrderConsumerMapper {

    void batchInsertEnterpriseOrderConsumer(@Param("list") List<EnterpriseOrderConsumerDO> enterpriseOrderConsumerDOList);

}
