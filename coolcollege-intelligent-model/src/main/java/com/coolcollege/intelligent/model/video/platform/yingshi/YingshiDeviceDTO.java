package com.coolcollege.intelligent.model.video.platform.yingshi;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/02
 */
@Data
public class YingshiDeviceDTO {

    //设备Id
    private String deviceSerial;
    //设备名称
    private String deviceName;
    //设备类型
    private String model;
    //设备状态
    private Integer status;
    //具有防护能力的设备布撤防状态：0-睡眠，8-在家，16-外出，普通IPC布撤防状态：0-撤防，1-布防
    private String defence;
    //是否加密
    private Integer isEncrypt;
    //告警声音模式：0-短叫，1-长叫，2-静音
    private Integer alarmSoundMode;
    //设备下线是否通知：0-不通知 1-通知
    private Integer offlineNotify;
    //设备大类
    private String category;
    //网络类型，如有线连接wire
    private String netType;
    //信号强度(%)
    private String signal;
    /**
     * 是否支持封面抓图: 0-不支持, 1-支持
     */
    private int supportCapture;

    private Boolean supportPassenger;


}
