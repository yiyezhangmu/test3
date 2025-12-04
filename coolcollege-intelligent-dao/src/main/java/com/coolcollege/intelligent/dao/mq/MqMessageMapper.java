package com.coolcollege.intelligent.dao.mq;


import com.coolcollege.intelligent.model.mq.MqMessageDO;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchenbiao
 * @date 2024-02-20 03:49
 */
public interface MqMessageMapper {

    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2024-02-20 03:49
     */
    int insertSelective(@Param("enterpriseId") String enterpriseId, @Param("record") MqMessageDO record);

    Integer updateMsgStatus(@Param("enterpriseId") String enterpriseId, @Param("msgId") String msgId, @Param("toStatus") String toStatus , @Param("fromStatus") String fromStatus);

    MqMessageDO getMsgById(@Param("enterpriseId") String enterpriseId, @Param("msgId") String msgId, @Param("status") String status);

    MqMessageDO getMsgMessageBySubTaskId(@Param("enterpriseId") String enterpriseId, @Param("subTaskId") Long subTaskId);
}