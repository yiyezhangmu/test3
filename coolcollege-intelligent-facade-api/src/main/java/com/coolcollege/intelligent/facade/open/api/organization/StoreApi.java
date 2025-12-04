package com.coolcollege.intelligent.facade.open.api.organization;

import com.coolcollege.intelligent.facade.dto.openApi.*;
import com.coolstore.base.response.rpc.OpenApiResponseVO;

/**
 * @Author suzhuhong
 * @Date 2022/7/18 17:03
 * @Version 1.0
 */
public interface StoreApi {
    /**
     * 门店列表
     * @param openApiStoreDTO
     * @return
     */
    OpenApiResponseVO storeList(OpenApiStoreDTO openApiStoreDTO);

    OpenApiResponseVO listIncreaseStore(OpenApiStoreDTO openApiStoreDTO);


    /**
     * 门店详情
     * @param openApiStoreDTO
     * @return
     */
    OpenApiResponseVO storeDetail(OpenApiStoreDTO openApiStoreDTO);

    /**
     * 添加门店
     * @param openApiAddStoreDTO
     * @return
     */
    OpenApiResponseVO addStore(OpenApiAddStoreDTO openApiAddStoreDTO);


    OpenApiResponseVO insertOrUpdateStore(OpenApiInsertOrUpdateStoreDTO openApiAddStoreDTO);

    /**
     * 更新门店
     * @param openApiAddStoreDTO
     * @return
     */
    OpenApiResponseVO updateStore(OpenApiAddStoreDTO openApiAddStoreDTO);

    OpenApiResponseVO updateStoreInfo(OpenApiUpdateStoreDTO openApiAddStoreDTO);

    OpenApiResponseVO removeStore(OpenApiAddStoreDTO openApiAddStoreDTO);

    /**
     * 添加门店分组
     * @param openApiStoreGroupDTO
     * @return
     */
    OpenApiResponseVO addStoreGroup(OpenApiStoreGroupDTO openApiStoreGroupDTO);

    /**
     * 移除门店分组
     * @param openApiStoreGroupDTO
     * @return
     */
    OpenApiResponseVO removeStoreGroup(OpenApiStoreGroupDTO openApiStoreGroupDTO);

    /**
     * 修改门店分组
     * @param openApiStoreGroupDTO
     * @return
     */
    OpenApiResponseVO updateStoreGroup(OpenApiStoreGroupDTO openApiStoreGroupDTO);

    /**
     * 查询门店分组列表
     * @param openApiStoreGroupDTO
     * @return
     */
    OpenApiResponseVO storeGroupList(OpenApiStoreGroupDTO openApiStoreGroupDTO);

    /**
     * 查询门店分组详情
     * @param openApiStoreGroupDTO
     * @return
     */
    OpenApiResponseVO getGroupInfo(OpenApiStoreGroupDTO openApiStoreGroupDTO);

    /**
     * 门店增加API
     * @param xfsgAddStoreDTO
     * @return
     */
    OpenApiResponseVO addXfsgStore(XfsgAddStoreDTO xfsgAddStoreDTO);

    /**
     * 门店转移API
     * @param xfsgTransferStoreDTO
     * @return
     */
    OpenApiResponseVO transferXfsgStore(XfsgTransferStoreDTO xfsgTransferStoreDTO);

}
