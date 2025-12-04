package com.coolcollege.intelligent.model.passengerflow;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/10/08
 */
@Data
public class PassengerFlowRecordDO {
    //主键ID
    private Long id;
    //门店ID
    private String storeId;
    //门店区域
    private String regionPath;
    //设备ID
    private String deviceId;
    //设备通道号
    private Integer channelNo;
    //是否有子通道
    private Boolean hasChildDevice;
    //场景Id
    private Long sceneId;
    /**
     * 场景类型：(进店，出店，进店出店，无)store_in,store_out,store_in_out,nothing
     */
    private String sceneType;
    //客流数据类型：小时，天，月
    private String flowType;
    //进店客流
    private Integer flowIn;
    //出店客流
    private Integer flowOut;
    //进店+出店客流
    private Integer flowInOut;
    //进店年份
    private Integer flowYear;
    //进店的时间（天）
    private Date flowDay;
    //进店的月份
    private Integer flowMonth;

    //进店时刻小时
    private Integer flowHour;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //更新人
    private String updateUser;

    //客流属性统计
    private String attributeCount;
    //过店客流
    private Integer flowPass;
}
