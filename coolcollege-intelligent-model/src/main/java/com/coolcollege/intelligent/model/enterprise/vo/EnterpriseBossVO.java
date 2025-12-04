package com.coolcollege.intelligent.model.enterprise.vo;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseBossDTO;
import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/11/13
 */
@Data
public class EnterpriseBossVO extends EnterpriseBossDTO {

    /**
     * 开通类型
     */
    private String openType;

    /**
     * corpid
     */
    private String corpId;

    //留资按钮开关
    private Boolean isRetainCapitalFlag = true;

}
