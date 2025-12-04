package com.coolcollege.intelligent.service.achievement.qyy.josiny;

import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.TargetListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.TargetListRes;

public interface PushTargetService {

    TargetListRes targetList(String enterpriseId, TargetListReq req);
}
