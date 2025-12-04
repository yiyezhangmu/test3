package com.coolcollege.intelligent.model.device.request;

import com.coolcollege.intelligent.model.device.EnterpriseAuthDeviceDO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MeiTuanDeviceUnBindRequest {

    @ApiModelProperty("美团门店ID")
    private String shopId;

    @ApiModelProperty("设备号")
    private String deviceCode;

    @ApiModelProperty("通道号")
    private String channelNo;

    @ApiModelProperty("绑定ID")
    private String bindId;

    public static MeiTuanDeviceUnBindRequest convert(EnterpriseAuthDeviceDO request) {
        MeiTuanDeviceUnBindRequest meiTuanDeviceUnBindRequest = new MeiTuanDeviceUnBindRequest();
        meiTuanDeviceUnBindRequest.setShopId(request.getThirdStoreId());
        meiTuanDeviceUnBindRequest.setDeviceCode(request.getDeviceId());
        meiTuanDeviceUnBindRequest.setChannelNo(request.getChannelNo());
        return meiTuanDeviceUnBindRequest;
    }

    public static List<MeiTuanDeviceUnBindRequest> convert(List<EnterpriseAuthDeviceDO> requestList) {
        List<MeiTuanDeviceUnBindRequest> meiTuanDeviceUnBindRequestList = new ArrayList<>();
        for (EnterpriseAuthDeviceDO request : requestList) {
            meiTuanDeviceUnBindRequestList.add(convert(request));
        }
        return meiTuanDeviceUnBindRequestList;
    }

}
