package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;


import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: GetUserSalesDTO
 * @Description: 获取
 * @date 2023-04-28 11:24
 */
@Data
public class GetUserSalesDTO {

    private String userId;

    private String salesDt;

    private Long dingDeptId;
}
