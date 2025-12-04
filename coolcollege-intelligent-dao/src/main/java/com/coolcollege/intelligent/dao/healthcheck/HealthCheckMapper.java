package com.coolcollege.intelligent.dao.healthcheck;

import org.apache.ibatis.annotations.Mapper;

/**
 * 健康检查Mapper
 */
@Mapper
public interface HealthCheckMapper {

		int checkMySqlHealth();

}
