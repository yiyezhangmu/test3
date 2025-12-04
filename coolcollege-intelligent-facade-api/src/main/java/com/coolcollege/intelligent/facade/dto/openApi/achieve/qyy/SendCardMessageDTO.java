package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: SendCardMessageDTO
 * @Description:发送卡片消息
 * @date 2023-04-19 19:42
 */
@Data
public class SendCardMessageDTO {

    public static final String RULE = "rule";

    public static final String SPECIFY = "specify";

    public static final String STORE = "store";
    public static final String REGION = "region";
    public static final String OTHER = "other";

    public static final String ALL = "all";

    public static final String SELECTED = "selected";

    public static final String SYS_FULL_JSON_OBJ = "sys_full_json_obj";

    /**
     * rule/specify
     */
    private String targetType;

    private List<TargetValue> targetValue;

    private JSONObject cardData;

    /**
     * 卡片接收人
     */
    private List<String> receiveUserIdList;


    @Data
    public static class TargetValue{

        // store/region/other
        private String type;

        // all/selected
        private String mode;

        // 当mode不是all 时，取values的值
        private List<String> values;

        public TargetValue(String type, String mode, List<String> values) {
            this.type = type;
            this.mode = mode;
            this.values = values;
        }
    }

}
