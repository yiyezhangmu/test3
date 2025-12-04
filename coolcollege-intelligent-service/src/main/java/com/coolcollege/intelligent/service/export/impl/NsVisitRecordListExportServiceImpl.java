package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.newstore.dao.NsVisitRecordDao;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.NsVisitRecordListExportRequest;
import com.coolcollege.intelligent.model.newstore.request.NsVisitRecordListRequest;
import com.coolcollege.intelligent.model.newstore.vo.NsVisitRecordListVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.newstore.NsVisitRecordService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * 新店拜访记录表导出
 * @author zhangnan
 * @date 2022-03-09 16:34
 */
@Service
public class NsVisitRecordListExportServiceImpl implements BaseExportService {

    @Resource
    private NsVisitRecordDao nsVisitRecordDao;
    @Resource
    private NsVisitRecordService nsVisitRecordService;
    @Resource
    private RegionService regionService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        NsVisitRecordListExportRequest exportRequest = (NsVisitRecordListExportRequest)request;
        return nsVisitRecordDao.selectVisitRecordCount(enterpriseId, exportRequest);
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_VISIT_RECORD_LIST;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        NsVisitRecordListRequest queryRequest = JSONObject.toJavaObject(request, NsVisitRecordListRequest.class);
        queryRequest.setPageNum(pageNum);
        queryRequest.setPageSize(pageSize);
        PageInfo<NsVisitRecordListVO> pageInfo = nsVisitRecordService.getRecordList(enterpriseId, queryRequest);
        if(CollectionUtils.isEmpty(pageInfo.getList())) {
            return pageInfo.getList();
        }
        for (NsVisitRecordListVO visitRecordListVO : pageInfo.getList()) {
            // 全路径区域
            visitRecordListVO.setRegionName(regionService.getAllRegionName(enterpriseId, visitRecordListVO.getRegionId()).getAllRegionName());
            String[] storeLongitudeLatitude = StringUtils.split(visitRecordListVO.getNewStoreLongitudeLatitude(), Constants.COMMA);
            visitRecordListVO.setNewStoreLocationAddress(MessageFormat.format(LOCATION_ADDRESS, visitRecordListVO.getNewStoreLocationAddress(),
                    storeLongitudeLatitude[0], storeLongitudeLatitude[1]));
            if(StringUtils.isBlank(visitRecordListVO.getSignInLongitudeLatitude())) {
                continue;
            }
            String[] signInLongitudeLatitude = StringUtils.split(visitRecordListVO.getSignInLongitudeLatitude(), Constants.COMMA);
            visitRecordListVO.setSignInAddress(MessageFormat.format(LOCATION_ADDRESS, visitRecordListVO.getSignInAddress(),
                    signInLongitudeLatitude[0], signInLongitudeLatitude[1]));
        }
        return pageInfo.getList();
    }

    public static final String LOCATION_ADDRESS = "{0}\n经度：{1}；维度：{2}；";
}
