package com.coolcollege.intelligent.model.selectcomponent;

import lombok.Data;

/**
 * @desc: 选人组件中岗位信息的返回
 * @author: xuanfeng
 * @date: 2021-10-27 15:07
 */
@Data
public class SelectComponentPositionVO {
    /**
     * id
     */
    private Long id;

    /**
     * 岗位id
     */
    private String positionId;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 人员数
     */
    private Integer userCount;

    private String source;
}
