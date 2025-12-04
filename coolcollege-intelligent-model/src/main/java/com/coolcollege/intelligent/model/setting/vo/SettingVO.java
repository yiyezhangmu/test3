package com.coolcollege.intelligent.model.setting.vo;

import com.coolcollege.intelligent.model.setting.EnterpriseVideoSettingDO;
import lombok.Data;

/**
 * describe:企业全局配置返回类，存在redis中
 *
 * @author zhouyiping
 * @date 2021/03/23
 */

@Data
public class SettingVO {
    ///视频服务配置

    private Long id;
    private String eid;

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
    private Boolean hasOpen;

    //海康云眸一级菜单区域ID
    private String hikCloudFristNodeId;

}
