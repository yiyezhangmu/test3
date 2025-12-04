package com.coolcollege.intelligent.model.device.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.elasticSearch.annotation.DocCount;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: EnterpriseDeviceCountVO
 * @Description:
 * @date 2024-09-13 10:34
 */
@Data
public class EnterpriseDeviceCountVO {

    @Excel(name = "企业Id")
    private String enterpriseId;

    @Excel(name = "企业名称")
    private String enterpriseName;

    @Excel(name = "设备数量")
    private Integer deviceNum;

    @Excel(name = "子通道数量")
    private Integer channelNum;

    @Excel(name = "设备和子通道总数")
    private Integer deviceChannelNum;

    public EnterpriseDeviceCountVO(String enterpriseId, String enterpriseName, Integer deviceNum, Integer channelNum, Integer deviceChannelNum) {
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.deviceNum = deviceNum;
        this.channelNum = channelNum;
        this.deviceChannelNum = deviceChannelNum;
    }
}
