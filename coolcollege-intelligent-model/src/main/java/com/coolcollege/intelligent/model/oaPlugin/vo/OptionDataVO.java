package com.coolcollege.intelligent.model.oaPlugin.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;


@Data
public class OptionDataVO {

    public Boolean success = true;

    public String errorCode;

    public String errorMessage;

    public JSONObject data;

    @Data
    public static class InitOptionData{
        public String bizAlias;
        private Prop props;
    }

    @Data
    public static class Prop{
        public String placeholder;
        public String label;
        public List<Option> options;
    }

    @Data
    public static class Option{
        public String key;
        public String value;
    }
}
