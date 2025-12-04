package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.common.enums.enterprise.AuthLevelEnum;
import com.coolcollege.intelligent.common.enums.enterprise.BossCluesSalesStageEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseStatusEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseVipTypeEnum;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseCluesMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskStoreMapper;
import com.coolcollege.intelligent.model.boss.request.BossEnterpriseExportRequest;
import com.coolcollege.intelligent.model.enterprise.EnterpriseCluesDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseBossDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseCluesDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseCluesRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseCluesExportVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseCluesVO;
import com.coolcollege.intelligent.model.impoetexcel.dto.EnterpriseCluesImportDTO;
import com.coolcollege.intelligent.model.system.dto.BossLoginUserDTO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseCluesService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolstore.base.enums.AppTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author chenyupeng
 * @since 2021/11/23
 */
@Service
@Slf4j
public class EnterpriseCluesServiceImpl implements EnterpriseCluesService {

    @Resource
    EnterpriseCluesMapper enterpriseCluesMapper;

    @Autowired
    private ExportUtil exportUtil;

    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor queryExecutor;

    @Resource
    private TaskStoreMapper taskStoreMapper;

    @Resource
    private EnterpriseMapper enterpriseMapper;

    @Override
    public EnterpriseCluesDTO saveEnterpriseClues(EnterpriseCluesDTO dto, BossLoginUserDTO user) {
        EnterpriseCluesDO enterpriseCluesDO = new EnterpriseCluesDO();
        BeanUtils.copyProperties(dto,enterpriseCluesDO);
        enterpriseCluesDO.setCreateId(user.getUserId());
        enterpriseCluesDO.setCreateName(user.getName());
        enterpriseCluesDO.setCreateTime(new Date());
        enterpriseCluesDO.setUpdateId(user.getUserId());
        enterpriseCluesDO.setUpdateName(user.getName());
        enterpriseCluesDO.setUpdateTime(new Date());
        enterpriseCluesMapper.save(enterpriseCluesDO);
        dto.setId(enterpriseCluesDO.getId());
        return dto;
    }

    @Override
    public void updateEnterpriseClues(EnterpriseCluesDTO dto, BossLoginUserDTO user) {
        EnterpriseCluesDO enterpriseCluesDO = new EnterpriseCluesDO();
        enterpriseCluesDO.setId(dto.getId());
        enterpriseCluesDO.setUserIds(dto.getUserIds());
        enterpriseCluesDO.setSalesStage(dto.getSalesStage());
        enterpriseCluesDO.setAppType(dto.getAppType());
        enterpriseCluesDO.setIsPay(dto.getIsPay());
        enterpriseCluesDO.setUpdateId(user.getUserId());
        enterpriseCluesDO.setUpdateName(user.getName());
        enterpriseCluesDO.setUpdateTime(new Date());
        enterpriseCluesMapper.update(enterpriseCluesDO);
    }

    @Override
    public void deleteEnterpriseClues(Long id) {
        enterpriseCluesMapper.deleteById(id);
    }

    @Override
    public PageInfo<EnterpriseCluesVO> listEnterpriseClues(EnterpriseCluesRequest request, BossLoginUserDTO user) {
        request.setUserId(user.getId());
        PageHelper.startPage(request.getPageNum(),request.getPageSize());
        List<EnterpriseCluesDTO> enterpriseCluesDTOS =  enterpriseCluesMapper.list(request);
        PageInfo<EnterpriseCluesVO> pageInfo = new PageInfo(enterpriseCluesDTOS);
        List<EnterpriseCluesVO> resultList = ListUtils.emptyIfNull(enterpriseCluesDTOS).stream()
                .map(e -> {
                    EnterpriseCluesVO vo = new EnterpriseCluesVO();
                    BeanUtils.copyProperties(e,vo);
                    String province = e.getProvince() == null ? "" : e.getProvince();
                    String city = e.getCity() == null ? "" : e.getCity();
                    vo.setProvinceCity(province + city);
                    vo.setEnterpriseId(e.getEnterpriseId());
                    vo.setName(e.getName());
                    vo.setStatus(e.getStatus());
                    vo.setMainCorpId(e.getMainCorpId());
                    vo.setAppType(e.getAppType());
                    vo.setIsVip(e.getIsVip());
                    vo.setIsAuthenticated(e.getIsAuthenticated());
                    vo.setAuthLevel(e.getAuthLevel());
                    vo.setPackageBeginDate(e.getPackageBeginDate());
                    vo.setPackageEndDate(e.getPackageEndDate());
                    vo.setIsPay(e.getIsPay());
                    if(StringUtils.isNotBlank(e.getDbName())){
                        vo.setDbNameNum(StringUtils.remove(e.getDbName(), "coolcollege_intelligent_"));
                    }
                    return vo;
                }).collect(Collectors.toList());
        pageInfo.setList(resultList);
        return pageInfo;
    }

    @Override
    public Integer importEnterpriseClues(Future<List<EnterpriseCluesImportDTO>> importTask, String originalFilename, BossLoginUserDTO user) {

        List<EnterpriseCluesImportDTO> importList = null;
        try {
            importList = importTask.get();
        }catch (Exception e){
            log.info("importEnterpriseClues：解析导入文件失败");
        }
        if(CollectionUtils.isEmpty(importList)){
            log.info("importEnterpriseClues：导入文件为空");
            return 0;
        }
        return importHandel(importList,user);
    }

    public Integer importHandel(List<EnterpriseCluesImportDTO> importList,BossLoginUserDTO user){
        //查出所有线索
        List<EnterpriseCluesDTO> enterpriseCluesDTOS =  enterpriseCluesMapper.list(new EnterpriseCluesRequest());
        Map<String, EnterpriseCluesDTO> nameCollect = ListUtils.emptyIfNull(enterpriseCluesDTOS).stream().
                collect(Collectors.toMap(EnterpriseCluesDTO::getName, data -> data, (a, b) -> a));

        List<EnterpriseCluesDO> enterpriseCluesDOS = new ArrayList<>();
        EnterpriseCluesDO temoDO;
        Date now = new Date();
        for (EnterpriseCluesImportDTO dto : importList) {
            if(StringUtils.isBlank(dto.getName())){
                continue;
            }
            temoDO = new EnterpriseCluesDO();
            temoDO.setCreateTime(now);
            temoDO.setCreateId(String.valueOf(user.getId()));
            temoDO.setCreateName(user.getName());
            temoDO.setUpdateTime(now);
            temoDO.setUpdateId(String.valueOf(user.getId()));
            temoDO.setUpdateName(user.getName());

            if(nameCollect.get(dto.getName()) != null){
                BeanUtils.copyProperties(nameCollect.get(dto.getName()),temoDO);
            }else {
                temoDO.setUserIds(String.valueOf(user.getId()));
            }
            temoDO.setName(dto.getName());
            if(dto.getStoreNum() != null){
                temoDO.setStoreNum(dto.getStoreNum());
            }
            if(StringUtils.isNotBlank(dto.getEnterpriseId())){
                temoDO.setEnterpriseId(dto.getEnterpriseId());
            }
            if(StringUtils.isNotBlank(dto.getMobile())){
                temoDO.setMobile(dto.getMobile());
            }
            if(StringUtils.isNotBlank(dto.getProvince())){
                temoDO.setProvince(dto.getProvince());
            }
            if(StringUtils.isNotBlank(dto.getCity())){
                temoDO.setCity(dto.getCity());
            }
            if(StringUtils.isNotBlank(dto.getAppType())){
                temoDO.setAppType(AppTypeEnum.getCode(dto.getAppType()));
            }
            if(StringUtils.isNotBlank(dto.getIsVip())){
                temoDO.setIsVip(EnterpriseVipTypeEnum.getCode(dto.getIsVip()));
            }
            if(StringUtils.isNotBlank(dto.getIndustry())){
                temoDO.setIndustry(dto.getIndustry());
            }
            if(StringUtils.isNotBlank(dto.getAuthLevel())){
                temoDO.setAuthLevel(AuthLevelEnum.getCode(dto.getAuthLevel()));
                temoDO.setIsAuthenticated(AuthLevelEnum.getCode(dto.getAuthLevel()) > 0);
            }
            if(StringUtils.isNotBlank(dto.getSalesStage())){
                temoDO.setSalesStage(BossCluesSalesStageEnum.getCode(dto.getSalesStage()));
            }
            if(StringUtils.isNotBlank(dto.getContact())){
                temoDO.setContact(dto.getContact());
            }
            if(StringUtils.isNotBlank(dto.getPackageBeginDate())){
                temoDO.setPackageBeginDate(DateUtil.parse(dto.getPackageBeginDate(),"yyyy-MM-dd"));
            }
            if(StringUtils.isNotBlank(dto.getPackageEndDate())){
                temoDO.setPackageEndDate(DateUtil.parse(dto.getPackageEndDate(),"yyyy-MM-dd"));
            }
            if(StringUtils.isNotBlank(dto.getIsPay())){
                temoDO.setIsPay("是".equals(dto.getIsPay()));
            }

            enterpriseCluesDOS.add(temoDO);
        }
        return enterpriseCluesMapper.batchSave(enterpriseCluesDOS);
    }

    @Override
    public List<EnterpriseCluesExportVO> exportEnterpriseClues(EnterpriseCluesRequest request) {

        List<EnterpriseCluesDTO> enterpriseCluesDTOS =  enterpriseCluesMapper.list(request);
        List<EnterpriseCluesExportVO> resultList = ListUtils.emptyIfNull(enterpriseCluesDTOS).stream()
                .map(e -> {
                    EnterpriseCluesExportVO vo = new EnterpriseCluesExportVO();
                    vo.setName(e.getName());
                    vo.setEnterpriseId(e.getEnterpriseId());
                    vo.setStatus(EnterpriseStatusEnum.getMessage(e.getStatus()));
                    vo.setIsPersonal(e.getMainCorpId() != null ? "是" : "否");
                    String province = e.getProvince() == null ? "" : e.getProvince();
                    String city = e.getCity() == null ? "" : e.getCity();
                    vo.setProvinceCity(province + city);
                    vo.setIsVip(EnterpriseVipTypeEnum.getMessage(e.getIsVip()));
                    vo.setIndustry(e.getIndustry());
                    vo.setAuthType(e.getAuthType());
                    vo.setIsAuthenticated(e.getIsAuthenticated() != null && e.getIsAuthenticated() ? "是" : "否");
                    vo.setAuthLevel(AuthLevelEnum.getMessage(e.getAuthLevel()));
                    vo.setAppType(AppTypeEnum.getMessage(e.getAppType()));
                    vo.setStoreNum(e.getStoreNum());
                    vo.setContact(e.getContact());
                    vo.setMobile(e.getMobile());
                    vo.setSalesStage(BossCluesSalesStageEnum.getMessage(e.getSalesStage()));
                    vo.setIsPay(e.getIsPay() != null && e.getIsPay() ? "是" : "否");
                    if(StringUtils.isNotBlank(e.getDbName())){
                        vo.setDbNameNum(StringUtils.remove(e.getDbName(), "coolcollege_intelligent_"));
                    }
                    return vo;
                }).collect(Collectors.toList());

        return resultList;
    }

    @Override
    public Integer syncEnterprise(BossLoginUserDTO user) {
        //所有企业
        List<EnterpriseBossDTO> enterpriseBossDTOS = enterpriseMapper.listEnterprise(new BossEnterpriseExportRequest());
        //所有企业线索
        List<EnterpriseCluesDTO> enterpriseCluesDTOS = enterpriseCluesMapper.list(new EnterpriseCluesRequest());
        Set<String> eidSet = enterpriseCluesDTOS.stream().map(EnterpriseCluesDTO::getEnterpriseId).collect(Collectors.toSet());
        //增量同步
        enterpriseBossDTOS = enterpriseBossDTOS.stream().filter(e -> !eidSet.contains(e.getId())).collect(Collectors.toList());;
        List<EnterpriseCluesDO> list = enterpriseBossDTOS.stream().map(e -> {
            EnterpriseCluesDO enterpriseCluesDO = new EnterpriseCluesDO();
            enterpriseCluesDO.setEnterpriseId(e.getId());
            enterpriseCluesDO.setName(e.getName());
            enterpriseCluesDO.setOriginalName(e.getOriginalName());
            enterpriseCluesDO.setMobile(e.getMobile());
            enterpriseCluesDO.setProvince(e.getProvince());
            enterpriseCluesDO.setCity(e.getCity());
            enterpriseCluesDO.setStatus(e.getStatus());
            enterpriseCluesDO.setLogo(e.getLogo());
            enterpriseCluesDO.setIsVip(e.getIsVip());
            enterpriseCluesDO.setAuthType(e.getAuthType());
            enterpriseCluesDO.setAuthUserId(e.getAuthUserId());
            enterpriseCluesDO.setIndustry(e.getIndustry());
            enterpriseCluesDO.setLogoName(e.getLogoName());
            enterpriseCluesDO.setCorpLogoUrl(e.getCorpLogoUrl());
            enterpriseCluesDO.setIsAuthenticated(e.getIsAuthenticated());
            enterpriseCluesDO.setAuthLevel(e.getAuthLevel());
            enterpriseCluesDO.setPackageBeginDate(e.getPackageBeginDate());
            enterpriseCluesDO.setPackageEndDate(e.getPackageEndDate());
            enterpriseCluesDO.setAppType(e.getAppType());
            enterpriseCluesDO.setCreateId(String.valueOf(user.getId()));
            enterpriseCluesDO.setUpdateId(String.valueOf(user.getId()));
            enterpriseCluesDO.setCreateName(user.getName());
            enterpriseCluesDO.setUpdateName(user.getName());
            enterpriseCluesDO.setCreateTime(new Date());
            enterpriseCluesDO.setUpdateTime(new Date());
            return enterpriseCluesDO;
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(list)){
            return 0;
        }
        return enterpriseCluesMapper.batchSave(list);
    }

}
