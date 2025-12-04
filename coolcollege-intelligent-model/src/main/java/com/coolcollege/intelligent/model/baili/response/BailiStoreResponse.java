package com.coolcollege.intelligent.model.baili.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/10
 */
@NoArgsConstructor
@Data
public class BailiStoreResponse {
    private Integer id;
    private Integer unitId;
    private String unitCode;
    private String unitName;
    private String storeNo;
    private Integer employeeId;
    private String employeeName;
    private String employeeCode;
    private String createUser;
    private String createTime;
    private String updateUser;
    private String updateTime;
    private Integer delflag;
    private String remark;
}
