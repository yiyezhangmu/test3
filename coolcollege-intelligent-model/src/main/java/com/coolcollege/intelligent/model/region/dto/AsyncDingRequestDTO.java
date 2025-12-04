package com.coolcollege.intelligent.model.region.dto;

import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import lombok.Data;


/**
 * @author byd
 */
@Data
public class AsyncDingRequestDTO {

    private String userName;

    private String eid;

    private EnterpriseSettingVO enterpriseSettingVO;

    private String dingCorpId;

    private String dbName;

    private String userId;

    private String appType;

    private Long regionId;

}
