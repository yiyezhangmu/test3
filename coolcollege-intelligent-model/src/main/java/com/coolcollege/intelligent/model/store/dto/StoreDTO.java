package com.coolcollege.intelligent.model.store.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.coolcollege.intelligent.model.device.vo.DeviceVO;
import com.coolcollege.intelligent.model.enums.StoreIsLockEnum;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentRegionVO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.vo.ExtendFieldInfoVO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @ClassName StoreDTO
 * @Description 用一句话描述什么
 */
@Data
public class StoreDTO {
    /**
     * 门店ID
     */
    @Excel(name = "门店id(请勿操作该栏！)", width = 40, orderNum = "5")
    private String storeId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 门店名称
     */
    @Excel(name = "门店名称(必填)", width = 20, orderNum = "1")
    private String storeName;

    /**
     * 门店编号
     */
    @Excel(name = "门店编号", width = 20, orderNum = "2")
    private String storeNum;
    /**
     * 门头照
     */
    private String avatar;

    /**
     * 所属区域  @门店信息补全使用  当做regionId使用
     */
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
    private String areaPath;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String county;

    /**
     * 店长ID
     */
    private String shopownerIds;

    /**
     * 联系方式
     */
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



    /**
     * 门店定位
     */
    private String locationAddress;

    private String thirdDeptId;
    /**
     * 详细地址
     */
    @Excel(name = "地址", width = 60, orderNum = "3")
    private String storeAddress;

    /**
     * 锁定状态
     */

    private String isLock;

    private String lockName;

    /**
     * 标签
     */
    private String tag;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 60, orderNum = "4")
    private String remark;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人员姓名
     */
    private String createName;

    /**
     * 创建人员ID
     */
    private String createUser;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 更新人员ID
     */
    private String updateName;

    /**
     * 更新人员姓名
     */
    private String updateUser;

    /**
     * 距离
     */
    private Integer distance;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 门店状态
     */
    private String isDelete;



    /**
     * 经纬度
     */
    private String longitudeLatitude;

    /**
     * 实例ID
     */
    private String bizInstId;

    /**
     * 是否设备打卡:device/设备打卡,not_device/非设备打卡
     */
    private String isDevice;

    /**
     * 阿里云租户id（视觉）
     */
    private String aliyunCorpId;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * B1名称
     */
    private String B1Name;
    /**
     * 表示是否收藏
     */
    private Integer status;

    /**
     * 门店来源
     */
    private String source;
    /**
     * 是否忽略
     */
    private Long isValid;

    /**
     * 钉钉id
     */
    private String dingId;


    /**
     * 设备列表
     */
    private List<DeviceVO> deviceList;

    private List<String> b1List;



    /**
     * 门店分组集合
     */
    private List<StoreGroupDO> storeGroupList;

    /**
     * 门店对应最后一级区域ID
     */
    private String areaId;

    /**
     * 门店人数
     */
    private Integer personCount;

    /**
     *vds corpId
     */
    private String vdsCorpId;

    private String regionPath;

    private Boolean hasVideo;

    /**
     * 门店状态（open：营业、closed：闭店、not_open：未开业）
     * */
    private String storeStatus;

    /**
     * 动态扩展字段
     * */
    private String extendField;

    /**
     * 是否完善门店信息
     * */
    private String isPerfect;

    /**
     * 动态扩展字段对象
     * */
    private List<ExtendFieldInfoVO> extendFieldInfoList;

    /**
     *  根目录->上级的区域
     */
    List<SelectComponentRegionVO> regions;

    /**
     * 门店对应区域id
     */
    private Long storeRegionId;

    private Date lastPatrolStoreTime;

    /**
     * 门店开店时间
     */
    private Date openDate;

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

    public String getLockName() {
        if (StringUtils.isNotEmpty(isLock)) {
            return StoreIsLockEnum.parse(isLock).getName();
        }
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }
}
