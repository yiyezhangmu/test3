package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseActivationMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.mapper.device.EnterpriseAuthDeviceDAO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.dto.DeviceAuthReportPageDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseActivationPageDTO;
import com.coolcollege.intelligent.model.enterprise.vo.DeviceAuthReportVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseActivationVO;
import com.coolcollege.intelligent.model.enums.StoreStatusEnum;
import com.coolcollege.intelligent.model.store.dto.StoreStatusStoreCountDTO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseActivationService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EnterpriseActivationServiceImpl implements EnterpriseActivationService {

    @Resource
    private EnterpriseActivationMapper enterpriseActivationMapper;
    @Resource
    private EnterpriseAuthDeviceDAO enterpriseAuthDeviceDAO;
    @Resource
    private EnterpriseMapper enterpriseMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private StoreMapper storeMapper;

    @Override
    public PageInfo<EnterpriseActivationVO> getEnterpriseActivationPage(EnterpriseActivationPageDTO param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        Page<EnterpriseActivationVO> enterpriseActivationPage = enterpriseActivationMapper.getEnterpriseActivationPage(param);
        return new PageInfo(enterpriseActivationPage);
    }

    @Override
    public PageInfo<DeviceAuthReportVO> getDeviceAuthReport(DeviceAuthReportPageDTO param) {
        if(param.isQueryEnterprise()){
            List<String> queryEnterpriseIds = enterpriseMapper.getQueryEnterpriseIds(param.getEnterpriseId(), param.getEnterpriseName(), param.getTag(), param.getCsm());
            if(CollectionUtils.isEmpty(queryEnterpriseIds)){
                return new PageInfo<>();
            }
            param.setEnterpriseIds(queryEnterpriseIds);
        }
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        Page<DeviceAuthReportVO> deviceAuthReportPage = enterpriseAuthDeviceDAO.getDeviceAuthReport(param);
        PageInfo<DeviceAuthReportVO> page = new PageInfo<>(deviceAuthReportPage);
        if(CollectionUtils.isNotEmpty(deviceAuthReportPage.getResult())){
            List<String> enterpriseIds = page.getList().stream().map(DeviceAuthReportVO::getEnterpriseId).collect(Collectors.toList());
            List<DeviceAuthReportVO> enterpriseCallNum = enterpriseAuthDeviceDAO.getEnterpriseCallNum(enterpriseIds, param.getQueryDate());
            Map<String, DeviceAuthReportVO> callNumMap = ListUtils.emptyIfNull(enterpriseCallNum).stream().collect(Collectors.toMap(DeviceAuthReportVO::getEnterpriseId, Function.identity()));
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigMapper.selectByEnterpriseIds(enterpriseIds);
            Map<String, EnterpriseConfigDO> enterpriseConfigMap = enterpriseConfigList.stream().collect(Collectors.toMap(EnterpriseConfigDO::getEnterpriseId, enterpriseConfigDO -> enterpriseConfigDO));
            for (DeviceAuthReportVO deviceAuthReportVO : page.getList()) {
                EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMap.get(deviceAuthReportVO.getEnterpriseId());
                if(Objects.isNull(enterpriseConfigDO)){
                    continue;
                }
                deviceAuthReportVO.setEnterpriseName(enterpriseConfigDO.getEnterpriseName());
                deviceAuthReportVO.setCsm(enterpriseConfigDO.getCsm());
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
                List<StoreStatusStoreCountDTO> storeStatusList = storeMapper.getStoreCountGroupByStoreStatus(deviceAuthReportVO.getEnterpriseId());
                int totalOpenStoreNum = storeStatusList.stream().filter(o -> StoreStatusEnum.OPEN.getValue().equals(o.getStoreStatus())).mapToInt(StoreStatusStoreCountDTO::getStoreCount).sum();
                int totalStoreNum = storeStatusList.stream().mapToInt(StoreStatusStoreCountDTO::getStoreCount).sum();
                deviceAuthReportVO.setTotalStoreNum(totalStoreNum);
                deviceAuthReportVO.setTotalOpenStoreNum(totalOpenStoreNum);
                DeviceAuthReportVO callNum = callNumMap.get(deviceAuthReportVO.getEnterpriseId());
                if(callNum != null){
                    deviceAuthReportVO.setMeituanMonthlyCallNum(callNum.getMeituanMonthlyCallNum());
                    deviceAuthReportVO.setElemeMonthlyCallNum(callNum.getElemeMonthlyCallNum());
                }else{
                    deviceAuthReportVO.setMeituanMonthlyCallNum(0);
                    deviceAuthReportVO.setElemeMonthlyCallNum(0);
                }
            }
        }
        page.setList(deviceAuthReportPage);
        return page;
    }

}
