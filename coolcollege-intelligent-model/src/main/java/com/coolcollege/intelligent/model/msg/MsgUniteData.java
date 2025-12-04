package com.coolcollege.intelligent.model.msg;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一消息发送体
 * @author byd
 */
@Data
public class MsgUniteData implements Serializable {



    private static final long serialVersionUID = 1L;
    /**
     * @see com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum
     * 消息类型
     */
    private String msgType;

    /**
     * 消息数据体
     */
    private String data;
}
