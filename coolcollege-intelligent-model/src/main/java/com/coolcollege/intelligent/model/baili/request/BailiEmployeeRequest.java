package com.coolcollege.intelligent.model.baili.request;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/05
 */
@Data
public class BailiEmployeeRequest extends BailiBaseRequest {
    /**
     * 员工ID
     */
    private Integer employeeId;

    /**
     * 员工工号
     */
    private String employeeCode;

    /**
     * 大区编号
     */
    private String zoneNo;

    /**
     *品牌代号
     */
    private String brandNo;

    /**
     *员工状态
     */
    private Integer employeeStatus;

    /**组织类型
     *
     */
    private String uniTypeName;

    /**
     *开始更新时间
     */
    private Date beginUpdateTime;

    /**
     *组织编码
     */
    private String unitCode;

    /**
     *结束更新时间
     */
    private Date endUpdateTime;

    /**
     *页码
     */
    private Integer page;

    /**
     *分页大小
     */
    private Integer pageSize;

}
