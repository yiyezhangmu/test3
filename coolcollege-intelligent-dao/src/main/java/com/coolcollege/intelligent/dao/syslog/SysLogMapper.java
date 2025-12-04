package com.coolcollege.intelligent.dao.syslog;

import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.model.syslog.request.SysLogRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2025-01-20 01:52
 */
public interface SysLogMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2025-01-20 01:52
     */
    int insertSelective(@Param("record") SysLogDO record, @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2025-01-20 01:52
     */
    int updateByPrimaryKeySelective(@Param("record") SysLogDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据id查询
     * @param enterpriseId 企业id
     * @param id 主键id
     * @return 实体对象
     */
    SysLogDO selectById(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    List<SysLogDO> selectByParams(@Param("enterpriseId") String enterpriseId, @Param("params") SysLogRequest params);
}