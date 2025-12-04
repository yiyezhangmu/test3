package com.coolcollege.intelligent.model.video;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/20
 */
@Data
public class VideoPollingDO {
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
    private String deviceIdListStr;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;
    /**
     * 创建人
     */
    private String createId;

    /**
     * 更新人
     */
    private String updateId;

}
