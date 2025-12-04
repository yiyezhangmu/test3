package com.coolcollege.intelligent.dao.device;

import com.coolcollege.intelligent.model.device.EnterpriseDeviceFetchStreamLogDO;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchenbiao
 * @date 2025-07-31 03:45
 */
public interface EnterpriseDeviceFetchStreamLogMapper {

    Integer insertLog(@Param("record") EnterpriseDeviceFetchStreamLogDO fetchStreamLog);

}