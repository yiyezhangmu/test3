package com.coolcollege.intelligent.facade.open.api.supervison;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiUpdateSupervisionTaskDTO;
import com.coolcollege.intelligent.facade.dto.openApi.SupervisionParentListDTO;
import com.coolstore.base.response.rpc.OpenApiResponseVO;


/**
 * @Author wxp
 * @Date 2023/2/7 17:03
 * @Version 1.0
 */
public interface SupervisionTaskApi {
    /**
     * 无需操作，配置校验规则  提供给沪上调用 可批量
     * @param dto
     * @return
     */
    OpenApiResponseVO batchUpdateSupervisionTaskStatus(OpenApiUpdateSupervisionTaskDTO dto);

    /**
     * 添加督导父任务
     * @param dto
     * @return
     */
     OpenApiResponseVO addSupervisionTaskParent(JSONObject jsonObject);

    /**
     * 获取父任务列表
     * @param dto
     * @return
     */
    OpenApiResponseVO getSupervisionTaskParentList(JSONObject jsonObject);

    /**
     * 督导按人任务列表
     * @param dto
     */
    OpenApiResponseVO listSupervisionTaskByParentId(JSONObject jsonObject);


    /**
     * 督导按门店任务列表
     * @param dto
     */
    OpenApiResponseVO listSupervisionStoreTaskByParentId(JSONObject jsonObject);

    /**
     * 更新门店任务状态
     * @param dto
     * @return
     */
    OpenApiResponseVO batchUpdateSupervisionStoreTaskStatus(OpenApiUpdateSupervisionTaskDTO dto);

    /**
     * 更新门店分组
     * @param jsonObject
     */
    OpenApiResponseVO updateStoreGroup(JSONObject jsonObject);

    /**
     * 更新用户分组
     * @param jsonObject
     * @return
     */
    OpenApiResponseVO updateUserGroup(JSONObject jsonObject);


}
