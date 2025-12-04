package com.coolcollege.intelligent.model.video.vo;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/20
 */
@Data
public class VideoPollingDetailVO {
    /**
     * 主键
     * 主键
     * isNullAble:0
     */
    private Long id;

    /**
     * 轮询主题
     * isNullAble:1
     */
    private String videoPollingName;

    /**
     * 分屏数目
     * isNullAble:1
     */
    private Integer splitScreenNum;

    /**
     * 间隔时间
     * isNullAble:1
     */
    private Integer playInterval;

    /**
     * 设备列表用逗号分隔
     * isNullAble:1
     */
    private List<VideoPollingDeviceDetailVO> deviceList;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

}
