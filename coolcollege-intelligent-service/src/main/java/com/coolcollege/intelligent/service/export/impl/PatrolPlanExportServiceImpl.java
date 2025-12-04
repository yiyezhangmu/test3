package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.patrol.PatrolPlanStatusEnum;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.patrolstore.request.PatrolPlanPageRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.PatrolPlanPageVO;
import com.coolcollege.intelligent.model.store.vo.StoreSignInfoVO;
import com.coolcollege.intelligent.model.unifytask.request.StoreTaskReportListRequest;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.patrolstore.PatrolPlanService;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: hu hu
 * @Date: 2025/2/7 13:54
 * @Description:
 */
@Service
public class PatrolPlanExportServiceImpl implements BaseExportService {

    @Resource
    private PatrolPlanService patrolPlanService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return Constants.MAX_EXPORT_SIZE;
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.PATROL_PLAN_EXPORT;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        PatrolPlanPageRequest param = JSONObject.toJavaObject(request, PatrolPlanPageRequest.class);
        param.setPageNum(pageNum);
        param.setPageSize(pageSize);
        PageInfo<PatrolPlanPageVO> pageInfo = patrolPlanService.getPatrolPlanPage(enterpriseId, param);
        if (CollectionUtils.isEmpty(pageInfo.getList())) {
            return new ArrayList<>();
        }
        return pageInfo.getList();
    }
}
