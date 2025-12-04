package com.coolcollege.intelligent.service.senyu;

import com.coolcollege.intelligent.model.senyu.request.SenYuBaseRequest;
import com.coolcollege.intelligent.model.senyu.request.SenYuEmployeeInfoRequest;
import com.coolcollege.intelligent.model.senyu.request.SenYuStoreRequest;
import com.coolcollege.intelligent.model.senyu.response.*;

/**
 * @author wxp
 */
public interface SenYuService {

    SenYuBaseResponse<String> test(SenYuBaseRequest request);

    /**
     * 根据身份证获取用户信息
     * @param request
     * @return
     */
    SenYuBaseResponse<SenYuEmployeeInfoResponse> getEmployeeInfoByIdCard(SenYuEmployeeInfoRequest request);

    /**
     * 获取全部门店列表的接口
     * @param request
     * @return
     */
    SenYuBaseResponse<SenYuStorePageResponse> listAllStoreByPage(SenYuStoreRequest request);

    /**
     * 获取用户直接管辖的门店列表
     * @param request
     * @return
     */
    SenYuBaseListResponse<SenYuStoreResponse> listAuthStores(SenYuEmployeeInfoRequest request);

    /**
     * 获取所有岗位的接口
     * @param request
     * @return
     */
    SenYuBaseListResponse<SenYuRoleResponse> listAllRoles(SenYuBaseRequest request);

    /**
     * 根据上级编码查询直属员⼯的接⼝   使用此接口同步区域
     * @param request
     * @return
     */
    SenYuBaseListResponse<SenYuEmployeeInfoResponse> listDirectEmployees(SenYuEmployeeInfoRequest request);

    /**
     * 查岗位下面的人
     * @param request
     * @return
     */
    SenYuBaseListResponse<SenYuEmployeeInfoResponse> listEmployeesByRoldIds(SenYuEmployeeInfoRequest request);

}
