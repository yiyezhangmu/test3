package com.coolcollege.intelligent.service.video;

import com.coolcollege.intelligent.model.video.platform.yingshi.*;
import com.coolcollege.intelligent.model.video.platform.yingshi.YingshiChannelDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.DeviceCapacityDTO;
import com.coolcollege.intelligent.model.video.platform.yingshi.YingshiDeviceDTO;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/31
 */
public interface YingshiDeviceService {

    String createYingshiAuthUrl(String eid, String storeId, String userId);


    YingshiDeviceDTO getYingshiDeviceInfo( String deviceId,String token);

    String getRedisToken(String eid);

    String getLiveUrl(String deviceId, Integer channelNo, Integer quality,Integer protocol, String startTime, String endTime,String token);

    List<YingshiChannelDTO> getChannelList(String eid,String deviceId, String token);

    //云台控制
    /**
     *开始云台控制
     * @param deviceId
     * @param channelNo
     * @param direction 操作命令，0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-放大，9-缩小，10-近焦距，11-远焦距
     *  @param speed 	云台速度：0-慢，1-适中，2-快，海康设备参数不可为0(直接判断不支持0)
     */
    Boolean ptzStart(String deviceId,Integer channelNo,Integer direction,Integer speed,String token);

    /**
     * 停止云台控制
     * @param deviceId
     * @param channelNo
     */
    Boolean ptzStop(String deviceId,Integer channelNo,String token);

    /**
     * 添加预置位
     * @param deviceId
     * @param channelNo
     * @param presetIndexName
     * @param token
     * @return
     */
    Integer addPreset(String deviceId,Integer channelNo,String presetIndexName,String token );


    /**
     * 删除预置位
     * @param deviceId
     * @param channelNo
     * @param presetIndex
     * @param token
     * @return
     */
    Boolean deletePreset(String deviceId,Integer channelNo,String presetIndex,String token);

    /**
     * 调用预置位
     * @param deviceId
     * @param channelNo
     * @param presetIndex
     * @param token
     * @return
     */
    Boolean invokePreset(String deviceId,Integer channelNo,String presetIndex,String token);

    /**
     * 获取客流统计开关状态
     * @param deviceId
     * @return
     */
    PassengerFlowSwitchStatusDTO passengerFlowSwitchStatus(String deviceId,String token);

    /**
     * 设置客流统计开关
     * @param deviceId
     * @param channelNo
     * @param enable
     * @param token
     * @return
     */
    Boolean savePassengerFlow(String  deviceId,Integer channelNo,Boolean enable,String token);

    /**
     * 查询设备某一天的统计客流数据
     * @param deviceId
     * @param channelNo
     * @param date 时间戳日期，精确至毫秒，默认为今天，date参数只能是0时0点0分0秒（如1561046400000可以，1561050000000不行）
     * @param token
     * @return
     */
    PassengerFlowDailyDTO passengerFlowDaily(String deviceId, Integer channelNo, Long date,String token);

    /**
     * 查询设备某一天每小时的客流数据
     * @param deviceId
     * @param channelNo
     * @param date 时间戳日期，精确至毫秒，默认为今天，date参数只能是0时0点0分0秒（如1561046400000可以，1561050000000不行）
     * @param token
     * @return
     */
    List<PassengerFlowHourlyDTO> passengerFlowHourly(String deviceId, Integer channelNo, Long date,String token);

    /**
     * 保存客流统计配置信息
     * @param deviceId
     * @param channelNo
     * @param line 统计线的两个坐标点，坐标范围为0到1之间的7位浮点数，(0,0)坐标在左上角，格式如{"x1": "0.0","y1": "0.5","x2": "1","y2": "0.5"}
     * @param direction 指示方向的两个坐标点，(x1,y1)为起始点，(x2,y2)为结束点格式如{"x1": "0.5","y1": "0.5","x2": "0.5","y2": "0.6"}，最好与统计线保持垂直
     * @param token
     * @return
     */
    Boolean savePassengerFlowConfig(String deviceId,Integer channelNo,String line,String direction,String token);

    /**
     * 获取客流统计配置信息
     * @param deviceId
     * @param channelNo
     * @param token
     * @return
     */
    PassengerFlowConfigDTO getPassengerFlowConfig(String deviceId,Integer channelNo,String token);



    /**
     * 抓拍图片
     * @param deviceId
     * @param channelNo
     * @param token
     * @return
     */
    String capture(String deviceId,Integer channelNo, String token);

    /**
     * 查询萤石设备能力集
     * @param deviceId
     * @param token
     * @return
     */
    DeviceCapacityDTO getYingshiDeviceCapaticy(String deviceId, String token);

    Boolean cancelAuth(String deviceSerials,String token);


    /**
     * 获取accessToken
     * @return
     */
    String getAccessToken();

}