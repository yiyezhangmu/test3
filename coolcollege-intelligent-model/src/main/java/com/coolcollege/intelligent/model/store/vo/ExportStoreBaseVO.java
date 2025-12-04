package com.coolcollege.intelligent.model.store.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.device.vo.DeviceVO;
import com.coolcollege.intelligent.model.enums.StoreIsLockEnum;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.dto.StoreSupervisorMappingDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/13
 */
@Data
public class ExportStoreBaseVO {

    /**
     * 门店ID
     */
    @Excel(name = "门店Id", width = 40, orderNum = "1")
    private String storeId;
    /**
     * 门店名称
     */
    @Excel(name = "门店名称", width = 20, orderNum = "2")
    private String storeName;

    /**
     * 门店编号
     */
    @Excel(name = "门店编号", width = 20, orderNum = "3")
    private String storeNum;

    /**
     * 区域全路径
     */
    @Excel(name = "区域", width = 20, orderNum = "4")
    private String region;

    /**
     * 详细地址
     */
    @Excel(name = "地址", width = 60, orderNum = "5")
    private String storeAddress;

    @Excel(name = "联系方式", width = 20, orderNum = "6")
    private String phone;

    /**
     * 经度
     */
    @Excel(name = "经度", width = 40, orderNum = "7")
    private String longitude;

    /**
     * 纬度
     */
    @Excel(name = "纬度", width = 40, orderNum = "8")
    private String latitude;

    /**
     * b1设备名称
     */
    @Excel(name = "b1", width = 40, orderNum = "9")
    private String b1DeviceName;

    /**
     * 摄像头设备名称
     */
    @Excel(name = "摄像头", width = 40, orderNum = "10")
    private String videoDeviceName;


    /**
     * 备注
     */
    @Excel(name = "备注", width = 60, orderNum = "11")
    private String remark;


}
