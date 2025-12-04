package com.coolcollege.intelligent.model.device.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.device.vo.DeviceAuthAppVO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/05
 */
@Data
public class DeviceMappingDTO {

    @Excel(name = "设备名称",orderNum = "1")
    private String deviceName;
    @Excel(name = "设备ID",orderNum = "2")
    private String deviceId;
    @Excel(name = "通道号",orderNum = "3")
    private String channelNo;
    @Excel(name = "设备状态" ,orderNum = "4", replace = {"离线_offline", "在线_online", "异常_null"} )
    private String deviceStatus;
    @Excel(name = "设备类型",orderNum = "5")
    private String deviceType;
    @Excel(name = "是否球机" ,orderNum = "6", replace = {"否_false", "是_true", "_null"} )
    private Boolean hasPtz;
    @Excel(name = "门店名称",orderNum = "7")
    private String storeName;
    @Excel(name = "门店状态",orderNum = "8")
    private String storeStatusName;
    @Excel(name = "门店场景",orderNum = "9")
    private String storeSceneName;
    @Excel(name = "添加时间", orderNum = "10")
    private String createDate;
    @Excel(name = "备注", orderNum = "11")
    private String remark;

    private Long createTime;
    /**
     * 门店ID
     */
    private String storeId;
    /**
     * 阿里云corpId
     */
    private String aliyunCorpId;

    /**
     *设备型号名称
     */
    private String deviceTypeName;

    private List<StoreAreaDTO> storeAreaDTOS;
    /**
     * 来源
     */
    private String source;
    /**
     *场景
     */
    private String scene;

    /**
     *绑定状态（0 未绑定，1 已经绑定门店）
     */
    private Boolean bindStatus;



    /**
     * 是否有子设备
     */
    private Boolean hasChildDevice;


    /**
     * 关联时间，导出用
     */
    private String bindTime;
    private String deviceSource;

    List<ChannelDTO> channelList;

    /**
     * 门店场景id
     */
    private Long storeSceneId;

    /**
     * 是否支持客流分析
     */
    private Boolean supportPassenger;

    /**
     *  是否开启客流分析
     */
    private Boolean enablePassenger;

    private String storeIds;

    private List<DeviceAuthAppVO> authList;

    private String storeStatus;


}
