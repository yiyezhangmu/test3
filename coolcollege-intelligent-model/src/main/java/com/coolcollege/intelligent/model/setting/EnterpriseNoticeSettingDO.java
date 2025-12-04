package com.coolcollege.intelligent.model.setting;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/22
 */
@Data
public class EnterpriseNoticeSettingDO {
    private Long id;
    private String enterpriseId;
    private String personGroupId;
    private String roleIdStr;

}
