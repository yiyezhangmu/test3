package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseUserWxDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2024-09-25 02:54
 */
public interface EnterpriseUserWxMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-09-25 02:54
     */
    int insertSelective(@Param("record") EnterpriseUserWxDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2024-09-25 02:54
     */
    int updateByPrimaryKeySelective(@Param("record") EnterpriseUserWxDO record, @Param("enterpriseId") String enterpriseId);

    EnterpriseUserWxDO getByOpenId(@Param("openid") String openid, @Param("enterpriseId") String enterpriseId);

    /**
     * 获取openids
     * @param userIds
     * @return
     */
    List<String> getOpenIdsByUserIds(@Param("enterpriseId") String enterpriseId, @Param("userIds") List<String> userIds);
}