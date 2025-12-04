package com.coolcollege.intelligent.model.setting;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/23
 */
@Data
public class EnterpriseVideoSettingDO {
    private Long id;

    //企业Id
    private String enterpriseId;
    //视频云端访问key（空则是默认阿里云）
    private String accessKeyId;
    //视频云端访问秘钥（空则是默认阿里云）
    private String secret;
    //云类型: ali 阿里云，yushi 宇视,yingshi 萤石云， other 其他
    private String yunType;
    //阿里云视频corpId
    private String aliyunCorpId;
    //是否接入视频
    private Boolean openVideoStreaming;
    //企业vdsId
    private String rootVdsCorpId;
    //是否打开数据分析
    private Boolean openDataAnalysis;
    private Boolean openAlarmEvent;
    private Boolean openYunControl;
    private Integer videoPlaybackType;
    private Boolean openWebHook;
    private String createName;
    private Date createTime;
    private String updateName;
    private Date updateTime;
    private Boolean hasOpen;
    private String accountType;
    private Date lastSyncTime;
    private Integer syncStatus;

    private String syncDeviceFlag;
    /**
     * 扩展信息
     */
    private String extendInfo;
}
