package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.patrolstore.dao.TbPatrolPlanDetailDao;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolPlanPageRequest;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: hu hu
 * @Date: 2025/2/7 13:54
 * @Description:
 */
@Service
public class PatrolPlanDetailExportServiceImpl implements BaseExportService {

    @Resource
    private TbPatrolPlanDetailDao tbPatrolPlanDetailDao;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return Constants.MAX_EXPORT_SIZE;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.PATROL_PLAN_DETAIL_EXPORT;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        PatrolPlanPageRequest param = JSONObject.toJavaObject(request, PatrolPlanPageRequest.class);
        PageHelper.startPage(pageNum, pageSize);
        return tbPatrolPlanDetailDao.getPatrolPlanDetailExportList(enterpriseId, param);
    }
}
