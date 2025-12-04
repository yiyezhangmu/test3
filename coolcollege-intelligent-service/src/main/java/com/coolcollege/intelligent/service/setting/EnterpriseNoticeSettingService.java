package com.coolcollege.intelligent.service.setting;

import com.coolcollege.intelligent.model.setting.request.EnterpriseNoticeSettingRequest;
import com.coolcollege.intelligent.model.setting.vo.EnterpriseNoticeSettingVO;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/02/22
 */
public interface EnterpriseNoticeSettingService {
    /**
     * 查看企业布控通知配置
     * @param eid
     * @return
     */
    List<EnterpriseNoticeSettingVO> listEnterpriseNotice(String eid);

    /**
     * 保存或更新企业布控通知配置
     * @param eid
     * @param request
     * @return
     */
    Boolean saveOrUpdateEnterpriseNotice(String eid,List<EnterpriseNoticeSettingRequest> requestList);
}
