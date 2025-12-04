package com.coolcollege.intelligent.model.setting.vo;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/22
 */
@Data
public class EnterpriseNoticeSettingVO {
    private String personGroupId;
    private String personGroupName;
    private List<EnterpriseNoticeRoleVO> roleVOList;

}
