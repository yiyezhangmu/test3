package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.newstore.dao.NsStoreDao;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.export.request.NsStoreListExportRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreListRequest;
import com.coolcollege.intelligent.model.newstore.vo.NsStoreVO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.newstore.NsStoreService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.List;

/**
 * 新店列表导出
 * @author zhangnan
 * @date 2022-03-09 16:34
 */
@Service
public class NsStoreListExportServiceImpl implements BaseExportService {

    @Resource
    private NsStoreDao nsStoreDao;
    @Resource
    private NsStoreService nsStoreService;
    @Resource
    private RegionService regionService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        NsStoreListExportRequest exportRequest = (NsStoreListExportRequest)request;
        return nsStoreDao.selectStoreCount(enterpriseId, exportRequest);
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_NEW_STORE_LIST;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        NsStoreListRequest queryRequest = JSONObject.toJavaObject(request, NsStoreListRequest.class);
        queryRequest.setPageNum(pageNum);
        queryRequest.setPageSize(pageSize);
        PageInfo<NsStoreVO> pageInfo = nsStoreService.getNsStoreList(enterpriseId, queryRequest);
        if(CollectionUtils.isEmpty(pageInfo.getList())) {
            return pageInfo.getList();
        }
        for (NsStoreVO nsStoreVO : pageInfo.getList()) {
            if(StringUtils.isNotBlank(nsStoreVO.getAvatar())) {
                nsStoreVO.setAvatar(nsStoreVO.getAvatar().replace(Constants.COMMA, Constants.BR));
            }
            // 全路径
            nsStoreVO.setRegionName(regionService.getAllRegionName(enterpriseId, nsStoreVO.getRegionId()).getAllRegionName());
            nsStoreVO.setLocationAddress(MessageFormat.format(LOCATION_ADDRESS, nsStoreVO.getLocationAddress(),
                    nsStoreVO.getLongitude(), nsStoreVO.getLatitude()));
        }
        return pageInfo.getList();
    }

    public static final String LOCATION_ADDRESS = "{0}\n经度：{1}；维度：{2}；";
}
