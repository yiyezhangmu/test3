package com.coolcollege.intelligent.controller.qyy;

import com.aliyun.tea.*;

public class DingCardTokenController {

    /**
     * 使用 Token 初始化账号Client
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dingtalkoauth2_1_0.Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

    public static void main(String[] args_) throws Exception {
        java.util.List<String> args = java.util.Arrays.asList(args_);
        com.aliyun.dingtalkoauth2_1_0.Client client = DingCardTokenController.createClient();
        com.aliyun.dingtalkoauth2_1_0.models.GetCorpAccessTokenRequest getCorpAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetCorpAccessTokenRequest()
                .setSuiteKey("suitenafp67wwvrszqyuy")
                .setSuiteSecret("_s60vwrB0dmrxhGPq9XQ08QxbndjuUOj5G6Vd8sODoreNWIDVEbSX1f4_KE_qmmD")
                .setAuthCorpId("asd")
                .setSuiteTicket("asd");
        try {
            client.getCorpAccessToken(getCorpAccessTokenRequest);
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }

        }
    }
}