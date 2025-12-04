package com.coolcollege.intelligent.model.video.platform.yushi.response;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/24
 */
@Data
public class YonghuiDeviceResponse {

    /**
     * total	int
     * 总记录数
     *
     * deviceList	object
     * 设备列表
     *
     * deviceSerial	string
     * 设备序列号
     *
     * deviceName	string
     * 设备名称
     *
     * deviceModel	string
     * 设备型号
     *
     * deviceVersion	string
     * 设备版本
     *
     * deviceType	int
     * 设备类型（-1-未知设备，0-IPC，1-NVR，2-VMS）
     *
     * status	int
     * 状态（0-离线，1-在线）
     *
     * latestOnline	long
     * 最近上线时间，UTC时间
     * */

    private String deviceSerial;
    private String deviceName;
    private String deviceModel;
    private String deviceVersion;
    private Integer deviceType;
    private Integer status;
    private Long latestOnline;
     /*
     * isShared	int
     * 是否是共享设备（0-非共享，1-通过组织共享，2-通过设备共享，3-通过通道共享）
     */
    private Integer isShared;




    
    
    
}
