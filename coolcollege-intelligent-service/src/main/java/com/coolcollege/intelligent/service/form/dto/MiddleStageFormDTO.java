package com.coolcollege.intelligent.service.form.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/07/20
 */
@Data
public class MiddleStageFormDTO {
    private String biz_code;
    private JSONObject x_biz;
    private String type;
    private String primary_key;
    private String cid;
    private JSONObject data;
}
