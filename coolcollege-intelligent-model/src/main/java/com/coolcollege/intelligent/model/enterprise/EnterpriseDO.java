package com.coolcollege.intelligent.model.enterprise;

import com.google.common.base.Strings;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName EnterpriseDO
 * @Description 用一句话描述什么
 * @author 首亮
 */
@Data
public class EnterpriseDO {
    /**
     * 企业主键
     */
    private String id;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 原始名称
     */
    private String originalName;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 状态
     */
    private Integer status;

    private String logo;

    /**
     * 用户类型(1:普通用户 2:付费用户  3:试用用户 4:共创用户)
     */
    private Integer isVip;

    /**
     * 授权人数
     */
    private Integer authType;

    private String authUserId;

    private String industry;

    /**
     * 企业logo
     */
    private String corpLogoUrl;

    /**
     * logo名称
     */
    private String logoName;

    /**
     * 企业是否认证
     */
    private Boolean isAuthenticated;

    /**
     * 企业认证等级，0：未认证，1：高级认证，2：中级认证，3：初级认证
     */
    private Integer authLevel;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 最后一次更新时间
     */
    private Date updateTime;


    /**
     * 套餐开始时间
     */
    private Date packageBeginDate;

    /**
     * 套餐结束时间
     */
    private Date packageEndDate;

    /**
     * 阿里云分组cropId
     */
    private String groupCropId;

    private String appType;

    /**
     * 企业标签
     */
    private String tag;

    /**
     * 门店数量限制
     */
    private Integer limitStoreCount;

    /**
     * 设备数量限制
     */
    private Integer limitDeviceCount;

    /**
     * 设置企业名称
     *
     * @param name 企业名称
     */
    public void setName(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            this.name = name.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "");
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Enterprise{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
