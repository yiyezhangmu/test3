package com.coolcollege.intelligent.model.setting.request;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/22
 */
@Data
public class EnterpriseNoticeSettingRequest {

    private String personGroupId;
    private List<Long> roleIdList;
}
