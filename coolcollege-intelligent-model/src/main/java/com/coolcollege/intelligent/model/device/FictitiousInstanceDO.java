package com.coolcollege.intelligent.model.device;

import lombok.Data;

/**
 * @ClassName FictitiousInstanceDO
 * @Description 用一句话描述什么
 * @author 首亮
 */
@Data
public class FictitiousInstanceDO {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 全局实例ID
     */
    private String bizInstId;

    /**
     * 全局打卡组ID
     */
    private String punchGroupId;
}
