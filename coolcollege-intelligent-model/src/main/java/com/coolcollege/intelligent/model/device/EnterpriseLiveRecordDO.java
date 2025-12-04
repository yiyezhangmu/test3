package com.coolcollege.intelligent.model.device;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/12
 */
@Data
public class EnterpriseLiveRecordDO {
    //主键
    private Long id;
    //企业ID
    private String enterpriseId;
    //流索引
    private String liveId;
    //播放状态 0未推流 1推流中 2异常流
    private Integer liveStatus;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //保活时间
    private Date liveTime;
    //设备名称
    private String deviceId;
    //设备通道
    private String channelNo;
    //拉流清晰度 0 高清 1标清 2普通
    private Integer streamIndex;
}
