package com.coolcollege.intelligent.dao.login;

import com.coolcollege.intelligent.model.login.EnterpriseLoginCountDTO;
import com.coolcollege.intelligent.model.login.UserLoginCountDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoginRecordMapper {


    /**
     * 插入登陆记录
     * @param enterpriseId
     * @param useId
     * @param createTime
     * @return
     */
    void insertLoginRecording(@Param("enterpriseId")String enterpriseId,
                              @Param("userId")String useId,
                              @Param("createTime")Long createTime);

    /**
     * 统计用户登陆次数
     * @param enterpriseId
     * @param userIdList
     * @return
     */
    UserLoginCountDTO countLoginRecord(@Param("enterpriseId")String enterpriseId,
                                       @Param("userIdList") List<String> userIdList );

    /**
     * 统计企业登录次数
     * @param enterpriseId
     * @return
     */
    EnterpriseLoginCountDTO countEnterpriseLogin(@Param("enterpriseId")String enterpriseId);
}
