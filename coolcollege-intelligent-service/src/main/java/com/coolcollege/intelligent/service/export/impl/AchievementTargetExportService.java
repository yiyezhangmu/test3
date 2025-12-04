package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.songxia.SongXiaEnterpriseEnum;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.achievement.request.AchievementTargetExportRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetExportVO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthBaseVisualDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业绩目标导出
 *
 * @author chenyupeng
 * @since 2022/2/25
 */
@Service
public class AchievementTargetExportService implements BaseExportService {

    @Autowired
    private AuthVisualService authVisualService;

    @Autowired
    private StoreMapper storeMapper;

    private static final String TITLE = "注意事项：\n" +
            "1、请勿擅自修改导入模板的表头字段，否则会导致导入失败！\n" +
            "2、模板默认会填入关联门店，下载模板后填写门店每月业绩目标即可。\n" +
            "3、填写业绩目标时，请填写大于0的数字；若有小数，请保留小数点后两位；若未填写，门店当月目标不变（不会识别）。\n" +
            "4、业绩目标导入按门店ID查重，检测到门店ID重复数据会覆盖，若未填写内容，将不录入。\n" +
            "5、每次最多导入2万条数据。";

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        AchievementTargetExportRequest exportRequest = (AchievementTargetExportRequest)request;

        if(Role.MASTER.getRoleEnum().equals(exportRequest.getUser().getSysRoleDO().getRoleEnum())){
            return (long)storeMapper.countAllStore(enterpriseId);
        }else {
            AuthBaseVisualDTO baseVisualDTO = authVisualService.baseAuth(enterpriseId, exportRequest.getUser().getUserId());
            if(CollectionUtils.isEmpty(baseVisualDTO.getStoreIdList()) && CollectionUtils.isEmpty(baseVisualDTO.getRegionIdList())){
                return 0L;
            }
            return storeMapper.countByRegionPathListOrStoreIds(enterpriseId, baseVisualDTO.getStoreIdList(), baseVisualDTO.getFullRegionPathList());
        }
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_ACHIEVEMENT_TARGET;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {


        AchievementTargetExportRequest exportRequest = JSONObject.toJavaObject(request, AchievementTargetExportRequest.class);
        List<StoreDO> storeDOList = new ArrayList<>();

        if(Role.MASTER.getRoleEnum().equals(exportRequest.getUser().getSysRoleDO().getRoleEnum())){
            PageHelper.startPage(pageNum,pageSize);
            if (SongXiaEnterpriseEnum.songXiaCompany(enterpriseId)){
                storeDOList = storeMapper.getSongxiaAllStore(enterpriseId);
            }else {
                storeDOList = storeMapper.getAllStore(enterpriseId);
            }


        }else {
            AuthBaseVisualDTO baseVisualDTO = authVisualService.baseAuth(enterpriseId, exportRequest.getUser().getUserId());
            if(CollectionUtils.isEmpty(baseVisualDTO.getStoreIdList()) && CollectionUtils.isEmpty(baseVisualDTO.getRegionIdList())){
                return new ArrayList<>();
            }
            PageHelper.startPage(pageNum,pageSize);
            if (SongXiaEnterpriseEnum.songXiaCompany(enterpriseId)){
                storeDOList = storeMapper.getSongXiaByRegionPathListOrStoreIds(enterpriseId,baseVisualDTO.getStoreIdList(), baseVisualDTO.getFullRegionPathList());
            }else {
                storeDOList = storeMapper.getByRegionPathListOrStoreIds(enterpriseId,baseVisualDTO.getStoreIdList(), baseVisualDTO.getFullRegionPathList());
            }
        }
        if(CollectionUtils.isEmpty(storeDOList)){
            return new ArrayList<>();
        }

        return storeDOList.stream().map(e->{
            AchievementTargetExportVO vo = new AchievementTargetExportVO();
            vo.setStoreName(e.getStoreName());
            vo.setStoreId(e.getStoreId());
            vo.setStoreNum(e.getStoreNum());
            vo.setYear(DateUtil.getSysYear());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
