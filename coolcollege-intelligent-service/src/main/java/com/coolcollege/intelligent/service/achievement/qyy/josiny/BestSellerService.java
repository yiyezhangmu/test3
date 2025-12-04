package com.coolcollege.intelligent.service.achievement.qyy.josiny;

import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.BestSellerListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.BestSellerRes;

public interface BestSellerService {
    BestSellerRes BestSellerList(String enterpriseId, BestSellerListReq req);
}
