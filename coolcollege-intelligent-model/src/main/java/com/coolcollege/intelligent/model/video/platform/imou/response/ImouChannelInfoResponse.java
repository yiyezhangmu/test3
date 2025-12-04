package com.coolcollege.intelligent.model.video.platform.imou.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/04/25
 */
@Data
public class ImouChannelInfoResponse {

    /**
     * 云存储状态，notExist：未开通套餐，using：开通云存储且没有过期，expired：套餐过期
     */
    private String csStatus;

    /**
     * 可选，被共享和授权的权限功能列表（逗号隔开）
     */
    private String shareFunctions;

    /**
     *动检开关状态 0:关闭状态，1：开启状态
     */
    private Integer alarmStatus;

    /**
     *通道名称
     */
    private String channelName;

    /**
     *是否在线 ，online-在线 offline-离线 close-未配置 sleep-休眠 upgrading升级中
     */
    private String channelOnline;

    /**
     *通道能力集
     */
    private String channelAbility;

    /**
     *设备序列号
     */
    private String deviceId;

    /**
     *
     */
    private String channelId;

    /**
     *缩略图URL，新设备可以通过刷新设备通道封面图或者上传设备通道封面图更新该字段
     */
    private String channelPicUrl;

    /**
     *""：设备属于自己；"share"：通过乐橙app共享给此用户
     */
    private String shareStatus;
}
