package com.coolcollege.intelligent.service.export.impl;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.user.dto.UserPersonnelStatusHistoryExportRequest;
import com.coolcollege.intelligent.model.user.dto.UserPersonnelStatusHistoryReportDTO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusHistoryReportVO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusHistoryVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.userstatus.UserPersonnelStatusHistoryService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/9 10:51
 */
@Service
@Slf4j
public class UserPersonnelStatusHistoryExportService implements BaseExportService  {

    @Autowired
    private UserPersonnelStatusHistoryService userPersonnelStatusHistoryService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        UserPersonnelStatusHistoryExportRequest statusRequest = (UserPersonnelStatusHistoryExportRequest) request;
        return (long) statusRequest.getAllUserIdList().size();
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_USER_PERSONNEL_STATUS_HISTORY;
    }

    @Override
    public List<Map<String, Object>> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        UserPersonnelStatusHistoryExportRequest statusRequest = JSONObject.toJavaObject(request, UserPersonnelStatusHistoryExportRequest.class);
        UserPersonnelStatusHistoryReportDTO query = statusRequest.getQuery();
        List<Date> dateList = DateUtils.getBetweenDate(query.getStartTime(), query.getEndTime());
        DateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_DAY);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        if (CollectionUtils.isEmpty(statusRequest.getAllUserIdList())) {
            return mapList;
        }
        PageInfo<UserPersonnelStatusHistoryReportVO> pageInfo = userPersonnelStatusHistoryService.
                getStatusHistoryReport(enterpriseId, statusRequest.getAllUserIdList(), statusRequest.getQuery());
        List<UserPersonnelStatusHistoryReportVO> historyList = pageInfo.getList();
        if (CollectionUtils.isEmpty(historyList)) {
            return new ArrayList<>();
        }
        historyList.forEach(his -> {
            Map<String, Object> params = new HashMap<>();
            params.put("userName", his.getUserName());
            List<UserPersonnelStatusHistoryVO> statusHistoryList = his.getStatusHistoryList();
            Map<String, UserPersonnelStatusHistoryVO> statusMap = ListUtils.emptyIfNull(statusHistoryList).stream()
                    .collect(Collectors.toMap(UserPersonnelStatusHistoryVO::getEffectiveTime, data -> data, (a, b) -> a));
            dateList.forEach(date -> {
                String effectiveTimeStr = dateFormat.format(date);
                UserPersonnelStatusHistoryVO history = statusMap.get(effectiveTimeStr);
                String statusValue = Constants.PERSONNEL_STATUS_NORMAL;
                if (history != null && !statusValue.equals(history.getStatusName())) {
                    statusValue = history.getStatusName();
                    if (StringUtils.isNotBlank(history.getRemarks())) {
                        statusValue = statusValue + "(" + history.getRemarks() + ")";
                    }
                }
                params.put(effectiveTimeStr, statusValue);
            });
            mapList.add(params);
        });
        return mapList;
    }

    @Override
    public List<ExcelExportEntity> exportFields(JSONObject request) {
        AtomicInteger orderNum = new AtomicInteger(1);
        List<ExcelExportEntity> beanList = new ArrayList<>();
        UserPersonnelStatusHistoryExportRequest statusRequest = JSONObject.toJavaObject(request, UserPersonnelStatusHistoryExportRequest.class);
        UserPersonnelStatusHistoryReportDTO query = statusRequest.getQuery();
        List<Date> dateList = DateUtils.getBetweenDate(query.getStartTime(), query.getEndTime());
        DateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_DAY);
        //开始组装导出列
        ExcelExportEntity userName = new ExcelExportEntity("姓名", "userName");
        userName.setOrderNum(orderNum.getAndIncrement());
        beanList.add(userName);
        dateList.forEach(da -> {
            String effectiveTimeStr = dateFormat.format(da);
            ExcelExportEntity effectiveTime = new ExcelExportEntity(effectiveTimeStr, effectiveTimeStr);
            effectiveTime.setOrderNum(orderNum.getAndIncrement());
            beanList.add(effectiveTime);
        });
        return beanList;
    }
}
