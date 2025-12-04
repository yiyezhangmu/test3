package com.coolcollege.intelligent.model.setting.vo;

import lombok.Data;

/**
 * @author byd
 * @date 2021-06-21 19:17
 */
@Data
public class StoreCheckSettingLevelVO {

    private Integer percent;

    private String keyName;

    private Integer qualifiedNum;

    public String getKeyName() {
        if("excellent".equals(this.keyName)){
            return "优秀";
        }else if("good".equals(this.keyName)){
            return "良好";
        }else if("eligible".equals(this.keyName)){
            return "合格";
        } else {
            return "不合格";
        }
    }
}
