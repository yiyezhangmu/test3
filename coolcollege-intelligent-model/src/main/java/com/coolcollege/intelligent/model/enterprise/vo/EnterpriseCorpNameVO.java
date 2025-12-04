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
public class EnterpriseCorpNameVO {

    /**
     * 门店数量
     */
    private Integer storeCount;

    /**
     * corpid
     */
    private String corpId;

    private String corpName;

    public EnterpriseCorpNameVO(Integer storeCount, String corpId, String corpName) {
        this.storeCount = storeCount;
        this.corpId = corpId;
        this.corpName = corpName;
    }
}
