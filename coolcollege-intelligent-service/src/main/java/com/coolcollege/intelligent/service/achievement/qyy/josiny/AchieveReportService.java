package com.coolcollege.intelligent.service.achievement.qyy.josiny;

import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.AchieveReportDDListReq;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.AchieveReportListReq;
import com.coolcollege.intelligent.model.achievement.qyy.dto.josiny.achieveReportProductListReq;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.AchieveReportDDListRes;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.AchieveReportListRes;
import com.coolcollege.intelligent.model.achievement.qyy.vo.josiny.AchieveReportProductListRes;

import java.util.List;

public interface AchieveReportService {

    AchieveReportListRes achieveReportList(String enterpriseId, AchieveReportListReq req);

    List<AchieveReportDDListRes> achieveReportDDList(String enterpriseId, AchieveReportDDListReq req);

    List<AchieveReportProductListRes> achieveReportProductList(String enterpriseId, achieveReportProductListReq req);
}
