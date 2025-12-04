package com.coolcollege.intelligent.model.device.request;

import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MeiTuanDeviceAuthRequest {

    @ApiModelProperty("美团门店ID")
    private String shopId;

    @ApiModelProperty("设备号")
    private String deviceCode;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("设备名称")
    private String deviceDesc;

    public static MeiTuanDeviceAuthRequest convert(EnterpriseAuthDeviceDO request){
        MeiTuanDeviceAuthRequest meiTuanDeviceAuthRequest = new MeiTuanDeviceAuthRequest();
        meiTuanDeviceAuthRequest.setShopId(request.getThirdStoreId());
        meiTuanDeviceAuthRequest.setDeviceCode(request.getDeviceId());
        meiTuanDeviceAuthRequest.setChannelNo(request.getChannelNo());
        meiTuanDeviceAuthRequest.setDeviceDesc(request.getDeviceName());
        return meiTuanDeviceAuthRequest;
    }

    public static List<MeiTuanDeviceAuthRequest> convert(List<EnterpriseAuthDeviceDO> requestList){
        List<MeiTuanDeviceAuthRequest> resultList = new ArrayList<>();
        for (EnterpriseAuthDeviceDO enterpriseAuthDeviceDO : requestList) {
            resultList.add(convert(enterpriseAuthDeviceDO));
        }
        return resultList;
    }

}
