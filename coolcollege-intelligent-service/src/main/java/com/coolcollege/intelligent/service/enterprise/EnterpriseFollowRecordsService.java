package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseFollowRecordsDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseFollowRecordsRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseFollowRecordsVO;
import com.coolcollege.intelligent.model.system.dto.BossLoginUserDTO;
import com.coolcollege.intelligent.model.userholder.BossUserHolder;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

/**
 * @author chenyupeng
 * @since 2021/11/24
 */
public interface EnterpriseFollowRecordsService {
    EnterpriseFollowRecordsDTO saveEnterpriseFollowRecords(EnterpriseFollowRecordsDTO dto, BossLoginUserDTO user);

    void updateEnterpriseFollowRecords(EnterpriseFollowRecordsDTO dto, BossLoginUserDTO user);

    void deleteEnterpriseFollowRecords(Long id);

    PageInfo<EnterpriseFollowRecordsVO> listEnterpriseFollowRecords(EnterpriseFollowRecordsRequest request);
}
