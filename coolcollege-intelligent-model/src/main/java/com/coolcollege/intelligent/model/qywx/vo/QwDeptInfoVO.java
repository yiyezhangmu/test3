package com.coolcollege.intelligent.model.qywx.vo;

import lombok.Data;

/**
 * 企微部门信息
 * @author ：xugangkun
 * @date ：2021/10/25 16:04
 */
@Data
public class QwDeptInfoVO {

    /**
     * 部门id
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 英文名称
     */
    private String nameEn;

    /**
     * 父部门id
     */
    private Long parentId;

    /**
     * 在父部门中的次序值。order值大的排序靠前
     */
    private Long order;
}
