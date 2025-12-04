package com.coolcollege.intelligent.model.senyu.request;

import lombok.Data;

/**
 * describe:
 *
 * @author wxp
 * @date 2021/09/07
 */
@Data
public class SenYuBaseRequest {

    private Long timeStamp;

    private String sign;

    /**
     * 1、根据身份证获取用户信息   getEmployeeInfoByIdCard
     * 2、获取全部门店列表的接口   listAllStoreByPage
     * 3、获取用户直接管辖的门店列表 listAuthStores
     * 4、获取所有岗位的接口  listAllRoles
     * 5、根据上级编码查询直属员⼯的接⼝ listDirectEmployees
     */
    private String api;


}
