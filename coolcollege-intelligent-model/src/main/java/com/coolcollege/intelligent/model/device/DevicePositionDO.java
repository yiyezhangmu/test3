package com.coolcollege.intelligent.model.device;

import lombok.Data;

import java.util.Date;

/**
 * describe:设备预制点位表
 *
 * @author zhouyiping
 * @date 2021/04/28
 */
@Data
public class DevicePositionDO {
    //主键
    private Long id;
    //设备Id
    private String deviceId;
    //通道号
    private String channelNo;
    //预制位名称
    private String devicePositionName;
    //预置位索引
    private String positionIndex;
    //云类型
    private String yunType;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //创建用户
    private String createUser;
    //更新用户
    private String updateUser;

}
