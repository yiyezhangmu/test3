package com.coolcollege.intelligent.service.achievement.qyy.josiny;

import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.RegionTopListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.RegionTopListRes;

import java.util.List;

public interface RegionTopService {

    List<RegionTopListRes> regionTopList(String enterpriseId, RegionTopListReq req);
}
