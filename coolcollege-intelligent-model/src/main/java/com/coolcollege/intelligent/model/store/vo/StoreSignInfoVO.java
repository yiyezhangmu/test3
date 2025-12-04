package com.coolcollege.intelligent.model.store.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @date 2023-05-18 02:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreSignInfoVO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;
    @Excel(name = "日期", orderNum = "1", width = 20, exportFormat = DateUtils.DATE_FORMAT_SEC_6)
    @ApiModelProperty("签到日期")
    private Date signDate;


    @ApiModelProperty("门店id")
    private String storeId;

    @Excel(name = "门店名称", orderNum = "2", width = 20)
    @ApiModelProperty("门店名称")
    private String storeName;

    @Excel(name = "门店编号", orderNum = "3", width = 20)
    @ApiModelProperty("门店编号")
    private String storeNum;

    @ApiModelProperty("区域ID")
    private Long regionId;

    @ApiModelProperty("区域路径(新)")
    private String regionWay;

    @ApiModelProperty("巡店人id")
    private String supervisorId;

    @Excel(name = "人员", orderNum = "4", width = 20)
    @ApiModelProperty("巡店人姓名")
    private String supervisorName;

    @Excel(name = "签到", orderNum = "5", width = 20, exportFormat = DateUtils.DATE_FORMAT_SEC_4)
    @ApiModelProperty("签到时间")
    private Date signStartTime;

    @Excel(name = "签退", orderNum = "7", width = 20, exportFormat = DateUtils.DATE_FORMAT_SEC_4)
    @ApiModelProperty("签退时间")
    private Date signEndTime;

    @ApiModelProperty("巡店时长")
    private Long tourTime;

    @Excel(name = "巡店时长", orderNum = "9", width = 20)
    @ApiModelProperty("巡店时长-")
    private String tourTimeStr;

    @ApiModelProperty("签到地址")
    private String signStartAddress;

    @ApiModelProperty("签退地址")
    private String signEndAddress;

    @ApiModelProperty("签到定位经纬度")
    private String startLongitudeLatitude;

    @ApiModelProperty("签退定位经纬度")
    private String endLongitudeLatitude;

    @ApiModelProperty("签到状态 1正常 2异常")
    private Integer signInStatus;

    @Excel(name = "签到信息", orderNum = "6", width = 20)
    @ApiModelProperty("签到信息 1正常 2异常")
    private String signInStatusStr;

    @ApiModelProperty("签退状态 1正常 2异常")
    private Integer signOutStatus;

    @Excel(name = "签退信息", orderNum = "8", width = 20)
    @ApiModelProperty("签退信息 1正常 2异常")
    private String signOutStatusStr;

    @Excel(name = "签到备注", orderNum = "12", width = 20)
    @ApiModelProperty("签到备注信息")
    private String signStartRemark;

    @Excel(name = "签退备注", orderNum = "13", width = 20)
    @ApiModelProperty("签退备注信息")
    private String signEndRemark;

    @ApiModelProperty("删除标记")
    private Boolean deleted;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("签到图片")
    private String signInPicture;

    @Excel(name = "签到图片", orderNum = "10", width = 20)
    @ApiModelProperty("签到图片视频")
    private String signInPictureVideo;

    @ApiModelProperty("签退图片")
    private String signOutPicture;

    @Excel(name = "签退图片", orderNum = "13", width = 20)
    @ApiModelProperty("签退视频")
    private String signOutPictureVideo;

    @ApiModelProperty("签到视频")
    private String signInVideo;

    @ApiModelProperty("签退视频")
    private String signOutVideo;

    public String getSignInStatusStr() {
        if (this.getSignInStatus() == null || this.getSignInStatus() == 0) {
            return "";
        }
        return "线下签到(" + (this.getSignInStatus() == 1 ? "正常" : "异常") + ")";
    }

    public String getSignOutStatusStr() {
        if (this.getSignOutStatus() == null || this.getSignOutStatus() == 0) {
            return "";
        }
        return "线下签退(" + (this.getSignOutStatus() == 1 ? "正常" : "异常") + ")";
    }


    public String getSignInPictureVideo() {
        String handleVideo = null;
        if (StringUtils.isBlank(this.signInVideo)) {
            return this.signInPicture;
        }
        SmallVideoInfoDTO handleVideoInfo = JSONObject.parseObject(this.signInVideo, SmallVideoInfoDTO.class);
        handleVideo = CollectionUtils.emptyIfNull(handleVideoInfo.getVideoList())
                .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.COMMA));
        if (StringUtils.isBlank(handleVideo)) {
            return this.signInPicture;
        }
        return StringUtils.isNotBlank(this.signInPicture) ? this.signInPicture + Constants.COMMA + handleVideo : handleVideo;
    }

    public String getSignOutPictureVideo() {
        String handleVideo = null;
        if (StringUtils.isBlank(this.signOutVideo)) {
            return this.signOutPicture;
        }
        SmallVideoInfoDTO handleVideoInfo = JSONObject.parseObject(this.signOutVideo, SmallVideoInfoDTO.class);
        handleVideo = CollectionUtils.emptyIfNull(handleVideoInfo.getVideoList())
                .stream().map(SmallVideoDTO::getVideoUrl).filter(Objects::nonNull).collect(Collectors.joining(Constants.COMMA));
        if (StringUtils.isBlank(handleVideo)) {
            return this.signOutPicture;
        }
        return StringUtils.isNotBlank(this.signOutPicture) ? this.signOutPicture + Constants.COMMA + handleVideo : handleVideo;
    }
}