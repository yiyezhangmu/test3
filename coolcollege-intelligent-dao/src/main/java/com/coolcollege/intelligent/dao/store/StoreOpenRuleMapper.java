package com.coolcollege.intelligent.dao.store;

import com.coolcollege.intelligent.model.store.StoreOpenRuleDO;
import com.coolcollege.intelligent.model.store.dto.CountStoreRuleDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2023-05-12 01:49
 */
public interface StoreOpenRuleMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-05-12 01:49
     */
    int insertSelective(@RequestParam("record") StoreOpenRuleDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-05-12 01:49
     */
    StoreOpenRuleDO selectByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-05-12 01:49
     */
    int updateByPrimaryKeySelective(StoreOpenRuleDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-05-12 01:49
     */
    int deleteByPrimaryKey(Long id, @Param("enterpriseId") String enterpriseId);

    List<StoreOpenRuleDO> list(@Param("enterpriseId") String enterpriseId,
                               @Param("createUserId") String createUserId,
                               @Param("regionId") String regionId,
                               @Param("newStoreTaskStatus") String newStoreTaskStatus,
                               @Param("mappingId") List<String> mappingId,
                               @Param("ruleName") String ruleName);

    CountStoreRuleDTO count(@Param("eid") String eid,
                            @Param("regionId") String regionId,
                            @Param("mappingId") List<String> mappingId);
}