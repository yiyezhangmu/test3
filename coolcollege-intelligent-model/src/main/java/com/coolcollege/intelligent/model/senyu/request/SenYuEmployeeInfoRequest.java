package com.coolcollege.intelligent.model.senyu.request;

import lombok.Data;

/**
 * describe:
 *
 * @author wxp
 * @date 2021/09/07
 */
@Data
public class SenYuEmployeeInfoRequest extends SenYuBaseRequest {

    /**
     *身份证号码
     */
    private String idCard;

    /**
     * 上级编码  userId
     */
    private String parentCode;

    /**
     *岗位编号集合，,分隔
     */
    private String roleIds;

    private Integer page = 1;

    private Integer pageSize = 50;

}
