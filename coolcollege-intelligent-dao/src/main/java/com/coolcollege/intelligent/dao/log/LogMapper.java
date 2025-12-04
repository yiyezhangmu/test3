package com.coolcollege.intelligent.dao.log;

import com.coolcollege.intelligent.model.log.ExceptionLogDO;
import com.coolcollege.intelligent.model.log.OperationLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2021/1/7 17:52
 */
@Mapper
public interface LogMapper {

    /**
     * 插入操作日志
     * @param enterpriseId
     * @param log
     */
    void insertLogOperate(@Param("enterpriseId") String enterpriseId, @Param("log") OperationLogDO log);

    /**
     * 插入异常日志
     * @param enterpriseId
     * @param exceptionLog
     */
    void insertExceptionOperate(@Param("enterpriseId") String enterpriseId, @Param("exceptionLog") ExceptionLogDO exceptionLog);
}
