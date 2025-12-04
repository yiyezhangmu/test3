package com.coolcollege.intelligent.model.device.dto;

import com.coolcollege.intelligent.model.device.vo.DeviceAuthAppVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/06
 */
@Data
public class ChannelDTO {
    private Long id;

    private String unionId;
    private String deviceId;
    private String channelNo;
    private String channelName;
    private Boolean hasPtz;
    private String status;
    private String source;
    private String deviceSource;

    private Date createTime;

    private String parentDeviceId;

    /**
     * 门店场景id
     */
    private Long storeSceneId;

    /**
     * 门店场景id名称
     */
    private String storeSceneName;

    private String remark;

    private List<DeviceAuthAppVO> authList;

    
}
