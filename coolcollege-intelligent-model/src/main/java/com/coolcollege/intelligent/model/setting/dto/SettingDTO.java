package com.coolcollege.intelligent.model.setting.dto;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/08
 */
@Data
public class SettingDTO {
    private String eid;

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
    private Boolean hasOpen;

}
