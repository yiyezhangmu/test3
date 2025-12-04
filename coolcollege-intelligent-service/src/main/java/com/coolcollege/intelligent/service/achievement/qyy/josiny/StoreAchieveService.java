package com.coolcollege.intelligent.service.achievement.qyy.josiny;

import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.StoreAchieveListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.StoreAchieveListRes;

import java.util.List;

public interface StoreAchieveService {

    List<StoreAchieveListRes> StoreAchieveList(String enterpriseId, StoreAchieveListReq req);

}
