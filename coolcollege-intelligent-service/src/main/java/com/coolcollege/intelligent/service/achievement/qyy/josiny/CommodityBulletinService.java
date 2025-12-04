package com.coolcollege.intelligent.service.achievement.qyy.josiny;


import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.CommodityBulletinListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.CommodityBulletinListRes;

public interface CommodityBulletinService {


    CommodityBulletinListRes commodityBulletinList(String enterpriseId, CommodityBulletinListReq req);
}
