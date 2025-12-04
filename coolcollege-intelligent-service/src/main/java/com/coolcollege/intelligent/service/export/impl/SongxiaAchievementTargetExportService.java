package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.songxia.SongXiaEnterpriseEnum;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.achievement.AchievementTargetDetailMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDetailDO;
import com.coolcollege.intelligent.model.achievement.request.AchievementTargetExportRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTargetSongXiaExportVO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.region.dto.AuthBaseVisualDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SongxiaAchievementTargetExportService implements BaseExportService {

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private AuthVisualService authVisualService;

    @Resource
    private AchievementTargetDetailMapper achievementTargetDetailMapper;


    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        AchievementTargetExportRequest exportRequest = (AchievementTargetExportRequest) request;

        if (Role.MASTER.getRoleEnum().equals(exportRequest.getUser().getSysRoleDO().getRoleEnum())) {
            return (long) storeMapper.countAllStore(enterpriseId);
        } else {
            AuthBaseVisualDTO baseVisualDTO = authVisualService.baseAuth(enterpriseId, exportRequest.getUser().getUserId());
            if (CollectionUtils.isEmpty(baseVisualDTO.getStoreIdList()) && CollectionUtils.isEmpty(baseVisualDTO.getRegionIdList())) {
                return 0L;
            }
            return storeMapper.countByRegionPathListOrStoreIds(enterpriseId, baseVisualDTO.getStoreIdList(), baseVisualDTO.getFullRegionPathList());
        }
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.SONGXIA_EXPORT_ACHIEVEMENT_TARGET;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {

        AchievementTargetExportRequest exportRequest = JSONObject.toJavaObject(request, AchievementTargetExportRequest.class);
        List<StoreDO> storeDOList = new ArrayList<>();

        if (Role.MASTER.getRoleEnum().equals(exportRequest.getUser().getSysRoleDO().getRoleEnum())) {
            PageHelper.startPage(pageNum, pageSize);
            storeDOList = storeMapper.getAllStore(enterpriseId);

        } else {
            AuthBaseVisualDTO baseVisualDTO = authVisualService.baseAuth(enterpriseId, exportRequest.getUser().getUserId());
            if (CollectionUtils.isEmpty(baseVisualDTO.getStoreIdList()) && CollectionUtils.isEmpty(baseVisualDTO.getRegionIdList())) {
                return Collections.emptyList();
            }
            PageHelper.startPage(pageNum, pageSize);
            storeDOList = storeMapper.getByRegionPathListOrStoreIds(enterpriseId, baseVisualDTO.getStoreIdList(), baseVisualDTO.getFullRegionPathList());
        }
        if (CollectionUtils.isEmpty(storeDOList)) {
            return Collections.emptyList();
        }

        List<AchievementTargetSongXiaExportVO> resultList = storeDOList.parallelStream().map(e -> {
            AchievementTargetSongXiaExportVO vo = new AchievementTargetSongXiaExportVO();
            vo.setStoreName(e.getStoreName());
            vo.setStoreId(e.getStoreId());
            vo.setStoreNum(e.getStoreNum());
            vo.setYear(exportRequest.getAchievementYear());
            vo.setStatus(e.getStoreStatus());
            return vo;
        }).collect(Collectors.toList());

        List<String> storeIds = storeDOList.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
        List<AchievementTargetDetailDO> targetByStores = achievementTargetDetailMapper.getTargetByStores(enterpriseId, storeIds,exportRequest.getAchievementYear());
        Map<String, List<AchievementTargetDetailDO>> achieveTargetMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(targetByStores)) {
            achieveTargetMap = targetByStores.stream().collect(Collectors.groupingBy(AchievementTargetDetailDO::getStoreId));
        }
        Map<String, List<AchievementTargetDetailDO>> finalAchieveTargetMap = achieveTargetMap;
        resultList.forEach(achievementTargetExportVO -> {
            List<AchievementTargetDetailDO> achievementTargetList = finalAchieveTargetMap.get(achievementTargetExportVO.getStoreId());
            if (CollectionUtils.isNotEmpty(achievementTargetList)) {
                achievementTargetList.forEach(target -> {
                    LocalDate localDate = target.getBeginDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    int month = localDate.getMonthValue();
                    switch (month) {
                        case 1:
                            achievementTargetExportVO.setJanuary(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 2:
                            achievementTargetExportVO.setFebruary(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 3:
                            achievementTargetExportVO.setMarch(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 4:
                            achievementTargetExportVO.setApril(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 5:
                            achievementTargetExportVO.setMay(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 6:
                            achievementTargetExportVO.setJune(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 7:
                            achievementTargetExportVO.setJuly(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 8:
                            achievementTargetExportVO.setAugust(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 9:
                            achievementTargetExportVO.setSeptember(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 10:
                            achievementTargetExportVO.setOctober(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 11:
                            achievementTargetExportVO.setNovember(String.valueOf(target.getAchievementTarget()));
                            break;
                        case 12:
                            achievementTargetExportVO.setDecember(String.valueOf(target.getAchievementTarget()));
                            break;
                        default:
                            break;
                    }
                });
            }
        });
        return resultList;
    }
}
