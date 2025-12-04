package com.coolcollege.intelligent.facade.open.api;

import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: DemoApi
 * @Description:
 * @date 2022-07-06 14:00
 */
public interface DemoApi {

    OpenApiResponseVO test01(String appType, String appId);


    OpenApiResponseVO test02(EnterpriseLoginDTO param);


    OpenApiResponseVO test03(String enterpriseId, EnterpriseLoginDTO param);



    @Data
    class EnterpriseLoginDTO {
        private String enterpriseId;
        private String userId;
    }

}
