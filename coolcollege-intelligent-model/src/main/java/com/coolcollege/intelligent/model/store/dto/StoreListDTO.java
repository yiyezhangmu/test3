package com.coolcollege.intelligent.model.store.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.vo.ExtendFieldInfoVO;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/3/1 20:14
 * @Version 1.0
 */
@Data
@Builder
public class StoreListDTO {

    private String storeArea;
    /**
     * 区域id
     */
    private Long regionId;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 区域名称路径
     */
    private String regionPath;

    private String storeId;

    private String storeName;

    private String storeNum;

    private String avatar;

    private String telephone;

    /**
     * 营业时间
     */
    private String businessHours;
    /**
     * 门店面积
     */
    private String storeAcreage;
    /**
     * 门店带宽
     */
    private String storeBandwidth;

    private String locationAddress;

    /**
     * 门店状态（open：营业、closed：闭店、not_open：未开业）
     * */
    private String storeStatus;
    /**
     * 详细地址
     */
    private String storeAddress;

    /**
     * 扩展字段
     */
    private String extendField;

    /**
     * 是否完善门店信息
     * */
    private String isPerfect;

    /**
     * 动态扩展字段对象
     * */
    private List<ExtendFieldInfoVO> extendFieldInfoList;

    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    private String longitudeLatitude;

    private List<StoreGroupDO> storeGroupList;

    /**
     * 门店开店时间
     */
    private Date openDate;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人员姓名
     */
    private String createName;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    public String getLongitudeLatitude() {
        if (StringUtils.isNotEmpty(longitude) && StringUtils.isNotEmpty(latitude)) {
            return longitude + "," + latitude;
        }
        return longitudeLatitude;
    }

    public void setLongitudeLatitude(String longitudeLatitude) {
        this.longitudeLatitude = longitudeLatitude;
    }



}
