package com.coolcollege.intelligent.model.enterprise.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/11/22
 */
@Data
public class EnterpriseStoreCheckNewDTO {

    /**
     * 字段名
     */
    private List<String> fieldNameList;
    /**
     * 巡店设置
     */
    private EnterpriseStoreCheckRequestNewDTO storeCheckSetting;
}
