package com.coolcollege.intelligent.controller.form.request;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * describe:表单中台的传入参数
 *
 * @author zhouyiping
 * @date 2020/07/20
 */
@Data
public class FormRequest {

    private String biz_code;
    private JSONObject x_biz;
    private String type;
    private String primary_key;
    private String cid;
    private JSONObject data;
}
