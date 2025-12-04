package com.coolcollege.intelligent.dao.enterprise;
import java.util.List;

import com.coolcollege.intelligent.model.enterprise.EnterpriseOperateLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface EnterpriseOperateLogMapper {

    int insert(EnterpriseOperateLogDO record);

    EnterpriseOperateLogDO selectByPrimaryKey(Long id);

    int updateStatusById(@Param("updatedStatus")Integer updatedStatus,@Param("id")Long id);

    int updateStatusAndOperateEndTimeById(@Param("updatedStatus")Integer updatedStatus,
                                          @Param("updatedOperateEndTime")Date updatedOperateEndTime ,
                                          @Param("remark")String remark,
                                          @Param("id")Long id);

    EnterpriseOperateLogDO getLatestLogByEnterpriseIdAndOptType(@Param("enterpriseId")String enterpriseId, @Param("operateType")String operateType);

    EnterpriseOperateLogDO getLatestSuccessLog(@Param("enterpriseId")String enterpriseId,@Param("operateType")String operateType,@Param("status")Integer status);

    int updateStageStatusById(@Param("updatedStatus")Integer updatedStatus,
                         @Param("updatedOperateEndTime")Date updatedOperateEndTime ,
                         @Param("remark")String remark,
                         @Param("syncFailStage") String syncFailStage,
                         @Param("id")Long id);

}